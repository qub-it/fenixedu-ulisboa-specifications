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
package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;

import pt.ist.fenixframework.Atomic;

public class CreditsInEnrolmentPeriod extends CreditsInEnrolmentPeriod_Base {

    protected CreditsInEnrolmentPeriod() {
        super();
    }

    @Atomic
    static public CreditsInEnrolmentPeriod createForSemester(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer semester) {
        return create(configuration, credits, semester);
    }

    @Atomic
    static public CreditsInEnrolmentPeriod create(final CurricularPeriodConfiguration configuration, final BigDecimal credits) {
        return create(configuration, credits, /* semester */(Integer) null);
    }

    @Atomic
    static private CreditsInEnrolmentPeriod create(final CurricularPeriodConfiguration configuration, final BigDecimal credits,
            final Integer semester) {

        final CreditsInEnrolmentPeriod result = new CreditsInEnrolmentPeriod();
        result.init(configuration, credits, semester);
        return result;
    }

    @Override
    protected String getLabel() {
        if (getSemester() != null) {
            return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName() + ".semester", getCredits()
                    .toString(), getSemester().toString());

        } else {
            return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getCredits().toString());
        }
    }
    
    @Override
    public RuleResult execute(final EnrolmentContext enrolmentContext) {
        if (getSemester() != null) {

            if (getSemester().intValue() != enrolmentContext.getExecutionPeriod().getSemester().intValue()) {
                return createNA();
            }

            return executeBySemester(enrolmentContext);
        }

        return executeByYear(enrolmentContext);
    }

    private RuleResult executeByYear(final EnrolmentContext enrolmentContext) {
        BigDecimal total = BigDecimal.ZERO;

        for (final IDegreeModuleToEvaluate degreeModuleToEvaluate : getEnroledAndEnroling(enrolmentContext, i -> i
                .getExecutionPeriod().getExecutionYear() == enrolmentContext.getExecutionPeriod().getExecutionYear())) {

            final BigDecimal credits = BigDecimal.valueOf(degreeModuleToEvaluate.getEctsCredits());
            total = total.add(credits);
        }

        return total.compareTo(getCredits()) <= 0 ? createTrue() : createFalseLabelled(total);
    }

    private RuleResult executeBySemester(final EnrolmentContext enrolmentContext) {
        BigDecimal total = BigDecimal.ZERO;

        for (final IDegreeModuleToEvaluate degreeModuleToEvaluate : getEnroledAndEnroling(enrolmentContext,
                i -> i.getExecutionPeriod() == enrolmentContext.getExecutionPeriod())) {

            final BigDecimal credits =
                    BigDecimal.valueOf(degreeModuleToEvaluate.getAccumulatedEctsCredits(enrolmentContext.getExecutionPeriod()));
            total = total.add(credits);
        }

        return total.compareTo(getCredits()) <= 0 ? createTrue() : createFalseLabelled(total);
    }

}
