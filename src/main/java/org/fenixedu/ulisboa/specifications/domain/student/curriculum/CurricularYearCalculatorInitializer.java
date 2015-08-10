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
package org.fenixedu.ulisboa.specifications.domain.student.curriculum;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.function.Supplier;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.Curriculum.CurricularYearCalculator;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.CycleCurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class CurricularYearCalculatorInitializer {

    static private final Logger logger = LoggerFactory.getLogger(CurricularYearCalculatorInitializer.class);

    static public void init() {

        if (ULisboaConfiguration.getConfiguration().getCurricularYearCalculatorOverride()) {

            Curriculum.setCurricularYearCalculator(CURRICULAR_YEAR_CALCULATOR);
            logger.info("Overriding default");

        } else {

            logger.info("Using default");
        }
    }

    private static Supplier<CurricularYearCalculator> CURRICULAR_YEAR_CALCULATOR = () -> new CurricularYearCalculator() {

        private BigDecimal approvedCredits;

        private BigDecimal remainingCredits;

        private Integer curricularYear;

        private Integer totalCurricularYears;

        @Override
        public Integer curricularYear(final Curriculum curriculum) {
            if (curricularYear == null) {

                curricularYear = calculateCurricularYear(curriculum);
            }

            return curricularYear;
        }

        @Override
        public Integer totalCurricularYears(final Curriculum curriculum) {
            if (totalCurricularYears == null) {

                final StudentCurricularPlan scp = curriculum.getStudentCurricularPlan();
                totalCurricularYears =
                        scp == null ? 0 : scp.getDegreeCurricularPlan().getDurationInYears(getCycleType(curriculum));
            }

            return totalCurricularYears;
        }

        @Override
        public BigDecimal approvedCredits(final Curriculum curriculum) {
            if (approvedCredits == null) {

                approvedCredits = BigDecimal.ZERO;
                for (final ICurriculumEntry entry : curriculum.getCurricularYearEntries()) {
                    approvedCredits = approvedCredits.add(entry.getEctsCreditsForCurriculum());
                }
                accountForDirectIngressions(curriculum);
            }

            return approvedCredits;
        }

        @Override
        public BigDecimal remainingCredits(final Curriculum curriculum) {
            if (remainingCredits == null) {

                remainingCredits = BigDecimal.ZERO;
                for (final ICurriculumEntry entry : curriculum.getCurricularYearEntries()) {
                    if (entry instanceof Dismissal) {
                        final Dismissal dismissal = (Dismissal) entry;
                        if (dismissal.getCredits().isCredits() || dismissal.getCredits().isEquivalence()
                                || (dismissal.isCreditsDismissal() && !dismissal.getCredits().isSubstitution())) {
                            remainingCredits = remainingCredits.add(entry.getEctsCreditsForCurriculum());
                        }
                    }
                }
            }

            return remainingCredits;
        }

        private void accountForDirectIngressions(final Curriculum curriculum) {
            if (getCycleType(curriculum) != null) {
                return;
            }
            if (!curriculum.getStudentCurricularPlan().getDegreeCurricularPlan().isBolonhaDegree()) {
                return;
            }
            //this is to prevent some oddly behavior spotted (e.g. student 57276)
            if (curriculum.getStudentCurricularPlan().getCycleCurriculumGroups().isEmpty()) {
                return;
            }
            CycleCurriculumGroup sgroup =
                    Collections.min(curriculum.getStudentCurricularPlan().getCycleCurriculumGroups(),
                            CycleCurriculumGroup.COMPARATOR_BY_CYCLE_TYPE_AND_ID);
            CycleType cycleIter = sgroup.getCycleType().getPrevious();
            while (cycleIter != null) {
                if (curriculum.getStudentCurricularPlan().getDegreeCurricularPlan().getCycleCourseGroup(cycleIter) != null) {
                    approvedCredits = approvedCredits.add(new BigDecimal(cycleIter.getEctsCredits()));
                }
                cycleIter = cycleIter.getPrevious();
            }
        }

        private CycleType getCycleType(final Curriculum curriculum) {
            if (!curriculum.hasCurriculumModule() || !curriculum.isBolonha()) {
                return null;
            }

            final CurriculumModule module = curriculum.getCurriculumModule();
            final CycleType cycleType = module.isCycleCurriculumGroup() ? ((CycleCurriculumGroup) module).getCycleType() : null;
            return cycleType;
        }

        private Integer calculateCurricularYear(final Curriculum curriculum) {
            Integer result = 1;

            final Registration registration = curriculum.getStudentCurricularPlan().getRegistration();
            final DegreeCurricularPlan dcp = curriculum.getStudentCurricularPlan().getDegreeCurricularPlan();
            for (int i = totalCurricularYears(curriculum); i > 1; i--) {

                final CurricularPeriod curricularPeriod = CurricularPeriodServices.getCurricularPeriod(dcp, i);
                final CurricularPeriodConfiguration configuration =
                        curricularPeriod == null ? null : curricularPeriod.getConfiguration();

                if (configuration == null) {
                    throw new DomainException("curricularRules.ruleExecutors.logic.unavailable", BundleUtil.getString(
                            Bundle.BOLONHA, "label.enrolmentPeriodRestrictions"));
                }

                final RuleResult ruleResult = configuration.verifyRulesForTransition(curriculum);
                if (ruleResult.isTrue()) {
                    result = i;
                    break;
                }
            }

            logger.info("[REG][{}][CURRICULAR_YEAR][{}]", registration.getNumber(), String.valueOf(result));
            return result;
        }

    };

}
