package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.lists;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
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
        model.addAttribute(
                "executionSemesters",
                Bennu.getInstance().getExecutionPeriodsSet().stream()
                        .sorted(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR.reversed()).collect(Collectors.toList()));
        return "academicAdminOffice/lists/studentsByCurricularCourses";
    }

    @RequestMapping(value = "/executionSemesters/{executionSemester}", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getAvailableDegreesForExecutionSemester(
            @PathVariable("executionSemester") ExecutionSemester executionSemester) {
        JsonArray result = new JsonArray();
        ExecutionYear executionYear = executionSemester.getExecutionYear();
        Bennu.getInstance().getDegreesSet().stream()
                .filter(degree -> !degree.getExecutionDegreesForExecutionYear(executionYear).isEmpty())
                .sorted(Degree.COMPARATOR_BY_NAME).forEach(degree -> addDegree(result, degree, executionYear));
        return new GsonBuilder().create().toJson(result);
    }

    private void addDegree(JsonArray response, Degree degree, ExecutionYear executionYear) {
        JsonObject degreeJson = new JsonObject();
        degreeJson.addProperty("text", degree.getNameI18N(executionYear).getContent() + " - " + degree.getDegreeTypeName());
        degreeJson.addProperty("id", degree.getExternalId());
        response.add(degreeJson);
    }

    @RequestMapping(value = "/executionSemesters/{executionSemester}/{degree}/classes", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getAvailableClassesForDegreeInExecutionSemester(
            @PathVariable("executionSemester") ExecutionSemester executionSemester, @PathVariable("degree") Degree degree) {
        JsonArray result = new JsonArray();
        degree.getExecutionDegreesForExecutionYear(executionSemester.getExecutionYear()).stream()
                .flatMap(ed -> ed.getSchoolClassesSet().stream()).filter(sc -> sc.getExecutionPeriod() == executionSemester)
                .sorted(SchoolClass.COMPARATOR_BY_NAME).distinct().forEach(sc -> addSchooClass(result, sc));
        return new GsonBuilder().create().toJson(result);
    }

    private void addSchooClass(JsonArray result, SchoolClass sc) {
        JsonObject schoolClassJson = new JsonObject();
        schoolClassJson.addProperty("text", sc.getNome());
        schoolClassJson.addProperty("id", sc.getExternalId());
        result.add(schoolClassJson);
    }

    @RequestMapping(value = "/executionSemesters/{executionSemester}/{degree}/courses", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody String getAvailableCoursesForDegreeInExecutionSemester(
            @PathVariable("executionSemester") ExecutionSemester executionSemester, @PathVariable("degree") Degree degree) {
        JsonArray result = new JsonArray();
        degree.getExecutionDegreesForExecutionYear(executionSemester.getExecutionYear()).stream()
                .flatMap(ed -> ed.getDegreeCurricularPlan().getExecutionCoursesByExecutionPeriod(executionSemester).stream())
                .sorted(ExecutionCourse.EXECUTION_COURSE_NAME_COMPARATOR).forEach(ec -> addExecutionCourse(result, ec));

        return new GsonBuilder().create().toJson(result);
    }

    private void addExecutionCourse(JsonArray result, ExecutionCourse ec) {
        JsonObject schoolClassJson = new JsonObject();
        schoolClassJson.addProperty("text", ec.getName());
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

    @RequestMapping(value = "/classes/{class}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody String getStudentsEnroledInClass(@PathVariable("class") SchoolClass schoolClass) {
        JsonArray result = new JsonArray();
        //We are only interested in shifts which do not have more than one class (ignore shared shifts)
        Predicate<? super Shift> shiftsWithOneClass = s -> s.getAssociatedClassesSet().size() == 1;
        schoolClass.getAssociatedShiftsSet().stream().filter(shiftsWithOneClass).flatMap(s -> s.getStudentsSet().stream())
                .map(r -> r.getStudent()).sorted(Student.NAME_COMPARATOR).forEach(student -> addStudent(result, student));
        return new GsonBuilder().create().toJson(result);
    }

    @RequestMapping(value = "/shifts/{shift}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody String getStudentsEnroledInShift(@PathVariable("shift") Shift shift) {
        JsonArray result = new JsonArray();
        shift.getStudentsSet().stream().map(r -> r.getStudent()).sorted(Student.NAME_COMPARATOR)
                .forEach(student -> addStudent(result, student));
        return new GsonBuilder().create().toJson(result);
    }

    private void addStudent(JsonArray result, Student student) {
        JsonObject schoolClassJson = new JsonObject();
        schoolClassJson.addProperty("name", student.getName());
        schoolClassJson.addProperty("email", calculatePersonalEmail(student));
        schoolClassJson.addProperty("studentNumber", student.getNumber());
        schoolClassJson.addProperty("id", student.getExternalId());
        result.add(schoolClassJson);
    }

    //TODO remove in the future.
    //Since first time candidacy students still have unconfirmed mail addresses, we need to calculate them in a different way
    public String calculatePersonalEmail(Student student) {
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
