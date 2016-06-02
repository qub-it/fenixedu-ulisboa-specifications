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
package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import pt.ist.fenixWebFramework.rendererExtensions.util.IPresentableEnum;

public enum AggregationGradeCalculator implements IPresentableEnum {

    BY_COMPOSITION {

        @Override
        public Grade calculate(final CurriculumAggregator aggregator, final StudentCurricularPlan plan) {

            final BigDecimal sumEntriesInput =
                    aggregator.getEntriesSet().stream().map(calculateEntryInput(plan)).reduce(BigDecimal.ZERO, BigDecimal::add);

            final BigDecimal sumEntriesFactor =
                    aggregator.getEntriesSet().stream().map(e -> e.getGradeFactor()).reduce(BigDecimal.ZERO, BigDecimal::add);

            final BigDecimal value = sumEntriesInput.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : sumEntriesInput
                    .divide(sumEntriesFactor, RoundingMode.HALF_UP);

            return createGrade(value);
        }
    },

    BY_WEIGHING {

        @Override
        public Grade calculate(final CurriculumAggregator aggregator, final StudentCurricularPlan plan) {

            final BigDecimal sumEntriesInput =
                    aggregator.getEntriesSet().stream().map(calculateEntryInput(plan)).reduce(BigDecimal.ZERO, BigDecimal::add);

            final BigDecimal value = sumEntriesInput;

            return createGrade(value);
        }

    };

    @Override
    public String getLocalizedName() {
        return ULisboaSpecificationsUtil.bundleI18N(AggregationGradeCalculator.class.getSimpleName() + "." + name())
                .getContent(I18N.getLocale());
    }

    public GradeScale getGradeScale() {
        return GradeScale.TYPE20;
    }

    abstract public Grade calculate(final CurriculumAggregator aggregator, final StudentCurricularPlan plan);

    static private Function<CurriculumAggregatorEntry, BigDecimal> calculateEntryInput(final StudentCurricularPlan plan) {
        return i -> i.calculateGradeValue(plan).multiply(i.getGradeFactor());
    }

    static private Grade createGrade(final BigDecimal value) {
        if (BigDecimal.valueOf(20d).compareTo(value) < 0) {
            throw new ULisboaSpecificationsDomainException("error.CurriculumAggregator.GradeScale.unsupports.ConclusionGrade");
        }

        return Grade.createGrade(value.setScale(0, RoundingMode.HALF_UP).toString(), getGradeScale());
    }

}
