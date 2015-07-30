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

        @ConfigurationProperty(key = "domain.academic.curricularYearCalculator.override", defaultValue = "true")
        public Boolean getCurricularYearCalculatorOverride();

        @ConfigurationProperty(key = "domain.academic.curricularYearConfiguration.initialize", defaultValue = "false")
        public Boolean getCurricularPeriodConfigurationInitialize();

        @ConfigurationProperty(key = "quality.mode")
        public Boolean isQualityMode();

        @ConfigurationProperty(key = "quality.mode.masterPassword")
        public String getMasterPassword();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
