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
package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.studentEnrolment.bolonha;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationContext;
import org.fenixedu.academic.domain.enrolment.EnroledCurriculumModuleWrapper;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.NoCourseGroupCurriculumGroup;
import org.fenixedu.academic.dto.student.enrollment.bolonha.NoCourseGroupEnroledCurriculumModuleWrapper;
import org.fenixedu.academic.dto.student.enrollment.bolonha.StudentCurriculumEnrolmentBean;
import org.fenixedu.academic.dto.student.enrollment.bolonha.StudentCurriculumGroupBean;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;

@Mapping(path = "/improvementBolonhaStudentEnrolment", module = "academicAdministration",
        formBean = "bolonhaStudentEnrollmentForm", functionality = SearchForStudentsDA.class)
@Forwards({
        @Forward(name = "chooseEvaluationSeason",
                path = "/academicAdminOffice/student/enrollment/bolonha/chooseEvaluationSeason.jsp"),
        @Forward(name = "showDegreeModulesToEnrol",
                path = "/academicAdminOffice/student/enrollment/bolonha/showDegreeModulesToEnrol.jsp") })
public class AcademicAdminOfficeImprovementBolonhaStudentEnrolmentDA extends
        org.fenixedu.academic.ui.struts.action.administrativeOffice.studentEnrolment.bolonha.AcademicAdminOfficeImprovementBolonhaStudentEnrolmentDA {

    @Override
    public ActionForward prepareChooseEvaluationSeason(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {

        super.prepareChooseEvaluationSeason(mapping, form, request, response);

        request.setAttribute("chooseEvaluationSeasonBean", new ImprovementChooseEvaluationSeasonBean());

        return mapping.findForward("chooseEvaluationSeason");
    }

    @Override
    protected ActionForward prepareShowDegreeModulesToEnrol(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response, StudentCurricularPlan studentCurricularPlan, ExecutionSemester executionSemester,
            final EvaluationSeason evaluationSeason) {

        super.prepareShowDegreeModulesToEnrol(mapping, form, request, response, studentCurricularPlan, executionSemester,
                evaluationSeason);

        request.setAttribute("bolonhaStudentEnrollmentBean",
                new ImprovementBolonhaStudentEnrolmentBean(studentCurricularPlan, executionSemester, evaluationSeason));

        return mapping.findForward("showDegreeModulesToEnrol");
    }

    @Override
    protected String getAction() {
        return "/improvementBolonhaStudentEnrolment.do";
    }

    @SuppressWarnings("serial")
    static private class ImprovementChooseEvaluationSeasonBean
            extends org.fenixedu.academic.dto.student.enrollment.bolonha.ImprovementChooseEvaluationSeasonBean {

        @Override
        public Collection<EvaluationSeason> getActiveEvaluationSeasons() {
            return EvaluationSeasonServices.findByActive(true).filter(i -> i.isImprovement())
                    .sorted(EvaluationSeasonServices.SEASON_ORDER_COMPARATOR).collect(Collectors.toList());
        }
    }

    @SuppressWarnings("serial")
    static private class ImprovementBolonhaStudentEnrolmentBean
            extends org.fenixedu.academic.dto.student.enrollment.bolonha.ImprovementBolonhaStudentEnrolmentBean {

        public ImprovementBolonhaStudentEnrolmentBean(final StudentCurricularPlan studentCurricularPlan,
                final ExecutionSemester executionSemester, final EvaluationSeason evaluationSeason) {
            super(studentCurricularPlan, executionSemester, evaluationSeason);
            setRootStudentCurriculumGroupBean(createBean(studentCurricularPlan, executionSemester, evaluationSeason));
        }

        private static StudentCurriculumGroupBean createBean(final StudentCurricularPlan scp, final ExecutionSemester semester,
                final EvaluationSeason evaluationSeason) {
            return ImprovementStudentCurriculumGroupBean.create(scp.getRoot(), semester, evaluationSeason);
        }
    }

    @SuppressWarnings("serial")
    static private class ImprovementStudentCurriculumGroupBean
            extends org.fenixedu.academic.dto.student.enrollment.bolonha.ImprovementStudentCurriculumGroupBean {

        protected ImprovementStudentCurriculumGroupBean(final EvaluationSeason evaluationSeason) {
            super(evaluationSeason);
        }

        @Override
        public StudentCurriculumGroupBean create(final CurriculumGroup curriculumGroup,
                final ExecutionSemester executionSemester) {
            return new StudentCurriculumGroupBean(curriculumGroup, executionSemester, null) {

                @Override
                protected List<IDegreeModuleToEvaluate> buildCourseGroupsToEnrol(CurriculumGroup group,
                        ExecutionSemester executionSemester) {
                    return Collections.emptyList();
                }

                @Override
                protected List<StudentCurriculumEnrolmentBean> buildCurricularCoursesEnroled(CurriculumGroup group,
                        ExecutionSemester executionSemester) {

                    List<StudentCurriculumEnrolmentBean> result = new ArrayList<StudentCurriculumEnrolmentBean>();
                    for (CurriculumModule curriculumModule : group.getCurriculumModulesSet()) {
                        if (curriculumModule.isEnrolment()) {
                            Enrolment enrolment = (Enrolment) curriculumModule;

                            if (enrolment.isEnroledInSeason(getEvaluationSeason(), executionSemester)) {
                                result.add(new StudentCurriculumEnrolmentBean(enrolment));
                            }
                        }
                    }

                    return result;
                }

                @Override
                protected List<IDegreeModuleToEvaluate> buildCurricularCoursesToEnrol(CurriculumGroup group,
                        ExecutionSemester executionSemester) {

                    final List<IDegreeModuleToEvaluate> result = new ArrayList<IDegreeModuleToEvaluate>();

                    for (CurriculumModule curriculumModule : group.getCurriculumModulesSet()) {
                        if (curriculumModule.isEnrolment()) {
                            final Enrolment enrolment = (Enrolment) curriculumModule;

                            if (Enrolment.getPredicateImprovement().fill(getEvaluationSeason(), executionSemester,
                                    EnrolmentEvaluationContext.MARK_SHEET_EVALUATION).testExceptionless(enrolment)) {

                                if (enrolment.parentCurriculumGroupIsNoCourseGroupCurriculumGroup()) {
                                    result.add(new NoCourseGroupEnroledCurriculumModuleWrapper(enrolment,
                                            enrolment.getExecutionPeriod()));
                                } else {
                                    result.add(new EnroledCurriculumModuleWrapper(enrolment, enrolment.getExecutionPeriod()));
                                }
                            }
                        }
                    }

                    return result;
                }

                @Override
                protected List<StudentCurriculumGroupBean> buildCurriculumGroupsEnroled(CurriculumGroup parentGroup,
                        ExecutionSemester executionSemester, int[] curricularYears) {

                    final List<StudentCurriculumGroupBean> result = new ArrayList<StudentCurriculumGroupBean>();

                    final Set<CurriculumGroup> curriculumGroupsToEnrolmentProcess =
                            parentGroup.getCurriculumGroupsToEnrolmentProcess();
                    if (!parentGroup.isNoCourseGroupCurriculumGroup()) {
                        for (final NoCourseGroupCurriculumGroup curriculumGroup : parentGroup
                                .getNoCourseGroupCurriculumGroups()) {
                            if (curriculumGroup.isVisible()) {
                                curriculumGroupsToEnrolmentProcess.add(curriculumGroup);
                            }
                        }
                    }

                    for (final CurriculumGroup curriculumGroup : curriculumGroupsToEnrolmentProcess) {
                        result.add(create(curriculumGroup, executionSemester));
                    }

                    return result;
                }

                @Override
                public List<IDegreeModuleToEvaluate> getSortedDegreeModulesToEvaluate() {
                    final List<IDegreeModuleToEvaluate> result =
                            new ArrayList<IDegreeModuleToEvaluate>(getCurricularCoursesToEnrol());
                    Collections.sort(result, IDegreeModuleToEvaluate.COMPARATOR_BY_EXECUTION_PERIOD);
                    return result;
                }

                @Override
                public boolean isToBeDisabled() {
                    return true;
                }
            };
        }

        public static StudentCurriculumGroupBean create(final CurriculumGroup curriculumGroup,
                final ExecutionSemester executionSemester, final EvaluationSeason evaluationSeason) {
            return new ImprovementStudentCurriculumGroupBean(evaluationSeason).create(curriculumGroup, executionSemester);
        }

    }

}
