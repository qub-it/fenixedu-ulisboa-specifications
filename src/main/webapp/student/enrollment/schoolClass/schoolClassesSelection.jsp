<%--
   This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
   copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
   software development project between Quorum Born IT and Serviços Partilhados da
   Universidade de Lisboa:
    - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
    - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
  
   Contributors: shezad.anavarali@qub-it.com
  
   
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
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link href='${pageContext.request.contextPath}/themes/fenixedu-learning-theme/static/css/fullcalendar.css' rel='stylesheet' />
<link href='${pageContext.request.contextPath}/themes/fenixedu-learning-theme/static/css/fullcalendar.print.css' rel='stylesheet' media='print' />
<link href='${pageContext.request.contextPath}/themes/fenixedu-learning-theme/static/css/schedule.css' rel='stylesheet' rel='stylesheet' />

<script src='${pageContext.request.contextPath}/themes/fenixedu-learning-theme/static/js/moment.min.js'></script>
<script src='${pageContext.request.contextPath}/themes/fenixedu-learning-theme/static/js/jquery-ui.fullCalendar.custom.min.js'></script>
<script src='${pageContext.request.contextPath}/themes/fenixedu-learning-theme/static/js/fullcalendar.js'></script>

<style>
.nav-tabs > li > a:hover, .nav-tabs > li > a:focus {
    text-decoration: none;
    background-color: #eee;
}
.fc-today {
   background: transparent !important;
} 
.calendarHoverIn {
	background: #F00;
	cursor:pointer;
}
.calendarHoverIn:after { 
    content: "[X] <bean:message bundle="APPLICATION_RESOURCES" key="label.remove" />";
    font-weight: bold;
}
</style>

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<bean:message bundle="STUDENT_RESOURCES" key="title.student.shift.enrollment" />
		<small></small>
	</h1>
</div>

<c:set var="canContinueProcess" value="${not schoolClassEmptyButSelectionMandatory}" />

<%-- NAVIGATION --%>
<logic:present name="enrolmentProcess">
	<bean:define id="enrolmentProcess" name="enrolmentProcess" type="org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess"/>
	
	<div class="well well-sm" style="display: inline-block">
	    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	    &nbsp;
	    <%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="<%= enrolmentProcess.getReturnURL(request) %>">
			<bean:message bundle="APPLICATION_RESOURCES" key="label.back" />
		</a>
	    &nbsp;|&nbsp;
	    <c:if test="${canContinueProcess}">
			<bean:define id="continueHACKLink"><%= request.getContextPath() + org.fenixedu.ulisboa.specifications.ui.student.enrolment.EnrolmentManagementDA.createEnrolmentStepEndProcess().getEntryPointURL()  %>&studentCurricularPlanOID=<c:out value="${enrolmentProcess.studentCurricularPlan.externalId}" />&executionSemesterOID=<c:out value="${enrolmentProcess.executionSemester.externalId}" /></bean:define>
			<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="<%= enrolmentProcess.getContinueURL(request) %>">
				<bean:message bundle="APPLICATION_RESOURCES" key="button.continue" />
			</a>
		</c:if>
		<c:if test="${not canContinueProcess}">
			<span class="text-muted"><bean:message bundle="APPLICATION_RESOURCES" key="button.continue" /></span>
		</c:if>		
		&nbsp;
		<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
	</div>
</logic:present>

<%-- qubExtension, dynamic and moved up --%>
<bean:define id="action" name="action"/>

<c:if test="${empty enrolmentBeans}">
	<div class="alert alert-danger" role="alert"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="message.schoolClassStudentEnrollment.noOpenPeriods" /></div>
</c:if>

<logic:messagesPresent message="true" property="error">
	<div class="alert alert-danger alert-dismissible" role="alert">
	  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	  <strong><bean:message bundle="STUDENT_RESOURCES" key="label.enrollment.errors.in.enrolment" />: </strong>
	  <html:messages id="messages" message="true" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" property="error"><bean:write name="messages" /></html:messages>
	</div>
</logic:messagesPresent>
<logic:messagesPresent message="true" property="success">
	<div class="alert alert-success alert-dismissible" role="alert">
	  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	  <html:messages id="messages" message="true" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" property="success"><bean:write name="messages" /></html:messages>
	</div>
</logic:messagesPresent>

<c:forEach items="${enrolmentBeans}" var="enrolmentBean">
	<c:set value="${enrolmentBean.currentSchoolClass}" var="currentSchoolClass"/>
	<c:set value="${enrolmentBean.schoolClassToDisplay}" var="schoolClassToDisplay"/>
  	<c:set value="${enrolmentBean.schoolClassesToEnrol}" var="schoolClassesToEnrol"/>
	
	<div class="panel panel-default">
	  <div class="panel-heading">
		<h3>
			<c:out value="${enrolmentBean.enrolmentPeriod.executionSemester.qualifiedName}" />
			<small><c:out value="${enrolmentBean.registration.activeDegreeCurricularPlan.degree.presentationName}" /></small>
				<c:if test="${not empty schoolClassesToEnrol}">
					<c:if test="${not empty currentSchoolClass}">
						<span title="<bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassStudentEnrollment.selectedSchoolClassForPeriod" />" class="glyphicon glyphicon-ok text-success" aria-hidden="true"></span>
				  	</c:if>
					<c:if test="${empty currentSchoolClass}">
						<span title="<bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassStudentEnrollment.noSelectedSchoolClassForPeriod" />" class="glyphicon glyphicon-exclamation-sign text-warning" aria-hidden="true"></span>
				  	</c:if>				  	
				</c:if>
		</h3>
	  </div>
	  <div class="panel-body">
	  	<c:if test="${empty schoolClassesToEnrol}">
	  		<div class="alert alert-warning" role="alert"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="message.schoolClassStudentEnrollment.noAvailableSchoolClassesForPeriod" /></div>
	  	</c:if>
	  	<c:if test="${not empty schoolClassesToEnrol}">
	  		<p class="text-muted"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.shiftEnrolment.schoolClassesCurricularYear" />: <strong><c:out value="${enrolmentBean.curricularYear}" /></strong></p>
			<ul class="nav nav-tabs">
				<c:forEach items="${enrolmentBean.schoolClassesToEnrol}" var="schoolClass">
					
					<bean:define id="activeClass"><c:if test="${schoolClass eq schoolClassToDisplay}">active</c:if> q</bean:define>
					<li class="<%= activeClass %>">
					
						<bean:define id="link"><%=action.toString()%>?method=viewSchoolClass&schoolClassID=<c:out value="${schoolClass.externalId}" />&studentCurricularPlanOID=<c:out value="${enrolmentBean.studentCurricularPlan.externalId}" />&enrolmentPeriodID=<c:out value="${enrolmentBean.enrolmentPeriod.externalId}" /><c:if test="${not empty workflowRegistrationOid}">&workflowRegistrationOid=${workflowRegistrationOid}</c:if></bean:define>
						<html:link page="<%= link %>">
							<c:out value="${schoolClass.editablePartOfName}" />
							<c:if test="${(not empty currentSchoolClass) and (schoolClass eq currentSchoolClass)}">
								<span class="badge"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassStudentEnrollment.selected" /></span>
							</c:if>
						</html:link>
						
					</li>
				</c:forEach>
			</ul>
			
			<p></p>
			
			<c:if test="${not empty schoolClassToDisplay}">
				<c:choose>
				    <c:when test="${(not empty currentSchoolClass) and (schoolClassToDisplay eq currentSchoolClass)}">
				    	<c:set value="true" var="renderingCurrentSchoolClass"/>
						<bean:define id="removeSchoolClassLink"><%=action.toString()%>?method=enrollInSchoolClass&studentCurricularPlanOID=<c:out value="${enrolmentBean.studentCurricularPlan.externalId}" />&enrolmentPeriodID=<c:out value="${enrolmentBean.enrolmentPeriod.externalId}" /><c:if test="${not empty workflowRegistrationOid}">&workflowRegistrationOid=${workflowRegistrationOid}</c:if></bean:define>
						<html:link onclick="disabledOnClick(this);" page="<%= removeSchoolClassLink %>" styleClass="btn btn-warning btn-xs mtop15">
							<span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="button.schoolClassStudentEnrollment.unselectSchoolClass" />
						</html:link>			
				    </c:when>
				    <c:otherwise>
						<bean:define id="selectSchoolClassLink"><%=action.toString()%>?method=enrollInSchoolClass&schoolClassID=<c:out value="${schoolClassToDisplay.externalId}" />&studentCurricularPlanOID=<c:out value="${enrolmentBean.studentCurricularPlan.externalId}" />&enrolmentPeriodID=<c:out value="${enrolmentBean.enrolmentPeriod.externalId}" /><c:if test="${not empty workflowRegistrationOid}">&workflowRegistrationOid=${workflowRegistrationOid}</c:if></bean:define>
						<bean:define id="selectSchoolClassLinkCssClass">btn btn-primary btn-xs mtop15 <c:if test="${not enrolmentBean.schoolClassToDisplayFree}">disabled</c:if></bean:define>
						<html:link onclick="disabledOnClick(this);" page="<%= selectSchoolClassLink %>" styleClass="<%= selectSchoolClassLinkCssClass %>">
							<span class="glyphicon glyphicon-ok" aria-hidden="true"></span> <bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="button.schoolClassStudentEnrollment.selectSchoolClass" />
						</html:link>		    
				    	<c:if test="${not enrolmentBean.schoolClassToDisplayFree}">&nbsp;&nbsp;<span class="text-warning"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassStudentEnrollment.fullSchoolClass" /></span></c:if>
				    </c:otherwise>
				</c:choose>
				
				<p class="text-muted mtop15 pull-right"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.shiftEnrolment.matchedCoursesNumber" />: <strong><c:out value="${enrolmentBean.matchedCoursesNumber}" /></strong></p><br/>
		
				<c:set value="${enrolmentBean.schoolClassToDisplayLessonsJson}" var="schoolClassToDisplayLessonsJson"/>
				<c:if test="${not empty schoolClassToDisplayLessonsJson}">
					<c:forEach items="${enrolmentBean.schoolClassToDisplayShifts}" var="schoolClassShift">
						<bean:define id="removeShiftLink"><%=action.toString()%>?method=removeShift&studentCurricularPlanOID=<c:out value="${enrolmentBean.studentCurricularPlan.externalId}" />&enrolmentPeriodID=<c:out value="${enrolmentBean.enrolmentPeriod.externalId}" />&shiftID=<c:out value="${schoolClassShift.externalId}" /><c:if test="${not empty workflowRegistrationOid}">&workflowRegistrationOid=${workflowRegistrationOid}</c:if></bean:define>
						<html:link page="<%= removeShiftLink %>" styleClass="btn btn-danger removeShiftLink hidden" styleId="removeShiftLink-${schoolClassShift.externalId}">
							<bean:message bundle="APPLICATION_RESOURCES" key="label.remove" />&nbsp;&nbsp;<span class="glyphicon glyphicon-remove" style="color: #FFF"></span>
						</html:link>					 
					</c:forEach>
				 
					<div id="calendar-${schoolClassToDisplay.externalId}"></div>
					<script>
					$(document).ready(function() {
						
						var i18nDayNames = [
							"<bean:message bundle="ENUMERATION_RESOURCES" key="SUNDAY.short" />",
							"<bean:message bundle="ENUMERATION_RESOURCES" key="MONDAY.short" />",
							"<bean:message bundle="ENUMERATION_RESOURCES" key="TUESDAY.short" />",
							"<bean:message bundle="ENUMERATION_RESOURCES" key="WEDNESDAY.short" />",
							"<bean:message bundle="ENUMERATION_RESOURCES" key="THURSDAY.short" />",
							"<bean:message bundle="ENUMERATION_RESOURCES" key="FRIDAY.short" />",
							"<bean:message bundle="ENUMERATION_RESOURCES" key="SATURDAY.short" />"
						];						
						
						$('#calendar-<c:out value="${schoolClassToDisplay.externalId}"/>').fullCalendar({
								header: { left: '', center: '', right: '' },
								defaultView: 'agendaWeek',
								columnFormat: { week: 'ddd' },			
								minTime: '08:00',
								maxTime: '24:00',
								height: 'auto',
								timeFormat: 'HH:mm',
								axisFormat: 'HH:mm',
								allDaySlot : false,
								dayNamesShort: i18nDayNames,
								firstDay: 1,	
								hiddenDays: [0],						
								editable: false,
								eventLimit: true, // allow "more" link when too many events
								events: <c:out value="${schoolClassToDisplayLessonsJson}" escapeXml="false" />,
								<c:if test="${renderingCurrentSchoolClass}">
									eventClick: function(calEvent, jsEvent, view) {
										$('#removeShiftModal .modal-title').text(calEvent.title);
										$('#removeShiftModal .modal-footer .removeShiftLink').remove();
										$('#removeShiftModal .modal-footer').append($('#removeShiftLink-' + calEvent.shiftId).clone());
										$('#removeShiftModal .modal-footer #removeShiftLink-' + calEvent.shiftId).removeClass('hidden');
										$('#removeShiftModal').modal({ });							        
								    },
								    eventMouseover: function(calEvent, jsEvent, view) {
								     	$(this).addClass('calendarHoverIn');
								    },
								    eventMouseout: function(calEvent, jsEvent, view) {
								     	$(this).removeClass('calendarHoverIn');
								    }								    
								    
								</c:if>
							});
						
					});
					</script>	
					
					<div id="removeShiftModal" class="modal fade bs-remove-shift-modal-sm" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
						<div class="modal-dialog modal-sm" >
							<div class="modal-content">
								<div class="modal-header">
								  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
								  <h4 class="modal-title">"label.shiftEnrolment.removeShift"</h4>
								</div>
								<div class="modal-body">
									<bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.shiftEnrolment.removeShift.confirmation" />
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default" data-dismiss="modal"><bean:message bundle="APPLICATION_RESOURCES" key="button.cancel" /></button>	
								</div>
							</div>
						</div>
					</div>										
					
				</c:if>
			</c:if>
			
	  	</c:if>	  	
	  </div>
	</div>

</c:forEach>

<script>
function disabledOnClick(btn) {
	$(btn).addClass('disabled');
}
</script>