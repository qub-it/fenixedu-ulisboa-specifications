package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments;

import java.util.Comparator;
import java.util.Optional;

import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;

public class AlmostFilledClassesComparator implements Comparator<SchoolClass> {
    // Return at the beggining the course which is the most filled, but still has space
    // This allows a "fill first" kind of school class scheduling

    @Override
    public int compare(SchoolClass sc1, SchoolClass sc2) {
        Integer sc1Vacancies = getFreeVacancies(sc1);
        Integer sc2Vacancies = getFreeVacancies(sc2);

        return sc1Vacancies.compareTo(sc2Vacancies) != 0 ? sc1Vacancies.compareTo(sc2Vacancies) : SchoolClass.COMPARATOR_BY_NAME
                .compare(sc1, sc2);
    }

    public static Integer getFreeVacancies(SchoolClass schoolClass) {
        final Optional<Shift> minShift = schoolClass.getAssociatedShiftsSet().stream()
                .min((s1, s2) -> getShiftVacancies(s1).compareTo(getShiftVacancies(s2)));
        return minShift.isPresent() ? getShiftVacancies(minShift.get()) : 0;
    }

    public static Integer getShiftVacancies(final Shift shift) {
        return shift.getLotacao() - shift.getStudentsSet().size();
    }

}
