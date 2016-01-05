<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>

	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="<%= request.getParameter("periodTableTitle") %>" /></h3>
		</div>
		<div class="panel-body">
			
			<table id="enrolledPeriodsTable_${periodType}" class="table responsive table-bordered table-hover">
				<thead>
					<tr>
						<th><spring:message code="label.RaidesRequests.academicPeriod" /></th>
						<th><spring:message code="label.RaidesRequests.begin" /></th>
						<th><spring:message code="label.RaidesRequests.end" /></th>
						<th><spring:message code="label.RaidesRequests.enrolledInAcademicPeriod" /></th>
						<th><spring:message code="label.RaidesRequests.enrolmentEctsConstraint" /></th>
						<th><spring:message code="label.RaidesRequests.minEnrolmentEcts" /></th>
						<th><spring:message code="label.RaidesRequests.maxEnrolmentEcts" /></th>
						<th><spring:message code="label.RaidesRequests.enrolmentYearsConstraint" /></th>
						<th><spring:message code="label.RaidesRequests.minEnrolmentYears" /></th>
						<th><spring:message code="label.RaidesRequests.maxEnrolmentYears" /></th>
					</tr>
				</thead>
				
				<tbody>
					<tr ng-repeat="p in object.periods | filter:{periodInputType: '<%= request.getParameter("periodType") %>'}">
						<td>{{ p.academicPeriodQualifiedName }}</td>
						<td>{{ p.begin }}</td>
						<td>{{ p.end }}</td>
						<td>{{ p.enrolledInAcademicPeriod ? '<spring:message code="label.true" />': '<spring:message code="label.false" />' }}</td>
						<td>{{ p.enrolmentEctsConstraint ? '<spring:message code="label.true" />' : '<spring:message code="label.false" />' }}</td>
						<td>{{ p.minEnrolmentEcts }}</td>
						<td>{{ p.maxEnrolmentEcts }}</td>
						<td>{{ p.enrolmentYearsConstraint ? '<spring:message code="label.true" />' : '<spring:message code="label.false" />' }}</td>
						<td>{{ p.minEnrolmentYears }}</td>
						<td>{{ p.maxEnrolmentYears }}</td>
					</tr>
				</tbody>
			</table>
		</div>
		
		<div class="panel-footer">
			<a class="bnt btn-default btn-xs" href="#" onclick="addPeriod(<%= request.getParameter("periodType") %>)">
				<spring:message code="label.RaidesRequests.addPeriod" />
			</a>
			&nbsp;
			<a class="bnt btn-default btn-xs" href="#" ng-click="cleanPeriods('<%= request.getParameter("periodType") %>')">
				<spring:message code="label.RaidesRequests.cleanPeriods" />
			</a>
		</div>
	</div>	
