package org.fenixedu.ulisboa.specifications.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaPortalConfiguration;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@WebListener
public class FenixeduUlisboaSpecificationsInitializer implements ServletContextListener {

    @Atomic(mode = TxMode.SPECULATIVE_READ)
    @Override
    public void contextInitialized(ServletContextEvent event) {
        configurePortal();
        configureType20GradeScaleLogic();
        configureTypeQualitativeGradeScaleLogic();
    }

    protected void configurePortal() {
        ULisboaPortalConfiguration ulisboaPortal = PortalConfiguration.getInstance().getUlisboaPortal();
        if (ulisboaPortal == null) {
            ulisboaPortal = new ULisboaPortalConfiguration();
            ulisboaPortal.setPortal(PortalConfiguration.getInstance());
        }
    }

    protected void configureTypeQualitativeGradeScaleLogic() {
        if (StringUtils.isBlank(ULisboaConfiguration.getConfiguration().typeQualitativeGradeScaleLogic())) {
            throw new RuntimeException("Property 'gradescale.typequalitative.logic.class' must be defined in configuration file");

        }

        GradeScale.TYPEQUALITATIVE.setLogic(loadGradeScaleLogicClass(ULisboaConfiguration.getConfiguration()
                .typeQualitativeGradeScaleLogic()));
    }

    protected void configureType20GradeScaleLogic() {
        if (StringUtils.isBlank(ULisboaConfiguration.getConfiguration().type20GradeScaleLogic())) {
            throw new RuntimeException("Property 'gradescale.type20.logic.class' must be defined in configuration file");
        }

        GradeScale.TYPE20.setLogic(loadGradeScaleLogicClass(ULisboaConfiguration.getConfiguration().type20GradeScaleLogic()));
    }

    private GradeScaleLogic loadGradeScaleLogicClass(String className) {

        try {
            return (GradeScaleLogic) Class.forName(className).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("An error occured loading grade scale logic class :" + className);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}