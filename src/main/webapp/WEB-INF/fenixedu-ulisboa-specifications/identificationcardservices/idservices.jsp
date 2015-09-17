<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>


<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.identificationCardServices.idServices" />
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


<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="label.identificationCardServices.cgd"/></h3>
	</div>
	<div class="panel-body">
		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>
					<tr>
						<th scope="row" class="action-label col-xs-1"><strong><spring:message code="label.identificationCardServices.cgdmod43" /></strong></th> 
						<td  class="col-xs-11">
							<c:if test="${empty webserviceSuccess}">
								<a class="btn btn-default" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/identificationcardservices/sendCGDMod43" ><span class="action-icon  glyphicon glyphicon-cloud-upload" aria-hidden="true"></span><spring:message code="label.event.sendMod43" /></a>
							</c:if>
							<c:if test="${not empty webserviceSuccess}">
								<c:if test="${webserviceSuccess}">
									<a class="btn btn-default" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/identificationcardservices/sendCGDMod43" ><span class="action-icon  glyphicon glyphicon-cloud-upload" aria-hidden="true"></span><spring:message code="label.event.sendMod43" /></a>
								</c:if>
								<c:if test="${not webserviceSuccess}">
									<a class="btn btn-default" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/identificationcardservices/downloadCGDMod43"><span class="action-icon  glyphicon glyphicon-download-alt" aria-hidden="true"></span><spring:message code="label.action.downloadCGDMod43" /></a>
								</c:if>
							</c:if>
						</td> 
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<style>
	.table > tbody > tr > th.action-label {
		vertical-align: middle;
	}
	.action-icon {
		margin-right: 6px;
	}
</style>

