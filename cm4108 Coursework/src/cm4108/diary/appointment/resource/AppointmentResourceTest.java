package cm4108.diary.appointment.resource;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cm4108.diary.appointment.model.Appointment;
import cm4108.diary.appointment.model.AppointmentDatabase;
import cm4108.diary.appointment.model.PersistentDB;

class AppointmentResourceTest {
	
	private static AppointmentDatabase testDB;
	long startTime;
	long endTime;
	
	
	@BeforeAll
	static void initAll() {
		AppointmentResourceTest.testDB = PersistentDB.getInstance();
	}
	

	@Test
	void testAddAppointment() {
		List<Appointment> appointmentsToAdd = new ArrayList<>();
		
		Appointment appointment1 = new Appointment("1", 1574691345190L, 20, "Bob", "Adding an appointment");
		Appointment appointment2 = new Appointment("2", 1574789155895L, 20, "Bob", "idek");
		Appointment appointment3 = new Appointment("3", 1574812800000L, 20, "Bob", "random");
		Appointment appointment4 = new Appointment("4", 1574899200000L, 20, "Bob", "testing");
		
		appointmentsToAdd.add(appointment1);
		appointmentsToAdd.add(appointment2);
		appointmentsToAdd.add(appointment3);
		appointmentsToAdd.add(appointment4);
		
		appointmentsToAdd.forEach(a -> AppointmentResourceTest.testDB.addAppointment(a));
	}

	@Test
	void testRetrieveAppointment() {
		AppointmentResourceTest.testDB.findAppointmentById("1");
	}

	@Before
	void recordStartTime() {
		startTime = System.currentTimeMillis();
	}
	
	@Test
	void testRetrieveAppointmentsFromDates() {
		AppointmentResourceTest.testDB.findAppointmentsBetweenDates("Bob", 1572566400000L, 1574985600000L);
	}
	
	@After
	void recordEndTime() {
		endTime = System.currentTimeMillis();
        System.out.println("Querying execution time : " + (endTime - startTime));
	}
	
	@Test
	void testScanAppointmentsFromDates() {
		AppointmentResourceTest.testDB.scanAppointmentsBetweenDates("Bob", 1572566400000L, 1574985600000L);
	}

	/*@Test
	void testDeleteAppointment() {
		AppointmentResourceTest.testDB.deleteAppointmentById("1");
	}*/

	@Test
	void testUpdateAppointment() {
		Appointment appointmentToUpdate = AppointmentResourceTest.testDB.findAppointmentById("1");
		appointmentToUpdate.setDescription("Updating an appointment test");
		AppointmentResourceTest.testDB.updateAppointmentById("1", appointmentToUpdate);
	}

}
