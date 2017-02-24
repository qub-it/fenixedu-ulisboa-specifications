package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.FinantialInstitution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.person.IDDocumentType;

public class RAIDES_03_FC_UpdateSecondNationality extends CustomTask {
    
    private static class Entry {
        
        public String idDocumentNumber;
        public String idDocumentType;
        public String secondNationalityCode;

        private Entry( final String idDocumentNumber, final String idDocumentType, final String secondNationalityCode) {
            this.idDocumentNumber = idDocumentNumber;
            this.idDocumentType = idDocumentType;
            this.secondNationalityCode = secondNationalityCode;
        }
    }

    private static List<Entry> ENTRIES = new ArrayList<>();

    static {
        
        ENTRIES.add(new Entry("12782847", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("13485234", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("13770788", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("12448685", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("13754473", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("14095199", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("10405184", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("14493371", "IDENTITY_CARD", "AR"));
        ENTRIES.add(new Entry("13978203", "IDENTITY_CARD", "AT"));
        ENTRIES.add(new Entry("14346422", "IDENTITY_CARD", "AU"));
        ENTRIES.add(new Entry("14517979", "IDENTITY_CARD", "BE"));
        ENTRIES.add(new Entry("13930453", "IDENTITY_CARD", "BE"));
        ENTRIES.add(new Entry("14170433", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("13822922", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("13727368", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("13937623", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("15934064", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("AA3663016", "PASSPORT", "BR"));
        ENTRIES.add(new Entry("15261248", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("13045468", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("15559902", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("AA0780496", "PASSPORT", "BR"));
        ENTRIES.add(new Entry("14354212", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("15796682", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("14154278", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("31602473", "IDENTITY_CARD", "BY"));
        ENTRIES.add(new Entry("12283075", "IDENTITY_CARD", "CA"));
        ENTRIES.add(new Entry("13934590", "IDENTITY_CARD", "CA"));
        ENTRIES.add(new Entry("12947543", "IDENTITY_CARD", "CA"));
        ENTRIES.add(new Entry("14158968", "IDENTITY_CARD", "CA"));
        ENTRIES.add(new Entry("13827885", "IDENTITY_CARD", "CA"));
        ENTRIES.add(new Entry("13073161", "IDENTITY_CARD", "CA"));
        ENTRIES.add(new Entry("14218640", "IDENTITY_CARD", "CA"));
        ENTRIES.add(new Entry("13647371", "IDENTITY_CARD", "CA"));
        ENTRIES.add(new Entry("12483944", "IDENTITY_CARD", "CH"));
        ENTRIES.add(new Entry("13599644", "IDENTITY_CARD", "CH"));
        ENTRIES.add(new Entry("11770772", "IDENTITY_CARD", "CH"));
        ENTRIES.add(new Entry("14167536", "IDENTITY_CARD", "CV"));
        ENTRIES.add(new Entry("30368444", "IDENTITY_CARD", "CV"));
        ENTRIES.add(new Entry("31179013", "IDENTITY_CARD", "CV"));
        ENTRIES.add(new Entry("15829966", "IDENTITY_CARD", "CV"));
        ENTRIES.add(new Entry("13640305", "IDENTITY_CARD", "CV"));
        ENTRIES.add(new Entry("15683499", "IDENTITY_CARD", "CV"));
        ENTRIES.add(new Entry("13911083", "IDENTITY_CARD", "DE"));
        ENTRIES.add(new Entry("12706344", "IDENTITY_CARD", "DE"));
        ENTRIES.add(new Entry("14027675", "IDENTITY_CARD", "DE"));
        ENTRIES.add(new Entry("13625294", "IDENTITY_CARD", "DE"));
        ENTRIES.add(new Entry("14131694", "IDENTITY_CARD", "DK"));
        ENTRIES.add(new Entry("FH619269", "PASSPORT", "ES"));
        ENTRIES.add(new Entry("10305652", "IDENTITY_CARD", "ES"));
        ENTRIES.add(new Entry("12740888", "IDENTITY_CARD", "ES"));
        ENTRIES.add(new Entry("13922425", "IDENTITY_CARD", "ES"));
        ENTRIES.add(new Entry("cv464351", "PASSPORT", "ES"));
        ENTRIES.add(new Entry("12383635", "IDENTITY_CARD", "ES"));
        ENTRIES.add(new Entry("14290519", "IDENTITY_CARD", "ES"));
        ENTRIES.add(new Entry("15959575", "IDENTITY_CARD", "FR"));
        ENTRIES.add(new Entry("13927075", "IDENTITY_CARD", "FR"));
        ENTRIES.add(new Entry("14159274", "IDENTITY_CARD", "FR"));
        ENTRIES.add(new Entry("11439404", "IDENTITY_CARD", "FR"));
        ENTRIES.add(new Entry("13470922", "IDENTITY_CARD", "FR"));
        ENTRIES.add(new Entry("11538618", "IDENTITY_CARD", "GB"));
        ENTRIES.add(new Entry("15325863", "IDENTITY_CARD", "GB"));
        ENTRIES.add(new Entry("13747289", "IDENTITY_CARD", "GB"));
        ENTRIES.add(new Entry("14142521", "IDENTITY_CARD", "GB"));
        ENTRIES.add(new Entry("9991291", "IDENTITY_CARD", "GB"));
        ENTRIES.add(new Entry("13861639", "IDENTITY_CARD", "GN"));
        ENTRIES.add(new Entry("14013518", "IDENTITY_CARD", "IT"));
        ENTRIES.add(new Entry("FF115772", "PASSPORT", "IT"));
        ENTRIES.add(new Entry("CX202572", "PASSPORT", "IT"));
        ENTRIES.add(new Entry("31259862", "IDENTITY_CARD", "MD"));
        ENTRIES.add(new Entry("12924764", "IDENTITY_CARD", "MZ"));
        ENTRIES.add(new Entry("14426427", "IDENTITY_CARD", "MZ"));
        ENTRIES.add(new Entry("13733329", "IDENTITY_CARD", "NL"));
        ENTRIES.add(new Entry("31035546", "IDENTITY_CARD", "NL"));
        ENTRIES.add(new Entry("14255957", "IDENTITY_CARD", "NL"));
        ENTRIES.add(new Entry("14148242", "IDENTITY_CARD", "NL"));
        ENTRIES.add(new Entry("14258972", "IDENTITY_CARD", "NL"));
        ENTRIES.add(new Entry("14204089", "IDENTITY_CARD", "NL"));
        ENTRIES.add(new Entry("15587767", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31790375", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("18015071", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31352867", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30980346", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31647803", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31406191", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31389808", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30011335", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("18016023", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31598602", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31466389", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31149984", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30499592", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("14338247", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30493850", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30434824", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30865419", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31648597", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("27700334", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31264744", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("15613478", "PASSPORT", "PT"));
        ENTRIES.add(new Entry("31698381", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31551532", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("15684215", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("14243287", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("14384514", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("15962327", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31618040", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("N0245938", "PASSPORT", "PT"));
        ENTRIES.add(new Entry("13346142", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("15880330", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31679574", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31472619", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30078656", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30646175", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31667318", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("13490416", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31116432", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31111074", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31166530", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31308075", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("13505652", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("18013497", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31522246", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("13823581", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30279954", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31692724", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("15321697", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30606912", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30836656", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("13498217", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31482387", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("14395048", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30059187", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30047652", "IDENTITY_CARD", "RO"));
        ENTRIES.add(new Entry("13646224", "IDENTITY_CARD", "RU"));
        ENTRIES.add(new Entry("13984009", "IDENTITY_CARD", "SE"));
        ENTRIES.add(new Entry("15098402", "IDENTITY_CARD", "ST"));
        ENTRIES.add(new Entry("30455906", "IDENTITY_CARD", "ST"));
        ENTRIES.add(new Entry("13930146", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("13797744", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("13384558", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("14212333", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("10547521", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("14401823", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("14409520", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("14543127", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("14290086", "IDENTITY_CARD", "VE"));
        ENTRIES.add(new Entry("30751074", "IDENTITY_CARD", "VE"));
        ENTRIES.add(new Entry("15088209", "IDENTITY_CARD", "VE"));
        ENTRIES.add(new Entry("13613951", "IDENTITY_CARD", "ZA"));
    }

    private static final Object FC_NIF = "502618418";
    private static final String FC_NAME = "Faculdade de Ciências da Universidade de Lisboa";
    
    @Override
    public void runTask() throws Exception {
        doIt();

       throw new RuntimeException("Abort!");
    }

    private void doIt() {
        FinantialInstitution finantialInstitution = FinantialInstitution.findAll().iterator().next();
        
        if(!FC_NIF.equals(finantialInstitution.getFiscalNumber())) {
            throw new RuntimeException("not expected finantial institution");
        }

        if(!finantialInstitution.getName().startsWith(FC_NAME)) {
            throw new RuntimeException("not expected finantial institution");
        }
        
        for (final Entry entry : ENTRIES) {
            final Person person = findPerson(entry);
            
            if(person == null) {
                taskLog("E\tCOUNTRY CODE NOT FOUND\t%s\t%s\t%s\n", 
                        entry.idDocumentNumber,
                        entry.idDocumentType,
                        entry.secondNationalityCode);
                continue;
            }
            
            final Country country = Country.readByTwoLetterCode(entry.secondNationalityCode);
            
            if(country == null) {
                taskLog("E\tCOUNTRY CODE NOT FOUND\t%s\t%s\t%s\n", 
                        entry.idDocumentNumber,
                        entry.idDocumentType,
                        entry.secondNationalityCode);
                continue;
            }
            
            if(person.getPersonUlisboaSpecifications().getSecondNationality() != null) {
                taskLog("W\tPERSON WITH SECOND NATIONALITY\t%s\t%s\t%s\t%s\t%s\n", 
                        entry.idDocumentNumber, 
                        entry.idDocumentType,
                        entry.secondNationalityCode,
                        person.getDocumentIdNumber(), 
                        person.getIdDocumentType(), 
                        person.getPersonUlisboaSpecifications().getSecondNationality().getCode());
                continue;
            }
            
            {
                person.getPersonUlisboaSpecifications().setSecondNationality(country);
                taskLog("C\tCHANGE SECOND NATIONALITY\t%s\t%s\t%s\t%s\t%s\t%s\n", 
                        entry.idDocumentNumber, 
                        entry.idDocumentType,
                        entry.secondNationalityCode,
                        person.getDocumentIdNumber(), 
                        person.getIdDocumentType(), 
                        person.getPersonUlisboaSpecifications().getSecondNationality().getCode());
                continue;
            }
        }
    }

    private Person findPerson(final Entry entry) {
        final IDDocumentType idDocumentType = IDDocumentType.valueOf(entry.idDocumentType);

        final Collection<Person> personSet = Person.findPersonByDocumentID(entry.idDocumentNumber);
        
        if(personSet.isEmpty()) {
            return null;
        }
        
        Person personToConsider = null;
        for (final Person person : personSet) {
            if(person.getIdDocumentType() == idDocumentType) {
                if(personToConsider != null) {
                    throw new RuntimeException(String.format("Found duplicate person: %s, %s", person.getName(), person.getDocumentIdNumber()));
                }
                
                personToConsider = person;
            }
        }
        
        return personToConsider;
    }

}