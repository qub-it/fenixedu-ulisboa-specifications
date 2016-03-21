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
	        	<div class="panel-heading-actions">
	        		<a href="" class="btn btn-xs btn-default" ng-show="editMode" ng-click="toggleEditMode()" data-toggle="tooltip" title="<spring:message code="label.cancel" />"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
	        		<button class="btn btn-xs btn-default" ng-show="editMode" ng-click="edit()" data-toggle="tooltip" title="<spring:message code="label.save" />"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></button>
	        		<a href="" class="btn btn-xs btn-default" ng-show="!editMode" ng-click="toggleEditMode()" data-toggle="tooltip" title="<spring:message code="label.edit" />"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a>
	        	</div>
	    	</div>
	    	<div class="panel panel-body">
	    					
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
                
                $scope.editMode = false;
                $scope.toggleEditMode = function () {
                	var editMode = $scope.editMode;
                	$scope.editMode = !editMode;
                	if ($scope.editMode) {
                		for (var id = 0; id < $scope.fieldIds.length; id++) {
                			$scope.backups[$scope.fieldIds[id]] = $scope.object[$scope.fieldIds[id]];
                		}
                	} else {
                		for (var id = 0; id < $scope.fieldIds.length; id++) {
                			$scope.object[$scope.fieldIds[id]] = $scope.backups[$scope.fieldIds[id]];
                		}
                	}
                	// God forgive me, for I have sinned...
                	$('.form-control-read-only ~ .bennu-localized-string-textArea').toggle();
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
                	$('.bennu-localized-string-textArea').hide();
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