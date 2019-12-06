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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationContext;
import org.fenixedu.academic.domain.enrolment.EnroledCurriculumModuleWrapper;
import org.fenixedu.academic.domain.enrolment.EnrolmentServices;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.exceptions.EnrollmentDomainException;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.NoCourseGroupCurriculumGroup;
import org.fenixedu.academic.dto.student.enrollment.bolonha.NoCourseGroupEnroledCurriculumModuleWrapper;
import org.fenixedu.academic.dto.student.enrollment.bolonha.StudentCurriculumEnrolmentBean;
import org.fenixedu.academic.dto.student.enrollment.bolonha.StudentCurriculumGroupBean;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.ui.renderers.student.enrollment.bolonha.ImprovementEnrolmentLayout;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.AttendsServices;

import com.google.common.collect.Lists;

import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixframework.Atomic;

@Mapping(path = "/improvementBolonhaStudentEnrolment", module = "academicAdministration",
        formBean = "bolonhaStudentEnrollmentForm", functionality = SearchForStudentsDA.class)
@Forwards({
        @Forward(name = "chooseEvaluationSeason",
                path = "/academicAdminOffice/student/enrollment/bolonha/chooseEvaluationSeason.jsp"),
        @Forward(name = "showDegreeModulesToEnrol",
                path = "/academicAdminOffice/student/enrollment/bolonha/showDegreeModulesToEnrol.jsp"),
        @Forward(name = "showImprovementsToAttend",
                path = "/academicAdminOffice/student/enrollment/bolonha/showImprovementsToAttend.jsp") })
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
            HttpServletResponse response, StudentCurricularPlan studentCurricularPlan, ExecutionInterval executionSemester,
            final EvaluationSeason evaluationSeason) {

        super.prepareShowDegreeModulesToEnrol(mapping, form, request, response, studentCurricularPlan, executionSemester,
                evaluationSeason);

        request.setAttribute("bolonhaStudentEnrollmentBean",
                new ImprovementBolonhaStudentEnrolmentBean(studentCurricularPlan, executionSemester, evaluationSeason));
        request.setAttribute("enrolmentLayoutClassName", ImprovementEnrolmentLayout.class.getName());

        return mapping.findForward("showDegreeModulesToEnrol");
    }

    @Override
    protected String getAction() {
        return "/improvementBolonhaStudentEnrolment.do";
    }

    @Override
    public ActionForward enrolInDegreeModules(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixServiceException {

        final ImprovementBolonhaStudentEnrolmentBean bean =
                (ImprovementBolonhaStudentEnrolmentBean) getBolonhaStudentEnrollmentBeanFromViewState();
        try {
            StudentCurricularPlan studentCurricularPlan = bean.getStudentCurricularPlan();
            final RuleResult ruleResults = studentCurricularPlan.enrol(bean.getExecutionPeriod(),
                    new HashSet<IDegreeModuleToEvaluate>(bean.getDegreeModulesToEvaluate()), bean.getCurriculumModulesToRemove(),
                    bean.getCurricularRuleLevel(), bean.getEvaluationSeason());

            if (!bean.getDegreeModulesToEvaluate().isEmpty() || !bean.getCurriculumModulesToRemove().isEmpty()) {
                addActionMessage("success", request, "label.save.success");
            }

            if (ruleResults.isWarning()) {
                addRuleResultMessagesToActionMessages("warning", request, ruleResults);
            }

            enroledWithSuccess(request, bean);

        } catch (EnrollmentDomainException ex) {
            addRuleResultMessagesToActionMessages("error", request, ex.getFalseResult());

            return prepareShowDegreeModulesToEnrol(mapping, form, request, response, bean);

        } catch (DomainException ex) {
            addActionMessage("error", request, ex.getKey(), ex.getArgs());

            return prepareShowDegreeModulesToEnrol(mapping, form, request, response, bean);
        }

        // qubExtension, if at least one improvement doesn't have attends
        final Collection<ImprovementAttendsBean> improvementAttendsBeans = getImprovementAttendsBeans(bean);
        if (improvementAttendsBeans.stream().anyMatch(i -> !i.isShiftEnroled())) {

            return prepareShowImprovementsToAttend(mapping, form, request, response, bean, improvementAttendsBeans);

        } else {

            RenderUtils.invalidateViewState();
            return prepareShowDegreeModulesToEnrol(mapping, form, request, response, bean.getStudentCurricularPlan(),
                    bean.getExecutionPeriod(), bean.getEvaluationSeason());
        }
    }

    private ActionForward prepareShowImprovementsToAttend(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response,
            final ImprovementBolonhaStudentEnrolmentBean bean, final Collection<ImprovementAttendsBean> improvementAttendsBeans) {

        request.setAttribute("bolonhaStudentEnrollmentBean", bean);
        request.setAttribute("evaluationSeason", bean.getEvaluationSeason().getName().getContent());
        request.setAttribute("improvementAttendsBeans", improvementAttendsBeans);

        addDebtsWarningMessages(bean.getStudentCurricularPlan().getRegistration().getStudent(), bean.getExecutionPeriod(),
                request);

        return mapping.findForward("showImprovementsToAttend");
    }

    public ActionForward enrolInAttend(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) {

        final ImprovementBolonhaStudentEnrolmentBean bean =
                (ImprovementBolonhaStudentEnrolmentBean) getBolonhaStudentEnrollmentBeanFromViewState();

        final Enrolment enrolment = getDomainObject(request, "enrolmentId");
        final ExecutionCourse executionCourse = getDomainObject(request, "executionCourseId");
        final ExecutionInterval improvementInterval = bean.getExecutionPeriod();
        createOrSwitchAttend(enrolment, improvementInterval, executionCourse);

        return prepareShowImprovementsToAttend(mapping, form, request, response, bean, getImprovementAttendsBeans(bean));
    }

    @Atomic
    static private void createOrSwitchAttend(final Enrolment enrolment, final ExecutionInterval improvementInterval,
            final ExecutionCourse executionCourse) {

        final Attends current = enrolment.getAttendsFor(improvementInterval);
        if (current != null) {
            // the UI garantees that we won't be able to switch if any shifts are associated with the existing attends
            current.delete();
        }

        AttendsServices.createAttend(enrolment, executionCourse);
    }

    static private Collection<ImprovementAttendsBean> getImprovementAttendsBeans(
            final ImprovementBolonhaStudentEnrolmentBean input) {
        final List<ImprovementAttendsBean> result = Lists.newArrayList();

        final ExecutionInterval interval = input.getExecutionPeriod();
        for (final EnrolmentEvaluation evaluation : input.getStudentCurricularPlan().getEnroledImprovements(interval)) {
            result.add(createImprovementAttendsBeans(evaluation.getEnrolment(), interval));
        }

        Collections.sort(result, new BeanComparator<>("enrolment.externalId"));
        return result;
    }

    static private ImprovementAttendsBean createImprovementAttendsBeans(final Enrolment enrolment,
            final ExecutionInterval interval) {

        final ImprovementAttendsBean result = new ImprovementAttendsBean(enrolment, interval);
        if (result.getAttends() == null) {

            // find unique execution
            final ExecutionCourse executionUnique = result.getExecutionCourseUnique();
            if (executionUnique != null) {
                result.setAttends(AttendsServices.createAttend(enrolment, executionUnique));
            }
        }

        return result;
    }

    static public class ImprovementAttendsBean {

        private Enrolment enrolment;
        private ExecutionInterval semester;
        private Attends attends;
        private Set<ExecutionCourse> executionCourses;

        private ImprovementAttendsBean(final Enrolment enrolment, final ExecutionInterval interval) {
            this.enrolment = enrolment;
            this.semester = interval;
            setAttends(enrolment.getAttendsFor(interval));
        }

        public Enrolment getEnrolment() {
            return enrolment;
        }

        public Attends getAttends() {
            return this.attends;
        }

        public void setAttends(final Attends input) {
            this.attends = input;
        }

        public boolean isShiftEnroled() {
            return getAttends() != null && getAttends().hasAnyShiftEnrolments();
        }

        public Set<ExecutionCourse> getExecutionCourses() {
            if (executionCourses == null) {
                executionCourses = EnrolmentServices.getExecutionCourses(getEnrolment(), semester);
            }

            return executionCourses;
        }

        /**
         * This is ugly, but may avoid unnecessary UI interaction.
         * Will be useless if exists more than one execution course for this curricular course
         * Based on org.fenixedu.academic.domain.Enrolment.createAttendForImprovement(ExecutionSemester)
         */
        private ExecutionCourse getExecutionCourseUnique() {
            return EnrolmentServices.getExecutionCourseUnique(getEnrolment(), semester);
        }
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
                final ExecutionInterval executionInterval, final EvaluationSeason evaluationSeason) {
            super(studentCurricularPlan, executionInterval, evaluationSeason);
            setRootStudentCurriculumGroupBean(createBean(studentCurricularPlan, executionInterval, evaluationSeason));
        }

        private static StudentCurriculumGroupBean createBean(final StudentCurricularPlan scp, final ExecutionInterval semester,
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
                final ExecutionInterval executionInterval) {
            return new StudentCurriculumGroupBean(curriculumGroup, executionInterval, null) {

                @Override
                protected List<IDegreeModuleToEvaluate> buildCourseGroupsToEnrol(CurriculumGroup group,
                        ExecutionInterval executionInterval) {
                    return Collections.emptyList();
                }

                @Override
                protected List<StudentCurriculumEnrolmentBean> buildCurricularCoursesEnroled(CurriculumGroup group,
                        ExecutionInterval executionInterval) {

                    List<StudentCurriculumEnrolmentBean> result = new ArrayList<StudentCurriculumEnrolmentBean>();
                    for (CurriculumModule curriculumModule : group.getCurriculumModulesSet()) {
                        if (curriculumModule.isEnrolment()) {
                            Enrolment enrolment = (Enrolment) curriculumModule;

                            if (enrolment.isEnroledInSeason(getEvaluationSeason(), executionInterval)) {
                                result.add(new StudentCurriculumEnrolmentBean(enrolment));
                            }
                        }
                    }

                    return result;
                }

                @Override
                protected List<IDegreeModuleToEvaluate> buildCurricularCoursesToEnrol(CurriculumGroup group,
                        ExecutionInterval executionInterval) {

                    final List<IDegreeModuleToEvaluate> result = new ArrayList<IDegreeModuleToEvaluate>();

                    for (CurriculumModule curriculumModule : group.getCurriculumModulesSet()) {
                        if (curriculumModule.isEnrolment()) {
                            final Enrolment enrolment = (Enrolment) curriculumModule;

                            if (enrolment.isApproved()
                                    && Enrolment.getPredicateSeason()
                                            .fill(getEvaluationSeason(), executionInterval,
                                                    EnrolmentEvaluationContext.MARK_SHEET_EVALUATION)
                                            .testExceptionless(enrolment)) {

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
                        ExecutionInterval executionInterval, int[] curricularYears) {

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
                        result.add(create(curriculumGroup, executionInterval));
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
                final ExecutionInterval executionSemester, final EvaluationSeason evaluationSeason) {
            return new ImprovementStudentCurriculumGroupBean(evaluationSeason).create(curriculumGroup, executionSemester);
        }

    }

}
