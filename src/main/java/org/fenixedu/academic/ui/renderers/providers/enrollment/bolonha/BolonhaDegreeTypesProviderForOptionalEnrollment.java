/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
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
package org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanComparator;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularRules.AnyCurricularCourse;
import org.fenixedu.academic.domain.curricularRules.CompositeRule;
import org.fenixedu.academic.domain.curricularRules.CurricularRule;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.studentCurriculum.CycleCurriculumGroup;
import org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentOptionalEnrollmentBean;
import org.fenixedu.academic.ui.renderers.providers.BolonhaDegreeTypesProvider;
import org.fenixedu.bennu.core.security.Authenticate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.emory.mathcs.backport.java.util.Collections;

public class BolonhaDegreeTypesProviderForOptionalEnrollment extends BolonhaDegreeTypesProvider {

    @Override
    public Object provide(Object source, Object currentValue) {
        final List<DegreeType> result = Lists.newArrayList();

        if (source instanceof BolonhaStudentOptionalEnrollmentBean) {

            final BolonhaStudentOptionalEnrollmentBean bean = (BolonhaStudentOptionalEnrollmentBean) source;
            result.addAll(getDegreeTypes(bean));
        }

        Collections.sort(result, new BeanComparator<>("name.content"));
        return result;
    }

    static private Set<DegreeType> getDegreeTypes(final BolonhaStudentOptionalEnrollmentBean bean) {
        final Set<DegreeType> result = Sets.newHashSet();

        final CurricularRule curricularRule = getCurricularRule(bean);
        if (curricularRule != null) {

            // curricular rule configured
            result.addAll(getAnyCurricularCourseRuleDegreesTypesFor(curricularRule));
        }

        if (result.isEmpty()) {

            final CycleCurriculumGroup group =
                    bean.getSelectedDegreeModuleToEnrol().getCurriculumGroup().getParentCycleCurriculumGroup();
            
            if (group != null) {
                final CycleType cycleType = group.getCycleType();

                if (isStudentEnroling(bean)) {

                    result.addAll(getDegreeTypes(cycleType));

                } else {

                    // allow enroling of optional of all cycle types below
                    for (final CycleType iter : CycleType.values()) {
                        if (iter.isBeforeOrEquals(cycleType)) {

                            result.addAll(getDegreeTypes(iter));
                        }
                    }
                }

            } else {

                // not in a cycle group hierarchy, inspecting from the degree's cycle type
                for (final CycleType cycleType : bean.getStudentCurricularPlan().getDegreeType().getCycleTypes()) {
                    result.addAll(getDegreeTypes(cycleType));
                }
            }
        }

        // failsafe
        if (result.isEmpty()) {
            result.addAll(readAll(bean));
        }

        return result;
    }

    static protected CurricularRule getCurricularRule(final BolonhaStudentOptionalEnrollmentBean bean) {
        CurricularRule result = null;

        final IDegreeModuleToEvaluate degreeModuleToEnrol = bean.getSelectedDegreeModuleToEnrol();
        final ExecutionSemester executionPeriod = bean.getExecutionPeriod();

        for (final CurricularRule curricularRule : degreeModuleToEnrol.getCurricularRulesFromDegreeModule(executionPeriod)) {
            if (curricularRule instanceof AnyCurricularCourse || curricularRule.isCompositeRule()) {
                result = curricularRule;
                break;
            }
            if (curricularRule.isCompositeRule()) {
                result = curricularRule;
            }
        }

        return result;
    }

    static private boolean isStudentEnroling(final BolonhaStudentOptionalEnrollmentBean bean) {
        return Authenticate.getUser().getPerson() == bean.getStudentCurricularPlan().getPerson();
    }

    static private Set<DegreeType> getDegreeTypes(final CycleType cycleType) {
        return DegreeType.all().filter(i -> i.hasCycleTypes(cycleType)).collect(Collectors.toSet());
    }

    static private Set<DegreeType> getAnyCurricularCourseRuleDegreesTypesFor(final CurricularRule curricularRule) {
        final Set<DegreeType> result = Sets.newHashSet();

        if (curricularRule.isCompositeRule()) {

            for (final CurricularRule childRule : ((CompositeRule) curricularRule).getCurricularRulesSet()) {
                result.addAll(getAnyCurricularCourseRuleDegreesTypesFor(childRule));
            }

        } else if (curricularRule instanceof AnyCurricularCourse) {
            result.addAll(providePossibleDegreeTypes((AnyCurricularCourse) curricularRule));
        }

        return result;
    }

    static private Set<DegreeType> providePossibleDegreeTypes(final AnyCurricularCourse rule) {
        final Set<DegreeType> result = Sets.newHashSet();

        final DegreeType degreeType = rule.getBolonhaDegreeType();
        if (degreeType != null) {
            result.add(degreeType);
        }

        final Degree degree = rule.getDegree();
        if (degree != null) {
            result.add(degree.getDegreeType());
        }

        return result;
    }

    static private Set<DegreeType> readAll(final BolonhaStudentOptionalEnrollmentBean bean) {
        final Set<DegreeType> result = Sets.newHashSet();

        for (Iterator<DegreeType> iterator = DegreeType.all().filter(type -> !type.isEmpty()).iterator(); iterator.hasNext();) {
            final DegreeType degreeType = iterator.next();

            for (final Degree degree : degreeType.getDegreeSet()) {
                if (isExecuted(degree, bean.getExecutionYear())) {
                    result.add(degreeType);
                }
            }
        }

        return result;
    }

    static protected boolean isExecuted(final Degree degree, final ExecutionYear executionYear) {
        return !degree.getExecutionDegreesForExecutionYear(executionYear).isEmpty();
    }

}
