<%--
   This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
   copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
   software development project between Quorum Born IT and Serviços Partilhados da
   Universidade de Lisboa:
    - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
    - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
  
   Contributors: joao.roxo@qub-it.com
  
   
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
<%@page import="org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<html:xhtml/>

<bean:define id="personBean" name="personBean" type="org.fenixedu.academic.dto.person.PersonBean"/>

	<h3 class="mbottom025"><bean:message key="label.others" bundle="APPLICATION_RESOURCES"/></h3>
<%-- <% if (FirstYearRegistrationConfiguration.requiresVaccination(personBean.getPerson())) {%> --%>
	<table class="tstyle1 thright thlight mtop0">
	  <tr>
	    <th class="width14em">Validade da Vacina Antitetânica:</th>
	    <td>
		    <%
		    	PersonUlisboaSpecifications personUl = personBean.getPerson().getPersonUlisboaSpecifications();
		    	if ((personUl == null) || (personUl.getVaccinationValidity() == null)) {
		    %>
		    	-
	    	<% } else { %>
		    	<fr:view name="personBean" property="person.personUlisboaSpecifications.vaccinationValidity"/>
		    <% } %>
	    </td>
	  </tr>
	</table>
<%-- <% } %> --%>
