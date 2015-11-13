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
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DeclarationRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DeclarationRequest_Base;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.EnrolmentCertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.EnrolmentDeclarationRequest;
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
     *       = Levantar Pedidos criados = 
     *       + Listar <Classname,ServiceRequestType> por instalação para identificar todas as configuraçoes existentes de ServiceRequest
     *       - Para cada configuração perceber quais as propriedades válidas (que precisam de se copiadas).
     *       
     *       = Relações&Slots =
     *       + Manter identificador, versioningCreator, creationDate
     *       + Associar: AdministrativeOffice, Bennu, AcademicServiceRequestYear, ServiceRequestType, Lista de estados(AcademicServiceRequestSituations), Lista de GeneratedDocuments
     *       
     *       = Integração AcademicTreasury=
     *       + Criar os pedidos académicos sem gerar dívida (signaless)
     *       + Criar os pedidos academicos sem gerar novo identificador (num/ano)
     *       + No AcademicTreasuryEvent, fazer setAcademicServiceRequest(clone)
     *       
     *       = Properties=
     *       ? Determinar melhor forma de criar as props
     *       
     *       = Remocao =
     *       - Apagar DocumentRequestGeneratedDocuments
     *       - Apagar AcademicServiceRequest
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
            bean.setServiceRequestPropertyBeans(createProperties(bean, asr));

            ULisboaServiceRequest clone = ULisboaServiceRequest.cloneULisboaServiceRequest(bean, asr);
            cloneGeneratedDocuments(asr, clone);
        }
    }

    private <T extends AcademicServiceRequest> List<ServiceRequestPropertyBean> createProperties(ULisboaServiceRequestBean bean,
            T serviceRequest) {
        List<ServiceRequestPropertyBean> properties = new ArrayList<ServiceRequestPropertyBean>();
        ServiceRequestType srt = serviceRequest.getServiceRequestType();
        for (ServiceRequestSlotEntry entry : srt.getServiceRequestSlotEntriesSet()) {
            String code = entry.getServiceRequestSlot().getCode();
            ServiceRequestPropertyBean property = null;
            switch (code) {
            case ULisboaConstants.LANGUAGE:
                property = new ServiceRequestPropertyBean(entry);
                property.setCode(code);
                property.setValue(serviceRequest.getLanguage().toString().replace("_", "-"));
                break;
            case ULisboaConstants.DOCUMENT_PURPOSE_TYPE:
                property = new ServiceRequestPropertyBean(entry);
                property.setCode(code);
                if (serviceRequest instanceof DeclarationRequest) {
                    property.setValue(((DeclarationRequest) serviceRequest).getDocumentPurposeTypeInstance().getExternalId());
                } else {
                    property.setValue(((CertificateRequest) serviceRequest).getDocumentPurposeTypeInstance().getExternalId());
                }
                break;
            case ULisboaConstants.OTHER_DOCUMENT_PURPOSE:
                property = new ServiceRequestPropertyBean(entry);
                property.setCode(code);
                if (serviceRequest instanceof DeclarationRequest) {
                    property.setValue(((DeclarationRequest) serviceRequest).getOtherDocumentPurposeTypeDescription());
                } else {
                    property.setValue(((CertificateRequest) serviceRequest).getOtherDocumentPurposeTypeDescription());
                }
                break;
            case ULisboaConstants.IS_DETAILED:
                property = new ServiceRequestPropertyBean(entry);
                property.setCode(code);
                if (serviceRequest instanceof CertificateRequest) {
                    property.setValue(((CertificateRequest) serviceRequest).getDetailed().toString());
                }
                break;
            /* This PROPERTY is always added despite being or not a slot (see clone method) */
//            case ULisboaConstants.EXECUTION_YEAR:
//                property = new ServiceRequestPropertyBean(entry);
//                property.setCode(code);
//                property.setValue(serviceRequest.getExecutionYear().getExternalId());
//                break;
            case ULisboaConstants.IS_URGENT:
                property = new ServiceRequestPropertyBean(entry);
                property.setCode(code);
                if (serviceRequest instanceof CertificateRequest) {
                    property.setValue(((CertificateRequest) serviceRequest).getUrgentRequest().toString());
                }
                break;
            case ULisboaConstants.CYCLE_TYPE:
                property = new ServiceRequestPropertyBean(entry);
                property.setCode(code);
                property.setValue(serviceRequest.getRequestedCycle().toString());
                break;
            case ULisboaConstants.NUMBER_OF_UNITS:
                property = new ServiceRequestPropertyBean(entry);
                property.setCode(code);
                property.setValue(serviceRequest.getNumberOfUnits().toString());
            default:
                break;
            }
            if (property != null) {
                properties.add(property);
            }
        }
        return properties;
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
}
