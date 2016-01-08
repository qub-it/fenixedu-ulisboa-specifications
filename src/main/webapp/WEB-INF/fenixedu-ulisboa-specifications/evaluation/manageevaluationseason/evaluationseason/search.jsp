<%@page import="org.fenixedu.academic.domain.EvaluationSeason"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.evaluation.manageevaluationseason.EvaluationSeasonController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.angularToolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css"
	rel="stylesheet" />
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
		<spring:message code="label.evaluation.manageEvaluationSeason.searchEvaluationSeason" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a class=""
		href="${pageContext.request.contextPath}<%=EvaluationSeasonController.CREATE_URL%>"><spring:message
			code="label.event.create" /></a>
</div>
<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>

<c:choose>
	<c:when test="${not empty searchevaluationseasonResultsDataSet}">
		<table id="searchevaluationseasonTable" class="table responsive table-bordered table-hover" width="100%">
			<thead>
				<tr>
					<th><spring:message code="label.EvaluationSeason.name" /></th>
					<th><spring:message code="label.EvaluationSeason.acronym" /></th>
					<th><spring:message code="label.EvaluationSeason.code" /></th>
					<th><spring:message code="label.EvaluationSeason.active" /></th>
					<th><spring:message code="label.EvaluationSeason.type" /></th>
					<th><spring:message code="label.EvaluationSeason.requiresEnrolmentEvaluation" /></th>
					<%-- Operations Column --%>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="var" items="${searchevaluationseasonResultsDataSet}">
					<tr>
						<td><c:out value="${var.name.content}"></c:out></td>
						<td><c:out value="${var.acronym.content}"></c:out></td>
						<td><c:out value="${var.code}"></c:out></td>
						<td><c:if test="${var.information.active}">
								<spring:message code="label.true" />
							</c:if> <c:if test="${not var.information.active}">
								<spring:message code="label.false" />
							</c:if></td>
						<td><c:out
								value="<%=EvaluationSeasonServices
									.getTypeDescriptionI18N((EvaluationSeason) pageContext.getAttribute("var"))
									.getContent()%>"></c:out></td>
						<td><c:if
								test="<%=EvaluationSeasonServices
									.isRequiresEnrolmentEvaluation((EvaluationSeason) pageContext.getAttribute("var"))%>">
								<spring:message code="label.true" />
							</c:if> <c:if
								test="<%=!EvaluationSeasonServices
									.isRequiresEnrolmentEvaluation((EvaluationSeason) pageContext.getAttribute("var"))%>">
								<spring:message code="label.false" />
							</c:if></td>
						<td><a class="btn btn-default btn-xs"
							href="${pageContext.request.contextPath}<%=EvaluationSeasonController.SEARCH_TO_VIEW_ACTION_URL%>${var.externalId}"><spring:message
									code='label.view' /></a>&nbsp;&nbsp;
							<form method="post"
								action="${pageContext.request.contextPath}<%=EvaluationSeasonController.SEARCH_TO_ORDER_UP_ACTION_URL%>${var.externalId}/orderup">
								<button type="submit" class="btn btn-default btn-xs">
									<spring:message code="label.EvaluationSeason.order.up" />
								</button>
							</form>&nbsp;&nbsp;
							<form method="post"
								action="${pageContext.request.contextPath}<%=EvaluationSeasonController.SEARCH_TO_ORDER_DOWN_ACTION_URL%>${var.externalId}/orderdown">
								<button type="submit" class="btn btn-default btn-xs">
									<spring:message code="label.EvaluationSeason.order.down" />
								</button>
							</form></td>

					</tr>
				</c:forEach>
			</tbody>
		</table>
		<script type="text/javascript">
	    createDataTablesWithSortSwitch('searchevaluationseasonTable',
		    true /*filterable*/, false /*show tools*/,
		    false /*paging*/, false /* sortable */,
		    "${pageContext.request.contextPath}",
		    "${datatablesI18NUrl}");
	</script>
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


