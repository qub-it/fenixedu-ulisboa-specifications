<%@page import="org.fenixedu.ulisboa.specifications.ui.evaluation.managelooseevaluation.LooseEvaluationController"%>
<%@page import="org.fenixedu.commons.i18n.I18N"%>
<%@page import="org.fenixedu.bennu.core.i18n.BundleUtil"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/taglib/academic" prefix="academic" %>
<%@ page isELIgnored="true"%>

<html:xhtml/>

		<li>
			<bean:define id="url7"><%= request.getContextPath() %><%=LooseEvaluationController.CREATE_URL%><bean:write name="studentEnrolmentBean" property="studentCurricularPlan.externalId"/>/<bean:write name="studentEnrolmentBean" property="executionPeriod.externalId"/></bean:define>
			<%-- Ugly hack to force ulisboa specification bundle (this page is included inside academic, which is core) --%>
			<html:link href='<%= url7 %>' target="_blank"><%= BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources",I18N.getLocale(), "label.LooseEvaluationBean") %></html:link>
			
		</li>
