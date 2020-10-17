<%@page import="java.util.Collection"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.registrationsdgesexport.RegistrationDGESStateBeanController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- TODO save in project -->
<%-- <spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" /> --%>
<%-- <script type="text/javascript" src="${datatablesUrl}"></script> --%>

<script type="text/javascript" src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/select/1.2.0/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/buttons.html5.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.2.2/css/buttons.dataTables.min.css" />


<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%-- ${portal.toolkit()} --%>

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
	<h1><spring:message code="label.registrationsDgesExport.searchRegistrationDGESStateBean" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
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
angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

    $scope.object= ${objectBeanJson};
    $scope.postBack = createAngularPostbackFunction($scope);
    $scope.entries = [];

    $scope.cancelActionMessage = '<spring:message code="label.registration.confirmAction.cancel" />';
    $scope.reactivateActionMessage = '<spring:message code="label.registration.confirmAction.reactivate" />';
    $scope.registerActionMessage = '<spring:message code="label.registration.confirmAction.register" />';
    
    $scope.executionYearsValues = [
    <c:forEach items="${executionYears}" var="executionYear">
        { 'id' : '${executionYear.id}',
          'text' : '${executionYear.text}',  
          'normalizedText' : '${executionYear.normalizedText}',
        },
    </c:forEach>         
    ];
    $scope.ingressTypesValues = [
    <c:forEach items="${ingressTypes}" var="ingressType">
        { 'id' : '${ingressType.id}',
          'text' : '${ingressType.text}',  
          'normalizedText' : '${ingressType.normalizedText}',
        },
    </c:forEach>         
    ];
    $scope.phasesValues = [
    <c:forEach items="${phases}" var="phase">
        { 'id' : '${phase}',
          'text' : '${phase}',  
          'normalizedText' : '${phase}',
        },
    </c:forEach>         
    ];
    $scope.candidacyStatesValues = [
    <c:forEach items="${candidacyStates}" var="candidacyState">
        { 'id' : '${candidacyState.id}',
          'text' : '${candidacyState.text}',  
          'normalizedText' : '${candidacyState.normalizedText}',
        },
    </c:forEach>         
    ];

    $scope.executionYear = '${selectedExecutionYear.externalId}';
    $scope.candidacySituationType = '${selectedCandidacyState}';
    $scope.phase = '${selectedPhase}';
    $scope.ingressType = '${selectedIngressType.externalId}';
    $scope.exportStatistics = '${exportStatistics}';
    
    $scope.booleanvalues = [
       { name : '<spring:message code="label.no"/>', value : false },
       { name : '<spring:message code="label.yes"/>', value : true } 
    ];
    
    $scope.checkBoxClick = function(rowId, isToInsert, name) {
	    console.log(name);
	    if(isToInsert) {
		    $scope.entries.push(rowId);
	    }else {
		    var indexOf = $scope.entries.indexOf(rowId);
		    if(indexOf != -1) {
			    $scope.entries.splice(indexOf, 1);
		    } else {
			    alert('Error when deselecting a candidacy!');
		    }
	    }
	    $scope.$apply();
    };
    $scope.registerCandidacies = function() {
        $scope.object.candidaciesToRegister = [];

        angular.forEach($scope.entries, function(value,key) {
            $scope.object.candidaciesToRegister.push(value);
        });
        
        var url = '${pageContext.request.contextPath}<%= RegistrationDGESStateBeanController.REGISTER_URL %>';
        $('form[id="searchForm"]').attr('action', url);
        $scope.actionMessage = $scope.registerActionMessage;
        $scope.$apply();
        $('#confirmModal').modal('toggle');  
    };
    $scope.reactivateCandidacies = function() {
	    $scope.object.candidaciesToReactivate = [];

	    angular.forEach($scope.entries, function(value,key) {
            $scope.object.candidaciesToReactivate.push(value);
	    });
	    
	    var url = '${pageContext.request.contextPath}<%= RegistrationDGESStateBeanController.REACTIVATE_URL %>';
	    $('form[id="searchForm"]').attr('action', url);
	    $scope.actionMessage = $scope.reactivateActionMessage;
	    $scope.$apply();
	    $('#confirmModal').modal('toggle');  
    };
    $scope.cancelCandidacies = function () {
        $scope.object.candidaciesToCancel = [];
        angular.forEach($scope.entries, function(value,key) {
            $scope.object.candidaciesToCancel.push(value);
        });
        var url = '${pageContext.request.contextPath}<%= RegistrationDGESStateBeanController.CANCEL_URL %>';
        $('form[id="searchForm"]').attr('action', url);
        $scope.actionMessage = $scope.cancelActionMessage;
        $scope.$apply();
        $('#confirmModal').modal('toggle');  
    }
    $scope.submitSearch = function() {
        $('form[id="searchForm"]').submit();    
    };
    $scope.submitForm = function() {
        $('#confirmModal').modal('toggle');  
	    $('form[id="searchForm"]').submit();	
    };
    $scope.selectAll = function() {
	    $('#searchregistrationdgesstatebeanTable').DataTable().rows().select();
	    $scope.entries = [];
	    angular.forEach($('#searchregistrationdgesstatebeanTable').DataTable().rows().data(), function(value, key) {
	        $scope.entries.push(value.DT_RowId);        
	    });
    }
    $scope.deselectAll = function() {
	    $('#searchregistrationdgesstatebeanTable').DataTable().rows().deselect();
	    $scope.entries = [];
    }
}]);
</script>


<form id="searchForm" action="${pageContext.request.contextPath}<%= RegistrationDGESStateBeanController.SEARCH_URL %>"  method="post" 
      class="form-horizontal" ng-app="angularApp" ng-controller="angularController">

        <input name="executionYear" type="hidden" value="{{ executionYear }}" />
        <input name="candidacySituationType" type="hidden" value="{{ candidacySituationType }}" />
        <input name="phase" type="hidden" value="{{ phase }}" />
        <input name="ingressType" type="hidden" value="{{ ingressType }}" />
        <input name="exportStatistics" type="hidden" value="{{ exportStatistics }}" />
    

    <div class="panel panel-primary">
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.search" />
            </h3>
        </div>

        <div class="panel panel-body">
            <div class="form-group row">
                <label for="dgesExportation_executionYear" class="col-sm-2 control-label required-field">
                   <spring:message code="label.dges.importation.process.execution.year"/>
                </label>

                <div class="col-sm-4">
                    <ui-select  id="dgesExportation_executionYear" name="executionYear" ng-model="$parent.executionYear" theme="bootstrap">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="executionYear.id as executionYear in executionYearsValues | filter: {normalizedText : $select.search}">
                            <span ng-bind-html="executionYear.text"></span>
                        </ui-select-choices> 
                    </ui-select>                 
                </div>
            </div>
            <div class="form-group row">
                <label for="dgesExportation_phase" class="col-sm-2 control-label required-field">
                   <spring:message code="label.dges.importation.process.entry.phase"/>:
                </label>

                <div class="col-sm-4">
                    <ui-select  id="dgesExportation_phase" name="phase" ng-model="$parent.phase" theme="bootstrap">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="phase.id as phase in phasesValues | filter: {normalizedText : $select.search}">
                            <span ng-bind-html="phase.text"></span>
                        </ui-select-choices> 
                    </ui-select>                 
                </div>
            </div>
            <div class="form-group row">
                <label for="dgesExportation_candidacySituationType" class="col-sm-2 control-label required-field">
                   <spring:message code="label.dges.importation.process.candidacy.state"/>
                </label>

                <div class="col-sm-4">
                    <ui-select  id="dgesExportation_candidacySituationType" name="candidacySituationType" ng-model="$parent.candidacySituationType" theme="bootstrap">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="candidacySituationType.id as candidacySituationType in candidacyStatesValues | filter: {normalizedText : $select.search}">
                            <span ng-bind-html="candidacySituationType.text"></span>
                        </ui-select-choices> 
                    </ui-select>                 
                </div>
            </div>
            <div class="form-group row">
                <label for="dgesExportation_ingressType" class="col-sm-2 control-label required-field">
                   <spring:message code="label.dges.importation.process.ingress.type"/>
                </label>

                <div class="col-sm-4">
                    <ui-select  id="dgesExportation_ingressType" name="ingressType" ng-model="$parent.ingressType" theme="bootstrap">
                        <ui-select-match allow-clear="true">{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="ingressType.id as ingressType in ingressTypesValues | filter: {normalizedText : $select.search}">
                            <span ng-bind-html="ingressType.text"></span>
                        </ui-select-choices> 
                    </ui-select>                 
                </div>
            </div>
            <div class="form-group row">
                <label for="dgesExportation_beginDate" class="col-sm-2 control-label required-field">
                   <spring:message code="label.dges.importation.process.begin.date"/>
                </label>

                <div class="col-sm-4">
                    <input id="dgesImportation_beginDate" class="form-control" type="text" name="beginDate" bennu-date="beginDate" />            
                </div>
            </div>
            <div class="form-group row">
                <label for="dgesExportation_endDate" class="col-sm-2 control-label required-field">
                   <spring:message code="label.dges.importation.process.end.date"/>
                </label>

                <div class="col-sm-4">
                    <input id="dgesImportation_endDate" class="form-control" type="text" name="endDate" bennu-date="endDate" />            
                </div>
            </div>
            <div class="form-group row">
                <label for="dgesExportation_exportStatistics" class="col-sm-2 control-label required-field">
                   <spring:message code="label.dges.importation.process.full.data"/>
                </label>

                <div class="col-sm-4">
                    <ui-select id="dgesExportation_exportStatistics" name="exportStatistics"
                        ng-model="$parent.exportStatistics" theme="bootstrap" > 
                        <ui-select-match>
                            {{$select.selected.name}}
                        </ui-select-match> 
                        <ui-select-choices repeat="bvalue.value as bvalue in booleanvalues | filter: $select.search">
                            <span ng-bind-html="bvalue.name | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
                </div>
            </div>

        </div>
        <div class="panel-footer">
            <button type="button" class="btn btn-primary" role="button" ng-click="submitSearch()"><spring:message code="label.submit" /></button>
        </div>
    </div>
	
	<%
		boolean exportStatistics = false;
		Object exportStatisticsObj = request.getAttribute("exportStatistics");
		if (exportStatisticsObj != null && ((Boolean) exportStatisticsObj)) {
		    exportStatistics = true;
		}
	%>

<% int collectionSize = ((Collection)request.getAttribute("searchregistrationdgesstatebeanResultsDataSet")).size(); %>

      <input type="hidden" name="candidaciesToCancel" ng-repeat="candidacy in object.candidaciesToCancel" value="{{ candidacy }}" />
      <input type="hidden" name="candidaciesToReactivate" ng-repeat="candidacy in object.candidaciesToReactivate" value="{{ candidacy }}" />
      <input type="hidden" name="bean" value="{{ object }}" />
      
<c:choose>
	<c:when test="${not empty searchregistrationdgesstatebeanResultsDataSet}">
        <div class="panel">

        <div class="panel panel-body">
            <button type="button" class="btn btn-primary" role="button" ng-click="selectAll()">
                <span class="glyphicon glyphicon-check" aria-hidden="true"></span>
                &nbsp;
                <spring:message code="label.event.add.all" />
            </button>
            <button type="button" class="btn btn-primary" role="button" ng-click="deselectAll()">
                <span class="glyphicon glyphicon-unchecked" aria-hidden="true"></span>
                &nbsp;
                <spring:message code="label.event.delete.all" />
            </button>
		<table id="searchregistrationdgesstatebeanTable" class="table responsive table-bordered table-hover" style="width:100%">
			<thead>
				<tr>
                    <th><spring:message code="label.HouseholdInformationForm.executionYear"/></th>
                    <% if (exportStatistics) { %>
                    <th><spring:message code="label.Degree.degreeType"/></th>
                    <% } %>
					<th><spring:message code="label.studentsListByCurricularCourse.degree"/></th>
                    <% if (exportStatistics) { %>
                    <th><spring:message code="label.Degree.name"/></th>
                    <% } %>
					<th><spring:message code="label.identification.number"/></th>
					<th><spring:message code="label.student"/></th>
					<th><spring:message code="label.is.registered"/></th>
                    <th><spring:message code="label.number.ucs"/></th>
					<th><spring:message code="label.candidacy"/></th>
					<% if (exportStatistics) { %>
					<th><spring:message code="label.FiliationForm.nationality"/></th>
					<th><spring:message code="label.FiliationForm.secondNationality"/></th>
                    <th><spring:message code="label.Person.birthYear"/></th>
                    <th><spring:message code="label.FiliationForm.countryOfBirth" /></th>
                    <th><spring:message code="label.FiliationForm.districtOfBirth" /></th>
                    <th><spring:message code="label.FiliationForm.districtSubdivisionOfBirth" /></th>
                    <th><spring:message code="label.FiliationForm.parishOfBirth" /></th>
                    <th><spring:message code="label.Person.gender"/></th>
					<th><spring:message code="label.Registration.ingressionType"/></th>
					<th><spring:message code="label.PersonalInformationForm.ingressionOption"/></th>
					<th><spring:message code="label.PersonalInformationForm.firstOptionDegreeDesignation.short"/></th>
					<th><spring:message code="label.PersonalInformationForm.firstOptionInstitution.short"/></th>
                    <th><spring:message code="label.ResidenceInformationForm.countryOfResidence" /></th>
                    <th><spring:message code="label.ResidenceInformationForm.districtOfResidence" /></th>
                    <th><spring:message code="label.ResidenceInformationForm.districtSubdivisionOfResidence" /></th>
                    <th><spring:message code="label.ResidenceInformationForm.parishOfResidence" /></th>
                    <th><spring:message code="label.ResidenceInformationForm.dislocatedFromPermanentResidence" /></th>
					<th><spring:message code="label.firstTimeCandidacy.fillResidenceInformation"/></th>
					<th><spring:message code="label.PersonalInformationForm.profession"/></th>
					<th><spring:message code="label.PersonalInformationForm.professionTimeType.short"/></th>
					<th><spring:message code="label.PersonalInformationForm.professionalCondition"/></th>
					<th><spring:message code="label.PersonalInformationForm.professionType"/></th>
					<th><spring:message code="label.FiliationForm.fatherName"/></th>
					<th><spring:message code="label.HouseholdInformationForm.fatherSchoolLevel.short"/></th>
					<th><spring:message code="label.HouseholdInformationForm.fatherProfessionalCondition.short"/></th>
					<th><spring:message code="label.HouseholdInformationForm.fatherProfessionType.short"/></th>
					<th><spring:message code="label.FiliationForm.motherName"/></th>
					<th><spring:message code="label.HouseholdInformationForm.motherSchoolLevel.short"/></th>
					<th><spring:message code="label.HouseholdInformationForm.motherProfessionalCondition.short"/></th>
					<th><spring:message code="label.HouseholdInformationForm.motherProfessionType.short"/></th>
					<th><spring:message code="label.HouseholdInformationForm.householdSalarySpan.short"/></th>
					<th><spring:message code="label.firstTimeCandidacy.fillDisabilities"/></th>
					<th><spring:message code="label.DisabilitiesForm.needsDisabilitySupport.short"/></th>
					<th><spring:message code="label.MotivationsExpectationsForm.universityDiscoveryMeansAnswers.short"/></th>
					<th><spring:message code="label.MotivationsExpectationsForm.universityChoiceMotivationAnswers.short"/></th>
                    <th><spring:message code="label.OriginInformationForm.countryWhereFinishedPreviousCompleteDegree" /></th>
                    <th><spring:message code="label.OriginInformationForm.districtWhereFinishedPreviousCompleteDegree" /></th>
                    <th><spring:message code="label.OriginInformationForm.districtSubdivisionWhereFinishedPreviousCompleteDegree" /></th>
                    <th><spring:message code="label.OriginInformationForm.schoolLevel" /></th>
                    <th><spring:message code="label.OriginInformationForm.institution" /></th>
                    <th><spring:message code="label.OriginInformationForm.degreeDesignation" /></th>
                    <th><spring:message code="label.OriginInformationForm.conclusionGrade" /></th>
                    <th><spring:message code="label.OriginInformationForm.conclusionYear" /></th>
                    <th><spring:message code="label.OriginInformationForm.highSchoolType" /></th>
					<% } %>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
        </div>
        <div class="panel-footer">
            <button type="button" class="btn btn-primary" role="button" ng-click="registerCandidacies()" ng-disabled="!entries.length">
<!--                 <span class="glyphicon glyphicon-ok-sign" aria-hidden="true"></span> -->
<!--                 &nbsp; -->
                <spring:message code="label.registration.register" />
            </button>
            <button type="button" class="btn btn-primary" role="button" ng-click="reactivateCandidacies()" ng-disabled="!entries.length">
<!--                 <span class="glyphicon glyphicon-ok-sign" aria-hidden="true"></span> -->
<!--                 &nbsp; -->
                <spring:message code="label.registration.reactivate" />
            </button>
            <button type="button" class="btn btn-primary" role="button" ng-click="cancelCandidacies()" ng-disabled="!entries.length">
<!--                 <span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span> -->
<!--                 &nbsp; -->
                <spring:message code="label.registration.cancel" />
            </button>
        </div>
        </div>
	</c:when>
	<c:otherwise>
        <div class="alert alert-warning" role="alert">
        	<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span><spring:message code="label.noResultsFound" /></p>
        </div>	
	</c:otherwise>
</c:choose>

    <div class="modal fade" id="confirmModal">
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
                    <p id="confirmMessage">
                        <spring:message code="label.registration.confirmAction.partA" />
                        {{ actionMessage }}
                        <spring:message code="label.registration.confirmAction.partB" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="confirmButton" class="btn btn-primary" type="button" ng-click="submitForm()">
                        <spring:message code="label.confirmation" />
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
	var searchregistrationdgesstatebeanDataSet = [
			<c:forEach items="${searchregistrationdgesstatebeanResultsDataSet}" var="searchResult" varStatus="loop">
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
					DT_RowId : "<c:out value='${searchResult.candidacyId}'/>",
				    executionYear : "<c:out value='${searchResult.executionYear}'/>",
                    <% if (exportStatistics) { %>
                        degreeTypeName : "<c:out value='${searchResult.degreeTypeName}'/>",
                    <% } %>
					degreeCode : "<c:out value='${searchResult.degreeCode}'/>",
                    <% if (exportStatistics) { %>
                        degreeName : "<c:out value='${searchResult.degreeName}'/>",
                    <% } %>
					idnumber : "<c:out value='${searchResult.idNumber}'/>",
					name : "<c:out value='${searchResult.name}'/>",
					registrationstate : "<c:out value='${searchResult.registrationState}'/>",
                    numberOfUCsEnrolled : "<c:out value='${searchResult.numberOfUCsEnrolled}'/>",
					candidacyState : "<c:out value='${searchResult.candidacyState}'/>",
					<% if (exportStatistics) { %>
					nationality : "<c:out value='${searchResult.nationality}'/>",
					secondNationality : "<c:out value='${searchResult.secondNationality}'/>",
					birthYear : "<c:out value='${searchResult.birthYear}'/>",
					countryOfBirth : "<c:out value='${searchResult.countryOfBirth}'/>",
					districtOfBirth : "<c:out value='${searchResult.districtOfBirth}'/>",
					districtSubdivisionOfBirth : "<c:out value='${searchResult.districtSubdivisionOfBirth}'/>",
					parishOfBirth : "<c:out value='${searchResult.parishOfBirth}'/>",
					gender : "<c:out value='${searchResult.gender}'/>",
					ingressionType : "<c:out value='${searchResult.ingressionType}'/>",
					placingOption : "<c:out value='${searchResult.placingOption}'/>",
					firstOptionDegree : "<c:out value='${searchResult.firstOptionDegree}'/>",
					firstOptionInstitution : "<c:out value='${searchResult.firstOptionInstitution}'/>",
                    countryOfResidence : "<c:out value='${searchResult.countryOfResidence}'/>",
                    districtOfResidence : "<c:out value='${searchResult.districtOfResidence}'/>",
                    districtSubdivisionOfResidence : "<c:out value='${searchResult.districtSubdivisionOfResidence}'/>",
                    parishOfResidence : "<c:out value='${searchResult.parishOfResidence}'/>",
                    isDislocated : "<c:out value='${searchResult.isDislocated}'/>",
                    dislocatedResidenceType : "<c:out value='${searchResult.dislocatedResidenceType}'/>",
					profession : "<c:out value='${searchResult.profession}'/>",
					professionTimeType : "<c:out value='${searchResult.professionTimeType}'/>",
					professionalCondition : "<c:out value='${searchResult.professionalCondition}'/>",
					professionType : "<c:out value='${searchResult.professionType}'/>",
					fatherName : "<c:out value='${searchResult.fatherName}'/>",
					fatherSchoolLevel : "<c:out value='${searchResult.fatherSchoolLevel}'/>",
					fatherProfessionalCondition : "<c:out value='${searchResult.fatherProfessionalCondition}'/>",
					fatherProfessionType : "<c:out value='${searchResult.fatherProfessionType}'/>",
					motherName : "<c:out value='${searchResult.motherName}'/>",
					motherSchoolLevel : "<c:out value='${searchResult.motherSchoolLevel}'/>",
					motherProfessionalCondition : "<c:out value='${searchResult.motherProfessionalCondition}'/>",
					motherProfessionType : "<c:out value='${searchResult.motherProfessionType}'/>",
					salarySpan : "<c:out value='${searchResult.salarySpan}'/>",
					disabilityType : "<c:out value='${searchResult.disabilityType}'/>",
					needsDisabilitySupport : "<c:out value='${searchResult.needsDisabilitySupport}'/>",
					universityDiscoveryString : "<c:out value='${searchResult.universityDiscoveryString}'/>",
					universityChoiceString : "<c:out value='${searchResult.universityChoiceString}'/>",
					precedentCountry : "<c:out value='${searchResult.precedentCountry}'/>",
					precedentDistrict : "<c:out value='${searchResult.precedentDistrict}'/>",
					precedentDistrictSubdivision : "<c:out value='${searchResult.precedentDistrictSubdivision}'/>",
					precedentSchoolLevel : "<c:out value='${searchResult.precedentSchoolLevel}'/>",
					precedentInstitution : "<c:out value='${searchResult.precedentInstitution}'/>",
					precedentDegreeDesignation : "<c:out value='${searchResult.precedentDegreeDesignation}'/>",
					precedentConclusionGrade : "<c:out value='${searchResult.precedentConclusionGrade}'/>",
					precedentConclusionYear : "<c:out value='${searchResult.precedentConclusionYear}'/>",
					precedentHighSchoolType : "<c:out value='${searchResult.precedentHighSchoolType}'/>",
					<% } %>
				},
            </c:forEach>
    ];	

		var table = $('#searchregistrationdgesstatebeanTable').DataTable({
			language : {
				url : "${datatablesI18NUrl}",
				//This is a dirty hack, because the number of selected rows is not being updated
				select: {
                    rows: {
                        _: "",
                        0: "",
                        1: ""
                    }
                }
			},
			columns: [
			    { data: 'executionYear' },
                <% if (exportStatistics) { %>
                    { data: 'degreeTypeName' },
                <% } %>
		        { data: 'degreeCode' },
                <% if (exportStatistics) { %>
                    { data: 'degreeName' },
                <% } %>
		        { data: 'idnumber' },
				{ data: 'name' },
				{ data: 'registrationstate' },
                { data: 'numberOfUCsEnrolled' },
				{ data: 'candidacyState' },
				<% if (exportStatistics) { %>
				{ data: 'nationality', visible: false },
				{ data: 'secondNationality', visible: false },
		        { data: 'birthYear', visible: false },
                { data: 'countryOfBirth', visible: false },
                { data: 'districtOfBirth', visible: false },
                { data: 'districtSubdivisionOfBirth', visible: false },
                { data: 'parishOfBirth', visible: false },
                { data: 'gender', visible: false },
                { data: 'ingressionType', visible: false },
				{ data: 'placingOption', visible: false },
				{ data: 'firstOptionDegree', visible: false },
				{ data: 'firstOptionInstitution', visible: false },
                { data: 'countryOfResidence', visible: false },
                { data: 'districtOfResidence', visible: false },
                { data: 'districtSubdivisionOfResidence', visible: false },
                { data: 'parishOfResidence', visible: false },
                { data: 'isDislocated', visible: false },
				{ data: 'dislocatedResidenceType', visible: false },
				{ data: 'profession', visible: false },
				{ data: 'professionTimeType', visible: false },
				{ data: 'professionalCondition', visible: false },
				{ data: 'professionType', visible: false },
				{ data: 'fatherName', visible: false },
				{ data: 'fatherSchoolLevel', visible: false },
				{ data: 'fatherProfessionalCondition', visible: false },
				{ data: 'fatherProfessionType', visible: false },
				{ data: 'motherName', visible: false },
				{ data: 'motherSchoolLevel', visible: false },
				{ data: 'motherProfessionalCondition', visible: false },
				{ data: 'motherProfessionType', visible: false },
				{ data: 'salarySpan', visible: false },
				{ data: 'disabilityType', visible: false },
				{ data: 'needsDisabilitySupport', visible: false },
				{ data: 'universityDiscoveryString', visible: false },
				{ data: 'universityChoiceString', visible: false },
                { data: 'precedentCountry', visible: false },
                { data: 'precedentDistrict', visible: false },
                { data: 'precedentDistrictSubdivision', visible: false },
                { data: 'precedentSchoolLevel', visible: false },
                { data: 'precedentInstitution', visible: false },
                { data: 'precedentDegreeDesignation', visible: false },
                { data: 'precedentConclusionGrade', visible: false },
                { data: 'precedentConclusionYear', visible: false },
                { data: 'precedentHighSchoolType', visible: false },
				<% } %>
			],
			data : searchregistrationdgesstatebeanDataSet,
			dom: '<"col-sm-5"l><"col-sm-3"f><"col-sm-3"B>rtip', //FilterBox = YES && ExportOptions = YES
			buttons: [
		        'copyHtml5',
		        'excelHtml5',
		        'csvHtml5',
		        'pdfHtml5'
			],
        	tableTools: {
            	sSwfPath: "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
        	},
		});
		table.columns.adjust().draw();
		  $('#searchregistrationdgesstatebeanTable tbody').on( 'click', 'tr', function () {
                angular.element($('#searchForm')).scope().checkBoxClick(this.id, !$(this).hasClass('selected'), $(this).children()[2]);
	            $(this).toggleClass('selected');
	            $(this).children().removeClass("sorting_1");
		  } );
	}); 
</script>

