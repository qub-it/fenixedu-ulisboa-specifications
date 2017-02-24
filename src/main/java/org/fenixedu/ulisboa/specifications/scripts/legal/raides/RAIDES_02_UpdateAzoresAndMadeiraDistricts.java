package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.StringNormalizer;

import com.google.common.base.Strings;

public class RAIDES_02_UpdateAzoresAndMadeiraDistricts extends CustomTask {

    private static final String ILHA_TERCEIRA = "Ilha Terceira";
    private static final String ILHA_DA_MADEIRA = "Ilha da Madeira";
    private static final String ILHA_DO_FAIAL = "Ilha do Faial";
    private static final String ILHA_DE_SAO_MIGUEL = "Ilha de São Miguel";

    @Override
    public void runTask() throws Exception {
        doIt();
        
        throw new RuntimeException("abort");
    }

    private void doIt() {
        int w = 0;
        
        int total = Bennu.getInstance().getPartysSet().size();
        
        int c = 0;
        
        for (final Party party : Bennu.getInstance().getPartysSet()) {
            if((++c % 1000) == 0) {
                taskLog("Count %d/%d\n", c, total);
            }
            
            for (final PartyContact partyContact : party.getPartyContacts(PhysicalAddress.class)) {
                final PhysicalAddress address = (PhysicalAddress) partyContact;

                if (normalize("Funchal").equals(normalize(address.getDistrictOfResidence()))) {
                    w++;

                    taskLog("C\t%s\t%s\t%s\t%s\n", partyContact.getExternalId(), party.getName(), address.getDistrictOfResidence(), ILHA_DA_MADEIRA);
                    address.setDistrictOfResidence(ILHA_DA_MADEIRA);
                } else if (normalize("Ponta Delgada").equals(normalize(address.getDistrictOfResidence()))) {
                    w++;

                    taskLog("C\t%s\t%s\t%s\t%s\n", partyContact.getExternalId(), party.getName(), address.getDistrictOfResidence(), ILHA_DE_SAO_MIGUEL);
                    address.setDistrictOfResidence(ILHA_DE_SAO_MIGUEL);
                } else if (normalize("Horta").equals(normalize(address.getDistrictOfResidence()))) {
                    w++;

                    taskLog("C\t%s\t%s\t%s\t%s\n", partyContact.getExternalId(), party.getName(), address.getDistrictOfResidence(), ILHA_DO_FAIAL);
                    address.setDistrictOfResidence(ILHA_DO_FAIAL);
                } else if (normalize("Angra do Heroismo").equals(normalize(address.getDistrictOfResidence()))) {
                    w++;

                    taskLog("C\t%s\t%s\t%s\t%s\n", partyContact.getExternalId(), party.getName(), address.getDistrictOfResidence(), ILHA_TERCEIRA);
                    address.setDistrictOfResidence(ILHA_TERCEIRA);
                }
            }
        }

        taskLog("Change: %d", w++);
    }

    private static String normalize(final String value) {
        if(Strings.isNullOrEmpty(value)) {
            return "";
        }
        
        return StringNormalizer.normalize(value.trim()).toLowerCase();
    }


}
