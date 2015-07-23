package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.Person;

import pt.ist.fenixframework.consistencyPredicates.ConsistencyPredicate;

public class PersonUlisboaSpecifications extends PersonUlisboaSpecifications_Base {
    private PersonUlisboaSpecifications(Person person) {
        super();
        setPerson(person);
    }

    public static PersonUlisboaSpecifications findOrCreate(Person person) {
        PersonUlisboaSpecifications personUlisboaSpecifications = person.getPersonUlisboaSpecifications();
        if (personUlisboaSpecifications != null) {
            return personUlisboaSpecifications;
        } else {
            return new PersonUlisboaSpecifications(person);
        }
    }

    @ConsistencyPredicate
    private boolean checkHasPerson() {
        return getPerson() != null;
    }
}
