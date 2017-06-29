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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html:xhtml />
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@page import="org.fenixedu.commons.i18n.I18N"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.studentEnrolment.bolonha.AcademicAdminOfficeImprovementBolonhaStudentEnrolmentDA.ImprovementAttendsBean"%>
<%@page import="org.fenixedu.academic.domain.ExecutionSemester"%>
<%@page import="org.fenixedu.academic.domain.ExecutionCourse"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.services.ExecutionCourseServices"%>
<h2>
	<bean:write name="bolonhaStudentEnrollmentBean"  property="funcionalityTitle" />
</h2>

<bean:define id="executionSemester" name="bolonhaStudentEnrollmentBean" property="executionPeriod" />
<bean:define id="periodSemester" name="bolonhaStudentEnrollmentBean" property="executionPeriod.semester" />
<bean:define id="executionYearName" name="bolonhaStudentEnrollmentBean" property="executionPeriod.executionYear.year" />



<p class="mtop15 mbottom025">
	<strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.executionPeriod"/>:</strong> <bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.periodDescription" arg0="<%=periodSemester.toString()%>" arg1="<%=executionYearName.toString()%>" />
</p>
<p class="mtop0 mbottom025">
	<strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.registration"/>:</strong> <bean:write name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan.degreeCurricularPlan.presentationName"/> 
</p>
<logic:present name="evaluationSeason">
	<p class="mtop0 mbottom025">
		<strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.evaluationSeason"/>:</strong> <bean:write name="evaluationSeason" /> 
	</p>
</logic:present>

<bean:define id="registration" name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan.registration" />
<bean:define id="student" name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan.registration.student" />

<logic:messagesPresent message="true" property="success">
	<div class="success0" style="padding: 0.5em;">
	<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="success">
		<span><bean:write name="messages" /></span>
	</html:messages>
	</div>
</logic:messagesPresent>

<logic:messagesPresent message="true" property="warning" >
	<div class="warning0" style="padding: 0.5em;">
	<p class="mvert0"><strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.student.enrollment.warnings.in.enrolment" />:</strong></p>
	<ul class="mvert05">
		<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="warning">
			<% pageContext.setAttribute("messages", ((String) pageContext.getAttribute("messages")).replaceAll("\\?\\?\\?" + I18N.getLocale().toString() + "\\.", "").replaceAll("\\?\\?\\?", ""));%>
			<li><span><bean:write name="messages" /></span></li>
		</html:messages>
	</ul>
	</div>
</logic:messagesPresent>

<logic:messagesPresent message="true" property="error">
	<div class="error0" style="padding: 0.5em;">
	<p class="mvert0"><strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.student.enrollment.errors.in.enrolment" />:</strong></p>
	<ul class="mvert05">
		<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="error">
			<% pageContext.setAttribute("messages", ((String) pageContext.getAttribute("messages")).replaceAll("\\?\\?\\?" + I18N.getLocale().toString() + "\\.", "").replaceAll("\\?\\?\\?", ""));%>
			<li><span><bean:write name="messages" /></span></li>
		</html:messages>
	</ul>
	</div>
</logic:messagesPresent>

<bean:define id="action" name="action"/>
<html:form action="<%= action.toString() %>">
	<html:hidden property="method" value=""/>
	<html:hidden property="withRules"/>
	<html:hidden property="enrolmentId" value=""/>
	<html:hidden property="executionCourseId" value=""/>

	<p class="mtop025 mbottom1">
		<html:submit bundle="HTMLALT_RESOURCES" altKey="submit.back" onclick="this.form.method.value='backToStudentEnrollments';"><bean:message bundle="APPLICATION_RESOURCES"  key="label.back"/></html:submit>			
	</p>
	
	<fr:edit id="bolonhaStudentEnrolments" name="bolonhaStudentEnrollmentBean" visible="false" />
	
	<h3 class="mtop2">
		<bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.title.manageImprovementsToAttend"/>
			<small><c:out value="${registration.student.person.name} (${registration.student.number})" /></small>
	</h3>

	<c:forEach items="${improvementAttendsBeans}" var="bean">

		<bean:define id="enrolmentId" name="bean" property="enrolment.externalId" />
		<p class="mtop3"><strong><c:out value="${bean.enrolment.code}" /> - <c:out value="${bean.enrolment.name}" /></strong></p>
		
		<p>
		 	<c:if test="${empty bean.attends}">
				<c:choose>
		 			<c:when test="${empty bean.executionCourses}">
						<em><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.manageImprovementsToAttend.noExecutionCourse" arg0="<%=((ExecutionSemester)executionSemester).getQualifiedName()%>"/></em>
					</c:when>
					<c:otherwise>
						<em><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.manageImprovementsToAttend.availableExecutionCourses"/></em>
					</c:otherwise>
				</c:choose>
		 	</c:if>
			
		 	<c:if test="${not empty bean.attends}">
		 		<bean:define id="chosenExecutionCourse" name="bean" property="attends.executionCourse" />
		 		<p><em><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.manageImprovementsToAttend.chosenExecutionCourse"/>:</em> <b><%=ExecutionCourseServices.getDegreeCurricularPlanPresentation(((ExecutionCourse)pageContext.getAttribute("chosenExecutionCourse")), false)%></b>
		 		<p><em><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.manageImprovementsToAttend.shiftEnroled"/>:</em> 
		 			<c:choose>
		 				<c:when test="${bean.shiftEnroled}"><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.yes"/></c:when>
		 				<c:otherwise><strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.no"/></strong></c:otherwise>
	 				</c:choose>
 				</p>
		 		
		 		<c:choose>
		 			<c:when test="bean.shiftEnroled"><em><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.manageImprovementsToAttend.shiftEnroled.otherExecutionCourses"/></em></c:when>
		 			<c:otherwise>
		 				<c:if test="${fn:length(bean.executionCourses) gt 1}">
			 				<em><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.manageImprovementsToAttend.shiftNotEnroled.otherExecutionCourses"/></em>
		 				</c:if>
	 				</c:otherwise>
	 			</c:choose>
		 	</c:if>
	 	</p>

		<c:forEach items="${bean.executionCourses}" var="executionCourse">	
		
		 	<c:if test="${empty bean.attends || bean.attends.executionCourse != executionCourse}">
		 		<c:if test="${not bean.shiftEnroled}">
					<bean:define id="executionCourseId" name="executionCourse" property="externalId" />
					<html:submit 
						onclick="<%= "this.form.method.value='enrolInAttend';this.form.enrolmentId.value='" + enrolmentId + "';this.form.executionCourseId.value='" + executionCourseId +"';"%>"
						> 
						<%=ExecutionCourseServices.getDegreeCurricularPlanPresentation(((ExecutionCourse)pageContext.getAttribute("executionCourse")), true)%>
					</html:submit>
				</c:if>
		 	</c:if>

		</c:forEach>
	
	</c:forEach>

</html:form>
