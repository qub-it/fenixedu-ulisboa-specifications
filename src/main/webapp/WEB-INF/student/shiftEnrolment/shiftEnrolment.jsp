<%--
   This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
   copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
   software development project between Quorum Born IT and Serviços Partilhados da
   Universidade de Lisboa:
    - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
    - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
  
   Contributors: shezad.anavarali@qub-it.com
   				 diogo.simoes@qub-it.com
  
   
   This file is part of FenixEdu fenixedu-ulisboa-specifications.
  
   FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
  
   FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.
  
   You should have received a copy of the GNU Lesser General Public License
   along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/datetime-1.0" prefix="dt" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html:xhtml/>
${portal.angularToolkit()}

<script	src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>

<script type="text/javascript">
(function() {
	var app = angular.module("shiftEnrolmentApp", [ 'ngRoute', 'bennuToolkit' ]);

	app.controller("ShiftEnrolmentCtrl", [
			'$scope',
			'$http',
			function($scope, $http) {

				$scope.fetchPossibleShiftsToEnrol = function(registrationID, executionCourseID, shiftType) {
					$scope.shiftsToEnrol = null;
					$http.get(window.contextPath + '/student/shiftEnrolment/possibleShiftsToEnrol.json/' + registrationID + '/' + executionCourseID + '/' + shiftType).success(
							function(result) {
								$scope.shiftsToEnrol = result;
							});
				};
				
				$scope.prepareRemoveShift = function(shiftID) {
					$scope.shiftID = shiftID;
				}

			} ]);
})();

</script>

<spring:url var="staticUrl" value="/themes/fenixedu-learning-theme/static"/>

<link href='${staticUrl}/css/fullcalendar.css' rel='stylesheet' />
<link href='${staticUrl}/css/fullcalendar.print.css' rel='stylesheet' media='print' />
<link href='${staticUrl}/css/schedule.css' rel='stylesheet' rel='stylesheet' />

<script src='${staticUrl}/js/moment.min.js'></script>
<script src='${staticUrl}/js/jquery-ui.fullCalendar.custom.min.js'></script>
<script src='${staticUrl}/js/fullcalendar.js'></script>


<style>
	.nav-tabs > li > a:hover, .nav-tabs > li > a:focus {
	    text-decoration: none;
	    background-color: #eee;
	}
	.fc-today {
	   background: transparent !important;
	}
	.btn-finish {
		margin: 12px 0 0 48px;
	}
	
	.glyphicon.spinning {
	    animation: spin 1s infinite linear;
	    -webkit-animation: spin2 1s infinite linear;
	}
	
	@keyframes spin {
	    from { transform: scale(1) rotate(0deg); }
	    to { transform: scale(1) rotate(360deg); }
	}
	
	@-webkit-keyframes spin2 {
	    from { -webkit-transform: rotate(0deg); }
	    to { -webkit-transform: rotate(360deg); }
	}	
</style>

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.title.shiftEnrolment"/>
		<small></small>
	</h1>
</div>

<c:set var="canContinueProcess" value="${mandatoryShiftsEnrolled}" />

<%-- NAVIGATION --%>	
<c:if test="${not empty enrolmentProcess}">
	<div class="well well-sm" style="display: inline-block">
	    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	    &nbsp;
	    <%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="${enrolmentProcess.getReturnURL(pageContext.request))}">
			<spring:message code="label.event.back" />
		</a>
	    &nbsp;|&nbsp;
			<c:if test="${canContinueProcess}">
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="${enrolmentProcess.getContinueURL(pageContext.request)}">
					<spring:message code="label.continue" />
				</a>
			</c:if>
			<c:if test="${not canContinueProcess}">
				<span class="text-muted"><spring:message code="label.continue" /></span>
			</c:if>
		&nbsp;
		<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
	</div>
</c:if>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">
        <c:forEach items="${infoMessages}" var="message">
            <p><span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span> ${message}</p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">
        <c:forEach items="${errorMessages}" var="message">
            <p><span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}</p>
        </c:forEach>
    </div>
</c:if>

<div ng-app="shiftEnrolmentApp">
	<div ng-controller="ShiftEnrolmentCtrl">

	<c:if test="${empty enrolmentBeans}">
		<div class="alert alert-warning" role="alert"><spring:message code="message.shiftEnrolment.noOpenPeriods"/></div>
	</c:if>
	<c:if test="${not empty enrolmentBeans}">
	
		<ul class="nav nav-tabs">
			<c:forEach items="${enrolmentBeans}" var="enrolmentBean">
				<li role="presentation" class="<c:out value="${enrolmentBean.selected ? 'active' : ' '}" />">
					<a href="${pageContext.request.contextPath}/student/shiftEnrolment/switchEnrolmentPeriod/${enrolmentBean.registration.externalId}/${enrolmentBean.enrolmentPeriod.externalId}">
						<c:out value="${enrolmentBean.enrolmentPeriod.executionSemester.qualifiedName}" /> 
						<br/><span class="small text-muted"><c:out value="${enrolmentBean.registration.degree.presentationName}" /></span>
					</a>
				</li>
				<c:if test="${enrolmentBean.selected}"><c:set var="selectedEnrolmentBean" value="${enrolmentBean}" /></c:if>
			</c:forEach>
		</ul>	
	
		<c:if test="${empty shiftsToEnrol}">
			<div class="alert alert-warning" role="alert"><spring:message code="message.shiftEnrolment.noShiftsToEnrol"/></div>
		</c:if>
		<c:if test="${not empty shiftsToEnrol}">
		
<!-- 			<c:if test="${numberOfExecutionCoursesHavingNotEnroledShifts > 0}"> -->
<!-- 				<div class="alert alert-warning" role="alert"><span class="glyphicon glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> <c:out value="${numberOfExecutionCoursesHavingNotEnroledShifts}" /> _Atenção: Ainda falta inscrever-se em Turnos referentes a x disciplina(s)._</div> -->
<!-- 			</c:if> -->
<!-- 			<c:if test="${numberOfExecutionCoursesHavingNotEnroledShifts == 0}"> -->
<!-- 				<div class="alert alert-success" role="alert"><span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>_A Reserva de Turmas foi efectuada com sucesso em todas as disciplinas que frequenta._</div> -->
<!-- 			</c:if>		 -->
			
			<table class="table table-bordered">
				<tr>
					<th><spring:message code="label.shiftEnrolment.enroledCourses"/></th>
<!-- 					<th class="text-center">T <span class="glyphicon glyphicon-question-sign text-muted" data-toggle="tooltip" data-placement="top" title="_práticas_"></span></th> -->
					<th class="text-center"><spring:message code="label.shiftType.description.theoric"/></th>
					<%-- <th class="text-center"><spring:message code="label.shiftType.description.pratic"/></th> --%>
					<th class="text-center"><spring:message code="label.shiftType.description.laboratory"/></th>
					<%-- <th class="text-center"><spring:message code="label.shiftType.description.theoricoPratic"/></th> --%>
					<th class="text-center"><spring:message code="label.shiftType.description.problems"/> / <spring:message code="label.shiftType.description.theoricoPratic"/></th>
					<th class="text-center"><spring:message code="label.shiftType.description.fieldWork"/></th>
					<th class="text-center"><spring:message code="label.shiftType.description.seminary"/></th>
					<th class="text-center"><spring:message code="label.shiftType.description.trainingPeriod"/></th>
					<th class="text-center"><spring:message code="label.shiftType.description.tutorialOrientation"/></th>
				</tr>
				<c:forEach items="${shiftsToEnrol}" var="shiftToEnrol">
					<tr>
						<td class="col-md-3"><c:out value="${shiftToEnrol.executionCourse.name}" /></td>
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.theoricType}">
								<c:if test="${not empty shiftToEnrol.theoricShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.theoricShift.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.theoricShift.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.theoricShift}">
									<button class="btn btn-danger btn-sm" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.theoricType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						<%-- 
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.praticType}">
								<c:if test="${not empty shiftToEnrol.praticShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.praticShift.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.praticShift.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.praticShift}">
									<button class="btn btn-danger" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.praticType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						--%>
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.laboratoryType}">
								<c:if test="${not empty shiftToEnrol.laboratoryShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.laboratoryShift.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.laboratoryShift.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.laboratoryShift}">
									<button class="btn btn-danger" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.laboratoryType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						<%-- 
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.theoricoPraticType}">
								<c:if test="${not empty shiftToEnrol.theoricoPraticShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.theoricoPraticType.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.theoricoPraticType.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.theoricoPraticShift}">
									<button class="btn btn-danger" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.theoricoPraticType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						--%>
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.problemsType}">
								<c:if test="${not empty shiftToEnrol.problemsShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.problemsShift.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.problemsShift.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.problemsShift}">
									<button class="btn btn-danger" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.problemsType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.fieldWorkType}">
								<c:if test="${not empty shiftToEnrol.fieldWorkShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.fieldWorkShift.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.fieldWorkShift.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.fieldWorkShift}">
									<button class="btn btn-danger" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.fieldWorkType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.seminaryType}">
								<c:if test="${not empty shiftToEnrol.seminaryShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.seminaryShift.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.seminaryShift.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.seminaryShift}">
									<button class="btn btn-danger" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.seminaryType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.trainingType}">
								<c:if test="${not empty shiftToEnrol.trainingShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.trainingShift.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.trainingShift.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.trainingShift}">
									<button class="btn btn-danger" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.trainingType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						<td class="col-md-1 text-center">
							<c:if test="${not empty shiftToEnrol.tutorialOrientationType}">
								<c:if test="${not empty shiftToEnrol.tutorialOrientationShift}">
									<button class="btn btn-success btn-sm" data-toggle="modal" data-target=".bs-remove-shift-modal-sm" ng-click="prepareRemoveShift(${shiftToEnrol.tutorialOrientationShift.externalId})"
										rel="tooltip" data-placement="bottom" title="${shiftToEnrol.tutorialOrientationShift.lessonPresentationString}">
										<spring:message code="label.shiftEnrolment.enroled"/>&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
									</button>
								</c:if>
								<c:if test="${empty shiftToEnrol.tutorialOrientationShift}">
									<button class="btn btn-danger" data-toggle="modal" data-target=".bs-add-shift-modal-sm" ng-click="fetchPossibleShiftsToEnrol(${selectedEnrolmentBean.registration.externalId}, ${shiftToEnrol.executionCourse.externalId}, '${shiftToEnrol.tutorialOrientationType.name}')">
										<spring:message code="label.shiftEnrolment.enrol"/>
									</button>    
								</c:if>
							</c:if>
						</td>
						
					</tr>
				</c:forEach>
			</table>		
			
			    
			<div id="addShiftModal" class="modal fade bs-add-shift-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
				<div class="modal-dialog modal-sm" >
					<div class="modal-content">
						<div class="modal-header">
						  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						  <h4 class="modal-title"><spring:message code="label.shiftEnrolment.addShift"/></h4>
						</div>
						<div class="modal-body">
							<table class="table table-bordered" ng-show="shiftsToEnrol.length > 0">
								<tbody>
									<tr ng-repeat="shift in shiftsToEnrol track by $index">
										<td>{{shift.lessons}}</td>
										<td>
											<a href="${pageContext.request.contextPath}/student/shiftEnrolment/addShift/${selectedEnrolmentBean.registration.externalId}/${selectedEnrolmentBean.enrolmentPeriod.externalId}/{{shift.externalId}}">
												<span class="glyphicon glyphicon-plus-sign fa-2x" data-toggle="tooltip" data-placement="right" title='<spring:message code="label.shiftEnrolment.enrol"/>'></span>
											</a>
										</td>
									</tr>
								</tbody>
							</table>					
							<span ng-show="shiftsToEnrol.length == 0" class="text-danger">
								<spring:message code="message.shiftEnrolment.noShiftsAvailableToEnrol"/>
							</span>		
							<span ng-show="shiftsToEnrol == null">
								<span class="glyphicon glyphicon-refresh spinning"></span>
							</span>	
						</div>					
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>						
						</div>
					</div>
				</div>
			</div>
			
			<div id="removeShiftModal" class="modal fade bs-remove-shift-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
				<div class="modal-dialog modal-sm" >
					<div class="modal-content">
						<div class="modal-header">
						  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						  <h4 class="modal-title"><spring:message code="label.shiftEnrolment.removeShift"/></h4>
						</div>
						<div class="modal-body">
							<spring:message code="label.shiftEnrolment.removeShift.confirmation"/>					
						</div>					
						<div class="modal-footer">
							<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="button.cancel"/></button>	
							<a class="btn btn-primary" href="${pageContext.request.contextPath}/student/shiftEnrolment/removeShift/${selectedEnrolmentBean.registration.externalId}/${selectedEnrolmentBean.enrolmentPeriod.externalId}/{{shiftID}}" role="button"><spring:message code="button.confirm"/></a>
						</div>
					</div>
				</div>
			</div>			
			
			<p>&nbsp;</p>
			<h3><spring:message code="label.shiftEnrolment.registrationSchedule"/>:</h3>
			
			<c:if test="${not empty selectedEnrolmentBean.lessonsOverlaps}">
				<div class="alert alert-warning" role="alert">
					<p><span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> 
					<strong><spring:message code="message.shiftEnrolment.overlappingLessons"/>:</strong></p>
					<c:forEach items="${selectedEnrolmentBean.lessonsOverlaps}" var="lessonOverlap">
							<c:out value="${lessonOverlap.key.diaSemana}" />
							<dt:format pattern="HH:mm"><c:out value="${lessonOverlap.key.inicio.timeInMillis}" /></dt:format> - 
							<dt:format pattern="HH:mm"><c:out value="${lessonOverlap.key.fim.timeInMillis}" /></dt:format>
							&nbsp;(<c:out value="${lessonOverlap.key.shift.executionCourse.name} - ${lessonOverlap.key.shift.shiftTypesCodePrettyPrint}" />) 
							<span class="glyphicon glyphicon glyphicon-flash" aria-hidden="true"></span> 
							<c:forEach items="${lessonOverlap.value}" var="otherLesson">
								<c:out value="${otherLesson.diaSemana}" />
								<dt:format pattern="HH:mm"><c:out value="${otherLesson.inicio.timeInMillis}" /></dt:format> - 
								<dt:format pattern="HH:mm"><c:out value="${otherLesson.fim.timeInMillis}" /></dt:format>
								&nbsp;(<c:out value="${otherLesson.shift.executionCourse.name} - ${otherLesson.shift.shiftTypesCodePrettyPrint}" />) 
							</c:forEach>
							<br/>
					</c:forEach>				
				</div>
			</c:if>
			
			<div id="calendar"></div>
			
			<spring:url var="eventsUrl" value="/student/shiftEnrolment/currentSchedule.json/${selectedEnrolmentBean.registration.externalId}/${selectedEnrolmentBean.enrolmentPeriod.executionSemester.externalId}"/>
			<script>
			$(document).ready(function() {
				$(function () {
				  $('[data-toggle="tooltip"]').tooltip();
				  $('[rel="tooltip"]').tooltip();
				})
				
				var i18nDayNames = [
					"<spring:message code="label.weekday.short.sunday"/>",
					"<spring:message code="label.weekday.short.monday"/>",
					"<spring:message code="label.weekday.short.tuesday"/>",
					"<spring:message code="label.weekday.short.wednesday"/>",
					"<spring:message code="label.weekday.short.thursday"/>",
					"<spring:message code="label.weekday.short.friday"/>",
					"<spring:message code="label.weekday.short.saturday"/>"
				];
				
				$('#calendar').fullCalendar({
						header: { left: '', center: '', right: '' },
						defaultView: 'agendaWeek',
						columnFormat: { week: 'ddd' },			
						minTime: '08:00',
						maxTime: '24:00',
						timeFormat: 'HH:mm',
						axisFormat: 'HH:mm',
						allDaySlot : false,
						dayNamesShort: i18nDayNames,
						firstDay: 1,	
						hiddenDays: [0],						
						editable: false,
						eventLimit: true, // allow "more" link when too many events
						events : "${eventsUrl}",
						height: "auto"
					});
				
			});
			</script>
		
		</c:if>		
		
	</c:if>    
    </div>
</div>

<%-- NAVIGATION --%>	
<c:if test="${not empty shiftsToEnrol}">
	<c:if test="${not empty enrolmentProcess}">
		<div class="well well-sm mtop15" style="display: inline-block">
		    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
		    &nbsp;
		    <%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="${enrolmentProcess.getReturnURL(pageContext.request))}">
				<spring:message code="label.event.back" />
			</a>
		    &nbsp;|&nbsp;
				<c:if test="${canContinueProcess}">
					<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="${enrolmentProcess.getContinueURL(pageContext.request))}">
						<spring:message code="label.continue" />
					</a>
				</c:if>
				<c:if test="${not canContinueProcess}">
					<span class="text-muted"><spring:message code="label.continue" /></span>
				</c:if>
			&nbsp;		
			<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>	
		</div>
	</c:if>
</c:if>
