<!--
 /**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoe@qub-it.com 
 *
 * 
 * This file is part of FenixEdu Specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
 -->
<%@page import="org.fenixedu.ulisboa.specifications.domain.serviceRequests.UIComponentType"%>
<%@page import="org.fenixedu.academic.predicate.AccessControl"%>
<%@page import="org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType"%>
<%@page import="org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.student.ulisboaservicerequest.ULisboaServiceRequestController"%>
<%@page import="org.fenixedu.commons.i18n.I18N"%>
<%@page import="org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest.ULisboaServiceRequestManagementController"%>
<%@page import="org.fenixedu.ulisboa.specifications.util.ULisboaConstants"%>
<%@page import="org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeType"%>
<%@page import="org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance"%>
<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter"%>
<%@page import="org.fenixedu.academic.domain.student.Registration" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>

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
<%-- ${portal.toolkit()} --%>

<link
    href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
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
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/angularjs-dropdown-multiselect.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/lodash.js/2.4.1/lodash.min.js"></script>

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.servicerequests.ULisboaServiceRequest.updateULisboaServiceRequest" />
        <small></small>
    </h1>
</div>
<%-- NAVIGATION --%>

<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    <a class=""
        href="${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.READ_ACADEMIC_REQUEST_URL %>${ serviceRequest.externalId }">
        <spring:message code="label.event.back" />
    </a>
    &nbsp; 
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
    Array.prototype.containsId = function(elementId){
        for(var i = 0; i < this.length; i++) {
        	if(this[i].code == elementId) {
        		return i;
        	}
        }
    	return -1;
    };
    angular.isUndefinedOrNull = function(val) {
        return angular.isUndefined(val) || val === null
    };
    angular.module('angularAppULisboaServiceRequest',
            [ 'ngSanitize', 'ui.select', 'bennuToolkit', 'angularjs-dropdown-multiselect' ])
           .controller(
           'ULisboaServiceRequestController', [ '$scope', function($scope) {

               $scope.object = angular.extend({"showForceUpdate" : true}, ${ulisboaServiceRequestBeanJson});
               $scope.initObject = function () {
        	       angular.forEach($scope.object.serviceRequestPropertyBeans, function(element, index) {
                       if (element.uiComponentType == '<%= UIComponentType.DROP_DOWN_ONE_VALUE%>') {
                           if(element.code == '<%= ULisboaConstants.LANGUAGE %>') {
                               element.value = element.localeValue;
                           } else if(element.code == '<%= ULisboaConstants.CYCLE_TYPE %>') {
                               element.value = element.cycleTypeValue;
                           } else {
                               element.value = element.domainObjectValue;
                           }
                       }
                       if (element.uiComponentType == '<%= UIComponentType.DROP_DOWN_MULTIPLE %>') {
                	       angular.forEach(element.domainObjectListValue, function(el, i) {
                               element.domainObjectListValue[i] = { id : el };
                           })
                       }
        	       });
               }
               $scope.booleanvalues= [
                 {name: '<spring:message code="label.no"/>', value: false},
                 {name: '<spring:message code="label.yes"/>', value: true}
               ];
               //Dependencies for ngShow
               $scope.showElement = function (elementId) {
               	return $scope.otherDocumentPurposeDependency(elementId) && $scope.hideRequiredFieldsWithOneValue(elementId);
               };
               $scope.hideRequiredFieldsWithOneValue = function(elementId) {
                   var elementIndex = $scope.object.serviceRequestPropertyBeans.containsId(elementId);
                   var element = $scope.object.serviceRequestPropertyBeans[elementIndex];
                   if(!angular.isUndefinedOrNull(element.dataSource) && element.dataSource.length == 1 && element.required) {
                       if(element.uiComponentType == '<%= UIComponentType.DROP_DOWN_MULTIPLE %>') {
                	       element.domainObjectListValue = [{'id' : element.dataSource[0].id}];
                       }
                       if(element.uiComponentType == '<%= UIComponentType.DROP_DOWN_ONE_VALUE %>') {
                           element.value = element.dataSource[0].id;
                       }
                       return false;
                   }
                   return true; 
               };
               $scope.otherDocumentPurposeDependency = function (elementId) {
                   if(elementId != '<%= ULisboaConstants.OTHER_DOCUMENT_PURPOSE %>') {
                	   return true;
                   }
                   var docPurposeIndex = $scope.object.serviceRequestPropertyBeans.containsId('<%= ULisboaConstants.DOCUMENT_PURPOSE_TYPE %>');
                   var otherDocPurposeIndex = $scope.object.serviceRequestPropertyBeans.containsId('<%= ULisboaConstants.OTHER_DOCUMENT_PURPOSE %>');
                   <% String otherName = DocumentPurposeTypeInstance.findUnique(DocumentPurposeType.OTHER).getExternalId(); %>
                   if(docPurposeIndex != -1 && otherDocPurposeIndex != -1 && $scope.object.serviceRequestPropertyBeans[docPurposeIndex].value == '<%= otherName %>') {
                       if (docPurposeIndex+1 != otherDocPurposeIndex) {
                    	   $scope.object.serviceRequestPropertyBeans.splice(docPurposeIndex+1,0,$scope.object.serviceRequestPropertyBeans.splice(otherDocPurposeIndex,1)[0]);
                       }
                       $scope.object.serviceRequestPropertyBeans[otherDocPurposeIndex].required = true;
                       return true;
                   }
                   $scope.object.serviceRequestPropertyBeans[otherDocPurposeIndex].required = false;
                   return false;
               };
               $scope.language = '<%= I18N.getLocale().toString() %>'.replace(/_/g,"-");
               $scope.multiSelectOptions = { displayProp : 'text', idProp: 'id', externalIdProp : 'id' };
               $scope.translationTexts = {
                       checkAll: '<spring:message code="label.angularjs.multiselect.checkAll" />',
                       uncheckAll: '<spring:message code="label.angularjs.multiselect.uncheckAll" />',
                       selectionCount: '<spring:message code="label.angularjs.multiselect.selectionCount" />',
                       selectionOf: '/',
                       searchPlaceholder: '<spring:message code="label.angularjs.multiselect.searchPlaceholder" />',
                       buttonDefaultText: '<spring:message code="label.angularjs.multiselect.buttonDefaultText" />',
                       dynamicButtonTextSuffix: '<spring:message code="label.angularjs.multiselect.dynamicButtonTextSuffix" />'                		
               };
               $scope.transformDataToSubmit = function () {
                   // ULisboaServiceRequestBean is expecting a DateTime 
                   if($scope.object.requestDate && $scope.object.requestDate.match("^[0-9]{4}-[0-9]{2}-[0-9]{2}$") !== null) {
                       $scope.object.requestDate = $scope.object.requestDate + "T00:00:00.000Z";
                   }
                   if($scope.object.requestDate && $scope.object.requestDate.length === 0) {
                       $scope.object.requestDate = undefined;
                   }
                   // Change the Properties' values for the expected format
                   angular.forEach($scope.object.serviceRequestPropertyBeans, function(element, index) {
                       element.dataSource= undefined;
                       // ServiceRequestPropertyBean is expecting a DateTime
                       if (element.uiComponentType == '<%= UIComponentType.DATE %>') {
                           if(element.dateTimeValue && element.dateTimeValue.match("^[0-9]{4}-[0-9]{2}-[0-9]{2}$") !== null) {
                               element.dateTimeValue = element.dateTimeValue + "T00:00:00.000Z";
                           }
                           if(element.dateTimeValue && element.dateTimeValue.length === 0) {
                               element.dateTimeValue = undefined;
                           }
                       }
                       // ServiceRequestPropertyBean is expecting an array of Strings and not an array of JsonObjects
                       if (element.uiComponentType == '<%= UIComponentType.DROP_DOWN_MULTIPLE %>') {
                           angular.forEach(element.domainObjectListValue, function(el, i) {
                               element.domainObjectListValue[i] = el.id;
                           })
                       }
                       // DropDown one value can be stored in three different variables
                       if (element.uiComponentType == '<%= UIComponentType.DROP_DOWN_ONE_VALUE%>') {
                           if(element.code == '<%= ULisboaConstants.LANGUAGE %>') {
                               element.localeValue = element.value;
                           } else if(element.code == '<%= ULisboaConstants.CYCLE_TYPE %>') {
                               element.cycleTypeValue = element.value;
                           } else {
                               element.domainObjectValue = element.value;
                           }
                       }
                   });
                   $scope.$apply();        	   
               }
               $scope.submitFormIfValid = function (event) {
            	   if($scope.object.showForceUpdate == true) {
            		   $("#uLisboaServiceRequestUpdateModal").modal('toggle');
            		   return;
            	   }
            	   
                   if($scope['form'].$invalid) {
               	       return;
                   }
                   $scope.transformDataToSubmit();
                   $('form').submit();
               }
           } ]);
</script>

<form name='form' method="post" class="form-horizontal"
    ng-app="angularAppULisboaServiceRequest"
    ng-controller="ULisboaServiceRequestController"
    ng-submit="form.$valid"
    ng-init="initObject()"
        action='${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.UPDATE_URL %>${serviceRequest.externalId}'
    >

    <input name="bean" type="hidden" value="{{ object }}" />

    <div class="panel panel-default">
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.ULisboaServiceRequest.documentType" />
                </div>
                <div class="col-sm-7">
                    <c:out value="${ serviceRequest.serviceRequestType.name.content }" />
                </div>
                
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.ULisboaServiceRequest.requestDate" />
                </div>
                <div class="col-sm-7">
                    <input id="requestDate" class="form-control" type="text" bennu-date="object.requestDate" 
                        name="field" ng-required="true" />  
                </div>
            </div>

            <div class="form-group row" ng-repeat="serviceRequestProperty in object.serviceRequestPropertyBeans" ng-show="showElement(serviceRequestProperty.code)">
                <div class="col-sm-2 control-label">
                    {{ serviceRequestProperty.label[language] }}
                </div>
                <div class="col-sm-7">
                    <ui-select id="{{ serviceRequestProperty.code}}" name="field"
                        ng-model="serviceRequestProperty.value"
                        ng-if="serviceRequestProperty.uiComponentType == 'DROP_DOWN_ONE_VALUE'"
                        ng-disabled="!serviceRequestProperty.isEditable"
                        theme="bootstrap" ng-required="serviceRequestProperty.required"> 
                        <ui-select-match allow-clear="serviceRequestProperty.isEditable">
                            {{$select.selected.text}}
                        </ui-select-match> 
                        <ui-select-choices repeat="element.id as element in serviceRequestProperty.dataSource | filter: $select.search">
                            <span ng-bind-html="element.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>    
                    <ui-select id="{{serviceRequestProperty.code}}" name="field"
                        ng-model="serviceRequestProperty.booleanValue"
                        ng-if="serviceRequestProperty.uiComponentType == 'DROP_DOWN_BOOLEAN'"
                        ng-disabled="!serviceRequestProperty.isEditable"
                        theme="bootstrap" ng-required="serviceRequestProperty.required"> 
                        <ui-select-match allow-clear="serviceRequestProperty.isEditable">
                            {{$select.selected.name}}
                        </ui-select-match> 
                        <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                            <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select> 
                    <input id="{{ serviceRequestProperty.code }}" class="form-control" ng-if="serviceRequestProperty.uiComponentType == 'TEXT'"
                           type="text" ng-model="serviceRequestProperty.stringValue" name="field" ng-required="serviceRequestProperty.required" ng-disabled="!serviceRequestProperty.isEditable" 
                           value='<c:out value='${requestScope[serviceRequestProperty.code]}'/>'
                    />
                    <input id="{{ serviceRequestProperty.code }}" class="form-control" ng-if="serviceRequestProperty.uiComponentType == 'NUMBER'"
                           type="number" ng-model="serviceRequestProperty.integerValue" name="field"  ng-required="serviceRequestProperty.required" ng-disabled="!serviceRequestProperty.isEditable"
                           value='<c:out value='${requestScope[serviceRequestProperty.code]}'/>'
                    />
                    <input id="{{ serviceRequestProperty.code }}" class="form-control" ng-if="serviceRequestProperty.uiComponentType == 'TEXT_LOCALIZED_STRING'"
                           type="text" bennu-localized-string="serviceRequestProperty.localizedStringValue" name="field" ng-required="serviceRequestProperty.required"
                           value='<c:out value='${requestScope[serviceRequestProperty.code]}'/>'
                    />
                    <input id="{{ serviceRequestProperty.code }}" class="form-control" ng-if="serviceRequestProperty.uiComponentType == 'DATE'"
                           type="text" bennu-date="serviceRequestProperty.dateTimeValue" name="field" ng-required="serviceRequestProperty.required"
                           value='<c:out value='${requestScope[serviceRequestProperty.code]}'/>'
                    />    
                    <div id="{{serviceRequestProperty.code}}" name="field" class="ui-select-container ui-select-bootstrap dropdown" 
                        ng-if="serviceRequestProperty.uiComponentType == 'DROP_DOWN_MULTIPLE'"
                        ng-dropdown-multiselect="" options="serviceRequestProperty.dataSource"
                        selected-model="serviceRequestProperty.domainObjectListValue" extra-settings="multiSelectOptions" translation-texts="translationTexts" >
                    </div>
                </div>
                <div class="col-sm-3">
                    <span class="alert alert-warning btn-xs"
                       ng-show="serviceRequestProperty.required">
                        <spring:message code="warning.required.field" />
                    </span>
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <button ng-disabled="angular.isUndefinedOrNull(object.serviceRequestType) || object.serviceRequestType === '' || form.$invalid" ng-click="submitFormIfValid($event)" class="btn btn-primary" type="button" role="button">
                <spring:message code="label.submit" />
            </button>
        </div>
    </div>


    <!-- modal -->
    <div class="modal fade" id="uLisboaServiceRequestUpdateModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="form-group row">
                        <spring:message code="label.ULisboaServiceRequest.forceUpdate"/>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.cancel" />
                    </button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" ng-click="object.forceUpdate=false;object.showForceUpdate=false;submitFormIfValid(null)">
                        <spring:message code="label.UlisboaServiceRequest.negforceUpdateButton" />
                    </button>
                    <button id="confirmUpdateButton" class="btn btn-danger" type="button" role="button" data-dismiss="modal"
                            ng-click="object.showForceUpdate=false;object.forceUpdate=true;submitFormIfValid(null)">
                        <spring:message code="label.UlisboaServiceRequest.forceUpdateButton" />
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
	$(document).ready(function() {

	});
</script>
