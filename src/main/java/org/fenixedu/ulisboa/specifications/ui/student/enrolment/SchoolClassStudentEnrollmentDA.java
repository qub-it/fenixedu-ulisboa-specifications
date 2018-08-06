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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.enrolment.schoolClass.SchoolClassEnrolmentPreference;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionInterval;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.student.StudentApplication.StudentEnrollApp;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AutomaticEnrolment;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.joda.time.DateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.Atomic;

/**
 * @author shezad - Jul 9, 2015
 *
 */
@StrutsFunctionality(app = StudentEnrollApp.class, path = "schoolClass-student-enrollment",
        titleKey = "link.schoolClass.student.enrolment", bundle = "FenixeduUlisboaSpecificationsResources")
@Mapping(module = "student", path = "/schoolClassStudentEnrollment")
@Forwards(@Forward(name = "showSchoolClasses", path = "/student/enrollment/schoolClass/schoolClassesSelection.jsp"))
public class SchoolClassStudentEnrollmentDA extends FenixDispatchAction {

    static final private String MAPPING_MODULE = "/student";
    static final private String MAPPING = MAPPING_MODULE + "/schoolClassStudentEnrollment";
    static final private String ACTION = MAPPING + ".do";

    protected String getAction() {
        return ACTION.replace(MAPPING_MODULE, "");
    }

    static public String getEntryPointURL() {
        return ACTION;
    }

    @EntryPoint
    public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final SchoolClass selectedSchoolClass = (SchoolClass) request.getAttribute("selectedSchoolClass");
        final AcademicEnrolmentPeriod selectedEnrolmentPeriod =
                (AcademicEnrolmentPeriod) request.getAttribute("selectedEnrolmentPeriod");
        final ExecutionSemester executionSemester = selectedEnrolmentPeriod == null ? getDomainObject(request,
                "executionSemesterOID") : selectedEnrolmentPeriod.getExecutionSemester();
        final StudentCurricularPlan scp = getDomainObject(request, "studentCurricularPlanOID");

        final Student student = Authenticate.getUser().getPerson().getStudent();
        final List<SchoolClassStudentEnrollmentDTO> enrolmentBeans = new ArrayList<SchoolClassStudentEnrollmentDTO>();

        Collection<ExecutionSemester> automaticEnrolmentSemesters = new HashSet<>();
        boolean schoolClassEmptyButSelectionMandatory = false;
        for (final AcademicEnrolmentPeriodBean iter : AcademicEnrolmentPeriod.getEnrolmentPeriodsOpenOrUpcoming(student)) {
            if (isValidPeriodForUser(iter)) {

                if (iter.getEnrolmentPeriod().getAutomaticEnrolment() == AutomaticEnrolment.YES_UNEDITABLE) {
                    automaticEnrolmentSemesters.add(iter.getExecutionSemester());
                }
                if (!automaticEnrolmentSemesters.isEmpty()) {
                    continue;
                }

                final SchoolClassStudentEnrollmentDTO schoolClassStudentEnrollmentBean = new SchoolClassStudentEnrollmentDTO(iter,
                        executionSemester == iter.getExecutionSemester() ? selectedSchoolClass : null);
                enrolmentBeans.add(schoolClassStudentEnrollmentBean);

                if (iter.getSchoolClassSelectionMandatory() && schoolClassStudentEnrollmentBean.getCurrentSchoolClass() == null
                        && !schoolClassStudentEnrollmentBean.getSchoolClassesToEnrol().isEmpty()) {
                    schoolClassEmptyButSelectionMandatory = true;
                }
            }
        }

        request.setAttribute("schoolClassEmptyButSelectionMandatory", schoolClassEmptyButSelectionMandatory);

        if (!enrolmentBeans.isEmpty()) {
            enrolmentBeans.sort(Comparator.naturalOrder());
        }

        request.setAttribute("enrolmentBeans", enrolmentBeans);
        request.setAttribute("action", getAction());
        if (executionSemester != null && scp != null) {
            final EnrolmentProcess enrolmentProcess = EnrolmentProcess.find(executionSemester, scp);
            request.setAttribute("enrolmentProcess", enrolmentProcess);

            if (!automaticEnrolmentSemesters.isEmpty() && enrolmentProcess != null) {
                final Registration registration = scp.getRegistration();
                for (ExecutionSemester executionSemesterToEnrol : automaticEnrolmentSemesters) {
                    final SchoolClass schoolClass = readFirstUnfilledClass(registration, executionSemesterToEnrol).orElse(null);
                    if (schoolClass != null) {
                        enrolOnSchoolClass(schoolClass, registration);
                    }
                }
                final String url = enrolmentProcess.getContinueURL(request);
                final ActionForward forward = new ActionForward(url.replaceFirst(request.getContextPath(), ""), true);
                forward.setModule("/");
                return forward;
            }
        }

        return mapping.findForward("showSchoolClasses");
    }

    @Atomic
    protected void enrolOnSchoolClass(final SchoolClass schoolClass, final Registration registration) {
        if (schoolClass == null) {
            throw new DomainException("error.RegistrationOperation.avaliable.schoolClass.not.found");
        }

        RegistrationServices.replaceSchoolClass(registration, schoolClass, schoolClass.getExecutionPeriod());
    }

    private Optional<SchoolClass> readFirstUnfilledClass(Registration registration, final ExecutionSemester executionSemester) {
        final DegreeCurricularPlan degreeCurricularPlan = registration.getLastDegreeCurricularPlan();
        if (degreeCurricularPlan != null) {
            final ExecutionDegree executionDegree =
                    degreeCurricularPlan.getExecutionDegreeByYear(executionSemester.getExecutionYear());
            if (executionDegree != null) {
                final Comparator<SchoolClass> enrolmentMethod = getAutomaticSchoolClassEnrolmentMethod();
                return executionDegree.getSchoolClassesSet().stream().filter(sc -> sc.getAnoCurricular().equals(1)
                        && executionSemester == sc.getExecutionPeriod() && getFreeVacancies(sc) > 0).max(enrolmentMethod);
            }
        }
        return Optional.empty();
    }

    private Comparator<SchoolClass> getAutomaticSchoolClassEnrolmentMethod() {

        final AutomaticSchoolClassEnrolmentMethod method = AutomaticSchoolClassEnrolmentMethod
                .valueOf(ULisboaConfiguration.getConfiguration().getAutomaticSchoolClassEnrolmentMethod());

        if (method == AutomaticSchoolClassEnrolmentMethod.FILL_FIRST) {
            return new MostFilledFreeClass();
        } else if (method == AutomaticSchoolClassEnrolmentMethod.ROUND_ROBIN) {
            return new LeastFilledFreeClass();
        } else {
            throw new IllegalArgumentException("Unexpected automatic school class enrolment method");
        }

    }

    private static enum AutomaticSchoolClassEnrolmentMethod {
        FILL_FIRST,

        ROUND_ROBIN;
    }

    class MostFilledFreeClass implements Comparator<SchoolClass> {
        // Return at the beggining the course which is the most filled, but still has space
        // This allows a "fill first" kind of school class scheduling

        @Override
        public int compare(SchoolClass sc1, SchoolClass sc2) {
            Integer sc1Vacancies = getFreeVacancies(sc1);
            Integer sc2Vacancies = getFreeVacancies(sc2);

            return sc1Vacancies.compareTo(sc2Vacancies) != 0 ? sc1Vacancies
                    .compareTo(sc2Vacancies) : SchoolClass.COMPARATOR_BY_NAME.compare(sc1, sc2);
        }

    }

    class LeastFilledFreeClass implements Comparator<SchoolClass> {
        // Return at the beggining the course which is the most filled, but still has space
        // This allows a "fill first" kind of school class scheduling

        @Override
        public int compare(SchoolClass sc1, SchoolClass sc2) {
            Integer sc1MinEnrolments = getMinShiftEnrolmentsCount(sc1);
            Integer sc2MinEnrolments = getMinShiftEnrolmentsCount(sc2);

            return sc2MinEnrolments.compareTo(sc1MinEnrolments) != 0 ? sc2MinEnrolments
                    .compareTo(sc1MinEnrolments) : SchoolClass.COMPARATOR_BY_NAME.compare(sc1, sc2);
        }

    }

    private Integer getFreeVacancies(SchoolClass schoolClass) {
        final Optional<Shift> minShift = schoolClass.getAssociatedShiftsSet().stream()
                .min((s1, s2) -> (getShiftVacancies(s1).compareTo(getShiftVacancies(s2))));
        return minShift.isPresent() ? getShiftVacancies(minShift.get()) : 0;
    }

    private Integer getMinShiftEnrolmentsCount(SchoolClass schoolClass) {
        final Optional<Shift> minShift = schoolClass.getAssociatedShiftsSet().stream()
                .min((s1, s2) -> Integer.compare(s1.getStudentsSet().size(), s2.getStudentsSet().size()));
        return minShift.isPresent() ? minShift.get().getStudentsSet().size() : 0;
    }

    private Integer getShiftVacancies(final Shift shift) {
        return shift.getLotacao() - shift.getStudentsSet().size();
    }

    public ActionForward viewSchoolClass(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final SchoolClass schoolClass = getDomainObject(request, "schoolClassID");
        final AcademicEnrolmentPeriod enrolmentPeriod = getDomainObject(request, "enrolmentPeriodID");

        request.setAttribute("selectedSchoolClass", schoolClass);
        request.setAttribute("selectedEnrolmentPeriod", enrolmentPeriod);

        return prepare(mapping, form, request, response);
    }

    public ActionForward enrollInSchoolClass(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final SchoolClass schoolClass = getDomainObject(request, "schoolClassID");
        final AcademicEnrolmentPeriod enrolmentPeriod = getDomainObject(request, "enrolmentPeriodID");
        final StudentCurricularPlan scp = getDomainObject(request, "studentCurricularPlanOID");
        final Registration registration = scp.getRegistration();

        try {
            atomic(() -> RegistrationServices.replaceSchoolClass(registration, schoolClass,
                    enrolmentPeriod.getExecutionSemester()));
            final String successMessage =
                    schoolClass != null ? "message.schoolClassStudentEnrollment.enrollInSchoolClass.success" : "message.schoolClassStudentEnrollment.unenrollInSchoolClass.success";
            addActionMessage("success", request, successMessage);
        } catch (DomainException e) {
            addActionMessage("error", request, e.getKey(), e.getArgs());
        }

        request.setAttribute("selectedSchoolClass", schoolClass);
        request.setAttribute("selectedEnrolmentPeriod", enrolmentPeriod);

        return prepare(mapping, form, request, response);
    }

    public ActionForward removeShift(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final Shift shift = getDomainObject(request, "shiftID");
        final SchoolClass schoolClass = getDomainObject(request, "schoolClassID");
        final AcademicEnrolmentPeriod enrolmentPeriod = getDomainObject(request, "enrolmentPeriodID");
        final StudentCurricularPlan scp = getDomainObject(request, "studentCurricularPlanOID");
        final Registration registration = scp.getRegistration();

        try {
            atomic(() -> registration.removeShifts(shift));
            addActionMessage("success", request, "message.schoolClassStudentEnrollment.removeShift.success");
        } catch (DomainException e) {
            addActionMessage("error", request, e.getKey(), e.getArgs());
        }

        request.setAttribute("selectedSchoolClass", schoolClass);
        request.setAttribute("selectedEnrolmentPeriod", enrolmentPeriod);

        return prepare(mapping, form, request, response);
    }

    static private boolean isValidPeriodForUser(final AcademicEnrolmentPeriodBean ep) {
        return ep.isOpen() && ep.isForClasses();
    }

    @SuppressWarnings("serial")
    public static class SchoolClassStudentEnrollmentDTO implements Serializable, Comparable<SchoolClassStudentEnrollmentDTO> {

        private final AcademicEnrolmentPeriodBean enrolmentPeriod;
        private final SchoolClass schoolClassToDisplay;

        public SchoolClassStudentEnrollmentDTO(final AcademicEnrolmentPeriodBean enrolmentPeriod,
                final SchoolClass schoolClassToDisplay) {

            super();
            this.enrolmentPeriod = enrolmentPeriod;
            this.schoolClassToDisplay = schoolClassToDisplay;
        }

        public Registration getRegistration() {
            return enrolmentPeriod.getRegistration();
        }

        public StudentCurricularPlan getStudentCurricularPlan() {
            return enrolmentPeriod.getStudentCurricularPlan();
        }

        public AcademicEnrolmentPeriod getEnrolmentPeriod() {
            return enrolmentPeriod.getEnrolmentPeriod();
        }

        public ExecutionSemester getExecutionSemester() {
            return enrolmentPeriod.getExecutionSemester();
        }

        public SchoolClass getCurrentSchoolClass() {
            return RegistrationServices.getSchoolClassBy(getRegistration(), getExecutionSemester()).orElse(null);
        }

        public SchoolClass getSchoolClassToDisplay() {
            if (schoolClassToDisplay != null) {
                return schoolClassToDisplay;
            }
            final SchoolClass currentSchoolClass = getCurrentSchoolClass();
            return currentSchoolClass != null ? currentSchoolClass : getSchoolClassesToEnrol().stream().findFirst().orElse(null);
        }

        public int getMatchedCoursesNumber() {
            final SchoolClass schoolClassToDisplay = getSchoolClassToDisplay();
            if (schoolClassToDisplay != null) {
                return RegistrationServices.getAttendingShifts(schoolClassToDisplay, getRegistration()).stream()
                        .map(s -> s.getExecutionCourse()).collect(Collectors.toSet()).size();
            }
            return 0;
        }

        public boolean isSchoolClassToDisplayFree() {
            return RegistrationServices.isSchoolClassFree(getSchoolClassToDisplay(), getRegistration());
        }

        public List<Shift> getSchoolClassToDisplayShifts() {
            final SchoolClass schoolClassToDisplay = getSchoolClassToDisplay();
            final SchoolClass currentSchoolClass = getCurrentSchoolClass();

            if (schoolClassToDisplay != null) {
                List<Shift> shifts = RegistrationServices.getAttendingShifts(schoolClassToDisplay, getRegistration());

                // if displaying current schoolClass, show only shifts of class that are enrolled
                if (schoolClassToDisplay == currentSchoolClass) {
                    final List<Shift> enrolledShifts = getRegistration().getShiftsFor(schoolClassToDisplay.getExecutionPeriod());
                    shifts = shifts.stream().filter(s -> enrolledShifts.contains(s)).collect(Collectors.toList());
                }
                return shifts;
            }
            return Collections.emptyList();
        }

        public String getSchoolClassToDisplayLessonsJson() {

            final JsonArray result = new JsonArray();
            for (Shift shift : getSchoolClassToDisplayShifts()) {
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
                    event.addProperty("title",
                            shift.getExecutionCourse().getName() + " (" + shift.getShiftTypesCodePrettyPrint() + ")");
                    event.addProperty("shiftId", shift.getExternalId());
                    event.addProperty("shiftLessons", shift.getLessonPresentationString());
                    event.addProperty("shiftTypes", shift.getShiftTypesPrettyPrint());
                    result.add(event);
                }
            }

            return result.toString();

        }

        public List<SchoolClass> getSchoolClassesToEnrol() {
            int curricularYear = getCurricularYear();
            final List<SchoolClass> schoolClasses = RegistrationServices
                    .getSchoolClassesToEnrolBy(getRegistration(), getRegistration().getLastDegreeCurricularPlan(),
                            getExecutionSemester())
                    .stream().filter(s -> s.getAnoCurricular().equals(curricularYear))
                    .sorted((s1, s2) -> s1.getNome().compareTo(s2.getNome())).collect(Collectors.toList());

            return filterSchoolClassesByPrecedence(schoolClasses);
        }

        public List<SchoolClass> getInitialSchoolClassesToEnrol() {
            final List<SchoolClass> schoolClasses =
                    RegistrationServices.getInitialSchoolClassesToEnrolBy(getRegistration(), getExecutionSemester()).stream()
                            .sorted((s1, s2) -> s1.getNome().compareTo(s2.getNome())).collect(Collectors.toList());
            return filterSchoolClassesByPrecedence(schoolClasses);
        }

        private List<SchoolClass> filterSchoolClassesByPrecedence(final List<SchoolClass> schoolClasses) {
            final ExecutionSemester previousExecutionSemester = getExecutionSemester().getPreviousExecutionPeriod();
            if (previousExecutionSemester != null) {
                final SchoolClass previousSchoolClass =
                        RegistrationServices.getSchoolClassBy(getRegistration(), previousExecutionSemester).orElse(null);
                if (previousSchoolClass != null && previousSchoolClass.getNextSchoolClass() != null) {
                    return schoolClasses.contains(previousSchoolClass.getNextSchoolClass()) ? Collections
                            .singletonList(previousSchoolClass.getNextSchoolClass()) : Collections.emptyList();
                }
            }

            final ExecutionSemester nextExecutionSemester = getExecutionSemester().getNextExecutionPeriod();
            if (nextExecutionSemester != null) {
                final SchoolClass nextSchoolClass =
                        RegistrationServices.getSchoolClassBy(getRegistration(), nextExecutionSemester).orElse(null);
                if (nextSchoolClass != null && !nextSchoolClass.getPreviousSchoolClassesSet().isEmpty()) {
                    final List<SchoolClass> result = new ArrayList<>(schoolClasses);
                    result.retainAll(nextSchoolClass.getPreviousSchoolClassesSet());
                    return result;
                }
            }

            return schoolClasses;
        }

        public int getCurricularYear() {
            final ExecutionSemester executionSemester = getExecutionSemester();
            return RegistrationServices.getCurricularYear(getRegistration(), executionSemester.getExecutionYear()).getResult();
        }

        public List<SchoolClassEnrolmentPreference> getEnrolmentPreferencesSorted() {
            final RegistrationDataByExecutionInterval registrationDataByInterval = getOrCreateRegistrationDataByInterval();
            return registrationDataByInterval.getSchoolClassEnrolmentPreferencesSet().stream().sorted()
                    .collect(Collectors.toList());
        }

        // not the first execution semester, so we give the possibility of mantain last year schoolClass and skip preference choice
        public boolean isCanSkipEnrolmentPreferences() {

            final ExecutionSemester executionSemester = getExecutionSemester();
            final ExecutionSemester previousSemester = executionSemester.getPreviousExecutionPeriod();
            final Registration registration = getRegistration();

            // no previous semester or previous semester from different year
            if (previousSemester == null || previousSemester.getExecutionYear() != executionSemester.getExecutionYear()) {
                return false;
            }

            // no school class in previous semester
            final SchoolClass previousSchoolClass =
                    RegistrationServices.getSchoolClassBy(registration, previousSemester).orElse(null);
            if (previousSchoolClass == null) {
                return false;
            }

            // no shifts for previous school class (and therefore no shifts for student curricular year
            if (Collections.disjoint(previousSchoolClass.getAssociatedShiftsSet(), registration.getShiftsFor(previousSemester))) {
                return false;
            }

            // check if exists school class for this semester
            final Optional<SchoolClass> schoolClassForThisSemester =
                    RegistrationServices.getInitialSchoolClassesToEnrolBy(registration, executionSemester).stream()
                            .filter(sc -> sc.getName().equals(previousSchoolClass.getName())).findFirst();
            return schoolClassForThisSemester.isPresent();
        }

        public boolean isHasEnrolmentPreferencesProcessStarted() {
            return !getOrCreateRegistrationDataByInterval().getSchoolClassEnrolmentPreferencesSet().isEmpty();
        }

        @Atomic
        public RegistrationDataByExecutionInterval getOrCreateRegistrationDataByInterval() {
            return RegistrationDataByExecutionInterval.getOrCreateRegistrationDataByInterval(getRegistration(),
                    getExecutionSemester());
        }

        @Override
        public int compareTo(SchoolClassStudentEnrollmentDTO o) {
            int result = Degree.COMPARATOR_BY_NAME_AND_ID.compare(getRegistration().getDegree(), o.getRegistration().getDegree());
            return result == 0 ? getExecutionSemester().compareTo(o.getExecutionSemester()) : result;
        }
    }

}
