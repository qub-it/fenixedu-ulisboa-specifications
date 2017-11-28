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
package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResultMessage;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleLevel;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.exceptions.EnrollmentDomainException;
import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.dml.runtime.RelationAdapter;

abstract public class CurriculumAggregatorListeners {

    static private final Logger logger = LoggerFactory.getLogger(CurriculumAggregatorListeners.class);

    static protected RelationAdapter<DegreeModule, CurriculumModule> ON_CREATION =
            new RelationAdapter<DegreeModule, CurriculumModule>() {

                @Override
                public void afterAdd(final DegreeModule degreeModule, final CurriculumModule curriculumModule) {
                    final Dismissal dismissal = getDismissalToInspect(curriculumModule);

                    // avoid internal invocation with null 
                    if (dismissal == null || degreeModule == null) {
                        return;
                    }

                    if (!CurriculumAggregatorServices.isAggregationsActive(dismissal.getExecutionYear())) {
                        return;
                    }

                    checkToEnrol(dismissal);
                    CurriculumAggregatorServices.updateAggregatorEvaluation(dismissal);
                }
            };

    static protected RelationAdapter<Dismissal, Credits> ON_DELETION = new RelationAdapter<Dismissal, Credits>() {

        @Override
        public void beforeRemove(Dismissal dismissal, final Credits credits) {
            dismissal = getDismissalToInspect(dismissal);

            // avoid internal invocation with null 
            if (dismissal == null || credits == null) {
                return;
            }

            if (!CurriculumAggregatorServices.isAggregationsActive(dismissal.getExecutionYear())) {
                return;
            }

            checkToRemove(dismissal);
            CurriculumAggregatorServices.updateAggregatorEvaluation(dismissal);
        }
    };

    static private Dismissal getDismissalToInspect(final CurriculumModule curriculumModule) {
        if (curriculumModule == null) {
            return null;
        }

        // Enrolments are dealt with explicitly upon their grade change
        if (!(curriculumModule instanceof Dismissal)) {
            return null;
        }

        return (Dismissal) curriculumModule;
    }

    /**
     * @see {@link org.fenixedu.ulisboa.specifications.domain.studentCurriculum.StudentCurricularPlanEnrolmentManager.checkToEnrol()}
     */
    static private void checkToEnrol(final CurriculumLine input) {
        final Set<IDegreeModuleToEvaluate> toChange = Sets.newHashSet();

        final StudentCurricularPlan plan = input.getStudentCurricularPlan();
        final ExecutionSemester semester = input.getExecutionPeriod();
        final Context chosen = CurriculumAggregatorServices.getContext(input);
        final Set<Context> allChosen = Sets.newHashSet(chosen);

        for (final IDegreeModuleToEvaluate iter : CurriculumAggregatorServices.getAggregationParticipantsToEnrol(chosen, plan,
                semester, allChosen)) {

            if (iter.getContext().getChildDegreeModule().isLeaf()) {
                final CurricularCourse curricularCourse = ((CurricularCourse) iter.getContext().getChildDegreeModule());
                if (curricularCourse.getEctsCredits().equals(0d)) {

                    // nothing to be done, no need to enrol in participants that won't contribute with ECTS 
                    continue;
                }
            }

            toChange.add(iter);
        }

        enrolmentManage(toChange, Lists.newArrayList(), plan, semester);
    }

    /**
     * @see {@link org.fenixedu.ulisboa.specifications.domain.studentCurriculum.StudentCurricularPlanEnrolmentManager.checkToRemove()}
     */
    static private void checkToRemove(final CurriculumLine input) {
        final Set<CurriculumModule> toChange = Sets.newHashSet();

        final StudentCurricularPlan plan = input.getStudentCurricularPlan();
        final ExecutionSemester semester = input.getExecutionPeriod();

        for (final CurriculumModule iter : CurriculumAggregatorServices
                .getAggregationParticipantsToRemove(CurriculumAggregatorServices.getContext(input), plan, semester)) {

            toChange.add(iter);
        }

        enrolmentManage(Sets.newHashSet(), Lists.newArrayList(toChange), plan, semester);
    }

    static private void enrolmentManage(final Set<IDegreeModuleToEvaluate> toEnrol, List<CurriculumModule> toRemove,
            final StudentCurricularPlan plan, final ExecutionSemester semester) {

        if (!toEnrol.isEmpty() || !toRemove.isEmpty()) {

            RuleResult ruleResult;
            try {
                ruleResult = new StudentCurricularPlanEnrolmentManager(
                        new EnrolmentContext(plan, semester, toEnrol, toRemove, CurricularRuleLevel.ENROLMENT_WITH_RULES))
                                .manage();
            } catch (final EnrollmentDomainException e) {
                ruleResult = e.getFalseResult();
            }

            // TODO legidio, best way to deal with rule results in this case?
            final String ruleResultString = convertToString(ruleResult);
            if (!Strings.isNullOrEmpty(ruleResultString)) {
                logger.warn(ruleResultString);
            }
        }
    }

    static private String convertToString(final RuleResult ruleResult) {
        final StringBuilder builder = new StringBuilder();

        for (final RuleResultMessage message : ruleResult.getMessages()) {
            if (message.isToTranslate()) {
                builder.append(translateRuleMessage(message));
            } else {
                builder.append(message.getMessage());
            }
            builder.append("\n");
        }

        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    static private String translateRuleMessage(final RuleResultMessage message) {
        return MessageFormat.format(BundleUtil.getString("resources.ApplicationResources", I18N.getLocale(), message.getMessage())
                .replace("{0}", "'{0}'"), (Object[]) message.getArgs());
    }

}
