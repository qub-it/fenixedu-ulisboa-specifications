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

<style>
	.ects-row {
		margin-top: 2em;
	}
</style>


<div class="modal fade" id="deleteECTSTableModal">
    <div class="modal-dialog">
        <div class="modal-content">
	        <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                <span aria-hidden="true">&times;</span>
	            </button>
	            <h4 class="modal-title">
	                <spring:message code="label.confirmation" />
	            </h4>
	        </div>
	        <div class="modal-body">
	            <div class="form-group row">
	                <spring:message code="label.gradingTables.deleteConfirmation" />
	            </div>
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-default" data-dismiss="modal">
	                <spring:message code="label.close" />
	            </button>
	            <a id="submitButton" class="btn btn-primary" href="#">
	                <spring:message code="label.confirm" />
	            </a>
	        </div>
        </div>
    </div>
</div>
<script type="text/javascript">
function openDeletionModal(url) {
    $("#deleteECTSTableModal #submitButton").attr("href", url);
    $('#deleteECTSTableModal').modal('toggle');
}
</script>


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

<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="label.gradingTables.instituionTable" /></h3>
	</div>
	<div class="panel-body">
		<div class="row">
			<div class="col-xs-2">
				<a class="btn btn-primary" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.CREATE_INSTITUTIONAL_URL%>${selectedYear.externalId}/${sectoken}"><spring:message code='label.gradingTables.generateTable'/></a>
			</div>
		</div>
		<div class="row ects-row">
			<div class="col-xs-12">
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
										<a class="btn btn-danger btn-xs" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/${institutionGradeTable.externalId}/${sectoken}"><spring:message code='label.gradingTables.delete'/></a>
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
	</div>
</div>


<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="label.gradingTables.degreeTables" /></h3>
	</div>
	<div class="panel-body">
		<div class="row">
			<div class="col-xs-2">
				<a class="btn btn-primary" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.CREATE_DEGREES_URL%>${selectedYear.externalId}/${sectoken}"><spring:message code='label.gradingTables.generateTables'/></a>
			</div>
		</div>
		<c:choose>
		    <c:when test="${not empty degreeGradeTable}">
		    	<div class="row ects-row">
					<div class="col-xs-12">
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
										<a class="btn btn-danger btn-xs" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/${degreeTable.externalId}/${sectoken}"><spring:message code='label.gradingTables.delete'/></a>
									</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12">
						<a id="select-all-degrees" class="btn btn-default"><spring:message code='label.gradingTables.selectAll'/></a>
						<a id="delete-all-selected-degrees" class="btn btn-danger" href="#"><spring:message code='label.gradingTables.deleteSelected'/></a>
					</div>
				</div>
			</c:when>
		    <c:otherwise>
		    	<div class="row ects-row">
					<div class="col-xs-12">
				        <div class="alert alert-warning" role="alert">
				            <p>
				                <span class="glyphicon glyphicon-exclamation-sign"
				                    aria-hidden="true">&nbsp;</span>
				                <spring:message code="label.noResultsFound" />
				            </p>
				        </div>
			        </div>
		        </div>
		    </c:otherwise>
		</c:choose>
	</div>
</div>


<div class="panel panel-default">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="label.gradingTables.courseTables" /></h3>
	</div>
	<div class="panel-body">
		<div class="row">
			<div class="col-xs-2">
				<a class="btn btn-primary" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.CREATE_COURSESS_URL%>${selectedYear.externalId}/${sectoken}"><spring:message code='label.gradingTables.generateTables'/></a>
			</div>
		</div>
		<c:choose>
		    <c:when test="${not empty courseGradeTable}">
		    	<div class="row ects-row">
					<div class="col-xs-12">
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
										<a class="btn btn-danger btn-xs" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/${courseTable.externalId}/${sectoken}"><spring:message code='label.gradingTables.delete'/></a>
									</td>
								</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-12">
						<a id="select-all-courses" class="btn btn-default"><spring:message code='label.gradingTables.selectAll'/></a>
						<a id="delete-all-selected-courses" class="btn btn-danger" href="#"><spring:message code='label.gradingTables.deleteSelected'/></a>
					</div>
				</div>
			</c:when>
		    <c:otherwise>
		    	<div class="row ects-row">
					<div class="col-xs-12">
				        <div class="alert alert-warning" role="alert">
				            <p>
				                <span class="glyphicon glyphicon-exclamation-sign"
				                    aria-hidden="true">&nbsp;</span>
				                <spring:message code="label.noResultsFound" />
				            </p>
				        </div>
					</div>
				</div>
		    </c:otherwise>
		</c:choose>
	</div>
</div>


<script>

	function delay (pred, fn) {
		if (pred()) {
			fn();
		} else {
			setTimeout(delay, 200, pred, fn)
		}
	}
	
	$(document).ready(function() {

 		createDataTables('institutionGradeTable', false /*filterable*/, false /*show tools*/, false /*paging*/, "${pageContext.request.contextPath}", "${datatablesI18NUrl}");
 		delay(
 			function () {
				return $('#institutionGradeTable_info').length && $('#institutionGradeTable').length;
			},
			function () {
				$('#institutionGradeTable_info').hide();
	 	 		$('#institutionGradeTable .sorting, #institutionGradeTable .sorting_desc, #institutionGradeTable .sorting_asc').each( function (index) {
	 	 			$(this).removeClass('sorting');
	 	 			$(this).removeClass('sorting_asc');
	 	 			$(this).removeClass('sorting_desc');
	 	 			$(this).off('click');
	 	 		});
	 	 		$('#institutionGradeTable a').on('click', function () {
	 	 			openDeletionModal($(this).attr('href'));
	 	 			return false;
	 	 		});
			});

		createDataTables('degreeGradeTable', true /*filterable*/, false /*show tools*/, true /*paging*/, "${pageContext.request.contextPath}", "${datatablesI18NUrl}");
		delay(
	 			function () {
					return $('#degreeGradeTable_info').length && $('#degreeGradeTable').length;
				},
				function () {
		 	 		$('#degreeGradeTable .sorting, #degreeGradeTable .sorting_desc, #degreeGradeTable .sorting_asc').each( function (index) {
		 	 			if (index > 1) {
		 	 				$(this).removeClass('sorting');
			 	 			$(this).removeClass('sorting_asc');
			 	 			$(this).removeClass('sorting_desc');
			 	 			$(this).off('click');		 	 				
		 	 			}
		 	 		});
		 	 		$('#degreeGradeTable a').on('click', function () {
		 	 			openDeletionModal($(this).attr('href'));
		 	 			return false;
		 	 		});
				});
		$('#select-all-degrees').on('click', function (evt) {
			evt.preventDefault();
			$('#degreeGradeTable tbody tr').addClass('selected');
			$('#delete-all-selected-degrees').attr('disabled', false);
			return;
		});
		$('#delete-all-selected-degrees').attr('disabled', true);
		$('#delete-all-selected-degrees').on('click', function () {
			var oids = '';
			var url = "${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/";
			var token = "/${sectoken}";
			$('#degreeGradeTable tr.selected a').each( function (index) {
				var slugs = $(this).attr('href').split('/');
				slugs.pop() // discard token...
				oids += slugs.pop() + '+';
			});
			oids = oids.slice(0, -1);
			openDeletionModal(url+oids+token)
			return;
		});
		$('#degreeGradeTable tbody').off('click');
		$('#degreeGradeTable tbody').on('click', 'tr', function () {
			$(this).toggleClass('selected');
			if ($('#degreeGradeTable tr.selected').length) {
				$('#delete-all-selected-degrees').attr('disabled', false);
			} else {
				$('#delete-all-selected-degrees').attr('disabled', true);
			}
		});
		
		createDataTables('courseGradeTable', true /*filterable*/, false /*show tools*/, true /*paging*/, "${pageContext.request.contextPath}", "${datatablesI18NUrl}");
		delay(
	 			function () {
					return $('#courseGradeTable_info').length && $('#courseGradeTable').length;
				},
				function () {
		 	 		$('#courseGradeTable .sorting, #courseGradeTable .sorting_desc, #courseGradeTable .sorting_asc').each( function (index) {
		 	 			if (index > 1) {
		 	 				$(this).removeClass('sorting');
			 	 			$(this).removeClass('sorting_asc');
			 	 			$(this).removeClass('sorting_desc');
			 	 			$(this).off('click');		 	 				
		 	 			}
		 	 		});
		 	 		$('#courseGradeTable a').on('click', function () {
		 	 			openDeletionModal($(this).attr('href'));
		 	 			return false;
		 	 		});
				});
		$('#select-all-courses').on('click', function (evt) {
			evt.preventDefault();
			$('#courseGradeTable tbody tr').addClass('selected');
			$('#delete-all-selected-courses').attr('disabled', false);
			return;
		});
		$('#delete-all-selected-courses').attr('disabled', true);
		$('#delete-all-selected-courses').on('click', function () {
			var oids = '';
			var url = "${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.DELETE_TABLES_URL%>${selectedYear.externalId}/";
			var token = "/${sectoken}";
			$('#courseGradeTable tr.selected a').each( function (index) {
				var slugs = $(this).attr('href').split('/');
				slugs.pop() // discard token...
				oids += slugs.pop() + '+';
			});
			oids = oids.slice(0, -1);
			openDeletionModal(url+oids+token)
			return;
		});
		$('#courseGradeTable tbody').off('click');
		$('#courseGradeTable tbody').on('click', 'tr', function () {
			$(this).toggleClass('selected');
			if ($('#courseGradeTable tr.selected').length) {
				$('#delete-all-selected-courses').attr('disabled', false);
			} else {
				$('#delete-all-selected-courses').attr('disabled', true);
			}
		});
 
	}); 
</script>

