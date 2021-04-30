package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitName;
import org.fenixedu.academic.domain.organizationalStructure.UnitName.ExternalAcademicUnitNameLimitedOrderedSet;
import org.fenixedu.academic.domain.organizationalStructure.UnitName.ExternalUnitNameLimitedOrderedSet;
import org.fenixedu.academic.domain.organizationalStructure.UnitNamePart;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.ResidenceInformationForm;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import pt.ist.fenixframework.FenixFramework;

public class OriginInformationForm implements CandidancyForm {

    private static final long serialVersionUID = 1L;

    private SchoolLevelType schoolLevel;
    private String otherSchoolLevel;
    private String conclusionGrade;
    private String degreeDesignation;
    private String conclusionYear;
    private String institutionOid;
    private String institutionName;
    private DegreeDesignation raidesDegreeDesignation;
    private Country countryWhereFinishedPreviousCompleteDegree;
    private District districtWhereFinishedPreviousCompleteDegree;
    private DistrictSubdivision districtSubdivisionWhereFinishedPreviousCompleteDegree;
    private AcademicalInstitutionType highSchoolType;

    private boolean districtAndSubdivisionRequired;
    private List<TupleDataSourceBean> institutionValues;
    private String institutionNamePart;
    private List<TupleDataSourceBean> schoolLevelValues;
    private List<TupleDataSourceBean> raidesDegreeDesignationValues;
    private String degreeNamePart;
    private List<TupleDataSourceBean> countriesValues;
    private List<TupleDataSourceBean> districtsValues;
    private List<TupleDataSourceBean> districtSubdivisionValues;
    private List<TupleDataSourceBean> highSchoolTypeValues;

    public OriginInformationForm() {
        setSchoolLevelValues(getAllowedSchoolLevelTypeValues());
        setHighSchoolTypeValues(AcademicalInstitutionType.getHighSchoolTypes());
        updateLists();
    }

    @Override
    public void updateLists() {
        setCountriesValues(Bennu.getInstance().getCountrysSet());
        //Populate districts
        if (countryWhereFinishedPreviousCompleteDegree == Country.readDefault()) {
            setDistrictsValues(FormAbstractController.getDistrictsWithSubdivisionsAndParishes()
                    .filter(d -> !ResidenceInformationForm.oldDistrictCodes.contains(new Integer(d.getCode())))
                    .collect(Collectors.toList()));
        }
        //Populate district subdivisions
        if (districtWhereFinishedPreviousCompleteDegree != null) {
            setDistrictSubdivisionValues(FormAbstractController
                    .getSubdivisionsWithParishes(districtWhereFinishedPreviousCompleteDegree).collect(Collectors.toList()));
        }

        if (schoolLevel != null) {
            Predicate<DegreeDesignation> matchesName =
                    dd -> schoolLevel.getEquivalentDegreeClassifications().contains(dd.getDegreeClassification().getCode())
            /* && StringNormalizer.normalize(getFullDescription(dd)).contains(StringNormalizer.normalize(degreeNamePart == null ? "" : degreeNamePart)) */;

            Set<Unit> units = new HashSet<>();
            Collection<DegreeDesignation> possibleDesignations = new HashSet<>();
            if (institutionOid != null) {
                Unit unit = FenixFramework.getDomainObject(institutionOid);
                if (unit != null) {
                    units.add(unit);
                    possibleDesignations.addAll(unit.getDegreeDesignationSet());
                }
            } else {
                institutionOid = institutionName;
                possibleDesignations.addAll(Bennu.getInstance().getDegreeDesignationsSet());
            }
            if (raidesDegreeDesignation != null) {
                setDegreeNamePart(getFullDescription(raidesDegreeDesignation));
            }
            if (schoolLevel.isHigherEducation() && countryWhereFinishedPreviousCompleteDegree == Country.readDefault()) {
                units.addAll(findExternalAcademicUnit(institutionNamePart == null ? "" : institutionNamePart, 100).stream()
                        .map(i -> i.getUnit()).filter(i -> !getDegreeDesignationsWithSameSchoolLevel(i).isEmpty())
                        .collect(Collectors.toSet()));
                setRaidesInstitutionValues(units);
                setRaidesDegreeDesignationValues(
                        possibleDesignations.stream().filter(matchesName)/*.limit(50)*/.collect(Collectors.toList()));
                //put raides designation
                setDegreeDesignation("");
            } else {
                units.addAll(findExternalUnit(institutionNamePart == null ? "" : institutionNamePart, 100).stream()
                        .filter(i -> i.getUnit().isNoOfficialExternal()).map(i -> i.getUnit()).collect(Collectors.toSet()));
                setInstitutionValues(units);
                setRaidesDegreeDesignation(null);
                setRaidesDegreeDesignationValues(Collections.emptyList());
            }
        }

    }

    private List<DegreeDesignation> getDegreeDesignationsWithSameSchoolLevel(final Unit i) {
        List<String> classifications = schoolLevel.getEquivalentDegreeClassifications();
        return i.getDegreeDesignationSet().stream().filter(d -> classifications.contains(d.getDegreeClassification().getCode()))
                .collect(Collectors.toList());
    }

    //TODOJN - extract find methods
    public static Collection<UnitName> findExternalAcademicUnit(final String name, final int size) {
        final ExternalAcademicUnitNameLimitedOrderedSet academicUnitNameLimitedOrderedSet =
                new ExternalAcademicUnitNameLimitedOrderedSet(size);
        findUnits(academicUnitNameLimitedOrderedSet, name);
        return academicUnitNameLimitedOrderedSet;
    }

    public static Collection<UnitName> findExternalUnit(final String namePart, final int size) {
        final ExternalUnitNameLimitedOrderedSet unitNameLimitedOrderedSet = new ExternalUnitNameLimitedOrderedSet(size);
        findUnits(unitNameLimitedOrderedSet, namePart);
        return unitNameLimitedOrderedSet;
    }

    protected static void findUnits(final TreeSet<UnitName> unitNameLimitedOrderedSet, final String name) {
        //TODOJN - improve, too many verifications
        final String normalizedName = StringNormalizer.normalize(name.trim());
        final String[] nameParts = UnitNamePart.getNameParts(normalizedName);
        if (nameParts.length <= 0) {
            return;
        }
        final Collection<UnitName> unitNames = new ArrayList<>();
        final Collection<UnitNamePart> unitNameParts = findNameParts(nameParts[0]);

        if (unitNameParts.isEmpty()) {
            return;
        }

        for (UnitNamePart unitNamePart : unitNameParts) {
            unitNames.addAll(unitNamePart.getUnitNameSet());
        }

        if (nameParts.length > 1) {
            for (UnitName unitName : unitNames) {
                final String normalizedUnitName = unitName.getName();
                if (containsAll(normalizedUnitName, nameParts)) {
                    if (sequentialNameParts(normalizedUnitName, normalizedName)) {
                        //Special case - match the all name
                        // this case we will remove all other options and present the correct one
                        //Joao Amaral - 29/04/2020
                        if (unitName.getName().trim().toLowerCase().equals(normalizedName.trim().toLowerCase())) {
                            unitNameLimitedOrderedSet.clear();
                            unitNameLimitedOrderedSet.add(unitName);
                            return;
                        }
                        unitNameLimitedOrderedSet.add(unitName);
                    }
                }
            }
        } else {
            for (UnitName unitName : unitNames) {
                unitNameLimitedOrderedSet.add(unitName);
            }
        }
    }

    private static final Map<String, Collection<UnitNamePart>> unitNamePartIndexMap = new HashMap<>();

    public static Collection<UnitNamePart> findNameParts(final String namePart) {
        final String normalizedNamePart = StringNormalizer.normalize(namePart);

        final Collection<UnitNamePart> indexedUnitNamePart = unitNamePartIndexMap.get(normalizedNamePart);
        if (indexedUnitNamePart != null) {
            return indexedUnitNamePart;
        }

        Collection<UnitNamePart> unitNameParts = new ArrayList<>();
        for (final UnitNamePart unitNamePart : Bennu.getInstance().getUnitNamePartSet()) {
            final String otherUnitNamePart = unitNamePart.getNamePart();
            if (otherUnitNamePart.contains(normalizedNamePart)) {
                unitNameParts.add(unitNamePart);
            }
        }

        if (!unitNamePartIndexMap.containsKey(normalizedNamePart) && !unitNameParts.isEmpty()) {
            unitNamePartIndexMap.put(normalizedNamePart, unitNameParts);
        }

        return unitNameParts;
    }

    private static boolean containsAll(final String normalizedUnitName, final String[] nameParts) {
        for (final String namePart : nameParts) {
            if (!normalizedUnitName.contains(namePart)) {
                return false;
            }
        }
        return true;
    }

    private static boolean sequentialNameParts(final String normalizedUnitName, final String name) {
        String[] normalizedUnitNames = normalizedUnitName.split(" ");
        String[] names = name.split(" ");
        if (names.length > normalizedUnitNames.length) {
            return false;
        }
        int containedParts = 0;
        for (String normalizedUnitName2 : normalizedUnitNames) {
            for (int j = containedParts; j < names.length; j++) {
                if (normalizedUnitName2.contains(names[j])) {
                    containedParts++;
                    break;
                }
            }
        }
        return names.length == containedParts;
    }

    public SchoolLevelType getSchoolLevel() {
        return schoolLevel;
    }

    public void setSchoolLevel(final SchoolLevelType schoolLevel) {
        this.schoolLevel = schoolLevel;
    }

    public String getOtherSchoolLevel() {
        return otherSchoolLevel;
    }

    public void setOtherSchoolLevel(final String otherSchoolLevel) {
        this.otherSchoolLevel = otherSchoolLevel;
    }

    public String getConclusionGrade() {
        return conclusionGrade;
    }

    public void setConclusionGrade(final String conclusionGrade) {
        this.conclusionGrade = conclusionGrade;
    }

    public String getDegreeDesignation() {
        if (getSchoolLevel() != null && getSchoolLevel().isHigherEducation() && getRaidesDegreeDesignation() != null) {
            return getRaidesDegreeDesignation().getDescription();
        }
        return degreeDesignation;
    }

    public void setDegreeDesignation(final String degreeDesignation) {
        this.degreeDesignation = degreeDesignation;
    }

    public String getConclusionYear() {
        return conclusionYear;
    }

    public void setConclusionYear(final String conclusionYear) {
        this.conclusionYear = conclusionYear;
    }

    public String getInstitutionOid() {
        return institutionOid;
    }

    public void setInstitutionOid(final String institutionOid) {
        this.institutionOid = institutionOid;
    }

    public DegreeDesignation getRaidesDegreeDesignation() {
        return raidesDegreeDesignation;
    }

    public void setRaidesDegreeDesignation(final DegreeDesignation raidesDegreeDesignation) {
        this.raidesDegreeDesignation = raidesDegreeDesignation;
    }

    public Country getCountryWhereFinishedPreviousCompleteDegree() {
        return countryWhereFinishedPreviousCompleteDegree;
    }

    public void setCountryWhereFinishedPreviousCompleteDegree(final Country countryWhereFinishedPreviousCompleteDegree) {
        this.countryWhereFinishedPreviousCompleteDegree = countryWhereFinishedPreviousCompleteDegree;
    }

    public AcademicalInstitutionType getHighSchoolType() {
        if (getSchoolLevel() != null && getSchoolLevel().isHighSchoolOrEquivalent()) {
            return highSchoolType;
        }
        return null;
    }

    public void setHighSchoolType(final AcademicalInstitutionType highSchoolType) {
        this.highSchoolType = highSchoolType;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(final String institutionName) {
        this.institutionName = institutionName;
    }

    public District getDistrictWhereFinishedPreviousCompleteDegree() {
        return districtWhereFinishedPreviousCompleteDegree;
    }

    public void setDistrictWhereFinishedPreviousCompleteDegree(final District districtWhereFinishedPreviousCompleteDegree) {
        this.districtWhereFinishedPreviousCompleteDegree = districtWhereFinishedPreviousCompleteDegree;
    }

    public DistrictSubdivision getDistrictSubdivisionWhereFinishedPreviousCompleteDegree() {
        return districtSubdivisionWhereFinishedPreviousCompleteDegree;
    }

    public void setDistrictSubdivisionWhereFinishedPreviousCompleteDegree(
            final DistrictSubdivision districtSubdivisionWhereFinishedPreviousCompleteDegree) {
        this.districtSubdivisionWhereFinishedPreviousCompleteDegree = districtSubdivisionWhereFinishedPreviousCompleteDegree;
    }

    public boolean isDistrictAndSubdivisionRequired() {
        return districtAndSubdivisionRequired;
    }

    public void setDistrictAndSubdivisionRequired(final boolean districtAndSubdivisionRequired) {
        this.districtAndSubdivisionRequired = districtAndSubdivisionRequired;
    }

    public List<TupleDataSourceBean> getInstitutionValues() {
        return institutionValues;
    }

    public void setRaidesInstitutionValues(final Collection<Unit> institutionValues) {
        this.institutionValues = institutionValues.stream().map(u -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(u.getExternalId());
            String code = !Strings.isNullOrEmpty(u.getCode()) ? "[" + u.getCode() + "]" : "";
            tuple.setText(code + " " + u.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public void setInstitutionValues(final Collection<Unit> institutionValues) {
        this.institutionValues = institutionValues.stream().map(u -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(u.getExternalId());
            tuple.setText(u.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getSchoolLevelValues() {
        return schoolLevelValues;
    }

    public void setSchoolLevelValues(final List<SchoolLevelType> schoolLevelValues) {
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
        result.add(SchoolLevelType.MASTER_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.DEGREE);
        result.add(SchoolLevelType.DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.DOCTORATE_DEGREE);
        result.add(SchoolLevelType.DOCTORATE_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.MASTER_DEGREE);
        result.add(SchoolLevelType.MASTER_DEGREE_INTEGRATED);
        result.add(SchoolLevelType.OTHER);
        result.add(SchoolLevelType.HIGH_SCHOOL_OR_EQUIVALENT);
        result.add(SchoolLevelType.MEDIUM_EDUCATION);
        result.add(SchoolLevelType.TECHNICAL_SPECIALIZATION);
        result.add(SchoolLevelType.TECHNICAL_PROFESSIONAL_HIGHER_DIPLOMA);
        result.add(SchoolLevelType.FIRST_CYCLE_INTEGRATED_MASTER_DEGREE);
        result.add(SchoolLevelType.FIRST_CYCLE_BASIC_SCHOOL);
        result.add(SchoolLevelType.SECOND_CYCLE_BASIC_SCHOOL);
        result.add(SchoolLevelType.THIRD_CYCLE_BASIC_SCHOOL);

        return result;
    }

    public List<TupleDataSourceBean> getRaidesDegreeDesignationValues() {
        return raidesDegreeDesignationValues;
    }

    public void setRaidesDegreeDesignationValues(final List<DegreeDesignation> raidesDegreeDesignationValues) {
        this.raidesDegreeDesignationValues = raidesDegreeDesignationValues.stream().map(dd -> {
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

    public List<TupleDataSourceBean> getCountriesValues() {
        return countriesValues;
    }

    public void setCountriesValues(final Collection<Country> countriesValues) {
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

    public void setDistrictsValues(final Collection<District> districtsValues) {
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

    public void setDistrictSubdivisionValues(final Collection<DistrictSubdivision> districtSubdivisionValues) {
        this.districtSubdivisionValues = districtSubdivisionValues.stream().map(ds -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(ds.getExternalId());
            tuple.setText(ds.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getHighSchoolTypeValues() {
        return highSchoolTypeValues;
    }

    public void setHighSchoolTypeValues(final List<AcademicalInstitutionType> highSchoolTypeValues) {
        this.highSchoolTypeValues = highSchoolTypeValues.stream().map(ait -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(ait.toString());
            tuple.setText(BundleUtil.getString(BUNDLE, ait.getName()));
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public String getInstitutionNamePart() {
        return institutionNamePart;
    }

    public void setInstitutionNamePart(final String institutionNamePart) {
        this.institutionNamePart = institutionNamePart;
    }

    public String getDegreeNamePart() {
        return degreeNamePart;
    }

    public void setDegreeNamePart(final String degreeNamePart) {
        this.degreeNamePart = degreeNamePart;
    }

}
