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

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.academic.domain.evaluation.EvaluationServices;
import org.fenixedu.academic.domain.evaluation.config.MarkSheetSettings;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheetChangeRequestStateEnum;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheetStateEnum;
import org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.academic.domain.evaluation.season.rule.EvaluationSeasonRule;
import org.fenixedu.academic.domain.evaluation.season.rule.EvaluationSeasonShiftType;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.services.ExecutionCourseServices;
import org.fenixedu.ulisboa.specifications.domain.services.PersonServices;
import org.fenixedu.ulisboa.specifications.domain.services.evaluation.EnrolmentEvaluationServices;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.report.AbstractSeasonReport;
import org.fenixedu.ulisboa.specifications.service.evaluation.MarkSheetStatusReportService;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
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

    private Evaluation courseEvaluation;
    private List<TupleDataSourceBean> courseEvaluationDataSource;
    private Boolean courseEvaluationsAvailable = true;

    private Person certifier;
    private Set<Person> responsibles = Sets.newHashSet();
    private List<TupleDataSourceBean> certifierDataSource;

    private Set<Shift> shifts;
    private List<TupleDataSourceBean> shiftsDataSource;

    private CompetenceCourseMarkSheetStateEnum markSheetState;
    private List<TupleDataSourceBean> markSheetStateDataSource;

    private GradeScale gradeScale;
    private List<TupleDataSourceBean> gradeScaleDataSource;

    private CompetenceCourseMarkSheetChangeRequestStateEnum changeRequestState;
    private List<TupleDataSourceBean> changeRequestStateDataSource;

    private String reason;

    private List<MarkBean> updateGradeBeans;
    private List<MarkBean> updateGradeAvailableDateBeans;

    private boolean byTeacher = false;

    private LocalDate expireDate;

    private String changeRequestReason;

    private String changeRequestComments;

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

    public Set<EvaluationSeason> getEvaluationSeasonsToReport() {
        return getEvaluationSeason() == null ? EvaluationSeasonServices.findByActive(true).collect(Collectors.toSet()) : Sets
                .newHashSet(getEvaluationSeason());
    }

    public List<String> getReportsSummaryForCompetenceCourse() {
        final List<String> result = Lists.newArrayList();

        if (getCompetenceCourse() != null) {
            result.addAll(getReportsSummary(MarkSheetStatusReportService.getReportsForCompetenceCourse(getExecutionSemester(),
                    getCompetenceCourse(), getEvaluationSeasonsToReport())));
        }

        return result;
    }

    public List<String> getReportsSummaryForExecutionCourse() {
        final List<String> result = Lists.newArrayList();

        if (getExecutionCourse() != null) {
            result.addAll(getReportsSummary(MarkSheetStatusReportService.getReportsForExecutionCourse(getExecutionCourse())));
        }

        return result;
    }

    public List<String> getReportsSummary(final List<? extends AbstractSeasonReport> input) {

        return input.stream()
                .sorted((x, y) -> EvaluationSeasonServices.SEASON_ORDER_COMPARATOR.compare(x.getSeason(), y.getSeason()))
                .map(report -> {

                    return "<strong>" + EvaluationSeasonServices.getDescriptionI18N(report.getSeason()).getContent()
                            + "</strong>: " + Joiner.on("; ").join(

                                    reportLabelFor("totalStudents", String.valueOf(report.getTotalStudents())),

                                    reportLabelFor("notEvaluatedStudents", String.valueOf(report.getNotEvaluatedStudents())),

                                    reportLabelFor("evaluatedStudents", String.valueOf(report.getEvaluatedStudents())),

                                    reportLabelFor("marksheetsTotal", String.valueOf(report.getMarksheetsTotal())),

                                    reportLabelFor("marksheetsToConfirm", String.valueOf(report.getMarksheetsToConfirm()))

                            );

                }).collect(Collectors.toList());
    }

    static private String reportLabelFor(final String field, final String value) {
        return BundleUtil.getString(ULisboaConstants.BUNDLE, "label.MarksheetStatusReport.report." + field) + ": <strong>" + value
                + "</strong>";
    }

    public String getEvaluationDatePresentation() {
        return CompetenceCourseMarkSheet.getEvaluationDatePresentation(getEvaluationDateTime());
    }

    public DateTime getEvaluationDateTime() {
        return CompetenceCourseMarkSheet.getEvaluationDateTime(getCourseEvaluation(), getEvaluationDate());
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
        final Set<Professorship> shiftProfessorships = getShifts() == null ? Sets.newHashSet() : getShifts().stream()
                .flatMap(i -> i.getAssociatedShiftProfessorshipSet().stream().map(x -> x.getProfessorship()))
                .filter(i -> i != null).collect(Collectors.toSet());

        final Set<Professorship> professorships =
                shiftProfessorships.isEmpty() ? getFilteredExecutionCourses(getExecutionCourse())
                        .flatMap(e -> e.getProfessorshipsSet().stream()).collect(Collectors.toSet()) : shiftProfessorships;
        final Set<Person> teachers = professorships.stream().map(p -> p.getPerson()).collect(Collectors.toSet());
        this.responsibles =
                professorships.stream().filter(p -> p.isResponsibleFor()).map(p -> p.getPerson()).collect(Collectors.toSet());

        // set available options
        final Set<Person> available;
        if (isByTeacher()) {

            if (MarkSheetSettings.getInstance().getAllowTeacherToChooseCertifier()) {
                available = teachers;
            } else {
                available = Sets.newHashSet(Authenticate.getUser().getPerson());
            }

            if (MarkSheetSettings.getInstance().getLimitCertifierToResponsibleTeacher()) {

                for (final Iterator<Person> iterator = available.iterator(); iterator.hasNext();) {
                    if (!this.responsibles.contains(iterator.next())) {
                        iterator.remove();
                    }
                }
            }

        } else {
            available = Bennu.getInstance().getTeachersSet().stream().map(t -> t.getPerson()).collect(Collectors.toSet());
        }

        // build data source
        this.certifierDataSource = available.stream().sorted(Person.COMPARATOR_BY_NAME).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());

            final String prefix = isByTeacher() ? "" : this.responsibles.contains(x) ? "+* " : teachers.contains(x) ? "* " : "";
            tuple.setText(prefix + getPersonDescription(x));

            return tuple;

        }).collect(Collectors.toList());
    }

    static public String getPersonDescription(final Person input) {
        return input == null ? null : PersonServices.getDisplayName(input) + " (" + input.getUsername() + ")";
    }

    public boolean getLimitTeacherCreation() {
        return isByTeacher() && MarkSheetSettings.getInstance().getLimitCreationToResponsibleTeacher()
                && !this.responsibles.contains(Authenticate.getUser().getPerson());
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

    private void updateShiftsDataSource() {

        if (getEvaluationSeason() == null || (getCourseEvaluation() == null && !getCourseEvaluationDataSource().isEmpty())) {
            return;
        }

        // inspect professorships
        final Set<ExecutionCourse> executionCourses =
                getFilteredExecutionCourses(getExecutionCourse()).collect(Collectors.toSet());
        final Set<Professorship> professorships =
                executionCourses.stream().flatMap(e -> e.getProfessorshipsSet().stream()).collect(Collectors.toSet());

        // set available options
        Set<Shift> available;

        // only filter by professorship if any has been configured
        final Set<Shift> professorshipShifts = professorships.stream()
                .flatMap(i -> i.getAssociatedShiftProfessorshipSet().stream().map(x -> x.getShift())).collect(Collectors.toSet());
        if (isByTeacher() && !professorshipShifts.isEmpty()) {

            available = professorships.stream().filter(prof -> prof.getPerson() == Authenticate.getUser().getPerson())
                    .flatMap(prof -> prof.getAssociatedShiftProfessorshipSet().stream().map(x -> x.getShift()))
                    .collect(Collectors.toSet());
        } else {
            available = executionCourses.stream().flatMap(e -> e.getAssociatedShifts().stream()).collect(Collectors.toSet());
        }

        // filter by course evaluation
        final Set<Shift> evaluationShifts = EvaluationServices.findCourseEvaluationShifts(getCourseEvaluation());
        if (isByTeacher() && !evaluationShifts.isEmpty()) {
            available = Sets.intersection(evaluationShifts, available);
        }

        // filter by configured types
        final Set<ShiftType> allowedTypes = EvaluationSeasonRule.find(getEvaluationSeason(), EvaluationSeasonShiftType.class)
                .stream().flatMap(i -> i.getShiftTypes().getTypes().stream()).collect(Collectors.toSet());
        if (isByTeacher() && !allowedTypes.isEmpty()) {
            available = available.stream().filter(i -> !Sets.intersection(allowedTypes, i.getSortedTypes()).isEmpty())
                    .collect(Collectors.toSet());
        }

        // build data source
        this.shiftsDataSource = available.stream().sorted(Shift.SHIFT_COMPARATOR_BY_NAME).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(String.format("%s [%s]", x.getNome(), x.getShiftTypesCodePrettyPrint()));

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

        if (value.size() == 1) {
            setExecutionCourse(value.iterator().next());
        }

        this.executionCourseDataSource = value.stream().sorted(ExecutionCourse.EXECUTION_COURSE_NAME_COMPARATOR).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());

            final String name = x.getNameI18N().getContent();
            tuple.setText(name.replace("'", " ").replace("\"", " ") + " [" + getExecutionCoursePresentation(x) + "]");

            return tuple;

        }).collect(Collectors.toList());
    }

    static public String getExecutionCoursePresentation(final ExecutionCourse input) {
        return ExecutionCourseServices.getDegreeCurricularPlanPresentation(input, true);
    }

    public Evaluation getCourseEvaluation() {
        return courseEvaluation;
    }

    public void setCourseEvaluation(Evaluation courseEvaluation) {
        this.courseEvaluation = courseEvaluation;
    }

    public List<TupleDataSourceBean> getCourseEvaluationDataSource() {
        return this.courseEvaluationDataSource;
    }

    public void updateCourseEvaluationDataSource() {

        this.courseEvaluationDataSource = getFilteredExecutionCourses(getExecutionCourse()).flatMap(e -> EvaluationServices
                .findExecutionCourseEvaluations(e, getEvaluationSeason()).stream().filter(i -> i.getEvaluationDate() != null))
                .map(x -> {

                    final TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());

                    final String name = x.getPresentationName();
                    tuple.setText(name.replace("'", " ").replace("\"", " ") + " ["
                            + new SimpleDateFormat(EnrolmentEvaluationServices.EVALUATION_DATE_TIME_FORMAT)
                                    .format(x.getEvaluationDate())
                            + "]");

                    return tuple;

                }).collect(Collectors.toList());

        setCourseEvaluationsAvailable(!this.courseEvaluationDataSource.isEmpty());
    }

    public Boolean getCourseEvaluationsAvailable() {
        return courseEvaluationsAvailable;
    }

    public void setCourseEvaluationsAvailable(Boolean courseEvaluationsAvailable) {
        this.courseEvaluationsAvailable = courseEvaluationsAvailable;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<MarkBean> getUpdateGradeBeans() {
        return updateGradeBeans;
    }

    public void setUpdateGradeBeans(final List<MarkBean> input) {
        this.updateGradeBeans = input;
    }

    public List<MarkBean> getUpdateGradeAvailableDateBeans() {
        return updateGradeAvailableDateBeans;
    }

    public void setUpdateGradeAvailableDateBeans(final List<MarkBean> input) {
        this.updateGradeAvailableDateBeans = input;
    }

    public boolean isByTeacher() {
        return byTeacher;
    }

    public void setByTeacher(final boolean input) {
        this.byTeacher = input;
        updateCertifierDataSource();
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getChangeRequestReason() {
        return changeRequestReason;
    }

    public String getChangeRequestComments() {
        return changeRequestComments;
    }

    public void setChangeRequestReason(String changeRequestReason) {
        this.changeRequestReason = changeRequestReason;
    }

    public void setChangeRequestComments(String changeRequestComments) {
        this.changeRequestComments = changeRequestComments;
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

    public void setMarkSheetStateDataSource(final List<CompetenceCourseMarkSheetStateEnum> value) {
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

    public CompetenceCourseMarkSheetChangeRequestStateEnum getChangeRequestState() {
        return changeRequestState;
    }

    public List<TupleDataSourceBean> getChangeRequestStateDataSource() {
        return changeRequestStateDataSource;
    }

    public void setChangeRequestState(CompetenceCourseMarkSheetChangeRequestStateEnum changeRequestState) {
        this.changeRequestState = changeRequestState;
    }

    public void setChangeRequestStateDataSource(List<CompetenceCourseMarkSheetChangeRequestStateEnum> value) {
        this.changeRequestStateDataSource = value.stream()
                .map(x -> new TupleDataSourceBean(x.name(), x.getDescriptionI18N().getContent())).collect(Collectors.toList());
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
        setExpireDate(markSheet.getExpireDate());

        setUpdateGradeBeans(buildBeans(true));
        setUpdateGradeAvailableDateBeans(buildBeans(false));

        update();
    }

    static public CompetenceCourseMarkSheetBean createForTeacher(final ExecutionCourse executionCourse, final Person certifier) {
        final CompetenceCourseMarkSheetBean result = new CompetenceCourseMarkSheetBean();

        result.setByTeacher(true);
        result.setExecutionSemester(executionCourse.getExecutionPeriod());
        result.setCompetenceCourse(executionCourse.getCompetenceCourses().size() == 1 ? executionCourse.getCompetenceCourses()
                .iterator().next() : null);
        result.setCertifier(certifier);
        result.setExecutionCourse(executionCourse);

        result.update();

        return result;
    }

    public void update() {
        setExecutionSemesterDataSource(ExecutionSemester.readNotClosedExecutionPeriods().stream().collect(Collectors.toSet()));

        updateCompetenceCourseDataSource();

        setExecutionCourseDataSource(getFilteredExecutionCourses(null).collect(Collectors.toSet()));

        updateCourseEvaluationDataSource();

        setEvaluationSeasonDataSource(EvaluationSeasonServices.findByActive(true).collect(Collectors.toSet()));

        updateCertifierDataSource();

        updateShiftsDataSource();

        setMarkSheetStateDataSource(Lists.newArrayList(CompetenceCourseMarkSheetStateEnum.values()));

        setGradeScaleDataSource(Lists.newArrayList(GradeScale.TYPE20, GradeScale.TYPE20_ABSOLUTE, GradeScale.TYPEQUALITATIVE,
                GradeScale.TYPEAPT));

        setChangeRequestStateDataSource(Lists.newArrayList(CompetenceCourseMarkSheetChangeRequestStateEnum.values()));
    }

    private Stream<ExecutionCourse> getFilteredExecutionCourses(final ExecutionCourse toFilter) {

        if (getCompetenceCourse() == null || getExecutionSemester() == null) {
            return Collections.<ExecutionCourse> emptyList().stream();
        }

        return getCompetenceCourse().getExecutionCoursesByExecutionPeriod(getExecutionSemester()).stream()
                .filter(e -> toFilter == null || e == toFilter);
    }

    private List<MarkBean> buildBeans(final boolean searchNew) {
        final List<MarkBean> result = Lists.newArrayList();

        if (getCompetenceCourseMarkSheet() != null) {

            if (searchNew) {
                getCompetenceCourseMarkSheet().getExecutionCourseEnrolmentsNotInAnyMarkSheet().forEach(enrolment -> {
                    final MarkBean markBean = new MarkBean(getCompetenceCourseMarkSheet(), enrolment);
                    result.add(markBean);
                });
            }

            getCompetenceCourseMarkSheet().getEnrolmentEvaluationSet().forEach(evaluation -> {
                final MarkBean markBean = new MarkBean(getCompetenceCourseMarkSheet(), evaluation);
                result.add(markBean);
            });
        }

        Collections.sort(result);

        return result;
    }

    private void validateUpdateGradeBeans() {

        for (final MarkBean markBean : getUpdateGradeBeans()) {
            markBean.setErrorMessage(null);
            markBean.validate();
        }

        if (getUpdateGradeBeans().stream().anyMatch(e -> e.getErrorMessage() != null)) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetBean.invalid.evaluations");
        }
    }

    public void updateGrades() {
        validateUpdateGradeBeans();

        for (final MarkBean markBean : getUpdateGradeBeans()) {
            markBean.updateGrade();
        }
    }

    public boolean isSupportsEmptyGrades() {
        return EvaluationSeasonServices.isSupportsEmptyGrades(getEvaluationSeason());
    }

    public void checkRulesForSubmission() {

        if (!isSupportsEmptyGrades() && getUpdateGradeBeans().stream().anyMatch(e -> Strings.isNullOrEmpty(e.getGradeValue()))) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetBean.emptyGrades",
                    getEvaluationSeason().getName().getContent());
        }
    }

    public void updateGradeAvailableDates() {
        for (final MarkBean markBean : getUpdateGradeAvailableDateBeans()) {
            markBean.updateGradeAvailableDate();
        }
    }

}
