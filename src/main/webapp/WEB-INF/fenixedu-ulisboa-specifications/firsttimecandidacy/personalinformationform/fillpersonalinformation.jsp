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
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}${controllerURL}/back"><spring:message code="label.back"/></a>	
</div>

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
			<c:if test="${not partial}">
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.gender" />
				</label>

				<div class="col-sm-4">
					<div class="form-control-static"><c:out value='${not empty param.gender ? param.gender : personalInformationForm.gender.localizedName }' /></div>
				</div>
			</div>
			<c:if test="${not personalInformationForm.isForeignStudent}">
				<div class="form-group row">
					<label class="col-sm-2 control-label">
						<spring:message code="label.PersonalInformationForm.documentIdNumber" />
					</label>
	
					<div class="col-sm-10">
						<div class="form-control-static"><c:out value='${personalInformationForm.documentIdNumber }' /></div>
					</div>
				</div>
			</c:if>
			<c:if test="${personalInformationForm.isForeignStudent}">
				<div class="form-group row">
					<label class="col-sm-2 control-label required-field">
						<spring:message code="label.PersonalInformationForm.documentIdNumber" />
					</label>
	
					<div class="col-sm-10">
						<input id="personalInformationForm_documentIdNumber" class="form-control" type="text" name="documentIdNumber" required title="<spring:message code="label.field.required"/>"
							value='${not empty param.documentidnumber ? param.documentidnumber : personalInformationForm.documentIdNumber }'/>
					</div>
				</div>
			</c:if>
			
			<c:if test="${not personalInformationForm.isForeignStudent}">
				<div class="form-group row">
					<label class="col-sm-2 control-label">
						<spring:message code="label.PersonalInformationForm.idDocumentType" />
					</label>
	
					<div class="col-sm-10">
						<div class="form-control-static"><c:out value='${personalInformationForm.idDocumentType.localizedName }' /></div>
					</div>
				</div>
			</c:if>
			<c:if test="${personalInformationForm.isForeignStudent}">
				<div class="form-group row">
					<label class="col-sm-2 control-label required-field">
						<spring:message code="label.PersonalInformationForm.idDocumentType" />
					</label>
	
					<div class="col-sm-4">
						<select id="personalInformationForm_idDocumentType" class="form-control" name="idDocumentType" >
							<option value=""></option>
							<c:forEach items="${idDocumentTypeValues}" var="documentType">
								<option value="${documentType}">${documentType.localizedName}</option>
							</c:forEach>
						</select>
						<script>
							$("#personalInformationForm_idDocumentType").val('<c:out value='${not empty param.iddocumenttype ? param.iddocumenttype : personalInformationForm.idDocumentType }'/>');
						</script>
					</div>
				</div>
			</c:if>
			
			
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
				<label for="personalInformationForm_documentIdExpirationDate" class="col-sm-2 control-label required-field">
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
			<c:if test="${not personalInformationForm.isForeignStudent}">
				<div class="form-group row">
					<label for="personalInformationForm_socialSecurityNumber" class="col-sm-2 control-label required-field">
						<spring:message
							code="label.PersonalInformationForm.socialSecurityNumber" />
					</label>
	
					<div class="col-sm-10">
						<input id="personalInformationForm_socialSecurityNumber"
							class="form-control" type="text" name="socialSecurityNumber"
							value='<c:out value='${not empty param.socialsecuritynumber ? param.socialsecuritynumber : personalInformationForm.socialSecurityNumber }'/>'
							required pattern="(\d{9})" title="<spring:message code="label.PersonalInformationForm.socialSecurityNumber.required"/>"/>
					</div>
				</div>
			</c:if>
			<c:if test="${personalInformationForm.isForeignStudent}">
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
			</c:if>
			</c:if>
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
			<c:if test="${1 lt placingOption}">
				<div class="form-group row">
					<label class="col-sm-2 control-label">
						<spring:message
							code="label.PersonalInformationForm.firstOptionInstitution" />
					</label>
		
					<div class="col-sm-10">
						<select id="personalInformationForm_firstOptionInstitution" class="form-control" name="firstOptionInstitution">
							<c:if test="${personalInformationForm.firstOptionInstitution != null}">
								<option value="${personalInformationForm.firstOptionInstitution.externalId}" selected><c:out value='${personalInformationForm.firstOptionInstitution.name}'/></option>
							</c:if>
						</select>
					</div>
				</div>
				<div class="form-group row">
					<div class="col-sm-2 control-label">
						<spring:message
							code="label.PersonalInformationForm.firstOptionDegreeDesignation" />
					</div>
	
					<div class="col-sm-10">
						<select id="personalInformationForm_firstOptionDegreeDesignation" class="form-control" name="firstOptionDegreeDesignation">
							<option value="${personalInformationForm.firstOptionDegreeDesignation.externalId}" selected><c:out value='${personalInformationForm.firstOptionDegreeDesignation.description}'/></option>
						</select>
					</div>
				</div>
			</c:if>
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
						<option value=""></option>
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
						 <option value="${personalInformationForm.grantOwnerProvider}" selected>${personalInformationForm.grantOwnerProviderName}</option>
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

<script>
$(document).ready(function() {
	
		ajaxForGrantOwnerProvider = {
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
		  };
		
		function updateGrantProviderAjax(){
			val = $("#personalInformationForm_grantOwnerType").val();
			if(val == "OTHER_INSTITUTION_GRANT_OWNER" || val == "ORIGIN_COUNTRY_GRANT_OWNER"){
				ajaxForGrantOwnerProvider.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/externalUnitFreeOption/";
			}
			else{
				ajaxForGrantOwnerProvider.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/externalUnit/";
			}
				$("#personalInformationForm_grantOwnerProvider").select2({ajax : ajaxForGrantOwnerProvider});
		}
		
		updateGrantProviderAjax();
		
		updateGrantProvider = function(){
				val = $("#personalInformationForm_grantOwnerType").val();
				grantOwnerProvider = $("#personalInformationForm_grantOwnerProvider");
				if(val == "STUDENT_WITHOUT_SCHOLARSHIP"){
					grantOwnerProvider.select2("enable", false);
				}
				else{
					grantOwnerProvider.select2("enable", true);
					updateGrantProviderAjax();
				}
				grantOwnerProvider.select2('val', '');
		};
		$("#personalInformationForm_grantOwnerType").on("change", updateGrantProvider);
		updateGrantProvider();
		
		
		
		$("#personalInformationForm_firstOptionInstitution").select2(
				{
				  ajax: {
					    url: "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/academicUnit/",
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

		ajaxDataForDegreesDesignations = {
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
			    			  text : data[result]["degreeDesignationText"],
			    			  id : data[result]["degreeDesignationId"],
			    	  }
			      }
			      return {
			        results: newData
			      };
			    },
			    cache: true
			  };
		updateDegreeDesignationsUrl = function(){
			ajaxDataForDegreesDesignations.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/degreeDesignation/" + $("#personalInformationForm_firstOptionInstitution").val(); 
			$("#personalInformationForm_firstOptionDegreeDesignation").select2({ajax: ajaxDataForDegreesDesignations});
		}
		updateDegreeDesignationsUrl();
		$("#personalInformationForm_firstOptionInstitution").on("select2:select", function(e) {
			updateDegreeDesignationsUrl();
		});
	});
</script>
