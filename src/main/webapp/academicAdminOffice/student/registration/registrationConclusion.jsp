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
<%@page import="org.fenixedu.academic.domain.student.RegistrationServices"%>
<%@page import="org.fenixedu.academic.domain.student.curriculum.conclusion.RegistrationConclusionServices"%>
<%@page import="org.fenixedu.academic.domain.student.curriculum.Curriculum"%>
<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@page import="org.apache.struts.action.ActionMessages"%>
<%@page import="org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil"%>
<%@page import="org.fenixedu.commons.i18n.I18N" %>
<%@page import="org.fenixedu.academic.dto.student.RegistrationConclusionBean" %>
<%@page import="org.fenixedu.academic.domain.Grade" %>
<%@page import="org.fenixedu.academic.predicate.AccessControl"%>
<%@page import="org.fenixedu.academic.domain.student.gradingTable.DegreeGradingTable" %>
<%@page import="org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup" %>
<%@page import="org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType" %>
<%@page import="org.fenixedu.bennu.core.security.Authenticate" %>
<%@page import="org.fenixedu.bennu.core.i18n.BundleUtil" %>
<%@page import="org.fenixedu.academic.util.Bundle"%>
<%@page import="org.fenixedu.ulisboa.specifications.servlet.FenixeduUlisboaSpecificationsInitializer" %>

<html:xhtml/>

<fmt:setLocale value='<%= I18N.getLocale().getLanguage() %>'/>
<fmt:setBundle basename="<%= ULisboaSpecificationsUtil.BUNDLE %>" var="ULISBOA_SPECIFICATIONS_RESOURCES"/>

<bean:define id="registrationConclusionBean" name="registrationConclusionBean" type="org.fenixedu.academic.dto.student.RegistrationConclusionBean"/>

<script type="text/javascript">
    $(document).ready(function() {
        $("#revert").click(function(e) {
            if (confirm('<bean:message key="label.program.conclusion.confirm.revert" bundle="APPLICATION_RESOURCES"/>') === false) {
                e.preventDefault();
            }
        });
        
        var eecc = <%
            
            if (registrationConclusionBean != null
                    && registrationConclusionBean.getFinalGrade() != null
                    && registrationConclusionBean.getFinalGrade().getValue() != null
                    && registrationConclusionBean.getConclusionYear() != null) {
                
                DegreeGradingTable table = DegreeGradingTable.find(registrationConclusionBean.getConclusionYear(), registrationConclusionBean.getProgramConclusion(), registrationConclusionBean.getRegistration());
                if (table != null) {
                    out.println("'" + table.getEctsGrade(registrationConclusionBean.getFinalGrade().getValue()) + "'");
                } else if (AcademicAuthorizationGroup.get(AcademicOperationType.MANAGE_CONCLUSION).isMember(Authenticate.getUser())) {
                    String url = request.getContextPath();
                    url += "/ulisboaspecifications/ectsgradingtable/search/?executionYear=";
                    url += registrationConclusionBean.getConclusionYear().getExternalId();
                    
                    String label = BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                            "label.gradingTables.curriculumRenderer.generateDegreeTable");
                    
                    out.println("'<a href=\"" + url + "\">" + label + "</a>'");
                } else {
                    out.println("'-'");
                }
                
            } else {
                out.println("'-'");
            }
        
        
        %>;
        
        $("#conclusionInformation table tbody tr:nth-child(7)").after(
                '<tr>' +
                    '<th scope="row"><fmt:message key="label.gradingTables.curriculumRenderer.ectsGrade" bundle="${ULISBOA_SPECIFICATIONS_RESOURCES}" />:</th>' +
                    '<td>' + eecc + '</td>' +
                '</tr>');
    });
</script>

<h2><bean:message key="student.registrationConclusionProcess" bundle="ACADEMIC_OFFICE_RESOURCES"/></h2>
    
<p>
    <html:link page="/student.do?method=visualizeRegistration" paramId="registrationID" paramName="registrationConclusionBean" paramProperty="registration.externalId">
        <bean:message key="link.student.back" bundle="ACADEMIC_OFFICE_RESOURCES"/>
    </html:link>
</p>

<logic:equal name="registrationConclusionBean" property="hasAccessToRegistrationConclusionProcess" value="true">

    <logic:equal name="registrationConclusionBean" property="conclusionProcessed" value="true">
        <br/>
        <div class="error0"><strong><bean:message  key="message.conclusion.process.already.performed" bundle="ACADEMIC_OFFICE_RESOURCES"/></strong></div>
        <br/>
        <logic:present name="registrationConclusionBean" property="conclusionProcess">
            <logic:equal name="registrationConclusionBean" property="canRepeatConclusionProcess" value="true">
                <fr:form action="/registration.do?method=revertRegistrationConclusionLastVersion">
                    <fr:edit id="registrationConclusionBean" name="registrationConclusionBean" visible="false" />
                    <button class="btn btn-danger" id="revert" title="<bean:message key="label.program.conclusion.confirm.revert" bundle="APPLICATION_RESOURCES"/>">
                            <bean:message bundle="APPLICATION_RESOURCES" key="label.revert"/>
                    </button>
                </fr:form>
            </logic:equal>
        </logic:present>
    </logic:equal>
    
    <html:messages id="message" message="true" bundle="APPLICATION_RESOURCES" property="<%= ActionMessages.GLOBAL_MESSAGE %>" >
        <hr />
        <p>
            <span class="error0"><!-- Error messages go here --><bean:write name="message" /></span>
        </p>
    </html:messages>
    
    <html:messages id="message" message="true" bundle="ACADEMIC_OFFICE_RESOURCES" property="illegal.access">
        <p>
            <span class="error0"><!-- Error messages go here --><bean:write name="message" /></span>
        </p>
    </html:messages>


    <div style="float: right;">
        <bean:define id="personID" name="registrationConclusionBean" property="registration.student.person.username"/>
        <html:img align="middle" src="<%= request.getContextPath() + "/user/photo/" + personID.toString()%>" altKey="personPhoto" bundle="IMAGE_RESOURCES" styleClass="showphoto"/>
    </div>
    
    <p class="mvert2">
        <span class="showpersonid">
        <bean:message key="label.student" bundle="ACADEMIC_OFFICE_RESOURCES"/>: 
            <fr:view name="registrationConclusionBean" property="registration.student" schema="student.show.personAndStudentInformation.short">
                <fr:layout name="flow">
                    <fr:property name="labelExcluded" value="true"/>
                </fr:layout>
            </fr:view>
        </span>
    </p>
    
    <logic:present name="registrationConclusionBean" property="registration.ingressionType">
        <h3 class="mbottom05"><bean:message key="label.registrationDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
        <fr:view name="registrationConclusionBean" property="registration" schema="student.registrationDetail" >
            <fr:layout name="tabular">
                <fr:property name="classes" value="tstyle4 thright thlight mtop05"/>
                <fr:property name="rowClasses" value=",,tdhl1,,,,,,"/>
            </fr:layout>
        </fr:view>
    </logic:present>
    
    <logic:notPresent name="registrationConclusionBean" property="registration.ingressionType">
        <h3 class="mbottom05"><bean:message key="label.registrationDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
        <fr:view name="registrationConclusionBean" property="registration" schema="student.registrationsWithStartData" >
            <fr:layout name="tabular">
                <fr:property name="classes" value="tstyle4 thright thlight mtop05"/>
                <fr:property name="rowClasses" value=",,tdhl1,,,,,,"/>
            </fr:layout>
        </fr:view>
    </logic:notPresent>
    
    <%
        boolean hasProcessedProgramConclusionInOtherPlan = RegistrationConclusionServices.hasProcessedProgramConclusionInOtherPlan(registrationConclusionBean.getCurriculumGroup().getStudentCurricularPlan(), registrationConclusionBean.getProgramConclusion());
        boolean canProcessProgramConclusionInPreviousPlans = RegistrationConclusionServices.canProcessProgramConclusionInPreviousPlans(registrationConclusionBean.getCurriculumGroup().getStudentCurricularPlan(), registrationConclusionBean.getProgramConclusion());
    %>  
    
    <% if (hasProcessedProgramConclusionInOtherPlan) { %>
        <div class="alert alert-danger" role="alert"><strong><bean:message key="message.programConclusion.conclusion.type.is.already.processed.in.another.plan" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES"/></strong></div>
    <% } else { %>
    
        <%-- Credits in group not correct  --%>         
        <h3 class="mtop1 mbottom05"><bean:message key="label.summary" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
    
        <logic:iterate id="curriculumGroup" name="registrationConclusionBean" property="curriculumGroupsNotVerifyingStructure" type="org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup">
            <p>
                <span class="error0">
                <%=BundleUtil.getString(Bundle.APPLICATION, "label.curriculumGroupsNotVerifyingStructure", registrationConclusionBean.getConclusionYear() != null ? registrationConclusionBean.getConclusionYear().getQualifiedName() : ExecutionYear.readCurrentExecutionYear().getQualifiedName(), curriculumGroup.getFullPath(), curriculumGroup.getAprovedEctsCredits().toString(), curriculumGroup.getCreditsConcluded(registrationConclusionBean.getConclusionYear()).toString())%>
                </span>
            </p>
        </logic:iterate>
            
        <%-- Registration Not Concluded  --%>
        <!-- qubExtension, allow manual conclusion process for accumulated registrations -->
        <%
        if (
            RegistrationServices.isCurriculumAccumulated(registrationConclusionBean.getRegistration())
        ) {
        %>
            <p>
                <span class="warning0"><bean:message key="curriculumAccumulated.enabled" bundle="ACADEMIC_OFFICE_RESOURCES"/></span>
            </p>
        <%
        } else {
        %>
        <logic:equal name="registrationConclusionBean" property="concluded" value="false">
            <p>
                <span class="error0"><bean:message key="registration.not.concluded" bundle="ACADEMIC_OFFICE_RESOURCES"/></span>
            </p>
            <strong><bean:message  key="student.registrationConclusionProcess.data" bundle="ACADEMIC_OFFICE_RESOURCES" /></strong>
            <logic:equal name="registrationConclusionBean" property="byGroup" value="true" >
                <%-- Conclusion Process For Cycle  --%>
                <fr:view name="registrationConclusionBean" schema="RegistrationConclusionBean.viewForCycle">
                    <fr:layout name="tabular">
                        <fr:property name="classes" value="tstyle4 thright thlight mvert05"/>
                        <fr:property name="columnClasses" value=",,tderror1 tdclear"/>
                    </fr:layout>
                </fr:view>
            </logic:equal>
        </logic:equal>
        <%
        }
        %> 
        
        
        <%-- Registration Concluded  --%>
        <!-- qubExtension, allow manual conclusion process for accumulated registrations -->
        <%
        if (
            registrationConclusionBean.isConcluded() || RegistrationServices.isCurriculumAccumulated(registrationConclusionBean.getRegistration())
        ) {
        %>
            
            <%-- Conclusion Processed  --%>
            <logic:equal name="registrationConclusionBean" property="conclusionProcessed" value="true">
                <logic:equal name="registrationConclusionBean" property="byGroup" value="true" >
    
                    <%-- Conclusion Process For Cycle  --%>
                    <div style="float: left;" id="conclusionInformation">
                        <strong><bean:message  key="student.registrationConclusionProcess.data" bundle="ACADEMIC_OFFICE_RESOURCES" /></strong>
                        <fr:view name="registrationConclusionBean" schema="RegistrationConclusionBean.viewForCycleWithConclusionProcessedInformation">
                            <fr:layout name="tabular">
                                <fr:property name="classes" value="tstyle4 thright thlight mvert05"/>
                                <fr:property name="columnClasses" value=",,tderror1 tdclear"/>
                            </fr:layout>
                        </fr:view>
                    </div>
    
                    <div style="float: left; margin-left: 20px;">               
                        <logic:equal name="registrationConclusionBean" property="canRepeatConclusionProcess" value="true">      
                            
                            <strong><bean:message  key="student.new.registrationConclusionProcess.data" bundle="ACADEMIC_OFFICE_RESOURCES" /></strong>      
                            <fr:view name="registrationConclusionBean" schema="RegistrationConclusionBean.viewConclusionPreviewForCycle">
                                <fr:layout name="tabular">
                                    <fr:property name="classes" value="tstyle4 thright thlight mvert05"/>
                                    <fr:property name="columnClasses" value=",,tderror1 tdclear"/>
                                </fr:layout>
                            </fr:view>
                        </logic:equal>
                    </div>
    
                    <div style="clear: both;"></div>
                </logic:equal>
                
                <div style="clear: both;"></div>
    
            </logic:equal>
            
            <%-- Conclusion Not Processed  --%>
            <logic:equal name="registrationConclusionBean" property="conclusionProcessed" value="false">
                <logic:iterate id="curriculumModule" name="registrationConclusionBean" property="curriculumModulesWithNoConlusionDate">
                    <p>
                        <span class="error0"><bean:write name="curriculumModule" property="fullPath"/> não tem data de conclusão, assegure-se que está concluído e todas as datas de avaliação estão inseridas no sistema.</span>
                    </p>
                </logic:iterate>
                
                <logic:equal name="registrationConclusionBean" property="byGroup" value="true" >
                    <%-- Conclusion Process For Cycle  --%>
                    <strong><bean:message  key="student.registrationConclusionProcess.data" bundle="ACADEMIC_OFFICE_RESOURCES" /></strong>
                    <fr:view name="registrationConclusionBean" schema="RegistrationConclusionBean.viewConclusionPreviewForCycle">
                        <fr:layout name="tabular">
                            <fr:property name="classes" value="tstyle4 thright thlight mvert05"/>
                            <fr:property name="columnClasses" value=",,tderror1 tdclear"/>
                        </fr:layout>
                    </fr:view>
                </logic:equal>
            </logic:equal>
        
            <p class="mtop05">
                <bean:define id="registrationId" name="registrationConclusionBean" property="registration.externalId" />        
                <logic:notEmpty name="registrationConclusionBean" property="curriculumGroup">
                    <bean:define id="curriculumGroupId" name="registrationConclusionBean" property="curriculumGroup.externalId" />
                    <html:link action="<%="/registration.do?method=prepareRegistrationConclusionDocument&amp;registrationId=" + registrationId + "&amp;curriculumGroupId=" + curriculumGroupId %>" target="_blank">
                        Folha de <bean:message key="student.registrationConclusionProcess" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </logic:notEmpty>
            </p>
        <%
        }
        %>
    
        <%-- Form used to concluded process or to repeat --%>
        <!-- qubExtension, allow manual conclusion process for accumulated registrations -->
        <%
        // override of getCanBeConclusionProcessed() behaviour
        if (
            (!registrationConclusionBean.isConclusionProcessed() || registrationConclusionBean.getRegistration().canRepeatConclusionProcess(AccessControl.getPerson()))
            && (registrationConclusionBean.isConcluded() || RegistrationServices.isCurriculumAccumulated(registrationConclusionBean.getRegistration()))
        ) {
        %>
            <fr:form action="/registration.do?method=doRegistrationConclusion">
            
                <fr:edit id="registrationConclusionBean" name="registrationConclusionBean" visible="false" />
    
                <h3 class="mtop15 mbottom05"><bean:message  key="student.registrationConclusionProcess.data" bundle="ACADEMIC_OFFICE_RESOURCES" /></h3>
    
                <fr:edit id="registrationConclusionBean-manage" name="registrationConclusionBean">
                    <fr:schema bundle="APPLICATION_RESOURCES" type="org.fenixedu.academic.dto.student.RegistrationConclusionBean">
                        <fr:slot name="calculatedConclusionDate" readOnly="true">
                            <fr:property name="classes" value="bold" />
                        </fr:slot>
                        <fr:slot name="enteredConclusionDate" layout="input">
                            <fr:property name="format" value="dd-MM-yyyy"/>
                            <%-- // QubEdu extension
                            <fr:property name="bundle" value="APPLICATION_RESOURCES"/>
                            <fr:property name="comment" value="label.registrationConclusionProcess.enteredConclusionDate.comment"/>
                            <fr:property name="commentLocation" value="right" />
                             --%>
                        </fr:slot>
    
                        <!-- qubExtension, allow manual conclusion process for accumulated registrations -->
                        <%
                        // override of getCanBeConclusionProcessed() behaviour
                        if (
                            registrationConclusionBean.isConcluded() || RegistrationServices.isCurriculumAccumulated(registrationConclusionBean.getRegistration())
                        ) {
                        %>
                            <fr:slot name="enteredAverageGrade"/>
                            <fr:slot name="enteredFinalAverageGrade"/>
                            <fr:slot name="enteredDescriptiveGrade"/>
                        <%
                        }
                        %>
                        <fr:slot name="observations" key="label.anotation" bundle="ACADEMIC_OFFICE_RESOURCES" layout="longText">
                            <fr:property name="columns" value="25" />
                            <fr:property name="rows" value="5" />
                        </fr:slot>
                    </fr:schema>
                    <fr:layout name="tabular-editable">
                        <fr:property name="classes" value="tstyle4 thright thlight mvert05"/>
                        <fr:property name="columnClasses" value=",,tderror1 tdclear"/>
                    </fr:layout>
                </fr:edit>
    
                    <% if (canProcessProgramConclusionInPreviousPlans && !hasProcessedProgramConclusionInOtherPlan) { %>
                        <div class="alert alert-warning" role="alert"><strong><bean:message key="message.programConclusion.conclusion.type.should.be.processed.in.previous.plan" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES"/></strong></div>
                    <% } %>
                    <div style="float: left; margin-top: 1.5em">
                        <logic:equal name="registrationConclusionBean" property="conclusionProcessed" value="false">
                            <html:submit bundle="HTMLALT_RESOURCES" altKey="submit.submit">
                                <bean:message bundle="APPLICATION_RESOURCES" key="label.finish"/>
                            </html:submit>
                        </logic:equal>
                        
                        <%-- // QubEdu extension, Explicit button label of update --%>
                        <logic:equal name="registrationConclusionBean" property="conclusionProcessed" value="true">
                            <html:submit bundle="HTMLALT_RESOURCES" altKey="submit.submit">
                                <bean:message bundle="APPLICATION_RESOURCES" key="label.update"/>
                            </html:submit>
                        </logic:equal>
                    </div>
    
                    <%-- QubEdu extension --%>
                    <div style="float: left; margin-left: 2em; margin-top: 2em;"><span class="warning0"><bean:message key="label.registrationConclusionProcess.enteredConclusionDate.comment" bundle="APPLICATION_RESOURCES" /></span></div>
                    <div style="clear: both;"></div>
                    
            </fr:form>
        <%
        }
        %>
            
            <%-- // QubEdu extension, moved to end of page --%>
            <h3 class="mtop15 mbottom05"><bean:message key="registration.curriculum" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
    
                <p>
                    <fr:view name="registrationConclusionBean" property="curriculumForConclusion">
                        <fr:layout>
                            <fr:property name="visibleCurricularYearEntries" value="false" />
                        </fr:layout>
                    </fr:view>
                </p>
                
                <%-- Extension --%>
                <p>
                    <%
                    final RegistrationConclusionBean bean = (RegistrationConclusionBean) request.getAttribute("registrationConclusionBean");
                    final Curriculum curriculum = bean == null ? null : (Curriculum) bean.getCurriculumForConclusion();
                    request.setAttribute("curriculum", curriculum);
                    %>
                    <jsp:include page="<%=org.fenixedu.ulisboa.specifications.ULisboaConfiguration.getConfiguration().getCurriculumGradeCalculatorVisualizer()%>" /> 
                </p>   
        
        
        <% } %>
                 

</logic:equal>

<logic:equal name="registrationConclusionBean" property="hasAccessToRegistrationConclusionProcess" value="false">
    <p class="mtop15">
        <em class="error0"><bean:message key="error.not.authorized.to.registration.conclusion.process" bundle="ACADEMIC_OFFICE_RESOURCES"/></em>
    </p>
</logic:equal>
