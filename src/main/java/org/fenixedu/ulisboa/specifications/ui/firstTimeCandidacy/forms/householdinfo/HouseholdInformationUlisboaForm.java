package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

import com.google.common.collect.Lists;

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
    private GrantOwnerType grantOwnerType;
    private List<TupleDataSourceBean> grantOwnerTypeValues;
    private Boolean flunkedUniversity;
    private Integer flunkedUniversityTimes;
    private Country countryHighSchool;
    private List<TupleDataSourceBean> countryHighSchoolValues;

    public HouseholdInformationUlisboaForm() {
        this(true);
    }

    public HouseholdInformationUlisboaForm(boolean initDTOs) {
        if (initDTOs) {
            setGrantOwnerTypeValues(Arrays.asList(GrantOwnerType.values()));
            setCountryHighSchoolValues(Lists.newArrayList(Country.readDistinctCountries()));

            updateLists();
        }
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

    public Country getCountryHighSchool() {
        return countryHighSchool;
    }

    public void setCountryHighSchool(final Country countryHighSchool) {
        this.countryHighSchool = countryHighSchool;
    }

    public List<TupleDataSourceBean> getCountryHighSchoolValues() {
        return countryHighSchoolValues;
    }

    public void setCountryHighSchoolValues(final List<Country> countryHighSchoolValues) {
        this.countryHighSchoolValues = countryHighSchoolValues.stream().map((c) -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(c.getExternalId());
            tuple.setText(c.getLocalizedName().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

}
