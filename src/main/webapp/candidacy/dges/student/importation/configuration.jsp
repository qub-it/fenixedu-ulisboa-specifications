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
<%@ page isELIgnored="true"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<html:xhtml />

	<h2><bean:message key="link.dges.importation.configuration" bundle="ULISBOA_SPECIFICATIONS_RESOURCES" /></h2>
	
	<html:link page="/dgesStudentImportationProcess.do?method=list">
		<bean:message key="button.back" bundle="APPLICATION_RESOURCES"/>
	</html:link>

	<fr:form id="uLisboaSpecifications.registrationProtocolForm" action="/dgesStudentImportationProcess.do?method=configuration">
		<fr:edit id="uLisboaSpecifications.registrationProtocol" name="uLisboaSpecificationsRoot">
			<fr:schema bundle="ULISBOA_SPECIFICATIONS_RESOURCES" type="org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot">
				<fr:slot name="defaultRegistrationProtocol" layout="menu-select-postback" key="label.dges.importation.default.registrationProtocol">
					<fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.RegistrationProtocolsProvider"/>
					<fr:property name="format" value="${code} - ${description.content}" />
					<fr:property name="destination" value="postback" />
				</fr:slot>
			</fr:schema>
			
			<fr:destination name="postback" path="/dgesStudentImportationProcess.do?method=configuration" />
		</fr:edit>
	</fr:form>
	
	
	<br/>
	<bean:message key="label.dges.importation.configuration.contingent.to.ingressions" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/>:<br/>
	<logic:notEmpty name="uLisboaSpecificationsRoot" property="contingentToIngressionsSet">
		<fr:view name="uLisboaSpecificationsRoot" property="contingentToIngressionsSet">
			<fr:schema bundle="ULISBOA_SPECIFICATIONS_RESOURCES" type="org.fenixedu.ulisboa.specifications.domain.ContingentToIngression">
				<fr:slot name="contingent" key="label.dges.importation.configuration.contingent"/>
				<fr:slot name="ingressionType" key="label.dges.importation.configuration.ingressionType">
					<fr:property name="format" value="${code} - ${description.content}" />
				</fr:slot>
			</fr:schema>
			<fr:layout name="tabular">
			   		<fr:property name="classes" value="tstyle1 mtop05" />
					<fr:link label="button.delete,APPLICATION_RESOURCES" name="delete" link="/dgesStudentImportationProcess.do?method=deleteContingentToIngression&contingent=${contingent}"/>
			</fr:layout>
		</fr:view>
	</logic:notEmpty>
	<logic:empty name="uLisboaSpecificationsRoot" property="contingentToIngressionsSet">
		<em><bean:message key="label.dges.importation.configuration.contingent.to.ingressions.empty" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/></em><br/>
	</logic:empty>
	<br/>
	<bean:message key="label.dges.importation.configuration.add.contingent.to.ingressions" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/>:
	<fr:form id="contingentToIngressionBeanToAddForm" action="/dgesStudentImportationProcess.do?method=addContingentToIngression">
		<fr:edit id="contingentToIngressionBeanToAdd" name="contingentToIngressionBean">
			<fr:schema bundle="ULISBOA_SPECIFICATIONS_RESOURCES" type="org.fenixedu.ulisboa.specifications.action.student.access.importation.DgesStudentImportationProcessDA$ContingentToIngressionBean">
				<fr:slot name="contingent" key="label.dges.importation.configuration.contingent" required="true"/>
				<fr:slot name="ingressionType" layout="menu-select" key="label.dges.importation.configuration.ingressionType" required="true">
					<fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.choiceType.replacement.single.IngressionTypeProvider"/>
					<fr:property name="format" value="${code} - ${description.content}" />
				</fr:slot>
			</fr:schema>
		</fr:edit>
		
		<html:submit><bean:message key="button.add" bundle="APPLICATION_RESOURCES"/></html:submit>	
	</fr:form>
