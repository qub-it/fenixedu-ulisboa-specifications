/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoes@qub-it.com
 *               jnpa@reitoria.ulisboa.pt
 *
 *
 * This file is part of FenixEdu QubDocs.
 *
 * FenixEdu QubDocs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu QubDocs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu QubDocs.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fenixedu.ulisboa.specifications.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.StudentNumber;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;

public class ULisboaServiceRequestRegistrationBean implements IBean {

    private static final int SHOW_RESULTS_LIMIT = 100;
    private Student student;
    private Registration registration;
    private String studentSearchText;
    private List<TupleDataSourceBean> studentDataSource;
    private List<TupleDataSourceBean> registrationDataSource;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(final Registration registration) {
        this.registration = registration;
    }

    public List<TupleDataSourceBean> getStudentDataSource() {
        return studentDataSource;
    }

    public void setStudentDataSource(Collection<Student> studentDataSource) {
        this.studentDataSource = studentDataSource.stream().map(s -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(s.getExternalId());
            tuple.setText(s.getNumber() + " - " + s.getName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getRegistrationDataSource() {
        return registrationDataSource;
    }

    public void setRegistrationDataSource(Collection<Registration> registrationDataSource) {
        this.registrationDataSource = registrationDataSource.stream().map(r -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(r.getExternalId());
            tuple.setText("[" + r.getDegree().getCode() + "] " + r.getDegree().getPresentationName() + " ("
                    + r.getStartDate().toString("yyyy-MM-dd") + ") - " + r.getActiveStateTypeEnum().getDescription());
            return tuple;
        }).collect(Collectors.toList());
    }

    public ULisboaServiceRequestRegistrationBean() {
    }

    public ULisboaServiceRequestRegistrationBean(final Student student) {
        this();
        setStudent(student);
        setRegistrationDataSource(student.getRegistrationsSet());
    }

    public void updateModelLists() {
        if (StringUtils.isNotBlank(studentSearchText) && studentSearchText.length() > 3) {
            if (studentSearchText.matches("[0-9]+")) {
                List<StudentNumber> studentNumbers = Bennu.getInstance().getStudentNumbersSet().stream()
                        .filter(x -> x.getNumber().toString().contains(studentSearchText)).limit(SHOW_RESULTS_LIMIT)
                        .collect(Collectors.toList());
                setStudentDataSource(studentNumbers.stream().map(x -> x.getStudent()).collect(Collectors.toList()));
            } else if (studentSearchText.matches("[a-zA-Z ]+")) {
                Collection<Person> persons = Person.findPerson(studentSearchText, SHOW_RESULTS_LIMIT);
                setStudentDataSource(persons.stream().filter(p -> p.getStudent() != null).map(p -> p.getStudent())
                        .collect(Collectors.toList()));
            } else {
                List<Student> result = Person.readAllPersons().stream().filter(p -> p.getStudent() != null).filter(p -> {
                    String value = p.getStudent().getNumber() + " " + p.getName();
                    return value.contains(studentSearchText);
                }).limit(SHOW_RESULTS_LIMIT).map(p -> p.getStudent()).collect(Collectors.toList());
                setStudentDataSource(result);
            }
        }

        if (student != null) {
            setRegistrationDataSource(student.getRegistrationsSet());
        }
    }

}
