<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.managemobilityregistrationinformation.RegistrationController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
		<spring:message code="label.manageMobilityRegistrationInformation.searchRegistration" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
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



<div class="panel panel-default">
	<form method="get" class="form-horizontal">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.MobilityRegistrationInformation.studentNumber" />
				</div>

				<div class="col-sm-10">
					<input id="registration_number" class="form-control" type="text" name="number" value='<c:out value='${param.number}'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.MobilityRegistrationInformation.studentName" />
				</div>

				<div class="col-sm-10">
					<input id="registration_name" class="form-control" type="text" name="name" value='<c:out value='${param.name}'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.MobilityRegistrationInformation.withMobilityInformation" />
				</div>

				<div class="col-sm-10">
                    <select id="registration_withMobilityInformation"
                        class="js-example-basic-single"
                        name="withMobilityInformation">
                        <option value=""><spring:message code="label.choose.one" /></option>
                        <option value="false"><spring:message code="label.no" /></option>
                        <option value="true"><spring:message code="label.yes" /></option>
                    </select>
			
			        <script>
	                	$(document).ready(function() {
	                	     $("#registration_withMobilityInformation").select2().select2('val', '<c:out value='${param.withMobilityInformation}'/>');
	                	});
                	</script>
                    
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.search" />" />
		</div>
	</form>
</div>

<c:if test="${fn:length(searchregistrationResultsDataSet) > 200}">
	<div class="alert alert-warning" role="alert">
		<p>
			<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
			<spring:message code="label.limitexceeded" arguments="200;${fn:length(searchregistrationResultsDataSet)}" argumentSeparator=";" htmlEscape="false" />
		</p>
	</div>
</c:if>

<c:choose>
	<c:when test="${not empty searchregistrationResultsDataSet}">
		<table id="searchregistrationTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.studentNumber" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.studentName" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.degreeName" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.mobilityInformationCount" />
					</th>
					<th>
						<spring:message code="label.MobilityRegistrationInformation.lastEnrolmentYear" />
					</th>
					<%-- Operations Column --%>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="registration" items="${searchregistrationResultsDataSet}" varStatus="loop">
					<c:if test="${loop.index < 200}">
						<tr>
							<td>
								<c:out value="${registration.number}" />
							</td>
							<td>
								<c:out value="${registration.student.person.name}" />
							</td>
							<td>
								<c:if test="${not empty registration.degree.code}">[<c:out value="${registration.degree.code}" />]</c:if>
								<c:out value="${registration.degree.presentationNameI18N.content}" />
							</td>
							<td>
								<c:out value="${fn:length(registration.mobilityRegistrationInformationsSet)}" />
							</td>
							<td>
								<c:out value="${empty registration.lastEnrolmentExecutionYear ? '' : registration.lastEnrolmentExecutionYear.qualifiedName}" />
							</td>
							<td>
								<a class="btn btn-default btn-xs"
									href="${pageContext.request.contextPath}<%= RegistrationController.SEARCH_TO_VIEW_ACTION_URL %>/${registration.externalId}">
									<spring:message code='label.view' />
								</a>
							</td>
						</tr>
					</c:if>
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

						var table = $('#searchregistrationTable')
								.DataTable(
										{
											language : {
												url : "${datatablesI18NUrl}",
											},
											//CHANGE_ME adjust the actions column width if needed
											"columnDefs" : [
											//74
											{
												"width" : "74px",
												"targets" : 3
											} ],
											//Documentation: https://datatables.net/reference/option/dom
											//"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
											//"dom" : 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
											"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
											//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
											"tableTools" : {
												"sSwfPath" : "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"
											}
										});
						table.columns.adjust().draw();

						$('#searchregistrationTable tbody').on('click', 'tr',
								function() {
									$(this).toggleClass('selected');
								});

					});
</script>

