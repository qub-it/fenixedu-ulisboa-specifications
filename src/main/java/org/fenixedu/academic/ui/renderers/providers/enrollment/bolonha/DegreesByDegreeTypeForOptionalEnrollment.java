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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularRules.AnyCurricularCourse;
import org.fenixedu.academic.domain.curricularRules.CompositeRule;
import org.fenixedu.academic.domain.curricularRules.CurricularRule;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentOptionalEnrollmentBean;
import org.fenixedu.bennu.core.domain.Bennu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DegreesByDegreeTypeForOptionalEnrollment extends DegreesByDegreeType {

    @Override
    public Object provide(Object source, Object currentValue) {
        final List<Degree> result = Lists.newArrayList();

        if (source instanceof BolonhaStudentOptionalEnrollmentBean) {

            final BolonhaStudentOptionalEnrollmentBean bean = (BolonhaStudentOptionalEnrollmentBean) source;
            result.addAll(getDegrees(bean));

            final Degree current = (Degree) currentValue;
            if (!result.contains(current)) {
                bean.setDegree(null);
            }
        }

        Collections.sort(result, Degree.COMPARATOR_BY_NAME);
        return result;
    }

    static private Set<Degree> getDegrees(final BolonhaStudentOptionalEnrollmentBean bean) {
        final Set<Degree> result = Sets.newHashSet();

        final DegreeType degreeType = bean.getDegreeType();
        if (degreeType != null) {

            final ExecutionYear executionYear = bean.getExecutionYear();

            final CurricularRule curricularRule = BolonhaDegreeTypesProviderForOptionalEnrollment.getCurricularRule(bean);
            if (curricularRule != null) {

                // curricular rule configured
                result.addAll(getAnyCurricularCourseRuleDegreesFor(curricularRule, degreeType, executionYear));
            }

            // failsafe
            if (result.isEmpty()) {
                result.addAll(readAll(degreeType, executionYear));
            }
        }

        return result;
    }

    static private Set<Degree> getAnyCurricularCourseRuleDegreesFor(final CurricularRule curricularRule,
            final DegreeType degreeType, final ExecutionYear executionYear) {

        final Set<Degree> result = Sets.newHashSet();

        if (curricularRule.isCompositeRule()) {

            for (final CurricularRule childRule : ((CompositeRule) curricularRule).getCurricularRulesSet()) {
                result.addAll(getAnyCurricularCourseRuleDegreesFor(childRule, degreeType, executionYear));
            }

        } else if (curricularRule instanceof AnyCurricularCourse) {
            result.addAll(providePossibleDegrees((AnyCurricularCourse) curricularRule, degreeType, executionYear));
        }

        return result;
    }

    static private Set<Degree> providePossibleDegrees(final AnyCurricularCourse rule, final DegreeType degreeType,
            final ExecutionYear executionYear) {

        final Set<Degree> result = Sets.newHashSet();

        final Degree degree = rule.getDegree();
        if (degree != null) {

            if (degree.getDegreeType() == degreeType
                    && BolonhaDegreeTypesProviderForOptionalEnrollment.isExecuted(degree, executionYear)) {

                result.add(degree);
            }

        }

        return result;
    }

    static private Set<Degree> readAll(final DegreeType degreeType, final ExecutionYear executionYear) {
        final Set<Degree> result = Sets.newHashSet();

        for (final Degree degree : Bennu.getInstance().getDegreesSet()) {
            if (degree.getDegreeType() != null && degree.getDegreeType() == degreeType
                    && BolonhaDegreeTypesProviderForOptionalEnrollment.isExecuted(degree, executionYear)) {

                result.add(degree);
            }
        }

        return result;
    }

}
