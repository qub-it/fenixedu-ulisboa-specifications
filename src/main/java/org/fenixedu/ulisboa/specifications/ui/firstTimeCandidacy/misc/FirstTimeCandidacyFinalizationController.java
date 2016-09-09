package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.misc;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.AdmittedCandidacySituation;
import org.fenixedu.academic.domain.candidacy.CandidacySummaryFile;
import org.fenixedu.academic.domain.candidacy.RegisteredCandidacySituation;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academictreasury.services.reports.DocumentPrinter;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequestGeneratedDocument;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CGDPdfFiller;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(FirstTimeCandidacyFinalizationController.CONTROLLER_URL)
public class FirstTimeCandidacyFinalizationController extends FirstTimeCandidacyAbstractController {
    private static final Locale PT = new Locale("pt", "PT");
    private static final String LANGUAGE_SLOT = "language";
    private static final String ENROLMENT_PROOF_SERVICE_TYPE = "ENROLMENT_PROOF";
    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/finalization";

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return redirect(CgdDataAuthorizationController.CONTROLLER_URL, model, redirectAttributes);
    }

    protected static final String _WITHOUT_MODEL_URI = "/withoutModel";
    public static final String WITHOUT_MODEL_URL = CONTROLLER_URL + _WITHOUT_MODEL_URI;

    @RequestMapping(value = _WITHOUT_MODEL_URI)
    public String documentsprint(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        printToCandidacySummaryFile(model, false);

        Registration registration = FirstTimeCandidacyController.getCandidacy().getRegistration();
        Student student = registration.getStudent();
        return finished(student, model);
    }

    protected static final String _WITH_MODEL_URI = "/withModel";
    public static final String WITH_MODEL_URL = CONTROLLER_URL + _WITH_MODEL_URI;

    @RequestMapping(value = _WITH_MODEL_URI)
    public String documentsprintWithModel43(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        printToCandidacySummaryFile(model, true);

        Registration registration = FirstTimeCandidacyController.getCandidacy().getRegistration();
        Student student = registration.getStudent();
        PersonUlisboaSpecifications personSpecifications = student.getPerson().getPersonUlisboaSpecifications();

        if (personSpecifications != null && !personSpecifications.getAuthorizeSharingDataWithCGD()) {
            addWarningMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.finished.noUniversityCard"), model);
        } else {
            addWarningMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.finished.deliverDocumentByHand"), model);
        }
        return finished(student, model);
    }

    public String finished(Student student, Model model) {
        StudentAccessServices.triggerSyncStudentToExternal(student);

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.documentsPrint.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/finished";
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
        final ServiceRequestType serviceRequestType =
                ServiceRequestType.findByCode(ENROLMENT_PROOF_SERVICE_TYPE).findFirst().orElse(null);
        final ServiceRequestSlot languageSlot = ServiceRequestSlot.getByCode(LANGUAGE_SLOT);
        final ServiceRequestSlotEntry languageSlotEntry = serviceRequestType.getServiceRequestSlotEntriesSet().stream()
                .filter(s -> s.getServiceRequestSlot() == languageSlot).findFirst().orElse(null);

        ULisboaServiceRequestBean bean = new ULisboaServiceRequestBean();
        final ServiceRequestPropertyBean propertyBean = new ServiceRequestPropertyBean(languageSlotEntry);

        propertyBean.setLocaleValue(PT);

        bean.setServiceRequestType(serviceRequestType);
        bean.setRegistration(registration);
        bean.setRequestedOnline(true);
        bean.setServiceRequestPropertyBeans(Lists.newArrayList(propertyBean));
        final ULisboaServiceRequest request = ULisboaServiceRequest.create(bean);

        ULisboaServiceRequestGeneratedDocument printedDocument = request.downloadDocument();
        appendSummaryFile(printedDocument.getContent(), candidacy);
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

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        throw new RuntimeException("Error you should not call this method.");
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

}
