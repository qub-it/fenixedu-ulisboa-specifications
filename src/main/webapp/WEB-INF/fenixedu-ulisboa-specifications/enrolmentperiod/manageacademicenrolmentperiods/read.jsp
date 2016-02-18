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


<div class="modal fade" id="deleteModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteForm" action="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.DELETE_URL %>/${ academicEnrolmentPeriod.externalId }" method="POST">
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
                            code="label.manageAcademicEnrolmentPeriods.readAcademicEnrolmentPeriod.confirmDelete" />
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
        <spring:message code="label.manageAcademicEnrolmentPeriod.read" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.SEARCH_URL %>">
        <spring:message code="label.event.back" />
    </a>
    &nbsp;|&nbsp;
    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="#" data-toggle="modal" data-target="#deleteModal">
        <spring:message code="label.event.delete" />
    </a>
    &nbsp;|&nbsp;
    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.UPDATE_URL %>/${ academicEnrolmentPeriod.externalId }">
        <spring:message code="label.event.update" />
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
Array.prototype.containsId = function(elementId){
    for(var i = 0; i < this.length; i++) {
        if(this[i] == elementId) {
            return i;
        }
    }
    return -1;
};
angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null
};
angular.module('angularApp', ['ngSanitize', 'ui.select']).controller('angularController', ['$scope', function($scope) {

    $scope.object= angular.fromJson('${academicEnrolmentPeriodBeanJson}');;
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
            { name : '<spring:message code="label.no"/>', value : false },
            { name : '<spring:message code="label.yes"/>', value : true } 
    ];
    //DegreeCurricularPlan Functions
    $scope.addDegreeCurricularPlan = function(model) {
        if (angular.isUndefinedOrNull($scope.degreeCurricularPlan)) {
            return;
        }
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.ADD_CURRICULAR_PLAN_URL%>/${academicEnrolmentPeriod.externalId}/' + $scope.degreeCurricularPlan;
        $('form[name="formPlan"]').find('input[name="postback"]').attr('value', url);
        
        $scope.form = $scope.formPlan;
        $scope.postBack(model);
        
        $scope.degreeCurricularPlan = undefined;
    };
    $scope.deleteDegreeCurricularPlan = function(degreeCurricularPlan, model) {
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.REMOVE_CURRICULAR_PLAN_URL%>/${academicEnrolmentPeriod.externalId}/' + degreeCurricularPlan;
        $('form[name="formPlan"]').find('input[name="postback"]').attr('value', url);
        $('#deletePlanModal').modal('toggle');
    }
    $scope.submitDeletePlan = function() {
        $('#deletePlanModal').modal('toggle');  
        $scope.form = $scope.formPlan;
        $scope.postBack(null);                 
    };
    $scope.getPlanName = function(id) {
        var name;
        angular.forEach(
                $scope.object.degreeCurricularPlanDataSource,
                function(curricularPlan) {
                    if (curricularPlan.id == id) {
                        name = curricularPlan.text;
                    }
                }, id, name)
        return name;
    }
    //StatuteType Functions    
    $scope.addStatuteType = function(model) {
        if (angular.isUndefinedOrNull($scope.statuteType)) {
            return;
        }
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.ADD_STATUTE_TYPE_URL%>/${academicEnrolmentPeriod.externalId}/' + $scope.statuteType;
        $('form[name="formStatute"]').find('input[name="postback"]').attr('value', url);
        
        $scope.form = $scope.formStatute;
        $scope.postBack(model);
        
        $scope.statuteType = undefined;
    };
    
    $scope.deleteStatuteType = function(statuteType, model) {
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.REMOVE_STATUTE_TYPE_URL%>/${academicEnrolmentPeriod.externalId}/' + statuteType;
        $('form[name="formStatute"]').find('input[name="postback"]').attr('value', url);
        $('#deleteStatuteModal').modal('toggle');
    }
    $scope.submitDeleteStatute = function() {
        $('#deleteStatuteModal').modal('toggle');  
        $scope.form = $scope.formStatute;
        $scope.postBack(null);                 
    };
    $scope.getStatuteName = function(id) {
        var name;
        angular.forEach(
                $scope.object.statuteTypeDataSource,
                function(statuteType) {
                    if (statuteType.id == id) {
                        name = statuteType.text;
                    }
                }, id, name)
        return name;
    }

    
    
}]).filter('filterMultipleValues', function() {
    return function(inputArray, otherArray) {
	    var items = {
		    array: otherArray,
		    output: []
	    };
	    angular.forEach(inputArray, function(value,key) {
		    if(this.array.containsId(value.id) === -1) {
			    this.output.push(value);
		    }
	    }, items);
	    return items.output;
    };
});
</script>


<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.details" />
        </h3>
    </div>
    <div class="panel-body">
        <form name='formRead' ng-app="angularApp" ng-controller="angularController" method="get" class="form-horizontal"
             action='#'>
             
                <input name="postback" type="hidden" value="#" />
                <input name="bean" type="hidden" value="{{ object }}" />
            
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.type" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.enrolmentPeriodType.descriptionI18N.content }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.executionSemester" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.executionSemester.qualifiedName }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.startDate" /></th>
                        <td><joda:format value='${academicEnrolmentPeriod.startDate}' style='SM' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.endDate" /></th>
                        <td><joda:format value='${academicEnrolmentPeriod.endDate}' style='SM' /></td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.specialSeason" /></th>
                        <td>
                            <c:if test="${academicEnrolmentPeriod.specialSeason}">
                                <spring:message code="label.true" />
                            </c:if> <c:if test="${not academicEnrolmentPeriod.specialSeason}">
                                <spring:message code="label.false" />
                            </c:if>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.firstTimeRegistration" /></th>
                        <td>
                            <c:if test="${academicEnrolmentPeriod.firstTimeRegistration}">
                                <spring:message code="label.true" />
                            </c:if> <c:if test="${not academicEnrolmentPeriod.firstTimeRegistration}">
                                <spring:message code="label.false" />
                            </c:if>
                        </td>
                    </tr> 
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes" /></th>
                        <td>
                            <c:if test="${academicEnrolmentPeriod.restrictToSelectedStatutes}">
                                <spring:message code="label.true" />
                            </c:if> <c:if test="${not academicEnrolmentPeriod.restrictToSelectedStatutes}">
                                <spring:message code="label.false" />
                            </c:if>
                        </td>
                    </tr>                                       
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.minStudentNumber" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.minStudentNumber }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.maxStudentNumber" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.maxStudentNumber }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.curricularYear" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.curricularYear }' /></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>

<form name="formPlan" ng-app="angularApp" ng-controller="angularController" method="post" class="form-horizontal" 
      action='#'>          
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.AcademicEnrolmentPeriod.degreeCurricularPlans" />
            </h3>
        </div>
        <input type="hidden" name="postback" value='#' />
        <input name="bean" type="hidden" value="{{ object }}" />
        
        <div class="panel panel-body">
            <div class="form-group row">
                <div class="col-sm-5">
                    <ui-select id="AcademicEnrolmentPeriod_degreeCurricularPlans"
                        ng-model="$parent.degreeCurricularPlan"
                        theme="bootstrap"> 
                        <ui-select-match allow-clear="true">
                            {{$select.selected.text}}
                        </ui-select-match> 
                        <ui-select-choices repeat="element.id as element in object.degreeCurricularPlanDataSource | filter: $select.search | filterMultipleValues : object.degreeCurricularPlans">
                            <span ng-bind-html="element.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
                <div class="col-sm-5">
                    <button type="button" class="btn btn-default" ng-click="addDegreeCurricularPlan($model)">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                </div>               
            </div>
        </div>
        <div class="panel panel-body">
              <table id="serviceRequestTypePropertiesTable"
                class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <th><spring:message
                                code="label.AcademicEnrolmentPeriod.degreeCurricularPlan" /></th>
                        <!-- operation column -->
                        <th style="width: 25%"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        ng-repeat="plan in object.degreeCurricularPlans">
                        <td>{{ getPlanName(plan) }}</td>
                        <td>
                            <a class="btn btn-danger" ng-click="deleteDegreeCurricularPlan(plan, $model)">
                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                &nbsp;
                                <spring:message code="label.delete" /> 
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    
    <div class="modal fade" id="deletePlanModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.AcademicEnrolmentPeriod.DegreeCurricularPlan.confirmRemove" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger" type="button" ng-click="submitDeletePlan()">
                        <spring:message code="label.delete" />
                    </button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.modal -->
    
</form>

<form name="formStatute" ng-app="angularApp" ng-controller="angularController" method="post" class="form-horizontal" 
      action='#'>          
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.AcademicEnrolmentPeriod.statuteTypes" />
            </h3>
        </div>
        <input type="hidden" name="postback" value='#' />
        <input name="bean" type="hidden" value="{{ object }}" />
        
        <div class="panel panel-body">
            <div class="form-group row">
                <div class="col-sm-5">
                    <ui-select id="AcademicEnrolmentPeriod_statuteTypes"
                        ng-model="$parent.statuteType"
                        theme="bootstrap"> 
                        <ui-select-match allow-clear="true">
                            {{$select.selected.text}}
                        </ui-select-match> 
                        <ui-select-choices repeat="element.id as element in object.statuteTypeDataSource | filter: $select.search | filterMultipleValues : object.statutesTypes  ">
                            <span ng-bind-html="element.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
                <div class="col-sm-5">
                    <button type="button" class="btn btn-default" ng-click="addStatuteType($model)">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                </div>               
            </div>
        </div>
        <div class="panel panel-body">
              <table id="statuteTypeTable"
                class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <th><spring:message
                                code="label.AcademicEnrolmentPeriod.statuteType" /></th>
                        <!-- operation column -->
                        <th style="width: 25%"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        ng-repeat="statute in object.statutesTypes">
                        <td>{{ getStatuteName(statute) }}</td>
                        <td>
                            <a class="btn btn-danger" ng-click="deleteStatuteType(statute, $model)">
                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                &nbsp;
                                <spring:message code="label.delete" /> 
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    
    <div class="modal fade" id="deleteStatuteModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.AcademicEnrolmentPeriod.StatuteType.confirmRemove" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger" type="button" ng-click="submitDeleteStatute()">
                        <spring:message code="label.delete" />
                    </button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.modal -->
    
</form>


<script>

angular.bootstrap($('form[name="formPlan"]')[0],['angularApp']);
angular.bootstrap($('form[name="formStatute"]')[0],['angularApp']);


$(document).ready(function() {

});
</script>

