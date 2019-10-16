<!--
 /**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoe@qub-it.com 
 *
 * 
 * This file is part of FenixEdu Specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
 -->
<%@page import="org.fenixedu.ulisboa.specifications.ui.ectsgradingtable.EctsGradingTableBackofficeController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/zs/dataTables.bootstrap.min.css" />
<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />
<link href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/omnis.css" rel="stylesheet" />

<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>

${portal.toolkit()}



<div class="page-header">
	<h1><spring:message code="label.gradingTables.settings.edit" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}<%= EctsGradingTableBackofficeController.SEARCH_URL%>${selectedYear.externalId}" ><spring:message code="label.event.back" /></a></div>
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
				<div class="col-sm-2 control-label"><spring:message code="label.gradingTables.settings.minSampleSize"/></div>
				<div class="col-sm-10">
					<input id="gradingTableSettings_minSampleSize" class="form-control" type="number" placeholder="Min: 30" min="30" step="1" name="minSampleSize"  value='<c:out value='${not empty param.minSampleSize ? param.minSampleSize : gradingTableSettings.minSampleSize }'/>'  required />
				</div>	
			</div>		
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.gradingTables.settings.minPastYears"/></div>
				<div class="col-sm-10">
					<input id="gradingTableSettings_minPastYears" class="form-control" type="number" placeholder="Min: 3" min="3" max="5" step="1" name="minPastYears" value='<c:out value="${not empty param.minPastYears ? param.minPastYears : gradingTableSettings.minPastYears }"/>' required /> 
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label"><spring:message code="label.gradingTables.settings.maxPastYears"/></div>
				<div class="col-sm-10">
					<input id="gradingTableSettings_maxPastYears" class="form-control" type="number" placeholder="Max: 5" min="3" max="5" step="1" name="maxPastYears" value='<c:out value="${not empty param.maxPastYears ? param.maxPastYears : gradingTableSettings.maxPastYears }"/>' required /> 
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.gradingTables.settings.degreeTypes" />
				</div>
				<div class="col-sm-2">
					<select id="gradingTableSettings_degreeTypes" class="js-example-basic-single" name="degreeTypes" multiple="multiple">
					</select>
					<script>
						var degree_type_options = [
							<c:forEach items="${degreeTypeOptions}" var="element">
								{
									text :"<c:out value='${element.name.content}'/>",
									id : "<c:out value='${element.externalId}'/>"
								},
							</c:forEach>
						];
						
						var selectedOptions = [
							<c:forEach items="${gradingTableSettings.applicableDegreeTypesSet}" var="element" varStatus="loop"><c:out value='${element.externalId}'/>    ${!loop.last ? ',' : ''}	</c:forEach>
						];
						//Init Select2Options
						initSelect2Multiple("#gradingTableSettings_degreeTypes",degree_type_options, selectedOptions);
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
