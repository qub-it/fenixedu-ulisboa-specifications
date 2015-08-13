package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.util.Optional;
import java.util.function.Predicate;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.DgesStudentImportationProcess;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/scheduleclasses")
public class ScheduleClassesController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String scheduleclasses(Model model, RedirectAttributes redirectAttributes) {
        Predicate<? super Registration> hasDgesImportationProcessForCurrentYear =
                DgesStudentImportationProcess.registrationHasDgesImportationProcessForCurrentYear();
        Optional<Registration> findAny =
                AccessControl.getPerson().getStudent().getRegistrationsSet().stream()
                        .filter(hasDgesImportationProcessForCurrentYear).findAny();
        if (findAny.isPresent()) {
            Registration registration = findAny.get();

            Degree degree = registration.getDegree();
            if (degree.getFirstYearRegistrationConfiguration() == null
                    || !degree.getFirstYearRegistrationConfiguration().getRequiresClassesEnrolment()) {
                //School does not require first year classes enrolment
                return scheduleclassesToContinue(model, redirectAttributes);
            }
        } else {
//This should never happen, but strange things happen
            throw new RuntimeException("Functionality only provided for candidates with current dges process");
        }

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/scheduleclasses";
    }

    @RequestMapping(value = "/openshiftenrollments")
    public String scheduleclassesToOpenShiftEnrollments(Model model, RedirectAttributes redirectAttributes) {
//        
        ExecutionSemester executionSemester = ExecutionSemester.readActualExecutionSemester();

        Predicate<? super Registration> hasDgesImportationProcessForCurrentYear =
                DgesStudentImportationProcess.registrationHasDgesImportationProcessForCurrentYear();
        Optional<Registration> findAny =
                AccessControl.getPerson().getStudent().getRegistrationsSet().stream()
                        .filter(hasDgesImportationProcessForCurrentYear).findAny();
        if (findAny.isPresent()) {
            Registration registration = findAny.get();
            String link = "/student/studentShiftEnrollmentManager.do?method=start&executionSemesterID=%s&registrationOID=%s";
            String format = String.format(link, executionSemester.getExternalId(), registration.getExternalId());

            //request
            String injectChecksumInUrl =
                    GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), format, request.getSession());
            return redirect(injectChecksumInUrl, model, redirectAttributes);
        } else {
            //This should never happen, but strange things happen
            throw new RuntimeException("Functionality only provided for candidates with current dges process");
        }
    }

    @RequestMapping(value = "/continue")
    public String scheduleclassesToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/showscheduledclasses", model, redirectAttributes);
    }
}
