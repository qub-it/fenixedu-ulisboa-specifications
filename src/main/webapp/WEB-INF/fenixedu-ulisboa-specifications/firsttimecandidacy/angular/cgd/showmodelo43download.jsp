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

<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}${controllerURL}/back"><spring:message code="label.back"/></a>	
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

	
	<p>&nbsp;</p>
	
	<c:if test="${dueToError}">
		<p><spring:message code="label.CgdDataAuthorization.error" /></p>
	</c:if>
	<c:if test="${not dueToError}">
	<p><spring:message code="label.CgdDataAuthorization.byChoice" /></p>
	</c:if>
	<p>
		<a class="btn btn-primary" href="<%= request.getContextPath() %>${printURL}" target="_blank">
			<span class="glyphicon glyphicon-circle-arrow-down" aria-hidden="true"></span>&nbsp;
			<spring:message code="label.CgdDataAuthorization.print" />
		</a>
	</p>
</div>

<form method="post" class="form-horizontal">
	<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.continue" />" />
</form>

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
	.cgd-accept-icon,.cgd-decline-icon {
		float: right;
	}
	.cgd-accept-action {
		margin: 0 46%;
	    color: #d7ffe4;
	    font-size: 17px;
	    border-radius: 29px;
	    border-width: 2px;
	    border-style: solid;
	    padding: 6px 7px;
		-webkit-box-shadow: 0px 0px 3px 1px rgba(189,254,211,1);
		-moz-box-shadow: 0px 0px 3px 1px rgba(189,254,211,1);
		box-shadow: 0px 0px 3px 1px rgba(189,254,211,1);
		display: none;
	}
	.cgd-accept-loader {
		margin: 0 46%;
	    color: #d7ffe4;
	    font-size: 24px;
		display: none;
		-webkit-animation: rotate 1s linear infinite;
		-moz-animation: rotate 1s linear infinite;
		-o-animation: rotate 1s linear infinite;
		animation: rotate 1s linear infinite;
	}
	.cgd-decline-action {
		margin: 0 46%;
	    color: #f2f2f2;
	    font-size: 17px;
	    border-radius: 29px;
	    border-width: 2px;
	    border-style: solid;
	    padding: 6px 7px;
		-webkit-box-shadow: 0px 0px 3px 1px rgba(242,242,242,1);
		-moz-box-shadow: 0px 0px 3px 1px rgba(242,242,242,1);
		box-shadow: 0px 0px 3px 1px rgba(242,242,242,1);
		display: none;
	}
	.cgd-decline-loader {
		margin: 0 46%;
	    color: #f2f2f2;
	    font-size: 24px;
		display: none;
		-webkit-animation: rotate 1s linear infinite;
		-moz-animation: rotate 1s linear infinite;
		-o-animation: rotate 1s linear infinite;
		animation: rotate 1s linear infinite;
	}
	.furtherInfo {
		text-align: center;
	}
	
	@-webkit-keyframes rotate {
		from {
			-webkit-transform: rotate(0deg);
		}
		to { 
			-webkit-transform: rotate(360deg);
		}
	}
	@-moz-keyframes rotate {
		from {
			-webkit-transform: rotate(0deg);
		}
		to { 
			-webkit-transform: rotate(360deg);
		}
	}
	@-o-keyframes rotate {
		from {
			-webkit-transform: rotate(0deg);
		}
		to { 
			-webkit-transform: rotate(360deg);
		}
	}
	@keyframes rotate {
		from {
			-webkit-transform: rotate(0deg);
		}
		to { 
			-webkit-transform: rotate(360deg);
		}
	}
</style>

