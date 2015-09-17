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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.CurricularPeriodRule;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsEnroledAsFirstTime;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsInCurricularPeriod;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsInEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsNotEnroled;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.ApprovedCredits;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.FlunkedCredits;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.FlunkedCurricularCourses;
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

            final DegreeType degreeType = dcp.getDegree().getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {
                
                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                // TODO legidio, config next week...
                CreditsInEnrolmentPeriod.create(configYear1, BigDecimal.valueOf(200));
                
            } else {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1, 5);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(84));
                CreditsInCurricularPeriod.createForYearInterval(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2, 5);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear3, BigDecimal.valueOf(84));
                CreditsInCurricularPeriod.createForYearInterval(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 3, 5);

                final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
                if (configYear4 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear4, BigDecimal.valueOf(84));
                CreditsInCurricularPeriod.createForYearInterval(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 4, 5);

                final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
                if (configYear5 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear5, BigDecimal.valueOf(84));
                CreditsInCurricularPeriod.createForYear(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 5);
            }
        }
    }

    static private void initRuleEnrolmentFMV() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleEnrolment for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
            if (configYear1 == null) {
                continue;
            }

            if (dcp.getDegree().getCode().equals("200") /*Doutoramento*/) {
                CreditsInEnrolmentPeriod.create(configYear1, BigDecimal.valueOf(180));
            } else {
                CreditsInEnrolmentPeriod.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, BigDecimal.ZERO, /* yearMin */2,
                        Math.min(dcp.getDurationInYears(), 6)/* yearMax */);
                CreditsNotEnroled.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            }

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            if (dcp.getDegree().getCode().equals("103") /*MSA*/) {
                CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(70));
                CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
                CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);
            } else if (dcp.getDegree().getCode().equals("113") /*MEZ*/) {
                CreditsInEnrolmentPeriod.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
                CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
                CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);
            } else if (dcp.getDegree().getCode().equals("200") /*Doutoramento*/) {
                CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(180));
            } else {
                CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(80));
                CreditsInCurricularPeriod.createForYearInterval(configYear2, BigDecimal.ZERO, /* yearMin */3, /* yearMax */6);
                CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
                CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);
            }

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear3, BigDecimal.valueOf(80));
            CreditsInCurricularPeriod.createForYearInterval(configYear3, BigDecimal.ZERO, /* yearMin */4, /* yearMax */6);
            CreditsNotEnroled.create(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            CreditsNotEnroled.create(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);
            CreditsNotEnroled.create(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear4, BigDecimal.valueOf(80));
            CreditsInCurricularPeriod.createForYearInterval(configYear4, BigDecimal.ZERO, /* yearMin */5, /* yearMax */6);
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 3);
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear5, BigDecimal.valueOf(80));
            CreditsInCurricularPeriod.createForYear(configYear5, BigDecimal.ZERO, /* year */6);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 4);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 3);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear6, BigDecimal.valueOf(40));
            CreditsNotEnroled.create(configYear6, BigDecimal.valueOf(30));
            CreditsNotEnroled.create(configYear6, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 5);
            CreditsNotEnroled.create(configYear6, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 4);
            CreditsNotEnroled.create(configYear6, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 3);
            CreditsNotEnroled.create(configYear6, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);
            CreditsNotEnroled.create(configYear6, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);
        }
    }

    static private void initRuleEnrolmentRUL() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleEnrolment for {}", dcp.getPresentationName());

            final boolean includeEnrolmentsOnValidation =
                    StringUtils.equals(dcp.getDegree().getCode(), "5595")
                            || StringUtils.equals(dcp.getDegree().getCode(), "9822");

            final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
            if (configYear1 == null) {
                continue;
            }

            CreditsInEnrolmentPeriod.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYearInterval(configYear1, BigDecimal.ZERO, 2/* yearMin */,
                    Math.min(dcp.getDurationInYears(), 6)/* yearMax */);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(84), includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYearInterval(configYear2, BigDecimal.ZERO, 3/* yearMin */,
                    Math.min(dcp.getDurationInYears(), 6)/* yearMax */);

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear3, BigDecimal.valueOf(84), includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYearInterval(configYear3, BigDecimal.ZERO, /* yearMin */4,
                    Math.min(dcp.getDurationInYears(), 6)/* yearMax */);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear4, BigDecimal.valueOf(84), includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYearInterval(configYear4, BigDecimal.ZERO, /* yearMin */5,
                    Math.min(dcp.getDurationInYears(), 6)/* yearMax */);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear5, BigDecimal.valueOf(84), includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod
                    .createForYear(configYear5, BigDecimal.ZERO, Math.min(dcp.getDurationInYears(), 6)/* yearMax */);

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear6, BigDecimal.valueOf(84), includeEnrolmentsOnValidation);

        }
    }

    static private void initRuleEnrolmentFMD() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleEnrolment for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
            if (configYear1 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear1, BigDecimal.valueOf(45), 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear1, BigDecimal.valueOf(45), 2);
            CreditsInCurricularPeriod.createForYearInterval(configYear1, BigDecimal.ZERO, /* yearMin */2, /* yearMax */
                    Math.min(dcp.getDurationInYears(), 5));
            CreditsNotEnroled.create(configYear1, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear2, BigDecimal.valueOf(45), 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear2, BigDecimal.valueOf(45), 2);
            CreditsInCurricularPeriod.createForYearInterval(configYear2, BigDecimal.ZERO, /* yearMin */3, /* yearMax */
                    Math.min(dcp.getDurationInYears(), 5));
            CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            CreditsNotEnroled.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear3, BigDecimal.valueOf(45), 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear3, BigDecimal.valueOf(45), 2);
            if (dcp.getDegreeDuration().intValue() > 3) {
                CreditsInCurricularPeriod.createForYearInterval(configYear3, BigDecimal.ZERO, /* yearMin */4, /* yearMax */
                        Math.min(dcp.getDurationInYears(), 5));
            }
            CreditsNotEnroled.create(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            CreditsNotEnroled.create(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);
            CreditsNotEnroled.create(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear4, BigDecimal.valueOf(45), 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear4, BigDecimal.valueOf(45), 2);
            if (dcp.getDegreeDuration().intValue() > 4) {
                CreditsInCurricularPeriod.createForYearInterval(configYear4, BigDecimal.ZERO, /* yearMin */5, /* yearMax */5);
            }
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 3);
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);
            CreditsNotEnroled.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear5, BigDecimal.valueOf(45), 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear5, BigDecimal.valueOf(45), 2);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 4);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 3);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 2);
            CreditsNotEnroled.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR, 1);

        }
    }

    static private void initRuleEnrolmentFL() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleEnrolment for {}", dcp.getPresentationName());

            for (int i = 1; i <= dcp.getDurationInYears(); i++) {

                final CurricularPeriodConfiguration config = findOrCreateConfig(dcp, i);
                if (config == null) {
                    continue;
                }

                if (dcp.getDegree().getCode().equals("5618")) {
                    CurricularPeriodRule rule = CreditsInEnrolmentPeriod.create(config, BigDecimal.valueOf(120));
                    rule.messagePrefixDisabled();

                } else if (dcp.getDegree().getCode().equals("5153")) {
                    CurricularPeriodRule rule = CreditsInEnrolmentPeriod.create(config, BigDecimal.valueOf(96));
                    rule.messagePrefixDisabled();

                } else {
                    CurricularPeriodRule rule = CreditsInEnrolmentPeriod.create(config, BigDecimal.valueOf(84));
                    rule.messagePrefixDisabled();

                    rule = CreditsEnroledAsFirstTime.create(config, BigDecimal.valueOf(60));
                    rule.messagePrefixDisabled();
                }

            }

        }
    }

    static private void initRuleTransitionFF() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleTransition for {}", dcp.getPresentationName());

            BigDecimal maxFlunked = BigDecimal.valueOf(24);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            ApprovedCredits.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.subtract(maxFlunked));

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            ApprovedCredits.create(configYear3,
                    FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(2)).subtract(maxFlunked));

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            ApprovedCredits.create(configYear4,
                    FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(3)).subtract(maxFlunked));

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            ApprovedCredits.create(configYear5,
                    FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(4)).subtract(maxFlunked));

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            ApprovedCredits.create(configYear6,
                    FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(5)).subtract(maxFlunked));
        }
    }

    static private void initRuleTransitionFMV() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            if (dcp.getDegree().getCode().equals("103") /*MSA*/) {
                FlunkedCredits.create(configYear2, BigDecimal.valueOf(10));
            } else if (dcp.getDegree().getCode().equals("113") /*MEZ*/) {
                FlunkedCredits.create(configYear2, BigDecimal.valueOf(0));
            } else if (dcp.getDegree().getCode().equals("200") /*Doutoramento*/) {
                FlunkedCredits.create(configYear2, BigDecimal.valueOf(0));
            } else {
                FlunkedCredits.create(configYear2, BigDecimal.valueOf(20));
                createStudentStatuteExecutiveRuleFor(configYear2, "38");
            }

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            FlunkedCredits.create(configYear3, BigDecimal.valueOf(20));
            FlunkedCredits.createForYear(configYear3, BigDecimal.valueOf(10), /* year */1);
            createStudentStatuteExecutiveRuleFor(configYear3, "39");

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            FlunkedCredits.create(configYear4, BigDecimal.valueOf(20));
            FlunkedCredits.createForYear(configYear4, BigDecimal.valueOf(10), /* year */2);
            FlunkedCredits.createForYear(configYear4, BigDecimal.ZERO, /* year */1);
            createStudentStatuteExecutiveRuleFor(configYear4, "40");

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            FlunkedCredits.create(configYear5, BigDecimal.valueOf(20));
            FlunkedCredits.createForYear(configYear5, BigDecimal.valueOf(10), /* year */3);
            FlunkedCredits.createForYearInterval(configYear5, BigDecimal.ZERO, /* yearMin */1, /* yearMax */2);
            createStudentStatuteExecutiveRuleFor(configYear5, "41");

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            FlunkedCredits.create(configYear6, BigDecimal.valueOf(20));
            FlunkedCredits.createForYear(configYear6, BigDecimal.valueOf(10), /* year */4);
            FlunkedCredits.createForYearInterval(configYear6, BigDecimal.ZERO, /* yearMin */1, /* yearMax */3);
            createStudentStatuteExecutiveRuleFor(configYear6, "42");

        }
    }

    protected static void createStudentStatuteExecutiveRuleFor(final CurricularPeriodConfiguration configuration,
            final String code) {
        final List<StatuteType> statutesByCode = StatuteType.readAll(i -> i.getCode().equals(code)).collect(Collectors.toList());

        if (!statutesByCode.isEmpty()) {
            StudentStatuteExecutiveRule.create(configuration, statutesByCode.get(0));
        }

    }

    static private void initRuleTransitionRUL() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleTransition for {}", dcp.getPresentationName());

            BigDecimal maxFlunked = BigDecimal.valueOf(24);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            ApprovedCredits.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.subtract(maxFlunked));

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            ApprovedCredits.create(configYear3,
                    FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(2)).subtract(maxFlunked));

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            ApprovedCredits.create(configYear4,
                    FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(3)).subtract(maxFlunked));

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            ApprovedCredits.create(configYear5,
                    FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(4)).subtract(maxFlunked));

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            ApprovedCredits.create(configYear6,
                    FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(5)).subtract(maxFlunked));
        }
    }

    static private void initRuleTransitionFL() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleTransition for {}", dcp.getPresentationName());

            BigDecimal maxFlunked = BigDecimal.valueOf(24);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            CurricularPeriodRule rule =
                    ApprovedCredits.create(configYear2, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.subtract(maxFlunked));
            rule.messagePrefixDisabled();

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            rule =
                    ApprovedCredits.create(configYear3, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(2))
                            .subtract(maxFlunked));
            rule.messagePrefixDisabled();

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            rule =
                    ApprovedCredits.create(configYear4, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(3))
                            .subtract(maxFlunked));
            rule.messagePrefixDisabled();

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            rule =
                    ApprovedCredits.create(configYear5, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(4))
                            .subtract(maxFlunked));
            rule.messagePrefixDisabled();

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            rule =
                    ApprovedCredits.create(configYear6, FlunkedCredits.FLUNKED_CREDITS_BY_YEAR.multiply(BigDecimal.valueOf(5))
                            .subtract(maxFlunked));
            rule.messagePrefixDisabled();
        }
    }

    static private void initRuleTransitionFMD() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.info("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            if (StringUtils.equals(dcp.getDegree().getCode(), "9556" /*LHO*/)) {
                FlunkedCredits.create(configYear2, BigDecimal.valueOf(15));
                FlunkedCurricularCourses.create(configYear2, BigDecimal.ZERO, 1, "9200114,9200112");
            } else if (StringUtils.equals(dcp.getDegree().getCode(), "9791" /*LPD*/)) {
                FlunkedCredits.create(configYear2, BigDecimal.valueOf(15));
            } else {
                FlunkedCredits.create(configYear2, BigDecimal.valueOf(15));
            }

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }

            if (StringUtils.equals(dcp.getDegree().getCode(), "9556" /*LHO*/)) {
                FlunkedCredits.create(configYear3, BigDecimal.valueOf(15));
                FlunkedCurricularCourses.create(configYear3, BigDecimal.ZERO, 1, "9200114,9200112");
                FlunkedCurricularCourses.create(configYear3, BigDecimal.ZERO, 2, "9200127,9200126");
            } else if (StringUtils.equals(dcp.getDegree().getCode(), "9791" /*LPD*/)) {
                FlunkedCredits.create(configYear3, BigDecimal.valueOf(15));
            } else {
                FlunkedCredits.create(configYear3, BigDecimal.ZERO);
            }

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            FlunkedCredits.create(configYear4, BigDecimal.ZERO);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            FlunkedCredits.create(configYear5, BigDecimal.ZERO);

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            FlunkedCredits.create(configYear6, BigDecimal.ZERO);

        }
    }

}
