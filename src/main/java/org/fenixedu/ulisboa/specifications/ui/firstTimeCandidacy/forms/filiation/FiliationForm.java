package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.filiation;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class FiliationForm implements CandidancyForm {

    private Country secondNationality;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private Country countryOfBirth;
    private District districtOfBirth;
    private DistrictSubdivision districtSubdivisionOfBirth;
    private Parish parishOfBirth;
    private String fatherName;
    private String motherName;

    private List<TupleDataSourceBean> nationalitiesValues;
    private List<TupleDataSourceBean> countriesValues;
    private List<TupleDataSourceBean> districtsValues;
    private List<TupleDataSourceBean> districtSubdivisionValues;
    private List<TupleDataSourceBean> parishValues;

    public FiliationForm() {
        updateLists();
    }

    @Override
    public void updateLists() {
        setNationalitiesValues(Bennu.getInstance().getCountrysSet());
        setCountriesValues(Bennu.getInstance().getCountrysSet());

        if (countryOfBirth == Country.readDefault()) {
            setDistrictsValues(FormAbstractController.getDistrictsWithSubdivisionsAndParishes().collect(Collectors.toList()));
        }
        if (districtOfBirth != null) {
            setDistrictSubdivisionValues(
                    FormAbstractController.getSubdivisionsWithParishes(districtOfBirth).collect(Collectors.toList()));
        }
        if (districtSubdivisionOfBirth != null) {
            setParishValues(districtSubdivisionOfBirth.getParishSet());
        }
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Country getNationality() {
        return countryOfBirth;
    }

    public Parish getParishOfBirth() {
        return parishOfBirth;
    }

    public void setParishOfBirth(Parish parishOfBirth) {
        this.parishOfBirth = parishOfBirth;
    }

    public DistrictSubdivision getDistrictSubdivisionOfBirth() {
        return districtSubdivisionOfBirth;
    }

    public void setDistrictSubdivisionOfBirth(DistrictSubdivision districtSubdivisionOfBirth) {
        this.districtSubdivisionOfBirth = districtSubdivisionOfBirth;
    }

    public District getDistrictOfBirth() {
        return districtOfBirth;
    }

    public void setDistrictOfBirth(District districtOfBirth) {
        this.districtOfBirth = districtOfBirth;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public Country getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(Country countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public Country getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(Country secondNationality) {
        this.secondNationality = secondNationality;
    }

    public List<TupleDataSourceBean> getNationalitiesValues() {
        return nationalitiesValues;
    }

    public void setNationalitiesValues(Collection<Country> nationalitiesValues) {
        this.nationalitiesValues = nationalitiesValues.stream().map(c -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(c.getExternalId());
            tuple.setText(c.getCountryNationality().getContent());
            return tuple;
        }).collect(Collectors.toList());
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

    public List<TupleDataSourceBean> getParishValues() {
        return parishValues;
    }

    public void setParishValues(Collection<Parish> parishValues) {
        this.parishValues = parishValues.stream().map(p -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(p.getExternalId());
            tuple.setText(p.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }
}
