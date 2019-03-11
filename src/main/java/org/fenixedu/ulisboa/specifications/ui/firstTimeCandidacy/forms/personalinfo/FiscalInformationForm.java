package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

public class FiscalInformationForm implements Serializable, CandidancyForm  {

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
    
    
    public FiscalInformationForm() {
    }
    
    @Override
    public void updateLists() {
        // TODO Auto-generated method stub
        
    }

    protected boolean isResidenceInformationFilled() {
        return !(getDistrictOfResidence() == null || getDistrictSubdivisionOfResidence() == null || parishOfResidence == null
                || StringUtils.isEmpty(address) || StringUtils.isEmpty(areaCode));
    }

    
    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on
    
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
