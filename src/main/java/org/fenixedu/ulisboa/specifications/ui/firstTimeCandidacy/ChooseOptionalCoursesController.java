package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/chooseoptionalcourses")
public class ChooseOptionalCoursesController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String chooseoptionalcourses(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/chooseoptionalcourses";
    }

    @RequestMapping(value = "/opencourseenrollments")
    public String chooseoptionalcoursesToOpenCourseEnrollments(Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        //TODO this is "not properly calculated";
        ExecutionSemester executionSemester = ExecutionYear.readCurrentExecutionYear().getExecutionPeriodsSet().iterator().next();
        Registration registration =
                AccessControl.getPerson().getStudent().getActiveRegistrationsIn(executionSemester).iterator().next();
        String link = "/student/bolonhaStudentEnrollment.do?method=prepare&executionSemesterID=%s&registrationOid=%s";
        String format = String.format(link, executionSemester.getExternalId(), registration.getExternalId());

        //request
        String injectChecksumInUrl =
                GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), format, request.getSession());
        return redirect(injectChecksumInUrl, model, redirectAttributes);
    }

    @RequestMapping(value = "/continue")
    public String chooseoptionalcoursesToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/showselectedcourses", model, redirectAttributes);
    }
}
