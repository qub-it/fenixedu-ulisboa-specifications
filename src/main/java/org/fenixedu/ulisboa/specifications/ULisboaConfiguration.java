package org.fenixedu.ulisboa.specifications;

import org.fenixedu.commons.configuration.ConfigurationInvocationHandler;
import org.fenixedu.commons.configuration.ConfigurationManager;
import org.fenixedu.commons.configuration.ConfigurationProperty;

public class ULisboaConfiguration {

    @ConfigurationManager(description = "ULisboa Configuration")
    public static interface ConfigurationProperties {

        @ConfigurationProperty(key = "gradescale.typequalitative.logic.class")
        public String typeQualitativeGradeScaleLogic();

        @ConfigurationProperty(key = "domain.academic.curriculumGradeCalculator.visualizer",
                defaultValue = "/academicAdminOffice/student/registration/curriculumGradeCalculator.jsp")
        public String getCurriculumGradeCalculatorVisualizer();

        @ConfigurationProperty(key = "domain.academic.curriculumGradeCalculator.rawGradeRoundingMode", defaultValue = "HALF_UP")
        public String getCurriculumGradeCalculatorRawGradeRoundingMode();

        @ConfigurationProperty(key = "domain.academic.curriculumGradeCalculator.rawGradeRoundingMode.forDegrees",
                defaultValue = "")
        public String getCurriculumGradeCalculatorRawGradeRoundingModeForDegrees();

        @ConfigurationProperty(key = "domain.academic.curriculumAggregator.firstExecutionYearName", defaultValue = "2016/2017")
        public String getCurriculumAggregatorFirstExecutionYearName();

        @ConfigurationProperty(key = "services.sas.activeStudents.enabled", defaultValue = "false")
        public Boolean getActiveStudentsServoceEnable();

        @ConfigurationProperty(key = "services.sas.activeStudents.threadNumber", defaultValue = "20")
        public Integer getActiveStudentsThreadNumber();

        @ConfigurationProperty(key = "executionCourse.more.funcs", defaultValue = "false")
        public Boolean getShowAllExecutionCourseFuncs();

        @ConfigurationProperty(key = "ldap.integration.useCustomGivenNames", defaultValue = "false")
        public Boolean getUseCustomGivenNames();

        @ConfigurationProperty(key = "ldap.integration.sendHashedPassword", defaultValue = "true")
        public Boolean getSendHashedPassword();

        @ConfigurationProperty(key = "domain.academic.student.enrolment.automaticSchoolClassEnrolmentMethod",
                defaultValue = "FILL_FIRST")
        public String getAutomaticSchoolClassEnrolmentMethod();
    }

    public static ConfigurationProperties getConfiguration() {
        return ConfigurationInvocationHandler.getConfiguration(ConfigurationProperties.class);
    }

}
