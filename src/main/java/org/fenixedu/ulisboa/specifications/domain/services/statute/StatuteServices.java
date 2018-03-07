package org.fenixedu.ulisboa.specifications.domain.services.statute;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.StatuteType;

import com.google.common.collect.Sets;

public class StatuteServices extends org.fenixedu.academic.domain.student.services.StatuteServices {

    static public Collection<StatuteType> findStatuteTypes(final Registration registration, final ExecutionYear executionYear) {

        final Set<StatuteType> result = Sets.newHashSet();
        for (final ExecutionSemester executionSemester : executionYear.getExecutionPeriodsSet()) {
            result.addAll(findStatuteTypes(registration, executionSemester));
        }

        return result;

    }

    static public String getStatuteTypesDescription(final Registration registration, final ExecutionSemester executionSemester) {
        return findStatuteTypes(registration, executionSemester).stream().map(s -> s.getName().getContent())
                .collect(Collectors.joining(", "));

    }

    static public String getCodeAndName(StatuteType statuteType) {
        return statuteType.getCode() + " - " + statuteType.getName().getContent();
    }

}
