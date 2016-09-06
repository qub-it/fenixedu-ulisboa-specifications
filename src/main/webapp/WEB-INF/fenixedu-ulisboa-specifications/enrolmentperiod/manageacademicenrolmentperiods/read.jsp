<%@page import="org.fenixedu.ulisboa.specifications.ui.enrolmentperiod.manageenrolmentperiod.AcademicEnrolmentPeriodController"%>
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

<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/angularjs-dropdown-multiselect.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/lodash.js/2.4.1/lodash.min.js"></script>


<div class="modal fade" id="deleteModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteForm" action="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.DELETE_URL %>/${ academicEnrolmentPeriod.externalId }" method="POST">
                <div class="modal-header">
                    <button type="button" class="close"
                        data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.manageAcademicEnrolmentPeriods.readAcademicEnrolmentPeriod.confirmDelete" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                        data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger"
                        type="submit">
                        <spring:message code="label.delete" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.manageAcademicEnrolmentPeriod.read" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.SEARCH_URL %>">
        <spring:message code="label.event.back" />
    </a>
    &nbsp;|&nbsp;
    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="#" data-toggle="modal" data-target="#deleteModal">
        <spring:message code="label.event.delete" />
    </a>
    &nbsp;|&nbsp;
    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.UPDATE_URL %>/${ academicEnrolmentPeriod.externalId }">
        <spring:message code="label.event.update" />
    </a>
</div>

<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
                ${message}
            </p>
        </c:forEach>

    </div>
</c:if>

<script>
Array.prototype.containsId = function(elementId){
    for(var i = 0; i < this.length; i++) {
        if(this[i] == elementId) {
            return i;
        }
    }
    return -1;
};
angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null
};
angular.module('angularApp', ['ngSanitize', 'ui.select', 'angularjs-dropdown-multiselect']).controller('angularController', ['$scope', function($scope) {

    $scope.object= angular.fromJson('${academicEnrolmentPeriodBeanJson}');;
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
            { name : '<spring:message code="label.no"/>', value : false },
            { name : '<spring:message code="label.yes"/>', value : true } 
    ];
    $scope.plansToRemove = [];
    $scope.plansToAdd = [];
    $scope.statutesToRemove = [];
    $scope.statutesToAdd = [];
    $scope.ingressionsToRemove = [];
    $scope.ingressionsToAdd = [];
    
    //DegreeCurricularPlan Functions
    $scope.selectAllPlans = function(event) {
	    var checkbox = event.target;
	    var action = (checkbox.checked ? 'add' : 'remove');
	    for( var i = 0; i < $scope.object.degreeCurricularPlans.length; i++) {
		    $scope.updateSelectedPlan(action, $scope.object.degreeCurricularPlans[i]);
	    }
    };
    $scope.updateSelectionPlan = function(event, plan) {
	    var checkbox = event.target;
	    var action = (checkbox.checked ? 'add' : 'remove');
	    $scope.updateSelectedPlan(action, plan);
    };
    $scope.updateSelectedPlan = function (action, id) {
	    if (action === 'add' && $scope.plansToRemove.indexOf(id) === -1) {
	        $scope.plansToRemove.push(id)
 	    }
        if (action === 'remove' && $scope.plansToRemove.indexOf(id) !== -1) {
            $scope.plansToRemove.splice($scope.plansToRemove.indexOf(id), 1);
        }	   
    };
    $scope.getSelectedClassForPlan = function(plan) {
	    return $scope.isPlanSelected(plan) ? 'selected' : '';
	};
	$scope.isPlanSelected = function(plan) {
	    return $scope.plansToRemove.indexOf(plan) >= 0;
	};
	$scope.isAllPlansSelected = function() {
	    return $scope.plansToRemove.length === $scope.object.degreeCurricularPlans.length;
	};
    $scope.addDegreeCurricularPlan = function(model) {
	    //Expected the id value and not an object with id attribute
	    angular.forEach($scope.plansToAdd, function (plan, index) {
		    $scope.plansToAdd[index] = plan.id;
	    });

	    url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.ADD_ALL_CURRICULAR_PLAN_URL %>/${academicEnrolmentPeriod.externalId}/';
        $('form[name="formPlan"]').find('input[name="postback"]').attr('value', url);
        
        $scope.form = $scope.formPlan;
        $scope.postBack(model);
        
        $scope.plansToAdd = [];
    };
	$scope.deleteSelectedDegreeCurricularPlans = function () {
	    url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.REMOVE_ALL_CURRICULAR_PLAN_URL %>/${academicEnrolmentPeriod.externalId}';
	    $('form[name="formPlan"]').find('input[name="postback"]').attr('value', url);
	    $('#deletePlanMessage').text('<spring:message code="label.AcademicEnrolmentPeriod.DegreeCurricularPlan.confirmRemove.all" />');
	    $('#deletePlanModal').modal('toggle');
	};
    $scope.deleteDegreeCurricularPlan = function(degreeCurricularPlan, model) {
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.REMOVE_CURRICULAR_PLAN_URL%>/${academicEnrolmentPeriod.externalId}/' + degreeCurricularPlan;
        $('form[name="formPlan"]').find('input[name="postback"]').attr('value', url);
        $('#deletePlanMessage').text('<spring:message code="label.AcademicEnrolmentPeriod.DegreeCurricularPlan.confirmRemove" />');
        $('#deletePlanModal').modal('toggle');
    }
    $scope.submitDeletePlan = function() {
        $('#deletePlanModal').modal('toggle');  
        $scope.form = $scope.formPlan;
        $scope.postBack(null);
        $scope.plansToRemove = [];
    };
    $scope.getPlanName = function(id) {
        var name;
        angular.forEach(
                $scope.object.degreeCurricularPlanDataSource,
                function(curricularPlan) {
                    if (curricularPlan.id == id) {
                        name = curricularPlan.text;
                    }
                }, id, name)
        return name;
    }
    //StatuteType Functions    
    $scope.selectAllStatutes = function(event) {
        var checkbox = event.target;
        var action = (checkbox.checked ? 'add' : 'remove');
        for( var i = 0; i < $scope.object.statutesTypes.length; i++) {
            $scope.updateSelectedStatute(action, $scope.object.statutesTypes[i]);
        }
    };
    $scope.updateSelectionStatute = function(event, plan) {
        var checkbox = event.target;
        var action = (checkbox.checked ? 'add' : 'remove');
        $scope.updateSelectedStatute(action, plan);
    };
    $scope.updateSelectedStatute = function (action, id) {
        if (action === 'add' && $scope.statutesToRemove.indexOf(id) === -1) {
            $scope.statutesToRemove.push(id)
        }
        if (action === 'remove' && $scope.statutesToRemove.indexOf(id) !== -1) {
            $scope.statutesToRemove.splice($scope.statutesToRemove.indexOf(id), 1);
        }
    };
    $scope.getSelectedClassForStatute = function(statute) {
        return $scope.isStatuteSelected(statute) ? 'selected' : '';
    };
    $scope.isStatuteSelected = function(statute) {
        return $scope.statutesToRemove.indexOf(statute) >= 0;
    };
    $scope.isAllStatutesSelected = function() {
        return $scope.statutesToRemove.length === $scope.object.statutesTypes.length;
    };
    $scope.addStatuteType = function(model) {
        //Expected the id value and not an object with id attribute
        angular.forEach($scope.statutesToAdd, function (statute, index) {
            $scope.statutesToAdd[index] = statute.id;
        });
        
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.ADD_ALL_STATUTE_TYPE_URL%>/${academicEnrolmentPeriod.externalId}/';
        $('form[name="formStatute"]').find('input[name="postback"]').attr('value', url);
        
        $scope.form = $scope.formStatute;
        $scope.postBack(model);
        
        $scope.statutesToAdd = [];
    };
    $scope.deleteSelectedStatuteTypes = function () {
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.REMOVE_ALL_STATUTE_TYPE_URL %>/${academicEnrolmentPeriod.externalId}';
        $('form[name="formStatute"]').find('input[name="postback"]').attr('value', url);
        $('#deleteStatuteMessage').text('<spring:message code="label.AcademicEnrolmentPeriod.StatuteType.confirmRemove.all" />');
        $('#deleteStatuteModal').modal('toggle');
    };
    $scope.deleteStatuteType = function(statuteType, model) {
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.REMOVE_STATUTE_TYPE_URL%>/${academicEnrolmentPeriod.externalId}/' + statuteType;
        $('form[name="formStatute"]').find('input[name="postback"]').attr('value', url);
        $('#deleteStatuteMessage').text('<spring:message code="label.AcademicEnrolmentPeriod.StatuteType.confirmRemove" />');
        $('#deleteStatuteModal').modal('toggle');
    }
    $scope.submitDeleteStatute = function() {
        $('#deleteStatuteModal').modal('toggle');  
        $scope.form = $scope.formStatute;
        $scope.postBack(null);         
        $scope.statutesToRemove = [];
    };
    $scope.getStatuteName = function(id) {
        var name;
        angular.forEach(
                $scope.object.statuteTypeDataSource,
                function(statuteType) {
                    if (statuteType.id == id) {
                        name = statuteType.text;
                    }
                }, id, name)
        return name;
    }
    //IngressionType Functions    
    $scope.selectAllIngressions = function(event) {
        var checkbox = event.target;
        var action = (checkbox.checked ? 'add' : 'remove');
        for( var i = 0; i < $scope.object.ingressionTypes.length; i++) {
            $scope.updateSelectedIngression(action, $scope.object.ingressionTypes[i]);
        }
    };
    $scope.updateSelectionIngression = function(event, ingression) {
        var checkbox = event.target;
        var action = (checkbox.checked ? 'add' : 'remove');
        $scope.updateSelectedIngression(action, ingression);
    };
    $scope.updateSelectedIngression = function (action, id) {
        if (action === 'add' && $scope.ingressionsToRemove.indexOf(id) === -1) {
            $scope.ingressionsToRemove.push(id)
        }
        if (action === 'remove' && $scope.ingressionsToRemove.indexOf(id) !== -1) {
            $scope.ingressionsToRemove.splice($scope.ingressionsToRemove.indexOf(id), 1);
        }
    };
    $scope.getSelectedClassForIngression = function(ingression) {
        return $scope.isIngressionSelected(ingression) ? 'selected' : '';
    };
    $scope.isIngressionSelected = function(ingression) {
        return $scope.ingressionsToRemove.indexOf(ingression) >= 0;
    };
    $scope.isAllIngressionsSelected = function() {
        return $scope.ingressionsToRemove.length === $scope.object.ingressionTypes.length;
    };
    $scope.addIngressionType = function(model) {
        //Expected the id value and not an object with id attribute
        angular.forEach($scope.ingressionsToAdd, function (ingression, index) {
            $scope.ingressionsToAdd[index] = ingression.id;
        });
        
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.ADD_ALL_INGRESSION_TYPE_URL%>/${academicEnrolmentPeriod.externalId}/';
        $('form[name="formIngression"]').find('input[name="postback"]').attr('value', url);
        
        $scope.form = $scope.formIngression;
        $scope.postBack(model);
        
        $scope.ingressionsToAdd = [];
    };
    $scope.deleteSelectedIngressionTypes = function () {
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.REMOVE_ALL_INGRESSION_TYPE_URL %>/${academicEnrolmentPeriod.externalId}';
        $('form[name="formIngression"]').find('input[name="postback"]').attr('value', url);
        $('#deleteIngressionMessage').text('<spring:message code="label.AcademicEnrolmentPeriod.IngressionType.confirmRemove.all" />');
        $('#deleteIngressionModal').modal('toggle');
    };
    $scope.deleteIngressionType = function(ingressionType, model) {
        url = '${pageContext.request.contextPath}<%= AcademicEnrolmentPeriodController.REMOVE_INGRESSION_TYPE_URL%>/${academicEnrolmentPeriod.externalId}/' + ingressionType;
        $('form[name="formIngression"]').find('input[name="postback"]').attr('value', url);
        $('#deleteIngressionMessage').text('<spring:message code="label.AcademicEnrolmentPeriod.IngressionType.confirmRemove" />');
        $('#deleteIngressionModal').modal('toggle');
    }
    $scope.submitDeleteIngression = function() {
        $('#deleteIngressionModal').modal('toggle');  
        $scope.form = $scope.formIngression;
        $scope.postBack(null);         
        $scope.ingressionsToRemove = [];
    };
    $scope.getIngressionName = function(id) {
        var name;
        angular.forEach(
                $scope.object.ingressionTypeDataSource,
                function(ingressionType) {
                    if (ingressionType.id == id) {
                        name = ingressionType.text;
                    }
                }, id, name)
        return name;
    }
    
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
    
    
    
}]).filter('filterMultipleValues', function() {
    return function(inputArray, otherArray) {
	    var items = {
		    array: otherArray,
		    output: []
	    };
	    angular.forEach(inputArray, function(value,key) {
		    if(this.array.containsId(value.id) === -1) {
			    this.output.push(value);
		    }
	    }, items);
	    return items.output;
    };
});
</script>


<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.details" />
        </h3>
    </div>
    <div class="panel-body">
        <form name='formRead' ng-app="angularApp" ng-controller="angularController" method="get" class="form-horizontal"
             action='#'>
             
                <input name="postback" type="hidden" value="#" />
                <input name="bean" type="hidden" value="{{ object }}" />
            
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.type" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.enrolmentPeriodType.descriptionI18N.content }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.executionSemester" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.executionSemester.qualifiedName }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.startDate" /></th>
                        <td><joda:format value='${academicEnrolmentPeriod.startDate}' style='SM' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.endDate" /></th>
                        <td><joda:format value='${academicEnrolmentPeriod.endDate}' style='SM' /></td>
                    </tr>                    
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.firstTimeRegistration" /></th>
                        <td>
                            <c:if test="${academicEnrolmentPeriod.firstTimeRegistration}">
                                <spring:message code="label.true" />
                            </c:if> <c:if test="${not academicEnrolmentPeriod.firstTimeRegistration}">
                                <spring:message code="label.false" />
                            </c:if>
                        </td>
                    </tr> 
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes" /></th>
                        <td>
                            <c:if test="${academicEnrolmentPeriod.restrictToSelectedStatutes}">
                                <spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.true" />
                            </c:if> <c:if test="${not academicEnrolmentPeriod.restrictToSelectedStatutes}">
                                <spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedStatutes.false" />
                            </c:if>
                        </td>
                    </tr>                                       
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedIngressions" /></th>
                        <td>
                            <c:if test="${academicEnrolmentPeriod.restrictToSelectedIngressionTypes}">
                                <spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedIngressions.true" />
                            </c:if> <c:if test="${not academicEnrolmentPeriod.restrictToSelectedIngressionTypes}">
                                <spring:message code="label.AcademicEnrolmentPeriod.restrictToSelectedIngressions.false" />
                            </c:if>
                        </td>
                    </tr>                                       
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.minStudentNumber" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.minStudentNumber }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.maxStudentNumber" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.maxStudentNumber }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.curricularYear" /></th>
                        <td><c:out value='${ academicEnrolmentPeriod.curricularYear }' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.AcademicEnrolmentPeriod.schoolClassSelectionMandatory" /></th>
                        <td>
                            <c:if test="${academicEnrolmentPeriod.schoolClassSelectionMandatory}">
                                <spring:message code="label.true" />
                            </c:if> <c:if test="${not academicEnrolmentPeriod.schoolClassSelectionMandatory}">
                                <spring:message code="label.false" />
                            </c:if>
                        </td>
                    </tr>                     
                </tbody>
            </table>
        </form>
    </div>
</div>

<form name="formPlan" ng-app="angularApp" ng-controller="angularController" method="post" class="form-horizontal" 
      action='#'>          
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.AcademicEnrolmentPeriod.degreeCurricularPlans" />
            </h3>
        </div>
        <input type="hidden" name="postback" value='#' />
        <input type="hidden" name="plansToAdd" ng-repeat="plan in plansToAdd" value="{{ plan }}" />
        <input type="hidden" name="plansToRemove" ng-repeat="plan in plansToRemove" value="{{ plan }}" />
        <input name="bean" type="hidden" value="{{ object }}" />
        
        <div class="panel panel-body">
            <div class="form-group row">
                <div class="col-sm-9">
                    <div id="AcademicEnrolmentPeriod_degreeCurricularPlans" name="AcademicEnrolmentPeriod_degreeCurricularPlans" class="ui-select-container ui-select-bootstrap dropdown" 
                        ng-dropdown-multiselect="" options="object.degreeCurricularPlanDataSource"
                        selected-model="plansToAdd" extra-settings="multiSelectOptions" translation-texts="translationTexts" >
                    </div>
                </div>
                <div class="col-sm-3">
                    <button type="button" class="btn btn-default" ng-click="addDegreeCurricularPlan($model)" ng-disabled="plansToAdd.length === 0">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                </div>               
            </div>
        </div>
        <div class="panel panel-body">
              <table id="degreeCurricularPlansTable"
                class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <%-- Check Column --%>
                        <th style="width: 35px;">
                            <input type="checkbox" ng-click="selectAllPlans($event)" ng-checked="isAllPlansSelected()" />
                        </th>
                        <th><spring:message code="label.AcademicEnrolmentPeriod.degreeCurricularPlan" /></th>
                        <!-- operation column -->
                        <th style="width: 60px"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="plan in object.degreeCurricularPlans" ng-class="getSelectedClassForPlan(plan)">
                        <td>
                            <input class="form-control" type="checkbox" ng-checked="isPlanSelected(plan)" ng-click="updateSelectionPlan($event, plan)" />
                        </td>
                        <td>{{ getPlanName(plan) }}</td>
                        <td>
                            <a class="btn btn-danger" ng-click="deleteDegreeCurricularPlan(plan, $model)">
                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                &nbsp;
                                <spring:message code="label.event.delete" /> 
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="panel panel-body">
            <a class="btn btn-danger" ng-click="deleteSelectedDegreeCurricularPlans()" ng-disabled="plansToRemove.length === 0">
                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                &nbsp;
                <spring:message code="label.event.delete.all" /> 
            </a>
        </div>
    </div>
    
    <div class="modal fade" id="deletePlanModal">
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
                    <p id="deletePlanMessage">
                        <spring:message
                            code="label.AcademicEnrolmentPeriod.DegreeCurricularPlan.confirmRemove" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger" type="button" ng-click="submitDeletePlan()">
                        <spring:message code="label.event.delete" />
                    </button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.modal -->
    
</form>

<form name="formStatute" ng-app="angularApp" ng-controller="angularController" method="post" class="form-horizontal" 
      action='#'>          
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.AcademicEnrolmentPeriod.statuteTypes" />
            </h3>
        </div>
        <input type="hidden" name="postback" value='#' />
        <input type="hidden" name="statutesToAdd" ng-repeat="statute in statutesToAdd" value="{{ statute }}" />
        <input type="hidden" name="statutesToRemove" ng-repeat="statute in statutesToRemove" value="{{ statute }}" />
        <input name="bean" type="hidden" value="{{ object }}" />
        
        <div class="panel panel-body">
            <div class="form-group row">
                <div class="col-sm-7">
                    <div id="AcademicEnrolmentPeriod_statuteTypes" name="AcademicEnrolmentPeriod_statuteTypes" class="ui-select-container ui-select-bootstrap dropdown" 
                        ng-dropdown-multiselect="" options="object.statuteTypeDataSource"
                        selected-model="statutesToAdd" extra-settings="multiSelectOptions" translation-texts="translationTexts" >
                    </div>
                </div>
                <div class="col-sm-5">
                    <button type="button" class="btn btn-default" ng-click="addStatuteType($model)" ng-disabled="statutesToAdd.length === 0">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                </div>               
            </div>
        </div>
        <div class="panel panel-body">
              <table id="statuteTypeTable"
                class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <%-- Check Column --%>
                        <th style="width: 35px;">
                            <input type="checkbox" ng-click="selectAllStatutes($event)" ng-checked="isAllStatutesSelected()" />                        
                        </th>
                        <th><spring:message code="label.AcademicEnrolmentPeriod.statuteType" /></th>
                        <!-- operation column -->
                        <th style="width: 60px"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="statute in object.statutesTypes" ng-class="getSelectedClassForStatute(statute)">
                        <td>
                            <input class="form-control" type="checkbox" ng-checked="isStatuteSelected(statute)" ng-click="updateSelectionStatute($event, statute)" />
                        </td>
                        <td>{{ getStatuteName(statute) }}</td>
                        <td>
                            <a class="btn btn-danger" ng-click="deleteStatuteType(statute, $model)">
                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                &nbsp;
                                <spring:message code="label.event.delete" /> 
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="panel panel-body">
            <a class="btn btn-danger" ng-click="deleteSelectedStatuteTypes()" ng-disabled="statutesToRemove.length === 0">
                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                &nbsp;
                <spring:message code="label.event.delete.all" /> 
            </a>
        </div>
    </div>
    
    <div class="modal fade" id="deleteStatuteModal">
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
                    <p id="deleteStatuteMessage">
                        <spring:message
                            code="label.AcademicEnrolmentPeriod.StatuteType.confirmRemove" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger" type="button" ng-click="submitDeleteStatute()">
                        <spring:message code="label.event.delete" />
                    </button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.modal -->
    
</form>

<form name="formIngression" ng-app="angularApp" ng-controller="angularController" method="post" class="form-horizontal" 
      action='#'>          
    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.AcademicEnrolmentPeriod.ingressionTypes" />
            </h3>
        </div>
        <input type="hidden" name="postback" value='#' />
        <input type="hidden" name="ingressionsToAdd" ng-repeat="ingression in ingressionsToAdd" value="{{ ingression }}" />
        <input type="hidden" name="ingressionsToRemove" ng-repeat="ingression in ingressionsToRemove" value="{{ ingression }}" />
        <input name="bean" type="hidden" value="{{ object }}" />
        
        <div class="panel panel-body">
            <div class="form-group row">
                <div class="col-sm-7">
                    <div id="AcademicEnrolmentPeriod_ingressionTypes" name="AcademicEnrolmentPeriod_ingressionTypes" class="ui-select-container ui-select-bootstrap dropdown" 
                        ng-dropdown-multiselect="" options="object.ingressionTypeDataSource"
                        selected-model="ingressionsToAdd" extra-settings="multiSelectOptions" translation-texts="translationTexts" >
                    </div>
                </div>
                <div class="col-sm-5">
                    <button type="button" class="btn btn-default" ng-click="addIngressionType($model)" ng-disabled="ingressionsToAdd.length === 0">
                        <span class="glyphicon glyphicon-plus-sign" aria-hidden="true" ></span> &nbsp;<spring:message code="label.event.add" />
                    </button>
                </div>               
            </div>
        </div>
        <div class="panel panel-body">
              <table id="statuteTypeTable"
                class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <%-- Check Column --%>
                        <th style="width: 35px;">
                            <input type="checkbox" ng-click="selectAllIngressions($event)" ng-checked="isAllIngressionsSelected()" />                        
                        </th>
                        <th><spring:message code="label.AcademicEnrolmentPeriod.ingressionType" /></th>
                        <!-- operation column -->
                        <th style="width: 60px"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="ingression in object.ingressionTypes" ng-class="getSelectedClassForIngression(ingression)">
                        <td>
                            <input class="form-control" type="checkbox" ng-checked="isIngressionSelected(ingression)" ng-click="updateSelectionIngression($event, ingression)" />
                        </td>
                        <td>{{ getIngressionName(ingression) }}</td>
                        <td>
                            <a class="btn btn-danger" ng-click="deleteIngressionType(ingression, $model)">
                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                &nbsp;
                                <spring:message code="label.event.delete" /> 
                            </a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="panel panel-body">
            <a class="btn btn-danger" ng-click="deleteSelectedIngressionTypes()" ng-disabled="ingressionsToRemove.length === 0">
                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                &nbsp;
                <spring:message code="label.event.delete.all" /> 
            </a>
        </div>
    </div>
    
    <div class="modal fade" id="deleteIngressionModal">
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
                    <p id="deleteIngressionMessage">
                        <spring:message
                            code="label.AcademicEnrolmentPeriod.IngressionType.confirmRemove" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger" type="button" ng-click="submitDeleteIngression()">
                        <spring:message code="label.event.delete" />
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

angular.bootstrap($('form[name="formPlan"]')[0],['angularApp']);
angular.bootstrap($('form[name="formStatute"]')[0],['angularApp']);
angular.bootstrap($('form[name="formIngression"]')[0],['angularApp']);

$(document).ready(function() {
    
});
</script>

