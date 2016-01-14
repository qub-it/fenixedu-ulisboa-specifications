<%@page
	import="org.fenixedu.ulisboa.specifications.ui.curricularrules.manageanycurricularcourseexceptionsconfiguration.AnyCurricularCourseExceptionsConfigurationController"%>
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
			code="label.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.readAnyCurricularCourseExceptionsConfiguration" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-cloud-upload" aria-hidden="true"></span>&nbsp;
	<a class=""
		href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/curricularrules/manageanycurricularcourseexceptionsconfiguration/anycurricularcourseexceptionsconfiguration/read/${anyCurricularCourseExceptionsConfiguration.externalId}/importcompetencecourses"><spring:message
			code="label.event.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.importCompetenceCourses" /></a>
	&nbsp;|&nbsp; <span class="glyphicon glyphicon-cloud-download" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/curricularrules/manageanycurricularcourseexceptionsconfiguration/anycurricularcourseexceptionsconfiguration/read/${anyCurricularCourseExceptionsConfiguration.externalId}/exportcompetencecourses"><spring:message
			code="label.event.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.exportCompetenceCourses" /></a>
	&nbsp;|&nbsp; <span class="glyphicon glyphicon-off" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="javascript:showClearCompetenceCoursesConfirmModal();"><spring:message
			code="label.event.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.clearCompetenceCourses" /></a>
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

<script>
	angular
			.module('angularAppAnyCurricularCourseExceptionsConfiguration',
					[ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
			.controller('AnyCurricularCourseExceptionsConfigurationController',	[
							'$scope',
							function($scope) {
								
								$scope.booleanvalues = [
										{
											name : '<spring:message code="label.no"/>',
											value : false
										},
										{
											name : '<spring:message code="label.yes"/>',
											value : true
										} ];

								$scope.object = ${anyCurricularCourseExceptionsConfigurationBeanJson};
								
								
								$scope.postBack = createAngularPostbackFunction($scope);
								
								$scope.submit = function(){
									if ($scope.object.competenceCourse != '') {
										$('#addCompetenceCourseForm').submit();	
									}
								}
								
								



							 }]);
</script>



<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:message code="label.details" />
		</h3>
	</div>
	<div class="panel-body">
		<form method="post" class="form-horizontal" id="addCompetenceCourseForm"
			ng-app="angularAppAnyCurricularCourseExceptionsConfiguration"
			ng-controller="AnyCurricularCourseExceptionsConfigurationController"
			action='${pageContext.request.contextPath}/<%=AnyCurricularCourseExceptionsConfigurationController.READ_URL%>${anyCurricularCourseExceptionsConfiguration.externalId}/addcompetencecourse'>

			<input name="bean" type="hidden" value="{{ object }}" />

			<div class="form-group row">
				<div class="col-sm-6">
										
					<select id="anyCurricularCourseExceptionsConfiguration_competenceCourses" name="competencecourses" class="form-control" ng-model="object.competenceCourse" >
						<option></option>
						<c:forEach var="each" items="${anyCurricularCourseExceptionsConfigurationBean.competenceCoursesDataSource}">
							<option value="${each.id}">${each.text}</option>
						</c:forEach>
					</select>
		
				</div>
				<div class="col-sm-2">
					<button class="btn glyphicon glyphicon-plus-sign" type="button" ng-click="submit()">
						<spring:message code="label.add" />
					</button>
				</div>
			</div>
			<div class="row">&nbsp;</div>
			<div class="row">
				<div class="col-sm-6">
					<c:choose>
						<c:when
							test="${not empty anyCurricularCourseExceptionsConfiguration.competenceCoursesSet}">
														
							<table id="competenceCourseTable" class="table table-bordered table-hover">
								<thead>
									<tr>
										<th><spring:message code="label.CompetenceCourse.code" /></th>
										<th><spring:message code="label.CompetenceCourse.name" /></th>
										<th><spring:message code="label.CompetenceCourse.ectsCredits" /></th>
										<th> </th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="competenceCourse" items="${anyCurricularCourseExceptionsConfiguration.competenceCoursesSet}">
									<tr>
										<td><c:out value="${competenceCourse.code}"></c:out></td>
										<td><c:out value="${competenceCourse.name}"></c:out></td>
										<td><c:out value="${competenceCourse.ectsCredits}"></c:out></td>
										<td><a  class="btn btn-xs btn-danger" href="#" onClick="javascript:showRemoveCompetenceCourseConfirmModal('${competenceCourse.externalId}')">
		                            			<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>&nbsp;<spring:message code='label.remove'/>
		                            		</a></td>
									</tr>
									</c:forEach>
								</tbody>
							</table>

							<script type="text/javascript">
								createDataTables('competenceCourseTable',true /*filterable*/, false /*show tools*/, true /*paging*/, "${pageContext.request.contextPath}","${datatablesI18NUrl}");
							</script>

						
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


		</form>
	</div>
</div>


<div class="modal fade" id="modalDialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="modalForm" method="POST">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body">
					<p id="modalMessage"></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.no" />
					</button>
					<button class="btn btn-danger" type="submit">
						<spring:message code="label.yes" />
					</button>
				</div>
			</form>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->



<script>

function showRemoveCompetenceCourseConfirmModal(externalId) {
	   url = '${pageContext.request.contextPath}/<%=AnyCurricularCourseExceptionsConfigurationController.READ_URL%>${anyCurricularCourseExceptionsConfiguration.externalId}/removecompetencecourse/' + externalId;
	   showConfirmationModal(url,'<spring:message code="label.event.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.removeCompetenceCourse.confirm" />');
}
	 
function showClearCompetenceCoursesConfirmModal() {
	   url = '${pageContext.request.contextPath}/<%=AnyCurricularCourseExceptionsConfigurationController.READ_URL%>${anyCurricularCourseExceptionsConfiguration.externalId}/clearcompetencecourses/';
	   showConfirmationModal(url,'<spring:message code="label.event.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.clearCompetenceCourses.confirm" />');
}
	 
function showConfirmationModal(url,message) {
	   $("#modalForm").attr("action", url);
	   $("#modalMessage").html(message);
	   $('#modalDialog').modal('toggle')
}

                             
$(document).ready(function() {
	


	
});

</script>
