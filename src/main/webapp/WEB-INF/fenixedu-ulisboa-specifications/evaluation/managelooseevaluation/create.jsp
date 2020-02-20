<%--
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: anil.mamede@qub-it.com
 *
 * 
 * This file is part of FenixEdu QubDocs.
 *
 * FenixEdu QubDocs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu QubDocs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu QubDocs.  If not, see <http://www.gnu.org/licenses/>.
 --%>

<%@page import="org.joda.time.LocalDate"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.evaluation.managelooseevaluation.LooseEvaluationController"%>
<%@page import="org.fenixedu.academic.domain.EvaluationSeason"%>
<%@page import="org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices"%>
<%@page import="org.fenixedu.academic.domain.EnrolmentEvaluation"%>
<%@page import="org.fenixedu.academic.domain.Enrolment"%>
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
	<h3 class="mtop2">
		<spring:message code="label.manage.createLooseEvaluationBean" /> <span class="badge"><c:out value="${executionSemester.qualifiedName}" /></small></span>
        <br/><small><c:out value="${studentCurricularPlan.registration.student.person.name} (${studentCurricularPlan.registration.number})" /></small>
	</h3>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<a href="${pageContext.request.contextPath}${backUrl}">
        <span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;
        <spring:message code="label.event.back" />
    </a>
</div>
<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>${message}</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>${message}</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>${message}</p>
		</c:forEach>

	</div>
</c:if>

<script type="text/javascript">
	  function processDelete(externalId) {
	    url = "${pageContext.request.contextPath}<%=LooseEvaluationController.DELETE_URL%>${studentCurricularPlan.externalId}/" + externalId + "/${executionSemester.externalId}";
	    $("#deleteForm").attr("action", url);
	    $('#deleteModal').modal('toggle')
	  }
      function processAnnul(externalId) {
          url = "${pageContext.request.contextPath}<%=LooseEvaluationController.ANNUL_URL%>${studentCurricularPlan.externalId}/" + externalId + "/${executionSemester.externalId}";
          $("#annulForm").attr("action", url);
          $('#annulModal').modal('toggle')
      }
      function processActivate(externalId) {
          url = "${pageContext.request.contextPath}<%=LooseEvaluationController.ACTIVATE_URL%>${studentCurricularPlan.externalId}/" + externalId + "/${executionSemester.externalId}";
          $("#activateForm").attr("action", url);
          $('#activateModal').modal('toggle')
      }
</script>

<div class="modal fade" id="deleteModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<form id="deleteForm" action="#" method="POST">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">
						<spring:message code="label.confirmation" />
					</h4>
				</div>
				<div class="modal-body">
					<p>
						<spring:message
							code="label.manage.createLooseEvaluationBean.confirmDelete" />
					</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						<spring:message code="label.close" />
					</button>
					<button id="deleteButton" class="btn btn-danger" type="submit">
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

<div class="modal fade" id="annulModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="annulForm" action="#" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.manage.createLooseEvaluationBean.confirmAnnul" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="annulButton" class="btn btn-info" type="submit">
                        <spring:message code="label.annul" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<div class="modal fade" id="activateModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <form id="activateForm" action="#" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"
                        aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">
                        <spring:message code="label.confirmation" />
                    </h4>
                </div>
                <div class="modal-body">
                    <p>
                        <spring:message
                            code="label.manage.createLooseEvaluationBean.confirmActivate" />
                    </p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <spring:message code="label.close" />
                    </button>
                    <button id="activateButton" class="btn btn-danger" type="submit">
                        <spring:message code="label.activate" />
                    </button>
                </div>
            </form>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.LooseEvaluationBean.enrolment" />
				</div>

				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="looseEvaluationBean_enrolment"
						class="js-example-basic-single" name="enrolment">
						<option value=""></option>
						<%-- empty option remove it if you don't want to have it or give it a label --%>
					</select>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.LooseEvaluationBean.grade" />
				</div>

				<div class="col-sm-1">
					<input id="looseEvaluationBean_grade" class="form-control"
						type="text" name="grade"
						value="${not empty param.grade ? param.grade : looseEvaluationBean.grade }" />
				</div>
				
				<div class="col-sm-2">
					<select id="looseEvaluationBean_gradescale" class="form-control"
						name="gradescale">
						<option value=""></option>
						<%-- empty option remove it if you don't want to have it or give it a label --%>
						<c:forEach items="${gradeScaleValues}" var="field">
							<option value="${field.id}">${field.text}</option>
						</c:forEach>
					</select>
					<script>
						$("#looseEvaluationBean_gradescale").val("${not empty param.gradescale ? param.gradescale : looseEvaluationBean.gradeScale }");
					</script>
				</div>
			</div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.LooseEvaluationBean.type" />
                </div>

                <div class="col-sm-4">
                    <select id="looseEvaluationBean_type" class="form-control"
                        name="type">
                        <option value=""></option>
                        <%-- empty option remove it if you don't want to have it or give it a label --%>
                        <c:forEach items="${typeValues}" var="field">
                            <option value="${field.externalId}"
                                ng-improvement="${field.improvement}"><%=EvaluationSeasonServices
                                    .getDescriptionI18N((EvaluationSeason) pageContext.getAttribute("field"))
                                    .getContent()%></option>
                        </c:forEach>
                    </select>
                    <script>
                        $("#looseEvaluationBean_type").val("${not empty param.type ? param.type : looseEvaluationBean.type }");
                    </script>
                </div>
            </div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.LooseEvaluationBean.examDate" />
				</div>

				<div class="col-sm-4">
					<input id="looseEvaluationBean_examDate" class="form-control"
						type="text" name="examdate" bennu-date
						value="${not empty param.examdate ? param.examdate : looseEvaluationBean.examDate }"
                        required="true" />
				</div>
			</div>
            <div class="form-group row">
                <div class="col-sm-2 control-label">
                    <spring:message code="label.LooseEvaluationBean.availableDate" />
                </div>
                

                <div class="col-sm-4">
                    <%
                       pageContext.setAttribute("defaultDate", new LocalDate().toString("yyyy-MM-dd"));
                    %>
                    <input id="looseEvaluationBean_availableDate" class="form-control"
                        type="text" name="availabledate" bennu-date
                        value="${not empty param.availabledate ? param.availabledate : defaultDate }" 
                        required="true"/>
                </div>
            </div>
			<div class="form-group row"
				id="looseEvaluationBean_improvementSemesterRow">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.LooseEvaluationBean.improvementSemester" />
				</div>

				<div class="col-sm-4">
					<select id="looseEvaluationBean_improvementSemester"
						class="form-control" name="improvementsemester">
						<option value=""></option>
						<%-- empty option remove it if you don't want to have it or give it a label --%>
						<c:forEach items="${improvementSemesterValues}" var="field">
							<option value="${field.externalId}">${field.qualifiedName}</option>
						</c:forEach>
					</select>
					<script>
						$("#looseEvaluationBean_improvementSemester").val("${not empty param.improvementsemester ? param.improvementsemester : looseEvaluationBean.improvementSemester }");
					</script>
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button"
				value="<spring:message code="label.create" />" />
		</div>
	</div>

	<c:choose>
		<c:when test="${not empty evaluationsSet}">
			<table id="evaluationsSetTable"
				class="table table-bordered table-hover">
				<thead>
					<tr>
						<%--!!!  Field names here --%>
						<th><spring:message
								code="label.LooseEvaluationBean.enrolmentEvaluation.enrolment.code" /></th>
						<th><spring:message
								code="label.name" /></th>
						<%--
                        <th><spring:message
								code="label.LooseEvaluationBean.enrolmentEvaluation.enrolment.executionSemester" /></th>
                         --%>
						<th><spring:message
								code="label.LooseEvaluationBean.enrolmentEvaluation.grade" /></th>
						<th><spring:message
								code="label.LooseEvaluationBean.enrolmentEvaluation.evaluationSeason" /></th>
						<th><spring:message
								code="label.LooseEvaluationBean.enrolmentEvaluation.improvementSemester" /></th>
                        <th><spring:message
                                code="label.LooseEvaluationBean.enrolmentEvaluation.examDate" /></th>
                        <th><spring:message
                                code="label.LooseEvaluationBean.availableDate" /></th>
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

</form>



<script>
$(document).ready(function() {

	var evaluationsDataSet = [
	                           			<c:forEach items="${evaluationsSet}" var="searchResult">
                                           <%
                                            final EnrolmentEvaluation evaluation = ((EnrolmentEvaluation) pageContext.getAttribute("searchResult"));
                                           %>

                                           <%-- Field access / formatting  here CHANGE_ME --%>
	                           				{
	                           				"DT_RowId" : '<c:out value='${searchResult.externalId}'/>',
	                           "code" : "<c:out value='${searchResult.enrolment.code}'/>",
	                           "enrolment" : "<%=evaluation.getEnrolment().getPresentationName().getContent().replace("'", " ").replace("\"", " ")%></br><span class='smalltxt'><spring:message  code='label.group'/>: <i><c:out value='${searchResult.enrolment.curriculumGroup.name.content}'/></i></span>",
	                           <%--"executionSemester" : "<c:out value='${searchResult.enrolment.executionPeriod.qualifiedName}'/>",--%>
	                           "grade" : "<c:out value='${searchResult.grade.value}'/><%if (!evaluation.getGrade().isEmpty()) {%> <span class='color888 smalltxt'>(<span class='smalltxt'><c:out value='${searchResult.grade.extendedValue.content}' />, </span><c:out value='${searchResult.grade.gradeScale.name.content}' />)</span><%}%>",
	                           "evaluationSeason" : "<c:out value='${searchResult.evaluationSeason.name.content}'/>",
	                           "improvementSemester" : 
                                   "<%
	                                    if (EvaluationSeasonServices.isDifferentEvaluationSemesterAccepted(evaluation.getEvaluationSeason())) {
                                            out.append(evaluation.getExecutionPeriod().getQualifiedName());
                                        } else {
                                            out.append("-");
                                        }
                                    %>",
	                           "examDate" : "<c:out value='${searchResult.examDateYearMonthDay}'/>",
	                           "gradeAvailableDate" : "<c:out value='${searchResult.gradeAvailableDateYearMonthDay}'/>",
	                           "actions" :
                                   <%
                                       if (evaluation.isFinal()) {
                                   %>
                                       " <a  class=\"btn btn-xs btn-danger\" href=\"#\" onClick=\"javascript:processDelete('${searchResult.externalId}')\"><span class=\"glyphicon glyphicon-trash\" aria-hidden=\"true\"></span>&nbsp;<spring:message code='label.delete'/></a>"
                                   <%
                                       } else if (evaluation.isAnnuled()) {
                                    %>
                                        " <a  class=\"btn btn-xs btn-info\" href=\"#\" onClick=\"javascript:processActivate('${searchResult.externalId}')\"><span class=\"glyphicon glyphicon-play\" aria-hidden=\"true\"></span>&nbsp;<spring:message code='label.activate'/></a>"
                                    <%
                                        } else {
                                    %>
                                        " <a  class=\"btn btn-xs btn-warning\" href=\"#\" onClick=\"javascript:processAnnul('${searchResult.externalId}')\"><span class=\"glyphicon glyphicon-stop\" aria-hidden=\"true\"></span>&nbsp;<spring:message code='label.annul'/></a>"
                                    <%
                                        }
                                    %>
                                   ,
	                               
	                           			},
	                                       </c:forEach>
	                               ];
	
	
		<%-- Block for providing enrolment options --%>
		enrolment_options = [
			<c:forEach items="${LooseEvaluationBean_enrolment_options}" var="element"> 
				{
					text : "${element.code} - <%=((Enrolment) pageContext.getAttribute("element")).getPresentationName().getContent().replace("'", " ").replace("\"", " ")%>",
					id : "${element.externalId}"
				},
			</c:forEach>
		];
		
		$("#looseEvaluationBean_enrolment").select2(
			{
				data : enrolment_options,
			}	  
	    );
	    
	    $("#looseEvaluationBean_enrolment").select2().select2('val', "${param.enrolment}");
	
		<%-- End block for providing enrolment options --%>
	
		
		$("#looseEvaluationBean_type").change(function() {
			var visible = $("#looseEvaluationBean_type option:selected" ).attr('ng-improvement');
			
			if(visible === 'true') {
				$("#looseEvaluationBean_improvementSemesterRow").show();
			} else {
				$("#looseEvaluationBean_improvementSemesterRow").hide();
			}
		});
		
		$("#looseEvaluationBean_improvementSemesterRow").hide();
		
		
		$("#looseEvaluationBean_type").select2().select2('val', "${param.type}");
		
		
		
		var table = $('#evaluationsSetTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
			{ data: 'code' },
			{ data: 'enrolment' },
			<%--{ data: 'executionSemester' },--%>
			{ data: 'grade' },
			{ data: 'evaluationSeason' },
			{ data: 'improvementSemester' },
			{ data: 'examDate' },
			{ data: 'gradeAvailableDate' },
			{ data: 'actions' }
			
		],
		//CHANGE_ME adjust the actions column width if needed
		"columnDefs": [
		//74
		               { "width": "74px", "targets": 6 } 
		             ],
		"data" : evaluationsDataSet,
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
		
		  $('#evaluationsSetTable tbody').on( 'click', 'tr', function () {
		        $(this).toggleClass('selected');
		    } );
		
	});
</script>
