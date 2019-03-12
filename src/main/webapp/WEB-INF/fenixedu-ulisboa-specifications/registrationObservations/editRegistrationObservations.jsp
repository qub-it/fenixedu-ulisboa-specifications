<%@page import="org.fenixedu.ulisboa.specifications.domain.RegistrationObservations"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h3><spring:message code="label.student.observations"/></h3>
<div>
	<c:if test="${saved}">
		<div class="alert alert-info" role="alert">
			<spring:message code="label.saved"/>
		</div>
	</c:if>
	<c:if test="${deleted}">
		<div class="alert alert-info" role="alert">
			<spring:message code="label.deleted"/>
		</div>
	</c:if>
</div>
<div>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${returnURL}"><spring:message code="label.back"/></a>	
</div>
</div>

<c:if test="${not empty  observations}">
	 <table class="paginated tstyle2 thright thlight thcenter table">
					<thead>
						<tr>
							<th><spring:message code="label.student.observations.by"></spring:message>:</th>
							<th><spring:message code="label.student.observations.in"></spring:message>:</th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${observations}" var="observation">
						
							<tr>
								<td class="acenter">
									${observation.versioningUpdatedBy.username}
								</td>
								<td class="acenter">
									<%=((RegistrationObservations) pageContext.getAttribute("observation")).getVersioningUpdateDate().getDate().toString("dd/MM/yyyy hh:mm:ss")%>
								</td>
								<td class="acenter">
									${ observation.asHtml}
								</td>
								<c:if test="${writeAccess}">
									<td>
										<a href="${pageContext.request.contextPath}/registrations/${registration.externalId}/observations/${observation.externalId}#editform" class="observation-header-element">
										 	<spring:message code="label.student.observations.edit"></spring:message>
										 </a>
										<form style="display: inline-block" method="POST" action="${pageContext.request.contextPath}/registrations/${registration.externalId}/observations/${observation.externalId}">
											<input type="hidden" name="delete" value="delete" />
											<input type="hidden" name="observations" value="" />
											<a href="javascript: void(0)" onclick="deleteObservation(this)"><spring:message code="label.student.observations.delete"/></a>
									 	</form> 
									</td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
			</table>
			<script>
			function deleteObservation(el){
				if(confirm("<spring:message code="label.student.observations.delete.confirm"/>")){
					el.parentElement.submit();
				}
				return false;
			}
			</script>
 </c:if>
<c:if test="${ empty  observations}">
	<spring:message code="label.student.observations.empty"></spring:message>
</c:if>

<c:if test="${writeAccess}">
	<div>
		<a class="btn btn-default" href="${pageContext.request.contextPath}/registrations/${registration.externalId}/observations/create#editform">
			<spring:message code="label.createNew"></spring:message>
		</a>
	</div>
</c:if>

<c:if test="${createMode}">
	<h2><spring:message code="label.student.observations.new"></spring:message></h2>
	<form method="POST" action="${pageContext.request.contextPath}/registrations/${registration.externalId}/observations">
	<div>
	<textarea name="observations" rows="15" style="width:100%">
	</textarea>
	</div>
	<div>
	<input type="submit" value="<spring:message code="label.create"/>"/>
	</div>
</form>
</c:if>
<c:if test="${not empty editObservationText}">
	<h2><spring:message code="label.student.observations.edit"></spring:message></h2>
	<spring:message code="label.student.observations.by"></spring:message>:
	${editObservation.versioningUpdatedBy.username}
	<spring:message code="label.student.observations.in"></spring:message>:
	<%=((RegistrationObservations) request.getAttribute("editObservation")).getVersioningUpdateDate().getDate().toString("dd/MM/yyyy hh:mm:ss")%>
	<form id="editform" method="POST" action="${pageContext.request.contextPath}/registrations/${registration.externalId}/observations/${editObservation.externalId}">
	<div>
	<textarea name="observations" rows="15" style="width:100%">
	</textarea>
		<input type="submit" value="<spring:message code="label.edit"/>"/>
	</div>
	</form>
</c:if>


<script>
	<c:if test="${not empty editObservationText}">
		$('textarea').text('${editObservationText}');
	</c:if>
	

	$('.paginated').each(function() {
	    var currentPage = 0;
	    var numPerPage = 5;
	    var $table = $(this);
	    $table.bind('repaginate', function() {
	        $table.find('tbody tr').hide().slice(currentPage * numPerPage, (currentPage + 1) * numPerPage).show();
	    });
	    $table.trigger('repaginate');
	    var numRows = $table.find('tbody tr').length;
	    var numPages = Math.ceil(numRows / numPerPage);
	    var $pager = $('<div class="pager"></div>');
	    for (var page = 0; page < numPages; page++) {
	        $('<button class="page-number"></span>').text(page + 1).bind('click', {
	            newPage: page
	        }, function(event) {
	            currentPage = event.data['newPage'];
	            $table.trigger('repaginate');
	            $(this).addClass('active').siblings().removeClass('active');
	        }).appendTo($pager).addClass('clickable');
	    }
	    $pager.insertBefore($table).find('button.page-number:first').addClass('active');
	});
	
</script>