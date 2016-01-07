<%@page import="org.fenixedu.ulisboa.specifications.ui.legal.report.raides.RaidesConfigurationController"%>
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

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%--${portal.toolkit()}--%>

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageRaidesConfiguration.read" />
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">

	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= RaidesConfigurationController.EDIT_URL %>">
		<spring:message code="label.event.update" />
	</a>
</div>

<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
				${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				${message}
			</p>
		</c:forEach>

	</div>
</c:if>

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
							<spring:message code="label.RaidesInstance.passwordToZip" />
						</th>
						<td>
							<c:out value='${raidesInstance.passwordToZip}' />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<h3><spring:message code="label.RaidesInstance.enrolledAgreements" /></h3>

<c:choose>
	<c:when test="${not empty raidesInstance.enrolledAgreementsSet}">
		<table id="simpletable"
			class="table responsive table-bordered table-hover">
			<thead>
				<tr class="row">
					<th class="col-sm-2"><spring:message code="label.RaidesInstance.agreementCode" /></th>
					<th><spring:message code="label.RaidesInstance.agreementName" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="a" items="${raidesInstance.enrolledAgreementsSet}">
					<tr class="row">
						<td class="col-sm-2"><c:out value="${a.code}" /></td>
						<td><c:out value="${a.description.content}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<h3><spring:message code="label.RaidesInstance.mobilityAgreements" /></h3>

<c:choose>
	<c:when test="${not empty raidesInstance.mobilityAgreementsSet}">
		<table id="simpletable"
			class="table responsive table-bordered table-hover">
			<thead>
				<tr class="row">
					<th class="col-sm-2"><spring:message code="label.RaidesInstance.agreementCode" /></th>
					<th><spring:message code="label.RaidesInstance.agreementName" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="a" items="${raidesInstance.mobilityAgreementsSet}">
					<tr class="row">
						<td class="col-sm-2"><c:out value="${a.code}" /></td>
						<td><c:out value="${a.description.content}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<h3><spring:message code="label.RaidesInstance.degreeChangeIngressions" /></h3>

<c:choose>
	<c:when test="${not empty raidesInstance.degreeChangeIngressionsSet}">
		<table id="simpletable"
			class="table responsive table-bordered table-hover">
			<thead>
				<tr class="row">
					<th class="col-sm-2"><spring:message code="label.RaidesInstance.ingressionCode" /></th>
					<th><spring:message code="label.RaidesInstance.ingressionName" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="ingressionType" items="${raidesInstance.degreeChangeIngressionsSet}">
					<tr class="row">
						<td class="col-sm-2"><c:out value="${ingressionType.code}" /></td>
						<td><c:out value="${ingressionType.description.content}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<h3><spring:message code="label.RaidesInstance.degreeTransferIngressions" /></h3>

<c:choose>
	<c:when test="${not empty raidesInstance.degreeTransferIngressionsSet}">
		<table id="simpletable"
			class="table responsive table-bordered table-hover">
			<thead>
				<tr class="row">
					<th class="col-sm-2"><spring:message code="label.RaidesInstance.ingressionCode" /></th>
					<th><spring:message code="label.RaidesInstance.ingressionName" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="ingressionType" items="${raidesInstance.degreeTransferIngressionsSet}">
					<tr class="row">
						<td class="col-sm-2"><c:out value="${ingressionType.code}" /></td>
						<td><c:out value="${ingressionType.description.content}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<h3><spring:message code="label.RaidesInstance.generalAccessRegimeIngressions" /></h3>

<c:choose>
	<c:when test="${not empty raidesInstance.generalAccessRegimeIngressionsSet}">
		<table id="simpletable"
			class="table responsive table-bordered table-hover">
			<thead>
				<tr class="row">
					<th class="col-sm-2"><spring:message code="label.RaidesInstance.ingressionCode" /></th>
					<th><spring:message code="label.RaidesInstance.ingressionName" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="ingressionType" items="${raidesInstance.generalAccessRegimeIngressionsSet}">
					<tr class="row">
						<td class="col-sm-2"><c:out value="${ingressionType.code}" /></td>
						<td><c:out value="${ingressionType.description.content}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<script>
	$(document).ready(function() {

	});
</script>
