package cm4108.diary.appointment.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	public Response addAppointment(
			@FormParam("dateTime") String dateTime,
			@FormParam("duration") String duration,
			@FormParam("owner") String owner,
			@FormParam("description") String description) {
		try {
			
			Appointment appointment = new Appointment();
			
			Date date = formatToDate(dateTime);
			
			appointment.setDateAndTime(date.getTime());
			appointment.setDuration(Double.valueOf(duration));
			appointment.setOwner(owner);
			appointment.setDescription(description);
			
			AppointmentResource.database.addAppointment(appointment);
			return Response.status(201).entity("Appointment added.").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Adding a new appointment has failed.").build();
		}
	}

	/**
	 * Retrieve an appointment by its ID
	 * @param appointmentId
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{appointmentId}")
	public Appointment retrieveAppointment(@PathParam("appointmentId") String appointmentId) {
		Appointment appointment = AppointmentResource.database.findAppointmentById(appointmentId);
		return appointment;
	}
	
	/**
	 * Retrieve appointments between two specified dates
	 * @param firstDate
	 * @param secondDate
	 * @return
	 * @throws ParseException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{firstDate}/{secondDate}")
	public List<Appointment> retrieveAppointmentsFromDates(
			@PathParam("firstDate") String firstDate, 
			@PathParam("secondDate") String secondDate) throws ParseException {
		
		Date date1 = formatToDate(firstDate);
		Date date2 = formatToDate(secondDate);
		
		List<Appointment> appointments = AppointmentResource.database.findAppointmentsBetweenDates(date1.getTime(), date2.getTime());
		
		return appointments;
		
	}
	
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{appointmentId}")
	public Response deleteAppointment(@PathParam("appointmentId") String appointmentId) {
		try {
			AppointmentResource.database.deleteAppointmentById(appointmentId);
			return Response.status(200).entity("Appointment successfully removed.").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Deleting appointment has failed.").build();
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
	
	/**
	 * Method to format date received from client.
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 */
	private Date formatToDate(String dateTime) throws ParseException {
		// Date formatting
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(dateTime);
		return date;
	}
	
	
	

}
