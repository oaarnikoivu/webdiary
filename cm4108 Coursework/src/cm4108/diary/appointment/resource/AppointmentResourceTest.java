package cm4108.diary.appointment.resource;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cm4108.diary.appointment.model.Appointment;
import cm4108.diary.appointment.model.AppointmentDatabase;
import cm4108.diary.appointment.model.PersistentDB;

class AppointmentResourceTest {
	
	static AppointmentDatabase testDB;
	
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
		Appointment appointment5 = new Appointment("5", 1574899200000L, 60, "Marya", "Another test");
		Appointment appointment6 = new Appointment("6", 1574691345190L, 20, "Bob", "Adding an appointment");
		Appointment appointment7 = new Appointment("7", 1574789155895L, 20, "Bob", "idek");
		Appointment appointment8 = new Appointment("8", 1574812800000L, 20, "Mary", "random");
		Appointment appointment9 = new Appointment("9", 1574899200000L, 20, "Sam", "testing");
		Appointment appointment10 = new Appointment("10", 1574899200000L, 60, "Bob", "Another test");
		
		appointmentsToAdd.add(appointment1);
		appointmentsToAdd.add(appointment2);
		appointmentsToAdd.add(appointment3);
		appointmentsToAdd.add(appointment4);
		appointmentsToAdd.add(appointment5);
		appointmentsToAdd.add(appointment6);
		appointmentsToAdd.add(appointment7);
		appointmentsToAdd.add(appointment8);
		appointmentsToAdd.add(appointment9);
		appointmentsToAdd.add(appointment10);
				
		appointmentsToAdd.forEach(a -> AppointmentResourceTest.testDB.addAppointment(a));
	}

	@Test
	void testRetrieveAppointment() {
		AppointmentResourceTest.testDB.findAppointmentById("1");
	}


	@Test
	void testQueryAppointmentsFromDates() {
		List<Appointment> appointments = (List<Appointment>) AppointmentResourceTest.testDB.queryAppointmentsBetweenDates("Bob", 1572566400000L, 1574985600000L);
		appointments.forEach(a -> {
			if (!(a.getOwner().equals("Bob")))
				fail("Should only have retrieved appointments for Bob.");
		});
	}
	
	
	@Test
	void testScanAppointmentsFromDates() {
		List<Appointment> appointments = (List<Appointment>) AppointmentResourceTest.testDB.scanAppointmentsBetweenDates("Bob", 1572566400000L, 1574985600000L);
		appointments.forEach(a -> {
			if (!(a.getOwner().equals("Bob")))
				fail("Should only have retrieved appointments for Bob.");
		});
	}


	@Test
	void testUpdateAppointment() {
		// Retrieve appointment to update
		Appointment appointmentToUpdate = AppointmentResourceTest.testDB.findAppointmentById("1");
		
		// Store description in separate variable 
		String oldDescription = appointmentToUpdate.getDescription();
		
		// Update description
		appointmentToUpdate.setDescription("Updating an appointment test");
		
		// Send to database
		AppointmentResourceTest.testDB.updateAppointmentById("1", appointmentToUpdate);
		
		// Retrieve appointment again
		Appointment updatedAppointment = AppointmentResourceTest.testDB.findAppointmentById("1");
		
		// Check that stored description is not the same as
		assertTrue(updatedAppointment.getDescription() != oldDescription);
	}
	
	@Test
	void testDeleteAppointment() {
		// Delete appointment with id of "1"
		AppointmentResourceTest.testDB.deleteAppointmentById("1");
		
		// Try and retrieve the deleted appointment
		Appointment appointment = AppointmentResourceTest.testDB.findAppointmentById("1");
		
		// Check that the appointment is null
		assertTrue(appointment == null);
	}

}
