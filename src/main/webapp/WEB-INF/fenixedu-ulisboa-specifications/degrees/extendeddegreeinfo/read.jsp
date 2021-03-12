<%@page import="org.fenixedu.ulisboa.specifications.ui.degrees.extendedinfo.ExtendedDegreeInformationController"%>
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
            code="label.manageServiceRequestTypes.readServiceRequestType" />
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
                        <th scope="row" class="col-xs-3"><spring:message code="label.extendedDegreeInformation.backoffice." /></th>
                        <td><c:out value='${serviceRequestType.code}' /></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>


PREFIXO de label
label.extendedDegreeInformation.backoffice


private ExecutionYear executionYear;
    private Degree degree;
    private String degreeType;
    private String degreeAcron;
    private String degreeSitePublicUrl;
    private String degreeSiteManagementUrl;
    private String auditInfo;

    // DegreeInfo fields
    private LocalizedString name;
    private LocalizedString description;
    private LocalizedString history;
    private LocalizedString objectives;
    private LocalizedString designedFor;
    private LocalizedString professionalExits;
    private LocalizedString operationalRegime;
    private LocalizedString gratuity;
    private LocalizedString additionalInfo;
    private LocalizedString links;
    private LocalizedString testIngression;
    private LocalizedString classifications;
    private LocalizedString accessRequisites;
    private LocalizedString candidacyDocuments;
    private Integer driftsInitial;
    private Integer driftsFirst;
    private Integer driftsSecond;
    private Double markMin;
    private Double markMax;
    private Double markAverage;
    private LocalizedString qualificationLevel;
    private LocalizedString recognitions;
    private LocalizedString prevailingScientificArea;

    // ExtendedDegreeInfo fields
    private LocalizedString scientificAreas;
    private LocalizedString studyProgrammeDuration;
    private LocalizedString studyRegime;
    private LocalizedString studyProgrammeRequirements;
    private LocalizedString higherEducationAccess;
    private LocalizedString professionalStatus;
    private LocalizedString supplementExtraInformation;
    private LocalizedString supplementOtherSources;

