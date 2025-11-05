package org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo;

import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.dml.DynamicField;
import org.fenixedu.commons.i18n.LocalizedString;

public record DegreeInfoRecord(DegreeInfo degreeInfo) {

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

    public LocalizedString getQualificationLevel() {
        return DynamicField.getFieldValue(degreeInfo, DegreeInfo.QUALIFICATION_LEVEL);
    }
}
