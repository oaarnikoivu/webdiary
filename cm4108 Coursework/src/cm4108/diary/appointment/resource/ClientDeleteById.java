package cm4108.diary.appointment.resource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class ClientDeleteById {
	public static void main(String[] args) {
		
		String id = "1";
		
		Client client = ClientBuilder.newClient();
		
		WebTarget AppointmentByIdTarget = client.target("http://localhost:8080/webdiary/api/appointment/" + id);
		
		boolean response = AppointmentByIdTarget.request().delete(Boolean.class);
		
		System.out.println(response);
	}

}
