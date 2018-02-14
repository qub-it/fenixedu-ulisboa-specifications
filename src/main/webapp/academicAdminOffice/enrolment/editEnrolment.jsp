<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ page isELIgnored="true"%>

<h1>
	<bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="title.manageEnrolments.edit" />
</h1>


<logic:messagesPresent message="true" property="success">
	<ul class="nobullet list6">
		<html:messages id="messages" message="true" property="success"
			bundle="ACADEMIC_OFFICE_RESOURCES">
			<li><span class="success0"><bean:write name="messages" /></span></li>
		</html:messages>
	</ul>
</logic:messagesPresent>
<logic:messagesPresent message="true" property="error">
	<p>
		<span class="error0">
			<html:messages id="messages" message="true" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" property="error">
				<bean:write name="messages" />
			</html:messages>
		</span>
	</p>
</logic:messagesPresent>
<logic:messagesPresent>
	<ul class="nobullet list6">
		<html:messages id="messages" bundle="ACADEMIC_OFFICE_RESOURCES">
			<li><span class="error0"><bean:write name="messages" /></span></li>
		</html:messages>
	</ul>
</logic:messagesPresent>

<bean:define id="studentCurricularPlanId" name="enrolmentBean" property="enrolment.studentCurricularPlan.externalId" />
<bean:define id="executionPeriodId" name="enrolmentBean" property="enrolment.executionPeriod.externalId" />

<fr:edit id="enrolmentBean" name="enrolmentBean" action="/studentEnrolmentsExtended.do?method=editEnrolment">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2 mtop2 thright" />
		<fr:property name="columnClasses" value=",,tderror1 tdclear" />
		<fr:property name="requiredMarkShown" value="true" />
	</fr:layout>
	<fr:schema bundle="ACADEMIC_OFFICE_RESOURCES" type="<%= request.getAttribute("enrolmentBean").getClass().getName() %>">
		<fr:slot name="enrolment.code" key="label.code" readOnly="true"/>
		<fr:slot name="enrolment.presentationName" key="label.name" readOnly="true"/>
		<fr:slot name="enrolment.curriculumGroup.fullPath" key="label.group" readOnly="true"/>
		<fr:slot name="enrolment.weigthForCurriculum" key="label.set.evaluation.enrolment.weight" readOnly="true"/>
		<fr:slot name="enrolment.ectsCreditsForCurriculum" key="label.ects.credits" readOnly="true"/>
		<fr:slot name="enrolment.creationDateDateTime" key="label.enrolmentDate" layout="no-time" readOnly="true"/>
		<fr:slot name="annulmentDate" key="label.annulmentDate" required="true"/>
	</fr:schema>
	<fr:destination name="invalid" path="/studentEnrolmentsExtended.do?method=prepareEditEnrolmentInvalid"/>
	<fr:destination name="cancel" path='<%= "/studentEnrolmentsExtended.do?method=prepare&scpID=" + studentCurricularPlanId.toString() + "&executionSemesterID=" + executionPeriodId.toString() %>'/>
</fr:edit>
