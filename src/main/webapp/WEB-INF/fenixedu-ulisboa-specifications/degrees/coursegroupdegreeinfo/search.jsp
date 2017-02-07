<%@page import="org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo.CourseGroupDegreeInfoController"%>
<%@page import="org.fenixedu.academic.domain.ExecutionSemester"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

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
function openDeleteModal(externalId) {
    url = "${pageContext.request.contextPath}<%= CourseGroupDegreeInfoController.DELETE_URL %>/" + externalId;
    $("#deleteForm").attr("action", url);
    $('#deleteModal').modal('toggle');
}
</script>

<div class="modal fade" id="deleteModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteForm" action="#" method="POST">
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
                            code="label.manageCourseGroupDegreeInfo.searchCourseGroupDegreeInfo.confirmDelete" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                        data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger"
                        type="submit">
                        <spring:message code="label.delete" />
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
        <spring:message code="label.manageCourseGroupDegreeInfo.search" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= CourseGroupDegreeInfoController.CREATE_URL %>">
        <spring:message code="label.event.create" />
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

//    $scope.object= angular.fromJson('${BeanJson}');
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
            { name : '<spring:message code="label.no"/>', value : false },
            { name : '<spring:message code="label.yes"/>', value : true } 
    ];
    
}]);
</script>

<div class="panel panel-default">
    <form method="get" class="form-horizontal">
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.CourseGroupDegreeInfo.executionYear" />
                </div>

                <div class="col-sm-4">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="degreeDocumentInfo_executionYear"
                        class="js-example-basic-single"
                        name="executionYear">
                        <option value="">&nbsp;</option>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.CourseGroupDegreeInfo.degree" />
                </div>

                <div class="col-sm-4">
                    <select id="degreeDocumentInfo_degree"
                        class="js-example-basic-single"
                        name="extendedDegreeInfo">
                        <option value="">&nbsp;</option>
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


<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
    action='#'>

    <input name="postback" type="hidden" value="#" />
    <input name="bean" type="hidden" value="{{ object }}" />

    <div class="panel panel-primary">
        <c:choose>
            <c:when test="${ not empty degreeDocumentInfoResult }">
                <div class="panel panel-body">
                    <table id="searchDegreeDocumentInfosTable"
                      class="table responsive table-bordered table-hover" width="100%">
                        <thead>
                            <tr>
                                <th>
                                    <spring:message code="label.CourseGroupDegreeInfo.executionYear" />
                                </th>
                                <th>
                                    <spring:message code="label.CourseGroupDegreeInfo.degree" />
                                </th>
                                <th>
                                    <spring:message code="label.CourseGroupDegreeInfo.courseGroup" />
                                </th>
                                <th>
                                    <spring:message code="label.CourseGroupDegreeInfo.degreeName" />
                                </th>
                                <!-- operation column -->
                                <th style="width: 170px"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="degreeDocumentInfo" items="${ degreeDocumentInfoResult }">
                            <tr>
                                <td><c:out value="${ degreeDocumentInfo.extendedDegreeInfo.degreeInfo.executionYear.qualifiedName }" /></td>
                                <td><c:out value="${ degreeDocumentInfo.extendedDegreeInfo.degreeInfo.name.content }" /></td>
                                <td><c:out value="${ degreeDocumentInfo.courseGroup.name }" /></td>
                                <td><c:out value="${ degreeDocumentInfo.degreeName.content }" /></td>
                                <td>
                                    <a class="btn btn-default btn-xs"
                                        href="${pageContext.request.contextPath}<%= CourseGroupDegreeInfoController.SEARCH_VIEW_URL %>/${ degreeDocumentInfo.externalId }">
                                            <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
                                            &nbsp;
                                            <spring:message code='label.view' />
                                    </a>
                                    <a class="btn btn-danger btn-xs" onClick="openDeleteModal('${degreeDocumentInfo.externalId}')">
                                            <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                            &nbsp;
                                            <spring:message code='label.delete' />
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="panel panel-body">
                    <div class="alert alert-warning" role="alert">
                        <p>
                            <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                            <spring:message code="label.noResultsFound" />
                        </p>
                    </div>
                </div>    
            </c:otherwise>
        </c:choose>    
    </div>
</form>

<script>
$(document).ready( function() {
    var table = $('#searchDegreeDocumentInfosTable').DataTable({
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
		"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
		//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
		"tableTools" : {
			"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
		}
	});
	table.columns.adjust().draw();
	
	
	//DROPDOWN execution semester
	executionSemester_options = [
        <c:forEach items="${executionYears}" var="element"> 
            {
                text :"<c:out value='${element.qualifiedName}'/>", 
                id : "<c:out value='${element.externalId}'/>"
            },
        </c:forEach>
    ];
	                    
    $("#degreeDocumentInfo_executionYear").select2({
        data : executionSemester_options,
    });

	$("#degreeDocumentInfo_executionYear").select2().select2('val', '<c:out value='${param.executionYear}'/>');
	
	
	//DROPDOWN degree
	degree_options = [
        <c:forEach items="${degrees}" var="element"> 
            {
                text :"<c:out value='${element.code} - ${element.name}'/>", 
                id : "<c:out value='${element.externalId}'/>"
            },
        </c:forEach>
    ];
                        
    var sortFunction = function(a,b) { return a.text.localeCompare(b.text) };

	
    $("#degreeDocumentInfo_degree").select2({
        data : degree_options.sort(sortFunction),
    });
                                
    $("#degreeDocumentInfo_degree").select2().select2('val', '<c:out value='${param.extendedDegreeInfo}'/>');

    $('[data-toggle="tooltip"]').tooltip();
});
</script>

