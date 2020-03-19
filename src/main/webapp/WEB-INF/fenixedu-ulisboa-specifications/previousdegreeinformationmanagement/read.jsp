<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.PreviousDegreeManagementController"%>
<%@page import="org.fenixedu.academic.domain.SchoolLevelType" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>



<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.PreviousDegreeManagement.read" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">

	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
	<a class="" href="${pageContext.request.contextPath}<%= PreviousDegreeManagementController.UPDATE_URL %>/${registration.externalId}">
		<spring:message code="label.update" />
	</a>
</div>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:message code="label.details" />
		</h3>
	</div>
	<div class="panel-body">

		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.studentNumber" />
						</th>
						<td>
							<c:out value='${registration.student.number}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.studentName" />
						</th>
						<td>
							<c:out value='${registration.student.person.name}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.registration.degreeName" />
						</th>
						<td>
							<c:out value='${registration.degree.nameI18N}' />
						</td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.precedentCountry" />
						</th>
						<td>
							<c:out value='${registration.studentCandidacy.previousDegreeInformation.country.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.precedentSchoolLevel" />
						</th>
						<td>
							<c:out value='${registration.studentCandidacy.previousDegreeInformation.schoolLevel.localizedName}' />
						</td>
					</tr>
					
					<c:if test='${registration.studentCandidacy.previousDegreeInformation.schoolLevel.name == "OTHER"}'>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.otherPrecedentSchoolLevel" />
						</th>
						<td>
							<c:out value='${registration.studentCandidacy.previousDegreeInformation.otherSchoolLevel}' />
						</td>
					</tr>
					</c:if>
					
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.precedentInstitution" />
						</th>
						<td>
							<c:if test="${not empty registration.studentCandidacy.previousDegreeInformation.institution.code}">
								[<c:out value='${registration.studentCandidacy.previousDegreeInformation.institution.code}' />]
								-
							</c:if>
							<c:out value='${registration.studentCandidacy.previousDegreeInformation.institution.nameI18n.content}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.precedentDegreeDesignation" />
						</th>
						<td>
							<c:if test="${not empty previousDegreeInformationForm.raidesPrecedentDegreeDesignation}">
								[<c:out value='${previousDegreeInformationForm.raidesPrecedentDegreeDesignation.code}' />]
								<c:out value='${previousDegreeInformationForm.raidesPrecedentDegreeDesignation.degreeClassification.description1}' />
								-
								<c:out value='${previousDegreeInformationForm.raidesPrecedentDegreeDesignation.description}' />
							</c:if>
							<c:if test="${empty previousDegreeInformationForm.raidesPrecedentDegreeDesignation}">
								<c:out value='${previousDegreeInformationForm.precedentDegreeDesignation}' />
							</c:if>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.PreviousDegreeInformationForm.numberOfEnrolmentsInPreviousDegrees" />
						</th>
						<td>
							<c:out value='${registration.studentCandidacy.previousDegreeInformation.numberOfEnrolmentsInPreviousDegrees}' />
						</td>
					</tr>
					
				</tbody>
			</table>
		</form>
	</div>
</div>

