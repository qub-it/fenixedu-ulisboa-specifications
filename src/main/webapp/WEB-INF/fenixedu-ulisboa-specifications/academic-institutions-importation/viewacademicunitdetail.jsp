<%@page import="org.fenixedu.ulisboa.specifications.ui.legal.academicinstitutions.importation.AcademicInstitutionsImportationController"%>
<%@page import="org.fenixedu.academic.domain.Country"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/css/select2.min.css" rel="stylesheet" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/js/select2.full.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.AcademicInstitutionsImportationController.viewacademicunits.title" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
	<a class="" href="${pageContext.request.contextPath}/<%= AcademicInstitutionsImportationController.UPLOAD_ACADEMIC_UNITS_FILE_URL %>">
		<spring:message code="label.back"/>
	</a>
</div>

<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"${pageContext.request.contextPath}
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>

<table class="table" id="academicUnitsTable" >
	<tbody>
		<tr>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsImportationController.officialCode" /></th>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsImportationController.name" /></th>
			<th scope="row" class="col-xs-3"><spring:message code="label.AcademicInstitutionsImportationController.degreeDesignations.number" /></th>
			<th></th>
		</tr>

		<c:forEach var="unit" items="${academicUnits}">
			<tr>
				<td><c:out value="${unit.nameI18n.content}" /></td>
				<td><c:out value="${unit.degreeDesignationSet.size()}" /></td>
				<td>
					<a href="${pageContext.request.contextPath}/<%= AcademicInstitutionsImportationController.VIEW_ACADEMIC_UNIT_DETAIL_URL %>"><spring:message code="label.view" /></a>
				</td>
			</tr>
		</c:forEach>

	</tbody>
</table>
<script>
	createDataTables(
			"academicUnitsTable",
			false,
			false,
			false,
			"${pageContext.request.contextPath}",
			"${datatablesI18NUrl}");
</script>

<script>

$(document).ready(function() {
	
});

</script>
