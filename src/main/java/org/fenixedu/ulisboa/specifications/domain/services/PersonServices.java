package org.fenixedu.ulisboa.specifications.domain.services;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;

public class PersonServices {

    static public void setPersonnelNumber(final Person person, final String number) {
        PersonUlisboaSpecifications.findOrCreate(person).setPersonnelNumber(number);
    }

    static public String getPersonnelNumber(final Person person) {
        return person.getPersonUlisboaSpecifications() == null ? null : person.getPersonUlisboaSpecifications()
                .getPersonnelNumber();
    }

    static public String getDisplayName(final Person input) {
        String result = "";

        if (input != null && input.getProfile() != null) {
            result = input.getProfile().getDisplayName();

            if (result.equals(input.getName()) || !result.trim().contains(" ")) {
                result = input.getFirstAndLastName();
            }
        }

        return result;
    }
}
