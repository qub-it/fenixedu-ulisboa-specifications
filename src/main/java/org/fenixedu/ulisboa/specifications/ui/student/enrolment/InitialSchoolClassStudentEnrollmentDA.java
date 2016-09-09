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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.student.StudentApplication.StudentEnrollApp;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.SchoolClassStudentEnrollmentDA.SchoolClassStudentEnrollmentDTO;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;

/**
 * 
 * @author shezad
 *
 */
@StrutsFunctionality(app = StudentEnrollApp.class, path = "initialSchoolClass-student-enrollment",
        titleKey = "link.initialSchoolClass.student.enrolment", bundle = "FenixeduUlisboaSpecificationsResources")
@Mapping(module = "student", path = "/initialSchoolClassStudentEnrollment")
@Forwards(@Forward(name = "showSchoolClasses", path = "/student/enrollment/schoolClass/initialSchoolClassesSelection.jsp"))
public class InitialSchoolClassStudentEnrollmentDA extends FenixDispatchAction {

    static final private String MAPPING_MODULE = "/student";
    static final private String MAPPING = MAPPING_MODULE + "/initialSchoolClassStudentEnrollment";
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

        boolean schoolClassEmptyButSelectionMandatory = false;
        for (final AcademicEnrolmentPeriodBean iter : AcademicEnrolmentPeriod.getEnrolmentPeriodsOpenOrUpcoming(student)) {
            if (isValidPeriodForUser(iter)) {

                final SchoolClassStudentEnrollmentDTO schoolClassStudentEnrollmentBean =
                        new SchoolClassStudentEnrollmentDTO(iter, null);
                enrolmentBeans.add(schoolClassStudentEnrollmentBean);

                final EnrolmentProcess enrolmentProcess =
                        EnrolmentProcess.find(iter.getExecutionSemester(), iter.getStudentCurricularPlan());
                if (enrolmentProcess != null) {
                    request.setAttribute("enrolmentProcess", enrolmentProcess);
                }

                if (iter.getSchoolClassSelectionMandatory() && schoolClassStudentEnrollmentBean.getCurrentSchoolClass() == null
                        && !schoolClassStudentEnrollmentBean.getInitialSchoolClassesToEnrol().isEmpty()) {
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

        return mapping.findForward("showSchoolClasses");
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

        return prepare(mapping, form, request, response);
    }

    static private boolean isValidPeriodForUser(final AcademicEnrolmentPeriodBean ep) {
        return ep.isOpen() && ep.isForInitialClasses();
    }

}
