<%--
   This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
   copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
   software development project between Quorum Born IT and Serviços Partilhados da
   Universidade de Lisboa:
    - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
    - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
  
   Contributors: daniel.pires@qub-it.com
  
   
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
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<html:xhtml/>

<bean:define id="studentID" type="java.lang.String" name="student" property="externalId"/>

<p class="mtop2">
	<html:link page="<%= "/student.do?method=prepareEditPersonalData&studentID=" + studentID %>">
		<bean:message key="link.student.back" bundle="ACADEMIC_OFFICE_RESOURCES"/>
	</html:link>
</p>

<h3 class="mbottom025"><bean:message key="label.healthCardNumber" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" />:</h3>
<fr:form action="<%= "/healthCardNumber.do?method=editHealthCardNumber&studentID=" + studentID %>">	
	<fr:edit id="healthCardNumberBean" name="healthCardNumberBean">
		<fr:schema bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES" type="org.fenixedu.ulisboa.specifications.action.student.HealthCardNumberDA$HealthCardNumberBean">
			<fr:slot key="label.string" name="healthCardNumber"/>
		</fr:schema>
		<fr:layout name="tabular" >
			<fr:property name="classes" value="tstyle1 thlight thright mtop025"/>
	        <fr:property name="columnClasses" value="width14em,,tdclear tderror1"/>
		</fr:layout>
	</fr:edit>
	
	<html:submit><bean:message key="button.submit" bundle="ACADEMIC_OFFICE_RESOURCES" /></html:submit>
</fr:form>
