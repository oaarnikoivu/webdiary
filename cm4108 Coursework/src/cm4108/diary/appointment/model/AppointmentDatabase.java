package cm4108.diary.appointment.model;

import java.util.Collection;

public interface AppointmentDatabase {
	
	public Appointment findAppointmentById(String id);
	public Collection<Appointment> findAppointmentsBetweenDates(String owner, long fromDate, long toDate);
	public void addAppointment(Appointment appointment);
	public void deleteAppointmentById(String id);
	public void updateAppointmentById(String id, Appointment appointment);
	
}
