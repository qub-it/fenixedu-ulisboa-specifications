package org.fenixedu.ulisboa.specifications.domain.services.teacher;

import java.util.Collection;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.dto.teacher.executionCourse.NextPossibleSummaryLessonsAndDatesBean;
import org.fenixedu.academic.predicate.AccessControl;

public class SummaryServices {

    public static Collection<NextPossibleSummaryLessonsAndDatesBean> filterSummariesManagementForLoggedPerson(
            final Collection<NextPossibleSummaryLessonsAndDatesBean> possibleSummaries) {
        return possibleSummaries.stream().filter(bean -> isShiftSummariesManageableByLoggedPerson(bean.getShift()))
                .sorted(NextPossibleSummaryLessonsAndDatesBean.COMPARATOR_BY_DATE_AND_HOUR).collect(Collectors.toList());
    }

    public static boolean isShiftSummariesManageableByLoggedPerson(final Shift shift) {
        final Person loggedPerson = AccessControl.getPerson();
        return shift.getAssociatedShiftProfessorshipSet().isEmpty() || shift.getAssociatedShiftProfessorshipSet().stream()
                .anyMatch(sp -> sp.getProfessorship().getPerson() == loggedPerson);
    }

}
