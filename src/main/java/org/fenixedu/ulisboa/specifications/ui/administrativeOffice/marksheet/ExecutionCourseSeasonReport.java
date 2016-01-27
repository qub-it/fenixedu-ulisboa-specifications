package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.marksheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.joda.time.LocalDate;

public class ExecutionCourseSeasonReport extends AbstractSeasonReport {

    private ExecutionCourse executionCourse;

    private List<CurricularCourseSeasonReport> entries = new ArrayList<CurricularCourseSeasonReport>();

    public ExecutionCourseSeasonReport(final ExecutionCourse executionCourse, final EvaluationSeason season,
            final LocalDate evaluationDate, final Collection<CurricularCourseSeasonReport> entries) {
        super(season, evaluationDate);
        this.executionCourse = executionCourse;
        this.entries.addAll(entries);
    }

    @Override
    public Collection<Person> getResponsibles() {
        final Collection<Person> result = new HashSet<Person>();

        for (final Professorship professorship : getExecutionCourse().getProfessorshipsSet()) {
            if (professorship.isResponsibleFor()) {
                result.add(professorship.getPerson());
            }
        }

        return result;
    }

    @Override
    public Integer getNotEvaluatedStudents() {
        int result = 0;

        for (final CurricularCourseSeasonReport entry : this.entries) {
            result += entry.getNotEvaluatedStudents().intValue();
        }

        return result;
    }

    @Override
    public Integer getEvaluatedStudents() {
        int result = 0;

        for (final CurricularCourseSeasonReport entry : this.entries) {
            result += entry.getEvaluatedStudents().intValue();
        }

        return result;
    }

    @Override
    public Integer getMarksheetsToConfirm() {
        int result = 0;

        for (final CurricularCourseSeasonReport entry : this.entries) {
            result += entry.getMarksheetsToConfirm().intValue();
        }

        return result;
    }

    @Override
    public ExecutionSemester getExecutionSemester() {
        return getExecutionCourse().getExecutionPeriod();
    }

    public ExecutionCourse getExecutionCourse() {
        return this.executionCourse;
    }

}
