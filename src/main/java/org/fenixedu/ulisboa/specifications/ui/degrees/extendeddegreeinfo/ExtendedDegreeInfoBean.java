package org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

public class ExtendedDegreeInfoBean implements IBean {

    private ExecutionYear executionYear;
    private List<TupleDataSourceBean> executionYearOptions;
    private Degree degree;
    private List<TupleDataSourceBean> degreeOptions;
    private String degreeType;
    private String degreeAcron;

    // DegreeInfo fields
    private LocalizedString name;
    private LocalizedString description;
    private LocalizedString history;
    private LocalizedString objectives;
    private LocalizedString designedFor;
    private LocalizedString professionalExits; // "Professional Exits"? LOL!!
    private LocalizedString operationalRegime;
    private LocalizedString gratuity;
    private LocalizedString additionalInfo;
    private LocalizedString links;
    private LocalizedString testIngression;
    private LocalizedString classifications;
    private LocalizedString accessRequisites;
    private LocalizedString candidacyDocuments;
    private Integer driftsInitial;
    private Integer driftsFirst;
    private Integer driftsSecond;
    private Double markMin;
    private Double markMax;
    private Double markAverage;
    private LocalizedString qualificationLevel;
    private LocalizedString recognitions;

    // ExtendedDegreeInfo fields
    private LocalizedString scientificAreas;
    private LocalizedString studyRegime;
    private LocalizedString studyProgrammeRequirements;
    private LocalizedString higherEducationAccess;
    private LocalizedString professionalStatus;
    private LocalizedString supplementExtraInformation;
    private LocalizedString supplementOtherSources;

    public ExtendedDegreeInfoBean() {
        setExecutionYear(ExecutionYear.readCurrentExecutionYear());
        setExecutionYearOptions(ExecutionYear.readNotClosedExecutionYears());

        Set<Degree> allDegrees = new TreeSet<Degree>(Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID);
        allDegrees.addAll(Bennu.getInstance().getDegreesSet());
        setDegree(allDegrees.stream().findFirst().orElse(null));
        setDegreeOptions(allDegrees);
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public List<TupleDataSourceBean> getExecutionYearOptions() {
        return executionYearOptions;
    }

    public void setExecutionYearOptions(Collection<ExecutionYear> executionYearOptions) {
        this.executionYearOptions = executionYearOptions.stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).map(ey -> {
            TupleDataSourceBean tupleDataSourceBean = new TupleDataSourceBean();
            tupleDataSourceBean.setId(ey.getExternalId());
            tupleDataSourceBean.setText(ey.getBeginCivilYear() + "/" + ey.getEndCivilYear());
            return tupleDataSourceBean;
        }).collect(Collectors.toList());
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public List<TupleDataSourceBean> getDegreeOptions() {
        return degreeOptions;
    }

    public void setDegreeOptions(Collection<Degree> degreeOptions) {
        this.degreeOptions = degreeOptions.stream().sorted(Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID).map(d -> {
            TupleDataSourceBean tupleDataSourceBean = new TupleDataSourceBean();
            tupleDataSourceBean.setId(d.getExternalId());
            tupleDataSourceBean.setText(d.getDegreeTypeName() + "-" + d.getNameI18N(getExecutionYear()).getContent());
            return tupleDataSourceBean;
        }).collect(Collectors.toList());
    }

    public String getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType;
    }

    public String getDegreeAcron() {
        return degreeAcron;
    }

    public void setDegreeAcron(String degreeAcron) {
        this.degreeAcron = degreeAcron;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    public LocalizedString getHistory() {
        return history;
    }

    public void setHistory(LocalizedString history) {
        this.history = history;
    }

    public LocalizedString getObjectives() {
        return objectives;
    }

    public void setObjectives(LocalizedString objectives) {
        this.objectives = objectives;
    }

    public LocalizedString getDesignedFor() {
        return designedFor;
    }

    public void setDesignedFor(LocalizedString designedFor) {
        this.designedFor = designedFor;
    }

    public LocalizedString getProfessionalExits() {
        return professionalExits;
    }

    public void setProfessionalExits(LocalizedString professionalExits) {
        this.professionalExits = professionalExits;
    }

    public LocalizedString getOperationalRegime() {
        return operationalRegime;
    }

    public void setOperationalRegime(LocalizedString operationalRegime) {
        this.operationalRegime = operationalRegime;
    }

    public LocalizedString getGratuity() {
        return gratuity;
    }

    public void setGratuity(LocalizedString gratuity) {
        this.gratuity = gratuity;
    }

    public LocalizedString getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(LocalizedString additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public LocalizedString getLinks() {
        return links;
    }

    public void setLinks(LocalizedString links) {
        this.links = links;
    }

    public LocalizedString getTestIngression() {
        return testIngression;
    }

    public void setTestIngression(LocalizedString testIngression) {
        this.testIngression = testIngression;
    }

    public LocalizedString getClassifications() {
        return classifications;
    }

    public void setClassifications(LocalizedString classifications) {
        this.classifications = classifications;
    }

    public LocalizedString getAccessRequisites() {
        return accessRequisites;
    }

    public void setAccessRequisites(LocalizedString accessRequisites) {
        this.accessRequisites = accessRequisites;
    }

    public LocalizedString getCandidacyDocuments() {
        return candidacyDocuments;
    }

    public void setCandidacyDocuments(LocalizedString candidacyDocuments) {
        this.candidacyDocuments = candidacyDocuments;
    }

    public Integer getDriftsInitial() {
        return driftsInitial;
    }

    public void setDriftsInitial(Integer driftsInitial) {
        this.driftsInitial = driftsInitial;
    }

    public Integer getDriftsFirst() {
        return driftsFirst;
    }

    public void setDriftsFirst(Integer driftsFirst) {
        this.driftsFirst = driftsFirst;
    }

    public Integer getDriftsSecond() {
        return driftsSecond;
    }

    public void setDriftsSecond(Integer driftsSecond) {
        this.driftsSecond = driftsSecond;
    }

    public Double getMarkMin() {
        return markMin;
    }

    public void setMarkMin(Double markMin) {
        this.markMin = markMin;
    }

    public Double getMarkMax() {
        return markMax;
    }

    public void setMarkMax(Double markMax) {
        this.markMax = markMax;
    }

    public Double getMarkAverage() {
        return markAverage;
    }

    public void setMarkAverage(Double markAverage) {
        this.markAverage = markAverage;
    }

    public LocalizedString getQualificationLevel() {
        return qualificationLevel;
    }

    public void setQualificationLevel(LocalizedString qualificationLevel) {
        this.qualificationLevel = qualificationLevel;
    }

    public LocalizedString getRecognitions() {
        return recognitions;
    }

    public void setRecognitions(LocalizedString recognitions) {
        this.recognitions = recognitions;
    }

    public LocalizedString getScientificAreas() {
        return scientificAreas;
    }

    public void setScientificAreas(LocalizedString scientificAreas) {
        this.scientificAreas = scientificAreas;
    }

    public LocalizedString getStudyRegime() {
        return studyRegime;
    }

    public void setStudyRegime(LocalizedString studyRegime) {
        this.studyRegime = studyRegime;
    }

    public LocalizedString getStudyProgrammeRequirements() {
        return studyProgrammeRequirements;
    }

    public void setStudyProgrammeRequirements(LocalizedString studyProgrammeRequirements) {
        this.studyProgrammeRequirements = studyProgrammeRequirements;
    }

    public LocalizedString getHigherEducationAccess() {
        return higherEducationAccess;
    }

    public void setHigherEducationAccess(LocalizedString higherEducationAccess) {
        this.higherEducationAccess = higherEducationAccess;
    }

    public LocalizedString getProfessionalStatus() {
        return professionalStatus;
    }

    public void setProfessionalStatus(LocalizedString professionalStatus) {
        this.professionalStatus = professionalStatus;
    }

    public LocalizedString getSupplementExtraInformation() {
        return supplementExtraInformation;
    }

    public void setSupplementExtraInformation(LocalizedString supplementExtraInformation) {
        this.supplementExtraInformation = supplementExtraInformation;
    }

    public LocalizedString getSupplementOtherSources() {
        return supplementOtherSources;
    }

    public void setSupplementOtherSources(LocalizedString supplementOtherSources) {
        this.supplementOtherSources = supplementOtherSources;
    }
}
