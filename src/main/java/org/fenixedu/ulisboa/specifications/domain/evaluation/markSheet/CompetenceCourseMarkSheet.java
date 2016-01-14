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

package org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.util.EnrolmentEvaluationState;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EvaluationComparator;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonPeriod;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonPeriod.EvaluationSeasonPeriodType;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.rule.EvaluationSeasonRule;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.rule.GradeScaleValidator;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class CompetenceCourseMarkSheet extends CompetenceCourseMarkSheet_Base {

    static final private Logger logger = LoggerFactory.getLogger(CompetenceCourseMarkSheet.class);

    protected CompetenceCourseMarkSheet() {
        super();
    }

    protected void init(final ExecutionSemester executionSemester, final CompetenceCourse competenceCourse,
            final ExecutionCourse executionCourse, final EvaluationSeason evaluationSeason, final LocalDate evaluationDate,
            GradeScale gradeScale, final Person certifier, final Set<Shift> shifts, final LocalDate expireDate) {

        setExecutionSemester(executionSemester);
        setCompetenceCourse(competenceCourse);
        setExecutionCourse(executionCourse);
        setEvaluationSeason(evaluationSeason);
        setEvaluationDate(evaluationDate);
        setGradeScale(gradeScale);
        setCertifier(certifier);
        getShiftSet().addAll(shifts);
        setExpireDate(expireDate);
        checkRules();
    }

    private void checkRules() {

        if (getExecutionSemester() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.executionSemester.required");
        }

        if (getCompetenceCourse() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.competenceCourse.required");
        }

        if (getExecutionCourse() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.executionCourse.required");
        }

        if (getEvaluationSeason() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.evaluationSeason.required");
        }

        if (getEvaluationDate() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.evaluationDate.required");
        }

        if (getCertifier() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.certifier.required");
        }

        if (getGradeScale() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.gradeScale.required");
        }

        for (final EnrolmentEvaluation enrolmentEvaluation : getEnrolmentEvaluationSet()) {
            if (enrolmentEvaluation.getGradeScale() != getGradeScale()) {
                throw new ULisboaSpecificationsDomainException(
                        "error.CompetenceCourseMarkSheet.marksheet.already.contains.evaluations.with.another.grade.scale");
            }
        }

        if (getEnrolmentEvaluationSet().isEmpty() && getExecutionCourseEnrolmentsNotInAnyMarkSheet().isEmpty()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CompetenceCourseMarkSheet.no.enrolments.found.for.grade.submission");
        }

        checkIfEvaluationDateIsInExamsPeriod();
        checkIfEvaluationsDateIsEqualToMarkSheetEvaluationDate();
    }

    private void checkIfEvaluationDateIsInExamsPeriod() {
        final Set<EvaluationSeasonPeriod> periods = getExamsPeriods();

        if (periods.isEmpty()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CompetenceCourseMarkSheet.evaluationDateNotInExamsPeriod.undefined",
                    EvaluationSeasonServices.getDescriptionI18N(getEvaluationSeason()).getContent(),
                    getExecutionSemester().getQualifiedName());
        }

        for (final EvaluationSeasonPeriod iter : periods) {

            if (!iter.isContainingDate(getEvaluationDate())) {
                throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.evaluationDateNotInExamsPeriod",
                        getEvaluationDate().toString(), EvaluationSeasonPeriod.getIntervalsDescription(periods));
            }
        }
    }

    protected void checkIfIsGradeSubmissionAvailable() {

        if (getExpireDate() != null) {

            if (getExpireDate().isBefore(new LocalDate())) {
                throw new ULisboaSpecificationsDomainException(
                        "error.CompetenceCourseMarkSheet.notInGradeSubmissionPeriod.expired");
            }

            return;
        }

        final Set<EvaluationSeasonPeriod> periods = getGradeSubmissionPeriods();

        if (periods.isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.notInGradeSubmissionPeriod.undefined",
                    EvaluationSeasonServices.getDescriptionI18N(getEvaluationSeason()).getContent(),
                    getExecutionSemester().getQualifiedName());
        }

        for (final EvaluationSeasonPeriod iter : periods) {

            if (!iter.isContainingDate(new LocalDate())) {
                throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.notInGradeSubmissionPeriod",
                        EvaluationSeasonPeriod.getIntervalsDescription(periods));
            }
        }
    }

    private void checkIfEvaluationsDateIsEqualToMarkSheetEvaluationDate() {
        for (final EnrolmentEvaluation iter : getEnrolmentEvaluationSet()) {
            if (!iter.getExamDateYearMonthDay().toLocalDate().isEqual(getEvaluationDate())) {
                throw new ULisboaSpecificationsDomainException(
                        "error.CompetenceCourseMarkSheet.evaluations.examDate.must.be.equal.marksheet.evaluationDate");
            }
        }
    }

    @Atomic
    public void edit(final LocalDate evaluationDate, final GradeScale gradeScale, final Person certifier,
            final LocalDate expireDate) {

        if (!isEdition()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CompetenceCourseMarkSheet.markSheet.can.only.be.updated.in.edition.state");
        }

        getEnrolmentEvaluationSet().forEach(e ->
        {
            e.setExamDateYearMonthDay(evaluationDate == null ? null : evaluationDate.toDateTimeAtStartOfDay().toYearMonthDay());
            e.setPersonResponsibleForGrade(certifier);
        });

        init(getExecutionSemester(), getCompetenceCourse(), getExecutionCourse(), getEvaluationSeason(), evaluationDate,
                gradeScale, certifier, getShiftSet(), expireDate);

        checkRules();
    }

    void editExpireDate(LocalDate expireDate) {
        super.setExpireDate(expireDate);
        checkRules();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {

        if (!isEdition()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CompetenceCourseMarkSheet.markSheet.can.only.be.deleted.in.edition.state");
        }

        setExecutionSemester(null);
        setCompetenceCourse(null);
        setExecutionCourse(null);
        setEvaluationSeason(null);
        setCertifier(null);
        getShiftSet().clear();
        getEnrolmentEvaluationSet().clear();

        final Iterator<CompetenceCourseMarkSheetStateChange> stateIterator = getStateChangeSet().iterator();
        while (stateIterator.hasNext()) {
            final CompetenceCourseMarkSheetStateChange stateChange = stateIterator.next();
            stateIterator.remove();
            stateChange.delete();
        }

        final Iterator<CompetenceCourseMarkSheetChangeRequest> changeRequestIterator = getChangeRequestsSet().iterator();
        while (changeRequestIterator.hasNext()) {
            final CompetenceCourseMarkSheetChangeRequest changeRequest = changeRequestIterator.next();
            changeRequestIterator.remove();
            changeRequest.delete();
        }

        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        deleteDomainObject();
    }

    public Set<EvaluationSeasonPeriod> getGradeSubmissionPeriods() {
        return getEvaluationSeasonPeriods(EvaluationSeasonPeriodType.GRADE_SUBMISSION);
    }

    public Set<EvaluationSeasonPeriod> getExamsPeriods() {
        return getEvaluationSeasonPeriods(EvaluationSeasonPeriodType.EXAMS);
    }

    private Set<EvaluationSeasonPeriod> getEvaluationSeasonPeriods(final EvaluationSeasonPeriodType periodType) {
        final Set<EvaluationSeasonPeriod> result = Sets.<EvaluationSeasonPeriod> newHashSet();

        for (final EvaluationSeasonPeriod iter : getExecutionSemester().getEvaluationSeasonPeriodSet()) {
            if (iter.getPeriodType() == periodType && iter.getSeason() == getEvaluationSeason()
                    && !Sets.intersection(iter.getExecutionDegrees(), getExecutionDegrees()).isEmpty()) {
                result.add(iter);
            }
        }

        return result;
    }

    private Set<ExecutionDegree> getExecutionDegrees() {
        return getExecutionCourse().getAssociatedCurricularCoursesSet().stream()
                .map(i -> i.getExecutionDegreeFor(getExecutionSemester().getAcademicInterval())).collect(Collectors.toSet());
    }

    // @formatter: off
    /************
     * SERVICES
     ************/
    // @formatter: on

    @Atomic
    public static CompetenceCourseMarkSheet create(final ExecutionSemester executionSemester,
            final CompetenceCourse competenceCourse, final ExecutionCourse executionCourse,
            final EvaluationSeason evaluationSeason, final LocalDate evaluationDate, final Person certifier,
            final Set<Shift> shifts, final boolean byTeacher) {

        final CompetenceCourseMarkSheet result = new CompetenceCourseMarkSheet();
        result.init(executionSemester, competenceCourse, executionCourse, evaluationSeason, evaluationDate, GradeScale.TYPE20,
                certifier, shifts, null);
        CompetenceCourseMarkSheetStateChange.createEditionState(result, byTeacher, null);
        return result;
    }

    public static Stream<CompetenceCourseMarkSheet> findBy(final ExecutionCourse executionCourse) {

        final Set<CompetenceCourseMarkSheet> result = Sets.newHashSet();

        if (executionCourse != null) {

            for (final CurricularCourse curricularCourse : executionCourse.getAssociatedCurricularCoursesSet()) {
                result.addAll(curricularCourse.getCompetenceCourse().getCompetenceCourseMarkSheetSet());
            }
        }

        return result.stream().filter(c -> c.getExecutionSemester() == executionCourse.getExecutionPeriod());
    }

    public static Stream<CompetenceCourseMarkSheet> findBy(final ExecutionSemester executionSemester,
            final CompetenceCourse competenceCourse, final CompetenceCourseMarkSheetStateEnum markSheetState,
            final CompetenceCourseMarkSheetChangeRequestStateEnum changeRequestState) {

        final Set<CompetenceCourseMarkSheet> result = Sets.newHashSet();
        if (executionSemester != null) {
            result.addAll(executionSemester.getCompetenceCourseMarkSheetSet());
        }

        return result.stream()

                .filter(c -> competenceCourse == null || c.getCompetenceCourse() == competenceCourse)

                .filter(c -> markSheetState == null || c.isInState(markSheetState))

                .filter(c -> changeRequestState == null
                        || c.getChangeRequestsSet().stream().anyMatch(r -> r.getState() == changeRequestState));
    }

    private CompetenceCourseMarkSheetStateChange getFirstStateChange() {
        return getStateChangeSet().stream().min(CompetenceCourseMarkSheetStateChange::compareTo).get();
    }

    private CompetenceCourseMarkSheetStateChange getStateChange() {
        return getStateChangeSet().stream().max(CompetenceCourseMarkSheetStateChange::compareTo).get();
    }

    public boolean isEdition() {
        return getStateChange().isEdition();
    }

    public boolean isSubmitted() {
        return getStateChange().isSubmitted();
    }

    public boolean isConfirmed() {
        return getStateChange().isConfirmed();
    }

    public DateTime getCreationDate() {
        return getFirstStateChange().getDate();
    }

    public Person getCreator() {
        return getFirstStateChange().getResponsible();
    }

    public String getState() {
        return getStateChange().getState().getDescriptionI18N().getContent();
    }

    public DateTime getStateDate() {
        return getStateChange().getDate();
    }

    @Atomic
    public void markAsPrinted() {
        super.setPrinted(true);
    }

    public ExecutionYear getExecutionYear() {
        return getExecutionSemester().getExecutionYear();
    }

    public String getShiftsDescription() {
        return getShiftSet().stream().map(i -> i.getNome()).collect(Collectors.joining(", "));
    }

    private Set<Enrolment> getEnrolmentsNotInAnyMarkSheet() {
        final Set<Enrolment> result = Sets.newHashSet();

        final ExecutionSemester executionSemester = getExecutionSemester();
        final EvaluationSeason evaluationSeason = getEvaluationSeason();

        for (final ExecutionCourse executionCourse : getCompetenceCourse()
                .getExecutionCoursesByExecutionPeriod(getExecutionSemester())) {

            if (getExecutionCourse() == null || executionCourse == getExecutionCourse()) {

                for (final CurricularCourse curricularCourse : executionCourse.getAssociatedCurricularCoursesSet()) {

                    for (final Enrolment enrolment : getEnrolmentsForGradeSubmission(curricularCourse)) {

                        final Optional<EnrolmentEvaluation> finalEvaluation =
                                enrolment.getEnrolmentEvaluation(evaluationSeason, executionSemester, true);
                        if (finalEvaluation.isPresent()) {
                            continue;
                        }

                        final Optional<EnrolmentEvaluation> temporaryEvaluation =
                                enrolment.getEnrolmentEvaluation(evaluationSeason, executionSemester, false);
                        if (temporaryEvaluation.isPresent() && temporaryEvaluation.get().getCompetenceCourseMarkSheet() != null) {
                            continue;
                        }

                        result.add(enrolment);
                    }
                }
            }
        }

        return result;
    }

    public Set<Enrolment> getExecutionCourseEnrolmentsNotInAnyMarkSheet() {

        final Set<Enrolment> result = Sets.newHashSet();
        for (final Enrolment enrolment : getEnrolmentsNotInAnyMarkSheet()) {

            if (getCompetenceCourse().isAnual()
                    && getExecutionSemester() == getExecutionSemester().getExecutionYear().getLastExecutionPeriod()) {

                final ExecutionCourse otherExecutionCourse =
                        enrolment.getExecutionCourseFor(getExecutionSemester().getExecutionYear().getFirstExecutionPeriod());
                if (otherExecutionCourse != null && otherExecutionCourse.getAssociatedCurricularCoursesSet()
                        .containsAll(getExecutionCourse().getAssociatedCurricularCoursesSet())) {

                    if (enrolment.getAttendsByExecutionCourse(otherExecutionCourse) != null) {
                        result.add(enrolment);
                    }
                }

            } else {
                if (enrolment.getAttendsByExecutionCourse(getExecutionCourse()) != null) {
                    result.add(enrolment);
                }
            }
        }

        return result;
    }

    private Set<Enrolment> getEnrolmentsForGradeSubmission(final CurricularCourse curricularCourse) {
        final Set<Enrolment> result = new HashSet<Enrolment>();

        final ExecutionSemester executionSemester = getExecutionSemester();
        final EvaluationSeason season = getEvaluationSeason();

        for (final CurriculumModule curriculumModule : curricularCourse.getCurriculumModulesSet()) {

            if (!curriculumModule.isEnrolment()) {
                continue;
            }

            final Enrolment enrolment = (Enrolment) curriculumModule;

            if (enrolment.isAnnulled()) {
                continue;
            }

            if (!season.isImprovement() && !enrolment.isValid(executionSemester)) {
                continue;
            }

            if (!isEnrolmentCandidateForEvaluation(enrolment)) {
                continue;
            }

            if (!getShiftSet().isEmpty() && !EnrolmentServices.containsAnyShift(enrolment, getShiftSet())) {
                continue;
            }

            final Optional<EnrolmentEvaluation> evaluation = enrolment.getEnrolmentEvaluation(season, executionSemester, false);
            if (evaluation.isPresent() && evaluation.get().getCompetenceCourseMarkSheet() != null) {
                continue;
            }

            result.add(enrolment);
        }

        return result;
    }

    private boolean isEnrolmentCandidateForEvaluation(final Enrolment enrolment) {
        final ExecutionSemester executionSemester = getExecutionSemester();
        final EvaluationSeason season = getEvaluationSeason();

        if (enrolment.isEvaluatedInSeason(season, executionSemester)) {
            return false;
        }

        final Collection<EnrolmentEvaluation> evaluations = getAllFinalEnrolmentEvaluations(enrolment);
        final EnrolmentEvaluation latestEvaluation = getLatestEnrolmentEvaluation(evaluations);
        final boolean isApproved = latestEvaluation != null && latestEvaluation.isApproved();

        // this evaluation season is for not approved enrolments
        if (!season.isImprovement() && isApproved) {
            return false;
        }

        // this evaluation season is for approved enrolments
        if (season.isImprovement() && !isApproved) {
            return false;
        }

        if (EvaluationSeasonServices.hasPreviousSeasonBlockingGrade(season, latestEvaluation)) {
            return false;
        }

        if (!EvaluationSeasonServices.hasRequiredPreviousSeasonMinimumGrade(season, evaluations)) {
            return false;
        }

        final Optional<EnrolmentEvaluation> temporaryEvaluation =
                enrolment.getEnrolmentEvaluation(season, executionSemester, false);

        if (season.isImprovement() && temporaryEvaluation.isPresent()) {
            return temporaryEvaluation.get().getExecutionPeriod() == executionSemester;
        }

        return !EvaluationSeasonServices.isRequiresEnrolmentEvaluation(season) || !temporaryEvaluation.isPresent();
    }

    /**
     * Returns final evaluations that took place before the evaluation date
     */
    private Collection<EnrolmentEvaluation> getAllFinalEnrolmentEvaluations(final Enrolment enrolment) {
        final Collection<EnrolmentEvaluation> evaluations = enrolment.getAllFinalEnrolmentEvaluations();

        for (final Iterator<EnrolmentEvaluation> iterator = evaluations.iterator(); iterator.hasNext();) {
            final EnrolmentEvaluation enrolmentEvaluation = iterator.next();

            final YearMonthDay examDate = enrolmentEvaluation.getExamDateYearMonthDay();
            if (examDate != null && !examDate.isBefore(getEvaluationDate())) {
                iterator.remove();
            }
        }
        return evaluations;
    }

    private EnrolmentEvaluation getLatestEnrolmentEvaluation(final Collection<EnrolmentEvaluation> evaluations) {
        return ((evaluations == null || evaluations.isEmpty()) ? null : Collections.<EnrolmentEvaluation> max(evaluations,
                new EvaluationComparator()));
    }

    public boolean isGradeValueAccepted(final String gradeValue) {

        if (StringUtils.isNotBlank(gradeValue)) {

            final GradeScaleValidator validator = getGradeScaleValidator();
            if (validator == null) {
                return getGradeScale().belongsTo(gradeValue);
            } else {
                return validator.isGradeValueAccepted(gradeValue);
            }
        }

        return false;
    }

    public GradeScaleValidator getGradeScaleValidator() {
        final SortedSet<GradeScaleValidator> result = Sets.newTreeSet(DomainObjectUtil.COMPARATOR_BY_ID);

        for (final EvaluationSeasonRule rule : getEvaluationSeason().getRulesSet()) {
            if (rule instanceof GradeScaleValidator) {
                final GradeScaleValidator validator = (GradeScaleValidator) rule;

                if (validator.getGradeScale() != getGradeScale()) {
                    continue;
                }

                final Set<DegreeType> markSheetDegreeTypes = getExecutionCourse().getAssociatedCurricularCoursesSet().stream()
                        .map(c -> c.getDegree().getDegreeType()).collect(Collectors.toSet());
                if (Sets.intersection(markSheetDegreeTypes, validator.getDegreeTypeSet()).isEmpty()) {
                    continue;
                }

                result.add(validator);
            }
        }

        if (result.size() > 1) {
            logger.warn("Mark sheet {} has more than one GradeScaleValidator configured, returning the oldest", this);
        }

        return result.isEmpty() ? null : result.first();
    }

    public String getGradeScaleDescription() {
        final GradeScaleValidator validator = getGradeScaleValidator();
        return validator == null ? getGradeScale().getDescription() : validator.getRuleDescription().getContent();
    }

    @Atomic
    public void confirm(boolean byTeacher) {

        if (!isSubmitted()) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.must.be.submitted.to.confirm");
        }

        if (getEnrolmentEvaluationSet().isEmpty()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CompetenceCourseMarkSheet.enrolmentEvaluations.required.to.confirm.markSheet");
        }

        for (final EnrolmentEvaluation evaluation : getEnrolmentEvaluationSet()) {
            //TODO: force evaluation checksum generation
            evaluation.setWhenDateTime(new DateTime());
            evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.FINAL_OBJ);
            evaluation.setPerson(Authenticate.getUser().getPerson());
            EnrolmentServices.updateState(evaluation.getEnrolment());
        }

        CompetenceCourseMarkSheetStateChange.createConfirmedState(this, byTeacher, null);

    }

    @Atomic
    public void submit(boolean byTeacher) {

        if (!isEdition()) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.must.be.edition.to.confirm");
        }

        final CompetenceCourseMarkSheetStateChange stateChange =
                CompetenceCourseMarkSheetStateChange.createSubmitedState(this, byTeacher, null);

        final CompetenceCourseMarkSheetSnapshot snapshot =
                CompetenceCourseMarkSheetSnapshot.create(stateChange, getCompetenceCourse().getCode(),
                        getCompetenceCourse().getNameI18N().toLocalizedString(), getExecutionSemester().getQualifiedName(),
                        getEvaluationSeason().getName(), getCertifier().getName(), getEvaluationDate());

        for (final EnrolmentEvaluation evaluation : getSortedEnrolmentEvaluations()) {
            final Registration registration = evaluation.getRegistration();
            final Degree degree = evaluation.getStudentCurricularPlan().getDegree();
            snapshot.addEntry(registration.getNumber(), registration.getName(), evaluation.getGrade(), degree.getCode(),
                    degree.getNameI18N().toLocalizedString(), EnrolmentServices.getShiftsDescription(evaluation.getEnrolment()));
        }

        snapshot.finalize();
    }

    @Atomic
    public void revertToEdition(boolean byTeacher, String reason) {

        if (isEdition()) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheet.already.in.edition");
        }

        for (final EnrolmentEvaluation evaluation : getEnrolmentEvaluationSet()) {
            evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
            EnrolmentServices.updateState(evaluation.getEnrolment());
        }

        CompetenceCourseMarkSheetStateChange.createEditionState(this, byTeacher, reason);
    }

    public String getCheckSum() {
        if (isEdition()) {
            return null;
        }

        return getLastSnapshot().get().getCheckSum();
    }

    public String getFormattedCheckSum() {
        if (isEdition()) {
            return null;
        }

        return getLastSnapshot().get().getFormattedCheckSum();
    }

    public SortedSet<EnrolmentEvaluation> getSortedEnrolmentEvaluations() {

        final Comparator<EnrolmentEvaluation> byStudentName =
                (x, y) -> x.getRegistration().getStudent().getName().compareTo(y.getRegistration().getStudent().getName());

        final SortedSet<EnrolmentEvaluation> result =
                Sets.newTreeSet(byStudentName.thenComparing(DomainObjectUtil.COMPARATOR_BY_ID));

        result.addAll(getEnrolmentEvaluationSet());

        return result;

    }

    private Optional<CompetenceCourseMarkSheetStateChange> getLastStateBy(CompetenceCourseMarkSheetStateEnum type) {
        return getStateChangeSet().stream().filter(s -> s.getState() == type)
                .max(CompetenceCourseMarkSheetStateChange::compareTo);

    }

    public Optional<CompetenceCourseMarkSheetSnapshot> getLastSnapshot() {
        final Optional<CompetenceCourseMarkSheetStateChange> lastStateChange =
                getLastStateBy(CompetenceCourseMarkSheetStateEnum.findSubmited());

        return Optional.of(lastStateChange.isPresent() ? lastStateChange.get().getSnapshot() : null);
    }

    public Collection<CompetenceCourseMarkSheetSnapshot> getSnapshots() {
        return getStateChangeSet().stream().filter(s -> s.getState() == CompetenceCourseMarkSheetStateEnum.findSubmited())
                .map(s -> s.getSnapshot()).collect(Collectors.toSet());

    }

    public Collection<CompetenceCourseMarkSheetSnapshot> getPreviousSnapshots() {

        final Collection<CompetenceCourseMarkSheetSnapshot> snapshots = getSnapshots();
        if (snapshots.isEmpty()) {
            return Collections.emptySet();
        }

        final CompetenceCourseMarkSheetSnapshot lastSnapshot = getLastSnapshot().get();
        return snapshots.stream().filter(s -> s != lastSnapshot).collect(Collectors.toSet());

    }

    public boolean isInState(CompetenceCourseMarkSheetStateEnum markSheetState) {
        return getStateChange().getState() == markSheetState;
    }

    public boolean isCertifierExecutionCourseResponsible() {
        final Professorship professorship = getExecutionCourse().getProfessorship(getCertifier());
        return professorship != null && professorship.isResponsibleFor();
    }

    public CompetenceCourseMarkSheetChangeRequest getLastChangeRequest() {
        final Optional<CompetenceCourseMarkSheetChangeRequest> result =
                getChangeRequestsSet().stream().max(CompetenceCourseMarkSheetChangeRequest.COMPARATOR_BY_REQUEST_DATE);

        return result.isPresent() ? result.get() : null;
    }

    public CompetenceCourseMarkSheetChangeRequest getLastPendingChangeRequest() {
        final Optional<CompetenceCourseMarkSheetChangeRequest> result = getChangeRequestsSet().stream().filter(r -> r.isPending())
                .max(CompetenceCourseMarkSheetChangeRequest.COMPARATOR_BY_REQUEST_DATE);

        return result.isPresent() ? result.get() : null;

    }

    public SortedSet<CompetenceCourseMarkSheetChangeRequest> getSortedChangeRequests() {

        final SortedSet<CompetenceCourseMarkSheetChangeRequest> result =
                Sets.newTreeSet(CompetenceCourseMarkSheetChangeRequest.COMPARATOR_BY_REQUEST_DATE.reversed());

        result.addAll(getChangeRequestsSet());

        return result;

    }

}
