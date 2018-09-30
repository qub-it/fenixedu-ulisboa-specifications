<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.ProfessionalInformationManagementController"%>
<%@page import="org.fenixedu.academic.domain.ExecutionYear"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.HouseholdInformationUlisboaManagementController"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.HouseholdInformationManagementController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>

<spring:url var="datatablesUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
    value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl"
    value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%-- ${portal.angularToolkit()} --%>
${portal.toolkit()}

<link
    href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
<link
    href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link
    href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript"
    src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>

<script type="text/javascript">
    function openCreateModal() {
        $("#householdininformationCreateForm").modal('toggle');
      }

</script>

<div class="modal fade" id="householdininformationCreateForm">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="householdininformationCreateForm" action="${pageContext.request.contextPath}<%= HouseholdInformationManagementController.CREATE_URL %>/${student.externalId}" method="GET">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.HouseholdInformationForm.executionYear.required" />
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="form-group row">
                        <div class="col-sm-2 control-label">
                            <spring:message code="label.HouseholdInformationForm.executionYear"/>
                        </div>
                        <div class="col-sm-10 control-label">
                            <select id="executionYear" name="executionYear" class="form-control">
                                <c:forEach var="executionYear" items="${ allowedExecutionYears }">
                                    <option value="${ executionYear.externalId }"><c:out value="${ executionYear.qualifiedName }" /></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>                    
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="confirmButton" class="btn btn-primary" type="submit">
                        <spring:message code="label.confirm" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.HouseholdInformationManagement.search" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">

	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="#" onclick="openCreateModal()">
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
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.idDocumentType" />
						</th>
						<td>
							<c:out value='${student.person.idDocumentType.localizedName}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.documentIdNumber" />
						</th>
						<td>
							<c:out value='${student.person.documentIdNumber}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.documentIdControlNumber" />
						</th>
						<td>
							<c:choose>
								<c:when test="${not empty student.person.identificationDocumentSeriesNumberValue}">
									<c:out value='${student.person.identificationDocumentSeriesNumberValue}' />
								</c:when>
								<c:when test="${not empty student.person.identificationDocumentExtraDigitValue}">
									<c:out value='${student.person.identificationDocumentExtraDigitValue}' />
								</c:when>
							</c:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.HouseholdInformationForm.details" />
        </h3>
    </div>

    <spring:message code="label.yes" var="yesLabel" />
    <spring:message code="label.no" var="noLabel" />
    <div class="panel-body">
        <table class="table">
            <tbody>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.flunkedBeforeUniversity" />
                    </th>
                    <td>
                        <c:if test="${ student.person.personUlisboaSpecifications.flunkedBeforeUniversity != null }">
                            <c:out value='${ student.person.personUlisboaSpecifications.flunkedBeforeUniversity ? yesLabel : noLabel}' />
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.flunkedHighSchool" />
                    </th>
                    <td>
                        <c:if test="${ student.person.personUlisboaSpecifications.flunkedHighSchool  != null}">
                            <c:out value='${ student.person.personUlisboaSpecifications.flunkedHighSchool ? yesLabel : noLabel}' />
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.flunkedHighSchoolTimes" />
                    </th>
                    <td>
                        <c:out value='${ student.person.personUlisboaSpecifications.flunkedHighSchoolTimes}' />
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.flunkedPreHighSchool" />
                    </th>
                    <td>
                        <c:if test="${ student.person.personUlisboaSpecifications.flunkedPreHighSchool  != null}">
                            <c:out value='${ student.person.personUlisboaSpecifications.flunkedPreHighSchool ? yesLabel : noLabel}' />
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.flunkedPreHighSchoolTimes" />
                    </th>
                    <td>
                        <c:out value='${ student.person.personUlisboaSpecifications.flunkedPreHighSchoolTimes}' />
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.socialBenefitsInHighSchool" />
                    </th>
                    <td>
                        <c:if test="${  student.person.personUlisboaSpecifications.socialBenefitsInHighSchool != null }">
                            <c:out value='${ student.person.personUlisboaSpecifications.socialBenefitsInHighSchool ? yesLabel : noLabel}' />
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.socialBenefitsInHighSchoolDescription" />
                    </th>
                    <td>
                        <c:out value='${ student.person.personUlisboaSpecifications.socialBenefitsInHighSchoolDescription}' />
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.firstTimeInPublicUniv" />
                    </th>
                    <td>
                        <c:if test="${  student.person.personUlisboaSpecifications.firstTimeInPublicUniv != null }">
                            <c:out value='${ student.person.personUlisboaSpecifications.firstTimeInPublicUniv ? yesLabel : noLabel}' />
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.publicUnivCandidacies" />
                    </th>
                    <td>
                        <c:out value='${ student.person.personUlisboaSpecifications.publicUnivCandidacies}' />
                    </td>
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3">
                        <spring:message code="label.HouseholdInformationForm.firstTimeInUlisboa" />
                    </th>
                    <td>
                        <c:if test="${ student.person.personUlisboaSpecifications.firstTimeInUlisboa != null }">
                            <c:out value='${student.person.personUlisboaSpecifications.firstTimeInUlisboa ? yesLabel : noLabel}' />
                        </c:if>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="panel-footer">
        <a class="btn btn-primary"
            href="${pageContext.request.contextPath}<%= HouseholdInformationUlisboaManagementController.UPDATE_URL %>/${student.externalId}/<%= ExecutionYear.readCurrentExecutionYear().getExternalId() %>">
            <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
            &nbsp;
            <spring:message code='label.update' />
        </a>
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
								<spring:message code='label.title.householdinformationmanagement' />
							</a>
                            <a class="btn btn-default btn-xs"
                                href="${pageContext.request.contextPath}<%= ProfessionalInformationManagementController.READ_URL %>/${student.externalId}/${pid.executionYear.externalId}">
                                <spring:message code='label.title.professionalInformationManagement' />
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

						$('#searchmobilityprogramtypeTable tbody').on('click',
								'tr', function() {
									$(this).toggleClass('selected');
								});

					});
</script>


