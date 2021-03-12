<%@page
    import="org.fenixedu.ulisboa.specifications.ui.degrees.extendedinfo.ExtendedDegreeInformationController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

<spring:url var="datatablesUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
    value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl"
    value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%-- ${portal.toolkit()} --%>

<link
    href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
<link
    href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link
    href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css"
    rel="stylesheet" />
<script
    src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript"
    src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script
    src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>

<script
    src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css"
    href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script
    src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>


<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message
            code="label.extendedDegreeInformation.backoffice.title.read" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp; 
    <a class="" href="${pageContext.request.contextPath}<%= ExtendedDegreeInformationController.READ_URL %>${degreeInfo.externalId}">
        <spring:message code="label.event.back" />
    </a>
    &nbsp; 
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

<script>
    angular.module('angularAppExtendedDegreeInformationController',
            [ 'ngSanitize', 'ui.select', 'bennuToolkit' ]).controller(
            'ExtendedDegreeInformationController', [ '$scope', function($scope) {

                $scope.object = ${extendedDegreeInfoBeanJson};
                $scope.booleanvalues= [
                  {name: '<spring:message code="label.no"/>', value: false},
                  {name: '<spring:message code="label.yes"/>', value: true}
                ];
            } ]);
</script>

<form method="post" class="form-horizontal"
    ng-app="angularAppExtendedDegreeInformationController"
    ng-controller="ExtendedDegreeInformationController"
    action='${pageContext.request.contextPath}<%= ExtendedDegreeInformationController.UPDATE_URL %>${degreeInfo.externalId}'>
    
    <input name="bean" type="hidden" value="{{ object }}" />
    
    <div class="panel panel-default">
        <div class="panel-body">

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.name" /></div> 
                <div class="col-sm-8">
                    <input type="text" id="extendedDegreeInformation_name" class="form-control" ng-readonly="false" bennu-localized-string="object.name" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />" />
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.description" /></div>  
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_description" rows="6" class="form-control" ng-show="editMode['description']" ng-readonly="false" bennu-localized-string="object.description" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.history" /></div>  
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_history" rows="6" class="form-control" ng-readonly="false" bennu-localized-string="object.history" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.objectives" /></div>   
                <div class="col-sm-8">
                    <div>
                        <textarea toolbar="size,style,lists,align,links,table,undo,fullscreen,source" 
                            id="extendedDegreeInformation_objectives" rows="6" class="form-control" ng-readonly="false" bennu-localized-string="object.objectives" ng-html-editor placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>                           
                    </div>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.designedFor" /></div>  
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_designedFor" rows="6" class="form-control" ng-show="editMode['designedFor']" ng-readonly="false" bennu-localized-string="object.designedFor" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.professionalExits" /></div>    
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_professionalExits" rows="6" class="form-control" ng-show="editMode['professionalExits']" ng-readonly="false" bennu-localized-string="object.professionalExits" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.operationalRegime" /></div>    
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_operationalRegime" rows="6" class="form-control" ng-show="editMode['operationalRegime']" ng-readonly="false" bennu-localized-string="object.operationalRegime" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.gratuity" /></div> 
                <div class="col-sm-8">
                    <div>
                        <textarea toolbar="size,style,lists,align,links,table,undo,fullscreen,source" 
                            id="extendedDegreeInformation_gratuity" rows="6" class="form-control" ng-readonly="false" bennu-localized-string="object.gratuity" ng-html-editor placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>                           
                    </div>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.additionalInfo" /></div>   
                <div class="col-sm-8">
                    <div>
                        <textarea toolbar="size,style,lists,align,links,table,undo,fullscreen,source" 
                            id="extendedDegreeInformation_additionalInfo" rows="6" class="form-control" ng-readonly="false" bennu-localized-string="object.additionalInfo" ng-html-editor placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>                           
                    </div>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.links" /></div>    
                <div class="col-sm-8">
                        <textarea toolbar="size,style,lists,align,links,table,undo,fullscreen,source" 
                            id="extendedDegreeInformation_links" rows="6" class="form-control" ng-readonly="false" bennu-localized-string="object.links" ng-html-editor placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>                           
                    </div>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.testIngression" /></div>   
                <div class="col-sm-8">
                    <div>
                        <textarea toolbar="size,style,lists,align,links,table,undo,fullscreen,source" 
                            id="extendedDegreeInformation_testIngression" rows="6" class="form-control" ng-readonly="false" bennu-localized-string="object.testIngression" ng-html-editor placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>                           
                    </div>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.classifications" /></div>  
                <div class="col-sm-8">
                    <div>
                        <textarea toolbar="size,style,lists,align,links,table,undo,fullscreen,source" 
                            id="extendedDegreeInformation_classifications" rows="6" class="form-control" ng-readonly="false" bennu-localized-string="object.classifications" ng-html-editor placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>                           
                    </div>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.accessRequisites" /></div> 
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_accessRequisites" rows="6" class="form-control" ng-show="editMode['accessRequisites']" ng-readonly="false" bennu-localized-string="object.accessRequisites" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.candidacyDocuments" /></div>   
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_candidacyDocuments" rows="6" class="form-control" ng-show="editMode['candidacyDocuments']" ng-readonly="false" bennu-localized-string="object.candidacyDocuments" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>

            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.driftsInitial" /></div>    
                <div class="col-sm-8">
                    <input id="extendedDegreeInformation_driftsInitial" class="form-control" type="number" ng-readonly="false" ng-model="object.driftsInitial" />
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.driftsFirst" /></div>  
                <div class="col-sm-8">
                    <input id="extendedDegreeInformation_driftsFirst" class="form-control" type="number" ng-readonly="false" ng-model="object.driftsFirst" />
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.driftsSecond" /></div> 
                <div class="col-sm-8">
                    <input id="extendedDegreeInformation_driftsSecond" class="form-control" type="number" ng-readonly="false" ng-model="object.driftsSecond" />
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.markMin" /></div>  
                <div class="col-sm-8">
                    <input id="extendedDegreeInformation_markMin" class="form-control" type="number" pattern="[0-9]+(\.[0-9][0-9]?[0-9]?)?" min="0" step="0.01" ng-readonly="false" ng-model="object.markMin" />
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.markMax" /></div>  
                <div class="col-sm-8">
                    <input id="extendedDegreeInformation_markMax" class="form-control" type="number" pattern="[0-9]+(\.[0-9][0-9]?[0-9]?)?" min="0" step="0.01" ng-readonly="false" ng-model="object.markMax" />
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.markAverage" /></div>  
                <div class="col-sm-8">
                    <input id="extendedDegreeInformation_markAverage" class="form-control" type="number" pattern="[0-9]+(\.[0-9][0-9]?[0-9]?)?" min="0" step="0.01" ng-readonly="false" ng-model="object.markAverage" />
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.qualificationLevel" /></div>   
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_qualificationLevel" rows="6" class="form-control" ng-show="editMode['qualificationLevel']" ng-readonly="false" bennu-localized-string="object.qualificationLevel" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.recognitions" /></div> 
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_recognitions" rows="6" class="form-control" ng-show="editMode['recognitions']" ng-readonly="false" bennu-localized-string="object.recognitions" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>              
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.prevailingScientificArea" /></div> 
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_prevailingScientificArea" rows="6" class="form-control" ng-show="editMode['prevailingScientificArea']" ng-readonly="false" bennu-localized-string="object.prevailingScientificArea" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.scientificAreas" /></div>  
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_scientificAreas" rows="6" class="form-control" ng-show="editMode['scientificAreas']" ng-readonly="false" bennu-localized-string="object.scientificAreas" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.studyProgrammeDuration" /></div>   
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_studyProgrammeDuration" rows="6" class="form-control" ng-show="editMode['studyProgrammeDuration']" ng-readonly="false" bennu-localized-string="object.studyProgrammeDuration" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.studyRegime" /></div>  
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_studyRegime" rows="6" class="form-control" ng-show="editMode['studyRegime']" ng-readonly="false" bennu-localized-string="object.studyRegime" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.studyProgrammeRequirements" /></div>   
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_studyProgrammeRequirements" rows="6" class="form-control" ng-show="editMode['studyProgrammeRequirements']" ng-readonly="false" bennu-localized-string="object.studyProgrammeRequirements" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.higherEducationAccess" /></div>    
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_higherEducationAccess" rows="6" class="form-control" ng-show="editMode['higherEducationAccess']" ng-readonly="false" bennu-localized-string="object.higherEducationAccess" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.professionalStatus" /></div>   
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_professionalStatus" rows="6" class="form-control" ng-show="editMode['professionalStatus']" ng-readonly="false" bennu-localized-string="object.professionalStatus" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.supplementExtraInformation" /></div>   
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_supplementExtraInformation" rows="6" class="form-control" ng-show="editMode['supplementExtraInformation']" ng-readonly="false" bennu-localized-string="object.supplementExtraInformation" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>
            
            <div class="form-group row">
                <div class="col-sm-2 control-label"><spring:message code="label.extendedDegreeInformation.backoffice.supplementOtherSources" /></div>   
                <div class="col-sm-8">
                    <textarea id="extendedDegreeInformation_supplementOtherSources" rows="6" class="form-control" ng-show="editMode['supplementOtherSources']" ng-readonly="false" bennu-localized-string="object.supplementOtherSources" placeholder="<spring:message code="label.extendedDegreeInformation.backoffice.noDataDefined.writeMode" />"></textarea>
                </div>
            </div>

        </div>
        <div class="panel-footer">
            <input type="submit" class="btn btn-default" role="button"
                value="<spring:message code="label.submit" />" />
        </div>
    </div>
</form>
<div id="onload-marker" ng-show="false" ng-init="onload()"></div>

<script>
$(document).ready(function() {
	
});
</script>
