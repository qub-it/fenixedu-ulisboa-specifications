<%@page import="org.fenixedu.ulisboa.specifications.ui.servicerequesttype.ServiceRequestTypeController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

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

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message
            code="label.manageServiceRequestTypes.readServiceRequestType" />
        <small></small>
    </h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= ServiceRequestTypeController.SEARCH_URL %>">
        <spring:message code="label.event.back" />
    </a> 
    &nbsp;|&nbsp; 
    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= ServiceRequestTypeController.UPDATE_URL %>${serviceRequestType.externalId}">
        <spring:message code="label.event.update" />
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

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.details" />
        </h3>
    </div>
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.code" /></th>
                        <td><c:out
                                value='${serviceRequestType.code}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.name" /></th>
                        <td><c:out
                                value='${serviceRequestType.name.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.active" /></th>
                        <td><c:if
                                test="${serviceRequestType.active}">
                                <spring:message code="label.true" />
                            </c:if> <c:if
                                test="${not serviceRequestType.active}">
                                <spring:message code="label.false" />
                            </c:if></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.payable" /></th>
                        <td><c:if
                                test="${serviceRequestType.payable}">
                                <spring:message code="label.true" />
                            </c:if> <c:if
                                test="${not serviceRequestType.payable}">
                                <spring:message code="label.false" />
                            </c:if></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.notifyUponConclusion" /></th>
                        <td><c:if
                                test="${serviceRequestType.notifyUponConclusion}">
                                <spring:message code="label.true" />
                            </c:if> <c:if
                                test="${not serviceRequestType.notifyUponConclusion}">
                                <spring:message code="label.false" />
                            </c:if></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.printable" /></th>
                        <td><c:if
                                test="${serviceRequestType.printable}">
                                <spring:message code="label.true" />
                            </c:if> <c:if
                                test="${not serviceRequestType.printable}">
                                <spring:message code="label.false" />
                            </c:if></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.requestedOnline" /></th>
                        <td><c:if
                                test="${serviceRequestType.requestedOnline}">
                                <spring:message code="label.true" />
                            </c:if> <c:if
                                test="${not serviceRequestType.requestedOnline}">
                                <spring:message code="label.false" />
                            </c:if></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.serviceRequestCategory" /></th>
                        <td><spring:message code="${ serviceRequestType.serviceRequestCategory.qualifiedName }" /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message
                                code="label.ServiceRequestType.uLisboaServiceRequestProcessors" /></th>
                        <td>
                            <c:forEach var="processor" items="${ serviceRequestType.ULisboaServiceRequestProcessorsSet }" >
                                <p><c:out value="${ processor.name.content }"/></p>
                            </c:forEach>
                        </td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>

<script>
    angular.isUndefinedOrNull = function(val) {
        return angular.isUndefined(val) || val === null
    };
    angular.module('angularAppServiceRequestType',
            [ 'ngSanitize', 'ui.select', 'bennuToolkit' ]).controller(
            'ServiceRequestTypeController', [ '$scope', function($scope) {

            	
                $scope.object = ${serviceRequestSlotsBeanJson};
                $scope.postBack = createAngularPostbackFunction($scope);
                $scope.booleanvalues= [
                  {name: '<spring:message code="label.no"/>', value: false},
                  {name: '<spring:message code="label.yes"/>', value: true}
                ];
                $scope.addSlotEntry = function(model) {
                    if (angular.isUndefinedOrNull($scope.serviceRequestSlot)) {
                        return;
                    }
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.ADD_PROPERTY_URL%>${serviceRequestType.externalId}';
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $('form[name="form"]').find('input[name="required"]').attr('value', false);
                    $('form[name="form"]').find('input[name="orderNumber"]').attr('value', $scope.object.serviceRequestSlotEntries.length);
                    $('form[name="form"]').find('input[name="serviceRequestSlot"]').attr('value', $scope.serviceRequestSlot);
                    $scope.postBack(model);
                    
                    $scope.serviceRequestSlot = undefined;
                };
                $scope.addDefaultSlotEntries = function(model) {
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.ADD_DEFAULT_PROPERTIES_URL%>${serviceRequestType.externalId}';
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $scope.postBack(null);
                }
                $scope.deleteEntry = function(slotEntry, model) {
                	url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.DELETE_PROPERTY_URL%>${serviceRequestType.externalId}/' + slotEntry.entry;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $('#deleteModal').modal('toggle');
                }
                $scope.submitDeleteEntry = function() {
                    $('#deleteModal').modal('toggle');                	
                    $scope.postBack(null);                 
                };
                $scope.moveUp = function(slotEntry, model) {
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.MOVE_UP_PROPERTY_URL%>${serviceRequestType.externalId}/' + slotEntry.entry;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $scope.postBack(model);                 
                }
                $scope.moveDown = function(slotEntry, model) {
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.MOVE_DOWN_PROPERTY_URL%>${serviceRequestType.externalId}/' + slotEntry.entry;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $scope.postBack(model);                 
                }
                $scope.changeRequired = function(slotEntry, model) {
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.UPDATE_PROPERTY_URL%>${serviceRequestType.externalId}/' + slotEntry.entry;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $('form[name="form"]').find('input[name="required"]').attr('value', slotEntry.required);
                	$scope.postBack(model);
                }
            } ]);
</script>


<form name="form" ng-app="angularAppServiceRequestType"
      ng-controller="ServiceRequestTypeController" method="post" class="form-horizontal" 
      action='#'>          
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.ServiceRequestType.serviceRequestSlot" />
            </h3>
        </div>
        <input type="hidden" name="postback" value='#' />
        <input name="bean" type="hidden" value="{{ object }}" />
        
        <!-- Attributes to create a new ServiceRequestSlotEntry -->
        <input type="hidden" name="required" value="" />
        <input type="hidden" name="orderNumber" value="" />
        <input type="hidden" name="serviceRequestSlot" value="" />
        
        <div class="panel panel-body">
            <div class="form-group row">
                <div class="col-sm-5">
                    <ui-select id="serviceRequestType_serviceRequestSlot"
                        ng-model="$parent.serviceRequestSlot"
                        theme="bootstrap"> 
                        <ui-select-match allow-clear="true">
                            {{$select.selected.text}}
                        </ui-select-match> 
                        <ui-select-choices repeat="element.id as element in object.serviceRequestSlotsDataSource | filter: $select.search">
                            <span ng-bind-html="element.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
                <div class="col-sm-5">
                	<button type="button" class="btn btn-default" ng-click="addSlotEntry($model)">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                    &nbsp;&nbsp;&nbsp;
                    <button type="button" class="btn btn-default" ng-click="addDefaultSlotEntries($model)">
                		<span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add.defaultProperties" />
                	</button>
                </div>               
            </div>
        </div>
        <div class="panel panel-body">
              <table id="serviceRequestTypePropertiesTable"
                class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <th><spring:message
                                code="label.ServiceRequestSlot.label" /></th>
                        <th style="width: 10%"><spring:message
                                code="label.ServiceRequestSlotEntry.required" /></th>
                        <!-- operation column -->
                        <th style="width: 25%"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        ng-repeat="slotEntry in object.serviceRequestSlotEntries">
                        <td>{{ slotEntry.label }}</td>
                        <td ng-hide="slotEntry.editing">
                                <div ng-show="slotEntry.required">
                                <spring:message code="label.true"/>
                                </div>
                                <div ng-hide="slotEntry.required">
                                <spring:message code="label.false"/>
                                </div>
                        </td>
                        <td ng-show="slotEntry.editing">
                            <ui-select id="serviceRequestSlotEntry_required"
                                ng-model="slotEntry.required"
                                theme="bootstrap" > 
                                <ui-select-match>
                                    {{$select.selected.name}}
                                </ui-select-match> 
                                <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                                    <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                                </ui-select-choices>
                            </ui-select>
                        </td>
                        <td>
                            <a class="btn btn-default" ng-click="changeRequired(slotEntry)" ng-show="slotEntry.editing">
                                <span class="glyphicon glyphicon-ok" aria-hidden="true"></span> 
                                &nbsp;
                            </a>
                            <a class="btn btn-default" ng-click="slotEntry.editing = true" ng-hide="slotEntry.editing">
                                <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> 
                                &nbsp;
                            </a>

                            <button type="button" class="btn btn-default" ng-click="moveDown(slotEntry)" ng-disabled="slotEntry.orderNumber == object.serviceRequestSlotEntries.length - 1">
                                <span class="glyphicon glyphicon-arrow-down" aria-hidden="true"></span> 
                                &nbsp;
                            </button>
                            <button type="button" class="btn btn-default" ng-click="moveUp(slotEntry)" ng-disabled="slotEntry.orderNumber == 0">
                                <span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span> 
                                &nbsp;
                            </button>
                            &nbsp;&nbsp;
                            <a class="btn btn-danger" ng-click="deleteEntry(slotEntry, $model)">
                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span> 
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
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
                    <p>
                        <spring:message
                            code="label.serviceRequestType.serviceRequestSlotEntry.confirmDelete" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger" type="button" ng-click="submitDeleteEntry()">
                        <spring:message code="label.delete" />
                    </button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.modal -->
    
</form>


<form name="createRestrictionForm" ng-app="angularAppServiceRequestRestriction" id="createRestrictionForm"
      ng-controller="ServiceRequestRestrictionController" method="post" class="form-horizontal" 
      action='#'>

    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.ServiceRequestType.serviceRequestRestrictions" />
            </h3>
        </div>
        <input type="hidden" name="postback" value='${pageContext.request.contextPath}<%= ServiceRequestTypeController.CREATE_RESTRICTION_POSTBACK_URL %>${serviceRequestType.externalId}' />
        <input name="bean" type="hidden" value="{{ object }}" />
        
        <div class="panel panel-body">
	        <div class="form-group row">
	        	<div class="col-sm-1"></div>
				<div class="col-sm-1 control-label"><spring:message code="label.ServiceRequestType.ServiceRequestRestriction.degreeType"/></div>
				<div class="col-sm-6">
					<ui-select id="serviceRequestRestriction_degreeType" class="" name="degreetype" ng-model="$parent.object.degreeType" on-select="onDegreeTypeChange($item, $model)" theme="bootstrap" ng-disabled="disabled" >
									<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
									<ui-select-choices repeat="degreeType.id as degreeType in object.degreeTypeDataSource | filter: $select.search">
	 										<span ng-bind-html="degreeType.text | highlight: $select.search"></span>
									</ui-select-choices>
							</ui-select>				
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-1"></div>
				<div class="col-sm-1 control-label"><spring:message code="label.ServiceRequestType.ServiceRequestRestriction.degree"/></div>
				<div class="col-sm-6">
					<ui-select id="serviceRequestRestriction_degree" class="" name="degree" ng-model="$parent.object.degree" theme="bootstrap" ng-disabled="disabled" >
									<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
									<ui-select-choices repeat="degree.id as degree in object.degreeDataSource | filter: $select.search">
	 										<span ng-bind-html="degree.text | highlight: $select.search"></span>
									</ui-select-choices>
							</ui-select>				
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-1"></div>
				<div class="col-sm-1 control-label"><spring:message code="label.ServiceRequestType.ServiceRequestRestriction.programConclusion"/></div>
				<div class="col-sm-6">
					<ui-select id="serviceRequestRestriction_programConclusion" class="" name="programConclusion" ng-model="$parent.object.programConclusion" theme="bootstrap" ng-disabled="disabled" >
									<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
									<ui-select-choices repeat="programConclusion.id as programConclusion in object.programConclusionDataSource | filter: $select.search">
	 										<span ng-bind-html="programConclusion.text | highlight: $select.search"></span>
									</ui-select-choices>
							</ui-select>				
				</div>
			</div>
            <div class="form-group row">
                <div class="col-sm-2"></div>
                <div class="col-sm-1">
                    <button type="button" class="btn btn-default" ng-disabled="isFormFilled()" ng-click="addRestriction($model)">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                </div>
            </div>
        </div>
        <div class="panel panel-body">
              <table id="serviceRequestRestrcitionsTable"
                class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <th><spring:message code="label.ServiceRequestType.ServiceRequestRestriction.degreeType" /></th>
                        <th><spring:message code="label.ServiceRequestType.ServiceRequestRestriction.degree" /></th>
                        <th><spring:message code="label.ServiceRequestType.ServiceRequestRestriction.programConclusion" /></th>
                        <!-- operation column -->
                        <th style="width: 20%"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="restrictionItem in object.restrictions">
                        <td>{{ restrictionItem.degreeType }}</td>
                        <td>{{ restrictionItem.degree }}</td>
                        <td>{{ restrictionItem.programConclusion }}</td>
                        <td>
                            <a class="btn btn-danger" ng-click="deleteRestriction(restrictionItem.restriction, $model)">
                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span> 
                                &nbsp;
                                <spring:message code="label.event.delete" />
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    
    <div class="modal fade" id="deleteRestrictionModal">
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
                    <p>
                        <spring:message
                            code="label.serviceRequestType.serviceRequestRestriction.confirmDelete" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger" type="button" ng-click="submitDeleteRestriction()">
                        <spring:message code="label.delete" />
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
angular.module('angularAppServiceRequestRestriction',
        [ 'ngSanitize', 'ui.select', 'bennuToolkit' ]).controller(
        'ServiceRequestRestrictionController', [ '$scope', function($scope) {

            $scope.booleanvalues= [
              {name: '<spring:message code="label.no"/>', value: false},
              {name: '<spring:message code="label.yes"/>', value: true}
            ];
            
            $scope.object = ${serviceRequestRestrictionBeanJson};
        	$scope.postBack = createAngularPostbackFunction($scope); 

        	$scope.onDegreeTypeChange = function(degreeType, model) {
        		var url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.CREATE_RESTRICTION_POSTBACK_URL %>${serviceRequestType.externalId}';
            	$('form[name="createRestrictionForm"]').find('input[name="postback"]').attr('value', url);
                $scope.object.degree = undefined;
                $scope.form = $scope.createRestrictionForm;
                $scope.postBack(model);
            };
            
            $scope.isFormFilled = function () {
            	return !$scope.object.degreeType && !$scope.object.degree && !$scope.object.programConclusion;
            };
            
            $scope.addRestriction = function (model) {
            	var url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.CREATE_RESTRICTION_URL %>${serviceRequestType.externalId}';
            	$('form[name="createRestrictionForm"]').find('input[name="postback"]').attr('value', url);
            	$scope.form = $scope.createRestrictionForm;
                $scope.postBack(null);
            };
            
            $scope.resetRestrictionForm = function () {
            	$scope.object.degree = undefined;
            	$scope.object.degreeType = undefined;
            	$scope.object.programConclusion = undefined;
            	$scope.object.degreeDataSource = [];
            };
            
            $scope.deleteRestriction = function(restriction, model) {
            	var url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.DELETE_RESTRICTION_URL %>' + restriction;
            	$('form[name="createRestrictionForm"]').find('input[name="postback"]').attr('value', url);
            	$('#deleteRestrictionModal').modal('toggle');
            	
            };
            
            $scope.submitDeleteRestriction = function () {
            	$('#deleteRestrictionModal').modal('toggle');
            	$scope.form = $scope.createRestrictionForm;
                $scope.postBack(null);
            };
            
        } ]);
angular.bootstrap($('#createRestrictionForm')[0],['angularAppServiceRequestRestriction']);

$(document).ready(function() {
});
</script>

