<%@page import="org.fenixedu.ulisboa.specifications.ui.managemobilityregistrationinformation.MobilityRegistrationInformationController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
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
		<spring:message code="label.manageMobilityRegistrationInformation.updateMobilityRegistrationInformation" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= MobilityRegistrationInformationController.SEARCH_URL %>/${registration.externalId}">
		<spring:message code="label.event.back" />
	</a>
	|&nbsp;&nbsp;
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

angular.module('angularAppMobilityRegistrationInformation', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('MobilityRegistrationInformationController', ['$scope', function($scope) {

 	$scope.object=${mobilityRegistrationInformationBeanJson};

	
	$scope.booleanvalues = [ {
		name : '<spring:message code="label.yes"/>',
		value : true
	}, {
		name : '<spring:message code="label.no"/>',
		value : false
	} ];
 	
 	
 	$scope.postBack = createAngularPostbackFunction($scope);
 	
 	$scope.onDegreeChange = function(degree, model) {
 		$scope.object.degreeCurricularPlan = '';
 		$scope.object.branchCourseGroup = '';
 		$scope.postBack(model);
 	}
 	
 	$scope.onDegreeCurricularPlanChange = function(degreeCurricularPlan, model) {
 		$scope.object.branchCourseGroup = '';
 		$scope.postBack(model);
 	}
 	
 	$scope.onCountryUnitChange = function(degreeCurricularPlan, model) {
 		$scope.object.foreignInstitutionUnit = '';
 		$scope.postBack(model);
 	}
 	
 	$scope.submitForm = function() {
		$('#updateForm').submit();
	}
 	
 	
}]);
</script>

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
						<th scope="row" class="col-xs-3">
							<spring:message code="label.MobilityRegistrationInformation.studentNumber" />
						</th>
						<td>
							<c:out value='${registration.student.number}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.MobilityRegistrationInformation.studentName" />
						</th>
						<td>
							<c:out value='${registration.student.person.name}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.MobilityRegistrationInformation.degreeName" />
						</th>
						<td>
							<c:out value='${registration.degree.presentationName}' />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<form id="updateForm" name='form' method="post" class="form-horizontal" ng-app="angularAppMobilityRegistrationInformation"
	ng-controller="MobilityRegistrationInformationController"
	action='${pageContext.request.contextPath}<%= MobilityRegistrationInformationController.UPDATE_URL  %>/${mobilityRegistrationInformationBean.mobilityRegistrationInformation.externalId}'>
	
	<input type="hidden" name="postback" value='${pageContext.request.contextPath}<%=MobilityRegistrationInformationController.UPDATEPOSTBACK_URL%>' />
	

	<input name="bean" type="hidden" value="{{ object }}" />
	<div class="panel panel-default">
		<div class="panel-body">
		
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.incoming" />
				</div>

				<div class="col-sm-8">
					<select id="mobilityRegistrationInformation_incoming" name="incoming" class="form-control" ng-model="object.incoming"
						ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues" ng-disabled="true">
					</select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.national" />
				</div>
				
				<div class="col-sm-8">
					<select id="national" name="national" class="form-control" ng-model="object.national" ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.programDuration" />
				</div>

				<div class="col-sm-8">
					<ui-select id="mobilityRegistrationInformation_programDuration" class="" name="programduration"
						ng-model="$parent.object.programDuration" theme="bootstrap" ng-disabled="disabled"> <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
					<ui-select-choices repeat="programDuration.id as programDuration in object.programDurationDataSource | filter: $select.search">
					<span ng-bind-html="programDuration.text | highlight: $select.search"></span> </ui-select-choices> </ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.begin" />
				</div>

				<div class="col-sm-8">
					<%-- Relation to side 1 drop down rendered in input --%>
					<ui-select id="mobilityRegistrationInformation_begin" class="" name="begin" ng-model="$parent.object.begin" theme="bootstrap"
						ng-disabled="disabled"> <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> <ui-select-choices
						repeat="begin.id as begin in object.beginDataSource | filter: $select.search"> <span
						ng-bind-html="begin.text | highlight: $select.search"></span> </ui-select-choices> </ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.end" />
				</div>

				<div class="col-sm-8">
					<%-- Relation to side 1 drop down rendered in input --%>
					<ui-select id="mobilityRegistrationInformation_end" class="" name="end" ng-model="$parent.object.end" theme="bootstrap"
						ng-disabled="disabled"> 
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices
							repeat="end.id as end in object.endDataSource | filter: $select.search">
							<span ng-bind-html="end.text | highlight: $select.search"></span> 
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.beginDate" />
				</div>

				<div class="col-sm-8">
					<input class="form-control" type="text" bennu-date="object.beginDate"/>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.endDate" />
				</div>

				<div class="col-sm-8">
					<input class="form-control" type="text" bennu-date="object.endDate"/>
				</div>
			</div>
			
			<div class="form-group row" ng-show="object.incoming === true">
				<div class="col-sm-3 control-label" >
					<spring:message code="label.MobilityRegistrationInformation.originMobilityProgrammeLevel" />
				</div>

				<div class="col-sm-8">
					<%-- Relation to side 1 drop down rendered in input --%>
					<ui-select id="mobilityRegistrationInformation_originMobilityProgrammeLevel" class="" name="originmobilityprogrammelevel"
						ng-model="$parent.object.originMobilityProgrammeLevel" theme="bootstrap" ng-disabled="disabled">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
						<ui-select-choices repeat="mobilityProgrammeLevel.id as mobilityProgrammeLevel in object.mobilityProgrammeLevelDataSource | filter: $select.search">
							<span ng-bind-html="mobilityProgrammeLevel.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row" ng-show="object.incoming === true">
				<div class="col-sm-3 control-label" >
					<spring:message code="label.MobilityRegistrationInformation.otherOriginMobilityProgrammeLevel" />
				</div>

				<div class="col-sm-8">
                    <input id="mobilityRegistrationInformation_otherOriginMobilityProgrammeLevel" class="form-control" type="text" 
                    	ng-model="object.otherOriginMobilityProgrammeLevel" />
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.mobilityActivityType" />
				</div>

				<div class="col-sm-8">
					<%-- Relation to side 1 drop down rendered in input --%>
					<ui-select id="mobilityRegistrationInformation_mobilityActivityType" class="" name="mobilityactivitytype"
						ng-model="$parent.object.mobilityActivityType" theme="bootstrap" ng-disabled="disabled">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
						<ui-select-choices repeat="mobilityActivityType.id as mobilityActivityType in object.mobilityActivityTypeDataSource | filter: $select.search">
							<span ng-bind-html="mobilityActivityType.text | highlight: $select.search"></span> 
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.mobilityProgramType" />
				</div>

				<div class="col-sm-8">
					<%-- Relation to side 1 drop down rendered in input --%>
					<ui-select id="mobilityRegistrationInformation_mobilityProgramType" class="" name="mobilityprogramtype"
						ng-model="$parent.object.mobilityProgramType" theme="bootstrap" ng-disabled="disabled">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
						<ui-select-choices repeat="mobilityProgramType.id as mobilityProgramType in object.mobilityProgramTypeDataSource | filter: $select.search">
							<span ng-bind-html="mobilityProgramType.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row" ng-show="object.incoming === true">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.degreeBased" />
				</div>
				
				<div class="col-sm-8">
					<select id="degreeBased" name="degreeBased" class="form-control" ng-model="object.degreeBased" ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
					</select>
				</div>
			</div>
			
			<div ng-show="object.incoming === true && object.degreeBased === false">
			
				<div class="form-group row">
					<div class="col-sm-3 control-label"  >
						<spring:message code="label.MobilityRegistrationInformation.mobilityScientificArea" />
					</div>
	
					<div class="col-sm-8">
						<%-- Relation to side 1 drop down rendered in input --%>
						<ui-select id="mobilityRegistrationInformation_mobilityScientificArea" class="" name="mobilityscientificarea"
							ng-model="$parent.object.mobilityScientificArea" theme="bootstrap" ng-disabled="disabled">
							<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="mobilityScientificArea.id as mobilityScientificArea in object.mobilityScientificAreaDataSource | filter: $select.search">
								<span ng-bind-html="mobilityScientificArea.text | highlight: $select.search"></span>
							</ui-select-choices>
						</ui-select>
					</div>
				</div>
				
				
				<div class="form-group row">
					<div class="col-sm-3 control-label" >
						<spring:message code="label.MobilityRegistrationInformation.incomingMobilityProgrammeLevel" />
					</div>
	
					<div class="col-sm-8">
						<%-- Relation to side 1 drop down rendered in input --%>
						<ui-select id="mobilityRegistrationInformation_incomingMobilityProgrammeLevel" class="" name="incomingmobilityprogrammelevel"
							ng-model="$parent.object.incomingMobilityProgrammeLevel" theme="bootstrap" ng-disabled="disabled">
							<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="mobilityProgrammeLevel.id as mobilityProgrammeLevel in object.mobilityProgrammeLevelDataSource | filter: $select.search">
								<span ng-bind-html="mobilityProgrammeLevel.text | highlight: $select.search"></span>
							</ui-select-choices>
						</ui-select>
					</div>
				</div>
				
				<div class="form-group row">
					<div class="col-sm-3 control-label" >
						<spring:message code="label.MobilityRegistrationInformation.otherIncomingMobilityProgrammeLevel" />
					</div>
	
					<div class="col-sm-8">
	                    <input id="mobilityRegistrationInformation_otherIncomingMobilityProgrammeLevel" class="form-control" type="text" 
	                    	ng-model="object.otherIncomingMobilityProgrammeLevel" />
					</div>
				</div>
						
			</div>
			
			<div ng-show="object.incoming === true && object.degreeBased === true">
			
				<div class="form-group row">
					<div class="col-sm-3 control-label"  >
						<spring:message code="label.MobilityRegistrationInformation.degree" />
					</div>
	
					<div class="col-sm-8">
						
						<%-- Relation to side 1 drop down rendered in input --%>
						<ui-select id="mobilityRegistrationInformation_degree" class="" name="degree"
							ng-model="$parent.object.degree" theme="bootstrap" ng-disabled="disabled" on-select="onDegreeChange($item, $model)" on-remove="onDegreeChange($item, $model)" allow-clear="true">
							<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="degree.id as degree in object.degreeDataSource | filter: $select.search">
								<span ng-bind-html="degree.text | highlight: $select.search"></span>
							</ui-select-choices>
						</ui-select>
					</div>
				</div>
				
				
				<div class="form-group row">
					<div class="col-sm-3 control-label" >
						<spring:message code="label.MobilityRegistrationInformation.degreeCurricularPlan" />
					</div>
	
					<div class="col-sm-8">
						<%-- Relation to side 1 drop down rendered in input --%>
						<ui-select id="mobilityRegistrationInformation_degreeCurricularPlan" class="" name="degreeCurricularPlan"
							ng-model="$parent.object.degreeCurricularPlan" theme="bootstrap" ng-disabled="disabled" on-select="onDegreeCurricularPlanChange($item, $model)" on-remove="onDegreeCurricularPlanChange($item, $model)">
							<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="degreeCurricularPlan.id as degreeCurricularPlan in object.degreeCurricularPlanDataSource | filter: $select.search">
								<span ng-bind-html="degreeCurricularPlan.text | highlight: $select.search"></span>
							</ui-select-choices>
						</ui-select>
					</div>
				</div>
				
				<div class="form-group row">
					<div class="col-sm-3 control-label" >
						<spring:message code="label.MobilityRegistrationInformation.branchCourseGroup" />
					</div>
	
					<div class="col-sm-8">
						<%-- Relation to side 1 drop down rendered in input --%>
						<ui-select id="mobilityRegistrationInformation_branchCourseGroup" class="" name="branchCourseGroup"
							ng-model="$parent.object.branchCourseGroup" theme="bootstrap" ng-disabled="disabled">
							<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="branchCourseGroup.id as branchCourseGroup in object.branchCourseGroupDataSource | filter: $select.search">
								<span ng-bind-html="branchCourseGroup.text | highlight: $select.search"></span>
							</ui-select-choices>
						</ui-select>
					</div>
				</div>
						
			</div>
			
			<div class="form-group row">
				<div class="col-sm-3 control-label" >
					<spring:message code="label.MobilityRegistrationInformation.countryUnit" />
				</div>

				<div class="col-sm-8">
					<%-- Relation to side 1 drop down rendered in input --%>
					<ui-select id="mobilityRegistrationInformation_countryUnit" class="" name="countryUnit"
						ng-model="$parent.object.countryUnit" theme="bootstrap" ng-disabled="disabled" on-select="onCountryUnitChange($item, $model)" on-remove="onCountryUnitChange($item, $model)">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
						<ui-select-choices repeat="countryUnit.id as countryUnit in object.countryUnitDataSource | filter: $select.search">
							<span ng-bind-html="countryUnit.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			

			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.foreignInstitutionUnit" />
				</div>

				<div class="col-sm-8">
					<%-- Relation to side 1 drop down rendered in input --%>
					<ui-select id="mobilityRegistrationInformation_foreignInstitutionUnit" class="" name="foreigninstitutionunit"
						ng-model="$parent.object.foreignInstitutionUnit" theme="bootstrap" ng-disabled="disabled">
						<ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match>
						<ui-select-choices
							repeat="foreignInstitutionUnit.id as foreignInstitutionUnit in object.foreignInstitutionUnitDataSource | filter: $select.search">
							<span ng-bind-html="foreignInstitutionUnit.text | highlight: $select.search"></span> 
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MobilityRegistrationInformation.remarks" />
				</div>

				<div class="col-sm-8">
					<textarea id="mobilityRegistrationInformation_remarks"  rows="6" cols="6" class="form-control" ng-model="object.remarks"></textarea>
				</div>
			</div>
			
		</div>
		<div class="panel-footer">
			<button type="button" class="btn btn-primary" role="button" ng-click="submitForm()"><spring:message code="label.submit" /></button>
		</div>
	</div>
</form>

<script>
$(document).ready(function() {
	
});
</script>
