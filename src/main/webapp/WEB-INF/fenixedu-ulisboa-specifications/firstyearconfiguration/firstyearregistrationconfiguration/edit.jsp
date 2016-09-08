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
	<h1><spring:message code="label.firstYearConfiguration.edit" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/"  ><spring:message code="label.event.firstYearConfiguration.cancel" /></a>
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
						<p id="errorMessages" > <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
  							${message}
  						</p>
					</c:forEach>
					
				</div>	
			</c:if>


<h2><spring:message code="label.FirstYearRegistrationConfiguration.mod43TemplateFile"/></h2>
<form id="templateForm" class="form-horizontal" enctype="multipart/form-data" action="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/edit/uploadTemplate" method="POST">
	<div class="panel panel-default">
  		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.FirstYearRegistrationConfiguration.mod43TemplateFile"/></div> 
				<div class="col-sm-4">
					<input type="file" id="templateContent" name="mod43Template" accept="application/pdf" required />
					<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />"/>
				</div>
			</div>
		</div>
	</div>
</form>


<h2><spring:message code="label.FirstYearRegistrationConfiguration.introductionText"/></h2>
<form id="introductionTextForm" class="form-horizontal" action="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/edit/introductionText" method="POST">
	<div class="panel panel-default">
  		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.FirstYearRegistrationConfiguration.introductionText"/></div> 
				<div class="col-sm-4">
					<textarea type="text" id="introductionText" name="introductionText" bennu-localized-string ><c:out value='${firstYearRegistrationGlobalConfiguration.introductionText.json()}' /></textarea>
					<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />"/>
				</div>
			</div>
		</div>
	</div>
</form>

<style>
#templateForm input {
	display: inline-block;
}
</style>

<script>
</script>

