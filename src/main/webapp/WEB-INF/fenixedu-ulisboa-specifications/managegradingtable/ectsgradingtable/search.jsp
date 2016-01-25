<%@page import="org.fenixedu.ulisboa.specifications.ui.ectsgradingtable.EctsGradingTableBackofficeController"%>
<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter"%>
<%@page import="org.fenixedu.academic.domain.organizationalStructure.Unit"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/zs/dataTables.bootstrap.min.css" />
<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />
<link href="${pageContext.request.contextPath}/static/treasury/css/dataTables.responsive.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />

<script src="${pageContext.request.contextPath}/static/treasury/js/dataTables.responsive.js"></script>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/treasury/js/omnis.js"></script>

${portal.toolkit()}


<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message
            code="label.gradingTables.manageECTSGradingTables" />
        <small></small>
    </h1>
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

<div class="panel panel-default">
    <form method="get" class="form-horizontal" action="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.SEARCH_URL%>">
        <div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.academicRequest.executionYear" />
                </div>
                <div class="col-sm-6">
                    <select id="academicRequest_executionYear"
                        class="js-example-basic-single"
                        name="executionYear">
                        <c:forEach var="year" items="${executionYearsList}">
                        	<c:if test="${year.externalId != selectedYear.externalId}">
                        		<option value="${year.externalId}">${year.qualifiedName}</option>
                        	</c:if>
                        	<c:if test="${year.externalId == selectedYear.externalId}">                    	
	                        	<option value="${year.externalId}" selected>${year.qualifiedName}</option>
                        	</c:if>
                        </c:forEach>
                    </select>
                    <script type="text/javascript">
		                $("#academicRequest_executionYear").select2();
                    </script>
                </div>
            </div>
        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-default" role="button"
                value="<spring:message code="label.search" />" />
        </div>
    </form>
</div>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="label.gradingTables.instituionTable" /></h3>
	</div>
	<div class="panel-body">
		<div>
			<a class="btn btn-default" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.CREATE_INSTITUTIONAL_URL%>${selectedYear.externalId}"><spring:message code='label.gradingTables.generateTable'/></a>
		</div>
		<c:choose>
		    <c:when test="${institutionGradeTable != null}">
				<table id="institutionGradeTable" class="table responsive table-bordered table-hover">
					<thead>
						<tr>
							<th><!-- School Name  --></th>
							<c:forEach var="conversion" items="${institutionGradeTable.data.table}" varStatus="loop">
								<th><c:out value='${conversion.mark}'/></th>
							</c:forEach>
				            <th><!-- Row actions  --></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><%= Unit.getInstitutionName().getContent() %></td>
							<c:forEach var="conversion" items="${institutionGradeTable.data.table}" varStatus="loop">
								<td><c:out value='${conversion.ectsGrade}'/></td>
							</c:forEach>
							<td>
								<a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/${institutionGradeTable.externalId}"><spring:message code='label.gradingTables.delete'/></a>
							</td>
						</tr>
					</tbody>
				</table>
			</c:when>
		    <c:otherwise>
		        <div class="alert alert-warning" role="alert">
		            <p>
		                <span class="glyphicon glyphicon-exclamation-sign"
		                    aria-hidden="true">&nbsp;</span>
		                <spring:message code="label.noResultsFound" />
		            </p>
		        </div>
		    </c:otherwise>
		</c:choose>
	</div>
</div>


<div>
	<a class="btn btn-default" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.CREATE_DEGREES_URL%>${selectedYear.externalId}"><spring:message code='label.gradingTables.generateTable'/></a>
</div>
<c:choose>
    <c:when test="${not empty degreeGradeTable}">
		<table id="degreeGradeTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th><spring:message code='label.gradingTables.degree'/></th>
					<th><spring:message code='label.gradingTables.programConclusion'/></th>
					<c:forEach var="dataHeader" items="${degreeGradeTableHeaders}" varStatus="loop_h">
						<th><c:out value='${dataHeader}'/></th>
					</c:forEach>
		            <th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="degreeTable" items="${degreeGradeTable}" varStatus="loop_out">
				<tr>
					<td><c:out value="${degreeTable.degree.presentationNameI18N.content}" /></td>
					<td><c:out value="${degreeTable.programConclusion.name.content}" /></td>
					<c:forEach var="conversion" items="${degreeTable.data.table}" varStatus="loop">
						<td><c:out value='${conversion.ectsGrade}'/></td>
					</c:forEach>
					<td>
						<a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/${degreeTable.externalId}"><spring:message code='label.gradingTables.delete'/></a>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		<div>
			<a id="select-all-degrees" class="btn btn-default"><spring:message code='label.gradingTables.selectAll'/></a>
			<a id="delete-all-selected-degrees" class="btn btn-default" href="#"><spring:message code='label.gradingTables.deleteSelected'/></a>
		</div>
	</c:when>
    <c:otherwise>
        <div class="alert alert-warning" role="alert">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span>
                <spring:message code="label.noResultsFound" />
            </p>
        </div>
    </c:otherwise>
</c:choose>

<div>
	<a class="btn btn-default" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.CREATE_COURSESS_URL%>${selectedYear.externalId}"><spring:message code='label.gradingTables.generateTable'/></a>
</div>
<c:choose>
    <c:when test="${not empty courseGradeTable}">
		<table id="courseGradeTable" class="table responsive table-bordered table-hover">			
			<thead>
				<tr>
					<th><spring:message code='label.gradingTables.course'/></th>
					<c:forEach var="dataHeader" items="${courseGradeTableHeaders}" varStatus="loop_h">
						<th><c:out value='${dataHeader}'/></th>
					</c:forEach>
		            <th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="courseTable" items="${courseGradeTable}" varStatus="loop_out">
				<tr>
					<td><c:out value="${courseTable.competenceCourse.nameI18N.content}" /></td>
					<c:forEach var="conversion" items="${courseTable.data.table}" varStatus="loop">
						<td><c:out value='${conversion.ectsGrade}'/></td>
					</c:forEach>
					<td>
						<a class="btn btn-default btn-xs" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/${courseTable.externalId}"><spring:message code='label.gradingTables.delete'/></a>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		<div>
			<a id="select-all-courses" class="btn btn-default"><spring:message code='label.gradingTables.selectAll'/></a>
			<a id="delete-all-selected-courses" class="btn btn-default" href="#"><spring:message code='label.gradingTables.deleteSelected'/></a>
		</div>
	</c:when>
    <c:otherwise>
        <div class="alert alert-warning" role="alert">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span>
                <spring:message code="label.noResultsFound" />
            </p>
        </div>
    </c:otherwise>
</c:choose>
<script>
	
	$(document).ready(function() {

 		createDataTables('institutionGradeTable', false /*filterable*/, false /*show tools*/, false /*paging*/, "${pageContext.request.contextPath}", "${datatablesI18NUrl}");
 		// This needs a delayer to wait for the institutionGradeTable layout.
 		$('#institutionGradeTable_info').hide();
 		$('#institutionGradeTable .sorting, #institutionGradeTable .sorting_desc, #institutionGradeTable .sorting_asc').each( function (index) {
 			$(this).removeClass('sorting');
 			$(this).removeClass('sorting_asc');
 			$(this).removeClass('sorting_desc');
 			$(this).off('click');
 		});
		
		createDataTables('degreeGradeTable', true /*filterable*/, false /*show tools*/, true /*paging*/, "${pageContext.request.contextPath}", "${datatablesI18NUrl}");
		$('#select-all-degrees').on('click', function (evt) {
			evt.preventDefault();
			$('#degreeGradeTable tr').addClass('selected');
			return;
		});
		$('#delete-all-selected-degrees').on('click', function () {
			var oids = '';
			var url = "${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/";
			$('#degreeGradeTable tr.selected a').each( function (index) {
				var slugs = $(this).attr('href').split('/');
				oids += slugs.pop() + '+';
			});
			oids = oids.slice(0, -1);
			$(this).attr('href', url+oids);
		});
		
		createDataTables('courseGradeTable', true /*filterable*/, false /*show tools*/, true /*paging*/, "${pageContext.request.contextPath}", "${datatablesI18NUrl}");
		$('#select-all-courses').on('click', function (evt) {
			evt.preventDefault();
			$('#courseGradeTable tr').addClass('selected');
			return;
		});
		$('#delete-all-selected-courses').on('click', function () {
			var oids = '';
			var url = "${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/";
			$('#courseGradeTable tr.selected a').each( function (index) {
				var slugs = $(this).attr('href').split('/');
				oids += slugs.pop() + '+';
			});
			oids = oids.slice(0, -1);
			$(this).attr('href', url+oids);
		});
 
	}); 
</script>

