<%@page import="org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.importation.DgesImportationProcessController"%>
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
        <spring:message code="label.firstYearConfiguration.searchFirstYearRegistrationConfiguration" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= DgesImportationProcessController.SEARCH_URL %>">
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
angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null;
};
angular.isStringEmpty = function(val) {
	return (!val || 0 === val.length);
};
angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

    $scope.object= ${dgesImportProcessConfigurationBeanJson};
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
            { name : '<spring:message code="label.no"/>', value : false },
            { name : '<spring:message code="label.yes"/>', value : true } 
    ];

    $scope.edit = {};
    $scope.edit.details = false;
    $scope.edit.contingentMappings = true;
    $scope.edit.degreeMappings = false;
    $scope.data = {};
    

    $scope.updateTextAreaAttr = function() {
    	var readOnly = !$scope.edit.details;
        $('#introductionText textarea').attr("readonly", readOnly);    	
    };
    
    //Details
    $scope.getRegistrationProtocolName = function(registrationProtocolId) {
    	var result = "";
        angular.forEach($scope.object.registrationProtocolDataSource, function(element, index) {
            if(element.id == registrationProtocolId) {
                result = element.text;
            }
        });
        return result;
    };
    //Contingent Mapping
    $scope.getIngressionTypeName = function(ingressionTypeId) {
        var ingressionTypeName = "";
        
        angular.forEach($scope.object.ingressionTypeDataSource, function(element, index) {
            if(element.id == ingressionTypeId) {
                ingressionTypeName = element.text;
            }
        });
        
        return ingressionTypeName;
    };
    $scope.addMapping = function(model) {
        if (angular.isUndefinedOrNull($scope.data.ingressionType)) {
            return;
        }
        if (angular.isUndefinedOrNull($scope.data.contingent) || angular.isStringEmpty($scope.data.contingent)){
        	return;
        }
        
        url = '${pageContext.request.contextPath}<%= DgesImportationProcessController.MANAGE_CONTINGENT_MAPPING_URL %>';
        $('form[name="form"]').find('input[name="postback"]').attr('value', url);
        $scope.postBack(model);   
        
        $scope.data.ingressionType = undefined;
        $scope.data.contingent = undefined;
    };
    $scope.deleteMapping = function(mapping, model) {
        url = '${pageContext.request.contextPath}<%= DgesImportationProcessController.MANAGE_CONTINGENT_MAPPING_URL%>';
        $('form[name="form"]').find('input[name="postback"]').attr('value', url);
        $scope.data.contingentMappingId = mapping;
        $('p[id="deleteModal_message"]').text('<spring:message code="label.dgesImportationProcess.contingentMapping.confirmDelete" />');
        $('#deleteModal').modal('toggle');
    }
    $scope.submitDeleteMapping = function() {
        $('#deleteModal').modal('toggle');                  
        $scope.postBack(null);                 
    };
    //Degree Mapping
    $scope.getBooleanLabel = function(booleanValue) {
    	var result = "";
    	angular.forEach($scope.booleanvalues, function(element,index) {
        	if(booleanValue == element.value) {
        		result = element.name;
        	}
    	});
    	return result;
    }
    $scope.getDegreeCurricularPlanName = function(mappingObject, degreeCurricularPlanId) {
        var degreeCurricularPlanName = "";
        
        angular.forEach(mappingObject.degreeCurricularPlanDataSource, function(element, index) {
            if(element.id == mappingObject.degreeCurricularPlan) {
            	degreeCurricularPlanName = element.text;
            }
        });
        
        return degreeCurricularPlanName;
    };
    $scope.addDegree = function(model) {
        if (angular.isUndefinedOrNull($scope.data.degree)) {
            return;
        }

        $scope.data.requiresVaccination = false;

        url = '${pageContext.request.contextPath}<%= DgesImportationProcessController.MANAGE_DEGREE_CONFIGURATION_URL %>';
        $('form[name="form"]').find('input[name="postback"]').attr('value', url);
        $scope.postBack(model);   
        
        $scope.data.degree = undefined;
        $scope.data.degreeCurricularPlan = undefined;
    };
    $scope.submitDegreeMapping = function(slotEntry, model) {
        url = '${pageContext.request.contextPath}<%= DgesImportationProcessController.MANAGE_DEGREE_CONFIGURATION_URL %>';
        $scope.data.degree = slotEntry.degree;
        $scope.data.requiresVaccination = slotEntry.requiresVaccination;
        $scope.data.degreeCurricularPlan = slotEntry.degreeCurricularPlan;
        $('form[name="form"]').find('input[name="postback"]').attr('value', url);
        $scope.postBack(model);   
        
        slotEntry.editing=false;
        $scope.data.degree = undefined;
        $scope.data.requiresVaccination = false;
        $scope.data.degreeCurricularPlan = undefined;
    };
    $scope.deleteDegreeMapping = function(mapping, model) {
        url = '${pageContext.request.contextPath}<%= DgesImportationProcessController.DELETE_DEGREE_CONFIGURATION_URL%>';
        $('form[name="form"]').find('input[name="postback"]').attr('value', url);
        $scope.data.configuration = mapping;
        $('p[id="deleteModal_message"]').text('<spring:message code="label.dgesImportationProcess.degreeMapping.confirmDelete" />');
        $('#deleteModal').modal('toggle');
    }
    
    $scope.transformDataToSubmit = function () {
    	angular.forEach($scope.object.introductionText, function(element, key) {
    	    $scope.object.introductionText.key = JSON.stringify(element);
    	});
    	
    	$scope.object.introductionText = JSON.stringify($scope.object.introductionText);
    }
    $scope.submitForm = function (event) {
    	
        $scope.transformDataToSubmit();
        
        $('form[name="form"]').submit();
    }
}]);
</script>

<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController" enctype="multipart/form-data"
    action="${pageContext.request.contextPath}<%= DgesImportationProcessController.CONFIGURATION_UPDATE_URL %>">

    <input name="postback" type="hidden" value="#" />
    <input name="bean" type="hidden" value="{{ object }}" />
    
    <input name="ingressionType" type="hidden" value="{{ data.ingressionType }}" />
    <input name="contingentMappingId" type="hidden" value="{{ data.contingentMappingId }}" />

    <input name="degree" type="hidden" value="{{ data.degree }}" />
    <input name="requiresVaccination" type="hidden" value="{{ data.requiresVaccination }}" />
    <input name="degreeCurricularPlan" type="hidden" value="{{ data.degreeCurricularPlan }}" />
    <input name="configuration" type="hidden" value="{{ data.configuration }}" />
    
    <div class="panel panel-default">
        <div class="panel-body">        
            <div class="panel panel-primary">
                <div class="panel-heading">
                   <h3 class="panel-title"><spring:message code="label.dgesImportation.base.information"/></h3>
                </div>
                
                <div class="panel-body">        
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.dges.importation.default.registrationProtocol" />
                        </div>
        
                        <div class="col-sm-10" ng-show="edit.details">
                            <ui-select  id="dgesImportationProcess_registrationProtocol" name="registrationProtocol" ng-model="$parent.object.defaultRegistrationProtocol" theme="bootstrap">
                                <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                                <ui-select-choices  repeat="type.id as type in object.registrationProtocolDataSource | filter: $select.search">
                                    <span ng-bind-html="type.text | highlight: $select.search"></span>
                                </ui-select-choices> 
                            </ui-select>
                        </div>
                        
                        <div class="col-sm-10" ng-hide="edit.details">
                            {{ getRegistrationProtocolName(object.defaultRegistrationProtocol) }}
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.FirstYearRegistrationConfiguration.introductionText" />
                        </div>
        
                        <div class="col-sm-10" id="introductionText" ng-mouseover="updateTextAreaAttr()">
                            <textarea type="text" ng-readonly="edit.details" name="introductionText" bennu-localized-string="object.introductionText" ></textarea>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.FirstYearRegistrationConfiguration.infoAcademicRequisitionText" />
                        </div>
        
                        <div class="col-sm-10" ng-show="edit.details">
                            <input id="infoAcademicRequisitionText" class="form-control" type="text" ng-model="object.infoAcademicRequisitionText" name="field" />
                        </div>
                        <div class="col-sm-10" ng-hide="edit.details">
                            <input id="infoAcademicRequisitionText" disabled class="form-control" type="text" ng-model="object.infoAcademicRequisitionText" name="field" />
                        </div>
                    </div>                    
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.FirstYearRegistrationConfiguration.redirectUrl" />
                        </div>
        
                        <div class="col-sm-10" ng-show="edit.details">
                            <input id="redirectUrl" class="form-control" type="text" ng-model="object.redirectUrl" name="field" />
                        </div>
                        <div class="col-sm-10" ng-hide="edit.details">
                            <input id="redirectUrl" disabled class="form-control" type="text" ng-model="object.redirectUrl" name="field" />
                        </div>
                    </div>  
                    <div class="form-group row">
                        <div class="col-sm-2 control-label"><spring:message code="label.FirstYearRegistrationConfiguration.mod43TemplateFile"/></div> 
        
                        <div class="col-sm-4" ng-show="edit.details">
                            <input type="file" name="cgdTemplateFile" id="cgdTemplateFile" accept="application/pdf" />
                        </div>
                        <div class="col-sm-4" ng-hide="edit.details">
                            <a href="${pageContext.request.contextPath}<%= DgesImportationProcessController.DOWNLOAD_CGD_URL %>/{{ object.cgdTemplateId }}">
                                {{ object.cgdTemplateName }}
                            </a>
                        </div>
                    </div>  
                </div>
                <div class="panel-footer" ng-hide="edit.details">
                    <a class="btn btn-primary" ng-click="edit.details=true;">
                        <span class="glyphicon glyphicon-edit" aria-hidden="true"></span> 
                        &nbsp;
                        <spring:message code="label.edit" />
                    </a>
                </div>
                <div class="panel-footer" ng-show="edit.details">
                    <a class="btn btn-primary" ng-click="edit.details=false;">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 
                        &nbsp;
                        <spring:message code="label.cancel" />
                    </a>
                    &nbsp;
                    <button type="button" class="btn btn-primary" role="button" ng-click="submitForm($event)">
                        <span class="glyphicon glyphicon-ok" aria-hidden="true"></span> 
                        &nbsp;
                        <spring:message code="label.submit" /> 
                    </button>
                </div>
            </div>
            <div class="panel panel-primary">
                <div class="panel-heading">
                   <h3 class="panel-title"><spring:message code="label.dges.importation.configuration.contingent.to.ingressions"/></h3>
                </div>
                
                <div class="panel-body">   
                    <div class="form-group row">
                        <div class="col-sm-1 control-label"><spring:message code="label.dges.importation.configuration.contingent"/></div> 
                        <div class="col-sm-3">
                            <input id="dgesImportationProcess_contingent" ng-disabled="!edit.contingentMappings" class="form-control" type="text" ng-model="data.contingent" 
                                   name="contingent" value='' />
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-1 control-label"><spring:message code="label.dges.importation.configuration.ingressionType"/></div> 
                        <div class="col-sm-3">
                            <ui-select id="dgesImportationProcess_ingressionTypes"
                                ng-model="$parent.data.ingressionType"
                                theme="bootstrap"> 
                                <ui-select-match allow-clear="true">
                                    {{$select.selected.text}}
                                </ui-select-match> 
                                <ui-select-choices repeat="element.id as element in object.ingressionTypeDataSource | filter: $select.search">
                                    <span ng-bind-html="element.text | highlight: $select.search"></span>
                                </ui-select-choices>
                            </ui-select>
                        </div>
                        <div class="col-sm-2">
                            <button type="button" class="btn btn-default" ng-click="addMapping($model)" ng-disabled="!edit.contingentMappings">
                                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                            </button>
                        </div>               
                    </div>
                </div>
                
                <div class="panel-body">
                    <table id="serviceRequestTypePropertiesTable"
                            class="table responsive table-bordered table-hover">
                        <thead>
                            <tr>
                                <th width="40%">
                                    <spring:message code="label.dges.importation.configuration.contingent" />
                                </th>
                                <th width="40%">
                                    <spring:message code="label.dges.importation.configuration.ingressionType" />
                                </th>
                                <!-- operation column -->
                                <th style="width: 10%"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat="mapping in object.contingentMappings">
                                <td>{{ mapping.contingent }}</td>
                                <td>{{ getIngressionTypeName(mapping.ingressionType) }}</td>
                                <td>
                                    <a ng-disabled="!edit.contingentMappings" class="btn btn-danger" ng-click="deleteMapping(mapping.contingentToIngression, $model)">
                                        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                        &nbsp;
                                        <spring:message code="label.remove" /> 
                                    </a>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="panel-footer">
                </div>
            </div>
            <div class="panel panel-primary">
                <div class="panel-heading">
                        <h3 class="panel-title"><spring:message code="label.FirstYearRegistrationConfiguration.configurationByDegree"/></h3>
                </div>
                
                <div class="panel panel-body">
                    <div class="form-group row">
                        <div class="col-sm-1 control-label"><spring:message code="label.HouseholdInformationForm.registration.degreeName"/></div> 
                        <div class="col-sm-5">
                            <ui-select id="dgesImportationProcess_degrees"
                                ng-model="$parent.data.degree"
                                theme="bootstrap"> 
                                <ui-select-match allow-clear="true">
                                    {{$select.selected.text}}
                                </ui-select-match> 
                                <ui-select-choices repeat="element.id as element in object.activeDegreeDataSource | filter: $select.search">
                                    <span ng-bind-html="element.text | highlight: $select.search"></span>
                                </ui-select-choices>
                            </ui-select>
                        </div>
                        <div class="col-sm-4">
                            <button type="button" class="btn btn-default" ng-click="addDegree($model)">
                                <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                            </button>
                        </div>               
                    </div>
                </div>
                
                <div class="panel-body">      
                    <table id="degreesConfigurationTable"
                        class="table responsive table-bordered table-hover" width="100%">
                        <thead>
                            <tr>
                                <th><spring:message
                                        code="label.FirstYearRegistrationConfiguration.degreeName" /></th>
                                <th style="width: 8%"><spring:message
                                        code="label.FirstYearRegistrationConfiguration.degreeMinistryCode" /></th>
                                <th style="width: 8%"><spring:message
                                        code="label.FirstYearRegistrationConfiguration.requiresVaccination" /></th>
                                <th style="width: 30%"><spring:message
                                        code="label.FirstYearRegistrationConfiguration.degreeCurricularPlan" /></th>
                                <!-- operation column -->
                                <th style="width: 15%"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat="slotEntry in object.activeDegrees">
                                <td>{{ slotEntry.label }}</td>
                                <td>{{ slotEntry.code }}</td>
                                <td>
                                    <ui-select id="dgesImportationProcess_requiresVaccination"
                                        ng-model="slotEntry.requiresVaccination" ng-show="slotEntry.editing"
                                        theme="bootstrap" > 
                                        <ui-select-match>
                                            {{$select.selected.name}}
                                        </ui-select-match> 
                                        <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                                            <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                                        </ui-select-choices>
                                    </ui-select>
                                    <span ng-hide="slotEntry.editing">
                                        {{ getBooleanLabel(slotEntry.requiresVaccination) }}
                                    </span>
                                </td>
                                <td>
                                    <ui-select id="dgesImportationProcess_degreeCurricularPlan"
                                        ng-model="slotEntry.degreeCurricularPlan" ng-show="slotEntry.editing"
                                        theme="bootstrap" > 
                                        <ui-select-match>
                                            {{$select.selected.text}}
                                        </ui-select-match> 
                                        <ui-select-choices repeat="degreeCurricularPlan.id as degreeCurricularPlan in slotEntry.degreeCurricularPlanDataSource | filter: $select.search">
                                            <span ng-bind-html="degreeCurricularPlan.text | highlight: $select.search"></span>
                                        </ui-select-choices>
                                    </ui-select>
                                    <span ng-hide="slotEntry.editing">
                                        {{ getDegreeCurricularPlanName(slotEntry) }}
                                    </span>
                                </td>
                                <td>
                                    <a class="btn btn-default" ng-hide="slotEntry.editing" ng-click="slotEntry.editing=true">
                                        <span class="glyphicon glyphicon-edit" aria-hidden="true"></span> 
                                    </a>
                                    &nbsp;
                                    <a class="btn btn-default" ng-show="slotEntry.editing" ng-click="slotEntry.editing=false">
                                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 
                                    </a>
                                    &nbsp;
                                    <a class="btn btn-default" ng-show="slotEntry.editing" ng-click="submitDegreeMapping(slotEntry, $model)">
                                        <span class="glyphicon glyphicon-ok" aria-hidden="true"></span> 
                                    </a>
                                    &nbsp;
                                    <a class="btn btn-danger" ng-click="deleteDegreeMapping(slotEntry.configuration, $model)">
                                        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span> 
                                    </a>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="panel-footer">
                </div>
            </div>
        </div>

        <div class="modal fade" id="deleteModal">
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
                        <p id="deleteModal_message">
                            
                        </p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">
                            <spring:message code="label.close" />
                        </button>
                        <button id="deleteButton" class="btn btn-danger" type="button" ng-click="submitDeleteMapping()">
                            <spring:message code="label.delete" />
                        </button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->

    </div>
</form>


<script>
$(document).ready(function() {
    
    
    });
</script>
