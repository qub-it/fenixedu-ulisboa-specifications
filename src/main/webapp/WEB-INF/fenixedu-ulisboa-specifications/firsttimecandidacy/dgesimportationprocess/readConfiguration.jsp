<%@page import="org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.importation.DgesImportationProcessController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<script type="text/javascript" src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/select/1.2.0/js/dataTables.select.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/2.5.0/jszip.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/pdfmake.min.js"></script>
<script type="text/javascript" src="https://cdn.rawgit.com/bpampuch/pdfmake/0.1.18/build/vfs_fonts.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/buttons/1.2.2/js/buttons.html5.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/buttons/1.2.2/css/buttons.dataTables.min.css" />


<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />

<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%-- ${portal.angularToolkit()} --%>
${portal.toolkit()}

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
    <h1><spring:message code="label.firstYearConfiguration.searchFirstYearRegistrationConfiguration" />
        <small></small>
    </h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
    <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= DgesImportationProcessController.SEARCH_URL %>">
        <spring:message code="label.event.back" />
    </a> 
    &nbsp;|&nbsp;
    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= DgesImportationProcessController.CONFIGURATION_UPDATE_URL %>"  >
        <spring:message code="label.event.update" />
    </a>
    &nbsp;
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

<div class="panel panel-primary">
    <div class="panel-heading">
       <h3 class="panel-title"><spring:message code="label.details"/></h3>
    </div>
    <div class="panel-body">
        <table class="table">
            <tbody>
                <tr>
                    <th scope="row" class="col-xs-3"><spring:message code="label.dges.importation.default.registrationProtocol"/></th> 
                    <td>
                        <c:out value='${defaultRegistrationProtocol.code} - ${defaultRegistrationProtocol.description.content}'/>
                    </td> 
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3"><spring:message code="label.FirstYearRegistrationConfiguration.introductionText"/></th> 
                    <td>
                        <textarea bennu-localized-string><c:out value='${globalConfiguration.introductionText.json()}' /></textarea>
                    </td> 
                </tr>
                <tr>
                    <th scope="row" class="col-xs-3"><spring:message code="label.FirstYearRegistrationConfiguration.infoAcademicRequisitionText"/></th> 
                    <td>
                        <c:out value='${globalConfiguration.infoAcademicRequisitionText}' />
                    </td> 
                </tr>
                <tr>
                   <th scope="row" class="col-xs-3"><spring:message code="label.FirstYearRegistrationConfiguration.mod43TemplateFile"/></th> 
                   <td>
                       <a href="${pageContext.request.contextPath}<%= DgesImportationProcessController.DOWNLOAD_CGD_URL %>/${globalConfiguration.mod43Template.externalId}">
                           <c:out value='${globalConfiguration.mod43Template.displayName}'/>
                       </a>
                   </td> 
                </tr>
            </tbody>
        </table>
    </div>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
       <h3 class="panel-title"><spring:message code="label.dges.importation.configuration.contingent.to.ingressions"/></h3>
    </div>
    <div class="panel-body">
        <c:choose>
            <c:when test="${not empty contingentMappings}">
                <table id="contigentMappingTable" class="table responsive table-bordered table-hover" width="100%">
                    <thead>
                        <tr>
                            <th>
                                <spring:message code="label.dges.importation.configuration.contingent" />
                            </th>
                            <th>
                                <spring:message code="label.dges.importation.configuration.ingressionType" />
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="mapping" items="${ contingentMappings }">
                            <tr>
                                <td><c:out value="${ mapping.contingent }" /></td>
                                <td><c:out value="${ mapping.ingressionType.code } - ${ mapping.ingressionType.description.content }" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="alert alert-warning" role="alert">
                    <p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>         
                            <spring:message code="label.noResultsFound" />
                    </p>
                </div>  
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="panel panel-primary">
    <div class="panel-heading">
       <h3 class="panel-title"><spring:message code="label.FirstYearRegistrationConfiguration.configurationByDegree"/></h3>
    </div>
    <div class="panel-body">
        <c:choose>
            <c:when test="${not empty activeDegreesMappings}">
                <table id="activeDegreesMappingTable" class="table responsive table-bordered table-hover" width="100%">
                    <thead>
                        <tr>
                            <th><spring:message code="label.FirstYearRegistrationConfiguration.degreeName"/></th>
                            <th><spring:message code="label.FirstYearRegistrationConfiguration.degreeMinistryCode"/></th>
                            <th><spring:message code="label.FirstYearRegistrationConfiguration.requiresVaccination"/></th>
                            <th><spring:message code="label.FirstYearRegistrationConfiguration.degreeCurricularPlan"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="mapping" items="${ activeDegreesMappings }">
                            <tr>
                                <td><c:out value="[${mapping.degree.code}] ${mapping.degree.presentationName}" /></td>
                                <td><c:out value="${mapping.degree.ministryCode}" /></td>
                                <td>
                                    <c:if test="${mapping.requiresVaccination}">
                                        <spring:message code="label.true" />
                                    </c:if>
                                    <c:if test="${not mapping.requiresVaccination}">
                                        <spring:message code="label.false" />
                                    </c:if>
                                </td>
                                <td><c:out value="${ mapping.degreeCurricularPlan.name}" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="alert alert-warning" role="alert">
                    <p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>         
                            <spring:message code="label.noResultsFound" />
                    </p>
                </div>  
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
$(document).ready(function() {
	
    var table = $('#contigentMappingTable').DataTable({
        language : {
            url : "${datatablesI18NUrl}",
        },
        //Documentation: https://datatables.net/reference/option/dom
        //"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
        //"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
        //"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        dom: '<"col-sm-5"l><"col-sm-3"f><"col-sm-3"B>rtip', //FilterBox = YES && ExportOptions = YES
        buttons: [
            'copyHtml5',
            'excelHtml5',
            'csvHtml5',
            'pdfHtml5'
        ],
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"            
        }
    });
    table.columns.adjust().draw();
    
    var table2 = $('#activeDegreesMappingTable').DataTable({
        language : {
            url : "${datatablesI18NUrl}",
        },
        //Documentation: https://datatables.net/reference/option/dom
        //"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
        //"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
        //"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        dom: '<"col-sm-5"l><"col-sm-3"f><"col-sm-3"B>rtip', //FilterBox = YES && ExportOptions = YES
        buttons: [
            'copyHtml5',
            'excelHtml5',
            'csvHtml5',
            'pdfHtml5'
        ],
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"            
        }
    });
    table2.columns.adjust().draw();

    $("textarea").attr("readonly", true);
    
    });
</script>
