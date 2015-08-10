<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>
<%@ page isELIgnored="true"%>

<html:xhtml/>

		<li>
			<bean:define id="url7"><%= request.getContextPath() %>/looseevaluation/create/<bean:write name="studentEnrolmentBean" property="studentCurricularPlan.externalId"/></bean:define>
			<html:link href='<%= url7 %>'>Lançamento de Notas Avulsas</html:link>
		</li>
