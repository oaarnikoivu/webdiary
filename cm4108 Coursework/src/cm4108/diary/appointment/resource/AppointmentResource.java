package cm4108.diary.appointment.resource;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cm4108.diary.appointment.exception.AppointmentNotFoundException;
import cm4108.diary.appointment.exception.AppointmentsNotFoundException;
import cm4108.diary.appointment.model.Appointment;
import cm4108.diary.appointment.model.AppointmentDatabase;
import cm4108.diary.appointment.model.PersistentDB;

@Path("/appointment")
public class AppointmentResource {
	
	private static AppointmentDatabase database = PersistentDB.getInstance();
	
	/**
	 * Add a new appointment
	 * @param dateTime
	 * @param duration
	 * @param owner
	 * @param description
	 * @return
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response addAppointment(Appointment appointment) {
		try {
			AppointmentResource.database.addAppointment(appointment);
			return Response.status(201).entity("Appointment successfully added").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(400)
					.entity("Something went wrong. Params accepted: owner, description, dateAndTime, duration")
					.build();
		}
	}

	/**
	 * Retrieve an appointment by its ID
	 * @param appointmentId
	 * @return
	 */
	@GET
	@Path("{appointmentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Appointment retrieveAppointment(@PathParam("appointmentId") String appointmentId) {
		Appointment appointment = AppointmentResource.database.findAppointmentById(appointmentId);
		if (appointment != null)
			return appointment;
		throw new AppointmentNotFoundException(appointmentId);
	}
	
	/**
	 * Retrieve appointments between two specified dates for a specific user 
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws ParseException
	 */
	@GET
	@Path("{owner}/{fromDate}/{toDate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Appointment> retrieveAppointmentsFromDates(
			@PathParam("owner") String owner,
			@PathParam("fromDate") long fromDate, 
			@PathParam("toDate") long toDate) throws ParseException {
		
		// Uncomment to use DynamoDB query expression instead 
		/* List<Appointment> appointments = (List<Appointment>) 
				AppointmentResource.database.queryAppointmentsBetweenDates(owner, fromDate, toDate); */
		
		List<Appointment> appointments = (List<Appointment>) 
				AppointmentResource.database.scanAppointmentsBetweenDates(owner, fromDate, toDate);
		
		if (appointments != null) 
			return appointments;
		throw new AppointmentsNotFoundException(owner);
	}
	
	/**
	 * Delete an appointment given by its ID
	 * @param appointmentId
	 * @return
	 */
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{appointmentId}")
	public Response deleteAppointment(@PathParam("appointmentId") String appointmentId) {
		try {
			AppointmentResource.database.deleteAppointmentById(appointmentId);
			return Response.status(200).entity("Appointment successfully removed.").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Removing the appointment has failed.").build();
		}
	}
	
	/**
	 * Update an appointment given by its ID.
	 * @param appointmentId
	 * @param appointment
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{appointmentId}")
	public Response updateAppointment(
			@PathParam("appointmentId") String appointmentId,
			Appointment appointment) {
		
		try {
			AppointmentResource.database.updateAppointmentById(appointmentId, appointment);
			return Response.status(200).entity("Appointment successfully updated.").build();
		} catch (Exception e) {
			return Response.status(400).entity("Missing parameters...").build();
		}
	}
	
	
}
