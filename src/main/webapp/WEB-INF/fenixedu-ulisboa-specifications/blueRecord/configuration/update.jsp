<%@page import="org.fenixedu.ulisboa.specifications.ui.blue_record.configuration.BlueRecordConfigurationController"%>
<%@page
    import="org.fenixedu.ulisboa.specifications.ui.reports.registrationhistory.RegistrationHistoryReportController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables"
    uri="http://github.com/dandelion/datatables"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

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
${portal.angularToolkit()}
<%--${portal.toolkit()}--%>

<link
    href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
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
    src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script
    src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script
    src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message
            code="label.title.blueRecordConfiguration" />
        <small></small>
    </h1>
</div>


<%-- NAVIGATION --%>

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


<script type="text/javascript">


angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {
            $scope.booleanvalues = [ {
                name : '<spring:message code="label.no"/>',
                value : false
            }, {
                name : '<spring:message code="label.yes"/>',
                value : true
            } ];

            $scope.object = ${blueRecordConfigurationBeanJson};
            $scope.postBack = createAngularPostbackFunction($scope);

            $scope.onBeanChange = function(model) {
                $scope.object.degrees = [];
                $scope.postBack(model);
            }
            
    }]);
    
</script>



<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
    action="${pageContext.request.contextPath}<%= BlueRecordConfigurationController.UPDATE_URL %>">

    <input name="postback" type="hidden" value="${pageContext.request.contextPath}<%= BlueRecordConfigurationController.UPDATE_POSTBACK_URL %>" />
    <input name="bean" type="hidden" value="{{ object }}" />
        
        
    <div class="panel panel-primary">
        <div class="panel-body">

            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.RegistrationHistoryReportParametersBean.degreeTypes" />
                </div>
                <div class="col-sm-6">
                    <ui-select id="degreeTypesSelect" name="degreeTypes"
                        ng-model="$parent.object.degreeTypes" theme="bootstrap"
                        on-select="onBeanChange($model)"
                        on-remove="onBeanChange($model)" multiple="true">
                    <ui-select-match>{{$item.text}}</ui-select-match> <ui-select-choices
                        repeat="degreeType.id as degreeType in object.degreeTypeDataSource | filter: $select.search">
                    <span ng-bind-html="degreeType.text | highlight: $select.search"></span>
                    </ui-select-choices> </ui-select>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.title.blueRecordConfiguration.excludeDegrees" />
                </div>
                <div class="col-sm-6">
                    <ui-select id="degreesSelect" name="degrees"
                        ng-model="$parent.object.degrees" theme="bootstrap"
                        multiple="true"> <ui-select-match>{{$item.text}}</ui-select-match>
                    <ui-select-choices
                        repeat="degree.id as degree in object.degreeDataSource | filter: $select.search">
                    <span ng-bind-html="degree.text | highlight: $select.search"></span>
                    </ui-select-choices> </ui-select>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.title.blueRecordConfiguration.isCgdFormToFill" />
                </div>
                <div class="col-sm-6">
                    <ui-select id="isCgdFormToFill" name="isCgdFormToFill"
                        ng-model="$parent.object.isCgdFormToFill"
                        theme="bootstrap" > 
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


<script type="text/javascript">

</script>



