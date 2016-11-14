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
<%@ page isELIgnored="true"%>
<%@ page language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:xhtml/>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<h2><bean:message key="title.manage.schedule"/> <span class="small"><bean:write name="context_selection_bean" property="academicInterval.pathName"/></span></h2>

<h4>
	<bean:write name="context_selection_bean" property="executionDegree.degree.code"/> - <bean:write name="context_selection_bean" property="executionDegree.presentationName"/> 
	<span class="small"><bean:write name="context_selection_bean" property="curricularYear.year"/>&ordm; ano</span>
</h4>

<fr:form action="/chooseContext.do">
	<fr:edit name="context_selection_bean">
		<fr:schema type="org.fenixedu.academic.dto.resourceAllocationManager.ContextSelectionBean" bundle="SOP_RESOURCES">
			<fr:slot name="executionDegree" layout="menu-select-postback" key="property.context.degree"
				validator="pt.ist.fenixWebFramework.renderers.validators.RequiredValidator">
				<fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.ExecutionDegreeForAcademicSemesterSelectionBeanProvider" />
				<fr:property name="format" value="${degree.code} - ${presentationName}" />
				<fr:property name="sortBy" value="presentationName" />
				<fr:property name="destination" value="degreePostBack" />
				<fr:property name="nullOptionHidden" value="false" />
			</fr:slot>
			<fr:slot name="curricularYear" layout="menu-select-postback" key="property.context.curricular.year"
				validator="pt.ist.fenixWebFramework.renderers.validators.RequiredValidator">
				<fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.CurricularYearForExecutionDegreeProvider" />
				<fr:property name="format" value="${year} &ordm;" />
				<fr:property name="sortBy" value="year" />
				<fr:property name="destination" value="yearPostBack" />
				<fr:property name="nullOptionHidden" value="true" />
			</fr:slot>
		</fr:schema>		
	
		<fr:destination name="degreePostBack" path="/chooseContext.do?method=choosePostBackToContext" />
		<fr:destination name="yearPostBack" path="/chooseContext.do?method=choosePostBackToContext" />
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle5 thlight thright mtop05 mbottom0 thmiddle" />
			<fr:property name="columnClasses" value="width12em,width800px,tdclear tderror1" />
		</fr:layout>
	</fr:edit>
</fr:form>

<fr:form action="/chooseExecutionPeriod.do">
	<fr:edit schema="academicIntervalSelectionBean.choosePostBack"
		name="context_selection_bean">
		<fr:destination name="intervalPostBack" path="/chooseExecutionPeriod.do?method=choose" />
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle5 thlight thright mtop0 thmiddle" />
			<fr:property name="columnClasses" value="width12em,width800px,tdclear tderror1" />
		</fr:layout>
	</fr:edit>
</fr:form>

<div class="row">
<br />
	<div class="col-sm-offset-3 col-sm-3">
		<bean:define id="manageClassesLink">/manageClasses.do?method=listClasses&academicInterval=<bean:write name="academicInterval"/>&curricular_year_oid=<bean:write name="context_selection_bean" property="curricularYear.externalId"/>&execution_degree_oid=<bean:write name="context_selection_bean" property="executionDegree.externalId"/></bean:define>
		<html:link styleClass="btn btn-primary" page="<%= manageClassesLink %>">
			<bean:message key="link.manage.turmas" />
		</html:link>
	</div>

	<div class="col-sm-3">
		<bean:define id="manageShiftsLink">/manageShifts.do?method=listShifts&academicInterval=<bean:write name="academicInterval"/>&curricular_year_oid=<bean:write name="context_selection_bean" property="curricularYear.externalId"/>&execution_degree_oid=<bean:write name="context_selection_bean" property="executionDegree.externalId"/></bean:define>
		<html:link styleClass="btn btn-primary" page="<%= manageShiftsLink %>">
			<bean:message key="link.manage.turnos" />
		</html:link>
	</div>
</div>
