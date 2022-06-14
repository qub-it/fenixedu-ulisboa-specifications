package org.fenixedu.ulisboa.specifications.ui.degreeStructure;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseInformation;
import org.fenixedu.academic.domain.degreeStructure.CurricularStage;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.ui.struts.action.BolonhaManager.BolonhaManagerApplication.CompetenceCourseManagementApp;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.bennu.core.presentationTier.renderers.autoCompleteProvider.AutoCompleteProvider;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;

@StrutsFunctionality(app = CompetenceCourseManagementApp.class, path = "searchCompetenceCourse",
        titleKey = "label.search.competenceCourses")
@Mapping(module = "bolonhaManager", path = "/degreeStructure/searchCompetenceCourse")
@Forwards({ @Forward(name = "searchCompetenceCourse",
        path = "/degreeStructure/searchCompetenceCourse/searchCompetenceCourses.jsp") })
public class SearchCompetenceCoursesDA extends FenixDispatchAction {

    @EntryPoint
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        final SearchCompetenceCourseBean searchBean =
                getRenderedObject("searchBean") != null ? getRenderedObject("searchBean") : new SearchCompetenceCourseBean();
        RenderUtils.invalidateViewState();
        request.setAttribute("searchBean", searchBean);
        return mapping.findForward("searchCompetenceCourse");
    }

    public ActionForward approveCompetenceCourse(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        final CompetenceCourse competenceCourse = getDomainObject(request, "competenceCourseID");

        try {
            atomic(() -> competenceCourse.setCurricularStage(CurricularStage.APPROVED));
            addActionMessage("success", request, "successAction");
        } catch (DomainException e) {
            addActionMessage("error", request, e.getMessage());
            e.printStackTrace();
        }

        final SearchCompetenceCourseBean searchBean = new SearchCompetenceCourseBean();
        searchBean.setCompetenceCourse(competenceCourse);
        request.setAttribute("searchBean", searchBean);
        return mapping.findForward("searchCompetenceCourse");
    }

    public ActionForward editCompetenceCourseInformation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        final CompetenceCourseInformation competenceCourseInformation = getRenderedObject();
        final SearchCompetenceCourseBean searchBean = new SearchCompetenceCourseBean();
        searchBean.setCompetenceCourse(competenceCourseInformation.getCompetenceCourse());
        request.setAttribute("searchBean", searchBean);
        return mapping.findForward("searchCompetenceCourse");
    }

    public static class SearchCompenceCourseProvider implements AutoCompleteProvider<CompetenceCourse> {

        private static int DEFAULT_SIZE = 50;

        @Override
        public Collection<CompetenceCourse> getSearchResults(Map<String, String> argsMap, String value, int maxCount) {
            final List<CompetenceCourse> result =
                    CompetenceCourse.searchBolonhaCompetenceCourses(value, "").stream().collect(Collectors.toList());
            result.addAll(CompetenceCourse.searchBolonhaCompetenceCourses("", value).stream().collect(Collectors.toList()));
            return result.size() > DEFAULT_SIZE ? result.subList(0, DEFAULT_SIZE - 1) : result;
        }

    }

    public static class SearchCompetenceCourseBean implements Serializable {

        private CompetenceCourse competenceCourse;

        public SearchCompetenceCourseBean() {
        }

        public CompetenceCourse getCompetenceCourse() {
            return competenceCourse;
        }

        public void setCompetenceCourse(CompetenceCourse competenceCourse) {
            this.competenceCourse = competenceCourse;
        }

    }

}