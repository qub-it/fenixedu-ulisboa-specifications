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
import java.util.Map;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Maps;

public class CreditsNotEnroled extends CreditsNotEnroled_Base {

    protected CreditsNotEnroled() {
        super();
    }

    @Atomic
    static public CreditsNotEnroled create(final CurricularPeriodConfiguration configuration, final BigDecimal credits) {

        final CreditsNotEnroled result = new CreditsNotEnroled();
        result.init(configuration, credits, (Integer) null /* semester */);
        return result;
    }

    @Override
    protected String getLabel() {
        return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getCredits().toString(),
                getConfiguration().getCurricularPeriod().getFullLabel());
    }

    @Override
    public RuleResult execute(final EnrolmentContext enrolmentContext) {
        // TODO legidio
        // ir buscar o curriculum
        // pros approved ects
        // os do ano curricular
        // so temos lines
        // saber o ano, agrupar
        // aprovado + enroled + enroling > 60 (ou 30, se ao semestre), tudo OK
        // senao, aviso

        final DegreeCurricularPlan dcp = getDegreeCurricularPlan();
        final int year = getConfiguration().getCurricularPeriod().getAbsoluteOrderOfChild();
        final CurricularPeriod configured = CurricularPeriodServices.getCurricularPeriod(dcp, year);
        if (configured == null) {
            return createFalseConfiguration();
        }

        final ICurriculum curriculum =
                enrolmentContext.getRegistration().getCurriculum(enrolmentContext.getExecutionPeriod().getExecutionYear());

        BigDecimal total = getCreditsApproved(curriculum, configured);
        total = total.add(getCreditsEnroledAndEnroling(enrolmentContext, configured));

        return total.compareTo(getCredits()) > 0 ? createTrue() : createWarningLabelled();
    }

    private BigDecimal getCreditsApproved(final ICurriculum curriculum, final CurricularPeriod curricularPeriod) {
        BigDecimal result = BigDecimal.ZERO;

        final Map<CurricularPeriod, BigDecimal> curricularPeriodCredits = CurricularPeriodServices.mapYearCredits(curriculum);
        final BigDecimal credits = curricularPeriodCredits.get(curricularPeriod);
        if (credits != null) {
            result = result.add(credits);
        }

        return result;
    }

    static private BigDecimal getCreditsEnroledAndEnroling(final EnrolmentContext enrolmentContext,
            final CurricularPeriod curricularPeriod) {

        BigDecimal result = BigDecimal.ZERO;

        final Map<CurricularPeriod, BigDecimal> curricularPeriodCredits = mapYearCredits(enrolmentContext);
        final BigDecimal credits = curricularPeriodCredits.get(curricularPeriod);
        if (credits != null) {
            result = result.add(credits);
        }

        return result;
    }

    static private Map<CurricularPeriod, BigDecimal> mapYearCredits(final EnrolmentContext enrolmentContext) {
        final Map<CurricularPeriod, BigDecimal> result = Maps.newHashMap();

        final DegreeCurricularPlan dcp = enrolmentContext.getStudentCurricularPlan().getDegreeCurricularPlan();

        for (final IDegreeModuleToEvaluate iter : getEnroledAndEnroling(enrolmentContext)) {

            final int year = iter.getContext().getCurricularYear();
            final CurricularPeriod curricularPeriod = CurricularPeriodServices.getCurricularPeriod(dcp, year);

            if (curricularPeriod != null) {

                final BigDecimal credits = BigDecimal.valueOf(iter.getEctsCredits());
                CurricularPeriodServices.addYearCredits(result, curricularPeriod, credits);
            }
        }

        return result;
    }

}
