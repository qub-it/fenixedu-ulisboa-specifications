<%--

    Copyright © 2002 Instituto Superior Técnico

    This file is part of FenixEdu Academic.

    FenixEdu Academic is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Academic is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@page import="org.fenixedu.academic.domain.student.RegistrationServices"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<html:xhtml />
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@page import="org.fenixedu.commons.i18n.I18N"%>
<%@page import="org.fenixedu.academic.ui.renderers.student.enrollment.bolonha.EnrolmentLayout"%>
<%@page import="org.fenixedu.academic.domain.curricularRules.executors.RuleResultMessage"%>
<%@page import="org.fenixedu.academic.domain.student.curriculum.CurriculumConfigurationInitializer.CurricularYearResult"%>
<%@page import="org.fenixedu.academic.domain.student.curriculum.CurriculumConfigurationInitializer"%>


<h2>
	<bean:write name="bolonhaStudentEnrollmentBean"  property="funcionalityTitle" />
</h2>

<bean:define id="periodSemester" name="bolonhaStudentEnrollmentBean" property="executionPeriod.childOrder" />
<bean:define id="executionYear" name="bolonhaStudentEnrollmentBean" property="executionPeriod.executionYear" type="org.fenixedu.academic.domain.ExecutionYear" />
<bean:define id="executionYearName" name="executionYear" property="year" />
<bean:define id="registration" name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan.registration" type="org.fenixedu.academic.domain.student.Registration" />
<bean:define id="student" name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan.registration.student" />

<p class="mtop15">
    <span class="showpersonid">
        <bean:message key="label.student" bundle="ACADEMIC_OFFICE_RESOURCES"/>:
        <fr:view name="student" schema="student.show.personAndStudentInformation.short">
            <fr:layout name="flow">
                <fr:property name="labelExcluded" value="true"/>
            </fr:layout>
        </fr:view>
    </span>
</p>

<p class="mtop15 mbottom025">
	<strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.executionPeriod"/>:</strong> <bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.periodDescription" arg0="<%=periodSemester.toString()%>" arg1="<%=executionYearName.toString()%>" />
</p>
<p class="mtop0 mbottom025">
    <strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.registration"/>:</strong> [<bean:write name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan.degree.code"/>] <bean:write name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan.degreeCurricularPlan.presentationName"/> 
</p>
<p class="mtop0 mbottom025">
    <% 
    final CurricularYearResult result = RegistrationServices.getCurricularYear(registration, executionYear); 
    pageContext.setAttribute("curricularYear", result.getResult());    
    %>
    <a href="#" onclick="javascript: jQuery('#curricularYearJustifications').slideToggle('fast');">
        <strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.curricularYear"/>:</strong>  <bean:write name="curricularYear" />
        <span class="ui-icon ui-icon-triangle-2-n-s" style="margin-bottom: -4px"></span>
    </a>
    <div id="curricularYearJustifications" class="bgcolor3 padding1" style="border: #ddd dotted 1px; display: none;">
        <%=result.getJustificationPresentation().replace("; ", "<br/>") %>
    </div>
</p>
<logic:present name="evaluationSeason">
	<p class="mtop0 mbottom025">
		<strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.evaluationSeason"/>:</strong> <bean:write name="evaluationSeason" /> 
	</p>
	<p class="mtop0 mbottom025">
		<strong><bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.ects.enrolled"/>:</strong> <bean:write name="enroledEctsCredits" /> 
	</p>
	<p class="mtop0 mbottom025">
		<strong><bean:write name="label.ects.extra"/>:</strong> <bean:write name="enroledExtraEctsCredits" /> 
	</p>
</logic:present>

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

	
	<p class="mtop10 mbottom1">
		<button type="submit" class="btn btn-primary" onclick="this.form.method.value='enrolInDegreeModules'; openPleaseWaitDialog();"><bean:message bundle="APPLICATION_RESOURCES"  key="label.save"/></button>
		<html:submit bundle="HTMLALT_RESOURCES" altKey="submit.back" onclick="this.form.method.value='backToStudentEnrollments';"><bean:message bundle="APPLICATION_RESOURCES"  key="label.back"/></html:submit>
		<a href="#" class="btn btn-primary" onclick="javascript: $('.emptyGroup').toggle();"><bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.toggle.emptyGroups"/></a>			
	</p>
	
	<fr:edit id="bolonhaStudentEnrolments" name="bolonhaStudentEnrollmentBean">
		<fr:layout name="bolonha-student-enrolment">
			<logic:present name="enrolmentLayoutClassName">
				<fr:property name="defaultLayout" value="<%=String.valueOf(request.getAttribute("enrolmentLayoutClassName"))%>"/>
			</logic:present>
			<logic:notPresent name="enrolmentLayoutClassName">
				<%-- qubExtension --%>
				<fr:property name="defaultLayout" value="<%=EnrolmentLayout.class.getName()%>"/>
			</logic:notPresent>
			<fr:property name="enrolmentClasses" value="se_enrolled smalltxt,se_enrolled smalltxt aright,se_enrolled smalltxt aright,se_enrolled smalltxt aright,se_enrolled aright" />
			<fr:property name="temporaryEnrolmentClasses" value="se_temporary smalltxt,se_temporary smalltxt aright,se_temporary smalltxt aright,se_temporary smalltxt aright,se_temporary aright" />
			<fr:property name="impossibleEnrolmentClasses" value="se_impossible smalltxt,se_impossible smalltxt aright,se_impossible smalltxt aright,se_impossible smalltxt aright,se_impossible aright" />
			<fr:property name="curricularCourseToEnrolClasses" value="smalltxt, smalltxt aright, smalltxt aright, aright" />				
			<fr:property name="groupRowClasses" value="se_groups" />

			<fr:property name="encodeGroupRules" value="true" />
			<fr:property name="encodeCurricularRules" value="true" />
			
			<fr:property name="allowedToChooseAffinityCycle" value="true"/>
			<fr:property name="allowedToEnrolInAffinityCycle" value="true"/>
						
		</fr:layout>
	</fr:edit>
	
	<p class="mtop10 mbottom1">
		<button type="submit" class="btn btn-primary" onclick="this.form.method.value='enrolInDegreeModules'; openPleaseWaitDialog();"><bean:message bundle="APPLICATION_RESOURCES"  key="label.save"/></button>
		<html:submit bundle="HTMLALT_RESOURCES" altKey="submit.back" onclick="this.form.method.value='backToStudentEnrollments';"><bean:message bundle="APPLICATION_RESOURCES"  key="label.back"/></html:submit>
		<a href="#" class="btn btn-primary" onclick="javascript: $('.emptyGroup').toggle();"><bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="label.toggle.emptyGroups"/></a>
	</p>

</html:form>

<table class="mtop2">
<tr>
	<td><div style="width: 10px; height: 10px; border: 1px solid #84b181; background: #eff9ee; float:left;"></div></td>
	<td><bean:message bundle="APPLICATION_RESOURCES"  key="label.confirmedEnrollments"/><span class="color888"> (<bean:message bundle="APPLICATION_RESOURCES"  key="label.greenLines"/>)</span></td>
</tr>
<tr>
	<td><div style="width: 10px; height: 10px; border: 1px solid #be5a39; background: #ffe9e2; float:left;"></div></td>
	<td><bean:message bundle="APPLICATION_RESOURCES"  key="label.impossibleEnrollments"/><span class="color888"> (<bean:message bundle="APPLICATION_RESOURCES"  key="label.redLines"/>)</span></td>
</tr>
</table>

<%-- qubExtension --%>
<jsp:include page="<%= "/layout/pleasewait.jsp"%>"/>

<%-- qubExtension, academic bug fix --%>
<script type="text/javascript">
(function () {
    $('.showinfo3.mvert0').removeClass('table');
	$('.smalltxt.noborder.table').removeClass('table');
})();
</script>


<%-- qubExtension, more credits info --%>
<style type="text/css">
	.curriculumGroupConcluded { margin: 1em 0; padding: 0.2em 0.5em 0.2em 0.5em; background-color: #e2f5e2; color: #146e14; }
	.minimumCreditsConcludedInCurriculumGroup { margin: 1em 0; padding: 0.2em 0.5em 0.2em 0.5em; background-color: #fbf8cc; color: #805500; }
	.wrongCreditsDistributionError {margin: 1em 0; padding: 0.2em 0.5em 0.2em 0.5em; background-color: #A60000; color: #ffffff; }
</style>
