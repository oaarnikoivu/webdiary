package cm4108.diary.appointment.resource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cm4108.diary.appointment.model.Appointment;
import cm4108.diary.appointment.model.AppointmentDatabase;
import cm4108.diary.appointment.model.PersistentDB;

class AppointmentResourceTest {
	
	private static AppointmentDatabase testDB;
	
	@BeforeAll
	static void initAll() {
		AppointmentResourceTest.testDB = PersistentDB.getInstance();
	}
	

	@Test
	void testAddAppointment() {
		Appointment appointment = new Appointment("1", 1574691345190L, 20, "Adding an appointment", "Bob");
		AppointmentResourceTest.testDB.addAppointment(appointment);
	}

	@Test
	void testRetrieveAppointment() {
		AppointmentResourceTest.testDB.findAppointmentById("1");
	}

	@Test
	void testRetrieveAppointmentsFromDates() {
		AppointmentResourceTest.testDB.findAppointmentsBetweenDates("Bob", 1572566400000L, 1574726400000L);
	}

	@Test
	void testDeleteAppointment() {
		AppointmentResourceTest.testDB.deleteAppointmentById("1");
	}

	@Test
	void testUpdateAppointment() {
		Appointment appointmentToUpdate = AppointmentResourceTest.testDB.findAppointmentById("1");
		appointmentToUpdate.setDescription("Updating an appointment test");
		AppointmentResourceTest.testDB.updateAppointmentById("1", appointmentToUpdate);
	}

}
