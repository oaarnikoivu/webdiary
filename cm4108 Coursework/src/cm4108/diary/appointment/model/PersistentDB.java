package cm4108.diary.appointment.model;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
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
			
			CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(Appointment.class);
			createTableRequest.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
			DynamoDBUtil.getDynamoDBClient(PersistentDB.REGION, PersistentDB.LOCAL_ENDPOINT).createTable(createTableRequest);
		}
		return PersistentDB.instance;
	}

	@Override
	public Appointment findAppointmentById(String id) {
		return PersistentDB.dynamoDBMapper.load(Appointment.class, id);
	}

	@Override
	public void addAppointment(Appointment a) {
		Appointment appointment = new Appointment(
				a.getDateAndTime(), 
				a.getDuration(), 
				a.getOwner(), 
				a.getDescription());
			
		PersistentDB.dynamoDBMapper.save(appointment);
	}

	@Override
	public void deleteAppointmentById(String id) {
		Appointment appointment = PersistentDB.dynamoDBMapper.load(Appointment.class, id);
		
		if (appointment != null)
			PersistentDB.dynamoDBMapper.delete(appointment);
	}

	@Override
	public void updateAppointmentById(String id, Appointment appointment) {
		Appointment appointmentToUpdate = PersistentDB.dynamoDBMapper.load(Appointment.class, id);
		
		appointmentToUpdate.setDateAndTime(appointment.getDateAndTime());
		appointmentToUpdate.setDuration(appointment.getDuration());
		appointmentToUpdate.setOwner(appointment.getOwner());
		appointmentToUpdate.setDescription(appointment.getDescription());
		
		PersistentDB.dynamoDBMapper.save(appointmentToUpdate);
		
	}

	@Override
	public Collection<Appointment> findAppointmentsBetweenDates(String owner, long fromDate, long toDate) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
		
		eav.put(":owner", new AttributeValue().withS(owner));
		eav.put(":date1", new AttributeValue().withN (String.valueOf(fromDate)));
		eav.put(":date2", new AttributeValue().withN(String.valueOf(toDate)));
			
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression("#o = :owner and dateAndTime between :date1 and :date2")
				.withExpressionAttributeValues(eav);
		
		Map<String, String> expression = new HashMap<>();

        expression.put("#o", "owner");
        
        scanExpression.withExpressionAttributeNames(expression);
		
		List<Appointment> appointments = PersistentDB.dynamoDBMapper.scan(Appointment.class, scanExpression);
		
		return appointments;
			
	}
}
