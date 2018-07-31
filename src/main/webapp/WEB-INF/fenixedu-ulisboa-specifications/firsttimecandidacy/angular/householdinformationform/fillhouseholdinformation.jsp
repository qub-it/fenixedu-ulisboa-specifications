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
	<h1><spring:message code="label.firstTimeCandidacy.fillHouseHoldInformation" />
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

<style>
#formTable {
    border-collapse: collapse;
    width: 100%;
}

#formTable td, #formTable th {
    border: 1px solid #ddd;
    padding: 8px;
}

#formTable tr:nth-child(even){background-color: #f2f2f2;}

#formTable tr:hover {background-color: #ddd;}

#formTable th {
    padding-top: 12px;
    padding-bottom: 12px;
    text-align: left;
    color: white;
}
</style>

<script>
angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

    $scope.object= ${householdInformationFormJson};
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
            
            <div class="table-responsive">
            
                <table class="table table-striped table-bordered">
                    <tr>
                        <th class="col-sm-2"></th>
                        <th class="col-sm-5 text-bold">
                            <spring:message code="label.HouseholdInformationForm.mother" />
                        </th>
                        <th class="col-sm-5 text-bold">
                            <spring:message code="label.HouseholdInformationForm.father" />
                        </th>
                    </tr>
                    <tr>
                        <td>
                            <div class="control-label required-field">
                                <spring:message code="label.HouseholdInformationForm.bothSchoolLevel" />
                            </div>
                        </td>
                        <td>
                            <ui-select  id="householdInformationForm_motherSchoolLevel" name="motherSchoolLevel" ng-model="$parent.object.motherSchoolLevel" theme="bootstrap">
                                <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                                <ui-select-choices  repeat="motherSchoolLevel.id as motherSchoolLevel in object.schoolLevelValues | filter: {normalizedText : $select.search}">
                                    <span ng-bind-html="motherSchoolLevel.text"></span>
                                </ui-select-choices> 
                            </ui-select>
                        </td>
                        <td>
                            <ui-select  id="householdInformationForm_fatherSchoolLevel" name="fatherSchoolLevel" ng-model="$parent.object.fatherSchoolLevel" theme="bootstrap">
                                <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                                <ui-select-choices  repeat="fatherSchoolLevel.id as fatherSchoolLevel in object.schoolLevelValues | filter: {normalizedText : $select.search}">
                                    <span ng-bind-html="fatherSchoolLevel.text"></span>
                                </ui-select-choices> 
                            </ui-select> 
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="control-label required-field">
                                <spring:message code="label.HouseholdInformationForm.bothProfessionalCondition" />
                            </div>
                        </td>
                        <td>
                            <ui-select  id="householdInformationForm_motherProfessionalCondition" name="motherProfessionalCondition" ng-model="$parent.object.motherProfessionalCondition" theme="bootstrap">
                                <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                                <ui-select-choices  repeat="motherProfessionalCondition.id as motherProfessionalCondition in object.professionalConditionValues | filter: {normalizedText : $select.search}">
                                    <span ng-bind-html="motherProfessionalCondition.text"></span>
                                </ui-select-choices> 
                            </ui-select> 
                        </td>
                        <td>
                            <ui-select  id="householdInformationForm_fatherProfessionalCondition" name="fatherProfessionalCondition" ng-model="$parent.object.fatherProfessionalCondition" theme="bootstrap">
                                <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                                <ui-select-choices  repeat="fatherProfessionalCondition.id as fatherProfessionalCondition in object.professionalConditionValues | filter: {normalizedText : $select.search}">
                                    <span ng-bind-html="fatherProfessionalCondition.text"></span>
                                </ui-select-choices> 
                            </ui-select>                 
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="control-label required-field">
                                <spring:message code="label.HouseholdInformationForm.bothProfessionType" />
                            </div>
                        </td>
                        <td>
                            <ui-select  id="householdInformationForm_motherProfessionType" name="motherProfessionType" ng-model="$parent.object.motherProfessionType" theme="bootstrap">
                                <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                                <ui-select-choices  repeat="motherProfessionType.id as motherProfessionType in object.professionTypeValues | filter: {normalizedText : $select.search}">
                                    <span ng-bind-html="motherProfessionType.text"></span>
                                </ui-select-choices> 
                            </ui-select> 
                        </td>
                        <td>
                            <ui-select  id="householdInformationForm_fatherProfessionType" name="fatherProfessionType" ng-model="$parent.object.fatherProfessionType" theme="bootstrap">
                                <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                                <ui-select-choices  repeat="fatherProfessionType.id as fatherProfessionType in object.professionTypeValues | filter: {normalizedText : $select.search}">
                                    <span ng-bind-html="fatherProfessionType.text"></span>
                                </ui-select-choices> 
                            </ui-select> 
                        </td>
                    </tr>
                </table>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.HouseholdInformationForm.householdSalarySpan" />
                </div>

                <div class="col-sm-6">
                    <ui-select  id="householdInformationForm_householdSalarySpan" name="householdSalarySpan" ng-model="$parent.object.householdSalarySpan" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="householdSalarySpan.id as householdSalarySpan in object.salarySpanValues | filter: {normalizedText : $select.search}">
                            <span ng-bind-html="householdSalarySpan.text"></span>
                        </ui-select-choices> 
                    </ui-select> 
                </div>
            </div>
        </div>
    </div>
    <c:if test="${ showDislocated }">
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="form-group row">
                    <div class="col-sm-2 control-label required-field">
                        <spring:message
                            code="label.ResidenceInformationForm.dislocatedFromPermanentResidence" />
                    </div>
    
                    <div class="col-sm-6">
                        <ui-select id="householdInformationForm_dislocatedFromPermanentResidence" name="dislocatedFromPermanentResidence"
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
            </div>
        </div>
    </c:if>            

    <div class="panel panel-default">
        <div class="panel-body">

            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.HouseholdInformationForm.livesAlone" />
                </div>

                <div class="col-sm-6">
                    <ui-select id="householdInformationForm_livesAlone" name="livesAlone"
                        ng-model="$parent.object.livesAlone"
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
            
            <div class="form-group row" ng-show="object.livesAlone == false">
                <div class="col-sm-2 control-label required-field">
                    <spring:message
                        code="label.HouseholdInformationForm.livesWith" />
                </div>
                <div class="col-sm-6">
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <tr><td>
                                <input ng-model="object.livesWithParents" type="checkbox">
                                <spring:message code="label.HouseholdInformationForm.parents" />
                            </td></tr>
                            <tr><td>
                                <input ng-model="object.livesWithBrothers" type="checkbox">
                                <spring:message code="label.HouseholdInformationForm.brothers" />
                            </td></tr>
                            <tr><td>
                                <input ng-model="object.livesWithChildren" type="checkbox">
                                <spring:message code="label.HouseholdInformationForm.children" />
                            </td></tr>
                            <tr><td>
                                <input ng-model="object.livesWithLifemate" type="checkbox">
                                <spring:message code="label.HouseholdInformationForm.lifeMate" />
                            </td></tr>
                            <tr><td>
                                <input ng-model="object.livesWithOthers" type="checkbox">
                                <spring:message code="label.HouseholdInformationForm.other" />
                            </td></tr>
                        </table>
                    </div>
                </div>
           </div>
            
            
            <div class="form-group row" ng-show="object.livesAlone == false && object.livesWithOthers">
                <div class="col-sm-2 control-label required-field">
                    <spring:message
                        code="label.HouseholdInformationForm.livesWithOthersDesc" />
                </div>

                <div class="col-sm-6">
                    <input id="householdInformationForm_livesWithOthersDesc" class="form-control" type="text" ng-model="object.livesWithOthersDesc" name="field" />
                </div>
            </div>

            <div class="table-responsive">
            
                <table class="table table-striped table-bordered">
                    <tr>
                        <th class="col-sm-2"></th>
                        <th class="col-sm-5 text-bold">
                            <spring:message code="label.HouseholdInformationForm.brothers" />
                        </th>
                        <th class="col-sm-5 text-bold">
                            <spring:message code="label.HouseholdInformationForm.children" />
                        </th>
                    </tr>
                    <tr>
                        <td>
                            <div class="control-label">
                                <spring:message code="label.HouseholdInformationForm.numberBoth" />
                            </div>
                        </td>
                        <td>
                            <input id="householdInformationForm_numBrothers" class="form-control" type="number" ng-model="object.numBrothers" name="field" />
                        </td>
                        <td>
                            <input id="householdInformationForm_numChildren" class="form-control" type="number" ng-model="object.numChildren" name="field" />
                        </td>
                    </tr>
                </table>
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

