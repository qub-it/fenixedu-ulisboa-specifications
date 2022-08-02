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
<%@page import="org.fenixedu.academic.service.AcademicPermissionService"%>
<%@page import="org.joda.time.LocalDate"%>
<%@page import="org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.student.ulisboaservicerequest.ULisboaServiceRequestController"%>
<%@page import="org.fenixedu.academic.predicate.AccessControl"%>
<%@page import="org.fenixedu.bennu.core.security.Authenticate"%>
<%@page import="org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType"%>
<%@page import="org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup"%>
<%@page import="org.fenixedu.ulisboa.specifications.util.ULisboaConstants"%>
<%@page import="org.fenixedu.academic.util.Bundle"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@page import="org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear"%>
<%@page import="org.fenixedu.commons.i18n.I18N" %>
<%@page import="org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest.ULisboaServiceRequestManagementController"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest"%>
<%@page import="java.util.stream.Collectors" %>
<%@page import="org.fenixedu.academic.domain.student.Registration" %>

<c:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<c:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js" />
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<c:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<c:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />
<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%-- ${portal.toolkit()} --%>

<link href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dropdown.multi.level.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<fmt:setLocale value='<%= I18N.getLocale().getLanguage() %>'/>
<fmt:setBundle basename="<%= ULisboaConstants.BUNDLE %>" var="lang"/>
<fmt:setBundle basename="<%= Bundle.ACADEMIC %>" var="ACADEMIC_OFFICE_RESOURCES"/>

    <bean:define id="registration" name="registration" scope="request" type="org.fenixedu.academic.domain.student.Registration"/>

<bean:define id="deliveryWarning">
<fmt:message key="academic.service.request.delivery.confirmation" bundle="${ACADEMIC_OFFICE_RESOURCES}" />
</bean:define>

    <h3 class="mtop25 mbottom05 separator2"><fmt:message key="academic.services" bundle="${ACADEMIC_OFFICE_RESOURCES}" /></h3>

    <academic:allowed operation="SERVICE_REQUESTS" permission="ACADEMIC_REQUISITIONS">                        
        <div class="well well-sm" style="display: inline-block">
            <span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span>
            &nbsp;
            <a class=""
                href="${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.HISTORY_ACADEMIC_REQUEST_URL %>${registration.externalId}">
                <fmt:message key="label.academicRequest.viewHistoryLog" bundle="${lang}" />
            </a>
            &nbsp;|&nbsp;
            <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
            &nbsp; 
            <a class=""
                href="${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.CREATE_URL %>${registration.externalId}">
                <fmt:message key="label.academicRequest.createServiceRequest" bundle="${lang}" />
            </a>
            &nbsp;       
        </div>
    </academic:allowed>

    <academic:notAllowed operation="SERVICE_REQUESTS" permission="ACADEMIC_REQUISITIONS">
    <div class="well well-sm" style="display: inline-block">
        <span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span>
        &nbsp;
        <a class=""
            href="${pageContext.request.contextPath}<%= ULisboaServiceRequestController.HISTORY_SERVICE_REQUEST_URL %>${registration.externalId}">
            <fmt:message key="label.academicRequest.viewHistoryLog" bundle="${lang}" />
        </a>
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
        &nbsp; 
        <a class=""
            href="${pageContext.request.contextPath}<%= ULisboaServiceRequestController.CREATE_SERVICE_REQUEST_URL %>${registration.externalId}">
            <fmt:message key="label.academicRequest.createServiceRequest" bundle="${lang}" />
        </a>
        &nbsp;
    </div>
    </academic:notAllowed>        

    <div id="content">
        <%
        request.setAttribute("newAcademicServiceRequests", ULisboaServiceRequest.findNewAcademicServiceRequests(registration).collect(Collectors.toList()));
        request.setAttribute("processingAcademicServiceRequests", ULisboaServiceRequest.findProcessingAcademicServiceRequests(registration).collect(Collectors.toList()));
        request.setAttribute("toDeliverAcademicServiceRequests",ULisboaServiceRequest.findToDeliverAcademicServiceRequests(registration).collect(Collectors.toList()));
        %>
        <ul id="tabs" class="nav nav-tabs" data-tabs="tabs">
            <li class="active"><a href="#newRequests" data-toggle="tab"><fmt:message key="label.academicRequest.newRequests" bundle="${lang}" /></a></li>
            <li><a href="#notConcludedRequests" data-toggle="tab"><fmt:message key="label.academicRequest.notConcludedRequests" bundle="${lang}" /></a></li>
            <li><a href="#notDeliveredRequests" data-toggle="tab"><fmt:message key="label.academicRequest.notDeliveredRequests" bundle="${lang}" /></a></li>
        </ul>
        <div id="tabContent" class="tab-content">
            <div class="tab-pane active" id="newRequests">
                <!--  New Requests -->
                <p></p>
                <c:choose>
                    <c:when test="${not empty newAcademicServiceRequests}">                        
                        <table id="newAcademicRequestsTable"
                            class="table responsive table-bordered table-hover" width="100%">
                            <thead>
                                <tr>
                                    <th><fmt:message key="label.academicRequest.requestDate" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.activeSituationDate" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.serviceRequestNumberYear" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.description" bundle="${lang}" /></th>
                                    <th><!-- Operations --></th>
                                </tr>                              
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning" role="alert">
                            <p>
                                <span
                                    class="glyphicon glyphicon-exclamation-sign"
                                    aria-hidden="true">&nbsp;</span>
                                <fmt:message key="label.noResultsFound" bundle="${lang}" />
                            </p>
    
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="tab-pane" id="notConcludedRequests">
                <!-- Not concluded Requests -->
                <p></p>
                <c:choose>
                    <c:when test="${not empty processingAcademicServiceRequests}">
                        <table id="processingAcademicServiceRequestsTable"
                            class="table responsive table-bordered table-hover" width="100%">
                            <thead>
                                <tr>
                                    <th><fmt:message key="label.academicRequest.requestDate" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.activeSituationDate" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.serviceRequestNumberYear" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.description" bundle="${lang}" /></th>
                                    <th><!-- Operations --></th>
                                </tr>                              
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning" role="alert">
                            <p>
                                <span
                                    class="glyphicon glyphicon-exclamation-sign"
                                    aria-hidden="true">&nbsp;</span>
                                <fmt:message key="label.noResultsFound" bundle="${lang}" />
                            </p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="tab-pane" id="notDeliveredRequests">
                <!-- Not delivered Requests -->
                <p></p>
                <c:choose>
                    <c:when test="${not empty toDeliverAcademicServiceRequests}">
                        <table id="toDeliverAcademicServiceRequestsTable"
                            class="table responsive table-bordered table-hover" width="100%">
                            <thead>
                                <tr>
                                    <th><fmt:message key="label.academicRequest.requestDate" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.activeSituationDate" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.serviceRequestNumberYear" bundle="${lang}" /></th>
                                    <th><fmt:message key="label.academicRequest.description" bundle="${lang}" /></th>
                                    <th><!-- Operations --></th>
                                </tr>                              
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning" role="alert">
                            <p>
                                <span
                                    class="glyphicon glyphicon-exclamation-sign"
                                    aria-hidden="true">&nbsp;</span>
                                <fmt:message key="label.noResultsFound" bundle="${lang}" />
                            </p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>    
    </div>
    
<script type="text/javascript">
    $(function(){
        $('a[href*="deliveredAcademicServiceRequest"]').each(function(index) {
            $(this).click(function() {  
                return confirm("<%= deliveryWarning %>");
            });
        });
    });

    var toDeliverAcademicServiceRequestsDataSet = [
      <c:forEach var="academicRequest" items="${ toDeliverAcademicServiceRequests }">
         <c:set var="requestDate" value="${academicRequest.requestDate.toString('yyyy-MM-dd')}" />
         <c:set var="activeSituationDate" value="${academicRequest.activeSituationDate.toString('yyyy-MM-dd')}" />
         {"requestDate" : "<c:out value='${ requestDate }' />",
         "activeSituationDate" : '<c:out value="${ activeSituationDate }" />',
         "serviceRequestNumberYear" : '<c:out value="${ academicRequest.serviceRequestNumberYear }" />',
         "description" : '<c:out value="${ academicRequest.description }" />',
         "actions" : 
        	 <% if(AcademicAuthorizationGroup.get(AcademicOperationType.SERVICE_REQUESTS, null, null, null).isMember(AccessControl.getPerson().getUser()) || AcademicPermissionService.hasAccess("ACADEMIC_REQUISITIONS", AccessControl.getPerson().getUser())) {%>
             " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.READ_ACADEMIC_REQUEST_URL %>${ academicRequest.externalId } \"><fmt:message key='label.view' bundle='${lang}' /></a>" +
             ""
             <%} else {%>
             " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestController.READ_SERVICE_REQUEST_URL %>${ academicRequest.externalId } \"><fmt:message key='label.view' bundle='${lang}' /></a>" +
             ""
             <%}%>
         },
     </c:forEach>                               
    ];
    
    var processingAcademicServiceRequestsDataSet = [
      <c:forEach var="academicRequest" items="${ processingAcademicServiceRequests }">
         <c:set var="requestDate" value="${academicRequest.requestDate.toString('yyyy-MM-dd')}" />
         <c:set var="activeSituationDate" value="${academicRequest.activeSituationDate.toString('yyyy-MM-dd')}" />
         {"requestDate" : "<c:out value='${ requestDate }' />",
         "activeSituationDate" : '<c:out value="${ activeSituationDate }" />',
         "serviceRequestNumberYear" : '<c:out value="${ academicRequest.serviceRequestNumberYear }" />',
         "description" : '<c:out value="${ academicRequest.description }" />',
         "actions" : 
             <% if(AcademicAuthorizationGroup.get(AcademicOperationType.SERVICE_REQUESTS, null, null, null).isMember(AccessControl.getPerson().getUser()) || AcademicPermissionService.hasAccess("ACADEMIC_REQUISITIONS", AccessControl.getPerson().getUser())) {%>
             " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.READ_ACADEMIC_REQUEST_URL %>${ academicRequest.externalId } \"><fmt:message key='label.view' bundle='${lang}' /></a>" +
             ""
             <%} else {%>
             " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestController.READ_SERVICE_REQUEST_URL %>${ academicRequest.externalId } \"><fmt:message key='label.view' bundle='${lang}' /></a>" +
             ""
             <%}%>
         },
     </c:forEach>                               
    ];    
    
    var newAcademicRequestsDataSet = [
        <c:forEach var="academicRequest" items="${ newAcademicServiceRequests }">
           <c:set var="requestDate" value="${academicRequest.requestDate.toString('yyyy-MM-dd')}" />
           <c:set var="activeSituationDate" value="${academicRequest.activeSituationDate.toString('yyyy-MM-dd')}" />
           {"requestDate" : "<c:out value='${ requestDate }' />",
           "activeSituationDate" : '<c:out value="${ activeSituationDate }" />',
           "serviceRequestNumberYear" : '<c:out value="${ academicRequest.serviceRequestNumberYear }" />',
           "description" : '<c:out value="${ academicRequest.description }" />',
           "actions" : 
               <% if(AcademicAuthorizationGroup.get(AcademicOperationType.SERVICE_REQUESTS, null, null, null).isMember(AccessControl.getPerson().getUser()) || AcademicPermissionService.hasAccess("ACADEMIC_REQUISITIONS", AccessControl.getPerson().getUser())) {%>
               " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.READ_ACADEMIC_REQUEST_URL %>${ academicRequest.externalId } \"><fmt:message key='label.view' bundle='${lang}' /></a>" +
               ""
               <%} else {%>
               " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestController.READ_SERVICE_REQUEST_URL %>${ academicRequest.externalId } \"><fmt:message key='label.view' bundle='${lang}' /></a>" +
               ""
               <%}%>
           },
       </c:forEach>                               
    ];
    
    $(document).ready(function() {
        var initTable = function (tableId, data) {
            var dataTable = $(tableId).DataTable({language : {
                url : "${datatablesI18NUrl}", 
                responsive: true
            },
            "columns": [
                { data: 'requestDate' },
                { data: 'activeSituationDate' },
                { data: 'serviceRequestNumberYear' },
                { data: 'description' },
                { data: 'actions',className:"all" }
            ],
            "columnDefs": [
                           { "width": "54px", "targets": 4 } 
                         ],
            "data" : data,
            "dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
            "tableTools": {
                "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"            
            }
            });
            dataTable.columns.adjust().draw();
            
            $(tableId + ' tbody').on( 'click', 'tr', function () {
                $(this).toggleClass('selected');
            } );
        }
        initTable('#newAcademicRequestsTable', newAcademicRequestsDataSet);
        initTable('#processingAcademicServiceRequestsTable', processingAcademicServiceRequestsDataSet);
        initTable('#toDeliverAcademicServiceRequestsTable', toDeliverAcademicServiceRequestsDataSet);
    }); 
</script>
    