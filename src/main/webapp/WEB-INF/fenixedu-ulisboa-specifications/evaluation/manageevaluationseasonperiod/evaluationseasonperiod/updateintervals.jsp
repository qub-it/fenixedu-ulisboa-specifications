<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonPeriod"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices"%>
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
		<spring:message code="label.evaluation.manageEvaluationSeasonPeriod.updateEvaluationSeasonPeriod" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=EvaluationSeasonPeriodController.SEARCH_URL%>/"><spring:message
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
	
    angular.module('angularAppPeriod',
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
		$scope.postBack = createAngularPostbackFunction($scope);

		//Begin here of Custom Screen business JS - code
		
		$scope.isUndefinedOrNull = function(val){
			return angular.isUndefined(val) || val === null
		}

		$scope.addInterval = function() {

		    if ($scope.isUndefinedOrNull($scope.object.start) || $scope.isUndefinedOrNull($scope.object.end)) {
				return;				
			}
		    
			$('#form').attr('action','${pageContext.request.contextPath}<%=EvaluationSeasonPeriodController.UPDATEINTERVALS_URL%>${period.externalId}/add');
			$('#form').submit();	
		}

		$scope.removeInterval = function(start,end) {
			$("#deleteConfirmationForm").attr("action", '${pageContext.request.contextPath}<%=EvaluationSeasonPeriodController.UPDATEINTERVALS_URL%>${period.externalId}/remove/' + start + '/' + end);
			$('#deleteConfirmation').modal('toggle')

		}
	 }
]);
</script>

<div class="modal fade" id="deleteConfirmation">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="deleteConfirmationForm" method="POST">
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
					<p id="modalMessage"><spring:message code="label.delete.confirm"></spring:message></p>
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

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:message code="label.details" />
		</h3>
	</div>
	<div class="panel-body">
	
		<table class="table">
			<tbody>
				<tr>
					<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeasonPeriod.executionSemester" /></th>
					<td><c:out value='${period.executionSemester.qualifiedName}' /></td>
				</tr>
				<tr>
					<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeasonPeriod.periodType" /></th>
					<td><c:out value='${period.periodType.descriptionI18N.content}' /></td>
				</tr>
				<tr>
					<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeasonPeriod.season" /></th>
					<td><c:out
							value='<%=EvaluationSeasonServices
					.getDescriptionI18N(((EvaluationSeasonPeriod) request.getAttribute("period")).getSeason())
					.getContent()%>' /></td>
				</tr>
				<tr>
					<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeasonPeriod.intervals" /></th>
					<td><c:out value='${period.intervalsDescription}' /></td>
				</tr>
				<tr>
					<th scope="row" class="col-xs-3"><spring:message code="label.EvaluationSeasonPeriod.degrees" /></th>
					<td><c:out value='${period.degreesDescription}' /></td>
				</tr>
			</tbody>
		</table>
	</div>
</div>

<form id="form" name='form' method="post" class="form-horizontal" ng-app="angularAppPeriod"
	ng-controller="EvaluationSeasonPeriodController" action="">

	<input name="bean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		<div class="panel-body">
		
			<div class="row">
				<div class="col-sm-1">
					<spring:message code="label.EvaluationSeasonPeriod.start" />
				</div>
				
				<div class="col-sm-3">
					<input class="form-control" type="text" bennu-date="object.start" />
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-1">
					<spring:message code="label.EvaluationSeasonPeriod.end" />
				</div>
				
				<div class="col-sm-3">
					<input class="form-control" type="text" bennu-date="object.end" />
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-1">
				</div>
								
				<div class="col-sm-3">
					<button class="glyphicon glyphicon-plus-sign btn btn-default" type="button" ng-click="addInterval()">
						<spring:message code="label.add" />
					</button>
				</div>
			</div>
		

			<table id="degreesDataTable" class="table responsive table-bordered table-hover" width="100%">
				<thead>
					<tr>
						<th><spring:message code="label.EvaluationSeasonPeriod.start" /></th>
						<th><spring:message code="label.EvaluationSeasonPeriod.end" /></th>
						<th> </th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${bean.period.intervals}">
						<tr>
							<td><joda:format value="${item.start}" style="S-"/></td>
							<td><joda:format value="${item.end}" style="S-"/></td>
							<td>
								<button class="btn btn-danger btn-xs" type="button" role="button" ng-click="removeInterval('${item.start.toLocalDate()}','${item.end.toLocalDate()}')"> 
									<spring:message	code='label.remove' />
								</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<script type="text/javascript">
		createDataTablesWithSortSwitch('degreesDataTable',
			true /*filterable*/, false /*show tools*/,
			false /*paging*/, true /*sortable*/,
			"${pageContext.request.contextPath}",
			"${datatablesI18NUrl}");
	    </script>
		</div>
	</div>


</form>

<script>
    $(document).ready(function() {

    });
</script>
