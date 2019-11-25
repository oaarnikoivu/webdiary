package cm4108.diary.appointment.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class AppointmentNotFoundException extends WebApplicationException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppointmentNotFoundException(String appointmentId) {
		super(Response.status(Response.Status.NOT_FOUND)
				.entity("Appointment ID: " + appointmentId + " not found.")
				.type("text/plain")
				.build());
	}

}
