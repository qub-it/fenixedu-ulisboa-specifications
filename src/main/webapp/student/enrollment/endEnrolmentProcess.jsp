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
		<bean:message key="label.EnrolmentProcess.end" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/>
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
			<bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.finish" />
		</a>
		&nbsp;
		<span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>
	</div>
</logic:present>

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

<logic:notEmpty name="enrolmentProcess">	
	<table class="tstyle1 thlight mtop15 table">
		<thead>
			<tr>
				<th scope="col" width="80px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.EnrolmentProcess.executionYear" /></th>
				<th scope="col" class="aleft"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.EnrolmentProcess.curricularPlan" /></th>
				<th scope="col" width="350px;"><bean:message bundle="ULISBOA_SPECIFICATIONS_RESOURCES" key="label.EnrolmentProcess.steps" /></th>
			</tr>
		</thead>
		<tbody>
				<bean:define id="bean" name="enrolmentProcess" type="org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess"/>
				<tr>
					<td class="acenter"><c:out value="${ bean.executionYear.qualifiedName }" /></td>
					<td class="aleft"><c:out value="${ bean.studentCurricularPlan.presentationName }" /></td>
					<td class="acenter">
						<ol>
							<c:forEach var="stepDescription" items="${bean.stepsDescriptions}">
								<li><c:out value="${ stepDescription }" /></li>
							</c:forEach>
						</ol>
					</td>
				</tr>
		</tbody>
	</table>
</logic:notEmpty>

