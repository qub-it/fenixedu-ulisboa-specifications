package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.FinantialInstitution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.person.IDDocumentType;

public class RAIDES_03_FMH_UpdateSecondNationality extends CustomTask {
    
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
        
        ENTRIES.add(new Entry("R371894", "PASSPORT", "PT"));
        ENTRIES.add(new Entry("11275317", "IDENTITY_CARD", "ES"));
        ENTRIES.add(new Entry("FJ296900", "PASSPORT", "ES"));
        ENTRIES.add(new Entry("15226314", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("02593671", "IDENTITY_CARD", "MZ"));

    }

    private static final Object FMH_NIF = "501621288";
    private static final String FMH_NAME = "Faculdade de Motricidade Humana";
    
    @Override
    public void runTask() throws Exception {
        doIt();

       throw new RuntimeException("Abort!");
    }

    private void doIt() {
        FinantialInstitution finantialInstitution = FinantialInstitution.findAll().iterator().next();
        
        if(!FMH_NIF.equals(finantialInstitution.getFiscalNumber())) {
            throw new RuntimeException("not expected finantial institution");
        }

        if(!finantialInstitution.getName().startsWith(FMH_NAME)) {
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