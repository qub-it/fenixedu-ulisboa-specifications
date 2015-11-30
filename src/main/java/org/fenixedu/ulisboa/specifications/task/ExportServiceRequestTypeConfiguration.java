package org.fenixedu.ulisboa.specifications.task;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Optional;
import java.util.function.Consumer;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

public class ExportServiceRequestTypeConfiguration extends CustomTask {

    StringBuilder document = new StringBuilder();

    @Override
    public void runTask() throws Exception {
        final File csv =
                new File("/tmp/SRTConfiguration/MapaSRT-" + Bennu.getInstance().getInstitutionUnit().getAcronym() + ".csv");
        final FileOutputStream fos = new FileOutputStream(csv);
        writeHeaders();
        writeServiceRequestTypes();
        fos.write(document.toString().getBytes());
        fos.flush();
        fos.close();
    }

    private void writeHeaders() {
        StringBuilder headers = new StringBuilder();
        headers.append("Code\t");
        headers.append("Name\t");
        headers.append("Active\t");
        headers.append("Emolument\t");
        headers.append("Notify\t");
        headers.append("Print\t");
        headers.append("Online\t");
        headers.append("Category\t");
        headers.append("Processors\t");
        headers.append("language\t");
        headers.append("documentPurposeType\t");
        headers.append("otherDocumentPurpose\t");
        headers.append("isDetailed\t");
        headers.append("isUrgent\t");
        headers.append("cycleType\t");
        headers.append("programConclusion\t");
        headers.append("numberOfUnits\t");
        headers.append("numberOfDays\t");
        headers.append("numberOfPages\t");
        headers.append("executionYear\t");
        headers.append("curricularPlan\t");
        headers.append("approvedExtraCurriculum\t");
        headers.append("approvedStandaloneCurriculum\t");
        headers.append("approvedEnrolments\t");
        headers.append("curriculum\t");
        headers.append("enrolmentsByYear\t");
        document.append(headers.toString());
        document.append("\n");
    }

    private void writeServiceRequestTypes() {
        Consumer<ServiceRequestType> print = srt -> {
            StringBuilder row = new StringBuilder();
            row.append(srt.getCode() + "\t");
            row.append(srt.getName().json() + "\t");
            row.append(srt.isActive() ? "Y\t" : "N\t");
            row.append(srt.isPayable() ? "Y\t" : "N\t");
            row.append(srt.isToNotifyUponConclusion() ? "Y\t" : "N\t");
            row.append(srt.isPrintable() ? "Y\t" : "N\t");
            row.append(srt.isRequestedOnline() ? "Y\t" : "N\t");
            row.append(srt.getServiceRequestCategory().getName() + "\t");
            row.append(getProcessors(srt));
            row.append(getSlotConfig(srt, ULisboaConstants.LANGUAGE));
            row.append(getSlotConfig(srt, ULisboaConstants.DOCUMENT_PURPOSE_TYPE));
            row.append(getSlotConfig(srt, ULisboaConstants.OTHER_DOCUMENT_PURPOSE));
            row.append(getSlotConfig(srt, ULisboaConstants.IS_DETAILED));
            row.append(getSlotConfig(srt, ULisboaConstants.IS_URGENT));
            row.append(getSlotConfig(srt, ULisboaConstants.CYCLE_TYPE));
            row.append(getSlotConfig(srt, ULisboaConstants.PROGRAM_CONCLUSION));
            row.append(getSlotConfig(srt, ULisboaConstants.NUMBER_OF_UNITS));
            row.append(getSlotConfig(srt, ULisboaConstants.NUMBER_OF_DAYS));
            row.append(getSlotConfig(srt, ULisboaConstants.NUMBER_OF_PAGES));
            row.append(getSlotConfig(srt, ULisboaConstants.EXECUTION_YEAR));
            row.append(getSlotConfig(srt, ULisboaConstants.CURRICULAR_PLAN));
            row.append(getSlotConfig(srt, ULisboaConstants.APPROVED_EXTRA_CURRICULUM));
            row.append(getSlotConfig(srt, ULisboaConstants.APPROVED_STANDALONE_CURRICULUM));
            row.append(getSlotConfig(srt, ULisboaConstants.APPROVED_ENROLMENTS));
            row.append(getSlotConfig(srt, ULisboaConstants.CURRICULUM));
            row.append(getSlotConfig(srt, ULisboaConstants.ENROLMENTS_BY_YEAR));
            document.append(row.toString());
            document.append("\n");
        };
        ServiceRequestType.findAll().filter(srt -> srt.getAcademicServiceRequestsSet().size() > 0)
                .sorted(ServiceRequestType.COMPARE_BY_CATEGORY_THEN_BY_NAME).forEach(print);
        ServiceRequestType.findAll().filter(srt -> srt.getAcademicServiceRequestsSet().size() == 0)
                .sorted(ServiceRequestType.COMPARE_BY_CATEGORY_THEN_BY_NAME).forEach(print);
    }

    private String getSlotConfig(ServiceRequestType srt, String slotCode) {
        Optional<ServiceRequestSlotEntry> slot = srt.getServiceRequestSlotEntriesSet().stream()
                .filter(srse -> srse.getServiceRequestSlot().getCode().equals(slotCode)).findFirst();
        if (slot.isPresent()) {
            return "Y/" + (slot.get().getRequired() ? "Y/" : "N/") + (slot.get().getOrderNumber() + 1) + "\t";
        } else {
            return "0\t";
        }
    }

    private String getProcessors(ServiceRequestType srt) {
        StringBuilder processors = new StringBuilder();
        srt.getULisboaServiceRequestProcessorsSet().stream()
                .forEach(srp -> processors.append(srp.getName().getContent() + " || "));
        if (processors.length() > 0) {
            processors.delete(processors.length() - 4, processors.length());
        }
        processors.append("\t");
        return processors.toString();
    }
}
