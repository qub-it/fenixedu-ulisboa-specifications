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

        @ConfigurationProperty(key = "domain.academic.enrolments.EnrolmentEvaluationsDependOnAcademicalActsBlocked",
                defaultValue = "true")
        public Boolean getEnrolmentsInEvaluationsDependOnAcademicalActsBlocked();

        @ConfigurationProperty(key = "domain.academic.enrolmentPredicate.override", defaultValue = "true")
        public Boolean getEnrolmentPredicateOverride();

        @ConfigurationProperty(key = "domain.academic.registrationRegimeVerifier.override", defaultValue = "true")
        public Boolean getRegistrationRegimeVerifierOverride();

        @ConfigurationProperty(key = "domain.academic.curricularYearCalculator.override", defaultValue = "true")
        public Boolean getCurricularYearCalculatorOverride();

        @ConfigurationProperty(key = "domain.academic.curricularYearConfiguration.initialize", defaultValue = "false")
        public Boolean getCurricularPeriodConfigurationInitialize();

        @ConfigurationProperty(key = "quality.mode")
        public Boolean isQualityMode();

        @ConfigurationProperty(key = "quality.mode.masterPassword")
        public String getMasterPassword();

        @ConfigurationProperty(key = "quality.mode.lightMasterPassword")
        public String getLightMasterPassword();

        @ConfigurationProperty(key = "services.sas.activeStudents.enabled", defaultValue = "false")
        public Boolean getActiveStudentsServoceEnable();

        @ConfigurationProperty(key = "services.sas.activeStudents.threadNumber", defaultValue = "20")
        public Integer getActiveStudentsThreadNumber();

    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
