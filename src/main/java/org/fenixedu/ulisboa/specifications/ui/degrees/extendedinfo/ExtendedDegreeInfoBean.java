package org.fenixedu.ulisboa.specifications.ui.degrees.extendedinfo;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.IBean;
import org.fenixedu.commons.i18n.LocalizedString;

public class ExtendedDegreeInfoBean implements IBean {

    private ExecutionYear executionYear;
    private Degree degree;
    private String degreeType;
    private String degreeAcron;
    private String degreeSitePublicUrl;
    private String degreeSiteManagementUrl;
    private String auditInfo;

    // DegreeInfo fields
    private LocalizedString name;
    private LocalizedString description;
    private LocalizedString history;
    private LocalizedString objectives;
    private LocalizedString designedFor;
    private LocalizedString professionalExits;
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
    private LocalizedString prevailingScientificArea;

    // ExtendedDegreeInfo fields
    private LocalizedString scientificAreas;
    private LocalizedString studyProgrammeDuration;
    private LocalizedString studyRegime;
    private LocalizedString studyProgrammeRequirements;
    private LocalizedString higherEducationAccess;
    private LocalizedString professionalStatus;
    private LocalizedString supplementExtraInformation;
    private LocalizedString supplementOtherSources;

    public ExtendedDegreeInfoBean() {
    }

    public ExtendedDegreeInfoBean(DegreeInfo degreeInfo) {

    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
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

    public String getDegreeSitePublicUrl() {
        return degreeSitePublicUrl;
    }

    public void setDegreeSitePublicUrl(String degreeSitePublicUrl) {
        this.degreeSitePublicUrl = degreeSitePublicUrl;
    }

    public String getDegreeSiteManagementUrl() {
        return degreeSiteManagementUrl;
    }

    public void setDegreeSiteManagementUrl(String degreeSiteManagementUrl) {
        this.degreeSiteManagementUrl = degreeSiteManagementUrl;
    }

    public String getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(String auditInfo) {
        this.auditInfo = auditInfo;
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

    public LocalizedString getPrevailingScientificArea() {
        return prevailingScientificArea;
    }

    public void setPrevailingScientificArea(LocalizedString prevailingScientificArea) {
        this.prevailingScientificArea = prevailingScientificArea;
    }

    public LocalizedString getScientificAreas() {
        return scientificAreas;
    }

    public void setScientificAreas(LocalizedString scientificAreas) {
        this.scientificAreas = scientificAreas;
    }

    public LocalizedString getStudyProgrammeDuration() {
        return studyProgrammeDuration;
    }

    public void setStudyProgrammeDuration(LocalizedString studyProgrammeDuration) {
        this.studyProgrammeDuration = studyProgrammeDuration;
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

    public void updateLists() {
    }
}
