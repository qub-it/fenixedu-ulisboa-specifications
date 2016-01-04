package org.fenixedu.ulisboa.specifications.domain.legal.raides.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequestParameters;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

public class RaidesRequestParameter extends LegalReportRequestParameters implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private Unit institution;
    private String institutionCode;
    private String moment;
    private String interlocutorName;
    private String interlocutorEmail;
    private String interlocutorPhone;
    private boolean filterEntriesWithErrors;

    private List<RaidesRequestPeriodParameter> periods = Lists.newArrayList();

    private List<RegistrationProtocol> agreementsForMobility = Lists.newArrayList();
    private List<RegistrationProtocol> agreementsForEnrolled = Lists.newArrayList();

    private List<IngressionType> ingressionsForDegreeChange = Lists.newArrayList();
    private List<IngressionType> ingressionsForDegreeTransfer = Lists.newArrayList();
    private List<IngressionType> ingressionsForGeneralAccessRegime = Lists.newArrayList();

    private List<Degree> degrees = Lists.newArrayList();

    private List<TupleDataSourceBean> registrationProtocolsDataSource;
    private List<TupleDataSourceBean> ingressionTypesDataSource;

    
    /**
     * Used to display data with Angular
     */
    
    private String institutionName;
    
    public RaidesRequestParameter(final String institutionCode, final String moment, final String interlocutorName,
            final String interlocutorEmail, final String interlocutorPhone, final boolean filterEntriesWithErrors) {
        setInstitutionCode(institutionCode);
        setMoment(moment);
        setInterlocutorName(interlocutorName);
        setInstitutionCode(institutionCode);
        setInterlocutorPhone(interlocutorPhone);
        setFilterEntriesWithErrors(filterEntriesWithErrors);

        loadDataSources();
    }

    /**
     * Empty constructor to create in interface
     */
    public RaidesRequestParameter() {
        loadDataSources();
    }

    private void loadDataSources() {
        loadRegistrationProtocolsDataSource();
        loadIngressionTypesDataSource();
    }

    private void loadIngressionTypesDataSource() {
        this.ingressionTypesDataSource = Lists.newArrayList();
        
        this.ingressionTypesDataSource.add(ULisboaConstants.SELECT_OPTION);
        
        this.ingressionTypesDataSource.addAll(Bennu.getInstance().getIngressionTypesSet().stream().map(r -> new TupleDataSourceBean(r.getExternalId(), r.getLocalizedName()))
                        .collect(Collectors.toList()));
        
    }

    private void loadRegistrationProtocolsDataSource() {
        this.registrationProtocolsDataSource = Lists.newArrayList();

        this.registrationProtocolsDataSource.add(ULisboaConstants.SELECT_OPTION);
        this.registrationProtocolsDataSource.addAll(
                Bennu.getInstance().getRegistrationProtocolsSet().stream().sorted(RegistrationProtocol.AGREEMENT_COMPARATOR)
                        .map(r -> new TupleDataSourceBean(r.getExternalId(), r.getDescription().getContent()))
                        .collect(Collectors.toList()));
    }

    public void checkRules() {
        if (getDegrees().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.RaidesReportRequest.degrees.required");
        }

        if (getInstitution() != null) {
            throw new ULisboaSpecificationsDomainException("error.RaidesReportRequest.unit.required");
        }
    }

    public RaidesRequestPeriodParameter addPeriod(final RaidesPeriodInputType periodType, final ExecutionYear academicPeriod,
            final LocalDate begin, final LocalDate end, final boolean enrolledInAcademicPeriod,
            final boolean enrolmentEctsConstraint, final BigDecimal minEnrolmentEcts, final BigDecimal maxEnrolmentEcts,
            final boolean enrolmentYearsConstraint, final Integer minEnrolmentYears, final Integer maxEnrolmentYears) {

        RaidesRequestPeriodParameter periodParameter = new RaidesRequestPeriodParameter(academicPeriod, begin, end,
                enrolledInAcademicPeriod, periodType, enrolmentEctsConstraint, minEnrolmentEcts, maxEnrolmentEcts,
                enrolmentYearsConstraint, minEnrolmentYears, maxEnrolmentYears);

        periods.add(periodParameter);

        return periodParameter;
    }

    public List<RaidesRequestPeriodParameter> getPeriodsForEnrolled() {
        final List<RaidesRequestPeriodParameter> result = Lists.newArrayList();

        for (final RaidesRequestPeriodParameter periodParameter : getPeriods()) {
            if (periodParameter.getPeriodInputType().isForEnrolled()) {
                result.add(periodParameter);
            }
        }

        return result;
    }

    public List<RaidesRequestPeriodParameter> getPeriodsForGraduated() {
        final List<RaidesRequestPeriodParameter> result = Lists.newArrayList();

        for (final RaidesRequestPeriodParameter periodParameter : getPeriods()) {
            if (periodParameter.getPeriodInputType().isForGraduated()) {
                result.add(periodParameter);
            }
        }

        return result;
    }

    public List<RaidesRequestPeriodParameter> getPeriodsForInternationalMobility() {
        final List<RaidesRequestPeriodParameter> result = Lists.newArrayList();

        for (final RaidesRequestPeriodParameter periodParameter : getPeriods()) {
            if (periodParameter.getPeriodInputType().isForInternationalMobility()) {
                result.add(periodParameter);
            }
        }

        return result;
    }

    public void edit(final Unit institution, final String institutionCode, final String moment, final String interlocutorName,
            final String interlocutorEmail, final String interlocutorPhone, final boolean filterEntriesWithErrors) {
        this.institution = institution;
        this.institutionCode = institutionCode;
        this.moment = moment;
        this.interlocutorName = interlocutorName;
        this.interlocutorEmail = interlocutorEmail;
        this.interlocutorPhone = interlocutorPhone;
        this.filterEntriesWithErrors = filterEntriesWithErrors;
    }

    /* *****************
     * GETTERS & SETTERS
     * *****************
     */

    public Unit getInstitution() {
        return institution;
    }

    public void setInstitution(Unit institution) {
        this.institution = institution;
        this.institutionName = institution != null ? institution.getNameI18n().getContent() : "";
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(final String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getMoment() {
        return moment;
    }

    public void setMoment(final String moment) {
        this.moment = moment;
    }

    public String getInterlocutorName() {
        return interlocutorName;
    }

    public void setInterlocutorName(final String interlocutorName) {
        this.interlocutorName = interlocutorName;
    }

    public String getInterlocutorEmail() {
        return interlocutorEmail;
    }

    public void setInterlocutorEmail(String interlocutorEmail) {
        this.interlocutorEmail = interlocutorEmail;
    }

    public String getInterlocutorPhone() {
        return interlocutorPhone;
    }

    public void setInterlocutorPhone(String interlocutorPhone) {
        this.interlocutorPhone = interlocutorPhone;
    }

    public List<RaidesRequestPeriodParameter> getPeriods() {
        return periods;
    }

    public void setPeriods(List<RaidesRequestPeriodParameter> periods) {
        this.periods = periods;
    }

    public List<RegistrationProtocol> getAgreementsForMobility() {
        return agreementsForMobility;
    }

    public void setAgreementsForMobility(List<RegistrationProtocol> agreementsForMobility) {
        this.agreementsForMobility = agreementsForMobility;
    }

    public List<RegistrationProtocol> getAgreementsForEnrolled() {
        return agreementsForEnrolled;
    }

    public void setAgreementsForEnrolled(List<RegistrationProtocol> agreementsForEnrolled) {
        this.agreementsForEnrolled = agreementsForEnrolled;
    }

    public List<IngressionType> getIngressionsForDegreeChange() {
        return ingressionsForDegreeChange;
    }

    public void setIngressionsForDegreeChange(List<IngressionType> ingressionsForDegreeChange) {
        this.ingressionsForDegreeChange = ingressionsForDegreeChange;
    }

    public List<IngressionType> getIngressionsForDegreeTransfer() {
        return ingressionsForDegreeTransfer;
    }

    public void setIngressionsForDegreeTransfer(List<IngressionType> ingressionsForDegreeTransfer) {
        this.ingressionsForDegreeTransfer = ingressionsForDegreeTransfer;
    }

    public List<IngressionType> getIngressionsForGeneralAccessRegime() {
        return ingressionsForGeneralAccessRegime;
    }

    public void setIngressionsForGeneralAccessRegime(List<IngressionType> ingressionsForGeneralAccessRegime) {
        this.ingressionsForGeneralAccessRegime = ingressionsForGeneralAccessRegime;
    }

    public List<Degree> getDegrees() {
        return degrees;
    }

    public void setDegrees(List<Degree> degrees) {
        this.degrees = degrees;
    }

    public boolean isFilterEntriesWithErrors() {
        return filterEntriesWithErrors;
    }

    public void setFilterEntriesWithErrors(boolean filterEntriesWithErrors) {
        this.filterEntriesWithErrors = filterEntriesWithErrors;
    }

}
