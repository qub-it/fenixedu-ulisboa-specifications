<%@page import="org.fenixedu.ulisboa.specifications.ui.legal.report.raides.RaidesConfigurationController"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesConfiguration"%>
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
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesInstance.enrolledAgreements" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="enrolledAgreementsSelect" name="enrolledAgreements"
						ng-model="$parent.object.enrolledAgreements" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="registrationProtocol.id as registrationProtocol in object.registrationProtocolsDataSource | filter: $select.search">
							<span ng-bind-html="registrationProtocol.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
		</div>
	</div>
	
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesInstance.mobilityAgreements" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="mobilityAgreementsSelect" name="mobilityAgreements"
						ng-model="$parent.object.mobilityAgreements" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="registrationProtocol.id as registrationProtocol in object.registrationProtocolsDataSource | filter: $select.search">
							<span ng-bind-html="registrationProtocol.text | highlight: $select.search"></span>
						</ui-select-choices>interstellar streaming
					</ui-select>
				</div>
			</div>
		</div>
	</div>
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesInstance.degreeChangeIngressions" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="degreeChangeIngressionsSelect" name="degreeChangeIngressions"
						ng-model="$parent.object.degreeChangeIngressions" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.ingressionTypesDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
		</div>
	</div>
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesInstance.degreeTransferIngressions" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="degreeTransferIngressionsSelect" name="degreeTransferIngressions"
						ng-model="$parent.object.degreeTransferIngressions" theme="bootstrap" multiple="true">
						<ui-select-match>{{$item.text}}</ui-select-match> 
						<ui-select-choices repeat="i.id as i in object.ingressionTypesDataSource | filter: $select.search">
							<span ng-bind-html="i.text | highlight: $select.search"></span>
						</ui-select-choices>
					</ui-select>
				</div>
			</div>
		</div>
	</div>
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesInstance.generalAccessRegimeIngressions" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
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
	</div>
	
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
		</div>
		
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.formsAvailableToStudents" />
				</div>

				<div class="col-sm-10">
                    <select 
                        name="formsAvailableToStudents" class="form-control"
                        ng-model="object.formsAvailableToStudents"
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
                    </select>
				</div>
			</div>
		</div>

		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.blueRecordStartMessageContent" /> (PT)
				</div>

				<div class="col-sm-10">
					<textarea rows="20" class="form-control" ng-model="object.blueRecordStartMessageContentPt" 
						name="blueRecordStartMessageContentPt" >
					</textarea>
				</div>
			</div>
		</div>
		
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesInstance.blueRecordStartMessageContent" /> (EN)
				</div>

				<div class="col-sm-10">
					<textarea rows="20" class="form-control" ng-model="object.blueRecordStartMessageContentEn" 
						name="blueRecordStartMessageContentEn" >
					</textarea>
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
