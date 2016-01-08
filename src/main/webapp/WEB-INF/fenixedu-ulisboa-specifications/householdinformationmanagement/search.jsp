<%@page import="org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.HouseholdInformationFormController"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.HouseholdInformationManagementController"%>
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
	<h1><spring:message code="label.HouseholdInformationManagement.search" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<a class="" href="${pageContext.request.contextPath}<%= HouseholdInformationManagementController.CREATE_URL %>/${student.externalId}">
		<spring:message code="label.create" />
	</a>
</div>


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
							<spring:message code="label.HouseholdInformationForm.studentNumber" />
						</th>
						<td>
							<c:out value='${student.number}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.studentName" />
						</th>
						<td>
							<c:out value='${student.person.name}' />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<c:choose>
	<c:when test="${not empty student.personalIngressionsDataSet}">
		<table id="simpletable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th>
						<spring:message code="label.HouseholdInformationForm.executionYear" />
					</th>
					<th>
						<spring:message code="label.HouseholdInformationForm.professionType" />
					</th>
					<th>
						<spring:message code="label.HouseholdInformationForm.grantOwnerType" />
					</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="pid" items="${student.personalIngressionsDataSet}">
					<tr>
						<td>
							<c:out value="${pid.executionYear.qualifiedName}" />
						</td>
						<td>
							<c:out value="${pid.professionType.localizedName}" />
						</td>
						<td>
							<c:if test="${not empty pid.grantOwnerType}">
								<spring:message code="label.GrantOwnerType.${pid.grantOwnerType}" />
							</c:if>
						</td>
						<td>
							<a class="btn btn-default btn-xs"
								href="${pageContext.request.contextPath}<%= HouseholdInformationManagementController.READ_URL %>/${student.externalId}/${pid.executionYear.externalId}">
								<spring:message code='label.view' />
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

						var table = $('#simpletable')
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
											
											"order": [[ 0, "desc" ]],

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

						$('#searchmobilityprogramtypeTable tbody').on('click',
								'tr', function() {
									$(this).toggleClass('selected');
								});

					});
</script>


