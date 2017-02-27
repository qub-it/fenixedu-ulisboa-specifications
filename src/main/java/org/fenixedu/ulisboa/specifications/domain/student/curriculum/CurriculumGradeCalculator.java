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
package org.fenixedu.ulisboa.specifications.domain.student.curriculum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.curriculum.AverageType;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurriculumGradeCalculator
        implements org.fenixedu.academic.domain.student.curriculum.Curriculum.CurriculumGradeCalculator {

    static private final Logger logger = LoggerFactory.getLogger(CurriculumGradeCalculator.class);

    private BigDecimal sumPiCi;

    private BigDecimal sumPi;

    private Grade rawGrade;

    private Grade finalGrade;

    private void doCalculus(Curriculum curriculum) {
        sumPiCi = BigDecimal.ZERO;
        sumPi = BigDecimal.ZERO;
        countAverage(curriculum.getEnrolmentRelatedEntries(), curriculum.getAverageType());
        countAverage(curriculum.getDismissalRelatedEntries(), curriculum.getAverageType());
        BigDecimal avg = calculateAverage();
        rawGrade = Grade.createGrade(avg.setScale(2, getRawGradeRoundingMode()).toString(), GradeScale.TYPE20);
        finalGrade = Grade.createGrade(avg.setScale(0, RoundingMode.HALF_UP).toString(), GradeScale.TYPE20);
    }

    static private RoundingMode getRawGradeRoundingMode() {
        RoundingMode result = RoundingMode.HALF_UP;

        try {
            result = RoundingMode
                    .valueOf(ULisboaConfiguration.getConfiguration().getCurriculumGradeCalculatorRawGradeRoundingMode());
        } catch (final Throwable t) {
            logger.warn("Failed to read Curriculum Grade Calculator Raw Grade Rounding Mode");
        }

        return result;
    }

    private void countAverage(final Set<ICurriculumEntry> entries, AverageType averageType) {
        for (final ICurriculumEntry entry : entries) {
            if (entry.getGrade().isNumeric()) {
                final BigDecimal weigth = entry.getWeigthForCurriculum();

                if (averageType == AverageType.WEIGHTED) {
                    sumPi = sumPi.add(weigth);
                    sumPiCi = sumPiCi.add(entry.getWeigthForCurriculum().multiply(entry.getGrade().getNumericValue()));
                } else if (averageType == AverageType.SIMPLE) {
                    sumPi = sumPi.add(BigDecimal.ONE);
                    sumPiCi = sumPiCi.add(entry.getGrade().getNumericValue());
                } else {
                    throw new DomainException("Curriculum.average.type.not.supported");
                }
            }
        }
    }

    private BigDecimal calculateAverage() {
        return sumPi.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : sumPiCi.divide(sumPi, 2 * 2 + 1, RoundingMode.HALF_UP);
    }

    @Override
    public Grade rawGrade(Curriculum curriculum) {
        if (rawGrade == null) {
            doCalculus(curriculum);
        }
        return rawGrade;
    }

    @Override
    public Grade finalGrade(Curriculum curriculum) {
        if (finalGrade == null) {
            doCalculus(curriculum);
        }
        return finalGrade;
    }

    @Override
    public BigDecimal weigthedGradeSum(Curriculum curriculum) {
        if (sumPiCi == null) {
            doCalculus(curriculum);
        }
        return sumPiCi;
    }

}
