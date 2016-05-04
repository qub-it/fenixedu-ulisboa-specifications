package org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.MarkSheet;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.report.CurricularCourseSeasonReport;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.report.ExecutionCourseSeasonReport;
import org.joda.time.LocalDate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class MarkSheetStatusReportService {

    public List<ExecutionCourseSeasonReport> generateExecutionCourseReport(final ExecutionCourse executionCourse) {
        final Collection<EvaluationSeason> seasons = EvaluationSeasonServices.findByActive(true).collect(Collectors.toList());

        final List<ExecutionCourseSeasonReport> result = new ArrayList<ExecutionCourseSeasonReport>();
        for (final EvaluationSeason season : seasons) {

            final Multimap<LocalDate, CurricularCourseSeasonReport> reportsByEvaluationDate = ArrayListMultimap.create();
            for (final CurricularCourseSeasonReport curricularCourseReport : generateCurricularCoursesReport(
                    executionCourse.getAssociatedCurricularCoursesSet(), season, executionCourse.getExecutionPeriod())) {
                reportsByEvaluationDate.put(curricularCourseReport.getEvaluationDate(), curricularCourseReport);
            }

            for (final Map.Entry<LocalDate, Collection<CurricularCourseSeasonReport>> entry : reportsByEvaluationDate.asMap()
                    .entrySet()) {
                result.add(new ExecutionCourseSeasonReport(executionCourse, season, entry.getKey(), entry.getValue()));
            }

        }

        return result;
    }

    public List<CurricularCourseSeasonReport> generateCurricularCourseReport(final ExecutionInterval executionInterval) {
        List<EvaluationSeason> activeSeasons = EvaluationSeasonServices.findByActive(true).collect(Collectors.toList());
        return generateCurricularCourseReport(executionInterval, activeSeasons);
    }

    public List<CurricularCourseSeasonReport> generateCurricularCourseReport(final ExecutionInterval executionInterval,
            final Collection<EvaluationSeason> seasons) {

        final List<CurricularCourseSeasonReport> result = new ArrayList<CurricularCourseSeasonReport>();
        final ExecutionSemester executionSemester =
                ExecutionInterval.assertExecutionIntervalType(ExecutionSemester.class, executionInterval);
        final Set<CurricularCourse> toProcess = collectCurricularCoursesToProcess(executionSemester);

        for (final EvaluationSeason season : seasons) {
            result.addAll(generateCurricularCoursesReport(toProcess, season, executionSemester));
        }

        return result;
    }

    private Set<CurricularCourse> collectCurricularCoursesToProcess(final ExecutionSemester executionSemester) {

        final Set<CurricularCourse> result = new HashSet<CurricularCourse>();

        for (final ExecutionCourse executionCourse : executionSemester.getAssociatedExecutionCoursesSet()) {
            for (final CompetenceCourse competenceCourse : executionCourse.getCompetenceCourses()) {

                result.addAll(competenceCourse.getAssociatedCurricularCoursesSet());
            }
        }

        //improvement of evaluations approved in previous years
        for (final EnrolmentEvaluation enrolmentEvaluation : executionSemester.getEnrolmentEvaluationsSet()) {
            result.add(enrolmentEvaluation.getEnrolment().getCurricularCourse());
        }

        return result;
    }

    private List<CurricularCourseSeasonReport> generateCurricularCoursesReport(
            final Collection<CurricularCourse> curricularCourses, final EvaluationSeason season,
            final ExecutionSemester executionSemester) {
        final List<CurricularCourseSeasonReport> result = new ArrayList<CurricularCourseSeasonReport>();
        for (final CurricularCourse curricularCourse : curricularCourses) {
            addNonEmptyCurricularCourseReport(result,
                    generateCurricularCourseReport(curricularCourse, season, executionSemester, null));
        }
        return result;
    }

    private CurricularCourseSeasonReport generateCurricularCourseReport(final CurricularCourse curricularCourse,
            final EvaluationSeason season, final ExecutionSemester executionSemester, final LocalDate evaluationDate) {

        final CurricularCourseSeasonReport result =
                new CurricularCourseSeasonReport(curricularCourse, season, executionSemester, evaluationDate);

        //TODOJN : -> Egidio getEnrolmentsForGradeSubmission não existe
//        result.setNotEvaluatedStudents(EvaluationSeasonServices
//                .getEnrolmentsForGradeSubmission(season, curricularCourse, evaluationDate, executionSemester).size());
        result.setNotEvaluatedStudents(2);

        final Set<Enrolment> enrolmentsToProcess = new HashSet<Enrolment>();
        enrolmentsToProcess.addAll(curricularCourse.getEnrolmentsByAcademicInterval(executionSemester.getAcademicInterval()));

        for (final EnrolmentEvaluation enrolmentEvaluation : executionSemester.getEnrolmentEvaluationsSet()) {
            if (enrolmentEvaluation.getEvaluationSeason() == season
                    && enrolmentEvaluation.getEnrolment().getCurricularCourse() == curricularCourse) {
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

        int marksheetsToConfirm = 0;
        for (final MarkSheet markSheet : curricularCourse.getMarkSheetsByPeriod(executionSemester)) {
            //TODOJN : Egidio -> o primeiro if era isCancelled, mas aqui não existe..
            if (markSheet.isNotConfirmed() || markSheet.isRectification() || markSheet.getEvaluationSeason() != season) {
                continue;
            }

            if (!markSheet.isConfirmed()) {
                marksheetsToConfirm++;
            }
        }

        result.setMarksheetsToConfirm(marksheetsToConfirm);

        return result;
    }

    protected void addNonEmptyCurricularCourseReport(final List<CurricularCourseSeasonReport> result,
            final CurricularCourseSeasonReport seasonReportEntry) {

        if (seasonReportEntry.getTotalStudents().intValue() > 0) {
            result.add(seasonReportEntry);
        }
    }

}
