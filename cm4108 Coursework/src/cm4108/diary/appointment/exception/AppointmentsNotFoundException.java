package cm4108.diary.appointment.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class AppointmentsNotFoundException extends WebApplicationException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppointmentsNotFoundException(String owner) {
		super(Response.status(Response.Status.NOT_FOUND)
				.entity("Could not find appointments for owner: " + owner)
				.type("text/plain")
				.build());
	}

}
