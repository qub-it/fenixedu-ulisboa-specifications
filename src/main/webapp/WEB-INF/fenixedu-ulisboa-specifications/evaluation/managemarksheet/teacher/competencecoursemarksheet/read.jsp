<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheetStateChange"%>
<%@page import="org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.CompetenceCourseMarkSheetBean"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet"%>
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

<script type="text/javascript">
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
	
					$scope.postBack = createAngularPostbackFunction($scope);
	
					//Begin here of Custom Screen business JS - code
					
					$scope.submitMarkSheet = function() {
						$scope.showConfirmation('${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.SUBMIT_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}', 
								'<spring:message code="label.evaluation.manageMarkSheet.administrative.readCompetenceCourseMarkSheet.confirmSubmit.teacher" />', 
								'<spring:message code="label.event.evaluation.manageMarkSheet.submit" />');
					}
					
                    $scope.confirmMarkSheet = function() {                      
                        $scope.showConfirmation('${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.CONFIRM_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}', 
                                '<spring:message code="label.evaluation.manageMarkSheet.administrative.readCompetenceCourseMarkSheet.confirmConfirm" />', 
                                '<spring:message code="label.event.evaluation.manageMarkSheet.confirm" />');
                    }
                    
					$scope.deleteMarkSheet = function() {	
						$scope.showConfirmation('${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.DELETE_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}', 
								'<spring:message code="label.evaluation.manageMarkSheet.administrative.readCompetenceCourseMarkSheet.confirmDelete" />', 
								'<spring:message code="label.remove" />');
					}
					
					$scope.showConfirmation = function(url,message,actionText) {
						$('#confirmationForm').attr('action', url);
						$('#confirmationMessage').html(message);
						$('#confirmationButton').html(actionText);
						$('#confirmationModal').modal('toggle');
					}
					
					$scope.showLastChangeRequestDetails = function(){
						$('#lastChangeRequestDetailsDialog').modal('toggle');
					}
					
					$scope.showCreateChangeRequest = function(){
						$('#createChangeRequestDialog').modal('toggle');
					}
					
					$scope.createChangeRequest = function(){
						if ($scope.reason != null && $scope.reason != undefined){							
							$('#createChangeRequestForm').submit();
						}
					}
					
					
					

			   }]);
</script>


<div ng-app="angularAppCompetenceCourseMarkSheet" ng-controller="CompetenceCourseMarkSheetController">

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.evaluation.manageMarkSheet.readCompetenceCourseMarkSheet" />
		<small></small>
	</h1>
</div>
<div class="modal fade" id="confirmationModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="confirmationForm"	action="#" method="post">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body">
					<p id="confirmationMessage"></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.close" />
					</button>
					<button id="confirmationButton" class="btn btn-danger" type="submit">
						
					</button>
				</div>
			</form>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<div class="modal fade" id="lastChangeRequestDetailsDialog">
	<div class="modal-dialog">
		<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.CompetenceCourseMarkSheet.lastChangeRequest" />
					</h4>
				</div>
				<div class="modal-body">
				
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.state" />
						</div>
						
						<div class="col-sm-5">
							<c:out value="${competenceCourseMarkSheet.lastChangeRequest.state.descriptionI18N.content}" />
						</div>
					</div>
					
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.requestDate" />
						</div>
						
						<div class="col-sm-5">
							<joda:format value="${competenceCourseMarkSheet.lastChangeRequest.requestDate}" pattern="yyyy-MM-dd HH:mm:ss" />
						</div>
					</div>
					
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.requester" />
						</div>
						
						<div class="col-sm-5">
                            <c:if test="${not empty competenceCourseMarkSheet.lastChangeRequest}">
                                <c:out value="<%=CompetenceCourseMarkSheetBean.getPersonDescription(((CompetenceCourseMarkSheet)request.getAttribute("competenceCourseMarkSheet")).getLastChangeRequest().getRequester())%>"></c:out>
                            </c:if>
						</div>
					</div>
					
					
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.reason" />
						</div>
						
						<div class="col-sm-5">
							<pre style="border:none;background-color: inherit;white-space: pre-wrap;"><c:out value="${competenceCourseMarkSheet.lastChangeRequest.reason}"/></pre>
						</div>
					</div>
					
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.responseDate" />
						</div>
						
						<div class="col-sm-5">
							<joda:format value="${competenceCourseMarkSheet.lastChangeRequest.responseDate}" pattern="yyyy-MM-dd HH:mm:ss" />
						</div>
					</div>
					
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.responder" />
						</div>
						
						<div class="col-sm-5">
                            <c:if test="${not empty competenceCourseMarkSheet.lastChangeRequest}">
                                <c:out value="<%=CompetenceCourseMarkSheetBean.getPersonDescription(((CompetenceCourseMarkSheet)request.getAttribute("competenceCourseMarkSheet")).getLastChangeRequest().getResponder())%>"></c:out>
                            </c:if>
						</div>
					</div>
					
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.comments" />
						</div>
						
						<div class="col-sm-5">
							<pre style="border:none;background-color: inherit;white-space: pre-wrap;"><c:out value="${competenceCourseMarkSheet.lastChangeRequest.comments}"/></pre>
						</div>
					</div>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.close" />
					</button>
				</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<div class="modal fade" id="createChangeRequestDialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="createChangeRequestForm" action="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.CREATE_CHANGE_REQUEST_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}" method="post">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.CompetenceCourseMarkSheetChangeRequest" />
					</h4>
				</div>
				<div class="modal-body">
					<div class="form-group row">
						<div class="col-sm-4 control-label">
							<spring:message code="label.CompetenceCourseMarkSheetChangeRequest.reason" />
						</div>
						
						<div class="col-sm-5">
							<textarea rows="10" cols="30" name="reason" ng-model="reason"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.cancel" />
					</button>
					<button id="confirmationButton" class="btn btn-danger" type="button" ng-click="createChangeRequest()">
						<spring:message code="label.submit" />
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
		href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.SEARCH_URL%>${executionCourse.externalId}"><spring:message
			code="label.event.back" /></a> 
			
	<c:if test="${competenceCourseMarkSheet.edition}">		
		&nbsp;|&nbsp; <span
			class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.UPDATE_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}"><spring:message
				code="label.event.update" /></a>
	
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.READ_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}/updateevaluations"><spring:message
				code="label.event.evaluation.manageMarkSheet.updateEvaluations" /></a>
				
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-lock" aria-hidden="true"></span>&nbsp;<a class="" ng-click="submitMarkSheet()"
			href="#"><spring:message code="label.event.evaluation.manageMarkSheet.submit" /></a>
			
		<c:if test="${empty competenceCourseMarkSheet.snapshots}">
			&nbsp;|&nbsp; <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;<a class="" href="#" 
				ng-click="deleteMarkSheet()"><spring:message code="label.event.delete" /></a>
		</c:if>
	</c:if>
	
	<c:if test="${!competenceCourseMarkSheet.edition}">
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-print" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.PRINT_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}"><spring:message
				code="label.event.evaluation.manageMarkSheet.print" /></a>
		
		<c:if test="${competenceCourseMarkSheet.lastPendingChangeRequest == null}">		
			&nbsp;|&nbsp; <span class="glyphicon glyphicon-retweet" aria-hidden="true"></span>&nbsp;<a class=""
				href="#" ng-click="showCreateChangeRequest()"><spring:message code="label.CompetenceCourseMarkSheetChangeRequest" /></a>
		</c:if>
	</c:if>
    
    <c:if test="${competenceCourseMarkSheet.submitted && competenceCourseMarkSheet.supportsTeacherConfirmation}">
        &nbsp;|&nbsp; <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>&nbsp;<a class="" ng-click="confirmMarkSheet()"
            href="#"><spring:message code="label.event.evaluation.manageMarkSheet.confirm" /></a>
    </c:if>
			
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
	
	<spring:message code="label.yes" var="yesLabel" />	
	<spring:message code="label.no" var="noLabel" />

	<div class="panel-body">
		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.creationDate" /></th>
						<td><joda:format value="${competenceCourseMarkSheet.creationDate}" pattern="yyyy-MM-dd HH:mm" /></td>
					</tr>
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
						<td><c:out value="${competenceCourseMarkSheet.evaluationDatePresentation}"/></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.state" /></th>
						<td><span class="label label-primary"><c:out value="${competenceCourseMarkSheet.state}"/></span></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.certifier" /></th>
						<td>
                            <c:out value="<%=CompetenceCourseMarkSheetBean.getPersonDescription(((CompetenceCourseMarkSheet)request.getAttribute("competenceCourseMarkSheet")).getCertifier())%>"></c:out>
                        </td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.printed" /></th>
						<td><c:out value="${competenceCourseMarkSheet.printed ? yesLabel : noLabel}"></c:out></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.checkSum" /></th>
						<td>
							<c:choose>
								<c:when test="${empty competenceCourseMarkSheet.formattedCheckSum}">
									-
								</c:when>
								<c:otherwise>
									<c:out value='${competenceCourseMarkSheet.formattedCheckSum}' />
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.shifts" /></th>
						<td><c:out value='${competenceCourseMarkSheet.shiftsDescription}' /></td>
					</tr>
					<c:if test="${not empty competenceCourseMarkSheet.expireDate}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.expireDate" /></th>
						<td><joda:format value="${competenceCourseMarkSheet.expireDate}" pattern="yyyy-MM-dd"/></td>
					</tr>
					</c:if>
					<c:if test="${competenceCourseMarkSheet.lastChangeRequest != null}">
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.lastChangeRequest" /></th>
						<td>
							<span class="label label-primary"><c:out value='${competenceCourseMarkSheet.lastChangeRequest.state.descriptionI18N.content}' /></span> 
							(<a href="#" ng-click="showLastChangeRequestDetails()"><span class="glyphicon glyphicon-search" aria-hidden="true"></span>&nbsp;<spring:message code="label.details" /></a>)
						</td>
					</tr>
					</c:if>
				</tbody>
			</table>
		</form>
	</div>
</div>

<%-- State Changes --%>
<h2><spring:message code="label.CompetenceCourseMarkSheet.stateChanges"></spring:message></h2>
<table id="stateChangesTable" class="table responsive table-bordered table-hover" width="100%">
	<thead>
		<tr>
			<th><spring:message code="label.CompetenceCourseMarkSheetStateChange.date" /></th>
			<th><spring:message code="label.CompetenceCourseMarkSheetStateChange.state" /></th>
			<th><spring:message code="label.CompetenceCourseMarkSheetStateChange.responsible" /></th>
			<th><spring:message code="label.CompetenceCourseMarkSheetStateChange.byTeacher" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="each" items="${competenceCourseMarkSheet.stateChangeSet}">
		<tr>
			<td><joda:format value="${each.date}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			<td><c:out value="${each.state.descriptionI18N.content}"></c:out></td>
			<td><c:out value="<%=CompetenceCourseMarkSheetBean.getPersonDescription(((CompetenceCourseMarkSheetStateChange)pageContext.getAttribute("each")).getResponsible())%>"></c:out></td>
			<td><c:out value="${each.byTeacher ? yesLabel : noLabel}"></c:out></td>
		</tr>
		</c:forEach>
	</tbody>
</table>
<script type="text/javascript">
	createDataTables('stateChangesTable',false /*filterable*/, false /*show tools*/, false /*paging*/, "${pageContext.request.contextPath}","${datatablesI18NUrl}");
</script>

</div>
