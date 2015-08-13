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
	<h1><spring:message code="label.firstTimeCandidacy.fillContacts" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
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

<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ContactsForm.phoneNumber" />
				</div>

				<div class="col-sm-10">
					<input id="contactsForm_phoneNumber" class="form-control"
						type="text" name="phoneNumber" pattern="(\d{4,15})"
						value='<c:out value='${not empty param.phonenumber ? param.phonenumber : contactsForm.phoneNumber }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ContactsForm.mobileNumber" />
				</div>

				<div class="col-sm-10">
					<input id="contactsForm_mobileNumber" class="form-control"
						type="text" name="mobileNumber" pattern="(\d{4,15})"
						value='<c:out value='${not empty param.mobilenumber ? param.mobilenumber : contactsForm.mobileNumber }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ContactsForm.email" />
				</div>

				<div class="col-sm-10">
					<input id="contactsForm_email" class="form-control" type="email"
						name="email" required
						value='<c:out value='${not empty param.email ? param.email : contactsForm.email }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ContactsForm.webAddress" />
				</div>

				<div class="col-sm-10">
					<input id="contactsForm_webAddress" class="form-control"
						type="text" name="webAddress" pattern="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?"
						value='<c:out value='${not empty param.webaddress ? param.webaddress : contactsForm.webAddress }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ContactsForm.isEmailAvailable" />
				</div>

				<div class="col-sm-2">
					<select id="contactsForm_isEmailAvailable" name="isEmailAvailable"
						class="form-control">
						<option value="false"><spring:message code="label.no" /></option>
						<option value="true"><spring:message code="label.yes" /></option>
					</select>
					<script>
						$("#contactsForm_isEmailAvailable").val('<c:out value='${not empty param.isemailavailable ? param.isemailavailable : contactsForm.isEmailAvailable }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.ContactsForm.isHomepageAvailable" />
				</div>

				<div class="col-sm-2">
					<select id="contactsForm_isHomepageAvailable"
						name="isHomepageAvailable" class="form-control">
						<option value="false"><spring:message code="label.no" /></option>
						<option value="true"><spring:message code="label.yes" /></option>
					</select>
					<script>
						$("#contactsForm_isHomepageAvailable").val('<c:out value='${not empty param.ishomepageavailable ? param.ishomepageavailable : contactsForm.isHomepageAvailable }'/>');
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

<script>
$(document).ready(function() {


	});
</script>
