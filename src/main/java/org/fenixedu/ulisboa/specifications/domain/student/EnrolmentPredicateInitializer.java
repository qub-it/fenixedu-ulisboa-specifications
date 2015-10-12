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
package org.fenixedu.ulisboa.specifications.domain.student;

import java.util.function.Supplier;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.Enrolment.EnrolmentPredicate;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationContext;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class EnrolmentPredicateInitializer {

    static private final Logger logger = LoggerFactory.getLogger(EnrolmentPredicateInitializer.class);

    static public void init() {

        if (ULisboaConfiguration.getConfiguration().getEnrolmentPredicateOverride()) {

            Enrolment.setPredicateSeason(PREDICATE_SEASON);
            Enrolment.setPredicateImprovement(PREDICATE_IMPROVEMENT);
            logger.info("Overriding default");

        } else {

            logger.info("Using default");
        }
    }

    static private Supplier<EnrolmentPredicate> PREDICATE_SEASON = () -> new EnrolmentPredicate() {

        public boolean test(final Enrolment enrolment) {

            if (enrolment.isEvaluatedInSeason(getEvaluationSeason(), getExecutionSemester())) {
                throw new DomainException("error.EvaluationSeason.enrolment.evaluated.in.this.season", enrolment.getName()
                        .getContent(), getEvaluationSeason().getName().getContent());
            }

            if (getContext() == EnrolmentEvaluationContext.MARK_SHEET_EVALUATION) {
                if (enrolment.isEnroledInSeason(getEvaluationSeason(), getExecutionSemester())) {
                    throw new DomainException("error.EvaluationSeason.already.enroled.in.this.season", enrolment.getName()
                            .getContent(), getEvaluationSeason().getName().getContent());
                }

                if (enrolment.isApproved() && !getEvaluationSeason().isImprovement()) {
                    throw new DomainException("error.EvaluationSeason.evaluation.already.approved", enrolment.getName()
                            .getContent(), getEvaluationSeason().getName().getContent());
                }
            }

            return true;
        }

    };

    static private Supplier<EnrolmentPredicate> PREDICATE_IMPROVEMENT = () -> new EnrolmentPredicate() {

        @Override
        public boolean test(final Enrolment enrolment) {
            final ExecutionSemester improvementSemester = getExecutionSemester();

            final ExecutionSemester enrolmentSemester = enrolment.getExecutionPeriod();
            if (improvementSemester.isBeforeOrEquals(enrolmentSemester)) {
                throw new DomainException("error.EnrolmentEvaluation.improvement.semester.must.be.after.or.equal.enrolment",
                        enrolment.getName().getContent());
            }

            if (!enrolment.isApproved()) {
                throw new DomainException(
                        "curricularRules.ruleExecutors.ImprovementOfApprovedEnrolmentExecutor.degree.module.hasnt.been.approved",
                        enrolment.getName().getContent());
            }

            PREDICATE_SEASON.get().fill(getEvaluationSeason(), improvementSemester, getContext()).test(enrolment);

            final DegreeModule degreeModule = enrolment.getDegreeModule();
            if (!degreeModule.hasAnyParentContexts(improvementSemester)) {
                throw new DomainException(
                        "curricularRules.ruleExecutors.ImprovementOfApprovedEnrolmentExecutor.degree.module.has.no.context.in.present.execution.period",
                        enrolment.getName().getContent(), improvementSemester.getQualifiedName());
            }

            return true;
        }
    };

}
