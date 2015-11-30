<%@page import="org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest.ULisboaServiceRequestManagementController"%>
<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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

<link
    href="${pageContext.request.contextPath}/static/treasury/css/dataTables.responsive.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/static/treasury/js/dataTables.responsive.js"></script>
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
    src="${pageContext.request.contextPath}/static/treasury/js/omnis.js"></script>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}


<div class="modal fade" id="uLisboaServiceRequestConcludeModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="uLisboaServiceRequestConcludeForm" action="#" method="POST">
                <input type="hidden" name="redirect" value="true" />
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
<script type="text/javascript">
function openConcludeModal(url, notify) {
    $("#uLisboaServiceRequestConcludeForm").attr("action", url);
    if(notify) {
        $('#uLisboaServiceRequestConcludeModal').modal('toggle');
    } else {
        $("#uLisboaServiceRequestConcludeForm").submit();
    }
}
function submit(url) {
    $("#uLisboaServiceRequestConcludeForm").attr("action", url);
    $("#uLisboaServiceRequestConcludeForm").submit();
}
</script>

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message
            code="label.serviceRequests.ULisboaServiceRequest" />
        <small></small>
    </h1>
</div>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon glyphicon-ok-sign"
                    aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>
    </div>
</c:if>

<div class="panel panel-default">
    <form method="get" class="form-horizontal">
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.academicRequest.executionYear" />
                </div>

                <div class="col-sm-6">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicRequest_executionYear"
                        class="js-example-basic-single"
                        name="executionYear">
                        <option value="">&nbsp;</option>
                        <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.academicRequest.degreeType" />
                </div>

                <div class="col-sm-6">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicRequest_degreeType"
                        class="js-example-basic-single"
                        name="degreeType">
                        <option value="">&nbsp;</option>
                        <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.academicRequest.degree" />
                </div>

                <div class="col-sm-6">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicRequest_degree"
                        class="js-example-basic-single"
                        name="degree">
                        <option value="">&nbsp;</option>
                        <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.academicRequest.serviceRequestType" />
                </div>

                <div class="col-sm-6">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicRequest_serviceRequestType"
                        class="js-example-basic-single"
                        name="serviceRequestType">
                        <option value="">&nbsp;</option>
                        <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.academicRequest.state" />
                </div>

                <div class="col-sm-6">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicRequest_state"
                        class="js-example-basic-single"
                        name="state">
                        <option value="">&nbsp;</option>
                        <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.academicRequest.urgent" />
                </div>

                <div class="col-sm-6">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicRequest_urgent"
                        class="js-example-basic-single"
                        name="urgent">
                        <option value="false"><spring:message code="label.no" /></option>
                        <option value="true"><spring:message code="label.yes" /></option>
                    </select>
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-default" role="button"
                value="<spring:message code="label.search" />" />
        </div>
    </form>
</div>

<c:choose>
    <c:when test="${not empty searchServiceRequestsSet}">
        <table id="searchServiceRequestsTable"
            class="table responsive table-bordered table-hover"
            width="100%">
            <thead>
                <tr>
                    <%--!!!  Field names here --%>
                    <th><spring:message code="label.academicRequest.serviceRequestNumberYear" /></th>
                    <th><spring:message code="label.academicRequest.requestDate" /></th>
                    <th><spring:message code="label.academicRequest.description" /></th>
                    <th><spring:message code="label.academicRequest.student.number" /></th>
                    <th><spring:message code="label.academicRequest.student.name" /></th>
                    <th><spring:message code="label.academicRequest.degree" /></th>
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
	var searchServiceRequestsDataSet = [
			<c:forEach items="${searchServiceRequestsSet}" var="searchResult">
			   <c:set var="requestDate" value="${searchResult.requestDate.toString('yyyy-MM-dd')}" />
			   <c:set var="url" value="/academicAdministration/student.do?method=visualizeRegistration&registrationID=${ searchResult.registration.externalId}" scope="request"/>
			   <c:choose>
			       <c:when test="${searchResult.isValidTransition(searchResult.academicServiceRequestSituationType, 'PROCESSING')}">
			           <c:set var="controllerURL" value="<%= ULisboaServiceRequestManagementController.PROCESS_ACADEMIC_REQUEST_URL %>" />
			           <c:set var="nextStepAction" value="submit('${pageContext.request.contextPath}${controllerURL}${ searchResult.externalId }')" />
                       <c:set var="nextStepLabel" value="label.event.process" />
			       </c:when>
			       <c:when test="${searchResult.isValidTransition(searchResult.academicServiceRequestSituationType,'CONCLUDED')}">
                       <c:set var="controllerURL" value="<%= ULisboaServiceRequestManagementController.CONCLUDE_ACADEMIC_REQUEST_URL %>" />
			           <c:set var="nextStepAction" value="openConcludeModal('${pageContext.request.contextPath}${controllerURL}${ searchResult.externalId }', ${ searchResult.serviceRequestType.isToNotifyUponConclusion() })" />
                       <c:set var="nextStepLabel" value="label.event.conclude" />
			       </c:when>
                   <c:when test="${searchResult.isValidTransition(searchResult.academicServiceRequestSituationType,'DELIVERED')}}">
                       <c:set var="controllerURL" value="<%= ULisboaServiceRequestManagementController.DELIVER_ACADEMIC_REQUEST_URL %>" />
                       <c:set var="nextStepAction" value="submit('${pageContext.request.contextPath}${controllerURL}${ searchResult.externalId }')" />
                       <c:set var="nextStepLabel" value="label.event.deliver" />
                   </c:when>
			   </c:choose>
				{
				"serviceRequestNumberYear" : "<c:out value='${searchResult.serviceRequestNumberYear}'/>",
                "requestDate" : "<c:out value='${requestDate}'/>",
                "description" : "<c:out value='${ searchResult.description }' />",
                "studentNumber" : "<c:out value='${ searchResult.registration.student.number }' />",
                "studentName" : "<c:out value='${ searchResult.registration.student.name }' />",
                "degree" : "<c:out value='${ searchResult.registration.degree.name }' />",
                "actions" : 
                    " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.READ_ACADEMIC_REQUEST_URL %>${searchResult.externalId}\"><spring:message code='label.view.request'/></a>" +
                    " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), (String) request.getAttribute("url"), session) %>\"><spring:message code='label.view.registration'/></a>" +
                    <c:if test="${not empty nextStepAction}">
                        "<a  class=\"btn btn-default btn-xs\" href=\"#\" onclick=\"${nextStepAction}\"><spring:message code='${nextStepLabel}'/></a>" + 
                    </c:if>
                    "" 
                },				
            </c:forEach>
    ];
	
	$(document).ready(function() {
	  <%-- Begin of select academicRequest_executionYear --%>
	    execution_options = [
	       <c:forEach items="${executionYearsList}" var="element"> 
	       {
	    	    text :"<c:out value='${element.name}'/>", 
	            id : "<c:out value='${element.externalId}'/>"
	       },
	       </c:forEach>
	    ];
	    $("#academicRequest_executionYear").select2({
	    	   data : execution_options,
	    });
	    <%-- If it's not from parameter change param.productGroup to whatever you need (it's the externalId already) --%>
        $("#academicRequest_executionYear").select2().select2('val', '<c:out value='${param.executionYear}'/>');
      <%-- END of select academicRequest_executionYear --%>
      <%-- Begin of select academicRequest_executionYear --%>
      degreeType_options = [
         <c:forEach items="${degreeTypesList}" var="element"> 
         {
              text :"<c:out value='${element.name.content}'/>", 
              id : "<c:out value='${element.externalId}'/>"
         },
         </c:forEach>
      ];
      $("#academicRequest_degreeType").select2({
             data : degreeType_options,
      });
      <%-- If it's not from parameter change param.productGroup to whatever you need (it's the externalId already) --%>
      $("#academicRequest_degreeType").select2().select2('val', '<c:out value='${param.degreeType}'/>');
     <%-- END of select academicRequest_executionYear --%> 		
     <%-- Begin of select academicRequest_executionYear --%>
     degree_options = [
        <c:forEach items="${degreesList}" var="element"> 
        {
             text :"<c:out value='${element.nameI18N.content}'/>", 
             id : "<c:out value='${element.externalId}'/>"
        },
        </c:forEach>
     ];
     $("#academicRequest_degree").select2({
            data : degree_options,
     });
     <%-- If it's not from parameter change param.productGroup to whatever you need (it's the externalId already) --%>
     $("#academicRequest_degree").select2().select2('val', '<c:out value='${param.degree}'/>');
     <%-- END of select academicRequest_executionYear --%>        
     <%-- Begin of select academicRequest_executionYear --%>
     serviceRequestType_options = [
        <c:forEach items="${serviceRequestTypesList}" var="element"> 
        {
             text :"<c:out value='${element.name.content}'/>", 
             id : "<c:out value='${element.externalId}'/>"
        },
        </c:forEach>
     ];
     $("#academicRequest_serviceRequestType").select2({
            data : serviceRequestType_options,
     });
     <%-- If it's not from parameter change param.productGroup to whatever you need (it's the externalId already) --%>
     $("#academicRequest_serviceRequestType").select2().select2('val', '<c:out value='${param.serviceRequestType}'/>');
     <%-- END of select academicRequest_executionYear --%>        
     <%-- Begin of select academicRequest_executionYear --%>
     state_options = [
        <c:forEach items="${states}" var="element"> 
        {
             text :"<c:out value='${element.localizedName}'/>", 
             id : "<c:out value='${element}'/>"
        },
        </c:forEach>
     ];
     $("#academicRequest_state").select2({
            data : state_options,
     });
     <%-- If it's not from parameter change param.productGroup to whatever you need (it's the externalId already) --%>
     $("#academicRequest_state").select2().select2('val', '<c:out value='${param.state}'/>');
     <%-- END of select academicRequest_executionYear --%>        
     $("#academicRequest_urgent").select2().select2('val', '<c:out value='${param.urgent}'/>');
     


		var table = $('#searchServiceRequestsTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
        "columns": [
                    { data: 'serviceRequestNumberYear' },
                    { data: 'requestDate' },
                    { data: 'description' },
                    { data: 'studentNumber' },
                    { data: 'studentName' },
                    { data: 'degree' },
                    { data: 'actions',className:"all" }
			
		],
		"columnDefs": [
			       		//54
			       		//128
			       		               { "width": "128px", "targets": 6 } 
			       		             ],
		"data" : searchServiceRequestsDataSet,
		//Documentation: https://datatables.net/reference/option/dom
"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
        }
		});
		table.columns.adjust().draw();
		
		  $('#searchServiceRequestsTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

