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
<%-- WARNING: This JSP is used to generate the first year candidate report (printAllDocuments.jsp). Beware when editing it! --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/struts-example-1.0" prefix="app" %>
<%@ page import="org.fenixedu.academic.servlet.taglib.sop.v3.TimeTableType" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@page import="java.util.stream.Collectors"%>

<% request.setAttribute("executionSemesters", ExecutionYear.readCurrentExecutionYear().getExecutionPeriodsSet().stream().sorted().collect(Collectors.toList())); %>

<html:html xhtml="true">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="<%= request.getContextPath() %>/CSS/dotist_timetables.css" rel="stylesheet" type="text/css" />
		<link href="<%= request.getContextPath() %>/CSS/print.css" rel="stylesheet" media="print" type="text/css" />
		<title><bean:message key="private.student.view.timetable" bundle="TITLES_RESOURCES"/></title>
	</head>
	<body>
	
		<bean:define id="registrationId" name="registrationId" />
		<ul class="nav nav-tabs">
			<c:forEach items="${executionSemesters}" var="executionSemester">
				<bean:define id="executionSemesterID" name="executionSemester" property="externalId" />
				<bean:define id="activeSemester">. <c:if test="${executionSemesterID eq executionSemesterId}">active</c:if></bean:define>
				 <li role="presentation" class="<%= activeSemester %>">
					<bean:define id="executionSemesterID" name="executionSemester" property="externalId" />
					<html:link page="<%="/studentTimeTable.do?method=showTimeTable&registrationId=" + registrationId + "&executionSemesterID=" + executionSemesterID %>">
						<c:out value="${executionSemester.qualifiedName}"></c:out>
					</html:link>			
				 </li>
			</c:forEach>
		</ul>
	
		<div class="mtop2" align="center">
			<app:gerarHorario name="infoLessons" type="<%= TimeTableType.CLASS_TIMETABLE %>" application="${pageContext.request.contextPath}"/>
		</div>
		<logic:present name="tutor">
			<div style="text-align: left">
				<strong><bean:message key="label.tutor" bundle="APPLICATION_RESOURCES"/></strong><br/>
				<bean:write name="tutor" property="name"/><br/>
				<bean:write name="tutor" property="defaultEmailAddressValue"/>
			</div>
		</logic:present>
	</body>
</html:html>