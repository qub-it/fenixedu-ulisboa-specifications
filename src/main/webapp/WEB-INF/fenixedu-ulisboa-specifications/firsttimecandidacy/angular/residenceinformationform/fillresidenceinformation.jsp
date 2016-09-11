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
	<h1><spring:message code="label.firstTimeCandidacy.fillResidenceInformation" />
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

    $scope.object= ${residenceInformationFormJson};
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
                            { name : '<spring:message code="label.no"/>', value : false },
                            { name : '<spring:message code="label.yes"/>', value : true } 
                    ];
    $scope.defaultCountry = '<%= Country.readDefault().getExternalId() %>';
    $scope.onAreaCodeRefresh = function(areaCode, namePart, model) {
        if(namePart.length <= 3 || namePart === $scope.object.areaCodePart) {
            $scope.object.areaCodeValues = undefined;
            return;
        }
        $scope.object.areaCodePart = namePart;
        $scope.$apply();  
        $scope.postBack(model);
    };
    $scope.onSchoolTimeAreaCodeRefresh = function(areaCode, namePart, model) {
        if(namePart.length <= 3 || namePart === $scope.object.schoolTimeAreaCodePart) {
            $scope.object.schoolTimeAreaCodeValues = undefined;
            return;
        }
        $scope.object.schoolTimeAreaCodePart = namePart;
        $scope.$apply();  
        $scope.postBack(model);
    };
    $scope.onCountryChange = function(country, model) {
        $scope.object.countriesValues = undefined;
        $scope.object.areaCodeValues = undefined;
        
        $scope.object.areaCode = undefined;
        $scope.object.areaCodePart = undefined;
        
        $scope.postBack(model);
    };
    $scope.onAreaCodeChange = function (areaCode, model) {
	    $scope.postBack(model);	
    }
    $scope.onResidenceTypeChange = function(residenceType, model) {
	    if($scope.object.schoolTimeResidenceType == null) {
		    return;
	    }

	    $scope.postBack(model);
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
					<spring:message
						code="label.ResidenceInformationForm.countryOfResidence" />
				</div>

				<div class="col-sm-10">
                    <ui-select  id="residenceInformationForm_countryOfResidence" name="countryOfResidence" ng-model="$parent.object.countryOfResidence" on-select="onCountryChange($item,$model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="country.id as country in object.countriesValues | filter: $select.search">
                            <span ng-bind-html="country.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>    
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.ResidenceInformationForm.address" />
				</div>

				<div class="col-sm-10">
					<input id="residenceInformationForm_address" class="form-control"
						type="text" ng-model="object.address" name="address"
						value='<c:out value='${not empty param.address ? param.address : residenceInformationForm.address }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field" id="labelArea">
					<spring:message code="label.ResidenceInformationForm.area" />
				</div>

				<div class="col-sm-10">
					<input id="residenceInformationForm_area" class="form-control"
						type="text" ng-model="object.area" name="area"
						value='<c:out value='${not empty param.area ? param.area : residenceInformationForm.area }'/>' />
				</div>
			</div>
			<div class="form-group row" ng-show="object.countryOfResidence === defaultCountry">
				<div class="col-sm-2 control-label" ng-class="{'required-field': object.districtsValues.length}"  id="labelAreaCode">
					<spring:message code="label.ResidenceInformationForm.areaCode" />
				</div>

				<div class="col-sm-10">
                    <ui-select  id="residenceInformationForm_areaCode" name="areaCode" ng-model="$parent.object.areaCode" on-select="onAreaCodeChange($item,$model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="areaCode.id as areaCode in object.areaCodeValues | filter: $select.search"
                                            refresh="onAreaCodeRefresh($item, $select.search, $model)"
                                            refresh-delay="0">
                            <span ng-bind-html="areaCode.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>    
				</div>
			</div>
			<div class="form-group row" ng-show="object.countryOfResidence === defaultCountry">
				<div class="col-sm-2 control-label" ng-class="{'required-field': object.districtsValues.length}" id="labelDistrictOfResidence">
					<spring:message
						code="label.ResidenceInformationForm.districtOfResidence" />
				</div>

				<div class="col-sm-10">
                    <div class="form-control-static">{{ object.districtOfResidenceName }}</div>
				</div>
			</div>
			<div class="form-group row" ng-show="object.countryOfResidence === defaultCountry">
				<div class="col-sm-2 control-label" ng-class="{'required-field': object.districtSubdivisionValues.length}" id="labelDistrictSubdivisionOfResidence">
					<spring:message
						code="label.ResidenceInformationForm.districtSubdivisionOfResidence" />
				</div>

				<div class="col-sm-10">
                    <div class="form-control-static">{{ object.districtSubdivisionOfResidenceName }}</div>
				</div>
			</div>
			<div class="form-group row" ng-show="object.countryOfResidence === defaultCountry">
				<div class="col-sm-2 control-label" ng-class="{'required-field': object.parishValues.length}" id="labelParishOfResidence">
					<spring:message
						code="label.ResidenceInformationForm.parishOfResidence" />
				</div>

				<div class="col-sm-10">
                    <div class="form-control-static">{{ object.parishOfResidenceName }}</div>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.ResidenceInformationForm.dislocatedFromPermanentResidence" />
				</div>

				<div class="col-sm-2">
                    <ui-select id="residenceInformationForm_dislocatedFromPermanentResidence" name="dislocatedFromPermanentResidence"
                        ng-model="$parent.object.dislocatedFromPermanentResidence"
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
			<div class="form-group row" ng-show="object.dislocatedFromPermanentResidence">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.ResidenceInformationForm.schoolTimeAddress" />
				</div>

				<div class="col-sm-10">
					<input id="residenceInformationForm_schoolTimeAddress"
						class="form-control" ng-model="object.schoolTimeAddress" type="text" name="schoolTimeAddress"
						value='<c:out value='${not empty param.schooltimeaddress ? param.schooltimeaddress : residenceInformationForm.schoolTimeAddress }'/>' />
				</div>
			</div>
			<div class="form-group row" ng-show="object.dislocatedFromPermanentResidence">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.ResidenceInformationForm.schoolTimeArea" />
				</div>

				<div class="col-sm-10">
					<input id="residenceInformationForm_schoolTimeArea"
						class="form-control" ng-model="object.schoolTimeArea" type="text" name="schoolTimeArea"
						value='<c:out value='${not empty param.schooltimearea ? param.schooltimearea : residenceInformationForm.schoolTimeArea }'/>' />
				</div>
			</div>
			<div class="form-group row" ng-show="object.dislocatedFromPermanentResidence">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.ResidenceInformationForm.schoolTimeAreaCode" />
				</div>

				<div class="col-sm-10">
                    <ui-select  id="residenceInformationForm_schoolTimeAreaCode" name="schoolTimeAreaCode" ng-model="$parent.object.schoolTimeAreaCode" on-select="onAreaCodeChange($item, $model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="areaCode.id as areaCode in object.schoolTimeAreaCodeValues | filter: $select.search"
                                            refresh="onSchoolTimeAreaCodeRefresh($item, $select.search, $model)"
                                            refresh-delay="0">
                            <span ng-bind-html="areaCode.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select> 
				</div>
			</div>
			<div class="form-group row" ng-show="object.dislocatedFromPermanentResidence">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.ResidenceInformationForm.schoolTimeDistrictOfResidence" />
				</div>

				<div class="col-sm-10">
                    <div class="form-control-static">{{ object.schoolTimeDistrictOfResidenceName }}</div>
				</div>
			</div>
			<div class="form-group row" ng-show="object.dislocatedFromPermanentResidence">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.ResidenceInformationForm.schoolTimeDistrictSubdivisionOfResidence" />
				</div>

				<div class="col-sm-10">
                    <div class="form-control-static">{{ object.schoolTimeDistrictSubdivisionOfResidenceName }}</div>
				</div>
			</div>
			<div class="form-group row" ng-show="object.dislocatedFromPermanentResidence">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.ResidenceInformationForm.schoolTimeParishOfResidence" />
				</div>

				<div class="col-sm-10">
                    <div class="form-control-static">{{ object.schoolTimeParishOfResidenceName }}</div>
				</div>
			</div>
			<div class="form-group row" ng-show="object.dislocatedFromPermanentResidence">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.ResidenceInformationForm.schoolTimeResidenceType" />
				</div>

				<div class="col-sm-4">
                    <ui-select  id="residenceInformationForm_schoolTimeResidenceType" name="schoolTimeResidenceType" ng-model="$parent.object.schoolTimeResidenceType" on-select="onResidenceTypeChange($item, $model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="dislocatedResidenceType.id as dislocatedResidenceType in object.residenceTypeValues | filter: $select.search">
                            <span ng-bind-html="dislocatedResidenceType.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select> 
				</div>
			</div>
			<div class="form-group row" ng-show="object.dislocatedFromPermanentResidence && object.isOtherResidenceType">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.ResidenceInformationForm.otherSchoolTimeResidenceType" />
				</div>

				<div class="col-sm-10">
					<input id="residenceInformationForm_otherSchoolTimeResidenceType" class="form-control" type="text" ng-model="object.otherSchoolTimeResidenceType" name="otherSchoolTimeResidenceType"
						value='<c:out value='${not empty param.otherSchoolTimeResidenceType ? param.otherSchoolTimeResidenceType : residenceInformationForm.otherSchoolTimeResidenceType }'/>' />
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

