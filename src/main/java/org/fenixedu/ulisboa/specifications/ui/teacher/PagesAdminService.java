package org.fenixedu.ulisboa.specifications.ui.teacher;

import static java.util.Comparator.comparing;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.accessControl.StudentGroup;
import org.fenixedu.academic.domain.accessControl.StudentSharingDegreeOfCompetenceOfExecutionCourseGroup;
import org.fenixedu.academic.domain.accessControl.StudentSharingDegreeOfExecutionCourseGroup;
import org.fenixedu.academic.domain.accessControl.TeacherGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.io.domain.GroupBasedFile;
import org.fenixedu.bennu.io.servlet.FileDownloadServlet;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.Post;
import org.fenixedu.cms.domain.PostFile;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.Component;
import org.fenixedu.cms.domain.component.StaticPost;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Service
public class PagesAdminService {

    private final Predicate<MenuItem> isStaticPage = menuItem -> menuItem.getPage() != null
            && menuItem.getPage().getComponentsSet().stream().filter(StaticPost.class::isInstance)
                    .map(component -> ((StaticPost) component).getPost()).filter(post -> post != null).findFirst().isPresent();

    protected static Stream<Page> dynamicPages(final Site site) {
        return site.getPagesSet().stream().filter(PagesAdminService::isDynamicPage)
                .filter(page -> !site.getInitialPage().equals(page)).sorted(comparing(Page::getName));
    }

    protected static boolean isDynamicPage(final Page page) {
        return !page.getComponentsSet().stream().filter(StaticPost.class::isInstance).findAny().isPresent();
    }

    static List<Group> permissionGroups(final Site site) {
        if (site.getExecutionCourse() != null) {
            //TODO: review if still makes sense
            return getContextualPermissionGroups(site);
        }

        return ImmutableList.of(Group.anyone(), Group.logged());
    }

    //TODO - this was a method initially in ExecutionCourseSite
    public static List<Group> getContextualPermissionGroups(final Site site) {
        List<Group> groups = Lists.newArrayList();
        groups.add(Group.anyone());
        groups.add(Group.logged());
        groups.add(TeacherGroup.get(site.getExecutionCourse()));
        groups.add(TeacherGroup.get(site.getExecutionCourse()).or(StudentGroup.get(site.getExecutionCourse())));
        groups.add(StudentSharingDegreeOfExecutionCourseGroup.get(site.getExecutionCourse()));
        groups.add(StudentSharingDegreeOfCompetenceOfExecutionCourseGroup.get(site.getExecutionCourse()));
        return groups;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected void delete(final MenuItem menuItem) {
        //recursive call to remove associated childrens
        menuItem.getChildrenSorted().forEach(this::delete);
        //deleting a page allready deletes all the associated menu items and components
        menuItem.getPage().delete();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected Optional<MenuItem> create(final Site site, final MenuItem parent, final LocalizedString name,
            final LocalizedString body, final LocalizedString excerpt) {
        Menu menu = site.getMenusSet().stream().findFirst().orElse(null);
        Page page = Page.create(site, menu, parent, Post.sanitize(name), true, "view", Authenticate.getUser());
        Category category = site.getOrCreateCategoryForSlug("content", new LocalizedString().with(I18N.getLocale(), "Content"));
        Post post = Post.create(site, page, Post.sanitize(name), Post.sanitize(body), Post.sanitize(excerpt), category, true,
                Authenticate.getUser());
        page.addComponents(new StaticPost(post));
        MenuItem menuItem = page.getMenuItemsSet().stream().findFirst().get();
        if (parent != null) {
            parent.add(menuItem);
        } else {
            menu.add(menuItem);
        }
        return Optional.of(menuItem);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected MenuItem edit(final MenuItem menuItem, LocalizedString name, LocalizedString body, LocalizedString excerpt,
            final Group canViewGroup, final Boolean visible) {
        name = Post.sanitize(name);
        body = Post.sanitize(body);
        excerpt = Post.sanitize(excerpt);
        if (!menuItem.getName().equals(name)) {
            menuItem.setName(name);
        }
        Post post = postForPage(menuItem.getPage());

        if (visible != null) {
            menuItem.getPage().setPublished(visible);
        }

        if (!menuItem.getPage().getName().equals(name)) {
            menuItem.getPage().setName(name);
        }

        if (post.getBody() == null && body != null || post.getBody() != null && !post.getBody().equals(body)) {
            post.setBody(body);
        }

        if (post.getExcerpt() == null && excerpt != null || post.getExcerpt() != null && !post.getExcerpt().equals(excerpt)) {
            post.setBodyAndExcerpt(body, excerpt);
        }

        if (!post.getName().equals(name)) {
            post.setName(name);
        }

        if (canViewGroup != null && !post.getCanViewGroup().equals(canViewGroup)) {
            post.setCanViewGroup(canViewGroup);
        }

        return menuItem;
    }

    @Atomic(mode = TxMode.WRITE)
    protected void moveTo(final MenuItem item, final MenuItem parent, MenuItem insertAfter) {
        Menu menu = item.getMenu();

        if (insertAfter == null && parent == null) {
            insertAfter = getLastBuiltinContent(menu);
        }

        if (parent == null) {
            MenuItem.fixOrder(menu.getToplevelItemsSorted().collect(Collectors.toList()));
            int newPosition = insertAfter == null ? 0 : insertAfter.getPosition() + 1;
            menu.putAt(item, newPosition);
        } else {
            MenuItem.fixOrder(parent.getChildrenSorted());
            int newPosition = insertAfter == null ? 0 : insertAfter.getPosition() + 1;
            parent.putAt(item, newPosition);
        }
    }

    private MenuItem getLastBuiltinContent(final Menu menu) {
        return menu.getToplevelItemsSorted().sorted(Comparator.reverseOrder()).filter(isStaticPage.negate()).findFirst()
                .orElse(null);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    protected GroupBasedFile addAttachment(final String name, final MultipartFile attachment, final MenuItem menuItem)
            throws IOException {
        Post post = postForPage(menuItem.getPage());
        GroupBasedFile file = new GroupBasedFile(name, attachment.getOriginalFilename(), attachment.getBytes(), Group.anyone());
        PostFile postFile = new PostFile(post, file, false, 0);
        post.addFiles(postFile);
        return file;
    }

    private Post postForPage(final Page page) {
        return page.getComponentsSet().stream().filter(component -> component instanceof StaticPost)
                .map(component -> ((StaticPost) component).getPost()).filter(post -> post != null).findFirst().get();
    }

    protected JsonObject serialize(final Site site) {
        JsonObject data = new JsonObject();
        if (!site.getMenusSet().isEmpty()) {
            Menu menu = site.getMenusSet().stream().findFirst().get();
            JsonObject root = new JsonObject();
            root.add("title", site.getName().json());
            root.add("root", new JsonPrimitive(true));
            root.add("isFolder", new JsonPrimitive(true));
            root.add("expanded", new JsonPrimitive(true));
            root.add("key", new JsonPrimitive("null"));

            JsonArray groupsJson = new JsonArray();
            for (Group group : permissionGroups(site)) {
                groupsJson.add(serializeGroup(group));
            }

            JsonArray child = new JsonArray();
            menu.getToplevelItemsSorted().filter(isStaticPage).map(item -> serialize(item, false))
                    .forEach(json -> child.add(json));
            root.add("children", child);
            data.add("root", root);
            data.add("groups", groupsJson);
        }
        return data;
    }

    protected JsonObject serialize(final MenuItem item, final boolean withBody) {
        JsonObject root = new JsonObject();

        root.add("title", item.getName().json());
        if (item.getParent() != null) {
            root.add("menuItemParentId", new JsonPrimitive(item.getParent().getExternalId()));
        }
        root.add("key", new JsonPrimitive(item.getExternalId()));
        String pageAddress = Optional.ofNullable(item.getUrl()).orElse(item.getPage().getAddress());
        root.add("pageAddress", new JsonPrimitive(pageAddress));
        root.add("position", new JsonPrimitive(item.getPosition()));
        root.add("isFolder", new JsonPrimitive(Optional.ofNullable(item.getFolder()).orElse(false)));
        root.addProperty("visible", item.getPage().isPublished());

        if (withBody) {
            root.add("body", data(item.getMenu().getSite(), item));
            root.add("excerpt", dataExcerpt(item.getMenu().getSite(), item));
        }

        root.add("files", serializeAttachments(item.getPage()));

        if (item.getChildrenSet().size() > 0) {
            root.add("folder", new JsonPrimitive(true));
            JsonArray children = new JsonArray();
            item.getChildrenSorted().stream().filter(isStaticPage).forEach(subitem -> children.add(serialize(subitem, false)));
            root.add("children", children);
        }
        root.addProperty("canViewGroupIndex", canViewGroupIndex(item.getPage(), postForPage(item.getPage()).getCanViewGroup()));

        return root;
    }

    private Integer canViewGroupIndex(final Page page, final Group group) {
        List<Group> permissionGroups = permissionGroups(page.getSite());
        for (int i = 0; i < permissionGroups.size(); ++i) {
            if (permissionGroups.get(i).equals(group)) {
                return i;
            }
        }
        return 0;
    }

    private JsonObject serializeGroup(final Group group) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", group.getPresentationName());
        jsonObject.addProperty("expression", group.getExpression());
        return jsonObject;
    }

    protected JsonElement serializeAttachments(final Page page) {
        //TODO: review if it makes sense, since now postfiles and attachments were merged
        Post post = postForPage(page);
        JsonArray filesJson = new JsonArray();
        for (GroupBasedFile postFile : post.getAttachmentFilesSorted().map(PostFile::getFiles).collect(Collectors.toList())) {
            JsonObject json = describeFile(page, postFile);
            json.addProperty("visible", true);
            filesJson.add(json);
        }
        if (filesJson.size() > 0) {
            filesJson.get(filesJson.size() - 1).getAsJsonObject().addProperty("last", true);
        }
        for (GroupBasedFile postFile : post.getEmbeddedFilesSorted().map(PostFile::getFiles).collect(Collectors.toList())) {
            JsonObject json = describeFile(page, postFile);
            json.addProperty("visible", false);
            filesJson.add(json);
        }
        return filesJson;
    }

    protected JsonObject describeFile(final Page page, final GroupBasedFile file) {
        JsonObject postFileJson = new JsonObject();
        postFileJson.addProperty("name", file.getDisplayName());
        postFileJson.addProperty("filename", file.getFilename());
        postFileJson.addProperty("externalId", file.getExternalId());
        postFileJson.addProperty("creationDate", file.getCreationDate().toString());
        postFileJson.addProperty("contentType", file.getContentType());
        postFileJson.addProperty("size", file.getSize());
        postFileJson.addProperty("downloadUrl", FileDownloadServlet.getDownloadUrl(file));
        postFileJson.addProperty("group", canViewGroupIndex(page, file.getAccessGroup()));
        return postFileJson;
    }

    @Atomic
    protected GroupBasedFile addPostFile(final MultipartFile attachment, final MenuItem menuItem) throws IOException {
        GroupBasedFile f = new GroupBasedFile(attachment.getOriginalFilename(), attachment.getOriginalFilename(),
                attachment.getBytes(), Group.anyone());
        int size = postForPage(menuItem.getPage()).getFilesSet().size();
        //TODO: review if it makes sense, since now postfiles and attachments were merged
        postForPage(menuItem.getPage()).addFiles(new PostFile(postForPage(menuItem.getPage()), f, false, size));
        return f;
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void delete(final MenuItem menuItem, final GroupBasedFile file) {
        //TODO: review if it makes sense, since now postfiles and attachments were merged
        Post post = postForPage(menuItem.getPage());

        List<PostFile> postFiles = post.getAttachmentFilesSorted().collect(Collectors.toList());
        int attachmentPosition = postFiles.stream().map(PostFile::getFiles).filter(f -> f == file)
                .map(f -> f.getPostFile().getIndex()).findAny().orElse(-1);
        if (attachmentPosition != -1) {
            postFiles.remove(attachmentPosition);
            file.delete();
        }
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void updateAttachment(final MenuItem menuItem, final GroupBasedFile attachment, final int newPosition,
            final int groupPosition, final String displayName, final boolean visible) {
        if (displayName != null) {
            attachment.setDisplayName(displayName);
        }
        attachment.setAccessGroup(permissionGroups(menuItem.getMenu().getSite()).get(groupPosition));

        Post post = postForPage(menuItem.getPage());

        Stream<PostFile> postFileSorted = visible ? post.getAttachmentFilesSorted() : post.getEmbeddedFilesSorted();

        List<PostFile> result = postFileSorted.collect(Collectors.toList());
        PostFile postFile = postFileSorted.filter(p -> p.getFiles() == attachment).findAny().orElse(null);

        if (postFile != null) {
            int currentPosition = postFile.getIndex();
            if (currentPosition != newPosition) {
                result.remove(postFile);
                result.add(newPosition, postFile);
                post.fixOrder(result);
            }
        } else {
            result.add(newPosition, new PostFile(post, attachment, !visible, newPosition));
            post.fixOrder(result);
        }
    }

    protected void copyStaticPage(final MenuItem oldMenuItem, final Site newSite, final Menu newMenu, final MenuItem newParent) {
        if (oldMenuItem.getPage() != null) {
            Page oldPage = oldMenuItem.getPage();
            staticPost(oldPage).ifPresent(oldPost -> {
                Page newPage = new Page(newSite, oldPage.getName());
                newPage.setTemplate(newSite.getTheme().templateForType(oldPage.getTemplate().getType()));
                newPage.setCreatedBy(Authenticate.getUser());
                newPage.setPublished(false);

                for (Component component : oldPage.getComponentsSet()) {
                    if (component instanceof StaticPost) {
                        StaticPost staticPostComponent = (StaticPost) component;
                        Post newPost = clonePost(staticPostComponent.getPost(), newSite);
                        newPost.setActive(true);
                        StaticPost newComponent = new StaticPost(newPost);
                        newPage.addComponents(newComponent);
                    }
                }

                MenuItem newMenuItem = MenuItem.create(newMenu, newPage, oldMenuItem.getName(), newParent);
                newMenuItem.setPosition(oldMenuItem.getPosition());
                newMenuItem.setUrl(oldMenuItem.getUrl());
                newMenuItem.setFolder(oldMenuItem.getFolder());

                oldMenuItem.getChildrenSet().stream().forEach(child -> copyStaticPage(child, newSite, newMenu, newMenuItem));
            });
        }
    }

    private Post clonePost(final Post oldPost, final Site newSite) {
        Post newPost = new Post(newSite);
        newPost.setName(oldPost.getName());
        newPost.setBodyAndExcerpt(oldPost.getBody(), oldPost.getExcerpt());
        newPost.setCreationDate(new DateTime());
        newPost.setCreatedBy(Authenticate.getUser());
        newPost.setActive(oldPost.getActive());

        for (Category oldCategory : oldPost.getCategoriesSet()) {
            Category newCategory = newSite.getOrCreateCategoryForSlug(oldCategory.getSlug(), oldCategory.getName());
            newPost.addCategories(newCategory);
        }

        //TODO review and do only one cycle
        int i = 0;
        for (GroupBasedFile groupBasedFile : oldPost.getAttachmentFilesSorted().map(PostFile::getFiles)
                .collect(Collectors.toList())) {
            GroupBasedFile attachmentCopy = new GroupBasedFile(groupBasedFile.getDisplayName(), groupBasedFile.getFilename(),
                    groupBasedFile.getContent(), Group.anyone());
            new PostFile(newPost, attachmentCopy, false, i++);
        }

        i = 0;
        for (GroupBasedFile groupBasedFile : oldPost.getEmbeddedFilesSorted().map(PostFile::getFiles)
                .collect(Collectors.toList())) {
            GroupBasedFile attachmentCopy = new GroupBasedFile(groupBasedFile.getDisplayName(), groupBasedFile.getFilename(),
                    groupBasedFile.getContent(), Group.anyone());
            new PostFile(newPost, attachmentCopy, true, i++);
        }

        return newPost;
    }

    private Optional<Post> staticPost(final Page page) {
        return page.getComponentsSet().stream().filter(StaticPost.class::isInstance).map(StaticPost.class::cast)
                .map(StaticPost::getPost).findFirst();
    }

    public JsonElement data(final Site site, final MenuItem item) {
        return postForPage(item.getPage()).getBody() != null ? postForPage(item.getPage()).getBody().json() : new JsonObject();
    }

    public JsonElement dataExcerpt(final Site site, final MenuItem item) {
        return postForPage(item.getPage()).getExcerpt() != null ? postForPage(item.getPage()).getExcerpt()
                .json() : new JsonObject();
    }
}
