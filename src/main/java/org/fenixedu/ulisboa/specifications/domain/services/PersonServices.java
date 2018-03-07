package org.fenixedu.ulisboa.specifications.domain.services;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;

public class PersonServices extends org.fenixedu.academic.domain.person.services.PersonServices {

    static public void setPersonnelNumber(final Person person, final String number) {
        PersonUlisboaSpecifications.findOrCreate(person).setPersonnelNumber(number);
    }

    static public String getPersonnelNumber(final Person person) {
        return person.getPersonUlisboaSpecifications() == null ? null : person.getPersonUlisboaSpecifications()
                .getPersonnelNumber();
    }
}
