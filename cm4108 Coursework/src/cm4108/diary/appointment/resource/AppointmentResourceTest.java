package cm4108.diary.appointment.resource;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

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
		Appointment appointment5 = new Appointment("5", 1574899200000L, 60, "Marya", "Another test");
		Appointment appointment6 = new Appointment("6", 1574691345190L, 20, "Bob", "Adding an appointment");
		Appointment appointment7 = new Appointment("7", 1574789155895L, 20, "Bob", "idek");
		Appointment appointment8 = new Appointment("8", 1574812800000L, 20, "Mary", "random");
		Appointment appointment9 = new Appointment("9", 1574899200000L, 20, "Sam", "testing");
		Appointment appointment10 = new Appointment("10", 1574899200000L, 60, "Bob", "Another test");
		Appointment appointment11 = new Appointment("11", 1574789155895L, 20, "Bob", "idek");
		Appointment appointment12 = new Appointment("12", 1574812800000L, 20, "Bob", "random");
		Appointment appointment13 = new Appointment("13", 1574899200000L, 20, "Bob", "testing");
		Appointment appointment14 = new Appointment("14", 1574899200000L, 60, "Bob", "Another test");
		Appointment appointment15 = new Appointment("15", 1574691345190L, 20, "Bob", "Adding an appointment");
		Appointment appointment16 = new Appointment("16", 1574789155895L, 20, "Bob", "idek");
		Appointment appointment17 = new Appointment("17", 1574812800000L, 20, "Bob", "random");
		Appointment appointment18 = new Appointment("18", 1574899200000L, 20, "Bob", "testing");
		Appointment appointment19 = new Appointment("19", 1574899200000L, 60, "Sam", "Another test");
		Appointment appointment20 = new Appointment("20", 1574691345190L, 20, "Bob", "Adding an appointment");
		Appointment appointment21 = new Appointment("21", 1574789155895L, 20, "Mary", "idek");
		Appointment appointment22 = new Appointment("22", 1574812800000L, 20, "Mary", "random");
		Appointment appointment23 = new Appointment("23", 1574899200000L, 20, "Sam", "testing");
		Appointment appointment24 = new Appointment("24", 1574899200000L, 60, "Bob", "Another test");
		Appointment appointment25 = new Appointment("25", 1574789155895L, 20, "Bob", "idek");
		Appointment appointment26 = new Appointment("26", 1574812800000L, 20, "Bob", "random");
		Appointment appointment27 = new Appointment("27", 1574899200000L, 20, "Bob", "testing");
		Appointment appointment28 = new Appointment("28", 1574899200000L, 60, "Jeff", "Another test");
		
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
		appointmentsToAdd.add(appointment11);
		appointmentsToAdd.add(appointment12);
		appointmentsToAdd.add(appointment13);
		appointmentsToAdd.add(appointment14);
		appointmentsToAdd.add(appointment15);
		appointmentsToAdd.add(appointment16);
		appointmentsToAdd.add(appointment17);
		appointmentsToAdd.add(appointment18);
		appointmentsToAdd.add(appointment19);
		appointmentsToAdd.add(appointment20);
		appointmentsToAdd.add(appointment21);
		appointmentsToAdd.add(appointment22);
		appointmentsToAdd.add(appointment23);
		appointmentsToAdd.add(appointment24);
		appointmentsToAdd.add(appointment25);
		appointmentsToAdd.add(appointment26);
		appointmentsToAdd.add(appointment27);
		appointmentsToAdd.add(appointment28);
		
		appointmentsToAdd.forEach(a -> AppointmentResourceTest.testDB.addAppointment(a));
	}

	@Test
	void testRetrieveAppointment() {
		AppointmentResourceTest.testDB.findAppointmentById("1");
	}


	@Test
	void testQueryAppointmentsFromDates() {
		List<Appointment> appointments = (List<Appointment>) AppointmentResourceTest.testDB.queryAppointmentsBetweenDates("Jeff", 1572566400000L, 1574985600000L);
		appointments.forEach(a -> {
			if (!(a.getOwner().equals("Jeff")))
				fail("Should only have retrieved appointments for Bob.");
		});
	}
	
	
	@Test
	void testScanAppointmentsFromDates() {
		List<Appointment> appointments = (List<Appointment>) AppointmentResourceTest.testDB.scanAppointmentsBetweenDates("Jeff", 1572566400000L, 1574985600000L);
		appointments.forEach(a -> {
			if (!(a.getOwner().equals("Jeff")))
				fail("Should only have retrieved appointments for Bob.");
		});
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
