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
		<br />
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
		<br />
	<input type="submit" value="Pesquisar" style="width : 100px">
	<br />
	<br />
</form>
<c:choose>
	<c:when test="${not empty searchregistrationdgesstatebeanResultsDataSet}">
		<table id="searchregistrationdgesstatebeanTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th><spring:message code="label.studentsListByCurricularCourse.degree"/></th>
					<th><spring:message code="label.identification.number"/></th>
					<th><spring:message code="label.student"/></th>
					<th><spring:message code="label.is.registered"/></th>
					<th><spring:message code="label.candidacy"/></th>
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
				"DT_RowId" : '<c:out value='${searchResult.candidacyId}'/>',
"degreeCode" : "<c:out value='${searchResult.degreeCode}'/>",
"idnumber" : "<c:out value='${searchResult.idNumber}'/>",
"name" : "<c:out value='${searchResult.name}'/>",
"registrationstate" : "<c:out value='${searchResult.registrationState}'/>",
"candidacyState" : "<c:out value='${searchResult.candidacyState}'/>",
			},
            </c:forEach>
    ];
	
	$(document).ready(function() {

	


		var table = $('#searchregistrationdgesstatebeanTable').DataTable({
			language : {
				url : "${datatablesI18NUrl}",			
			},
			columns: [
				{ data: 'degreeCode' },
				{ data: 'idnumber' },
				{ data: 'name' },
				{ data: 'registrationstate' },
				{ data: 'candidacyState' },	
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

