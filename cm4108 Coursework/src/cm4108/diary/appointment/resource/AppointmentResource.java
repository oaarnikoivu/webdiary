package cm4108.diary.appointment.resource;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;
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
	
	private final String INCORRECT_PARAM_MESSAGE = "All input fields must be filled!";
	
	private static AppointmentDatabase database = PersistentDB.getInstance();
	
	/*
	 * Add a new appointment
	 * @return
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response addAppointment(Appointment appointment) {
		try {
		
			// Make sure client is not sending empty parameters
			// Check that owner and description are not empty strings and that they are the correct 'String' type
			// Check that dateAndTime and duration are not equal to 0 since long and int automatically convert null values to 0
			if (appointment.getOwner() == "" || appointment.getDescription() == "" 
					|| !(appointment.getOwner() instanceof String) 
					|| !(appointment.getDescription() instanceof String)) 
				return Response.status(400).entity(INCORRECT_PARAM_MESSAGE).build();
			if (appointment.getDateAndTime() == 0.0 || appointment.getDuration() == 0) 
				return Response.status(400).entity(INCORRECT_PARAM_MESSAGE).build();
			
			AppointmentResource.database.addAppointment(appointment);
			return Response.status(201).entity("Appointment successfully added").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500)
					.entity("Adding a new appointment has failed!")
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
	 * I use the @NotNull annotation to ensure that the required parameters are passed in
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws ParseException
	 */
	@GET
	@Path("{owner}/{fromDate}/{toDate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Appointment> retrieveAppointmentsFromDates(
			@NotNull @PathParam("owner") String owner,
			@NotNull @PathParam("fromDate") long fromDate, 
			@NotNull @PathParam("toDate") long toDate) throws ParseException {
		
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
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{appointmentId}")
	public Response updateAppointment(
			@PathParam("appointmentId") String appointmentId,
			Appointment appointment) {
		
		try {
			
			// Make sure client is not sending empty parameters
			// Check that owner and description are not empty strings and that they are the correct 'String' type
			// Check that dateAndTime and duration are not equal to 0 since long and int automatically convert null values to 0
			if (appointment.getOwner() == "" || appointment.getDescription() == "" 
					|| !(appointment.getOwner() instanceof String) 
					|| !(appointment.getDescription() instanceof String)) 
				return Response.status(400).entity(INCORRECT_PARAM_MESSAGE).build();
			if (appointment.getDateAndTime() == 0.0 || appointment.getDuration() == 0) 
				return Response.status(400).entity(INCORRECT_PARAM_MESSAGE).build();
			
			AppointmentResource.database.updateAppointmentById(appointmentId, appointment);
			return Response.status(200).entity("Appointment successfully updated.").build();
		} catch (Exception e) {
			return Response.status(500).entity("Updating an existing appointment has failed!").build();
		}
	}
	
	
}
