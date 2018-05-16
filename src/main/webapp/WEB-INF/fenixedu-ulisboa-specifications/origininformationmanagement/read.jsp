<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.OriginInformationManagementController"%>
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
		<spring:message code="label.OriginInformationManagement.read" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	
	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
	<a class="" href="${pageContext.request.contextPath}<%= OriginInformationManagementController.UPDATE_URL %>/${registration.externalId}">
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
							<spring:message code="label.OriginInformationForm.studentNumber" />
						</th>
						<td>
							<c:out value='${registration.student.number}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.studentName" />
						</th>
						<td>
							<c:out value='${registration.student.person.name}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.registration.degreeName" />
						</th>
						<td>
							<c:out value='${registration.degree.nameI18N.content}' />
						</td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.countryWhereFinishedPreviousCompleteDegree" />
						</th>
						<td>
							<c:out value='${registration.studentCandidacy.precedentDegreeInformation.country.localizedName.content}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.schoolLevel" />
						</th>
						<td>
							<c:out value='${registration.studentCandidacy.precedentDegreeInformation.schoolLevel.localizedName}' />
						</td>
					</tr>
					
					<c:if test='${registration.studentCandidacy.precedentDegreeInformation.schoolLevel.name == "OTHER"}'>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.otherSchoolLevel" />
						</th>
						<td>
							<c:out value='${registration.studentCandidacy.precedentDegreeInformation.otherSchoolLevel}' />
						</td>
					</tr>
					</c:if>
					
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.institution" />
						</th>
						<td>
							<c:if test="${not empty registration.studentCandidacy.precedentDegreeInformation.institution.code}">
								[<c:out value='${registration.studentCandidacy.precedentDegreeInformation.institution.code}' />]
								-
							</c:if>
							<c:out value='${registration.studentCandidacy.precedentDegreeInformation.institution.nameI18n.content}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.degreeDesignation" />
						</th>
						<td>
							<c:if test="${not empty originInformationForm.raidesDegreeDesignation}">
								[<c:out value='${originInformationForm.raidesDegreeDesignation.code}' />]
								<c:out value='${originInformationForm.raidesDegreeDesignation.degreeClassification.description1}' />
								&nbsp;-&nbsp;
								<c:out value='${originInformationForm.raidesDegreeDesignation.description}' />
							</c:if>
							<c:if test="${empty originInformationForm.raidesDegreeDesignation}">
								<c:out value='${originInformationForm.degreeDesignation}' />
							</c:if>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.conclusionYear" />
						</th>
						<td>
                            <c:if test="${ registration.studentCandidacy.precedentDegreeInformation.conclusionYear == '0' }">
                                <c:out value='0000' />
                            </c:if>
                            <c:if test="${ registration.studentCandidacy.precedentDegreeInformation.conclusionYear != '0' }">
    							<c:out value='${registration.studentCandidacy.precedentDegreeInformation.conclusionYear}' />
                            </c:if>
						</td>
					</tr>
					
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.conclusionGrade" />
						</th>
						<td>
							<c:out value='${registration.studentCandidacy.precedentDegreeInformation.conclusionGrade}' />
						</td>
					</tr>
					
					<c:if test='${registration.studentCandidacy.precedentDegreeInformation.schoolLevel.name == "HIGH_SCHOOL_OR_EQUIVALENT"}'>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.OriginInformationForm.highSchoolType" />
						</th>
						<td>
							<c:if test="${not empty registration.studentCandidacy.precedentDegreeInformation.personalIngressionData.highSchoolType}">
								<spring:message code="label.AcademicalInstitutionType.${registration.studentCandidacy.precedentDegreeInformation.personalIngressionData.highSchoolType}" />
							</c:if>
						</td>
					</tr>
					</c:if>

				</tbody>
			</table>
		</form>
	</div>
</div>

