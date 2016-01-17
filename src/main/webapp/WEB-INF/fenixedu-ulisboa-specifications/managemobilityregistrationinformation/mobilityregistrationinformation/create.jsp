<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()} 
<%--${portal.toolkit()}--%>

<link href="${pageContext.request.contextPath}/static/specifications/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/specifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.manageMobilityRegistrationInformation.createMobilityRegistrationInformation" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/specifications/managemobilityregistrationinformation/mobilityregistrationinformation/"  ><spring:message code="label.event.back" /></a>
|&nbsp;&nbsp;</div>
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
						<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
  							${message}
  						</p>
					</c:forEach>
					
				</div>	
			</c:if>

<script>

angular.module('angularAppMobilityRegistrationInformation', ['ngSanitize', 'ui.select']).controller('MobilityRegistrationInformationController', ['$scope', function($scope) {

 	$scope.object=angular.fromJson('${mobilityRegistrationInformationBeanJson}');
	$scope.postBack = createAngularPostbackFunction($scope); 

	//Begin here of Custom Screen business JS - code
 	
}]);
</script>

<form name='form' method="post" class="form-horizontal"
	ng-app="angularAppMobilityRegistrationInformation" ng-controller="MobilityRegistrationInformationController"
	action='${pageContext.request.contextPath}/specifications/managemobilityregistrationinformation/mobilityregistrationinformation/create'>

	<input type="hidden" name="postback"
		value='${pageContext.request.contextPath}specifications/managemobilityregistrationinformation/mobilityregistrationinformation/createpostback' />
		
	<input name="bean" type="hidden" value="{{ object }}" />
<div class="panel panel-default">
  <div class="panel-body">
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.MobilityRegistrationInformation.incoming"/></div> 

<div class="col-sm-2">
<select id="mobilityRegistrationInformation_incoming" name="incoming" class="form-control" ng-model="object.incoming">
<option value="false"><spring:message code="label.no"/></option>
<option value="true"><spring:message code="label.yes"/></option>				
</select>
	<script>
		$("#mobilityRegistrationInformation_incoming").val('<c:out value='${not empty param.incoming ? param.incoming : mobilityRegistrationInformation.incoming }'/>');
	</script>	
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.MobilityRegistrationInformation.programDuration"/></div> 

<div class="col-sm-4">
		<ui-select id="mobilityRegistrationInformation_programDuration" class="form-control" name="programduration" ng-model="$parent.object.programDuration" theme="bootstrap" ng-disabled="disabled" >
    						<ui-select-match >{{$select.selected.text}}</ui-select-match>
    						<ui-select-choices repeat="programDuration.id as programDuration in object.programDurationDataSource | filter: $select.search">
      							<span ng-bind-html="programDuration.text | highlight: $select.search"></span>
    						</ui-select-choices>
  						</ui-select>				
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.MobilityRegistrationInformation.begin"/></div> 

<div class="col-sm-4">
	<%-- Relation to side 1 drop down rendered in input --%>
		<ui-select id="mobilityRegistrationInformation_begin" class="form-control" name="begin" ng-model="$parent.object.begin" theme="bootstrap" ng-disabled="disabled" >
    						<ui-select-match >{{$select.selected.text}}</ui-select-match>
    						<ui-select-choices repeat="begin.id as begin in object.beginDataSource | filter: $select.search">
      							<span ng-bind-html="begin.text | highlight: $select.search"></span>
    						</ui-select-choices>
  						</ui-select>				
				</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.MobilityRegistrationInformation.end"/></div> 

<div class="col-sm-4">
	<%-- Relation to side 1 drop down rendered in input --%>
		<ui-select id="mobilityRegistrationInformation_end" class="form-control" name="end" ng-model="$parent.object.end" theme="bootstrap" ng-disabled="disabled" >
    						<ui-select-match >{{$select.selected.text}}</ui-select-match>
    						<ui-select-choices repeat="end.id as end in object.endDataSource | filter: $select.search">
      							<span ng-bind-html="end.text | highlight: $select.search"></span>
    						</ui-select-choices>
  						</ui-select>				
				</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.MobilityRegistrationInformation.mobilityProgramType"/></div> 

<div class="col-sm-4">
	<%-- Relation to side 1 drop down rendered in input --%>
		<ui-select id="mobilityRegistrationInformation_mobilityProgramType" class="form-control" name="mobilityprogramtype" ng-model="$parent.object.mobilityProgramType" theme="bootstrap" ng-disabled="disabled" >
    						<ui-select-match >{{$select.selected.text}}</ui-select-match>
    						<ui-select-choices repeat="mobilityProgramType.id as mobilityProgramType in object.mobilityProgramTypeDataSource | filter: $select.search">
      							<span ng-bind-html="mobilityProgramType.text | highlight: $select.search"></span>
    						</ui-select-choices>
  						</ui-select>				
				</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.MobilityRegistrationInformation.mobilityActivityType"/></div> 

<div class="col-sm-4">
	<%-- Relation to side 1 drop down rendered in input --%>
		<ui-select id="mobilityRegistrationInformation_mobilityActivityType" class="form-control" name="mobilityactivitytype" ng-model="$parent.object.mobilityActivityType" theme="bootstrap" ng-disabled="disabled" >
    						<ui-select-match >{{$select.selected.text}}</ui-select-match>
    						<ui-select-choices repeat="mobilityActivityType.id as mobilityActivityType in object.mobilityActivityTypeDataSource | filter: $select.search">
      							<span ng-bind-html="mobilityActivityType.text | highlight: $select.search"></span>
    						</ui-select-choices>
  						</ui-select>				
				</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.MobilityRegistrationInformation.foreignInstitutionUnit"/></div> 

<div class="col-sm-4">
	<%-- Relation to side 1 drop down rendered in input --%>
		<ui-select id="mobilityRegistrationInformation_foreignInstitutionUnit" class="form-control" name="foreigninstitutionunit" ng-model="$parent.object.foreignInstitutionUnit" theme="bootstrap" ng-disabled="disabled" >
    						<ui-select-match >{{$select.selected.text}}</ui-select-match>
    						<ui-select-choices repeat="foreignInstitutionUnit.id as foreignInstitutionUnit in object.foreignInstitutionUnitDataSource | filter: $select.search">
      							<span ng-bind-html="foreignInstitutionUnit.text | highlight: $select.search"></span>
    						</ui-select-choices>
  						</ui-select>				
				</div>
</div>		
  </div>
  <div class="panel-footer">
		<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />"/>
	</div>
</div>
</form>

<script>
$(document).ready(function() {

// Put here the initializing code for page
	});
</script>
