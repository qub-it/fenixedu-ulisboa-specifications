package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;

import pt.ist.fenixframework.consistencyPredicates.ConsistencyPredicate;

public class PersonUlisboaSpecificationsByExecutionYear extends PersonUlisboaSpecificationsByExecutionYear_Base {

    public PersonUlisboaSpecificationsByExecutionYear() {
        super();
    }

    public PersonUlisboaSpecificationsByExecutionYear(final PersonUlisboaSpecifications personUl,
            final ExecutionYear executionYear) {
        super();
        setPersonUl(personUl);
        setExecutionYear(executionYear);
    }

    public void delete() {
        setPersonUl(null);
        setExecutionYear(null);
        setHouseholdSalarySpan(null);
        setProfessionTimeType(null);

        deleteDomainObject();
    }

    @ConsistencyPredicate
    private boolean checkHasPerson() {
        return getPersonUl() != null;
    }

    public static PersonUlisboaSpecificationsByExecutionYear findOrCreate(final Person person,
            final ExecutionYear executionYear) {

        PersonUlisboaSpecifications personUlisboaSpecifications = person.getPersonUlisboaSpecifications();
        if (personUlisboaSpecifications == null) {
            personUlisboaSpecifications = PersonUlisboaSpecifications.findOrCreate(person);
        }

        PersonUlisboaSpecificationsByExecutionYear result =
                personUlisboaSpecifications.getPersonUlisboaSpecificationsByExcutionYear(executionYear);
        if (result == null) {
            result = new PersonUlisboaSpecificationsByExecutionYear(personUlisboaSpecifications, executionYear);
        }

        return result;
    }
}
