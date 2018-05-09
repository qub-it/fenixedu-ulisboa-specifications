package org.fenixedu.ulisboa.specifications.ui.teacher;

import static java.util.function.Predicate.isEqual;
import static java.util.stream.Collectors.toList;
import static org.fenixedu.academic.predicate.AccessControl.check;
import static org.fenixedu.academic.predicate.AccessControl.getPerson;
import static pt.ist.fenixframework.FenixFramework.atomic;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.ui.spring.controller.teacher.TeacherView;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.fenixedu.cms.domain.Menu;
import org.fenixedu.cms.domain.MenuItem;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.Atomic;

@Controller
@RequestMapping("/teacher/{executionCourse}/pages")
public class TeacherPagesController extends ExecutionCourseController {

    @Autowired
    PagesAdminService service;

    @RequestMapping(method = RequestMethod.GET)
    public TeacherView all(final Model model, @PathVariable final ExecutionCourse executionCourse) {
        hasAccess(executionCourse);
        model.addAttribute("executionCourse", executionCourse);
        model.addAttribute("professorship", executionCourse.getProfessorship(getPerson()));
        model.addAttribute("site", executionCourse.getSite());
        model.addAttribute("previousExecutionCourses", previousExecutionCourses(executionCourse).collect(toList()));
        return new TeacherView("executionCourse/site/teacherPages", executionCourse);
    }

    @RequestMapping(value = "options", method = RequestMethod.POST)
    public RedirectView editOptions(@PathVariable final ExecutionCourse executionCourse,
            @RequestParam(required = false, defaultValue = "") final String alternativeSite) {
        hasAccess(executionCourse);
        atomic(() -> executionCourse.getSite().setAlternativeSite(alternativeSite));
        return new RedirectView(String.format("/teacher/%s/pages", executionCourse.getExternalId()), true);
    }

    @RequestMapping(value = "copyContent", method = RequestMethod.POST)
    public RedirectView copyContent(@PathVariable final ExecutionCourse executionCourse,
            @RequestParam final ExecutionCourse previousExecutionCourse, final RedirectAttributes redirectAttributes) {
        canCopyContent(executionCourse, previousExecutionCourse);
        try {
            copyContent(previousExecutionCourse.getSite(), executionCourse.getSite());
        } catch (RuntimeException e) {
            LoggerFactory.getLogger(TeacherPagesController.class).error("error importing site content", e);
            //error occurred while importing content
            redirectAttributes.addFlashAttribute("importError", true);
            return new RedirectView(String.format("/teacher/%s/pages", executionCourse.getExternalId()), true);
        }
        return new RedirectView(String.format("/teacher/%s/pages", executionCourse.getExternalId()), true);
    }

    @Atomic
    private void copyContent(final Site from, final Site to) {
        Menu newMenu = to.getMenusSet().stream().findAny().get();
        LocalizedString newPageName = new LocalizedString().with(Locale.getDefault(),
                from.getExecutionCourse().getExecutionPeriod().getQualifiedName());
        MenuItem emptyPageParent = service.create(to, null, newPageName, new LocalizedString(), new LocalizedString()).get();
        emptyPageParent.getPage().setPublished(false);
        emptyPageParent.setTop(newMenu);
        for (Menu oldMenu : from.getMenusSet()) {
            oldMenu.getToplevelItemsSorted().forEach(menuItem -> service.copyStaticPage(menuItem, to, newMenu, emptyPageParent));
        }
    }

    private Stream<ExecutionCourse> previousExecutionCourses(final ExecutionCourse executionCourse) {
        Set<Degree> degrees = executionCourse.getAssociatedCurricularCoursesSet().stream()
                .map(c -> c.getDegreeCurricularPlan().getDegree()).distinct().collect(Collectors.toSet());
        return executionCourse.getCompetenceCourses().stream()
                .flatMap(competence -> competence.getAssociatedCurricularCoursesSet().stream())
                .filter(curricularCourse -> degrees.contains(curricularCourse.getDegreeCurricularPlan().getDegree()))
                .flatMap(curricularCourse -> curricularCourse.getAssociatedExecutionCoursesSet().stream())
                .filter(ec -> ec != executionCourse).filter(ec -> ec.getSite() != null).distinct()
                .sorted(ExecutionCourse.EXECUTION_COURSE_COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME.reversed());
    }

    private void canCopyContent(final ExecutionCourse executionCourse, final ExecutionCourse previousExecutionCourse) {
        hasAccess(executionCourse);
        check(p -> previousExecutionCourses(executionCourse).filter(isEqual(previousExecutionCourse)).findAny().isPresent());
    }

    private void hasAccess(final ExecutionCourse executionCourse) {
        Professorship professorship = executionCourse.getProfessorship(getPerson());
        check(person -> professorship != null && professorship.getPermissions().getSections());
    }

    @Override
    protected Class<?> getFunctionalityType() {
        return ManageExecutionCourseDA.class;
    }

    @Override
    Boolean getPermission(final Professorship prof) {
        return prof.getPermissions().getSections();
    }
}
