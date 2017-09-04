<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<script
  src="https://code.jquery.com/ui/1.9.1/jquery-ui.min.js"
  integrity="sha256-UezNdLBLZaG/YoRcr48I68gr8pb5gyTBM+di5P8p6t8="
  crossorigin="anonymous"></script>
<link rel="stylesheet" type="text/css" media="screen" href="https://code.jquery.com/ui/1.9.1/themes/smoothness/jquery-ui.css">

<script type="text/css">//<![CDATA[
.no-close .ui-dialog-titlebar-close {display: none }
//]]></script>

<div id="pleaseWaitContainer" class="selector">
	<div style="text-align: center;" class="color777 italic">
		<bean:message bundle="APPLICATION_RESOURCES" key="message.pleaseWait.message" />
		<br />
		
		<%-- 
		<html:img align="middle" src="/static/lib/fancytree/skin-lion/loading.gif" module="/" />
		--%>
	</div>
</div>

<bean:define id="noContextString">
	<bean:message bundle="APPLICATION_RESOURCES" key="message.pleaseWait" />
</bean:define>

<bean:define id="selectedTopLevelContainerName">
<%--
	<logic:present name="<%= org.fenixedu.academic.domain.functionalities.FunctionalityContext.CONTEXT_KEY %>">
		<logic:notEmpty  name="<%= org.fenixedu.academic.domain.functionalities.FunctionalityContext.CONTEXT_KEY %>" property="selectedTopLevelContainer">
			<bean:write name="<%= org.fenixedu.academic.domain.functionalities.FunctionalityContext.CONTEXT_KEY %>" property="selectedTopLevelContainer.name"/>
		</logic:notEmpty>
		<logic:empty  name="<%= org.fenixedu.academic.domain.functionalities.FunctionalityContext.CONTEXT_KEY %>" property="selectedTopLevelContainer">
--%>
			<bean:write name="noContextString"/>
<%--
		</logic:empty>
	</logic:present>
--%>
</bean:define>

<script type="text/javascript">//<![CDATA[
                                          
	function openPleaseWaitDialog() {
		$('#pleaseWaitContainer').dialog('open');
	}

	$('#pleaseWaitContainer').dialog({
		title: "<bean:write name="selectedTopLevelContainerName" />",
		autoOpen: false,
		modal: true,
		draggable: false,
		closeOnEscape: false,
		open: function(event, ui) { $(".ui-dialog-titlebar-close").hide(); },
		resizable: false,
		height: 120,
		width: 400
	});
	
//]]></script>
