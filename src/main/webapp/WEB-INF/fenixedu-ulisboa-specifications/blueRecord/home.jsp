<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
	.start {
		font-size: 14px;
	}
</style>
<div class="page-header fuelux">
	<h1><spring:message code="label.firstTimeCandidacy.instructions" />
	</h1>
</div>
<div class="well start"><spring:message code="label.event.firstTimeCandidacy.start"  /></div>
<div>
	<a class="btn btn-primary start" href="${pageContext.request.contextPath}${nextURL}"><span class="glyphicon glyphicon-play-circle" aria-hidden="true"></span>&nbsp;<spring:message code="label.event.firstTimeCandidacy.start"  /></a>
</div>

