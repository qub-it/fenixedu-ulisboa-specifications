<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url var="staticUrl" value="/themes/fenixedu-learning-theme/static"/>

<link href='${staticUrl}/css/fullcalendar.css' rel='stylesheet' />
<link href='${staticUrl}/css/fullcalendar.print.css' rel='stylesheet' media='print' />
<link href='${staticUrl}/css/schedule.css' rel='stylesheet' rel='stylesheet' />
<script src='${staticUrl}/js/moment.min.js'></script>
<script src='${staticUrl}/js/jquery-ui.fullCalendar.custom.min.js'></script>
<script src='${staticUrl}/js/fullcalendar.js'></script>

<spring:url var="eventsUrl" value="/student/shiftEnrolment/currentSchedule.json/${registration.externalId}/${semester.externalId}"/>

<div id="calendar"></div>
<script>
$(document).ready(function() {
	$(function () {
	  $('[data-toggle="tooltip"]').tooltip();
	  $('[rel="tooltip"]').tooltip();
	})
	
	var i18nDayNames = [
		"<spring:message code="label.weekday.short.sunday"/>",
		"<spring:message code="label.weekday.short.monday"/>",
		"<spring:message code="label.weekday.short.tuesday"/>",
		"<spring:message code="label.weekday.short.wednesday"/>",
		"<spring:message code="label.weekday.short.thursday"/>",
		"<spring:message code="label.weekday.short.friday"/>",
		"<spring:message code="label.weekday.short.saturday"/>"
	];
	
	$('#calendar').fullCalendar({
			header: { left: '', center: '', right: '' },
			defaultView: 'agendaWeek',
			columnFormat: { week: 'ddd' },			
			minTime: '08:00',
			maxTime: '24:00',
			timeFormat: 'HH:mm',
			axisFormat: 'HH:mm',
			allDaySlot : false,
			dayNamesShort: i18nDayNames,
			firstDay: 1,	
			hiddenDays: [0],						
			editable: false,
			eventLimit: true, // allow "more" link when too many events
			events : "${eventsUrl}",
			height: "auto"
		});
});
</script>