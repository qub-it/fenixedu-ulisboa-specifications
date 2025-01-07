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

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.gradingTable.CourseGradingTable;
import org.fenixedu.academic.domain.student.gradingTable.DegreeGradingTable;
import org.fenixedu.academic.domain.util.email.CurrentUserReplyTo;
import org.fenixedu.academic.domain.util.email.Message;
import org.fenixedu.academic.domain.util.email.Recipient;
import org.fenixedu.academic.domain.util.email.Sender;
import org.fenixedu.academic.dto.evaluation.markSheet.MarkBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.servlet.ExceptionHandlerFilter;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalDevModeExceptionHandler;
import org.fenixedu.bennu.portal.servlet.PortalExceptionHandler;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.qubdocs.academic.documentRequests.providers.CurriculumEntry;
import org.fenixedu.ulisboa.specifications.domain.CourseGroupDegreeInfo;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.MaximumNumberOfCreditsForEnrolmentPeriodEnforcer;
import org.fenixedu.ulisboa.specifications.domain.ULisboaPortalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestOutputType;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.external.cgd.SyncRegistrationWithCgd;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorMarkSheetServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorRulesInitializer;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo.ConclusionInformationDataProvider;
import org.fenixedu.ulisboa.specifications.task.tmp.FixBugProcessorTypeTask;
import org.fenixedu.ulisboa.specifications.task.tmp.UpdateServiceRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qubit.solution.fenixedu.integration.cgd.domain.configuration.CgdIntegrationConfiguration;
import com.qubit.solution.fenixedu.integration.cgd.domain.configuration.CgdMod43Template;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

@WebListener
public class FenixeduUlisboaSpecificationsInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(FenixeduUlisboaSpecificationsInitializer.class);

    public static final String BUNDLE = "resources/FenixeduUlisboaSpecificationsResources";

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
    }

    @Atomic(mode = TxMode.SPECULATIVE_READ)
    @Override
    public void contextInitialized(final ServletContextEvent event) {

        ULisboaSpecificationsRoot.init();

        configurePortal();
        configureMarkSheetSpecifications();
        configureMaximumNumberOfCreditsForEnrolmentPeriod();

        ULisboaSpecificationsRoot.getInstance().getCurriculumAggregatorSet().stream().filter(i -> i.getSince() == null)
                .forEach(i -> i.setSince(ExecutionYear.readExecutionYearByName("2016/2017")));

        CurriculumAggregatorRulesInitializer.init();

        DynamicGroup dynamicGroup = org.fenixedu.bennu.core.groups.DynamicGroup.get("employees");
        if (!dynamicGroup.isDefined()) {
            dynamicGroup.toPersistentGroup();
        }

        setupCustomExceptionHandler(event);
        setupListenerForDegreeDelete();

        ULisboaServiceRequest.setupListenerForPropertiesDeletion();
        ULisboaServiceRequest.setupListenerForServiceRequestTypeDeletion();

        ServiceRequestSlot.initStaticSlots();
        ServiceRequestOutputType.initOutputTypes();
        ULisboaServiceRequestProcessor.initValidators();

        try {
            new UpdateServiceRequestType().runTask();
            new FixBugProcessorTypeTask().runTask();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        CurriculumEntry.setCourseEctsGradeProviderProvider(entry -> CourseGradingTable.getEctsGrade(entry));
        ConclusionInformationDataProvider
                .setDegreeEctsGradeProviderProvider(conclusion -> DegreeGradingTable.getEctsGrade(conclusion));

        CourseGroupDegreeInfo.setupDeleteListener();

//        AuthenticationRedirector.registerRedirectionHandler(new BlueRecordRedirector());

        setupDeleteListenerForPrecedentDegreeInformation();

        StudentAccessServices.subscribeSyncRegistration(new SyncRegistrationWithCgd());

        migrateCgdTemplate();

        Signal.register("academicServiceRequest.sendConcludeEmailEvent", (DomainObjectEvent<AcademicServiceRequest> request) -> {
            sendConcludeEmail(request.getInstance());
        });
    }

    private void sendConcludeEmail(AcademicServiceRequest request) {
        String body = BundleUtil.getString(Bundle.APPLICATION, "mail.academicServiceRequest.concluded.message1");
        body += " " + request.getServiceRequestNumberYear();
        body += " " + BundleUtil.getString(Bundle.APPLICATION, "mail.academicServiceRequest.concluded.message2");
        body += " '" + request.getDescription();
        body += "' " + BundleUtil.getString(Bundle.APPLICATION, "mail.academicServiceRequest.concluded.message3");

        if (request.getAcademicServiceRequestType() == AcademicServiceRequestType.SPECIAL_SEASON_REQUEST) {
            body += "\n" + BundleUtil.getString(Bundle.APPLICATION, "mail.academicServiceRequest.concluded.messageSSR4B");
            body += "\n\n" + BundleUtil.getString(Bundle.APPLICATION, "mail.academicServiceRequest.concluded.messageSSR5");
        } else {

            body += "\n\n" + BundleUtil.getString(Bundle.APPLICATION, "mail.academicServiceRequest.concluded.message4");

        }

        final Sender sender = Bennu.getInstance().getSystemSender();
        final Recipient recipient = new Recipient(request.getPerson().getUser().groupOf());
        new Message(sender, Collections.singletonList(new CurrentUserReplyTo()), recipient.asCollection(),
                request.getDescription(), body, "");
    }

    private void migrateCgdTemplate() {
        CgdIntegrationConfiguration cgdIntegrationConfiguration = CgdIntegrationConfiguration.getInstance();
        FirstYearRegistrationGlobalConfiguration firstYearConf = FirstYearRegistrationGlobalConfiguration.getInstance();

        CgdMod43Template mod43Template = firstYearConf.getMod43Template();
        if (mod43Template != null && !cgdIntegrationConfiguration.hasMod43Template()) {
            byte[] content = null;
            try {
                content = mod43Template.getContent();
            } catch (Throwable t) {
                // in dev machines this will throw an exception since the
                // file does not exist. Just ignoring it so it doesn't mess
                // the startup.
                content = new byte[] {};
            }
            cgdIntegrationConfiguration.uploadMod43Template(mod43Template.getFilename(), content);
            firstYearConf.setMod43Template(null);
            mod43Template.delete();
        }
    }

    private void setupListenerForDegreeDelete() {
        //we need to delete FirstTime Configuration when a degree is deleted
//        Degree.getRelationDegreeFirstYearRegistrationConfiguration().removeListener(new Relation

        FenixFramework.getDomainModel().registerDeletionListener(Degree.class, degree -> {
            degree.getFirstYearRegistrationConfigurationsSet().forEach(c -> c.delete());

            Site site = degree.getSite();
            if (site != null) {
                site.delete();
            }
        });
    }

    static private void configurePortal() {
        ULisboaPortalConfiguration ulisboaPortal = PortalConfiguration.getInstance().getUlisboaPortal();
        if (ulisboaPortal == null) {
            ulisboaPortal = new ULisboaPortalConfiguration();
            ulisboaPortal.setPortal(PortalConfiguration.getInstance());
        }
    }

    static private void configureMarkSheetSpecifications() {
        MarkBean.setGradeSuggestionCalculator(bean -> {
            final CurriculumAggregator aggregator = CurriculumAggregatorServices.getAggregator(bean.getEnrolment());
            if (aggregator != null && aggregator.isCandidateForEvaluation(bean.getMarkSheet().getEvaluationSeason())) {
                return aggregator.calculateConclusionGrade(bean.getEnrolment().getStudentCurricularPlan());
            }

            return Grade.createEmptyGrade();
        });

        CurriculumAggregatorMarkSheetServices.init();
    }

    static private void configureMaximumNumberOfCreditsForEnrolmentPeriod() {
        final MaximumNumberOfCreditsForEnrolmentPeriodEnforcer enforcer =
                MaximumNumberOfCreditsForEnrolmentPeriodEnforcer.getInstance();

        if (enforcer != null) {
            enforcer.delete();
        }
    }

    private void setupCustomExceptionHandler(final ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        PortalExceptionHandler exceptionHandler =
                CoreConfiguration.getConfiguration().developmentMode() == Boolean.TRUE ? new PortalDevModeExceptionHandler(
                        servletContext) : new FenixEduUlisboaExceptionHandler(servletContext);
        ExceptionHandlerFilter.setExceptionHandler(exceptionHandler);
    }

    private void setupDeleteListenerForPrecedentDegreeInformation() {
        FenixFramework.getDomainModel().registerDeletionListener(PrecedentDegreeInformation.class, precedentDegreeInformation -> {
            precedentDegreeInformation.setDistrictSubdivision(null);
            precedentDegreeInformation.setDistrict(null);
        });
    }

}
