package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.SalarySpan;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

public class HouseholdInformationForm implements CandidancyForm {

    private ExecutionYear executionYear;
    private SchoolLevelType motherSchoolLevel;
    private ProfessionType motherProfessionType;
    private ProfessionalSituationConditionType motherProfessionalCondition;
    private SchoolLevelType fatherSchoolLevel;
    private ProfessionType fatherProfessionType;
    private ProfessionalSituationConditionType fatherProfessionalCondition;
    private SalarySpan householdSalarySpan;
    private Boolean dislocatedFromPermanentResidence;
    private Integer numBrothers;
    private Integer numChildren;
    private Boolean livesAlone;
    private Boolean livesWithParents;
    private Boolean livesWithBrothers;
    private Boolean livesWithChildren;
    private Boolean livesWithLifemate;
    private Boolean livesWithOthers;
    private String livesWithOthersDesc;
    private List<TupleDataSourceBean> schoolLevelValues;
    private List<TupleDataSourceBean> professionalConditionValues;
    private List<TupleDataSourceBean> professionTypeValues;
    private List<TupleDataSourceBean> salarySpanValues;
    private Boolean flunkedUniversity;
    private Integer flunkedUniversityTimes;
    private GrantOwnerType grantOwnerType;
    private List<TupleDataSourceBean> grantOwnerTypeValues;

    public HouseholdInformationForm() {
        setProfessionalConditionValues(Arrays.asList(ProfessionalSituationConditionType.values()));
        setProfessionTypeValues(Arrays.asList(ProfessionType.values()));
        setSchoolLevelValues(Arrays.asList(SchoolLevelType.values()));
        setSalarySpanValues(SalarySpan.readAll().sorted().collect(Collectors.toList()));
        setGrantOwnerTypeValues(Arrays.asList(GrantOwnerType.values()));

        updateLists();
    }

    @Override
    public void updateLists() {

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

    public Boolean getDislocatedFromPermanentResidence() {
        return dislocatedFromPermanentResidence != null && dislocatedFromPermanentResidence;
    }

    public void setDislocatedFromPermanentResidence(final Boolean dislocatedFromPermanentResidence) {
        this.dislocatedFromPermanentResidence = dislocatedFromPermanentResidence;
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

    public Boolean getLivesWithParents() {
        return livesWithParents;
    }

    public void setLivesWithParents(Boolean livesWithParents) {
        this.livesWithParents = livesWithParents;
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

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(final ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public List<TupleDataSourceBean> getSchoolLevelValues() {
        return schoolLevelValues;
    }

    public void setSchoolLevelValues(final List<SchoolLevelType> schoolLevelValues) {
        this.schoolLevelValues = schoolLevelValues.stream()
                .map(sl -> new TupleDataSourceBean(sl.toString(), sl.getLocalizedName())).collect(Collectors.toList());
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

    public List<TupleDataSourceBean> getSalarySpanValues() {
        return salarySpanValues;
    }

    public void setSalarySpanValues(final List<SalarySpan> salarySpanValues) {
        //Use salary span sort
        this.salarySpanValues =
                salarySpanValues.stream().map(ss -> new TupleDataSourceBean(ss.getExternalId(), ss.getDescription().getContent()))
                        .collect(Collectors.toList());
    }

    public GrantOwnerType getGrantOwnerType() {
        return grantOwnerType;
    }

    public void setGrantOwnerType(final GrantOwnerType grantOwnerType) {
        this.grantOwnerType = grantOwnerType;
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
}
