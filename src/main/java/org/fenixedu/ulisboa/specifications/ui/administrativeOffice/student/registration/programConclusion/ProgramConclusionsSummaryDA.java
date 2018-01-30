package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.student.registration.programConclusion;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionServices;

@Mapping(path = "/programConclusionsSummary", module = "academicAdministration", functionality = SearchForStudentsDA.class)
@Forwards({ @Forward(name = "viewProgramConclusionsSummary",
        path = "/academicAdminOffice/student/registration/programConclusion/viewProgramConclusionsSummary.jsp") })
public class ProgramConclusionsSummaryDA extends FenixDispatchAction {

    public ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        request.setAttribute("studentCurricularPlan", getStudentCurricularPlan(request));
        request.setAttribute("conclusionBeans", getConclusionBeans(request));

        return mapping.findForward("viewProgramConclusionsSummary");

    }

    private List<RegistrationConclusionBean> getConclusionBeans(final HttpServletRequest request) {
        final StudentCurricularPlan curricularPlan = getStudentCurricularPlan(request);
        return RegistrationConclusionServices.getProgramConclusionProcesses(curricularPlan).stream()
                .sorted((x, y) -> x.getConclusionDate().compareTo(y.getConclusionDate()))
                .map(cp -> new RegistrationConclusionBean(curricularPlan, cp.getGroup().getDegreeModule().getProgramConclusion()))
                .collect(Collectors.toList());
    }

    private StudentCurricularPlan getStudentCurricularPlan(HttpServletRequest request) {
        return getDomainObject(request, "scpID");
    }

}
