<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>


<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message
			code="label.firstTimeCandidacy.fillPersonalInformation" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon glyphicon-ok-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>
				<span class="glyphicon glyphicon-exclamation-sign"
					aria-hidden="true">&nbsp;</span> ${message}
			</p>
		</c:forEach>

	</div>
</c:if>

<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.name" />
				</label>

				<div class="col-sm-10">
					<div class="form-control-static"><c:out value='${not empty param.name ? param.name : personalInformationForm.name }' /></div>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.username" />
				</label>

				<div class="col-sm-10">
					<div class="form-control-static"><c:out value='${not empty param.username ? param.username : personalInformationForm.username }' /></div>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.gender" />
				</label>

				<div class="col-sm-4">
					<div class="form-control-static"><c:out value='${not empty param.gender ? param.gender : personalInformationForm.gender.localizedName }' /></div>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.documentIdNumber" />
				</label>

				<div class="col-sm-10">
					<div class="form-control-static"><c:out value='${not empty param.documentidnumber ? param.documentidnumber : personalInformationForm.documentIdNumber }' /></div>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.idDocumentType" />
				</label>

				<div class="col-sm-4">
					<div class="form-control-static"><c:out value='${not empty param.iddocumenttype ? param.iddocumenttype : personalInformationForm.idDocumentType.localizedName }' /></div>
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_identificationDocumentSeriesNumber" class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.identificationDocumentSeriesNumber" />
				</label>

				<div class="col-sm-10">
					<input
						id="personalInformationForm_identificationDocumentSeriesNumber"
						class="form-control" type="text"
						name="identificationDocumentSeriesNumber"
						value='<c:out value='${not empty param.identificationdocumentseriesnumber ? param.identificationdocumentseriesnumber : personalInformationForm.identificationDocumentSeriesNumber }'/>'
						required pattern="[0-9]|([0-9][a-zA-Z][a-zA-Z][0-9])" />
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_documentIdEmissionLocation" class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.documentIdEmissionLocation" />
				</label>

				<div class="col-sm-10">
					<input id="personalInformationForm_documentIdEmissionLocation"
						class="form-control" type="text" name="documentIdEmissionLocation"
						value='<c:out value='${not empty param.documentidemissionlocation ? param.documentidemissionlocation : personalInformationForm.documentIdEmissionLocation }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_documentIdEmissionDate" class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.documentIdEmissionDate" />
				</label>

				<div class="col-sm-4">
					<input id="personalInformationForm_documentIdEmissionDate"
						class="form-control" type="text" name="documentIdEmissionDate"
						bennu-date
						value='<c:out value='${not empty param.documentidemissiondate ? param.documentidemissiondate : personalInformationForm.documentIdEmissionDate }'/>'/>
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_documentIdExpirationDate" class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.documentIdExpirationDate" />
				</label>

				<div class="col-sm-4">
					<input id="personalInformationForm_documentIdExpirationDate"
						class="form-control" type="text" name="documentIdExpirationDate"
						bennu-date
						value='<c:out value='${not empty param.documentidexpirationdate ? param.documentidexpirationdate : personalInformationForm.documentIdExpirationDate }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_socialSecurityNumber" class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.socialSecurityNumber" />
				</label>

				<div class="col-sm-10">
					<input id="personalInformationForm_socialSecurityNumber"
						class="form-control" type="text" name="socialSecurityNumber"
						value='<c:out value='${not empty param.socialsecuritynumber ? param.socialsecuritynumber : personalInformationForm.socialSecurityNumber }'/>'
						pattern="(\d{9})"/>
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_maritalStatus" class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.maritalStatus" />
				</label>

				<div class="col-sm-4">
					<select id="personalInformationForm_maritalStatus"
						class="form-control" name="maritalStatus">
						<c:forEach items="${maritalStatusValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#personalInformationForm_maritalStatus").val('<c:out value='${not empty param.maritalstatus ? param.maritalstatus : personalInformationForm.maritalStatus }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_professionalCondition" class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.professionalCondition" />
				</label>

				<div class="col-sm-4">
					<select id="personalInformationForm_professionalCondition"
						class="form-control" name="professionalCondition">
						<c:forEach items="${professionalConditionValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#personalInformationForm_professionalCondition").val('<c:out value='${not empty param.professionalcondition ? param.professionalcondition : personalInformationForm.professionalCondition }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_profession" class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.profession" />
				</label>

				<div class="col-sm-10">
					<input id="personalInformationForm_profession" class="form-control"
						type="text" name="profession"
						value='<c:out value='${not empty param.profession ? param.profession : personalInformationForm.profession }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_professionTimeType" class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.professionTimeType" />
				</label>

				<div class="col-sm-4">
					<select id="personalInformationForm_professionTimeType" class="form-control" name="professionTimeType">
						<option value=""><spring:message code="label.choose.one"/></option>
						<c:forEach items="${professionTimeTypeValues}" var="value">
							<option value='<c:out value='${value.externalId}'/>'><c:out value='${value.description.content}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#personalInformationForm_professionTimeType").val('<c:out value='${not empty param.professiontimetype ? param.professiontimetype : personalInformationForm.professionTimeType.externalId }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_professionType" class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.professionType" />
				</label>

				<div class="col-sm-4">
					<select id="personalInformationForm_professionType"
						class="form-control" name="professionType">
						<c:forEach items="${professionTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#personalInformationForm_professionType").val('<c:out value='${not empty param.professiontype ? param.professiontype : personalInformationForm.professionType }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_grantOwnerType" class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.grantOwnerType" />
				</label>

				<div class="col-sm-10">
					<select id="personalInformationForm_grantOwnerType"	class="form-control" name="grantOwnerType">
						<c:forEach items="${grantOwnerTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><spring:message code="${field.qualifiedName}"/></option>
						</c:forEach>
					</select>
					<script>
						$("#personalInformationForm_grantOwnerType").val('<c:out value='${not empty param.grantownertype ? param.grantownertype : personalInformationForm.grantOwnerType }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.grantOwnerProvider" />
				</label>

				<div class="col-sm-10">
					<select id="personalInformationForm_grantOwnerProvider" class="form-control" name="grantOwnerProvider">
					<option selected value='${not empty personalInformationForm.grantOwnerProvider ? personalInformationForm.grantOwnerProvider.externalId : "" }'>
						${not empty personalInformationForm.grantOwnerProvider ? personalInformationForm.grantOwnerProvider.name : ""}
					</option> 
					</select>
					
					
					
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
		$("#personalInformationForm_grantOwnerProvider").select2(
				{
				  ajax: {
				    url: "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/personalinformationform/unit/",
				    dataType: 'json',
				    delay: 250,
				    data: function (params) {
				      return {
				        namePart: params.term, // search term
				        page: params.page
				      };
				    },
				    processResults: function (data, page) {
				      newData = []
				      for(var result in data){
				    	  newData[result] = {
				    			  text : data[result]["unitName"],
				    			  id : data[result]["unitExternalId"],
				    	  }
				      }
				      return {
				        results: newData
				      };
				    },
				    cache: true
				  }});
		
		updateGrantProvider = function(){
				val = $("#personalInformationForm_grantOwnerType").val();
				grantOwnerProvider = $("#personalInformationForm_grantOwnerProvider");
				if(val == "STUDENT_WITHOUT_SCHOLARSHIP"){
					grantOwnerProvider.select2("enable", false);
					grantOwnerProvider.select2('val', '');
				}
				else{
					grantOwnerProvider.select2("enable", true);
				}
		};
		$("#personalInformationForm_grantOwnerType").on("change", updateGrantProvider);
		updateGrantProvider();
		
	});
</script>
