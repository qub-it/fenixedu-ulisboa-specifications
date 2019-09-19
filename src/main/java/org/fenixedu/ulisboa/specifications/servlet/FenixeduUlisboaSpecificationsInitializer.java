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
import java.util.Comparator;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.FenixeduAcademicExtensionsInitializer;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.gradingTable.CourseGradingTable;
import org.fenixedu.academic.domain.student.gradingTable.DegreeGradingTable;
import org.fenixedu.academic.dto.evaluation.markSheet.MarkBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.servlet.ExceptionHandlerFilter;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalDevModeExceptionHandler;
import org.fenixedu.bennu.portal.servlet.PortalExceptionHandler;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.qubdocs.academic.documentRequests.providers.CurriculumEntry;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.treasury.domain.paymentcodes.pool.PaymentCodePool;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.authentication.ULisboaAuthenticationRedirector;
import org.fenixedu.ulisboa.specifications.domain.CourseGroupDegreeInfo;
import org.fenixedu.ulisboa.specifications.domain.MaximumNumberOfCreditsForEnrolmentPeriodEnforcer;
import org.fenixedu.ulisboa.specifications.domain.ULisboaPortalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.UsernameSequenceGenerator;
import org.fenixedu.ulisboa.specifications.domain.grade.fa.FATypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fba.FBATypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fc.FCTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fd.FDTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.ff.FFTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fl.FLTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fm.FMTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fmd.FMDTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fmh.FMHTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fmv.FMVTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.fp.FPTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.ics.ICSTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.ie.IETypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.igot.IGOTTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.isa.ISATypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.iscsp.ISCSPTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.iseg.ISEGTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.rul.RULTypeQualitativeGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestOutputType;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorMarkSheetServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorRulesInitializer;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo.ConclusionInformationDataProvider;
import org.fenixedu.ulisboa.specifications.task.tmp.FixBugProcessorTypeTask;
import org.fenixedu.ulisboa.specifications.task.tmp.UpdateServiceRequestType;
import org.fenixedu.ulisboa.specifications.ui.blue_record.authentication.BlueRecordRedirector;
import org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.CurriculumLayout;
import org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.StudentCurricularPlanLayout;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

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
        configureTypeQualitativeGradeScaleLogic();
        configureMarkSheetSpecifications();
        configureMaximumNumberOfCreditsForEnrolmentPeriod();

        EnrolmentProcess.init();

        ULisboaSpecificationsRoot.getInstance().getCurriculumAggregatorSet().stream().filter(i -> i.getSince() == null)
                .forEach(i -> i.setSince(ExecutionYear.readExecutionYearByName("2016/2017")));

        CurriculumAggregatorRulesInitializer.init();

        StudentCurricularPlanLayout.register();
        CurriculumLayout.register();

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

        ULisboaAuthenticationRedirector.registerRedirectionHandler(new BlueRecordRedirector());

        initTreasuryNextReferenceCode();

        setupDeleteListenerForPrecedentDegreeInformation();

    }

    static private void initTreasuryNextReferenceCode() {
        PaymentCodePool.findAll().forEach(pool -> {

            if (pool.getNextReferenceCode() == null) {

                logger.info("Initializing Payment code pool next reference code: " + pool.getExternalId());

                final PaymentReferenceCode lastCode = findLastPaymentReferenceCode(pool);
                if (lastCode != null) {
                    final String code = pool.getUseCheckDigit() ? lastCode
                            .getReferenceCodeWithoutCheckDigits() : getSequentialNumber(lastCode);
                    pool.setNextReferenceCode(Long.valueOf(code) + 1);
                } else {
                    logger.warn("Payment code pool initialized with minReferenceCode attribute: " + pool.getExternalId());
                    pool.setNextReferenceCode(pool.getMinReferenceCode());
                }
            }
        });
    }

    static private PaymentReferenceCode findLastPaymentReferenceCode(final PaymentCodePool referenceCodePool) {
        if (referenceCodePool.getUseCheckDigit()) {

            //Sort the payment referenceCodes
            return referenceCodePool.getPaymentReferenceCodesSet().stream()
                    .max((x, y) -> Long.valueOf(x.getReferenceCodeWithoutCheckDigits())
                            .compareTo(Long.valueOf(y.getReferenceCodeWithoutCheckDigits())))
                    .orElse(null);

        } else {

            final Set<PaymentReferenceCode> paymentCodes = referenceCodePool.getPaymentReferenceCodesSet();
            return paymentCodes.isEmpty() ? null : Collections.max(paymentCodes, COMPARATOR_BY_PAYMENT_SEQUENTIAL_DIGITS);
        }
    }

    static private Comparator<PaymentReferenceCode> COMPARATOR_BY_PAYMENT_SEQUENTIAL_DIGITS =
            new Comparator<PaymentReferenceCode>() {
                @Override
                public int compare(final PaymentReferenceCode leftPaymentCode, final PaymentReferenceCode rightPaymentCode) {
                    final String leftSequentialNumber = getSequentialNumber(leftPaymentCode);
                    final String rightSequentialNumber = getSequentialNumber(rightPaymentCode);

                    int comparationResult = leftSequentialNumber.compareTo(rightSequentialNumber);

                    return comparationResult == 0 ? leftPaymentCode.getExternalId()
                            .compareTo(rightPaymentCode.getExternalId()) : comparationResult;
                }
            };

    static private String getSequentialNumber(final PaymentReferenceCode paymentCode) {
        return paymentCode.getReferenceCode().substring(0, paymentCode.getReferenceCode().length() - 2);
    }

    private void setupListenerForDegreeDelete() {
        //we need to delete FirstTime Configuration when a degree is deleted
//        Degree.getRelationDegreeFirstYearRegistrationConfiguration().removeListener(new Relation

        FenixFramework.getDomainModel().registerDeletionListener(Degree.class, new DeletionListener<Degree>() {

            @Override
            public void deleting(final Degree degree) {
                degree.getFirstYearRegistrationConfigurationsSet().forEach(c -> c.delete());

                Site site = degree.getSite();
                if (site != null) {
                    site.delete();
                }
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

    static private void configureTypeQualitativeGradeScaleLogic() {

        GradeScaleLogic logic = null;

        final Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();
        if (institutionUnit != null) {
            switch (institutionUnit.getAcronym()) {

            case "RUL":
                logic = new RULTypeQualitativeGradeScaleLogic();
                break;
            case "FL":
                logic = new FLTypeQualitativeGradeScaleLogic();
                break;
            case "FF":
                logic = new FFTypeQualitativeGradeScaleLogic();
                break;
            case "FMD":
                logic = new FMDTypeQualitativeGradeScaleLogic();
                break;
            case "FMV":
                logic = new FMVTypeQualitativeGradeScaleLogic();
                break;
            case "IGOT":
                logic = new IGOTTypeQualitativeGradeScaleLogic();
                break;
            case "FP":
                logic = new FPTypeQualitativeGradeScaleLogic();
                break;
            case "FDUL":
                logic = new FDTypeQualitativeGradeScaleLogic();
                break;
            case "IE":
                logic = new IETypeQualitativeGradeScaleLogic();
                break;
            case "FBA":
                logic = new FBATypeQualitativeGradeScaleLogic();
                break;
            case "FM":
                logic = new FMTypeQualitativeGradeScaleLogic();
                break;
            case "ICS":
                logic = new ICSTypeQualitativeGradeScaleLogic();
                break;
            case "FMH":
                logic = new FMHTypeQualitativeGradeScaleLogic();
                break;
            case "FC":
                logic = new FCTypeQualitativeGradeScaleLogic();
                break;
            case "FA":
                logic = new FATypeQualitativeGradeScaleLogic();
                break;
            case "ISA":
                logic = new ISATypeQualitativeGradeScaleLogic();
                break;
            case "ISEG":
                logic = new ISEGTypeQualitativeGradeScaleLogic();
                break;
            case "ISCSP":
                logic = new ISCSPTypeQualitativeGradeScaleLogic();
                break;
            default:
                break;
            }
        }

        if (logic == null) {
            logger.warn("Grade Logic for institution '"
                    + (institutionUnit != null ? institutionUnit.getAcronym() : "unknown institution")
                    + "' not found. Attempting to load from configuration property.");
            logic = FenixeduAcademicExtensionsInitializer.loadClass("gradescale.typequalitative.logic.class",
                    ULisboaConfiguration.getConfiguration().typeQualitativeGradeScaleLogic());
        }

        if (logic != null) {
            GradeScale.TYPEQUALITATIVE.setLogic(logic);
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
        FenixFramework.getDomainModel().registerDeletionListener(PrecedentDegreeInformation.class,
                new DeletionListener<PrecedentDegreeInformation>() {
                    @Override
                    public void deleting(PrecedentDegreeInformation precedentDegreeInformation) {
                        precedentDegreeInformation.setDistrictSubdivision(null);
                        precedentDegreeInformation.setDistrict(null);
                    }
                });
    }

}
