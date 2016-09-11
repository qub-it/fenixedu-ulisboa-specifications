/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
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
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStep;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStepTemplate;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

@StrutsFunctionality(app = StudentEnrollApp.class, path = "student-enrolment-management",
        titleKey = "label.title.enrolmentManagement", bundle = "FenixeduUlisboaSpecificationsResources")
@Mapping(module = "ulisboa-specifications", path = "/student/enrolmentManagement")
@Forwards(value = {

        @Forward(name = "choosePersonalDataAuthorizationChoice",
                path = "/student/enrollment/choosePersonalDataAuthorizationChoice.jsp"),

        @Forward(name = "chooseEnrolmentProcess", path = "/student/enrollment/chooseEnrolmentProcess.jsp"),

        @Forward(name = "endEnrolmentProcess", path = "/student/enrollment/endEnrolmentProcess.jsp"),

})
public class EnrolmentManagementDA extends FenixDispatchAction {

    static final private String MAPPING = "/ulisboa-specifications/student/enrolmentManagement";
    static final private String ACTION = MAPPING + ".do";

    static public String getEntryPointURL() {
        return ACTION;
    }

    @EntryPoint
    public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final Student student = checkUser();
        final List<AcademicEnrolmentPeriodBean> periods = AcademicEnrolmentPeriod.getEnrolmentPeriodsOpenOrUpcoming(student);

        request.setAttribute("enrolmentProcesses", EnrolmentProcess.buildProcesses(periods));

        periods.sort(Comparator.comparing(AcademicEnrolmentPeriodBean::getStartDate)
                .thenComparing(AcademicEnrolmentPeriodBean::getEnrolmentPeriodType));
        request.setAttribute("periodsOpenBeans", periods.stream().filter(i -> i.isOpen()).collect(Collectors.toList()));
        request.setAttribute("periodsUpcomingBeans", periods.stream().filter(i -> i.isUpcoming()).collect(Collectors.toList()));

        return mapping.findForward("chooseEnrolmentProcess");
    }

    static public Student checkUser() {
        final Student student = Authenticate.getUser().getPerson().getStudent();

        if (student == null) {
            throw new SecurityException("error.authorization.notGranted");
        }

        return student;
    }

    static public EnrolmentStepTemplate createEnrolmentStepEndProcess() {
        return new EnrolmentStepTemplate(

                ULisboaSpecificationsUtil.bundleI18N("label.EnrolmentProcess.end"),

                getEndURL(),

                (enrolmentProcess) -> {
                    return EnrolmentStep.buildArgsStruts(enrolmentProcess.getExecutionSemester(),
                            enrolmentProcess.getStudentCurricularPlan());
                },

                (enrolmentProcess) -> {
                    return true;
                });
    }

    static public String getEndURL() {
        return ACTION + "?method=endEnrolmentProcess";
    }

    public ActionForward endEnrolmentProcess(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        final ExecutionSemester executionSemester = getDomainObject(request, "executionSemesterOID");
        final StudentCurricularPlan scp = getDomainObject(request, "studentCurricularPlanOID");
        final EnrolmentProcess process = EnrolmentProcess.find(executionSemester, scp);

        request.setAttribute("enrolmentProcess", process);
        return mapping.findForward("endEnrolmentProcess");
    }

}
