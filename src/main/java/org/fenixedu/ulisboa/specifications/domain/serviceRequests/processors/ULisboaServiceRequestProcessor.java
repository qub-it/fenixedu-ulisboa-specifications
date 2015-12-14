package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.Collection;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.util.LocalizedStringUtil;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public abstract class ULisboaServiceRequestProcessor extends ULisboaServiceRequestProcessor_Base {

    protected ULisboaServiceRequestProcessor() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ULisboaServiceRequestProcessor(final LocalizedString name) {
        this();
        init(name);
        checkRules();
    }

    protected void init(final LocalizedString name) {
        setName(name);
    }

    private void checkRules() {
        if (LocalizedStringUtil.isTrimmedEmpty(getName())) {
            throw new ULisboaSpecificationsDomainException("error.ULisboaServiceRequestValidator.name.required");
        }
        getName()
                .getLocales()
                .stream()
                .forEach(
                        l -> {
                            if (findByName(getName().getContent(l)).count() > 1) {
                                throw new ULisboaSpecificationsDomainException(
                                        "error.ULisboaServiceRequestValidator.name.duplicated", l.toString());
                            };
                        });
    }

    @Atomic
    public void edit(final LocalizedString name) {
        setName(name);
        checkRules();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
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
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.STATE_LOGGER_PROCESSOR)).count() == 0) {
            StateLoggerProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.STATE_LOGGER_PROCESSOR));
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR))
                .count() == 0) {
            FillEnrolmentsByYearPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR));
        }
        if (findByName(
                BundleUtil.getString(ULisboaConstants.BUNDLE,
                        ULisboaConstants.FILL_STANDALONE_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR)).count() == 0) {
            FillStandaloneEnrolmentsByYearPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_STANDALONE_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR));
        }
        if (findByName(
                BundleUtil.getString(ULisboaConstants.BUNDLE,
                        ULisboaConstants.FILL_EXTRACURRICULAR_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR)).count() == 0) {
            FillExtracurricularEnrolmentsByYearPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_EXTRACURRICULAR_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR));
        }
        if (findByName(
                BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_STANDALONE_CURRICULUM_PROPERTY_PROCESSOR))
                .count() == 0) {
            FillStandAlonePropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_STANDALONE_CURRICULUM_PROPERTY_PROCESSOR));
        }
        if (findByName(
                BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.FILL_APPROVED_ENROLMENTS_PROPERTY_PROCESSOR))
                .count() == 0) {
            FillApprovedEnrolmentsPropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_APPROVED_ENROLMENTS_PROPERTY_PROCESSOR));
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.AUTOMATIC_ONLINE_REQUEST_PROCESSOR))
                .count() == 0) {
            AutomaticOnlineRequestProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.AUTOMATIC_ONLINE_REQUEST_PROCESSOR));
        }
        if (findByName(BundleUtil.getString(ULisboaConstants.BUNDLE, ULisboaConstants.PROGRAM_CONCLUSION_PROCESSOR)).count() == 0) {
            ProgramConclusionProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.PROGRAM_CONCLUSION_PROCESSOR));
        }
    }

    public abstract void process(ULisboaServiceRequest request);

}
