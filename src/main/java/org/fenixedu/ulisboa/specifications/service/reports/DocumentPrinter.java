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

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.qubdocs.FenixEduDocumentGenerator;
import org.fenixedu.qubdocs.academic.documentRequests.providers.ConclusionInformationDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.CurriculumEntriesDataProvider;
import org.fenixedu.qubdocs.academic.documentRequests.providers.CurriculumEntryRemarksDataProvider;
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
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import pt.ist.fenixframework.Atomic;

import com.qubit.terra.docs.core.DocumentTemplateEngine;
import com.qubit.terra.docs.core.IDocumentTemplateService;

public class DocumentPrinter {

    public PrintedDocument print(ULisboaServiceRequest serviceRequest) {

        if (!serviceRequest.getServiceRequestType().isPrintable()) {
            throw new DomainException("error.service.reports.DocumentPrinter.ULisboaServiceRequest.is.not.printable");
        }

        final ExecutionYear executionYear = serviceRequest.getExecutionYear();
        final Registration registration = serviceRequest.getRegistration();
        final CycleType requestedCycle = serviceRequest.getCycleType();
        /*
         * TODO: Rever propriedades em falta: ProgramConclusion
         */
        //final ProgramConclusion programConclusion = serviceRequest.hasProperty(Constants.PROGRAM_CONCLUSION) ? serviceRequest.findProperty(Constants.PROGRAM_CONCLUSION).getProgramConclusion() : null;
        final ProgramConclusion programConclusion = null;

        final DegreeType degreeType = serviceRequest.getRegistration().getDegreeType();
        final Degree degree = serviceRequest.getRegistration().getDegree();

        AcademicServiceRequestTemplate academicServiceRequestTemplate = serviceRequest.getAcademicServiceRequestTemplate();
        if (academicServiceRequestTemplate == null) {
            academicServiceRequestTemplate =
                    AcademicServiceRequestTemplate.findTemplateFor(serviceRequest.getLanguage(),
                            serviceRequest.getServiceRequestType(), degreeType, programConclusion, degree);
        }

        final FenixEduDocumentGenerator generator =
                FenixEduDocumentGenerator.create(academicServiceRequestTemplate, FenixEduDocumentGenerator.PDF);

        if (serviceRequest.getDocumentSigner() == null) {
            resetDocumentSigner(serviceRequest);
        }

        generator.registerDataProvider(new PersonReportDataProvider(serviceRequest.getPerson()));
        generator.registerDataProvider(new RegistrationDataProvider(registration));
        generator.registerDataProvider(new LocalizedDatesProvider());
        generator.registerDataProvider(new ServiceRequestDataProvider(serviceRequest, executionYear));
        generator.registerDataProvider(new DegreeCurricularPlanInformationDataProvider(registration, requestedCycle,
                executionYear));
        generator.registerDataProvider(new EnrolmentsDataProvider(registration, executionYear, serviceRequest.getLanguage()));

        generator.registerDataProvider(new DocumentSignerDataProvider(serviceRequest));

        generator.registerDataProvider(new ConclusionInformationDataProvider(registration, programConclusion));

        generator.registerDataProvider(new CurriculumEntriesDataProvider(registration, requestedCycle,
                new CurriculumEntryRemarksDataProvider(registration), serviceRequest.getLanguage()));

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

        return new PrintedDocument(report, "application/PDF", "pdf");
    }

    @Atomic
    private void resetDocumentSigner(ULisboaServiceRequest documentRequest) {
        documentRequest.setDocumentSigner(DocumentSigner.findDefaultDocumentSignature());
    }

    public static synchronized void registerService() {
        IDocumentTemplateService service = DocumentPrinterConfiguration.getInstance();
        DocumentTemplateEngine.registerServiceImplementations(service);
    }

    public static class PrintedDocument {

        private final byte[] data;
        private final String contentType;
        private final String fileExtension;

        public PrintedDocument(byte[] data, String contentType, String fileExtension) {
            this.data = data;
            this.contentType = contentType;
            this.fileExtension = fileExtension;
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

    }

}
