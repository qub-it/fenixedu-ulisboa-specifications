<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ page isELIgnored="true"%>

<h1>
	<bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="title.manageCredits.edit" />
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

<bean:define id="studentCurricularPlanId" name="creditsBean" property="credits.studentCurricularPlan.externalId" />

<fr:edit id="creditsBean" name="creditsBean" action="/studentDismissalsExtended.do?method=editCredits">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2 mtop2 thright" />
		<fr:property name="columnClasses" value=",,tderror1 tdclear" />
		<fr:property name="requiredMarkShown" value="true" />
	</fr:layout>
	<fr:schema bundle="ACADEMIC_OFFICE_RESOURCES" type="<%= request.getAttribute("creditsBean").getClass().getName() %>">
		<fr:slot name="officialDate" key="label.manageCredits.officialDate" help="label.manageCredits.officialDate.help" bundle="ACADEMIC_OFFICE_RESOURCES" />
		<fr:slot name="reason" layout="menu-select" key="label.manageCredits.reason" help="label.manageCredits.reason.help" bundle="ACADEMIC_OFFICE_RESOURCES">
			<fr:property name="from" value="reasonOptions" />
			<fr:property name="format" value="${reason}" />
			<fr:property name="sortBy" value="reason=asc" />
		</fr:slot>
	</fr:schema>
	<fr:destination name="invalid" path="/studentDismissalsExtended.do?method=prepareEditCreditsInvalid"/>
	<fr:destination name="cancel" path='<%= "/studentDismissals.do?method=manage&scpID=" + studentCurricularPlanId.toString() %>'/>
</fr:edit>
