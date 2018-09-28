<%@page import="org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page import="org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheet"%>
<%@page import="org.fenixedu.bennu.core.security.Authenticate"%>
<%@page
    import="org.fenixedu.ulisboa.specifications.ui.evaluation.managemarksheet.student.StudentCompetenceCourseMarkSheetController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<c:set var="student" value="<%= Authenticate.getUser().getPerson().getStudent() %>" />

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

<script>
    angular
        .module('angularAppCompetenceCourseMarkSheet',
            [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
        .controller(
            'CompetenceCourseMarkSheetController',
            [
                '$scope',
                function($scope) {
                );
</script>

<div ng-app="angularAppCompetenceCourseMarkSheet" ng-controller="CompetenceCourseMarkSheetController">

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.evaluation.manageMarkSheet.readGrades" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
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

<c:if test="${ competenceCourseMarkSheetBean.hasStudent(student) && competenceCourseMarkSheet.isConfirmed() }">
	<div class="panel panel-primary">
	
	    <div class="panel-heading">
	        <h3 class="panel-title">
	            <spring:message code="label.details" />
	        </h3>
	    </div>
	
	    <div class="panel-body">
	        <table class="table">
	            <tbody>
	                <tr>
	                    <th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.executionSemester" /></th>
	                    <td><c:out value="${competenceCourseMarkSheet.executionSemester.qualifiedName}"/></td>
	                </tr>
	                <tr>
	                    <th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.competenceCourse" /></th>
	                    <td><c:out value="${competenceCourseMarkSheet.competenceCourse.code}"/> - <c:out value="${competenceCourseMarkSheet.competenceCourse.nameI18N.content}"/></td>
	                </tr>
	                <tr>
	                    <th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.evaluationSeason" /></th>
	                    <td><c:out value="<%=EvaluationSeasonServices.getDescriptionI18N(((CompetenceCourseMarkSheet)request.getAttribute("competenceCourseMarkSheet")).getEvaluationSeason()).getContent()%>"/></td>
	                </tr>
	                <tr>
	                    <th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.evaluationDate" /></th>
	                    <td><c:out value="${competenceCourseMarkSheet.evaluationDatePresentation}"/></td>
	                </tr>
	            </tbody>
	        </table>
	    </div>
	</div>
	
	
	<div class="panel panel-default">
	    <div class="panel-body">
	    
	    	<c:choose>
				<c:when test="${not empty competenceCourseMarkSheetBean.gradeBeans}">
			        <table id="gradeTable" class="table responsive table-bordered table-hover" width="100%">
			            <thead>
			                <tr>
			                    <th><spring:message code="label.MarkBean.studentNumber" /></th>
			                    <th><spring:message code="label.MarkBean.studentName" /></th>
			                    <th><spring:message code="label.MarkBean.degreeCode" /></th>
			                    <th><spring:message code="label.MarkBean.shifts" /></th>
			                    <th><spring:message code="label.MarkBean.gradeValue" /></th>
			                </tr>
			            </thead>
			            <tbody>
							<c:forEach var="evaluation" items="${competenceCourseMarkSheetBean.gradeBeans}">
								<tr>
				                    <td><c:out value="${evaluation.studentNumber}" /></td>
				                    <td><c:out value="${evaluation.studentName}" /></td>
				                    <td><c:out value="${evaluation.degreeCode}" /></td>
				                    <td><c:out value="${evaluation.shifts}" /></td>
				                    <td><c:out value="${evaluation.gradeValue}" /></td>
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
	        
	    </div>
	</div>
</c:if>

</div>
