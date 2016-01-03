<%@page import="org.fenixedu.ulisboa.specifications.ui.degrees.precedence.DegreesPrecedenceController"%>

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
		<spring:message code="label.manageDegreePrecedences.addPrecedentDegree" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
	&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= DegreesPrecedenceController.VIEW_URL %>/${degree.externalId}">
		<spring:message code="label.event.back" />
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

<script>

angular.module('angularApp', ['ngSanitize', 'ui.select']).controller('angularController', ['$scope', function($scope) {

 	$scope.object=${beanJson};
 	$scope.postBack = createAngularPostbackFunction($scope);
 	
 	$scope.degreeTypeChange = function(degreeTypeId) {
 		$scope.$apply(function() { 
 			$scope.object.degreeType = degreeTypeId;
 		});
 		
 		$("#form").submit();
 	}
 	
}]);
</script>

<form id="addPrecedentDegreeForm" name="addPrecedentDegreeForm" method="post"
	action='${pageContext.request.contextPath}<%= DegreesPrecedenceController.ADD_DEGREE_URL  %>/${degree.externalId}'>
	<input type="hidden" id="precedentDegreeId" name="precedentDegreeId" value="" />
</form>

<script>

	function addPrecedentDegree(externalId) {
		$("#precedentDegreeId").attr('value', externalId);
		$("#addPrecedentDegreeForm").submit();
	}
	
</script>

<form id="form" name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
	action='${pageContext.request.contextPath}<%= DegreesPrecedenceController.ADD_DEGREE_POSTBACK_URL  %>/${degree.externalId}'>

	<input name="bean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-3 control-label">
					<spring:message code="label.DegreePrecedences.degreeType" />
				</div>

				<div class="col-sm-8">
					<ui-select id="degreeType" class="" name="degreetype" ng-model="$parent.object.degreeType" theme="bootstrap" ng-disabled="disabled" ng-change="$parent.degreeTypeChange($parent.object.degreeType);">
					<ui-select-match>{{$select.selected.text}}</ui-select-match> <ui-select-choices
						repeat="degreeType.id as degreeType in object.degreeTypeDataSource | filter: $select.search"> <span
						ng-bind-html="degreeType.text | highlight: $select.search"></span> </ui-select-choices> </ui-select>
				</div>
			</div>
		</div>
	</div>

	<c:choose>
		<c:when test="${not empty bean.selectedDegrees}">
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
					<c:forEach var="degree" items="${bean.selectedDegrees}">
						<tr>
							<td>
								<c:out value="${degree.code}" />
							</td>
							<td>
								<c:out value="${degree.degreeType.name.content}" />
							</td>
							<td>
								<c:out value="${degree.nameI18N.content}" />
							</td>
							<td>
								<a class="btn btn-default btn-xs" onclick="addPrecedentDegree(${degree.externalId}); return false;" href="#">
									<spring:message code='label.add' />
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


</form>

<script>
$(document)
.ready(
		function() {

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
								"dom" : '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
								//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
								//"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
								//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
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
