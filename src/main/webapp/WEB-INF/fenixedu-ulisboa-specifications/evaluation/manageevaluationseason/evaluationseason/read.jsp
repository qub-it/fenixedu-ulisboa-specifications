<%@page import="org.fenixedu.academic.domain.EvaluationSeason"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.evaluation.manageevaluationseason.EvaluationSeasonController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.angularToolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css"
	rel="stylesheet" />
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
		<spring:message code="label.evaluation.manageEvaluationSeason.readEvaluationSeason" />
		<small></small>
	</h1>
</div>
<div class="modal fade" id="deleteModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="deleteForm"
				action="${pageContext.request.contextPath}<%=EvaluationSeasonController.DELETE_URL%>${evaluationSeason.externalId}"
				method="POST">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body">
					<p>
						<spring:message code="label.evaluation.manageEvaluationSeason.readEvaluationSeason.confirmDelete" />
					</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.close" />
					</button>
					<button id="deleteButton" class="btn btn-danger" type="submit">
						<spring:message code="label.delete" />
					</button>
				</div>
			</form>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=EvaluationSeasonController.CONTROLLER_URL%>"><spring:message
			code="label.event.back" /></a> &nbsp;&nbsp;|&nbsp;&nbsp; <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;<a
		class="" href="#" data-toggle="modal" data-target="#deleteModal"><spring:message code="label.event.delete" /></a>
	&nbsp;&nbsp;|&nbsp;&nbsp; <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=EvaluationSeasonController.UPDATE_URL%>${evaluationSeason.externalId}"><spring:message
			code="label.event.update" /></a> &nbsp;&nbsp;|&nbsp;&nbsp; <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}<%=EvaluationSeasonController.READ_URL%>${evaluationSeason.externalId}/readrules"><spring:message
			code="label.event.evaluation.manageEvaluationSeason.readRules" /></a>
</div>
<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
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
						<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeason.name" /></th>
						<td><c:out value='${evaluationSeason.name.content}' /></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeason.acronym" /></th>
						<td><c:out value='${evaluationSeason.acronym.content}' /></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeason.code" /></th>
						<td><c:out value='${evaluationSeason.code}' /></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeason.active" /></th>
						<td><c:if test="${evaluationSeason.information.active}">
								<spring:message code="label.true" />
							</c:if> <c:if test="${not evaluationSeason.information.active}">
								<spring:message code="label.false" />
							</c:if></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeason.type" /></th>
						<td><c:out
								value="<%=EvaluationSeasonServices
						.getTypeDescriptionI18N((EvaluationSeason) request.getAttribute("evaluationSeason"))
						.getContent()%>"></c:out></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeason.requiresEnrolmentEvaluation" /></th>
						<td><c:if
								test="<%=EvaluationSeasonServices
						.isRequiresEnrolmentEvaluation((EvaluationSeason) request.getAttribute("evaluationSeason"))%>">
								<spring:message code="label.true" />
							</c:if> <c:if
								test="<%=!EvaluationSeasonServices
						.isRequiresEnrolmentEvaluation((EvaluationSeason) request.getAttribute("evaluationSeason"))%>">
								<spring:message code="label.false" />
							</c:if></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<script>
    $(document).ready(function() {

    });
</script>
