<%@page import="org.fenixedu.academic.dto.student.RegistrationConclusionBean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" %>
<%@ page isELIgnored="false"%>
<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@page import="org.fenixedu.academic.domain.student.Registration"%>
<%@page import="org.fenixedu.academic.domain.student.curriculum.ICurriculum"%>
<html:xhtml />

<h2><bean:message key="label.programConclusionsSummary" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES"/></h2>

<bean:define id="registration" name="studentCurricularPlan" property="registration" />

<p>
	<html:link page="/student.do?method=visualizeRegistration" paramId="registrationID" paramName="registration" paramProperty="externalId">
		<bean:message key="link.student.back" bundle="ACADEMIC_OFFICE_RESOURCES"/>
	</html:link>
</p>


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
<span class="bold">${registration.degree.presentationName} ${not empty registration.degree.code ? '('.concat(registration.degree.code).concat(')') : ''} - ${studentCurricularPlan.name}</span> 

<div style="clear: both;"></div>

<c:choose>
	<c:when test="${conclusionBeans.size() > 0}">
		<ul id="tabs" class="nav nav-tabs" data-tabs="tabs">
			<c:forEach var="conclusionBean" varStatus="conclusionBeanIterationStatus" items="${conclusionBeans}">
		    	<li class="${conclusionBeanIterationStatus.index == 0 ? 'active' : ''}"><a href="#pc_${conclusionBean.programConclusion.externalId}" data-toggle="tab">${conclusionBean.programConclusion.name.content} - ${conclusionBean.programConclusion.description.content}</a></li>
			</c:forEach>
		</ul>
		<div id="tabContent" class="tab-content">
			<c:forEach var="conclusionBean" varStatus="conclusionBeanIterationStatus" items="${conclusionBeans}">
			    <div class="tab-pane${conclusionBeanIterationStatus.index == 0 ? ' active' : ''}" id="pc_${conclusionBean.programConclusion.externalId}">
			    	<fr:view name="conclusionBean" schema="RegistrationConclusionBean.viewForCycleWithConclusionProcessedInformation">
						<fr:layout name="tabular">
							<fr:property name="classes" value="tstyle4 thright thlight mvert05"/>
							<fr:property name="columnClasses" value=",,tderror1 tdclear"/>
						</fr:layout>
					</fr:view>
					<fr:view name="conclusionBean" property="curriculumForConclusion">
						<fr:layout>
							<fr:property name="visibleCurricularYearEntries" value="false" />
						</fr:layout>
					</fr:view>
			    </div>
		    </c:forEach>
		</div>
	</c:when>
	<c:otherwise>
		<bean:message key="label.noResultsFound" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES"/>
	</c:otherwise>
</c:choose>


