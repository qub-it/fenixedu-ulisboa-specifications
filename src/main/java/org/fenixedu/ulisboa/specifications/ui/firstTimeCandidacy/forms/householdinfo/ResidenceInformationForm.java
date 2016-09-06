package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.domain.ResidenceType;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;

import pt.ist.standards.geographic.Planet;
import pt.ist.standards.geographic.PostalCode;

public class ResidenceInformationForm implements CandidancyForm {

    private Country countryOfResidence;
    private String address;
    private String areaCode; // zip code
    private String area; // location
    private Parish parishOfResidence;
    private District districtOfResidence;
    private DistrictSubdivision districtSubdivisionOfResidence;
    private Boolean dislocatedFromPermanentResidence;
    private District schoolTimeDistrictOfResidence;
    private DistrictSubdivision schoolTimeDistrictSubdivisionOfResidence;
    private String schoolTimeAddress;
    private String schoolTimeAreaCode;
    private String schoolTimeArea;
    private Parish schoolTimeParishOfResidence;
    private ResidenceType schoolTimeResidenceType;
    private String otherSchoolTimeResidenceType;

    private List<TupleDataSourceBean> countriesValues;
    private List<TupleDataSourceBean> districtsValues;
    private List<TupleDataSourceBean> districtSubdivisionValues;
    private List<TupleDataSourceBean> schoolTimeDistrictSubdivisionValues;
    private List<TupleDataSourceBean> parishValues;
    private List<TupleDataSourceBean> schoolTimeParishValues;
    private List<TupleDataSourceBean> areaCodeValues;
    private String areaCodePart;
    private List<TupleDataSourceBean> schoolTimeAreaCodeValues;
    private String schoolTimeAreaCodePart;
    private List<TupleDataSourceBean> residenceTypeValues;
    private List<String> otherResidenceTypeValues;
    private boolean isOtherResidenceType;

    public ResidenceInformationForm() {
        setResidenceTypeValues(Bennu.getInstance().getResidenceTypesSet());
        setOtherResidenceTypeValues(
                Bennu.getInstance().getResidenceTypesSet().stream().filter(rt -> rt.isOther()).collect(Collectors.toList()));
        updateLists();
    }

    @Override
    public void updateLists() {
        setCountriesValues(Bennu.getInstance().getCountrysSet());
        setDistrictsValues(FormAbstractController.getDistrictsWithSubdivisionsAndParishes().collect(Collectors.toList()));

        //Process isOtherResidenceType boolean
        if (schoolTimeResidenceType != null && schoolTimeResidenceType.isOther()) {
            setOtherResidenceType(true);
        } else {
            setOtherResidenceType(false);
        }
        //Populate district subdivisions
        if (districtOfResidence != null) {
            setDistrictSubdivisionValues(
                    FormAbstractController.getSubdivisionsWithParishes(districtOfResidence).collect(Collectors.toList()));
        }
        if (schoolTimeDistrictOfResidence != null) {
            setSchoolTimeDistrictSubdivisionValues(FormAbstractController
                    .getSubdivisionsWithParishes(schoolTimeDistrictOfResidence).collect(Collectors.toList()));
        }
        //Populate parishes
        if (districtSubdivisionOfResidence != null) {
            setParishValues(districtSubdivisionOfResidence.getParishSet());
        }
        if (schoolTimeDistrictSubdivisionOfResidence != null) {
            setSchoolTimeParishValues(schoolTimeDistrictSubdivisionOfResidence.getParishSet());
        }
        //Populate zip code
        List<String> postalCodes = getAllPostCodes().stream().map(pc -> pc.exportAsString().split(";")[4] + " " + pc.parent.name)
                .collect(Collectors.toList());
        if (getCountryOfResidence() == Country.readDefault()) {
            if (getAreaCode() != null) {
                setAreaCodePart(getAreaCode());
            }
            if (areaCodePart == null) {
                setAreaCodeValuesFormatted(Collections.emptyList());
            } else {
                setAreaCodeValuesFormatted(
                        postalCodes.stream().filter(pc -> pc.contains(areaCodePart)).limit(50).collect(Collectors.toList()));
            }
        } else {
            setAreaCode(null);
            setAreaCodePart(null);
            setAreaCodeValuesFormatted(Collections.emptyList());
        }
        if (getSchoolTimeAreaCode() != null) {
            setSchoolTimeAreaCodePart(getSchoolTimeAreaCode());
        }
        if (schoolTimeAreaCodePart == null) {
            setSchoolTimeAreaCodeValuesFormatted(Collections.emptyList());
        } else {
            setSchoolTimeAreaCodeValuesFormatted(postalCodes.stream().filter(pc -> pc.contains(schoolTimeAreaCodePart)).limit(50)
                    .collect(Collectors.toList()));
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Parish getParishOfResidence() {
        return parishOfResidence;
    }

    public void setParishOfResidence(Parish parishOfResidence) {
        this.parishOfResidence = parishOfResidence;
    }

    public District getDistrictOfResidence() {
        return districtOfResidence;
    }

    public void setDistrictOfResidence(District districtOfResidence) {
        this.districtOfResidence = districtOfResidence;
    }

    public DistrictSubdivision getDistrictSubdivisionOfResidence() {
        return districtSubdivisionOfResidence;
    }

    public void setDistrictSubdivisionOfResidence(DistrictSubdivision districtSubdivisionOfResidence) {
        this.districtSubdivisionOfResidence = districtSubdivisionOfResidence;
    }

    public Boolean getDislocatedFromPermanentResidence() {
        return dislocatedFromPermanentResidence;
    }

    public void setDislocatedFromPermanentResidence(Boolean dislocatedFromPermanentResidence) {
        this.dislocatedFromPermanentResidence = dislocatedFromPermanentResidence;
    }

    public District getSchoolTimeDistrictOfResidence() {
        return schoolTimeDistrictOfResidence;
    }

    public void setSchoolTimeDistrictOfResidence(District schoolTimeDistrictOfResidence) {
        this.schoolTimeDistrictOfResidence = schoolTimeDistrictOfResidence;
    }

    public DistrictSubdivision getSchoolTimeDistrictSubdivisionOfResidence() {
        return schoolTimeDistrictSubdivisionOfResidence;
    }

    public void setSchoolTimeDistrictSubdivisionOfResidence(DistrictSubdivision schoolTimeDistrictSubdivisionOfResidence) {
        this.schoolTimeDistrictSubdivisionOfResidence = schoolTimeDistrictSubdivisionOfResidence;
    }

    public String getSchoolTimeAddress() {
        return schoolTimeAddress;
    }

    public void setSchoolTimeAddress(String schoolTimeAddress) {
        this.schoolTimeAddress = schoolTimeAddress;
    }

    public String getSchoolTimeAreaCode() {
        return schoolTimeAreaCode;
    }

    public void setSchoolTimeAreaCode(String schoolTimeAreaCode) {
        this.schoolTimeAreaCode = schoolTimeAreaCode;
    }

    public String getSchoolTimeArea() {
        return schoolTimeArea;
    }

    public void setSchoolTimeArea(String schoolTimeArea) {
        this.schoolTimeArea = schoolTimeArea;
    }

    public Parish getSchoolTimeParishOfResidence() {
        return schoolTimeParishOfResidence;
    }

    public void setSchoolTimeParishOfResidence(Parish schoolTimeParishOfResidence) {
        this.schoolTimeParishOfResidence = schoolTimeParishOfResidence;
    }

    public Country getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(Country countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    protected boolean isSchoolTimeAddressComplete() {
        return isSchoolTimeRequiredInformationAddressFilled()
                && !isAnyEmpty(schoolTimeAddress, schoolTimeAreaCode, schoolTimeArea) && schoolTimeParishOfResidence != null
                && schoolTimeResidenceType != null;
    }

    protected boolean isAnyEmpty(String... fields) {
        for (String each : fields) {
            if (StringUtils.isEmpty(each)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isSchoolTimeRequiredInformationAddressFilled() {
        return getSchoolTimeDistrictOfResidence() != null && getSchoolTimeDistrictSubdivisionOfResidence() != null;
    }

    protected boolean isResidenceInformationFilled() {
        return !(getDistrictOfResidence() == null || getDistrictSubdivisionOfResidence() == null || parishOfResidence == null
                || StringUtils.isEmpty(address) || StringUtils.isEmpty(areaCode) || StringUtils.isEmpty(area));
    }

    protected boolean isAnySchoolTimeAddressInformationFilled() {
        return getSchoolTimeDistrictOfResidence() != null || getSchoolTimeDistrictSubdivisionOfResidence() != null
                || isAnyFilled(schoolTimeAddress, schoolTimeAreaCode, schoolTimeArea, otherSchoolTimeResidenceType)
                || schoolTimeParishOfResidence != null || schoolTimeResidenceType != null;
    }

    protected boolean isAnyFilled(final String... fields) {
        for (final String each : fields) {
            if (!StringUtils.isEmpty(each)) {
                return true;
            }
        }

        return false;
    }

    public ResidenceType getSchoolTimeResidenceType() {
        return schoolTimeResidenceType;
    }

    public void setSchoolTimeResidenceType(ResidenceType schoolTimeResidenceType) {
        this.schoolTimeResidenceType = schoolTimeResidenceType;
    }

    public String getOtherSchoolTimeResidenceType() {
        return otherSchoolTimeResidenceType;
    }

    public void setOtherSchoolTimeResidenceType(String otherSchoolTimeResidenceType) {
        this.otherSchoolTimeResidenceType = otherSchoolTimeResidenceType;
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

    public List<TupleDataSourceBean> getSchoolTimeDistrictSubdivisionValues() {
        return schoolTimeDistrictSubdivisionValues;
    }

    public void setSchoolTimeDistrictSubdivisionValues(Collection<DistrictSubdivision> districtSubdivisionValues) {
        this.schoolTimeDistrictSubdivisionValues = districtSubdivisionValues.stream().map(ds -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(ds.getExternalId());
            tuple.setText(ds.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getSchoolTimeParishValues() {
        return schoolTimeParishValues;
    }

    public void setSchoolTimeParishValues(Collection<Parish> parishValues) {
        this.schoolTimeParishValues = parishValues.stream().map(p -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(p.getExternalId());
            tuple.setText(p.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getAreaCodeValues() {
        return areaCodeValues;
    }

    public void setAreaCodeValues(List<PostalCode> areaCodeValues) {
        this.areaCodeValues = areaCodeValues.stream().map(pc -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(pc.exportAsString().split(";")[4] + " " + pc.parent.name);
            tuple.setText(pc.exportAsString().split(";")[4] + " " + pc.parent.name);
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getSchoolTimeAreaCodeValues() {
        return schoolTimeAreaCodeValues;
    }

    public void setSchoolTimeAreaCodeValues(List<PostalCode> schoolTimeAreaCodeValues) {
        this.schoolTimeAreaCodeValues = schoolTimeAreaCodeValues.stream().map(pc -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(pc.exportAsString().split(";")[4] + " " + pc.parent.name);
            tuple.setText(pc.exportAsString().split(";")[4] + " " + pc.parent.name);
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());;
    }

    public void setAreaCodeValuesFormatted(List<String> areaCodeValues) {
        this.areaCodeValues = areaCodeValues.stream().map(pc -> new TupleDataSourceBean(pc, pc))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public void setSchoolTimeAreaCodeValuesFormatted(List<String> schoolTimeAreaCodeValues) {
        this.schoolTimeAreaCodeValues = schoolTimeAreaCodeValues.stream().map(pc -> new TupleDataSourceBean(pc, pc))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public String getAreaCodePart() {
        return areaCodePart;
    }

    public void setAreaCodePart(String areaCodePart) {
        this.areaCodePart = areaCodePart;
    }

    public String getSchoolTimeAreaCodePart() {
        return schoolTimeAreaCodePart;
    }

    public void setSchoolTimeAreaCodePart(String schoolTimeAreaCodePart) {
        this.schoolTimeAreaCodePart = schoolTimeAreaCodePart;
    }

    private Collection<PostalCode> getAllPostCodes() {
        return Planet.getEarth().getPlace("PRT").getPlaces().stream().flatMap(d -> d.getPlaces().stream())
                .flatMap(m -> m.getPlaces().stream()).flatMap(l -> l.getPlaces().stream()).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getResidenceTypeValues() {
        return residenceTypeValues;
    }

    public void setResidenceTypeValues(Collection<ResidenceType> residenceTypeValues) {
        this.residenceTypeValues = residenceTypeValues.stream()
                .map(rt -> new TupleDataSourceBean(rt.getExternalId(), rt.getDescription().getContent()))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<String> getOtherResidenceTypeValues() {
        return otherResidenceTypeValues;
    }

    public void setOtherResidenceTypeValues(List<ResidenceType> otherResidenceTypeValues) {
        this.otherResidenceTypeValues =
                otherResidenceTypeValues.stream().map(rt -> rt.getExternalId()).collect(Collectors.toList());
    }

    public boolean isOtherResidenceType() {
        return isOtherResidenceType;
    }

    public void setOtherResidenceType(boolean isOtherResidenceType) {
        this.isOtherResidenceType = isOtherResidenceType;
    }

}
