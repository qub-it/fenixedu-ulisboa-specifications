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
	<h1><spring:message code="label.firstTimeCandidacy.fillHouseHoldInformation" />
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
				<label for="householdInformationForm_professionalCondition" class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.professionalCondition" />
				</label>

				<div class="col-sm-4">
					<select id="householdInformationForm_professionalCondition"
						class="form-control" name="professionalCondition">
						<c:forEach items="${professionalConditionValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						$("#householdInformationForm_professionalCondition").select2().select2('val', '<c:out value='${not empty param.professionalcondition ? param.professionalcondition : householdInformationForm.professionalCondition }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<label for="householdInformationForm_professionType" class="col-sm-2 control-label required-field">
					<spring:message code="label.HouseholdInformationForm.professionType" />
				</label>

				<div class="col-sm-4">
					<select id="householdInformationForm_professionType"
						class="form-control" name="professionType">
						<c:forEach items="${professionTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						$("#householdInformationForm_professionType").select2().select2('val', '<c:out value='${not empty param.professiontype ? param.professiontype : householdInformationForm.professionType }'/>');
					});
					</script>
				</div>
			</div>			
			<div class="form-group row">
				<label for="householdInformationForm_profession" class="col-sm-2 control-label">
					<spring:message code="label.HouseholdInformationForm.profession" />
				</label>

				<div class="col-sm-10">
					<input id="householdInformationForm_profession" class="form-control"
						type="text" name="profession"
						value='<c:out value='${not empty param.profession ? param.profession : householdInformationForm.profession }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<label for="householdInformationForm_professionTimeType" class="col-sm-2 control-label">
					<spring:message code="label.HouseholdInformationForm.professionTimeType" />
				</label>

				<div class="col-sm-4">
					<select id="householdInformationForm_professionTimeType" class="form-control" name="professionTimeType">
						<option value=""></option>
						<c:forEach items="${professionTimeTypeValues}" var="value">
							<option value='<c:out value='${value.externalId}'/>'><c:out value='${value.description.content}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
					
						$("#householdInformationForm_professionTimeType").select2().select2('val', '<c:out value='${not empty param.professiontimetype ? param.professiontimetype : householdInformationForm.professionTimeType.externalId }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<label for="householdInformationForm_grantOwnerType" class="col-sm-2 control-label required-field">
					<spring:message code="label.HouseholdInformationForm.grantOwnerType" />
				</label>

				<div class="col-sm-10">
					<select id="householdInformationForm_grantOwnerType"	class="form-control" name="grantOwnerType">
						<c:forEach items="${grantOwnerTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><spring:message code="${field.qualifiedName}"/></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						
						$("#householdInformationForm_grantOwnerType").select2().select2('val', '<c:out value='${not empty param.grantownertype ? param.grantownertype : householdInformationForm.grantOwnerType }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message
						code="label.HouseholdInformationForm.grantOwnerProvider" />
				</label>

				<div class="col-sm-10">
					<select id="householdInformationForm_grantOwnerProvider" class="form-control" name="grantOwnerProvider">
						 <option value="${householdInformationForm.grantOwnerProvider}" selected>${householdInformationForm.grantOwnerProviderName}</option>
					</select>
				</div>
			</div>
		
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.motherSchoolLevel" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_motherSchoolLevel"
						class="form-control" name="motherSchoolLevel">
						<option value=""></option>
						<c:forEach items="${schoolLevelValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						
						$("#householdInformationForm_motherSchoolLevel").select2().select2('val', '<c:out value='${not empty param.motherschoollevel ? param.motherschoollevel : householdInformationForm.motherSchoolLevel }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.motherProfessionType" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_motherProfessionType"
						class="form-control" name="motherProfessionType">
						<option value=""></option>
						<c:forEach items="${professionTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						
						$("#householdInformationForm_motherProfessionType").select2().select2('val', '<c:out value='${not empty param.motherprofessiontype ? param.motherprofessiontype : householdInformationForm.motherProfessionType }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.motherProfessionalCondition" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_motherProfessionalCondition"
						class="form-control" name="motherProfessionalCondition">
						<option value=""></option>
						<c:forEach items="${professionalConditionValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						
						$("#householdInformationForm_motherProfessionalCondition").select2().select2('val', '<c:out value='${not empty param.motherprofessionalcondition ? param.motherprofessionalcondition : householdInformationForm.motherProfessionalCondition }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.fatherSchoolLevel" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_fatherSchoolLevel"
						class="form-control" name="fatherSchoolLevel">
						<option value=""></option>
						<c:forEach items="${schoolLevelValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						
						$("#householdInformationForm_fatherSchoolLevel").select2().select2('val', '<c:out value='${not empty param.fatherschoollevel ? param.fatherschoollevel : householdInformationForm.fatherSchoolLevel }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.fatherProfessionType" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_fatherProfessionType"
						class="form-control" name="fatherProfessionType">
						<option value=""></option>
						<c:forEach items="${professionTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						
						$("#householdInformationForm_fatherProfessionType").select2().select2('val', '<c:out value='${not empty param.fatherprofessiontype ? param.fatherprofessiontype : householdInformationForm.fatherProfessionType }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.fatherProfessionalCondition" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_fatherProfessionalCondition"
						class="form-control" name="fatherProfessionalCondition">
						<option value=""></option>
						<c:forEach items="${professionalConditionValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						$("#householdInformationForm_fatherProfessionalCondition").select2().select2('val', '<c:out value='${not empty param.fatherprofessionalcondition ? param.fatherprofessionalcondition : householdInformationForm.fatherProfessionalCondition }'/>');
					});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.HouseholdInformationForm.householdSalarySpan" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_householdSalarySpan" class="form-control" name="householdSalarySpan">
						<option value=""></option>
						<c:forEach items="${salarySpanValues}" var="salarySpanValue">
							<option value='<c:out value='${salarySpanValue.externalId}'/>'><c:out value='${salarySpanValue.description.content}' /></option>
						</c:forEach>
					</select>
					<script>
					$(document).ready(function() {
						$("#householdInformationForm_householdSalarySpan").select2().select2('val', '<c:out value='${not empty param.householdsalaryspan ? param.householdsalaryspan : householdInformationForm.householdSalarySpan.externalId }'/>');
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
	val = $("#householdInformationForm_grantOwnerType").val();
	if(val == "OTHER_INSTITUTION_GRANT_OWNER" || val == "ORIGIN_COUNTRY_GRANT_OWNER"){
		ajaxForGrantOwnerProvider.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/externalUnitFreeOption/";
	}
	else{
		ajaxForGrantOwnerProvider.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/externalUnit/";
	}
		$("#householdInformationForm_grantOwnerProvider").select2({ajax : ajaxForGrantOwnerProvider});
}

updateGrantProviderAjax();

updateGrantProvider = function(){
		val = $("#householdInformationForm_grantOwnerType").val();
		grantOwnerProvider = $("#householdInformationForm_grantOwnerProvider");
		if(val == "STUDENT_WITHOUT_SCHOLARSHIP"){
			grantOwnerProvider.select2("enable", false);
		}
		else{
			grantOwnerProvider.select2("enable", true);
			updateGrantProviderAjax();
		}
		grantOwnerProvider.select2('val', '');
};
$("#householdInformationForm_grantOwnerType").on("change", updateGrantProvider);
updateGrantProvider();
	
	
});
</script>
