<%--
   This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
   copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
   software development project between Quorum Born IT and Serviços Partilhados da
   Universidade de Lisboa:
    - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
    - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
  
  
   
   This file is part of FenixEdu fenixedu-ulisboa-specifications.
  
   FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
  
   FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.
  
   You should have received a copy of the GNU Lesser General Public License
   along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@page import="org.fenixedu.commons.i18n.I18N"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.student.enrolment.EnrolmentManagementDA"%>


<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<html:xhtml />

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<bean:message key="label.title.enrolmentManagement" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/>
		<small></small>
	</h1>
</div>

<logic:empty name="enrolmentProcesses">
	<em><bean:message key="label.EnrolmentProcess.noResults" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/></em>
</logic:empty>

<logic:messagesPresent message="true" property="success">
	<div class="success0" style="padding: 0.5em;">
		<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="success">
			<span><bean:write name="messages" /></span>
		</html:messages>
	</div>
</logic:messagesPresent>

<logic:messagesPresent message="true" property="warning" >
	<div class="warning0" style="padding: 0.5em;">
		<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="warning">
			<span><bean:write name="messages" /></span>
		</html:messages>
	</div>
</logic:messagesPresent>

<logic:messagesPresent message="true" property="error">
	<div class="error0" style="padding: 0.5em;">
		<html:messages id="messages" message="true" bundle="APPLICATION_RESOURCES" property="error">
			<span><bean:write name="messages" /></span>
		</html:messages>
	</div>
</logic:messagesPresent>

<logic:notEmpty name="enrolmentProcesses">	
	<table class="tstyle1 thlight mtop15 table">
		<thead>
			<tr>
				<th scope="col" width="80px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.EnrolmentProcess.executionYear" /></th>
				<th scope="col" class="aleft"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.EnrolmentProcess.curricularPlan" /></th>
				<th scope="col" width="350px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.EnrolmentProcess.steps" /></th>
				<th scope="col" width="150px;"></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="bean" items="${ enrolmentProcesses }">
				<bean:define id="enrolmentProcess" name="bean" type="org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess"/>
				<tr>
					<td class="acenter"><c:out value="${ bean.executionYear.qualifiedName }" /></td>
					<td class="aleft"><c:out value="${ bean.studentCurricularPlan.presentationName }" /></td>
					<td>
						<ol>
							<c:forEach var="stepDescription" items="${bean.stepsDescriptions}">
								<li><c:out value="${ stepDescription }" /></li>
							</c:forEach>
						</ol>
					</td>
					<td class="acenter">
						<div class="well well-sm" style="display: inline-block">
							<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a class="" href="<%= enrolmentProcess.getContinueURL(request) %>">
								<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.EnrolmentProcess.begin" />
							</a>
							&nbsp;<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
						</div>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</logic:notEmpty>

<h3 class="mtop15 separator2">
	<bean:message key="label.EnrolmentProcess.AcademicEnrolmentPeriod.open" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/>
</h3>
<logic:empty name="periodsOpenBeans">
	<em><bean:message key="label.EnrolmentProcess.AcademicEnrolmentPeriod.open.noResults" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/></em>
</logic:empty>
<logic:notEmpty name="periodsOpenBeans">
	<table class="tstyle1 thlight mtop025 table">
		<thead>
			<tr>
				<th scope="col" width="80px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.executionSemester" /></th>
				<th scope="col" class="aleft"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.degreeCurricularPlan" /></th>
				<th scope="col" width="150px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.type" /></th>
				<th scope="col" width="100px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.startDate" /></th>
				<th scope="col" width="100px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.endDate" /></th>
				<th scope="col" width="150px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.studentStatuteTypes" /></th>
				<th scope="col" width="150px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.statuteTypes" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="bean" items="${ periodsOpenBeans }">
				<tr>
					<td class="acenter"><c:out value="${ bean.executionSemester.qualifiedName }" /></td>
					<td class="aleft"><c:out value="${ bean.studentCurricularPlan.presentationName }" /></td>
					<td class="acenter"><c:out value="${ bean.enrolmentPeriodType.descriptionI18N.content }" /></td>
					<td class="acenter"><joda:format value='${bean.startDate}' pattern="dd-MM-yyyy HH:mm" /></td>
					<td class="acenter"><joda:format value='${bean.endDate}' pattern="dd-MM-yyyy HH:mm" /></td>
					<td class="acenter">
		                <c:if test="${ bean.studentStatuteTypes.size() > 5 }">
		                    <c:set var="statuteMessage">
		                        <c:forEach var="element" items="${ bean.studentStatuteTypes }">
		                            <li><c:out value="${ element.name.content }"/> </li>
		                        </c:forEach>
		                    </c:set>
		                    <div data-toggle="tooltip" data-html="true" title="${ statuteMessage }" >
		                        <spring:message code="message.AcademicEnrolmentPeriod.has.x.statuteTypes" arguments="${ bean.studentStatuteTypes.size() }" />
		                    </div>
		                </c:if>
		                <c:if test="${ bean.studentStatuteTypes.size() <= 5 && academicEnrolmentPeriod.statuteTypesSet.size() > 0 }">
		                    <c:forEach var="element" items="${ bean.studentStatuteTypes }">
		                        <li><c:out value="${ element.name.content }"/> </li>
		                    </c:forEach>
		                </c:if>
		                <c:if test="${ bean.studentStatuteTypes.size() == 0 }">-</c:if>
					</td>
					<td class="acenter">
	                    <c:set var="statuteMeaning">
	                    	<c:if test="${ bean.restrictToSelectedStatutes }">
	                    		<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.true" />
	                    	</c:if>
	                    	<c:if test="${ !bean.restrictToSelectedStatutes }">
	                    		<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.false" />
	                    	</c:if>
	                    </c:set>
		                <c:if test="${ bean.statuteTypes.size() > 5 }">
		                    <c:set var="statuteMessage">
		                    	<c:out value="${ statuteMeaning }"/>: 
		                        <c:forEach var="element" items="${ bean.statuteTypes }">
		                            <li><c:out value="${ element.name.content }"/> </li>
		                        </c:forEach>
		                    </c:set>
		                    <div data-toggle="tooltip" data-html="true" title="${ statuteMessage }" >
		                        <spring:message code="message.AcademicEnrolmentPeriod.has.x.statuteTypes" arguments="${ bean.statuteTypes.size() }" />
		                    </div>
		                </c:if>
		                <c:if test="${ bean.studentStatuteTypes.size() <= 5 && academicEnrolmentPeriod.statuteTypesSet.size() > 0 }">
		                	<c:out value="${ statuteMeaning }"/>: 
		                    <c:forEach var="element" items="${ bean.statuteTypes }">
		                        <li><c:out value="${ element.name.content }"/> </li>
		                    </c:forEach>
		                </c:if>
		                <c:if test="${ bean.statuteTypes.size() == 0 }">-</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</logic:notEmpty>


<h3 class="mbottom05 mtop15 separator2">
	<bean:message key="label.EnrolmentProcess.AcademicEnrolmentPeriod.upcoming" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/>
</h3>
<logic:empty name="periodsUpcomingBeans">
	<em><bean:message key="label.EnrolmentProcess.AcademicEnrolmentPeriod.upcoming.noResults" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/></em>
</logic:empty>
<logic:notEmpty name="periodsUpcomingBeans">
	<table class="tstyle1 thlight mtop025 table">
		<thead>
			<tr>
				<th scope="col" width="80px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.executionSemester" /></th>
				<th scope="col" class="aleft"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.degreeCurricularPlan" /></th>
				<th scope="col" width="150px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.type" /></th>
				<th scope="col" width="100px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.startDate" /></th>
				<th scope="col" width="100px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.endDate" /></th>
				<th scope="col" width="150px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.studentStatuteTypes" /></th>
				<th scope="col" width="150px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.statuteTypes" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="bean" items="${ periodsUpcomingBeans }">
				<tr>
					<td class="acenter"><c:out value="${ bean.executionSemester.qualifiedName }" /></td>
					<td class="aleft"><c:out value="${ bean.studentCurricularPlan.presentationName }" /></td>
					<td class="acenter"><c:out value="${ bean.enrolmentPeriodType.descriptionI18N.content }" /></td>
					<td class="acenter"><joda:format value='${bean.startDate}'  pattern="dd-MM-yyyy HH:mm" /></td>
					<td class="acenter"><joda:format value='${bean.endDate}'  pattern="dd-MM-yyyy HH:mm" /></td>
					<td class="acenter">
		                <c:if test="${ bean.studentStatuteTypes.size() > 5 }">
		                    <c:set var="statuteMessage">
		                        <c:forEach var="element" items="${ bean.studentStatuteTypes }">
		                            <li><c:out value="${ element.name.content }"/> </li>
		                        </c:forEach>
		                    </c:set>
		                    <div data-toggle="tooltip" data-html="true" title="${ statuteMessage }" >
		                        <spring:message code="message.AcademicEnrolmentPeriod.has.x.statuteTypes" arguments="${ bean.studentStatuteTypes.size() }" />
		                    </div>
		                </c:if>
		                <c:if test="${ bean.studentStatuteTypes.size() <= 5 && academicEnrolmentPeriod.statuteTypesSet.size() > 0 }">
		                    <c:forEach var="element" items="${ bean.studentStatuteTypes }">
		                        <li><c:out value="${ element.name.content }"/> </li>
		                    </c:forEach>
		                </c:if>
		                <c:if test="${ bean.studentStatuteTypes.size() == 0 }">-</c:if>
					</td>
					<td class="acenter">
	                    <c:set var="statuteMeaning">
	                    	<c:if test="${ bean.restrictToSelectedStatutes }">
	                    		<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.true" />
	                    	</c:if>
	                    	<c:if test="${ !bean.restrictToSelectedStatutes }">
	                    		<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.false" />
	                    	</c:if>
	                    </c:set>
		                <c:if test="${ bean.statuteTypes.size() > 5 }">
		                    <c:set var="statuteMessage">
		                    	<c:out value="${ statuteMeaning }"/>: 
		                        <c:forEach var="element" items="${ bean.statuteTypes }">
		                            <li><c:out value="${ element.name.content }"/> </li>
		                        </c:forEach>
		                    </c:set>
		                    <div data-toggle="tooltip" data-html="true" title="${ statuteMessage }" >
		                        <spring:message code="message.AcademicEnrolmentPeriod.has.x.statuteTypes" arguments="${ bean.statuteTypes.size() }" />
		                    </div>
		                </c:if>
		                <c:if test="${ bean.studentStatuteTypes.size() <= 5 && academicEnrolmentPeriod.statuteTypesSet.size() > 0 }">
		                	<c:out value="${ statuteMeaning }"/>: 
		                    <c:forEach var="element" items="${ bean.statuteTypes }">
		                        <li><c:out value="${ element.name.content }"/> </li>
		                    </c:forEach>
		                </c:if>
		                <c:if test="${ bean.statuteTypes.size() == 0 }">-</c:if>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</logic:notEmpty>
