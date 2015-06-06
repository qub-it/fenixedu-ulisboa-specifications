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

    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
