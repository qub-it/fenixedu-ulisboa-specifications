package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.lists;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.services.ExecutionCourseServices;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.studentListByCurricularCourse",
        accessGroup = "academic(STUDENT_LISTINGS)")
@RequestMapping(StudentsListByCurricularCourseController.CONTROLLER_URL)
public class StudentsListByCurricularCourseController extends FenixeduUlisboaSpecificationsBaseController {
    public static final String CONTROLLER_URL = "studentsListByCurricularCourse";

    @RequestMapping
    public String home(Model model) {
        model.addAttribute("executionSemesters", Bennu.getInstance().getExecutionPeriodsSet().stream()
                .sorted(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR.reversed()).collect(Collectors.toList()));
        return "academicOffice/lists/studentsByCurricularCourses";
    }

    @RequestMapping(value = "/executionSemesters/{executionSemester}", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getAvailableDegreesForExecutionSemester(
            @PathVariable("executionSemester") ExecutionSemester executionSemester) {
        JsonArray result = new JsonArray();

        executionSemester.getExecutionYear().getExecutionDegreesSet().stream()
                .sorted(ExecutionDegree.EXECUTION_DEGREE_COMPARATORY_BY_DEGREE_TYPE_AND_NAME)
                .forEach(ed -> addDegree(result, ed));

        return new GsonBuilder().create().toJson(result);
    }

    private void addDegree(JsonArray response, ExecutionDegree executionDegree) {
        JsonObject degreeJson = new JsonObject();
        degreeJson.addProperty("text",
                "(" + executionDegree.getDegree().getCode() + ") " + executionDegree.getPresentationName());
        degreeJson.addProperty("id", executionDegree.getExternalId());
        response.add(degreeJson);
    }

    @RequestMapping(value = "/executionSemesters/{executionSemester}/{executionDegree}/classes", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getAvailableClassesForDegreeInExecutionSemester(
            @PathVariable("executionSemester") ExecutionSemester executionSemester,
            @PathVariable("executionDegree") ExecutionDegree executionDegree) {
        JsonArray result = new JsonArray();
        executionDegree.getSchoolClassesSet().stream().filter(sc -> sc.getExecutionPeriod() == executionSemester)
                .sorted(SchoolClass.COMPARATOR_BY_NAME).distinct().forEach(sc -> addSchooClass(result, sc));
        return new GsonBuilder().create().toJson(result);
    }

    private void addSchooClass(JsonArray result, SchoolClass sc) {
        JsonObject schoolClassJson = new JsonObject();
        schoolClassJson.addProperty("text", sc.getEditablePartOfName().toString());
        schoolClassJson.addProperty("id", sc.getExternalId());
        result.add(schoolClassJson);
    }

    @RequestMapping(value = "/executionSemesters/{executionSemester}/{executionDegree}/courses", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getAvailableCoursesForDegreeInExecutionSemester(
            @PathVariable("executionSemester") ExecutionSemester executionSemester,
            @PathVariable("executionDegree") ExecutionDegree executionDegree) {
        JsonArray result = new JsonArray();
        executionDegree.getDegreeCurricularPlan().getExecutionCourses(executionSemester).stream()
                .sorted(ExecutionCourse.EXECUTION_COURSE_NAME_COMPARATOR).forEach(ec -> addExecutionCourse(result, ec));
        return new GsonBuilder().create().toJson(result);
    }

    @RequestMapping(value = "/executionSemesters/{executionSemester}/courses", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getAvailableCoursesForExecutionSemester(
            @PathVariable("executionSemester") ExecutionSemester executionSemester) {
        JsonArray result = new JsonArray();
        executionSemester.getAssociatedExecutionCoursesSet().stream().sorted(ExecutionCourse.EXECUTION_COURSE_NAME_COMPARATOR)
                .forEach(ec -> addExecutionCourse(result, ec));
        return new GsonBuilder().create().toJson(result);
    }

    private void addExecutionCourse(JsonArray result, ExecutionCourse ec) {
        JsonObject schoolClassJson = new JsonObject();
        schoolClassJson.addProperty("text", "(" + ExecutionCourseServices.getCode(ec) + ") " + ec.getName());
        schoolClassJson.addProperty("id", ec.getExternalId());
        result.add(schoolClassJson);
    }

    @RequestMapping(value = "/executionCourse/{executionCourse}", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getAvailableShiftsForExecutionCourse(
            @PathVariable("executionCourse") ExecutionCourse executionCourse) {
        JsonArray result = new JsonArray();
        executionCourse.getAssociatedShifts().stream().sorted(Shift.SHIFT_COMPARATOR_BY_NAME).forEach(s -> addShift(result, s));;
        return new GsonBuilder().create().toJson(result);
    }

    private void addShift(JsonArray result, Shift s) {
        JsonObject schoolClassJson = new JsonObject();
        schoolClassJson.addProperty("text", s.getPresentationName());
        schoolClassJson.addProperty("id", s.getExternalId());
        result.add(schoolClassJson);
    }

    Comparator<? super Registration> registrationComparatorByStudentName =
            (x, y) -> Student.NAME_COMPARATOR.compare(x.getStudent(), y.getStudent());

    @RequestMapping(value = "/executionCourseRegistrations/{executionCourse}", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getRegistrationsForExecutionCourse(@PathVariable("executionCourse") ExecutionCourse course) {
        final JsonArray result = new JsonArray();

        addStudents(result,
                course.getAttendsSet().stream().filter(a -> !a.getEnrolment().isAnnulled()).map(a -> a.getRegistration()));
        return new GsonBuilder().create().toJson(result);
    }

    @RequestMapping(value = "/classes/{class}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody String getStudentsEnroledInClass(@PathVariable("class") SchoolClass schoolClass) {
        final JsonArray result = new JsonArray();

        addStudents(result, schoolClass.getRegistrationsSet().stream());
        return new GsonBuilder().create().toJson(result);
    }

    @SuppressWarnings("null")
    @RequestMapping(value = "/shifts/{shift}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody String getStudentsEnroledInShift(@PathVariable("shift") Shift shift) {
        final JsonArray result = new JsonArray();

        final ExecutionCourse course = shift.getExecutionCourse();
        addStudents(result, shift.getStudentsSet().stream().filter(r -> !hasEnrolmentAnnuled(r, course)));
        return new GsonBuilder().create().toJson(result);
    }

    static private boolean hasEnrolmentAnnuled(final Registration registration, final ExecutionCourse course) {
        final Attends attends = registration == null ? null : registration.getAssociatedAttendsSet().stream()
                .filter(a -> a.getExecutionCourse() == course).findAny().orElse(null);
        return attends != null && attends.getEnrolment().isAnnulled();
    }

    private void addStudents(final JsonArray studentsJson, final Stream<Registration> registrations) {
        registrations.distinct().sorted(registrationComparatorByStudentName)
                .forEach(registration -> addStudent(studentsJson, registration));
    }

    private void addStudent(final JsonArray studentsJson, final Registration registration) {
        final JsonObject studentJson = new JsonObject();

        final Student student = registration.getStudent();
        studentJson.addProperty("picture", request.getContextPath() + "/user/photo/" + student.getPerson().getUsername());
        studentJson.addProperty("name", student.getName());
        studentJson.addProperty("email", calculatePersonalEmail(student));
        studentJson.addProperty("institutionalEmail", student.getPerson().getInstitutionalEmailAddressValue());
        studentJson.addProperty("studentNumber", registration.getNumber().toString());
        studentJson.addProperty("id", student.getExternalId());
        studentJson.addProperty("degreeCode", registration.getDegree().getCode());
        studentJson.addProperty("degree", registration.getLastDegreeCurricularPlan().getPresentationName());
        studentJson.addProperty("phone", registration.getPerson().getDefaultPhoneNumber());
        studentJson.addProperty("mobilePhone", registration.getPerson().getDefaultMobilePhoneNumber());

        studentsJson.add(studentJson);
    }

    //TODO remove in the future.
    //Since first time candidacy students still have unconfirmed mail addresses, we need to calculate them in a different way
    static public String calculatePersonalEmail(Student student) {
        Optional<EmailAddress> findFirst =
                student.getPerson().getPartyContactsSet().stream().filter(x -> x instanceof EmailAddress)
                        .map(EmailAddress.class::cast).filter(x -> !x.getValue().endsWith("@campus.ul.pt")).findAny();
        if (findFirst.isPresent()) {
            return findFirst.get().getValue();
        } else {
            return student.getPerson().getEmailForSendingEmails();
        }

    }
}
