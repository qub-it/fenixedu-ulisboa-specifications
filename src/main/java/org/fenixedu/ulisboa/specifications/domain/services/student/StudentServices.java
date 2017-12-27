package org.fenixedu.ulisboa.specifications.domain.services.student;

import java.util.List;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;

public class StudentServices {

    public static List<Registration> findActiveFirstTimeRegistrationsOrWithEnrolments(final ExecutionYear executionYear,
            final Student student) {
        final List<Registration> result = Lists.newArrayList();

        for (final Registration registration : student.getRegistrationsSet()) {
            if (!registration.isActive()) {
                continue;
            }

            if (registration.getEnrolments(executionYear).isEmpty() && registration.getRegistrationYear() != executionYear) {
                continue;
            }

            result.add(registration);
        }

        Collections.sort(result, Registration.COMPARATOR_BY_START_DATE);

        return result;
    }
    
}
