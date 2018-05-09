package org.fenixedu.ulisboa.specifications.ui.teacher;

import static java.lang.String.format;
import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;
import static org.fenixedu.bennu.core.security.Authenticate.getUser;
import static org.fenixedu.cms.domain.Post.CREATION_DATE_COMPARATOR;
import static pt.ist.fenixframework.FenixFramework.atomic;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.spring.controller.teacher.TeacherView;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlet.FileDownloadServlet;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.PermissionEvaluation;
import org.fenixedu.cms.domain.PermissionsArray.Permission;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostFile;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.math.IntMath;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.Atomic;

@Controller
@RequestMapping("/teacher/{executionCourse}/announcements")
public class AnnouncementsAdminController extends ExecutionCourseController {
    private static final LocalizedString ANNOUNCEMENT =
            getLocalizedString("resources.FenixEduLearningResources", "label.announcements");

    private static final int PER_PAGE = 5;

    @RequestMapping(method = RequestMethod.GET)
    public TeacherView all(final Model model, @RequestParam(required = false, defaultValue = "1") int page) {
        Professorship professorship = executionCourse.getProfessorship(AccessControl.getPerson());
        AccessControl.check(person -> professorship != null && professorship.getPermissions().getAnnouncements());
        List<Post> announcements = getAnnouncements(executionCourse.getSite());
        model.addAttribute("executionCourse", executionCourse);
        int pages = IntMath.divide(announcements.size(), PER_PAGE, RoundingMode.CEILING);
        if (page < 1) {
            page = 1;
        }
        if (pages > 0 && page > pages) {
            page = pages;
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("pages", pages);
        model.addAttribute("announcements",
                announcements.stream().skip((page - 1) * PER_PAGE).limit(PER_PAGE).collect(Collectors.toList()));
        model.addAttribute("professorship", professorship);
        return new TeacherView("executionCourse/announcements/announcements", executionCourse);
    }

    @RequestMapping(value = "{postSlug}/delete", method = RequestMethod.POST)
    public RedirectView delete(@PathVariable final String postSlug) {
        Post post = executionCourse.getSite().postForSlug(postSlug);
        atomic(() -> post.delete());
        return viewAll(executionCourse);
    }

    @RequestMapping(value = "{postSlug}/addFile.json", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String addFileJson(final Model model, @PathVariable final ExecutionCourse executionCourse,
            @PathVariable(value = "postSlug") final String slugPost,
            @RequestParam("attachment") final MultipartFile[] attachments) throws IOException {
        Site s = executionCourse.getSite();

        //TODO - review permissions
        PermissionEvaluation.canDoThis(s, Permission.EDIT_POSTS);

        Post p = s.postForSlug(slugPost);
        JsonArray array = new JsonArray();

        Arrays.asList(attachments).stream().map((attachment) -> {
            GroupBasedFile f = null;
            try {
                f = addFile(attachment, p);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("displayname", f.getDisplayName());
            obj.addProperty("filename", f.getFilename());
            obj.addProperty("url", FileDownloadServlet.getDownloadUrl(f));
            return obj;
        }).forEach(x -> array.add(x));

        return array.toString();
    }

    @Atomic
    private GroupBasedFile addFile(final MultipartFile attachment, final Post p) throws IOException {
        GroupBasedFile f = new GroupBasedFile(attachment.getOriginalFilename(), attachment.getOriginalFilename(),
                attachment.getBytes(), Group.anyone());
        int count = (int) p.getEmbeddedFilesSorted().count();
        new PostFile(p, f, true, count);

        return f;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public RedirectView create(@PathVariable final ExecutionCourse executionCourse, @RequestParam final LocalizedString name,
            @RequestParam final LocalizedString body, @RequestParam final LocalizedString excerpt,
            @RequestParam(required = false, defaultValue = "false") final boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) final DateTime publicationStarts)
            throws Exception {
        Site site = executionCourse.getSite();
        atomic(() -> {
            Post post = Post.create(site, null, Post.sanitize(name), Post.sanitize(body), Post.sanitize(excerpt),
                    announcementsCategory(site), active, getUser());
            if (publicationStarts == null) {
                post.setPublicationBegin(null);
                post.setPublicationEnd(null);
            } else {
                post.setPublicationBegin(publicationStarts);
                post.setPublicationEnd(DateTime.now().plusYears(20));
            }
        });
        return viewAll(executionCourse);
    }

    @RequestMapping(value = "{postSlug}/edit", method = RequestMethod.POST)
    public RedirectView edit(@PathVariable final ExecutionCourse executionCourse, @PathVariable final String postSlug,
            @RequestParam final LocalizedString name, @RequestParam final LocalizedString body,
            @RequestParam final LocalizedString excerpt,
            @RequestParam(required = false, defaultValue = "false") final boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) final DateTime publicationStarts) {
        Post post = executionCourse.getSite().postForSlug(postSlug);
        atomic(() -> {
            post.setName(Post.sanitize(name));
            post.setBodyAndExcerpt(body, excerpt);
            post.setActive(active);
            if (publicationStarts == null) {
                post.setPublicationBegin(null);
                post.setPublicationEnd(null);
            } else {
                post.setPublicationBegin(publicationStarts);
                post.setPublicationEnd(DateTime.now().plusYears(20));
            }
        });
        return viewAll(executionCourse);
    }

    private RedirectView viewAll(final ExecutionCourse executionCourse) {
        return new RedirectView(format("/teacher/%s/announcements", executionCourse.getExternalId()), true);
    }

    private List<Post> getAnnouncements(final Site site) {
        return announcementsCategory(site).getPostsSet().stream().sorted(CREATION_DATE_COMPARATOR).collect(Collectors.toList());
    }

    private Category announcementsCategory(final Site site) {
        return site.getOrCreateCategoryForSlug("announcement", ANNOUNCEMENT);
    }

    @Override
    protected Class<?> getFunctionalityType() {
        return ManageExecutionCourseDA.class;
    }

    @Override
    Boolean getPermission(final Professorship prof) {
        return prof.getPermissions().getAnnouncements();
    }
}
