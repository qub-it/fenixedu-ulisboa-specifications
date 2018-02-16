<%@page import="org.fenixedu.academic.domain.evaluation.config.MarkSheetSettings"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.evaluation.config.managemarksheetsettings.MarkSheetSettingsController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

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
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%--${portal.toolkit()}--%>

<link
	href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css"
	rel="stylesheet" />
<script
	src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
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
	src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>
<script
	src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script
	src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message
			code="label.evaluation.config.manageMarkSheetSettings.readMarkSheetSettings" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}<%=MarkSheetSettingsController.UPDATE_URL%>${markSheetSettings.externalId}"><spring:message
			code="label.event.evaluation.config.manageMarkSheetSettings.update" /></a> &nbsp; | &nbsp;
	<span class="glyphicon glyphicon-cloud-upload" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}<%=MarkSheetSettingsController.READ_URL%>${markSheetSettings.externalId}/updatetemplatefile"><spring:message
			code="label.event.evaluation.config.manageMarkSheetSettings.updateTemplateFile" /></a>
</div>
<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:message code="label.details" />
		</h3>
	</div>
	<div class="panel-body">
		<form method="post" class="form-horizontal">
			<spring:message code="label.yes" var="yesLabel" />
			<spring:message code="label.no" var="noLabel" />
			<table class="table">
				<tbody>
                    <tr>
                        <th scope="row" class="col-xs-4"><spring:message
                                code="label.MarkSheetSettings.limitCreationToResponsibleTeacher" /></th>
                        <td>
                            <c:out value="${markSheetSettings.limitCreationToResponsibleTeacher ? yesLabel : noLabel }"></c:out>
                        </td>
                    </tr>
					<tr>
						<th scope="row" class="col-xs-4"><spring:message
								code="label.MarkSheetSettings.requiredNumberOfShifts" /></th>
						<td>
                            <c:choose>
                                <c:when test="<%=MarkSheetSettings.isUnspecifiedNumberOfShifts()%>">
                                    <spring:message code="label.CompetenceCourseMarkSheet.shifts.unspecified" />
                                </c:when>
                                <c:when test="<%=MarkSheetSettings.isNotAllowedShifts()%>">
                                    <spring:message code="label.CompetenceCourseMarkSheet.shifts.not.allowed" />
                                </c:when>
                                <c:when test="<%=MarkSheetSettings.isRequiredAtLeastOneShift()%>">
                                    <spring:message code="label.CompetenceCourseMarkSheet.shift.required" />
                                </c:when>
                                <c:otherwise>
        							<c:out value="${markSheetSettings.requiredNumberOfShifts}"></c:out>
                                </c:otherwise>
                            </c:choose>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-4"><spring:message
								code="label.MarkSheetSettings.allowTeacherToChooseCertifier" /></th>
						<td>
							<c:out value="${markSheetSettings.allowTeacherToChooseCertifier ? yesLabel : noLabel }"></c:out>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-4"><spring:message
								code="label.MarkSheetSettings.limitCertifierToResponsibleTeacher" /></th>
						<td>
							<c:out value="${markSheetSettings.limitCertifierToResponsibleTeacher ? yesLabel : noLabel }"></c:out>
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-4"><spring:message
								code="label.MarkSheetSettings.templateFile" /></th>
						<td>
							<c:choose>
								<c:when test="${empty markSheetSettings.templateFile}">
									-
								</c:when>
								<c:otherwise>
									<a href="${pageContext.request.contextPath}<%=MarkSheetSettingsController.DOWNLOADTEMPLATEFILE_URL%>${markSheetSettings.externalId}">
										<c:out value='${markSheetSettings.templateFile.filename}' />
									</a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>


