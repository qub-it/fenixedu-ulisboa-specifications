package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import com.google.common.base.Strings;

public class SetDefaultCountryOnPhysicalAddressesWithoutCountry extends CustomTask {

    @Override
    public void runTask() throws Exception {
        
        int toChangeCount = 0;
        for (final Party party : Bennu.getInstance().getPartysSet()) {
            for (final PartyContact partyContact : party.getPartyContacts(PhysicalAddress.class)) {
                final PhysicalAddress address = (PhysicalAddress) partyContact;
                
                if(address.getCountryOfResidence() != null) {
                    continue;
                }
                
                if(findDistrictByName(address.getDistrictOfResidence()) == null) {
                    continue;
                }
                
                final District district = findDistrictByName(address.getDistrictOfResidence());
                
                if(findDistrictSubdivisionByName(district, address.getDistrictSubdivisionOfResidence()) == null) {
                    continue;
                }
                
                toChangeCount++;
                address.setCountryOfResidence(Country.readDefault());
            }
        }
        
        taskLog("TO CHANGE: " + toChangeCount);
    }

    
    public District findDistrictByName(final String name) {
        if(Strings.isNullOrEmpty(name)) {
            return null;
        }
        
        final String n = name;
        for (final District district : Bennu.getInstance().getDistrictsSet()) {
            if (district.getName().equals(n.trim())) {
                return district;
            }
        }

        return null;
    }

    public DistrictSubdivision findDistrictSubdivisionByName(final District district, final String name) {
        if(Strings.isNullOrEmpty(name)) {
            return null;
        }
        
        DistrictSubdivision result = null;
        String n = name.trim();
        if (district != null && !Strings.isNullOrEmpty(n)) {
            for (final DistrictSubdivision iter : Bennu.getInstance().getDistrictSubdivisionsSet()) {
                if (iter.getDistrict().equals(district) && n.toLowerCase().equals(iter.getName().toLowerCase())) {
                    if (result != null) {
                        throw new ULisboaSpecificationsDomainException("error.DistrictSubdivision.found.duplicate",
                                district.getCode(), name, result.toString(), iter.toString());
                    }
                    result = iter;
                }
            }
        }

        return result;
    }
    
}
