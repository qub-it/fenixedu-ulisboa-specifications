package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.Candidacy;
import org.fenixedu.academic.domain.candidacy.DegreeCandidacy;
import org.fenixedu.academic.domain.candidacy.IMDCandidacy;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.joda.time.YearMonthDay;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.firstTimeCandidacy")
@RequestMapping(FirstTimeCandidacyController.CONTROLLER_URL)
public class FirstTimeCandidacyController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/home";

    private static final String _INSTRUCTIONS_URI = "/instructions";
    public static final String INSTRUCTIONS_URL = CONTROLLER_URL + _INSTRUCTIONS_URI;

    @RequestMapping
    public String home(Model model) {
        Stream<Candidacy> firstTimeCandidacies =
                AccessControl.getPerson().getCandidaciesSet().stream().filter(firstTimeCandidaciesPredicate);
        long count = firstTimeCandidacies.count();
        if (count == 0) {
            throw new RuntimeException(
                    "Students with no DegreeCandidacies or IMDCandidacies are not supported in the first time registration flow");
        }
        if (count > 1) {
            throw new RuntimeException(
                    "Students with multiple DegreeCandidacies or IMDCandidacies are not supported in the first time registration flow");
        }

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/instructions";
    }

    @RequestMapping(value = "/continue")
    public String instructionsToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/personalinformationform/fillpersonalinformation",
                model, redirectAttributes);
    }

    private static Predicate<Candidacy> firstTimeCandidaciesPredicate =
            c -> ((c instanceof DegreeCandidacy) || (c instanceof IMDCandidacy));

    public static StudentCandidacy getStudentCandidacy() {
        Stream<Candidacy> firstTimeCandidacies =
                AccessControl.getPerson().getCandidaciesSet().stream().filter(firstTimeCandidaciesPredicate);
        return (StudentCandidacy) firstTimeCandidacies.findAny().get();
    }

    @Atomic
    public static PersonalIngressionData getOrCreatePersonalIngressionData(PrecedentDegreeInformation precedentInformation) {
        PersonalIngressionData personalData = null;
        personalData = precedentInformation.getPersonalIngressionData();
        Student student = AccessControl.getPerson().getStudent();
        if (personalData == null) {
            personalData = student.getPersonalIngressionDataByExecutionYear(ExecutionYear.readCurrentExecutionYear());
            if (personalData != null) {
                //if the student already has a PID it will have another PDI associated, it's necessary to add the new PDI
                personalData.addPrecedentDegreesInformations(precedentInformation);
            } else {
                personalData = new PersonalIngressionData(ExecutionYear.readCurrentExecutionYear(), precedentInformation);
            }
        }

        // It is necessary to create an early Registration so that the RAIDES objects are consistent
        // see PrecedentDegreeInformation.checkHasAllRegistrationOrPhdInformation()
        getOrCreateRegistration();

        return personalData;
    }

    private static Registration getOrCreateRegistration() {
        StudentCandidacy studentCandidacy = FirstTimeCandidacyController.getStudentCandidacy();
        Registration registration = studentCandidacy.getRegistration();
        if (registration != null) {
            return registration;
        }
        registration = new Registration(studentCandidacy.getPerson(), studentCandidacy);

        PrecedentDegreeInformation pdi = studentCandidacy.getPrecedentDegreeInformation();
        pdi.setRegistration(registration);
        pdi.getPersonalIngressionData().setStudent(studentCandidacy.getPerson().getStudent());

        DegreeCurricularPlan degreeCurricularPlan = studentCandidacy.getExecutionDegree().getDegreeCurricularPlan();
        ExecutionSemester semester = ExecutionSemester.readActualExecutionSemester();
        StudentCurricularPlan
                .createBolonhaStudentCurricularPlan(registration, degreeCurricularPlan, new YearMonthDay(), semester);

        return registration;
    }
}
