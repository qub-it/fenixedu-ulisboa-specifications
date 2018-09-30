<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.ProfessionalInformationManagementController"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.HouseholdInformationManagementController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

${portal.toolkit()}

<link href="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/fenixedu-ulisboa-specifications/js/omnis.js"></script>



<%-- TITLE --%>
<div class="page-header">
    <h1>
        <spring:message code="label.title.professionalInformationManagement" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= HouseholdInformationManagementController.SEARCH_URL %>/${student.externalId}">
        <spring:message code="label.back" />
    </a>
    
    &nbsp;|&nbsp;
    
    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    <a class="" href="${pageContext.request.contextPath}<%= ProfessionalInformationManagementController.UPDATE_URL %>/${student.externalId}/${personalIngressionData.executionYear.externalId}">
        <spring:message code="label.update" />
    </a>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.details" />
        </h3>
    </div>
    
    <c:set var="personUl" value="${ personalIngressionData.student.person.personUlisboaSpecifications }" />
    <c:set var="personUlExecutionYear" value="${ personUl.getPersonUlisboaSpecificationsByExcutionYear(personalIngressionData.executionYear) }" />
    <spring:message code="label.yes" var="yesLabel" />
    <spring:message code="label.no" var="noLabel" />
    
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>
                   <tr>
                       <th scope="row" class="col-xs-3">
                           <spring:message code="label.HouseholdInformationForm.professionalCondition" />
                       </th>
                       <td>
                           <c:out value='${personalIngressionData.professionalCondition.localizedName}' />
                       </td>
                   </tr>
                   <tr>
                       <th scope="row" class="col-xs-3">
                           <spring:message code="label.HouseholdInformationForm.profession" />
                       </th>
                       <td>
                           <c:out value='${personalIngressionData.student.person.profession}' />
                       </td>
                   </tr>
                   <tr>
                       <th scope="row" class="col-xs-3">
                           <spring:message code="label.HouseholdInformationForm.professionType" />
                       </th>
                       <td>
                           <c:out value='${personalIngressionData.professionType.localizedName}' />
                       </td>
                   </tr>
                   <tr>
                       <th scope="row" class="col-xs-3">
                           <spring:message code="label.HouseholdInformationForm.professionTimeType" />
                       </th>
                       <td>
                           <c:out value='${personUlExecutionYear.professionTimeType.description.content}' />
                       </td>
                   </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>

