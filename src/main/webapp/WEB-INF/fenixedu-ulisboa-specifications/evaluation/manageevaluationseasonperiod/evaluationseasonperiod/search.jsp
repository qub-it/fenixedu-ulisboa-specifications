<%@page import="org.fenixedu.ulisboa.specifications.ui.evaluation.manageevaluationseasonperiod.EvaluationSeasonPeriodController"%>
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
		<spring:message code="label.evaluation.manageEvaluationSeasonPeriod.searchEvaluationSeasonPeriod" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=EvaluationSeasonPeriodController.CREATE_URL%>"><spring:message
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
    angular.module('angularAppEvaluationSeasonPeriod',
	    [ 'ngSanitize', 'ui.select', 'bennuToolkit' ]).controller(
	    'EvaluationSeasonPeriodController', [ '$scope', function($scope) {
		$scope.booleanvalues = [ {
		    name : '<spring:message code="label.no"/>',
		    value : false
		}, {
		    name : '<spring:message code="label.yes"/>',
		    value : true
		} ];

		$scope.object = ${beanJson};
		
		//Begin here of Custom Screen business JS - code
		
		$scope.deletePeriod = function(periodId) {
			$("#deleteConfirmationForm").attr("action", '${pageContext.request.contextPath}<%=EvaluationSeasonPeriodController.DELETE_URL%>' + periodId);
			$('#deleteConfirmation').modal('toggle')
		}


	    } ]);
</script>

<div class="modal fade" id="deleteConfirmation">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="deleteConfirmationForm" method="POST" action="">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body"> 
					<p><spring:message code="label.delete.confirm"/></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.no" />
					</button>
					<button class="btn btn-danger" type="submit">
						<spring:message code="label.yes" />
					</button>
				</div>
			</form>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<form name='form' method="post" class="form-horizontal" ng-app="angularAppEvaluationSeasonPeriod"
	ng-controller="EvaluationSeasonPeriodController"
	action='${pageContext.request.contextPath}<%=EvaluationSeasonPeriodController.SEARCH_URL%>'>
		<div class="panel panel-default">
		
				<input name="bean" type="hidden" value="{{ object }}" />
		
				<div class="panel-body">
					<div class="form-group row">
						<div class="col-sm-2 control-label">
							<spring:message code="label.EvaluationSeasonPeriod.executionYear" />
						</div>
		
						<div class="col-sm-4">
							<ui-select id="executionYear" name="executionYear" ng-model="object.executionYear" theme="bootstrap"> <ui-select-match>{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="iterator.id as iterator in object.executionYearDataSource | filter: $select.search">
							<span ng-bind-html="iterator.text | highlight: $select.search"></span> </ui-select-choices> </ui-select>
						</div>
					</div>
					<div class="form-group row">
						<div class="col-sm-2 control-label">
							<spring:message code="label.EvaluationSeasonPeriod.periodType" />
						</div>
		
						<div class="col-sm-4">
							<ui-select id="periodType" name="periodType" ng-model="object.periodType" theme="bootstrap"> <ui-select-match>{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="iterator.id as iterator in object.periodTypeDataSource | filter: $select.search">
							<span ng-bind-html="iterator.text | highlight: $select.search"></span> </ui-select-choices> </ui-select>
						</div>
					</div>
					<div class="form-group row">
						<div class="col-sm-2 control-label">
							<spring:message code="label.EvaluationSeasonPeriod.season" />
						</div>
		
						<div class="col-sm-4">
							<ui-select id="season" name="season" ng-model="object.season" theme="bootstrap"> <ui-select-match>{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="iterator.id as iterator in object.seasonDataSource | filter: $select.search">
							<span ng-bind-html="iterator.text | highlight: $select.search"></span> </ui-select-choices> </ui-select>
						</div>
					</div>
				</div>
				<div class="panel-footer">
					<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.search" />" />
				</div>
		</div>
		
		
		<c:choose>
			<c:when test="${not empty searchResultsDataSet}">
				<table id="searchTable" class="table responsive table-bordered table-hover" width="100%">
					<thead>
						<tr>
							<th><spring:message code="label.EvaluationSeasonPeriod.executionSemester" /></th>
							<th><spring:message code="label.EvaluationSeasonPeriod.periodType" /></th>
							<th><spring:message code="label.EvaluationSeasonPeriod.season" /></th>
							<th><spring:message code="label.EvaluationSeasonPeriod.intervals" /></th>
							<th><spring:message code="label.EvaluationSeasonPeriod.degrees" /></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="var" items="${searchResultsDataSet}">
							<tr>
								<td><c:out value="${var.executionSemester.qualifiedName}"></c:out></td>
								<td><c:out value="${var.periodType.descriptionI18N.content}"></c:out></td>
								<td><c:out value="${var.season.name.content}"></c:out></td>
								<td><c:out value="${var.intervalsDescription}"></c:out></td>
								<td><c:out value="${var.degreesDescription}"></c:out></td>
								<td>
									<a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%=EvaluationSeasonPeriodController.UPDATEDEGREES_URL%>${var.externalId}">
										<spring:message	code='label.event.evaluation.manageEvaluationSeasonRule.updateDegrees' />
									</a>&nbsp;&nbsp;
									<a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%=EvaluationSeasonPeriodController.UPDATEINTERVALS_URL%>${var.externalId}">
										<spring:message	code='label.event.evaluation.manageEvaluationSeasonRule.updateIntervals' />
									</a>&nbsp;&nbsp;
									<button class="btn btn-danger btn-xs" role="button" type="button" ng-click="deletePeriod('${var.externalId}')"><spring:message code="label.delete"/></button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<script type="text/javascript">
			    createDataTables('searchTable', true /*filterable*/,
				    false /*show tools*/, false /*paging*/, 
				    "${pageContext.request.contextPath}",
				    "${datatablesI18NUrl}");
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
