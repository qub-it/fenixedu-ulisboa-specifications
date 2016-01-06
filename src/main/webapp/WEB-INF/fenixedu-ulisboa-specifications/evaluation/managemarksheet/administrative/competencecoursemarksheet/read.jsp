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
						$scope.showConfirmation('${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.SUBMIT_URL%>${competenceCourseMarkSheet.externalId}', 
								'<spring:message code="label.evaluation.manageMarkSheet.administrative.readCompetenceCourseMarkSheet.confirmSubmit" />', 
								'<spring:message code="label.event.evaluation.manageMarkSheet.submit" />');
					}
					
					$scope.confirmMarkSheet = function() {						
						$scope.showConfirmation('${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.CONFIRM_URL%>${competenceCourseMarkSheet.externalId}', 
								'<spring:message code="label.evaluation.manageMarkSheet.administrative.readCompetenceCourseMarkSheet.confirmConfirm" />', 
								'<spring:message code="label.event.evaluation.manageMarkSheet.confirm" />');
					}
					
					$scope.revertMarkSheetToEdition = function() {						
						$scope.showConfirmation('${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.REVERT_TO_EDITION_URL%>${competenceCourseMarkSheet.externalId}', 
								'<spring:message code="label.evaluation.manageMarkSheet.administrative.readCompetenceCourseMarkSheet.confirmRevertToEdition" />', 
								'<spring:message code="label.event.evaluation.manageMarkSheet.revertToEdition" />');
					}
					
					$scope.deleteMarkSheet = function() {	
						$scope.showConfirmation('${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.DELETE_URL%>${competenceCourseMarkSheet.externalId}', 
								'<spring:message code="label.evaluation.manageMarkSheet.administrative.readCompetenceCourseMarkSheet.confirmDelete" />', 
								'<spring:message code="label.delete" />');
					}
					
					$scope.showConfirmation = function(url,message,actionText) {
						$('#confirmationForm').attr('action', url);
						$('#confirmationMessage').html(message);
						$('#confirmationButton').html(actionText);
						$('#confirmationModal').modal('toggle')
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
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.CONTROLLER_URL%>"><spring:message
			code="label.event.back" /></a> 
			
	<c:if test="${competenceCourseMarkSheet.edition}">		
		&nbsp;|&nbsp; <span
			class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.UPDATE_URL%>${competenceCourseMarkSheet.externalId}"><spring:message
				code="label.event.update" /></a>
	
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.READ_URL%>${competenceCourseMarkSheet.externalId}/updateevaluations"><spring:message
				code="label.event.evaluation.manageMarkSheet.updateEvaluations" /></a>
				
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-lock" aria-hidden="true"></span>&nbsp;<a class="" ng-click="submitMarkSheet()"
			href="#"><spring:message code="label.event.evaluation.manageMarkSheet.submit" /></a>
			
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;<a class="" href="#" 
			ng-click="deleteMarkSheet()"><spring:message code="label.event.delete" /></a>
	</c:if>
	
	<c:if test="${!competenceCourseMarkSheet.edition}">
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-print" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.PRINT_URL%>${competenceCourseMarkSheet.externalId}"><spring:message
				code="label.event.evaluation.manageMarkSheet.print" /></a>
	</c:if>
			
	<c:if test="${competenceCourseMarkSheet.submitted}">
		&nbsp;|&nbsp; <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>&nbsp;<a class="" ng-click="confirmMarkSheet()"
			href="#"><spring:message code="label.event.evaluation.manageMarkSheet.confirm" /></a>
	</c:if>
	
	<c:if test="${!competenceCourseMarkSheet.edition}">
		&nbsp;|&nbsp; <span
			class="glyphicon glyphicon-retweet" aria-hidden="true"></span>&nbsp; <a class="" href="#" ng-click="revertMarkSheetToEdition()"><spring:message
				code="label.event.evaluation.manageMarkSheet.revertToEdition" /></a>
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
						<td><joda:format value="${competenceCourseMarkSheet.creationDate}" style="SM" /></td>
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
						<td><joda:format value="${competenceCourseMarkSheet.evaluationDate}" style="S-"/></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.state" /></th>
						<td><span class="label label-primary"><c:out value="${competenceCourseMarkSheet.state}"/></span></td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.certifier" /></th>
						<td><c:out value='${competenceCourseMarkSheet.certifier.name}' /></td>
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
			<td><joda:format value="${each.date}" style="SM" /></td>
			<td><c:out value="${each.state.descriptionI18N.content}"></c:out></td>
			<td><c:out value="${each.responsible.name}"></c:out></td>
			<td><c:out value="${each.byTeacher ? yesLabel : noLabel}"></c:out></td>
		</tr>
		</c:forEach>
	</tbody>
</table>
<script type="text/javascript">
	createDataTables('stateChangesTable',false /*filterable*/, false /*show tools*/, false /*paging*/, "${pageContext.request.contextPath}","${datatablesI18NUrl}");
</script>

<%-- Previous Versions --%>
<h2><spring:message code="label.CompetenceCourseMarkSheet.previousSnapshots"></spring:message></h2>
<table id="previousSnapshotsTable" class="table responsive table-bordered table-hover" width="100%">
	<thead>
		<tr>
			<th><spring:message code="label.CompetenceCourseMarkSheetStateChange.date" /></th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="each" items="${competenceCourseMarkSheet.previousSnapshots}">
		<tr>
			<td><joda:format value="${each.stateChange.date}" style="SM" /></td>
			<td>
				<a  class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.PRINT_SNAPSHOT_URL%>${each.externalId}">
					<spring:message code='label.event.evaluation.manageMarkSheet.print'/>
				</a>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>
<script type="text/javascript">
	createDataTables('previousSnapshotsTable',false /*filterable*/, false /*show tools*/, false /*paging*/, "${pageContext.request.contextPath}","${datatablesI18NUrl}");
</script>

</div>
