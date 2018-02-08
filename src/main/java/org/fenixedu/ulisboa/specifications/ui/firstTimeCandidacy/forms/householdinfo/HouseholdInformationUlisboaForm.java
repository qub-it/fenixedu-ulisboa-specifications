package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

public class HouseholdInformationUlisboaForm implements CandidancyForm {

    private Boolean flunkedBeforeUniversity;
    private Boolean flunkedHighSchool;
    private Integer flunkedHighSchoolTimes;
    private Boolean flunkedPreHighSchool;
    private Integer flunkedPreHighSchoolTimes;
    private Boolean socialBenefitsInHighSchool;
    private String socialBenefitsInHighSchoolDescription;
    private Boolean firstTimeInPublicUniv;
    private Integer publicUnivCandidacies;
    private Boolean firstTimeInUlisboa;
    private String bestQualitiesInThisCicle;

    public HouseholdInformationUlisboaForm() {
        updateLists();
    }

    @Override
    public void updateLists() {
    }

    public Boolean getFlunkedBeforeUniversity() {
        return flunkedBeforeUniversity;
    }

    public void setFlunkedBeforeUniversity(final Boolean flunkedBeforeUniversity) {
        this.flunkedBeforeUniversity = flunkedBeforeUniversity;
    }

    public Boolean getFlunkedHighSchool() {
        return flunkedHighSchool;
    }

    public void setFlunkedHighSchool(final Boolean flunkedHighSchool) {
        this.flunkedHighSchool = flunkedHighSchool;
    }

    public Integer getFlunkedHighSchoolTimes() {
        return flunkedHighSchoolTimes;
    }

    public void setFlunkedHighSchoolTimes(final Integer flunkedHighSchoolTimes) {
        this.flunkedHighSchoolTimes = flunkedHighSchoolTimes;
    }

    public Boolean getFlunkedPreHighSchool() {
        return flunkedPreHighSchool;
    }

    public void setFlunkedPreHighSchool(final Boolean flunkedPreHighSchool) {
        this.flunkedPreHighSchool = flunkedPreHighSchool;
    }

    public Integer getFlunkedPreHighSchoolTimes() {
        return flunkedPreHighSchoolTimes;
    }

    public void setFlunkedPreHighSchoolTimes(final Integer flunkedPreHighSchoolTimes) {
        this.flunkedPreHighSchoolTimes = flunkedPreHighSchoolTimes;
    }

    public Boolean getSocialBenefitsInHighSchool() {
        return socialBenefitsInHighSchool;
    }

    public void setSocialBenefitsInHighSchool(final Boolean socialBenefitsInHighSchool) {
        this.socialBenefitsInHighSchool = socialBenefitsInHighSchool;
    }

    public String getSocialBenefitsInHighSchoolDescription() {
        return socialBenefitsInHighSchoolDescription;
    }

    public void setSocialBenefitsInHighSchoolDescription(final String socialBenefitsInHighSchoolDescription) {
        this.socialBenefitsInHighSchoolDescription = socialBenefitsInHighSchoolDescription;
    }

    public Boolean getFirstTimeInPublicUniv() {
        return firstTimeInPublicUniv;
    }

    public void setFirstTimeInPublicUniv(final Boolean firstTimeInPublicUniv) {
        this.firstTimeInPublicUniv = firstTimeInPublicUniv;
    }

    public Boolean getFirstTimeInUlisboa() {
        return firstTimeInUlisboa;
    }

    public void setFirstTimeInUlisboa(final Boolean firstTimeInUlisboa) {
        this.firstTimeInUlisboa = firstTimeInUlisboa;
    }

    public Integer getPublicUnivCandidacies() {
        return publicUnivCandidacies;
    }

    public void setPublicUnivCandidacies(final Integer publicUnivCandidacies) {
        this.publicUnivCandidacies = publicUnivCandidacies;
    }

    public String getBestQualitiesInThisCicle() {
        return bestQualitiesInThisCicle;
    }

    public void setBestQualitiesInThisCicle(final String bestQualitiesInThisCicle) {
        this.bestQualitiesInThisCicle = bestQualitiesInThisCicle;
    }

}
