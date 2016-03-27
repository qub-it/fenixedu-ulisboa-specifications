<%@page import="org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo.ExtendedDegreeInfoController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />
<link rel="stylesheet" href="${datatablesCssUrl}" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />
<link href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />

${portal.angularToolkit()}

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<script type="text/javascript" src="${datatablesUrl}"></script>
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>
<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>



<div ng-app="angularAppExtendedDegreeInfo" id="manageExtendedDegreeInfo" ng-controller="ExtendedDegreeInfoController">
      
    <%-- TITLE --%>
	<div class="page-header">
	    <h1>
	        <spring:message
	            code="label.extendedDegreeInformation.backoffice.title" />
	        <small></small>
	    </h1>
	</div>
	
	<%-- ERROR MSGS --%>
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
	
	<form name="form" method="post" class="form-horizontal" action="#">
	
		<input type="hidden" name="postback" value='#' />
        <input name="bean" type="hidden" value="{{ object }}" />
	
		<%-- SEARCH-BY-YEAR-AND-DEGREE --%>
		<div class="panel panel-default">
	        <div class="panel-heading">
	            <h3 class="panel-title">
	        		<spring:message code="label.extendedDegreeInformation.backoffice.search" />
	        	</h3>
	    	</div>
	    	<div class="panel panel-body">
	    	
		        <div class="form-group row">
		        	<div class="col-sm-1"></div>
					<div class="col-sm-1 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.executionYear"/></div>
					<div class="col-sm-6">
						<ui-select id="extendedDegreeInformation_executionYear" class="" name="executionYear" ng-model="$parent.object.executionYear" on-select="onDegreeTypeChange($item, $model)" theme="bootstrap" ng-disabled="disabled" >
							<ui-select-match allow-clear="false">{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="executionYear.id as executionYear in object.executionYearOptions | filter: $select.search">
								<span ng-bind-html="executionYear.text | highlight: $select.search"></span>
							</ui-select-choices>
						</ui-select>				
					</div>
				</div>
				
				<div class="form-group row">
		        	<div class="col-sm-1"></div>
					<div class="col-sm-1 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.degree"/></div>
					<div class="col-sm-6">
						<ui-select id="extendedDegreeInformation_degree" class="" name="degree" ng-model="$parent.object.degree" on-select="onDegreeTypeChange($item, $model)" theme="bootstrap" ng-disabled="disabled" >
							<ui-select-match allow-clear="false">{{$select.selected.text}}</ui-select-match>
							<ui-select-choices repeat="degree.id as degree in object.degreeOptions | filter: $select.search">
								<span ng-bind-html="degree.text | highlight: $select.search"></span>
							</ui-select-choices>
						</ui-select>				
					</div>
				</div>
				
				<div class="form-group row">
		        	<div class="col-sm-1"></div>
					<div class="col-sm-1 control-label"></div>
					<div class="col-sm-6">
						<button ng-click="search()" class="btn btn-default"><spring:message code="label.search" /></button>
					</div>
				</div>
				
			</div>
	    </div>
	    
	    <%-- EXTENDED-DEGREE-INFO-FORM --%>
	    <div class="panel panel-primary">
	        <div class="panel-heading">
	            <h3 class="panel-title panel-title-with-actions">
	        		<span class="pane-title-header"><spring:message code="label.extendedDegreeInformation.backoffice.edit" /></span> <span ng-bind="year"></span> <span ng-bind="degreeType"></span> <span ng-bind="object.degreeAcron"></span>
	        	</h3>
	    	</div>
	    	<div class="panel panel-body">
	    		
	    		<div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.name" /></div>	
	                <div class="col-sm-7">
	                	<input type="text" id="extendedDegreeInformation_name_read" class="form-control form-control-read-only" ng-show="!editMode['name']" ng-readonly="true" ng-model="name" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />" />
	                	<input type="text" id="extendedDegreeInformation_name" class="form-control" ng-show="editMode['name']" ng-readonly="false" ng-localized-string="object.name" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />" />
	                </div>
	                <div class="col-sm-3">
		        		<a href="" class="btn btn-xs btn-default" ng-show="editMode['name']" ng-click="toggleEditMode('name')" data-toggle="tooltip" title="<spring:message code="label.cancel" />"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
		        		<button class="btn btn-xs btn-primary" ng-show="editMode['name']" ng-click="edit()" data-toggle="tooltip" title="<spring:message code="label.save" />"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></button>
		        		<a href="" class="btn btn-xs btn-default" ng-show="!editMode['name']" ng-click="toggleEditMode('name')" data-toggle="tooltip" title="<spring:message code="label.edit" />"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a>
		        	</div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.description" /></div>	
	                <div class="col-sm-7">
	                	<textarea id="extendedDegreeInformation_description_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode['description']" ng-readonly="true" ng-model="description" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_description" rows="6" class="form-control" ng-show="editMode['description']" ng-readonly="false" ng-localized-string="object.description" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	                <div class="col-sm-3">
		        		<a href="" class="btn btn-xs btn-default" ng-show="editMode['description']" ng-click="toggleEditMode('description')" data-toggle="tooltip" title="<spring:message code="label.cancel" />"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
		        		<button class="btn btn-xs btn-primary" ng-show="editMode['description']" ng-click="edit()" data-toggle="tooltip" title="<spring:message code="label.save" />"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></button>
		        		<a href="" class="btn btn-xs btn-default" ng-show="!editMode['description']" ng-click="toggleEditMode('description')" data-toggle="tooltip" title="<spring:message code="label.edit" />"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a>
		        	</div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.history" /></div>	
	                <div class="col-sm-7">
	                	<textarea id="extendedDegreeInformation_history_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode['history']" ng-readonly="true" ng-model="history" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_history" rows="6" class="form-control" ng-show="editMode['history']" ng-readonly="false" ng-localized-string="object.history" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	                <div class="col-sm-3">
		        		<a href="" class="btn btn-xs btn-default" ng-show="editMode['history']" ng-click="toggleEditMode('history')" data-toggle="tooltip" title="<spring:message code="label.cancel" />"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
		        		<button class="btn btn-xs btn-primary" ng-show="editMode['history']" ng-click="edit()" data-toggle="tooltip" title="<spring:message code="label.save" />"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></button>
		        		<a href="" class="btn btn-xs btn-default" ng-show="!editMode['history']" ng-click="toggleEditMode('history')" data-toggle="tooltip" title="<spring:message code="label.edit" />"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a>
		        	</div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.objectives" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_objectives_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="objectives" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_objectives" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.objectives" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.designedFor" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_designedFor_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="designedFor" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_designedFor" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.designedFor" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.professionalExits" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_professionalExits_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="professionalExits" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_professionalExits" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.professionalExits" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.operationalRegime" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_operationalRegime_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="operationalRegime" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_operationalRegime" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.operationalRegime" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.gratuity" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_gratuity_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="gratuity" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_gratuity" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.gratuity" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>

				<div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.additionalInfo" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_additionalInfo_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="additionalInfo" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_additionalInfo" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.additionalInfo" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.links" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_links_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="links" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_links" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.links" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>

				<div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.testIngression" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_testIngression_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="testIngression" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_testIngression" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.testIngression" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>

				<div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.classifications" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_classifications_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="classifications" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_classifications" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.classifications" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>

				<div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.accessRequisites" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_accessRequisites_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="accessRequisites" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_accessRequisites" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.accessRequisites" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>

				<div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.candidacyDocuments" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_candidacyDocuments_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="candidacyDocuments" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_candidacyDocuments" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.candidacyDocuments" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>

				<div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.driftsInitial" /></div>	
	                <div class="col-sm-7">
	                	<input id="extendedDegreeInformation_driftsInitial" class="form-control" type="number" ng-readonly="!editMode['driftsInitial']" ng-model="object.driftsInitial" />
	                </div>
	                <div class="col-sm-3">
		        		<a href="" class="btn btn-xs btn-default" ng-show="editMode['driftsInitial']" ng-click="toggleEditMode('driftsInitial')" data-toggle="tooltip" title="<spring:message code="label.cancel" />"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
		        		<button class="btn btn-xs btn-primary" ng-show="editMode['driftsInitial']" ng-click="edit()" data-toggle="tooltip" title="<spring:message code="label.save" />"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></button>
		        		<a href="" class="btn btn-xs btn-default" ng-show="!editMode['driftsInitial']" ng-click="toggleEditMode('driftsInitial')" data-toggle="tooltip" title="<spring:message code="label.edit" />"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a>
		        	</div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.driftsFirst" /></div>	
	                <div class="col-sm-10">
	                	<input id="extendedDegreeInformation_driftsFirst" class="form-control" type="number" ng-readonly="!editMode" ng-model="object.driftsFirst" />
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.driftsSecond" /></div>	
	                <div class="col-sm-10">
	                	<input id="extendedDegreeInformation_driftsSecond" class="form-control" type="number" ng-readonly="!editMode" ng-model="object.driftsSecond" />
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.markMin" /></div>	
	                <div class="col-sm-10">
	                	<input id="extendedDegreeInformation_markMin" class="form-control" type="number" ng-readonly="!editMode" ng-model="object.markMin" />
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.markMax" /></div>	
	                <div class="col-sm-10">
	                	<input id="extendedDegreeInformation_markMax" class="form-control" type="number" ng-readonly="!editMode" ng-model="object.markMax" />
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.markAverage" /></div>	
	                <div class="col-sm-10">
	                	<input id="extendedDegreeInformation_markAverage" class="form-control" type="number" ng-readonly="!editMode" ng-model="object.markAverage" />
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.qualificationLevel" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_qualificationLevel_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="qualificationLevel" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_qualificationLevel" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.qualificationLevel" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.recognitions" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_recognitions_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="recognitions" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_recognitions" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.recognitions" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>	            
	    		
				<div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.scientificAreas" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_scientificAreas_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="scientificAreas" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_scientificAreas" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.scientificAreas" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.studyRegime" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_studyRegime_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="studyRegime" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                    <textarea id="extendedDegreeInformation_studyRegime" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.studyRegime" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.studyProgrammeRequirements" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_studyProgrammeRequirements_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="studyProgrammeRequirements" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_studyProgrammeRequirements" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.studyProgrammeRequirements" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.higherEducationAccess" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_higherEducationAccess_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="higherEducationAccess" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_higherEducationAccess" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.higherEducationAccess" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.professionalStatus" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_professionalStatus_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="professionalStatus" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_professionalStatus" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.professionalStatus" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.supplementExtraInformation" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_supplementExtraInformation_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="supplementExtraInformation" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_supplementExtraInformation" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.supplementExtraInformation" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
	            <div class="form-group row">
	                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.supplementOtherSources" /></div>	
	                <div class="col-sm-10">
	                	<textarea id="extendedDegreeInformation_supplementOtherSources_read" rows="6" class="form-control form-control-read-only" ng-show="!editMode" ng-readonly="true" ng-model="supplementOtherSources" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.readMode" />"></textarea>
	                	<textarea id="extendedDegreeInformation_supplementOtherSources" rows="6" class="form-control" ng-show="editMode" ng-readonly="false" ng-localized-string="object.supplementOtherSources" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
	                </div>
	            </div>
	            
			</div>
	    </div>
	    
	</form>
	<div id="onload-marker" ng-show="false" ng-init="onload()"></div>
</div>

<script>
    angular.module('angularAppExtendedDegreeInfo',
            [ 'ngSanitize', 'ui.select', 'bennuToolkit' ]).controller(
            'ExtendedDegreeInfoController', [ '$scope', function($scope) {
            	
                $scope.object = ${extendedDegreeInfoBeanJson};
                $scope.postBack = createAngularPostbackFunction($scope);
                $scope.booleanvalues= [
                  {name: '<spring:message code="label.no"/>', value: false},
                  {name: '<spring:message code="label.yes"/>', value: true}
                ];
                
                $scope.locale = Bennu.locale.tag;
                $scope.fieldIds = [
                    "name",
                    "description",
                    "history",
                    "objectives",
                    "designedFor",
                    "professionalExits",
                    "operationalRegime",
                    "gratuity",
                    "additionalInfo",
                    "links",
                    "testIngression",
                    "classifications",
                    "accessRequisites",
                    "candidacyDocuments",
                    "driftsInitial",
                    "driftsFirst",
                    "driftsSecond",
                    "markMin",
                    "markMax",
                    "markAverage",
                    "qualificationLevel",
                    "recognitions",
                    "scientificAreas",
                	"studyRegime",
                	"studyProgrammeRequirements",
                	"higherEducationAccess",
                	"professionalStatus",
                	"supplementExtraInformation",
                	"supplementOtherSources",
                ];
                $scope.backups = {};
                for (var y = 0; y < $scope.object.executionYearOptions.length; y++) {
                	if ($scope.object.executionYearOptions[y].id === $scope.object.executionYear) {
                		$scope.year = $scope.object.executionYearOptions[y].text
                	}
                }
                $scope.degreeType = $scope.object.degreeType.match(/[A-Z][^\s]*/g).reduce( function (previous, current) {
                	return previous + current.substring(0,3);
                }, "");
                
                $scope.editMode = {};
                for (var id = 0; id < $scope.fieldIds.length; id++) {
                	$scope.editMode[$scope.fieldIds[id]] = false;
        		}
                $scope.toggleEditMode = function (id) {
                	$scope.editMode[id] = !($scope.editMode[id]);
                	if ($scope.editMode[id]) {
                		$scope.backups[id] = $scope.object[id];
                	} else {
                		$scope.object[id] = $scope.backups[id];
                	}
                	// God forgive me, for I have sinned...
                	$('#extendedDegreeInformation_' + id + ' ~ .bennu-localized-string-textArea, #extendedDegreeInformation_' + id + ' ~ .bennu-localized-string-input-group').toggle();
                };
                
                for (var id = 0; id < $scope.fieldIds.length; id++) {
        			$scope[$scope.fieldIds[id]] = $scope.object[$scope.fieldIds[id]] ? $scope.object[$scope.fieldIds[id]][$scope.locale] : undefined;
        		}
//              $scope.$watch('object.scientificAreas', function(value) {
//              	$scope.object.scientificAreas[$scope.locale];
//              });
                
                $scope.search = function () {
                	$('form').attr('action', '${pageContext.request.contextPath}<%= ExtendedDegreeInfoController.SEARCH_URL %>');
                };
                
                $scope.edit = function () {
                	$('form').attr('action', '${pageContext.request.contextPath}<%= ExtendedDegreeInfoController.UPDATE_URL %>');
                }
                <%--
                $scope.addSlotEntry = function(model) {
                    if (angular.isUndefinedOrNull($scope.serviceRequestSlot)) {
                        return;
                    }
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.ADD_PROPERTY_URL%>${serviceRequestType.externalId}';
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $('form[name="form"]').find('input[name="required"]').attr('value', false);
                    $('form[name="form"]').find('input[name="orderNumber"]').attr('value', $scope.object.serviceRequestSlotEntries.length);
                    $('form[name="form"]').find('input[name="serviceRequestSlot"]').attr('value', $scope.serviceRequestSlot);
                    $scope.postBack(model);
                    
                    $scope.serviceRequestSlot = undefined;
                };
                $scope.addDefaultSlotEntries = function(model) {
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.ADD_DEFAULT_PROPERTIES_URL%>${serviceRequestType.externalId}';
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $scope.postBack(null);
                }
                $scope.deleteEntry = function(slotEntry, model) {
                	url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.DELETE_PROPERTY_URL%>${serviceRequestType.externalId}/' + slotEntry.entry;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $('#deleteModal').modal('toggle');
                }
                $scope.submitDeleteEntry = function() {
                    $('#deleteModal').modal('toggle');                	
                    $scope.postBack(null);                 
                };
                $scope.moveUp = function(slotEntry, model) {
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.MOVE_UP_PROPERTY_URL%>${serviceRequestType.externalId}/' + slotEntry.entry;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $scope.postBack(model);                 
                }
                $scope.moveDown = function(slotEntry, model) {
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.MOVE_DOWN_PROPERTY_URL%>${serviceRequestType.externalId}/' + slotEntry.entry;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $scope.postBack(model);                 
                }
                $scope.changeRequired = function(slotEntry, model) {
                    url = '${pageContext.request.contextPath}<%= ServiceRequestTypeController.UPDATE_PROPERTY_URL%>${serviceRequestType.externalId}/' + slotEntry.entry;
                    $('form[name="form"]').find('input[name="postback"]').attr('value', url);
                    $('form[name="form"]').find('input[name="required"]').attr('value', slotEntry.required);
                	$scope.postBack(model);
                }
                --%>
                
                $scope.onload = function () {
                	$('.bennu-localized-string-textArea, .bennu-localized-string-input-group').hide();
                };
	} ]);
</script>

<style type="text/css">
	.panel-title-with-actions {
		display: inline-block;
		line-height: 3rem;
		
	}
	.panel-title-with-actions > .pane-title-header {
		font-weight: 800;
		margin-right: 2rem;
	}
	.panel-heading-actions {
		display: inline;
		float: right;
	}
	textarea::-webkit-input-placeholder { /* Chrome/Opera/Safari */
		font-style: italic;
	}
	textarea::-moz-placeholder { /* Firefox 19+ */
		font-style: italic;
	}
	textarea:-ms-input-placeholder { /* IE 10+ */
		font-style: italic;
	}
	textarea:-moz-placeholder { /* Firefox 18- */
		font-style: italic;
	}
</style>