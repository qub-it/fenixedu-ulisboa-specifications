<%--
    Copyright © 2015 Universidade de Lisboa
    
    This file is part of FenixEdu fenixedu-ulisboa-specifications.
    
    FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute 
    it and/or modify it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will
    be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu fenixedu-ulisboa-specifications.
    If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page isELIgnored="true"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<html:xhtml />
	<logic:present role="role(MANAGER)">
	<h2><bean:message key="title.dges.importation.process" bundle="ULISBOA_SPECIFICATIONS_RESOURCES" /></h2>
	
	<html:link page="/dgesStudentImportationProcess.do?method=configuration">
		<bean:message key="link.dges.importation.configuration" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/>
	</html:link>

	<fr:form action="/dgesStudentImportationProcess.do?method=createNewImportationProcess">
		<fr:edit id="importation.bean" name="importationBean" visible="false" />
		
		<fr:edit id="importation.bean.edit" name="importationBean">
			<fr:schema bundle="ULISBOA_SPECIFICATIONS_RESOURCES" type="org.fenixedu.ulisboa.specifications.action.student.access.importation.DgesStudentImportationProcessDA$DgesBaseProcessBean">
				<fr:slot name="executionYear" layout="menu-select-postback" key="label.dges.importation.process.execution.year" required="true" >
					<fr:property name="providerClass" value="org.fenixedu.academic.ui.renderers.providers.ExecutionYearsProvider"/>
					<fr:property name="format" value="${name}" />
					<fr:property name="sortBy" value="name=desc"/>
					<fr:property name="destination" value="postback" /> 
					<fr:property name="nullOptionHidden" value="true" />
				</fr:slot>
			</fr:schema>
			
			<fr:destination name="postback" path="/dgesStudentImportationProcess.do?method=list" />
		</fr:edit>
	</fr:form>				

	
	<p class="mtop15 mbottom05"><strong><bean:message key="title.dges.importation.process.jobs.done" bundle="ULISBOA_SPECIFICATIONS_RESOURCES" /></strong></p>
	
	
	<logic:empty name="importationJobsDone">
		<em><bean:message key="message.dges.importation.process.jobs.done.empty" bundle="ULISBOA_SPECIFICATIONS_RESOURCES" /></em>
	</logic:empty>
		
	<logic:notEmpty name="importationJobsDone" >
		<fr:view name="importationJobsDone" >
			<fr:schema bundle="ULISBOA_SPECIFICATIONS_RESOURCES" type="org.fenixedu.ulisboa.specifications.domain.student.importation.DgesStudentImportationProcess">
				<fr:slot name="filename" key="label.dges.importation.process.filename" />
				<fr:slot name="requestDate" key="label.dges.importation.process.request.date" />
				<fr:slot name="jobStartTime" key="label.dges.importation.process.start.time" />
				<fr:slot name="jobEndTime" key="label.dges.importation.process.end.time" />
				<fr:slot name="dgesStudentImportationFile" key="label.dges.importation.process.importation.content" bundle="ULISBOA_SPECIFICATIONS_RESOURCES" layout="link" >
					<fr:property name="moduleRelative" value="false" />
				</fr:slot>
			</fr:schema>
			
			<fr:layout name="tabular">
		   		<fr:property name="classes" value="tstyle1 mtop05" />
		    	<fr:property name="columnClasses" value=",,,acenter,,,,,," />
			
				<fr:link label="label.dges.importation.process.view,ULISBOA_SPECIFICATIONS_RESOURCES" name="view" link="/downloadQueuedJob.do?method=downloadFile&id=${externalId}" module="" />
			</fr:layout>
		</fr:view>
	</logic:notEmpty>
	
	<p class="mtop15 mbottom05"><strong><bean:message key="title.dges.importation.process.jobs.undone" bundle="ULISBOA_SPECIFICATIONS_RESOURCES" /></strong></p>
	
	<logic:empty name="importationJobsPending">
		<em><bean:message key="message.dges.importation.process.jobs.undone.empty" bundle="ULISBOA_SPECIFICATIONS_RESOURCES" /></em>
	</logic:empty>
	
	<logic:notEmpty name="importationJobsPending">
		<fr:view name="importationJobsPending" >
			<fr:schema bundle="ULISBOA_SPECIFICATIONS_RESOURCES" type="org.fenixedu.ulisboa.specifications.domain.student.importation.DgesStudentImportationProcess">
				<fr:slot name="requestDate" key="label.dges.importation.process.request.date" />
				<fr:slot name="jobStartTime" key="label.dges.importation.process.start.time" />
				<fr:slot name="jobEndTime" key="label.dges.importation.process.end.time" />
				<fr:slot name="isNotDoneAndCancelled" key="label.dges.importation.process.cancelled" />
				<fr:slot name="dgesStudentImportationFile" key="label.dges.importation.process.importation.content" layout="link" >
					<fr:property name="moduleRelative" value="false" />
				</fr:slot>
			</fr:schema>
			
			<fr:layout name="tabular">
		   		<fr:property name="classes" value="tstyle1 mtop05" />
		    	<fr:property name="columnClasses" value=",,,acenter,,,,,," />
	
				<fr:link label="label.dges.importation.process.cancel,ULISBOA_SPECIFICATIONS_RESOURCES" name="cancel" link="/dgesStudentImportationProcess.do?method=cancelJob&queueJobId=${externalId}" condition="isNotDoneAndNotCancelled"/>
			</fr:layout>
		</fr:view>
	</logic:notEmpty>
	
	<logic:equal name="canRequestJobImportationProcess" value="true">
	<p>
		<html:link page="/dgesStudentImportationProcess.do?method=prepareCreateNewImportationProcess">
			<bean:message key="link.dges.importation.process.request" bundle="ULISBOA_SPECIFICATIONS_RESOURCES"/>
		</html:link>
	</p>
	</logic:equal>

</logic:present>

