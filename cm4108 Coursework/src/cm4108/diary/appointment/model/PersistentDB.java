package cm4108.diary.appointment.model;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import cm4108.aws.util.DynamoDBUtil;

public class PersistentDB implements AppointmentDatabase {
	
	public static final String TABLE_NAME = "cm4108-coursework";
	private static final String REGION = "local";
	private static final String LOCAL_ENDPOINT = "http://localhost:8000";
	
	private static DynamoDBMapper dynamoDBMapper = null;
	private static PersistentDB instance = null;
	
	private PersistentDB() {
		
	}
	
	/**
	 * Create a new DynamoDB instance
	 * @return
	 */
	public static PersistentDB getInstance() {
		if (PersistentDB.instance == null) {
			PersistentDB.instance = new PersistentDB();
			PersistentDB.dynamoDBMapper = DynamoDBUtil.getDBMapper(PersistentDB.REGION, PersistentDB.LOCAL_ENDPOINT);
			
			// Create a table request
			CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(Appointment.class);
			
			// Set the amount of read and write activity that the table can support 
			final ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(5L, 5L);
			createTableRequest.setProvisionedThroughput(provisionedThroughput);
			createTableRequest.getGlobalSecondaryIndexes().forEach(v -> v.setProvisionedThroughput(provisionedThroughput));
			
			// Allow the global secondary index to query all attributes in the table 
			createTableRequest.getGlobalSecondaryIndexes().forEach(v -> v.setProjection(new Projection().withProjectionType(ProjectionType.ALL)));
			
			// Create the table
			DynamoDBUtil.getDynamoDBClient(PersistentDB.REGION, PersistentDB.LOCAL_ENDPOINT).createTable(createTableRequest);
		}
		return PersistentDB.instance;
	}
	
	/**
	 * Load an appointment given by its ID
	 */
	@Override
	public Appointment findAppointmentById(String id) {
		return PersistentDB.dynamoDBMapper.load(Appointment.class, id);
	}

	/**
	 * Method to add a new appointment
	 */
	@Override
	public void addAppointment(Appointment a) {
		Appointment appointment = new Appointment(
				a.getAppointmentId(),
				a.getDateAndTime(), 
				a.getDuration(), 
				a.getOwner(), 
				a.getDescription());
			
		PersistentDB.dynamoDBMapper.save(appointment);
	}

	/**
	 * Method for deleting an appointment given by its ID
	 */
	@Override
	public void deleteAppointmentById(String id) {
		Appointment appointment = PersistentDB.dynamoDBMapper.load(Appointment.class, id);
		
		if (appointment != null)
			PersistentDB.dynamoDBMapper.delete(appointment);
	}

	/**
	 * Method to update an appointment by its ID
	 */
	@Override
	public void updateAppointmentById(String id, Appointment appointment) {
		Appointment appointmentToUpdate = PersistentDB.dynamoDBMapper.load(Appointment.class, id);
		
		if (appointmentToUpdate != null) {
			appointmentToUpdate.setDateAndTime(appointment.getDateAndTime());
			appointmentToUpdate.setDuration(appointment.getDuration());
			appointmentToUpdate.setOwner(appointment.getOwner());
			appointmentToUpdate.setDescription(appointment.getDescription());
			
			PersistentDB.dynamoDBMapper.save(appointmentToUpdate);
		}		
	}

	/**
	 * Method for finding appointments between two dates for a specific user using a query expression
	 */
	@Override
	public Collection<Appointment> queryAppointmentsBetweenDates(String owner, long fromDate, long toDate) {
		// Define a new HashMap for the expression attribute values
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		
		eav.put(":owner", new AttributeValue().withS(owner));
		eav.put(":date1", new AttributeValue().withN (String.valueOf(fromDate)));
		eav.put(":date2", new AttributeValue().withN(String.valueOf(toDate)));
	
		// Query the index to retrieve a subset of appointments for a specific owner between 2 specified dates
		DynamoDBQueryExpression<Appointment> queryExpression = new DynamoDBQueryExpression<Appointment>()
				.withIndexName("OwnerIndex") // must specify the index name so that DynamoDB knows which index to query
				.withConsistentRead(false) // local secondary indexes support eventually consistent reads but not strongly consistent reads, you must specify withConsistentRead(false)
				.withKeyConditionExpression("#o = :owner and dateAndTime between :date1 and :date2")
				.withExpressionAttributeValues(eav);
		
		// Handle reserved keyword owner
		Map<String, String> expression = new HashMap<>();
        expression.put("#o", "owner");    
        queryExpression.withExpressionAttributeNames(expression);
		
        long startTime = System.currentTimeMillis();
        // Query the appointments using the query expression
        List<Appointment> appointments = PersistentDB.dynamoDBMapper.query(Appointment.class, queryExpression);
        long endTime = System.currentTimeMillis();
        
        // Calculate time taken to query 
        System.out.println("Querying time: " + (endTime - startTime));
		
		return appointments;	
	}

	/**
	 * Method for finding appointments between two dates for a specific user using a scan expression
	 */
	@Override
	public Collection<Appointment> scanAppointmentsBetweenDates(String owner, long fromDate, long toDate) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		
		eav.put(":owner", new AttributeValue().withS(owner));
		eav.put(":date1", new AttributeValue().withN(String.valueOf(fromDate)));
		eav.put(":date2", new AttributeValue().withN(String.valueOf(toDate)));
		
		// Scan the table with the relevant filter expression to retrieve subset of appointments for a specific owner between 2 specified dates
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression("#o = :owner and dateAndTime between :date1 and :date2")
				.withExpressionAttributeValues(eav);
		
		Map<String, String> expression = new HashMap<>();
        expression.put("#o", "owner");    
        scanExpression.withExpressionAttributeNames(expression);
        
        long startTime = System.currentTimeMillis();
        List<Appointment> appointments = PersistentDB.dynamoDBMapper.scan(Appointment.class, scanExpression);
        long endTime = System.currentTimeMillis();
        
        // Calculate time taken to scan 
        System.out.println("Scanning time: " + (endTime - startTime));
        
        return appointments;
				
	}
}

