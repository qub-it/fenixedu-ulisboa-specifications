<%@page import="org.fenixedu.academic.domain.ExecutionSemester"%>
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
<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/css/select2.min.css" rel="stylesheet" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/js/select2.full.min.js"></script>
<!-- 
<link href="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/css/select2.min.css" rel="stylesheet" />
<script src="${pageContext.request.contextPath}/webjars/select2/4.0.0-rc.2/dist/js/select2.min.js"></script>
--> 



<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.studentsListByCurricularCourse.list" /> 
	</h1>
</div>

<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.studentsListByCurricularCourse.semester" /> 	
				</div>


				<div class="col-sm-10">
					<select id="executionSemesters">
					<c:forEach items="${executionSemesters}" var="executionSemester">
						<option value="${executionSemester.externalId}">${executionSemester.qualifiedName}</option>
					</c:forEach>
				
					</select>
				</div>
			</div>

			<script type="text/javascript">
				$("#executionSemesters").select2();
				$("#executionSemesters").on("change", function(){
					id = $("#executionSemesters").val()
					$.ajax({url : "${pageContext.request.contextPath}/studentsListByCurricularCourse/executionSemesters/" + id, 
							success : function(data){
								$("#executionDegrees").children().remove();
								formattedData = [{id : "-1",text : ""}].concat(data);
								$("#executionDegrees").select2({data: formattedData});
								$("#executionDegrees").trigger("change");
							}
					});
					
					$.ajax({url : "${pageContext.request.contextPath}/studentsListByCurricularCourse/executionSemesters/" + id + "/courses", 
							success : function(data){
								$("#listBy").val("COURSE");
								$("#listBy").attr('disabled', false);
								$("#selectCourseOrClass").attr("disabled", false);
								$("#selectCourseOrClass").children().remove();
								formattedData = [{id : "-1",text : ""}].concat(data);
								$("#selectCourseOrClass").select2({data: formattedData});
								$("#selectCourseOrClass").select2().val("-1");
								$("#selectCourseOrClass").trigger("change");
							}
					});					
					
				});
			</script>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.studentsListByCurricularCourse.degree" /> 	
				</div>	
				<div class="col-sm-10">
					<select id="executionDegrees"></select>
				</div>
			</div>
			<script type="text/javascript">
				$("#executionDegrees").on("change", function(){

					if($("#executionDegrees").val() != "-1"){
						$("#listBy").attr('disabled', false);
					}
					else{
						$("#listBy").attr('disabled', true);
					}
					$("#listBy").select2().val('');
					$("#listBy").trigger("change");
				});
			</script>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.studentsListByCurricularCourse.listType" /> 	
				</div>	
				<div class="col-sm-10">
					<select id="listBy" disabled>
						<option value=""></option>
						<option value="COURSE"><spring:message code="label.studentsListByCurricularCourse.listByCourse"/></option>
						<option value="CLASS"><spring:message code="label.studentsListByCurricularCourse.listByClass"/></option>
					</select>
				</div>
			</div>
			<script type="text/javascript">
				$("#listBy").on("change", function(){
					val = $("#listBy").val();
					degree = $("#executionDegrees").val();
					executionSemester = $("#executionSemesters").val()
					if(val == "COURSE"){
						url = "${pageContext.request.contextPath}/studentsListByCurricularCourse/executionSemesters/" + executionSemester + "/" + degree + "/courses";
					}
					else if(val == "CLASS"){
						hideSelectShift();
						url = "${pageContext.request.contextPath}/studentsListByCurricularCourse/executionSemesters/" + executionSemester + "/" + degree + "/classes";
					}
					else if(!val){
						$("#selectCourseOrClass").select2().val("-1");
						$("#selectCourseOrClass").trigger("change");
						$("#selectCourseOrClass").attr("disabled", true);
						hideSelectShift();
						return;
					}
					$("#selectCourseOrClass").attr("disabled", false);
					
					$.ajax({url : url, 
						success : function(data){
							$("#selectCourseOrClass").children().remove();
							formattedData = [{id : "-1",text : ""}].concat(data);
							$("#selectCourseOrClass").select2({data: formattedData});
							$("#selectCourseOrClass").select2().val("-1");
							$("#selectCourseOrClass").trigger("change");
						}
					});

				});
			</script>
			<div class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.studentsListByCurricularCourse.courseOrClass" /> 	
				</div>	
				<div class="col-sm-10">
					<select id="selectCourseOrClass" disabled></select>
				</div>
			</div>
			<script type="text/javascript">
				$("#selectCourseOrClass").on("change",function(){
					val = $("#selectCourseOrClass").val();
			  		listBy = $("#listBy").val();
			  		if(val == "-1"){
			  			$('#listTable').dataTable().fnClearTable();	
			  		}
			  		if(listBy == "CLASS"){		  			
						$.ajax({ url : "${pageContext.request.contextPath}/studentsListByCurricularCourse/classes/" + val,
								success : updateTable
						});
			  		}
			  		if(listBy == "COURSE"){
			  			$('#listTable').dataTable().fnClearTable();
			  			if(val == "-1"){
			  				$("#selectShiftEntry").hide();
			  				return;
			  			}
			  			$.ajax(
	  						{
	  							url :  "${pageContext.request.contextPath}/studentsListByCurricularCourse/executionCourse/" + val,
	  							
			  					success : function(data){
			  						$("#selectShiftEntry").show();
			  						$("#selectShift").children().remove();
			  						$("#selectShift").select2({data : [{id : "-1", text : ""}].concat(data)});
	  							}
			  				}
						);
						
			  			$.ajax(
	  						{
	  							url :  "${pageContext.request.contextPath}/studentsListByCurricularCourse/executionCourseRegistrations/" + val,
			  					success : updateTable
			  				}
						);						
						
		  			}
		  		});
				
			</script>
			<div id="selectShiftEntry" class="form-group row">
				<div class="col-sm-2 control-label required-field">
					<spring:message code="label.studentsListByCurricularCourse.shift" /> 	
				</div>	
				<div class="col-sm-10">
					<select id="selectShift"></select>
				</div>
			</div>
			<script>
			$("#selectShift").on("change", function(){
				val =$("#selectShift").val(); 
				$.ajax({
					url :  "${pageContext.request.contextPath}/studentsListByCurricularCourse/shifts/" + val,
					success : updateTable
				});
			});
			</script>
		</div>
	</div>
</form>
<table id="listTable" class="table responsive table-bordered table-hover">
	<thead>
		<tr>
			<%--!!!  Field names here --%>
			<th><spring:message code="label.studentsListByCurricularCourse.picture"/></th>
			<th><spring:message code="label.studentsListByCurricularCourse.number"/></th>
			<th><spring:message code="label.studentsListByCurricularCourse.name"/></th>
			<th><spring:message code="label.studentsListByCurricularCourse.degreeCode"/></th>
			<th><spring:message code="label.studentsListByCurricularCourse.degree"/></th>
			<th><spring:message code="label.studentsListByCurricularCourse.email"/></th>
			<th><spring:message code="label.studentsListByCurricularCourse.institutionalEmail"/></th>
			<th><spring:message code="label.studentsListByCurricularCourse.phone"/></th>
			<th><spring:message code="label.studentsListByCurricularCourse.mobilePhone"/></th>
		</tr>
	</thead>
	<tbody>
		
	</tbody>
</table>
		

<script type="text/javascript">
		
	$(document).ready(function() {
		$("#executionSemesters").val("<%= ExecutionSemester.readActualExecutionSemester().getExternalId() %>").trigger("change");
		$("#listBy").select2();
		$("#selectCourseOrClass").select2();
		$("#selectShiftEntry").hide();
	}); 
	
	updateTable = function(data){
		//$('#listTable').DataTable({"data" : data});
		$('#listTable').dataTable().fnClearTable();
		if(data.length != 0){
			$('#listTable').dataTable().fnAddData(data);
			$('#listTable').dataTable().fnDraw();
		}	

	};
	var table = $('#listTable').DataTable({language : {
		url : "${datatablesI18NUrl}",			
	},
	"columns": [
				{ data: 'picture',
				  "render": function(data, type, row) {
				        return '<img src="'+data+'" />';
				    }				
				},
				{ data: 'studentNumber' },
				{ data: 'name' },
				{ data: 'degreeCode' },
				{ data: 'degree' },
				{ data: 'email' },
				{ data: 'institutionalEmail' },
				{ data: 'phone' },
				{ data: 'mobilePhone' }
			],
	"dom": '<"col-sm-6"l><"col-sm-3"f><"col-sm-3"T>rtip', 
       "tableTools": {
           "sSwfPath": "${pageContext.request.contextPath}/webjars/datatables-tools/2.2.4/swf/copy_csv_xls_pdf.swf"        	
      }
	});
	table.columns.adjust().draw();
	
	  $('#listTable tbody').on( 'click', 'tr', function () {
	        $(this).toggleClass('selected');
	  });
	  
	  hideSelectShift = function(){
		  try{
				$("#selectShift").select2("destroy");
			}
			catch(e){
				//will have exception if selectShift was not initted as a select2
			}
			$("#selectShiftEntry").hide();
	  }
</script>

