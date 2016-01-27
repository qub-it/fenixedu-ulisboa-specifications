package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.marksheet;

import java.util.Collection;
import java.util.HashSet;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.joda.time.LocalDate;

public class CurricularCourseSeasonReport extends AbstractSeasonReport {

    private CurricularCourse curricularCourse;

    private ExecutionSemester executionSemester;

    private Integer notEvaluatedStudents = 0;

    private Integer evaluatedStudents = 0;

    private Integer marksheetsToConfirm = 0;

    public CurricularCourseSeasonReport(final CurricularCourse curricularCourse, final EvaluationSeason season,
            final ExecutionSemester executionSemester, final LocalDate evaluationDate) {
        super(season, evaluationDate);
        this.curricularCourse = curricularCourse;
        this.executionSemester = executionSemester;
    }

    public CurricularCourse getCurricularCourse() {
        return curricularCourse;
    }

    @Override
    public Collection<Person> getResponsibles() {
        final Collection<Person> result = new HashSet<Person>();

        for (final ExecutionCourse executionCourse : getCurricularCourse()
                .getExecutionCoursesByExecutionPeriod(getExecutionSemester())) {
            for (final Professorship professorship : executionCourse.getProfessorshipsSet()) {
                if (professorship.isResponsibleFor()) {
                    result.add(professorship.getPerson());
                }
            }
        }

        return result;
    }

    @Override
    public Integer getNotEvaluatedStudents() {
        return notEvaluatedStudents;
    }

    public void setNotEvaluatedStudents(Integer notEvaluatedStudents) {
        this.notEvaluatedStudents = notEvaluatedStudents;
    }

    @Override
    public Integer getEvaluatedStudents() {
        return evaluatedStudents;
    }

    public void setEvaluatedStudents(Integer evaluatedStudents) {
        this.evaluatedStudents = evaluatedStudents;
    }

    @Override
    public Integer getMarksheetsToConfirm() {
        return marksheetsToConfirm;
    }

    public void setMarksheetsToConfirm(Integer marksheetsToConfirm) {
        this.marksheetsToConfirm = marksheetsToConfirm;
    }

    @Override
    public ExecutionSemester getExecutionSemester() {
        return executionSemester;
    }

}
