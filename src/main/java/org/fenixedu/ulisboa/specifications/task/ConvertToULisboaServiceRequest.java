package org.fenixedu.ulisboa.specifications.task;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.documents.DocumentRequestGeneratedDocument;
import org.fenixedu.academic.domain.documents.GeneratedDocument;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituation;
import org.fenixedu.academic.domain.serviceRequests.RegistrationAcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.CertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.CertificateRequest_Base;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DeclarationRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DeclarationRequest_Base;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DegreeFinalizationCertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DiplomaRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DiplomaSupplementRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.EnrolmentCertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.EnrolmentDeclarationRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.RegistryDiplomaRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.SchoolRegistrationCertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.SchoolRegistrationDeclarationRequest;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequestGeneratedDocument;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

public class ConvertToULisboaServiceRequest extends CustomTask {

    /*
     * TODO:
     *       = DTO Migrador =
     *       + Criar Engine do CSV
     *       + Criar DTO (fields)
     *       + Criar DTO (execute)
     *       
     *       = Levantar Pedidos criados = 
     *       + Listar <Classname,ServiceRequestType> por instalação para identificar todas as configuraçoes existentes de ServiceRequest
     *       + Para cada configuração perceber quais as propriedades válidas (que precisam de se copiadas).
     *       
     *       = Relações&Slots =
     *       + Manter identificador, versioningCreator, creationDate
     *       + Associar: AdministrativeOffice, Bennu, AcademicServiceRequestYear, ServiceRequestType, Lista de estados(AcademicServiceRequestSituations), Lista de GeneratedDocuments
     *       + Implementar cloner de propriedades no ASR
     *       
     *       = Integração AcademicTreasury=
     *       + Criar os pedidos académicos sem gerar dívida (signaless)
     *       + Criar os pedidos academicos sem gerar novo identificador (num/ano)
     *       + No AcademicTreasuryEvent, fazer setAcademicServiceRequest(clone)
     *       
     *       = Properties=
     *       + Determinar melhor forma de criar as props
     *       + No caso das propriedades: Discriminado, #Unidades, #Paginas, Urgente, Locale, CycleType; criar propriedade se este valor nao for null.
     *       
     *       = Remocao =
     *       + Apagar DocumentRequestGeneratedDocuments
     *       + Apagar AcademicServiceRequest
     *       
     */

    @Override
    public void runTask() throws Exception {
        for (AcademicServiceRequest asr : Bennu.getInstance().getAcademicServiceRequestsSet()) {
            if (asr instanceof ULisboaServiceRequest) {
                continue;
            }
            ServiceRequestType srt = asr.getServiceRequestType();
            ULisboaServiceRequestBean bean =
                    new ULisboaServiceRequestBean(((RegistrationAcademicServiceRequest) asr).getRegistration(), false);
            bean.setServiceRequestType(srt);
            //bean.setServiceRequestPropertyBeans(createProperties(bean, asr));

            ULisboaServiceRequest clone = ULisboaServiceRequest.cloneULisboaServiceRequest(bean, asr);
            createProperties(clone, asr);
            cloneGeneratedDocuments(asr, clone);
            deleteOldie(asr);
        }
    }

    private void createProperties(ULisboaServiceRequest clone, AcademicServiceRequest original) {

        // ExecutionYear
        if (original.getExecutionYear() != null) {
            clone.addServiceRequestProperties(ServiceRequestProperty.createForExecutionYear(original.getExecutionYear(),
                    ServiceRequestSlot.getByCode(ULisboaConstants.EXECUTION_YEAR)));
        }
        // isDetailed
        clone.addServiceRequestProperties(ServiceRequestProperty.createForBoolean(original.getDetailed(),
                ServiceRequestSlot.getByCode(ULisboaConstants.IS_DETAILED)));

        // isUrgent
        clone.addServiceRequestProperties(ServiceRequestProperty.createForBoolean(original.getDetailed(),
                ServiceRequestSlot.getByCode(ULisboaConstants.IS_DETAILED)));

        // Language
        if (original.getLanguage() != null) {
            clone.addServiceRequestProperties(ServiceRequestProperty.createForLocale(original.getLanguage(),
                    ServiceRequestSlot.getByCode(ULisboaConstants.LANGUAGE)));
        }

        // #Units
        if (original.getNumberOfUnits() != null) {
            clone.addServiceRequestProperties(ServiceRequestProperty.createForInteger(original.getNumberOfUnits(),
                    ServiceRequestSlot.getByCode(ULisboaConstants.NUMBER_OF_UNITS)));
        }

        // #Pages
        if (original.getNumberOfPages() != null) {
            clone.addServiceRequestProperties(ServiceRequestProperty.createForInteger(original.getNumberOfPages(),
                    ServiceRequestSlot.getByCode(ULisboaConstants.NUMBER_OF_PAGES)));
        }

        // CycleType
        if (original.getRequestedCycle() != null) {
            clone.addServiceRequestProperties(ServiceRequestProperty.createForCycleType(original.getRequestedCycle(),
                    ServiceRequestSlot.getByCode(ULisboaConstants.CYCLE_TYPE)));
        }

        // DocumentPurposeTypeInstance
        if (original instanceof CertificateRequest) {
            CertificateRequest certificate = (CertificateRequest) original;
            if (certificate.getDocumentPurposeTypeInstance() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForDocumentPurposeTypeInstance(
                        certificate.getDocumentPurposeTypeInstance(),
                        ServiceRequestSlot.getByCode(ULisboaConstants.DOCUMENT_PURPOSE_TYPE)));
            }
        }
        if (original instanceof DeclarationRequest) {
            DeclarationRequest declaration = (DeclarationRequest) original;
            if (declaration.getDocumentPurposeType() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForDocumentPurposeTypeInstance(
                        declaration.getDocumentPurposeTypeInstance(),
                        ServiceRequestSlot.getByCode(ULisboaConstants.DOCUMENT_PURPOSE_TYPE)));
            }
        }

        // OtherDocumentPurposeTypeInstance
        if (original instanceof CertificateRequest) {
            CertificateRequest certificate = (CertificateRequest) original;
            if (certificate.getOtherDocumentPurposeTypeDescription() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForString(
                        certificate.getOtherDocumentPurposeTypeDescription(),
                        ServiceRequestSlot.getByCode(ULisboaConstants.OTHER_DOCUMENT_PURPOSE)));
            }
        }
        if (original instanceof DeclarationRequest) {
            DeclarationRequest declaration = (DeclarationRequest) original;
            if (declaration.getDocumentPurposeType() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForString(
                        declaration.getOtherDocumentPurposeTypeDescription(),
                        ServiceRequestSlot.getByCode(ULisboaConstants.OTHER_DOCUMENT_PURPOSE)));
            }
        }

        // Curriculum
        if (original instanceof CertificateRequest) {
            CertificateRequest certificate = (CertificateRequest) original;
            if (certificate.getApprovedCurriculumEntries() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForICurriculumEntry(
                        certificate.getApprovedCurriculumEntries(), ServiceRequestSlot.getByCode(ULisboaConstants.CURRICULUM)));
            }
        }
        if (original instanceof DeclarationRequest) {
            DeclarationRequest declaration = (DeclarationRequest) original;
            if (declaration.getApprovedCurriculumEntries() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForICurriculumEntry(
                        declaration.getApprovedCurriculumEntries(), ServiceRequestSlot.getByCode(ULisboaConstants.CURRICULUM)));
            }
        }

        // ProgramConclusion
        if (original instanceof RegistryDiplomaRequest) {
            RegistryDiplomaRequest certificate = (RegistryDiplomaRequest) original;
            if (certificate.getProgramConclusion() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForProgramConclusion(
                        certificate.getProgramConclusion(), ServiceRequestSlot.getByCode(ULisboaConstants.PROGRAM_CONCLUSION)));
            }
        }
        if (original instanceof DiplomaRequest) {
            DiplomaRequest certificate = (DiplomaRequest) original;
            if (certificate.getProgramConclusion() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForProgramConclusion(
                        certificate.getProgramConclusion(), ServiceRequestSlot.getByCode(ULisboaConstants.PROGRAM_CONCLUSION)));
            }
        }
        if (original instanceof DiplomaSupplementRequest) {
            DiplomaSupplementRequest certificate = (DiplomaSupplementRequest) original;
            if (certificate.getProgramConclusion() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForProgramConclusion(
                        certificate.getProgramConclusion(), ServiceRequestSlot.getByCode(ULisboaConstants.PROGRAM_CONCLUSION)));
            }
        }
        if (original instanceof DegreeFinalizationCertificateRequest) {
            DegreeFinalizationCertificateRequest certificate = (DegreeFinalizationCertificateRequest) original;
            if (certificate.getProgramConclusion() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForProgramConclusion(
                        certificate.getProgramConclusion(), ServiceRequestSlot.getByCode(ULisboaConstants.PROGRAM_CONCLUSION)));
            }
        }

    }

    private void cloneGeneratedDocuments(AcademicServiceRequest asr, ULisboaServiceRequest ulsr) {
        for (DocumentRequestGeneratedDocument doc : asr.getDocumentSet()) {
            ULisboaServiceRequestGeneratedDocument uldoc =
                    ULisboaServiceRequestGeneratedDocument.store(ulsr, doc.getContentType(), doc.getFilename(), doc.getContent());
            uldoc.setOperator(doc.getOperator());
            uldoc.setVersioningCreationDate(doc.getVersioningCreationDate());
            uldoc.setVersioningCreator(doc.getVersioningCreator());
        }
    }

    private void deleteOldie(AcademicServiceRequest asr) {
        for (DocumentRequestGeneratedDocument doc : asr.getDocumentSet()) {
            doc.delete();
        }
        asr.delete();
    }
}
