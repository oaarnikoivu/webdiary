package cm4108.diary.appointment.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName=PersistentDB.TABLE_NAME)
public class Appointment {
	
	public String appointmentId; 
	
	public long dateAndTime; 
	
	public double duration;
	
	public String owner;
	
	public String description;
	
	public Appointment() {
		
	}
	
	public Appointment(String appointmentId, long dateAndTime, double duration, String owner, String description) {
		this.setAppointmentId(appointmentId);
		this.setDateAndTime(dateAndTime);
		this.setDuration(duration);
		this.setOwner(owner);
		this.setDescription(description);
	}
	

	@DynamoDBHashKey(attributeName="appointmentId")
	@DynamoDBAutoGeneratedKey
	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	@DynamoDBAttribute(attributeName="dateAndTime")
	public long getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(long dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	@DynamoDBAttribute(attributeName="duration")
	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	@DynamoDBAttribute(attributeName="owner")
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@DynamoDBAttribute(attributeName="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Appointment [appointmentId=" + appointmentId + ", dateAndTime=" + dateAndTime + ", duration=" + duration
				+ ", owner=" + owner + ", description=" + description + "]";
	}
	
	
	
	
}
