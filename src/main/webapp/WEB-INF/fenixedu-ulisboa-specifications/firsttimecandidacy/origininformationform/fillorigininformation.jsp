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
	<h1><spring:message code="label.firstTimeCandidacy.fillOriginInformation" />
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
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.schoolLevel"/></div> 

<div class="col-sm-4">
	<select id="originInformationForm_schoolLevel" class="form-control" name="schoollevel">
		<c:forEach items="${schoolLevelValues}" var="field">
			<option value='<c:out value='${field}'/>'><c:out value='${field.localizedName}'/></option>
		</c:forEach>
	</select>
	<script>
		$("#originInformationForm_schoolLevel").val('<c:out value='${not empty param.schoollevel ? param.schoollevel : originInformationForm.schoolLevel }'/>');
	</script>	
</div>
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.otherSchoolLevel"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_otherSchoolLevel" class="form-control" type="text" name="otherschoollevel"  value='<c:out value='${not empty param.otherschoollevel ? param.otherschoollevel : originInformationForm.otherSchoolLevel }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.conclusionGrade"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_conclusionGrade" class="form-control" type="text" name="conclusiongrade"  value='<c:out value='${not empty param.conclusiongrade ? param.conclusiongrade : originInformationForm.conclusionGrade }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.degreeDesignation"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_degreeDesignation" class="form-control" type="text" name="degreedesignation"  value='<c:out value='${not empty param.degreedesignation ? param.degreedesignation : originInformationForm.degreeDesignation }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.conclusionYear"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_conclusionYear" class="form-control" type="text" name="conclusionyear"  value='<c:out value='${not empty param.conclusionyear ? param.conclusionyear : originInformationForm.conclusionYear }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.birthYear"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_birthYear" class="form-control" type="text" name="birthyear"  value='<c:out value='${not empty param.birthyear ? param.birthyear : originInformationForm.birthYear }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.institution"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_institution" class="form-control" type="text" name="institution"  value='<c:out value='${not empty param.institution ? param.institution : originInformationForm.institution }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.institutionName"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_institutionName" class="form-control" type="text" name="institutionname"  value='<c:out value='${not empty param.institutionname ? param.institutionname : originInformationForm.institutionName }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.raidesDegreeDesignation"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_raidesDegreeDesignation" class="form-control" type="text" name="raidesdegreedesignation"  value='<c:out value='${not empty param.raidesdegreedesignation ? param.raidesdegreedesignation : originInformationForm.raidesDegreeDesignation }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.countryWhereFinishedPreviousCompleteDegree"/></div> 

<div class="col-sm-10">
	<input id="originInformationForm_countryWhereFinishedPreviousCompleteDegree" class="form-control" type="text" name="countrywherefinishedpreviouscompletedegree"  value='<c:out value='${not empty param.countrywherefinishedpreviouscompletedegree ? param.countrywherefinishedpreviouscompletedegree : originInformationForm.countryWhereFinishedPreviousCompleteDegree }'/>' />
</div>	
</div>		
<div class="form-group row">
<div class="col-sm-2 control-label"><spring:message code="label.OriginInformationForm.highSchoolType"/></div> 

<div class="col-sm-4">
	<select id="originInformationForm_highSchoolType" class="form-control" name="highschooltype">
		<c:forEach items="${highSchoolTypeValues}" var="field">
			<option value='<c:out value='${field}'/>'><c:out value='${field}'/></option>
		</c:forEach>
	</select>
	<script>
		$("#originInformationForm_highSchoolType").val('<c:out value='${not empty param.highschooltype ? param.highschooltype : originInformationForm.highSchoolType }'/>');
	</script>	
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
