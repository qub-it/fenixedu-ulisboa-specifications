package org.fenixedu.ulisboa.specifications.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.EvaluationConfiguration;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.MaximumNumberOfCreditsForEnrolmentPeriodEnforcer;
import org.fenixedu.ulisboa.specifications.domain.ULisboaPortalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EvaluationComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@WebListener
public class FenixeduUlisboaSpecificationsInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(FenixeduUlisboaSpecificationsInitializer.class);

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Atomic(mode = TxMode.SPECULATIVE_READ)
    @Override
    public void contextInitialized(ServletContextEvent event) {
        configurePortal();
        configureGradeScaleLogics();
        configureMaximumNumberOfCreditsForEnrolmentPeriod();
        configureEnrolmentEvaluationComparator();
    }

    private void configureEnrolmentEvaluationComparator() {
        EvaluationConfiguration.setEnrolmentEvaluationOrder(new EvaluationComparator());
    }

    static private void configurePortal() {
        ULisboaPortalConfiguration ulisboaPortal = PortalConfiguration.getInstance().getUlisboaPortal();
        if (ulisboaPortal == null) {
            ulisboaPortal = new ULisboaPortalConfiguration();
            ulisboaPortal.setPortal(PortalConfiguration.getInstance());
        }
    }

    static private void configureGradeScaleLogics() {
        try {
            configureType20GradeScaleLogic();
            configureTypeQualitativeGradeScaleLogic();
        } catch (RuntimeException e) {
            if (CoreConfiguration.getConfiguration().developmentMode()) {
                logger.info("You do not have a configured degree scale. This may lead to wrong system behaviour");
                return;
            }
            throw e;
        }
    }

    static private void configureTypeQualitativeGradeScaleLogic() {
        if (StringUtils.isBlank(ULisboaConfiguration.getConfiguration().typeQualitativeGradeScaleLogic())) {
            throw new RuntimeException("Property 'gradescale.typequalitative.logic.class' must be defined in configuration file");
        }

        final GradeScaleLogic logic = loadClass(ULisboaConfiguration.getConfiguration().typeQualitativeGradeScaleLogic());
        if (logic != null) {
            logger.info("Using " + logic.getClass().getSimpleName());
        }

        GradeScale.TYPEQUALITATIVE.setLogic(logic);
    }

    static private void configureType20GradeScaleLogic() {
        if (StringUtils.isBlank(ULisboaConfiguration.getConfiguration().type20GradeScaleLogic())) {
            throw new RuntimeException("Property 'gradescale.type20.logic.class' must be defined in configuration file");
        }

        final GradeScaleLogic logic = loadClass(ULisboaConfiguration.getConfiguration().type20GradeScaleLogic());
        if (logic != null) {
            logger.info("Using " + logic.getClass().getSimpleName());
        }

        GradeScale.TYPE20.setLogic(logic);
    }

    @SuppressWarnings("unchecked")
    static private <T> T loadClass(final String className) {
        try {

            T result = null;
            if (StringUtils.isNotBlank(className)) {
                result = (T) Class.forName(className).newInstance();
            }
            return result;

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("An error occured loading class: " + className, e);
        }
    }

    static private void configureMaximumNumberOfCreditsForEnrolmentPeriod() {
        MaximumNumberOfCreditsForEnrolmentPeriodEnforcer.init();

        final MaximumNumberOfCreditsForEnrolmentPeriodEnforcer enforcer =
                MaximumNumberOfCreditsForEnrolmentPeriodEnforcer.getInstance();

        if (enforcer == null) {

            LoggerFactory.getLogger(MaximumNumberOfCreditsForEnrolmentPeriodEnforcer.class).warn(
                    "Unavailable. If unchanged, rules are using default FenixEdu Academic's values.");

        } else if (ULisboaConfiguration.getConfiguration().getEnrolmentsMaxCreditsUpdateRules()) {

            enforcer.updateRules();
        }
    }

}
