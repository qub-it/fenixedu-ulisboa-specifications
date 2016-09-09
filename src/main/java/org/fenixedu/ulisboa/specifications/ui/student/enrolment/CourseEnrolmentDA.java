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

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleLevel;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentEnrollmentBean;
import org.fenixedu.academic.ui.struts.action.commons.student.enrollment.bolonha.AbstractBolonhaStudentEnrollmentDA;
import org.fenixedu.academic.ui.struts.action.student.StudentApplication.StudentEnrollApp;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.dto.student.enrollment.bolonha.CycleEnrolmentBean;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStep;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStepTemplate;
import org.joda.time.LocalDate;

@StrutsFunctionality(app = StudentEnrollApp.class, path = "courses-student-enrollment",
        titleKey = "label.title.courseEnrolmentRedirect", bundle = "FenixeduUlisboaSpecificationsResources")
@Mapping(module = "ulisboa-specifications", path = "/student/courseEnrolment")
@Forwards(value = {

        @Forward(name = "showDegreeModulesToEnrol", path = "/student/enrollment/bolonha/showDegreeModulesToEnrol.jsp"),

        @Forward(name = "showEnrollmentInstructions", path = "/student/enrollment/bolonha/showEnrollmentInstructions.jsp"),

        @Forward(name = "chooseCycleCourseGroupToEnrol", path = "/student/enrollment/bolonha/chooseCycleCourseGroupToEnrol.jsp"),

        @Forward(name = "chooseOptionalCurricularCourseToEnrol",
                path = "/student/enrollment/bolonha/chooseOptionalCurricularCourseToEnrol.jsp"),

})
public class CourseEnrolmentDA extends AbstractBolonhaStudentEnrollmentDA {

    static final private String MAPPING_MODULE = "/ulisboa-specifications";
    static final private String MAPPING = MAPPING_MODULE + "/student/courseEnrolment";
    static final public String ACTION = MAPPING + ".do";

    @Override
    protected CurricularRuleLevel getCurricularRuleLevel(final ActionForm input) {
        return CurricularRuleLevel.ENROLMENT_WITH_RULES; // student use case
    }

    /**
     * @deprecated All implementations return null
     */
    @Deprecated
    @Override
    protected int[] getCurricularYearForCurricularCourses() {
        return null; // all years
    }

    @Override
    protected String getAction() {
        return ACTION.replace(MAPPING_MODULE, "");
    }

    static public String getEntryPointURL() {
        return ACTION;
    }

    @Override
    @EntryPoint
    public ActionForward prepare(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        final ExecutionSemester executionSemester = getDomainObject(request, "executionSemesterOID");
        final StudentCurricularPlan scp = getDomainObject(request, "studentCurricularPlanOID");

        return prepareShowDegreeModulesToEnrol(mapping, form, request, response, scp, executionSemester);
    }

    @Override
    protected ActionForward prepareShowDegreeModulesToEnrol(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response, final StudentCurricularPlan scp,
            final ExecutionSemester semester) {

        setContext(request, semester, scp);
        return super.prepareShowDegreeModulesToEnrol(mapping, form, request, response, scp, semester);
    }

    @Override
    protected ActionForward prepareShowDegreeModulesToEnrol(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response, final BolonhaStudentEnrollmentBean bean) {

        setContext(request, bean.getExecutionPeriod(), bean.getStudentCurricularPlan());
        return super.prepareShowDegreeModulesToEnrol(mapping, form, request, response, bean);
    }

    static public EnrolmentStepTemplate createEnrolmentStepShowEnrollmentInstructions() {
        return new EnrolmentStepTemplate(

                BundleUtil.getLocalizedString(Bundle.STUDENT, "label.enrollment.courses.instructions"),

                getInstructionsEntryPointURL(),

                (enrolmentProcess) -> {
                    return EnrolmentStep.buildArgsStruts(enrolmentProcess.getExecutionSemester(),
                            enrolmentProcess.getStudentCurricularPlan());
                },

                (enrolmentProcess) -> {
                    return enrolmentProcess.getEnrolmentPeriods().stream().anyMatch(i -> i.isForCurricularCourses());
                });
    }

    static public String getInstructionsEntryPointURL() {
        return ACTION + "?method=showEnrollmentInstructions";
    }

    public ActionForward showEnrollmentInstructions(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {

        final ExecutionSemester executionSemester = getDomainObject(request, "executionSemesterOID");
        final StudentCurricularPlan scp = getDomainObject(request, "studentCurricularPlanOID");
        setContext(request, executionSemester, scp);

        return mapping.findForward("showEnrollmentInstructions");
    }

    @Override
    public ActionForward prepareChooseCycleCourseGroupToEnrol(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {

        final BolonhaStudentEnrollmentBean bean = getBolonhaStudentEnrollmentBeanFromViewState();
        setContext(request, bean.getExecutionPeriod(), bean.getStudentCurricularPlan());

        final CycleEnrolmentBean cycleEnrolmentBean = new CycleEnrolmentBean(bean.getStudentCurricularPlan(),
                bean.getExecutionPeriod(), bean.getCycleTypeToEnrol().getSourceCycleAffinity(), bean.getCycleTypeToEnrol());
        request.setAttribute("cycleEnrolmentBean", cycleEnrolmentBean);

        return mapping.findForward("chooseCycleCourseGroupToEnrol");
    }

    private EnrolmentProcess setContext(final HttpServletRequest request, final ExecutionSemester executionSemester,
            final StudentCurricularPlan scp) {

        final EnrolmentProcess process = EnrolmentProcess.find(executionSemester, scp);

        if (process != null) {
            checkUser(scp.getRegistration());
            final List<AcademicEnrolmentPeriodBean> periods =
                    process.getEnrolmentPeriods().stream().filter(i -> i.isForCurricularCourses()).collect(Collectors.toList());
            request.setAttribute("openedEnrolmentPeriods", periods);
        }

        if (TreasuryBridgeAPIFactory.implementation().isAcademicalActsBlocked(scp.getPerson(), new LocalDate())) {
            request.setAttribute("debtsMessage",
                    "error.StudentCurricularPlan.cannot.enrol.with.debts.for.previous.execution.years");
        }

        request.setAttribute("enrolmentProcess", process);
        request.setAttribute("registration", scp.getRegistration());
        request.setAttribute("action", getAction());
        return process;
    }

    static private Student checkUser(final Registration input) {
        final Student student = Authenticate.getUser().getPerson().getStudent();

        if (student == null || (input != null && input.getStudent() != student)) {
            throw new SecurityException("error.authorization.notGranted");
        }

        return student;
    }

}
