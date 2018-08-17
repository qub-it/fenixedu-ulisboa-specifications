<%@page import="org.fenixedu.academic.domain.organizationalStructure.Unit"%>
<%@page import="org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
	.start {
		font-size: 14px;
	}
</style>

<%-- TITLE --%>
<div class="page-header fuelux">
	<h1><spring:message code="label.firstTimeCandidacy.instructions.blueRecord" /></h1>
</div>

<div class="row well start">
	<%= ULisboaSpecificationsUtil.bundle("message.blueRecord.intro") %>
</div>

<div class="well start">
    <p><spring:message code="message.dataProcessingTerms" arguments="<%= Unit.getInstitutionName().getContent() %>" htmlEscape="false"/></p>
</div>

<div class="">
	<a class="btn btn-primary start" href="${pageContext.request.contextPath}${nextURL}">
		<span class="glyphicon glyphicon-play-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.firstTimeCandidacy.start"  />
	</a>
</div>

