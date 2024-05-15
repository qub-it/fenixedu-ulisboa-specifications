<%@page import="org.fenixedu.legalpt.ui.raides.RaidesConfigurationController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<jsp:include page="../../commons/angularInclude.jsp" />


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageRaidesConfiguration.edit" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= RaidesConfigurationController.READ_URL %>">
		<spring:message code="label.event.back" />
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

angular.module('angularApp', ['ngSanitize', 'ui.select']).controller('angularController', ['$scope', function($scope) {

 	$scope.object=${beanJson};
 	$scope.postBack = createAngularPostbackFunction($scope);
 	
	$scope.booleanvalues = [
    		{ name : '<spring:message code="label.no"/>', value : false },
    		{ name : '<spring:message code="label.yes"/>', value : true } 
	];
	                 	
 	
}]);
</script>

<form id="form" name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
	action='${pageContext.request.contextPath}<%= RaidesConfigurationController.EDIT_URL  %>'>

	<input name="bean" type="hidden" value="{{ object }}" />
	
	
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.institutionCode" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.institutionCode" name="institutionCode" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.interlocutorPhone" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorPhone" name="interlocutorPhone" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.passwordToZip" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.passwordToZip" name="passwordToZip" />
				</div>
			</div>
						
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.defaultDistrictOfResidence" />
				</div>
				
				<div class="col-sm-10">
					<ui-select id="defaultDistrictOfResidenceSelect" name="defaultdistrictofresidence"
						ng-model="$parent.object.defaultDistrictOfResidence" theme="bootstrap" >
						<ui-select-match>{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.defaultDistrictOfResidenceDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>			
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.reportGraduatedWithoutConclusionProcess" />
				</div>

				<div class="col-sm-10">
                    <select
                        name="reportGraduatedWithoutConclusionProcess" class="form-control"
                        ng-model="object.reportGraduatedWithoutConclusionProcess"
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
                    </select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.integratedMasterFirstCycleGraduatedReportOption" />
				</div>

				<div class="col-sm-10">

					<ui-select id="integratedMasterFirstCycleGraduatedReportOptionSelect" name="integratedMasterFirstCycleGraduatedReportOption"
						ng-model="$parent.object.integratedMasterFirstCycleGraduatedReportOption" theme="bootstrap" >
						<ui-select-match>{{$select.selected.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.integratedMasterFirstCycleGraduatedReportOptionsDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>

				</div>			
			</div>
			
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.grantOwnerStatuteTypes" />
				</div>

				<div class="col-sm-10">
					<ui-select	id="grantOwnerStatuteTypesSelect" name="grantOwnerStatuteTypes" ng-model="$parent.object.grantOwnerStatuteTypes" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices	repeat="statute.id as statute in object.grantOwnerStatuteTypesDataSource | filter: $select.search">
							<span ng-bind-html="statute.text | highlight: $select.search"></span>
						</ui-select-choices> 
					</ui-select>
				</div>
			</div>
			
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.enrolledAgreements" />
				</div>

				<div class="col-sm-10">
					<ui-select id="enrolledAgreementsSelect" name="enrolledAgreements"
						ng-model="$parent.object.enrolledAgreements" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="registrationProtocol.id as registrationProtocol in object.registrationProtocolsDataSource | filter: $select.search">
							<span ng-bind-html="registrationProtocol.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.mobilityAgreements" />
				</div>

				<div class="col-sm-10">
					<ui-select id="mobilityAgreementsSelect" name="mobilityAgreements"
						ng-model="$parent.object.mobilityAgreements" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="registrationProtocol.id as registrationProtocol in object.registrationProtocolsDataSource | filter: $select.search">
							<span ng-bind-html="registrationProtocol.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.degreeChangeIngressions" />
				</div>

				<div class="col-sm-10">
					<ui-select id="degreeChangeIngressionsSelect" name="degreeChangeIngressions"
						ng-model="$parent.object.degreeChangeIngressions" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.ingressionTypesDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.degreeTransferIngressions" />
				</div>

				<div class="col-sm-10">
					<ui-select id="degreeTransferIngressionsSelect" name="degreeTransferIngressions"
						ng-model="$parent.object.degreeTransferIngressions" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.ingressionTypesDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.generalAccessRegimeIngressions" />
				</div>

				<div class="col-sm-10">
					<ui-select id="ingressionsForGeneralAccessRegimeSelect" name="ingressionsForGeneralAccessRegime"
						ng-model="$parent.object.ingressionsForGeneralAccessRegime" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.ingressionTypesDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
			
			
		</div>
		
		<div class="panel-footer">
			<input type="submit" class="btn btn-default btn-xs" value="<spring:message code="label.submit" />" />
		</div>
	</div>	
	
</form>

<script>

$(document).ready(function() {
});

</script>
