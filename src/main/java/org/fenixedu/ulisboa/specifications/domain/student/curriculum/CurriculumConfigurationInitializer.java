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
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.Curriculum.CurricularYearCalculator;
import org.fenixedu.academic.domain.student.curriculum.Curriculum.CurriculumEntryPredicate;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup.ConclusionProcessEnabler;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup.CurriculumSupplier;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.CycleCurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.CurricularPeriodRule;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumLineServices;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.services.student.RegistrationDataServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.fenixedu.ulisboa.specifications.servlet.FenixeduUlisboaSpecificationsInitializer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

abstract public class CurriculumConfigurationInitializer {

    static private final Logger logger = LoggerFactory.getLogger(CurriculumConfigurationInitializer.class);

    static public void init() {

        if (ULisboaConfiguration.getConfiguration().getCurricularYearCalculatorOverride()) {
            Curriculum.setCurricularYearCalculator(CURRICULAR_YEAR_CALCULATOR);
            logger.info("CurricularYearCalculator: Overriding default");
        } else {
            logger.info("CurricularYearCalculator: Using default");
        }

        Curriculum.setCurriculumEntryPredicate(CURRICULUM_ENTRY_PREDICATE);
        logger.info("CurriculumEntryPredicate: Overriding default");

        CurriculumGroup.setCurriculumSupplier(CURRICULUM_SUPPLIER);
        logger.info("CurriculumSuppliers: Overriding default");

        CurriculumGroup.setConclusionProcessEnabler(CONCLUSION_PROCESS_ENABLER);
        logger.info("ConclusionProcessEnabler: Overriding default");
    }

    static private Supplier<CurricularYearCalculator> CURRICULAR_YEAR_CALCULATOR = () -> new CurricularYearCalculator() {

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
                totalCurricularYears = CurriculumConfigurationInitializer.totalCurricularYears(curriculum);
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
            CycleCurriculumGroup sgroup = Collections.min(curriculum.getStudentCurricularPlan().getCycleCurriculumGroups(),
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
            return CurriculumConfigurationInitializer.getCycleType(curriculum);
        }

        private Integer calculateCurricularYear(final Curriculum curriculum) {
            return CurriculumConfigurationInitializer.calculateCurricularYear(curriculum).getResult();
        }

    };

    static public CurricularYearResult calculateCurricularYear(final Curriculum curriculum) {
        final ExecutionYear executionYear = curriculum.getExecutionYear();
        final CurricularYearResult calculated = new CurricularYearResult(executionYear);
        final Registration registration = curriculum.getStudentCurricularPlan().getRegistration();

        final DegreeCurricularPlan dcp = curriculum.getStudentCurricularPlan().getDegreeCurricularPlan();
        RuleResult justification = null;
        for (int i = totalCurricularYears(curriculum); i > 1; i--) {

            final CurricularPeriod curricularPeriod = CurricularPeriodServices.getCurricularPeriod(dcp, i);
            final CurricularPeriodConfiguration configuration =
                    curricularPeriod == null ? null : curricularPeriod.getConfiguration();

            if (configuration == null) {
                throw new DomainException("curricularRules.ruleExecutors.logic.unavailable",
                        BundleUtil.getString(Bundle.BOLONHA, "label.enrolmentPeriodRestrictions"));
            }

            final RuleResult ruleResult = configuration.verifyRulesForTransition(curriculum);
            if (ruleResult.isTrue()) {
                calculated.setResult(i);
                if (justification == null) {
                    justification = ruleResult;
                }
                break;
            } else {
                justification = ruleResult;
            }
        }
        calculated.setJustification(justification);

        final Integer curricularYear = RegistrationDataServices.getOverridenCurricularYear(registration, executionYear);
        if (curricularYear != null) {
            final CurricularYearResult overriden = new CurricularYearResult(executionYear);
            overriden.setResult(curricularYear);

            overriden
                    .setJustification(RuleResult.createFalseWithLiteralMessage(curriculum.getCurriculumModule().getDegreeModule(),
                            BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                                    "label.curricularYear.overriden", String.valueOf(overriden.getResult()),
                                    String.valueOf(calculated.getResult()), calculated.getJustificationPresentation())));
            logger.debug("[REG][{}][CURRICULAR_YEAR][OVERRIDEN][{}]", registration.getNumber(),
                    String.valueOf(overriden.getResult()));
            return overriden;
        }

        logger.debug("[REG][{}][CURRICULAR_YEAR][{}]", registration.getNumber(), String.valueOf(calculated.getResult()));
        return calculated;
    }

    static private int totalCurricularYears(final Curriculum curriculum) {
        return curriculum.getStudentCurricularPlan() == null ? 0 : curriculum.getStudentCurricularPlan().getDegreeCurricularPlan()
                .getDurationInYears(getCycleType(curriculum));
    }

    static private CycleType getCycleType(final Curriculum curriculum) {
        if (!curriculum.hasCurriculumModule() || !curriculum.isBolonha()) {
            return null;
        }

        final CurriculumModule module = curriculum.getCurriculumModule();
        final CycleType cycleType = module.isCycleCurriculumGroup() ? ((CycleCurriculumGroup) module).getCycleType() : null;
        return cycleType;
    }

    static public class CurricularYearResult {

        private int curricularYear = 1;

        private RuleResult justification = null;

        private ExecutionYear executionYear;

        public CurricularYearResult(final ExecutionYear executionYear) {
            this.setExecutionYear(executionYear);
        }

        public int getResult() {
            return curricularYear;
        }

        private void setResult(int result) {
            this.curricularYear = result;
        }

        public RuleResult getJustification() {
            return justification;
        }

        private void setJustification(final RuleResult justification) {
            this.justification = justification;
        }

        public String getJustificationPresentation() {
            return CurricularPeriodRule.getMessages(this.justification).replace("Aluno do", "Falhou");
        }

        public ExecutionYear getExecutionYear() {
            return executionYear;
        }

        public void setExecutionYear(ExecutionYear executionYear) {
            this.executionYear = executionYear;
        }
    }

    static private Supplier<CurriculumEntryPredicate> CURRICULUM_ENTRY_PREDICATE = () -> new CurriculumEntryPredicate() {

        @Override
        public boolean test(final ICurriculumEntry input) {

            if (input instanceof CurriculumLine) {
                final CurriculumLine line = (CurriculumLine) input;

                if (CurriculumLineServices.isExcludedFromCurriculum(line)) {
                    return false;
                }

                final Context context = CurriculumAggregatorServices.getContext(line);
                if (context != null && context.getCurriculumAggregatorEntry() != null) {
                    return false;
                }
            }

            return true;
        }
    };

    static public Supplier<CurriculumSupplier> CURRICULUM_SUPPLIER = () -> new CurriculumSupplier() {

        @Override
        public Curriculum get(final CurriculumGroup curriculumGroup, final DateTime when, final ExecutionYear executionYear) {

            final Curriculum result = Curriculum.createEmpty(curriculumGroup, executionYear);

            if (curriculumGroup.wasCreated(when)) {
                for (final CurriculumModule curriculumModule : curriculumGroup.getCurriculumModulesSet()) {
                    result.add(curriculumModule.getCurriculum(when, executionYear));
                }
            }

            final StudentCurricularPlan scp = curriculumGroup.getStudentCurricularPlan();
            final Registration registration = scp.getRegistration();
            if (RegistrationServices.isCurriculumAccumulated(registration)) {

                for (final StudentCurricularPlan otherScp : registration.getSortedStudentCurricularPlans()) {
                    if (otherScp.getStartDateYearMonthDay().isBefore(scp.getStartDateYearMonthDay())) {

                        for (final CurriculumGroup otherGroup : otherGroups(otherScp, curriculumGroup)) {
                            result.add(otherGroup.getCurriculum(when, executionYear));
                        }
                    }
                }
            }

            return result;
        }

        private List<CurriculumGroup> otherGroups(final StudentCurricularPlan otherScp, final CurriculumGroup originalGroup) {

            final List<CurriculumGroup> result = Lists.newArrayList();
            result.add(otherScp.getRoot());
            result.addAll(otherScp.getAllCurriculumGroups());

            final Predicate<CurriculumGroup> predicate;
            final ProgramConclusion programConclusion = originalGroup.getDegreeModule().getProgramConclusion();
            if (programConclusion == null) {

                // take into account this special case: we might be dealing with all of curriculum, not a specific program conclusion
                // eg: integrated master in IST
                predicate = otherGroup -> originalGroup.isRoot() && otherGroup.isRoot();

            } else {

                predicate = otherGroup -> otherGroup.getDegreeModule() != null
                        && otherGroup.getDegreeModule().getProgramConclusion() == programConclusion;
            }

            return result.stream().filter(predicate).collect(Collectors.toList());
        }

    };

    static private Supplier<ConclusionProcessEnabler> CONCLUSION_PROCESS_ENABLER = () -> new ConclusionProcessEnabler() {

        @Override
        public boolean isAllowed(final CurriculumGroup input) {
            if (input != null) {
                return input.isConcluded() || RegistrationServices.isCurriculumAccumulated(input.getRegistration());
            }

            return false;
        }
    };

}
