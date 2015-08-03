package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.util.Locale;

import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.service.factoryExecutors.DocumentRequestCreator;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint")
public class DocumentsPrintController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String documentsprint(Model model, RedirectAttributes redirectAttributes) {
        StudentCandidacy candidacy = InstructionsController.getStudentCandidacy();
        Registration registration = candidacy.getRegistration();
        DocumentRequestCreator documentRequestCreator = new DocumentRequestCreator(registration);
        documentRequestCreator.setChosenServiceRequestType(ServiceRequestType.findUnique(AcademicServiceRequestType.DOCUMENT,
                DocumentRequestType.SCHOOL_REGISTRATION_DECLARATION));
        documentRequestCreator.setLanguage(Locale.getDefault());
        documentRequestCreator.setProgramConclusion(ProgramConclusion.conclusionsFor(registration).findAny().get());
        DocumentRequest document = (DocumentRequest) documentRequestCreator.execute();
        resetDocumentSigner(document);
        processConcludeAndDeliver(document);
        byte[] bytes = document.generateDocument();
        Model43PrintController.appendSummaryFile(bytes, candidacy);

        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/finished", model, redirectAttributes);
    }

    @RequestMapping(value = "/continue")
    public String documentsprintToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/finished", model, redirectAttributes);
    }

    @Atomic
    private void resetDocumentSigner(DocumentRequest documentRequest) {
        documentRequest.setDocumentSigner(DocumentSigner.findDefaultDocumentSignature());
    }

    @Atomic
    private void processConcludeAndDeliver(DocumentRequest documentRequest) {
        documentRequest.process();
        documentRequest.concludeServiceRequest();
        documentRequest.delivered();
    }
}
