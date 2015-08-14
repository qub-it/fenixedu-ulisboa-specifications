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
					<spring:message
						code="label.HouseholdInformationForm.motherSchoolLevel" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_motherSchoolLevel"
						class="form-control" name="motherSchoolLevel">
						<option value=""><spring:message code="label.choose.one"/></option>
						<c:forEach items="${schoolLevelValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#householdInformationForm_motherSchoolLevel").val('<c:out value='${not empty param.motherschoollevel ? param.motherschoollevel : householdInformationForm.motherSchoolLevel }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.HouseholdInformationForm.motherProfessionType" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_motherProfessionType"
						class="form-control" name="motherProfessionType">
						<option value=""><spring:message code="label.choose.one"/></option>
						<c:forEach items="${professionTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#householdInformationForm_motherProfessionType").val('<c:out value='${not empty param.motherprofessiontype ? param.motherprofessiontype : householdInformationForm.motherProfessionType }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.HouseholdInformationForm.motherProfessionalCondition" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_motherProfessionalCondition"
						class="form-control" name="motherProfessionalCondition">
						<option value=""><spring:message code="label.choose.one"/></option>
						<c:forEach items="${professionalConditionValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#householdInformationForm_motherProfessionalCondition").val('<c:out value='${not empty param.motherprofessionalcondition ? param.motherprofessionalcondition : householdInformationForm.motherProfessionalCondition }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.HouseholdInformationForm.fatherSchoolLevel" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_fatherSchoolLevel"
						class="form-control" name="fatherSchoolLevel">
						<option value=""><spring:message code="label.choose.one"/></option>
						<c:forEach items="${schoolLevelValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#householdInformationForm_fatherSchoolLevel").val('<c:out value='${not empty param.fatherschoollevel ? param.fatherschoollevel : householdInformationForm.fatherSchoolLevel }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.HouseholdInformationForm.fatherProfessionType" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_fatherProfessionType"
						class="form-control" name="fatherProfessionType">
						<option value=""><spring:message code="label.choose.one"/></option>
						<c:forEach items="${professionTypeValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#householdInformationForm_fatherProfessionType").val('<c:out value='${not empty param.fatherprofessiontype ? param.fatherprofessiontype : householdInformationForm.fatherProfessionType }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.HouseholdInformationForm.fatherProfessionalCondition" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_fatherProfessionalCondition"
						class="form-control" name="fatherProfessionalCondition">
						<option value=""><spring:message code="label.choose.one"/></option>
						<c:forEach items="${professionalConditionValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field.localizedName}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#householdInformationForm_fatherProfessionalCondition").val('<c:out value='${not empty param.fatherprofessionalcondition ? param.fatherprofessionalcondition : householdInformationForm.fatherProfessionalCondition }'/>');
					</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.HouseholdInformationForm.householdSalarySpan" />
				</div>

				<div class="col-sm-4">
					<select id="householdInformationForm_householdSalarySpan" class="form-control" name="householdSalarySpan">
						<option value=""><spring:message code="label.choose.one"/></option>
						<c:forEach items="${salarySpanValues}" var="salarySpanValue">
							<option value='<c:out value='${salarySpanValue.externalId}'/>'><c:out value='${salarySpanValue.description.content}' /></option>
						</c:forEach>
					</select>
					<script>
						$("#householdInformationForm_householdSalarySpan").val('<c:out value='${not empty param.householdsalaryspan ? param.householdsalaryspan : householdInformationForm.householdSalarySpan.externalId }'/>');
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
