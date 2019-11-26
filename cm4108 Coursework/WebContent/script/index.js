const baseURL="api";
let appointmentId = "1";
var oldDate; 

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
	
	$('#appointmentDialog').dialog({
		modal:true,
		autoOpen: false,
		minWidth: 500,
		minHeight: 280
	});
	
	// add appointment click handler 
	$('#addAppointment').click(function() {
			
		// clear any appointment input values
		clearAppointmentInput();
		
		// open dialog
		$('#appointmentDialog').dialog('open', true);
		$('#appointmentDialog').dialog({title: 'Add Appointment'});
		
		$('#saveAppointment').show();
		$('#updateAppointment').hide();
		$('#deleteAppointment').hide();
	});
	
	// show appointment(s) click hander
	$('#showAppointment').click(function() {
		
		// hide appointment pop up if open
		$('#appointmentDialog').dialog('close');
		
		var owner = $('#user').val().replace(/\s/g, '');
		var fromDate = formatToDate($('#fromDatepicker').val().replace(/\s/g, ''));
		var toDate = formatToDate($('#toDatepicker').val().replace(/\s/g, ''));
		
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
	
	// save appointment on save clicked
	$("#saveAppointment").click(function() {
		saveAppointment();
	});
	
	// handle update appointment clicked 
	$('#updateAppointment').click(function() {
		updateAppointment(appointmentId);
	});
	
	$('#deleteAppointment').click(function() {
		deleteAppointment(appointmentId);
		$('#appointmentDialog').dialog('close');
	})
	
	// hide appointment popup on cancel clicked
	$("#cancelAppointment").click(function() {
		$('#appointmentDialog').dialog('close');
	});
}

/**
 * Function to add a new appointment
 * @returns
 */
function saveAppointment() {	
	const url = baseURL + '/appointment';
	
	// use jQuery shorthand Ajax POST function
	$.ajax({
		type: 'POST',
		url: url,
		contentType: 'application/json',
		data: formToJSON(),
		success: function(data, status, xhr) {
			alert(xhr.responseText); // success message received from back-end
		},
		error: function(xhr, status, error) {
			alert('saveAppointment error: ' + error);
			console.log(xhr.responseText)
		}
	});
}

/**
 * Empty appointment input (if any)
 * @returns
 */
function clearAppointmentInput() {
	$('#description').val("");
	$('#addAppointmentDatepicker').val("");
	$('#timepicker').val("");
	$('#duration').val("");
	$('#owner').val("");
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
		$('#appointments').empty(); // empty any existing appointment values 
		
		appointments.forEach(a => {
			console.log(a);
			var id = a['appointmentId']; // get appointment ID from JSON data
			var owner = a['owner']; // get appointment owner from JSON data
			var dateTime = a['dateAndTime']; // get appointment date and timestamp from JSON data
			var description = a['description']; // get appointment description from JSON data
			var duration = a['duration']; // get appointment duration from JSON data
			
			console.log(description);
			
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
			
			$('#appointments li').click(function() {
				appointmentClicked($(this).attr('id'));
			});
		});
	});
}

/**
 * Function to handle when an appointment is clicked
 * @param id
 * @returns
 */
function appointmentClicked(id) {
	const url = baseURL + '/appointment/' + id;
	
	// set the global appointment id so that it can be used when updating 
	appointmentId = id;

	$('#appointments li').removeClass('selected');
	
	$('#'+id).addClass('selected');
	
	// clear existing appointment dialog values 
	clearAppointmentInput();
	
	// open dialog
	$('#appointmentDialog').dialog('open', true);
	$('#appointmentDialog').dialog({title: 'Update Appointment'});
	
	$('#saveAppointment').hide();
	$('#updateAppointment').show();
	$('#deleteAppointment').show();
	
		
	// retrieve appointment data for user given by id
	$.getJSON(url, function(jsonData) {
		var owner = jsonData['owner'];
		var description = jsonData['description'];
		var date = new Date(jsonData['dateAndTime']);
		var duration = jsonData['duration'];
		
		var hours = date.getHours();
		var minutes = ('0' + date.getMinutes()).slice(-2); // set to 2 decimal places 
		var startTime = hours + ":" + minutes;
		
		// Append AM or PM to start time depending on the received value for dateAndTime 
		hours > 11 ? startTime += " PM" : startTime += " AM";
		
		// replace dash with slash for date value
		date = replaceAll(date.toLocaleDateString(), '/', '-');
		
		oldDate = date;
			
		// set values to retrieved values 
		$('#owner').val(owner);
		$('#description').val(description);
		$('#addAppointmentDatepicker').val(date);	
		$('#timepicker').val(startTime);
		$('#duration').val(duration);
	});
}

/**
 * Function for updating an appointment given its id 
 * @param id
 * @returns
 */
function updateAppointment(id) {
	const url = baseURL + "/appointment/" + id;
	
	$.ajax({
		type: 'PUT',
		url: url,
		contentType: 'application/json',
		data: formToJSON(),
		success: function(data, textStatus, xhr) {
			alert(xhr.responseText);
		},
		error: function(xhr, textStatus, errorThrown) {
			alert('updateAppointment error: ' + textStatus);
			console.log(xhr.responseText);
		}
	});
}

/**
 * Function to delete an appointment given by its ID 
 * @param id
 * @returns
 */
function deleteAppointment(id) {
	const url = baseURL + "/appointment/" + id;
	
	$.ajax({
		type: 'DELETE',
		url: url,
		success: function(data, textStatus, xhr) {
			alert(xhr.responseText);
		},
		error: function(xhr, textStatus, errorThrown) {
			alert('deleteAppointment error: ' + textStatus);
			console.log(xhr.responseText);
		}
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

/**
 * Function to convert form values to JSON object 
 * @returns
 */
function formToJSON() {
	var date = $('#addAppointmentDatepicker').val();
	var startTime = $('#timepicker').val();
	
	startTime = startTime.replace('AM', '');
	startTime = startTime.replace('PM', '');
	
	// Handle date formating 
	if (date != oldDate) {
		var day = date.split('-')[2];
		var month = date.split('-')[1] - 1;
		var year = date.split('-')[0];
	} else {
		var day = date.split('-')[0];
		var month = date.split('-')[1] - 1;
		var year = date.split('-')[2];
	}
	
	var hours = startTime.substr(0, startTime.indexOf(':'));
	var minutes = startTime.split(':').pop();
		
	var d = new Date(year, month, day, hours, minutes);
	
	// return a JSON object and remove any unnecessary whitespace 
	return JSON.stringify({
		"owner": $('#owner').val().replace(/\s/g, ''),
		"description": $('#description').val(),
		"dateAndTime": d.getTime(),
		"duration": $('#duration').val().replace(/\s/g, '')
	});
}



