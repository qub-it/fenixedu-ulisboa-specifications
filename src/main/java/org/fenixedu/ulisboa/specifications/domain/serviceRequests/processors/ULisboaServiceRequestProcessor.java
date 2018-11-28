package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.Collection;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.util.LocalizedStringUtil;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public abstract class ULisboaServiceRequestProcessor extends ULisboaServiceRequestProcessor_Base {

    protected ULisboaServiceRequestProcessor() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ULisboaServiceRequestProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        init(name, exclusiveTransation);
        checkRules();
    }

    protected void init(final LocalizedString name, final Boolean exclusiveTransation) {
        setName(name);
        setExclusiveTransation(exclusiveTransation);
    }

    private void checkRules() {
        if (LocalizedStringUtil.isTrimmedEmpty(getName())) {
            throw new ULisboaSpecificationsDomainException("error.ULisboaServiceRequestValidator.name.required");
        }
        getName().getLocales().stream().forEach(l -> {
            if (findByName(getName().getContent(l)).count() > 1) {
                throw new ULisboaSpecificationsDomainException("error.ULisboaServiceRequestValidator.name.duplicated",
                        l.toString());
            } ;
        });
    }

    @Atomic
    public void edit(final LocalizedString name, final Boolean exclusiveTransation) {
        setName(name);
        setExclusiveTransation(exclusiveTransation);
        checkRules();
    }

    @Override
    protected void checkForDeletionBlockers(final Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
        if (!getServiceRequestTypesSet().isEmpty()) {
            blockers.add(BundleUtil.getString(ULisboaConstants.BUNDLE,
                    "error.ULisboaServiceRequestValidator.connected.ServiceRequestTypes"));
        }
    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setBennu(null);
        deleteDomainObject();
    }

    public boolean runExclusiveTransation() {
        return getExclusiveTransation();
    }

    @Override
    public Boolean getExclusiveTransation() {
        return super.getExclusiveTransation() != null ? super.getExclusiveTransation() : Boolean.FALSE;
    }

    public static Stream<ULisboaServiceRequestProcessor> findAll() {
        return Bennu.getInstance().getULisboaServiceRequestProcessorsSet().stream();
    }

    public static Stream<ULisboaServiceRequestProcessor> findByName(final String name) {
        return findAll().filter(request -> LocalizedStringUtil.isEqualToAnyLocaleIgnoreCase(request.getName(), name));
    }

    public static ULisboaServiceRequestProcessor getByName(final String name) {
        return findByName(name).findFirst().get();
    }

    public static void initValidators() {
        ULisboaServiceRequestProcessor processor =
                findAll().filter(p -> p instanceof ValidateImprovementEnrolmentProcessor).findAny().orElse(null);
        if (processor != null) {
            for (ServiceRequestType serviceRequestType : processor.getServiceRequestTypesSet()) {
                serviceRequestType.removeULisboaServiceRequestProcessors(processor);
            }
            processor.delete();
        }
        processor = findAll().filter(p -> p instanceof ValidateSpecialSeasonEnrolmentProcessor).findAny().orElse(null);
        if (processor != null) {
            for (ServiceRequestType serviceRequestType : processor.getServiceRequestTypesSet()) {
                serviceRequestType.removeULisboaServiceRequestProcessors(processor);
            }
            processor.delete();
        }

        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.STATE_LOGGER_PROCESSOR)).count() == 0) {
            StateLoggerProcessor.create(
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, ULisboaConstants.STATE_LOGGER_PROCESSOR),
                    Boolean.FALSE);
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR))
                .count() == 0) {
            FillEnrolmentsByYearPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR), Boolean.FALSE);
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE,
                ULisboaConstants.FILL_STANDALONE_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR)).count() == 0) {
            FillStandaloneEnrolmentsByYearPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_STANDALONE_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR), Boolean.FALSE);
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE,
                ULisboaConstants.FILL_EXTRACURRICULAR_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR)).count() == 0) {
            FillExtracurricularEnrolmentsByYearPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_EXTRACURRICULAR_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR), Boolean.FALSE);
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_EXTRA_CURRICULUM_PROPERTY_PROCESSOR))
                .count() == 0) {
            FillExtracurricularApprovementsPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_EXTRA_CURRICULUM_PROPERTY_PROCESSOR), Boolean.FALSE);
        }
        if (findByName(
                BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_STANDALONE_CURRICULUM_PROPERTY_PROCESSOR))
                        .count() == 0) {
            FillStandAlonePropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_STANDALONE_CURRICULUM_PROPERTY_PROCESSOR), Boolean.FALSE);
        }
        if (findByName(
                BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_APPROVED_ENROLMENTS_PROPERTY_PROCESSOR))
                        .count() == 0) {
            FillApprovedEnrolmentsPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_APPROVED_ENROLMENTS_PROPERTY_PROCESSOR), Boolean.FALSE);
        }
        if (findByName(
                BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_ALL_PLANS_APPROVEMENTS_PROPERTY_PROCESSOR))
                        .count() == 0) {
            FillAllPlansApprovementsPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_ALL_PLANS_APPROVEMENTS_PROPERTY_PROCESSOR), Boolean.FALSE);
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_CURRICULUM_PROPERTY_PROCESSOR))
                .count() == 0) {
            FillCurriculumPropertyProcessor.create(
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_CURRICULUM_PROPERTY_PROCESSOR),
                    Boolean.FALSE);
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.AUTOMATIC_ONLINE_REQUEST_PROCESSOR))
                .count() == 0) {
            AutomaticOnlineRequestProcessor.create(
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, ULisboaConstants.AUTOMATIC_ONLINE_REQUEST_PROCESSOR),
                    Boolean.TRUE);
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.AUTOMATIC_REQUEST_PROCESSOR))
                .count() == 0) {
            AutomaticRequestProcessor.create(
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, ULisboaConstants.AUTOMATIC_REQUEST_PROCESSOR),
                    Boolean.TRUE);
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.VALIDATE_PROGRAM_CONCLUSION_PROCESSOR))
                .count() == 0) {
            ValidateProgramConclusionProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.VALIDATE_PROGRAM_CONCLUSION_PROCESSOR), Boolean.FALSE);
        }
        if (findByName(
                BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.VALIDATE_ENROLMENTS_EXISTENCE_BY_YEAR_PROCESSOR))
                        .count() == 0) {
            ValidateEnrolmentsExistenceByYearProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.VALIDATE_ENROLMENTS_EXISTENCE_BY_YEAR_PROCESSOR), Boolean.FALSE);
        }

        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_ACTIVED_ENROLMENTS_PROPERTY_PROCESSOR))
                .count() == 0) {
            FillActiveEnrolmentsPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_ACTIVED_ENROLMENTS_PROPERTY_PROCESSOR), Boolean.FALSE);
        }

        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_FLUNKED_ENROLMENTS_PROPERTY_PROCESSOR))
                .count() == 0) {
            FillNotApprovedEnrolmentsPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_FLUNKED_ENROLMENTS_PROPERTY_PROCESSOR), Boolean.FALSE);
        }

        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.VALIDATE_REQUIREMENTS_PROCESSOR))
                .count() == 0) {
            ValidateRequirementsInCreationProcessor validator =
                    (ValidateRequirementsInCreationProcessor) ValidateRequirementsInCreationProcessor.create(BundleUtil
                            .getLocalizedString(ULisboaConstants.BUNDLE, ULisboaConstants.VALIDATE_REQUIREMENTS_PROCESSOR),
                            Boolean.FALSE);
            validator.addServiceRequestTypeNeeded(Sets.newHashSet("UL_CERT_REGISTO_2VIA", "REGISTRY_DIPLOMA_REQUEST",
                    "PRE_FENIX_REGISTRY_DIPLOMA_REQUEST", "LEGACY_REGISTRY_DIPLOMA_REQUEST_REGISTRY_DIPLOMA_REQUEST",
                    "LEGACY_DOCUMENT_REGISTRY_CERTIFICATE_DUPLICATE"));
        }

    }

    public abstract void process(ULisboaServiceRequest request, boolean forceUpdate);

}
