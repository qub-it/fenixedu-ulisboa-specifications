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

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
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
	<h1><spring:message code="label.firstTimeCandidacy.fillPersonalInformation" />
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
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.name"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_name" class="form-control" type="text" name="name"  value='<c:out value='${not empty param.name ? param.name : personalInformationForm.name }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.username"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_username" class="form-control" type="text" name="username"  value='<c:out value='${not empty param.username ? param.username : personalInformationForm.username }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.gender"/></div> 

<div class="col-sm-4">
	<select id="personalInformationForm_gender" class="form-control" name="gender">
		<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME--%>
		<c:forEach items="${genderValues}" var="field">
			<option value='<c:out value='${field}'/>'><c:out value='${field}'/></option>
		</c:forEach>
	</select>
	<script>
		$("#personalInformationForm_gender").val('<c:out value='${not empty param.gender ? param.gender : personalInformationForm.gender }'/>');
	</script>	
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.documentIdNumber"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_documentIdNumber" class="form-control" type="text" name="documentidnumber"  value='<c:out value='${not empty param.documentidnumber ? param.documentidnumber : personalInformationForm.documentIdNumber }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.idDocumentType"/></div> 

<div class="col-sm-4">
	<select id="personalInformationForm_idDocumentType" class="form-control" name="iddocumenttype">
		<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME--%>
		<c:forEach items="${idDocumentTypeValues}" var="field">
			<option value='<c:out value='${field}'/>'><c:out value='${field}'/></option>
		</c:forEach>
	</select>
	<script>
		$("#personalInformationForm_idDocumentType").val('<c:out value='${not empty param.iddocumenttype ? param.iddocumenttype : personalInformationForm.idDocumentType }'/>');
	</script>	
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.identificationDocumentExtraDigit"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_identificationDocumentExtraDigit" class="form-control" type="text" name="identificationdocumentextradigit"  value='<c:out value='${not empty param.identificationdocumentextradigit ? param.identificationdocumentextradigit : personalInformationForm.identificationDocumentExtraDigit }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.identificationDocumentSeriesNumber"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_identificationDocumentSeriesNumber" class="form-control" type="text" name="identificationdocumentseriesnumber"  value='<c:out value='${not empty param.identificationdocumentseriesnumber ? param.identificationdocumentseriesnumber : personalInformationForm.identificationDocumentSeriesNumber }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.documentIdEmissionLocation"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_documentIdEmissionLocation" class="form-control" type="text" name="documentidemissionlocation"  value='<c:out value='${not empty param.documentidemissionlocation ? param.documentidemissionlocation : personalInformationForm.documentIdEmissionLocation }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.documentIdEmissionDate"/></div> 

<div class="col-sm-4">
	<input id="personalInformationForm_documentIdEmissionDate" class="form-control" type="text" name="documentidemissiondate"  bennu-datetime 
	value = '<c:out value='${not empty param.documentidemissiondate ? param.documentidemissiondate : personalInformationForm.documentIdEmissionDate }'/>' />
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.documentIdExpirationDate"/></div> 

<div class="col-sm-4">
	<input id="personalInformationForm_documentIdExpirationDate" class="form-control" type="text" name="documentidexpirationdate"  bennu-datetime 
	value = '<c:out value='${not empty param.documentidexpirationdate ? param.documentidexpirationdate : personalInformationForm.documentIdExpirationDate }'/>' />
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.socialSecurityNumber"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_socialSecurityNumber" class="form-control" type="text" name="socialsecuritynumber"  value='<c:out value='${not empty param.socialsecuritynumber ? param.socialsecuritynumber : personalInformationForm.socialSecurityNumber }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.professionType"/></div> 

<div class="col-sm-4">
	<select id="personalInformationForm_professionType" class="form-control" name="professiontype">
		<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME--%>
		<c:forEach items="${professionTypeValues}" var="field">
			<option value='<c:out value='${field}'/>'><c:out value='${field}'/></option>
		</c:forEach>
	</select>
	<script>
		$("#personalInformationForm_professionType").val('<c:out value='${not empty param.professiontype ? param.professiontype : personalInformationForm.professionType }'/>');
	</script>	
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.professionalCondition"/></div> 

<div class="col-sm-4">
	<select id="personalInformationForm_professionalCondition" class="form-control" name="professionalcondition">
		<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME--%>
		<c:forEach items="${professionalConditionValues}" var="field">
			<option value='<c:out value='${field}'/>'><c:out value='${field}'/></option>
		</c:forEach>
	</select>
	<script>
		$("#personalInformationForm_professionalCondition").val('<c:out value='${not empty param.professionalcondition ? param.professionalcondition : personalInformationForm.professionalCondition }'/>');
	</script>	
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.profession"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_profession" class="form-control" type="text" name="profession"  value='<c:out value='${not empty param.profession ? param.profession : personalInformationForm.profession }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.maritalStatus"/></div> 

<div class="col-sm-4">
	<select id="personalInformationForm_maritalStatus" class="form-control" name="maritalstatus">
		<option value=""></option> <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME--%>
		<c:forEach items="${maritalStatusValues}" var="field">
			<option value='<c:out value='${field}'/>'><c:out value='${field}'/></option>
		</c:forEach>
	</select>
	<script>
		$("#personalInformationForm_maritalStatus").val('<c:out value='${not empty param.maritalstatus ? param.maritalstatus : personalInformationForm.maritalStatus }'/>');
	</script>	
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.grantOwnerType"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_grantOwnerType" class="form-control" type="text" name="grantownertype"  value='<c:out value='${not empty param.grantownertype ? param.grantownertype : personalInformationForm.grantOwnerType }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.grantOwnerProvider"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_grantOwnerProvider" class="form-control" type="text" name="grantownerprovider"  value='<c:out value='${not empty param.grantownerprovider ? param.grantownerprovider : personalInformationForm.grantOwnerProvider }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.PersonalInformationForm.grantOwnerProviderName"/></div> 

<div class="col-sm-10">
	<input id="personalInformationForm_grantOwnerProviderName" class="form-control" type="text" name="grantownerprovidername"  value='<c:out value='${not empty param.grantownerprovidername ? param.grantownerprovidername : personalInformationForm.grantOwnerProviderName }'/>' />
</div>	
</div>		
  </div>
  <div class="panel-footer">
		<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.submit" />"/>
	</div>
</div>
</form>

<script>
$(document).ready(function() {


	});
</script>
