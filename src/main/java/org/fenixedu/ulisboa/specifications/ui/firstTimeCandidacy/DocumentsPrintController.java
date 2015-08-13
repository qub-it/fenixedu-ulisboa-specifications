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

import java.util.Locale;

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
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint")
public class DocumentsPrintController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String documentsprint(Model model, RedirectAttributes redirectAttributes) {
        StudentCandidacy candidacy = FirstTimeCandidacyController.getStudentCandidacy();
        Registration registration = candidacy.getRegistration();

        DocumentRequestCreator documentRequestCreator = new DocumentRequestCreator(registration);
        documentRequestCreator.setChosenServiceRequestType(ServiceRequestType.findUnique(AcademicServiceRequestType.DOCUMENT,
                DocumentRequestType.SCHOOL_REGISTRATION_DECLARATION));
        documentRequestCreator.setRequestedCycle(CycleType.FIRST_CYCLE);
        documentRequestCreator.setLanguage(Locale.getDefault());
        documentRequestCreator.setProgramConclusion(ProgramConclusion.conclusionsFor(registration).findAny().get());
        DocumentRequest document = (DocumentRequest) documentRequestCreator.execute();
        resetDocumentSigner(document);
        processConcludeAndDeliver(document);
        byte[] bytes = document.generateDocument();
        Model43PrintController.appendSummaryFile(bytes, candidacy);
        Model43PrintController.appendSummaryFile(
                DocumentPrinter.printRegistrationTuititionPaymentPlan(registration, DocumentPrinter.PDF), candidacy);
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
        documentRequest.setNumberOfPages(1);
        documentRequest.process();
        documentRequest.concludeServiceRequest();
        documentRequest.delivered();
    }
}
