package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.studentEnrolment.bolonha;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.academic.ui.renderers.student.enrollment.bolonha.SpecialSeasonEnrolmentLayout;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;

@Mapping(path = "/specialSeasonBolonhaStudentEnrolment", module = "academicAdministration",
        formBean = "bolonhaStudentEnrollmentForm", functionality = SearchForStudentsDA.class)
@Forwards({
        @Forward(name = "chooseEvaluationSeason",
                path = "/academicAdminOffice/student/enrollment/bolonha/chooseEvaluationSeason.jsp"),
        @Forward(name = "showDegreeModulesToEnrol",
                path = "/academicAdminOffice/student/enrollment/bolonha/showDegreeModulesToEnrol.jsp") })
public class AcademicAdminOfficeSpecialSeasonBolonhaStudentEnrolmentDA extends
        org.fenixedu.academic.ui.struts.action.administrativeOffice.studentEnrolment.bolonha.AcademicAdminOfficeSpecialSeasonBolonhaStudentEnrolmentDA {

    @Override
    public ActionForward prepareChooseEvaluationSeason(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {

        super.prepareChooseEvaluationSeason(mapping, form, request, response);

        request.setAttribute("chooseEvaluationSeasonBean", new SpecialSeasonChooseEvaluationSeasonBean());

        return mapping.findForward("chooseEvaluationSeason");
    }

    @Override
    protected ActionForward prepareShowDegreeModulesToEnrol(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response, StudentCurricularPlan studentCurricularPlan, ExecutionInterval executionInterval,
            final EvaluationSeason evaluationSeason) {

        super.prepareShowDegreeModulesToEnrol(mapping, form, request, response, studentCurricularPlan, executionInterval,
                evaluationSeason);

        request.setAttribute("enrolmentLayoutClassName", SpecialSeasonEnrolmentLayout.class.getName());

        return mapping.findForward("showDegreeModulesToEnrol");
    }

    @Override
    protected String getAction() {
        return "/specialSeasonBolonhaStudentEnrolment.do";
    }

    @SuppressWarnings("serial")
    static private class SpecialSeasonChooseEvaluationSeasonBean
            extends org.fenixedu.academic.dto.student.enrollment.bolonha.SpecialSeasonChooseEvaluationSeasonBean {

        @Override
        public Collection<EvaluationSeason> getActiveEvaluationSeasons() {
            return EvaluationSeasonServices.findByActive(true).filter(i -> i.isSpecial())
                    .sorted(EvaluationSeasonServices.SEASON_ORDER_COMPARATOR).collect(Collectors.toList());
        }
    }

}
