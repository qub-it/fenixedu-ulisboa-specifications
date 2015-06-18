package org.fenixedu.ulisboa.specifications.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaPortalConfiguration;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@WebListener
public class FenixeduUlisboaSpecificationsInitializer implements ServletContextListener {

    @Atomic(mode = TxMode.SPECULATIVE_READ)
    @Override
    public void contextInitialized(ServletContextEvent event) {
//        System.out.println("##################");
//        System.out.println("Type 20 Grade Scale " + ULisboaConfiguration.getConfiguration().type20GradeScaleLogic());
//        System.out.println("Type Qualitative Grade Scale "
//                + ULisboaConfiguration.getConfiguration().typeQualitativeGradeScaleLogic());
//        System.out.println("##################");

        ULisboaPortalConfiguration ulisboaPortal = PortalConfiguration.getInstance().getUlisboaPortal();
        if (ulisboaPortal == null) {
            ulisboaPortal = new ULisboaPortalConfiguration();
            ulisboaPortal.setPortal(PortalConfiguration.getInstance());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}