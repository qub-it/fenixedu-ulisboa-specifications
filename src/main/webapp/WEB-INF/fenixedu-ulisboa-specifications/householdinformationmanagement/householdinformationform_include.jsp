<%@page import="org.fenixedu.academic.domain.Country"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

	<div class="panel panel-default">
		<div class="panel-body">

			<div class="form-group row">
				<label for="householdInformationForm_professionalCondition" class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.professionalCondition" />
				</label>

				<div class="col-sm-6">
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

				<div class="col-sm-6">
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

				<div class="col-sm-6">
					<input id="householdInformationForm_profession" class="form-control"
						type="text" name="profession"
						value='<c:out value='${not empty param.profession ? param.profession : householdInformationForm.profession }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<label for="householdInformationForm_professionTimeType" class="col-sm-2 control-label">
					<spring:message code="label.HouseholdInformationForm.professionTimeType" />
				</label>

				<div class="col-sm-6">
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
			
			
		</div>
	</div>
	
	
	<div class="panel panel-default">
		<div class="panel-body">
	
			<div class="form-group row">
				<label for="householdInformationForm_grantOwnerType" class="col-sm-2 control-label required-field">
					<spring:message code="label.HouseholdInformationForm.grantOwnerType" />
				</label>

				<div class="col-sm-6">
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

				<div class="col-sm-6">
					<select id="householdInformationForm_grantOwnerProvider" class="form-control" name="grantOwnerProvider">
						 <option value="${householdInformationForm.grantOwnerProvider}" selected>${householdInformationForm.grantOwnerProviderName}</option>
					</select>
				</div>
			</div>
		</div>
	</div>
	
	<div class="panel panel-default">
		<div class="panel-body">
			
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message
						code="label.HouseholdInformationForm.motherSchoolLevel" />
				</div>

				<div class="col-sm-6">
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

				<div class="col-sm-6">
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

				<div class="col-sm-6">
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

				<div class="col-sm-6">
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

				<div class="col-sm-6">
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

				<div class="col-sm-6">
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

				<div class="col-sm-6">
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
	</div>
	
	<div class="panel panel-default">
		<div class="panel-body">

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
					$(document).ready(function() {
						$("#personalInformationForm_maritalStatus").select2().select2('val', '<c:out value='${householdInformationForm.maritalStatus }'/>');
					});
					</script>
				</div>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.HouseholdInformationForm.dislocatedFromPermanentResidence" />
				</div>

				<div class="col-sm-10">
					<select id="householdInformationForm_dislocatedFromPermanentResidence" name="dislocatedFromPermanentResidence" class="form-control">
						<option value="false"><spring:message code="label.no" /></option>
						<option value="true"><spring:message code="label.yes" /></option>
					</select>
				</div>
				<script>
					function currentSelectedCountry(){
						return $("#householdInformationForm_countryOfResidence").val();
					}
			
					function onChangeDislocatedFromPermanentResidence() {
						var dislocated = $("#householdInformationForm_dislocatedFromPermanentResidence").select2('val');
						if(dislocated === "true") {
							$("#labelCountryOfResidence").addClass('required-field');
							$("#labelPermanentResidenceDistrict").addClass('required-field');
							$("#labelPermanentResidentDistrictSubdivision").addClass('required-field');
							$("#labelDislocatedResidenceType").addClass('required-field');
						} else if(dislocated === "false") {

							$("#labelCountryOfResidence").removeClass('required-field');
							$("#labelPermanentResidenceDistrict").removeClass('required-field');
							$("#labelPermanentResidentDistrictSubdivision").removeClass('required-field');
							$("#labelDislocatedResidenceType").removeClass('required-field');
						}
						
						configureOriginInformationFieldsEditableState();
					}
				
					function configureOriginInformationFieldsEditableState(){
						var dislocated = $("#householdInformationForm_dislocatedFromPermanentResidence").select2('val');

						if(dislocated === "false" || currentSelectedCountry() != defaultCountry){
							$("#householdInformationForm_permanentResidenceDistrict").val("").trigger("change");
							$("#householdInformationForm_permanentResidentDistrictSubdivision").val("").trigger("change");
							
							$('#labelPermanentResidenceDistrict').removeClass("required-field");
							$('#labelPermanentResidentDistrictSubdivision').removeClass("required-field");
						} else {
							$('#labelPermanentResidenceDistrict').addClass("required-field");
							$('#labelPermanentResidentDistrictSubdivision').addClass("required-field");
						}
					}
					
					$(document).ready(function() {
						
						
						$("#householdInformationForm_dislocatedFromPermanentResidence").select2().select2('val', '<c:out value='${householdInformationForm.dislocatedFromPermanentResidence }'/>');
						$("#householdInformationForm_dislocatedFromPermanentResidence").select2().on("select2:select", function(e) {
							onChangeDislocatedFromPermanentResidence();
						});
					});
				</script>
			</div>

			<div class="form-group row">
				<div class="col-sm-2 control-label required-field" id="labelCountryOfResidence">
					<spring:message
						code="label.HouseholdInformationForm.countryOfResidence" />
				</div>

				<div class="col-sm-10">
					<select
						id="householdInformationForm_countryOfResidence"
						class="form-control" name="countryOfResidence">
							<option value="" />
						<c:forEach var="c" items="${countries}">
							<option value="${c.externalId}"><c:out value="${c.localizedName.content}" /></option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
					   	    $("#householdInformationForm_countryOfResidence").select2().select2('val', '<c:out value='${householdInformationForm.countryOfResidence.externalId}' />');
						});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field" id="labelPermanentResidenceDistrict">
					<spring:message
						code="label.HouseholdInformationForm.permanentResidenceDistrict" />
				</div>
				
				<div class="col-sm-10">
					<select
						id="householdInformationForm_permanentResidenceDistrict"
						class="form-control"
						name="permanentResidenceDistrict">
						<option value=""></option>
						<c:forEach var="d" items="${districts_options}">
							<option value="${d.externalId}">${d.name}</option>
						</c:forEach>
					</select>
					<script>
						$(document).ready(function() {
			             	 $("#householdInformationForm_permanentResidenceDistrict").select2().select2('val', '<c:out value='${householdInformationForm.permanentResidenceDistrict.externalId}'/>');
			             	 $("#householdInformationForm_permanentResidenceDistrict").select2().on("select2:select", function(e) {
			                   populateSubDistricts(e);
			                 });
						});
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field" id="labelPermanentResidentDistrictSubdivision">
					<spring:message
						code="label.HouseholdInformationForm.permanentResidentDistrictSubdivision" />
				</div>
				
				<div class="col-sm-10">
					<select
						id="householdInformationForm_permanentResidentDistrictSubdivision"
						class="form-control"
						name="permanentResidentDistrictSubdivision">
						<option value=""></option>
					</select>

				</div>
			</div>
			
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field" id="labelDislocatedResidenceType">
					<spring:message
						code="label.HouseholdInformationForm.dislocatedResidenceType" />
				</div>
				<div class="col-sm-10">
					<select id="householdInformationForm_dislocatedResidenceType"
						class="form-control"
						name="dislocatedResidenceType">
							<option value="" ></option>
						
						<c:forEach var="residenceType" items="${residenceType_values}">
							<option value="${residenceType.externalId}" ><c:out value="${residenceType.description.content}" /></option>
						</c:forEach>						
					</select>
					<script>
						$(document).ready(function() {
			             	 $("#householdInformationForm_dislocatedResidenceType").select2().select2('val', '<c:out value='${householdInformationForm.dislocatedResidenceType.externalId}'/>');
						});
					</script>
				</div>
			</div>
		</div>
	</div>
			
			<script>
				$(document).ready(function() {

					defaultCountry = <%= Country.readDefault().getExternalId() %>;

					sortFunction = function(a,b) {
						return a.text.localeCompare(b.text);
					};
				
				  	 populateSubDistricts = function(){
				  		 oid = $("#householdInformationForm_permanentResidenceDistrict")[0].value; 
				  		 $.ajax({url : "${pageContext.request.contextPath}/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/district/" + oid, 
				  				success: function(result){
				  					 //$("#originInformationForm_districtSubdivisionOfBirth").select2("destroy");
				  					 $("#householdInformationForm_permanentResidentDistrictSubdivision").children().remove();
				  					 $("#householdInformationForm_permanentResidentDistrictSubdivision").select2(
						             			{
						             				data : result.sort(sortFunction),
						             			}	  
						             	    );
				  					$("#householdInformationForm_permanentResidentDistrictSubdivision").select2();
				  					$("#householdInformationForm_permanentResidentDistrictSubdivision").select2().select2('val', '');
				  		 		}
				  		 });
				  	 };
			
					
				 	//setup sub-districts
				 	$("#householdInformationForm_permanentResidentDistrictSubdivision").select2();
				 	<c:if test="${not empty householdInformationForm.permanentResidenceDistrict}">
				 	subDistrictOptions = [
				            			<c:forEach items="${householdInformationForm.permanentResidenceDistrict.districtSubdivisions}" var="element"> 
				            				{
				            					text : "<c:out value='${element.name}'/>",  
				            					id : "<c:out value='${element.externalId}'/>"
				            				},
				            			</c:forEach>
				            		].sort(sortFunction);
			
				            	   $("#householdInformationForm_permanentResidentDistrictSubdivision").select2(
					             			{
					             				data : subDistrictOptions,
					             			}	  
					             	    );
					             	    
					             	    $("#householdInformationForm_permanentResidentDistrictSubdivision").select2().select2('val', '<c:out value='${householdInformationForm.permanentResidentDistrictSubdivision.externalId}'/>');
			
				   	</c:if>
				  	 
				   	 $("#householdInformationForm_countryOfResidence").select2().on("change", function(){
						configureOriginInformationFieldsEditableState();
					 });
				   	
					
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
					
					
					onChangeDislocatedFromPermanentResidence();
				});
			</script>
			