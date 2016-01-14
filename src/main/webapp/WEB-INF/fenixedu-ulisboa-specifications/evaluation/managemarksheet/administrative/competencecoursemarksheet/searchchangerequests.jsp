<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet"%>
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
				$scope.postBack = createAngularPostbackFunction($scope);
				
				
				$scope.showAuthorizeDialog = function(changeRequestId) {
					$('#searchChangeRequestsForm').attr('action', '${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.AUTHORIZE_CHANGE_REQUEST_URL%>${competenceCourseMarkSheet.externalId}' + '/' + changeRequestId)
					$('#authorizeDialog').modal('toggle');
				}
				
				$scope.authorize = function() {
					$('#searchChangeRequestsForm').submit();
				}
				
				$scope.showCloseDialog = function(changeRequestId) {
					$('#searchChangeRequestsForm').attr('action', '${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.CLOSE_CHANGE_REQUESTS_URL%>${competenceCourseMarkSheet.externalId}' + '/' + changeRequestId)
					$('#closeDialog').modal('toggle');
				}
				
				$scope.close = function() {
					$('#searchChangeRequestsForm').submit();
				}
				
			
			    } ]);
</script>

<div ng-app="angularAppCompetenceCourseMarkSheet" ng-controller="CompetenceCourseMarkSheetController">

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.evaluation.manageMarkSheet.changeRequests" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.READ_URL%>${competenceCourseMarkSheet.externalId}"><spring:message
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




<form id="searchChangeRequestsForm" name='form' method="post" class="form-horizontal" action="">

	<div class="modal fade" id="authorizeDialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.authorize" />
					</h4>
				</div>
				<div class="modal-body">
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheet.expireDate" />
						</div>
						
						<div class="col-sm-5">
							<input class="form-control" type="text" bennu-date="object.expireDate" />
						</div>
					</div>
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.comments" />
						</div>
						
						<div class="col-sm-5">
							<textarea rows="10" cols="30" ng-model="object.changeRequestComments"></textarea>
						</div>
					</div> 
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.cancel" />
					</button>
					<button class="btn btn-success" type="button" ng-click="authorize()">
						<spring:message code="label.authorize" />
					</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->
	
	<div class="modal fade" id="closeDialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.close" />
					</h4>
				</div>
				<div class="modal-body">
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.comments" />
						</div>
						
						<div class="col-sm-5">
							<textarea rows="10" cols="30" ng-model="object.changeRequestComments"></textarea>
						</div>
					</div> 
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.cancel" />
					</button>
					<button class="btn btn-danger" type="button" ng-click="close()">
						<spring:message code="label.close" />
					</button>
				</div>
			</div>
			<!-- /.modal-content -->
		</div>
		<!-- /.modal-dialog -->
	</div>
	<!-- /.modal -->

	<input name="bean" type="hidden" value="{{ object }}" />
	
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
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.executionSemester" /></th>
						<td><c:out value="${competenceCourseMarkSheet.executionSemester.qualifiedName}"/></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.competenceCourse" /></th>
						<td><c:out value="${competenceCourseMarkSheet.competenceCourse.code}"/> - <c:out value="${competenceCourseMarkSheet.competenceCourse.nameI18N.content}"/></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.evaluationSeason" /></th>
						<td><c:out value="<%=EvaluationSeasonServices.getDescriptionI18N(((CompetenceCourseMarkSheet)request.getAttribute("competenceCourseMarkSheet")).getEvaluationSeason()).getContent()%>"/></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.evaluationDate" /></th>
						<td><joda:format value="${competenceCourseMarkSheet.evaluationDate}" style="S-"/></td>
					</tr>
					<c:if test="${not empty competenceCourseMarkSheet.expireDate}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.expireDate" /></th>
						<td><joda:format value="${competenceCourseMarkSheet.expireDate}" style="S-"/></td>
					</tr>
					</c:if>
				</tbody>
			</table>
		</div>
	</div>
	

	<div class="panel panel-default">
		<div class="panel-body">
		
			<table id="changeRequestsDataTable" class="table table-bordered table-hover" width="100%">
				<thead>
					<tr>
						<th><spring:message code="label.CompetenceCourseMarkSheetChangeRequest.requestDate" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheetChangeRequest.requester" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheetChangeRequest.reason" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheetChangeRequest.responder" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheetChangeRequest.responseDate" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheetChangeRequest.comments" /></th>
						<th><spring:message code="label.CompetenceCourseMarkSheetChangeRequest.state" /></th>
						<th> </th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="changeRequest" items="${competenceCourseMarkSheet.sortedChangeRequests}">
					<tr>
						<td><joda:format value="${changeRequest.requestDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
						<td><c:out value="${changeRequest.requester.firstAndLastName}" /></td>
						<td><pre style="border:none;background-color: inherit;white-space: pre-wrap;"><c:out value="${changeRequest.reason}" /></pre></td>
						<td><c:out value="${changeRequest.responder.firstAndLastName}" /></td>
						<td><joda:format value="${changeRequest.responseDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
						<td><pre style="border:none;background-color: inherit;white-space: pre-wrap;"><c:out value="${changeRequest.comments}" /></pre></td>
						<td><c:out value="${changeRequest.state.descriptionI18N.content}" /></td>
						<td>
							<c:if test="${changeRequest.pending}">
								<a href="#" class="btn btn-success btn-xs" ng-click="showAuthorizeDialog('${changeRequest.externalId}')"><spring:message code="label.authorize" /></a>&nbsp;
								<a href="#" class="btn btn-danger btn-xs" ng-click="showCloseDialog('${changeRequest.externalId}')"><spring:message code="label.close" /></a>
							</c:if>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<script type="text/javascript">
			createDataTablesWithSortSwitch('changeRequestsDataTable',true /*filterable*/, false /*show tools*/, false /*paging*/,false /*sortable*/, "${pageContext.request.contextPath}","${datatablesI18NUrl}");
			</script>
		
						
		</div>
	</div>
</form>

</div>
