<%@page import="org.fenixedu.academic.domain.Country"%>
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
	<h1><spring:message code="label.firstTimeCandidacy.fillPreviousDegreeInformation" />
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

<script>
angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

    $scope.object= angular.fromJson('${previousDegreeInformationFormJson}');
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.isUndefinedOrNull = function(val) {
        return angular.isUndefined(val) || val === null 
    }
    $scope.booleanvalues = [
                            { name : '<spring:message code="label.no"/>', value : false },
                            { name : '<spring:message code="label.yes"/>', value : true } 
                    ];
    $scope.onInstitutionRefresh = function(institution, namePart, model) {
        if(namePart.length <= 3 || namePart === $scope.object.institutionNamePart) {
            return;
        }
        $scope.object.institutionNamePart = namePart;
        $scope.$apply();  
        $scope.postBack(model);
    };
    $scope.onDegreeDesignationRefresh = function(degreeDesignation, namePart, model) {
        if(namePart.length <= 3 || namePart === $scope.object.degreeNamePart) {
            return;
        }
        $scope.object.degreeNamePart = namePart;
        $scope.$apply();  
        $scope.postBack(model);
    };
    $scope.onCountryChange = function(country, model) {
        $scope.object.countriesValues = undefined;
        $scope.object.districtsValues = undefined;
        $scope.object.districtSubdivisionValues = undefined;
        
        $scope.object.permanentResidenceDistrict = undefined;
        $scope.object.permanentResidentDistrictSubdivision = undefined;
        
        $scope.postBack(model);
    };
    $scope.onSchoolLevelChange = function(schoolLevel, model) {
        $scope.object.precedentInstitutionOid = undefined;
        $scope.object.raidesPrecedentDegreeDesignation = undefined;

        $scope.object.institutionValues = undefined;
        $scope.object.raidesDegreeDesignationValues = undefined;
    };
    $scope.onInstitutionChange = function(institution, model) {
        $scope.object.raidesDegreeDesignationValues = undefined;

        $scope.object.raidesPrecedentDegreeDesignation = undefined;
        $scope.postBack(model);
    };
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
                <div class="col-sm-2 control-label required-field">
                    <spring:message
                        code="label.PreviousDegreeInformationForm.precedentCountry" />
                </div>
            
                <div class="col-sm-10">
                    <ui-select  id="previousDegreeInformationForm_precedentCountry" name="precedentCountry" ng-model="$parent.object.precedentCountry" on-select="onCountryChange($item,$model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="country.id as country in object.countriesValues | filter: $select.search">
                            <span ng-bind-html="country.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select> 
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label required-field">
                    <spring:message code="label.PreviousDegreeInformationForm.precedentSchoolLevel" />
                </div>
            
                <div class="col-sm-4">
                    <ui-select  id="previousDegreeInformationForm_precedentSchoolLevel" name="precedentSchoolLevel" ng-model="$parent.object.precedentSchoolLevel" on-select="onSchoolLevelChange($item,$model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="schoolLevel.id as schoolLevel in object.schoolLevelValues | filter: $select.search">
                            <span ng-bind-html="schoolLevel.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>
                </div>
            </div>
            
            <div class="form-group row" ng-show="object.precedentSchoolLevel === 'OTHER'">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.PreviousDegreeInformationForm.otherPrecedentSchoolLevel" />
                </div>
            
                <div class="col-sm-10">
                    <input id="previousDegreeInformationForm_otherPrecedentSchoolLevel"
                        class="form-control" type="text" ng-model="object.otherPrecedentSchoolLevel" name="otherPrecedentSchoolLevel"
                        value='<c:out value='${previousDegreeInformationForm.otherPrecedentSchoolLevel}'/>' />
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label required-field">
                    <spring:message code="label.PreviousDegreeInformationForm.precedentInstitution" />
                </div>
            
                <div class="col-sm-10">
                    <ui-select  id="previousDegreeInformationForm_precedentInstitution" name="precedentInstitutionOid" ng-model="$parent.object.precedentInstitutionOid" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="institution.id as institution in object.institutionValues"
                                            refresh="onInstitutionRefresh($item, $select.search, $model)"
                                            refresh-delay="0">
                            <span ng-bind-html="institution.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>                 
                </div>
            </div>
            <div class="form-group row" ng-hide="isUndefinedOrNull(object.precedentInstitutionOid) || object.raidesDegreeDesignationValues.length">
                <div class="col-sm-2 control-label required-field">
                    <spring:message
                        code="label.PreviousDegreeInformationForm.precedentDegreeDesignation" />
                </div>
            
                <div class="col-sm-10">
                    <input id="previousDegreeInformationForm_precedentDegreeDesignation"
                        class="form-control" type="text" ng-model="object.precedentDegreeDesignation" name="precedentDegreeDesignation"
                        value='<c:out value='${not empty param.precedentDegreeDesignation ? param.precedentDegreeDesignation : previousDegreeInformationForm.precedentDegreeDesignation }'/>' />
                </div>
            </div>
            <div class="form-group row" ng-show="isUndefinedOrNull(object.precedentInstitutionOid) || object.raidesDegreeDesignationValues.length">
                <div class="col-sm-2 control-label required-field">
                    <spring:message
                        code="label.PreviousDegreeInformationForm.raidesPrecedentDegreeDesignation" />
                </div>
            
                <div class="col-sm-10">
                    <ui-select  id="previousDegreeInformationForm_raidesPrecedentDegreeDesignation" name="raidesPrecedentDegreeDesignation" ng-model="$parent.object.raidesPrecedentDegreeDesignation" ng-disabled="isUndefinedOrNull(object.precedentInstitutionOid)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="raidesDegreeDesignation.id as raidesDegreeDesignation in object.raidesDegreeDesignationValues | filter: $select.search"
                                            refresh="onDegreeDesignationRefresh($item, $select.search, $model)"
                                            refresh-delay="0">
                            <span ng-bind-html="raidesDegreeDesignation.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label required-field">
                    <spring:message code="label.PreviousDegreeInformationForm.numberOfEnrolmentsInPreviousDegrees" />
                </div>
            
                <div class="col-sm-10">
                    <input id="previousDegreeInformationForm_numberOfEnrolmentsInPreviousDegrees"
                        class="form-control" type="text" ng-model="object.numberOfEnrolmentsInPreviousDegrees" name="numberOfEnrolmentsInPreviousDegrees" required pattern="\d+" title="<spring:message code="error.PreviousDegreeInformationForm.numberOfEnrolmentsInPreviousDegrees.required"/>"
                        value='<c:out value='${previousDegreeInformationForm.numberOfEnrolmentsInPreviousDegrees}'/>' />
                </div>
            </div>
		</div>
		<div class="panel-footer">
            <button type="button" class="btn btn-primary" role="button" ng-click="submitForm()"><spring:message code="label.submit" /></button>
		</div>
	</div>
</form>

<style>
	.required-field:after {
		content: '*';
		color: #e06565;
		font-weight: 900;
		margin-left: 2px;
		font-size: 14px;
		display: inline;
	}
</style>

<script>
    $(document).ready(function() {
    
    });
    
</script>
