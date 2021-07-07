<!--
 /**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Servi�os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright � 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright � 2015 Universidade de Lisboa (after any Go-Live phase)
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
        <spring:message code="label.servicerequests.ULisboaServiceRequest.createULisboaServiceRequest" />
        <small></small>
    </h1>
</div>
<%-- NAVIGATION --%>

<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    <a class=""
        href="${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.SEARCH_URL %>">
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
    function createPostbackFunction(angular_scope) {
        return function(model, successFunction) {
    
            angular_scope.$apply();
            var form = $('form[name="' + angular_scope.form.$name + '"]');
            var previousActionURL = form.attr("action");
            form.submit = function(e) {
                var postData = $(this).serializeArray();
                var formURL = $(this).attr("action");
                $.ajax({
                    url : formURL,
                    type : "POST",
                    data : postData,
                    success : function(data, textStatus, jqXHR) {
                        angular_scope.object = data;
                        angular_scope.isUISelectLoading = undefined;
                        angular_scope.$apply();
                        successFunction();
                    },
                    error : function(jqXHR, textStatus, errorThrown) {
                        messageAlert("Erro", jqXHR.responseText);
                    },
                });
            };
    
            form.attr("action", form.find('input[name="postback"]').attr('value'));
            form.submit();
            form.attr("action", previousActionURL);
        };
   }
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
           'ULisboaServiceRequestRegistrationController', [ '$scope', function($scope) {

               $scope.object = ${uLisboaServiceRequestRegistrationBeanJson};
               $scope.postBack = createPostbackFunction($scope);
               $scope.isUISelectLoading = {};
               $scope.getUISelectLoading = function() {
                   if($scope.isUISelectLoading == undefined) {
                       $scope.isUISelectLoading = {};      
                   }
                   return $scope.isUISelectLoading;
               };

               $scope.onDropDownRefresh = function(institution, namePart, model) {
                   if($scope.getUISelectLoading()['student'] == undefined) {
                       angular.extend($scope.getUISelectLoading(),{'student' : true});
                   }
                   $scope.isUISelectLoading.student = true;
                   $scope.object.studentSearchText = namePart;
                   $scope.$apply();  
                   $scope.transformDataToSubmit();
                   $scope.postBack(model, $scope.transformDataToShow);               
               }
               $scope.onDropDownChange = function(model) {
                   $scope.$apply();  
        	       $scope.transformDataToSubmit();
        	       $scope.postBack(model, $scope.transformDataToShow);
               }
               $scope.booleanvalues= [
                 {name: '<spring:message code="label.no"/>', value: false},
                 {name: '<spring:message code="label.yes"/>', value: true}
               ];
               $scope.transformDataToSubmit = function () {
                   $scope.$apply();        	   
               }
               $scope.transformDataToShow = function () {
                   $scope.$apply();            
               };
               $scope.submitFormIfValid = function (event) {
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
    ng-controller="ULisboaServiceRequestRegistrationController"
    ng-submit="form.$valid"
    action='${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.CREATE_WITH_REGISTRATION_URL %>'
    >

    <input type="hidden" name="postback"
        value='${pageContext.request.contextPath}<%= ULisboaServiceRequestManagementController.CREATE_WITH_REGISTRATION_POSTBACK_URL %>' 
    />

    <input name="bean" type="hidden" value="{{ object }}" />
    
   <div class="panel panel-default">
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label required-field">
                    <spring:message code="label.student" />
                </div>

                <div class="col-sm-9">
                    <ui-select reset-search-input="false" id="ulisboaServiceRequest_student" name="student" ng-model="$parent.object.student" on-select="onDropDownChange($item,$model)" theme="bootstrap">
                        <ui-select-match placeholder="{{typpingMessage}}">{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="student.id as student in object.studentDataSource"
                                            refresh="onDropDownRefresh($item, $select.search, $model)"
                                            refresh-delay="0">
                            <span ng-bind-html="student.text"></span>
                        </ui-select-choices> 
                    </ui-select> 
                </div>
                <div class="col-sm-1" ng-show="isUISelectLoading.student">
                    <i class="fa fa-spinner fa-spin" aria-hidden="true"></i>
                </div> 
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label required-field">
                    <spring:message code="label.registration" />
                </div>

                <div class="col-sm-9">
                    <ui-select reset-search-input="false" id="ulisboaServiceRequest_registration" name="registration" ng-model="$parent.object.registration" theme="bootstrap">
                        <ui-select-match placeholder="{{typpingMessage}}">{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="registration.id as registration in object.registrationDataSource">
                            <span ng-bind-html="registration.text"></span>
                        </ui-select-choices> 
                    </ui-select> 
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <button ng-disabled="angular.isUndefinedOrNull(object.registration) || object.registration === '' || form.$invalid" ng-click="submitFormIfValid($event)" class="btn btn-primary" type="button" role="button">
                <spring:message code="label.next" />
            </button>
        </div>
    </div>
</form>

<script>
	$(document).ready(function() {

	});
</script>
