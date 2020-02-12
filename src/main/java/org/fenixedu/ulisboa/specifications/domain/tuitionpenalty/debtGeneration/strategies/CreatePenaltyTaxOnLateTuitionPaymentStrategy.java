package org.fenixedu.ulisboa.specifications.domain.tuitionpenalty.debtGeneration.strategies;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.debtGeneration.AcademicDebtGenerationProcessingResult;
import org.fenixedu.academictreasury.domain.debtGeneration.AcademicDebtGenerationRule;
import org.fenixedu.academictreasury.domain.debtGeneration.IAcademicDebtGenerationRuleStrategy;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.services.EmolumentServices;
import org.fenixedu.academictreasury.util.AcademicTreasuryConstants;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.tuitionpenalty.TuitionPenaltyConfiguration;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class CreatePenaltyTaxOnLateTuitionPaymentStrategy implements IAcademicDebtGenerationRuleStrategy {

    private static Logger logger = LoggerFactory.getLogger(CreatePenaltyTaxOnLateTuitionPaymentStrategy.class);

    @Override
    public boolean isAppliedOnAcademicTaxDebitEntries() {
        return false;
    }

    @Override
    public boolean isAppliedOnOtherDebitEntries() {
        return false;
    }

    @Override
    public boolean isAppliedOnTuitionDebitEntries() {
        return false;
    }

    @Override
    public boolean isToAggregateDebitEntries() {
        return false;
    }

    @Override
    public boolean isToCloseDebitNote() {
        return false;
    }

    @Override
    public boolean isToCreateDebitEntries() {
        return false;
    }

    @Override
    public boolean isToCreatePaymentReferenceCodes() {
        return false;
    }

    @Override
    public boolean isEntriesRequired() {
        return false;
    }

    @Override
    public boolean isToAlignAcademicTaxesDueDate() {
        return false;
    }

    @Override
    @Atomic(mode = TxMode.READ)
    public List<AcademicDebtGenerationProcessingResult> process(final AcademicDebtGenerationRule rule) {

        if (!rule.isActive()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.not.active.to.process");
        }

        if (TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType() == null) {
            return Lists.newArrayList();
        }

        if (TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType() == null) {
            return Lists.newArrayList();
        }

        final List<AcademicDebtGenerationProcessingResult> resultList = Lists.newArrayList();
        
        for (final DegreeCurricularPlan degreeCurricularPlan : rule.getDegreeCurricularPlansSet()) {
            for (final Registration registration : degreeCurricularPlan.getRegistrations()) {

                if (registration.getStudentCurricularPlan(rule.getExecutionYear()) == null) {
                    continue;
                }

                if (!rule.getDegreeCurricularPlansSet()
                        .contains(registration.getStudentCurricularPlan(rule.getExecutionYear()).getDegreeCurricularPlan())) {
                    continue;
                }

                // Discard registrations not active and with no enrolments
                if (!registration.hasAnyActiveState(rule.getExecutionYear())) {
                    continue;
                }

                final AcademicDebtGenerationProcessingResult result = new AcademicDebtGenerationProcessingResult(rule, registration);
                resultList.add(result);
                try {
                    processPenaltiesForRegistration(rule, registration);
                } catch (final AcademicTreasuryDomainException e) {
                    result.markException(e);
                    logger.debug(e.getMessage());
                } catch (final Exception e) {
                    result.markException(e);
                    e.printStackTrace();
                }
            }
        }
        
        return resultList;
    }

    @Override
    @Atomic(mode = TxMode.READ)
    public List<AcademicDebtGenerationProcessingResult> process(final AcademicDebtGenerationRule rule, final Registration registration) {
        if (!rule.isActive()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.not.active.to.process");
        }

        if (TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType() == null) {
            return Lists.newArrayList();
        }

        if (TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType() == null) {
            return Lists.newArrayList();
        }

        if (TuitionPenaltyConfiguration.getInstance().getExecutionYearSlot() == null) {
            return Lists.newArrayList();
        }

        if (registration.getStudentCurricularPlan(rule.getExecutionYear()) == null) {
            return Lists.newArrayList();
        }

        if (!rule.getDegreeCurricularPlansSet()
                .contains(registration.getStudentCurricularPlan(rule.getExecutionYear()).getDegreeCurricularPlan())) {
            return Lists.newArrayList();
        }

        // Discard registrations not active and with no enrolments
        if (!registration.hasAnyActiveState(rule.getExecutionYear())) {
            return Lists.newArrayList();
        }

        final AcademicDebtGenerationProcessingResult result = new AcademicDebtGenerationProcessingResult(rule, registration);

        try {
            processPenaltiesForRegistration(rule, registration);
        } catch (final AcademicTreasuryDomainException e) {
            result.markException(e);
            logger.debug(e.getMessage());
        } catch (final Exception e) {
            result.markException(e);
            e.printStackTrace();
        }
        
        return Lists.newArrayList(result);
    }

    private void processPenaltiesForRegistration(final AcademicDebtGenerationRule rule, final Registration registration) {
        Optional<? extends AcademicTreasuryEvent> tuitionEventOptional =
                AcademicTreasuryEvent.findUniqueForRegistrationTuition(registration, rule.getExecutionYear());

        if (!tuitionEventOptional.isPresent()) {
            return;
        }

        final AcademicTreasuryEvent tuitionEvent = tuitionEventOptional.get();

        if (!tuitionEvent.isCharged()) {
            return;
        }

        outer: for (final DebitEntry tuitionInstallmentDebitEntry : DebitEntry.find(tuitionEvent).collect(Collectors.<DebitEntry> toSet())) {
            if (tuitionInstallmentDebitEntry.getProduct().getProductGroup() != AcademicTreasurySettings.getInstance().getTuitionProductGroup()) {
                continue;
            }

            if (tuitionInstallmentDebitEntry.getProduct().getTuitionInstallmentOrder() <= 0) {
                continue;
            }

            if (tuitionInstallmentDebitEntry.isInDebt()) {
                continue;
            }

            final DateTime lastPaymentDate = tuitionInstallmentDebitEntry.getLastPaymentDate();

            if (lastPaymentDate == null) {
                continue;
            }

            if (!tuitionInstallmentDebitEntry.getDueDate().isBefore(lastPaymentDate.toLocalDate())) {
                continue;
            }

            // Find academic service request for
            final ServiceRequestType type = TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType();
            final ServiceRequestSlot installmentOrderSlot =
                    TuitionPenaltyConfiguration.getInstance().getTuitionInstallmentOrderSlot();
            final ServiceRequestSlot executionYearSlot = TuitionPenaltyConfiguration.getInstance().getExecutionYearSlot();

            if (type == null || installmentOrderSlot == null || executionYearSlot == null) {
                throw new RuntimeException("error");
            }

            final Set<ULisboaServiceRequest> tuitionPenaltyRequests = ULisboaServiceRequest.findByRegistration(registration)
                    .filter(s -> s.getServiceRequestType() == type && s.getExecutionYear() == rule.getExecutionYear())
                    .collect(Collectors.toSet());

            // First check if is charged
            boolean isDebitEntryCharged = false;
            for (final ULisboaServiceRequest request : tuitionPenaltyRequests) {

                if (ServiceRequestProperty.find(request, installmentOrderSlot).count() == 0) {
                    throw new ULisboaSpecificationsDomainException(
                            "error.CreatePenaltyTaxOnLateTuitionPaymentStrategy.serviceRequest.without.installmentOrderSlot.on.iterate");
                }

                for (final ServiceRequestProperty property : ServiceRequestProperty.find(request, installmentOrderSlot).collect(Collectors.toSet())) {
                    if (property.getInteger() == null) {
                        continue;
                    }

                    if (!property.getInteger().equals(tuitionInstallmentDebitEntry.getProduct().getTuitionInstallmentOrder())) {
                        continue;
                    }

                    // Found! Get academic treasury event to check if it is charged
                    Optional<? extends AcademicTreasuryEvent> academicServiceRequest = AcademicTreasuryEvent.findUnique(request);
                    if (academicServiceRequest.isPresent() && academicServiceRequest.get().isCharged()) {
                    	isDebitEntryCharged = true;
                    }
                }
            }
            
            if(isDebitEntryCharged) {
            	continue outer;
            }
            
            // Is not charged, iterate over again and charge on the first request found
            for (final ULisboaServiceRequest request : tuitionPenaltyRequests) {

                if (ServiceRequestProperty.find(request, installmentOrderSlot).count() == 0) {
                    throw new ULisboaSpecificationsDomainException(
                            "error.CreatePenaltyTaxOnLateTuitionPaymentStrategy.serviceRequest.without.installmentOrderSlot.on.iterate");
                }

                for (final ServiceRequestProperty property : ServiceRequestProperty.find(request, installmentOrderSlot)
                        .collect(Collectors.toSet())) {
                    if (property.getInteger() == null) {
                        continue;
                    }

                    if (!property.getInteger().equals(tuitionInstallmentDebitEntry.getProduct().getTuitionInstallmentOrder())) {
                        continue;
                    }

                    // Found! Get academic treasury event to check if it is charged
                    Optional<? extends AcademicTreasuryEvent> academicServiceRequest = AcademicTreasuryEvent.findUnique(request);

                    if (!academicServiceRequest.isPresent() || !academicServiceRequest.get().isCharged()) {
                        // Charge
                        EmolumentServices.createAcademicServiceRequestEmolumentForDefaultFinantialEntity(request);
                    }

                    continue outer;
                }
            }

            createPenaltyRule(rule, registration, tuitionInstallmentDebitEntry, type, installmentOrderSlot, executionYearSlot);
        }
    }

    private ULisboaServiceRequest createPenaltyRule(final AcademicDebtGenerationRule rule, final Registration registration,
            final DebitEntry tuitionInstallmentDebitEntry, final ServiceRequestType type, final ServiceRequestSlot installmentOrderSlot,
            final ServiceRequestSlot executionYearSlot) {
        ULisboaServiceRequestBean bean = new ULisboaServiceRequestBean();

        bean.setRegistration(registration);
        bean.setServiceRequestType(type);
        bean.setRequestedOnline(false);
        bean.setRequestDate(new DateTime());

        ServiceRequestPropertyBean executionYearPropertyBean = new ServiceRequestPropertyBean();
        executionYearPropertyBean.setCode(executionYearSlot.getCode());
        executionYearPropertyBean.setUiComponentType(executionYearSlot.getUiComponentType());
        executionYearPropertyBean.setLabel(executionYearSlot.getLabel());
        executionYearPropertyBean.setRequired(false);
        executionYearPropertyBean.setDomainObjectValue(rule.getExecutionYear());
        bean.getServiceRequestPropertyBeans().add(executionYearPropertyBean);

        ServiceRequestPropertyBean installmentOrderPropertyBean = new ServiceRequestPropertyBean();
        installmentOrderPropertyBean.setCode(installmentOrderSlot.getCode());
        installmentOrderPropertyBean.setUiComponentType(installmentOrderSlot.getUiComponentType());
        installmentOrderPropertyBean.setLabel(installmentOrderSlot.getLabel());
        installmentOrderPropertyBean.setRequired(false);
        installmentOrderPropertyBean.setIntegerValue(tuitionInstallmentDebitEntry.getProduct().getTuitionInstallmentOrder());
        bean.getServiceRequestPropertyBeans().add(installmentOrderPropertyBean);

        ULisboaServiceRequest serviceRequest = ULisboaServiceRequest.create(bean);

        if (ServiceRequestProperty.find(serviceRequest, installmentOrderSlot).count() == 0) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CreatePenaltyTaxOnLateTuitionPaymentStrategy.serviceRequest.without.installmentOrderSlot.on.creation");
        }

        FenixFramework.atomic(() -> {
            if(AcademicTreasuryEvent.findUnique(serviceRequest).isPresent()) {
                AcademicTreasuryEvent academicTreasuryEvent = AcademicTreasuryEvent.findUnique(serviceRequest).get();
                Map<String, String> eventPropertiesMap = academicTreasuryEvent.getPropertiesMap();
                
                eventPropertiesMap.put(ULisboaSpecificationsUtil.bundle("label.CreatePenaltyTaxOnLateTuitionPaymentStrategy.created.by.academicDebtGenerationRule"), 
                        ULisboaSpecificationsUtil.bundle("label.true"));
                academicTreasuryEvent.editPropertiesMap(eventPropertiesMap);
                
                if(academicTreasuryEvent.isCharged()) {
                    final DebitEntry penaltyDebitEntry = DebitEntry.findActive(academicTreasuryEvent).iterator().next();
                    
                    Map<String, String> debitEntryPropertiesMap = penaltyDebitEntry.getPropertiesMap();
                    debitEntryPropertiesMap.put(ULisboaSpecificationsUtil.bundle("label.CreatePenaltyTaxOnLateTuitionPaymentStrategy.created.by.academicDebtGenerationRule"), 
                            ULisboaSpecificationsUtil.bundle("label.true"));
                    penaltyDebitEntry.editPropertiesMap(debitEntryPropertiesMap);
                }
            }
        });
        
        return serviceRequest;
    }

}
