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
		<spring:message code="label.manageMobilityRegistrationInformation.searchMobilityRegistrationInformation" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= MobilityRegistrationInformationController.CREATE_URL %>/${registration.externalId}">
		<spring:message code="label.event.create" />
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


<script type="text/javascript">
	function processDelete(externalId) {
		$("#mobilityRegistrationInformationId").attr("value", externalId);
		$('#deleteModal').modal('toggle');
	}
</script>


<div class="modal fade" id="deleteModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="deleteForm"
				action="${pageContext.request.contextPath}<%= MobilityRegistrationInformationController.SEARCH_TO_DELETE_ACTION_URL %>${registration.externalId}"
				method="POST">
				<input id="mobilityRegistrationInformationId" type="hidden" name="mobilityRegistrationInformationId" value="" />
				
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body">
					<p>
						<spring:message code="label.manageMobilityRegistrationInformation.searchMobilityRegistrationInformation.confirmDelete" />
					</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.close" />
					</button>
					<button id="deleteButton" class="btn btn-danger" type="submit">
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


<h4><spring:message code="label.manageMobilityRegistrationInformation.incoming" /></h4>

<c:choose>
	<c:when test="${not empty searchmobilityregistrationinformationResultsDataSetForOutgoing}">
		<table id="searchmobilityregistrationinformationTableForOutgoing" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.begin" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.end" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.mobilityProgramType" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.mobilityActivityType" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.foreignInstitutionUnit" />
					</th>
					<%-- Operations Column --%>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="information" items="${searchmobilityregistrationinformationResultsDataSetForOutgoing}">
					<tr>
						<td>
							<c:out value='${information.begin.qualifiedName}' />
						</td>
						<td>
							<c:out value='${information.end.qualifiedName}' />
						</td>
						<td>
							<c:out value='${information.mobilityProgramType.name.content}' />
						</td>
						<td>
							<c:out value='${information.mobilityActivityType.name.content}' />
						</td>
						<td>
							<c:out value='${information.foreignInstitutionUnit.nameI18n.content}' />
						</td>
						<td>
							<a class="btn btn-xs btn-danger" href="#" onclick="javascript:processDelete('${information.externalId}')">
								<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
								&nbsp;
								<spring:message code='label.delete' />
							</a>
						</td>
					</tr>
				</c:forEach>

			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>	
</c:choose>

<h4><spring:message code="label.manageMobilityRegistrationInformation.outgoing" /></h4>

<c:choose>
	<c:when test="${not empty searchmobilityregistrationinformationResultsDataSet}">
		<table id="searchmobilityregistrationinformationTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.programDuration" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.mobilityActivityType" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.foreignInstitutionUnit" />
					</th>
					<%-- Operations Column --%>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="information" items="${searchmobilityregistrationinformationResultsDataSet}">
					<tr>
						<td>
							<spring:message code="label.SchoolPeriodDuration.${information.programDuration}" />
						</td>
						<td>
							<c:out value='${information.mobilityActivityType.name.content}' />
						</td>
						<td>
							<c:out value='${information.foreignInstitutionUnit.nameI18n.content}' />
						</td>
						<td>
							<a class="btn btn-xs btn-danger" href="#" onclick="javascript:processDelete('${information.externalId}')">
								<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
								&nbsp;
								<spring:message code='label.delete' />
							</a>
						</td>
					</tr>
				</c:forEach>

			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-warning" role="alert">

			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>

		</div>

	</c:otherwise>
</c:choose>

<script>
	$(document)
			.ready(
					function() {

						var table = $(
								'#searchmobilityregistrationinformationTable')
								.DataTable(
										{
											language : {
												url : "${datatablesI18NUrl}",
											},
											"columns" : [ 
											{
												data : 'programduration'
											}, 
											{
												data : 'mobilityactivitytype'
											}, {
												data : 'foreigninstitutionunit'
											}, {
												data : 'actions'
											}

											],
											//CHANGE_ME adjust the actions column width if needed
											"columnDefs" : [
											//74
											{
												"width" : "74px",
												"targets" : 7
											} ],
											"data" : searchmobilityregistrationinformationDataSet,
											//Documentation: https://datatables.net/reference/option/dom
											"dom" : '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
											//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
											//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
											//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
											"tableTools" : {
												"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
											}
										});
						table.columns.adjust().draw();

						$('#searchmobilityregistrationinformationTable tbody')
								.on('click', 'tr', function() {
									$(this).toggleClass('selected');
								});

					});

	
	
	
$(document)
	.ready(
			function() {

				var table = $(
						'#searchmobilityregistrationinformationTableForOutgoing')
						.DataTable(
								{
									language : {
										url : "${datatablesI18NUrl}",
									},
									"columns" : [ 
									{
										data : 'begin'
									}, {
										data : 'end'
									}, {
										data : 'mobilityprogramtype'
									}, {
										data : 'mobilityactivitytype'
									}, {
										data : 'foreigninstitutionunit'
									}, {
										data : 'actions'
									}

									],
									//CHANGE_ME adjust the actions column width if needed
									"columnDefs" : [
									//74
									{
										"width" : "74px",
										"targets" : 7
									} ],
									"data" : searchmobilityregistrationinformationDataSet,
									//Documentation: https://datatables.net/reference/option/dom
									"dom" : '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
									//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
									//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
									//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
									"tableTools" : {
										"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
									}
								});
				table.columns.adjust().draw();

				$('#searchmobilityregistrationinformationTableForOutgoing tbody')
						.on('click', 'tr', function() {
							$(this).toggleClass('selected');
						});

			});
	
	</script>

