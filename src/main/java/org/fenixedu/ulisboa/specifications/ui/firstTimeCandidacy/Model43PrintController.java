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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.CandidacySummaryFile;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
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
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/model43print")
public class Model43PrintController extends FenixeduUlisboaSpecificationsBaseController {

    private @Autowired ServletContext context;

    private static final String CGD_PERSONAL_INFORMATION_PDF_PATH = "candidacy/firsttime/CGD43.pdf";

    @RequestMapping(produces = "application/pdf")
    public String model43print(Model model, RedirectAttributes redirectAttributes) {
        Person person = Authenticate.getUser().getPerson();
        InputStream pdfTemplateStream = context.getResourceAsStream(CGD_PERSONAL_INFORMATION_PDF_PATH);

        ByteArrayOutputStream stream;
        try {
            CGDPdfFiller cgdPdfFiller = new CGDPdfFiller();
            stream = cgdPdfFiller.getFilledPdf(person, pdfTemplateStream);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        byte[] pdfBytes = stream.toByteArray();

        appendSummaryFile(pdfBytes, FirstTimeCandidacyController.getStudentCandidacy());

        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint", model, redirectAttributes);
    }

    @RequestMapping(value = "/continue")
    public String model43printToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint", model, redirectAttributes);
    }

    @Atomic
    public static void appendSummaryFile(byte[] pdfByteArray, StudentCandidacy studentCandidacy) {
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
