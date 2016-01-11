<%@page import="org.fenixedu.academic.domain.Country"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<div class="form-group row">
	<div class="col-sm-2 control-label required-field">
		<spring:message
			code="label.PreviousDegreeInformationForm.precedentCountry" />
	</div>

	<div class="col-sm-10">
		<select id="previousDegreeInformationForm_precedentCountry" class="form-control" name="precedentCountry">
			<c:forEach var="c" items="${countries}">
				<option value="${c.externalId}">${c.localizedName.content}</option>
			</c:forEach>
		</select>
		<script>
			$(document).ready(function() {
	   	   	    $("#previousDegreeInformationForm_precedentCountry").select2()
	   	   	    	.select2('val', '<c:out value='${previousDegreeInformationForm.precedentCountry.externalId}'/>');
	   	   	    
		   	   	 $("#previousDegreeInformationForm_precedentCountry").select2().on("change", function() {
		   	   		configureOriginInformationFieldsEditableState();
		   	   		//enforce change event in school level to recalculate units provider endpoint
		   	   		$("#previousDegreeInformationForm_precedentSchoolLevel").trigger("change");
		   		 });
			});
		</script>
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
			<c:forEach items="${schoolLevelValues}" var="s">
				<option value='<c:out value='${s}'/>'><c:out value='${s.localizedName}' /></option>
			</c:forEach>
		</select>
		<script>
			$("#previousDegreeInformationForm_precedentSchoolLevel").select2()
				.select2('val', '<c:out value='${previousDegreeInformationForm.precedentSchoolLevel}'/>');
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
			value='<c:out value='${previousDegreeInformationForm.otherPrecedentSchoolLevel}'/>' />
	</div>
</div>
<div class="form-group row">
	<div class="col-sm-2 control-label required-field">
		<spring:message code="label.PreviousDegreeInformationForm.precedentInstitution" />
	</div>

	<div class="col-sm-10">
		<select id="previousDegreeInformationForm_precedentInstitution" class="form-control" name="precedentInstitutionOid">
			<option value=""></option>
			<option value="${previousDegreeInformationForm.precedentInstitutionOid}" selected>
				<c:out value='${previousDegreeInformationForm.precedentInstitutionName}'/>
			</option>
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
			value='<c:out value='${previousDegreeInformationForm.precedentDegreeDesignation }'/>' />
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
<div class="form-group row">
	<div class="col-sm-2 control-label required-field">
		<spring:message code="label.PreviousDegreeInformationForm.numberOfEnrolmentsInPreviousDegrees" />
	</div>

	<div class="col-sm-10">
		<input id="previousDegreeInformationForm_numberOfEnrolmentsInPreviousDegrees"
			class="form-control" type="text" name="numberOfEnrolmentsInPreviousDegrees" required pattern="\d+" title="<spring:message code="error.PreviousDegreeInformationForm.numberOfEnrolmentsInPreviousDegrees.required"/>"
			value='<c:out value='${previousDegreeInformationForm.numberOfEnrolmentsInPreviousDegrees}'/>' />
	</div>
</div>

<script>
	$(document).ready(function() {
	
		defaultCountry = <%=Country.readDefault().getExternalId()%>;
		sortFunction = function(a,b) {
			return a.text.localeCompare(b.text);
		};
	
	
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
			
			$("#previousDegreeInformationForm_precedentInstitution").select2().on("select2:select", function(e) {
				updateDegreeDesignationsUrl();
			});
			
			$("#previousDegreeInformationForm_precedentCountry").select2().on("select2:select", function(e) {
			});
		
	   	    
	   		$("#previousDegreeInformationForm_precedentSchoolLevel").trigger("change");
	   		
	   		updateDegreeDesignationsUrl();
	   		configureOriginInformationFieldsEditableState();
	});
	
</script>