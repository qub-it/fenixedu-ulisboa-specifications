package org.fenixedu.ulisboa.specifications.domain;

import java.util.List;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.curricularRules.MaximumNumberOfCreditsForEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

public class MaximumNumberOfCreditsForEnrolmentPeriodEnforcer extends MaximumNumberOfCreditsForEnrolmentPeriodEnforcer_Base {

    private static final Logger logger = LoggerFactory.getLogger(MaximumNumberOfCreditsForEnrolmentPeriodEnforcer.class);

    public MaximumNumberOfCreditsForEnrolmentPeriodEnforcer() {
        super();
    }

    public static void init() {
        if (getInstance() != null) {
            return;
        }
        makeInstance();
    }

    private static MaximumNumberOfCreditsForEnrolmentPeriodEnforcer instance;

    public static MaximumNumberOfCreditsForEnrolmentPeriodEnforcer getInstance() {
        if (instance == null) {
            instance = ULisboaSpecificationsRoot.getInstance().getMaximumNumberOfCreditsForEnrolmentPeriodEnforcer();
        }
        return instance;
    }

    @Atomic
    private static MaximumNumberOfCreditsForEnrolmentPeriodEnforcer makeInstance() {
        final MaximumNumberOfCreditsForEnrolmentPeriodEnforcer result = new MaximumNumberOfCreditsForEnrolmentPeriodEnforcer();

        result.setULisboaSpecificationsRoot(ULisboaSpecificationsRoot.getInstance());
        result.updateFromConfiguration();

        logger.info("Created with values: [maxCredits][{}], [maxCreditsPartialTime][{}]", result.getMaxCredits(),
                result.getMaxCreditsPartialTime());

        return result;
    }

    @Atomic
    private void updateFromConfiguration() {
        setMaxCredits(ULisboaConfiguration.getConfiguration().getEnrolmentsMaxCredits());
        setMaxCreditsPartialTime(ULisboaConfiguration.getConfiguration().getEnrolmentsMaxCreditsPartialTime());

        if (!ULisboaConfiguration.getConfiguration().getEnrolmentsMaxCreditsUpdateRules()) {
            logger.warn("Updating values without updating rules afterwards.");
        }
    }

    @Override
    public void setMaxCredits(final Double maxCredits) {
        super.setMaxCredits(maxCredits);

        if (getMaxCredits() == null) {
            logger.warn("MaxCredits empty. If unchanged, rules are using default FenixEdu Academic's values.");;
        }
    }

    @Override
    public void setMaxCreditsPartialTime(final Double maxCreditsPartialTime) {
        super.setMaxCreditsPartialTime(maxCreditsPartialTime);

        if (getMaxCreditsPartialTime() == null) {
            logger.warn("MaxCreditsPartialTime empty. If unchanged, rules are using default FenixEdu Academic's values.");;
        }
    }

    @Atomic
    public void updateRules() {

        if (ULisboaConfiguration.getConfiguration().getEnrolmentsMaxCreditsUpdateFromConfiguration()) {
            updateFromConfiguration();
        } else {
            logger.info("Updating rules without updating values first.");
        }

        logger.info("Updating rules with values: [maxCredits][{}], [maxCreditsPartialTime][{}]", getMaxCredits(),
                getMaxCreditsPartialTime());

        final ExecutionYear executionYear = ExecutionYear.readCurrentExecutionYear();
        for (final ExecutionDegree executionDegree : executionYear.getExecutionDegreesSet()) {
            final DegreeCurricularPlan degreeCurricularPlan = executionDegree.getDegreeCurricularPlan();

            final List<? extends ICurricularRule> curricularRules =
                    degreeCurricularPlan.getRoot().getCurricularRules(
                            CurricularRuleType.MAXIMUM_NUMBER_OF_CREDITS_FOR_ENROLMENT_PERIOD, executionYear);

            if (curricularRules.size() > 1) {

                logger.error("Update failed: more than one rule to update for DCP {}",
                        degreeCurricularPlan.getPresentationName(executionYear));
            } else {

                final MaximumNumberOfCreditsForEnrolmentPeriod rule =
                        (MaximumNumberOfCreditsForEnrolmentPeriod) curricularRules.iterator().next();

                // TODO legidio rule.setMaxCredits(getMaxCredits());
                // TODO legidio rule.setMaxCreditsPartialTime(getMaxCreditsPartialTime());
            }
        }
    }

}
