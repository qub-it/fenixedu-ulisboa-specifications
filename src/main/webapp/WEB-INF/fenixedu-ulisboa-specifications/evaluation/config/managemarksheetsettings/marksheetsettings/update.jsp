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
			code="label.evaluation.config.manageMarkSheetSettings.update" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}<%=MarkSheetSettingsController.READ_URL%>${markSheetSettings.externalId}"><spring:message
			code="label.event.back" /></a>
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


<form name='form' method="post" class="form-horizontal"
	action='${pageContext.request.contextPath}<%=MarkSheetSettingsController.UPDATE_URL%>${markSheetSettings.externalId}'
	enctype="multipart/form-data">

	<div class="panel panel-default">
		<div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-3 control-label">
                    <spring:message code="label.MarkSheetSettings.limitCreationToResponsibleTeacher" />
                </div>
                
                <div class="col-sm-6">
                        <input type="radio" name="limitCreationToResponsibleTeacher" value="true" <c:out value="${markSheetSettings.limitCreationToResponsibleTeacher ? 'checked' : ''}" /> /> <spring:message code="label.yes" /> &nbsp;
                        <input type="radio" name="limitCreationToResponsibleTeacher" value="false" <c:out value="${!markSheetSettings.limitCreationToResponsibleTeacher ? 'checked' : ''}" /> /> <spring:message code="label.no" />
                </div>
            </div>
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MarkSheetSettings.requiresExactlyOneShift" />
				</div>

				<div class="col-sm-6">
					<input type="radio" name="requiresExactlyOneShift" value="true" <c:out value="${markSheetSettings.requiresExactlyOneShift ? 'checked' : ''}" /> /> <spring:message code="label.yes" /> &nbsp;
					<input type="radio" name="requiresExactlyOneShift" value="false" <c:out value="${!markSheetSettings.requiresExactlyOneShift ? 'checked' : ''}" /> /> <spring:message code="label.no" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MarkSheetSettings.allowTeacherToChooseCertifier" />
				</div>

				<div class="col-sm-6">
					<input type="radio" name="allowTeacherToChooseCertifier" value="true" <c:out value="${markSheetSettings.allowTeacherToChooseCertifier ? 'checked' : ''}" /> /> <spring:message code="label.yes" /> &nbsp; 
					<input type="radio" name="allowTeacherToChooseCertifier" value="false" <c:out value="${!markSheetSettings.allowTeacherToChooseCertifier ? 'checked' : ''}" /> /> <spring:message code="label.no" />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.MarkSheetSettings.limitCertifierToResponsibleTeacher" />
				</div>
				
				<div class="col-sm-6">
						<input type="radio" name="limitCertifierToResponsibleTeacher" value="true" <c:out value="${markSheetSettings.limitCertifierToResponsibleTeacher ? 'checked' : ''}" /> /> <spring:message code="label.yes" /> &nbsp;
						<input type="radio" name="limitCertifierToResponsibleTeacher" value="false" <c:out value="${!markSheetSettings.limitCertifierToResponsibleTeacher ? 'checked' : ''}" /> /> <spring:message code="label.no" />
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button"
				value="<spring:message code="label.submit" />" />
		</div>
	</div>
</form>

