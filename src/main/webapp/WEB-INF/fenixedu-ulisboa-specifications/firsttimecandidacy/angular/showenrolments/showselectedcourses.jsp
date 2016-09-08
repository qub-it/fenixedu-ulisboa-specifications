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
    <h1><spring:message code="label.firstTimeCandidacy.showSelectedCourses" />
        <small></small>
    </h1>
</div>

<div class="well well-sm" style="display:inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}${controllerURL}/back"><spring:message code="label.back"/></a>
    &nbsp;&nbsp;|&nbsp;&nbsp;
    <span class="glyphicon glyphicon-arrow-right" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}${controllerURL}/continue"><spring:message code="label.event.firstTimeCandidacy.continue"  /></a>  
        
</div>
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

<div class="col-md-12">
    <div class="col-md-4">
        <c:if test="${not empty firstSemesterEnrolments}">
            <h2>${currentYear}&nbsp;<small><spring:message code="label.enrolments.first.semester" /></small></h2>
            
            <!-- Table -->
            <table class="table table-striped">
                <tbody>
                    <c:forEach items="${firstSemesterEnrolments}" var="enrolment">
                        <tr>
                            <td class="strong">
                                ${enrolment.curricularCourse.nameI18N.content}
                            </td>
                            <td class="">
                                <spring:message code="label.credits" arguments="${enrolment.ectsCredits}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>                        
            </table>
            <h5 class="strong"><spring:message code="label.total.credits" arguments="${firstSemesterCredits}"/></h5>
        </c:if>
    </div>
    <div class="col-md-4">
        <c:if test="${not empty secondSemesterEnrolments}">
            <h2>${currentYear}&nbsp;<small><spring:message code="label.enrolments.second.semester" /></small></h2>
            
            <!-- Table -->
            <table class="table table-striped">
                <tbody>
                    <c:forEach items="${secondSemesterEnrolments}" var="enrolment">
                        <tr>
                            <td class="strong">
                                ${enrolment.curricularCourse.nameI18N.content}
                            </td>
                            <td class="">
                                <spring:message code="label.credits" arguments="${enrolment.ectsCredits}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>                        
            </table>
            <h5 class="strong"><spring:message code="label.total.credits" arguments="${secondSemesterCredits}"/></h5>
        </c:if>
    </div>
</div>


<style>
    .strong {
        font-weight: bold;
    }
    .table {
        width: auto;
    }
</style>

<script>
$(document).ready(function() {

    
    
    });
</script>