package org.fenixedu.ulisboa.specifications.ui.teacher;

import static pt.ist.fenixframework.FenixFramework.getDomainObject;

import java.io.IOException;
import java.util.Optional;

import org.fenixedu.bennu.core.domain.exceptions.BennuCoreDomainException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.ist.fenixframework.FenixFramework;

@RestController
@RequestMapping("/pages/{siteId}/admin")
public class PagesAdminController {

    private static final String JSON_VALUE = "application/json; charset=utf-8";

    @Autowired
    PagesAdminService service;

    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = JSON_VALUE)
    public @ResponseBody String data(@PathVariable final String siteId) {
        return service.serialize(site(siteId)).toString();
    }

    @RequestMapping(value = "/data/{menuItem}", method = RequestMethod.GET, produces = JSON_VALUE)
    public @ResponseBody String data(@PathVariable final String siteId, @PathVariable final MenuItem menuItem) {
        return service.data(site(siteId), menuItem).toString();
    }

    @RequestMapping(value = "/dataExcerpt/{menuItem}", method = RequestMethod.GET, produces = JSON_VALUE)
    public @ResponseBody String dataExcerpt(@PathVariable final String siteId, @PathVariable final MenuItem menuItem) {
        return service.dataExcerpt(site(siteId), menuItem).toString();
    }

    @RequestMapping(method = RequestMethod.POST, consumes = JSON_VALUE)
    public @ResponseBody String create(@PathVariable final String siteId, @RequestBody final String bodyJson) {
        PagesAdminBean bean = new PagesAdminBean(bodyJson);
        Site site = site(siteId);
        Optional<MenuItem> menuItem = service.create(site, bean.getParent(), bean.getTitle(), bean.getBody(), bean.getExcerpt());
        return service.serialize(menuItem.get(), true).toString();
    }

    @RequestMapping(value = "/{menuItemId}", method = RequestMethod.DELETE)
    public @ResponseBody String delete(@PathVariable final String siteId, @PathVariable final String menuItemId) {
        service.delete(getDomainObject(menuItemId));
        return data(siteId);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = JSON_VALUE)
    public @ResponseBody String edit(@RequestBody final String bodyJson) {
        PagesAdminBean bean = new PagesAdminBean(bodyJson);
        MenuItem menuItem = service.edit(bean.getMenuItem(), bean.getTitle(), bean.getBody(), bean.getExcerpt(),
                bean.getCanViewGroup(), bean.isVisible());
        return service.serialize(menuItem, true).toString();
    }

    @RequestMapping(value = "{menuItemId}/addFile.json", method = RequestMethod.POST)
    public @ResponseBody String addFileJson(@PathVariable("menuItemId") final String menuItemId,
            @RequestParam("file") final MultipartFile file) throws IOException {
        MenuItem menuItem = FenixFramework.getDomainObject(menuItemId);
        GroupBasedFile addedFile = service.addPostFile(file, menuItem);
        return service.describeFile(menuItem.getPage(), addedFile).toString();
    }

    @RequestMapping(value = "/move", method = RequestMethod.PUT, consumes = JSON_VALUE)
    public @ResponseBody String move(@RequestBody final String bodyJson) {
        JsonObject json = new JsonParser().parse(bodyJson).getAsJsonObject();
        MenuItem item = getDomainObject(json.get("menuItemId").getAsString());
        MenuItem parent = getDomainObject(json.get("parent").getAsString());
        MenuItem insertAfter =
                getDomainObject(json.get("insertAfter").isJsonNull() ? null : json.get("insertAfter").getAsString());
        service.moveTo(item, parent, insertAfter);
        return service.serialize(item, false).toString();
    }

    @RequestMapping(value = "/attachment/{menuItemId}", method = RequestMethod.POST)
    public @ResponseBody String addAttachments(@PathVariable("menuItemId") final String menuItemId,
            @RequestParam("file") final MultipartFile file) throws IOException {
        service.addAttachment(file.getOriginalFilename(), file, getDomainObject(menuItemId));
        return getAttachments(menuItemId);
    }

    @RequestMapping(value = "/attachment/{menuItemId}/{fileId}", method = RequestMethod.DELETE, produces = JSON_VALUE)
    public @ResponseBody String deleteAttachments(@PathVariable final String menuItemId, @PathVariable final String fileId) {
        MenuItem menuItem = getDomainObject(menuItemId);
        GroupBasedFile postFile = getDomainObject(fileId);
        service.delete(menuItem, postFile);
        return getAttachments(menuItemId);
    }

    @RequestMapping(value = "/attachments", method = RequestMethod.GET)
    public @ResponseBody String getAttachments(@RequestParam(required = true) final String menuItemId) {
        MenuItem menuItem = getDomainObject(menuItemId);
        return service.serializeAttachments(menuItem.getPage()).toString();
    }

    @RequestMapping(value = "/attachment", method = RequestMethod.PUT)
    public @ResponseBody String updateAttachment(@RequestBody final String bodyJson) {
        JsonObject updateMessage = new JsonParser().parse(bodyJson).getAsJsonObject();
        MenuItem menuItem = getDomainObject(updateMessage.get("menuItemId").getAsString());
        GroupBasedFile attachment = getDomainObject(updateMessage.get("fileId").getAsString());
        service.updateAttachment(menuItem, attachment, updateMessage.get("position").getAsInt(),
                updateMessage.get("group").getAsInt(), updateMessage.get("name").getAsString(),
                updateMessage.get("visible").getAsBoolean());
        return getAttachments(menuItem.getExternalId());
    }

    @ModelAttribute("site")
    private Site site(@PathVariable final String siteId) {
        Site site = getDomainObject(siteId);
        if (!FenixFramework.isDomainObjectValid(site)) {
            throw BennuCoreDomainException.resourceNotFound(siteId);
        }

        if (site.getExecutionCourse() != null) {
            if (site.getExecutionCourse().getProfessorshipForCurrentUser() == null) {
                throw CmsDomainException.forbiden();
            }
        } else if (!PermissionEvaluation.canAccess(Authenticate.getUser(), site)) {
            throw CmsDomainException.forbiden();
        }

        return site;
    }
}
