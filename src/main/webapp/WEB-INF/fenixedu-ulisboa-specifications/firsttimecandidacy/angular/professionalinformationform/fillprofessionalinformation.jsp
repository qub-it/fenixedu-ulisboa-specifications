<%@page import="org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.HouseholdInformationFormController"%>
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
	<h1><spring:message code="label.firstTimeCandidacy.fillProfessionalInformation" />
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

    $scope.object= ${professionalInformationFormJson};
    $scope.postBack = createAngularPostbackFunction($scope);
    $scope.isUISelectLoading = {};
    $scope.getUISelectLoading = function() {
	    if($scope.isUISelectLoading == undefined) {
    		$scope.isUISelectLoading = {};		
	    }
	    return $scope.isUISelectLoading;
    };
    
    $scope.booleanvalues = [
                            { name : '<spring:message code="label.no"/>', value : false },
                            { name : '<spring:message code="label.yes"/>', value : true } 
                    ];
    $scope.transformData = function () {
    };
    $scope.typpingMessage = "<spring:message code='label.startTyping'/>";
    
    $scope.onChange = function(element, model) {
        $scope.postBack(model);
    };
    
    $scope.isProfessionEmpty = function() {
        return angular.isUndefined($scope.object.profession) || $scope.object.profession === null || $scope.object.profession.length == 0;
    }
    
    $scope.getProfessionClass = function() {
    	if($scope.object.professionRequired) {
    		return "required-field";
    	}
    	
    	return "";
    }
    
    $scope.getProfessionTimeTypeClass = function() {
    	if(!$scope.isProfessionEmpty() || $scope.object.professionRequired) {
    		return "required-field";
    	}
    	
		return "";
    }
    
    $scope.submitForm = function() {
	    $scope.transformData();
        $('form').submit();
    };

}]);
</script>

<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
     action="${pageContext.request.contextPath}${controllerURL}/${postAction}">

    <input type="hidden" name="postback"
        value='${pageContext.request.contextPath}${controllerURL}/fillPostback' />
        
    <input name="bean" type="hidden" value="{{ object }}" />
		
    <div class="panel panel-default">
        <div class="panel-body">
            <div class="form-group row">
                <label for="professionalInformationForm_professionalCondition" class="col-sm-2 control-label required-field">
                    <spring:message code="label.ProfessionalInformationForm.professionalCondition" />
                </label>

                <div class="col-sm-6">
                    <ui-select  id="professionalInformationForm_professionalCondition" name="professionalCondition" ng-model="$parent.object.professionalCondition" on-select="onChange($item,$model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="professionalCondition.id as professionalCondition in object.professionalConditionValues | filter: {normalizedText : $select.search}">
                            <span ng-bind-html="professionalCondition.text"></span>
                        </ui-select-choices> 
                    </ui-select>
                </div>
            </div>
            <div class="form-group row">
                <label for="professionalInformationForm_professionType" class="col-sm-2 control-label required-field">
                    <spring:message code="label.ProfessionalInformationForm.professionType" />
                </label>

                <div class="col-sm-6">
                    <ui-select  id="professionalInformationForm_professionType" name="professionType" ng-model="$parent.object.professionType" on-select="onChange($item,$model)" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="professionType.id as professionType in object.professionTypeValues | filter: {normalizedText : $select.search}">
                            <span ng-bind-html="professionType.text"></span>
                        </ui-select-choices> 
                    </ui-select>                
                </div>
            </div>          
            <div class="form-group row">
                <label for="professionalInformationForm_profession" class="col-sm-2 control-label" ng-class="getProfessionClass()">
                    <spring:message code="label.ProfessionalInformationForm.profession" />
                </label>

                <div class="col-sm-6">
                    <input id="professionalInformationForm_profession" class="form-control"
                        type="text" ng-model="object.profession" name="profession"
                        value='<c:out value='${not empty param.profession ? param.profession : professionalInformationForm.profession }'/>' />
                </div>
            </div>
            <div class="form-group row">
                <label for="professionalInformationForm_professionTimeType" class="col-sm-2 control-label" ng-class="getProfessionTimeTypeClass()">
                    <spring:message code="label.ProfessionalInformationForm.professionTimeType" />
                </label>

                <div class="col-sm-6">
                    <ui-select  id="professionalInformationForm_professionTimeType" name="professionTimeType" ng-model="$parent.object.professionTimeType" theme="bootstrap">
                        <ui-select-match allow-clear="true" >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="professionTimeType.id as professionTimeType in object.professionTimeTypeValues | filter: {normalizedText : $select.search}">
                            <span ng-bind-html="professionTimeType.text"></span>
                        </ui-select-choices> 
                    </ui-select>                
                </div>
            </div>
        </div>
    </div>
    
    <div class="panel panel-default">
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

