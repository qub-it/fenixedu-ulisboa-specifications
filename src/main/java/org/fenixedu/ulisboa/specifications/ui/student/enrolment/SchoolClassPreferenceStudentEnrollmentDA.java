/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2016 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2016 Universidade de Lisboa (after any Go-Live phase)
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.enrolment.schoolClass.SchoolClassEnrolmentPreference;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionInterval;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.student.StudentApplication.StudentEnrollApp;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.SchoolClassStudentEnrollmentDA.SchoolClassStudentEnrollmentDTO;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;

/**
 * 
 * @author shezad
 *
 */
@StrutsFunctionality(app = StudentEnrollApp.class, path = "schoolClassPreference-student-enrollment",
        titleKey = "link.schoolClassPreference.student.enrolment", bundle = "FenixeduUlisboaSpecificationsResources")
@Mapping(module = "student", path = "/schoolClassPreferenceStudentEnrollment")
@Forwards(@Forward(name = "showSchoolClasses", path = "/student/enrollment/schoolClass/schoolClassesPreferenceSelection.jsp"))
public class SchoolClassPreferenceStudentEnrollmentDA extends FenixDispatchAction {

    static final private String MAPPING_MODULE = "/student";
    static final private String MAPPING = MAPPING_MODULE + "/schoolClassPreferenceStudentEnrollment";
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

        final Student student = Authenticate.getUser().getPerson().getStudent();
        final List<SchoolClassStudentEnrollmentDTO> enrolmentBeans = new ArrayList<SchoolClassStudentEnrollmentDTO>();

        for (final AcademicEnrolmentPeriodBean iter : AcademicEnrolmentPeriodBean.getEnrolmentPeriodsOpenOrUpcoming(student)) {
            if (isValidPeriodForUser(iter)) {

                final ExecutionInterval executionSemester = iter.getExecutionSemester();
                final Registration registration = iter.getRegistration();

                // if no enrolments for period, student cannot choose preferences
                if (registration.getEnrolments(executionSemester).isEmpty()) {
                    continue;
                }

                final SchoolClassStudentEnrollmentDTO schoolClassStudentEnrollmentBean =
                        new SchoolClassStudentEnrollmentDTO(iter, null);
                enrolmentBeans.add(schoolClassStudentEnrollmentBean);

                // test if registration curricular year is still the same as school classes preferences year and clear them if not!
                if (schoolClassStudentEnrollmentBean.isHasEnrolmentPreferencesProcessStarted()) {
                    final RegistrationDataByExecutionInterval registrationData =
                            schoolClassStudentEnrollmentBean.getOrCreateRegistrationDataByInterval();
                    final Integer schoolClassPreferencesYear = registrationData.getSchoolClassEnrolmentPreferencesSet().iterator()
                            .next().getSchoolClass().getCurricularYear();

                    if (schoolClassPreferencesYear != null
                            && !schoolClassPreferencesYear.equals(schoolClassStudentEnrollmentBean.getCurricularYear())) {
                        atomic(() -> registrationData.getSchoolClassEnrolmentPreferencesSet().forEach(p -> p.delete()));
                    }
                }

                if (!schoolClassStudentEnrollmentBean.isCanSkipEnrolmentPreferences()
                        && !schoolClassStudentEnrollmentBean.isHasEnrolmentPreferencesProcessStarted()) {

                    // pre-initialize preferences selection
                    atomic(() -> {
                        final RegistrationDataByExecutionInterval registrationData = RegistrationDataByExecutionInterval
                                .getOrCreateRegistrationDataByInterval(registration, executionSemester);
                        SchoolClassEnrolmentPreference.initializePreferencesForRegistration(registrationData,
                                getSchoolClassesOptions(registration, executionSemester));
                    });
                }

                if (schoolClassStudentEnrollmentBean.isCanSkipEnrolmentPreferences()
                        && !schoolClassStudentEnrollmentBean.isHasEnrolmentPreferencesProcessStarted()) {
                    // pre-initialize school class enrolment, according to last semester school class
                    atomic(() -> enrollInSchoolClassWithSameNameAsPrevious(registration, executionSemester));
                }

                // we'll put previous school class just to display its name to the student
                if (schoolClassStudentEnrollmentBean.isCanSkipEnrolmentPreferences()) {
                    final ExecutionInterval previousSemester = executionSemester.getPrevious();
                    request.setAttribute("previousSchoolClass",
                            RegistrationServices.getSchoolClassBy(registration, previousSemester).orElse(null));
                }

                final EnrolmentProcess enrolmentProcess =
                        EnrolmentProcess.find(iter.getExecutionSemester(), iter.getStudentCurricularPlan());
                if (enrolmentProcess != null) {
                    request.setAttribute("enrolmentProcess", enrolmentProcess);
                }

            }
        }

        if (!enrolmentBeans.isEmpty()) {
            enrolmentBeans.sort(Comparator.naturalOrder());
        }

        request.setAttribute("enrolmentBeans", enrolmentBeans);
        request.setAttribute("action", getAction());

        return mapping.findForward("showSchoolClasses");
    }

    public ActionForward changePreferenceOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final SchoolClassEnrolmentPreference preference = getDomainObject(request, "schoolClassEnrolmentPreferenceID");
        final Boolean increment = Boolean.valueOf(request.getParameter("increment"));

        try {
            atomic(() -> preference.changePreferenceOrder(increment));
            final String successMessage = "message.schoolClassPreferenceStudentEnrollment.changePreferenceOrder.success";
            addActionMessage("success", request, successMessage);
        } catch (DomainException e) {
            addActionMessage("error", request, e.getKey(), e.getArgs());
        }

        return prepare(mapping, form, request, response);
    }

    public ActionForward dontSkipEnrolmentPreferences(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final RegistrationDataByExecutionInterval registrationData =
                getDomainObject(request, "registrationDataByExecutionIntervalID");
        final ExecutionSemester semester = registrationData.getExecutionInterval().convert(ExecutionSemester.class);
        final Registration registration = registrationData.getRegistration();

        try {
            // pre-initialize preferences selection
            atomic(() -> {
                SchoolClassEnrolmentPreference.initializePreferencesForRegistration(registrationData,
                        getSchoolClassesOptions(registration, semester));
                RegistrationServices.replaceSchoolClass(registration, null, semester);
            });

//            final String successMessage = "message.schoolClassPreferenceStudentEnrollment.changePreferenceOrder.success";
//            addActionMessage("success", request, successMessage);
        } catch (DomainException e) {
            addActionMessage("error", request, e.getKey(), e.getArgs());
        }

        return prepare(mapping, form, request, response);
    }

    public ActionForward clearEnrolmentPreferences(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final RegistrationDataByExecutionInterval registrationData =
                getDomainObject(request, "registrationDataByExecutionIntervalID");

        try {
            atomic(() -> {
                registrationData.getSchoolClassEnrolmentPreferencesSet().forEach(p -> p.delete());
                enrollInSchoolClassWithSameNameAsPrevious(registrationData.getRegistration(),
                        registrationData.getExecutionInterval().convert(ExecutionSemester.class));
            });
            final String successMessage = "message.schoolClassPreferenceStudentEnrollment.clearEnrolmentPreferences.success";
            addActionMessage("success", request, successMessage);
        } catch (DomainException e) {
            addActionMessage("error", request, e.getKey(), e.getArgs());
        }

        return prepare(mapping, form, request, response);
    }

    private void enrollInSchoolClassWithSameNameAsPrevious(final Registration registration,
            final ExecutionInterval executionSemester) {
        final SchoolClass previousSchoolClass =
                RegistrationServices.getSchoolClassBy(registration, executionSemester.getPrevious()).orElse(null);

        final SchoolClass schoolClass = previousSchoolClass != null ? getSchoolClassesOptions(registration, executionSemester)
                .stream().filter(sc -> sc.getName().equals(previousSchoolClass.getName())).findFirst().orElse(null) : null;
        RegistrationServices.replaceSchoolClass(registration, schoolClass, executionSemester);
    }

    private List<SchoolClass> getSchoolClassesOptions(final Registration registration,
            final ExecutionInterval executionSemester) {
        return RegistrationServices.getInitialSchoolClassesToEnrolBy(registration, executionSemester).stream()
                .sorted((s1, s2) -> s1.getNome().compareTo(s2.getNome())).collect(Collectors.toList());
    }

    static private boolean isValidPeriodForUser(final AcademicEnrolmentPeriodBean ep) {
        return ep.isOpen() && ep.isForClassesPreference();
    }

}
