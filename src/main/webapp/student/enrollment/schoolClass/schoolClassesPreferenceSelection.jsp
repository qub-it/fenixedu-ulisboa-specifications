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
		<bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassPreference.student.enrolment.header" />
		<small></small>
	</h1>
</div>

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
    	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="<%= enrolmentProcess.getContinueURL(request) %>">
			<bean:message bundle="APPLICATION_RESOURCES" key="button.continue" />
		</a>
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
	
	<div class="panel panel-default">
	  <div class="panel-heading">
		<h3>
			<c:out value="${enrolmentBean.enrolmentPeriod.executionSemester.qualifiedName}" />
			<small><c:out value="${enrolmentBean.registration.activeDegreeCurricularPlan.degree.presentationName}" /></small>
		</h3>
	  </div>
	  <div class="panel-body">
  		<c:if test="${enrolmentBean.canSkipEnrolmentPreferences}">
	  		<c:if test="${!enrolmentBean.hasEnrolmentPreferencesProcessStarted}">
	  			<div class="alert alert-info" role="alert">
		  			<bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="message.schoolClassPreferenceStudentEnrollment.dontSkipEnrolmentPreferences.instructions" />:&nbsp;&nbsp;
					<bean:define id="link"><%=action.toString()%>?method=dontSkipEnrolmentPreferences&registrationDataByExecutionIntervalID=<c:out value="${enrolmentBean.orCreateRegistrationDataByInterval.externalId}" /></bean:define>
					<html:link onclick="disabledOnClick(this);" page="<%= link %>" styleClass="btn btn-info"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassPreferenceStudentEnrollment.dontSkipEnrolmentPreferences" /></html:link>
				</div>	  			
	  		</c:if>
  			<c:if test="${enrolmentBean.hasEnrolmentPreferencesProcessStarted}">
  				<div class="alert alert-warning" role="alert">
	  				<bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="message.schoolClassPreferenceStudentEnrollment.clearEnrolmentPreferences.instructions" />::&nbsp;&nbsp;
	  				<bean:define id="link"><%=action.toString()%>?method=clearEnrolmentPreferences&registrationDataByExecutionIntervalID=<c:out value="${enrolmentBean.orCreateRegistrationDataByInterval.externalId}" /></bean:define>
					<html:link onclick="disabledOnClick(this);" page="<%= link %>" styleClass="btn btn-warning"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassPreferenceStudentEnrollment.clearEnrolmentPreferences" /></html:link>
				</div>
  			</c:if>
  		</c:if>
	  	<c:if test="${empty enrolmentBean.enrolmentPreferencesSorted and !enrolmentBean.canSkipEnrolmentPreferences}">
	  		<div class="alert alert-warning" role="alert"><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="message.schoolClassStudentEnrollment.noAvailableSchoolClassesForPeriod" /></div>
	  	</c:if>
	  	<c:if test="${not empty enrolmentBean.enrolmentPreferencesSorted}">
			<div class="alert alert-info" role="alert">
				<bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="message.schoolClassPreferenceStudentEnrollment.changePreferenceOrder.instructions" arg0='<span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span>' arg1='<span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span>'/>:
			</div>

		  	<table class="table" style="width: auto;">
		  		<tr>
		  			<th><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.schoolClassPreferenceStudentEnrollment.preferenceOrder" /></th>
		  			<th><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.shiftEnrolment.schoolClassesCurricularYear" />: <strong><c:out value="${enrolmentBean.curricularYear}" /></strong></th>
		  			<th></th>
		  			<th></th>
		  		</tr>
				<c:forEach items="${enrolmentBean.enrolmentPreferencesSorted}" var="enrolmentPreference" varStatus="status">
					<tr>
						<td align="center"><span class="badge bold"><c:out value="${enrolmentPreference.preferenceOrder}"/></span></td>
						<td align="center"><c:out value="${enrolmentPreference.schoolClass.editablePartOfName}" /></td>
							<td>
								<c:if test="${!status.first}">
									<bean:define id="link"><%=action.toString()%>?method=changePreferenceOrder&increment=true&schoolClassEnrolmentPreferenceID=<c:out value="${enrolmentPreference.externalId}" /></bean:define>
									<html:link titleKey="message.schoolClassPreferenceStudentEnrollment.changePreferenceOrder.increment" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" onclick="disabledOnClick(this);" page="<%= link %>"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></html:link>
								</c:if>
							</td>
							<td>
								<c:if test="${!status.last}">
									<bean:define id="link"><%=action.toString()%>?method=changePreferenceOrder&increment=false&schoolClassEnrolmentPreferenceID=<c:out value="${enrolmentPreference.externalId}" /></bean:define>
									<html:link titleKey="message.schoolClassPreferenceStudentEnrollment.changePreferenceOrder.decrement" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" onclick="disabledOnClick(this);" page="<%= link %>"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></html:link>													
								</c:if>
							</td>
						</tr>
				</c:forEach> 	  	
		  	</table>
	  	</c:if>	 
	  	 	
	  </div>
	</div>

</c:forEach>

<script>
function disabledOnClick(btn) {
	$(btn).addClass('disabled');
}
</script>