package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.CandidacySummaryFile;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CGDPdfFiller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/model43print")
public class Model43PrintController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String model43print(Model model, RedirectAttributes redirectAttributes) {
        Person person = Authenticate.getUser().getPerson();
        CGDPdfFiller cgdPdfFiller = new CGDPdfFiller();
        ByteArrayOutputStream stream;
        try {
            stream = cgdPdfFiller.getFilledPdf(person);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        appendSummaryFile(stream.toByteArray(), InstructionsController.getPersonFirstTimeCandidacy(person));

        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint", model, redirectAttributes);
    }

    @RequestMapping(value = "/continue")
    public String model43printToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint", model, redirectAttributes);
    }

    @Atomic
    public static void appendSummaryFile(byte[] pdfByteArray, StudentCandidacy studentCandidacy) {
        CandidacySummaryFile existingSummary = studentCandidacy.getSummaryFile();
        byte[] existingContent = existingSummary.getContent();
        ByteArrayOutputStream concatDoc = concatenateDocs(existingContent, pdfByteArray);
        existingSummary.delete();

        studentCandidacy.setSummaryFile(new CandidacySummaryFile(studentCandidacy.getPerson().getStudent().getNumber() + ".pdf",
                concatDoc.toByteArray(), studentCandidacy));
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
