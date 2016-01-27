package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.marksheet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Person;
import org.joda.time.LocalDate;

public abstract class AbstractSeasonReport {

    private EvaluationSeason season;

    private LocalDate evaluationDate;

    protected AbstractSeasonReport(final EvaluationSeason season, final LocalDate evaluationDate) {
        this.season = season;
        this.evaluationDate = evaluationDate;
    }

    public Set<String> getResponsibleNames() {
        final Set<String> result = new HashSet<String>();

        for (final Person person : getResponsibles()) {
            result.add(person.getName());
        }

        return result;

    }

    public Set<String> getResponsibleEmails() {
        final Set<String> result = new HashSet<String>();

        for (final Person person : getResponsibles()) {
            if (person.hasDefaultEmailAddress()) {
                result.add(person.getDefaultEmailAddressValue());
            }
        }

        return result;
    }

    public Integer getTotalStudents() {
        return getEvaluatedStudents() + getNotEvaluatedStudents();
    }

    public EvaluationSeason getSeason() {
        return season;
    }

    public LocalDate getEvaluationDate() {
        return this.evaluationDate;
    }

    abstract public Integer getNotEvaluatedStudents();

    abstract public Integer getEvaluatedStudents();

    abstract public Integer getMarksheetsToConfirm();

    abstract public Collection<Person> getResponsibles();

    abstract public ExecutionSemester getExecutionSemester();

}
