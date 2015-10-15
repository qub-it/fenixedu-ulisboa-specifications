package org.fenixedu.ulisboa.specifications.domain.serviceRequests.validators;

import java.util.Collection;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.fenixedu.treasury.util.LocalizedStringUtil;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.Constants;

import pt.ist.fenixframework.Atomic;

public abstract class ULisboaServiceRequestValidator extends ULisboaServiceRequestValidator_Base {

    protected ULisboaServiceRequestValidator() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ULisboaServiceRequestValidator(final LocalizedString name) {
        this();
        init(name);
        checkRules();
    }

    protected void init(final LocalizedString name) {
        setName(name);
    }

    private void checkRules() {
        if (LocalizedStringUtil.isTrimmedEmpty(getName())) {
            throw new DomainException("error.ULisboaServiceRequestValidator.name.required");
        }
        getName().getLocales().stream().forEach(l -> {
            if (findByName(getName().getContent(l)).count() > 1) {
                throw new TreasuryDomainException("error.ULisboaServiceRequestValidator.name.duplicated", l.toString());
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
            blockers.add(BundleUtil.getString(Constants.BUNDLE,
                    "error.ULisboaServiceRequestValidator.connected.ServiceRequestTypes"));
        }
    }

    @Atomic
    public void delete() {
        TreasuryDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setBennu(null);
        deleteDomainObject();
    }

    public static Stream<ULisboaServiceRequestValidator> findAll() {
        return Bennu.getInstance().getULisboaServiceRequestValidatorsSet().stream();
    }

    public static Stream<ULisboaServiceRequestValidator> findByName(final String name) {
        return findAll().filter(request -> LocalizedStringUtil.isEqualToAnyLocaleIgnoreCase(request.getName(), name));
    }

    public static void initValidators() {
        if (findAll().count() != 0) {
            return;
        }

        MockULisboaServiceRequestValidator.create(BundleUtil.getLocalizedString(Constants.BUNDLE,
                "label.MockULisboaServiceRequestValidator.name"));
    }

    public abstract boolean validate(ULisboaServiceRequest request);

}
