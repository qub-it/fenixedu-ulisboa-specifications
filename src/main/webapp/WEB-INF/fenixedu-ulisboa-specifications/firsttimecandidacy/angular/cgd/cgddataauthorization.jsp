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
				<a class="btn btn-primary panel-heading btn-panel-heading cgd-accept-panel-heading" href="${pageContext.request.contextPath}${controllerURL}/authorize" >
					<span class="cgd-accept-title"><spring:message code="label.CgdDataAuthorization.accept" htmlEscape="false"/></span>
					<span class="cgd-accept-icon glyphicon glyphicon-ok-circle" aria-hidden="true"></span>
					<span class="cgd-accept-action glyphicon glyphicon-thumbs-up" aria-hidden="true"></span>
					<span class="cgd-accept-loader glyphicon glyphicon-refresh" aria-hidden="true"></span>
				</a>
			</div>
			<div class="panel-body cgd-accept-panel-body">
				<spring:message code="label.CgdDataAuthorization.acceptanceConditions" htmlEscape="false"/>
			</div>
		</div>
	</div>
	
	<div class="col-md-6">
		<div class="panel panel-default">
			<div>
				<a class="btn btn-default panel-heading btn-panel-heading cgd-decline-panel-heading" href="${pageContext.request.contextPath}${controllerURL}/unauthorize" >
					<span class="cgd-decline-title"><spring:message code="label.CgdDataAuthorization.refuse" htmlEscape="false"/></span>
					<span class="cgd-decline-icon glyphicon glyphicon-remove-circle" aria-hidden="true"></span>
					<span class="cgd-decline-action glyphicon glyphicon-thumbs-down" aria-hidden="true"></span>
					<span class="cgd-decline-loader glyphicon glyphicon-refresh" aria-hidden="true"></span>
				</a>
			</div>
			<div class="panel-body cgd-decline-panel-body">
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

<script>
$(document).ready( function () {
	var acceptHeaderColor = $('.cgd-accept-panel-heading').css('background-color');
	var acceptBodyColor = $('.cgd-accept-panel-body').css('background-color');
	$('.cgd-accept-panel-heading').hover( function () {
		$('.cgd-accept-action').finish();
		$('.cgd-decline-action').finish();
		$('.cgd-accept-title,.cgd-accept-icon').hide();
		$('.cgd-accept-action').fadeIn("200");			
		$('.cgd-accept-panel-heading').css('background-color','#179b43');			
		$('.cgd-accept-panel-body').css('background-color','#e6ffee');
	}, function () {
		$('.cgd-accept-action').hide();
		$('.cgd-accept-title,.cgd-accept-icon').show();
		$('.cgd-accept-panel-heading').css('background-color', acceptHeaderColor);
		$('.cgd-accept-panel-body').css('background-color', acceptBodyColor);
	});
	$('.cgd-accept-panel-heading').click( function () {
		$('.cgd-accept-action').finish();
		$('.cgd-decline-action').finish();
		$('.cgd-accept-panel-heading').unbind();
		$('.cgd-decline-panel-heading').unbind();
		$('.cgd-accept-title,.cgd-accept-icon,.cgd-accept-action').hide();
		$('.cgd-accept-loader').show();
		$('.cgd-accept-panel-heading').css('background-color','#179b43');			
		$('.cgd-accept-panel-body').css('background-color','#e6ffee');
		$('.cgd-accept-panel-heading').css('cursor','progress');
		$('.cgd-decline-panel-heading').css('cursor','progress');
		$('html').css('cursor','progress');
		$('.cgd-accept-panel-heading')[0].href="javascript: void(0);";
		$('.cgd-decline-panel-heading')[0].href="javascript: void(0);";
	});
	
	var declineHeaderColor = $('.cgd-decline-panel-heading').css('background-color');
	var declineBodyColor = $('.cgd-decline-panel-body').css('background-color');
	$('.cgd-decline-panel-heading').hover( function () {
		$('.cgd-accept-action').finish();
		$('.cgd-decline-action').finish();
		$('.cgd-decline-title,.cgd-decline-icon').hide();
		$('.cgd-decline-action').fadeIn("200");			
		$('.cgd-decline-panel-heading').css('background-color','#898989');			
		$('.cgd-decline-panel-body').css('background-color','#f2f2f2');
	}, function () {
		$('.cgd-decline-action').hide();
		$('.cgd-decline-title,.cgd-decline-icon').show();
		$('.cgd-decline-panel-heading').css('background-color', declineHeaderColor);
		$('.cgd-decline-panel-body').css('background-color', declineBodyColor);
	});
	$('.cgd-decline-panel-heading').click( function () {
		$('.cgd-accept-action').finish();
		$('.cgd-decline-action').finish();
		$('.cgd-accept-panel-heading').unbind();
		$('.cgd-decline-panel-heading').unbind();
		$('.cgd-decline-title,.cgd-decline-icon,.cgd-decline-action').hide();
		$('.cgd-decline-loader').show()
		$('.cgd-decline-panel-heading').css('background-color','#898989');			
		$('.cgd-decline-panel-body').css('background-color','#f2f2f2');
		$('.cgd-accept-panel-heading').css('cursor','progress');
		$('.cgd-decline-panel-heading').css('cursor','progress');
		$('html').css('cursor','progress');
		$('.cgd-accept-panel-heading')[0].href="javascript: void(0);";
		$('.cgd-decline-panel-heading')[0].href="javascript: void(0);";		
	});
});
</script>
