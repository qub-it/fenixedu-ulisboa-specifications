<%@page import="org.fenixedu.academic.domain.ExecutionSemester"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.enrolmentperiod.manageenrolmentperiod.AcademicEnrolmentPeriodController"%>
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
    url = "${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.DELETE_URL %>/" + externalId;
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
                            code="label.manageAcademicEnrolmentPeriods.searchAcademicEnrolmentPeriod.confirmDelete" />
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
        <spring:message code="label.manageAcademicEnrolmentPeriod.search" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.CREATE_URL %>">
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

//    $scope.object= angular.fromJson('${BeanJson}');;
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
                    <spring:message code="label.AcademicEnrolmentPeriod.type" />
                </div>

                <div class="col-sm-4">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicEnrolmentPeriod_type"
                        class="js-example-basic-single"
                        name="enrolmentPeriodType">
                        <option value="">&nbsp;</option>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.automaticEnrolment" />
                </div>

                <div class="col-sm-4">
                    <select id="academicEnrolmentPeriod_automaticEnrolment"
                        class="js-example-basic-single"
                        name="automaticEnrolment">
                        <option value="">&nbsp;</option>
                    </select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.executionSemester" />
                </div>

                <div class="col-sm-4">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <select id="academicEnrolmentPeriod_executionSemester"
                        class="js-example-basic-single"
                        name="executionSemester">
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
            <c:when test="${ not empty academicEnrolmentPeriodsResult }">
                <div class="panel panel-body">
                    <table id="searchAcademicEnrolmentPeriodsTable"
                      class="table responsive table-bordered table-hover" width="100%">
                        <thead>
                            <tr>
                                <th>
                                    <spring:message code="label.AcademicEnrolmentPeriod.type" />
                                </th>
                                <th>
                                    <spring:message code="label.AcademicEnrolmentPeriod.startDate" />
                                </th>
                                <th>
                                    <spring:message code="label.AcademicEnrolmentPeriod.endDate" />
                                </th>
                                <th>
                                    <spring:message code="label.AcademicEnrolmentPeriod.degreeCurricularPlans" />
                                </th>
                                <th>
                                    <spring:message code="label.AcademicEnrolmentPeriod.statuteTypes" />
                                </th>
                                <th>
                                    <spring:message code="label.AcademicEnrolmentPeriod.ingressionTypes" />
                                </th>
                                <th>
                                    <spring:message code="label.AcademicEnrolmentPeriod.configuration" />
                                </th>
                                <!-- operation column -->
                                <th style="width: 170px"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="academicEnrolmentPeriod" items="${ academicEnrolmentPeriodsResult }">
                            <tr>
                                <td><c:out value="${ academicEnrolmentPeriod.enrolmentPeriodType.descriptionI18N.content }" /></td>
                                <td><joda:format value='${academicEnrolmentPeriod.startDate}' style='SM' /></td>
                                <td><joda:format value='${academicEnrolmentPeriod.endDate}' style='SM' /></td>
                                <td>
                                    <c:if test="${ academicEnrolmentPeriod.degreeCurricularPlansSet.size() > 5 }">
                                        <c:set var="planMessage">
                                            <c:forEach var="element" items="${ academicEnrolmentPeriod.degreeCurricularPlansSet }">
                                                <li><c:out value="[${ element.degree.code }] ${ element.presentationName }"/> </li>
                                            </c:forEach>
                                        </c:set>
                                        <div data-toggle="tooltip" data-html="true" title="${ planMessage }" >
                                            <spring:message code="message.AcademicEnrolmentPeriod.has.x.degreeCurricularPlans" arguments="${ academicEnrolmentPeriod.degreeCurricularPlansSet.size() }" />
                                        </div>
                                    </c:if>
                                    <c:if test="${ academicEnrolmentPeriod.degreeCurricularPlansSet.size() <= 5 }">
                                        <c:forEach var="element" items="${ academicEnrolmentPeriod.degreeCurricularPlansSet }">
                                            <li><c:out value="[${ element.degree.code }] ${ element.presentationName }"/> </li>
                                        </c:forEach>
                                    </c:if>
                                </td>
                                <td>
				                    <c:set var="statuteMeaning">
				                    	<c:if test="${ academicEnrolmentPeriod.restrictToSelectedStatutes }">
				                    		<spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.true" />
				                    	</c:if>
				                    	<c:if test="${ !academicEnrolmentPeriod.restrictToSelectedStatutes }">
				                    		<spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.false" />
				                    	</c:if>
				                    </c:set>
                                    <c:if test="${ academicEnrolmentPeriod.statuteTypesSet.size() > 5 }">
                                        <c:set var="statuteMessage">
                                        	<c:out value="${ statuteMeaning }"/>:
                                            <c:forEach var="element" items="${ academicEnrolmentPeriod.statuteTypesSet }">
                                                <li><c:out value="[${ element.code }] ${ element.name.content }"/> </li>
                                            </c:forEach>
                                        </c:set>
                                        <div data-toggle="tooltip" data-html="true" title="${ statuteMessage }" >
                                            <spring:message code="message.AcademicEnrolmentPeriod.has.x.statuteTypes" arguments="${ academicEnrolmentPeriod.statuteTypesSet.size() }" />
                                        </div>
                                    </c:if>
                                    <c:if test="${ academicEnrolmentPeriod.statuteTypesSet.size() <= 5 && academicEnrolmentPeriod.statuteTypesSet.size() > 0 }">
                                    	<c:out value="${ statuteMeaning }"/>:
                                        <c:forEach var="element" items="${ academicEnrolmentPeriod.statuteTypesSet }">
                                            <li><c:out value="[${ element.code }] ${ element.name.content }"/> </li>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${ academicEnrolmentPeriod.statuteTypesSet.size() == 0 }">-</c:if>
                                </td>
                                <td>
                                    <c:set var="ingressionMeaning">
                                        <c:if test="${ academicEnrolmentPeriod.restrictToSelectedIngressionTypes }">
                                            <spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedIngressions.true" />
                                        </c:if>
                                        <c:if test="${ !academicEnrolmentPeriod.restrictToSelectedIngressionTypes }">
                                            <spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedIngressions.false" />
                                        </c:if>
                                    </c:set>
                                    <c:if test="${ academicEnrolmentPeriod.ingressionTypesSet.size() > 5 }">
                                        <c:set var="ingressionMessage">
                                            <c:out value="${ ingressionMeaning }"/>:
                                            <c:forEach var="element" items="${ academicEnrolmentPeriod.ingressionTypesSet }">
                                                <li><c:out value="[${ element.code }] ${ element.description.content }"/> </li>
                                            </c:forEach>
                                        </c:set>
                                        <div data-toggle="tooltip" data-html="true" title="${ ingressionMessage }" >
                                            <spring:message code="message.AcademicEnrolmentPeriod.has.x.ingressionTypes" arguments="${ academicEnrolmentPeriod.ingressionTypesSet.size() }" />
                                        </div>
                                    </c:if>
                                    <c:if test="${ academicEnrolmentPeriod.ingressionTypesSet.size() <= 5 && academicEnrolmentPeriod.ingressionTypesSet.size() > 0 }">
                                        <c:out value="${ ingressionMeaning }"/>:
                                        <c:forEach var="element" items="${ academicEnrolmentPeriod.ingressionTypesSet }">
                                            <li><c:out value="[${ element.code }] ${ element.description.content }"/> </li>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${ academicEnrolmentPeriod.ingressionTypesSet.size() == 0 }">-</c:if>
                                </td>
                                <td>
                                	<li><c:out value="${ academicEnrolmentPeriod.executionSemester.qualifiedName }"/></li>
                                    <c:if test="${ academicEnrolmentPeriod.firstTimeRegistration }">
                                        <li> <spring:message code="label.AcademicEnrolmentPeriod.firstTimeRegistration"/> </li>
                                    </c:if>
                                    <c:if test="${ academicEnrolmentPeriod.minStudentNumber != null }">
                                        <li> <spring:message code="label.AcademicEnrolmentPeriod.minStudentNumber"/> - ${ academicEnrolmentPeriod.minStudentNumber } </li>
                                    </c:if>
                                    <c:if test="${ academicEnrolmentPeriod.maxStudentNumber != null }">
                                        <li> <spring:message code="label.AcademicEnrolmentPeriod.maxStudentNumber"/> - ${ academicEnrolmentPeriod.maxStudentNumber } </li>
                                    </c:if>
                                    <c:if test="${ academicEnrolmentPeriod.curricularYear != null }">
                                        <li> <spring:message code="label.AcademicEnrolmentPeriod.curricularYear"/> - ${ academicEnrolmentPeriod.curricularYear } </li>
                                    </c:if>
                                </td>
                                <td>
                                    <a class="btn btn-default btn-xs"
                                        href="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.SEARCH_VIEW_URL %>/${ academicEnrolmentPeriod.externalId }">
                                            <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
                                            &nbsp;
                                            <spring:message code='label.view' />
                                    </a>
                                    <a class="btn btn-danger btn-xs" onClick="openDeleteModal('${academicEnrolmentPeriod.externalId}')">
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
    var table = $('#searchAcademicEnrolmentPeriodsTable').DataTable({
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
		"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
		//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
		"tableTools" : {
			"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
		}
	});
	table.columns.adjust().draw();
	
	
	//DROPDOWN execution semester
	executionSemester_options = [
        <c:forEach items="${executionSemesters}" var="element"> 
            {
                text :"<c:out value='${element.qualifiedName}'/>", 
                id : "<c:out value='${element.externalId}'/>"
            },
        </c:forEach>
    ];
	                    
    $("#academicEnrolmentPeriod_executionSemester").select2({
        data : executionSemester_options,
    });

	$("#academicEnrolmentPeriod_executionSemester").select2().select2('val', '<c:out value='${param.executionSemester}'/>');
	
	
	//DROPDOWN type
	type_options = [
        <c:forEach items="${enrolmentPeriodTypes}" var="element"> 
            {
                text :"<c:out value='${element.descriptionI18N.content}'/>", 
                id : "<c:out value='${element.toString()}'/>"
            },
        </c:forEach>
    ];
                        
    $("#academicEnrolmentPeriod_type").select2({
        data : type_options,
    });
                                
    $("#academicEnrolmentPeriod_type").select2().select2('val', '<c:out value='${param.enrolmentPeriodType}'/>');

    //DROPDOWN automaticEnrolment
    type_options = [
        <c:forEach items="${automaticEnrolment}" var="element"> 
            {
                text :"<c:out value='${element.descriptionI18N.content}'/>", 
                id : "<c:out value='${element.toString()}'/>"
            },
        </c:forEach>
    ];
                        
    $("#academicEnrolmentPeriod_automaticEnrolment").select2({
        data : type_options,
    });
                                
    $("#academicEnrolmentPeriod_automaticEnrolment").select2().select2('val', '<c:out value='${param.automaticEnrolment}'/>');

    
    $('[data-toggle="tooltip"]').tooltip();

});
</script>

