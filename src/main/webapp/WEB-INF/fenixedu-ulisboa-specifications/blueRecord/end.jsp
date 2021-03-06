<%@page import="org.fenixedu.bennu.portal.domain.PortalConfiguration"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%> 

<style>
	.start {
		font-size: 14px;
	}
</style>

<%-- TITLE --%>
<div class="page-header fuelux">
	<h1><spring:message code="label.firstTimeCandidacy.instructions" /></h1>
</div>

<div class="well start">
	<spring:message code="label.event.firstTimeCandidacy.end"  />
</div>

<div>
<%
		if(request.getContextPath().endsWith("/")) {
		    request.setAttribute("path", "");
		} else {
		    request.setAttribute("path", "/");		    
		}
%>

	<a class="btn btn-primary start" href="<%= request.getContextPath() %>${path}">
		<span class="glyphicon glyphicon-play-circle" aria-hidden="true"></span>&nbsp;
		<spring:message code="label.event.firstTimeCandidacy.student.portal" />
	</a>
	
</div>