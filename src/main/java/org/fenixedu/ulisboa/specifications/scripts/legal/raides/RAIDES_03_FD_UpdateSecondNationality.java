package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.FinantialInstitution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.person.IDDocumentType;

public class RAIDES_03_FD_UpdateSecondNationality extends CustomTask {
    
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
        ENTRIES.add(new Entry("30902811", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("31315192", "IDENTITY_CARD", "RU"));
        ENTRIES.add(new Entry("13098836", "IDENTITY_CARD", "CV"));
        ENTRIES.add(new Entry("15931698", "IDENTITY_CARD", "CV"));
        ENTRIES.add(new Entry("30733199", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("14074665", "IDENTITY_CARD", "CH"));
        ENTRIES.add(new Entry("10094031", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("30900336", "IDENTITY_CARD", "ST"));
        ENTRIES.add(new Entry("30973603", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("08591801", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("15792430", "IDENTITY_CARD", "GW"));
        ENTRIES.add(new Entry("15732954", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("13522266", "IDENTITY_CARD", "FR"));
        ENTRIES.add(new Entry("14048728", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("18014854", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("13856390", "IDENTITY_CARD", "AO"));
        ENTRIES.add(new Entry("31649630", "IDENTITY_CARD", "PT"));
        ENTRIES.add(new Entry("30653228", "IDENTITY_CARD", "BG"));
        ENTRIES.add(new Entry("31322463", "IDENTITY_CARD", "BR"));
        ENTRIES.add(new Entry("31501986", "IDENTITY_CARD", "AO"));

    }

    private static final Object FD_NIF = "502736208";
    private static final String FD_NAME = "Faculdade de Direito de Lisboa";
    
    @Override
    public void runTask() throws Exception {
        doIt();

       throw new RuntimeException("Abort!");
    }

    private void doIt() {
        FinantialInstitution finantialInstitution = FinantialInstitution.findAll().iterator().next();
        
        if(!FD_NIF.equals(finantialInstitution.getFiscalNumber())) {
            throw new RuntimeException("not expected finantial institution");
        }

        if(!finantialInstitution.getName().startsWith(FD_NAME)) {
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