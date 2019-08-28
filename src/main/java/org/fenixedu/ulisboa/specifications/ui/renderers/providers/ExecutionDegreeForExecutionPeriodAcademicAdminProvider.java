package org.fenixedu.ulisboa.specifications.ui.renderers.providers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.interfaces.HasExecutionSemester;
import org.fenixedu.academic.predicate.AcademicPredicates;

import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

public class ExecutionDegreeForExecutionPeriodAcademicAdminProvider implements DataProvider {

    @Override
    public Object provide(Object source, Object currentValue) {
        final List<ExecutionDegree> executionDegrees = new ArrayList<ExecutionDegree>();

        final HasExecutionSemester hasExecutionSemester = (HasExecutionSemester) source;
        final ExecutionInterval executionPeriod = hasExecutionSemester.getExecutionPeriod();
        if (executionPeriod != null) {
            final ExecutionYear executionYear = executionPeriod.getExecutionYear();
            executionDegrees.addAll(executionYear.getExecutionDegreesSet());
        }

        
        TreeSet<ExecutionDegree> result =
                new TreeSet<ExecutionDegree>(Comparator.comparing(ExecutionDegree::getPresentationName));

        // ist150958: eliminate degrees for which there are no permissions
        for (ExecutionDegree executionDegree : executionDegrees) {
            if (AcademicPredicates.MANAGE_EXECUTION_COURSES.evaluate(executionDegree.getDegree())) {
                result.add(executionDegree);
            }
        }
        return result;
    }

    @Override
    public Converter getConverter() {
        return new DomainObjectKeyConverter();
    }

}
