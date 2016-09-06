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

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.manageAcademicEnrolmentPeriod.update" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.READ_URL %>/${academicEnrolmentPeriod.externalId}">
        <spring:message code="label.event.back" />
    </a> 
</div>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">
        <c:forEach items="${infoMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">
        <c:forEach items="${warningMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>
    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">
        <c:forEach items="${errorMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>
    </div>
</c:if>

<script>

angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

    $scope.object= angular.fromJson('${academicEnrolmentPeriodBeanJson}');;
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
            { name : '<spring:message code="label.no"/>', value : false },
            { name : '<spring:message code="label.yes"/>', value : true } 
    ];

    $scope.booleanvalues_restrictToSelectedStatutes = [
            { name : '<spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.false"/>', value : false },
            { name : '<spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.true"/>', value : true } 
    ];
    $scope.booleanvalues_restrictToSelectedIngressions = [
            { name : '<spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedIngressions.false"/>', value : false },
            { name : '<spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedIngressions.true"/>', value : true } 
    ];

    
}]);
</script>

<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
    action="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.UPDATE_URL %>/${ academicEnrolmentPeriod.externalId }">

    <input name="postback" type="hidden" value="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.CREATE_POSTBACK_URL %>" />
    <input name="bean" type="hidden" value="{{ object }}" />
    
    <div class="panel panel-default">
        <div class="panel-body">        
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.type" />
                </div>

                <div class="col-sm-10">
                    <ui-select	id="academicEnrolmentPeriod_type" name="type" ng-model="$parent.object.enrolmentPeriodType" theme="bootstrap">
                    	<ui-select-match >{{$select.selected.text}}</ui-select-match> 
                    	<ui-select-choices	repeat="type.id as type in object.enrolmentPeriodTypeDataSource | filter: $select.search">
                    		<span ng-bind-html="type.text | highlight: $select.search"></span>
                    	</ui-select-choices> 
                    </ui-select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.executionSemester" />
                </div>

                <div class="col-sm-10">
                    <ui-select id="academicEnrolmentPeriod_executionSemester" name=""
                        ng-model="$parent.object.executionSemester"
                        theme="bootstrap"> 
                        <ui-select-match>
                            {{$select.selected.text}}
                        </ui-select-match> 
                        <ui-select-choices repeat="element.id as element in object.executionSemesterDataSource | filter: $select.search">
                            <span ng-bind-html="element.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.startDate" />
                </div>

                <div class="col-sm-10">
                    <input class="form-control" type="text" bennu-date-time="object.startDate" />
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.endDate" />
                </div>

                <div class="col-sm-10">
                <input class="form-control" type="text" bennu-date-time="object.endDate" />
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.firstTimeRegistration" />
                </div>

                <div class="col-sm-10">
                    <ui-select id="academicEnrolmentPeriod_firstTimeRegistration" name="firstTimeRegistration"
                        ng-model="$parent.object.firstTimeRegistration"
                        theme="bootstrap"> 
                        <ui-select-match allow-clear="true">
                            {{$select.selected.name}}
                        </ui-select-match> 
                        <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                            <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes" />
                </div>

                <div class="col-sm-10">
                    <ui-select id="academicEnrolmentPeriod_restrictToSelectedStatutes" name="restrictToSelectedStatutes"
                        ng-model="$parent.object.restrictToSelectedStatutes"
                        theme="bootstrap"> 
                        <ui-select-match>
                            {{$select.selected.name}}
                        </ui-select-match> 
                        <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues_restrictToSelectedStatutes | filter: $select.search">
                            <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedIngressions" />
                </div>

                <div class="col-sm-10">
                    <ui-select id="academicEnrolmentPeriod_restrictToSelectedIngressions" name="restrictToSelectedIngressions"
                        ng-model="$parent.object.restrictToSelectedIngressionTypes"
                        theme="bootstrap"> 
                        <ui-select-match>
                            {{$select.selected.name}}
                        </ui-select-match> 
                        <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues_restrictToSelectedIngressions | filter: $select.search">
                            <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.minStudentNumber" />
                </div>

                <div class="col-sm-10">
                    <input id="academicEnrolmentPeriod_minStudentNumber" class="form-control"
                               type="number" ng-model="object.minStudentNumber" name="field"
                               value='<c:out value='${requestScope["academicEnrolmentPeriod_minStudentNumber"]}'/>'
                        />
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.maxStudentNumber" />
                </div>

                <div class="col-sm-10">
                    <input id="academicEnrolmentPeriod_maxStudentNumber" class="form-control"
                               type="number" ng-model="object.maxStudentNumber" name="field"
                               value='<c:out value='${requestScope["academicEnrolmentPeriod_maxStudentNumber"]}'/>'
                    />
                </div>
            </div>
                        <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.curricularYear" />
                </div>

                <div class="col-sm-10">
                    <input id="academicEnrolmentPeriod_curricularYear" class="form-control"
                               type="number" ng-model="object.curricularYear" name="field"
                               value='<c:out value='${requestScope["academicEnrolmentPeriod_curricularYear"]}'/>'
                    />
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.AcademicEnrolmentPeriod.schoolClassSelectionMandatory" />
                    &nbsp;<span class="glyphicon glyphicon-question-sign" aria-hidden="true" title="<spring:message code="label.AcademicEnrolmentPeriod.schoolClassSelectionMandatory.help" />"></span>
                </div>

                <div class="col-sm-10">
                    <ui-select id="academicEnrolmentPeriod_schoolClassSelectionMandatory" name="schoolClassSelectionMandatory"
                        ng-model="$parent.object.schoolClassSelectionMandatory"
                        theme="bootstrap"> 
                        <ui-select-match>
                            {{$select.selected.name}}
                        </ui-select-match> 
                        <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                            <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>            
        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-primary" role="button" value="<spring:message code="label.submit" />" />
        </div>
    </div>
</form>