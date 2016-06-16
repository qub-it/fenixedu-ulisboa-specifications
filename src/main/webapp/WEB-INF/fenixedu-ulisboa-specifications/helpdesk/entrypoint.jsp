<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<spring:url var="datatablesUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
    value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
    value="/CSS/dataTables/dataTables.bootstrap.min.css" />
    
${portal.toolkit()}

<div class="modal fade" id="helpdeskModal"></div>
<a id="openHelpdesk" href="javascript:" style="display:none;"></a>

<script type="text/javascript">
	/*(function() {
		$('#helpdeskModal').load('${themePath}/helpdeskReport.jsp');
		$('#helpdeskModal').modal({
			backdrop: 'static',
			keyboard: false
		});
		window.current$functionality = '${functionalityOid}';
	})();*/
	$( document ).ready( function () {
		setTimeout( function () {
			$('#openHelpdesk').click();
		}, 100);
	});
</script>

