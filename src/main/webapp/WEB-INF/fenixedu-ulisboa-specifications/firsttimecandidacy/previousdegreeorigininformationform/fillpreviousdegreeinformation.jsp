<%@page import="org.fenixedu.academic.domain.Country"%>
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
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/css/select2.min.css" rel="stylesheet" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/js/select2.full.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>



<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.firstTimeCandidacy.fillPreviousDegreeInformation" />
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

<p><strong>Indica a instituição e o curso que estavas matriculado no ano lectivo passado:</strong></p>
<p><em>TODO: ALTERAR TEXTO</em></p>

<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.PreviousDegreeInformationForm.precedentCountry" />
				</div>

				<div class="col-sm-10">
					<select
						id="previousDegreeInformationForm_precedentCountry"
						class="form-control"
						name="precedentCountry">
					</select>

				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.PreviousDegreeInformationForm.precedentSchoolLevel" />
				</div>

				<div class="col-sm-4">
					<select id="previousDegreeInformationForm_precedentSchoolLevel" class="form-control"
						name="precedentSchoolLevel">
						<option value=""></option>
						<c:forEach items="${schoolLevelValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#previousDegreeInformationForm_precedentSchoolLevel").select2().val('<c:out value='${not empty param.precedentschoollevel ? param.precedentschoollevel : previousDegreeInformationForm.precedentSchoolLevel }'/>');
					</script>
				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.PreviousDegreeInformationForm.otherPrecedentSchoolLevel" />
				</div>

				<div class="col-sm-10">
					<input id="previousDegreeInformationForm_otherPrecedentSchoolLevel"
						class="form-control" type="text" name="otherPrecedentSchoolLevel"
						value='<c:out value='${not empty param.otherprecedentschoollevel ? param.otherprecedentschoollevel : previousDegreeInformationForm.otherPrecedentSchoolLevel }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.PreviousDegreeInformationForm.precedentInstitution" />
				</div>

				<div class="col-sm-10">
					<select id="previousDegreeInformationForm_precedentInstitution" class="form-control" name="precedentInstitutionOid">
						<option value=""></option>
						<c:if test="${previousDegreeInformationForm.precedentInstitutionOid != null}">
							<option value="${previousDegreeInformationForm.precedentInstitutionOid}" selected><c:out value='${previousDegreeInformationForm.precedentInstitutionName}'/></option>
						</c:if>
					</select>
				</div>
			</div>
			<div class="form-group row"
				id="previousDegreeInformationForm_precedentDegreeDesignation_row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.PreviousDegreeInformationForm.precedentDegreeDesignation" />
				</div>

				<div class="col-sm-10">
					<input id="previousDegreeInformationForm_precedentDegreeDesignation"
						class="form-control" type="text" name="precedentDegreeDesignation"
						value='<c:out value='${not empty param.precedentdegreedesignation ? param.precedentdegreedesignation : previousDegreeInformationForm.precedentDegreeDesignation }'/>' />
				</div>
			</div>
			<div class="form-group row"
				id="previousDegreeInformationForm_raidesPrecedentDegreeDesignation_row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.PreviousDegreeInformationForm.raidesPrecedentDegreeDesignation" />
				</div>

				<div class="col-sm-10">
					<select id="previousDegreeInformationForm_raidesPrecedentDegreeDesignation" class="form-control" name="raidesPrecedentDegreeDesignation">
						<option value="${previousDegreeInformationForm.raidesPrecedentDegreeDesignation.externalId}" selected><c:out value='${previousDegreeInformationForm.raidesPrecedentDegreeDesignation.description}'/></option>
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
	defaultCountry = <%=Country.readDefault().getExternalId()%>;
	sortFunction = function(a,b) {
		return a.text.localeCompare(b.text);
	};
$(document).ready(function() {
	//setup country options	             		
	country_options = [
	             			<c:forEach items="${countries}" var="element"> 
	             				{
	             					text : "<c:out value='${element.name}'/>",  
	             					id : "<c:out value='${element.externalId}'/>"
	             				},
	             			</c:forEach>
	             		].sort(sortFunction);
   		$("#previousDegreeInformationForm_precedentCountry").select2(
   			{
   				data : country_options,
   			}	  
   	    );
   	    $("#previousDegreeInformationForm_precedentCountry").select2().select2('val', '<c:out value='${previousDegreeInformationForm.precedentCountry.externalId}'/>');

   	 $("#previousDegreeInformationForm_precedentCountry").select2().on("change", function(){
   		configureOriginInformationFieldsEditableState();
   		//enforce change event in school level to recalculate units provider endpoint
   		$("#previousDegreeInformationForm_precedentSchoolLevel").trigger("change");
	   });
   	    
   		$("#previousDegreeInformationForm_precedentSchoolLevel").trigger("change");
   		
   		updateDegreeDesignationsUrl();
   		configureOriginInformationFieldsEditableState();
	});
function currentSelectedCountry(){
	return $("#previousDegreeInformationForm_precedentCountry").val();
}

function configureOriginInformationFieldsEditableState(){
	bool = currentSelectedCountry() != defaultCountry;
}
   
	//setup units provider
	ajaxData = {
		url : "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/raidesUnit/",	    
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
	        results: newData.sort(sortFunction)
	      };
	    },
	    cache: true
	  };
	
	
	$("#previousDegreeInformationForm_precedentInstitution").select2({
		ajax: ajaxData,
		sorter: function(data) {
	        return data.sort(sortFunction);
	    }
	});
	$("#previousDegreeInformationForm_precedentInstitution").select2('val', '<c:out value='${previousDegreeInformationForm.precedentInstitutionOid}'/>');
	
	$("#previousDegreeInformationForm_highSchoolType_row").hide();
	
	
	function isHigherEducation(val){
		var higherEducation = [
			                   <c:forEach items="${schoolLevelValues}" var="field">
				                   	<c:if test="${field.higherEducation}">
				                   		"${field.name}",
				                   	</c:if>
								</c:forEach>
			                   ]
		return $.inArray(val, higherEducation) > -1;
	}
	
	schoolLevelChangeCount = 0;
	//Catch school level change events
	$("#previousDegreeInformationForm_precedentSchoolLevel").on("change", function(e){
		//array of courses which are considered by fenix as being higher education
		
		val = $("#previousDegreeInformationForm_precedentSchoolLevel").val();
		
		if(isHigherEducation(val) && currentSelectedCountry() == defaultCountry){
			$("#previousDegreeInformationForm_raidesPrecedentDegreeDesignation_row").show();
			$("#previousDegreeInformationForm_precedentDegreeDesignation_row").hide();
			ajaxData.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/raidesUnit/",
			$("#previousDegreeInformationForm_precedentInstitution").select2({ajax: ajaxData});
		}
		else{
			ajaxData.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/externalUnitFreeOption/",
			$("#previousDegreeInformationForm_precedentInstitution").select2({ajax: ajaxData});
			$("#previousDegreeInformationForm_raidesPrecedentDegreeDesignation_row").hide();
			$("#previousDegreeInformationForm_precedentDegreeDesignation_row").show();
		}
		
		if (schoolLevelChangeCount > 1) {
			$("#previousDegreeInformationForm_precedentInstitution").val("").trigger("change")
			$("#previousDegreeInformationForm_raidesPrecedentDegreeDesignation").val("");
			$("#previousDegreeInformationForm_precedentDegreeDesignation").val("");
		}
		schoolLevelChangeCount++;
		
		if(val == "OTHER"){
			$("#previousDegreeInformationForm_otherPrecedentSchoolLevel").attr('readonly', false);
		}
		else{
			$("#previousDegreeInformationForm_otherPrecedentSchoolLevel").attr('readonly', true);
			$("#previousDegreeInformationForm_otherPrecedentSchoolLevel").val("");
		}
	});
	//trigger change to update screen state
	$("#previousDegreeInformationForm_precedentSchoolLevel").trigger("change");
	
	ajaxDataForDegreesDesignations = {
		    dataType: 'json',
		    delay: 250,
		    data: function (params) {
		      return {
		        namePart: params.term, // search term
		        page: params.page,
		        schoolLevelType: $("#previousDegreeInformationForm_precedentSchoolLevel option:selected").val()		        
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
		        results: newData.sort(sortFunction)
		      };
		    },
		    cache: true
		  };
	
	updateDegreeDesignationsUrl = function(){
		ajaxDataForDegreesDesignations.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/degreeDesignation/" + $("#previousDegreeInformationForm_precedentInstitution").val(); 
		$("#previousDegreeInformationForm_raidesPrecedentDegreeDesignation").select2({ajax: ajaxDataForDegreesDesignations	});
	}
	
	updateDegreeDesignationsUrl();
	
	$("#previousDegreeInformationForm_precedentInstitution").select2().on("select2:select", function(e) {
		updateDegreeDesignationsUrl();
	});
	
	$("#previousDegreeInformationForm_precedentCountry").select2().on("select2:select", function(e) {
	});
	
	
</script>
