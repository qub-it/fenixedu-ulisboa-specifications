<%@page import="org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page import="org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheet"%>
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
				
				$scope.fillEmptyGradesWithValue = function() {
					$.each( $scope.object.updateGradeBeans, function( index, evaluation ){
						if (evaluation.gradeValue == undefined || evaluation.gradeValue == '') {
							$scope.object.updateGradeBeans[index].gradeValue = $scope.defaultGrade;
						}
					});
					
				}
				
				$scope.hasAnyEmptyGrade = function() {
					var foundEmptyGrades = false;
					$.each( $scope.object.updateGradeBeans, function( index, evaluation ){
						if (evaluation.gradeValue == undefined || evaluation.gradeValue == '') {
							foundEmptyGrades = true;
						}
					});
					
					
					return foundEmptyGrades;
					
				}
				
				$scope.clearGrades = function() {
					$.each( $scope.object.updateGradeBeans, function( index, evaluation ){
						$scope.object.updateGradeBeans[index].gradeValue = undefined;
					});
					
				}
				
                $scope.submitGradesWithEmptyCheck = function() {
                    if ($scope.hasAnyEmptyGrade() && <c:out value="${competenceCourseMarkSheetBean.supportsEmptyGrades}" />) {                      
                        $('#emptyGradesWarningModal').modal('toggle')
                    } else {
                        $scope.submitGrades();
                    }
                }
				
				$scope.submitGrades = function() {
					$('#updateEvaluationsForm').submit();
				}
				
				$scope.importExcel = function() {
					$('#importExcelModal').modal('toggle')
				}


			    } ]);
</script>

<div ng-app="angularAppCompetenceCourseMarkSheet" ng-controller="CompetenceCourseMarkSheetController">

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.evaluation.manageMarkSheet.updateEvaluations" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.READ_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}"><spring:message
			code="label.event.back" /></a>
			
	&nbsp;|&nbsp; <span class="glyphicon glyphicon-export" aria-hidden="true"></span>&nbsp;<a class=""
			href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.EXPORT_EXCEL_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}"><spring:message
				code="label.event.evaluation.manageMarkSheet.exportExcel" /></a>
	
	&nbsp;|&nbsp; <span class="glyphicon glyphicon-import" aria-hidden="true"></span>&nbsp;<a class=""
			href="#" ng-click="importExcel()"><spring:message
				code="label.event.evaluation.manageMarkSheet.importExcel" /></a>
	
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

<div class="modal fade" id="importExcelModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form method="POST" action="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.IMPORT_EXCEL_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}" enctype="multipart/form-data">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.event.evaluation.manageMarkSheet.importExcel" />
					</h4>
				</div>
				<div class="modal-body">
					<input type="file" name="file" required />
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.cancel" />
					</button>
					<button class="btn btn-danger" type="submit">
						<spring:message code="label.upload" />
					</button>
				</div>
			</form>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<div class="modal fade" id="emptyGradesWarningModal">
	<div class="modal-dialog">
		<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirm" />
					</h4>
				</div>
				<div class="modal-body">
					<p><spring:message code="label.evaluation.manageMarkSheet.updateEvaluations.emptyGradesWarning" /></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.cancel" />
					</button>
					<button class="btn btn-danger" type="button" ng-click="submitGrades()">
						<spring:message code="label.continue" />
					</button>
				</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<form id="updateEvaluationsForm" name='form' method="post" class="form-horizontal"
	action='${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.UPDATEEVALUATIONS_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}'>

	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.UPDATEEVALUATIONSPOSTBACK_URL%>${executionCourse.externalId}/${competenceCourseMarkSheet.externalId}' />

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
						<td><c:out value="${competenceCourseMarkSheet.evaluationDatePresentation}"/></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
	

	<div class="panel panel-default">
		<div class="panel-body">
		
			<div class="alert alert-info" role="alert">
				<spring:message code="label.evaluation.manageMarkSheet.updateEvaluations.instructions" arguments="${competenceCourseMarkSheet.gradeScaleDescription}" />
			</div>
		
    		<div class="form-group row">
	    		<div class="col-sm-9">
                    <button type="button" class="btn btn-primary" role="button" ng-click="submitGradesWithEmptyCheck()"><spring:message code="label.save" /></button>
                </div>
                <div class="col-sm-3">
    				<button class="btn glyphicon glyphicon-cog" type="button" ng-click="fillEmptyGradesWithValue()"> <spring:message code="label.fill" /></button>
    				<input type="text" name="defaultGrade" ng-model="defaultGrade" size="6" maxlength="6"/>
                </div>
    		</div>
        
			<table id="evaluationsTable" class="table responsive table-bordered table-hover" width="100%">
				<thead>
					<tr>
						<th><spring:message code="label.MarkBean.studentNumber" /></th>
						<th><spring:message code="label.MarkBean.studentName" /></th>
						<th><spring:message code="label.MarkBean.degreeCode" /></th>
						<th><spring:message code="label.MarkBean.statutes" /></th>
						<th><spring:message code="label.MarkBean.shifts" /></th>
						<th><spring:message code="label.MarkBean.gradeValue" /></th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="evaluation in object.updateGradeBeans">
						<td>{{evaluation.studentNumber}}</td>
						<td>{{evaluation.studentName}}</td>
						<td>{{evaluation.degreeCode}}</td>
						<td>{{evaluation.statutes}}</td>
						<td>{{evaluation.shifts}}</td>
						<td><input type="text" name="grade" ng-model="evaluation.gradeValue" size="6" maxlength="6"/>
							<span class="alert alert-danger btn-xs" ng-show="evaluation.errorMessage != null">{{evaluation.errorMessage}}</span>
                            <span class="alert alert-info btn-xs" ng-show="evaluation.infoMessage != null">{{evaluation.infoMessage}}</span>
						 </td>
					</tr>
				</tbody>
			</table>
			
		</div>
		<div class="panel-footer">
			<button type="button" class="btn btn-primary" role="button" ng-click="submitGradesWithEmptyCheck()"><spring:message code="label.save" /></button>
		</div>
	</div>
</form>

</div>
