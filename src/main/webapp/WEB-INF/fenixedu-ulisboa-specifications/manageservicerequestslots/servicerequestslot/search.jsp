<%@page import="org.fenixedu.ulisboa.specifications.ui.servicerequestslot.ServiceRequestSlotController"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags" %>

<spring:url var="datatablesUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl" value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl" value="/CSS/dataTables/dataTables.bootstrap.min.css" />
<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl" value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<link href="${pageContext.request.contextPath}/static/ulisboaspecifications/css/dataTables.responsive.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/dataTables.responsive.js"></script>
<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js"></script>
<script src="${pageContext.request.contextPath}/static/ulisboaspecifications/js/omnis.js"></script>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.manageServiceRequestSlots.searchServiceRequestSlot" />
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
    &nbsp;
    <a class="" href="${pageContext.request.contextPath}<%= ServiceRequestSlotController.CREATE_URL %>">
        <spring:message code="label.event.create" />
    </a>
    &nbsp;
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

<div class="panel panel-default">
	<form method="get" class="form-horizontal">
		<div class="panel-body">
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message
                        code="label.ServiceRequestSlot.code" />
                </div>

                <div class="col-sm-10">
                    <input id="serviceRequestSlot_code" class="form-control" type="text" name="code" value='<c:out value='${param.code}'/>' />
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.ServiceRequestSlot.uiComponentType" />
                </div>

                <div class="col-sm-10">
                    <select id="serviceRequestSlot_uicomponenttype"
                        class="js-example-basic-single" name="uicomponenttype">
                        <option value="">&nbsp;</option>
                        <%-- empty option remove it if you don't want to have it or give it a label CHANGE_ME --%>
                    </select>
                    <script>
                    uiComponentType_options = [
                        <c:forEach items="${uiComponentTypeList}" var="element"> 
                            {
                                text : "<c:out value='${element.descriptionI18N.content}'/>",  
                                id : "<c:out value='${element}'/>"
                            },
                        </c:forEach>
                    ];                    

                    //Init Select2Options
                    initSelect2("#serviceRequestSlot_uicomponenttype",uiComponentType_options, "<c:out value='${param.uicomponenttype}'/>"); //
                    </script>
                </div>
            </div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.ServiceRequestSlot.label" />
                </div>

                <div class="col-sm-10">
                    <input id="serviceRequestSlot_label" class="form-control" type="text" name="label" bennu-localized-string value='${not empty param.label ? param.label : "{}" } ' />
                </div>
            </div>            
		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button" value="<spring:message code="label.search" />" />
		</div>
	</form>
</div>

<c:choose>
	<c:when test="${not empty searchserviceRequestSlotResultsDataSet}">
		<table id="searchserviceRequestSlotTable" class="table responsive table-bordered table-hover" width="100%">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th><spring:message code="label.ServiceRequestSlot.code" /></th>
					<th><spring:message code="label.ServiceRequestSlot.uiComponentType" /></th>
					<th><spring:message code="label.ServiceRequestSlot.label" /></th>
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
				<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>
				<spring:message code="label.noResultsFound" />
			</p>
		</div>
	</c:otherwise>
</c:choose>

<script>
	var searchserviceRequestSlotDataSet = [
			<c:forEach items="${searchserviceRequestSlotResultsDataSet}" var="searchResult">
				{
				"code" : "<c:out value='${searchResult.code}'/>",
				"uicomponenttype" : "<c:out value='${searchResult.uiComponentType.descriptionI18N.content}'/>",
				"label" : "<c:out value='${searchResult.label.content}'/>",
			    "actions" :
			        " <a  class=\"btn btn-default btn-xs\" href=\"${pageContext.request.contextPath}<%= ServiceRequestSlotController.SEARCH_VIEW_URL %>${searchResult.externalId}\"><spring:message code='label.view'/></a>" +
			        "" 
		        },
            </c:forEach>
    ];
	
	$(document).ready(function() {

		var table = $('#searchserviceRequestSlotTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
					{ data: 'code' },
					{ data: 'uicomponenttype' },
		            { data: 'label' },
		            { data: 'actions', className:"all" }
		],
		"columnDefs": [
			       		//54
			       		//128
   		               { "width": "54px", "targets": 3 } 
		],
		"data" : searchserviceRequestSlotDataSet,
		//Documentation: https://datatables.net/reference/option/dom
//"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', //FilterBox = YES && ExportOptions = YES
//"dom": 'T<"clear">lrtip', //FilterBox = NO && ExportOptions = YES
"dom": '<"col-sm-6"l><"col-sm-6"f>rtip', //FilterBox = YES && ExportOptions = NO
//"dom": '<"col-sm-6"l>rtip', // FilterBox = NO && ExportOptions = NO
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/static/treasury/swf/copy_csv_xls_pdf.swf"
        }
		});
		table.columns.adjust().draw();
		
		  $('#searchserviceRequestSlotTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

