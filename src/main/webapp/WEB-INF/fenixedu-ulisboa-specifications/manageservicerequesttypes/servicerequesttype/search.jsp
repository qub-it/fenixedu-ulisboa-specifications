<%@page import="org.fenixedu.ulisboa.specifications.ui.servicerequesttype.ServiceRequestTypeController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
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
    <h1>
        <spring:message
            code="label.manageServiceRequestTypes.searchServiceRequestType" />
        <small></small>
    </h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
    <span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= ServiceRequestTypeController.CREATE_URL %>">
        <spring:message code="label.event.create" />
    </a> 
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

<script type="text/javascript">
	  function processDelete(externalId) {
	    url = "${pageContext.request.contextPath}<%= ServiceRequestTypeController.DELETE_URL %>" + externalId;
	    $("#deleteForm").attr("action", url);
	    $('#deleteModal').modal('toggle')
	  }
</script>

<div class="modal fade" id="deleteModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="deleteForm" action="#" method="POST">
                <div class="modal-header">
                    <button type="button" class="close"
                        data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.manageServiceRequestTypes.searchServiceRequestType.confirmDelete" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default"
                        data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="deleteButton" class="btn btn-danger"
                        type="submit">
                        <spring:message code="label.delete" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<c:choose>
    <c:when test="${not empty searchservicerequesttypeResultsDataSet}">
        <table id="searchservicerequesttypeTable"
            class="table responsive table-bordered table-hover" style="width:100%">
            <thead>
                <tr>
                    <%--!!!  Field names here --%>
                    <th><spring:message
                            code="label.ServiceRequestType.code" /></th>
                    <th><spring:message
                            code="label.ServiceRequestType.name" /></th>
                    <th><spring:message
                            code="label.ServiceRequestType.serviceRequestCategory" /></th>
                    <th><spring:message
                            code="label.ServiceRequestType.active" /></th>
                    <th><spring:message
                            code="label.ServiceRequestType.payable" /></th>
                    <th><spring:message
                            code="label.ServiceRequestType.notifyUponConclusion" /></th>
                    <th><spring:message
                            code="label.ServiceRequestType.printable" /></th>
                    <th><spring:message
                            code="label.ServiceRequestType.requestedOnline" /></th>
                    <th><spring:message
                            code="label.ServiceRequestType.printableOnline" /></th>
                    <%-- Operations Column --%>
                    <th></th>
                </tr>
            </thead>
            <tbody>

            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <div class="alert alert-warning" role="alert">
            <p>
                <span class="glyphicon glyphicon-exclamation-sign"
                    aria-hidden="true">&nbsp;</span>
                <spring:message code="label.noResultsFound" />
            </p>
        </div>
    </c:otherwise>
</c:choose>

<script>
	var searchservicerequesttypeDataSet = [
			<c:forEach items="${searchservicerequesttypeResultsDataSet}" var="searchResult">
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
				"DT_RowId" : '<c:out value='${searchResult.externalId}'/>',
"code" : "<c:out value='${searchResult.code}'/>",
"name" : "<c:out value='${searchResult.name.content}'/>",
"serviceRequestCategory" : "<c:out value='${searchResult.serviceRequestCategory.name}'/>",
"active" : "<c:if test="${searchResult.active}"><spring:message code="label.true" /></c:if><c:if test="${not searchResult.active}"><spring:message code="label.false" /></c:if>",
"payable" :"<c:if test="${searchResult.payable}"><spring:message code="label.true" /></c:if><c:if test="${not searchResult.payable}"><spring:message code="label.false" /></c:if>",
"notifyUponConclusion" : "<c:if test="${searchResult.notifyUponConclusion}"><spring:message code="label.true" /></c:if><c:if test="${not searchResult.notifyUponConclusion}"><spring:message code="label.false" /></c:if>",
"printable" : "<c:if test="${searchResult.printable}"><spring:message code="label.true" /></c:if><c:if test="${not searchResult.printable}"><spring:message code="label.false" /></c:if>",
"requestedOnline" : "<c:if test="${searchResult.requestedOnline}"><spring:message code="label.true" /></c:if><c:if test="${not searchResult.requestedOnline}"><spring:message code="label.false" /></c:if>",
"printableOnline" : "<c:if test="${searchResult.printableOnline}"><spring:message code="label.true" /></c:if><c:if test="${not searchResult.printableOnline}"><spring:message code="label.false" /></c:if>",
"actions" :
" <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ServiceRequestTypeController.READ_URL %>${searchResult.externalId}\"><spring:message code='label.view'/></a>" +
" <a  class=\"btn btn-xs btn-danger\" href=\"#\" onClick=\"javascript:processDelete('${searchResult.externalId}')\"><span class=\"glyphicon glyphicon-trash\" aria-hidden=\"true\"></span></a>" +
                "" 
			},
            </c:forEach>
    ];
	
	$(document).ready(function() {

		var table = $('#searchservicerequesttypeTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
			{ data: 'code' },
			{ data: 'name' },
			{ data: 'serviceRequestCategory' },
			{ data: 'active' },
			{ data: 'payable' },
			{ data: 'notifyUponConclusion'},
            { data: 'printable'},
            { data: 'requestedOnline'},
            { data: 'printableOnline'},
			{ data: 'actions',className:"all" }
			
		],
		//CHANGE_ME adjust the actions column width if needed
		"columnDefs": [
		//54
		//128
		               { "width": "128px", "targets": 8 } 
		             ],
		"data" : searchservicerequesttypeDataSet,
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
            "sSwfPath": "${pageContext.request.contextPath}/static/qubdocsreports/swf/copy_csv_xls_pdf.swf"
        }
		});
		table.columns.adjust().draw();
		
		  $('#searchservicerequesttypeTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

