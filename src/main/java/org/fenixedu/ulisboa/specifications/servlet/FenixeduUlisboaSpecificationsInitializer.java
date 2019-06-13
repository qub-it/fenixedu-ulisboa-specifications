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

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EvaluationConfiguration;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.academic.domain.Qualification;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.AnyCurricularCourseExceptionsInitializer;
import org.fenixedu.academic.domain.curricularRules.EnrolmentPeriodRestrictionsInitializer;
import org.fenixedu.academic.domain.curricularRules.StudentScheduleListeners;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleConfigurationInitializer;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.degreeStructure.OptionalCurricularCourse;
import org.fenixedu.academic.domain.enrolment.EnrolmentManagerFactoryInitializer;
import org.fenixedu.academic.domain.enrolment.EnrolmentPredicateInitializer;
import org.fenixedu.academic.domain.evaluation.EnrolmentEvaluationExtendedInformation;
import org.fenixedu.academic.domain.evaluation.EvaluationComparator;
import org.fenixedu.academic.domain.evaluation.config.MarkSheetSettings;
import org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYearExtendedInformation;
import org.fenixedu.academic.domain.student.RegistrationExtendedInformation;
import org.fenixedu.academic.domain.student.RegistrationRegimeVerifierInitializer;
import org.fenixedu.academic.domain.student.curriculum.CurriculumConfigurationInitializer;
import org.fenixedu.academic.domain.student.curriculum.CurriculumLineExtendedInformation;
import org.fenixedu.academic.domain.student.curriculum.EctsAndWeightProviders;
import org.fenixedu.academic.domain.student.curriculum.conclusion.ConclusionProcessListenersInitializer;
import org.fenixedu.academic.domain.student.gradingTable.CourseGradingTable;
import org.fenixedu.academic.domain.student.gradingTable.DegreeGradingTable;
import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.dto.evaluation.markSheet.MarkBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.servlet.ExceptionHandlerFilter;
import org.fenixedu.bennu.core.signals.Signal;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.servlet.PortalDevModeExceptionHandler;
import org.fenixedu.bennu.portal.servlet.PortalExceptionHandler;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.treasury.domain.paymentcodes.pool.PaymentCodePool;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.authentication.ULisboaAuthenticationRedirector;
import org.fenixedu.academic.domain.degree.ExtendedDegreeInfo;
import org.fenixedu.ulisboa.specifications.domain.MaximumNumberOfCreditsForEnrolmentPeriodEnforcer;
import org.fenixedu.academic.domain.student.RegistrationObservations;
import org.fenixedu.ulisboa.specifications.domain.ULisboaPortalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.UsernameSequenceGenerator;
import org.fenixedu.ulisboa.specifications.domain.grade.common.StandardType20AbsoluteGradeScaleLogic;
import org.fenixedu.ulisboa.specifications.domain.grade.common.StandardType20GradeScaleLogic;
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
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
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
import pt.ist.fenixframework.dml.runtime.RelationAdapter;

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

        if (ULisboaSpecificationsRoot.getInstance().getCurricularPeriodConfigurationSet().stream().anyMatch(
                c -> c.getBennu() == null) || Bennu.getInstance().getAnyCurricularCourseExceptionsConfiguration() == null) {
            migrateSpecificationsRootToBennuRoot();
        }

        ULisboaSpecificationsRoot.init();
        MarkSheetSettings.init();
        configurePortal();
        configureGradeScaleLogics();
        configureMarkSheetSpecifications();
        configureMaximumNumberOfCreditsForEnrolmentPeriod();
        EctsAndWeightProviders.init();
        EnrolmentPeriodRestrictionsInitializer.init();
        EnrolmentProcess.init();
        CurriculumConfigurationInitializer.init();
        ULisboaSpecificationsRoot.getInstance().getCurriculumAggregatorSet().stream().filter(i -> i.getSince() == null)
                .forEach(i -> i.setSince(ExecutionYear.readExecutionYearByName("2016/2017")));
        AnyCurricularCourseExceptionsInitializer.init();
        CurricularRuleConfigurationInitializer.init();
        RegistrationRegimeVerifierInitializer.init();
        EnrolmentPredicateInitializer.init();
        EnrolmentManagerFactoryInitializer.init();
        EvaluationSeasonServices.initialize();
        EvaluationSeasonServices.setEnrolmentsInEvaluationsDependOnAcademicalActsBlocked(
                ULisboaConfiguration.getConfiguration().getEnrolmentsInEvaluationsDependOnAcademicalActsBlocked());
        ConclusionProcessListenersInitializer.init();
        StudentCurricularPlanLayout.register();
        CurriculumLayout.register();
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
        setupListenerForCurricularPeriodDelete();
        setupListenerForEnrolmentDelete();
        setupListenerForSchoolClassDelete();
        setupListenersForStudentSchedule();
        setupListenerForInvalidEquivalences();
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

        CourseGradingTable.registerProvider();
        DegreeGradingTable.registerProvider();

        ExtendedDegreeInfo.setupDeleteListener();
        ExtendedDegreeInfo.setupCreationListener();

        RegistrationObservations.setupDeleteListener();

        CurriculumLineExtendedInformation.setupDeleteListener();

        EnrolmentEvaluationExtendedInformation.setupDeleteListener();

        RegistrationExtendedInformation.setupDeleteListener();

        RegistrationDataByExecutionYearExtendedInformation.setupDeleteListener();

        ULisboaAuthenticationRedirector.registerRedirectionHandler(new BlueRecordRedirector());

        initTreasuryNextReferenceCode();

        registerDeletionListenerOnCurriculumLineForCourseGradingTable();
        registerDeletionListenerOnDegreeModuleForCurriculumLineLogs();

        registerDeletionListenerOnQualification();
        registerDeletionListenerOnUnit();

    }

    @Atomic
    private void migrateSpecificationsRootToBennuRoot() {
        ULisboaSpecificationsRoot.getInstance().getCurricularPeriodConfigurationSet()
                .forEach(c -> c.setBennu(Bennu.getInstance()));
        if (ULisboaSpecificationsRoot.getInstance().getAnyCurricularCourseExceptionsConfiguration() != null) {            
            ULisboaSpecificationsRoot.getInstance().getAnyCurricularCourseExceptionsConfiguration().setBennu(Bennu.getInstance());
        }
    }

    private void registerDeletionListenerOnUnit() {
        FenixFramework.getDomainModel().registerDeletionListener(Unit.class, u -> {
            u.getAcademicAreasSet().clear();
        });
    }

    private void registerDeletionListenerOnQualification() {
        FenixFramework.getDomainModel().registerDeletionListener(Qualification.class, q -> {
            q.getAcademicAreasSet().clear();
            q.getQualificationTypesSet().clear();
            q.setDegreeUnit(null);
            q.setInstitutionUnit(null);
            q.setLevel(null);
        });
    }

    private void registerDeletionListenerOnCurriculumLineForCourseGradingTable() {
        FenixFramework.getDomainModel().registerDeletionListener(CurriculumLine.class, new DeletionListener<CurriculumLine>() {

            @Override
            public void deleting(final CurriculumLine line) {
                if (line.getCourseGradingTable() != null) {
                    line.getCourseGradingTable().delete();
                }
            }
        });
    }

    private void registerDeletionListenerOnDegreeModuleForCurriculumLineLogs() {
        FenixFramework.getDomainModel().registerDeletionListener(DegreeModule.class, dm -> {

            dm.getCurriculumLineLogsSet().forEach(log -> log.delete());

            if (dm instanceof OptionalCurricularCourse) {
                final OptionalCurricularCourse optionalCurricularCourse = (OptionalCurricularCourse) dm;
                optionalCurricularCourse.getOptionalEnrolmentLogsSet().forEach(log -> log.delete());
            }
        });

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

    private void setupListenerForEnrolmentDelete() {
        Attends.getRelationAttendsEnrolment().addListener(new RelationAdapter<Enrolment, Attends>() {
            @Override
            public void beforeRemove(final Enrolment enrolment, final Attends attends) {
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
            public void deleting(final SchoolClass schoolClass) {
                schoolClass.getRegistrationsSet().clear();
                schoolClass.setNextSchoolClass(null);
                schoolClass.getPreviousSchoolClassesSet().clear();
            }
        });
    }

    private void setupListenerForCurricularPeriodDelete() {
        FenixFramework.getDomainModel().registerDeletionListener(CurricularPeriod.class,
                (final CurricularPeriod curricularPeriod) -> {
                    if (curricularPeriod.getConfiguration() != null) {
                        curricularPeriod.getConfiguration().delete();
                    }
                });
    }

    private void setupListenerForInvalidEquivalences() {
        Dismissal.getRelationCreditsDismissalEquivalence().addListener(new RelationAdapter<Dismissal, Credits>() {
            @Override
            public void beforeAdd(final Dismissal dismissal, final Credits credits) {
                if (credits != null && dismissal != null && (dismissal.isCreditsDismissal() || dismissal.isOptional())
                        && credits.isEquivalence()) {
                    throw new DomainException("error.Equivalence.can.only.be.applied.to.curricular.courses");

                }
            }
        });
    }

    private void setupListenersForStudentSchedule() {
        Signal.register(Enrolment.SIGNAL_CREATED, StudentScheduleListeners.SHIFTS_ENROLLER);
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
            logic = loadClass("gradescale.typequalitative.logic.class",
                    ULisboaConfiguration.getConfiguration().typeQualitativeGradeScaleLogic());
        }

        if (logic != null) {
            GradeScale.TYPEQUALITATIVE.setLogic(logic);
        }
    }

    static private void configureType20GradeScaleLogic() {
        GradeScale.TYPE20_ABSOLUTE.setLogic(new StandardType20AbsoluteGradeScaleLogic());
        GradeScale.TYPE20.setLogic(new StandardType20GradeScaleLogic());
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

    @SuppressWarnings("unchecked")
    static public <T> T loadClass(final String key, final String value) {
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
            logger.debug("Using " + result.getClass().getSimpleName());
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

    private void setupCustomExceptionHandler(final ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        PortalExceptionHandler exceptionHandler =
                CoreConfiguration.getConfiguration().developmentMode() == Boolean.TRUE ? new PortalDevModeExceptionHandler(
                        servletContext) : new FenixEduUlisboaExceptionHandler(servletContext);
        ExceptionHandlerFilter.setExceptionHandler(exceptionHandler);
    }

}
