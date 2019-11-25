package cm4108.diary.appointment.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class InvalidParamException extends WebApplicationException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidParamException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST)
				.entity(message)
				.type("text/plain")
				.build());
	}
}
