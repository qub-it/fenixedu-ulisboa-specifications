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
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>
<%@ page language="java" %>
<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@page import="org.fenixedu.academic.domain.student.Registration"%>
<%@page import="org.fenixedu.academic.domain.student.curriculum.ICurriculum"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices"%>
<html:xhtml />

<h2><bean:message key="registration.curriculum" bundle="ACADEMIC_OFFICE_RESOURCES"/></h2>

<bean:define id="registrationCurriculumBean" name="registrationCurriculumBean" type="org.fenixedu.academic.dto.student.RegistrationCurriculumBean"/>
<%
	final Registration registration = registrationCurriculumBean.getRegistration();
	request.setAttribute("registration", registration);
	final ExecutionYear executionYear = registrationCurriculumBean.getExecutionYear();
	request.setAttribute("executionYear", executionYear);
%>

<academic:allowed operation="VIEW_FULL_STUDENT_CURRICULUM" program="<%= registration.getDegree() %>">
<p>
	<html:link page="/student.do?method=visualizeRegistration" paramId="registrationID" paramName="registration" paramProperty="externalId">
		<bean:message key="link.student.back" bundle="ACADEMIC_OFFICE_RESOURCES"/>
	</html:link>
</p>
</academic:allowed>


<div style="float: right;">
	<bean:define id="personID" name="registration" property="student.person.username"/>
	<html:img align="middle" src="<%= request.getContextPath() + "/user/photo/" + personID.toString()%>" altKey="personPhoto" bundle="IMAGE_RESOURCES" styleClass="showphoto"/>
</div>

<p class="mvert2">
	<span class="showpersonid">
	<bean:message key="label.student" bundle="ACADEMIC_OFFICE_RESOURCES"/>: 
		<fr:view name="registration" property="student" schema="student.show.personAndStudentInformation.short">
			<fr:layout name="flow">
				<fr:property name="labelExcluded" value="true"/>
			</fr:layout>
		</fr:view>
	</span>
</p>

<logic:present name="registration" property="ingressionType">

<h3 class="mbottom05"><bean:message key="label.registrationDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>

<fr:view name="registration" schema="student.registrationDetail" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle4 thright thlight mtop05"/>
		<fr:property name="rowClasses" value=",,tdhl1,,,,,,"/>
	</fr:layout>
</fr:view>
</logic:present>

<logic:notPresent name="registration" property="ingressionType">
<h3 class="mbottom05"><bean:message key="label.registrationDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
<fr:view name="registration" schema="student.registrationsWithStartData" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle4 thright thlight mtop05"/>
	</fr:layout>
</fr:view>
</logic:notPresent>

<fr:form action="/registration.do?method=viewRegistrationCurriculum">
	<fr:edit id="registrationCurriculumBean" 
		name="registrationCurriculumBean"
		visible="false"/>
	<fr:edit id="registrationCurriculumBean-executionYear" name="registrationCurriculumBean">
        <fr:schema type="org.fenixedu.academic.dto.student.RegistrationSelectExecutionYearBean" bundle="APPLICATION_RESOURCES">
            <fr:slot name="executionYear" layout="menu-select-postback" key="label.curriculum">
                <fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.ExecutionYearsFromRegistrationCurriculumLines"/>
                <fr:property name="format" value="\${qualifiedName}"/>
                <fr:property name="sortBy" value="qualifiedName=desc" />
                <fr:property name="destination" value="executionYearPostBack" />
                <fr:property name="defaultText" value="label.COMPLETED" />
                <fr:property name="key" value="true" />
                <fr:property name="bundle" value="ACADEMIC_OFFICE_RESOURCES" />
            </fr:slot>
        </fr:schema>
        <fr:destination name="executionYearPostBack" path="/registration.do?method=viewRegistrationCurriculum"/>
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle5 thright thlight mtop025 thmiddle"/>
			<fr:property name="columnClasses" value=",,tdclear"/>
		</fr:layout>
	</fr:edit>
</fr:form>

<%
	final ICurriculum curriculum = registrationCurriculumBean.getCurriculum(executionYear);
	request.setAttribute("curriculum", curriculum);	

	request.setAttribute("rawGrade", curriculum.getRawGrade());
	request.setAttribute("sumEctsCredits", curriculum.getSumEctsCredits());
	final Integer curricularYear = curriculum.getCurricularYear();
	request.setAttribute("curricularYear", curriculum.getCurricularYear());
	request.setAttribute("totalCurricularYears", curriculum.getTotalCurricularYears());
%>

<logic:equal name="curriculum" property="empty" value="true">
	<p class="mvert15">
		<em>
			<bean:message key="no.approvements" bundle="ACADEMIC_OFFICE_RESOURCES"/>
		</em>
	</p>	
</logic:equal>

<logic:equal name="curriculum" property="empty" value="false">
	<table class="tstyle4 thlight tdcenter mtop15">
		<tr>
			<th><bean:message key="label.numberAprovedCurricularCourses" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
			<th><bean:message key="label.total.ects.credits" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
			<th><bean:message key="average" bundle="STUDENT_RESOURCES"/></th>
			<logic:notEmpty name="executionYear">
				<th><bean:message key="label.curricular.year" bundle="STUDENT_RESOURCES"/></th>
			</logic:notEmpty>
			<logic:empty name="executionYear">
	            <logic:equal name="registrationCurriculumBean" property="conclusionProcessed" value="false">
		            <th><bean:message key="label.curricular.year" bundle="STUDENT_RESOURCES"/></th>
	            </logic:equal>
	            <logic:equal name="registrationCurriculumBean" property="conclusionProcessed" value="true">
		            <th><bean:message key="label.conclusionDate" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
		            <th><bean:message key="label.conclusionYear" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
	            </logic:equal>
			</logic:empty>
		</tr>
		<tr>
			<bean:size id="curricularEntriesCount" name="curriculum" property="curriculumEntries"/>
			<td><bean:write name="curricularEntriesCount"/></td>
			<td><bean:write name="sumEctsCredits"/></td>
			<logic:notEmpty name="executionYear">
                <td><bean:write name="rawGrade" property="value"/></td>
        		<td><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.curricularYear.begin.executionYear" arg0="<%=executionYear.getQualifiedName()%>" arg1="<%=String.valueOf(curricularYear)%>"/></td>
			</logic:notEmpty>
			<logic:empty name="executionYear">
				<logic:equal name="registrationCurriculumBean" property="conclusionProcessed" value="false">
	                <td><bean:write name="rawGrade" property="value"/></td>
				    <td><%=RegistrationServices.getCurricularYear(registration, (ExecutionYear) null).getResult()%></td>
				</logic:equal>
				<logic:equal name="registrationCurriculumBean" property="conclusionProcessed" value="true">
					<td><bean:write name="registrationCurriculumBean" property="finalGrade.value"/></td>
	                <td><bean:write name="registrationCurriculumBean" property="conclusionDate"/></td>
	                <td><bean:write name="registrationCurriculumBean" property="conclusionYear.qualifiedName"/></td>
				</logic:equal>			
			</logic:empty>
		</tr>
	</table>

		<p>
			<fr:view name="curriculum"/>
		</p>

        <%-- Extension --%>
        <p>
            <jsp:include page="/academicAdminOffice/student/registration/curriculumGradeCalculator.jsp" /> 
        </p>                

</logic:equal>
