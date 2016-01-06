<%@page import="org.fenixedu.ulisboa.specifications.ui.evaluation.managemarksheet.teacher.CompetenceCourseMarkSheetController"%>
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
		<spring:message code="label.evaluation.manageMarkSheet.createCompetenceCourseMarkSheet" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.SEARCH_URL%>${executionCourse.externalId}"><spring:message
			code="label.event.back" /></a>
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

<script>
    angular
	    .module('angularAppCompetenceCourseMarkSheet',
		    [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
	    .controller(
		    'CompetenceCourseMarkSheetController',
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

				$scope.object = ${competenceCourseMarkSheetBeanJson};
				$scope.form = {};
				$scope.form.object = $scope.object;

				$scope.postBack = createAngularPostbackFunction($scope);

				$scope.object.shifts = [];

				$scope.onBeanChange = function(model) {
					$scope.postBack(model);
				}
				
				$scope.createMarksheet = function() {
					$('#createForm').submit();
				}
								

			    } ]);
</script>

<form id="createForm" name='form' method="post" class="form-horizontal" ng-app="angularAppCompetenceCourseMarkSheet"
	ng-controller="CompetenceCourseMarkSheetController"
	action='${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.CREATE_URL%>${executionCourse.externalId}'>

	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.CREATEPOSTBACK_URL%>${executionCourse.externalId}' />

	<input name="bean" type="hidden" value="{{ object }}" />
	
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.CompetenceCourseMarkSheet.executionSemester" />
				</div>

				<div class="col-sm-6">
					<c:out value="${competenceCourseMarkSheetBean.executionSemester.qualifiedName}"/>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.CompetenceCourseMarkSheet.competenceCourse" />
				</div>

				<div class="col-sm-6">
					<c:out value="${competenceCourseMarkSheetBean.competenceCourse.code}" /> - <c:out value="${competenceCourseMarkSheetBean.competenceCourse.name}" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.CompetenceCourseMarkSheet.evaluationSeason" />
				</div>

				<div class="col-sm-4">
					<ui-select	id="evaluationSeasonSelect" name="evaluationSeason" ng-model="$parent.object.evaluationSeason" theme="bootstrap" on-select="onBeanChange($model)" on-remove="onBeanChange($model)">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices	repeat="evaluationSeason.id as evaluationSeason in object.evaluationSeasonDataSource | filter: $select.search">
							<span ng-bind-html="evaluationSeason.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>

				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.CompetenceCourseMarkSheet.evaluationDate" />
				</div>

				<div class="col-sm-4">
					<input class="form-control" type="text" bennu-date="object.evaluationDate" required="true"/>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.CompetenceCourseMarkSheet.certifier" />
				</div>

				<div class="col-sm-6">
					<ui-select	id="certifierSelect" name="certifier" ng-model="$parent.object.certifier" theme="bootstrap" on-select="onBeanChange($model)" on-remove="onBeanChange($model)">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices	repeat="certifier.id as certifier in object.certifierDataSource | filter: $select.search">
							<span ng-bind-html="certifier.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.CompetenceCourseMarkSheet.shifts" />
				</div>

				<div class="col-sm-6">
					<ui-select	id="shiftsSelect" name="shifts" ng-model="$parent.object.shifts" theme="bootstrap"  on-select="onBeanChange($model)" on-remove="onBeanChange($model)" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices	repeat="shift.id as shift in object.shiftsDataSource | filter: $select.search">
							<span ng-bind-html="shift.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<button type="button" class="btn btn-primary" role="button" ng-click="createMarksheet()"><spring:message code="label.submit" /></button>
		</div>
	</div>
</form>

