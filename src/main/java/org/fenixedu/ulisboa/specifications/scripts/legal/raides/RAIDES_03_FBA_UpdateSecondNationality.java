package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.FinantialInstitution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.person.IDDocumentType;

public class RAIDES_03_FBA_UpdateSecondNationality extends CustomTask {
    
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
        
        ENTRIES.add(new Entry("080664200224", "NATIVE_COUNTRY_IDENTITY_CARD", "BE"));
        ENTRIES.add(new Entry("30275496", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("30052574", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("13646340", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("31061552", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("14501355", "IDENTITY_CARD", "CL"));
        ENTRIES.add(new Entry("13511834", "IDENTITY_CARD", "DE"));
        ENTRIES.add(new Entry("14163955", "IDENTITY_CARD", "DE"));
        ENTRIES.add(new Entry("10316897", "IDENTITY_CARD", "DE"));
        ENTRIES.add(new Entry("244131146", "OTHER", "DE"));
        ENTRIES.add(new Entry("13796894", "IDENTITY_CARD", "FI"));
        ENTRIES.add(new Entry("13614592", "IDENTITY_CARD", "FR"));
        ENTRIES.add(new Entry("AU8047274", "OTHER", "IT"));
        ENTRIES.add(new Entry("11428438", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30058401", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("NUDJBCD61", "PASSPORT", "PT"));
        ENTRIES.add(new Entry("14056447", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30944530", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("31067871", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30783308", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("L5MZ50VJZ", "OTHER", "PT"));
        ENTRIES.add(new Entry("30837106", "IDENTITY_CARD", "RO"));
        ENTRIES.add(new Entry("12262868", "IDENTITY_CARD", "RU"));
        ENTRIES.add(new Entry("31457070", "IDENTITY_CARD", "TR"));
        ENTRIES.add(new Entry("31363151", "IDENTITY_CARD", "UA"));
        ENTRIES.add(new Entry("15006730", "IDENTITY_CARD", "US"));
        ENTRIES.add(new Entry("13603343", "IDENTITY_CARD", "ZA"));

    }

    private static final Object FBA_NIF = "504035541";
    private static final String FBA_NAME = "Faculdade de Belas-Artes da Universidade de Lisboa";
    
    @Override
    public void runTask() throws Exception {
        doIt();

       throw new RuntimeException("Abort!");
    }

    private void doIt() {
        FinantialInstitution finantialInstitution = FinantialInstitution.findAll().iterator().next();
        
        if(!FBA_NIF.equals(finantialInstitution.getFiscalNumber())) {
            throw new RuntimeException("not expected finantial institution");
        }

        if(!finantialInstitution.getName().startsWith(FBA_NAME)) {
            throw new RuntimeException("not expected finantial institution");
        }
        
        for (final Entry entry : ENTRIES) {
            final Person person = findPerson(entry);
            
            if(person == null) {
                taskLog("E\tPERSON NOT FOUND\t%s\t%s\t%s\n", 
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