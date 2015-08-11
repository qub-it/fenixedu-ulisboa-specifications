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
package org.fenixedu.ulisboa.specifications.domain.curricularPeriod;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsInCurricularPeriod;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsInEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsNotEnroled;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.FlunkedCredits;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.StudentStatuteExecutiveRule;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

public class CurricularPeriodConfigurationInitializer {

    private static final Logger logger = LoggerFactory.getLogger(CurricularPeriodConfigurationInitializer.class);

    static public void init() {
        if (ULisboaConfiguration.getConfiguration().getCurricularPeriodConfigurationInitialize()) {
            reset();
            create();
        }
    }

    @Atomic
    static private void reset() {
        final Set<CurricularPeriodConfiguration> configurations =
                ULisboaSpecificationsRoot.getInstance().getCurricularPeriodConfigurationSet();

        if (configurations.isEmpty()) {
            logger.info("No previous init to reset");

        } else {

            logger.info("About to reset");
            for (; !configurations.isEmpty(); configurations.iterator().next().delete()) {
                ;
            }

        }
    }

    static private void create() {
        final String acronym = Bennu.getInstance().getInstitutionUnit().getAcronym();
        logger.info("Init for " + acronym);

        switch (acronym) {

        case "FF":
            initRuleEnrolmentFF();
            initRuleTransitionFF();
            break;

        case "FMD":
            initRuleEnrolmentFMD();
            initRuleTransitionFMD();
            break;

        case "FMV":
            initRuleEnrolmentFMV();
            initRuleTransitionFMV();
            break;

        case "FL":
            initRuleEnrolmentFL();
            initRuleTransitionFL();
            break;

        case "RUL":
            initRuleEnrolmentRUL();
            initRuleTransitionRUL();
            break;

        default:
            logger.info("Init not found for " + acronym);
            break;
        }
    }

    static private ExecutionYear EXECUTION_YEAR;

    static public ExecutionYear getExecutionYear() {
        if (EXECUTION_YEAR == null) {
            EXECUTION_YEAR = ExecutionYear.readExecutionYearByName("2015/2016");
        }

        return EXECUTION_YEAR;
    }

    static private CurricularPeriodConfiguration findOrCreateConfig(final DegreeCurricularPlan dcp, final Integer year) {
        CurricularPeriodConfiguration result = null;

        final CurricularPeriod curricularPeriod = CurricularPeriodServices.getCurricularPeriod(dcp, year);
        if (curricularPeriod != null) {
            result = curricularPeriod.getConfiguration();

            if (result == null) {
                result = CurricularPeriodConfiguration.create(curricularPeriod);
            }
        }

        return result;
    }

    static private void initRuleEnrolmentFF() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleEnrolment for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
            if (configYear1 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(84));
            CreditsInCurricularPeriod.createForYear(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear3, BigDecimal.valueOf(84));
            CreditsInCurricularPeriod.createForYear(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear4, BigDecimal.valueOf(84));
            CreditsInCurricularPeriod.createForYear(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear5, BigDecimal.valueOf(84));
            CreditsInCurricularPeriod.createForYear(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);
        }
    }

    static private void initRuleEnrolmentFMD() {
        // TODO legidio
    }

    static private void initRuleEnrolmentFMV() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleEnrolment for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
            if (configYear1 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            CreditsInCurricularPeriod.createForYearInterval(configYear1, BigDecimal.ZERO, /* yearMin */2, /* yearMax */6);
            CreditsNotEnroled.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(80));
            CreditsInCurricularPeriod.createForYearInterval(configYear2, BigDecimal.ZERO, /* yearMin */3, /* yearMax */6);
            CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear3, BigDecimal.valueOf(80));
            CreditsInCurricularPeriod.createForYearInterval(configYear3, BigDecimal.ZERO, /* yearMin */4, /* yearMax */6);
            CreditsNotEnroled.create(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear4, BigDecimal.valueOf(80));
            CreditsInCurricularPeriod.createForYearInterval(configYear4, BigDecimal.ZERO, /* yearMin */5, /* yearMax */6);
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear5, BigDecimal.valueOf(80));
            CreditsInCurricularPeriod.createForYear(configYear5, BigDecimal.ZERO, /* year */6);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear6, BigDecimal.valueOf(50));
            CreditsNotEnroled.create(configYear6, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
        }
    }

    static private void initRuleEnrolmentFL() {
        // TODO legidio
    }

    static private void initRuleEnrolmentRUL() {
        // TODO legidio
    }

    static private void initRuleTransitionFF() {
        // TODO legidio
    }

    static private void initRuleTransitionFMD() {
        // TODO legidio
    }

    static private void initRuleTransitionFMV() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            FlunkedCredits.create(configYear2, BigDecimal.valueOf(20));

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            FlunkedCredits.create(configYear3, BigDecimal.valueOf(20));
            FlunkedCredits.createForYear(configYear3, BigDecimal.valueOf(10), /* year */1);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            FlunkedCredits.create(configYear4, BigDecimal.valueOf(20));
            FlunkedCredits.createForYear(configYear4, BigDecimal.valueOf(10), /* year */2);
            FlunkedCredits.createForYear(configYear4, BigDecimal.ZERO, /* year */1);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            FlunkedCredits.create(configYear5, BigDecimal.valueOf(20));
            FlunkedCredits.createForYear(configYear5, BigDecimal.valueOf(10), /* year */3);
            FlunkedCredits.createForYearInterval(configYear5, BigDecimal.ZERO, /* yearMin */1, /* yearMax */2);

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            FlunkedCredits.create(configYear6, BigDecimal.valueOf(20));
            FlunkedCredits.createForYear(configYear6, BigDecimal.valueOf(10), /* year */4);
            FlunkedCredits.createForYearInterval(configYear6, BigDecimal.ZERO, /* yearMin */1, /* yearMax */3);
            // TODO legidio final Stream<StatuteType> stream = StatuteType.readAll(i -> i.getCode().equals("36"));
            // TODO legidio StudentStatuteExecutiveRule.create(configYear6, stream.findFirst().get());
        }
    }

    static private void initRuleTransitionFL() {
        // TODO legidio
    }

    static private void initRuleTransitionRUL() {
        // TODO legidio
    }

}
