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

<link href="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/css/dataTables.tableTools.css" rel="stylesheet"/>
<script src="${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/js/dataTables.tableTools.js"></script>
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>						
<script type="text/javascript" src="${pageContext.request.contextPath}/webjars/bootbox/4.4.0/bootbox.js" ></script>



<%-- TITLE --%>
<div class="page-header">
	<h1><spring:message code="label.moodleExport.list" /> ${listType}
		<small></small>
	</h1>
</div>
<%-- NAVIGATION --%>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${pageContext.request.contextPath}/ff/moodleexport"   ><spring:message code="label.event.back"/></a>
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





<c:choose>
	<c:when test="${not empty resultsDataSet}">
		<table id="listTable" class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<%--!!!  Field names here --%>
					<th>username</th>
					 <th>firstname</th>
					 <th>lastname</th>
					 <th>email</th>
					 <th>code</th>
					 <th>curricularplan</th>

					 <c:forEach var="i" begin="0" end="${numberOfCourseColumns}">
						<th>course${i}</th>					
			         </c:forEach>
					 <c:forEach var="i" begin="0" end="${numberOfCourseColumns}">
						<th>role${i}</th>					
			         </c:forEach>					 
					 <th>auth</th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
				<div class="alert alert-warning" role="alert">
					
					<p> <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true">&nbsp;</span>			<spring:message code="label.noResultsFound" /></p>
					
				</div>	
		
	</c:otherwise>
</c:choose>

<script>
	var listDataSet = [
	                           
			<c:forEach items="${resultsDataSet}" var="searchResult">
				<%-- Field access / formatting  here CHANGE_ME --%>
				{
				"DT_RowId" : '<c:out value='${searchResult.username}'/>',
					"username" 	: "<c:out value='${searchResult.username}'/>",
 					"firstname"	: "<c:out value='${searchResult.firstname}'/>",
 					"lastname" 	: "<c:out value='${searchResult.lastname}'/>",
 					"email"		: "<c:out value='${searchResult.email}'/>",
 					"degreeMinistryCode" : "<c:out value='${searchResult.degreeMinistryCode}'/>",
 					"curricularPlan"	 : "<c:out value='${searchResult.curricularPlan}'/>",
 					"auth"		: "<c:out value='${searchResult.auth}'/>",
					<!-- Iterate courses-->
					<c:set var="count" value="0" scope="page" />
					<c:forEach items="${searchResult.courses}" var="course">
						"course${count}" : 	"${course}",
						<c:set var="count" value="${count + 1}" scope="page"/>
					</c:forEach>
					<c:forEach var="i" begin="${count}" end="${numberOfCourseColumns}">
						"course${i}" : 	"",					
	         		</c:forEach>
						
					<c:set var="count" value="0" scope="page" />
					<c:forEach items="${searchResult.roles}" var="role">
						"role${count}" : 	"${role}",
						<c:set var="count" value="${count + 1}" scope="page"/>
					</c:forEach>
					<c:forEach var="i" begin="${count}" end="${numberOfCourseColumns}">
						"role${i}" : 	"",					
	         		</c:forEach>						
				
			},
            </c:forEach>
    ];
	
	$(document).ready(function() {

		var table = $('#listTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
			{ data: 'username' },
			{ data: 'firstname' },
			{ data: 'lastname' },
			{ data: 'email' },
			{ data: 'degreeMinistryCode' },
			{ data: 'curricularPlan' },
			<c:forEach var="i" begin="0" end="${numberOfCourseColumns}">
				{ data: 'course${i}' },					
         	</c:forEach>
			<c:forEach var="i" begin="0" end="${numberOfCourseColumns}">
				{ data: 'role${i}' },					
         	</c:forEach>				
			{ data: 'auth' },
			
		],
		"data" : listDataSet,
"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', 
        "tableTools": {
            "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"        	
        }
		});
		table.columns.adjust().draw();
		
		  $('#listTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		  
	}); 
</script>

