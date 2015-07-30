package org.fenixedu.ulisboa.specifications.domain;

public class MaximumNumberOfCreditsForEnrolmentPeriodEnforcer extends MaximumNumberOfCreditsForEnrolmentPeriodEnforcer_Base {

    public MaximumNumberOfCreditsForEnrolmentPeriodEnforcer() {
        super();
    }

    private static MaximumNumberOfCreditsForEnrolmentPeriodEnforcer instance;

    public static MaximumNumberOfCreditsForEnrolmentPeriodEnforcer getInstance() {
        if (instance == null) {
            instance = ULisboaSpecificationsRoot.getInstance().getMaximumNumberOfCreditsForEnrolmentPeriodEnforcer();
        }
        return instance;
    }

    public void delete() {
        setULisboaSpecificationsRoot(null);
        super.deleteDomainObject();
    }

}
