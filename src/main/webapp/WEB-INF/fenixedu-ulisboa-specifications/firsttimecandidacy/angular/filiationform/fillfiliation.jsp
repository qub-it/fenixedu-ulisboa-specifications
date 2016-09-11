<%@page import="org.fenixedu.academic.domain.Country"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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
	<h1><spring:message code="label.firstTimeCandidacy.fillFiliation" />
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

    $scope.object= ${filiationFormJson};
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
                            { name : '<spring:message code="label.no"/>', value : false },
                            { name : '<spring:message code="label.yes"/>', value : true } 
                    ];
    
    $scope.onCountryChange = function(country, model) {
	    $scope.object.nationalitiesValues = undefined;
        $scope.object.countriesValues = undefined;
        $scope.object.districtsValues = undefined;
        $scope.object.districtSubdivisionValues = undefined;
        $scope.object.parishValues = undefined;
	    
        $scope.object.districtOfBirth = undefined;
        $scope.object.districtSubdivisionOfBirth = undefined;
        $scope.object.parishOfBirth = undefined;
        
        $scope.postBack(model);
    };
    $scope.onDistrictChange = function(district, model) {
        $scope.object.nationalitiesValues = undefined;
        $scope.object.countriesValues = undefined;
        $scope.object.districtsValues = undefined;
        $scope.object.districtSubdivisionValues = undefined;
        $scope.object.parishValues = undefined;
        
        $scope.object.districtSubdivisionOfBirth = undefined;
        $scope.object.parishOfBirth = undefined;
        
        $scope.postBack(model);
    };
    $scope.onDistrictSubdivisionChange = function(districtSubdivision, model) {
        $scope.object.nationalitiesValues = undefined;
        $scope.object.countriesValues = undefined;
        $scope.object.districtsValues = undefined;
        $scope.object.districtSubdivisionValues = undefined;
        $scope.object.parishValues = undefined;
        
        $scope.object.parishOfBirth = undefined;
        
        $scope.postBack(model);
    };
    $scope.getNationalityName = function(id) {
        var name;
        angular.forEach(
                $scope.object.nationalitiesValues,
                function(nationality) {
                    if (nationality.id == id) {
                        name = nationality.text;
                    }
                }, id, name)
        return name;
    }
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
					<spring:message code="label.FiliationForm.countryOfBirth" />
				</div>

				<div class="col-sm-10">
                    <ui-select  id="filiationForm_countryOfBirth" name="countryOfBirth" ng-model="$parent.object.countryOfBirth" on-select="onCountryChange($item,$model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="country.id as country in object.countriesValues | filter: $select.search">
                            <span ng-bind-html="country.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label" ng-class="{'required-field' : object.districtsValues.length}" id="labelDistrictOfBirth">
					<spring:message code="label.FiliationForm.districtOfBirth" />
				</div>

				<div class="col-sm-10">
                    <ui-select  id="filiationForm_districtOfBirth" name="districtOfBirth" ng-model="$parent.object.districtOfBirth" on-select="onDistrictChange($item,$model)" ng-disabled="!object.districtsValues.length" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="district.id as district in object.districtsValues | filter: $select.search">
                            <span ng-bind-html="district.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label" ng-class="{'required-field' : object.districtSubdivisionValues.length }" id="labelDistrictSubdivisionOfBirth">
					<spring:message
						code="label.FiliationForm.districtSubdivisionOfBirth" />
				</div>

				<div class="col-sm-10">
                    <ui-select  id="filiationForm_districtSubdivisionOfBirth" name="districtSubdivisionOfBirth" ng-model="$parent.object.districtSubdivisionOfBirth" on-select="onDistrictSubdivisionChange($item,$model)" ng-disabled="!object.districtSubdivisionValues.length" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="districtSubdivision.id as districtSubdivision in object.districtSubdivisionValues | filter: $select.search">
                            <span ng-bind-html="districtSubdivision.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label" ng-class="{'required-field' : object.parishValues.length }" id="labelParishOfBirth">
					<spring:message code="label.FiliationForm.parishOfBirth" />
				</div>

				<div class="col-sm-10">
                    <ui-select  id="filiationForm_parishOfBirth" name="parishOfBirth" ng-model="$parent.object.parishOfBirth" ng-disabled="!object.parishValues.length" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="parish.id as parish in object.parishValues | filter: $select.search">
                            <span ng-bind-html="parish.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>
				</div>
			</div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.FiliationForm.nationality" />
                </div>

                <div class="col-sm-10">
                    <div class="form-control-static">{{ getNationalityName(object.countryOfBirth) }}</div>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.FiliationForm.secondNationality" />
                </div>

                <div class="col-sm-10">
                    <ui-select  id="filiationForm_secondNationality" name="secondNationality" ng-model="$parent.object.secondNationality" theme="bootstrap">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="nationality.id as nationality in object.nationalitiesValues | filter: $select.search">
                            <span ng-bind-html="nationality.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select> 
                </div>
            </div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.FiliationForm.fatherName" />
				</div>

				<div class="col-sm-10">
					<input id="filiationForm_fatherName" class="form-control"
						type="text" ng-model="object.fatherName" name="fatherName" required title="<spring:message code="label.field.required"/>"
						value='<c:out value='${not empty param.fathername ? param.fathername : filiationForm.fatherName }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.FiliationForm.motherName" />
				</div>

				<div class="col-sm-10">
					<input id="filiationForm_motherName" class="form-control"
						type="text" ng-model="object.motherName" name="motherName" required title="<spring:message code="label.field.required"/>"
						value='<c:out value='${not empty param.mothername ? param.mothername : filiationForm.motherName }'/>' />
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