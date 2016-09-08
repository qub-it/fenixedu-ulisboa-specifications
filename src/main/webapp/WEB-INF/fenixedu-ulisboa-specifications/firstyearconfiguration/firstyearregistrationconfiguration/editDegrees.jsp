<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.firstyearconfiguration.FirstYearRegistrationConfigurationController"%>
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
            code="label.firstYearConfiguration.searchFirstYearRegistrationConfiguration" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/"  ><spring:message code="label.event.firstYearConfiguration.cancel" /></a>
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
            <p id="errorMessages" > <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>
    </div>  
</c:if>

<script>
    angular.isUndefinedOrNull = function(val) {
        return angular.isUndefined(val) || val === null
    };
    angular.module('angularApp',
            [ 'ngSanitize', 'ui.select', 'bennuToolkit' ]).controller(
            'angularController', [ '$scope', function($scope) {
                
                $scope.object = ${firstYearConfigurationBeanJson};
                $scope.postBack = createAngularPostbackFunction($scope);
                $scope.booleanvalues= [
                  {name: '<spring:message code="label.no"/>', value: false},
                  {name: '<spring:message code="label.yes"/>', value: true}
                ];
                //TODOJN - create a dropdown to choose the execution year
                $scope.executionYear = '<%= ExecutionYear.readCurrentExecutionYear().getExternalId() %>';
                
                $scope.addSlotEntry = function(model) {
                    if (angular.isUndefinedOrNull($scope.data.degree)) {
                        return;
                    }
                    url = '${pageContext.request.contextPath}<%= FirstYearRegistrationConfigurationController.ADD_DEGREE_CONFIGURATION_URL%>/' + $scope.data.degree + "/" + $scope.executionYear;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $('form[name="form"]').find('input[name="requiresVaccination"]').attr('value', false);
                    $('form[name="form"]').find('input[name="automaticEnrolment"]').attr('value', false);
                    $('form[name="form"]').find('input[name="degreeCurricularPlan"]').attr('value', undefined);
                    $scope.postBack(model);
                };
                $scope.deleteEntry = function(slotEntry, model) {
                    url = '${pageContext.request.contextPath}<%= FirstYearRegistrationConfigurationController.DELETE_DEGREE_CONFIGURATION_URL%>/' + slotEntry.degree + "/" + $scope.executionYear;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $('#deleteModal').modal('toggle');
                }
                $scope.submitDeleteEntry = function() {
                    $('#deleteModal').modal('toggle');                  
                    $scope.postBack(null);                 
                };
                $scope.submitForm = function() {
                    url = '${pageContext.request.contextPath}<%= FirstYearRegistrationConfigurationController.EDIT_DEGREE_CONFIGURATION_URL%>/' + $scope.executionYear;
                    $('form').attr('action', url);
                    $('form').submit();
                 };
            } ]);
</script>

<form name="form" ng-app="angularApp"
      ng-controller="angularController" method="post" class="form-horizontal" 
      action='#'>          
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.FirstYearRegistrationConfiguration.configurationByDegree" />
            </h3>
        </div>
        <input type="hidden" name="postback" value='#' />
        <input name="bean" type="hidden" value="{{ object }}" />
        
        <!-- Attributes to create Degree Configuration -->
        <input type="hidden" name="requiresVaccination" value="false" />
        <input type="hidden" name="automaticEnrolment" value="false" />
        <input type="hidden" name="degreeCurricularPlan" value="" />
        
        <div class="panel panel-body">
            <div class="form-group row">
                <div class="col-sm-5">
                    <ui-select id="firstYearConfiguration_degrees"
                        ng-model="$parent.data.degree"
                        theme="bootstrap"> 
                        <ui-select-match allow-clear="true">
                            {{$select.selected.text}}
                        </ui-select-match> 
                        <ui-select-choices repeat="element.id as element in object.activeDegreesDataSource | filter: $select.search">
                            <span ng-bind-html="element.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
                <div class="col-sm-5">
                    <button type="button" class="btn btn-default" ng-click="addSlotEntry($model)">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                </div>               
            </div>
        </div>
        <div class="panel panel-body">
              <table id="degreesConfigurationTable"
                class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <th><spring:message
                                code="label.FirstYearRegistrationConfiguration.degreeName" /></th>
                        <th style="width: 10%"><spring:message
                                code="label.FirstYearRegistrationConfiguration.degreeCode" /></th>
                        <th style="width: 10%"><spring:message
                                code="label.FirstYearRegistrationConfiguration.requiresVaccination" /></th>
                        <th style="width: 30%"><spring:message
                                code="label.FirstYearRegistrationConfiguration.degreeCurricularPlan" /></th>
                        <!-- operation column -->
                        <th style="width: 10%"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr
                        ng-repeat="slotEntry in object.activeDegrees">
                        <td>{{ slotEntry.label }}</td>
                        <td>{{ slotEntry.code }}</td>
                        <td>
                            <ui-select id="firstYearConfiguration_requiresVaccination"
                                ng-model="slotEntry.requiresVaccination"
                                theme="bootstrap" > 
                                <ui-select-match>
                                    {{$select.selected.name}}
                                </ui-select-match> 
                                <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                                    <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                                </ui-select-choices>
                            </ui-select>
                        </td>
<!--                         <td> -->
<!--                             <ui-select id="firstYearConfiguration_automaticEnrolment" -->
<!--                                 ng-model="slotEntry.automaticEnrolment" -->
<!--                                 theme="bootstrap" >  -->
<!--                                 <ui-select-match> -->
<!--                                     {{$select.selected.name}} -->
<!--                                 </ui-select-match>  -->
<!--                                 <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search"> -->
<!--                                     <span ng-bind-html="bvalue.name | highlight: $select.search"></span> -->
<!--                                 </ui-select-choices> -->
<!--                             </ui-select> -->
<!--                         </td> -->
                        <td>
                            <ui-select id="firstYearConfiguration_degreeCurricularPlan"
                                ng-model="slotEntry.degreeCurricularPlan"
                                theme="bootstrap" > 
                                <ui-select-match>
                                    {{$select.selected.text}}
                                </ui-select-match> 
                                <ui-select-choices repeat="degreeCurricularPlan.id as degreeCurricularPlan in slotEntry.degreeCurricularPlanDataSource | filter: $select.search">
                                    <span ng-bind-html="degreeCurricularPlan.text | highlight: $select.search"></span>
                                </ui-select-choices>
                            </ui-select>
                        </td>
                        <td>
                            <a class="btn btn-danger" ng-click="deleteEntry(slotEntry, $model)">
                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span> 
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="panel panel-footer">
        <button type="button" class="btn btn-primary" role="button" ng-click="submitForm()">
            <spring:message code="label.submit"/>
        </button>
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
                            code="label.FirstYearRegistrationConfiguration.degreeDelete.confirmationDelete" />
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
