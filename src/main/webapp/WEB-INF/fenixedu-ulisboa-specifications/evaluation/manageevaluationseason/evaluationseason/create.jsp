<%@page
	import="org.fenixedu.ulisboa.specifications.ui.evaluation.manageevaluationseason.EvaluationSeasonController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables"
	uri="http://github.com/dandelion/datatables"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<spring:url var="datatablesUrl"
	value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
	value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
	value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl"
	value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.angularToolkit()}

<link
	href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link
	href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link
	href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script
	src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script
	src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script
	src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message
			code="label.evaluation.manageEvaluationSeason.createEvaluationSeason" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}<%=EvaluationSeasonController.CONTROLLER_URL%>"><spring:message
			code="label.event.back" /></a>
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
				<span class="glyphicon glyphicon-exclamation-sign"
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

<script>
    angular
	    .module('angularAppEvaluationSeason',
		    [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
	    .controller(
		    'EvaluationSeasonController',
		    [
			    '$scope',
			    function($scope) {
				$scope.booleanvalues = [
					{
					    name : '<spring:message code="label.no"/>',
					    value : false
					},
					{
					    name : '<spring:message code="label.yes"/>',
					    value : true
					} ];

				$scope.object = ${evaluationSeasonBeanJson};
				$scope.postBack = createAngularPostbackFunction($scope);

				//Begin here of Custom Screen business JS - code

			    } ]);
</script>

<form name='form' method="post" class="form-horizontal"
	ng-app="angularAppEvaluationSeason"
	ng-controller="EvaluationSeasonController"
	action='${pageContext.request.contextPath}<%=EvaluationSeasonController.CREATE_URL%>'>

	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}<%=EvaluationSeasonController.CREATEPOSTBACK_URL%>' />

	<input name="bean" type="hidden" value="{{ object }}" />
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.EvaluationSeason.name" />
				</div>

				<div class="col-sm-10">
					<input id="evaluationSeason_name" class="form-control" type="text"
						name="name" ng-localized-string="object.name" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.EvaluationSeason.acronym" />
				</div>

				<div class="col-sm-10">
					<input id="evaluationSeason_acronym" class="form-control"
						type="text" name="acronym" ng-localized-string="object.acronym" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.EvaluationSeason.code" />
				</div>

				<div class="col-sm-10">
					<input id="evaluationSeason_code" class="form-control" type="text"
						ng-model="object.code" name="code" required />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.EvaluationSeason.active" />
				</div>

				<div class="col-sm-2">
					<select id="evaluationSeason_active" name="active"
						class="form-control" ng-model="object.active"
						ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.EvaluationSeason.normal" />
				</div>

				<div class="col-sm-2">
					<select id="evaluationSeason_normal" name="normal"
						class="form-control" ng-model="object.normal"
						ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.EvaluationSeason.special" />
				</div>

				<div class="col-sm-2">
					<select id="evaluationSeason_special" name="special"
						class="form-control" ng-model="object.special"
						ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.EvaluationSeason.improvement" />
				</div>

				<div class="col-sm-2">
					<select id="evaluationSeason_improvement" name="improvement"
						class="form-control" ng-model="object.improvement"
						ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.EvaluationSeason.specialAuthorization" />
				</div>

				<div class="col-sm-2">
					<select id="evaluationSeason_specialAuthorization"
						name="specialAuthorization" class="form-control"
						ng-model="object.specialAuthorization"
						ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button"
				value="<spring:message code="label.submit" />" />
		</div>
	</div>
</form>

<script>
    $(document).ready(function() {

	// Put here the initializing code for page
    });
</script>
