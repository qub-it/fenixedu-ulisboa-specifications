package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitName;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.domain.ResidenceType;
import org.fenixedu.ulisboa.specifications.domain.SalarySpan;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;

import pt.ist.fenixframework.FenixFramework;

public class HouseholdInformationForm implements CandidancyForm {

    private ExecutionYear executionYear;
    private SchoolLevelType motherSchoolLevel;
    private ProfessionType motherProfessionType;
    private ProfessionalSituationConditionType motherProfessionalCondition;
    private SchoolLevelType fatherSchoolLevel;
    private ProfessionType fatherProfessionType;
    private ProfessionalSituationConditionType fatherProfessionalCondition;
    private SalarySpan householdSalarySpan;
    private ProfessionalSituationConditionType professionalCondition;
    private String profession;
    private ProfessionType professionType;
    private ProfessionTimeType professionTimeType;
    private GrantOwnerType grantOwnerType;
    private String grantOwnerProvider;
    private String otherGrantOwnerProvider;
    private MaritalStatus maritalStatus;
    private Country countryHighSchool;
    private boolean dislocatedFromPermanentResidence;
    private Country countryOfResidence;
    private District permanentResidenceDistrict;
    private DistrictSubdivision permanentResidentDistrictSubdivision;
    private ResidenceType dislocatedResidenceType;
    private List<TupleDataSourceBean> professionalConditionValues;
    private List<TupleDataSourceBean> professionTypeValues;
    private List<TupleDataSourceBean> professionTimeTypeValues;
    private List<TupleDataSourceBean> grantOwnerTypeValues;
    private List<TupleDataSourceBean> grantOwnerProviderValues;
    private String grantOwnerProviderNamePart;
    private List<TupleDataSourceBean> schoolLevelValues;
    private List<TupleDataSourceBean> salarySpanValues;
    private List<TupleDataSourceBean> maritalStatusValues;
    private List<TupleDataSourceBean> residenceTypeValues;
    private List<TupleDataSourceBean> countriesValues;
    private List<TupleDataSourceBean> districtsValues;
    private List<TupleDataSourceBean> districtSubdivisionValues;

    public HouseholdInformationForm() {
        setProfessionalConditionValues(Arrays.asList(ProfessionalSituationConditionType.values()));
        setProfessionTypeValues(Arrays.asList(ProfessionType.values()));
        setProfessionTimeTypeValues(ProfessionTimeType.readAll().collect(Collectors.toList()));
        setGrantOwnerTypeValues(Arrays.asList(GrantOwnerType.values()));
        setSchoolLevelValues(Arrays.asList(SchoolLevelType.values()));
        setSalarySpanValues(SalarySpan.readAll().collect(Collectors.toList()));
        setCountriesValues(Bennu.getInstance().getCountrysSet());
        setResidenceTypeValues(Bennu.getInstance().getResidenceTypesSet());

        List<MaritalStatus> maritalStatusValues = new ArrayList<>();
        maritalStatusValues.addAll(Arrays.asList(MaritalStatus.values()));
        maritalStatusValues.remove(MaritalStatus.UNKNOWN);
        setMaritalStatusValues(maritalStatusValues);

        updateLists();
    }

    @Override
    public void updateLists() {
        Collection<UnitName> units =
                UnitName.findExternalUnit(grantOwnerProviderNamePart == null ? "" : grantOwnerProviderNamePart, 50);
        if (grantOwnerProvider != null) {
            Unit unit = FenixFramework.getDomainObject(grantOwnerProvider);
            units.add(unit.getUnitName());
        }
        setGrantOwnerProviderValues(units.stream().filter(i -> i.getUnit().isNoOfficialExternal()).collect(Collectors.toList()));

        if (countryOfResidence == Country.readDefault()) {
            setDistrictsValues(FormAbstractController.getDistrictsWithSubdivisionsAndParishes().collect(Collectors.toList()));
        }
        if (permanentResidenceDistrict != null) {
            setDistrictSubdivisionValues(
                    FormAbstractController.getSubdivisionsWithParishes(permanentResidenceDistrict).collect(Collectors.toList()));
        }

    }

    public SchoolLevelType getMotherSchoolLevel() {
        return motherSchoolLevel;
    }

    public void setMotherSchoolLevel(SchoolLevelType motherSchoolLevel) {
        this.motherSchoolLevel = motherSchoolLevel;
    }

    public ProfessionType getMotherProfessionType() {
        return motherProfessionType;
    }

    public void setMotherProfessionType(ProfessionType motherProfessionType) {
        this.motherProfessionType = motherProfessionType;
    }

    public ProfessionalSituationConditionType getMotherProfessionalCondition() {
        return motherProfessionalCondition;
    }

    public void setMotherProfessionalCondition(ProfessionalSituationConditionType motherProfessionalCondition) {
        this.motherProfessionalCondition = motherProfessionalCondition;
    }

    public SchoolLevelType getFatherSchoolLevel() {
        return fatherSchoolLevel;
    }

    public void setFatherSchoolLevel(SchoolLevelType fatherSchoolLevel) {
        this.fatherSchoolLevel = fatherSchoolLevel;
    }

    public ProfessionType getFatherProfessionType() {
        return fatherProfessionType;
    }

    public void setFatherProfessionType(ProfessionType fatherProfessionType) {
        this.fatherProfessionType = fatherProfessionType;
    }

    public ProfessionalSituationConditionType getFatherProfessionalCondition() {
        return fatherProfessionalCondition;
    }

    public void setFatherProfessionalCondition(ProfessionalSituationConditionType fatherProfessionalCondition) {
        this.fatherProfessionalCondition = fatherProfessionalCondition;
    }

    public SalarySpan getHouseholdSalarySpan() {
        return householdSalarySpan;
    }

    public void setHouseholdSalarySpan(SalarySpan householdSalarySpan) {
        this.householdSalarySpan = householdSalarySpan;
    }

    public ProfessionType getProfessionType() {
        return professionType;
    }

    public void setProfessionType(ProfessionType professionType) {
        this.professionType = professionType;
    }

    public ProfessionalSituationConditionType getProfessionalCondition() {
        return professionalCondition;
    }

    public void setProfessionalCondition(ProfessionalSituationConditionType professionalCondition) {
        this.professionalCondition = professionalCondition;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public GrantOwnerType getGrantOwnerType() {
        return grantOwnerType;
    }

    public void setGrantOwnerType(GrantOwnerType grantOwnerType) {
        this.grantOwnerType = grantOwnerType;
    }

    public String getGrantOwnerProvider() {
        return grantOwnerProvider;
    }

    public void setGrantOwnerProvider(String grantOwnerProvider) {
        this.grantOwnerProvider = grantOwnerProvider;
    }

    public String getOtherGrantOwnerProvider() {
        return otherGrantOwnerProvider;
    }

    public void setOtherGrantOwnerProvider(String otherGrantOwnerProvider) {
        this.otherGrantOwnerProvider = otherGrantOwnerProvider;
    }

    public ProfessionTimeType getProfessionTimeType() {
        return professionTimeType;
    }

    public void setProfessionTimeType(ProfessionTimeType professionTimeType) {
        this.professionTimeType = professionTimeType;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Country getCountryHighSchool() {
        return countryHighSchool;
    }

    public void setCountryHighSchool(Country countryHighSchool) {
        this.countryHighSchool = countryHighSchool;
    }

    public ResidenceType getDislocatedResidenceType() {
        return dislocatedResidenceType;
    }

    public void setDislocatedResidenceType(ResidenceType dislocatedResidenceType) {
        this.dislocatedResidenceType = dislocatedResidenceType;
    }

    public boolean isStudentWorking() {
        if (isWorkingCondition()) {
            return true;
        }
        if (!StringUtils.isEmpty(getProfession())) {
            return true;
        }
        if (getProfessionTimeType() != null) {
            return true;
        }
        if (isWorkingProfessionType()) {
            return true;
        }
        return false;
    }

    private boolean isWorkingCondition() {
        switch (getProfessionalCondition()) {
        case WORKS_FOR_OTHERS:
            return true;
        case EMPLOYEER:
            return true;
        case INDEPENDENT_WORKER:
            return true;
        case WORKS_FOR_FAMILY_WITHOUT_PAYMENT:
            return true;
        case HOUSEWIFE:
            return true;
        case MILITARY_SERVICE:
            return true;
        default:
            return false;
        }
    }

    private boolean isWorkingProfessionType() {
        switch (getProfessionType()) {
        case UNKNOWN:
            return false;
        case OTHER:
            return false;
        default:
            return true;
        }
    }

    public String getGrantOwnerProviderName() {
        Unit unit = FenixFramework.getDomainObject(getGrantOwnerProvider());
        if (unit == null) {
            return getGrantOwnerProvider();
        } else {
            return unit.getName();
        }
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public boolean isDislocatedFromPermanentResidence() {
        return dislocatedFromPermanentResidence;
    }

    public void setDislocatedFromPermanentResidence(boolean dislocatedFromPermanentResidence) {
        this.dislocatedFromPermanentResidence = dislocatedFromPermanentResidence;
    }

    public Country getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(Country countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public District getPermanentResidenceDistrict() {
        return permanentResidenceDistrict;
    }

    public void setPermanentResidenceDistrict(District permanentResidenceDistrict) {
        this.permanentResidenceDistrict = permanentResidenceDistrict;
    }

    public DistrictSubdivision getPermanentResidentDistrictSubdivision() {
        return permanentResidentDistrictSubdivision;
    }

    public void setPermanentResidentDistrictSubdivision(DistrictSubdivision permanentResidentDistrictSubdivision) {
        this.permanentResidentDistrictSubdivision = permanentResidentDistrictSubdivision;
    }

    public List<TupleDataSourceBean> getProfessionalConditionValues() {
        return professionalConditionValues;
    }

    public void setProfessionalConditionValues(List<ProfessionalSituationConditionType> professionalConditionValues) {
        this.professionalConditionValues = professionalConditionValues.stream()
                .map(psct -> new TupleDataSourceBean(psct.toString(), psct.getLocalizedName()))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getProfessionTypeValues() {
        return professionTypeValues;
    }

    public void setProfessionTypeValues(List<ProfessionType> professionTypeValues) {
        this.professionTypeValues =
                professionTypeValues.stream().map(pt -> new TupleDataSourceBean(pt.toString(), pt.getLocalizedName()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getProfessionTimeTypeValues() {
        return professionTimeTypeValues;
    }

    public void setProfessionTimeTypeValues(List<ProfessionTimeType> professionTimeTypeValues) {
        this.professionTimeTypeValues = professionTimeTypeValues.stream()
                .map(ptt -> new TupleDataSourceBean(ptt.getExternalId(), ptt.getDescription().getContent()))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getGrantOwnerTypeValues() {
        return grantOwnerTypeValues;
    }

    public void setGrantOwnerTypeValues(List<GrantOwnerType> grantOwnerTypeValues) {
        this.grantOwnerTypeValues = grantOwnerTypeValues.stream().map(got -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(got.toString());
            String gotLabel = BundleUtil.getString(BUNDLE, got.getQualifiedName());
            tuple.setText(gotLabel);
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getGrantOwnerProviderValues() {
        return grantOwnerProviderValues;
    }

    public void setGrantOwnerProviderValues(List<UnitName> grantOwnerProviderValues) {
        this.grantOwnerProviderValues = grantOwnerProviderValues.stream().map(un -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(un.getUnit().getExternalId());
            tuple.setText(un.getUnit().getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getSchoolLevelValues() {
        return schoolLevelValues;
    }

    public void setSchoolLevelValues(List<SchoolLevelType> schoolLevelValues) {
        this.schoolLevelValues = schoolLevelValues.stream()
                .map(sl -> new TupleDataSourceBean(sl.toString(), sl.getLocalizedName())).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getSalarySpanValues() {
        return salarySpanValues;
    }

    public void setSalarySpanValues(List<SalarySpan> salarySpanValues) {
        this.salarySpanValues =
                salarySpanValues.stream().map(ss -> new TupleDataSourceBean(ss.getExternalId(), ss.getDescription().getContent()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getMaritalStatusValues() {
        return maritalStatusValues;
    }

    public void setMaritalStatusValues(List<MaritalStatus> maritalStatusValues) {
        this.maritalStatusValues =
                maritalStatusValues.stream().map(ms -> new TupleDataSourceBean(ms.toString(), ms.getLocalizedName()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getResidenceTypeValues() {
        return residenceTypeValues;
    }

    public void setResidenceTypeValues(Collection<ResidenceType> residenceTypeValues) {
        this.residenceTypeValues = residenceTypeValues.stream()
                .map(rt -> new TupleDataSourceBean(rt.getExternalId(), rt.getDescription().getContent()))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getCountriesValues() {
        return countriesValues;
    }

    public void setCountriesValues(Collection<Country> countriesValues) {
        this.countriesValues = countriesValues.stream().map(c -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(c.getExternalId());
            tuple.setText(c.getLocalizedName().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getDistrictsValues() {
        return districtsValues;
    }

    public void setDistrictsValues(Collection<District> districtsValues) {
        this.districtsValues = districtsValues.stream().map(d -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(d.getExternalId());
            tuple.setText(d.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getDistrictSubdivisionValues() {
        return districtSubdivisionValues;
    }

    public void setDistrictSubdivisionValues(Collection<DistrictSubdivision> districtSubdivisionValues) {
        this.districtSubdivisionValues = districtSubdivisionValues.stream().map(ds -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(ds.getExternalId());
            tuple.setText(ds.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }
}
