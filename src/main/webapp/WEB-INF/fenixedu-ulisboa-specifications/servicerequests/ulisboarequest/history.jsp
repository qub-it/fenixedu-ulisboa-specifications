<!--
 /**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoe@qub-it.com 
 *
 * 
 * This file is part of FenixEdu Specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
 -->
<%@page import="org.fenixedu.ulisboa.specifications.ui.student.ulisboaservicerequest.ULisboaServiceRequestController"%>
<%@page import="org.fenixedu.academic.predicate.AccessControl"%>
<%@page import="org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType"%>
<%@page import="org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest.ULisboaServiceRequestManagementController"%>
<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter"%>
<%@page import="org.fenixedu.academic.domain.student.Registration" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>

<spring:url var="datatablesUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
    value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl"
    value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%-- ${portal.angularToolkit()} --%>
${portal.toolkit()}

<link
    href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
<link
    href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link
    href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript"
    src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>


<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.academicRequest.viewHistoryLog" />
        <small>
        </small>
    </h1>
</div>
<p>
    <strong>
        <c:out value="${registration.student.name} (${registration.student.number }) - ${registration.student.person.idDocumentType.localizedName} (${registration.student.person.documentIdNumber})" />
    </strong>
</p>
<% 
  Registration registration = (Registration) request.getAttribute("registration"); 
  String url = "/academicAdministration/student.do?method=visualizeRegistration&registrationID="+ registration.getExternalId();
%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp;
    <academic:allowed operation="SERVICE_REQUESTS">                      
        <a class=""
            href="${pageContext.request.contextPath}<%= GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, session) %>">
            <spring:message code="label.event.back" />
        </a>
    </academic:allowed>  
    <academic:notAllowed operation="SERVICE_REQUESTS">
        <a class=""
            href="${pageContext.request.contextPath}<%= ULisboaServiceRequestController.READ_REGISTRATION_URL %>${ registration.externalId }">
            <spring:message code="label.event.back" />
        </a>    
    </academic:notAllowed>
</div>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p> <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>
    </div>
</c:if>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.Registration.registrationDetails" />
        </h3>
    </div>
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.Registration.startDate" /></th>
                        <td><joda:format value='${registration.startDate}' style='S-' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.Registration.number" /></th>
                        <td><c:out value='${registration.number}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.Registration.degree" /></th>
                        <td><c:out value='${registration.degreeNameWithDescription}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.Registration.currentState" /></th>
                        <c:set var="situationLabel" value="${ registration.activeStateType.description }" />
                        <td><spring:message code="${ situationLabel }" /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.Registration.numberEnroledCurricularCoursesInCurrentYear" /></th>
                        <td><c:out value='${registration.numberEnroledCurricularCoursesInCurrentYear}' /></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>

<c:choose>
    <c:when test="${not empty uLisboaServiceRequestList}">
        <table id="historicAcademicServiceRequestsTable"
            class="table responsive table-bordered table-hover" width="100%">
            <thead>
                <tr>
                    <%--!!!  Field names here --%>
                    <th><spring:message code="label.academicRequest.requestDate" /></th>
                    <th><spring:message code="label.academicRequest.activeSituationDate" /></th>
                    <th><spring:message code="label.academicRequest.serviceRequestNumberYear" /></th>
                    <th><spring:message code="label.academicRequest.description" /></th>
                    <th><spring:message code="label.academicRequest.currentState" /></th>
                    <%-- Operations Column --%>
                    <th></th>
                </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="alert alert-warning" role="alert">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span>
                <spring:message code="label.noResultsFound" />
            </p>
        </div>
    </c:otherwise>
</c:choose>

<script>
    var uLisboaServiceRequestsDataSet = [
        <c:forEach items="${uLisboaServiceRequestList}" var="request">
        {
            "requestDate" : '<c:out value='${request.requestDate.toString("YYYY-MM-dd")}'/>',
            "activeSituationDate" : '<c:out value='${request.activeSituationDate.toString("YYYY-MM-dd")}'/>',
            "serviceRequestNumberYear" : '<c:out value='${request.serviceRequestNumberYear}'/>',
            "description" : '<c:out value='${request.description}'/>',
            "academicServiceRequestSituationType" : '<c:out value='${request.academicServiceRequestSituationType.localizedName}'/>',
            "actions" :
            	<% if (AcademicAuthorizationGroup.get(AcademicOperationType.SERVICE_REQUESTS).isMember(AccessControl.getPerson().getUser())) {%>
                " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.READ_ACADEMIC_REQUEST_URL %>${ request.externalId }\"><spring:message code='label.view'/></a>" +
                <c:if test="${request.serviceRequestType.printable}">
                    <c:if test="${ request.academicServiceRequestSituationType == 'CONCLUDED' || request.academicServiceRequestSituationType == 'DELIVERED' }">
                        " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URL %>${ request.externalId }\"><spring:message code='label.print'/></a>" +
                    </c:if>
                    <c:if test="${ not (serviceRequest.academicServiceRequestSituationType == 'CONCLUDED' || request.academicServiceRequestSituationType == 'DELIVERED') }">
                        " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.PRINT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }\"><spring:message code='label.print'/></a>" +
                    </c:if>
                </c:if>
                <c:if test="${not request.serviceRequestType.printable}">
                    " <a  class=\"btn btn-default btn-xs\" disabled href=\"${pageContext.request.contextPath}\"><spring:message code='label.print'/></a>" +
                </c:if>
                ""
                <%  } else {%>
                " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestController.READ_SERVICE_REQUEST_URL %>${ request.externalId }\"><spring:message code='label.view'/></a>" +
                " <a  class=\"btn btn-default btn-xs\" disabled href=\"${pageContext.request.contextPath}\"><spring:message code='label.print'/></a>" +
                ""
                <% }%>
        },
        </c:forEach>
    ];
    
    $(document).ready(function() {

    


        var table = $('#historicAcademicServiceRequestsTable').DataTable({language : {
            url : "${datatablesI18NUrl}",           
        },
        "columns": [
            { data: 'requestDate' },
            { data: 'activeSituationDate' },
            { data: 'serviceRequestNumberYear' },
            { data: 'description' },
            { data: 'academicServiceRequestSituationType' },
            { data: 'actions',className:"all" }
        ],
        "columnDefs": [
            { "width": "128px", "targets": 5 } 
        ],
        "data" : uLisboaServiceRequestsDataSet,
        //Documentation: https://datatables.net/reference/option/dom
//"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/static/treasury/swf/copy_csv_xls_pdf.swf"
        }
        });
        table.columns.adjust().draw();
        
//           $('#searchvattypeTable tbody').on( 'click', 'tr', function () {
//                 $(this).toggleClass('selected');
//             } );
          
    }); 
</script>
