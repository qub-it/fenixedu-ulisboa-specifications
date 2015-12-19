/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoes@qub-it.com
 *               jnpa@reitoria.ulisboa.pt
 *
 * 
 * This file is part of FenixEdu QubDocs.
 *
 * FenixEdu QubDocs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu QubDocs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu QubDocs.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.AcademicProgram;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.accounting.EventType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.documents.DocumentRequestGeneratedDocument;
import org.fenixedu.academic.domain.documents.GeneratedDocument;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituation;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.academic.domain.treasury.ITreasuryBridgeAPI;
import org.fenixedu.academic.domain.util.email.Message;
import org.fenixedu.academic.dto.serviceRequests.AcademicServiceRequestBean;
import org.fenixedu.academic.dto.serviceRequests.AcademicServiceRequestCreateBean;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academictreasury.domain.serviceRequests.ITreasuryServiceRequest;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.bennu.signals.Signal;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.qubdocs.domain.serviceRequests.AcademicServiceRequestTemplate;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.service.reports.DocumentPrinter;
import org.fenixedu.ulisboa.specifications.service.reports.DocumentPrinter.PrintedDocument;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.qubit.terra.docs.util.ReportGenerationException;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

public final class ULisboaServiceRequest extends ULisboaServiceRequest_Base implements ITreasuryServiceRequest {

    private static final Logger logger = LoggerFactory.getLogger(ULisboaServiceRequest.class);

    /*
     * TODOJN onde documentar isto
     * AcademicServiceRequest API used
     * get/set RequestDate
     * get/set AcademicServiceRequestYear
     * get/set ServiceRequestNumber
     * get/set AdministrativeOffice
     * get/set ServiceRequestType
     * get/set Registration
     * get/add AcademicServiceRequestSituation
     */

    /*
     * Constructors
     */

    protected ULisboaServiceRequest() {
        super();
    }

    protected ULisboaServiceRequest(ServiceRequestType serviceRequestType, Registration registration, boolean requestedOnline,
            DateTime requestDate) {
        this();
        setServiceRequestType(serviceRequestType);
        initAcademicServiceRequest(registration, requestDate);
        setRegistration(registration);
        setIsValid(true);
        setRequestedOnline(requestedOnline);
        Signal.emit(ITreasuryBridgeAPI.ACADEMIC_SERVICE_REQUEST_NEW_SITUATION_EVENT,
                new DomainObjectEvent<ULisboaServiceRequest>(this));
    }

    private void checkRules() {
        for (ServiceRequestProperty property : getServiceRequestPropertiesSet()) {
            ServiceRequestSlotEntry entry = ServiceRequestSlotEntry.findByServiceRequestProperty(property);
            if (entry == null) {
                continue;
            }
            if (entry.getRequired() && property.isNullOrEmpty()) {
                throw new ULisboaSpecificationsDomainException(
                        "error.serviceRequests.ULisboaServiceRequest.required.property.is.empty",
                        entry.getServiceRequestSlot().getLabel().getContent());
            }
        }
    }

    protected void initAcademicServiceRequest(Registration registration, DateTime requestDate) {
        //Use the Academic Service Request init, because there is unaccessible methods
        AcademicServiceRequestCreateBean bean = new AcademicServiceRequestCreateBean(registration);
        bean.setRequestDate(requestDate);
        bean.setRequestedCycle(registration.getDegreeType().getFirstOrderedCycleType());
        bean.setUrgentRequest(Boolean.FALSE);
        bean.setFreeProcessed(Boolean.FALSE);
        bean.setLanguage(I18N.getLocale());
        super.init(bean, registration.getDegree().getAdministrativeOffice());
    }

    /*
     * Create service and update method
     */

    @Atomic
    public static ULisboaServiceRequest create(ServiceRequestType serviceRequestType, Registration registration,
            boolean requestedOnline, DateTime requestDate) {
        ULisboaServiceRequest request = new ULisboaServiceRequest(serviceRequestType, registration, requestedOnline, requestDate);
        if (!request.hasExecutionYear()) {
            ServiceRequestProperty property;
            if (request.findProperty(ULisboaConstants.EXECUTION_YEAR) != null) {
                property = request.findProperty(ULisboaConstants.EXECUTION_YEAR);
                property.setExecutionYear(ExecutionYear.readCurrentExecutionYear());
            } else {
                property = ServiceRequestProperty.create(ServiceRequestSlot.getByCode(ULisboaConstants.EXECUTION_YEAR),
                        ExecutionYear.readCurrentExecutionYear());
                request.addServiceRequestProperties(property);
            }
        }
        request.processRequest();
        request.checkRules();
        return request;
    }

    @Atomic
    public static ULisboaServiceRequest create(ULisboaServiceRequestBean bean) {
        ULisboaServiceRequest request = new ULisboaServiceRequest(bean.getServiceRequestType(), bean.getRegistration(),
                bean.isRequestedOnline(), bean.getRequestDate());
        for (ServiceRequestPropertyBean propertyBean : bean.getServiceRequestPropertyBeans()) {
            ServiceRequestProperty property = ServiceRequestProperty.create(propertyBean);
            request.addServiceRequestProperties(property);
        }
        if (!request.hasExecutionYear()) {
            ServiceRequestProperty property;
            if (request.findProperty(ULisboaConstants.EXECUTION_YEAR) != null) {
                property = request.findProperty(ULisboaConstants.EXECUTION_YEAR);
                property.setExecutionYear(ExecutionYear.readCurrentExecutionYear());
            } else {
                property = ServiceRequestProperty.create(ServiceRequestSlot.getByCode(ULisboaConstants.EXECUTION_YEAR),
                        ExecutionYear.readCurrentExecutionYear());
                request.addServiceRequestProperties(property);
            }
        }
        request.processRequest();
        request.checkRules();
        return request;
    }

    @Atomic
    public void update(ULisboaServiceRequestBean bean) {
        setRequestDate(bean.getRequestDate());
        for (ServiceRequestPropertyBean propertyBean : bean.getServiceRequestPropertyBeans()) {
            ServiceRequestProperty property = findProperty(propertyBean.getCode());
            if (property == null) {
                property = ServiceRequestProperty.create(propertyBean);
                addServiceRequestProperties(property);
            } else {
                property.setValue(propertyBean.getValue());
            }
        }
    }

    /*
     * Delete methods
     * Academic Service Request calls these two methods
     */

    @Override
    protected void disconnect() {
        for (ServiceRequestProperty property : getServiceRequestPropertiesSet()) {
            property.delete();
        }
        setRegistration(null);
        setServiceRequestType(null);
        super.disconnect();
    }

    @Override
    protected void checkRulesToDelete() {
        super.checkRulesToDelete();
    }

    /*
     * Implementation of interfaces
     * QubDocReports Service Request Interface
     * Treasury Service Request Interface
     */

    public PrintedDocument generateDocument() {
        try {
            PrintedDocument document = DocumentPrinter.print(this);
            ULisboaServiceRequestGeneratedDocument.store(this, document.getContentType(), document.getFileName(),
                    document.getData());
            return document;
        } catch (ReportGenerationException rge) {
            String composedMessage = String.format("QubDocs failed while generating document [%s - %s].", getDescription(),
                    getServiceRequestNumberYear());
            logger.error(composedMessage, rge.getCause());
            throw new ULisboaSpecificationsDomainException(rge, "error.documentRequest.errorGeneratingDocument",
                    rge.getMessage());
        } catch (Throwable t) {
            logger.error(t.getMessage(), t.getCause());
            throw new ULisboaSpecificationsDomainException(t, "error.documentRequest.errorGeneratingDocument", t.getMessage());
        }
    }

    public ULisboaServiceRequestGeneratedDocument downloadDocument() {
        DateTime last = null;
        ULisboaServiceRequestGeneratedDocument lastDoc = null;
        for (ULisboaServiceRequestGeneratedDocument document : getGeneratedDocumentsSet()) {
            if (last == null || document.getCreationDate().isAfter(last)) {
                last = document.getCreationDate();
                lastDoc = document;
            }
        }
        return lastDoc;
    }

    public List<ServiceRequestProperty> getSortedServiceRequestProperties() {
        return getServiceRequestPropertiesSet().stream().sorted(ServiceRequestProperty.COMPARATE_BY_ENTRY_NUMBER)
                .collect(Collectors.toList());
    }

    @Atomic
    public void setPrintSettings(DocumentSigner signer, AcademicServiceRequestTemplate template) {
        setDocumentSigner(signer);
        setAcademicServiceRequestTemplate(template);
    }

    @Override
    public boolean isToPrint() {
        return getServiceRequestType().isPrintable();
    }

    public boolean isSelfIssued() {
        return getActiveSituation().getCreator() == getRegistration().getPerson();
    }

    @Override
    public Person getPerson() {
        return hasRegistation() ? getRegistration().getPerson() : null;
    }

    @Override
    public boolean hasRegistation() {
        return getRegistration() != null;
    }

    @Override
    public Locale getLanguage() {
        return hasLanguage() ? findProperty(ULisboaConstants.LANGUAGE).getLocale() : null;
    }

    @Override
    public boolean hasLanguage() {
        return hasProperty(ULisboaConstants.LANGUAGE);
    }

    public DocumentPurposeTypeInstance getDocumentPurposeTypeInstance() {
        return hasDocumentPurposeTypeInstance() ? findProperty(ULisboaConstants.DOCUMENT_PURPOSE_TYPE)
                .getDocumentPurposeTypeInstance() : null;
    }

    public boolean hasDocumentPurposeTypeInstance() {
        return hasProperty(ULisboaConstants.DOCUMENT_PURPOSE_TYPE);
    }

    public String getOtherDocumentPurposeTypeDescription() {
        return hasOtherDocumentPurposeTypeDescription() ? findProperty(ULisboaConstants.OTHER_DOCUMENT_PURPOSE)
                .getString() : null;
    }

    public boolean hasOtherDocumentPurposeTypeDescription() {
        return hasProperty(ULisboaConstants.OTHER_DOCUMENT_PURPOSE);
    }

    @Override
    public boolean isDetailed() {
        ServiceRequestProperty detailedProperty = findProperty(ULisboaConstants.IS_DETAILED);
        return detailedProperty != null && detailedProperty.getBooleanValue() != null ? detailedProperty
                .getBooleanValue() : false;
    }

    @Override
    public Integer getNumberOfUnits() {
        return hasNumberOfUnits() ? findProperty(ULisboaConstants.NUMBER_OF_UNITS).getInteger() : null;
    }

    @Override
    public boolean hasNumberOfUnits() {
        return hasProperty(ULisboaConstants.NUMBER_OF_UNITS);
    }

    @Override
    public boolean isUrgent() {
        ServiceRequestProperty urgentProperty = findProperty(ULisboaConstants.IS_URGENT);
        return urgentProperty != null && urgentProperty.getBooleanValue() != null ? urgentProperty.getBooleanValue() : false;
    }

    @Override
    public Integer getNumberOfPages() {
        return hasNumberOfPages() ? findProperty(ULisboaConstants.NUMBER_OF_PAGES).getInteger() : null;
    }

    @Override
    public boolean hasNumberOfPages() {
        return hasProperty(ULisboaConstants.NUMBER_OF_PAGES);
    }

    @Override
    public Integer getNumberOfDays() {
        return hasNumberOfDays() ? findProperty(ULisboaConstants.NUMBER_OF_DAYS).getInteger() : null;
    }

    @Override
    public boolean hasNumberOfDays() {
        return hasProperty(ULisboaConstants.NUMBER_OF_DAYS);
    }

    @Override
    public CycleType getCycleType() {
        return hasCycleType() ? findProperty(ULisboaConstants.CYCLE_TYPE).getCycleType() : null;
    }

    @Override
    public boolean hasCycleType() {
        return hasProperty(ULisboaConstants.CYCLE_TYPE);
    }

    public ProgramConclusion getProgramConclusion() {
        return hasProgramConclusion() ? findProperty(ULisboaConstants.PROGRAM_CONCLUSION).getProgramConclusion() : null;
    }

    public boolean hasProgramConclusion() {
        return hasProperty(ULisboaConstants.PROGRAM_CONCLUSION);
    }

    @Override
    public ExecutionYear getExecutionYear() {
        return hasExecutionYear() ? findProperty(ULisboaConstants.EXECUTION_YEAR).getExecutionYear() : null;
    }

    @Override
    public boolean hasExecutionYear() {
        return hasProperty(ULisboaConstants.EXECUTION_YEAR);
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        return hasStudentCurricularPlan() ? findProperty(ULisboaConstants.CURRICULAR_PLAN).getStudentCurricularPlan() : null;
    }

    public boolean hasStudentCurricularPlan() {
        return hasProperty(ULisboaConstants.CURRICULAR_PLAN);
    }

    @Override
    public Set<ICurriculumEntry> getApprovedExtraCurriculum() {
        return hasApprovedExtraCurriculum() ? new HashSet<>(
                findProperty(ULisboaConstants.APPROVED_EXTRA_CURRICULUM).getICurriculumEntriesSet()) : null;
    }

    @Override
    public boolean hasApprovedExtraCurriculum() {
        return hasProperty(ULisboaConstants.APPROVED_EXTRA_CURRICULUM);
    }

    @Override
    public Set<ICurriculumEntry> getApprovedStandaloneCurriculum() {
        return hasApprovedStandaloneCurriculum() ? new HashSet<>(
                findProperty(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM).getICurriculumEntriesSet()) : null;
    }

    @Override
    public boolean hasApprovedStandaloneCurriculum() {
        return hasProperty(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM);
    }

    @Override
    public Set<ICurriculumEntry> getApprovedEnrolments() {
        return hasApprovedEnrolments() ? new HashSet<>(
                findProperty(ULisboaConstants.APPROVED_ENROLMENTS).getICurriculumEntriesSet()) : null;
    }

    @Override
    public boolean hasApprovedEnrolments() {
        return hasProperty(ULisboaConstants.APPROVED_ENROLMENTS);
    }

    @Override
    public Set<ICurriculumEntry> getCurriculum() {
        return hasCurriculum() ? new HashSet<>(findProperty(ULisboaConstants.CURRICULUM).getICurriculumEntriesSet()) : null;
    }

    @Override
    public boolean hasCurriculum() {
        return hasProperty(ULisboaConstants.CURRICULUM);
    }

    @Override
    public Set<ICurriculumEntry> getEnrolmentsByYear() {
        return hasEnrolmentsByYear() ? new HashSet<>(
                findProperty(ULisboaConstants.ENROLMENTS_BY_YEAR).getICurriculumEntriesSet()) : null;
    }

    @Override
    public boolean hasEnrolmentsByYear() {
        return hasProperty(ULisboaConstants.ENROLMENTS_BY_YEAR);
    }

    @Override
    public Set<ICurriculumEntry> getStandaloneEnrolmentsByYear() {
        return hasStandaloneEnrolmentsByYear() ? new HashSet<>(
                findProperty(ULisboaConstants.STANDALONE_ENROLMENTS_BY_YEAR).getICurriculumEntriesSet()) : null;
    }

    @Override
    public boolean hasStandaloneEnrolmentsByYear() {
        return hasProperty(ULisboaConstants.STANDALONE_ENROLMENTS_BY_YEAR);
    }

    @Override
    public Set<ICurriculumEntry> getExtracurricularEnrolmentsByYear() {
        return hasExtracurricularEnrolmentsByYear() ? new HashSet<>(
                findProperty(ULisboaConstants.EXTRACURRICULAR_ENROLMENTS_BY_YEAR).getICurriculumEntriesSet()) : null;
    }

    @Override
    public boolean hasExtracurricularEnrolmentsByYear() {
        return hasProperty(ULisboaConstants.EXTRACURRICULAR_ENROLMENTS_BY_YEAR);
    }

    @Override
    public String getDescription() {
        return getServiceRequestType().getName().getContent();
    }

    @Override
    public boolean isFor(ExecutionYear executionYear) {
        if (hasProperty(ULisboaConstants.EXECUTION_YEAR)) {
            return findProperty(ULisboaConstants.EXECUTION_YEAR).getExecutionYear().equals(executionYear);
        }
        return false;
    }

    @Override
    protected boolean hasMissingPersonalInfo() {
        return getPerson() == null || Strings.isNullOrEmpty(getPerson().getName())
                || getPerson().getDateOfBirthYearMonthDay() == null || Strings.isNullOrEmpty(getPerson().getDocumentIdNumber())
                || getPerson().getIdDocumentType() == null;
    }

    public ServiceRequestProperty findProperty(String slotCode) {
        Optional<ServiceRequestProperty> property = getServiceRequestPropertiesSet().stream()
                .filter(prop -> prop.getServiceRequestSlot().getCode().equals(slotCode)).findFirst();
        if (property.isPresent()) {
            return property.get();
        }
        return null;
    }

    public boolean hasProperty(String slotCode) {
        Optional<ServiceRequestProperty> optProperty = getServiceRequestPropertiesSet().stream()
                .filter(property -> property.getServiceRequestSlot().getCode().equals(slotCode)).findFirst();
        return optProperty.isPresent() && optProperty.get().getValue() != null;
    }

    public List<AcademicServiceRequestSituation> getAcademicServiceRequestSituationsHistory() {
        return getAcademicServiceRequestSituationsSet().stream()
                .sorted(AcademicServiceRequestSituation.COMPARATOR_BY_MOST_RECENT_SITUATION_DATE_AND_ID)
                .collect(Collectors.toList());
    }

    public List<AcademicServiceRequestSituation> getFilteredAcademicServiceRequestSituations() {
        List<AcademicServiceRequestSituation> filteredSituations = new ArrayList<AcademicServiceRequestSituation>();
        List<AcademicServiceRequestSituation> allSituations = getAcademicServiceRequestSituationsHistory();
        int i = 0;
        for (int j = 0; j < getAcademicServiceRequestSituationsSet().size(); j++) {
            AcademicServiceRequestSituation situation = allSituations.get(i);
            AcademicServiceRequestSituation previous = allSituations.get(j);
            if (isValidTransition(previous.getAcademicServiceRequestSituationType(),
                    situation.getAcademicServiceRequestSituationType())) {
                i = j;
                filteredSituations.add(situation);
            }
        }
        filteredSituations.add(allSituations.get(i));
        return filteredSituations;
    }

    /**
     * Change State Methods
     */

    @Atomic
    public void transitToProcessState() {
        if (getAcademicServiceRequestSituationType() != AcademicServiceRequestSituationType.NEW) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.changeState",
                    getAcademicServiceRequestSituationType().getLocalizedName(),
                    AcademicServiceRequestSituationType.NEW.getLocalizedName());
        }
        if (!getIsValid()) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.request");
        }
        transitState(AcademicServiceRequestSituationType.PROCESSING, ULisboaConstants.EMPTY_JUSTIFICATION.getContent());
    }

    @Atomic
    public void transitToConcludedState() {
        if (getAcademicServiceRequestSituationType() != AcademicServiceRequestSituationType.PROCESSING) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.changeState",
                    getAcademicServiceRequestSituationType().getLocalizedName(),
                    AcademicServiceRequestSituationType.PROCESSING.getLocalizedName());
        }
        if (!getIsValid()) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.request");
        }
        if (getServiceRequestType().getPrintable() && getGeneratedDocumentsSet().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.must.generate.document");
        }
        transitState(AcademicServiceRequestSituationType.CONCLUDED, ULisboaConstants.EMPTY_JUSTIFICATION.getContent());
        if (getServiceRequestType().isToNotifyUponConclusion()) {
            sendConclusionNotification();
        }
    }

    @Atomic
    public void transitToDeliverState() {
        if (getAcademicServiceRequestSituationType() != AcademicServiceRequestSituationType.CONCLUDED) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.changeState",
                    getAcademicServiceRequestSituationType().getLocalizedName(),
                    AcademicServiceRequestSituationType.CONCLUDED.getLocalizedName());
        }
        if (!getIsValid()) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.request");
        }
        transitState(AcademicServiceRequestSituationType.DELIVERED, ULisboaConstants.EMPTY_JUSTIFICATION.getContent());
    }

    @Atomic
    public void transitToCancelState(String justification) {
        if (getAcademicServiceRequestSituationType() == AcademicServiceRequestSituationType.DELIVERED) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.changeState",
                    getAcademicServiceRequestSituationType().getLocalizedName(),
                    AcademicServiceRequestSituationType.DELIVERED.getLocalizedName());
        }
        transitState(AcademicServiceRequestSituationType.CANCELLED, justification);
    }

    @Atomic
    public void transitToRejectState(String justification) {
        if (getAcademicServiceRequestSituationType() == AcademicServiceRequestSituationType.DELIVERED) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.changeState",
                    getAcademicServiceRequestSituationType().getLocalizedName(),
                    AcademicServiceRequestSituationType.DELIVERED.getLocalizedName());
        }
        transitState(AcademicServiceRequestSituationType.REJECTED, justification);
    }

    @Atomic
    public void revertState(boolean notifyRevertAction) {
        if (getAcademicServiceRequestSituationType() == AcademicServiceRequestSituationType.NEW) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.invalid.revert");
        }
        if (notifyRevertAction) {
            sendReversionApology();
        }
        AcademicServiceRequestSituation previousSituation = getFilteredAcademicServiceRequestSituations().get(1);
        transitState(previousSituation.getAcademicServiceRequestSituationType(), previousSituation.getJustification());
    }

    private void transitState(AcademicServiceRequestSituationType type, String justification) {
        if (type == AcademicServiceRequestSituationType.CANCELLED || type == AcademicServiceRequestSituationType.REJECTED) {
            Signal.emit(ITreasuryBridgeAPI.ACADEMIC_SERVICE_REQUEST_REJECT_OR_CANCEL_EVENT,
                    new DomainObjectEvent<ULisboaServiceRequest>(this));
        } else {
            Signal.emit(ITreasuryBridgeAPI.ACADEMIC_SERVICE_REQUEST_NEW_SITUATION_EVENT,
                    new DomainObjectEvent<ULisboaServiceRequest>(this));
        }
        AcademicServiceRequestBean bean = new AcademicServiceRequestBean(type, AccessControl.getPerson(), justification);
        createAcademicServiceRequestSituations(bean);
        processRequest();
    }

    @Override
    public AcademicServiceRequestSituation getSituationByType(AcademicServiceRequestSituationType type) {
        return getFilteredAcademicServiceRequestSituations().stream()
                .filter(situation -> situation.getAcademicServiceRequestSituationType() == type).findFirst().orElse(null);
    }

    public boolean isValidTransition(AcademicServiceRequestSituationType previousType,
            AcademicServiceRequestSituationType currentType) {
        List<AcademicServiceRequestSituationType> anulledStates =
                Lists.newArrayList(AcademicServiceRequestSituationType.CANCELLED, AcademicServiceRequestSituationType.REJECTED);
        //Final states
        if (!anulledStates.contains(previousType) && anulledStates.contains(currentType)) {
            return true;
        }
        if (previousType == AcademicServiceRequestSituationType.CONCLUDED
                && currentType == AcademicServiceRequestSituationType.DELIVERED) {
            return true;
        }
        if (previousType == AcademicServiceRequestSituationType.PROCESSING
                && currentType == AcademicServiceRequestSituationType.CONCLUDED) {
            return true;
        }
        if (previousType == AcademicServiceRequestSituationType.NEW
                && currentType == AcademicServiceRequestSituationType.PROCESSING) {
            return true;
        }
        return false;
    }

    private void processRequest() {
        for (ULisboaServiceRequestProcessor uLisboaServiceRequestValidator : getServiceRequestType()
                .getULisboaServiceRequestProcessorsSet()) {
            uLisboaServiceRequestValidator.process(this);
        }
    }

    private void sendConclusionNotification() {
        String emailAddress = getPerson().getDefaultEmailAddressValue();
        String subject = BundleUtil.getString(ULisboaConstants.BUNDLE, getLanguage(),
                "message.ULisboaServiceRequest.conclusionNotification.subject", getDescription(), getServiceRequestNumberYear());
        String salutation = getPerson().isMale() ? BundleUtil.getString(ULisboaConstants.BUNDLE, getLanguage(),
                "message.ULisboaServiceRequest.salutation.male",
                getPerson().getProfile().getDisplayName()) : BundleUtil.getString(ULisboaConstants.BUNDLE,
                        "message.ULisboaServiceRequest.salutation.female", getPerson().getProfile().getDisplayName());
        String body = BundleUtil.getString(ULisboaConstants.BUNDLE, getLanguage(),
                "message.ULisboaServiceRequest.conclusionNotification.body", salutation, getDescription(),
                getServiceRequestNumberYear());
        sendEmail(emailAddress, subject, body);
    }

    private void sendReversionApology() {
        String emailAddress = getPerson().getDefaultEmailAddressValue();
        String subject = BundleUtil.getString(ULisboaConstants.BUNDLE, getLanguage(),
                "message.ULisboaServiceRequest.reversionApology.subject", getDescription(), getServiceRequestNumberYear());
        String salutation = getPerson().isMale() ? BundleUtil.getString(ULisboaConstants.BUNDLE, getLanguage(),
                "message.ULisboaServiceRequest.salutation.male",
                getPerson().getProfile().getDisplayName()) : BundleUtil.getString(ULisboaConstants.BUNDLE, getLanguage(),
                        "message.ULisboaServiceRequest.salutation.female", getPerson().getProfile().getDisplayName());
        String body = BundleUtil.getString(ULisboaConstants.BUNDLE, getLanguage(),
                "message.ULisboaServiceRequest.reversionApology.body", salutation, getDescription(),
                getServiceRequestNumberYear());
        sendEmail(emailAddress, subject, body);
    }

    private void sendEmail(String emailAddress, String subject, String body) {
        new Message(Bennu.getInstance().getSystemSender(), Collections.EMPTY_LIST, Collections.EMPTY_LIST, subject, body,
                emailAddress);
    }

    /**
     * Static services
     */

    public static Stream<ULisboaServiceRequest> findAll() {
        return Bennu.getInstance().getAcademicServiceRequestsSet().stream()
                .filter(request -> request instanceof ULisboaServiceRequest).map(ULisboaServiceRequest.class::cast);
    }

    public static Stream<ULisboaServiceRequest> findByRegistration(Registration registration) {
        return findAll().filter(request -> request.getRegistration().equals(registration));
    }

    public static Stream<ULisboaServiceRequest> findNewAcademicServiceRequests(Registration registration) {
        return findByRegistration(registration)
                .filter(request -> request.getAcademicServiceRequestSituationType() == AcademicServiceRequestSituationType.NEW);
    }

    public static Stream<ULisboaServiceRequest> findProcessingAcademicServiceRequests(Registration registration) {
        return findByRegistration(registration).filter(
                request -> request.getAcademicServiceRequestSituationType() == AcademicServiceRequestSituationType.PROCESSING);
    }

    public static Stream<ULisboaServiceRequest> findToDeliverAcademicServiceRequests(Registration registration) {
        return findByRegistration(registration).filter(
                request -> request.getAcademicServiceRequestSituationType() == AcademicServiceRequestSituationType.CONCLUDED);
    }

    /**
     * Delete Listener for Service Request Properties Relations
     * Delete Listener for Service Request Type
     */

    public static void setupListenerForPropertiesDeletion() {
        //Registration
        FenixFramework.getDomainModel().registerDeletionListener(Registration.class, new DeletionListener<Registration>() {

            @Override
            public void deleting(Registration registration) {
                for (ULisboaServiceRequest request : registration.getULisboaServiceRequestsSet()) {
                    request.setRegistration(null);
                    request.setIsValid(false);
                }
            }
        });
        //DocumentPurposeTypeInstance
        FenixFramework.getDomainModel().registerDeletionListener(DocumentPurposeTypeInstance.class,
                new DeletionListener<DocumentPurposeTypeInstance>() {

                    @Override
                    public void deleting(DocumentPurposeTypeInstance documentPurposeTypeInstance) {
                        for (ServiceRequestProperty property : documentPurposeTypeInstance.getServiceRequestPropertiesSet()) {
                            property.setDocumentPurposeTypeInstance(null);
                            property.getULisboaServiceRequest().setIsValid(false);
                        }
                    }
                });
        //Execution Year
        FenixFramework.getDomainModel().registerDeletionListener(ExecutionYear.class, new DeletionListener<ExecutionYear>() {

            @Override
            public void deleting(ExecutionYear executionYear) {
                for (ServiceRequestProperty property : executionYear.getServiceRequestPropertiesSet()) {
                    property.setExecutionYear(null);
                    property.getULisboaServiceRequest().setIsValid(false);
                }
            }
        });
        //Student Curricular Plan
        FenixFramework.getDomainModel().registerDeletionListener(StudentCurricularPlan.class,
                new DeletionListener<StudentCurricularPlan>() {

                    @Override
                    public void deleting(StudentCurricularPlan studentCurricularPlan) {
                        for (ServiceRequestProperty property : studentCurricularPlan.getServiceRequestPropertiesSet()) {
                            property.setStudentCurricularPlan(null);
                            property.getULisboaServiceRequest().setIsValid(false);
                        }
                    }
                });
        //ExternalEnrolment
        FenixFramework.getDomainModel().registerDeletionListener(ExternalEnrolment.class,
                new DeletionListener<ExternalEnrolment>() {

                    @Override
                    public void deleting(ExternalEnrolment externalEnrolment) {
                        for (ServiceRequestProperty property : externalEnrolment.getServiceRequestPropertiesSet()) {
                            property.removeExternalEnrolments(externalEnrolment);
                            property.getULisboaServiceRequest().setIsValid(false);
                        }
                    }
                });
        //CurriculumLine
        FenixFramework.getDomainModel().registerDeletionListener(CurriculumLine.class, new DeletionListener<CurriculumLine>() {

            @Override
            public void deleting(CurriculumLine curriculumLine) {
                for (ServiceRequestProperty property : curriculumLine.getServiceRequestPropertiesSet()) {
                    property.removeCurriculumLines(curriculumLine);
                    property.getULisboaServiceRequest().setIsValid(false);
                }

            }
        });
    }

    public static void setupListenerForServiceRequestTypeDeletion() {
        FenixFramework.getDomainModel().registerDeletionListener(ServiceRequestType.class,
                new DeletionListener<ServiceRequestType>() {
                    @Override
                    public void deleting(ServiceRequestType serviceRequestType) {
                        serviceRequestType.getULisboaServiceRequestProcessorsSet().clear();
                        serviceRequestType.getServiceRequestSlotEntriesSet().clear();
                    }
                });
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * ****************
     * <Deprecated API>
     * ****************
     */
    @Deprecated
    @Override
    public void edit(AcademicServiceRequestBean academicServiceRequestBean) {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.workFlow");
    }

    @Deprecated
    @Override
    protected String getDescription(AcademicServiceRequestType academicServiceRequestType, String specificServiceType) {
        return getDescription();
    }

    @Deprecated
    @Override
    protected String getDescription(AcademicServiceRequestType academicServiceRequestType) {
        return getDescription();
    }

    @Deprecated
    @Override
    public boolean isPayedUponCreation() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isPossibleToSendToOtherEntity() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isManagedWithRectorateSubmissionBatch() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public EventType getEventType() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public AcademicServiceRequestType getAcademicServiceRequestType() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean hasPersonalInfo() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public AcademicProgram getAcademicProgram() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected void checkRulesToChangeState(AcademicServiceRequestSituationType situationType) {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isDownloadPossible() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected void internalChangeState(AcademicServiceRequestBean academicServiceRequestBean) {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected void verifyIsToDeliveredAndIsPayed(AcademicServiceRequestBean academicServiceRequestBean) {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected void verifyIsToProcessAndHasPersonalInfo(AcademicServiceRequestBean academicServiceRequestBean) {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isPiggyBackedOnRegistry() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isCanGenerateRegistryCode() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isRequestForPerson() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isRequestForPhd() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isRequestForRegistration() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected void internalRevertToProcessingState() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public void revertToProcessingState() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isDiploma() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isPastDiploma() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isRegistryDiploma() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isDiplomaSupplement() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isBatchSet() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isRequestedWithCycle() {
        return hasCycleType();
    }

    @Deprecated
    @Override
    public boolean hasRegistryCode() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected List<AcademicServiceRequestSituationType> getNewSituationAcceptedSituationsTypes() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected List<AcademicServiceRequestSituationType> getProcessingSituationAcceptedSituationsTypes() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected List<AcademicServiceRequestSituationType> getSentToExternalEntitySituationAcceptedSituationsTypes() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected List<AcademicServiceRequestSituationType> getReceivedFromExternalEntitySituationAcceptedSituationsTypes() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    protected List<AcademicServiceRequestSituationType> getConcludedSituationAcceptedSituationsTypes() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public GeneratedDocument getLastGeneratedDocument() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public Set<DocumentRequestGeneratedDocument> getDocumentSet() {
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public Boolean getUrgentRequest() {
        return isUrgent();
        //throw new DomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public boolean isUrgentRequest() {
        return isUrgent();
        //throw new DomainException("error.serviceRequests.ULisboaServiceRequest.deprecated.method");
    }

    @Deprecated
    @Override
    public Boolean getDetailed() {
        return isDetailed();
    }

    /*
     * *****************
     * </Deprecated API>
     * *****************
     */

}
