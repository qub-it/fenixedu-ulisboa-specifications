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
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.student.StudentApplication.StudentEnrollApp;
import org.fenixedu.academic.ui.struts.action.student.enrollment.EnrolmentContextHandler;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects.EnrolmentManagementApp;
import org.joda.time.DateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.FenixFramework;

/**
 * @author shezad - Jul 9, 2015
 *
 */
@StrutsFunctionality(app = StudentEnrollApp.class, path = "schoolClass-student-enrollment",
        titleKey = "link.schoolClass.student.enrolment", bundle = "FenixeduUlisboaSpecificationsResources")
@Mapping(module = "student", path = "/schoolClassStudentEnrollment")
@Forwards(@Forward(name = "showSchoolClasses", path = "/student/enrollment/schoolClass/schoolClassesSelection.jsp"))
public class SchoolClassStudentEnrollmentDA extends FenixDispatchAction {

    static final private String MAPPING = "/student/schoolClassStudentEnrollment";
    static final private String ACTION = MAPPING + ".do";

    static public String getEntryPointURL(final HttpServletRequest request) {
        return EnrolmentManagementApp.getStrutsEntryPointURL(request, ACTION);
    }

    @EntryPoint
    public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final Student student = getUserView(request).getPerson().getStudent();

        final SchoolClass selectedSchoolClass = (SchoolClass) request.getAttribute("selectedSchoolClass");
        final AcademicEnrolmentPeriod selectedEnrolmentPeriod =
                (AcademicEnrolmentPeriod) request.getAttribute("selectedEnrolmentPeriod");

        final List<SchoolClassStudentEnrollmentDTO> enrollmentBeans = new ArrayList<SchoolClassStudentEnrollmentDTO>();

        Collection<Registration> registrationsToShow = null;

        //This variable will be present if we are in the middle of a workflow. The registrations being listed should be limited to this registration
        String workflowRegistrationOid = request.getParameter("workflowRegistrationOid");

        if (workflowRegistrationOid != null) {
            final Registration workflowRegistation = FenixFramework.getDomainObject(workflowRegistrationOid);
            registrationsToShow = Collections.singleton(workflowRegistation);
            request.setAttribute("workflowRegistrationOid", workflowRegistrationOid);

            // TODO legidio, replace with enrolmentProcess attribute (which will provide return and continue URLs
            request.setAttribute("returnURL", null);
        } else {
            registrationsToShow = student.getRegistrationsToEnrolInShiftByStudent();
        }

        for (final Registration registration : registrationsToShow) {

            // TODO legidio, ask Shezad for help on interpreting this
            if (EnrolmentContextHandler.getRegisteredEnrolmentContextHandler()
                    .getReturnURLForStudentInFullClasses(request, registration).isPresent()
                    && !registration.getExternalId().equals(workflowRegistrationOid)) {
                //Skip workflow based functionalities when we are not on that workflow
                continue;
            }

            for (final AcademicEnrolmentPeriodBean iter : AcademicEnrolmentPeriod.getEnrolmentPeriodsOpenOrUpcoming(student)) {
                if (isValidPeriodForUser(iter)) {
                    enrollmentBeans.add(new SchoolClassStudentEnrollmentDTO(registration, iter,
                            selectedEnrolmentPeriod == iter.getEnrolmentPeriod() ? selectedSchoolClass : null));
                }
            }
        }

        enrollmentBeans.sort(Comparator.naturalOrder());
        request.setAttribute("enrollmentBeans", enrollmentBeans);

        return mapping.findForward("showSchoolClasses");
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
        final Registration registration = getDomainObject(request, "registrationID");
        final AcademicEnrolmentPeriod enrolmentPeriod = getDomainObject(request, "enrolmentPeriodID");

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

        final SchoolClass schoolClass = getDomainObject(request, "schoolClassID");
        final Registration registration = getDomainObject(request, "registrationID");
        final AcademicEnrolmentPeriod enrolmentPeriod = getDomainObject(request, "enrolmentPeriodID");
        final Shift shift = getDomainObject(request, "shiftID");

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

        private final Registration registration;
        private final AcademicEnrolmentPeriodBean enrolmentPeriod;
        private final SchoolClass schoolClassToDisplay;

        public SchoolClassStudentEnrollmentDTO(final Registration registration, final AcademicEnrolmentPeriodBean enrolmentPeriod,
                SchoolClass schoolClassToDisplay) {
            super();
            this.registration = registration;
            this.enrolmentPeriod = enrolmentPeriod;
            this.schoolClassToDisplay = schoolClassToDisplay;
        }

        public Registration getRegistration() {
            return registration;
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
                return getAttendingShifts(schoolClassToDisplay).stream().map(s -> s.getExecutionCourse())
                        .collect(Collectors.toSet()).size();
            }
            return 0;
        }

        public boolean isSchoolClassToDisplayFree() {
            final SchoolClass schoolClassToDisplay = getSchoolClassToDisplay();
            if (schoolClassToDisplay != null) {
                return !getAttendingShifts(schoolClassToDisplay).stream()
                        .anyMatch(s -> s.getLotacao().intValue() <= s.getStudentsSet().size());
            }
            return false;
        }

        public List<Shift> getSchoolClassToDisplayShifts() {
            final SchoolClass schoolClassToDisplay = getSchoolClassToDisplay();
            final SchoolClass currentSchoolClass = getCurrentSchoolClass();

            if (schoolClassToDisplay != null) {
                List<Shift> shifts = getAttendingShifts(schoolClassToDisplay);

                // if displaying current schoolClass, show only shifts of class that are enrolled
                if (schoolClassToDisplay == currentSchoolClass) {
                    final List<Shift> enrolledShifts = getRegistration().getShiftsFor(schoolClassToDisplay.getExecutionPeriod());
                    shifts = shifts.stream().filter(s -> enrolledShifts.contains(s)).collect(Collectors.toList());
                }
                return shifts;
            }
            return Collections.emptyList();
        }

        protected List<Shift> getAttendingShifts(final SchoolClass schoolClassToDisplay) {
            final List<ExecutionCourse> attendingExecutionCourses =
                    getRegistration().getAttendingExecutionCoursesFor(schoolClassToDisplay.getExecutionPeriod());
            return schoolClassToDisplay.getAssociatedShiftsSet().stream()
                    .filter(s -> attendingExecutionCourses.contains(s.getExecutionCourse())).collect(Collectors.toList());
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
            return RegistrationServices
                    .getSchoolClassesToEnrolBy(getRegistration(), getRegistration().getActiveDegreeCurricularPlan(),
                            getExecutionSemester())
                    .stream().filter(s -> s.getAnoCurricular().equals(curricularYear))
                    .sorted((s1, s2) -> s1.getNome().compareTo(s2.getNome())).collect(Collectors.toList());
        }

        public int getCurricularYear() {
            final ExecutionSemester executionSemester = getExecutionSemester();
            return getRegistration().getCurricularYear(executionSemester.getExecutionYear());
        }

        @Override
        public int compareTo(SchoolClassStudentEnrollmentDTO o) {
            int result = Degree.COMPARATOR_BY_NAME_AND_ID.compare(getRegistration().getDegree(), o.getRegistration().getDegree());
            return result == 0 ? getExecutionSemester().compareTo(o.getExecutionSemester()) : result;
        }

    }
}