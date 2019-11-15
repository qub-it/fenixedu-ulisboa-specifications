<%@ page isELIgnored="true"%>
<%@page import="org.fenixedu.commons.i18n.I18N" %>
<%@page import="org.fenixedu.academic.domain.student.RegistrationObservations"%>
<%@page import="org.fenixedu.academic.domain.student.Registration"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
    
    <fmt:setLocale value=""/>
    <fmt:setBundle basename="resources.FenixeduUlisboaSpecificationsResources" var="lang"/>
    <bean:define id="registration" name="registration" scope="request" type="org.fenixedu.academic.domain.student.Registration"/>
    <academic:allowed operation="MANAGE_REGISTRATIONS" permission="ACADEMIC_OFFICE_REGISTRATION_ACCESS">
        <h3 class="mbottom05 mtop25 separator2"><fmt:message key="label.student.observations" bundle="${lang}" /></h3>
        <logic:present name="registration" property="registrationObservations">
            <logic:notEmpty name="registration" property="registrationObservations">
                <table class="tstyle2 thright thlight thcenter table">
                    <thead>
                        <tr>
                            <th><fmt:message key="label.student.observations.by" bundle="${lang}" />:</th>
                            <th><fmt:message key="label.student.observations.in" bundle="${lang}" />:</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            for(RegistrationObservations registrationObservations : RegistrationObservations.getLastThreeSortedObservations(registration)){
                                request.setAttribute("registrationObservations", registrationObservations);
                        %>
                            <tr>
                                <td class="acenter">
                                    <bean:write name="registrationObservations" property="versioningUpdatedBy.username"/>
                                </td>
                                <td class="acenter">
                                    <%= registrationObservations.getVersioningUpdateDate().getDate().toString("dd/MM/yyyy HH:mm:ss") %>
                                </td>
                                <td class="acenter">
                                    <bean:write name="registrationObservations" property="asLimitedHtml" filter="false"/>
                                </td>
                            </tr>
                        <%  } %>
                    </tbody>
                </table>
            </logic:notEmpty>
            <logic:empty name="registration" property="registrationObservations">
                <div>
                    <fmt:message key="label.student.observations.empty" bundle="${lang}" />
                </div>
            </logic:empty>
        </logic:present>
        <logic:notPresent name="registration" property="registrationObservations">
            <div>
                <fmt:message key="label.student.observations.empty" bundle="${lang}" />
            </div>
        </logic:notPresent>
        
        
        <% String observationsURL = request.getContextPath() + "/registrations/" + ((Registration)request.getAttribute("registration")).getExternalId() + "/observations"; %>
        <div> 
            <br>
            <html:link href="<%=observationsURL%>">
                 <img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
                <fmt:message key="label.student.observations.manage" bundle="${lang}" />
            </html:link>
        </div>
    </academic:allowed>