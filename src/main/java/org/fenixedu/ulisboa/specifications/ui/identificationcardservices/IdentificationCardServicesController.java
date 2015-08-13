package org.fenixedu.ulisboa.specifications.ui.identificationcardservices;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CGDPdfFiller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.identificationCardServices",
        accessGroup = "logged")
@RequestMapping(IdentificationCardServicesController.CONTROLLER_URL)
public class IdentificationCardServicesController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/identificationcardservices";
    private @Autowired ServletContext context;
    private static final String CGD_PERSONAL_INFORMATION_PDF_PATH = "candidacy/firsttime/CGD43.pdf";

    @RequestMapping
    public String home(Model model) {
        return "fenixedu-ulisboa-specifications/identificationcardservices/idservices";
    }

    @RequestMapping(value = "/downloadCGDMod43", produces = "application/pdf")
    public ResponseEntity<byte[]> downloadCGDMod43(Model model, RedirectAttributes redirectAttributes) {
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
        String filename = person.getUsername() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=" + filename);
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfBytes, headers, HttpStatus.OK);
        return response;
    }

}
