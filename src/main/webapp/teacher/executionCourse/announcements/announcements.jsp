<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<c:set var="context" scope="session" value="${pageContext.request.contextPath}/teacher/${executionCourse.externalId}/announcements"/>


<h2>
    <spring:message code="label.announcements"/>
    <a href="#" data-toggle="modal" data-target="#createModal" class="btn btn-primary pull-right" role="button">
        <spring:message code="action.create"/>
    </a>
</h2>

<hr />
<c:choose>
    <c:when test="${not empty announcements}">
        <c:forEach var="announcement" items="${announcements}">
            <div class="row">
                <div class="col-sm-10">
                    <h4>
                        <a href="${announcement.address}" target="_blank">${announcement.name.content}</a>
                    </h4>
                    <small>
                        <a href="mailto:<c:out value='${announcement.createdBy.email}'/>"><c:out value="${announcement.createdBy.name}"/></a>
                          -
                        ${announcement.creationDate.toString('dd MMMM yyyy, HH:mm', locale)}
                        <c:if test="${not empty announcement.publicationBegin && announcement.publicationBegin.isAfterNow()}">
                            <span class="badge" style="font-size: 100%"><spring:message code="label.announcement.publication"/> ${announcement.publicationBegin.toString('dd MMMM yyyy, HH:mm', locale)}</span>
                        </c:if>
                    </small>
                    <h5>
                        ${announcement.body.content}
                    </h5>
                </div>

                <div class="btn-group col-sm-2">
                    <div class="pull-right">
                        <button class="btn btn-default" data-toggle="modal" data-target="#editModal${announcement.externalId}">
                            <spring:message code="action.edit"/>
                        </button>
                        <button class="btn btn-danger" onclick="showDeleteConfirmation('${announcement.slug}');">
                            <spring:message code="action.delete"/>
                        </button>
                    </div>
                </div>
            </div>

            <hr />

            <!-- Modal panel for editing an announcement -->
            <div class="modal fade" id="editModal${announcement.externalId}" tabindex="-1" role="dialog" aria-hidden="true">
                <form method="post" action="${context}/${announcement.slug}/edit">
                    ${csrfField}
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">

                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal">
                                    <span aria-hidden="true">&times;</span>
                                    <span class="sr-only"><spring:message code="action.close"/></span>
                                </button>
                                <h4><c:out value="${announcement.name.content}" escapeXml="false"/></h4>
                            </div>

                            <div class="modal-body">
                                <div class="form-group">
                                    <label for="name" class="control-label">
                                        <spring:message code="label.announcement.title"/>
                                    </label>

                                    <input bennu-localized-string required-any name="name" id="name"
                                        placeholder="<spring:message code="label.announcement.title"/>"
                                        value='<c:out value="${announcement.name.json()}"/>'>
                                </div>

                                <div class="form-group">
                                    <label for="body" class="control-label">
                                        <spring:message code="label.announcement.content"/>
                                    </label>

                                    <input bennu-localized-string bennu-html-editor required-any name="body" id="body" data-post-slug="${announcement.slug}"
                                        placeholder="<spring:message code="label.announcement.content"/>"
                                        value='<c:out value="${announcement.body.json()}"/>'>
                                      <br/>
                                </div>
                                <div class="form-group">
                                    <div class="checkbox">
                                        <label class="control-label">
                                            <spring:message code="label.visible" />
                                            <input type="checkbox" name="active" value="true" ${announcement.active ? 'checked="checked"' : ''} />
                                        </label>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="control-label">
                                        <spring:message code="label.announcement.publication.date"/>
                                    </label>
                                    <div class="row">
                                        <div class="col-sm-6">
                                            <input bennu-datetime name="publicationStarts" value="${announcement.publicationBegin}">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" data-dismiss="modal" class="btn btn-default">
                                    <spring:message code="action.cancel"/>
                                </button>
                                <button type="submit" class="btn btn-primary">
                                    <spring:message code="action.save"/>
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </c:forEach>

        <c:if test="${pages > 1}">
            <nav class="text-center">
                <ul class="pagination">
                    <li ${currentPage == 1 ? 'class="disabled"' : ''}>
                        <a href="?page=${currentPage - 1}">&laquo;</a>
                    </li>
                    <c:forEach begin="1" end="${pages}" var="i">
                        <li ${i == currentPage ? 'class="active"' : ''}>
                            <a href="?page=${i}">${i}</a>
                        </li>
                    </c:forEach>
                    <li ${currentPage == pages ? 'class="disabled"' : ''}>
                        <a href="?page=${currentPage + 1}">&raquo;</a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </c:when>

    <c:otherwise>
        <h4><i><spring:message code="label.announcements.emtpy" /></i></h4>
    </c:otherwise>

</c:choose>

<!-- Modal panel for creating an announcement -->
<div class="modal fade" id="createModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form method="post" action="${context}/create">
        ${csrfField}
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">
                            <spring:message code="action.cancel" />
                        </span>
                    </button>
                    <h4><spring:message code="action.create.announcement"/></h4>
                </div>

                <div class="modal-body">
                    <div class="form-group">
                        <label for="name" class="control-label">
                            <spring:message code="label.announcement.title" />
                        </label>

                        <input bennu-localized-string required-any name="name" id="name"
                               placeholder="<spring:message code="label.announcement.title" />">
                    </div>

                    <div class="form-group">
                        <label for="announcementBody" class="control-label">
                            <spring:message code="label.announcement.content" />
                        </label>

                        <input bennu-localized-string bennu-html-editor toolbar="size,style,lists,align,colors,links,table,image,components,undo,fullscreen,source" required-any name="body" id="body"
                                   placeholder="<spring:message code="label.announcement.content" />">
                    </div>
                    <div class="form-group">
                        <div class="checkbox">
                            <label class="control-label">
                                <spring:message code="label.visible" />
                                <input type="checkbox" name="active" value="true" checked="checked" />
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label">
                            <spring:message code="label.announcement.publication.date"/>
                        </label>
                        <div class="row">
                            <div class="col-sm-6">
                                <input bennu-datetime name="publicationStarts">
                            </div>
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" data-dismiss="modal" class="btn btn-default">
                        <spring:message code="action.cancel" />
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <spring:message code="action.save" />
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>

<!-- Modal panel for deleting an announcement -->
<div class="modal fade" id="confirmDeleteModal" tabindex="-1" role="dialog" aria-hidden="true">
    <form id="deleteForm" method="post" action="#">
        ${csrfField}
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span>
                        <span class="sr-only">
                            <spring:message code="action.cancel" />
                        </span>
                    </button>
                    <h4><spring:message code="action.delete" /></h4>
                </div>
                <div class="modal-body">
                    <p><spring:message code="label.announcement.delete.confirmation"/></p>
                </div>
                <div class="modal-footer">
                    <button type="button" data-dismiss="modal" class="btn btn-default">
                        <spring:message code="action.cancel" />
                    </button>
                    <button type="submit" class="btn btn-danger">
                        <spring:message code="action.delete" />
                    </button>
                </div>
            </div>
        </div>
    </form>
</div>

${portal.toolkit()}

<script>
    function submitFiles(files, cb,postslug) {

        var formData = new FormData();
        for (var i = 0; i < files.length; i++) {
            formData.append('attachment', files[i]);
        }


        var xhr = new XMLHttpRequest();
        xhr.open('POST', postslug + 'addFile.json');

        function transferCanceled(event) {

        }

        function transferFailed(event) {

        }

        function transferComplete(event) {
            var objs = JSON.parse(event.currentTarget.response);
            cb(objs.map(function(x){ return x.url }));
        }

        function updateProgress(event) {
            if (event.lengthComputable) {
                var complete = (event.loaded / event.total * 100 | 0);
                //progress.value = progress.innerHTML = complete;
                console.log(complete);
            }
        }

        xhr.addEventListener("progress", updateProgress, false);
        xhr.addEventListener("load", transferComplete, false);
        xhr.addEventListener("error", transferFailed, false);
        xhr.addEventListener("abort", transferCanceled, false);

        xhr.send(formData);
    }
    $("[data-post-slug]").map(function(i,e){
        e = $(e);

        e.data("fileHandler", function(f,cb){
            submitFiles(f,cb,e.data("post-slug"));
        });
    });
</script>

<script>
    function showDeleteConfirmation(announcementSlug) {
        $('#deleteForm').attr('action', '${context}/' + announcementSlug + '/delete');
        $('#confirmDeleteModal').modal('show');
        return false;
    }
</script>
<style>
.modal-backdrop{z-index: 900}.modal{z-index: 1000;}
</style>
