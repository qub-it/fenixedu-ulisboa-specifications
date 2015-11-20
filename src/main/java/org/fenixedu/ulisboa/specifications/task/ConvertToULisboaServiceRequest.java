package org.fenixedu.ulisboa.specifications.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.documents.DocumentRequestGeneratedDocument;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.RegistrationAcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.CertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DeclarationRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DegreeFinalizationCertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DiplomaRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DiplomaSupplementRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.RegistryDiplomaRequest;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequestGeneratedDocument;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.AbstractDomainObject;

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
    Comparator<AbstractDomainObject> COMPARATOR_BY_OID = new Comparator<AbstractDomainObject>() {

        @Override
        public int compare(AbstractDomainObject o1, AbstractDomainObject o2) {
            return o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

    private Map<String, Pair<String, DateTime>> versioningData = new HashMap<String, Pair<String, DateTime>>();
    private List<String> versioningMySQLData = new ArrayList<String>();
    private boolean cloningSuccess = false;

    void success() {
        cloningSuccess = true;
    }

    @Override
    public void runTask() throws Exception {
        runCloning();
        generateMysqlData();
    }

    public void runCloning() {
        List<AcademicServiceRequest> academicServiceRequests =
                Bennu.getInstance().getAcademicServiceRequestsSet().stream()
                        .filter(req -> !(req instanceof ULisboaServiceRequest)).sorted(COMPARATOR_BY_OID)
                        .collect(Collectors.toList());
        for (AcademicServiceRequest asr : academicServiceRequests) {
            if (asr instanceof ULisboaServiceRequest) {
                continue;
            }
            ServiceRequestType srt = asr.getServiceRequestType();
            ULisboaServiceRequestBean bean =
                    new ULisboaServiceRequestBean(((RegistrationAcademicServiceRequest) asr).getRegistration(), false);
            bean.setServiceRequestType(srt);

            ULisboaServiceRequest clone = ULisboaServiceRequest.cloneULisboaServiceRequest(bean, asr);
            versioningData.put(clone.getExternalId(),
                    new Pair<String, DateTime>(asr.getVersioningCreator(), asr.getVersioningCreationDate()));
            versioningMySQLData.add("update ACADEMIC_SERVICE_REQUEST set VERSIONING_CREATOR = '" + asr.getVersioningCreator()
                    + "', VERSIONING_CREATION_DATE = '" + asr.getVersioningCreationDate() + "' where OID = '"
                    + clone.getExternalId() + "'");
            createProperties(clone, asr);
            cloneGeneratedDocuments(asr, clone);
            deleteOldie(asr);
        }
    }

    public void runVersioning() {
        if (cloningSuccess) {
            for (Entry<String, Pair<String, DateTime>> entry : versioningData.entrySet()) {
                DomainObject object = FenixFramework.getDomainObject(entry.getKey());
                if (object instanceof ULisboaServiceRequest) {
                    ULisboaServiceRequest ulsr = (ULisboaServiceRequest) object;
                    ulsr.setVersioningCreator(entry.getValue().getLeft());
                    ulsr.setVersioningCreationDate(entry.getValue().getRight());
                } else if (object instanceof ULisboaServiceRequestGeneratedDocument) {
                    ULisboaServiceRequestGeneratedDocument ulsrgd = (ULisboaServiceRequestGeneratedDocument) object;
                    ulsrgd.setVersioningCreator(entry.getValue().getLeft());
                    ulsrgd.setVersioningCreationDate(entry.getValue().getRight());
                }
            }
        }
    }

    public void generateMysqlData() throws IOException {
        StringBuilder sqlData = new StringBuilder("# MySQL script to migrate ULisboaServiceRequest versioning data");
        versioningMySQLData.stream().forEach(s -> sqlData.append(s));
        File file = new File("/usr/share/tomcat/" + "ULisboaServiceRequestVersioningMigration.sql");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(sqlData.toString().getBytes());
        fos.close();
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
        clone.addServiceRequestProperties(ServiceRequestProperty.createForBoolean(original.getUrgentRequest(),
                ServiceRequestSlot.getByCode(ULisboaConstants.IS_URGENT)));

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
            if (declaration.getDocumentPurposeTypeInstance() != null) {
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
            if (declaration.getOtherDocumentPurposeTypeDescription() != null) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForString(
                        declaration.getOtherDocumentPurposeTypeDescription(),
                        ServiceRequestSlot.getByCode(ULisboaConstants.OTHER_DOCUMENT_PURPOSE)));
            }
        }

        // Curriculum
        if (original instanceof CertificateRequest) {
            CertificateRequest certificate = (CertificateRequest) original;
            if (certificate.getApprovedCurriculumEntries() != null && !certificate.getApprovedCurriculumEntries().isEmpty()) {
                clone.addServiceRequestProperties(ServiceRequestProperty.createForICurriculumEntry(
                        certificate.getApprovedCurriculumEntries(), ServiceRequestSlot.getByCode(ULisboaConstants.CURRICULUM)));
            }
        }
        if (original instanceof DeclarationRequest) {
            DeclarationRequest declaration = (DeclarationRequest) original;
            if (declaration.getApprovedCurriculumEntries() != null && !declaration.getApprovedCurriculumEntries().isEmpty()) {
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
        Set<DocumentRequestGeneratedDocument> docs = new TreeSet<DocumentRequestGeneratedDocument>(COMPARATOR_BY_OID);
        docs.addAll(asr.getDocumentSet());
        for (DocumentRequestGeneratedDocument doc : docs) {
            ULisboaServiceRequestGeneratedDocument clone =
                    ULisboaServiceRequestGeneratedDocument.cloneAcademicServiceRequestDocument(doc, ulsr);
            versioningData.put(clone.getExternalId(),
                    new Pair<String, DateTime>(doc.getVersioningCreator(), doc.getVersioningCreationDate()));
            versioningMySQLData.add("update GENERIC_FILE set VERSIONING_CREATOR = '" + asr.getVersioningCreator()
                    + "', VERSIONING_CREATION_DATE = '" + asr.getVersioningCreationDate() + "' where OID = '"
                    + clone.getExternalId() + "'");
        }
    }

    private void deleteOldie(AcademicServiceRequest asr) {
        for (DocumentRequestGeneratedDocument doc : asr.getDocumentSet()) {
            doc.delete();
        }
        if (asr instanceof RegistryDiplomaRequest) {
            ((RegistryDiplomaRequest) asr).setDiplomaSupplement(null);
        }
        if (asr instanceof DiplomaSupplementRequest) {
            ((DiplomaSupplementRequest) asr).setRegistryDiplomaRequest(null);
        }
        if (asr instanceof RegistryDiplomaRequest) {
            ((RegistryDiplomaRequest) asr).setProgramConclusion(null);
        }
        if (asr instanceof DiplomaRequest) {
            ((DiplomaRequest) asr).setProgramConclusion(null);
        }
        if (asr instanceof DiplomaSupplementRequest) {
            ((DiplomaSupplementRequest) asr).setProgramConclusion(null);
        }
        if (asr instanceof DegreeFinalizationCertificateRequest) {
            ((DegreeFinalizationCertificateRequest) asr).setProgramConclusion(null);
        }
        if (asr instanceof DeclarationRequest) {
            ((DeclarationRequest) asr).setDocumentPurposeTypeInstance(null);
        }
        if (asr instanceof CertificateRequest) {
            ((CertificateRequest) asr).setDocumentPurposeTypeInstance(null);
        }
        asr.setServiceRequestType(null);
        asr.delete();
    }

    class Pair<L, R> {
        private L l;
        private R r;

        public Pair(L l, R r) {
            this.l = l;
            this.r = r;
        }

        public L getLeft() {
            return l;
        }

        public R getRight() {
            return r;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair) {
                if (getLeft() == null && getRight() == null) {
                    return ((Pair<?, ?>) obj).getLeft() == null && ((Pair<?, ?>) obj).getRight() == null;
                } else if (getLeft() == null) {
                    return getRight().equals(((Pair<?, ?>) obj).getRight());
                } else if (getRight() == null) {
                    return getLeft().equals(((Pair<?, ?>) obj).getLeft());
                } else {
                    return getLeft().equals(((Pair<?, ?>) obj).getLeft()) && getRight().equals(((Pair<?, ?>) obj).getRight());
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (getLeft() == null && getRight() == null) {
                return 1;
            } else if (getLeft() == null) {
                return getRight().hashCode();
            } else if (getRight() == null) {
                return getLeft().hashCode();
            } else {
                return getLeft().hashCode() ^ getRight().hashCode();
            }
        }
    }

    private static class CloningThread extends Thread {

        ConvertToULisboaServiceRequest task;

        public CloningThread(ConvertToULisboaServiceRequest task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                FenixFramework.getTransactionManager().withTransaction(new Callable() {

                    @Override
                    public Object call() throws Exception {
                        task.runCloning();
                        task.success();
                        return null;
                    }

                }, new Atomic() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return null;
                    }

                    @Override
                    public boolean flattenNested() {
                        return false;
                    }

                    @Override
                    public TxMode mode() {
                        return TxMode.WRITE;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static class VersioningThread extends Thread {

        ConvertToULisboaServiceRequest task;

        public VersioningThread(ConvertToULisboaServiceRequest task) {
            this.task = task;
        }

        @Override
        public void run() {
            try {
                FenixFramework.getTransactionManager().withTransaction(new Callable() {

                    @Override
                    public Object call() throws Exception {
                        task.runVersioning();
                        return null;
                    }

                }, new Atomic() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return null;
                    }

                    @Override
                    public boolean flattenNested() {
                        return false;
                    }

                    @Override
                    public TxMode mode() {
                        return TxMode.WRITE;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
