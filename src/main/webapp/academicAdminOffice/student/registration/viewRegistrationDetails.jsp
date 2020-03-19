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
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.stream.Stream"%>
<%@page import="org.fenixedu.academic.domain.StudentCurricularPlan"%>
<%@ page isELIgnored="true"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@page import="org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear"%>
<%@page import="org.fenixedu.academic.dto.student.RegistrationDataBean"%>
<%@page import="org.fenixedu.commons.i18n.I18N" %>
<%@page import="org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest.ULisboaServiceRequestManagementController"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest"%>
<%@page import="java.util.stream.Collectors" %>
<%@page import="org.fenixedu.academic.domain.student.Registration" %>
<%@page import="java.util.List"%>
<%@page import="com.google.common.collect.Lists"%>


<html:xhtml/>

    <bean:define id="registration" name="registration" scope="request" type="org.fenixedu.academic.domain.student.Registration"/>

    <div style="float: right;">
        <bean:define id="personID" name="registration" property="student.person.username"/>
        <html:img align="middle" src="<%= request.getContextPath() + "/user/photo/" + personID.toString()%>" altKey="personPhoto" bundle="IMAGE_RESOURCES" styleClass="showphoto"/>
    </div>

    <h2><bean:message key="label.visualizeRegistration" bundle="ACADEMIC_OFFICE_RESOURCES"/></h2>
    

    <p>
        <html:link page="/student.do?method=visualizeStudent" paramId="studentID" paramName="registration" paramProperty="student.externalId">
            <bean:message key="link.student.backToStudentDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/>
        </html:link>
    </p>

    

    
    <p class="mvert2">
        <span class="showpersonid">
        <bean:message key="label.student" bundle="ACADEMIC_OFFICE_RESOURCES"/>: 
            <fr:view name="registration" property="student" schema="student.show.personAndStudentInformation.short">
                <fr:layout name="flow">
                    <fr:property name="labelExcluded" value="true"/>
                </fr:layout>
            </fr:view>
        </span>
    </p>
    

    <logic:messagesPresent message="true" property="success">
        <div class="success0" style="padding: 0.5em;">
            <html:messages id="messages" message="true" bundle="ACADEMIC_OFFICE_RESOURCES" property="success">
                <span><bean:write name="messages" /></span>
            </html:messages>
        </div>
    </logic:messagesPresent>
    
    <logic:messagesPresent message="true" property="warning" >
        <div class="warning0" style="padding: 0.5em;">
            <html:messages id="messages" message="true" bundle="ACADEMIC_OFFICE_RESOURCES" property="warning">
                <span><bean:write name="messages" /></span>
            </html:messages>
        </div>
    </logic:messagesPresent>
    
    <logic:messagesPresent message="true" property="error">
        <div class="error0" style="padding: 0.5em;">
            <html:messages id="messages" message="true" bundle="ACADEMIC_OFFICE_RESOURCES" property="error">
                <span><bean:write name="messages" /></span>
            </html:messages>
        </div>
    </logic:messagesPresent>


    <logic:present name="registration" property="ingressionType">
        <h3 class="mtop2 mbottom05 separator2"><bean:message key="label.registrationDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
    </logic:present>
    
    <logic:notPresent name="registration" property="ingressionType">
        <h3 class="mtop2 mbottom05 separator2"><bean:message key="label.registrationDetails" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
    </logic:notPresent>
    <bean:define id="registration" name="registration" type="org.fenixedu.academic.domain.student.Registration"/>



<table>
    <tr>
        <td>
        
            <%-- Registration Details --%>
            <logic:present name="registration" property="ingressionType">
            <fr:view name="registration" schema="student.registrationDetail" >
                <fr:layout name="tabular">
                    <fr:property name="classes" value="tstyle4 thright thlight"/>
                    <fr:property name="rowClasses" value=",,,,,,,,"/>
                </fr:layout>
            </fr:view>
            </logic:present>
            <logic:notPresent name="registration" property="ingressionType">
            <fr:view name="registration" schema="student.registrationsWithStartData" >
                <fr:layout name="tabular">
                    <fr:property name="classes" value="tstyle4 thright thlight mtop0"/>
                    <fr:property name="rowClasses" value=",,,,,,,"/>
                </fr:layout>
            </fr:view>
            </logic:notPresent>
        
        </td>
        
        <td style="vertical-align: top; padding-top: 1em;">
            <academic:allowed operation="MANAGE_REGISTRATIONS" permission="ACADEMIC_OFFICE_REGISTRATION_ACCESS" program="<%= registration.getDegree() %>">
            <%-- qubExtension --%>
            <academic:allowed operation="STUDENT_ENROLMENTS" permission="ACADEMIC_OFFICE_ENROLMENTS" program="<%= registration.getDegree() %>">
            <p class="mtop0 pleft1 asd">
                <span class="dblock pbottom03">
                    <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
                    <html:link page="/registration.do?method=prepareViewRegistrationCurriculum" paramId="registrationID" paramName="registration" paramProperty="externalId">
                        <bean:message key="link.registration.viewCurriculum" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </span>
                <span class="dblock pbottom03">
                    <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
                    <html:link page="/manageRegistrationState.do?method=prepare" paramId="registrationId" paramName="registration" paramProperty="externalId">
                        <bean:message key="link.student.manageRegistrationState" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </span>
                <span class="dblock pbottom03">
                        <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
                        <html:link page="/manageIngression.do?method=prepare" paramId="registrationId" paramName="registration" paramProperty="externalId">
                            <bean:message key="link.student.manageIngressionAndAgreement" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                        </html:link>
                </span>
                <span class="dblock pbottom03">
                    <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
                    <html:link page="/manageRegistrationStartDates.do?method=prepare" paramId="registrationId" paramName="registration" paramProperty="externalId">
                        <bean:message key="link.student.manageRegistrationStartDates" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </span>     
                <academic:allowed operation="MANAGE_CONCLUSION" permission="ACADEMIC_OFFICE_CONCLUSION" program="<%= registration.getDegree() %>">
                    <%-- qubExtensions, removed conditional access to conclusion process page
                    <logic:equal name="registration" property="qualifiedToRegistrationConclusionProcess" value="true">
                     --%>
                        <span class="dblock pbottom03"> 
                            <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
                            <html:link page="/registration.do?method=prepareRegistrationConclusionProcess" paramId="registrationId" paramName="registration" paramProperty="externalId">
                                <bean:message key="student.registrationConclusionProcess" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                            </html:link>
                        </span>
                    <%--
                    </logic:equal>
                     --%>   
                </academic:allowed>
                <span class="dblock pbottom03"> 
                    <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
                    <html:link page="/registration.do?method=showRegimes" paramId="registrationId" paramName="registration" paramProperty="externalId">
                        <bean:message key="student.regimes" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </span>
<%-- qubExtension --%>
<%--                <academic:allowed operation="STUDENT_ENROLMENTS" program="<%= registration.getDegree() %>"> --%>
<!--                <span class="dblock pbottom03">  -->
<%--                    <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" /> --%>
<%--                    <html:link page="/registration.do?method=viewAttends" paramId="registrationId" paramName="registration" paramProperty="externalId"> --%>
<%--                        <bean:message key="student.registrationViewAttends" bundle="ACADEMIC_OFFICE_RESOURCES"/> --%>
<%--                    </html:link> --%>
<!--                </span> -->
<%--                </academic:allowed>      --%>
                
                <%-- Extension --%>
                <jsp:include page="viewRegistrationDetailsTreasuryDebtAccountLink_ulisboa_specification.jsp" /> 
            </p>
            </academic:allowed>
            </academic:allowed>
        </td>
    </tr>
</table>
    
    <!-- qubExtension -->
        <jsp:include page="registrationObservations.jsp"></jsp:include>
    <!-- /qubExtension -->
    
    <%-- Registration Data by Execution Year --%>
    <h3 class="mbottom05 mtop25 separator2"><bean:message key="title.registrationDataByExecutionYear" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
    <logic:empty name="registration" property="registrationDataByExecutionYear">
        <bean:message key="label.registrationDataByExecutionYear.noResults" bundle="ACADEMIC_OFFICE_RESOURCES"/>
    </logic:empty>
    <logic:notEmpty name="registration" property="registrationDataByExecutionYear">
        <%-- qubExtension --%>
        <%
        final List<RegistrationDataBean> datas = Lists.newArrayList();
        for (final RegistrationDataByExecutionYear data : registration.getRegistrationDataByExecutionYearSet()) {
            datas.add(new RegistrationDataBean(data));
        }
        pageContext.setAttribute("datas", datas);
        %>
        <fr:view name="datas">
            <fr:schema bundle="ACADEMIC_OFFICE_RESOURCES" type="<%= RegistrationDataBean.class.getName() %>">
                <fr:slot name="executionYear.qualifiedName" key="label.executionYear" />
                <fr:slot name="notApproved" key="label.notApproved"/>
                <fr:slot name="reingression" key="reingression"/>
                <%-- 
                />
                --%> 
                <fr:slot name="curricularYearPresentation" key="label.curricularYear"
                layout="output-with-hover">
                    <fr:property name="format" value="${curricularYearJustificationPresentation}" />
                    <fr:property name="useParent" value="true" />
                </fr:slot>
                <fr:slot name="schoolClassPresentation" key="label.schoolClass"/>
                <fr:slot name="enrolmentsCount" key="label.enrolmentsCount" layout="link">
                    <fr:property name="useParent" value="true" />
                    <fr:property name="linkFormat"  value="/studentEnrolmentsExtended.do?method=prepare&amp;scpID=${studentCurricularPlan.externalId}&amp;executionSemesterID=${executionYear.firstExecutionPeriod.externalId}"/>
                    <fr:property name="moduleRelative" value="true" />
                    <fr:property name="contextRelative" value="true" />
                </fr:slot>
                <fr:slot name="creditsConcluded" key="approved" bundle="BOLONHA_MANAGER_RESOURCES" layout="link">
                    <fr:property name="format" value="${creditsConcluded} Cred." />
                    <fr:property name="useParent" value="true" />
                    <fr:property name="linkFormat"  value="/viewStudentCurriculum.do?method=prepare&amp;registrationOID=${registration.externalId}"/>
                    <fr:property name="moduleRelative" value="true" />
                    <fr:property name="contextRelative" value="true" />
                </fr:slot>
                <fr:slot name="enroledEcts" key="enrolled" layout="link">
                    <fr:property name="format" value="${enroledEcts} Cred." />
                    <fr:property name="useParent" value="true" />
                    <fr:property name="linkFormat"  value="/studentEnrolmentsExtended.do?method=prepare&amp;scpID=${studentCurricularPlan.externalId}&amp;executionSemesterID=${executionYear.firstExecutionPeriod.externalId}"/>
                    <fr:property name="moduleRelative" value="true" />
                    <fr:property name="contextRelative" value="true" />
                </fr:slot>
                <fr:slot name="regimePresentation" key="label.regimeType" layout="link">
                    <fr:property name="useParent" value="true" />
                    <fr:property name="linkFormat"  value="/registration.do?method=showRegimes&amp;registrationId=${registration.externalId}"/>
                    <fr:property name="moduleRelative" value="true" />
                    <fr:property name="contextRelative" value="true" />
                </fr:slot>
                <fr:slot name="lastRegistrationStatePresentation" key="label.lastRegistrationState" layout="link">
                    <fr:property name="useParent" value="true" />
                    <fr:property name="linkFormat"  value="/manageRegistrationState.do?method=prepare&amp;registrationId=${registration.externalId}"/>
                    <fr:property name="moduleRelative" value="true" />
                    <fr:property name="contextRelative" value="true" />
                </fr:slot>
                <fr:slot name="enrolmentDate" key="label.enrolmentDate" />
                <fr:slot name="lastAcademicActDate" key="label.lastAcademicAct.date"/>
            </fr:schema>
            <fr:layout name="tabular">
                <fr:property name="classes" value="tstyle2 thright thlight thcenter"/>
                <fr:property name="columnClasses" value="acenter,acenter,acenter" />
                <fr:property name="groupLinks" value="false"/>
                <fr:property name="sortBy" value="executionYear=desc" />
    
                <%-- qubExtension --%>              
                <academic:allowed operation="STUDENT_ENROLMENTS" permission="ACADEMIC_OFFICE_ENROLMENTS" program="<%= registration.getDegree() %>">
                    <%-- qubExtension --%>
                    <fr:link name="shiftEnrolment" label="label.shifts,APPLICATION_RESOURCES" 
                                 link="/shiftEnrolment/${registration.externalId}/${executionYear.firstExecutionPeriod.externalId}" order="1" />
                    
                    <fr:link name="edit" label="label.edit,ACADEMIC_OFFICE_RESOURCES" 
                                 link="/manageRegistrationData.do?method=prepareEdit&registrationDataByExecutionYearId=${externalId}" order="2" />

                    <fr:property name="linkFormat(delete)" value="<%="/manageRegistrationData.do?method=delete&amp;registrationDataByExecutionYearId=${externalId}"%>"/>
                    <fr:property name="key(delete)" value="label.delete"/>
                    <fr:property name="bundle(delete)" value="ACADEMIC_OFFICE_RESOURCES"/>
                    <fr:property name="confirmationKey(delete)" value="info.RegistrationDataByExecutionYear.confirmDelete"/>
                    <fr:property name="confirmationBundle(delete)" value="ACADEMIC_OFFICE_RESOURCES"/>
                    <fr:property name="order(delete)" value="3"/>
                </academic:allowed>                          
    
            </fr:layout>
        </fr:view>
    </logic:notEmpty>
    
    
    <%-- Curricular Plans --%>
    <academic:allowed operation="MANAGE_REGISTRATIONS" permission="ACADEMIC_OFFICE_REGISTRATION_ACCESS" program="<%= registration.getDegree() %>">
    <h3 class="mbottom05 mtop25 separator2"><bean:message key="label.studentCurricularPlans" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
    <%-- qubExtension --%>
    <div onmouseout="document.getElementById('curriculumAccumulatedInfo').className='tooltip tooltipClosed';" id="curriculumAccumulatedInfo" class="tooltip tooltipClosed" onmouseover="document.getElementById('curriculumAccumulatedInfo').className='tooltip tooltipOpen';">
          <span style="font-weight: bold;"><%= registration.getDegreeNameWithDescription() + " (" + registration.getDegree().getCode() + ")" %></span>
          <div class="tooltipText">
            <% if(RegistrationServices.isCurriculumAccumulated(registration)) { %>
                <bean:message key="curriculumAccumulated.enabled" bundle="ACADEMIC_OFFICE_RESOURCES"/>
            <% } else { %>
                <bean:message key="curriculumAccumulated.disabled" bundle="ACADEMIC_OFFICE_RESOURCES"/>
            <% } %>
          </div>
          <script type="text/javascript">document.getElementById('curriculumAccumulatedInfo').className='tooltip tooltipClosed';</script>
    </div>
    
    <% 
        pageContext.setAttribute("sortedStudentCurricularPlans", registration.getSortedStudentCurricularPlans().stream().sorted(StudentCurricularPlan.STUDENT_CURRICULAR_PLAN_COMPARATOR_BY_START_DATE.reversed()).collect(Collectors.toList())); 
    %>
    
    <%-- qubExtension --%>
    <table class="tstyle2 thright thlight thcenter table">
      <tr>
        <th><bean:message key="label.executionYear" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
        <th><bean:message key="label.startDate" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
        <th><bean:message key="label.curricularPlan" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
        <th><bean:message key="label.RegistrationHistoryReport.primaryBranch" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
        <th><bean:message key="label.RegistrationHistoryReport.secondaryBranch" bundle="ACADEMIC_OFFICE_RESOURCES"/></th>
        <th><bean:message key="label.programConclusions" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES"/></th>
        <th> </th>
        <th> </th>
        <th> </th>
        <th> </th>
        <th> </th>
      </tr>
      <logic:iterate id="studentCurricularPlan" name="sortedStudentCurricularPlans">
            <% 
                int programConclusionCount = RegistrationConclusionServices.getProgramConclusionProcesses((StudentCurricularPlan) studentCurricularPlan).size();
            %>
          <tr>
            <td class="acenter"><fr:view name="studentCurricularPlan" property="startExecutionYear.qualifiedName" /></td>
            <td class="acenter"><fr:view name="studentCurricularPlan" property="startDateYearMonthDay" /></td>
            <td><fr:view name="studentCurricularPlan" property="degreeCurricularPlan.name" /></td>
            <td class="acenter"><fr:view name="studentCurricularPlan" property="majorBranchNames" /></td>
            <td class="acenter"><fr:view name="studentCurricularPlan" property="minorBranchNames" /></td>
            <td class="acenter"><span class="<%= programConclusionCount > 0 ? "badge bold" : "" %>"><%= programConclusionCount > 0 ? programConclusionCount : "-" %></span></td>
            
            <%-- Actions --%>
            <td class="acenter">
                <logic:equal name="studentCurricularPlan" property="allowedToManageEnrolments" value="true">
                    <html:link action="/studentEnrolmentsExtended.do?method=prepare" paramId="scpID" paramName="studentCurricularPlan" paramProperty="externalId">
                        <bean:message key="label.student.enrolments" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </logic:equal>
            </td>
                        
            <td class="acenter">
                <academic:allowed operation="MANAGE_EQUIVALENCES" permission="ACADEMIC_OFFICE_CREDITS_TRANSFER">
                    <html:link action="/studentDismissals.do?method=manage" paramId="scpID" paramName="studentCurricularPlan" paramProperty="externalId">
                        <bean:message key="label.dismissals" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </academic:allowed>
            </td>
                        
            <td class="acenter">
                <html:link action="/programConclusionsSummary.do?method=prepare" paramId="scpID" paramName="studentCurricularPlan" paramProperty="externalId">
                    <bean:message key="label.programConclusions" bundle="FENIXEDU_ULISBOA_SPECIFICATIONS_RESOURCES"/>
                </html:link>
            </td>
                        
            <td class="acenter">
                <logic:equal name="studentCurricularPlan" property="allowedToManageEnrolments" value="true">
                    <html:link action="/manageStudentCurricularPlans.do?method=prepareEdit" paramId="studentCurricularPlanId" paramName="studentCurricularPlan" paramProperty="externalId">
                        <bean:message key="label.edit" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </logic:equal>
            </td>
            
                    
            
            <td class="acenter">
                <logic:equal name="studentCurricularPlan" property="allowedToDelete" value="true">
                    <bean:define id="confirmDelete" type="java.lang.String">return confirm('<bean:message key="message.manageStudentCurricularPlans.delete.confirmation" bundle="ACADEMIC_OFFICE_RESOURCES"/>')</bean:define>
                    <html:link action="/manageStudentCurricularPlans.do?method=delete" paramId="studentCurricularPlanId" paramName="studentCurricularPlan" paramProperty="externalId" onclick="<%= confirmDelete %>">
                        <bean:message key="label.delete" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </logic:equal>
            </td>
                        
          </tr>
      </logic:iterate>
    </table>

    
    <p class="mtop0">

        <span>
            <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
            <html:link page="/viewStudentCurriculum.do?method=prepare" paramId="registrationOID" paramName="registration" paramProperty="externalId">
                <bean:message key="link.registration.viewStudentCurricularPlans" bundle="ACADEMIC_OFFICE_RESOURCES"/>
            </html:link>
        </span>
        
        <%-- qubExtension --%>
        <academic:allowed operation="STUDENT_ENROLMENTS" permission="ACADEMIC_OFFICE_ENROLMENTS" program="<%= registration.getDegree() %>">
        <span class="pleft1">
            <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
            <html:link page="/manageStudentCurricularPlans.do?method=prepareCreate" paramId="registrationId" paramName="registration" paramProperty="externalId">
                <bean:message key="link.manageStudentCurricularPlans.create" bundle="ACADEMIC_OFFICE_RESOURCES"/>
            </html:link>
        </span>
        </academic:allowed>
        
        <%-- qubExtension --%>
        <academic:allowed operation="STUDENT_ENROLMENTS" permission="ACADEMIC_OFFICE_ENROLMENTS" program="<%= registration.getDegree() %>">
        <span class="pleft1">
            <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
            <html:link page="/studentExternalEnrolments.do?method=manageExternalEnrolments" paramId="registrationId" paramName="registration" paramProperty="externalId">
                <bean:message key="label.student.externalEnrolments" bundle="ACADEMIC_OFFICE_RESOURCES"/>
            </html:link>
        </span>
        </academic:allowed>

    </p>
    
    </academic:allowed>
    
    <academic:notAllowed operation="MANAGE_REGISTRATIONS" permission="ACADEMIC_OFFICE_REGISTRATION_ACCESS" program="<%= registration.getDegree() %>">
        <academic:allowed operation="VIEW_FULL_STUDENT_CURRICULUM" permission="ACADEMIC_OFFICE_REGISTRATION_ACCESS">
            <h3 class="mbottom05 mtop25 separator2"><bean:message key="label.studentCurricularPlans" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
            
            <fr:view name="registration" property="sortedStudentCurricularPlans" schema="student.studentCurricularPlans" >
                <fr:layout name="tabular">
                    <fr:property name="classes" value="tstyle2 thright thlight thcenter"/>
                    <fr:property name="groupLinks" value="false"/>
                    
                    <fr:property name="linkFormat(enrol)" value="/studentEnrolmentsExtended.do?method=prepare&amp;scpID=${externalId}" />
                    <fr:property name="key(enrol)" value="link.student.enrolInCourses"/>
                    <fr:property name="bundle(enrol)" value="ACADEMIC_OFFICE_RESOURCES"/>
                    <fr:property name="contextRelative(enrol)" value="true"/>      
                    <fr:property name="visibleIf(enrol)" value="allowedToManageEnrolments" />
                    <fr:property name="order(enrol)" value="1"/>
                    
                    <%-- extension: Deprecated payment system 
                    <fr:property name="linkFormat(createAccountingEvents)" value="/accountingEventsManagement.do?method=prepare&amp;scpID=${externalId}" />
                    <fr:property name="key(createAccountingEvents)" value="label.accountingEvents.management.createEvents"/>
                    <fr:property name="bundle(createAccountingEvents)" value="ACADEMIC_OFFICE_RESOURCES"/>
                    <fr:property name="contextRelative(createAccountingEvents)" value="true"/>          
                    <fr:property name="order(createAccountingEvents)" value="3"/>
                    <fr:property name="visibleIf(createAccountingEvents)" value="allowedToManageAccountingEvents"/>
                    --%>
                </fr:layout>
            </fr:view>
            <p class="mtop0">
                <span>
                    <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
                    <html:link page="/viewStudentCurriculum.do?method=prepare" paramId="registrationOID" paramName="registration" paramProperty="externalId">
                        <bean:message key="link.registration.viewStudentCurricularPlans" bundle="ACADEMIC_OFFICE_RESOURCES"/>
                    </html:link>
                </span>
            </p>
        </academic:allowed>
    </academic:notAllowed>

    <jsp:include page="/WEB-INF/fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/uLisboaServiceRequestSection.jsp" />

    <%-- Precedence Info --%>
    
    <logic:present name="registration" property="studentCandidacy">
        <h3 class="mtop2 mbottom05 separator2"><bean:message key="label.person.title.precedenceDegreeInfo" bundle="ACADEMIC_OFFICE_RESOURCES"/></h3>
        <fr:view name="registration" property="studentCandidacy.completedDegreeInformation" schema="student.precedentDegreeInformation" >
            <fr:layout name="tabular">
                <fr:property name="classes" value="tstyle4 thright thlight mtop05"/>
            </fr:layout>
        </fr:view>
    </logic:present>
<bean:define id="deliveryWarning">
<bean:message bundle="ACADEMIC_OFFICE_RESOURCES"  key="academic.service.request.delivery.confirmation"/>
</bean:define>

<%-- TODO legidio, move this to a generic place --%>
<style type="text/css">
/*---------------------------------------
       Tooltip
---------------------------------------*/

div.tooltip {
display: inline;
position: relative;
opacity: initial;
}
div.tooltip img {
padding-top: 5px;
}
div.tooltipClosed div.tooltipText {
display: none;
}
div.tooltipOpen div.tooltipText {
<%--display: inline;--%>
}
div.tooltipOpen div.tooltipText p {
display: block !important;
margin: 0 !important;
}
div.tooltip div.tooltipText {
position: absolute;
z-index: 10;
left: 25px;
width: 325px;
padding: 5px 10px;
text-align: left;
background: #e3f0f6;
border: 2px solid #c8e3ec;
white-space: normal !important;
}
div.tooltip div.tooltipText a {
font-weight: normal;
}
div.tooltip div.tooltipText ul {
margin: 0;
padding: 0;
list-style: none;
text-align: left;
}
div.tooltip div.tooltipText ul li {
margin: 0;
padding: 0;
}
div.tooltip span { border-bottom: 1px dotted #888; cursor: default; }
div.tooltip div.tooltipText span { border-bottom: none; }
</style>
<script type="text/javascript">
$(".tooltipText").each(function( index ) {
    $( this ).html($( this ).html().replace("\"[", "<br/>\"[").replace("; [", ";<br/>["));
  });
</script>
