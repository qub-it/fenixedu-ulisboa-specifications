<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.HouseholdInformationManagementController"%>
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
		<spring:message code="label.HouseholdInformationManagement.read" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= HouseholdInformationManagementController.SEARCH_URL %>/${student.externalId}">
		<spring:message code="label.back" />
	</a>
	
	&nbsp;|&nbsp;
	
	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
	<a class="" href="${pageContext.request.contextPath}<%= HouseholdInformationManagementController.UPDATE_URL %>/${student.externalId}/${personalIngressionData.executionYear.externalId}">
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
							<spring:message code="label.HouseholdInformationForm.executionYear" />
						</th>
						<td>
							<c:out value='${personalIngressionData.executionYear.qualifiedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.professionalCondition" />
						</th>
						<td>
							<c:out value='${personalIngressionData.professionalCondition.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.professionType" />
						</th>
						<td>
							<c:out value='${personalIngressionData.professionType.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.professionTimeType" />
						</th>
						<td>
							<c:out value='${personalIngressionData.student.person.personUlisboaSpecifications.professionTimeType.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.grantOwnerType" />
						</th>
						<td>
							<c:if test="${not empty personalIngressionData.grantOwnerType}">
								<spring:message code="label.GrantOwnerType.${personalIngressionData.grantOwnerType}" />
							</c:if>
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.grantOwnerProvider" />
						</th>
						<td>
							<c:out value='${personalIngressionData.grantOwnerProvider.nameI18n.content}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.motherSchoolLevel" />
						</th>
						<td>
							<c:out value='${personalIngressionData.motherSchoolLevel.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.motherProfessionType" />
						</th>
						<td>
							<c:out value='${personalIngressionData.motherProfessionType.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.motherProfessionalCondition" />
						</th>
						<td>
							<c:out value='${personalIngressionData.motherProfessionalCondition.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.fatherSchoolLevel" />
						</th>
						<td>
							<c:out value='${personalIngressionData.fatherSchoolLevel.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.fatherProfessionType" />
						</th>
						<td>
							<c:out value='${personalIngressionData.fatherProfessionType.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.fatherProfessionalCondition" />
						</th>
						<td>
							<c:out value='${personalIngressionData.fatherProfessionalCondition.localizedName}' />
						</td>
					</tr>

					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.householdSalarySpan" />
						</th>
						<td>
							<c:out value='${personalIngressionData.student.person.personUlisboaSpecifications.householdSalarySpan.localizedName}' />
						</td>
					</tr>

				</tbody>
			</table>
		</form>
	</div>
</div>

