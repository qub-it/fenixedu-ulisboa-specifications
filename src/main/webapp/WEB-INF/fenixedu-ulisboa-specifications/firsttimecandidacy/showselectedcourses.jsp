<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js"/>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css"/>

<link rel="stylesheet" href="${datatablesCssUrl}"/>
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css"/>

${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>



<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.firstTimeCandidacy.showSelectedCourses" />
		<small></small>
	</h1>
</div>
	<c:if test="${not empty infoMessages}">
				<div class="alert alert-info" role="alert">
					
					<c:forEach items="${infoMessages}" var="message"> 
						<p> <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span>
  							${message}
  						</p>
					</c:forEach>
					
				</div>	
			</c:if>
			<c:if test="${not empty warningMessages}">
				<div class="alert alert-warning" role="alert">
					
					<c:forEach items="${warningMessages}" var="message"> 
						<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
  							${message}
  						</p>
					</c:forEach>
					
				</div>	
			</c:if>
			<c:if test="${not empty errorMessages}">
				<div class="alert alert-danger" role="alert">
					
					<c:forEach items="${errorMessages}" var="message"> 
						<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
  							${message}
  						</p>
					</c:forEach>
					
				</div>	
			</c:if>


<%-- NAVIGATION --%>
<div class="h3">${currentYear}</div>

<c:if test="${not empty firstSemesterEnrolments}">
	<div class="h4">
		<spring:message code="label.enrolments.first.semester" />
	</div>
	<div class="panel panel-default">
		<div class="panel-body">
			<c:forEach items="${firstSemesterEnrolments}" var="enrolment">
			<div class="form-group row">
				<div class="col-sm-2">
					<div><strong>${enrolment.name.content}</strong></div>
				</div>
				<div class="col-sm-10">
					<div><spring:message code="label.credits" arguments="${enrolment.ectsCredits}"/></div>
				</div>
			</div>
			</c:forEach>
		</div>
	</div>
	
	<div><spring:message code="label.total.credits" arguments="${firstSemesterCredits}"/></div>
<br/>
</c:if>

<c:if test="${not empty secondSemesterEnrolments}">
	<div class="h4">
		<spring:message code="label.enrolments.second.semester" />
	</div>
	<div class="panel panel-default">
		<div class="panel-body">
			<c:forEach items="${secondSemesterEnrolments}" var="enrolment">
			<div class="form-group row">
				<div class="col-sm-2">
					<div><strong>${enrolment.name.content}</strong></div>
				</div>
				<div class="col-sm-10">
					<div><spring:message code="label.credits" arguments="${enrolment.ectsCredits}"/></div>
				</div>
			</div>
			</c:forEach>
		</div>
	</div>
	
	<div><spring:message code="label.total.credits" arguments="${secondSemesterCredits}"/></div>
<br/>
</c:if>

<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-cog" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/showselectedcourses/continue"><spring:message code="label.event.firstTimeCandidacy.continue"  /></a>	
</div>

<script>
$(document).ready(function() {

	
	
	});
</script>
