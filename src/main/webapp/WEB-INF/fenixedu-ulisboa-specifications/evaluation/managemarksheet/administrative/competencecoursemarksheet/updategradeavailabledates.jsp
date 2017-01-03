<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet"%>
<%@page
    import="org.fenixedu.ulisboa.specifications.ui.evaluation.managemarksheet.administrative.CompetenceCourseMarkSheetController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="datatables" uri="http://github.com/dandelion/datatables"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.angularToolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css"
    rel="stylesheet" />
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

<script>
    angular
        .module('angularAppCompetenceCourseMarkSheet',
            [ 'ngSanitize', 'ui.select', 'bennuToolkit' ])
        .controller(
            'CompetenceCourseMarkSheetController',
            [
                '$scope',
                function($scope) {
                $scope.booleanvalues = [
                    {
                        name : '<spring:message code="label.no"/>',
                        value : false
                    },
                    {
                        name : '<spring:message code="label.yes"/>',
                        value : true
                    } ];

                $scope.object = ${competenceCourseMarkSheetBeanJson};
                $scope.postBack = createAngularPostbackFunction($scope);
                
                $scope.hasAnyEmptyDate = function() {
                    var foundEmptyDates = false;
                    $.each( $scope.object.updateGradeAvailableDateBeans, function( index, evaluation ){
                        if (evaluation.gradeAvailableDate == undefined || evaluation.gradeAvailableDate == '') {
                            foundEmptyDates = true;
                        }
                    });
                    
                    
                    return foundEmptyDates;
                    
                }
                
                $scope.submitDatesWithEmptyCheck = function() {
                    if ($scope.hasAnyEmptyDate()) {                      
                        $('#emptyDatesWarningModal').modal('toggle')
                    } else {
                        $scope.submitDates();
                    }
                }
                
                $scope.submitDates = function() {
                    $('#updateGradeAvailableDatesForm').submit();
                }
                
                } ]);
</script>

<div ng-app="angularAppCompetenceCourseMarkSheet" ng-controller="CompetenceCourseMarkSheetController">

<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.evaluation.manageMarkSheet.updateGradeAvailableDates" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class=""
        href="${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.READ_URL%>${competenceCourseMarkSheet.externalId}"><spring:message
            code="label.event.back" /></a>
</div>
<c:if test="${not empty infoMessages}">
    <div class="alert alert-info" role="alert">

        <c:forEach items="${infoMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon glyphicon-ok-sign" aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty warningMessages}">
    <div class="alert alert-warning" role="alert">

        <c:forEach items="${warningMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>

    </div>
</c:if>
<c:if test="${not empty errorMessages}">
    <div class="alert alert-danger" role="alert">

        <c:forEach items="${errorMessages}" var="message">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span> ${message}
            </p>
        </c:forEach>

    </div>
</c:if>

<div class="modal fade" id="emptyDatesWarningModal">
    <div class="modal-dialog">
        <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirm" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p><spring:message code="label.evaluation.manageMarkSheet.updateGradeAvailableDates.emptyDatesWarning" /></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.cancel" />
                    </button>
                    <button class="btn btn-danger" type="button" ng-click="submitDates()">
                        <spring:message code="label.continue" />
                    </button>
                </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<form id="updateGradeAvailableDatesForm" name='form' method="post" class="form-horizontal"
    action='${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.UPDATEGRADEAVAILABLEDATES_URL%>${competenceCourseMarkSheet.externalId}'>

    <input type="hidden" name="postback"
        value='${pageContext.request.contextPath}<%=CompetenceCourseMarkSheetController.UPDATEGRADEAVAILABLEDATESPOSTBACK_URL%>${competenceCourseMarkSheet.externalId}' />

    <input name="bean" type="hidden" value="{{ object }}" />
    
    <div class="panel panel-primary">
    
        <div class="panel-heading">
            <h3 class="panel-title">
                <spring:message code="label.details" />
            </h3>
        </div>
    
        <div class="panel-body">
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.executionSemester" /></th>
                        <td><c:out value="${competenceCourseMarkSheet.executionSemester.qualifiedName}"/></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.competenceCourse" /></th>
                        <td><c:out value="${competenceCourseMarkSheet.competenceCourse.code}"/> - <c:out value="${competenceCourseMarkSheet.competenceCourse.nameI18N.content}"/></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.evaluationSeason" /></th>
                        <td><c:out value="<%=EvaluationSeasonServices.getDescriptionI18N(((CompetenceCourseMarkSheet)request.getAttribute("competenceCourseMarkSheet")).getEvaluationSeason()).getContent()%>"/></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.CompetenceCourseMarkSheet.evaluationDate" /></th>
                        <td><joda:format value="${competenceCourseMarkSheet.evaluationDate}" pattern="yyyy-MM-dd"/></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    

    <div class="panel panel-default">
        <div class="panel-body">
        
            <%-- TODO legidio
            <div class="alert alert-info" role="alert">
                <spring:message code="label.evaluation.manageMarkSheet.updateGradeAvailableDates.instructions" arguments="${competenceCourseMarkSheet.gradeScaleDescription}" />
            </div>
             --%>
             
            <div class="form-group row">
                <div class="col-sm-9">
                    <button type="button" class="btn btn-primary" role="button" ng-click="submitDatesWithEmptyCheck()"><spring:message code="label.save" /></button>
                </div>
            </div>

        
            <table id="gradeavailabledateTable" class="table responsive table-bordered table-hover" width="100%">
                <thead>
                    <tr>
                        <th><spring:message code="label.MarkBean.studentNumber" /></th>
                        <th><spring:message code="label.MarkBean.studentName" /></th>
                        <th><spring:message code="label.MarkBean.degreeCode" /></th>
                        <th><spring:message code="label.MarkBean.shifts" /></th>
                        <th><spring:message code="label.MarkBean.gradeValue" /></th>
                        <th><spring:message code="label.LooseEvaluationBean.availableDate" /></th>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="evaluation in object.updateGradeAvailableDateBeans">
                        <td>{{evaluation.studentNumber}}</td>
                        <td>{{evaluation.studentName}}</td>
                        <td>{{evaluation.degreeCode}}</td>
                        <td>{{evaluation.shifts}}</td>
                        <td>{{evaluation.gradeValue}}</td>
                        <td>
                            <input type="text" name="gradeAvailableDate"
                                class="form-control" required="true" 
                                bennu-date="evaluation.gradeAvailableDate"
                                ng-bind-html="{{evaluation.gradeAvailableDate}}"
                                />
                            <span class="alert alert-danger btn-xs" ng-show="evaluation.errorMessage != null">{{evaluation.errorMessage}}</span>
                         </td>
                    </tr>
                </tbody>
            </table>
            
        </div>
        <div class="panel-footer">
            <button type="button" class="btn btn-primary" role="button" ng-click="submitDatesWithEmptyCheck()"><spring:message code="label.save" /></button>
        </div>
    </div>
</form>

</div>
