package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

import pt.ist.standards.geographic.Planet;
import pt.ist.standards.geographic.PostalCode;

public class FiscalInformationForm implements Serializable, CandidancyForm  {

    public static List<Integer> oldDistrictCodes = Arrays.asList(19, 20, 21, 22);
    private static Map<String, PostalCode> allPostalCodes;

    private Person person;
    
    private String socialSecurityNumber;

    private boolean associateExistingPhysicalAddresses;
    
    private PhysicalAddress fiscalAddress;
    
    private Country countryOfResidence;
    private String address;
    private String areaCode; // zip code
    private String area; // location
    private District districtOfResidence;
    private DistrictSubdivision districtSubdivisionOfResidence;

    private Parish parishOfResidence;
    private String districtOfResidenceName;
    private String districtSubdivisionOfResidenceName;
    private String parishOfResidenceName;
    
    private List<TupleDataSourceBean> countriesValues;
    private List<TupleDataSourceBean> areaCodeValues;
    private String areaCodePart;

    private List<TupleDataSourceBean> fiscalAddressValues;
    
    public FiscalInformationForm() {
    }
    
    public FiscalInformationForm(final Person person) {
        this.person = person;
    }
    
    @Override
    public void updateLists() {
        setCountriesValues(Bennu.getInstance().getCountrysSet());

        //Populate zip code
        updatePostalCodes();

        //Populate readOnly slots (district, subdivision and parish)
        updateReadOnlySlot();
        
        updateFiscalAddresses();
    }

    private void updateFiscalAddresses() {
        setFiscalAddressValues(this.person.getValidAddressesForFiscalData());
    }

    private void updatePostalCodes() {
        Collection<String> postalCodes = getAllPostalCodes().keySet();
        if (getCountryOfResidence() == Country.readDefault()) {
            if (areaCodePart == null) {
                setAreaCodeValuesFormatted(Collections.emptyList());
            } else {
                setAreaCodeValuesFormatted(postalCodes.stream()
                        .filter(pc -> StringNormalizer.normalize(pc).contains(StringNormalizer.normalize(areaCodePart))).limit(50)
                        .collect(Collectors.toList()));
            }
        } else {
            setAreaCodePart(null);
            setAreaCodeValuesFormatted(Collections.emptyList());
        }
    }

    private void updateReadOnlySlot() {
        if (StringUtils.isNotBlank(getAreaCode())) {
            PostalCode postalCode = getAllPostalCodes().get(getAreaCode());
            if (postalCode == null) {
                String key = getAllPostalCodes().keySet().stream().filter(x -> x.startsWith(getAreaCode().split(" ")[0]))
                        .findFirst().orElse(null);
                postalCode = getAllPostalCodes().get(key);
            }
            if (postalCode != null) {
                String districtCode = postalCode.parent.parent.parent.exportAsString().split(";")[1];
                District district = District.readByCode(districtCode);
                setDistrictOfResidence(district);
                if (district != null) {
                    districtOfResidenceName = district.getName();
                }

                String subdivisionCode = postalCode.parent.parent.exportAsString().split(";")[2];
                DistrictSubdivision subdivision = district.getDistrictSubdivisionsSet().stream()
                        .filter(s -> s.getCode().equals(subdivisionCode)).findFirst().orElse(null);
                setDistrictSubdivisionOfResidence(subdivision);
                if (subdivision != null) {
                    districtSubdivisionOfResidenceName = subdivision.getName();
                }

                String parishCode = postalCode.parent.exportAsString().split(";")[3];
                Parish parish =
                        subdivision.getParishSet().stream().filter(p -> p.getCode().equals(parishCode)).findFirst().orElse(null);
                setParishOfResidence(parish);
                if (parish != null) {
                    parishOfResidenceName = parish.getName();
                }
            }
        }

    }

    protected boolean isResidenceInformationFilled() {
        if(getCountryOfResidence() == null) {
            return false;
        }
        
        if(getCountryOfResidence().isDefaultCountry()) {
            return !(getDistrictOfResidence() == null || getDistrictSubdivisionOfResidence() == null || parishOfResidence == null
                    || StringUtils.isEmpty(address) || StringUtils.isEmpty(areaCode));
        } else {
            return !(StringUtils.isEmpty(getDistrictSubdivisionOfResidenceName()) || StringUtils.isEmpty(address));
        }
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

    public List<TupleDataSourceBean> getAreaCodeValues() {
        return areaCodeValues;
    }

    public List<TupleDataSourceBean> getFiscalAddressValues() {
        return fiscalAddressValues;
    }
    
    public void setFiscalAddressValues(List<PhysicalAddress> physicalAddresses) {
        this.fiscalAddressValues = physicalAddresses.stream().map(pc -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(pc.getExternalId());
            tuple.setText(pc.getUiFiscalPresentationValue());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public void setAreaCodeValuesFormatted(List<String> areaCodeValues) {
        this.areaCodeValues = areaCodeValues.stream().map(pc -> new TupleDataSourceBean(pc, pc))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public static Map<String, PostalCode> getAllPostalCodes() {
        if (allPostalCodes == null) {
            populateAllPostalCodes();
        }
        return allPostalCodes;
    }

    private static void populateAllPostalCodes() {
        Collection<PostalCode> allPortugalPostalCodes = getAllPortugalPostalCodes();
        allPostalCodes = new HashMap<>();
        for (PostalCode postalCode : allPortugalPostalCodes) {
            String stringPostalCode = postalCode.exportAsString().split(";")[4] + " " + postalCode.parent.name;
            allPostalCodes.put(stringPostalCode, postalCode);
        }
    }

    private static Collection<PostalCode> getAllPortugalPostalCodes() {
        return Planet.getEarth().getPlace("PRT").getPlaces().stream()
                .filter(d -> !oldDistrictCodes.contains(new Integer(d.exportAsString().split(";")[1])))
                .flatMap(d -> d.getPlaces().stream()).flatMap(m -> m.getPlaces().stream()).flatMap(l -> l.getPlaces().stream())
                .collect(Collectors.toList());
    }
    
    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on
    
    public Person getPerson() {
        return person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }
    
    public String getAreaCodePart() {
        return areaCodePart;
    }

    public void setAreaCodePart(String areaCodePart) {
        this.areaCodePart = areaCodePart;
    }
    
    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public boolean isAssociateExistingPhysicalAddresses() {
        return associateExistingPhysicalAddresses;
    }

    public void setAssociateExistingPhysicalAddresses(boolean associateExistingPhysicalAddresses) {
        this.associateExistingPhysicalAddresses = associateExistingPhysicalAddresses;
    }

    public PhysicalAddress getFiscalAddress() {
        return fiscalAddress;
    }

    public void setFiscalAddress(PhysicalAddress fiscalAddress) {
        this.fiscalAddress = fiscalAddress;
    }

    public Country getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(Country countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
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

    public Parish getParishOfResidence() {
        return parishOfResidence;
    }

    public void setParishOfResidence(Parish parishOfResidence) {
        this.parishOfResidence = parishOfResidence;
    }

    public String getDistrictOfResidenceName() {
        return districtOfResidenceName;
    }

    public void setDistrictOfResidenceName(String districtOfResidenceName) {
        this.districtOfResidenceName = districtOfResidenceName;
    }

    public String getDistrictSubdivisionOfResidenceName() {
        return districtSubdivisionOfResidenceName;
    }

    public void setDistrictSubdivisionOfResidenceName(String districtSubdivisionOfResidenceName) {
        this.districtSubdivisionOfResidenceName = districtSubdivisionOfResidenceName;
    }

    public String getParishOfResidenceName() {
        return parishOfResidenceName;
    }

    public void setParishOfResidenceName(String parishOfResidenceName) {
        this.parishOfResidenceName = parishOfResidenceName;
    }
    
    
    
    
    
}
