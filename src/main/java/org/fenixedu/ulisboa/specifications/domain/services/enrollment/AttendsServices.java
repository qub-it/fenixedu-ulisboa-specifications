package org.fenixedu.ulisboa.specifications.domain.services.enrollment;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

abstract public class AttendsServices {

    static private final Logger logger = LoggerFactory.getLogger(AttendsServices.class);

    @Atomic
    static public Attends createAttend(final Enrolment enrolment, final ExecutionCourse executionCourse) {
        Attends result = checkAttendsInExecutionCourse(enrolment, executionCourse);

        if (result == null) {
            final Registration registration = enrolment.getRegistration();

            // check attends in student
            result = enrolment.getStudent().getAttends(executionCourse);
            if (result == null) {
                result = new Attends(registration, executionCourse);

            } else {
                result.setRegistration(registration);
            }
        }

        result.setEnrolment(enrolment);

        return result;
    }

    static private Attends checkAttendsInExecutionCourse(final Enrolment enrolment, final ExecutionCourse executionCourse) {
        Attends result = null;

        final Registration registration = enrolment.getRegistration();
        final List<Attends> collection = executionCourse.getAttendsSet().stream().filter(i -> i.getRegistration() == registration)
                .collect(Collectors.toList());
        if (collection.size() == 1) {
            result = collection.iterator().next();
        } else if (collection.size() > 1) {
            throw new DomainException("Enrolment.found.two.attends.for.same.execution.period");
        }

        return result;
    }

}
