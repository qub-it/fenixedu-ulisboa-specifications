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
    <h1><spring:message code="label.firstTimeCandidacy.fillMobility" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}${controllerURL}/back"><spring:message code="label.back"/></a> 
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

<script>
angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

    $scope.object= ${mobilityFormJson};
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
                            { name : '<spring:message code="label.no"/>', value : false },
                            { name : '<spring:message code="label.yes"/>', value : true } 
                    ];
    
    $scope.submitForm = function() {
       $('form').submit();
    };
}]);
</script>

<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
     action="${pageContext.request.contextPath}${controllerURL}/fill">

    <input type="hidden" name="postback"
        value='${pageContext.request.contextPath}${controllerURL}/fillPostback' />
        
    <input name="bean" type="hidden" value="{{ object }}" />
    
    <div class="panel panel-default">
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-3 control-label required-field">
                    <spring:message code="label.mobilityRegistrationInformation.hasMobilityProgram" />
                </div>

                <div class="col-sm-8">
                    <ui-select id="mobilityRegistrationInformation_hasMobilityProgram" name="hasMobilityProgram"
                        ng-model="$parent.object.hasMobilityProgram" 
                        theme="bootstrap" > 
                        <ui-select-match>
                            {{$select.selected.name}}
                        </ui-select-match> 
                        <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues  | filter: $select.search">
                            <span ng-bind-html="bvalue.name"></span>
                        </ui-select-choices>
                    </ui-select>    
                </div>
            </div>

            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label">
                    <spring:message code="label.MobilityRegistrationInformation.programDuration" />
                </div>

                <div class="col-sm-8">
                    <ui-select id="mobilityRegistrationInformation_programDuration" class="" name="programduration"
                        ng-model="$parent.object.programDuration" theme="bootstrap" ng-disabled="disabled"> <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                    <ui-select-choices repeat="programDuration.id as programDuration in object.programDurationDataSource | filter: $select.search">
                    <span ng-bind-html="programDuration.text | highlight: $select.search"></span> </ui-select-choices> </ui-select>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label">
                    <spring:message code="label.MobilityRegistrationInformation.begin" />
                </div>

                <div class="col-sm-8">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <ui-select id="mobilityRegistrationInformation_begin" class="" name="begin" ng-model="$parent.object.begin" theme="bootstrap"
                        ng-disabled="disabled"> <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> <ui-select-choices
                        repeat="begin.id as begin in object.beginDataSource | filter: $select.search"> <span
                        ng-bind-html="begin.text | highlight: $select.search"></span> </ui-select-choices> </ui-select>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label">
                    <spring:message code="label.MobilityRegistrationInformation.end" />
                </div>

                <div class="col-sm-8">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <ui-select id="mobilityRegistrationInformation_end" class="" name="end" ng-model="$parent.object.end" theme="bootstrap"
                        ng-disabled="disabled"> 
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices
                            repeat="end.id as end in object.endDataSource | filter: $select.search">
                            <span ng-bind-html="end.text | highlight: $select.search"></span> 
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label">
                    <spring:message code="label.MobilityRegistrationInformation.beginDate" />
                </div>

                <div class="col-sm-8">
                    <input class="form-control" type="text" bennu-date="object.beginDate"/>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label">
                    <spring:message code="label.MobilityRegistrationInformation.endDate" />
                </div>

                <div class="col-sm-8">
                    <input class="form-control" type="text" bennu-date="object.endDate"/>
                </div>
            </div>

            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label" >
                    <spring:message code="label.MobilityRegistrationInformation.originMobilityProgrammeLevel" />
                </div>

                <div class="col-sm-8">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <ui-select id="mobilityRegistrationInformation_originMobilityProgrammeLevel" class="" name="originmobilityprogrammelevel"
                        ng-model="$parent.object.originMobilityProgrammeLevel" theme="bootstrap" ng-disabled="disabled">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                        <ui-select-choices repeat="mobilityProgrammeLevel.id as mobilityProgrammeLevel in object.mobilityProgrammeLevelDataSource | filter: $select.search">
                            <span ng-bind-html="mobilityProgrammeLevel.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label" >
                    <spring:message code="label.MobilityRegistrationInformation.otherOriginMobilityProgrammeLevel" />
                </div>

                <div class="col-sm-8">
                    <input id="mobilityRegistrationInformation_otherOriginMobilityProgrammeLevel" class="form-control" type="text" 
                        ng-model="object.otherOriginMobilityProgrammeLevel" />
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label">
                    <spring:message code="label.MobilityRegistrationInformation.mobilityActivityType" />
                </div>

                <div class="col-sm-8">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <ui-select id="mobilityRegistrationInformation_mobilityActivityType" class="" name="mobilityactivitytype"
                        ng-model="$parent.object.mobilityActivityType" theme="bootstrap" ng-disabled="disabled">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                        <ui-select-choices repeat="mobilityActivityType.id as mobilityActivityType in object.mobilityActivityTypeDataSource | filter: $select.search">
                            <span ng-bind-html="mobilityActivityType.text | highlight: $select.search"></span> 
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label">
                    <spring:message code="label.MobilityRegistrationInformation.mobilityProgramType" />
                </div>

                <div class="col-sm-8">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <ui-select id="mobilityRegistrationInformation_mobilityProgramType" class="" name="mobilityprogramtype"
                        ng-model="$parent.object.mobilityProgramType" theme="bootstrap" ng-disabled="disabled">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                        <ui-select-choices repeat="mobilityProgramType.id as mobilityProgramType in object.mobilityProgramTypeDataSource | filter: $select.search">
                            <span ng-bind-html="mobilityProgramType.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label"  >
                    <spring:message code="label.MobilityRegistrationInformation.mobilityScientificArea" />
                </div>

                <div class="col-sm-8">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <ui-select id="mobilityRegistrationInformation_mobilityScientificArea" class="" name="mobilityscientificarea"
                        ng-model="$parent.object.mobilityScientificArea" theme="bootstrap" ng-disabled="disabled">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                        <ui-select-choices repeat="mobilityScientificArea.id as mobilityScientificArea in object.mobilityScientificAreaDataSource | filter: $select.search">
                            <span ng-bind-html="mobilityScientificArea.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label" >
                    <spring:message code="label.MobilityRegistrationInformation.incomingMobilityProgrammeLevel" />
                </div>

                <div class="col-sm-8">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <ui-select id="mobilityRegistrationInformation_incomingMobilityProgrammeLevel" class="" name="incomingmobilityprogrammelevel"
                        ng-model="$parent.object.incomingMobilityProgrammeLevel" theme="bootstrap" ng-disabled="disabled">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                        <ui-select-choices repeat="mobilityProgrammeLevel.id as mobilityProgrammeLevel in object.mobilityProgrammeLevelDataSource | filter: $select.search">
                            <span ng-bind-html="mobilityProgrammeLevel.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label" >
                    <spring:message code="label.MobilityRegistrationInformation.otherIncomingMobilityProgrammeLevel" />
                </div>

                <div class="col-sm-8">
                    <input id="mobilityRegistrationInformation_otherIncomingMobilityProgrammeLevel" class="form-control" type="text" 
                        ng-model="object.otherIncomingMobilityProgrammeLevel" />
                </div>
            </div>
        
            <div class="form-group row" ng-show="object.hasMobilityProgram">
                <div class="col-sm-3 control-label" >
                    <spring:message code="label.MobilityRegistrationInformation.countryUnit" />
                </div>

                <div class="col-sm-8">
                    <%-- Relation to side 1 drop down rendered in input --%>
                    <ui-select id="mobilityRegistrationInformation_countryUnit" class="" name="countryUnit"
                        ng-model="$parent.object.countryUnit" theme="bootstrap" ng-disabled="disabled" on-select="onCountryUnitChange($item, $model)" on-remove="onCountryUnitChange($item, $model)">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
                        <ui-select-choices repeat="countryUnit.id as countryUnit in object.countryUnitDataSource | filter: $select.search">
                            <span ng-bind-html="countryUnit.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>

        </div>
        <div class="panel-footer">
            <button type="button" class="btn btn-primary" role="button" ng-click="submitForm()"><spring:message code="label.submit" /></button>
        </div>
    </div>
</form>

<script>
$(document).ready(function() {
});
</script>
