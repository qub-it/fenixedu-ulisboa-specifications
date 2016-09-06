package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitName;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.google.common.collect.Lists;

public class PersonalInformationForm implements Serializable, CandidancyForm {
    private static final long serialVersionUID = 1L;

    //can be either the series number or the extra digit
    private String identificationDocumentSeriesNumber;
    private String documentIdEmissionLocation;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate documentIdEmissionDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate documentIdExpirationDate;
    private String socialSecurityNumber;
    private Unit firstOptionInstitution;
    private DegreeDesignation firstOptionDegreeDesignation;
    private String documentIdNumber;
    private IDDocumentType idDocumentType;
    private Country countryHighSchool;
    private boolean firstYearRegistration;
    private boolean foreignStudent;
    private MaritalStatus maritalStatus;

    private List<TupleDataSourceBean> countryHighSchoolValues;
    private List<TupleDataSourceBean> idDocumentTypeValues;
    private List<TupleDataSourceBean> firstOptionInstitutionValues;
    private String institutionNamePart;
    private List<TupleDataSourceBean> firstOptionDegreeDesignationValues;
    private String degreeNamePart;
    private List<TupleDataSourceBean> maritalStatusValues;

    /* Read only */
    private String name;
    private String username;
    private Gender gender;

    public PersonalInformationForm() {
        setCountryHighSchoolValues(Lists.newArrayList(Country.readDistinctCountries()));
        setForeignStudent(getIsForeignStudent());

        List<IDDocumentType> idDocumentTypeValues = new ArrayList<IDDocumentType>();
        idDocumentTypeValues.addAll(Arrays.asList(IDDocumentType.values()));
        idDocumentTypeValues.remove(IDDocumentType.CITIZEN_CARD);
        setIdDocumentTypeValues(idDocumentTypeValues);

        List<MaritalStatus> maritalStatusValues = new ArrayList<>();
        maritalStatusValues.addAll(Arrays.asList(MaritalStatus.values()));
        maritalStatusValues.remove(MaritalStatus.UNKNOWN);
        setMaritalStatusValues(maritalStatusValues);

        updateLists();
    }

    @Override
    public void updateLists() {
        Collection<UnitName> academicUnitList =
                UnitName.findExternalAcademicUnit(institutionNamePart == null ? "" : institutionNamePart, 50);
        setFirstOptionInstitutionValues(Lists.newArrayList(academicUnitList));

        Collection<DegreeDesignation> possibleDesignations;
        if (firstOptionInstitution == null) {
            possibleDesignations = Bennu.getInstance().getDegreeDesignationsSet();
        } else {
            possibleDesignations = firstOptionInstitution.getDegreeDesignationSet();
        }
        Predicate<DegreeDesignation> matchesName = dd -> StringNormalizer.normalize(getFullDescription(dd))
                .contains(StringNormalizer.normalize(degreeNamePart == null ? "" : degreeNamePart));
        setFirstOptionDegreeDesignationValues(
                possibleDesignations.stream().filter(matchesName).limit(50).collect(Collectors.toList()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getDocumentIdNumber() {
        return documentIdNumber;
    }

    public void setDocumentIdNumber(String documentIdNumber) {
        this.documentIdNumber = documentIdNumber;
    }

    public IDDocumentType getIdDocumentType() {
        return idDocumentType;
    }

    public void setIdDocumentType(IDDocumentType idDocumentType) {
        this.idDocumentType = idDocumentType;
    }

    public String getDocumentIdEmissionLocation() {
        return documentIdEmissionLocation;
    }

    public void setDocumentIdEmissionLocation(String documentIdEmissionLocation) {
        this.documentIdEmissionLocation = documentIdEmissionLocation;
    }

    public LocalDate getDocumentIdEmissionDate() {
        return documentIdEmissionDate;
    }

    public void setDocumentIdEmissionDate(LocalDate documentIdEmissionDate) {
        this.documentIdEmissionDate = documentIdEmissionDate;
    }

    public LocalDate getDocumentIdExpirationDate() {
        return documentIdExpirationDate;
    }

    public void setDocumentIdExpirationDate(LocalDate documentIdExpirationDate) {
        this.documentIdExpirationDate = documentIdExpirationDate;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Country getCountryHighSchool() {
        return countryHighSchool;
    }

    public void setCountryHighSchool(Country countryHighSchool) {
        this.countryHighSchool = countryHighSchool;
    }

    public Unit getFirstOptionInstitution() {
        return firstOptionInstitution;
    }

    public void setFirstOptionInstitution(Unit firstOptionInstitution) {
        this.firstOptionInstitution = firstOptionInstitution;
    }

    public DegreeDesignation getFirstOptionDegreeDesignation() {
        return firstOptionDegreeDesignation;
    }

    public void setFirstOptionDegreeDesignation(DegreeDesignation firstOptionDegreeDesignation) {
        this.firstOptionDegreeDesignation = firstOptionDegreeDesignation;
    }

    public boolean getIsForeignStudent() {
        Country nationality = AccessControl.getPerson().getCountry();
        return nationality == null || !nationality.isDefaultCountry();
    }

    public boolean isFirstYearRegistration() {
        return firstYearRegistration;
    }

    public void setFirstYearRegistration(boolean firstYearRegistration) {
        this.firstYearRegistration = firstYearRegistration;
    }

    public String getIdentificationDocumentSeriesNumber() {
        return identificationDocumentSeriesNumber;
    }

    public void setIdentificationDocumentSeriesNumber(String identificationDocumentSeriesNumber) {
        this.identificationDocumentSeriesNumber = identificationDocumentSeriesNumber;
    }

    public List<TupleDataSourceBean> getCountryHighSchoolValues() {
        return countryHighSchoolValues;
    }

    public void setCountryHighSchoolValues(List<Country> countryHighSchoolValues) {
        this.countryHighSchoolValues = countryHighSchoolValues.stream().map((c) -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(c.getExternalId());
            tuple.setText(c.getLocalizedName().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getIdDocumentTypeValues() {
        return idDocumentTypeValues;
    }

    public void setIdDocumentTypeValues(List<IDDocumentType> idDocumentTypeValues) {
        this.idDocumentTypeValues = idDocumentTypeValues.stream().map((idDocType) -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(idDocType.toString());
            tuple.setText(idDocType.getLocalizedName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getFirstOptionInstitutionValues() {
        return firstOptionInstitutionValues;
    }

    public void setFirstOptionInstitutionValues(List<UnitName> firstOptionInstitutionValues) {
        this.firstOptionInstitutionValues = firstOptionInstitutionValues.stream().map(un -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(un.getUnit().getExternalId());
            tuple.setText(un.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public String getInstitutionNamePart() {
        return institutionNamePart;
    }

    public void setInstitutionNamePart(String institutionNamePart) {
        this.institutionNamePart = institutionNamePart;
    }

    public List<TupleDataSourceBean> getFirstOptionDegreeDesignationValues() {
        return firstOptionDegreeDesignationValues;
    }

    public void setFirstOptionDegreeDesignationValues(List<DegreeDesignation> firstOptionDegreeDesignationValues) {
        this.firstOptionDegreeDesignationValues = firstOptionDegreeDesignationValues.stream().map(dd -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(dd.getExternalId());
            tuple.setText(getFullDescription(dd));
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    private String getFullDescription(DegreeDesignation designation) {
        return "[" + designation.getCode() + "] " + designation.getDegreeClassification().getDescription1() + " - "
                + designation.getDescription();
    }

    public String getDegreeNamePart() {
        return degreeNamePart;
    }

    public void setDegreeNamePart(String degreeNamePart) {
        this.degreeNamePart = degreeNamePart;
    }

    public boolean isForeignStudent() {
        return foreignStudent;
    }

    public void setForeignStudent(boolean foreignStudent) {
        this.foreignStudent = foreignStudent;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public List<TupleDataSourceBean> getMaritalStatusValues() {
        return maritalStatusValues;
    }

    public void setMaritalStatusValues(List<MaritalStatus> maritalStatusValues) {
        this.maritalStatusValues =
                maritalStatusValues.stream().map(ms -> new TupleDataSourceBean(ms.toString(), ms.getLocalizedName()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }
}
