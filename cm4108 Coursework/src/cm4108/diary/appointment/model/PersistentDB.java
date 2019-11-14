package cm4108.diary.appointment.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	
	public static PersistentDB getInstance() {
		if (PersistentDB.instance == null) {
			PersistentDB.instance = new PersistentDB();
			PersistentDB.dynamoDBMapper = DynamoDBUtil.getDBMapper(PersistentDB.REGION, PersistentDB.LOCAL_ENDPOINT);
			
			CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(Appointment.class);
			createTableRequest.withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
			DynamoDBUtil.getDynamoDBClient(PersistentDB.REGION, PersistentDB.LOCAL_ENDPOINT).createTable(createTableRequest);
			
			Appointment testAppointment = new Appointment();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
			
			try {
				Date date = sdf.parse("2019-09-29 13:00:00");
				
				testAppointment.setAppointmentId("1");
				testAppointment.setDateAndTime(date.getTime());
				testAppointment.setDuration(60);
				testAppointment.setOwner("Jeff Jeff");
				testAppointment.setDescription("Testing description");
				
				PersistentDB.dynamoDBMapper.save(testAppointment);
				
				System.out.println("SAVED HASH KEY VALUE: " + testAppointment.getAppointmentId());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return PersistentDB.instance;
	}

	@Override
	public Appointment findAppointmentById(String id) {
		return PersistentDB.dynamoDBMapper.load(Appointment.class, id);
	}

	@Override
	public void addAppointment(Appointment appointment) {
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
	public List<Appointment> findAppointmentsBetweenDates(String owner, long fromDate, long toDate) {
		Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
			
		eav.put(":date1", new AttributeValue().withN (String.valueOf(fromDate)));
		eav.put(":date2", new AttributeValue().withN(String.valueOf(toDate)));
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression("dateAndTime between :date1 and :date2")
				.withExpressionAttributeValues(eav);
		
		List<Appointment> appointments = new ArrayList<>();
			
		List<Appointment> result = PersistentDB.dynamoDBMapper
				.scan(Appointment.class, scanExpression)
				.stream().filter(a -> a.getOwner().equals(owner))
				.collect(Collectors.toList());
				
		result.forEach(a -> appointments
				.add(PersistentDB.dynamoDBMapper.load(Appointment.class, a.getAppointmentId())));
		
		return appointments;
	}

}
