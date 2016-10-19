<%@page import="org.fenixedu.ulisboa.specifications.ui.degrees.precedence.DegreesPrecedenceController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
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
	<h1>
		<spring:message code="label.manageDegreePrecedences.viewDegree" />
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">

	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= DegreesPrecedenceController.SEARCH_URL %>">
		<spring:message code="label.event.back" />
	</a>

	&nbsp;|&nbsp;

	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= DegreesPrecedenceController.ADD_DEGREE_URL %>/${degree.externalId}">
		<spring:message code="label.DegreePrecedences.add" />
	</a>
</div>

<div class="modal fade" id="deleteModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="deleteForm" action="${pageContext.request.contextPath}<%= DegreesPrecedenceController.REMOVE_DEGREE_URL %>/${degree.externalId}"
				method="POST">
				
				<input id="degreeToRemoveId" type="hidden" name="degreeToRemoveId" value="" />
				
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
						<spring:message code="label.manageDegreePrecedences.confirmDelete" />
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
<script>
	function showDeleteModal(externalId) {
		$("#degreeToRemoveId").attr('value', externalId);
		$("#deleteModal").modal('show');
	};
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
							<spring:message code="label.DegreePrecedences.code" />
						</th>
						<td>
							<c:out value='${degree.code}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.DegreePrecedences.degreeType" />
						</th>
						<td>
							<c:out value='${degree.degreeType.name.content}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.DegreePrecedences.degreeName" />
						</th>
						<td>
							<c:out value='${degree.nameI18N.content}' />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<h3><spring:message code="label.DegreePrecedences.precedentDegrees" /></h3>

<c:choose>
	<c:when test="${not empty degree.precedentDegrees}">
		<table id="simpletablename" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th>
						<spring:message code="label.DegreePrecedences.code" />
					</th>
					<th>
						<spring:message code="label.DegreePrecedences.degreeType" />
					</th>
					<th>
						<spring:message code="label.DegreePrecedences.degreeName" />
					</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="precedentDegree" items="${degree.precedentDegrees}">
					<tr>
						<td>
							<c:out value="${precedentDegree.code}" />
						</td>
						<td>
							<c:out value="${precedentDegree.degreeType.name.content}" />
						</td>
						<td>
							<c:out value="${precedentDegree.nameI18N.content}" />
						</td>
						<td>
							<a class="btn btn-default btn-xs"
								onclick="showDeleteModal(${precedentDegree.externalId}); return false;" href="#">
								<spring:message code='label.remove' />
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
	$(document).ready(function() {

					var table = $('#simpletablename')
							.DataTable(
									{
										language : {
											url : "${datatablesI18NUrl}",
										},

										"columnDefs" : [
										//54
										{
											"width" : "54px",
											"targets" : 3
										} ],

										//Documentation: https://datatables.net/reference/option/dom
										//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
										//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
										//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
                                        dom: '<"col-sm-5"l><"col-sm-3"f><"col-sm-3"B>rtip', //FilterBox = YES && ExportOptions = YES
                                        buttons: [
                                            'copyHtml5',
                                            'excelHtml5',
                                            'csvHtml5',
                                            'pdfHtml5'
                                        ],
										"tableTools" : {
											"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
										}
									});
					table.columns.adjust().draw();

					$('#simpletablename tbody').on('click',
							'tr', function() {
								$(this).toggleClass('selected');
							});

	});
</script>
