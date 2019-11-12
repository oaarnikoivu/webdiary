const baseURL="api";

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

/**
 * Initialize page.
 * @returns
 */
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
		
		// clear any appointment input values
		clearAppointmentInput();
		
		// open dialog
		$('#addAppointmentPopup').dialog('open', true);
	});
	
	// show appointment(s) click hander
	$('#showAppointment').click(function() {
		// hide appointment pop up
		$('#addAppointmentPopup').dialog('close');
		
		var owner = $('#user').val();
		var fromDate = formatToDate($('#fromDatepicker').val());
		var toDate = formatToDate($('#toDatepicker').val());
		
		//retrieve appointments
		retrieveAppointments(owner, fromDate.getTime(), toDate.getTime());
		
	});
	
	// add date picker 
	$(function() {
			$('#fromDatepicker').datepicker({ dateFormat: 'yy-mm-dd' });
			$('#toDatepicker').datepicker({ dateFormat: 'yy-mm-dd' });
			$('#addAppointmentDatepicker').datepicker({ dateFormat: 'yy-mm-dd' });
		} 
	);
	
	// add a time picker
	$(function() {
		$('#timepicker').timepicker({
			timeFormat: 'H:mm p',
			interval: 60,
			dynamic: true,
			dropdown: true,
			scrollbar: true,
			zindex: 9999999
		});
	})
	
	$("#saveAppointment").click(function() {
		saveAppointment();
	})
	
	// hide appointment on cancel clicked
	$("#cancelAppointment").click(function() {
		$('#addAppointmentPopup').dialog('close');
	});
}

/**
 * 
 * @returns
 */
function saveAppointment() {
	
	var owner = $('#owner').val();
	var description = $('#description').val();
	
	var date = $('#addAppointmentDatepicker').val();	
	var time = $('#timepicker').val();
	
	time = time.replace('AM', '');
	time = time.replace('PM', '');
	
	var day = date.split('-')[2];
	var month = date.split('-')[1] - 1;
	var year = date.split('-')[0];
	
	var hours = time.substr(0, time.indexOf(':'));
	var minutes = time.split(':').pop();
	
	var d = new Date(year, month, day, hours, minutes);
	
	var duration = $('#duration').val();
	
	var url = baseURL + '/appointment';
	
	var data = {
			"owner": owner,
			"description": description,
			"date": d.getTime(),
			"duration": duration
	};
	
	// use jQuery shorthand Ajax POST function
	$.post(url, data, function() {
		alert("Appointment saved.");
	})
}

/**
 * Empty appointment input (if any)
 * @returns
 */
function clearAppointmentInput() {
	$('#description').val("");
	$('#addAppointmentDatepicker').val("");
	$('#startTime').val("");
	$('#duration').val("");
}

/**
 * Function to retrieve appointments for a specific owner between two given dates
 * @param owner
 * @param fromDate
 * @param toDate
 * @returns
 */
function retrieveAppointments(owner, fromDate, toDate) {	
	const url = baseURL + '/appointment/' + owner + '/' + fromDate + '/' + toDate;
	
	// use jQuery shorthand Ajax function to get JSON data
	$.getJSON(url, function(appointments) {
		$('#appointments').empty();
		
		appointments.forEach(a => {
			var id = a['appointmentId']; // get appointment ID from JSON data
			var owner = a['owner']; // get appointment owner from JSON data
			var dateTime = a['dateAndTime']; // get appointment date and timestamp from JSON data
			var description = a['description']; // get appointment description from JSON data
			var duration = a['duration']; // get appointment duration from JSON data
			
			// convert long date and timestamp to a JS date object
			var date = new Date(dateTime);
			
			// retrieve hours from date object
			var hours = date.getHours(); 
			
			// retrieve minutes from date object
			var minutes = ('0' + date.getMinutes()).slice(-2); // set to 2 decimal places
			
			date = replaceAll(date.toLocaleDateString(), '/', '-'); // convert dash to slash 
			
			// receive starting and end time of appointment
			var time = calculateTotalTime(hours, minutes, duration);
		
			// compose HTML list using appointment ID, date, time and description
			var htmlCode = "<li id='"+id+"'>"+date+" "+time+" "+description+"</li>";
			$('#appointments').append(htmlCode); // add a child to the appointment list 
		})
	})
}

/**
 * 
 * @param str
 * @param find
 * @param replace
 * @returns
 */
function replaceAll(str, find, replace) {
	return str.replace(new RegExp(find, 'g'), replace);
}

/**
 * 
 * @param str
 * @returns
 */
function formatToDate(str) {
	var day = str.split('-')[2];
	var month = str.split('-')[1] - 1;
	var year = str.split('-')[0];
	
	return new Date(year, month, day);
}

/**
 * 
 * @param num
 * @returns
 */
function timeConvert(num) {
	var hours = Math.floor(num / 60);
	var minutes = num % 60;
	return {
		"hours": hours,
		"minutes": minutes
	}
}

/**
 * 
 * @param hours
 * @param minutes
 * @param duration
 * @returns
 */
function calculateTotalTime(hours, minutes, duration) {
	var startTimeHours = hours;
	var startTimeMinutes = minutes;
	
	var endTimeHours = startTimeHours + timeConvert(duration)["hours"];
	var endTimeMinutes = startTimeMinutes + timeConvert(duration)["minutes"];
	
	var time = startTimeHours + ":" + startTimeMinutes + "-" + endTimeHours + ":" + ('0' + endTimeMinutes).slice(-2);
	
	return time;
}


