<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page
	import="org.fenixedu.ulisboa.specifications.ui.evaluation.managemarksheet.administrative.CompetenceCourseMarkSheetController"%>
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
		<spring:message code="label.evaluation.manageMarkSheet.searchCompetenceCourseMarkSheet" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.CREATE_URL%>"><spring:message
			code="label.event.create" /></a>
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

				//Begin here of Custom Screen business JS - code
				
				$scope.onBeanChange = function(model) {
					
					$scope.object.competenceCourse = '';
					
					$scope.postBack(model);
				}
				
				$scope.search  = function() {
					$('#searchForm').submit();
				}

			    } ]);
</script>



<form id="searchForm" name='form' method="post" class="form-horizontal" ng-app="angularAppCompetenceCourseMarkSheet" ng-controller="CompetenceCourseMarkSheetController"
action="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.SEARCH_URL%>">

	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.SEARCHPOSTBACK_URL%>${competenceCourseMarkSheet.externalId}' />

	<input name="bean" type="hidden" value="{{ object }}" />
	
	<div class="panel panel-default">
			<div class="panel-body">
				<div class="form-group row">
					<div class="col-sm-2 control-label">
						<spring:message code="label.CompetenceCourseMarkSheet.executionSemester" />
					</div>
	
					<div class="col-sm-6">
						<ui-select	id="executionSemesterSelect" name="executionSemester" ng-model="$parent.object.executionSemester" theme="bootstrap" on-select="onBeanChange($model)" on-remove="onBeanChange($model)">
							<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
							<ui-select-choices	repeat="executionSemester.id as executionSemester in object.executionSemesterDataSource | filter: $select.search">
								<span ng-bind-html="executionSemester.text | highlight: $select.search"></span>
							</ui-select-choices> 
						</ui-select>
	
					</div>
				</div>
				<div class="form-group row">
					<div class="col-sm-2 control-label">
						<spring:message code="label.CompetenceCourseMarkSheet.competenceCourse" />
					</div>
					<div class="col-sm-6">
						<ui-select	id="competenceCourseSelected" name="competenceCourse" ng-model="$parent.object.competenceCourse" theme="bootstrap">
							<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
							<ui-select-choices	repeat="competenceCourse.id as competenceCourse in object.competenceCourseDataSource | filter: $select.search">
								<span ng-bind-html="competenceCourse.text | highlight: $select.search"></span>
							</ui-select-choices> 
						</ui-select>
		
					</div>
				</div>
				<div class="form-group row">
					<div class="col-sm-2 control-label">
						<spring:message code="label.CompetenceCourseMarkSheet.state" />
					</div>
					
					<div class="col-sm-6">
						<ui-select	id="markSheetStateSelect" name="markSheetState" ng-model="$parent.object.markSheetState" theme="bootstrap">
							<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
							<ui-select-choices	repeat="markSheetState.id as markSheetState in object.markSheetStateDataSource | filter: $select.search">
								<span ng-bind-html="markSheetState.text | highlight: $select.search"></span>
							</ui-select-choices> 
						</ui-select>
					</div>
				</div>
			</div>
			<div class="panel-footer">
				<button type="button" class="btn btn-primary" role="button" ng-click="search()"><spring:message code="label.search" /></button> 
			</div>
	</div>
	
	
	<c:choose>
		<c:when test="${not empty searchcompetencecoursemarksheetResultsDataSet}">
			<spring:message code="label.yes" var="yesLabel"/>
			<spring:message code="label.no" var="noLabel"/>
		
			<table id="searchcompetencecoursemarksheetTable" class="table table-bordered table-hover" width="100%">
				<thead>
					<tr>
						<th><spring:message code="label.CompetenceCourseMarkSheet.creationDate" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheet.competenceCourse" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheet.evaluationSeason" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheet.evaluationDate" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheet.state" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheet.certifier" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheet.shifts" /></th>
						<%-- Operations Column --%>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="searchResult" items="${searchcompetencecoursemarksheetResultsDataSet}">
					<tr>
						<td><joda:format value="${searchResult.creationDate}" style="S-" /></td>
						<td><c:out value="${searchResult.competenceCourse.code}" /> - <c:out value="${searchResult.competenceCourse.nameI18N.content}" /></td>
						<td><c:out value="<%=EvaluationSeasonServices.getDescriptionI18N(((CompetenceCourseMarkSheet)pageContext.getAttribute("searchResult")).getEvaluationSeason()).getContent()%>"></c:out></td>
						<td><joda:format value="${searchResult.evaluationDate}" style="S-"/></td>
						<td><c:out value='${searchResult.state}'/></td>
						<td><c:out value='${searchResult.certifier.firstAndLastName}'/></td>
						<td><c:out value='${searchResult.shiftsDescription}'/></td>
						<td>
							<a  class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.SEARCH_TO_VIEW_ACTION_URL%>${searchResult.externalId}"><spring:message code='label.view'/></a>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<script type="text/javascript">
				createDataTables('searchcompetencecoursemarksheetTable',true /*filterable*/, false /*show tools*/, true /*paging*/, "${pageContext.request.contextPath}","${datatablesI18NUrl}");
			</script>
					
		</c:when>
		
		<c:otherwise>
			<div class="alert alert-warning" role="alert">
	
				<p>
					<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
					<spring:message code="label.noResultsFound" />
				</p>
	
			</div>
	
		</c:otherwise>
	</c:choose>
</form>
