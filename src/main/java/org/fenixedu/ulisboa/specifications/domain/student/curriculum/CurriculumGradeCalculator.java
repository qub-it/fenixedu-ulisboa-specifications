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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class CurriculumGradeCalculator
        implements org.fenixedu.academic.domain.student.curriculum.Curriculum.CurriculumGradeCalculator {

    static private final Logger logger = LoggerFactory.getLogger(CurriculumGradeCalculator.class);

    static final public RoundingMode ROUNDING_DEFAULT = RoundingMode.HALF_UP;

    static final public RoundingMode ROUNDING_TRUNCATE = RoundingMode.DOWN;

    static final private int FULL_SCALE = 2 * 2 + 1;

    static final public int RAW_SCALE = 2;

    private Curriculum curriculum;

    private BigDecimal sumPiCi;

    private BigDecimal sumPi;

    private Grade rawGrade;

    private Grade finalGrade;

    private void doCalculus(final Curriculum curriculum) {
        this.curriculum = curriculum;
        this.sumPiCi = BigDecimal.ZERO;
        this.sumPi = BigDecimal.ZERO;
        countAverage(curriculum.getEnrolmentRelatedEntries());
        countAverage(curriculum.getDismissalRelatedEntries());
        final BigDecimal avg = calculateAverage();

        // qubExtension, bug fix on finalGrade calculation, must use rawAvg
        final BigDecimal rawAvg = avg.setScale(RAW_SCALE,
                curriculum.getStudentCurricularPlan() == null ? ROUNDING_DEFAULT : getRawGradeRoundingMode(
                        curriculum.getStudentCurricularPlan().getDegree()));
        this.rawGrade = Grade.createGrade(rawAvg.toString(), GradeScale.TYPE20);
        this.finalGrade = Grade.createGrade(rawAvg.setScale(0, ROUNDING_DEFAULT).toString(), GradeScale.TYPE20);
    }

    static private RoundingMode getRawGradeRoundingMode(final Degree degree) {
        RoundingMode result = ROUNDING_DEFAULT;

        final String read = ULisboaConfiguration.getConfiguration().getCurriculumGradeCalculatorRawGradeRoundingModeForDegrees();
        final List<String> degreeCodes = read == null || read.isEmpty() ? Lists.newArrayList() : Arrays.asList(read.split(","));

        if (degreeCodes.isEmpty() || degreeCodes.contains(degree.getCode())) {

            try {
                result = RoundingMode
                        .valueOf(ULisboaConfiguration.getConfiguration().getCurriculumGradeCalculatorRawGradeRoundingMode());
            } catch (final Throwable t) {
                logger.warn("Failed to read Curriculum Grade Calculator Raw Grade Rounding Mode");
            }
        }

        return result;
    }

    private void countAverage(final Set<ICurriculumEntry> entries) {
        for (final ICurriculumEntry entry : entries) {
            if (entry.getGrade().isNumeric()) {
                final BigDecimal weigth = entry.getWeigthForCurriculum();

                sumPi = sumPi.add(weigth);
                sumPiCi = sumPiCi.add(entry.getWeigthForCurriculum().multiply(entry.getGrade().getNumericValue()));
            }
        }
    }

    protected BigDecimal calculateAverage() {
        return sumPi.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : sumPiCi.divide(sumPi, FULL_SCALE, ROUNDING_DEFAULT);
    }

    public BigDecimal calculateAverage(Curriculum curriculum) {
        if (sumPiCi == null) {
            doCalculus(curriculum);
        }

        return calculateAverage();
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

    public Curriculum getCurriculum() {
        return curriculum;
    }

    public BigDecimal getSumPiCi() {
        return sumPiCi;
    }

    public BigDecimal getSumPi() {
        return sumPi;
    }

    public boolean isCalculatorForDegree() {
        return true;
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        return getCurriculum() == null ? null : getCurriculum().getStudentCurricularPlan();
    }

}
