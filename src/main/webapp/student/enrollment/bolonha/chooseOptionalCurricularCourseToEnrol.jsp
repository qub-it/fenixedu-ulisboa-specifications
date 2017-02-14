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
<%@page import="com.google.common.collect.Lists"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<html:xhtml />
<%@page import="org.fenixedu.commons.i18n.I18N"%>
<%@page import="java.util.List"%>
<%@page import="org.fenixedu.academic.domain.Degree"%>
<%@page import="org.fenixedu.academic.domain.DegreeCurricularPlan"%>
<%@page import="org.fenixedu.academic.domain.degree.DegreeType"%>
<%@page import="org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha.BolonhaDegreeTypesProviderForOptionalEnrollment"%>
<%@page import="org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha.DegreesByDegreeTypeForOptionalEnrollment"%>
<%@page import="org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha.DegreeCurricularPlansForDegreeForOptionalEnrollment"%>
<%@page import="org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentOptionalEnrollmentBean"%>

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<bean:message key="label.enrollment.optional.course" bundle="STUDENT_RESOURCES" />
		<small></small>
	</h1>
</div>

<%-- qubExtension --%>
<bean:define id="action" value="/student/courseEnrolment.do" />
<fr:form action="<%=action.toString()%>">

	<input type="hidden" name="method" value="" />

    <%-- qubExtension, avoid choosing values in drop-downs if only one option is be available --%>
    <% 
    final BolonhaStudentOptionalEnrollmentBean bean = (BolonhaStudentOptionalEnrollmentBean) request.getAttribute("optionalEnrolmentBean");
    
    final BolonhaDegreeTypesProviderForOptionalEnrollment typeProvider = new BolonhaDegreeTypesProviderForOptionalEnrollment();
    
    final List<DegreeType> types = (List<DegreeType>) typeProvider.provide(bean, bean.getDegreeType());
    List<Degree> degrees = Lists.newArrayList();
    List<DegreeCurricularPlan> dcps = Lists.newArrayList();
    
    if (types.size() == 1) {
        bean.setDegreeType(types.iterator().next());
        
        final DegreesByDegreeTypeForOptionalEnrollment degreesProvider = new DegreesByDegreeTypeForOptionalEnrollment();
        degrees = (List<Degree>) degreesProvider.provide(bean, bean.getDegree());

        if (degrees.size() == 1) {
            bean.setDegree(degrees.iterator().next());
            
            final DegreeCurricularPlansForDegreeForOptionalEnrollment dcpsProvider = new DegreeCurricularPlansForDegreeForOptionalEnrollment();
            dcps = (List<DegreeCurricularPlan>) dcpsProvider.provide(bean, bean.getDegreeCurricularPlan());
            
            if (dcps.size() == 1) {
                bean.setDegreeCurricularPlan(dcps.iterator().next());
            }
        }
    }
    %>

	<fr:edit id="optionalEnrolment" name="optionalEnrolmentBean" visible="<%= !(types.size() == 1 && degrees.size() == 1 && dcps.size() == 1)%>">

		<%-- qubExtension --%>	
		<fr:schema bundle="STUDENT_RESOURCES"
			type="org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentOptionalEnrollmentBean">
			<fr:slot name="degreeType" key="label.degreeType"
				layout="menu-select-postback"
				validator="pt.ist.fenixWebFramework.renderers.validators.RequiredValidator">
				<fr:property name="format" value="\${name.content}" />
				<fr:property name="providerClass"
					value="org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha.BolonhaDegreeTypesProviderForOptionalEnrollment" />
				<fr:property name="destination" value="updateComboBoxes"/>
			</fr:slot>
			<fr:slot name="degree" key="label.degree"
				layout="menu-select-postback"
				validator="pt.ist.fenixWebFramework.renderers.validators.RequiredValidator">
				<fr:property name="providerClass"
					value="org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha.DegreesByDegreeTypeForOptionalEnrollment" />
				<fr:property name="format" value="\${name}" />
				<fr:property name="destination" value="updateComboBoxes"/>
			</fr:slot>
			<fr:slot name="degreeCurricularPlan" key="label.degreeCurricularPlan"
				layout="menu-select-postback"
				validator="pt.ist.fenixWebFramework.renderers.validators.RequiredValidator">
				<fr:property name="providerClass"
					value="org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha.DegreeCurricularPlansForDegreeForOptionalEnrollment" />
				<fr:property name="format" value="\${name}" />
				<fr:property name="destination" value="updateComboBoxes"/>
			</fr:slot>
		</fr:schema>
	
		<fr:destination name="updateComboBoxes"
			path="<%=action.toString() + "?method=updateParametersToSearchOptionalCurricularCourses"%>" />
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle4 thlight thright" />
			<fr:property name="columnClasses" value=",,tdclear tderror1" />
		</fr:layout>
	</fr:edit>
	
	<logic:messagesPresent message="true" property="error">
		<div class="mtop1 mbottom15 error0" style="padding: 0.5em;">
		<p class="mvert0"><strong><bean:message bundle="STUDENT_RESOURCES" key="label.enrollment.errors.in.enrolment" />:</strong></p>
		<ul class="mvert05">
			<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="error">
				<% pageContext.setAttribute("messages", ((String) pageContext.getAttribute("messages")).replaceAll("\\?\\?\\?" + I18N.getLocale().toString() + "\\.", "").replaceAll("\\?\\?\\?", ""));%>
				<li><span class="error0"><bean:write name="messages" /></span></li>
			</html:messages>
		</ul>
		</div>
	</logic:messagesPresent>

	<logic:present name="curricularRuleLabels">
		<logic:notEmpty name="curricularRuleLabels">
			<div class="smalltxt noborder">
				<ul class="mvert05">
					<logic:iterate id="curricularRuleLabel" name="curricularRuleLabels">
						<li><span style="color: #888"><bean:write name="curricularRuleLabel" /></span></li>
					</logic:iterate>
				</ul>
			</div>
		</logic:notEmpty>
	</logic:present>

	<logic:present name="optionalEnrolmentBean" property="degreeCurricularPlan">
		<fr:edit id="degreeCurricularPlan" name="optionalEnrolmentBean">
			<%-- qubExtension --%>
			<fr:layout name="bolonha-student-optional-enrolments-extended">
				<fr:property name="classes" value="mtop15" />
				<fr:property name="groupRowClasses" value="se_groups" />
			</fr:layout>
		</fr:edit>
	</logic:present>

</fr:form>

<fr:form action="<%=action.toString() + "?method=cancelChooseOptionalCurricularCourseToEnrol"%>">
<fr:edit id="optionalEnrolment" name="optionalEnrolmentBean" visible="false"/>
<p class="mtop15">
	<html:submit bundle="HTMLALT_RESOURCES" altKey="submit.submit">
		<bean:message bundle="APPLICATION_RESOURCES" key="label.cancel"/>
	</html:submit>
</p>
</fr:form>

<script type="text/javascript">
    $(function() {
		$('.showinfo3.mvert0').removeClass('table');
		$('.smalltxt.noborder.table').removeClass('table');
		$('form div[class="tstyle4 thlight thright"]').addClass('form-horizontal');
    });
</script>
