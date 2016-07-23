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
<html:xhtml/>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<logic:present role="role(STUDENT)">

<h1><bean:message bundle="STUDENT_RESOURCES" key="label.enrollment.courses.instructions"/></h1>

<h3 class="mtop15 separator2"><bean:message bundle="STUDENT_RESOURCES" key="label.introduction"/></h3>

<p><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="message.instructions.introduction"/></p>

<h3 class="mtop15 separator2"><bean:message bundle="STUDENT_RESOURCES" key="label.instructions.proceed"/></h3>

<p><b><bean:message bundle="STUDENT_RESOURCES" key="label.attention.nonCaps"/>:</b> <bean:message bundle="STUDENT_RESOURCES" key="message.instructions.proceed"/></p>

<ul class="list4">
<li><bean:message bundle="STUDENT_RESOURCES" key="label.instructions.proceed.register"/></li>
<li><bean:message bundle="STUDENT_RESOURCES" key="label.instructions.proceed.unsubscribe"/></li>
<li><bean:message bundle="STUDENT_RESOURCES" key="label.instructions.proceed.chooseGroup"/></li>
<li><bean:message bundle="STUDENT_RESOURCES" key="label.instructions.proceed.chooseDisciplines"/></li>
<li><bean:message bundle="STUDENT_RESOURCES" key="label.instructions.proceed.endProcess"/></li>
</ul>

<%-- qubExtension, EnrolmentProcess --%>
<logic:present name="enrolmentProcess">
	<bean:define id="enrolmentProcess" name="enrolmentProcess" type="org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess"/>

	<logic:present name="enrolmentProcess" property="executionSemester.enrolmentInstructions">
		<h3 class="mtop15 separator2"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.EnrolmentProcess.additional.instructions"/></h3>
	    <bean:write name="enrolmentProcess" property="executionSemester.enrolmentInstructions.tempInstructions.content" filter="false"/>
	</logic:present>

	<p class="mtop25 mbottom1">
		<div class="well well-sm" style="display: inline-block">
			<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
			<a class="" href="<%= enrolmentProcess.getReturnURL(request) %>">
				<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.event.back" />
			</a>
		</div>
		<div class="well well-sm" style="display: inline-block">
			<a class="" href="<%= enrolmentProcess.getContinueURL(request) %>">
				<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.continue" />
			</a>
			&nbsp;<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
		</div>
	</p>
</logic:present>

</logic:present>
