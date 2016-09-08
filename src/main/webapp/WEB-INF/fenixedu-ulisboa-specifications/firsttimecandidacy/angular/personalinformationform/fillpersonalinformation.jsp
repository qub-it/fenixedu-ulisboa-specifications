<%@page import="org.fenixedu.academic.FenixEduAcademicConfiguration"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo.PersonalInformationFormController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
${portal.angularToolkit()}
<%--${portal.toolkit()}--%>

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>

<script src="${pageContext.request.contextPath}/webjars/angular-sanitize/1.3.11/angular-sanitize.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.css" />
<script src="${pageContext.request.contextPath}/webjars/angular-ui-select/0.11.2/select.min.js"></script>

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message
			code="label.firstTimeCandidacy.fillPersonalInformation" />
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


<script>
angular.module('angularApp', ['ngSanitize', 'ui.select', 'bennuToolkit']).controller('angularController', ['$scope', function($scope) {

    $scope.object= angular.fromJson('${personalInformationFormJson}');
    $scope.postBack = createAngularPostbackFunction($scope);
    
    $scope.booleanvalues = [
                            { name : '<spring:message code="label.no"/>', value : false },
                            { name : '<spring:message code="label.yes"/>', value : true } 
                    ];
                    
    $scope.onFirstOptionInstitutionChange = function(institution, model) {
	    $scope.object.firstOptionDegreeDesignationValues = undefined;
	    $scope.postBack(model);
    };
    $scope.onFirstOptionInstitutionRefresh = function(institution, namePart, model) {
        if(namePart.length <= 3 || namePart === $scope.object.institutionNamePart) {
            return;
        }
        $scope.object.institutionNamePart = namePart;
        $scope.$apply();  
        $scope.postBack(model);
    };
    $scope.onFirstOptionDegreeDesignationRefresh = function(degreeDesignation, namePart, model) {
        if(namePart.length <= 3 || namePart === $scope.object.degreeNamePart) {
            return;
        }
        $scope.object.degreeNamePart = namePart;
        $scope.$apply();  
        $scope.postBack(model);
    };
    $scope.submitForm = function() {
       $('form').submit();
    };
}]);
</script>

<form name='form' method="post" class="form-horizontal" ng-app="angularApp" ng-controller="angularController"
     action="${pageContext.request.contextPath}${controllerURL}/fill">

    <input type="hidden" name="postback"
        value='${pageContext.request.contextPath}${controllerURL}/fillPostback' />
        
    <input name="bean" type="hidden" value="{{ object }}" />

	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.name" />
				</label>

				<div class="col-sm-10">
					<div class="form-control-static"><c:out value='${not empty param.name ? param.name : personalInformationForm.name }' /></div>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.username" />
				</label>

				<div class="col-sm-10">
					<div class="form-control-static"><c:out value='${not empty param.username ? param.username : personalInformationForm.username }' /></div>
				</div>
			</div>
			
            <c:if test="${partial || not personalInformationForm.isForeignStudent}">
                <div class="form-group row">
                    <label class="col-sm-2 control-label">
                        <spring:message code="label.PersonalInformationForm.idDocumentType" />
                    </label>
    
                    <div class="col-sm-10">
                        <div class="form-control-static"><c:out value='${personalInformationForm.idDocumentType.localizedName }' /></div>
                    </div>
                </div>
    			<div class="form-group row">
    				<label class="col-sm-2 control-label">
    					<spring:message code="label.PersonalInformationForm.documentIdNumber" />
    				</label>
    
    				<div class="col-sm-10">
    					<div class="form-control-static"><c:out value='${personalInformationForm.documentIdNumber}' /></div>
    				</div>
    			</div>
            </c:if>
			
		<c:if test="${identityCardExtraDigitRequired}">
			<div class="form-group row">
				<label for="personalInformationForm_identificationDocumentSeriesNumber" class="col-sm-2 control-label required-field">
					<spring:message
						code="label.PersonalInformationForm.identificationDocumentSeriesNumber" />
                    <a class="" href="#" data-toggle="modal" data-target="#showExtraDigitsImages">
                        <span class="glyphicon glyphicon-question-sign" aria-hidden="true"></span>
                    </a>
				</label>

				<div class="col-sm-10">
					<input id="personalInformationForm_identificationDocumentSeriesNumber"
						class="form-control" type="text" ng-model="object.identificationDocumentSeriesNumber" name="identificationDocumentSeriesNumber"
                        placeholder="<spring:message code='label.PersonalInformationForm.extraDigit.more.help'/>"
						value='<c:out value='${not empty param.identificationDocumentSeriesNumber ? param.identificationDocumentSeriesNumber : personalInformationForm.identificationDocumentSeriesNumber }'/>' />
				</div>
			</div>
		</c:if>
			
	    <c:if test="${not partial}">
			<div class="form-group row">
				<label class="col-sm-2 control-label">
					<spring:message code="label.PersonalInformationForm.gender" />
				</label>

				<div class="col-sm-4">
					<div class="form-control-static"><c:out value='${not empty param.gender ? param.gender : personalInformationForm.gender.localizedName }' /></div>
				</div>
			</div>
			<c:if test="${personalInformationForm.isForeignStudent}">
				<div class="form-group row">
					<label class="col-sm-2 control-label required-field">
						<spring:message code="label.PersonalInformationForm.documentIdNumber" />
					</label>
	
					<div class="col-sm-10">
						<input id="personalInformationForm_documentIdNumber" class="form-control" type="text" ng-model="object.documentIdNumber" name="documentIdNumber" required title="<spring:message code="label.field.required"/>"
							value='${not empty param.documentidnumber ? param.documentidnumber : personalInformationForm.documentIdNumber }'/>
					</div>
				</div>
				<div class="form-group row">
					<label class="col-sm-2 control-label required-field">
						<spring:message code="label.PersonalInformationForm.idDocumentType" />
					</label>
	
					<div class="col-sm-4">
                        <ui-select  id="personalInformationForm_idDocumentType" name="type" ng-model="$parent.object.idDocumentType" theme="bootstrap">
                            <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                            <ui-select-choices  repeat="type.id as type in object.idDocumentTypeValues | filter: $select.search">
                                <span ng-bind-html="type.text | highlight: $select.search"></span>
                            </ui-select-choices> 
                        </ui-select>     
					</div>
				</div>
			</c:if>
			
			<div class="form-group row">
				<label for="personalInformationForm_documentIdEmissionLocation" class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.documentIdEmissionLocation" />
				</label>

				<div class="col-sm-10">
					<input id="personalInformationForm_documentIdEmissionLocation"
						class="form-control" type="text" ng-model="object.documentIdEmissionLocation" name="documentIdEmissionLocation"
						value='<c:out value='${not empty param.documentidemissionlocation ? param.documentidemissionlocation : personalInformationForm.documentIdEmissionLocation }'/>' />
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_documentIdEmissionDate" class="col-sm-2 control-label">
					<spring:message
						code="label.PersonalInformationForm.documentIdEmissionDate" />
				</label>

				<div class="col-sm-4">
                    <input id="personalInformationForm_documentIdEmissionDate" class="form-control" type="text" name="documentIdEmissionDate" bennu-date="object.documentIdEmissionDate" /> 
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_documentIdExpirationDate" class="col-sm-2 control-label required-field">
					<spring:message
						code="label.PersonalInformationForm.documentIdExpirationDate" />
				</label>

				<div class="col-sm-4">
					<input id="personalInformationForm_documentIdExpirationDate" class="form-control" type="text" name="documentIdExpirationDate" bennu-date="object.documentIdExpirationDate" />
				</div>
			</div>
			<div class="form-group row">
				<label for="personalInformationForm_socialSecurityNumber" class="col-sm-2 control-label" ng-class="{'required-field' : !object.foreignStudent}">
					<spring:message
						code="label.PersonalInformationForm.socialSecurityNumber" />
				</label>

				<div class="col-sm-10">
					<input id="personalInformationForm_socialSecurityNumber"
						class="form-control" type="text" ng-model="object.socialSecurityNumber" name="socialSecurityNumber" ng-required="!object.foreignStudent" 
                        placeholder="<spring:message code='label.PersonalInformationForm.socialSecurityNumber.default' arguments='<%= FenixEduAcademicConfiguration.getConfiguration().getDefaultSocialSecurityNumber() %>'/>"
						value='<c:out value='${not empty param.socialsecuritynumber ? param.socialsecuritynumber : personalInformationForm.socialSecurityNumber }'/>'
						pattern="(\d{9})"/>
				</div>
			</div>
            <div class="form-group row">
                <div class="col-sm-2 control-label required-field">
                    <spring:message code="label.FiliationForm.dateOfBirth" />
                </div>

                <div class="col-sm-10">
                    <input id="filiationForm_dateOfBirth" class="form-control" type="text" name="dateOfBirth" bennu-date="object.dateOfBirth" /> 
                </div>
            </div>   
		</c:if>
			<c:if test="${1 lt placingOption}">
				<div class="form-group row">
					<label class="col-sm-2 control-label">
						<spring:message
							code="label.PersonalInformationForm.firstOptionInstitution" />
					</label>
		
					<div class="col-sm-10">
                        <ui-select ng-model="$parent.object.firstOptionInstitution"
                            on-select="onFirstOptionInstitutionChange($item, $model)" theme="bootstrap"> 
                            <ui-select-match>{{$select.selected.text}}</ui-select-match>
                            <ui-select-choices repeat="institution.id as institution in object.firstOptionInstitutionValues | filter: $select.search"
                                            refresh="onFirstOptionInstitutionRefresh($item, $select.search, $model)"
                                            refresh-delay="0">
                                <span ng-bind-html="institution.text | highlight: $select.search"></span>
                            </ui-select-choices> 
                        </ui-select>
					</div>
				</div>
				<div class="form-group row">
					<div class="col-sm-2 control-label">
						<spring:message
							code="label.PersonalInformationForm.firstOptionDegreeDesignation" />
					</div>
	
					<div class="col-sm-10">
                        <ui-select ng-model="$parent.object.firstOptionDegreeDesignation" theme="bootstrap"> 
                            <ui-select-match>{{$select.selected.text}}</ui-select-match>
                            <ui-select-choices repeat="degree.id as degree in object.firstOptionDegreeDesignationValues | filter: $select.search"
                                            refresh="onFirstOptionDegreeDesignationRefresh($item, $select.search, $model)"
                                            refresh-delay="0">
                                <span ng-bind-html="degree.text | highlight: $select.search"></span>
                            </ui-select-choices> 
                        </ui-select>
					</div>
				</div>
			</c:if>
			
			<div class="form-group row">
				<label class="col-sm-2 control-label required-field">
					<spring:message code="label.PersonalInformationForm.countryHighSchool" />
				</label>
				
				<div class="col-sm-10">
                    <ui-select id="personalInformationForm_countryHighSchool" name="countryHighSchool"
                        ng-model="$parent.object.countryHighSchool"
                        theme="bootstrap" > 
                        <ui-select-match allow-clear="true">
                            {{$select.selected.text}}
                        </ui-select-match> 
                        <ui-select-choices repeat="country.id as country in object.countryHighSchoolValues | filter: $select.search">
                            <span ng-bind-html="country.text | highlight: $select.search"></span>
                        </ui-select-choices>
                    </ui-select>
				</div>
			</div>
            <div class="form-group row">
                <label for="personalInformationForm_maritalStatus" class="col-sm-2 control-label required-field">
                    <spring:message code="label.PersonalInformationForm.maritalStatus" />
                </label>

                <div class="col-sm-4">
                    <ui-select  id="personalInformationForm_maritalStatus" name="maritalStatus" ng-model="$parent.object.maritalStatus" theme="bootstrap">
                        <ui-select-match >{{$select.selected.text}}</ui-select-match> 
                        <ui-select-choices  repeat="maritalStatus.id as maritalStatus in object.maritalStatusValues | filter: $select.search">
                            <span ng-bind-html="maritalStatus.text | highlight: $select.search"></span>
                        </ui-select-choices> 
                    </ui-select>                 
                </div>
            </div>
   
		</div>
		<div class="panel-footer">
            <button type="button" class="btn btn-primary" role="button" ng-click="submitForm()"><spring:message code="label.submit" /></button>
		</div>
	</div>
 
    <div class="modal fade" id="showExtraDigitsImages">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.PersonalInformationForm.help.extraDigit" />
                    </h4>
                </div>
                <div class="modal-body">
                    <center>
                        <img class="img-rounded img-responsive" title="Digitos de Segurança" alt="Dígitos extra do Cartão de Cidadão"
                        src="${pageContext.request.contextPath}/static/img/ajuda-digitos-de-seguranca.png"
                        />
                    </center>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                </div>
            </div>
            <!-- /.modal-content -->
        </div>
        <!-- /.modal-dialog -->
    </div>
    <!-- /.modal --> 
 
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
    span.glyphicon-question-sign {
        font-size: 2em;
    }
}
</style>

<script>
$(document).ready(function() {		

	});
</script>
