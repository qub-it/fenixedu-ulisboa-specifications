package org.fenixedu.ulisboa.specifications.task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.documents.DocumentRequestGeneratedDocument;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituation;
import org.fenixedu.academic.domain.serviceRequests.RegistrationAcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.CertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DeclarationRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DegreeFinalizationCertificateRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DiplomaRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DiplomaSupplementRequest;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.RegistryDiplomaRequest;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequestGeneratedDocument;

import pt.ist.fenixframework.core.AbstractDomainObject;

public class TestULisboaServiceRequest extends CustomTask {

    Comparator<AbstractDomainObject> COMPARATOR_BY_OID = new Comparator<AbstractDomainObject>() {

        @Override
        public int compare(AbstractDomainObject o1, AbstractDomainObject o2) {
            return o1.getExternalId().compareTo(o2.getExternalId());
        }
    };
    Comparator<ICurriculumEntry> COMPARATOR_ICE_BY_OID = new Comparator<ICurriculumEntry>() {

        @Override
        public int compare(ICurriculumEntry o1, ICurriculumEntry o2) {
            return o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

    @Override
    public void runTask() throws Exception {
        // TODO Auto-generated method stub
        printDebtAccounts();
        printULisboaServiceRequests();
//        printAcademicServiceRequests();
    }

    private final static String OUTPUT_DEBT_FILE_NAME = "/home/jnpa/Documents/new/debtAccounts.dat";
    private final static String OUTPUT_SERVICE_REQUEST_FILE_NAME = "/home/jnpa/Documents/new/serviceRequests.dat";
//    private final static String OUTPUT_DEBT_FILE_NAME = "/home/jnpa/Documents/old/debtAccounts.dat";
//    private final static String OUTPUT_SERVICE_REQUEST_FILE_NAME = "/home/jnpa/Documents/old/serviceRequests.dat";

    private final static Charset ENCODING = StandardCharsets.UTF_8;

    private void writeLargerTextFile(String aFileName, List<String> aLines) throws IOException {
        Path path = Paths.get(aFileName);
        Files.createDirectories(path.getParent());
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
            for (String line : aLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private void printDebtAccounts() throws IOException {
        List<String> debtAccountLines = new ArrayList<String>();
        List<DebtAccount> debtAccounts =
                Bennu.getInstance().getDebtAccountsSet().stream().sorted(COMPARATOR_BY_OID).collect(Collectors.toList());
        for (DebtAccount debtAccount : debtAccounts) {
            StringBuilder sb = new StringBuilder();
            sb.append(debtAccount.getExternalId());
            sb.append(debtAccount.getCustomer().getExternalId());
            sb.append(debtAccount.getClosed());
            sb.append(debtAccount.getTotalInDebt());
            debtAccountLines.add(sb.toString());
        }
        writeLargerTextFile(OUTPUT_DEBT_FILE_NAME, debtAccountLines);
    }

    private void printAcademicServiceRequests() throws IOException {
        List<String> serviceRequestLines = new ArrayList<String>();
        List<AcademicServiceRequest> academicServiceRequests = Bennu.getInstance().getAcademicServiceRequestsSet().stream()
                .filter(req -> !(req instanceof ULisboaServiceRequest)).sorted(COMPARATOR_BY_OID).collect(Collectors.toList());
        for (AcademicServiceRequest academicServiceRequest : academicServiceRequests) {
            if (academicServiceRequest instanceof ULisboaServiceRequest) {
                continue;
            }
            StringBuilder sb = new StringBuilder();

            if (academicServiceRequest.getLanguage() != null) {
                sb.append(academicServiceRequest.getLanguage());
            }

            if (academicServiceRequest instanceof DeclarationRequest) {
                DeclarationRequest request = (DeclarationRequest) academicServiceRequest;
                if (request.getDocumentPurposeTypeInstance() != null) {
                    sb.append(request.getDocumentPurposeTypeInstance().getExternalId());
                }
                sb.append(request.getOtherDocumentPurposeTypeDescription());
            }
            if (academicServiceRequest instanceof CertificateRequest) {
                CertificateRequest request = (CertificateRequest) academicServiceRequest;
                if (request.getDocumentPurposeTypeInstance() != null) {
                    sb.append(request.getDocumentPurposeTypeInstance().getExternalId());
                }
                sb.append(request.getOtherDocumentPurposeTypeDescription());
            }
            sb.append(academicServiceRequest.getDetailed());

            if (academicServiceRequest.getExecutionYear() != null) {
                sb.append(academicServiceRequest.getExecutionYear().getExternalId());
            }

            sb.append(academicServiceRequest.getUrgentRequest());

            sb.append(academicServiceRequest.getNumberOfUnits());

            sb.append(academicServiceRequest.getNumberOfPages());

            if (academicServiceRequest.getRequestedCycle() != null) {
                sb.append(academicServiceRequest.getRequestedCycle());
            }

            if (academicServiceRequest instanceof DeclarationRequest) {
                DeclarationRequest request = (DeclarationRequest) academicServiceRequest;
                if (request.getApprovedCurriculumEntries() != null && !request.getApprovedCurriculumEntries().isEmpty()) {
                    List<ICurriculumEntry> entries = request.getApprovedCurriculumEntries().stream().sorted(COMPARATOR_ICE_BY_OID)
                            .collect(Collectors.toList());
                    for (ICurriculumEntry curriculumEntry : entries) {
                        sb.append(curriculumEntry.getExternalId());
                    }
                }
            }
            if (academicServiceRequest instanceof CertificateRequest) {
                CertificateRequest request = (CertificateRequest) academicServiceRequest;
                if (request.getApprovedCurriculumEntries() != null && !request.getApprovedCurriculumEntries().isEmpty()) {
                    List<ICurriculumEntry> entries = request.getApprovedCurriculumEntries().stream().sorted(COMPARATOR_ICE_BY_OID)
                            .collect(Collectors.toList());
                    for (ICurriculumEntry curriculumEntry : entries) {
                        sb.append(curriculumEntry.getExternalId());
                    }
                }
            }
            // ProgramConclusion
            if (academicServiceRequest instanceof RegistryDiplomaRequest) {
                RegistryDiplomaRequest request = (RegistryDiplomaRequest) academicServiceRequest;
                sb.append(request.getProgramConclusion() != null ? request.getProgramConclusion().getExternalId() : "");
            }
            if (academicServiceRequest instanceof DiplomaRequest) {
                DiplomaRequest request = (DiplomaRequest) academicServiceRequest;
                sb.append(request.getProgramConclusion() != null ? request.getProgramConclusion().getExternalId() : "");
            }
            if (academicServiceRequest instanceof DiplomaSupplementRequest) {
                DiplomaSupplementRequest request = (DiplomaSupplementRequest) academicServiceRequest;
                sb.append(request.getProgramConclusion() != null ? request.getProgramConclusion().getExternalId() : "");
            }
            if (academicServiceRequest instanceof DegreeFinalizationCertificateRequest) {
                DegreeFinalizationCertificateRequest request = (DegreeFinalizationCertificateRequest) academicServiceRequest;
                sb.append(request.getProgramConclusion() != null ? request.getProgramConclusion().getExternalId() : "");
            }

            List<DocumentRequestGeneratedDocument> docs =
                    academicServiceRequest.getDocumentSet().stream().sorted(COMPARATOR_BY_OID).collect(Collectors.toList());
            for (DocumentRequestGeneratedDocument generatedDocument : docs) {
                sb.append(generatedDocument.getFilename());
                sb.append(generatedDocument.getVersioningCreator());
                sb.append(generatedDocument.getSize());
                sb.append(generatedDocument.getCreationDate());
            }
            List<AcademicServiceRequestSituation> situations = academicServiceRequest.getAcademicServiceRequestSituationsSet()
                    .stream().sorted(COMPARATOR_BY_OID).collect(Collectors.toList());
            for (AcademicServiceRequestSituation situation : situations) {
                sb.append(situation.getExternalId());
                sb.append(situation.getAcademicServiceRequestSituationType().getLocalizedName());
            }

            sb.append(academicServiceRequest.getAdministrativeOffice().getExternalId());
            sb.append(academicServiceRequest.getRequestDate());
            sb.append(academicServiceRequest.getServiceRequestNumberYear());
            if (academicServiceRequest instanceof RegistrationAcademicServiceRequest) {
                sb.append(((RegistrationAcademicServiceRequest) academicServiceRequest).getRegistration().getExternalId());
            }

            if (academicServiceRequest.getServiceRequestType() != null) {
                sb.append(academicServiceRequest.getServiceRequestType().getExternalId());
            }

            sb.append(academicServiceRequest.getVersioningCreator());
            sb.append(academicServiceRequest.getVersioningCreationDate());

            serviceRequestLines.add(sb.toString());

        }
        writeLargerTextFile(OUTPUT_SERVICE_REQUEST_FILE_NAME, serviceRequestLines);
    }

    private void printULisboaServiceRequests() throws IOException {
        List<String> serviceRequestLines = new ArrayList<String>();
        List<ULisboaServiceRequest> uLisboaServiceRequests = Bennu.getInstance().getAcademicServiceRequestsSet().stream()
                .filter(req -> req instanceof ULisboaServiceRequest).map(ULisboaServiceRequest.class::cast)
                .sorted((sr1, sr2) -> sr1.getExternalId().compareTo(sr2.getExternalId())).collect(Collectors.toList());
        for (ULisboaServiceRequest uLisboaServiceRequest : uLisboaServiceRequests) {
            StringBuilder sb = new StringBuilder();

            if (uLisboaServiceRequest.getLanguage() != null) {
                sb.append(uLisboaServiceRequest.getLanguage());
            }

            if (uLisboaServiceRequest.getDocumentPurposeTypeInstance() != null) {
                sb.append(uLisboaServiceRequest.getDocumentPurposeTypeInstance().getExternalId());
            }
            sb.append(uLisboaServiceRequest.getOtherDocumentPurposeTypeDescription());

            sb.append(uLisboaServiceRequest.isDetailed());

            if (uLisboaServiceRequest.getExecutionYear() != null) {
                sb.append(uLisboaServiceRequest.getExecutionYear().getExternalId());
            }

            sb.append(uLisboaServiceRequest.isDetailed());

            sb.append(uLisboaServiceRequest.getNumberOfUnits());

            sb.append(uLisboaServiceRequest.getNumberOfPages());

            if (uLisboaServiceRequest.hasCycleType()) {
                sb.append(uLisboaServiceRequest.getCycleType());
            }

            if (uLisboaServiceRequest.hasCurriculum()) {
                List<ICurriculumEntry> entries = uLisboaServiceRequest.getCurriculum().stream()
                        .sorted((e1, e2) -> e1.getExternalId().compareTo(e2.getExternalId())).collect(Collectors.toList());
                for (ICurriculumEntry curriculumEntry : entries) {
                    sb.append(curriculumEntry.getExternalId());
                }
            }
            if (uLisboaServiceRequest.hasProgramConclusion()) {
                sb.append(uLisboaServiceRequest.getProgramConclusion().getExternalId());
            }

            List<ULisboaServiceRequestGeneratedDocument> docs = uLisboaServiceRequest.getGeneratedDocumentsSet().stream()
                    .sorted(COMPARATOR_BY_OID).collect(Collectors.toList());
            for (ULisboaServiceRequestGeneratedDocument generatedDocument : docs) {
                sb.append(generatedDocument.getFilename());
                sb.append(generatedDocument.getVersioningCreator());
                sb.append(generatedDocument.getSize());
                sb.append(generatedDocument.getCreationDate());
            }
            List<AcademicServiceRequestSituation> situations = uLisboaServiceRequest.getAcademicServiceRequestSituationsSet()
                    .stream().sorted(COMPARATOR_BY_OID).collect(Collectors.toList());
            for (AcademicServiceRequestSituation situation : situations) {
                sb.append(situation.getExternalId());
                sb.append(situation.getAcademicServiceRequestSituationType().getLocalizedName());
            }
            sb.append(uLisboaServiceRequest.getAdministrativeOffice().getExternalId());
            sb.append(uLisboaServiceRequest.getRequestDate());
            sb.append(uLisboaServiceRequest.getServiceRequestNumberYear());

            sb.append(uLisboaServiceRequest.getRegistration().getExternalId());

            if (uLisboaServiceRequest.getServiceRequestType() != null) {
                sb.append(uLisboaServiceRequest.getServiceRequestType().getExternalId());
            }

            sb.append(uLisboaServiceRequest.getVersioningCreator());
            sb.append(uLisboaServiceRequest.getVersioningCreationDate());

            serviceRequestLines.add(sb.toString());
        }
        writeLargerTextFile(OUTPUT_SERVICE_REQUEST_FILE_NAME, serviceRequestLines);
    }
}
