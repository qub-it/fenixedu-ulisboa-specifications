/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: shezad.anavarali@qub-it.com
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui.student.enrolment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentType;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.StudentSchoolClassCurricularRule;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseServices;
import org.fenixedu.academic.domain.enrolment.period.AcademicEnrolmentPeriod;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.dto.ShiftToEnrol;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.exceptions.NotAuthorizedException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.shift.ReadShiftsToEnroll;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.Atomic;

/**
 * @author shezad - Aug 11, 2015
 *
 */
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.shiftEnrolment",
        accessGroup = "activeStudents")
@RequestMapping("/student/shiftEnrolment")
public class ShiftEnrolmentController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/student/shiftEnrolment";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private static PossibleShiftsToEnrolProvider possibleShiftsToEnrolProvider = getPossibleShiftsToEnrolDefaultProvider();

    @FunctionalInterface
    public interface PossibleShiftsToEnrolProvider {
        Collection<Shift> getShifts(ExecutionCourse executionCourse, ShiftType shiftType, Registration registration);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    static public String getEntryPointURL() {
        return SWITCHENROLMENTPERIOD_URL;
    }

    @RequestMapping
    public String home(Model model) {

        Registration selectedRegistration = (Registration) model.asMap().get("registration");
        AcademicEnrolmentPeriod selectedEnrolmentPeriod = (AcademicEnrolmentPeriod) model.asMap().get("enrolmentPeriod");

        final Student student = checkUser(selectedRegistration);
        final List<EnrolmentPeriodDTO> enrolmentBeans = new ArrayList<EnrolmentPeriodDTO>();

        for (final AcademicEnrolmentPeriodBean iter : AcademicEnrolmentPeriodBean.getEnrolmentPeriodsOpenOrUpcoming(student)) {
            if (isValidPeriodForUser(iter)) {

                boolean selected =
                        selectedRegistration == iter.getRegistration() && selectedEnrolmentPeriod == iter.getEnrolmentPeriod();
                enrolmentBeans.add(new EnrolmentPeriodDTO(iter, selected));
            }
        }

        if (!enrolmentBeans.isEmpty()) {
            enrolmentBeans.sort((eb1, eb2) -> eb1.compareTo(eb2));
            if (selectedRegistration == null || selectedEnrolmentPeriod == null) {
                final EnrolmentPeriodDTO selectedEnrolmentBean = enrolmentBeans.iterator().next();
                selectedEnrolmentBean.setSelected(true);
                selectedRegistration = selectedEnrolmentBean.getRegistration();
                selectedEnrolmentPeriod = selectedEnrolmentBean.getEnrolmentPeriod();
            }

            final ExecutionInterval executionSemester = selectedEnrolmentPeriod.getExecutionInterval();

            try {
                final List<ShiftToEnrol> shiftsToEnrol = ReadShiftsToEnroll
                        .readWithStudentRestrictionsForShiftsEnrolments(selectedRegistration, executionSemester);

                filterValidEnrolmentTypes(shiftsToEnrol, selectedRegistration, selectedEnrolmentPeriod);

                shiftsToEnrol.sort((s1, s2) -> s1.getExecutionCourse().getName().compareTo(s2.getExecutionCourse().getName()));
                model.addAttribute("shiftsToEnrol", shiftsToEnrol);
            } catch (NotAuthorizedException e) {
                addErrorMessage(e.getLocalizedMessage(), model);
            } catch (FenixServiceException e) {
                addErrorMessage(BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources", e.getMessage()), model);
            } catch (DomainException e) {
                addErrorMessage(e.getLocalizedMessage(), model);
            }
        }

        model.addAttribute("enrolmentBeans", enrolmentBeans);
        if (selectedRegistration != null && selectedEnrolmentPeriod != null) {
            model.addAttribute("enrolmentProcess", EnrolmentProcess.find(selectedEnrolmentPeriod.getExecutionSemester(),
                    selectedRegistration.getStudentCurricularPlan(selectedEnrolmentPeriod.getExecutionYear())));
        }

        checkIfMandatoryShiftsAreEnrolled(enrolmentBeans, model);

        return jspPage("shiftEnrolment");
    }

    private void filterValidEnrolmentTypes(final List<ShiftToEnrol> shiftsToEnrol, final Registration registration,
            AcademicEnrolmentPeriod period) {
        final Set<EnrolmentType> configured = period.getEnrolmentTypesSet();
        if (configured.isEmpty()) {
            return;
        }

        final StudentCurricularPlan scp = registration.getLastStudentCurricularPlan();
        final ExecutionYear year = period.getExecutionYear();

        for (final Attends attends : registration.readAttendsByExecutionPeriod(period.getExecutionSemester())) {
            if (attends.getEnrolment() != null) {
                int enrolmentsCount =
                        CompetenceCourseServices.countEnrolmentsUntil(scp, attends.getEnrolment().getCurricularCourse(), year);
                boolean flunked = enrolmentsCount > 1;
                if (!checkIfEnrolmentTypeIsValid(configured, flunked)) {
                    shiftsToEnrol.removeIf(ste -> ste.getExecutionCourse() == attends.getExecutionCourse());
                }
            }
        }
    }

    private boolean checkIfEnrolmentTypeIsValid(final Set<EnrolmentType> configured, boolean flunked) {
        for (final EnrolmentType enrolmentType : configured) {
            if (enrolmentType.isFlunked() && flunked) {
                return true;
            }
            if (enrolmentType.isNormal() && !flunked /* add here future 'not' conditions*/) {
                return true;
            }
        }
        return false;
    }

    private void checkIfMandatoryShiftsAreEnrolled(final List<EnrolmentPeriodDTO> enrolmentBeans, Model model) {
        for (final EnrolmentPeriodDTO enrolmentBean : enrolmentBeans) {
            final ExecutionInterval executionSemester = enrolmentBean.getExecutionSemester();
            final Registration registration = enrolmentBean.getRegistration();
            for (Enrolment enrolment : registration.getEnrolments(executionSemester)) {
                boolean allAvailableShiftsMustBeEnrolled = enrolment.getCurricularRules(executionSemester).stream()
                        .filter(cr -> cr instanceof StudentSchoolClassCurricularRule)
                        .map(cr -> (StudentSchoolClassCurricularRule) cr)
                        .anyMatch(ssccr -> ssccr.getAllAvailableShiftsMustBeEnrolled());

                if (allAvailableShiftsMustBeEnrolled) {
                    final Attends attends = enrolment.getAttendsFor(executionSemester);
                    if (attends != null) {
                        final ExecutionCourse executionCourse = attends.getExecutionCourse();
                        if (executionCourse.getAssociatedShifts().stream().flatMap(s -> s.getSortedTypes().stream()).distinct()
                                .anyMatch(st -> registration.getShiftFor(executionCourse, st) == null)) {
                            model.addAttribute("mandatoryShiftsEnrolled", false);
                            addErrorMessage(
                                    BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources",
                                            "message.StudentSchoolClassCurricularRule.allAvailableShiftsMustBeEnrolled.error"),
                                    model);
                            return;
                        }
                    }

                }
            }
        }
        model.addAttribute("mandatoryShiftsEnrolled", true);
    }

    private static final String _SWITCHENROLMENTPERIOD_URI = "/switchEnrolmentPeriod/";
    public static final String SWITCHENROLMENTPERIOD_URL = CONTROLLER_URL + _SWITCHENROLMENTPERIOD_URI;

    @RequestMapping(value = _SWITCHENROLMENTPERIOD_URI + "{registrationOid}/{periodOid}")
    public String switchEnrolmentPeriod(@PathVariable("registrationOid") Registration registration,
            @PathVariable("periodOid") AcademicEnrolmentPeriod enrolmentPeriod, Model model) {

        model.addAttribute("registration", registration);
        model.addAttribute("enrolmentPeriod", enrolmentPeriod);

        return home(model);
    }

    @RequestMapping(value = "possibleShiftsToEnrol.json/{registrationOid}/{executionCourseOid}/{shiftType}")
    public @ResponseBody String getPossibleShiftsToEnrol(@PathVariable("registrationOid") Registration registration,
            @PathVariable("executionCourseOid") ExecutionCourse executionCourse, @PathVariable("shiftType") ShiftType shiftType) {

        checkUser(registration);

        final Set<Shift> shifts = new HashSet<>();

        // if student has a school class and it has shifts for EC and type, we only can allow him to add those shifts
        final Optional<SchoolClass> optionalSchoolClass =
                RegistrationServices.getSchoolClassBy(registration, executionCourse.getExecutionPeriod());
        if (optionalSchoolClass.isPresent()) {
            shifts.addAll(optionalSchoolClass.get().getAssociatedShiftsSet().stream()
                    .filter(s -> s.getExecutionCourse() == executionCourse && s.getTypes().contains(shiftType))
                    .collect(Collectors.toSet()));
        }

        // otherwise, he can chose any shift
        if (shifts.isEmpty()) {
            shifts.addAll(possibleShiftsToEnrolProvider.getShifts(executionCourse, shiftType, registration));
        }

        final JsonArray result = new JsonArray();
        for (final Shift shift : shifts) {
            if (shift.getLotacao().intValue() > shift.getStudentsSet().size()) {
                JsonObject jsonShift = new JsonObject();
                jsonShift.addProperty("name", shift.getNome());
                jsonShift.addProperty("type", shift.getShiftTypesPrettyPrint());
                jsonShift.addProperty("lessons", shift.getLessonPresentationString());
                jsonShift.addProperty("externalId", shift.getExternalId());
                result.add(jsonShift);
            }
        }

        return new GsonBuilder().create().toJson(result);
    }

    public static PossibleShiftsToEnrolProvider getPossibleShiftsToEnrolDefaultProvider() {
        return (ExecutionCourse ec, ShiftType st, Registration r) -> ec.getShiftsByTypeOrderedByShiftName(st);
    }

    public static void setPossibleShiftsToEnrolProvider(PossibleShiftsToEnrolProvider possibleShiftsToEnrolProvider) {
        ShiftEnrolmentController.possibleShiftsToEnrolProvider = possibleShiftsToEnrolProvider;
    }

    @RequestMapping(value = "/addShift/{registrationOid}/{periodOid}/{shiftOid}")
    public String addShift(@PathVariable("registrationOid") Registration registration,
            @PathVariable("periodOid") AcademicEnrolmentPeriod enrolmentPeriod, @PathVariable("shiftOid") Shift shift,
            Model model) {

        checkUser(registration);

        if (shift.getTypes().stream().anyMatch(st -> registration.getShiftFor(shift.getExecutionCourse(), st) != null)) {
            addErrorMessage(BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources",
                    "message.shiftEnrolment.addShift.error.shiftTypeAlreadyEnrolled"), model);
        } else {
            try {
                addShiftService(registration, shift);
                addInfoMessage(BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources",
                        "message.shiftEnrolment.addShift.success"), model);
            } catch (DomainException e) {
                addErrorMessage(BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources", e.getMessage()), model);
            }
        }

        model.addAttribute("registration", registration);
        model.addAttribute("enrolmentPeriod", enrolmentPeriod);

        return home(model);
    }

    @Atomic
    protected void addShiftService(Registration registration, Shift shift) {
        if (!shift.reserveForStudent(registration)) {
            throw new DomainException("error.shiftEnrolment.shiftFull", shift.getNome(), shift.getShiftTypesPrettyPrint(),
                    shift.getExecutionCourse().getName());
        }
    }

    @RequestMapping(value = "/removeShift/{registrationOid}/{periodOid}/{shiftOid}")
    public String removeShift(@PathVariable("registrationOid") Registration registration,
            @PathVariable("periodOid") AcademicEnrolmentPeriod enrolmentPeriod, @PathVariable("shiftOid") Shift shift,
            Model model) {

        checkUser(registration);

        try {
            removeShiftService(registration, shift);
            addInfoMessage(BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources",
                    "message.shiftEnrolment.removeShift.success"), model);
        } catch (DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        model.addAttribute("registration", registration);
        model.addAttribute("enrolmentPeriod", enrolmentPeriod);

        return home(model);
    }

    @Atomic
    private void removeShiftService(Registration registration, Shift shift) {
        registration.removeShifts(shift);
    }

    @RequestMapping(value = "currentSchedule.json/{registrationOid}/{executionSemesterOid}",
            produces = "application/json; charset=utf-8")
    public @ResponseBody String schedule(@PathVariable("registrationOid") Registration registration,
            @PathVariable("executionSemesterOid") ExecutionSemester executionSemester) {

        checkUser(registration);

        final JsonArray result = new JsonArray();

        for (final Shift shift : registration.getShiftsFor(executionSemester)) {
            for (Lesson lesson : shift.getAssociatedLessonsSet()) {
                final DateTime now = new DateTime();
                final DateTime weekDay = now.withDayOfWeek(lesson.getDiaSemana().getDiaSemanaInDayOfWeekJodaFormat());
                final DateTime startTime = weekDay.withTime(lesson.getBeginHourMinuteSecond().getHour(),
                        lesson.getBeginHourMinuteSecond().getMinuteOfHour(), 0, 0);
                final DateTime endTime = weekDay.withTime(lesson.getEndHourMinuteSecond().getHour(),
                        lesson.getEndHourMinuteSecond().getMinuteOfHour(), 0, 0);

                final JsonObject event = new JsonObject();
                event.addProperty("id", lesson.getExternalId());
                event.addProperty("start", startTime.toString());
                event.addProperty("end", endTime.toString());
                event.addProperty("title", shift.getExecutionCourse().getName() + " (" + shift.getShiftTypesCodePrettyPrint()
                        + " - " + shift.getNome() + ")");
                result.add(event);
            }
        }

        return result.toString();
    }

    static private Student checkUser(final Registration input) {
        final Student student = Authenticate.getUser().getPerson().getStudent();

        if (student == null || (input != null && input.getStudent() != student)) {
            throw new SecurityException("error.authorization.notGranted");
        }

        return student;
    }

    @SuppressWarnings("serial")
    public static class EnrolmentPeriodDTO implements Serializable, Comparable<EnrolmentPeriodDTO> {

        private final AcademicEnrolmentPeriodBean enrolmentPeriod;
        private Boolean selected;

        public EnrolmentPeriodDTO(final AcademicEnrolmentPeriodBean enrolmentPeriod, final Boolean selected) {
            super();
            this.enrolmentPeriod = enrolmentPeriod;
            this.selected = selected;
        }

        public Registration getRegistration() {
            return enrolmentPeriod.getRegistration();
        }

        public AcademicEnrolmentPeriod getEnrolmentPeriod() {
            return enrolmentPeriod.getEnrolmentPeriod();
        }

        public ExecutionInterval getExecutionSemester() {
            return enrolmentPeriod.getExecutionSemester();
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        @Override
        public int compareTo(EnrolmentPeriodDTO o) {
            int result = Degree.COMPARATOR_BY_NAME_AND_ID.compare(getRegistration().getDegree(), o.getRegistration().getDegree());
            return result == 0 ? getExecutionSemester().compareTo(o.getExecutionSemester()) : result;
        }

        public Map<Lesson, Collection<Lesson>> getLessonsOverlaps() {
            final Map<Lesson, Collection<Lesson>> overlapsMap = new HashMap<Lesson, Collection<Lesson>>();

            try {
                final List<Lesson> allLessons = getRegistration().getShiftsFor(getExecutionSemester()).stream()
                        .flatMap(s -> s.getAssociatedLessonsSet().stream()).collect(Collectors.toList());
                while (!allLessons.isEmpty()) {
                    final Lesson lesson = allLessons.remove(0);
                    final Set<Lesson> overlappingLessons =
                            allLessons.stream().filter(l -> getLessonIntervalHack(l).overlaps(getLessonIntervalHack(lesson)))
                                    .collect(Collectors.toSet());
                    if (!overlappingLessons.isEmpty()) {
                        overlapsMap.put(lesson, overlappingLessons);
                    }
                }
            } catch (Exception e) {
                // new code that will enter in production the day before of enrolments
                // just in case of some untested exception, prevent the blowing of rest...
            }

            return overlapsMap;
        }

        /**
         * HACK: this interval is not accurate, because it doesn't takes into account lesson instance dates
         */
        private static Interval getLessonIntervalHack(final Lesson lesson) {
            final int weekDay = lesson.getDiaSemana().getDiaSemanaInDayOfWeekJodaFormat();
            return new Interval(
                    new LocalDate().toDateTime(lesson.getBeginHourMinuteSecond().toLocalTime()).withDayOfWeek(weekDay),
                    new LocalDate().toDateTime(lesson.getEndHourMinuteSecond().toLocalTime()).withDayOfWeek(weekDay));
        }
    }

    static private boolean isValidPeriodForUser(final AcademicEnrolmentPeriodBean ep) {
        return ep.isOpen() && ep.isForShift();
    }

}
