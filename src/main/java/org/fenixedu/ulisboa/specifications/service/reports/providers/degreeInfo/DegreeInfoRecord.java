package org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.dml.DynamicField;
import org.fenixedu.commons.i18n.LocalizedString;

public record DegreeInfoRecord(DegreeInfo degreeInfo) {

    public LocalizedString getDescription() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.DESCRIPTION);
    }

    public LocalizedString getHistory() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.HISTORY);
    }

    public LocalizedString getObjectives() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.OBJECTIVES);
    }

    public LocalizedString getDesignedFor() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.DESIGNED_FOR);
    }

    public LocalizedString getProfessionalExits() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.PROFESSIONAL_EXITS);
    }

    public LocalizedString getOperationalRegime() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.OPERATIONAL_REGIME);
    }

    public LocalizedString getGratuity() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.GRATUITY);
    }

    public LocalizedString getAdditionalInfo() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.ADDITIONAL_INFO);
    }

    public LocalizedString getLearningLanguages() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.LEARNING_LANGUAGES);
    }

    public LocalizedString getLinks() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.LINKS);
    }

    public LocalizedString getTestIngression() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.TEST_INGRESSION);
    }

    public LocalizedString getClassifications() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.CLASSIFICATIONS);
    }

    public LocalizedString getAccessRequisites() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.ACCESS_REQUISITES);
    }

    public LocalizedString getCandidacyDocuments() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.CANDIDACY_DOCUMENTS);
    }

    public int getDriftsInitial() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.DRIFTS_INITIAL);
    }

    public int getDriftsFirst() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.DRIFTS_FIRST);
    }

    public int getDriftsSecond() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.DRIFTS_SECOND);
    }

    public BigDecimal getMarkMin() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.MARK_MIN);
    }

    public BigDecimal getMarkMax() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.MARK_MAX);
    }

    public BigDecimal getMarkAverage() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.MARK_AVERAGE);
    }

    public LocalizedString getQualificationLevel() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.QUALIFICATION_LEVEL);
    }

    public LocalizedString getRecognitions() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.RECOGNITIONS);
    }

    public LocalizedString getPrevailingScientificArea() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.PREVAILING_SCIENTIFIC_AREA);
    }

    public LocalizedString getScientificAreas() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.SCIENTIFIC_AREAS);
    }

    public LocalizedString getStudyProgrammeDuration() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.STUDY_PROGRAMME_DURATION);
    }

    public LocalizedString getStudyRegime() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.STUDY_REGIME);
    }

    public LocalizedString getStudyProgrammeRequirements() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.STUDY_PROGRAMME_REQUIREMENTS);
    }

    public LocalizedString getHigherEducationAccess() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.HIGHER_EDUCATION_ACCESS);
    }

    public LocalizedString getProfessionalStatus() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.PROFESSIONAL_STATUS);
    }

    public LocalizedString getSupplementExtraInformation() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.SUPPLEMENT_EXTRA_INFORMATION);
    }

    public LocalizedString getSupplementOtherSources() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.SUPPLEMENT_OTHER_SOURCES);
    }
}
