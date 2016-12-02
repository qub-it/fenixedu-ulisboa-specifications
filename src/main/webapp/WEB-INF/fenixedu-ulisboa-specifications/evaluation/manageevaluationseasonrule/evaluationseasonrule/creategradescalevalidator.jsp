<%@page
	import="org.fenixedu.ulisboa.specifications.ui.evaluation.manageevaluationseasonrule.EvaluationSeasonRuleController"%>
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
			code="label.evaluation.manageEvaluationSeasonRule.createEvaluationSeasonRule" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}<%=EvaluationSeasonRuleController.SEARCH_URL%>${evaluationSeasonRuleBean.season.externalId}"><spring:message
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
	    .module('angularAppEvaluationSeasonRule',
		    [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
	    .controller(
		    'EvaluationSeasonRuleController',
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

				$scope.object = ${evaluationSeasonRuleBeanJson};
				$scope.postBack = createAngularPostbackFunction($scope);
			
				
				$scope.submit = function(){
					$('#createForm').submit();	
				}

				//Begin here of Custom Screen business JS - code

			    } ]);
</script>

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
						<th scope="row" class="col-xs-3"><spring:message
								code="EvaluationSeason" /></th>
						<td><c:out
								value='${evaluationSeasonRuleBean.seasonDescriptionI18N.content}' /></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="EvaluationSeasonRule" /></th>
						<td><c:out
								value='${evaluationSeasonRuleBean.descriptionI18N.content}' /></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<form id="createForm" name='form' method="post" class="form-horizontal"
	ng-app="angularAppEvaluationSeasonRule"
	ng-controller="EvaluationSeasonRuleController"
	action='${pageContext.request.contextPath}<%=EvaluationSeasonRuleController.CREATEGRADESCALEVALIDATOR_URL%>'>

	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}<%=EvaluationSeasonRuleController.CREATEGRADESCALEVALIDATORPOSTBACK_URL%>' />

	<input name="bean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		<div class="panel-body">
		
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.gradeScale" />
				</div>
	
				<div class="col-sm-6">
					<ui-select	id="gradeScaleSelect" name="gradeScale" ng-model="$parent.object.gradeScale" theme="bootstrap">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices	repeat="gradeScale.id as gradeScale in object.gradeScaleDataSource | filter: $select.search">
							<span ng-bind-html="gradeScale.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.gradeValues" />
				</div>
				
				<div class="col-sm-6">
					<input id="gradeValues" name="gradeValues" ng-model="object.gradeValues" class="form-control" type="text"/>
				</div>
			</div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.appliesToCurriculumAggregatorEntry" />
                </div>

                <div class="col-sm-2">
                    <select id="appliesToCurriculumAggregatorEntry" name="appliesToCurriculumAggregatorEntry"
                        class="form-control" ng-model="object.appliesToCurriculumAggregatorEntry"
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
                    </select>
                </div>
            </div>
			
			<div class="form-group row">
			
				<div class="col-sm-2 control-label">
					<spring:message code="label.Degree.degreeType" />
				</div>
				
				<div class="col-sm-6">
					<ui-select	id="degreeTypesSelect" name="degreeTypes" ng-model="$parent.object.degreeTypes" theme="bootstrap"  multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices	repeat="degreeType.id as degreeType in object.degreeTypesDataSource | filter: $select.search">
							<span ng-bind-html="degreeType.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
				
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ruleDescription" />
				</div>
				
				<div class="col-sm-6">
					<input id="ruleDescription" class="form-control" type="text" ng-localized-string="object.ruleDescription" name="ruleDescription"   />
				</div>
			</div>

		</div>
		<div class="panel-footer">
			<button type="button" class="btn btn-primary" role="button" ng-click="submit()"><spring:message code="label.submit" /></button>
		</div>
	</div>
</form>


