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
	
	$(function() {
		$('#addAppointmentPopup').hide();
	})
	
	// add appointment click handler 
	$('#addAppointment').click(function() {
		$('#addAppointmentPopup').show();
	})
	
	
	// add date picker 
	$(function() {
		$( "#datepicker" ).datepicker();
		} 
	);
	
	// hide appointment on cancel clicked
	$("#cancelBtn").click(function() {
		$('#addAppointmentPopup').hide();
	})
}


