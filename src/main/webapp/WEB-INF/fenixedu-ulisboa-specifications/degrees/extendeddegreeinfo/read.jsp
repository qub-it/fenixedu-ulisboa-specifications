<%@page import="org.fenixedu.ulisboa.specifications.ui.degrees.extendedinfo.ExtendedDegreeInformationController"%>
<%@page import="org.fenixedu.ulisboa.specifications.domain.services.AuditingServices"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>

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
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%-- ${portal.angularToolkit()} --%>
${portal.toolkit()}

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
    <a class="" href="${pageContext.request.contextPath}<%= ExtendedDegreeInformationController.SEARCH_URL %>">
        <spring:message code="label.event.back" />
    </a> 
    &nbsp;|&nbsp; 
    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= ExtendedDegreeInformationController.UPDATE_URL %>${degreeInfo.externalId}">
        <spring:message code="label.event.update" />
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

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <spring:message code="label.details" />
        </h3>
    </div>
    <div class="panel-body">
        <form method="post" class="form-horizontal">
            <table class="table">
                <tbody>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.executionInterval" /></th>
                        <td><c:out value='${degreeInfo.executionInterval.qualifiedName}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.degree" /></th>
                        <td><c:out value='${degreeInfo.degree.presentationNameI18N.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.degreeType" /></th>
                        <td><c:out value='${degreeInfo.degree.degreeType.name.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.degreeAcron" /></th>
                        <td><c:out value='${degreeInfo.degree.acronym}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.degreeSitePublicUrl" /></th>
                        <td><c:if test="${not empty degreeInfo.degree.siteUrl}">degrees/<c:out value='${degreeInfo.degree.siteUrl}' /></c:if></td>
                    </tr> 
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.degreeSiteManagementUrl" /></th>
                        <td><c:if test="${not empty degreeInfo.degree.siteUrl}">cms/sites/<c:out value='${degreeInfo.degree.siteUrl}' /></c:if></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.auditInfo" /></th>
                        <%    Object extendedDegreeInfo = request.getAttribute("extendedDegreeInfo"); %>
                        <%    if(extendedDegreeInfo != null) { %>
                            <td><%= AuditingServices.getAuditInfo(extendedDegreeInfo) %></td>
                        <%    } else { %>
                            <td></td>
                        <%    } %>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.name" /></th>
                        <td><c:out value='${degreeInfo.name.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.description" /></th>
                        <td><c:out value='${degreeInfo.description.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.history" /></th>
                        <td><c:out value='${degreeInfo.history.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.objectives" /></th>
                        <td><c:out value='${degreeInfo.objectives.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.designedFor" /></th>
                        <td><c:out value='${degreeInfo.designedFor.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.professionalExits" /></th>
                        <td><c:out value='${degreeInfo.professionalExits.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.operationalRegime" /></th>
                        <td><c:out value='${degreeInfo.operationalRegime.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.gratuity" /></th>
                        <td><c:out value='${degreeInfo.gratuity.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.additionalInfo" /></th>
                        <td><c:out value='${degreeInfo.additionalInfo.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.links" /></th>
                        <td><c:out value='${degreeInfo.links.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.testIngression" /></th>
                        <td><c:out value='${degreeInfo.testIngression.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.classifications" /></th>
                        <td><c:out value='${degreeInfo.classifications.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.accessRequisites" /></th>
                        <td><c:out value='${degreeInfo.accessRequisites.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.candidacyDocuments" /></th>
                        <td><c:out value='${degreeInfo.candidacyDocuments.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.driftsInitial" /></th>
                        <td><c:out value='${degreeInfo.driftsInitial}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.driftsFirst" /></th>
                        <td><c:out value='${degreeInfo.driftsFirst}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.driftsSecond" /></th>
                        <td><c:out value='${degreeInfo.driftsSecond}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.markMin" /></th>
                        <td><c:out value='${degreeInfo.markMax}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.markMax" /></th>
                        <td><c:out value='${degreeInfo.markMax}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.markAverage" /></th>
                        <td><c:out value='${degreeInfo.markAverage}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.qualificationLevel" /></th>
                        <td><c:out value='${degreeInfo.qualificationLevel.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.recognitions" /></th>
                        <td><c:out value='${degreeInfo.recognitions.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.prevailingScientificArea" /></th>
                        <td><c:out value='${degreeInfo.prevailingScientificArea.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.scientificAreas" /></th>
                        <td><c:out value='${extendedDegreeInfo.scientificAreas.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.studyProgrammeDuration" /></th>
                        <td><c:out value='${extendedDegreeInfo.studyProgrammeDuration.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.studyRegime" /></th>
                        <td><c:out value='${extendedDegreeInfo.studyRegime.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.studyProgrammeRequirements" /></th>
                        <td><c:out value='${extendedDegreeInfo.studyProgrammeRequirements.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.higherEducationAccess" /></th>
                        <td><c:out value='${extendedDegreeInfo.higherEducationAccess.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.professionalStatus" /></th>
                        <td><c:out value='${extendedDegreeInfo.professionalStatus.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.supplementExtraInformation" /></th>
                        <td><c:out value='${extendedDegreeInfo.supplementExtraInformation.content}' /></td>
                    </tr>
                    <tr>
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice.supplementOtherSources" /></th>
                        <td><c:out value='${extendedDegreeInfo.supplementOtherSources.content}' /></td>
                    </tr>
                    
                </tbody>
            </table>
        </form>
    </div>
</div>
