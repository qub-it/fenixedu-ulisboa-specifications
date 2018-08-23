package org.fenixedu.ulisboa.specifications.dto.student.enrollment.bolonha;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CycleCourseGroup;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.security.Authenticate;

@SuppressWarnings("serial")
public class CycleEnrolmentBean extends org.fenixedu.academic.dto.student.enrollment.bolonha.CycleEnrolmentBean {

    public CycleEnrolmentBean(final StudentCurricularPlan scp, final ExecutionSemester semester, final CycleType source,
            CycleType toEnrol) {
        super(scp, semester, source, toEnrol);
    }

    @Override
    public Collection<CycleCourseGroup> getCycleDestinationAffinities() {
        final Collection<CycleCourseGroup> affinities =
                getDegreeCurricularPlan().getDestinationAffinities(getSourceCycleAffinity());

        if (affinities.isEmpty()) {
            return Collections.emptyList();
        }

        final Student student = getStudent();
        if (student == null) {
            return affinities;
        }

        final List<CycleCourseGroup> result = new ArrayList<CycleCourseGroup>();
        for (final CycleCourseGroup cycleCourseGroup : affinities) {
            final DegreeCurricularPlan degreeCurricularPlan = cycleCourseGroup.getParentDegreeCurricularPlan();

            // qubExtensions
            if (degreeCurricularPlan.getAcademicEnrolmentPeriodsSet().stream()
                    .anyMatch(p -> p.isOpen() && p.isForCurricularCourses())) {
                result.add(cycleCourseGroup);
            }
            
        }
        return result;
    }

    // qubExtensions
    static private Student getStudent() {
        return Authenticate.getUser().getPerson().getStudent();
    }

    /*
     * Copy from super class 
     */
    private DegreeCurricularPlan getDegreeCurricularPlan() {
        return getStudentCurricularPlan().getDegreeCurricularPlan();
    }

}
