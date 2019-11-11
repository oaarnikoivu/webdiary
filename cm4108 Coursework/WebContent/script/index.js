var baseURL="api";

//the document ready function
try	{
	$(function()
		{
		init();
		}
	);
	} catch (e) {
		alert("*** jQuery not loaded. ***");
	}

function init() {
	
	$('#addAppointmentPopup').dialog({
		modal:true,
		autoOpen: false,
		title: 'Add Appointment',
		minWidth: 500,
		minHeight: 280
	});
	
	// add appointment click handler 
	$('#addAppointment').click(function() {
		$('#addAppointmentPopup').dialog('open', true);
	});
	
	// show appointment(s) click hander
	$('#showAppointment').click(function() {
		// hide appointment pop up
		$('#addAppointmentPopup').dialog('close');
		
		//retrieve appointments
		retrieveAppointments();
		
	});
	
	// add date picker 
	$(function() {
		$( "#datepicker" ).datepicker();
		} 
	);
	
	// hide appointment on cancel clicked
	$("#cancelBtn").click(function() {
		$('#addAppointmentPopup').hide();
	});
}

function retrieveAppointments() {
	var url = baseURL + '/appointments'; //URL of appointment service
	
	$.getJSON(url, function(appointments) {
		$('#appointments').empty();
		appointments.forEach(a => {
			var appointmentId = a['appointmentId'];
			var dateAndTime = a['dateAndTime'];
			var description = a['description'];
			var duration = a['duration'];
			var user = a['owner'];
			
			var htmlCode="<li id='"+date+"'>"+description+"</li>";
		})
	});
}


