package org.fenixedu.ulisboa.specifications.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;

@WebListener
public class FenixeduUlisboaSpecificationsInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
//        System.out.println("##################");
//        System.out.println("Type 20 Grade Scale " + ULisboaConfiguration.getConfiguration().type20GradeScaleLogic());
//        System.out.println("Type Qualitative Grade Scale "
//                + ULisboaConfiguration.getConfiguration().typeQualitativeGradeScaleLogic());
//        System.out.println("##################");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }
}