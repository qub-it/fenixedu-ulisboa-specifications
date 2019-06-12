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

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class CurriculumGradeCalculator extends org.fenixedu.academic.domain.student.curriculum.CurriculumGradeCalculator {

    static private final Logger logger = LoggerFactory.getLogger(CurriculumGradeCalculator.class);

    protected RoundingMode getRawGradeRoundingMode(final Degree degree) {
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

}
