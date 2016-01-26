<%@page import="org.fenixedu.academic.domain.Country"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

		<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.OriginInformationForm.countryWhereFinishedPreviousCompleteDegree" />
				</div>

				<div class="col-sm-10">
					<select
						id="originInformationForm_countryWhereFinishedPreviousCompleteDegree"
						class="form-control" name="countryWhereFinishedPreviousCompleteDegree">
						<c:forEach var="c" items="${countries}">
							<option value="${c.externalId}"><c:out value="${c.localizedName.content}" /></option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
					   	    $("#originInformationForm_countryWhereFinishedPreviousCompleteDegree").select2().select2('val', '<c:out value='${originInformationForm.countryWhereFinishedPreviousCompleteDegree.externalId}' />');
						});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<c:if test="${districtAndSubdivisionRequired}">
					<div class="col-sm-2 control-label required-field" id="labelDistrictOfGraduation">
						<spring:message
							code="label.OriginInformationForm.districtWhereFinishedPreviousCompleteDegree" />
					</div>
				</c:if>
				
				<c:if test="${not districtAndSubdivisionRequired}">
					<div class="col-sm-2 control-label" id="labelDistrictOfGraduation">
						<spring:message
							code="label.OriginInformationForm.districtWhereFinishedPreviousCompleteDegree" />
					</div>
				</c:if>
				
				<div class="col-sm-10">
					<select
						id="originInformationForm_districtWhereFinishedPreviousCompleteDegree"
						class="form-control"
						name="districtWhereFinishedPreviousCompleteDegree">
						<option value=""></option>
						<c:forEach var="d" items="${districts_options}">
							<option value="${d.externalId}">${d.name}</option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
			             	 $("#originInformationForm_districtWhereFinishedPreviousCompleteDegree").select2().select2('val', '<c:out value='${originInformationForm.districtWhereFinishedPreviousCompleteDegree.externalId}'/>');
			             	 $("#originInformationForm_districtWhereFinishedPreviousCompleteDegree").select2().on("select2:select", function(e) {
			                   populateSubDistricts(e);
			                 });
						});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<c:if test="${districtAndSubdivisionRequired}">
				<div class="col-sm-2 control-label required-field" id="labelDistrictSubdivisionOfGraduation">
					<spring:message
						code="label.OriginInformationForm.districtSubdivisionWhereFinishedPreviousCompleteDegree" />
				</div>
				</c:if>
				
				<c:if test="${not districtAndSubdivisionRequired}">
				<div class="col-sm-2 control-label" id="labelDistrictSubdivisionOfGraduation">
					<spring:message
						code="label.OriginInformationForm.districtSubdivisionWhereFinishedPreviousCompleteDegree" />
				</div>
				</c:if>
				
				<div class="col-sm-10">
					<select
						id="originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree"
						class="form-control"
						name="districtSubdivisionWhereFinishedPreviousCompleteDegree">
						<option value=""></option>
					</select>

				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.OriginInformationForm.schoolLevel" />
				</div>

				<div class="col-sm-4">
					<select id="originInformationForm_schoolLevel" class="form-control"
						name="schoolLevel">
						<option value=""></option>
						<c:forEach items="${schoolLevelValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
							$("#originInformationForm_schoolLevel").select2().select2('val', '<c:out value='${originInformationForm.schoolLevel}' />');
						});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.OriginInformationForm.otherSchoolLevel" />
				</div>

				<div class="col-sm-10">
					<input id="originInformationForm_otherSchoolLevel"
						class="form-control" type="text" name="otherSchoolLevel"
						value='<c:out value='${originInformationForm.otherSchoolLevel }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.OriginInformationForm.institution" />
				</div>

				<div class="col-sm-10">
					<select id="originInformationForm_institution" class="form-control" name="institutionOid">
						<option value=""></option>
						<option value="${originInformationForm.institutionOid}" selected><c:out value='${originInformationForm.institutionName}'/></option>
					</select>
				</div>
			</div>
			<div class="form-group row"
				id="originInformationForm_degreeDesignation_row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.OriginInformationForm.degreeDesignation" />
				</div>

				<div class="col-sm-10">
					<input id="originInformationForm_degreeDesignation"
						class="form-control" type="text" name="degreeDesignation"
						value='<c:out value='${originInformationForm.degreeDesignation}'/>' />
				</div>
			</div>
			<div class="form-group row"
				id="originInformationForm_raidesDegreeDesignation_row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.OriginInformationForm.raidesDegreeDesignation" />
				</div>

				<div class="col-sm-10">
					<select id="originInformationForm_raidesDegreeDesignation" class="form-control" name="raidesDegreeDesignation">
						<option value="${originInformationForm.raidesDegreeDesignation.externalId}" selected><c:out value='${originInformationForm.raidesDegreeDesignation.description}'/></option>
					</select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.OriginInformationForm.conclusionGrade" />
				</div>

				<div class="col-sm-10">
					<input id="originInformationForm_conclusionGrade"
						class="form-control" type="text" name="conclusionGrade" pattern="(\d{2})?" title="<spring:message code="label.OriginInformationForm.conclusionGrade.required"/>"
						value='<c:out value='${originInformationForm.conclusionGrade}'/>' />
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.OriginInformationForm.conclusionYear" />
				</div>

				<div class="col-sm-10">
					<input id="originInformationForm_conclusionYear"
						class="form-control" type="text" name="conclusionYear" required pattern="\d{4}" title="<spring:message code="label.OriginInformationForm.conclusionYear.required"/>"
						value='<c:out value='${originInformationForm.conclusionYear}'/>' />
				</div>
			</div>
			<div class="form-group row"
				id="originInformationForm_highSchoolType_row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.OriginInformationForm.highSchoolType" />
				</div>

				<div class="col-sm-4">
					<select id="originInformationForm_highSchoolType"
						class="form-control" name="highSchoolType">
						<option value=""></option>
						<c:forEach items="${highSchoolTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><spring:message code="${field.name}"/></option>
						</c:forEach>
					</select>
					<script>
						$("#originInformationForm_highSchoolType").select2().select2('val', '<c:out value='${originInformationForm.highSchoolType }'/>');
					</script>
				</div>
			</div>
			
			<script>
				defaultCountry = <%= Country.readDefault().getExternalId() %>;
				sortFunction = function(a,b) {
					return a.text.localeCompare(b.text);
				};
			
				$(document).ready(function() {
			
					function currentSelectedCountry(){
						return $("#originInformationForm_countryWhereFinishedPreviousCompleteDegree").val();
					}
			
					function configureOriginInformationFieldsEditableState(){
						bool = currentSelectedCountry() != defaultCountry;
						$("#originInformationForm_districtWhereFinishedPreviousCompleteDegree").attr("disabled", bool);
						$("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").attr("disabled", bool);
						
						if(bool){
							$("#originInformationForm_districtWhereFinishedPreviousCompleteDegree").val("").trigger("change");
							$("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").val("").trigger("change");
							
							
							$('#labelDistrictOfGraduation').removeClass("required-field");
							$('#labelDistrictSubdivisionOfGraduation').removeClass("required-field");
						} else {
							<c:if test="${districtAndSubdivisionRequired}">
							$('#labelDistrictOfGraduation').addClass("required-field");
							$('#labelDistrictSubdivisionOfGraduation').addClass("required-field");
							</c:if>
						}
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
					
					
					updateHighSchoolType = function(){
				 		schoolLevel = $("#originInformationForm_schoolLevel").val();
						country = $("#originInformationForm_countryWhereFinishedPreviousCompleteDegree option:selected").text();
						if ((schoolLevel == "HIGH_SCHOOL_OR_EQUIVALENT" || schoolLevel == "POST_HIGH_SCHOOL_SPECIALIZATION")){
							$("#originInformationForm_highSchoolType_row").show();
						}
						else {
							$("#originInformationForm_highSchoolType_row").hide();
						}
					}
					
					
					
					$("#originInformationForm_highSchoolType_row").hide();
					
					
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
					$("#originInformationForm_schoolLevel").on("change", function(e){
						//array of courses which are considered by fenix as being higher education
						
						updateHighSchoolType();
						
						val = $("#originInformationForm_schoolLevel").val();
						
						if(isHigherEducation(val) && currentSelectedCountry() == defaultCountry){
							$("#originInformationForm_raidesDegreeDesignation_row").show();
							$("#originInformationForm_degreeDesignation_row").hide();
							ajaxData.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/raidesUnit/",
							$("#originInformationForm_institution").select2({ajax: ajaxData});
						}
						else{
							ajaxData.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/externalUnitFreeOption/",
							$("#originInformationForm_institution").select2({ajax: ajaxData});
							$("#originInformationForm_raidesDegreeDesignation_row").hide();
							$("#originInformationForm_degreeDesignation_row").show();
						}
						
						if (schoolLevelChangeCount > 1) {
							$("#originInformationForm_institution").val("").trigger("change")
							$("#originInformationForm_raidesDegreeDesignation").val("");
							$("#originInformationForm_degreeDesignation").val("");
						}
						schoolLevelChangeCount++;
						
						if(val == "OTHER"){
							$("#originInformationForm_otherSchoolLevel").attr('readonly', false);
						}
						else{
							$("#originInformationForm_otherSchoolLevel").attr('readonly', true);
							$("#originInformationForm_otherSchoolLevel").val("");
						}
					});
					//trigger change to update screen state
					$("#originInformationForm_schoolLevel").trigger("change");
					
					ajaxDataForDegreesDesignations = {
						    dataType: 'json',
						    delay: 250,
						    data: function (params) {
						      return {
						        namePart: params.term, // search term
						        page: params.page,
						        schoolLevelType: $("#originInformationForm_schoolLevel option:selected").val()
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
						ajaxDataForDegreesDesignations.url = "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/degreeDesignation/" + $("#originInformationForm_institution").val(); 
						$("#originInformationForm_raidesDegreeDesignation").select2({ajax: ajaxDataForDegreesDesignations});
					}
					updateDegreeDesignationsUrl();
					$("#originInformationForm_institution").select2().on("select2:select", function(e) {
						updateDegreeDesignationsUrl();
					});
					
					$("#originInformationForm_countryWhereFinishedPreviousCompleteDegree").select2().on("select2:select", function(e) {
						updateHighSchoolType();
					});
					
						             	    
				  	 populateSubDistricts = function(){
				  		 oid = $("#originInformationForm_districtWhereFinishedPreviousCompleteDegree")[0].value; 
				  		 $.ajax({url : "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/district/" + oid, 
				  				success: function(result){
				  					 //$("#originInformationForm_districtSubdivisionOfBirth").select2("destroy");
				  					 $("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").children().remove();
				  					 $("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").select2(
						             			{
						             				data : result.sort(sortFunction),
						             			}	  
						             	    );
				  					$("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").select2();
				  					$("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").select2().select2('val', '');
				  		 		}
				  		 });
				  		 
				  	 }
			
				 	//setup sub-districts
				 	$("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").select2();
				 	<c:if test="${not empty originInformationForm.districtWhereFinishedPreviousCompleteDegree}">
				 	subDistrictOptions = [
				            			<c:forEach items="${originInformationForm.districtWhereFinishedPreviousCompleteDegree.districtSubdivisions}" var="element"> 
				            				{
				            					text : "<c:out value='${element.name}'/>",  
				            					id : "<c:out value='${element.externalId}'/>"
				            				},
				            			</c:forEach>
				            		].sort(sortFunction);
			
				            	   $("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").select2(
					             			{
					             				data : subDistrictOptions,
					             			}	  
					             	    );
					             	    
					             	    $("#originInformationForm_districtSubdivisionWhereFinishedPreviousCompleteDegree").select2().select2('val', '<c:out value='${originInformationForm.districtSubdivisionWhereFinishedPreviousCompleteDegree.externalId}'/>');
			
				   	</c:if>
					
			   	 $("#originInformationForm_countryWhereFinishedPreviousCompleteDegree").select2().on("change", function(){
			   		configureOriginInformationFieldsEditableState();
			   		//enforce change event in school level to recalculate units provider endpoint
			   		$("#originInformationForm_schoolLevel").trigger("change");
				 });
			   	    
			 	 $("#originInformationForm_schoolLevel").trigger("change");
			 		
			 	 updateDegreeDesignationsUrl();
			 	 configureOriginInformationFieldsEditableState();
			 	 
					$("#originInformationForm_institution").select2({
						ajax: ajaxData,
						sorter: function(data) {
					        return data.sort(sortFunction);
					    }
					});
					$("#originInformationForm_institution").select2('val', '<c:out value='${originInformationForm.institutionOid}'/>');
			 	 
			});
				
			</script>
