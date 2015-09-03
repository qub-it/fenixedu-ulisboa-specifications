<%@page import="org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration"%>
<%@page import="org.fenixedu.academic.domain.organizationalStructure.Unit"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

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

<link href="//www.fuelcdn.com/fuelux/3.11.0/css/fuelux.min.css" rel="stylesheet">
<script src="//www.fuelcdn.com/fuelux/3.11.0/js/fuelux.min.js"></script>



<%-- TITLE --%>
<div class="page-header fuelux">
	<h1><spring:message code="label.firstTimeCandidacy.instructions" />
	</h1>
<!-- 	<div class="wizard" data-initialize="wizard" id="myWizard"> -->
<!-- 		<div class="steps-container"> -->
<!-- 			<ul class="steps"> -->
<%-- 				<li data-step="1" class="active"><span class="badge">1</span><spring:message code="label.firstTimeCandidacy.instructions" /><span class="chevron"></span></li> --%>
<%-- 				<li data-step="2"><span class="badge">2</span><spring:message code="label.firstTimeCandidacy.fillPersonalInformation" /><span class="chevron"></span></li> --%>
<%-- 			 	<li data-step="3"><span class="badge">3</span><spring:message code="label.firstTimeCandidacy.fillFiliation" /><span class="chevron"></span></li> --%>
<%-- 			 	<li data-step="4"><span class="badge">4</span><spring:message code="label.firstTimeCandidacy.fillHouseHoldInformation" /><span class="chevron"></span></li> --%>
<%-- 			 	<li data-step="5"><span class="badge">5</span><spring:message code="label.firstTimeCandidacy.fillResidenceInformation" /><span class="chevron"></span></li> --%>
<%-- 			 	<li data-step="6"><span class="badge">6</span><spring:message code="label.firstTimeCandidacy.fillContacts" /><span class="chevron"></span></li> --%>
<%-- 			 	<li data-step="7"><span class="badge">7</span><spring:message code="label.firstTimeCandidacy.fillOriginInformation" /><span class="chevron"></span></li> --%>
<%-- 			 	<li data-step="8"><span class="badge">8</span><spring:message code="label.firstTimeCandidacy.fillDisabilities" /><span class="chevron"></span></li> --%>
<%-- 			 	<li data-step="9"><span class="badge">9</span><spring:message code="label.firstTimeCandidacy.fillMotivationsExpectations" /><span class="chevron"></span></li> --%>
<%-- 			 	<li data-step="10"><span class="badge">10</span><spring:message code="label.firstTimeCandidacy.createSchoolSpecificData" /><span class="chevron"></span></li> --%>
<!-- 			 	<li data-step="11"><span class="badge">11</span><span class="chevron"></span></li> -->
<!-- 			 	<li data-step="12"><span class="badge">12</span><span class="chevron"></span></li> -->
<!-- 			 	<li data-step="13"><span class="badge">13</span><span class="chevron"></span></li> -->
<!-- 			 	<li data-step="14"><span class="badge">14</span><span class="chevron"></span></li> -->
<!-- 			 	<li data-step="15"><span class="badge">15</span><span class="chevron"></span></li> -->
<!-- 			 	<li data-step="16"><span class="badge">16</span><span class="chevron"></span></li> -->
<!-- 			 	<li data-step="17"><span class="badge">17</span><span class="chevron"></span></li> -->
<!-- 			</ul> -->
<!-- 		</div> -->
<!-- 	</div> -->
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

<div class="well start">
	<p><%= FirstYearRegistrationGlobalConfiguration.getInstance().getIntroductionText().getContent() %></p>
	<p>&nbsp;</p>
	<p><spring:message code="label.firstTimeCandidacy.instructions.details" htmlEscape="false"/></p>
</div>

<c:if test="${empty errorMessages}">
	<div>
		<a class="btn btn-primary start" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/home/continue"><span class="glyphicon glyphicon-play-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.firstTimeCandidacy.start"  /></a>
	</div>
</c:if>


<style>
/*
	.page-header {
		border: none;
	}
	.fuelux .wizard {
		min-height: 30px;
	}	
	.steps .badge, .steps .badge-info {
		color: #fff;
		font-weight: 900;
		font-size: 10px;
		vertical-align: text-top;
	}
	.steps .badge {
		background-color: #bebebe;
		font-size: 10px;		
	}
	.fuelux .wizard > .steps-container > ul.steps li {
		font-size: 12px;
		height: 30px;
    	line-height: 30px;
	}
	.fuelux .wizard > .steps-container > ul.steps li .chevron {
		border: 15px solid transparent;
    	border-left: 9px solid #d4d4d4;
    }    
    .fuelux .wizard > .steps-container > ul.steps li.active .chevron:before {
    	border-left: 9px solid #eef7fb;
    }
	*/
	.start {
		font-size: 14px;
	}
	.required-field {
		font-size: 12px;
		font-family: Monospace;
		background-color: #e7e7e7;
		padding: 4px 8px;
		border-radius: 4px;
	}
	.required-field:after {
		content: '*';
		color: #e06565;
		font-weight: 900;
		padding-left: 2px;
	}
</style>

<script>
$(document).ready(function() {

	
	
	});
</script>
