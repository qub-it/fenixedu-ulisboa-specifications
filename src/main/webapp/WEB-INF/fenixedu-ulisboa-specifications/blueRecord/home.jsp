<%@page import="org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance"%>
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

<div class="row well start">
	<%= RaidesInstance.getInstance().getBlueRecordStartMessageContent() %>
</div>

<div class="">
	<a class="btn btn-primary start" href="${pageContext.request.contextPath}${nextURL}">
		<span class="glyphicon glyphicon-play-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.firstTimeCandidacy.start"  />
	</a>
</div>

