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
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ page import="org.apache.struts.action.ActionMessages" %>
<%@page import="org.fenixedu.commons.i18n.I18N"%>
<%@page import="org.fenixedu.academic.ui.renderers.student.enrollment.bolonha.EnrolmentLayout"%>
<html:xhtml />

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<bean:message bundle="STUDENT_RESOURCES"  key="label.enrollment.courses" />
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
			<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.event.back" />
		</a>
	    &nbsp;|&nbsp;
	    <%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="<%= enrolmentProcess.getContinueURL(request) %>">
			<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.continue" />
		</a>
		&nbsp;
		<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
	</div>
</logic:present>

<%-- qubExtension, dynamic and moved up --%>
<bean:define id="action" name="action"/>
<fr:form action="<%=action.toString()%>">
	<input type="hidden" name="method" />

	<bean:define id="periodSemester" name="bolonhaStudentEnrollmentBean" property="executionPeriod.semester" />
	<bean:define id="executionYearName" name="bolonhaStudentEnrollmentBean" property="executionPeriod.executionYear.year" />
	<bean:define id="studentCurricularPlan" name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan" type="org.fenixedu.academic.domain.StudentCurricularPlan" />

	<%-- qubExtension, blocking debts message --%>
	<logic:present name="debtsMessage">
	    <div class="alert alert-danger">
            ${fr:message('resources.ApplicationResources', debtsMessage)}
	    </div>
	</logic:present>
	<logic:notPresent name="debtsMessage">

		<%-- qubExtension, remove
		<p class="mtop0 mbottom15">
			<strong><bean:message bundle="STUDENT_RESOURCES"  key="label.registration.basic"/>:</strong> <bean:write name="bolonhaStudentEnrollmentBean" property="studentCurricularPlan.degreeCurricularPlan.presentationName"/> 
		</p>
	
		
		<ul class="mbottom15">
			<li>
				<html:link action='<%= action.toString() + "?method=showEnrollmentInstructions" %>' styleClass="externallink" target="_blank"><bean:message bundle="STUDENT_RESOURCES"  key="label.viewInstructions"/></html:link>
			</li>
			<li>
				
				
			</li>
			<li>
				<html:link action="/viewStudentCurriculum.do?method=prepare" paramId="registrationOID" paramName="studentCurricularPlan" paramProperty="registration.externalId" styleClass="externallink" target="_blank"><bean:message bundle="STUDENT_RESOURCES"  key="label.viewStudentCurricularPlan"/></html:link>
			</li>
			<li>			
				
				<% request.setAttribute("academicSupportAddress", org.fenixedu.academic.domain.Installation.getInstance().getAcademicEmailAddress()); %>
				<html:link href="mailto:${academicSupportAddress}" styleClass="externallink">
					<bean:message key="link.academicSupport" bundle="GLOBAL_RESOURCES"/>
				</html:link>
			</li>
		</ul>
		--%>
	
		<logic:messagesPresent message="true" property="success">
			<p>
			<span class="success0" style="padding: 0.25em;">
				<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="success">
					<span><bean:write name="messages" /></span>
				</html:messages>
			</span>
			</p>
		</logic:messagesPresent>
		
		<logic:messagesPresent message="true" property="warning" >
			<div class="warning0" style="padding: 0.5em;">
			<p class="mvert0"><strong><bean:message bundle="STUDENT_RESOURCES" key="label.enrollment.warnings.in.enrolment" />:</strong></p>
			<ul class="mvert05">
				<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="warning">
					<% pageContext.setAttribute("messages", ((String) pageContext.getAttribute("messages")).replaceAll("\\?\\?\\?" + I18N.getLocale().toString() + "\\.", "").replaceAll("\\?\\?\\?", ""));%>
					<li><span><bean:write name="messages" /></span></li>
				</html:messages>
			</ul>
			</div>
		</logic:messagesPresent>
	
		<logic:messagesPresent message="true" property="error">
			<div class="error0 mvert1" style="padding: 0.5em;">
				<p class="mvert0"><strong><bean:message bundle="STUDENT_RESOURCES" key="label.enrollment.errors.in.enrolment" />:</strong></p>
				<ul class="mvert05">
					<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="error">
						<% pageContext.setAttribute("messages", ((String) pageContext.getAttribute("messages")).replaceAll("\\?\\?\\?" + I18N.getLocale().toString() + "\\.", "").replaceAll("\\?\\?\\?", ""));%>
						<li><span><bean:write name="messages" /></span></li>
					</html:messages>
				</ul>
			</div>
		</logic:messagesPresent>
		
		<%-- qubExtension, remove
		<p class="mtop15 mbottom025">
			<bean:message bundle="APPLICATION_RESOURCES"  key="label.saveChanges.message"/>:
		</p>
		--%>
		
		<div style="display: inline-block;" class="mtop05 mbottom15">
			<button type="submit" class="btn btn-primary" onclick="submitForm(this);"><bean:message bundle="APPLICATION_RESOURCES"  key="label.save"/></button>
			<span class="infoop2">
				<bean:message bundle="APPLICATION_RESOURCES" key="label.warning.coursesAndGroupsSimultaneousEnrolment"/>
			</span>
		</div>

		<logic:present name="openedEnrolmentPeriods">		
			<ul class="nav nav-tabs">
				<logic:iterate id="period" name="openedEnrolmentPeriods">				
					<logic:equal name="bolonhaStudentEnrollmentBean" property="executionPeriod.externalId" value="${period.executionSemester.externalId}">
						<li role="presentation" class="active">
							<a href="#">${period.executionSemester.qualifiedName}
							<br/><span class="small text-muted">${bolonhaStudentEnrollmentBean.registration.degree.presentationName}</span>
							</a>
						</li>
					</logic:equal>
					<logic:notEqual name="bolonhaStudentEnrollmentBean" property="executionPeriod.externalId" value="${period.executionSemester.externalId}">
						<li role="presentation">							
							<html:link onclick="return checkState()" action="/student/courseEnrolment.do?method=prepare&executionSemesterOID=${period.executionSemester.externalId}&studentCurricularPlanOID=${bolonhaStudentEnrollmentBean.studentCurricularPlan.externalId}">
								${period.executionSemester.qualifiedName}
							</html:link>
						</li>
					</logic:notEqual>
				</logic:iterate>	
			</ul>			
		</logic:present>
	
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
				
				<fr:property name="allowedToChooseAffinityCycle" value="<%= org.fenixedu.academic.domain.student.Registration.getEnrolmentsAllowStudentToChooseAffinityCycle().toString() %>"/>
				<fr:property name="allowedToEnrolInAffinityCycle" value="<%= org.fenixedu.academic.domain.student.Registration.getEnrolmentsAllowStudentToEnrolInAffinityCycle().toString() %>"/>
				
			</fr:layout>
		</fr:edit>
		
		<%-- qubExtension, remove
		<p class="mtop15 mbottom05"><bean:message bundle="APPLICATION_RESOURCES"  key="label.saveChanges.message"/>:</p>
		--%>
		<p class="mtop05 mbottom1">
			<button type="submit" class="btn btn-primary" onclick="submitForm(this);"><bean:message bundle="APPLICATION_RESOURCES"  key="label.save"/></button>
		</p>
	
		<p class="mtop2 mbottom0"><em><bean:message bundle="APPLICATION_RESOURCES"  key="label.legend"/>:</em></p>
		
		<p class="mvert05"><em><bean:message  key="label.curriculum.credits.legend.minCredits" bundle="APPLICATION_RESOURCES"/></em></p>
		<p class="mvert05"><em><bean:message  key="label.curriculum.credits.legend.creditsConcluded" bundle="APPLICATION_RESOURCES"/></em></p>
		<p class="mvert05"><em><bean:message  key="label.curriculum.credits.legend.maxCredits" bundle="APPLICATION_RESOURCES"/></em></p>
		
		<%-- qubExtension, remove
		<table class="mtop0">
		<tr>
			<td><div style="width: 10px; height: 10px; border: 1px solid #84b181; background: #eff9ee; float:left;"></div></td>
			<td><bean:message bundle="APPLICATION_RESOURCES"  key="label.confirmedEnrollments"/><span class="color888"> (<bean:message bundle="APPLICATION_RESOURCES"  key="label.greenLines"/>)</span></td>
		</tr>
		<tr>
			<td><div style="width: 10px; height: 10px; border: 1px solid #b9b983; background: #fafce6; float:left;"></div></td>
			<td><bean:message bundle="APPLICATION_RESOURCES"  key="label.temporaryEnrollments"/><span class="color888"> (<bean:message bundle="APPLICATION_RESOURCES"  key="label.yellowLines"/>)</span></td>
		</tr>
		<tr>
			<td><div style="width: 10px; height: 10px; border: 1px solid #be5a39; background: #ffe9e2; float:left;"></div></td>
			<td><bean:message bundle="APPLICATION_RESOURCES"  key="label.impossibleEnrollments"/><span class="color888"> (<bean:message bundle="APPLICATION_RESOURCES"  key="label.redLines"/>)</span></td>
		</tr>
		 --%>
		</table>


	<%-- NAVIGATION --%>
	<logic:present name="enrolmentProcess">
		<bean:define id="enrolmentProcess" name="enrolmentProcess" type="org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess"/>
		
		<div class="well well-sm mtop15" style="display: inline-block">
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

	<%-- qubExtension, blocking debts message --%>
	</logic:notPresent>
</fr:form>

<script type="text/javascript">
function submitForm(btn) {
	btn.form.method.value = 'enrolInDegreeModules';
	$(btn).addClass('disabled');
	$(btn).html('A Guardar...'); // TODO legidio, $(btn).html('${portal.message('resources.ApplicationResources', 'label.saving')}'); 
}

(function () {
    $('.showinfo3.mvert0').removeClass('table');
	$('.smalltxt.noborder.table').removeClass('table');
})();

function checkState(){
	if($.grep($("input[type=checkbox]"), function (item) { return $(item).is("[checked]") != item.checked; }).length > 0){
		result = window.confirm("<bean:message bundle="STUDENT_RESOURCES"  key="label.changeSemesterWithoutSave"/>");
		if(!result){
			return false;
		}
	}
	return true;
}
</script>

<%-- qubExtension, more credits info --%>
<style type="text/css">
	.curriculumGroupConcluded { margin: 1em 0; padding: 0.2em 0.5em 0.2em 0.5em; background-color: #e2f5e2; color: #146e14; }
	.minimumCreditsConcludedInCurriculumGroup { margin: 1em 0; padding: 0.2em 0.5em 0.2em 0.5em; background-color: #fbf8cc; color: #805500; }
	.wrongCreditsDistributionError {margin: 1em 0; padding: 0.2em 0.5em 0.2em 0.5em; background-color: #A60000; color: #ffffff; }
</style>
