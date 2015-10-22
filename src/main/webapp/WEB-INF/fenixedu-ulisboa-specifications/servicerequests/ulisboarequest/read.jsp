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
<%@page import="org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest.ULisboaServiceRequestManagementController"%>
<%@page import="org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType"%>
<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter"%>
<%@page import="org.fenixedu.academic.domain.student.Registration" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

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
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

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


<script type="text/javascript">
      function openModal(url) {
        $("#uLisboaServiceRequestForm").attr("action", url);
        $('#uLisboaServiceRequestModal').modal('toggle')
      }
      function submit(url) {
          $("#uLisboaServiceRequestForm").attr("action", url);
          $("#uLisboaServiceRequestForm").submit();
      }
</script>

<div class="modal fade" id="uLisboaServiceRequestModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="uLisboaServiceRequestForm" action="#" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="form-group row">
                        <spring:message code="label.serviceRequests.ulisboarequest.confirmRejectOrCancel" />
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.serviceRequests.UlisboaServiceRequest.justification"/>
                        </div>
                        <div class="col-sm-10 control-label">
                            <input id="justification" name="justification" class="form-control" type="text" value="${ param.justification }"/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-primary" type="submit">
                        <spring:message code="label.confirm" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.academicRequest.readAcademicRequest" />
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
    <a class=""
        href="${pageContext.request.contextPath}<%= GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, session) %>">
        <spring:message code="label.event.back" />
    </a>

    <c:if test="${ serviceRequest.academicServiceRequestSituationType == 'NEW'}">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
        &nbsp; 
        <a class="" href="#" onclick="submit('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.PROCESS_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }')">
            <spring:message code="label.event.process" />
        </a>
    </c:if>
    <c:if test="${ serviceRequest.academicServiceRequestSituationType == 'PROCESSING'}">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-check" aria-hidden="true"></span>
        &nbsp; 
        <a class="" href="#" onclick="submit('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.CONCLUDE_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }')">
            <spring:message code="label.event.conclude" />
        </a>
    </c:if>
    <c:if test="${ serviceRequest.academicServiceRequestSituationType == 'CONCLUDED'}">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-share" aria-hidden="true"></span>
        &nbsp; 
        <a class="" href="#" onclick="submit('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.DELIVER_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }')">
            <spring:message code="label.event.deliver" />
        </a>
    </c:if>
    &nbsp;|&nbsp;
    <span class="glyphicon glyphicon-print" aria-hidden="true"></span>
    &nbsp; 
        <a class="" href="${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.PRINT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }">
        <spring:message code="label.print" />
    </a>

    <c:if test="${ serviceRequest.paymentURL != null }">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span>
        &nbsp; 
            <a class="" href="${ serviceRequest.paymentURL }">
            <spring:message code="label.payments" />
        </a>
    </c:if>

    &nbsp;|&nbsp;
    <div class="btn-group">
        <button type="button" class=" btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <span class="glyphicon glyphicon-list" aria-hidden="true"></span>&nbsp;
            <spring:message code="label.event.more" />
            <span class="caret"></span>
        </button>
        <ul class="dropdown-menu">
            <li>
        <a class="" href="#" onclick="submit('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.REVERT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }')">
                <span class="glyphicon glyphicon-retweet" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.revert" />
                </a>
            </li>
            <li>
        <a class="" href="#" onclick="openModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.REJECT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }')">
                <span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.reject" />
                </a>
            </li>
            <li>
        <a class="" href="#" onclick="openModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.CANCEL_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }')">
                <span class="glyphicon glyphicon-ban-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.cancel" />
                </a>
            </li>
        </ul>
    </div>
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

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.details" />
        </h3>
    </div>
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.academicRequest.requestDate" /></th>
                        <td><c:out value='${serviceRequest.requestDate.toString("YYYY-MM-dd")}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.academicRequest.activeSituationDate" /></th>
                        <td><c:out value='${serviceRequest.activeSituationDate.toString("YYYY-MM-dd")}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.academicRequest.serviceRequestNumberYear" /></th>
                        <td><c:out value='${serviceRequest.serviceRequestNumberYear}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.academicRequest.description" /></th>
                        <td><c:out value='${serviceRequest.description}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.academicRequest.currentState" /></th>
                        <td><c:out value='${serviceRequest.academicServiceRequestSituationType.localizedName}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.ULisboaServiceRequest.documentType" /></th>
                        <td><c:out value='${serviceRequest.serviceRequestType.name.content}' /></td>
                    </tr>
                    <c:forEach var="property" items="${ serviceRequest.serviceRequestPropertiesSet }">
                        <tr>
                            <th scope="row" class="col-xs-3"><spring:message code="${ property.serviceRequestSlot.label.content }" /></th>
                            <td>
                                <c:choose>
                                    <c:when test="${ property.booleanValue != null }">
                                        <c:if test="${ property.booleanValue }">
                                            <spring:message code="label.yes" />
                                        </c:if>
                                        <c:if test="${ not property.booleanValue }">
                                            <spring:message code="label.no" />                                
                                        </c:if>
                                    </c:when>
                                    <c:when test="${ not empty property.ICurriculumEntriesSet }">
                                        <c:forEach var="entry" items="${ property.ICurriculumEntriesSet }">
                                            <li><c:out value="${ entry.code } - ${ entry.name.content } - ${ entry.executionYear.qualifiedName }"/> </li>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value='${ property.string } ${ property.integer } 
                                                    ${ property.locale.displayLanguage }
                                                    ${ property.cycleType.description } 
                                                    ${ property.localizedString.content }
                                                    ${ property.dateTime.toLocalDate() } 
                                                    ${ propety.registration } ${ property.executionYear.name } 
                                                    ${ property.documentPurposeTypeInstance.name.content }
                                                    ${ property.studentCurricularPlan.name }' />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>                    
                    </c:forEach>
                </tbody>
            </table>
        </form>
    </div>
</div>

<script>
    $(document).ready(function() {

    });
</script>
