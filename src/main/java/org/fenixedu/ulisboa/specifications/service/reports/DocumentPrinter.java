/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoes@qub-it.com
 *               jnpa@reitoria.ulisboa.pt
 *
 * 
 * This file is part of FenixEdu QubDocs.
 *
 * FenixEdu QubDocs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu QubDocs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu QubDocs.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fenixedu.ulisboa.specifications.service.reports;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.qubdocs.FenixEduDocumentGenerator;
import org.fenixedu.qubdocs.academic.documentRequests.providers.ApprovedCurriculumEntriesDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.ConcludedCurriculumEntriesDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.ConclusionInformationDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.CurriculumInformationDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.DegreeCurricularPlanInformationDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.DocumentSignerDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.EnrolmentsDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.LocalizedDatesProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.RegistrationDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.ServiceRequestDataProvider;
import org.fenixedu.qubdocs.base.providers.PersonReportDataProvider;
import org.fenixedu.qubdocs.base.providers.UserReportDataProvider;
import org.fenixedu.qubdocs.domain.DocumentPrinterConfiguration;
import org.fenixedu.qubdocs.domain.serviceRequests.AcademicServiceRequestTemplate;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.joda.time.DateTime;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.dom.element.text.TextTextInputElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.qubit.terra.docs.core.DocumentTemplateEngine;
import com.qubit.terra.docs.core.IDocumentTemplateService;

import fr.opensagres.xdocreport.core.io.internal.ByteArrayOutputStream;
import pt.ist.fenixframework.Atomic;

public class DocumentPrinter {

    public static final String PREFIX = "qub-";

    public static PrintedDocument print(ULisboaServiceRequest serviceRequest) {

        if (!serviceRequest.getServiceRequestType().isPrintable()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.service.reports.DocumentPrinter.ULisboaServiceRequest.is.not.printable");
        }

        final ExecutionYear executionYear = serviceRequest.getExecutionYear();
        final Registration registration = serviceRequest.getRegistration();
        final CycleType requestedCycle = serviceRequest.getCycleType();
        final ProgramConclusion programConclusion = serviceRequest.getProgramConclusion();
        final DegreeType degreeType = serviceRequest.getRegistration().getDegreeType();
        final Degree degree = serviceRequest.getRegistration().getDegree();

        AcademicServiceRequestTemplate academicServiceRequestTemplate = serviceRequest.getAcademicServiceRequestTemplate();
        if (academicServiceRequestTemplate == null) {
            academicServiceRequestTemplate = AcademicServiceRequestTemplate.findTemplateFor(serviceRequest.getLanguage(),
                    serviceRequest.getServiceRequestType(), degreeType, programConclusion, degree);
        }

        final byte[] template = processQubInclude(academicServiceRequestTemplate);

        final FenixEduDocumentGenerator generator = FenixEduDocumentGenerator.create(template, FenixEduDocumentGenerator.PDF);

        if (serviceRequest.getDocumentSigner() == null) {
            resetDocumentSigner(serviceRequest);
        }

        generator.registerDataProvider(new PersonReportDataProvider(serviceRequest.getPerson()));
        generator.registerDataProvider(new RegistrationDataProvider(registration));
        generator.registerDataProvider(new LocalizedDatesProvider());
        generator.registerDataProvider(new ServiceRequestDataProvider(serviceRequest, executionYear));
        generator.registerDataProvider(
                new DegreeCurricularPlanInformationDataProvider(registration, requestedCycle, executionYear));
        if (serviceRequest.hasEnrolmentsByYear() || serviceRequest.hasStandaloneEnrolmentsByYear()
                || serviceRequest.hasExtracurricularEnrolmentsByYear()) {
            generator.registerDataProvider(new EnrolmentsDataProvider(registration, serviceRequest.getEnrolmentsByYear(),
                    serviceRequest.getStandaloneEnrolmentsByYear(), serviceRequest.getExtracurricularEnrolmentsByYear(),
                    executionYear, serviceRequest.getLanguage()));
        }

        generator.registerDataProvider(new DocumentSignerDataProvider(serviceRequest));

        generator.registerDataProvider(new ConclusionInformationDataProvider(registration, programConclusion));

        generator.registerDataProvider(new ApprovedCurriculumEntriesDataProvider(registration, serviceRequest
                .getApprovedEnrolments(), serviceRequest.getLanguage()));

        generator.registerDataProvider(new ConcludedCurriculumEntriesDataProvider(registration, serviceRequest.getCurriculum(),
                serviceRequest.getLanguage()));

//        generator.registerDataProvider(new CurriculumEntriesDataProvider(registration, programConclusion,
//                new CurriculumEntryRemarksDataProvider(registration), serviceRequest.getLanguage()));

        generator.registerDataProvider(new UserReportDataProvider());

        generator.registerDataProvider(new CurriculumInformationDataProvider(registration, executionYear));

        /*
         * TODO: Falta saber o que fazer com providers especificos para determinados tipos de documentos.
         *       Em vez de adicioná-los de forma condicional era preferível adicionar sempre, e estes omitirem-se
         *       se não tiverem dados/condições para injectarem chaves.
         */

//        if (documentRequest instanceof ExtraCurricularCertificateRequest) {
//            final ExtraCurricularCertificateRequest extraCurricularCertificateRequest =
//                    (ExtraCurricularCertificateRequest) documentRequest;
//
//            generator.registerDataProvider(new ExtraCurricularCoursesDataProvider(extraCurricularCertificateRequest
//                    .getEnrolmentsSet(), documentRequest.getLanguage(), new CurriculumEntryRemarksDataProvider(registration)));
//        }

//        if (documentRequest instanceof StandaloneEnrolmentCertificateRequest) {
//            final StandaloneEnrolmentCertificateRequest standaloneEnrolmentCertificateRequest =
//                    (StandaloneEnrolmentCertificateRequest) documentRequest;
//
//            generator.registerDataProvider(new StandaloneCurriculumEntriesDataProvider(registration,
//                    standaloneEnrolmentCertificateRequest.getEnrolmentsSet(),
//                    new CurriculumEntryRemarksDataProvider(registration), documentRequest.getLanguage(), new LocalDate()));
//        }

//        if (documentRequest instanceof ApprovementMobilityCertificateRequest) {
//            final ApprovementMobilityCertificateRequest approvementRequest =
//                    (ApprovementMobilityCertificateRequest) documentRequest;
//
//            final ApprovementCertificateCurriculumEntries entriesDataProvider =
//                    new ApprovementCertificateCurriculumEntries(approvementRequest, registration, requestedCycle,
//                            new CurriculumEntryRemarksDataProvider(registration), approvementRequest.getLanguage());
//
//            generator.registerDataProvider(entriesDataProvider);
//        }

//        generator.registerDataProvider(new ApprovementCertificateCurriculumEntries(serviceRequest, registration, requestedCycle,
//                new CurriculumEntryRemarksDataProvider(registration), serviceRequest.getLanguage()));

//        if (serviceRequest instanceof DegreeFinalizationCertificateRequest) {
//            generator.registerDataProvider(new CurriculumEntriesDataProvider(registration, requestedCycle,
//                    new CurriculumEntryRemarksDataProvider(registration), serviceRequest.getLanguage()));
//        }

        final byte[] report = generator.generateReport();

        return new PrintedDocument(serviceRequest, report, "application/pdf", "pdf");
    }

    private static byte[] processQubInclude(AcademicServiceRequestTemplate academicServiceRequestTemplate) {
        try {
            Pattern pattern = Pattern.compile("qubInclude\\(\"(.+)\"\\)");
            OdfTextDocument document = OdfTextDocument.loadDocument(
                    new ByteArrayInputStream(academicServiceRequestTemplate.getDocumentTemplateFile().getContent()));
            OdfTable dataTable = document.getTableByName("BlocoDeDados");
            if (dataTable == null || dataTable.getRowCount() != 1 || dataTable.getColumnCount() != 1) {
                System.out.println("Error");
            }
            NodeList cellNodesList = dataTable.getCellByPosition(0, 0).getOdfElement().getChildNodes();
            findIncludes(cellNodesList, pattern, academicServiceRequestTemplate.getLanguage());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            document.save("/home/jnpa/Documents/tentativa.odt");
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new ULisboaSpecificationsDomainException(e, "error.DocumentPrinter.processing.includes", e.getMessage());
        }
    }

    private static void findIncludes(NodeList nodesList, Pattern pattern, Locale locale) {
        for (int i = 0; i < nodesList.getLength(); i++) {
            Node child = nodesList.item(i);
            if (child instanceof TextTextInputElement) {
                processIncludes(child, pattern, locale);
            } else {
                findIncludes(child.getChildNodes(), pattern, locale);
            }
        }
    }

    private static void processIncludes(Node node, Pattern pattern, Locale locale) {
        Matcher matcher = pattern.matcher(node.getTextContent());
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String[] includes = matcher.group(1).split(" ");
            for (String include : includes) {
                List<AcademicServiceRequestTemplate> lst = AcademicServiceRequestTemplate.findAll().filter(template -> template
                        .getName().getContent(Locale.forLanguageTag("pt-PT")).equalsIgnoreCase(PREFIX + include))
                        .collect(Collectors.toList());
                if (lst.size() != 1) {
                    throw new ULisboaSpecificationsDomainException("error.DocumentPrinter.finding.include", include,
                            "" + lst.size());
                };
                String content = new String(lst.get(0).getDocumentTemplateFile().getContent());
                sb.append(content);
            }
        }
        node.setTextContent(matcher.replaceAll("") + sb.toString());
    }

    @Atomic
    private static void resetDocumentSigner(ULisboaServiceRequest documentRequest) {
        documentRequest.setDocumentSigner(DocumentSigner.findDefaultDocumentSignature());
    }

    public static synchronized void registerService() {
        IDocumentTemplateService service = DocumentPrinterConfiguration.getInstance();
        DocumentTemplateEngine.registerServiceImplementations(service);
    }

    public static class PrintedDocument {

        private final ULisboaServiceRequest serviceRequest;
        private final byte[] data;
        private final String contentType;
        private final String fileExtension;
        private final String fileName;

        public PrintedDocument(ULisboaServiceRequest serviceRequest, byte[] data, String contentType, String fileExtension) {
            this.serviceRequest = serviceRequest;
            this.data = data;
            this.contentType = contentType;
            this.fileExtension = fileExtension;
            this.fileName = generateFileName();
        }

        public byte[] getData() {
            return data;
        }

        public String getContentType() {
            return contentType;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public String getFileName() {
            return fileName;
        }

        private String generateFileName() {
            final StringBuilder result = new StringBuilder();
            result.append(serviceRequest.getPerson().getUsername());
            result.append("-");
            result.append(new DateTime().toString("YYYYMMMDD", serviceRequest.getLanguage()));
            result.append("-");
            result.append(
                    serviceRequest.getServiceRequestType().getName().getContent(serviceRequest.getLanguage()).replace(":", ""));
            result.append("-");
            result.append(serviceRequest.getLanguage().toString());

            return StringNormalizer.normalizePreservingCapitalizedLetters(result.toString()).replaceAll("\\s", "_") + "."
                    + fileExtension;
        }

    }

}
