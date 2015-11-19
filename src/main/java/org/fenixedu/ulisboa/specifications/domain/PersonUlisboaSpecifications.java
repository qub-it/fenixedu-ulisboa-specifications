/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: joao.roxo@qub-it.com
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    
    public void delete() {
        setPerson(null);
        deleteDomainObject();
    }

    @ConsistencyPredicate
    private boolean checkHasPerson() {
        return getPerson() != null;
    }
}
