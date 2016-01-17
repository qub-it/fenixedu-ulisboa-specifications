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

${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>



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



<form>
	<table>
	<tbody>
	<tr>
		<th><spring:message code="label.dges.importation.process.execution.year"/>&nbsp;</th>
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
	</tr><tr>
		<th><spring:message code="label.dges.importation.process.entry.phase"/>:&nbsp;</th>
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
	</tr><tr>
		<th></th>
		<td><input type="submit" value="Pesquisar" style="width : 100px"></td>
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

	<div align="right">
	<input id="exportStatistics" name="exportStatistics" type="checkbox" onclick="this.form.submit()" <%= exportStatistics? "checked='checked'" : ""%>>
		<label for="exportStatistics" onclick="this.form.submit()"><spring:message code="label.exportStatistics"/>&nbsp;&nbsp;&nbsp;&nbsp;</label>
	</input>
	</div>
</form>
<c:choose>
	<c:when test="${not empty searchregistrationdgesstatebeanResultsDataSet}">
		<table id="searchregistrationdgesstatebeanTable" class="table responsive table-bordered table-hover" style="width:100%">
			<thead>
				<tr>
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

<script>
	var searchregistrationdgesstatebeanDataSet = [
			<c:forEach items="${searchregistrationdgesstatebeanResultsDataSet}" var="searchResult">
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
					DT_RowId : "<c:out value='${searchResult.candidacyId}'/>",
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
	
	$(document).ready(function() {

	


		var table = $('#searchregistrationdgesstatebeanTable').DataTable({
			language : {
				url : "${datatablesI18NUrl}",			
			},
			columns: [
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
                sRowSelect: "single",
            	sSwfPath: "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
        	}
		});
		table.columns.adjust().draw();
		
		  $('#searchregistrationdgesstatebeanTable tbody').on( 'click', 'tr', function () {
			  	if ($(this).hasClass('selected')) {
			  		$("#revertCancelButton").prop('disabled', true);
			  		$("#revertCancelForm").attr('action', '${pageContext.request.contextPath}/registrationsdgesexport/reactivate/');
			  	} else {
			  		$("#revertCancelButton").prop('disabled', false);
			  		$("#revertCancelForm").attr('action', '${pageContext.request.contextPath}/registrationsdgesexport/reactivate/' + $(this).attr('id'));
			  	}
		        $(this).toggleClass('selected');
		        $(this).children().removeClass("sorting_1");
		    } );
	}); 
</script>

