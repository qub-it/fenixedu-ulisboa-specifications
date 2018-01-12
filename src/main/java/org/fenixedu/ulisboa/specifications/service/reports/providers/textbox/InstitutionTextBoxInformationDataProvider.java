package org.fenixedu.ulisboa.specifications.service.reports.providers.textbox;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.qubdocs.domain.InstitutionReportConfiguration;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;

public class InstitutionTextBoxInformationDataProvider implements IReportDataProvider {

    protected static final String KEY = "instTextBoxInfo";

    private final boolean showInstitutionBox;
    private final DocumentSigner signer;
    private final InstitutionReportConfiguration institutionConfiguration;
    private final Locale language;

    private InstitutionTextBoxInformation information;

    public InstitutionTextBoxInformationDataProvider(final boolean showInstitutionBox, final DocumentSigner signer,
            final Locale language, final InstitutionReportConfiguration institutionConfiguration) {
        this.showInstitutionBox = showInstitutionBox;
        this.signer = signer;
        this.institutionConfiguration = institutionConfiguration;
        this.language = language;
    }

    @Override
    public void registerFieldsAndImages(final IDocumentFieldsData documentFieldsData) {
    }

    @Override
    public boolean handleKey(final String key) {
        return KEY.equals(key);
    }

    @Override
    public Object valueForKey(final String key) {
        if (KEY.equals(key)) {
            return getInformation();
        }
        return null;
    }

    public class InstitutionTextBoxInformation {

        protected String responsibleUnit;
        protected String institutionName;
        protected String institutionAddress;
        protected String institutionSite;

        public InstitutionTextBoxInformation() {
            if (signer != null) {
                responsibleUnit = signer.getResponsibleUnit().getContent(language);
            }
            if (institutionConfiguration != null) {
                institutionName = institutionConfiguration.getName().getContent(language);
                institutionAddress = institutionConfiguration.getAddress();
                institutionSite = institutionConfiguration.getSite();
            }
        }

        public String getResponsibleUnit() {
            if (!showInstitutionBox || StringUtils.isBlank(responsibleUnit)) {
                return "";
            }
            return responsibleUnit;
        }

        public String getInstitutionName() {
            if (!showInstitutionBox || StringUtils.isBlank(institutionName)) {
                return "";
            }
            return institutionName;
        }

        public String getInstitutionAddress() {
            if (!showInstitutionBox || StringUtils.isBlank(institutionAddress)) {
                return "";
            }
            return institutionAddress;
        }

        public String getInstitutionSite() {
            if (!showInstitutionBox || StringUtils.isBlank(institutionSite)) {
                return "";
            }
            return institutionSite;
        }
    }

    private InstitutionTextBoxInformation getInformation() {
        if (information == null) {
            information = new InstitutionTextBoxInformation();
        }
        return information;
    }

}
