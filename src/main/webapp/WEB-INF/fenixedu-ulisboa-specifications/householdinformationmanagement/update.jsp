<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.HouseholdInformationManagementController"%>
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
	<h1><spring:message code="label.HouseholdInformationManagement.update" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
	<a class="" href="${pageContext.request.contextPath}<%= HouseholdInformationManagementController.READ_URL %>/${student.externalId}/${executionYear.externalId}">
		<spring:message code="label.back"/>
	</a>	
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
		<h3 class="panel-title">
			<spring:message code="label.details" />
		</h3>
	</div>
	<div class="panel-body">
		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.studentNumber" />
						</th>
						<td>
							<c:out value='${student.number}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.studentName" />
						</th>
						<td>
							<c:out value='${student.person.name}' />
						</td>
					</tr>
					<tr>
						<th scope="row" class="col-xs-3">
							<spring:message code="label.HouseholdInformationForm.executionYear" />
						</th>
						<td>
							<c:out value='${executionYear.qualifiedName}' />
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>

<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
		
			<jsp:include page="householdinformationform_include.jsp" />
			

			<div class="form-group row">
				<label class="col-sm-2 control.label required-field">
					<spring:message code="label.PersonalInformationForm.countryHighSchool" />
				</label>
				
				<div class="col-sm-10">
					<select id="householdInformationForm_countryHighSchool" class="form-control" name="countryHighSchool">
							<option value=""></option>
						<c:forEach var="country" items="${countryHighSchoolValues}">
							<option value="${country.externalId}"><c:out value="${country.localizedName.content}" /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {					
						$("#householdInformationForm_countryHighSchool").select2().select2('val', '<c:out value='${householdInformationForm.countryHighSchool.externalId }'/>');
					});
					
					</script>
				</div>
			</div>

		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button"
				value="<spring:message code="label.submit" />" />
		</div>
	</div>
</form>

<style>
	.required-field:after {
		content: '*';
		color: #e06565;
		font-weight: 900;
		margin-left: 2px;
		font-size: 14px;
		display: inline;
	}
</style>
