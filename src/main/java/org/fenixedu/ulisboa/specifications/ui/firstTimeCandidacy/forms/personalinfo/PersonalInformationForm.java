package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification.OriginInformationForm;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.google.common.base.Strings;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private Unit firstOptionInstitution;
    private DegreeDesignation firstOptionDegreeDesignation;
    private String documentIdNumber;
    private IDDocumentType idDocumentType;
    private Country countryHighSchool;
    private boolean firstYearRegistration;
    private boolean foreignStudent;
    private MaritalStatus maritalStatus;
    private Gender gender;

    private List<TupleDataSourceBean> countryHighSchoolValues;
    private List<TupleDataSourceBean> idDocumentTypeValues;
    private List<TupleDataSourceBean> firstOptionInstitutionValues;
    private String institutionNamePart;
    private List<TupleDataSourceBean> firstOptionDegreeDesignationValues;
    private String degreeNamePart;
    private List<TupleDataSourceBean> maritalStatusValues;
    private List<TupleDataSourceBean> genderValues;

    private List<TupleDataSourceBean> fiscalCountryValues;

    /* Read only */
    private String name;
    private String username;

    public PersonalInformationForm() {
        this(true);
    }

    public PersonalInformationForm(boolean initDTOs) {
        if (initDTOs) {
            setFiscalCountryValues(Lists.newArrayList(Country.readDistinctCountries()));
            setCountryHighSchoolValues(Lists.newArrayList(Country.readDistinctCountries()));
            setForeignStudent(getIsForeignStudent());

            List<IDDocumentType> idDocumentTypeValues = new ArrayList<>();
            idDocumentTypeValues.addAll(Arrays.asList(IDDocumentType.values()));
            idDocumentTypeValues.remove(IDDocumentType.CITIZEN_CARD);
            setIdDocumentTypeValues(idDocumentTypeValues);

            List<MaritalStatus> maritalStatusValues = new ArrayList<>();
            maritalStatusValues.addAll(Arrays.asList(MaritalStatus.values()));
            maritalStatusValues.remove(MaritalStatus.UNKNOWN);
            setMaritalStatusValues(maritalStatusValues);

            List<Gender> genderValues = new ArrayList<>();
            genderValues.addAll(Arrays.asList(Gender.values()));
            setGenderValues(genderValues);

            updateLists();
        }
    }

    @Override
    public void updateLists() {
        Set<Unit> units = new HashSet<>();
        Collection<DegreeDesignation> possibleDesignations = new HashSet<>();

        if (firstOptionInstitution != null) {
            units.add(firstOptionInstitution);
            possibleDesignations = firstOptionInstitution.getDegreeDesignationSet();
        } else {
            possibleDesignations = Bennu.getInstance().getDegreeDesignationsSet();
        }

        if (firstOptionDegreeDesignation != null) {
            setDegreeNamePart(getFullDescription(firstOptionDegreeDesignation));
        }

        units.addAll(OriginInformationForm.findExternalAcademicUnit(institutionNamePart == null ? "" : institutionNamePart, 50)
                .stream().map(i -> i.getUnit()).filter(i -> !i.getDegreeDesignationSet().isEmpty()).collect(Collectors.toSet()));

        setFirstOptionInstitutionValues(units);
        Predicate<DegreeDesignation> matchesName = dd -> StringNormalizer.normalize(getFullDescription(dd))
                .contains(StringNormalizer.normalize(degreeNamePart == null ? "" : degreeNamePart));
        setFirstOptionDegreeDesignationValues(
                possibleDesignations.stream().filter(matchesName).limit(50).collect(Collectors.toList()));
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    public String getDocumentIdNumber() {
        return documentIdNumber;
    }

    public void setDocumentIdNumber(final String documentIdNumber) {
        this.documentIdNumber = documentIdNumber;
    }

    public IDDocumentType getIdDocumentType() {
        return idDocumentType;
    }

    public void setIdDocumentType(final IDDocumentType idDocumentType) {
        this.idDocumentType = idDocumentType;
    }

    public String getDocumentIdEmissionLocation() {
        return documentIdEmissionLocation;
    }

    public void setDocumentIdEmissionLocation(final String documentIdEmissionLocation) {
        this.documentIdEmissionLocation = documentIdEmissionLocation;
    }

    public LocalDate getDocumentIdEmissionDate() {
        return documentIdEmissionDate;
    }

    public void setDocumentIdEmissionDate(final LocalDate documentIdEmissionDate) {
        this.documentIdEmissionDate = documentIdEmissionDate;
    }

    public LocalDate getDocumentIdExpirationDate() {
        return documentIdExpirationDate;
    }

    public void setDocumentIdExpirationDate(final LocalDate documentIdExpirationDate) {
        this.documentIdExpirationDate = documentIdExpirationDate;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Country getCountryHighSchool() {
        return countryHighSchool;
    }

    public void setCountryHighSchool(final Country countryHighSchool) {
        this.countryHighSchool = countryHighSchool;
    }

    public Unit getFirstOptionInstitution() {
        return firstOptionInstitution;
    }

    public void setFirstOptionInstitution(final Unit firstOptionInstitution) {
        this.firstOptionInstitution = firstOptionInstitution;
    }

    public DegreeDesignation getFirstOptionDegreeDesignation() {
        return firstOptionDegreeDesignation;
    }

    public void setFirstOptionDegreeDesignation(final DegreeDesignation firstOptionDegreeDesignation) {
        this.firstOptionDegreeDesignation = firstOptionDegreeDesignation;
    }

    public boolean getIsForeignStudent() {
        Country nationality = AccessControl.getPerson().getCountry();
        return nationality == null || !nationality.isDefaultCountry();
    }

    public boolean isFirstYearRegistration() {
        return firstYearRegistration;
    }

    public void setFirstYearRegistration(final boolean firstYearRegistration) {
        this.firstYearRegistration = firstYearRegistration;
    }

    public String getIdentificationDocumentSeriesNumber() {
        return identificationDocumentSeriesNumber;
    }

    public void setIdentificationDocumentSeriesNumber(final String identificationDocumentSeriesNumber) {
        this.identificationDocumentSeriesNumber = identificationDocumentSeriesNumber;
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

    public List<TupleDataSourceBean> getFiscalCountryValues() {
        return fiscalCountryValues;
    }

    public void setFiscalCountryValues(final List<Country> countryHighSchoolValues) {
        this.fiscalCountryValues = countryHighSchoolValues.stream().map((c) -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(c.getExternalId());
            tuple.setText(c.getLocalizedName().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getIdDocumentTypeValues() {
        return idDocumentTypeValues;
    }

    public void setIdDocumentTypeValues(final List<IDDocumentType> idDocumentTypeValues) {
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

    public void setFirstOptionInstitutionValues(final Collection<Unit> firstOptionInstitutionValues) {
        this.firstOptionInstitutionValues = firstOptionInstitutionValues.stream().map(u -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(u.getExternalId());
            String code = !Strings.isNullOrEmpty(u.getCode()) ? "[" + u.getCode() + "]" : "";
            tuple.setText(code + " " + u.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public String getInstitutionNamePart() {
        return institutionNamePart;
    }

    public void setInstitutionNamePart(final String institutionNamePart) {
        this.institutionNamePart = institutionNamePart;
    }

    public List<TupleDataSourceBean> getFirstOptionDegreeDesignationValues() {
        return firstOptionDegreeDesignationValues;
    }

    public void setFirstOptionDegreeDesignationValues(final List<DegreeDesignation> firstOptionDegreeDesignationValues) {
        this.firstOptionDegreeDesignationValues = firstOptionDegreeDesignationValues.stream().map(dd -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(dd.getExternalId());
            tuple.setText(getFullDescription(dd));
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    private String getFullDescription(final DegreeDesignation designation) {
        return "[" + designation.getCode() + "] " + designation.getDegreeClassification().getDescription1() + " - "
                + designation.getDescription();
    }

    public String getDegreeNamePart() {
        return degreeNamePart;
    }

    public void setDegreeNamePart(final String degreeNamePart) {
        this.degreeNamePart = degreeNamePart;
    }

    public boolean isForeignStudent() {
        return foreignStudent;
    }

    public void setForeignStudent(final boolean foreignStudent) {
        this.foreignStudent = foreignStudent;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(final MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public List<TupleDataSourceBean> getMaritalStatusValues() {
        return maritalStatusValues;
    }

    public void setMaritalStatusValues(final List<MaritalStatus> maritalStatusValues) {
        this.maritalStatusValues =
                maritalStatusValues.stream().map(ms -> new TupleDataSourceBean(ms.toString(), ms.getLocalizedName()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getGenderValues() {
        return genderValues;
    }

    public void setGenderValues(final List<Gender> genderValues) {
        this.genderValues = genderValues.stream().map(g -> new TupleDataSourceBean(g.toString(), g.getLocalizedName()))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

}
