package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Collection;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.qubdocs.FenixEduDocumentGenerator;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class ServiceRequestOutputType extends ServiceRequestOutputType_Base {

    protected ServiceRequestOutputType() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestOutputType(final String code, final LocalizedString label, final String extension) {
        this();
        setCode(code);
        setLabel(label);
        setExtension(extension);

        checkRules();
    }

    private void checkRules() {
        if (getCode() == null || getCode().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestOutputType.code.required");
        }

        if (readAll().filter(ot -> ot.getCode().equals(getCode())).count() != 1) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestOutputType.code.duplicated");
        }

        if (getLabel() == null || getLabel().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestOutputType.label.required");
        }

        if (getExtension() == null || getExtension().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestOutputType.extension.required");
        }

    }

    @Atomic
    public void edit(final String code, final LocalizedString label, final String extension) {
        setCode(code);
        setLabel(label);
        setExtension(extension);

        checkRules();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);

        if (!getServiceRequestTypesSet().isEmpty()) {
            blockers.add(BundleUtil.getString(ULisboaConstants.BUNDLE, "error.ServiceRequestOutputType.have.serviceRequestType"));
        }
    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setBennu(null);

        deleteDomainObject();
    }

    public static Stream<ServiceRequestOutputType> readAll() {
        return Bennu.getInstance().getServiceRequestOutputTypesSet().stream();
    }

    public static ServiceRequestOutputType readByCode(String code) {
        return readAll().filter(ot -> ot.getCode().equals(code)).findFirst().orElse(null);
    }

    public static ServiceRequestOutputType create(final String code, final LocalizedString label, final String extension) {
        return new ServiceRequestOutputType(code, label, extension);
    }

    @Atomic
    public static void initOutputTypes() {
        if (readByCode(FenixEduDocumentGenerator.PDF) == null) {
            create(FenixEduDocumentGenerator.PDF,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, FenixEduDocumentGenerator.PDF), "pdf");
        }
        if (readByCode(FenixEduDocumentGenerator.ODT) == null) {
            create(FenixEduDocumentGenerator.ODT,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, FenixEduDocumentGenerator.ODT), "odt");
        }
        if (readByCode(FenixEduDocumentGenerator.DOCX) == null) {
            create(FenixEduDocumentGenerator.DOCX,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, FenixEduDocumentGenerator.DOCX), "docx");
        }
    }

}
