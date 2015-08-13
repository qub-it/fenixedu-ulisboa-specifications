<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>


<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.identificationCardServices.idServices" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<%-- <div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/qubdocsreports/documentpurposetypes/documentpurposetypeinstance/create"   ><spring:message code="label.event.create" /></a>
</div> --%>

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
						<th scope="row" class="col-xs-1"><strong><spring:message code="label.identificationCardServices.cgdmod43" /></strong></th> 
						<td>
							<a class="" href="${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/identificationcardservices/downloadCGDMod43"><spring:message code="label.action.downloadCGDMod43" /></a>
						</td> 
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

