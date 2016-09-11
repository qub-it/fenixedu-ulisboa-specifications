package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import pt.ist.fenixframework.FenixFramework;

public class PreviousDegreeInformationForm implements CandidancyForm {

    private SchoolLevelType precedentSchoolLevel;
    private String otherPrecedentSchoolLevel;
    private String precedentDegreeDesignation;
    private String precedentInstitutionOid;
    private String precedentInstitutionName;
    private DegreeDesignation raidesPrecedentDegreeDesignation;
    private Country precedentCountry;
    private int numberOfEnrolmentsInPreviousDegrees;

    private List<TupleDataSourceBean> schoolLevelValues;
    private List<TupleDataSourceBean> countriesValues;
    private List<TupleDataSourceBean> institutionValues;
    private String institutionNamePart;
    private List<TupleDataSourceBean> raidesDegreeDesignationValues;
    private String degreeNamePart;

    public PreviousDegreeInformationForm() {
        setSchoolLevelValues(getAllowedSchoolLevelTypeValues());
        setCountriesValues(Bennu.getInstance().getCountrysSet());
        updateLists();
    }

    @Override
    public void updateLists() {
        if (precedentSchoolLevel != null) {
            Predicate<DegreeDesignation> matchesName = dd -> precedentSchoolLevel.getEquivalentDegreeClassifications()
                    .contains(dd.getDegreeClassification().getCode())
                    && StringNormalizer.normalize(getFullDescription(dd))
                            .contains(StringNormalizer.normalize(degreeNamePart == null ? "" : degreeNamePart));
            Set<Unit> units = new HashSet<Unit>();
            Collection<DegreeDesignation> possibleDesignations = new HashSet<DegreeDesignation>();
            if (precedentInstitutionOid != null) {
                Unit unit = FenixFramework.getDomainObject(precedentInstitutionOid);
                units.add(unit);
                possibleDesignations.addAll(unit.getDegreeDesignationSet());
            } else {
                possibleDesignations.addAll(Bennu.getInstance().getDegreeDesignationsSet());
            }
            if (raidesPrecedentDegreeDesignation != null) {
                setDegreeNamePart(getFullDescription(raidesPrecedentDegreeDesignation));
            }
            if (precedentSchoolLevel.isHigherEducation()) {
                units.addAll(OriginInformationForm
                        .findExternalAcademicUnit(institutionNamePart == null ? "" : institutionNamePart, 50).stream()
                        .map(i -> i.getUnit()).filter(i -> !i.getDegreeDesignationSet().isEmpty()).collect(Collectors.toSet()));
                setRaidesInstitutionValues(units);
                setRaidesDegreeDesignationValues(
                        possibleDesignations.stream().filter(matchesName).limit(50).collect(Collectors.toList()));
                setPrecedentDegreeDesignation("");
                if (raidesPrecedentDegreeDesignation != null) {
                    setDegreeNamePart(getFullDescription(raidesPrecedentDegreeDesignation));
                }
            } else {
                units.addAll(OriginInformationForm.findExternalUnit(institutionNamePart == null ? "" : institutionNamePart, 50)
                        .stream().filter(i -> i.getUnit().isNoOfficialExternal()).map(i -> i.getUnit())
                        .collect(Collectors.toSet()));
                setInstitutionValues(units);
                setRaidesPrecedentDegreeDesignation(null);
                setRaidesDegreeDesignationValues(Collections.emptyList());
            }
        }
    }

    public SchoolLevelType getPrecedentSchoolLevel() {
        return precedentSchoolLevel;
    }

    public void setPrecedentSchoolLevel(SchoolLevelType precedentSchoolLevel) {
        this.precedentSchoolLevel = precedentSchoolLevel;
    }

    public String getOtherPrecedentSchoolLevel() {
        return otherPrecedentSchoolLevel;
    }

    public void setOtherPrecedentSchoolLevel(String otherPrecedentSchoolLevel) {
        this.otherPrecedentSchoolLevel = otherPrecedentSchoolLevel;
    }

    public String getPrecedentInstitutionOid() {
        return precedentInstitutionOid;
    }

    public void setPrecedentInstitutionOid(String precedentInstitutionOid) {
        this.precedentInstitutionOid = precedentInstitutionOid;
    }

    public String getPrecedentInstitutionName() {
        return precedentInstitutionName;
    }

    public void setPrecedentInstitutionName(String precedentInstitutionName) {
        this.precedentInstitutionName = precedentInstitutionName;
    }

    public DegreeDesignation getRaidesPrecedentDegreeDesignation() {
        return raidesPrecedentDegreeDesignation;
    }

    public void setRaidesPrecedentDegreeDesignation(DegreeDesignation raidesPrecedentDegreeDesignation) {
        this.raidesPrecedentDegreeDesignation = raidesPrecedentDegreeDesignation;
    }

    public Country getPrecedentCountry() {
        return precedentCountry;
    }

    public void setPrecedentCountry(Country precedentCountry) {
        this.precedentCountry = precedentCountry;
    }

    public void setPrecedentDegreeDesignation(String precedentDegreeDesignation) {
        this.precedentDegreeDesignation = precedentDegreeDesignation;
    }

    public String getPrecedentDegreeDesignation() {
        if (getPrecedentSchoolLevel() != null && getPrecedentSchoolLevel().isHigherEducation()
                && getRaidesPrecedentDegreeDesignation() != null) {
            return getRaidesPrecedentDegreeDesignation().getDescription();
        }

        return precedentDegreeDesignation;
    }

    public int getNumberOfEnrolmentsInPreviousDegrees() {
        return numberOfEnrolmentsInPreviousDegrees;
    }

    public void setNumberOfEnrolmentsInPreviousDegrees(int numberOfEnrolmentsInPreviousDegrees) {
        this.numberOfEnrolmentsInPreviousDegrees = numberOfEnrolmentsInPreviousDegrees;
    }

    public List<TupleDataSourceBean> getInstitutionValues() {
        return institutionValues;
    }

    public void setRaidesInstitutionValues(Collection<Unit> institutionValues) {
        this.institutionValues = institutionValues.stream().map(u -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(u.getExternalId());
            String code = !Strings.isNullOrEmpty(u.getCode()) ? "[" + u.getCode() + "]" : "";
            tuple.setText(code + " " + u.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public void setInstitutionValues(Collection<Unit> institutionValues) {
        this.institutionValues = institutionValues.stream().map(u -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(u.getExternalId());
            tuple.setText(u.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
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

    public List<TupleDataSourceBean> getSchoolLevelValues() {
        return schoolLevelValues;
    }

    public void setSchoolLevelValues(List<SchoolLevelType> schoolLevelValues) {
        this.schoolLevelValues = schoolLevelValues.stream().map(slt -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(slt.toString());
            tuple.setText(slt.getLocalizedName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    protected List<SchoolLevelType> getAllowedSchoolLevelTypeValues() {
        final List<SchoolLevelType> result = Lists.newArrayList();

        result.add(SchoolLevelType.BACHELOR_DEGREE);
        result.add(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.DEGREE);
        result.add(SchoolLevelType.DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.DOCTORATE_DEGREE);
        result.add(SchoolLevelType.DOCTORATE_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.MASTER_DEGREE);
        result.add(SchoolLevelType.MASTER_DEGREE_INTEGRATED);
        result.add(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.OTHER);

        return result;
    }

    public String getInstitutionNamePart() {
        return institutionNamePart;
    }

    public void setInstitutionNamePart(String institutionNamePart) {
        this.institutionNamePart = institutionNamePart;
    }

    public List<TupleDataSourceBean> getRaidesDegreeDesignationValues() {
        return raidesDegreeDesignationValues;
    }

    public void setRaidesDegreeDesignationValues(List<DegreeDesignation> raidesDegreeDesignationValues) {
        this.raidesDegreeDesignationValues = raidesDegreeDesignationValues.stream().map(dd -> {
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

}
