<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<script
  src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js"
  integrity="sha256-VazP97ZCwtekAsvgPBSUwPFKdrwD3unUfSGVYrahUqU="
  crossorigin="anonymous"></script>
<link rel="stylesheet" type="text/css" media="screen" href="https://code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css">

<script type="text/css">//<![CDATA[
.no-close .ui-dialog-titlebar-close {display: none }
//]]></script>

<div id="pleaseWaitContainer" class="selector">
	<div style="text-align: center;" class="color777 italic">
		A processar, por favor aguarde...<br />Evite sair ou refrescar esta página.
		<br />
		<html:img align="middle" src='<%=request.getContextPath() + "/static/lib/fancytree/skin-lion/loading.gif"%>' />
	</div>
</div>

<bean:define id="noContextString">
	A processar, por favor aguarde...<br />Evite sair ou refrescar esta página.
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
		height: 125,
		width: 400
	});
	
//]]></script>
