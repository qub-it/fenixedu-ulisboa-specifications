/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EvaluationConfiguration;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.curricularRules.EnrolmentPeriodRestrictionsInitializer;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.ui.struts.action.student.enrollment.EnrolmentContextHandler;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.servlets.ExceptionHandlerFilter;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalDevModeExceptionHandler;
import org.fenixedu.bennu.portal.servlet.PortalExceptionHandler;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;
import org.fenixedu.ulisboa.specifications.domain.MaximumNumberOfCreditsForEnrolmentPeriodEnforcer;
import org.fenixedu.ulisboa.specifications.domain.RegistrationObservations;
import org.fenixedu.ulisboa.specifications.domain.ULisboaPortalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.UsernameSequenceGenerator;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfigurationInitializer;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.AnyCurricularCourseExceptionsInitializer;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EvaluationComparator;
import org.fenixedu.ulisboa.specifications.domain.evaluation.config.MarkSheetSettings;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;
import org.fenixedu.ulisboa.specifications.domain.student.EnrolmentPredicateInitializer;
import org.fenixedu.ulisboa.specifications.domain.student.RegistrationRegimeVerifierInitializer;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.CurricularYearCalculatorInitializer;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.EnrolmentManagerFactoryInitializer;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.UlisboaEnrolmentContextHandler;
import org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.StudentCurricularPlanLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;
import pt.ist.fenixframework.dml.runtime.RelationAdapter;

@WebListener
public class FenixeduUlisboaSpecificationsInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(FenixeduUlisboaSpecificationsInitializer.class);

    public static final String BUNDLE = "resources/FenixeduUlisboaSpecificationsResources";

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Atomic(mode = TxMode.SPECULATIVE_READ)
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ULisboaSpecificationsRoot.init();
        MarkSheetSettings.init();
        configurePortal();
        configureGradeScaleLogics();
        configureMaximumNumberOfCreditsForEnrolmentPeriod();
        EnrolmentPeriodRestrictionsInitializer.init();
        CurricularYearCalculatorInitializer.init();
        CurricularPeriodConfigurationInitializer.init();
        AnyCurricularCourseExceptionsInitializer.init();
        RegistrationRegimeVerifierInitializer.init();
        EnrolmentPredicateInitializer.init();
        EnrolmentManagerFactoryInitializer.init();
        EvaluationSeasonServices.initialize();
        StudentCurricularPlanLayout.register();
        configureEnrolmentEvaluationComparator();

        UsernameSequenceGenerator usernameSequenceGenerator =
                ULisboaSpecificationsRoot.getInstance().getUsernameSequenceGenerator();
        if (usernameSequenceGenerator == null) {
            usernameSequenceGenerator = new UsernameSequenceGenerator();
            ULisboaSpecificationsRoot.getInstance().setUsernameSequenceGenerator(usernameSequenceGenerator);
        }
        User.setUsernameGenerator(usernameSequenceGenerator);
        DynamicGroup dynamicGroup = org.fenixedu.bennu.core.groups.DynamicGroup.get("employees");
        if (!dynamicGroup.isDefined()) {
            dynamicGroup.toPersistentGroup();
        }

        setupCustomExceptionHandler(event);
        setupListenerForDegreeDelete();
        setupListenerForEnrolmentDelete();
        setupListenerForSchoolClassDelete();
        ULisboaServiceRequest.setupListenerForPropertiesDeletion();
        ULisboaServiceRequest.setupListenerForServiceRequestTypeDeletion();

        ServiceRequestSlot.initStaticSlots();
        ULisboaServiceRequestProcessor.initValidators();

        RegistrationObservations.setupDeleteListener();

        EnrolmentContextHandler.registerEnrolmentContextHandler(new UlisboaEnrolmentContextHandler());

    }

    private void setupListenerForDegreeDelete() {
        //we need to delete FirstTime Configuration when a degree is deleted
//        Degree.getRelationDegreeFirstYearRegistrationConfiguration().removeListener(new Relation

        FenixFramework.getDomainModel().registerDeletionListener(Degree.class, new DeletionListener<Degree>() {

            @Override
            public void deleting(Degree degree) {
                FirstYearRegistrationConfiguration firstYearRegistrationConfiguration =
                        degree.getFirstYearRegistrationConfiguration();
                if (firstYearRegistrationConfiguration != null) {
                    firstYearRegistrationConfiguration.delete();
                }
            }
        });
    }

    private void setupListenerForEnrolmentDelete() {
        Attends.getRelationAttendsEnrolment().addListener(new RelationAdapter<Enrolment, Attends>() {
            @Override
            public void beforeRemove(Enrolment enrolment, Attends attends) {
                final Registration registration = attends.getRegistration();
                if (registration != null) {
                    attends.getExecutionCourse().getAssociatedShifts().forEach(s -> s.removeStudents(registration));
                }
            }
        });
    }

    private void setupListenerForSchoolClassDelete() {
        FenixFramework.getDomainModel().registerDeletionListener(SchoolClass.class, new DeletionListener<SchoolClass>() {

            @Override
            public void deleting(SchoolClass schoolClass) {
                schoolClass.getRegistrationsSet().clear();
            }
        });
    }

    static private void configureEnrolmentEvaluationComparator() {
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
        configureType20GradeScaleLogic();
        configureTypeQualitativeGradeScaleLogic();
    }

    static private void configureTypeQualitativeGradeScaleLogic() {
        final GradeScaleLogic logic = loadClass("gradescale.typequalitative.logic.class",
                ULisboaConfiguration.getConfiguration().typeQualitativeGradeScaleLogic());

        if (logic != null) {
            GradeScale.TYPEQUALITATIVE.setLogic(logic);
        }
    }

    static private void configureType20GradeScaleLogic() {
        final GradeScaleLogic logic =
                loadClass("gradescale.type20.logic.class", ULisboaConfiguration.getConfiguration().type20GradeScaleLogic());

        if (logic != null) {
            GradeScale.TYPE20.setLogic(logic);
        }
    }

    @SuppressWarnings("unchecked")
    static private <T> T loadClass(final String key, final String value) {
        T result = null;

        try {

            if (StringUtils.isNotBlank(value)) {
                result = (T) Class.forName(value).newInstance();
            } else {

                final String message = "Property [" + key + "] must be defined in configuration file";
                if (CoreConfiguration.getConfiguration().developmentMode()) {
                    logger.error("{}. Empty value may lead to wrong system behaviour", message);
                } else {
                    throw new RuntimeException(message);
                }
            }

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("An error occured loading class: " + value, e);
        }

        if (result != null) {
            logger.info("Using " + result.getClass().getSimpleName());
        }

        return result;
    }

    static private void configureMaximumNumberOfCreditsForEnrolmentPeriod() {
        final MaximumNumberOfCreditsForEnrolmentPeriodEnforcer enforcer =
                MaximumNumberOfCreditsForEnrolmentPeriodEnforcer.getInstance();

        if (enforcer != null) {
            enforcer.delete();
        }
    }

    private void setupCustomExceptionHandler(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        PortalExceptionHandler exceptionHandler =
                CoreConfiguration.getConfiguration().developmentMode() == Boolean.TRUE ? new PortalDevModeExceptionHandler(
                        servletContext) : new PortalExceptionHandler(servletContext);
        ExceptionHandlerFilter.setExceptionHandler(new FenixEduUlisboaExceptionHandler(exceptionHandler));
    }
}
