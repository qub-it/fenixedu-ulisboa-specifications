<%--
   This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
   copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
   software development project between Quorum Born IT and Serviços Partilhados da
   Universidade de Lisboa:
    - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
    - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
  
   Contributors: shezad.anavarali@qub-it.com
  
   
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
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<bean:define id="shiftID" name="shift" property="externalId" />

<h2><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="title.manageShiftProfessorships" /> <small><c:out value="${shift.nome}" /></small></h2>
<h3>
	<bean:define id="executionCourse" name="shift" property="executionCourse" type="org.fenixedu.academic.domain.ExecutionCourse" />
	<%= org.fenixedu.ulisboa.specifications.domain.services.ExecutionCourseServices.getCode(executionCourse) %> - 
	<c:out value="${shift.executionCourse.name}" />
</h3>

<logic:messagesPresent message="true" property="error">
	<div class="alert alert-danger alert-dismissible" role="alert">
	  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	  <html:messages id="messages" message="true" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" property="error"><bean:write name="messages" /></html:messages>
	</div>
</logic:messagesPresent>
<logic:messagesPresent message="true" property="success">
	<div class="alert alert-success alert-dismissible" role="alert">
	  <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	  <html:messages id="messages" message="true" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" property="success"><bean:write name="messages" /></html:messages>
	</div>
</logic:messagesPresent>

<p>&nbsp;</p>

<h4><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.manageShiftProfessorships.teachersForThisShift" /></h4>
<c:forEach items="${shift.associatedShiftProfessorshipSet}" var="shiftProfessorship">
	<bean:define id="shiftProfessorshipID" name="shiftProfessorship" property="externalId" />
	
	<div class="panel panel-primary">
	  <div class="panel-heading">
	    <h3 class="panel-title">
			<c:out value="${shiftProfessorship.professorship.person.name}" />
			<html:link styleClass="btn btn-warning" action="<%= "/manageShiftProfessorships.do?method=deleteShiftProfessorship&shiftID=" + shiftID + "&shiftProfessorshipID=" + shiftProfessorshipID  %>">
				<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span> <bean:message key="label.remove" bundle="APPLICATION_RESOURCES"/>
			</html:link>		    
	    </h3>
	  </div>
	  <div class="panel-body">
		<fr:edit layout="flowLayout" id="<%= "editor-" + shiftProfessorshipID %>" name="shiftProfessorship" action="<%= "/manageShiftProfessorships.do?method=prepare&shiftID=" + shiftID  %>" >
			<fr:schema bundle="APPLICATION_RESOURCES" type="org.fenixedu.academic.domain.ShiftProfessorship" >
				<fr:slot name="percentage" key="property.shift.percentage" >
			<fr:validator name="org.fenixedu.academic.ui.renderers.validators.DoubleRangeValidator" >
               <fr:property name="lowerBound" value="0"/>
               <fr:property name="upperBound" value="100"/>				
			</fr:validator>
				</fr:slot>
			</fr:schema>
		</fr:edit>
	  </div>
	</div>
</c:forEach>

<p>&nbsp;</p>
<h4><bean:message bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" key="label.manageShiftProfessorships.teachersNotForThisShift" /></h4>
<ul class="list-group">
	<c:forEach items="${professorshipsToAdd}" var="professorship">
		<bean:define id="professorshipID" name="professorship" property="externalId" />
		<li class="list-group-item">
			<c:out value="${professorship.person.name}" />
			<html:link styleClass="btn btn-default" action="<%= "/manageShiftProfessorships.do?method=createShiftProfessorship&shiftID=" + shiftID + "&professorshipID=" + professorshipID  %>">
				<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> <bean:message key="label.add" bundle="APPLICATION_RESOURCES"/>
			</html:link>
		</li>
	</c:forEach>
</ul>
