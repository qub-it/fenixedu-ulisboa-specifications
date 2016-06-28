<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationInternshipGrade.ManageRegistrationInternshipGradeController"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationEntryGrade.ManageRegistrationEntryGradeController"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationResearchArea.ManageRegistrationResearchAreaController"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.PreviousDegreeManagementController"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.OriginInformationManagementController"%>
<%@page import="org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil"%>
<%@page import="org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord.HouseholdInformationManagementController"%>
<%@page import="org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory"%>

<%@ page isELIgnored="true"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html:xhtml/>

<bean:define id="registration" name="registration" scope="request" type="org.fenixedu.academic.domain.student.Registration"/>

<academic:allowed operation="MANAGE_STUDENT_PAYMENTS">
<% if(TreasuryBridgeAPIFactory.implementation().isPersonAccountTreasuryManagementAvailable(registration.getStudent().getPerson())) { %>
	<span class="dblock pbottom03">	
		<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
		<html:link target="_blank" href="<%= request.getContextPath() + TreasuryBridgeAPIFactory.implementation().getRegistrationAccountTreasuryManagementURL(registration) %>" >
			<bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.payments.management" />
		</html:link>
	</span>
<% } %>

	<span class="dblock pbottom03">	
		<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
		<html:link target="_blank" href="<%= request.getContextPath() + HouseholdInformationManagementController.SEARCH_URL + "/" + registration.getStudent().getExternalId() %>" >
			<%= ULisboaSpecificationsUtil.bundle("label.title.householdinformationmanagement") %>
		</html:link>
	</span>

	<span class="dblock pbottom03">	
		<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
		<html:link target="_blank" href="<%= request.getContextPath() + OriginInformationManagementController.READ_URL + "/" + registration.getExternalId() %>" >
			<%= ULisboaSpecificationsUtil.bundle("label.firstTimeCandidacy.fillOriginInformation") %>
		</html:link>
	</span>

	<span class="dblock pbottom03">	
		<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
		<html:link target="_blank" href="<%= request.getContextPath() + PreviousDegreeManagementController.READ_URL + "/" + registration.getExternalId() %>" >
			<%= ULisboaSpecificationsUtil.bundle("label.firstTimeCandidacy.fillPreviousDegreeInformation") %>
		</html:link>
	</span>


	<span class="dblock pbottom03">	
		<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
		<html:link target="_blank" href="<%= request.getContextPath() + ManageRegistrationResearchAreaController.EDIT_URL  + "/" + registration.getExternalId() %>" >
			<%= ULisboaSpecificationsUtil.bundle("label.ManageRegistrationResearchArea.link") %>
		</html:link>
	</span>

	<span class="dblock pbottom03">	
		<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
		<html:link target="_blank" href="<%= request.getContextPath() + ManageRegistrationEntryGradeController.EDIT_URL  + "/" + registration.getExternalId() %>" >
			<%= ULisboaSpecificationsUtil.bundle("label.ManageRegistrationEntryGrade.link") %>
		</html:link>
	</span>
	
	<span class="dblock pbottom03">	
		<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
		<html:link target="_blank" href="<%= request.getContextPath() + ManageRegistrationInternshipGradeController.EDIT_URL  + "/" + registration.getExternalId() %>" >
			<%= ULisboaSpecificationsUtil.bundle("label.ManageRegistrationInternshipGrade.link") %>
		</html:link>
	</span>

</academic:allowed>
