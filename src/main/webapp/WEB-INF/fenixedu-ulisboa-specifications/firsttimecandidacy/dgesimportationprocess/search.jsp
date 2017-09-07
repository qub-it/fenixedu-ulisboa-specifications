<%@page import="org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.importation.DgesImportationProcessController"%>
<%@page import="org.fenixedu.academic.domain.ExecutionSemester"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%--${portal.toolkit()}--%>

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<script type="text/javascript">
function openCancelModal(externalId) {
    url = "${pageContext.request.contextPath}<%= DgesImportationProcessController.CANCEL_URL %>/" + externalId;
    $("#cancelForm").attr("action", url);
    $('#cancelModal').modal('toggle');
}
function openResendModal(externalId) {
    url = "${pageContext.request.contextPath}<%= DgesImportationProcessController.RESEND_URL %>/" + externalId;
    $("#resendForm").attr("action", url);
    $('#resendModal').modal('toggle');
}
</script>

<div class="modal fade" id="cancelModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="cancelForm" action="#" method="POST">
                <div class="modal-header">
                    <button type="button" class="close"
                        data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.DgesImportationProcess.search.confirmCancel" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                        data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger"
                        type="submit">
                        <spring:message code="label.cancel" />
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
        <spring:message code="title.dges.importation.process" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= DgesImportationProcessController.CONFIGURATION_UPDATE_URL %>">
        <spring:message code="link.dges.importation.configuration" />
    </a>
    &nbsp;|&nbsp;
    <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= DgesImportationProcessController.CREATE_URL %>">
        <spring:message code="label.dgesImportProcess.create" />
    </a>
</div>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>

<script>

angular.module('angularApp', ['ngSanitize', 'ui.select']).controller('angularController', ['$scope', function($scope) {

    $scope.object= ${dgesBaseProcessBeanJson};
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
            { name : '<spring:message code="label.no"/>', value : false },
            { name : '<spring:message code="label.yes"/>', value : true } 
    ];
    
}]);
</script>

<div class="panel panel-default">
    <form method="post" class="form-horizontal" action="${pageContext.request.contextPath}<%= DgesImportationProcessController.SEARCH_URL %>" ng-app="angularApp" ng-controller="angularController">
        <input name="executionYear" type="hidden" value="{{ object.executionYear }}" />
    
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.dges.importation.process.execution.year" />
                </div>

                <div class="col-sm-4">
                    
                    <ui-select  id="dgesImportationProcess_executionYear" name="executionYear" ng-model="$parent.object.executionYear" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="type.id as type in object.executionYearDataSource | filter: $select.search">
                            <span ng-bind-html="type.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-default" role="button"
                value="<spring:message code="label.search" />" />
        </div>
    </form>
</div>

<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
    action='#'>

    <input name="postback" type="hidden" value="#" />
    <input name="bean" type="hidden" value="{{ object }}" />

    <div id="content">
        <ul id="tabs" class="nav nav-tabs" data-tabs="tabs">
            <li class="active"><a href="#jobsUndone" data-toggle="tab"><spring:message code="title.dges.importation.process.jobs.undone" /></a></li>
            <li>               <a href="#jobsDone" data-toggle="tab"><spring:message code="title.dges.importation.process.jobs.done" /></a></li>
        </ul>
        
        <div id="my-tab-content" class="tab-content">
            <div class="tab-pane active" id="jobsUndone">
                <p></p>
                <c:choose>
                    <c:when test="${not empty importationJobsPending}">
                        <table id="searchImportationJobsUnDoneTable" class="table responsive table-bordered table-hover" width="100%">
                            <thead>
                                <tr>
                                    <th>
                                        <spring:message code="label.dges.importation.process.filename" />
                                    </th>
                                    <th>
                                        <spring:message code="label.dges.importation.process.request.date" />
                                    </th>
                                    <th>
                                        <spring:message code="label.dges.importation.process.cancelled" />
                                    </th>
                                    <th>
                                        <spring:message code="label.dges.importation.process.importation.content" />
                                    </th>
                                    <!-- operation column -->
                                    <th style="width: 170px"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="process" items="${ importationJobsPending }">
                                    <tr>
                                        <td><c:out value="${ process.filename }" /></td>
                                        <td><joda:format value='${process.requestDate}' style='SM' /></td>
                                        <td>
                                            <c:if test="${ process.isNotDoneAndCancelled }">
                                                <spring:message code="label.yes"/>
                                            </c:if>
                                            <c:if test="${ not process.isNotDoneAndCancelled }">
                                                <spring:message code="label.no"/>
                                            </c:if>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}<%= DgesImportationProcessController.DOWNLOAD_URL %>/${process.externalId}">
                                                <fmt:formatNumber var="fileSize" value="${process.dgesStudentImportationFile.size / 1024}" maxFractionDigits="0" />
                                                <c:out value='${process.dgesStudentImportationFile.displayName} - ${fileSize}kB'/>
                                            </a>
                                        </td>
                                        <td>
                                            <a class="btn btn-danger" onClick="openCancelModal('${process.externalId}')">
                                                    <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                                                    &nbsp;
                                                    <spring:message code='label.cancel' />
                                            </a>                                            
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>                        
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning" role="alert">
                            <p>
                                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                                <spring:message code="label.noResultsFound" />
                            </p>
    
                        </div>
    
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="tab-pane" id="jobsDone">
                <p></p>
                <c:choose>
                    <c:when test="${not empty importationJobsDone}">
                        <table id="searchImportationJobsDoneTable" class="table responsive table-bordered table-hover" width="100%">
                            <thead>
                                <tr>
                                    <th>
                                        <spring:message code="label.dges.importation.process.filename" />
                                    </th>
                                    <th>
                                        <spring:message code="label.dges.importation.process.request.date" />
                                    </th>
                                    <th>
                                        <spring:message code="label.dges.importation.process.start.time" />
                                    </th>
                                    <th>
                                        <spring:message code="label.dges.importation.process.end.time" />
                                    </th>
                                    <th>
                                        <spring:message code="label.dges.importation.process.importation.content" />
                                    </th>
                                    <!-- operation column -->
                                    <th style="width: 170px"></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="process" items="${ importationJobsDone }">
                                    <tr>
                                        <td><c:out value="${ process.filename }" /></td>
                                        <td><joda:format value='${process.requestDate}' style='SM' /></td>
                                        <td><joda:format value='${process.jobStartTime}' style='SM' /></td>
                                        <td><joda:format value='${process.jobEndTime}' style='SM' /></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}<%= DgesImportationProcessController.DOWNLOAD_URL %>/${process.externalId}">
                                                <c:out value='${process.dgesStudentImportationFile.displayName}'/>
                                            </a>
                                        </td>
                                        <td>
                                            <a class="btn btn-default" href="${pageContext.request.contextPath}<%= DgesImportationProcessController.DOWNLOAD_LOG_URL %>/${process.externalId}">
                                                    <span class="glyphicon glyphicon-download" aria-hidden="true"></span>
                                                    &nbsp;
                                                    <spring:message code="label.download" />
                                            </a>                                            
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning" role="alert">
    
                            <p>
                                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                                <spring:message code="label.noResultsFound" />
                            </p>
    
                        </div>
    
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</form>



<script>
$(document).ready( function() {
    var table = $('#searchImportationJobsDoneTable').DataTable({
	    language : {
		    url : "${datatablesI18NUrl}",
		},
		"columnDefs" : [
		     //54
			{
			"width" : "180px",
			"targets" : 5
			} ],
		//Documentation: https://datatables.net/reference/option/dom
		//"dom" : '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
		//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
		//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
		"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
		"tableTools" : {
			"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
		}
	});
	table.columns.adjust().draw();
	
    var table2 = $('#searchImportationJobsUnDoneTable').DataTable({
        language : {
            url : "${datatablesI18NUrl}",
        },
        "columnDefs" : [
             //54
            {
            "width" : "180px",
            "targets" : 3
            } ],
        //Documentation: https://datatables.net/reference/option/dom
        //"dom" : '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
        //"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
        //"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
        "dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        "tableTools" : {
            "sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
        }
    });
    table2.columns.adjust().draw();
	
    //Enable Bootstrap Tabs
    $('#tabs').tab();
    
});
</script>

