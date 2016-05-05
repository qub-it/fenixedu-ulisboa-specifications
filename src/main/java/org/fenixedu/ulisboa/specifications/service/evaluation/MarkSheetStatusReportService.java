package org.fenixedu.ulisboa.specifications.service.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheetChangeRequestStateEnum;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheetStateEnum;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.report.CompetenceCourseSeasonReport;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.report.ExecutionCourseSeasonReport;
import org.joda.time.LocalDate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @see {@link com.qubit.qubEdu.module.academicOffice.domain.grade.marksheet.report.MarksheetStatusReportService}
 */
public class MarkSheetStatusReportService {

    public List<ExecutionCourseSeasonReport> generateExecutionCourseReport(final ExecutionCourse executionCourse) {
        final Collection<EvaluationSeason> seasons = EvaluationSeasonServices.findByActive(true).collect(Collectors.toList());

        final List<ExecutionCourseSeasonReport> result = new ArrayList<ExecutionCourseSeasonReport>();
        for (final EvaluationSeason season : seasons) {

            final Multimap<LocalDate, CompetenceCourseSeasonReport> reportsByEvaluationDate = ArrayListMultimap.create();
            for (final CompetenceCourseSeasonReport competenceCourseReport : generateCompetenceCoursesReport(
                    executionCourse.getCompetenceCourses(), season, executionCourse.getExecutionPeriod())) {
                reportsByEvaluationDate.put(competenceCourseReport.getEvaluationDate(), competenceCourseReport);
            }

            for (final Map.Entry<LocalDate, Collection<CompetenceCourseSeasonReport>> entry : reportsByEvaluationDate.asMap()
                    .entrySet()) {
                result.add(new ExecutionCourseSeasonReport(executionCourse, season, entry.getKey(), entry.getValue()));
            }

        }

        return result;
    }

    public List<CompetenceCourseSeasonReport> generateCompetenceCourseReport(final ExecutionInterval executionInterval) {
        final List<EvaluationSeason> activeSeasons = EvaluationSeasonServices.findByActive(true).collect(Collectors.toList());
        return generateCompetenceCourseReport(executionInterval, activeSeasons);
    }

    public List<CompetenceCourseSeasonReport> generateCompetenceCourseReport(final ExecutionInterval executionInterval,
            final Collection<EvaluationSeason> seasons) {

        final List<CompetenceCourseSeasonReport> result = new ArrayList<CompetenceCourseSeasonReport>();
        final ExecutionSemester executionSemester =
                ExecutionInterval.assertExecutionIntervalType(ExecutionSemester.class, executionInterval);
        final Set<CompetenceCourse> toProcess = collectCompetenceCoursesToProcess(executionSemester);

        for (final EvaluationSeason season : seasons) {
            result.addAll(generateCompetenceCoursesReport(toProcess, season, executionSemester));
        }

        return result;
    }

    private Set<CompetenceCourse> collectCompetenceCoursesToProcess(final ExecutionSemester executionSemester) {

        final Set<CompetenceCourse> result = new HashSet<CompetenceCourse>();

        for (final ExecutionCourse executionCourse : executionSemester.getAssociatedExecutionCoursesSet()) {
            result.addAll(executionCourse.getCompetenceCourses());
        }

        //improvement of evaluations approved in previous years
        for (final EnrolmentEvaluation enrolmentEvaluation : executionSemester.getEnrolmentEvaluationsSet()) {
            result.add(enrolmentEvaluation.getEnrolment().getCurricularCourse().getCompetenceCourse());
        }

        return result;
    }

    private List<CompetenceCourseSeasonReport> generateCompetenceCoursesReport(
            final Collection<CompetenceCourse> competenceCourses, final EvaluationSeason season,
            final ExecutionSemester executionSemester) {

        final List<CompetenceCourseSeasonReport> result = new ArrayList<CompetenceCourseSeasonReport>();
        for (final CompetenceCourse competenceCourse : competenceCourses) {

            addNonEmptyCompetenceCourseReport(result,
                    generateCompetenceCourseReport(competenceCourse, season, executionSemester, (LocalDate) null));
        }

        return result;
    }

    protected void addNonEmptyCompetenceCourseReport(final List<CompetenceCourseSeasonReport> result,
            final CompetenceCourseSeasonReport seasonReportEntry) {

        if (seasonReportEntry.getTotalStudents().intValue() > 0) {
            result.add(seasonReportEntry);
        }
    }

    private CompetenceCourseSeasonReport generateCompetenceCourseReport(final CompetenceCourse competenceCourse,
            final EvaluationSeason season, final ExecutionSemester executionSemester, final LocalDate evaluationDate) {

        final CompetenceCourseSeasonReport result =
                new CompetenceCourseSeasonReport(competenceCourse, season, executionSemester, evaluationDate);

        //TODOJN : -> Egidio getEnrolmentsForGradeSubmission não existe
//        result.setNotEvaluatedStudents(EvaluationSeasonServices
//                .getEnrolmentsForGradeSubmission(season, competenceCourse, evaluationDate, executionSemester).size());
        result.setNotEvaluatedStudents(2);

        final Set<Enrolment> enrolmentsToProcess = new HashSet<Enrolment>();
        competenceCourse.getAssociatedCurricularCoursesSet().stream().forEach(
                i -> enrolmentsToProcess.addAll(i.getEnrolmentsByAcademicInterval(executionSemester.getAcademicInterval())));

        for (final EnrolmentEvaluation enrolmentEvaluation : executionSemester.getEnrolmentEvaluationsSet()) {
            if (enrolmentEvaluation.getEvaluationSeason() == season
                    && enrolmentEvaluation.getEnrolment().getCurricularCourse().getCompetenceCourse() == competenceCourse) {
                enrolmentsToProcess.add(enrolmentEvaluation.getEnrolment());

            }
        }

        int evaluatedStudents = 0;
        for (final Enrolment enrolment : enrolmentsToProcess) {

            final Optional<EnrolmentEvaluation> activeEvaluation =
                    enrolment.getEnrolmentEvaluation(season, executionSemester, false);
            if (activeEvaluation.isPresent() && activeEvaluation.get().getCompetenceCourseMarkSheet() != null) {
                evaluatedStudents++;
            }
        }

        result.setEvaluatedStudents(evaluatedStudents);

        final long marksheetsToConfirm = CompetenceCourseMarkSheet
                .findBy(executionSemester, competenceCourse, (CompetenceCourseMarkSheetStateEnum) null, season,
                        (CompetenceCourseMarkSheetChangeRequestStateEnum) null)
                .filter(markSheet -> !markSheet.isConfirmed()).count();

        result.setMarksheetsToConfirm(Long.valueOf(marksheetsToConfirm).intValue());

        return result;
    }

}
