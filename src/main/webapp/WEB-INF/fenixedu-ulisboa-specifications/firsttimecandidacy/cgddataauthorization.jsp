<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ page import="org.fenixedu.academic.domain.organizationalStructure.Unit" %>

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
	<h1><spring:message code="label.firstTimeCandidacy.CgdDataAuthorization" />
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
			
<div class="jumbotron minitron">
	<img src="${pageContext.request.contextPath}/static/img/CartaoCaixaIU.png" />
	<h3><spring:message code="label.CgdDataAuthorization.title" /></h3>	
	<p><spring:message code="label.CgdDataAuthorization.firstParagraph" htmlEscape="false"/></p>
	<p><spring:message code="label.CgdDataAuthorization.secondParagraph" htmlEscape="false"/></p>
	<p><spring:message code="label.CgdDataAuthorization.thirdParagraph" htmlEscape="false"/></p>
	<ul>
		<li><p><spring:message code="label.CgdDataAuthorization.benefits.firstItem" htmlEscape="false"/></p></li>
		<li><p><spring:message code="label.CgdDataAuthorization.benefits.secondItem" htmlEscape="false"/></p></li>
		<li><p><spring:message code="label.CgdDataAuthorization.benefits.thirdItem" htmlEscape="false"/></p></li>
		<li><p><spring:message code="label.CgdDataAuthorization.benefits.fourthItem" htmlEscape="false"/></p></li>
	</ul>
	<p><spring:message code="label.CgdDataAuthorization.fourthParagraph" arguments='<%= Unit.getInstitutionName().getContent()  %>' htmlEscape="false"/></p>
</div>

<div class="row">
	<div class="col-md-6">
		<div class="panel panel-default">
			<div>
				<a class="btn btn-primary panel-heading btn-panel-heading" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization/authorize" ><spring:message code="label.CgdDataAuthorization.accept" htmlEscape="false"/></a>
			</div>
			<div class="panel-body">
				<spring:message code="label.CgdDataAuthorization.acceptanceConditions" htmlEscape="false"/>
			</div>
		</div>
	</div>
	
	<div class="col-md-6">
		<div class="panel panel-default">
			<div>
				<a class="btn btn-default panel-heading btn-panel-heading" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization/unauthorize" ><spring:message code="label.CgdDataAuthorization.refuse" htmlEscape="false"/></a>
			</div>
			<div class="panel-body">
				<spring:message code="label.CgdDataAuthorization.refusalConditions" htmlEscape="false"/>
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-md-12">
		<div class="panel panel-default">
			<div class="panel-body furtherInfo">
				<spring:message code="label.CgdDataAuthorization.furtherInfo" htmlEscape="false"/>
			</div>
		</div>
	</div>
</div>


<style>
	.minitron {
		background-color: #f0f0f0;
		border-radius: 8px;
	}
	.minitron h3 {
		font-size: 15px;
		font-weight: 600;
	}
	.minitron p {
		font-size: 14px;
	}
	.btn-panel-heading {
		width: 100%;
	    padding: 15px 10px;
	    text-align: left;
	    border: 0;
	    font-size: 15px;
	}
	.furtherInfo {
		text-align: center;
	}
</style>

<script>
$(document).ready(function() {

	
	
	});
</script>
