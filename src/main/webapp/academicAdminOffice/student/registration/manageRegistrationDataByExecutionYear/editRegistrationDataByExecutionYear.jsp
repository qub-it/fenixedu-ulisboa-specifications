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
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<h1>
	<bean:message bundle="ACADEMIC_OFFICE_RESOURCES"
		key="title.manageRegistrationDataByExecutionYear.edit" />
</h1>


<logic:messagesPresent message="true" property="success">
	<ul class="nobullet list6">
		<html:messages id="messages" message="true" property="success"
			bundle="ACADEMIC_OFFICE_RESOURCES">
			<li><span class="success0"><bean:write name="messages" /></span></li>
		</html:messages>
	</ul>
</logic:messagesPresent>
<logic:messagesPresent>
	<ul class="nobullet list6">
		<html:messages id="messages" bundle="ACADEMIC_OFFICE_RESOURCES">
			<li><span class="error0"><bean:write name="messages" /></span></li>
		</html:messages>
	</ul>
</logic:messagesPresent>

<bean:define id="registrationId" name="dataByExecutionYearBean" property="dataByExecutionYear.registration.externalId" />
<bean:define id="degree" name="dataByExecutionYearBean" property="dataByExecutionYear.registration.degree" />

<fr:edit id="dataByExecutionYearBean" name="dataByExecutionYearBean" action="/manageRegistrationData.do?method=edit">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2 mtop2 thright" />
		<fr:property name="columnClasses" value=",,tderror1 tdclear" />
		<fr:property name="requiredMarkShown" value="true" />
	</fr:layout>
	<fr:schema bundle="ACADEMIC_OFFICE_RESOURCES" type="<%= request.getAttribute("dataByExecutionYearBean").getClass().getName() %>">
		<fr:slot name="dataByExecutionYear.executionYear.qualifiedName" key="label.executionYear" bundle="ACADEMIC_OFFICE_RESOURCES" readOnly="true" />
		<fr:slot name="enrolmentDate" key="label.enrolmentDate" bundle="ACADEMIC_OFFICE_RESOURCES" />
        <academic:allowed operation="ENROLMENT_WITHOUT_RULES" permission="ACADEMIC_OFFICE_ENROLMENTS_ADMIN" program="<%= degree %>">
            <fr:slot name="overridenCurricularYear" key="label.curricularYear.overriden" bundle="ACADEMIC_OFFICE_RESOURCES" />
            <fr:slot name="curricularYearPresentation" key="label.curricularYear" bundle="ACADEMIC_OFFICE_RESOURCES" readOnly="true" />
            <fr:slot name="curricularYearJustificationPresentation" key="label.curricularYear.justification" bundle="ACADEMIC_OFFICE_RESOURCES" readOnly="true" />
        </academic:allowed>
	</fr:schema>
	<fr:destination name="invalid" path="/manageRegistrationData.do?method=prepareEditInvalid"/>
	<fr:destination name="cancel" path='<%= "/student.do?method=visualizeRegistration&registrationID=" + registrationId.toString() %>'/>
</fr:edit>

<%-- TODO legidio, move this to a generic place --%>
<style type="text/css">
    div.col-sm-10 { padding-top: 7px; }
    div.col-sm-10 div.input-group { padding-top: 0px; }
</style>
