package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitName;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.domain.SalarySpan;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

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
    private Boolean dislocatedFromPermanentResidence;
    private Boolean remuneratedActivityInPast;
    private String remuneratedActivityInPastDescription;
    private Boolean currentRemuneratedActivity;
    private String currentRemuneratedActivityDescription;
    private Integer numBrothers;
    private Integer numChildren;
    private Boolean livesAlone;
    private Boolean livesWithMother;
    private Boolean livesWithFather;
    private Boolean livesWithStepFather;
    private Boolean livesWithStepMother;
    private Boolean livesWithBrothers;
    private Boolean livesWithChildren;
    private Boolean livesWithLifemate;
    private Boolean livesWithOthers;
    private String livesWithOthersDesc;
    private Boolean flunkedUniversity;
    private Integer flunkedUniversityTimes;
    private List<TupleDataSourceBean> professionalConditionValues;
    private List<TupleDataSourceBean> professionTypeValues;
    private List<TupleDataSourceBean> professionTimeTypeValues;
    private List<TupleDataSourceBean> grantOwnerTypeValues;
    private List<TupleDataSourceBean> grantOwnerProviderValues;
    private String grantOwnerProviderNamePart;
    private List<TupleDataSourceBean> schoolLevelValues;
    private List<TupleDataSourceBean> salarySpanValues;

    public HouseholdInformationForm() {
        setProfessionalConditionValues(Arrays.asList(ProfessionalSituationConditionType.values()));
        setProfessionTypeValues(Arrays.asList(ProfessionType.values()));
        setProfessionTimeTypeValues(ProfessionTimeType.readAll().collect(Collectors.toList()));
        setGrantOwnerTypeValues(Arrays.asList(GrantOwnerType.values()));
        setSchoolLevelValues(Arrays.asList(SchoolLevelType.values()));
        setSalarySpanValues(SalarySpan.readAll().collect(Collectors.toList()));

        updateLists();
    }

    @Override
    public void updateLists() {
        Collection<UnitName> units =
                UnitName.findExternalUnit(grantOwnerProviderNamePart == null ? "" : grantOwnerProviderNamePart, 50);
        if (grantOwnerProvider != null) {
            Unit unit = FenixFramework.getDomainObject(grantOwnerProvider);
            if (unit != null) {
                units.add(unit.getUnitName());
            }
        } else {
            grantOwnerProvider = otherGrantOwnerProvider;
        }
        setGrantOwnerProviderValues(units.stream().filter(i -> i.getUnit().isNoOfficialExternal()).collect(Collectors.toList()));

    }

    public SchoolLevelType getMotherSchoolLevel() {
        return motherSchoolLevel;
    }

    public void setMotherSchoolLevel(final SchoolLevelType motherSchoolLevel) {
        this.motherSchoolLevel = motherSchoolLevel;
    }

    public ProfessionType getMotherProfessionType() {
        return motherProfessionType;
    }

    public void setMotherProfessionType(final ProfessionType motherProfessionType) {
        this.motherProfessionType = motherProfessionType;
    }

    public ProfessionalSituationConditionType getMotherProfessionalCondition() {
        return motherProfessionalCondition;
    }

    public void setMotherProfessionalCondition(final ProfessionalSituationConditionType motherProfessionalCondition) {
        this.motherProfessionalCondition = motherProfessionalCondition;
    }

    public SchoolLevelType getFatherSchoolLevel() {
        return fatherSchoolLevel;
    }

    public void setFatherSchoolLevel(final SchoolLevelType fatherSchoolLevel) {
        this.fatherSchoolLevel = fatherSchoolLevel;
    }

    public ProfessionType getFatherProfessionType() {
        return fatherProfessionType;
    }

    public void setFatherProfessionType(final ProfessionType fatherProfessionType) {
        this.fatherProfessionType = fatherProfessionType;
    }

    public ProfessionalSituationConditionType getFatherProfessionalCondition() {
        return fatherProfessionalCondition;
    }

    public void setFatherProfessionalCondition(final ProfessionalSituationConditionType fatherProfessionalCondition) {
        this.fatherProfessionalCondition = fatherProfessionalCondition;
    }

    public SalarySpan getHouseholdSalarySpan() {
        return householdSalarySpan;
    }

    public void setHouseholdSalarySpan(final SalarySpan householdSalarySpan) {
        this.householdSalarySpan = householdSalarySpan;
    }

    public ProfessionType getProfessionType() {
        return professionType;
    }

    public void setProfessionType(final ProfessionType professionType) {
        this.professionType = professionType;
    }

    public ProfessionalSituationConditionType getProfessionalCondition() {
        return professionalCondition;
    }

    public void setProfessionalCondition(final ProfessionalSituationConditionType professionalCondition) {
        this.professionalCondition = professionalCondition;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(final String profession) {
        this.profession = profession;
    }

    public GrantOwnerType getGrantOwnerType() {
        return grantOwnerType;
    }

    public void setGrantOwnerType(final GrantOwnerType grantOwnerType) {
        this.grantOwnerType = grantOwnerType;
    }

    public String getGrantOwnerProvider() {
        return grantOwnerProvider;
    }

    public void setGrantOwnerProvider(final String grantOwnerProvider) {
        this.grantOwnerProvider = grantOwnerProvider;
    }

    public String getOtherGrantOwnerProvider() {
        return otherGrantOwnerProvider;
    }

    public void setOtherGrantOwnerProvider(final String otherGrantOwnerProvider) {
        this.otherGrantOwnerProvider = otherGrantOwnerProvider;
    }

    public Boolean getDislocatedFromPermanentResidence() {
        return dislocatedFromPermanentResidence != null && dislocatedFromPermanentResidence;
    }

    public void setDislocatedFromPermanentResidence(final Boolean dislocatedFromPermanentResidence) {
        this.dislocatedFromPermanentResidence = dislocatedFromPermanentResidence;
    }

    public Boolean getRemuneratedActivityInPast() {
        return remuneratedActivityInPast;
    }

    public void setRemuneratedActivityInPast(final Boolean remuneratedActivityInPast) {
        this.remuneratedActivityInPast = remuneratedActivityInPast;
    }

    public String getRemuneratedActivityInPastDescription() {
        return remuneratedActivityInPastDescription;
    }

    public void setRemuneratedActivityInPastDescription(final String remuneratedActivityInPastDescription) {
        this.remuneratedActivityInPastDescription = remuneratedActivityInPastDescription;
    }

    public Boolean getCurrentRemuneratedActivity() {
        return currentRemuneratedActivity;
    }

    public void setCurrentRemuneratedActivity(final Boolean currentRemuneratedActivity) {
        this.currentRemuneratedActivity = currentRemuneratedActivity;
    }

    public String getCurrentRemuneratedActivityDescription() {
        return currentRemuneratedActivityDescription;
    }

    public void setCurrentRemuneratedActivityDescription(final String currentRemuneratedActivityDescription) {
        this.currentRemuneratedActivityDescription = currentRemuneratedActivityDescription;
    }

    public Integer getNumBrothers() {
        return numBrothers;
    }

    public void setNumBrothers(final Integer numBrothers) {
        this.numBrothers = numBrothers;
    }

    public Integer getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(final Integer numChildren) {
        this.numChildren = numChildren;
    }

    public Boolean getLivesAlone() {
        return livesAlone;
    }

    public void setLivesAlone(final Boolean livesAlone) {
        this.livesAlone = livesAlone;
    }

    public Boolean getLivesWithMother() {
        return livesWithMother;
    }

    public void setLivesWithMother(final Boolean livesWithMother) {
        this.livesWithMother = livesWithMother;
    }

    public Boolean getLivesWithFather() {
        return livesWithFather;
    }

    public void setLivesWithFather(final Boolean livesWithFather) {
        this.livesWithFather = livesWithFather;
    }

    public Boolean getLivesWithStepFather() {
        return livesWithStepFather;
    }

    public void setLivesWithStepFather(final Boolean livesWithStepFather) {
        this.livesWithStepFather = livesWithStepFather;
    }

    public Boolean getLivesWithStepMother() {
        return livesWithStepMother;
    }

    public void setLivesWithStepMother(final Boolean livesWithStepMother) {
        this.livesWithStepMother = livesWithStepMother;
    }

    public Boolean getLivesWithBrothers() {
        return livesWithBrothers;
    }

    public void setLivesWithBrothers(final Boolean livesWithBrothers) {
        this.livesWithBrothers = livesWithBrothers;
    }

    public Boolean getLivesWithChildren() {
        return livesWithChildren;
    }

    public void setLivesWithChildren(final Boolean livesWithChildren) {
        this.livesWithChildren = livesWithChildren;
    }

    public Boolean getLivesWithLifemate() {
        return livesWithLifemate;
    }

    public void setLivesWithLifemate(final Boolean livesWithLifemate) {
        this.livesWithLifemate = livesWithLifemate;
    }

    public Boolean getLivesWithOthers() {
        return livesWithOthers;
    }

    public void setLivesWithOthers(final Boolean livesWithOthers) {
        this.livesWithOthers = livesWithOthers;
    }

    public String getLivesWithOthersDesc() {
        return livesWithOthersDesc;
    }

    public void setLivesWithOthersDesc(final String livesWithOthersDesc) {
        this.livesWithOthersDesc = livesWithOthersDesc;
    }

    public Boolean getFlunkedUniversity() {
        return flunkedUniversity;
    }

    public void setFlunkedUniversity(final Boolean flunkedUniversity) {
        this.flunkedUniversity = flunkedUniversity;
    }

    public Integer getFlunkedUniversityTimes() {
        return flunkedUniversityTimes;
    }

    public void setFlunkedUniversityTimes(final Integer flunkedUniversityTimes) {
        this.flunkedUniversityTimes = flunkedUniversityTimes;
    }

    public ProfessionTimeType getProfessionTimeType() {
        return professionTimeType;
    }

    public void setProfessionTimeType(final ProfessionTimeType professionTimeType) {
        this.professionTimeType = professionTimeType;
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
        if (getProfessionalCondition() == null) {
            return false;
        }
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
        if (getProfessionType() == null) {
            return false;
        }
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

    public void setExecutionYear(final ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public List<TupleDataSourceBean> getProfessionalConditionValues() {
        return professionalConditionValues;
    }

    public void setProfessionalConditionValues(final List<ProfessionalSituationConditionType> professionalConditionValues) {
        this.professionalConditionValues = professionalConditionValues.stream()
                .map(psct -> new TupleDataSourceBean(psct.toString(), psct.getLocalizedName())).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getProfessionTypeValues() {
        return professionTypeValues;
    }

    public void setProfessionTypeValues(final List<ProfessionType> professionTypeValues) {
        this.professionTypeValues = professionTypeValues.stream()
                .map(pt -> new TupleDataSourceBean(pt.toString(), pt.getLocalizedName())).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getProfessionTimeTypeValues() {
        return professionTimeTypeValues;
    }

    public void setProfessionTimeTypeValues(final List<ProfessionTimeType> professionTimeTypeValues) {
        this.professionTimeTypeValues = professionTimeTypeValues.stream()
                .map(ptt -> new TupleDataSourceBean(ptt.getExternalId(), ptt.getDescription().getContent()))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getGrantOwnerTypeValues() {
        return grantOwnerTypeValues;
    }

    public void setGrantOwnerTypeValues(final List<GrantOwnerType> grantOwnerTypeValues) {
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

    public void setGrantOwnerProviderValues(final List<UnitName> grantOwnerProviderValues) {
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

    public void setSchoolLevelValues(final List<SchoolLevelType> schoolLevelValues) {
        this.schoolLevelValues = schoolLevelValues.stream()
                .map(sl -> new TupleDataSourceBean(sl.toString(), sl.getLocalizedName())).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getSalarySpanValues() {
        return salarySpanValues;
    }

    public void setSalarySpanValues(final List<SalarySpan> salarySpanValues) {
        this.salarySpanValues =
                salarySpanValues.stream().map(ss -> new TupleDataSourceBean(ss.getExternalId(), ss.getDescription().getContent()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

}
