package org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumModuleServices;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.joda.time.YearMonthDay;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

abstract public class RegistrationConclusionServices {

    public static Set<RegistrationConclusionInformation> inferConclusion(final Registration registration) {
        final Set<RegistrationConclusionInformation> result = Sets.newHashSet();

        for (final StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
            for (final ProgramConclusion programConclusion : getProgramConclusions(studentCurricularPlan)) {
                final RegistrationConclusionBean conclusionBean =
                        new RegistrationConclusionBean(studentCurricularPlan, programConclusion);

                if (conclusionBean.isConcluded()) {
                    result.add(new RegistrationConclusionInformation(conclusionBean));
                }
            }
        }

        return result;
    }

    public static Map<Registration, Set<RegistrationConclusionInformation>> inferConclusion(
            final Set<Registration> registrationsSet) {
        final Map<Registration, Set<RegistrationConclusionInformation>> mapResult = Maps.newHashMap();

        for (final Registration registration : registrationsSet) {
            final Set<RegistrationConclusionInformation> result = Sets.newHashSet();

            for (final StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
                for (final ProgramConclusion programConclusion : getProgramConclusions(studentCurricularPlan)) {
                    final RegistrationConclusionBean conclusionBean =
                            new RegistrationConclusionBean(studentCurricularPlan, programConclusion);

                    if (conclusionBean.isConcluded()) {
                        result.add(new RegistrationConclusionInformation(conclusionBean));
                    }
                }
            }

            mapResult.put(registration, result);
        }

        return mapResult;
    }

    private static Set<ProgramConclusion> getProgramConclusions(final StudentCurricularPlan studentCurricularPlan) {
        final Set<ProgramConclusion> result = Sets.newHashSet();

        final Set<CurriculumGroup> allCurriculumGroups = Sets.newHashSet(studentCurricularPlan.getAllCurriculumGroups());
        allCurriculumGroups.add(studentCurricularPlan.getRoot());

        for (final CurriculumGroup curriculumGroup : allCurriculumGroups) {
            if (curriculumGroup.getDegreeModule() == null) {
                continue;
            }

            if (curriculumGroup.getDegreeModule().getProgramConclusion() != null) {
                result.add(curriculumGroup.getDegreeModule().getProgramConclusion());
            }
        }

        return result;
    }

    /**
     * Motivation: accumulated Registrations can't calculate conclusion date when starting on a root, since it is never concluded
     * 
     * This is only used for a suggested conclusion date, it is not used by the domain
     */
    static public YearMonthDay calculateConclusionDate(final RegistrationConclusionBean input) {
        YearMonthDay result = input.calculateConclusionDate();

        if (result == null && input.getCurriculumGroup().isRoot()) {

            for (final CurriculumGroup group : getCurriculumGroupsForConclusion(input.getCurriculumGroup())) {

                final YearMonthDay calculated = CurriculumModuleServices.calculateLastAcademicActDate(group);
                if (calculated != null && (result == null || calculated.isAfter(result))) {
                    result = calculated;
                }
            }
        }

        return result;
    }

    static public Set<CurriculumGroup> getCurriculumGroupsForConclusion(final CurriculumGroup curriculumGroup) {
        final Set<CurriculumGroup> result = Sets.newHashSet(curriculumGroup);

        final StudentCurricularPlan scp = curriculumGroup.getStudentCurricularPlan();
        final Registration registration = scp.getRegistration();
        if (RegistrationServices.isCurriculumAccumulated(registration)) {

            for (final StudentCurricularPlan otherScp : registration.getSortedStudentCurricularPlans()) {
                if (otherScp.getStartDateYearMonthDay().isBefore(scp.getStartDateYearMonthDay())) {

                    for (final CurriculumGroup otherGroup : otherGroups(otherScp, curriculumGroup)) {
                        result.add(otherGroup);
                    }
                }
            }
        }

        return result;
    }

    static private List<CurriculumGroup> otherGroups(final StudentCurricularPlan otherScp, final CurriculumGroup originalGroup) {

        final List<CurriculumGroup> result = Lists.newArrayList();
        result.add(otherScp.getRoot());
        result.addAll(otherScp.getAllCurriculumGroups());

        final Predicate<CurriculumGroup> predicate;
        final ProgramConclusion programConclusion = originalGroup.getDegreeModule().getProgramConclusion();
        if (programConclusion == null) {

            // take into account this special case: we might be dealing with all of curriculum, not a specific program conclusion
            // eg: integrated master in IST
            predicate = otherGroup -> originalGroup.isRoot() && otherGroup.isRoot();

        } else {

            predicate = otherGroup -> otherGroup.getDegreeModule() != null
                    && otherGroup.getDegreeModule().getProgramConclusion() == programConclusion;
        }

        return result.stream().filter(predicate).collect(Collectors.toList());
    }

}
