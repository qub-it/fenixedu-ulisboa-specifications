/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: luis.egidio@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.domain.evaluation.config.MarkSheetSettings;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheetStateEnum;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CompetenceCourseMarkSheetBean implements IBean {

    private CompetenceCourseMarkSheet competenceCourseMarkSheet;

    private EvaluationSeason evaluationSeason;
    private List<TupleDataSourceBean> evaluationSeasonDataSource;

    private LocalDate evaluationDate;

    private ExecutionSemester executionSemester;
    private List<TupleDataSourceBean> executionSemesterDataSource;

    private CompetenceCourse competenceCourse;
    private List<TupleDataSourceBean> competenceCourseDataSource;

    private ExecutionCourse executionCourse;
    private List<TupleDataSourceBean> executionCourseDataSource;

    private Person certifier;
    private List<TupleDataSourceBean> certifierDataSource;

    private Set<Shift> shifts;
    private List<TupleDataSourceBean> shiftsDataSource;

    private CompetenceCourseMarkSheetStateEnum markSheetState;
    private List<TupleDataSourceBean> markSheetStateDataSource;

    private GradeScale gradeScale;
    private List<TupleDataSourceBean> gradeScaleDataSource;

    private String reason;

    private List<MarkBean> evaluations;

    private boolean byTeacher = false;

    public CompetenceCourseMarkSheet getCompetenceCourseMarkSheet() {
        return competenceCourseMarkSheet;
    }

    private void setCompetenceCourseMarkSheet(final CompetenceCourseMarkSheet input) {
        this.competenceCourseMarkSheet = input;
    }

    public EvaluationSeason getEvaluationSeason() {
        return evaluationSeason;
    }

    public void setEvaluationSeason(EvaluationSeason value) {
        evaluationSeason = value;
    }

    public List<TupleDataSourceBean> getEvaluationSeasonDataSource() {
        return evaluationSeasonDataSource;
    }

    public void setEvaluationSeasonDataSource(final Set<EvaluationSeason> value) {
        this.evaluationSeasonDataSource = value.stream().sorted(EvaluationSeasonServices.SEASON_ORDER_COMPARATOR).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(EvaluationSeasonServices.getDescriptionI18N(x).getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public ExecutionSemester getExecutionSemester() {
        return executionSemester;
    }

    public List<TupleDataSourceBean> getExecutionSemesterDataSource() {
        return executionSemesterDataSource;
    }

    public void setExecutionSemester(ExecutionSemester executionSemester) {
        this.executionSemester = executionSemester;
    }

    public void setExecutionSemesterDataSource(final Set<ExecutionSemester> value) {
        this.executionSemesterDataSource = value.stream().sorted(ExecutionSemester.COMPARATOR_BY_BEGIN_DATE.reversed()).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getQualifiedName());

            return tuple;

        }).collect(Collectors.toList());
    }

    public CompetenceCourse getCompetenceCourse() {
        return competenceCourse;
    }

    public List<TupleDataSourceBean> getCompetenceCourseDataSource() {
        return competenceCourseDataSource;
    }

    public void setCompetenceCourse(CompetenceCourse competenceCourse) {
        this.competenceCourse = competenceCourse;
    }

    private void updateCompetenceCourseDataSource() {
        final Set<CompetenceCourse> value;
        if (getExecutionCourse() != null) {
            value = getExecutionCourse().getAssociatedCurricularCoursesSet().stream().map(e -> e.getCompetenceCourse())
                    .collect(Collectors.toSet());
        } else if (getExecutionSemester() != null) {
            value = getExecutionSemester().getAssociatedExecutionCoursesSet().stream()
                    .flatMap(e -> e.getCompetenceCourses().stream()).collect(Collectors.toSet());
        } else {
            value = Sets.newHashSet();
        }

        this.competenceCourseDataSource = value.stream().sorted(CompetenceCourse.COMPETENCE_COURSE_COMPARATOR_BY_NAME).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getCode() + " - " + (x.getName().replace("'", " ").replace("\"", " ")));

            return tuple;

        }).collect(Collectors.toList());
    }

    public Person getCertifier() {
        return certifier;
    }

    public List<TupleDataSourceBean> getCertifierDataSource() {
        return certifierDataSource;
    }

    public void setCertifier(Person certifier) {
        this.certifier = certifier;
    }

    public void updateCertifierDataSource() {

        // inspect professorships
        final Set<Professorship> professorships = getFilteredExecutionCourses(getExecutionCourse())
                .flatMap(e -> e.getProfessorshipsSet().stream()).collect(Collectors.toSet());
        final Set<Person> teachers = professorships.stream().map(p -> p.getPerson()).collect(Collectors.toSet());
        final Set<Person> responsibles =
                professorships.stream().filter(p -> p.isResponsibleFor()).map(p -> p.getPerson()).collect(Collectors.toSet());

        // set available options
        final Set<Person> available;
        if (isByTeacher()) {

            if (MarkSheetSettings.getInstance().getAllowTeacherToChooseCertifier()) {
                available = teachers;
            } else {
                available = Sets.newHashSet(Authenticate.getUser().getPerson());
            }

        } else {
            available = Bennu.getInstance().getTeachersSet().stream().map(t -> t.getPerson()).collect(Collectors.toSet());
        }

        // build data source
        this.certifierDataSource = available.stream().sorted(Person.COMPARATOR_BY_NAME).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());

            final String prefix = isByTeacher() ? "" : responsibles.contains(x) ? "+* " : teachers.contains(x) ? "* " : "";
            tuple.setText(prefix + x.getFirstAndLastName() + " (" + x.getUsername() + ")");

            return tuple;

        }).collect(Collectors.toList());
    }

    public Set<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(Set<Shift> shifts) {
        this.shifts = shifts;
    }

    public List<TupleDataSourceBean> getShiftsDataSource() {
        return shiftsDataSource;
    }

    public void setShiftsDataSource(final Set<Shift> value) {
        this.shiftsDataSource = value.stream().sorted(Shift.SHIFT_COMPARATOR_BY_NAME).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getNome());

            return tuple;

        }).collect(Collectors.toList());
    }

    public ExecutionCourse getExecutionCourse() {
        return executionCourse;
    }

    public List<TupleDataSourceBean> getExecutionCourseDataSource() {
        return executionCourseDataSource;
    }

    public void setExecutionCourse(ExecutionCourse executionCourse) {
        this.executionCourse = executionCourse;
    }

    public void setExecutionCourseDataSource(final Set<ExecutionCourse> value) {

        this.executionCourseDataSource = value.stream().sorted(ExecutionCourse.EXECUTION_COURSE_NAME_COMPARATOR).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());

            final String name = x.getNameI18N().getContent();
            tuple.setText(name.replace("'", " ").replace("\"", " ") + " [" + x.getAssociatedCurricularCoursesSet().stream()
                    .map(i -> i.getDegree().getCode()).collect(Collectors.joining("; ")) + "]");

            return tuple;

        }).collect(Collectors.toList());
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<MarkBean> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<MarkBean> evaluations) {
        this.evaluations = evaluations;
    }

    public boolean isByTeacher() {
        return byTeacher;
    }

    public void setByTeacher(final boolean input) {
        this.byTeacher = input;
        updateCertifierDataSource();
    }

    public CompetenceCourseMarkSheetStateEnum getMarkSheetState() {
        return markSheetState;
    }

    public List<TupleDataSourceBean> getMarkSheetStateDataSource() {
        return markSheetStateDataSource;
    }

    public void setMarkSheetState(CompetenceCourseMarkSheetStateEnum markSheetState) {
        this.markSheetState = markSheetState;
    }

    public void setMarkSheetStateDataSource(final Set<CompetenceCourseMarkSheetStateEnum> value) {
        this.markSheetStateDataSource = value.stream()
                .map(x -> new TupleDataSourceBean(x.name(), x.getDescriptionI18N().getContent())).collect(Collectors.toList());
    }

    public GradeScale getGradeScale() {
        return gradeScale;
    }

    public List<TupleDataSourceBean> getGradeScaleDataSource() {
        return gradeScaleDataSource;
    }

    public void setGradeScale(GradeScale gradeScale) {
        this.gradeScale = gradeScale;
    }

    public void setGradeScaleDataSource(final List<GradeScale> value) {
        this.gradeScaleDataSource =
                value.stream().map(x -> new TupleDataSourceBean(x.name(), x.getDescription())).collect(Collectors.toList());
    }

    public CompetenceCourseMarkSheetBean() {
        update();
    }

    public CompetenceCourseMarkSheetBean(final CompetenceCourseMarkSheet markSheet) {
        setCompetenceCourseMarkSheet(markSheet);
        setEvaluationDate(markSheet.getEvaluationDate());
        setGradeScale(markSheet.getGradeScale());
        setEvaluationSeason(markSheet.getEvaluationSeason());
        setExecutionSemester(markSheet.getExecutionSemester());
        setCompetenceCourse(markSheet.getCompetenceCourse());
        setCertifier(markSheet.getCertifier());
        setShifts(Sets.newHashSet(markSheet.getShiftSet()));
        setExecutionCourse(markSheet.getExecutionCourse());

        setEvaluations(buildEvaluations());

        update();
    }

    public CompetenceCourseMarkSheetBean(final ExecutionCourse executionCourse, final Person certifier) {
        setExecutionSemester(executionCourse.getExecutionPeriod());
        setCompetenceCourse(executionCourse.getCompetenceCourses().size() == 1 ? executionCourse.getCompetenceCourses().iterator()
                .next() : null);
        setCertifier(certifier);
        setExecutionCourse(executionCourse);

        update();
    }

    public void update() {
        setExecutionSemesterDataSource(ExecutionSemester.readNotClosedExecutionPeriods().stream().collect(Collectors.toSet()));

        updateCompetenceCourseDataSource();

        setExecutionCourseDataSource(getFilteredExecutionCourses(null).collect(Collectors.toSet()));

        setEvaluationSeasonDataSource(EvaluationSeasonServices.findByActive(true).collect(Collectors.toSet()));

        updateCertifierDataSource();

        setShiftsDataSource(getFilteredExecutionCourses(getExecutionCourse()).flatMap(e -> e.getAssociatedShifts().stream())
                .collect(Collectors.toSet()));

        setMarkSheetStateDataSource(Sets.newHashSet(CompetenceCourseMarkSheetStateEnum.values()));

        setGradeScaleDataSource(Lists.newArrayList(GradeScale.TYPE20, GradeScale.TYPEQUALITATIVE, GradeScale.TYPEAPT));
    }

    private Stream<ExecutionCourse> getFilteredExecutionCourses(final ExecutionCourse toFilter) {

        if (getCompetenceCourse() == null || getExecutionSemester() == null) {
            return Collections.<ExecutionCourse> emptyList().stream();
        }

        return getCompetenceCourse().getExecutionCoursesByExecutionPeriod(getExecutionSemester()).stream()
                .filter(e -> toFilter == null || e == toFilter);
    }

    private List<MarkBean> buildEvaluations() {
        final List<MarkBean> result = Lists.newArrayList();

        if (getCompetenceCourseMarkSheet() != null) {

            getCompetenceCourseMarkSheet().getExecutionCourseEnrolmentsNotInAnyMarkSheet()
                    .forEach(e -> result.add(new MarkBean(getCompetenceCourseMarkSheet(), e)));

            getCompetenceCourseMarkSheet().getEnrolmentEvaluationSet().forEach(e -> {

                final MarkBean markBean = new MarkBean(getCompetenceCourseMarkSheet(), e.getEnrolment());
                markBean.setGradeValue(e.getGradeValue());
                result.add(markBean);

            });

        }

        Collections.sort(result);

        return result;
    }

    public void validateEvaluations() {

        for (final MarkBean markBean : getEvaluations()) {
            markBean.setErrorMessage(null);
            markBean.validate();
        }

        if (getEvaluations().stream().anyMatch(e -> e.getErrorMessage() != null)) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetBean.invalid.evaluations");
        }

    }

    public void updateEnrolmentEvaluations() {
        validateEvaluations();

        for (final MarkBean markBean : getEvaluations()) {
            markBean.updateEnrolmentEvaluation();
        }

    }

}
