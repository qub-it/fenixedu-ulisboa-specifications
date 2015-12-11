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
<%@page import="org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest.ULisboaServiceRequestManagementController"%>
<%@page import="org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType"%>
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


<academic:allowed operation="SERVICE_REQUESTS">
    <script type="text/javascript">
          function openConfirmationModal() {
              $("#uLisboaServiceRequestInvalidModal").modal('toggle');              
          }
          function openModal(url, name) {
            $("#"+ name + "Form").attr("action", url);
            $("#"+ name + "Modal").modal('toggle');
          }
          function openConcludeModal(url, notify) {
              $("#uLisboaServiceRequestConcludeForm").attr("action", url);
        	  if(notify) {
                  $('#uLisboaServiceRequestConcludeModal').modal('toggle');
              } else {
            	  $("#uLisboaServiceRequestConcludeForm").submit();
        	  }
          }
          function submit(url) {
              $("#uLisboaServiceRequestForm").attr("action", url);
              $("#uLisboaServiceRequestForm").submit();
          }
    </script>
    
    
    <div class="modal fade" id="uLisboaServiceRequestInvalidModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.title.printingSettings" />
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="form-group row">
                        <spring:message code="label.ULisboaServiceRequest.is.invalid.warning"/>
                        <ul>
                            <li> <spring:message code="label.ULisboaServiceRequest.invalid.instruction.one"/> </li>
                            <li> <spring:message code="label.ULisboaServiceRequest.invalid.instruction.two"/> </li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-primary" type="button" role="button" 
                            onclick="openConfirmationModal();openModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.PRINT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }', 'uLisboaServiceRequestPrint')">
                        <spring:message code="label.ULisboaServiceRequest.invalid.print" />
                    </button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.modal -->
    <div class="modal fade" id="uLisboaServiceRequestPrintModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <form id="uLisboaServiceRequestPrintForm" action="#" method="POST">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title">
                            <spring:message code="label.title.printingSettings" />
                        </h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group row">
                            <div class="col-sm-2 control-label">
                                <spring:message code="label.title.manageDocumentSignature"/>
                            </div>
                            <div class="col-sm-10 control-label">
                                <select id="signature" name="signature" class="form-control">
                                    <c:forEach var="signature" items="${ documentSignatures }">
                                        <option value="${ signature.externalId }"><c:out value="${ signature.responsibleName} - ${ signature.responsibleFunction.content }" /></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="col-sm-2 control-label">
                                <spring:message code="label.documentTemplates.template"/>
                            </div>
                            <div class="col-sm-10 control-label">
                                <select id="template" name="template" class="form-control">
                                    <c:forEach var="template" items="${ templates }">
                                        <option value="${ template.externalId }"><c:out value="${ template.name.content }" /></option>
                                    </c:forEach>
                                </select>
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
                            <c:set var="action"><spring:message code="label.serviceRequests.UlisboaServiceRequest.confirm.reject"/>/<spring:message code="label.serviceRequests.UlisboaServiceRequest.confirm.cancel"/></c:set>
                            <spring:message code="label.serviceRequests.UlisboaServiceRequest.confirm" arguments="${ action }" />                        
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
    
    <div class="modal fade" id="uLisboaServiceRequestRevertModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <form id="uLisboaServiceRequestRevertForm" action="#" method="POST">
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
                            <c:set var="action"><spring:message code="label.serviceRequests.UlisboaServiceRequest.confirm.revert"/></c:set>
                            <spring:message code="label.serviceRequests.UlisboaServiceRequest.confirm" arguments="${ action }" />                        
                        </div>
                        <c:if test="${serviceRequest.serviceRequestType.notifyUponConclusion}">
    	                    <div class="form-group row">
    	                        <div class="col-sm-2 control-label">
    	                            <spring:message code="label.serviceRequests.UlisboaServiceRequest.notifyRevertAction"/>
    	                        </div>
    	                        <div class="col-sm-10 control-label">
    	                            <select id="notifyRevertAction" name="notifyRevertAction" class="form-control">
    	                                <option value="false"><spring:message code="label.no" /></option>
    	                                <option value="true"><spring:message code="label.yes" /></option>
    	                            </select>
    	                        </div>
    	                    </div>
                        </c:if>
                        <c:if test="${ not serviceRequest.serviceRequestType.notifyUponConclusion }">
                            <input type="hidden" name="notifyRevertAction" value="false" />
                        </c:if>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            <spring:message code="label.close" />
                        </button>
                        <button id="submitButton" class="btn btn-primary" type="submit">
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
    
    <div class="modal fade" id="uLisboaServiceRequestConcludeModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <form id="uLisboaServiceRequestConcludeForm" action="#" method="POST">
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
                            <spring:message code="label.serviceRequests.UlisboaServiceRequest.confirmConclude" />
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            <spring:message code="label.close" />
                        </button>
                        <button id="submitButton" class="btn btn-primary" type="submit">
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
</academic:allowed>
<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.academicRequest.readAcademicRequest" />
        <small>
        </small>
    </h1>
</div>
<% 
  Registration registration = (Registration) request.getAttribute("registration"); 
  String url = "/academicAdministration/student.do?method=visualizeRegistration&registrationID="+ registration.getExternalId();
%>

<academic:notAllowed operation="SERVICE_REQUESTS">
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp; 
    <a class=""
        href="${pageContext.request.contextPath}<%= ULisboaServiceRequestController.READ_REGISTRATION_URL %>${ serviceRequest.registration.externalId }">
        <spring:message code="label.event.back.registration" />
    </a>
    &nbsp;|&nbsp;
    <span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span>
    &nbsp; 
    <a class=""
        href="${pageContext.request.contextPath}<%= ULisboaServiceRequestController.HISTORY_SERVICE_REQUEST_URL %>${ serviceRequest.registration.externalId }">
        <spring:message code="label.event.back.history" />
    </a>
    <c:if test="${ serviceRequest.isSelfIssued() && serviceRequest.serviceRequestType.printable && serviceRequest.isDelivered() && serviceRequest.isValid && serviceRequest.serviceRequestType.requestedOnline && not serviceRequest.serviceRequestType.payable }">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-print" aria-hidden="true"></span>
        &nbsp; 
        <a class=""
            href="${pageContext.request.contextPath}<%= ULisboaServiceRequestController.DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }">
            <spring:message code="label.print" />
        </a>
    </c:if>
</div>
</academic:notAllowed>

<academic:allowed operation="SERVICE_REQUESTS">
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp; 
    <a class=""
        href="${pageContext.request.contextPath}<%= GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, session) %>">
        <spring:message code="label.event.back.registration" />
    </a>
    <c:if test="${ serviceRequest.isValidTransition(serviceRequest.academicServiceRequestSituationType, 'PROCESSING') }">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
        &nbsp; 
        <a class="" href="#" onclick="submit('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.PROCESS_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }')">
            <spring:message code="label.event.process" />
        </a>
    </c:if>
    <c:if test="${ serviceRequest.isValidTransition(serviceRequest.academicServiceRequestSituationType,'CONCLUDED')}">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-check" aria-hidden="true"></span>
        &nbsp; 
        <a class="" href="#" onclick="openConcludeModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.CONCLUDE_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }', ${ serviceRequest.serviceRequestType.isToNotifyUponConclusion() })">
            <spring:message code="label.event.conclude" />
        </a>
    </c:if>
    <c:if test="${ serviceRequest.isValidTransition(serviceRequest.academicServiceRequestSituationType,'DELIVERED') }">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-share" aria-hidden="true"></span>
        &nbsp; 
        <a class="" href="#" onclick="submit('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.DELIVER_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }')">
            <spring:message code="label.event.deliver" />
        </a>
    </c:if>
    <c:if test="${serviceRequest.serviceRequestType.printable && !serviceRequest.isRejected() && !serviceRequest.isCancelled() && !serviceRequest.isNewRequest()}">
	    &nbsp;|&nbsp;
	    <span class="glyphicon glyphicon-print" aria-hidden="true"></span>
	    &nbsp;
        <c:choose>
            <c:when test="${ serviceRequest.academicServiceRequestSituationType == 'CONCLUDED' || serviceRequest.academicServiceRequestSituationType == 'DELIVERED' }">
                <a class="" href="${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }">
            </c:when>
            <c:when test="${ not serviceRequest.isValid }">
                <a class="" href="#" onclick="openConfirmationModal()">
            </c:when>
            <c:otherwise>
                <a class="" href="#" onclick="openModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.PRINT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }', 'uLisboaServiceRequestPrint')">
            </c:otherwise>
        </c:choose>
        <spring:message code="label.print" />
	    </a>
    </c:if>

    <c:if test="${ serviceRequest.paymentURL != null }">
        &nbsp;|&nbsp;
        <span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span>
        &nbsp; 
            <a class="" href="${pageContext.request.contextPath}${serviceRequest.paymentURL }">
            <spring:message code="label.event.payments" />
        </a>
    </c:if>

    <c:choose>
        <c:when test="${ serviceRequest.academicServiceRequestSituationType == 'DELIVERED' || serviceRequest.academicServiceRequestSituationType == 'CANCELLED' || serviceRequest.academicServiceRequestSituationType == 'REJECTED' }">
            &nbsp;|&nbsp;
            <span class="glyphicon glyphicon-retweet" aria-hidden="true"></span>
            &nbsp;
            <a class="" href="#" onclick="openModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.REVERT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }', 'uLisboaServiceRequestRevert')">
                <spring:message code="label.event.revert" />
            </a>
        </c:when>
        <c:otherwise>
            &nbsp;|&nbsp;
            <div class="btn-group">
                <button type="button" class=" btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span class="glyphicon glyphicon-list" aria-hidden="true"></span>&nbsp;
                    <spring:message code="label.event.more" />
                    <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">
                    <li>
                        <a class="" href="#" onclick="openModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.REVERT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }', 'uLisboaServiceRequestRevert')">
                        <span class="glyphicon glyphicon-retweet" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.revert" />
                        </a>
                    </li>
                    <li>
                        <a class="" href="#" onclick="openModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.REJECT_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }', 'uLisboaServiceRequest')">
                        <span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.reject" />
                        </a>
                    </li>
                    <li>
                        <a class="" href="#" onclick="openModal('${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.CANCEL_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }', 'uLisboaServiceRequest')">
                        <span class="glyphicon glyphicon-ban-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.cancel" />
                        </a>
                    </li>
<!--                     <li> -->
<%--                         <a class="" href="${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.UPDATE_URL %>${ serviceRequest.externalId }"> --%>
<%--                             <span class="glyphicon glyphicon-ban-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.update" /> --%>
<!--                         </a> -->
<!--                     </li>                     -->
                </ul>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</academic:allowed>

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
        <table class="table">
            <tbody>
                <tr>
                    <th scope="row" class="col-xs-3"><spring:message code="label.Registration.student.name" /></th>
                    <td><c:out value='${registration.student.name}' /></td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3"><spring:message code="label.Registration.student.documentType" /></th>
                    <td><c:out value='${registration.student.person.idDocumentType.localizedName }' /></td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3"><spring:message code="label.Registration.student.documentNumber" /></th>
                    <td><c:out value='${registration.student.person.documentIdNumber}' /></td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3"><spring:message code="label.Registration.student.number" /></th>
                    <td><c:out value='${registration.student.number}' /></td>
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
            </tbody>
        </table>
    </div>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.serviceRequests.ULisboaServiceRequest.details" />
        </h3>
    </div>
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.ULisboaServiceRequest.documentType" /></th>
                        <td><c:out value='${serviceRequest.serviceRequestType.name.content}' /></td>
                    </tr>                
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.academicRequest.serviceRequestNumberYear" /></th>
                        <td><c:out value='${serviceRequest.serviceRequestNumberYear}' /></td>
                    </tr>                
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.ULisboaServiceRequest.requestDate" /></th>
                        <td><joda:format value='${serviceRequest.requestDate}' style='S-' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.ULisboaServiceRequest.activeSituationDate" /></th>
                        <td><joda:format value='${serviceRequest.activeSituationDate}' style='S-' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.serviceRequests.AcademicServiceRequestSituation.state" /></th>
                        <td><span class="label label-primary"><c:out value='${ serviceRequest.activeSituation.academicServiceRequestSituationType.localizedName }' /></span></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.serviceRequests.AcademicServiceRequestSituation.date" /></th>
                        <td><joda:format value='${ serviceRequest.activeSituation.creationDate }' style='S-' /></td>
                    </tr>    
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.serviceRequests.AcademicServiceRequestSituation.responsible" /></th>
                            <academic:allowed operation="SERVICE_REQUESTS">                        
                                <td><c:out value='${ serviceRequest.activeSituation.creator.name }' /></td>
                            </academic:allowed>
                            <academic:notAllowed operation="SERVICE_REQUESTS">
                                <td><c:out value='${ serviceRequest.activeSituation.creator.profile.displayName }' /></td>
                            </academic:notAllowed>
                    </tr>
                    <c:if test="${ not empty serviceRequest.activeSituation.justification }">
                        <tr>
                            <th scope="row" class="col-xs-3"><spring:message code="label.serviceRequests.AcademicServiceRequestSituation.justification" /></th>
                            <td><c:out value='${ serviceRequest.activeSituation.justification }' /></td>
                        </tr>
                    </c:if>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.academicRequest.requestedOnline" /></th>
                        <td>
                            <c:if test="${serviceRequest.requestedOnline}">
                                <spring:message code="label.yes" />
                            </c:if>            
                            <c:if test="${not serviceRequest.requestedOnline}">
                                <spring:message code="label.no" />
                            </c:if>
                        </td>
                    </tr>                
                    <c:forEach var="property" items="${ serviceRequest.sortedServiceRequestProperties }">
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



<script type="text/javascript">
    $(document).ready(function() {

    });
    

</script>
