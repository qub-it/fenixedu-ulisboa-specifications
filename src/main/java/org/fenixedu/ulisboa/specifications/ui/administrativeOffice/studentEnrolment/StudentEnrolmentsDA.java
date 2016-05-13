/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: luis.egidio@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.studentEnrolment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.dto.administrativeOffice.studentEnrolment.StudentEnrolmentBean;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.academic.ui.struts.action.exceptions.FenixActionException;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumLineServices;

@Mapping(path = "/studentEnrolmentsExtended", module = "academicAdministration", functionality = SearchForStudentsDA.class)
@Forwards({
        @Forward(name = "prepareChooseExecutionPeriod", path = "/academicAdminOffice/chooseStudentEnrolmentExecutionPeriod.jsp"),
        @Forward(name = "visualizeRegistration", path = "/academicAdministration/student.do?method=visualizeRegistration") })
public class StudentEnrolmentsDA
        extends org.fenixedu.academic.ui.struts.action.administrativeOffice.studentEnrolment.StudentEnrolmentsDA {

    @Override
    public ActionForward prepare(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response) throws FenixActionException {

        final StudentCurricularPlan plan = getDomainObject(request, "scpID");

        if (plan != null) {
            StudentEnrolmentBean studentEnrolmentBean = getBean(request);
            if (studentEnrolmentBean == null) {
                studentEnrolmentBean = new StudentEnrolmentBean();

                // QubExtension
                ExecutionSemester executionSemester = getDomainObject(request, "executionSemesterID");
                if (executionSemester == null) {
                    final ExecutionYear lastScpExecutionYear = plan.getLastExecutionYear();
                    final ExecutionSemester currentSemester = ExecutionSemester.readActualExecutionSemester();
                    executionSemester = lastScpExecutionYear == null
                            || currentSemester.getExecutionYear() == lastScpExecutionYear ? currentSemester : lastScpExecutionYear
                                    .getFirstExecutionPeriod();
                }
                studentEnrolmentBean.setExecutionPeriod(executionSemester);
            }
            studentEnrolmentBean.setStudentCurricularPlan(plan);
            return showExecutionPeriodEnrolments(studentEnrolmentBean, mapping, actionForm, request, response);
        } else {
            throw new FenixActionException();
        }
    }

    // @QubExtension
    private StudentEnrolmentBean getBean(final HttpServletRequest request) {
        StudentEnrolmentBean result = (StudentEnrolmentBean) getRenderedObject("studentEnrolmentBean");

        if (result == null) {
            result = (StudentEnrolmentBean) getFromRequest(request, "studentEnrolmentBean");
        }

        return result;
    }

    /*
     * Copy from super
     */
    private ActionForward showExecutionPeriodEnrolments(StudentEnrolmentBean studentEnrolmentBean, ActionMapping mapping,
            ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("studentEnrolmentBean", studentEnrolmentBean);

        if (studentEnrolmentBean.getExecutionPeriod() != null) {
            request.setAttribute("studentEnrolments", studentEnrolmentBean.getStudentCurricularPlan()
                    .getEnrolmentsByExecutionPeriod(studentEnrolmentBean.getExecutionPeriod()));
            request.setAttribute("studentImprovementEnrolments", studentEnrolmentBean.getStudentCurricularPlan()
                    .getEnroledImprovements(studentEnrolmentBean.getExecutionPeriod()));
            request.setAttribute("studentSpecialSeasonEnrolments", studentEnrolmentBean.getStudentCurricularPlan()
                    .getEnroledSpecialSeasons(studentEnrolmentBean.getExecutionPeriod()));
        }

        return mapping.findForward("prepareChooseExecutionPeriod");
    }

    @Override
    public ActionForward annulEnrolment(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        try {
            atomic(() -> {
                final Enrolment enrolment = getDomainObject(request, "enrolmentId");
                enrolment.annul();

                // qubExtension
                CurriculumLineServices.updateAggregatorGrade(enrolment);
            });
        } catch (DomainException e) {
            addActionMessage(request, e.getKey(), e.getArgs());
        }

        final StudentEnrolmentBean studentEnrolmentBean = new StudentEnrolmentBean();
        studentEnrolmentBean.setExecutionPeriod(getDomainObject(request, "executionPeriodId"));
        studentEnrolmentBean.setStudentCurricularPlan(getDomainObject(request, "scpID"));
        return showExecutionPeriodEnrolments(studentEnrolmentBean, mapping, form, request, response);

    }

    @Override
    public ActionForward activateEnrolment(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        try {
            atomic(() -> {
                final Enrolment enrolment = getDomainObject(request, "enrolmentId");
                enrolment.activate();

                // qubExtension
                CurriculumLineServices.updateAggregatorGrade(enrolment);
            });
        } catch (DomainException e) {
            addActionMessage(request, e.getKey(), e.getArgs());
        }

        final StudentEnrolmentBean studentEnrolmentBean = new StudentEnrolmentBean();
        studentEnrolmentBean.setExecutionPeriod(getDomainObject(request, "executionPeriodId"));
        studentEnrolmentBean.setStudentCurricularPlan(getDomainObject(request, "scpID"));
        return showExecutionPeriodEnrolments(studentEnrolmentBean, mapping, form, request, response);

    }

}
