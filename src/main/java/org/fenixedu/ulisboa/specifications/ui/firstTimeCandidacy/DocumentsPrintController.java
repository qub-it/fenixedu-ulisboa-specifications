/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: joao.roxo@qub-it.com 
 *               nuno.pinheiro@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
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
package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.CandidacySummaryFile;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.service.factoryExecutors.DocumentRequestCreator;
import org.fenixedu.academictreasury.services.reports.DocumentPrinter;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CGDPdfFiller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint")
public class DocumentsPrintController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String documentsprint(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        printToCandidacySummaryFile(false);
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/finished", model, redirectAttributes);
    }

    @RequestMapping(value = "/withModel43")
    public String documentsprintWithModel43(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        printToCandidacySummaryFile(true);
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/finished", model, redirectAttributes);
    }

    private void printToCandidacySummaryFile(boolean includeModel43) {
        StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        Registration registration = candidacy.getRegistration();
        resetCandidacySummaryFile(candidacy);

        byte[] registrationDeclarationbytes = printRegistrationDeclaration(candidacy, registration);
        appendSummaryFile(registrationDeclarationbytes, candidacy);

        if (includeModel43) {
            byte[] model43bytes = printModel43();
            appendSummaryFile(model43bytes, candidacy);
        }

        byte[] tuitionPlanbytes = DocumentPrinter.printRegistrationTuititionPaymentPlan(registration, DocumentPrinter.PDF);
        appendSummaryFile(tuitionPlanbytes, candidacy);
    }

    @Atomic
    private static void resetCandidacySummaryFile(StudentCandidacy studentCandidacy) {
        CandidacySummaryFile summaryFile = studentCandidacy.getSummaryFile();
        if (summaryFile != null) {
            summaryFile.setStudentCandidacy(null);
            summaryFile.delete();
        }
    }

    private @Autowired ServletContext context;

    private static final String CGD_PERSONAL_INFORMATION_PDF_PATH = "candidacy/firsttime/CGD43.pdf";

    @Atomic
    private byte[] printRegistrationDeclaration(StudentCandidacy candidacy, Registration registration) {
        DocumentRequestCreator documentRequestCreator = new DocumentRequestCreator(registration);
        documentRequestCreator.setChosenServiceRequestType(ServiceRequestType.findUnique(AcademicServiceRequestType.DOCUMENT,
                DocumentRequestType.SCHOOL_REGISTRATION_DECLARATION));
        documentRequestCreator.setRequestedCycle(CycleType.FIRST_CYCLE);
        documentRequestCreator.setLanguage(Locale.getDefault());
        documentRequestCreator.setProgramConclusion(ProgramConclusion.conclusionsFor(registration).findAny().get());
        DocumentRequest document = (DocumentRequest) documentRequestCreator.execute();
        resetDocumentSigner(document);
        processConcludeAndDeliver(document);

        return document.generateDocument();
    }

    private void resetDocumentSigner(DocumentRequest documentRequest) {
        documentRequest.setDocumentSigner(DocumentSigner.findDefaultDocumentSignature());
    }

    private void processConcludeAndDeliver(DocumentRequest documentRequest) {
        documentRequest.setNumberOfPages(1);
        documentRequest.process();
        documentRequest.concludeServiceRequest();
        documentRequest.delivered();
    }

    private byte[] printModel43() {
        Person person = Authenticate.getUser().getPerson();

        InputStream pdfTemplateStream;
        if (FirstYearRegistrationGlobalConfiguration.getInstance().hasMod43Template()) {
            pdfTemplateStream =
                    new ByteArrayInputStream(FirstYearRegistrationGlobalConfiguration.getInstance().getMod43Template()
                            .getContent());
        } else {
            pdfTemplateStream = context.getResourceAsStream(CGD_PERSONAL_INFORMATION_PDF_PATH);
        }

        ByteArrayOutputStream stream;
        try {
            CGDPdfFiller cgdPdfFiller = new CGDPdfFiller();
            stream = cgdPdfFiller.getFilledPdf(person, pdfTemplateStream);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return stream.toByteArray();
    }

    @Atomic
    private static void appendSummaryFile(byte[] pdfByteArray, StudentCandidacy studentCandidacy) {
        CandidacySummaryFile existingSummary = studentCandidacy.getSummaryFile();
        if (existingSummary != null) {
            byte[] existingContent = existingSummary.getContent();
            pdfByteArray = concatenateDocs(existingContent, pdfByteArray).toByteArray();
            existingSummary.setStudentCandidacy(null);
            existingSummary.delete();
        }

        studentCandidacy.setSummaryFile(new CandidacySummaryFile(studentCandidacy.getPerson().getStudent().getNumber() + ".pdf",
                pdfByteArray, studentCandidacy));
    }

    private static ByteArrayOutputStream concatenateDocs(byte[] existingDoc, byte[] newDoc) {
        ByteArrayOutputStream concatenatedPdf = new ByteArrayOutputStream();
        try {
            PdfCopyFields copy = new PdfCopyFields(concatenatedPdf);
            copy.addDocument(new PdfReader(existingDoc));
            copy.addDocument(new PdfReader(newDoc));
            copy.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
        return concatenatedPdf;
    }
}
