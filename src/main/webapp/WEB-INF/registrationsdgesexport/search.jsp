<%@page import="java.util.Collection"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.registrationsdgesexport.RegistrationDGESStateBeanController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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

    //$scope.object= ${contactsFormJson};
    $scope.postBack = createAngularPostbackFunction($scope);
    $scope.entries = [];
    $scope.populateEntries = function (length) {
	    for(var i = 0; i < length; i++) {
		    if($scope.entries.length < i) {
			    $scope.entries[i].checked = false;
		    }else {
			    $scope.entries[i] = {'checked': false};
		    }
	    }
    };
    $scope.booleanvalues = [
                            { name : '<spring:message code="label.no"/>', value : false },
                            { name : '<spring:message code="label.yes"/>', value : true } 
                    ];
    $scope.submitForm = function() {
        $('form[id="candidaciesTable"]').submit();
        };
    $scope.checkBoxClick = function(rowIndex, rowId) {
	    $scope.entries[rowIndex].checked = !$scope.entries[rowIndex].checked;
	    console.log($scope.rowId);
	    $scope.$apply();
    };
    $scope.reactivateCandidacies = function() {
	    $scope.candidaciesToReactivate = [];
	    angular.forEach($scope.entries, function(value,key) {
		    if(value.checked) {
		        var checkBox = $($('#candidaciesTable tr input')[key]);
			    $scope.candidaciesToReactivate.push(checkBox.attr('id'));
		    }
	    });
	    var url = '${pageContext.request.contextPath}<%= RegistrationDGESStateBeanController.REACTIVATE_URL %>';
	    $('form[id="candidaciesTable"]').attr('action', url);
	    $scope.$apply();
        $('form[id="candidaciesTable"]').submit();
    }
    $scope.cancelCandidacies = function () {
        $scope.candidaciesToCancel = [];
        angular.forEach($scope.entries, function(value,key) {
            if(value.checked) {
                var checkBox = $($('#candidaciesTable tr input')[key]);
                $scope.candidaciesToCancel.push(checkBox.attr('id'));
            }
        });
        var url = '${pageContext.request.contextPath}<%= RegistrationDGESStateBeanController.CANCEL_URL %>';
        $('form[id="candidaciesTable"]').attr('action', url);
        $scope.$apply();
        $('form[id="candidaciesTable"]').submit();	
    }
}]);
</script>

<style>
table {
    border-collapse: separate;
    border-spacing: 1em;
}
</style>

<form action="${pageContext.request.contextPath}<%= RegistrationDGESStateBeanController.SEARCH_URL %>" method="post">
    <table>
	   <tbody>
	   <tr>
		  <th>
            <spring:message code="label.dges.importation.process.execution.year"/>&nbsp;
          </th>
	      <td>
	       <select name="executionYear" style="width : 100px">
		      <c:forEach items="${executionYears}" var="executionYear">
			     <c:if test = "${executionYear == selectedExecutionYear}">
				     <option selected value="${executionYear.externalId}">${executionYear.name}</option>
				 </c:if>
				 <c:if test = "${executionYear != selectedExecutionYear}">
			         <option value="${executionYear.externalId}">${executionYear.name}</option>
				 </c:if>
			  </c:forEach>
		   </select>
	      </td>
	   </tr>
       <tr>
	       <th>
               <spring:message code="label.dges.importation.process.entry.phase"/>:&nbsp;
           </th>
	       <td>
		      <select name="phase" style="width : 100px">
			  <c:forEach items="${phases}" var="phase">
			      <c:if test = "${phase == selectedPhase}">
				      <option selected value="${phase}">${phase}</option>
				  </c:if>
				  <c:if test = "${phase != selectedPhase}">
					  <option value="${phase}">${phase}</option>
				  </c:if>
			  </c:forEach>
		      </select>
	       </td>
	   </tr>
       <tr>
           <th>
               <spring:message code="label.dges.importation.process.candidacy.state"/>:&nbsp;
           </th>
           <td>
              <select name="candidacySituationType" style="width : 100px">
              <c:forEach items="${candidacyStates}" var="state">
                  <c:if test = "${state.id == selectedCandidacyState}">
                      <option selected value="${state.id}">${state.text}</option>
                  </c:if>
                  <c:if test = "${state.id != selectedCandidacyState}">
                      <option value="${state.id}">${state.text}</option>
                  </c:if>
              </c:forEach>
              </select>
           </td>
       </tr>
       <tr>
           <th>
               <spring:message code="label.dges.importation.process.full.data"/>:&nbsp;
           </th>
           <td>
              <select name="exportStatistics" style="width : 100px">
                  <c:if test = "${exportStatistics}">
                      <option value="false"><spring:message code="label.no"/></option>
                  </c:if>
                  <c:if test = "${exportStatistics}">
                      <option selected value="true"><spring:message code="label.yes" /></option>
                  </c:if>
                  <c:if test = "${not exportStatistics}">
                      <option selected value="false"><spring:message code="label.no"/></option>
                  </c:if>
                  <c:if test = "${not exportStatistics}">
                      <option value="true"><spring:message code="label.yes" /></option>
                  </c:if>
              </select>
           </td>
       </tr>
       <tr>
	       <th></th>
		   <td><input type="submit" style="width : 100px" value="<spring:message code='label.search' />"></td>
	   </tr>
	</tbody>
</table>
	
	<%
		boolean exportStatistics = false;
		Object exportStatisticsObj = request.getAttribute("exportStatistics");
		if (exportStatisticsObj != null && ((Boolean) exportStatisticsObj)) {
		    exportStatistics = true;
		}
	%>

</form>
<% int collectionSize = ((Collection)request.getAttribute("searchregistrationdgesstatebeanResultsDataSet")).size(); %>

<form id="candidaciesTable" method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
      action="#" />
      
      <input type="hidden" name="candidaciesToCancel" ng-repeat="candidacy in candidaciesToCancel" value="{{ candidacy }}" />
      <input type="hidden" name="candidaciesToReactivate" ng-repeat="candidacy in candidaciesToReactivate" value="{{ candidacy }}" />
      
      
<c:choose>
	<c:when test="${not empty searchregistrationdgesstatebeanResultsDataSet}">
		<table id="searchregistrationdgesstatebeanTable" class="table responsive table-bordered table-hover" style="width:100%" ng-init="populateEntries(<%= collectionSize %>)">
			<thead>
				<tr>
                    <th></th>
					<th><spring:message code="label.studentsListByCurricularCourse.degree"/></th>
					<th><spring:message code="label.identification.number"/></th>
					<th><spring:message code="label.student"/></th>
					<th><spring:message code="label.is.registered"/></th>
					<th><spring:message code="label.candidacy"/></th>
					<% if (exportStatistics) { %>
					<th><spring:message code="label.FiliationForm.nationality"/></th>
					<th><spring:message code="label.FiliationForm.secondNationality"/></th>
					<th><spring:message code="label.Registration.ingressionType"/></th>
					<th><spring:message code="label.PersonalInformationForm.ingressionOption"/></th>
					<th><spring:message code="label.PersonalInformationForm.firstOptionDegreeDesignation.short"/></th>
					<th><spring:message code="label.PersonalInformationForm.firstOptionInstitution.short"/></th>
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
					<% } %>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
        <div class="panel-footer">
            <button type="button" class="btn btn-primary" role="button" ng-click="reactivateCandidacies()"><spring:message code="label.registration.reactivate" /></button>
            <button type="button" class="btn btn-primary" role="button" ng-click="cancelCandidacies()"><spring:message code="label.registration.cancel" /></button>
        </div>
  
		<form id="revertCancelForm" action="/registrationsdgesexport/reactivate/">
			<button id="revertCancelButton" disabled onclick="return confirm('<spring:message code="label.remove.cancellation.confirm"/>');"><spring:message code="label.remove.cancellation"/></button>
		</form>
	</c:when>
	<c:otherwise>
        <div class="alert alert-warning" role="alert">
        	<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span><spring:message code="label.noResultsFound" /></p>
        </div>	
	</c:otherwise>
</c:choose>
</form>

<script>

	$(document).ready(function() {
	var searchregistrationdgesstatebeanDataSet = [
			<c:forEach items="${searchregistrationdgesstatebeanResultsDataSet}" var="searchResult" varStatus="loop">
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
					DT_RowId : "<c:out value='${searchResult.candidacyId}'/>",
					selectionBox : "<input class='form-control' id='${searchResult.candidacyId}' ng-model='entries[${ loop.index }].checked' type='checkbox'/>",
					degreeCode : "<c:out value='${searchResult.degreeCode}'/>",
					idnumber : "<c:out value='${searchResult.idNumber}'/>",
					name : "<c:out value='${searchResult.name}'/>",
					registrationstate : "<c:out value='${searchResult.registrationState}'/>",
					candidacyState : "<c:out value='${searchResult.candidacyState}'/>",
					<% if (exportStatistics) { %>
					nationality : "<c:out value='${searchResult.nationality}'/>",
					secondNationality : "<c:out value='${searchResult.secondNationality}'/>",
					ingressionType : "<c:out value='${searchResult.ingressionType}'/>",
					placingOption : "<c:out value='${searchResult.placingOption}'/>",
					firstOptionDegree : "<c:out value='${searchResult.firstOptionDegree}'/>",
					firstOptionInstitution : "<c:out value='${searchResult.firstOptionInstitution}'/>",
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
					<% } %>
				},
            </c:forEach>
    ];	

		var table = $('#searchregistrationdgesstatebeanTable').DataTable({
			language : {
				url : "${datatablesI18NUrl}",			
			},
			columns: [
			    { data:'selectionBox'},
				{ data: 'degreeCode', width: "12%" },
				{ data: 'idnumber', width: "13%" },
				{ data: 'name', width: "50%" },
				{ data: 'registrationstate', width: "12%" },
				{ data: 'candidacyState', width: "13%" },
				<% if (exportStatistics) { %>
				{ data: 'nationality', visible: false },
				{ data: 'secondNationality', visible: false },
				{ data: 'ingressionType', visible: false },
				{ data: 'placingOption', visible: false },
				{ data: 'firstOptionDegree', visible: false },
				{ data: 'firstOptionInstitution', visible: false },
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
				<% } %>
			],
			data : searchregistrationdgesstatebeanDataSet,
			dom: '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
        	tableTools: {
            	sSwfPath: "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
        	}
		});
		table.columns.adjust().draw();
		  $('#searchregistrationdgesstatebeanTable tbody').on( 'click', 'tr', function () {
		        var checkBox = $($('#candidaciesTable tr input')[this.rowIndex - 1]);
                angular.element($('#candidaciesTable')).scope().checkBoxClick(this.rowIndex - 1);
	            $(this).toggleClass('selected');
	            checkBox.prop('checked', !checkBox.prop('checked'));
	            $(this).children().removeClass("sorting_1");
		  } );
	}); 
</script>

