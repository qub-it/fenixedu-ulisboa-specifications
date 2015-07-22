package org.fenixedu.ulisboa.specifications;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class ULisboaConfiguration {

    @ConfigurationManager(description = "ULisboa Configuration")
    public static interface ConfigurationProperties {

        @ConfigurationProperty(key = "gradescale.type20.logic.class")
        public String type20GradeScaleLogic();

        @ConfigurationProperty(key = "gradescale.typequalitative.logic.class")
        public String typeQualitativeGradeScaleLogic();

        @ConfigurationProperty(key = "domain.academic.enrolments.maxCredits.value")
        public Double getEnrolmentsMaxCredits();

        @ConfigurationProperty(key = "domain.academic.enrolments.maxCredits.valuePartialTime")
        public Double getEnrolmentsMaxCreditsPartialTime();

        @ConfigurationProperty(key = "domain.academic.enrolments.maxCredits.updateValuesFromConfiguration", defaultValue = "false")
        public Boolean getEnrolmentsMaxCreditsUpdateFromConfiguration();
        
        @ConfigurationProperty(key = "domain.academic.enrolments.maxCredits.updateRulesValues", defaultValue = "false")
        public Boolean getEnrolmentsMaxCreditsUpdateRules();
        
        @ConfigurationProperty(key = "quality.mode")
        public Boolean isQualityMode();

        @ConfigurationProperty(key = "quality.mode.masterPassword")
        public String getMasterPassword();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
