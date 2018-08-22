package org.fenixedu.ulisboa.specifications.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
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

    @Atomic
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

    @Override
    public void setFormsAnswered(String formsAnswered) {
        throw new ULisboaSpecificationsDomainException(
                "error.PersonUlisboaSpecificationsByExecutionYear.wrong.method.setAllFormsAnswered");
    }

    @Override
    public String getFormsAnswered() {
        throw new ULisboaSpecificationsDomainException(
                "error.PersonUlisboaSpecificationsByExecutionYear.wrong.method.getAllFormsAnswered");
    }

    //use set to remove duplicates
    @Atomic
    public void setAllFormsAnswered(List<String> formsAnswered) {
        super.setFormsAnswered(Sets.newHashSet(formsAnswered).stream().collect(Collectors.joining(";")));
    }

    @Atomic
    public void addFormsAnswered(String formAnswered) {
        String tmp = super.getFormsAnswered();
        if (tmp == null) {
            tmp = "";
        }
        tmp += ";" + formAnswered;

        Set<String> result = Sets.newHashSet(tmp.split(";"));
        super.setFormsAnswered(result.stream().collect(Collectors.joining(";")));
    }

    public List<String> getAllFormsAnswered() {
        String tmp = super.getFormsAnswered();

        if (tmp == null) {
            return Collections.emptyList();
        }

        String[] formsAnswered = tmp.split(";");
        return Arrays.asList(formsAnswered);
    }

    public boolean isFormAnswered(String classSimpleName) {
        String formsAnswered = super.getFormsAnswered();
        if (formsAnswered == null || classSimpleName == null) {
            return false;
        }

        formsAnswered = formsAnswered.replaceAll("BlueRecord", "").replaceAll("Controler", "Controller");
        classSimpleName = classSimpleName.replaceAll("BlueRecord", "").replaceAll("Controler", "Controller");

        return formsAnswered.contains(classSimpleName);
    }

}
