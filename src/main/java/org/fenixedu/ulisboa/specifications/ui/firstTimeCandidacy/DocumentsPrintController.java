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

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.AdmittedCandidacySituation;
import org.fenixedu.academic.domain.candidacy.CandidacySummaryFile;
import org.fenixedu.academic.domain.candidacy.RegisteredCandidacySituation;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.service.factoryExecutors.DocumentRequestCreator;
import org.fenixedu.academictreasury.services.reports.DocumentPrinter;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CGDPdfFiller;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(DocumentsPrintController.CONTROLLER_URL)
public class DocumentsPrintController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint";

    public static final String WITH_MODEL43_URL = CONTROLLER_URL + "/withModel43";

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(Model model, RedirectAttributes redirectAttributes) {
        return redirect(CgdDataAuthorizationController.CONTROLLER_URL, model, redirectAttributes);
    }

    @RequestMapping
    public String documentsprint(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        printToCandidacySummaryFile(model, false);

        Registration registration = FirstTimeCandidacyController.getCandidacy().getRegistration();
        Student student = registration.getStudent();
        return finished(student, model);
    }

    @RequestMapping(value = "/withModel43")
    public String documentsprintWithModel43(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        printToCandidacySummaryFile(model, true);

        Registration registration = FirstTimeCandidacyController.getCandidacy().getRegistration();
        Student student = registration.getStudent();
        if (!student.getPerson().getPersonUlisboaSpecifications().getAuthorizeSharingDataWithCGD()) {
            addWarningMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.finished.noUniversityCard"), model);
        } else {
            addWarningMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.finished.deliverDocumentByHand"), model);
        }
        return finished(student, model);
    }

    public String finished(Student student, Model model) {
        StudentAccessServices.triggerSyncStudentToExternal(student);

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.documentsPrint.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/finished";
    }

    private void printToCandidacySummaryFile(Model model, boolean includeModel43) {
        StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        Registration registration = candidacy.getRegistration();
        resetCandidacySummaryFile(candidacy);

        try {
            printRegistrationDeclaration(candidacy, registration);
        } catch (Exception ex) {
            ex.printStackTrace();
            addWarningMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.registrationDeclaration.print.failed"),
                    model);
        }

        if (includeModel43) {
            try {
                printModel43(candidacy);
            } catch (Exception ex) {
                ex.printStackTrace();
                addWarningMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.model43.print.failed"), model);
            }
        }

        try {
            printTuitionPaymentPlan(candidacy, registration);
        } catch (Exception ex) {
            ex.printStackTrace();
            addWarningMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.tuitionPayment.print.failed"), model);
        }
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

    @Atomic(mode = TxMode.WRITE)
    private void printRegistrationDeclaration(StudentCandidacy candidacy, Registration registration) {
        DocumentRequestCreator documentRequestCreator = new DocumentRequestCreator(registration);
        documentRequestCreator.setChosenServiceRequestType(ServiceRequestType.findUnique(AcademicServiceRequestType.DOCUMENT,
                DocumentRequestType.SCHOOL_REGISTRATION_DECLARATION));
        documentRequestCreator.setRequestedCycle(CycleType.FIRST_CYCLE);
        documentRequestCreator.setLanguage(Locale.getDefault());
        documentRequestCreator.setProgramConclusion(ProgramConclusion.conclusionsFor(registration).findAny().get());
        DocumentRequest document = (DocumentRequest) documentRequestCreator.execute();
        resetDocumentSigner(document);
        processConcludeAndDeliver(document);

        appendSummaryFile(document.generateDocument(), candidacy);
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

    private void printModel43(StudentCandidacy candidacy) {
        Person person = Authenticate.getUser().getPerson();

        InputStream pdfTemplateStream;
        if (FirstYearRegistrationGlobalConfiguration.getInstance().hasMod43Template()) {
            pdfTemplateStream = new ByteArrayInputStream(
                    FirstYearRegistrationGlobalConfiguration.getInstance().getMod43Template().getContent());
        } else {
            pdfTemplateStream = context.getResourceAsStream(CGD_PERSONAL_INFORMATION_PDF_PATH);
        }

        ByteArrayOutputStream stream;
        try {
            CGDPdfFiller cgdPdfFiller = new CGDPdfFiller();
            stream = cgdPdfFiller.getFilledPdf(person, pdfTemplateStream);
        } catch (IOException | DocumentException e) {
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        appendSummaryFile(stream.toByteArray(), candidacy);
    }

    private void printTuitionPaymentPlan(StudentCandidacy candidacy, Registration registration) {
        byte[] tuitionPlanbytes = DocumentPrinter.printRegistrationTuititionPaymentPlan(registration, DocumentPrinter.PDF);
        appendSummaryFile(tuitionPlanbytes, candidacy);
    }

    @Atomic(mode = TxMode.WRITE)
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

    @RequestMapping(value = "/printalldocuments", produces = "application/pdf")
    public ResponseEntity<byte[]> finishedToPrintAllDocuments(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            // Cannot return redirect() with a return type of ResponseEntity<byte[]>
            throw new RuntimeException("Cannot finish candidacy - period is not open");
        }
        Person person = AccessControl.getPerson();
        StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        concludeStudentCandidacy(person, candidacy);

        byte[] pdfBytes = new byte[0];
        if (candidacy.getSummaryFile() != null) {
            pdfBytes = candidacy.getSummaryFile().getContent();
        }
        String filename = person.getStudent().getNumber() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline;filename=" + filename);
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfBytes, headers, HttpStatus.OK);

        Authenticate.logout(request.getSession());

        return response;
    }

    @Atomic
    private void concludeStudentCandidacy(Person person, StudentCandidacy candidacy) {
        AdmittedCandidacySituation situation = new AdmittedCandidacySituation(candidacy, person);
        situation.setSituationDate(situation.getSituationDate().minusMinutes(1));

        new RegisteredCandidacySituation(candidacy, person);
    }
}
