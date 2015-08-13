<%@page import="org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory"%>

<%@ page isELIgnored="true"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>

<html:xhtml/>

<bean:define id="registration" name="registration" scope="request" type="org.fenixedu.academic.domain.student.Registration"/>

<academic:allowed operation="MANAGE_STUDENT_PAYMENTS">
<% if(TreasuryBridgeAPIFactory.implementation().isPersonAccountTreasuryManagementAvailable(registration.getStudent().getPerson())) { %>
	<span class="dblock pbottom03">	
		<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="<bean:message key="dotist_post" bundle="IMAGE_RESOURCES" />" />
		<html:link href="<%= request.getContextPath() + TreasuryBridgeAPIFactory.implementation().getRegistrationAccountTreasuryManagementURL(registration) %>" >
			<bean:message bundle="ACADEMIC_OFFICE_RESOURCES" key="label.payments.management" />
		</html:link>
	</span>
<% } %>
</academic:allowed>
