<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<h3><spring:message code="label.student.observations"/></h3>
<div>
	<c:if test="${saved}">
		<div class="alert alert-info" role="alert">
			<spring:message code="label.saved"/>
		</div>
	</c:if>
</div>
<div>
<div class="well well-sm" style="display:inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a class="" href="${returnURL}"><spring:message code="label.back"/></a>	
</div>
</div>
<form method="POST">
	<div>
	<textarea name="observations" rows="15" style="width:100%">
	</textarea>
	</div>
	<div>
	<input type="submit" value="<spring:message code="label.submit"/>"/>
	</div>
</form>
<script>
	<c:if test="${not empty observations}">
		$('textarea').text("${observations}");
	</c:if>
</script>