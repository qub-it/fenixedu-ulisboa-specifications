
<%@page import="org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesPeriodInputType"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.legal.report.raides.RaidesRequestsController"%>

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
		<spring:message code="label.manageRaidesRequests.create" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= RaidesRequestsController.SEARCH_URL %>">
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

angular.module('angularApp', ['ngSanitize', 'ui.select']).controller('angularController', ['$scope', function($scope) {

 	$scope.object=${beanJson};
 	$scope.postBack = createAngularPostbackFunction($scope);
 	
 	
	$scope.booleanvalues = [
		{ name : '<spring:message code="label.no"/>', value : false },
		{ name : '<spring:message code="label.yes"/>', value : true} 
	];
 	
 	$scope.cleanPeriods = function(periodType) {
		var i = $scope.object.periods.length;
		while(i--) {
			if($scope.object.periods[i].periodInputType === periodType) {
				$scope.object.periods.splice(i, 1);					
			}
		}
 	}
 	
}]);
</script>

<form id="form" name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
	action='${pageContext.request.contextPath}<%= RaidesRequestsController.CREATE_URL  %>'>

	<input name="bean" type="hidden" value="{{ object }}" />
	
	<jsp:include page="create_period_table.jsp">
		<jsp:param value="title.RaidesRequests.periodInputType.enrolled" name="periodTableTitle" />
		<jsp:param value="<%= RaidesPeriodInputType.ENROLLED.name() %>" name="periodType" />
	</jsp:include>

	<jsp:include page="create_period_table.jsp">
		<jsp:param value="title.RaidesRequests.periodInputType.graduated" name="periodTableTitle" />
		<jsp:param value="<%= RaidesPeriodInputType.GRADUATED.name() %>" name="periodType" />
	</jsp:include>

	<jsp:include page="create_period_table.jsp">
		<jsp:param value="title.RaidesRequests.periodInputType.internationalMobility" name="periodTableTitle" />
		<jsp:param value="<%= RaidesPeriodInputType.INTERNATIONAL_MOBILITY.name() %>" name="periodType" />
	</jsp:include>
	
	
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.agreementsForEnrolled" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="agreementsForEnrolledSelect" name="agreementsForEnrolled"
						ng-model="$parent.object.agreementsForEnrolled" theme="bootstrap" multiple="true">
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
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.agreementsForMobility" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="agreementsForEnrolledSelect" name="agreementsForMobility"
						ng-model="$parent.object.agreementsForMobility" theme="bootstrap" multiple="true">
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
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.ingressionsForDegreeChange" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="ingressionsForDegreeChangeSelect" name="ingressionsForDegreeChange"
						ng-model="$parent.object.ingressionsForDegreeChange" theme="bootstrap" multiple="true">
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
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.ingressionsForDegreeTransfer" /></h3>
		</div>
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-6">
					<ui-select id="ingressionsForDegreeTransferSelect" name="ingressionsForDegreeTransfer"
						ng-model="$parent.object.ingressionsForDegreeTransfer" theme="bootstrap" multiple="true">
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
			<h3 class="panel-title"><spring:message code="label.RaidesRequests.ingressionsForGeneralAccessRegime" /></h3>
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
					<spring:message code="label.RaidesRequests.institution" />
				</div>

				<div class="col-sm-10">
					{{ object.institutionName }}
				</div>
				
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.institutionCode" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.institutionCode" name="code" />
				</div>
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.moment" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.moment" name="code" />
				</div>
			</div>			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.interlocutorName" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorName" name="code" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.interlocutorEmail" />
				</div>

				<div class="col-sm-10">
					<input class="form-control" type="text" ng-model="object.interlocutorEmail" name="code" />
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.RaidesRequests.filterEntriesWithErrors" />
				</div>
				
				<div class="col-sm-10">
                    <select 
                        name="filterEntriesWithErrors" class="form-control"
                        ng-model="object.filterEntriesWithErrors"
                        ng-options="bvalue.value as bvalue.name for bvalue in booleanvalues">
                    </select>
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
