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


<style>
.nav-tabs > li > a:hover, .nav-tabs > li > a:focus {
    text-decoration: none;
    background-color: #eee;
}
</style>

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.initialSchoolClass.student.enrolment.header" />
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
  	<c:set value="${enrolmentBean.initialSchoolClassesToEnrol}" var="schoolClassesToEnrol"/>
	
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
  		<p class="text-muted"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.shiftEnrolment.schoolClassesCurricularYear" />: <strong><c:out value="${enrolmentBean.curricularYear}" /></strong></p>
	  	<c:if test="${empty schoolClassesToEnrol}">
	  		<div class="alert alert-warning" role="alert"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="message.schoolClassStudentEnrollment.noAvailableSchoolClassesForPeriod" /></div>
	  	</c:if>
	  	<c:if test="${not empty schoolClassesToEnrol}">
			<p class="text-muted"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.shiftEnrolment.schoolClassesCurricularYear.selectOneSchoolClass" />:</p>
			<ul class="nav nav-tabs">
				<c:forEach items="${schoolClassesToEnrol}" var="schoolClass">
					<li class="${(not empty currentSchoolClass) and (schoolClass eq currentSchoolClass) ? 'active' : ''}">
						<bean:define id="link"><%=action.toString()%>?method=enrollInSchoolClass&schoolClassID=<c:out value="${schoolClass.externalId}" />&studentCurricularPlanOID=<c:out value="${enrolmentBean.studentCurricularPlan.externalId}" />&enrolmentPeriodID=<c:out value="${enrolmentBean.enrolmentPeriod.externalId}" /><c:if test="${not empty workflowRegistrationOid}">&workflowRegistrationOid=${workflowRegistrationOid}</c:if></bean:define>
						<html:link page="<%= link %>">
							<c:out value="${schoolClass.editablePartOfName}" />
							<c:if test="${(not empty currentSchoolClass) and (schoolClass eq currentSchoolClass)}">
								<span class="badge"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassStudentEnrollment.selected" /></span>
							</c:if>
							<c:if test="${empty currentSchoolClass || not ((schoolClass eq currentSchoolClass))}">
								<span class="badge" style="text-transform: lowercase;"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="button.schoolClassStudentEnrollment.selectSchoolClass" /></span>
							</c:if>
						</html:link>
					</li>
				</c:forEach>
			</ul>
			<c:if test="${not empty currentSchoolClass}">
				<bean:define id="removeSchoolClassLink"><%=action.toString()%>?method=enrollInSchoolClass&studentCurricularPlanOID=<c:out value="${enrolmentBean.studentCurricularPlan.externalId}" />&enrolmentPeriodID=<c:out value="${enrolmentBean.enrolmentPeriod.externalId}" /><c:if test="${not empty workflowRegistrationOid}">&workflowRegistrationOid=${workflowRegistrationOid}</c:if></bean:define>
				<html:link onclick="disabledOnClick(this);" page="<%= removeSchoolClassLink %>" styleClass="btn btn-default btn-xs mtop15">
					<span class="glyphicon glyphicon-remove" aria-hidden="true"></span> <bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="button.schoolClassStudentEnrollment.unselectSchoolClass" />
				</html:link>				
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