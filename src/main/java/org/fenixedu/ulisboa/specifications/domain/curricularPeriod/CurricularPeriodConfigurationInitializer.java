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
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.CurricularPeriodRule;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsEnroledAsFirstTime;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsInCurricularPeriod;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsInEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment.CreditsNotEnroled;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.ApprovedCourses;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.ApprovedCredits;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.FlunkedCredits;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.FlunkedCurricularCourses;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition.StudentStatuteExecutiveRule;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import pt.ist.fenixframework.Atomic;

public class CurricularPeriodConfigurationInitializer {

    static private final Logger logger = LoggerFactory.getLogger(CurricularPeriodConfigurationInitializer.class);

    // 'shortcuts'
    static final private BigDecimal _0 = BigDecimal.ZERO;
    static final private BigDecimal _2 = BigDecimal.valueOf(2);
    static final private BigDecimal _3 = BigDecimal.valueOf(3);
    static final private BigDecimal _4 = BigDecimal.valueOf(4);
    static final private BigDecimal _5 = BigDecimal.valueOf(5);
    static final private BigDecimal _8 = BigDecimal.valueOf(8);
    static final private BigDecimal _10 = BigDecimal.valueOf(10);
    static final private BigDecimal _12 = BigDecimal.valueOf(12);
    static final private BigDecimal _15 = BigDecimal.valueOf(15);
    static final private BigDecimal _18 = BigDecimal.valueOf(18);
    static final private BigDecimal _20 = BigDecimal.valueOf(20);
    static final private BigDecimal _24 = BigDecimal.valueOf(24);
    static final private BigDecimal _30 = BigDecimal.valueOf(30);
    static final private BigDecimal _40 = BigDecimal.valueOf(40);
    static final private BigDecimal _42 = BigDecimal.valueOf(42);
    static final private BigDecimal _45 = BigDecimal.valueOf(45);
    static final private BigDecimal _60 = FlunkedCredits.FLUNKED_CREDITS_BY_YEAR;
    static final private BigDecimal _72 = BigDecimal.valueOf(72);
    static final private BigDecimal _80 = BigDecimal.valueOf(80);
    static final private BigDecimal _84 = BigDecimal.valueOf(84);
    static final private BigDecimal _180 = BigDecimal.valueOf(180);

    static public void init() {
        //TODO: delete
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
        Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();
        if (institutionUnit == null) {
            //Bennu was not bootstrapped yet
            return;
        }
        final String acronym = institutionUnit.getAcronym();
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

        case "FBA":
            initRuleEnrolmentFBA();
            initRuleTransitionFBA();
            break;

        case "FC":
            initRuleEnrolmentFCUL();
            initRuleTransitionFCUL();
            break;

        case "FDUL":
            initRuleEnrolmentFD();
            initRuleTransitionFD();
            break;

        case "FM":
            initRuleEnrolmentFM();
            initRuleTransitionFM();
            break;

        case "FMH":
            initRuleEnrolmentFMH();
            initRuleTransitionFMH();
            break;

        case "FP":
            initRuleEnrolmentFP();
            initRuleTransitionFP();
            break;

        case "IE":
            initRuleEnrolmentIE();
            initRuleTransitionIE();
            break;

        case "ICS":
            initRuleEnrolmentICS();
            initRuleTransitionICS();
            break;

        case "IGOT":
            initRuleEnrolmentIGOT();
            initRuleTransitionIGOT();
            break;

        case "FA":
            initRuleEnrolmentFA();
            initRuleTransitionFA();
            break;

        default:
            logger.info("Init not found for " + acronym);
            break;
        }
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
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            final DegreeType degreeType = dcp.getDegree().getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _60, 1, 2);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _84);
                CreditsInCurricularPeriod.createForYear(configYear2, _60, 2);

            } else {

                final int yearMax = Math.min(dcp.getDurationInYears(), 5);

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _60, 1, yearMax);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear2, _60, 2, yearMax);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear3, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear3, _60, 3, yearMax);

                final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
                if (configYear4 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear4, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear4, _60, 4, yearMax);

                final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
                if (configYear5 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear5, _84);
                CreditsInCurricularPeriod.createForYear(configYear5, _60, yearMax);
            }
        }
    }

    static private void initRuleEnrolmentFMV() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
            if (configYear1 == null) {
                continue;
            }

            if (dcp.getDegree().getCode().equals("200") /*Doutoramento*/) {
                CreditsInEnrolmentPeriod.create(configYear1, _180);
            } else {
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, /* yearMin */2,
                        Math.min(dcp.getDurationInYears(), 6)/* yearMax */);
                CreditsNotEnroled.create(configYear1, _60);
            }

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            if (dcp.getDegree().getCode().equals("103") /*MSA*/) {
                CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(70));
                CreditsNotEnroled.create(configYear2, _60);
                CreditsNotEnroled.create(configYear2, _60, 1);
            } else if (dcp.getDegree().getCode().equals("113") /*MEZ*/) {
                CreditsInEnrolmentPeriod.create(configYear2, _60);
                CreditsNotEnroled.create(configYear2, _60);
                CreditsNotEnroled.create(configYear2, _60, 1);
            } else if (dcp.getDegree().getCode().equals("200") /*Doutoramento*/) {
                CreditsInEnrolmentPeriod.create(configYear2, _180);
            } else {
                CreditsInEnrolmentPeriod.create(configYear2, _80);
                CreditsInCurricularPeriod.createForYearInterval(configYear2, _0, /* yearMin */3, /* yearMax */6);
                CreditsNotEnroled.create(configYear2, _60);
                CreditsNotEnroled.create(configYear2, _60, 1);
            }

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear3, _80);
            CreditsInCurricularPeriod.createForYearInterval(configYear3, _0, /* yearMin */4, /* yearMax */6);
            CreditsNotEnroled.create(configYear3, _60);
            CreditsNotEnroled.create(configYear3, _60, 2);
            CreditsNotEnroled.create(configYear3, _60, 1);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear4, _80);
            CreditsInCurricularPeriod.createForYearInterval(configYear4, _0, /* yearMin */5, /* yearMax */6);
            CreditsNotEnroled.create(configYear4, _60);
            CreditsNotEnroled.create(configYear4, _60, 3);
            CreditsNotEnroled.create(configYear4, _60, 2);
            CreditsNotEnroled.create(configYear4, _60, 1);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear5, _80);
            CreditsInCurricularPeriod.createForYear(configYear5, _0, /* year */6);
            CreditsNotEnroled.create(configYear5, _60);
            CreditsNotEnroled.create(configYear5, _60, 4);
            CreditsNotEnroled.create(configYear5, _60, 3);
            CreditsNotEnroled.create(configYear5, _60, 2);
            CreditsNotEnroled.create(configYear5, _60, 1);

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear6, _40);
            CreditsNotEnroled.create(configYear6, _30);
            CreditsNotEnroled.create(configYear6, _60, 5);
            CreditsNotEnroled.create(configYear6, _60, 4);
            CreditsNotEnroled.create(configYear6, _60, 3);
            CreditsNotEnroled.create(configYear6, _60, 2);
            CreditsNotEnroled.create(configYear6, _60, 1);
        }
    }

    static private void initRuleEnrolmentRUL() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            // exceptions
            final boolean configYear2ExtendedEcts = StringUtils.equals(dcp.getDegree().getCode(), "5315");
            final boolean includeEnrolmentsOnValidation = StringUtils.equals(dcp.getDegree().getCode(), "5595")
                    || StringUtils.equals(dcp.getDegree().getCode(), "9822");

            final int yearMax = Math.min(dcp.getDurationInYears(), 6);

            final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
            if (configYear1 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear1, _60, includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, 2/* yearMin */, yearMax);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(configYear2ExtendedEcts ? 120 : 84),
                    includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYearInterval(configYear2, _0, 3/* yearMin */, yearMax);

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear3, _84, includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYearInterval(configYear3, _0, /* yearMin */4, yearMax);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear4, _84, includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYearInterval(configYear4, _0, /* yearMin */5, yearMax);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear5, _84, includeEnrolmentsOnValidation);
            CreditsInCurricularPeriod.createForYear(configYear5, _0, yearMax);

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.create(configYear6, _84, includeEnrolmentsOnValidation);

        }
    }

    static private void initRuleEnrolmentFMD() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            final int yearMax = Math.min(dcp.getDurationInYears(), 5);

            final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
            if (configYear1 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear1, _45, 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear1, _45, 2);
            CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, /* yearMin */2, yearMax);
            CreditsNotEnroled.create(configYear1, _60);

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear2, _45, 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear2, _45, 2);
            CreditsInCurricularPeriod.createForYearInterval(configYear2, _0, /* yearMin */3, yearMax);
            CreditsNotEnroled.create(configYear2, _60);
            CreditsNotEnroled.create(configYear2, _60, 1);

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear3, _45, 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear3, _45, 2);
            if (dcp.getDegreeDuration().intValue() > 3) {
                CreditsInCurricularPeriod.createForYearInterval(configYear3, _0, /* yearMin */4, yearMax);
            }
            CreditsNotEnroled.create(configYear3, _60);
            CreditsNotEnroled.create(configYear3, _60, 2);
            CreditsNotEnroled.create(configYear3, _60, 1);

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear4, _45, 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear4, _45, 2);
            if (dcp.getDegreeDuration().intValue() > 4) {
                CreditsInCurricularPeriod.createForYearInterval(configYear4, _0, /* yearMin */5, /* yearMax */5);
            }
            CreditsNotEnroled.create(configYear4, _60);
            CreditsNotEnroled.create(configYear4, _60, 3);
            CreditsNotEnroled.create(configYear4, _60, 2);
            CreditsNotEnroled.create(configYear4, _60, 1);

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            CreditsInEnrolmentPeriod.createForSemester(configYear5, _45, 1);
            CreditsInEnrolmentPeriod.createForSemester(configYear5, _45, 2);
            CreditsNotEnroled.create(configYear5, _60);
            CreditsNotEnroled.create(configYear5, _60, 4);
            CreditsNotEnroled.create(configYear5, _60, 3);
            CreditsNotEnroled.create(configYear5, _60, 2);
            CreditsNotEnroled.create(configYear5, _60, 1);

        }
    }

    static private void initRuleEnrolmentFL() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            for (int i = 1; i <= dcp.getDurationInYears(); i++) {

                final CurricularPeriodConfiguration config = findOrCreateConfig(dcp, i);
                if (config == null) {
                    continue;
                }
                if (dcp == null || dcp.getDegree() == null || dcp.getDegree().getCode() == null) {
                    continue;
                }
                if (dcp.getDegree().getCode().equals("5618")) {
                    CurricularPeriodRule rule = CreditsInEnrolmentPeriod.create(config, BigDecimal.valueOf(120));
                    rule.messagePrefixDisabled();

                } else if (dcp.getDegree().getCode().equals("5153")) {
                    CurricularPeriodRule rule = CreditsInEnrolmentPeriod.create(config, BigDecimal.valueOf(96));
                    rule.messagePrefixDisabled();

                } else {
                    CurricularPeriodRule rule = CreditsInEnrolmentPeriod.create(config, _84);
                    rule.messagePrefixDisabled();

                    rule = CreditsEnroledAsFirstTime.create(config, _60);
                    rule.messagePrefixDisabled();
                }

            }

        }
    }

    static private void initRuleTransitionFF() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            ApprovedCredits.create(configYear2, _60.subtract(_24));

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            ApprovedCredits.create(configYear3, _60.multiply(_2).subtract(_24));

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            ApprovedCredits.create(configYear4, _60.multiply(_3).subtract(_24));

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            ApprovedCredits.create(configYear5, _60.multiply(_4).subtract(_24));

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            ApprovedCredits.create(configYear6, _60.multiply(_5).subtract(_24));
        }
    }

    static private void initRuleTransitionFMV() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            if (dcp.getDegree().getCode().equals("103") /*MSA*/) {
                FlunkedCredits.create(configYear2, _10);
            } else if (dcp.getDegree().getCode().equals("113") /*MEZ*/) {
                FlunkedCredits.create(configYear2, _0);
            } else if (dcp.getDegree().getCode().equals("200") /*Doutoramento*/) {
                FlunkedCredits.create(configYear2, _0);
            } else {
                FlunkedCredits.create(configYear2, _20);
                createStudentStatuteExecutiveRuleFor(configYear2, "38");
            }

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            FlunkedCredits.create(configYear3, _20);
            FlunkedCredits.createForYear(configYear3, _10, /* year */1);
            createStudentStatuteExecutiveRuleFor(configYear3, "39");

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            FlunkedCredits.create(configYear4, _20);
            FlunkedCredits.createForYear(configYear4, _10, /* year */2);
            FlunkedCredits.createForYear(configYear4, _0, /* year */1);
            createStudentStatuteExecutiveRuleFor(configYear4, "40");

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            FlunkedCredits.create(configYear5, _20);
            FlunkedCredits.createForYear(configYear5, _10, /* year */3);
            FlunkedCredits.createForYearInterval(configYear5, _0, /* yearMin */1, /* yearMax */2);
            createStudentStatuteExecutiveRuleFor(configYear5, "41");

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            FlunkedCredits.create(configYear6, _20);
            FlunkedCredits.createForYear(configYear6, _10, /* year */4);
            FlunkedCredits.createForYearInterval(configYear6, _0, /* yearMin */1, /* yearMax */3);
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
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            ApprovedCredits.create(configYear2, _60.subtract(_24));
            createStudentStatuteExecutiveRuleFor(configYear2, "42");

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            ApprovedCredits.create(configYear3, _60.multiply(_2).subtract(_24));
            createStudentStatuteExecutiveRuleFor(configYear3, "43");

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            ApprovedCredits.create(configYear4, _60.multiply(_3).subtract(_24));
            createStudentStatuteExecutiveRuleFor(configYear4, "44");

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            ApprovedCredits.create(configYear5, _60.multiply(_4).subtract(_24));
            createStudentStatuteExecutiveRuleFor(configYear5, "45");

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            ApprovedCredits.create(configYear6, _60.multiply(_5).subtract(_24));
            createStudentStatuteExecutiveRuleFor(configYear6, "46");
        }
    }

    static private void initRuleTransitionFL() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }
            CurricularPeriodRule rule = ApprovedCredits.create(configYear2, _60.subtract(_24), true);
            rule.messagePrefixDisabled();
            createStudentStatuteExecutiveRuleFor(configYear2, "102");

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            rule = ApprovedCredits.create(configYear3, _60.multiply(_2).subtract(_24), true);
            rule.messagePrefixDisabled();
            createStudentStatuteExecutiveRuleFor(configYear3, "103");

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            rule = ApprovedCredits.create(configYear4, _60.multiply(_3).subtract(_24), true);
            rule.messagePrefixDisabled();
            createStudentStatuteExecutiveRuleFor(configYear4, "104");

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            rule = ApprovedCredits.create(configYear5, _60.multiply(_4).subtract(_24), true);
            rule.messagePrefixDisabled();
            createStudentStatuteExecutiveRuleFor(configYear5, "105");

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            rule = ApprovedCredits.create(configYear6, _60.multiply(_5).subtract(_24), true);
            rule.messagePrefixDisabled();
            createStudentStatuteExecutiveRuleFor(configYear6, "106");
        }
    }

    static private void initRuleTransitionFMD() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            if (StringUtils.equals(dcp.getDegree().getCode(), "9556" /*LHO*/)) {
                FlunkedCredits.create(configYear2, _15);
                FlunkedCurricularCourses.create(configYear2, _0, 1, "9200114,9200112");
            } else if (StringUtils.equals(dcp.getDegree().getCode(), "9791" /*LPD*/)) {
                FlunkedCredits.create(configYear2, _15);
            } else if (StringUtils.equals(dcp.getDegree().getCode(), "5596")
                    || StringUtils.equals(dcp.getDegree().getCode(), "5597")) {
                FlunkedCredits.create(configYear2, _0);
            } else {
                FlunkedCredits.create(configYear2, _15);
            }
            createStudentStatuteExecutiveRuleFor(configYear2, "29");

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }

            if (StringUtils.equals(dcp.getDegree().getCode(), "9556" /*LHO*/)) {
                FlunkedCredits.create(configYear3, _15);
                FlunkedCurricularCourses.create(configYear3, _0, 1, "9200114,9200112");
                FlunkedCurricularCourses.create(configYear3, _0, 2, "9200127,9200126");
            } else if (StringUtils.equals(dcp.getDegree().getCode(), "9791" /*LPD*/)) {
                FlunkedCredits.create(configYear3, _15);
            } else {
                FlunkedCredits.create(configYear3, _0);
            }
            createStudentStatuteExecutiveRuleFor(configYear3, "30");

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            FlunkedCredits.create(configYear4, _0);
            createStudentStatuteExecutiveRuleFor(configYear4, "31");

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            FlunkedCredits.create(configYear5, _0);
            createStudentStatuteExecutiveRuleFor(configYear5, "32");

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            FlunkedCredits.create(configYear6, _0);
            createStudentStatuteExecutiveRuleFor(configYear6, "33");
        }
    }

    static private void initRuleEnrolmentSecondYear() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            final DegreeType degreeType = dcp.getDegree().getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _60, 1, 2);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _84);
                CreditsInCurricularPeriod.createForYear(configYear2, _60, 2);

            } else {

                final int yearMax = Math.min(dcp.getDurationInYears(), 5);

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _60, 1, yearMax);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear2, _60, 2, yearMax);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear3, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear3, _60, 3, yearMax);

                final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
                if (configYear4 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear4, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear4, _60, 4, yearMax);

                final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
                if (configYear5 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear5, _84);
                CreditsInCurricularPeriod.createForYear(configYear5, _60, yearMax);
            }
        }
    }

    static private void initRuleTransitionSecondYear() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
            if (configYear2 == null) {
                continue;
            }

            ApprovedCredits.create(configYear2, _60.subtract(_24));

            final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
            if (configYear3 == null) {
                continue;
            }
            ApprovedCredits.create(configYear3, _60.multiply(_2).subtract(_24));

            final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
            if (configYear4 == null) {
                continue;
            }
            ApprovedCredits.create(configYear4, _60.multiply(_3).subtract(_24));

            final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
            if (configYear5 == null) {
                continue;
            }
            ApprovedCredits.create(configYear5, _60.multiply(_4).subtract(_24));

            final CurricularPeriodConfiguration configYear6 = findOrCreateConfig(dcp, 6);
            if (configYear6 == null) {
                continue;
            }
            ApprovedCredits.create(configYear6, _60.multiply(_5).subtract(_24));
        }
    }

    static private void initRuleEnrolmentFBA() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            final Degree degree = dcp.getDegree();
            final DegreeType degreeType = degree.getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, 1, 2);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                // TODO N+L
                CreditsInEnrolmentPeriod.create(configYear2, _84);

            } else if (degreeType.isThirdCycle()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _60, 1, 2);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _84);
                CreditsInCurricularPeriod.createForYear(configYear2, _60, 2);

            } else if (degreeType.isSpecializationCycle()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);

            } else {

                final int yearMax = Math.min(dcp.getDurationInYears(), 5);

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _60, 1, yearMax);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear2, _60, 2, yearMax);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear3, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear3, _60, 3, yearMax);

                final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
                if (configYear4 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear4, _84);
                CreditsInCurricularPeriod.createForYearInterval(configYear4, _60, 4, yearMax);

                final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
                if (configYear5 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear5, _84);
                CreditsInCurricularPeriod.createForYear(configYear5, _60, yearMax);
            }
        }
    }

    static private void initRuleEnrolmentFCUL() {
        initRuleEnrolmentSecondYear();
    }

    static private void initRuleEnrolmentFD() {
        initRuleEnrolmentSecondYear();
    }

    static private void initRuleEnrolmentFM() {
        initRuleEnrolmentSecondYear();
    }

    static private void initRuleEnrolmentFMH() {
        initRuleEnrolmentSecondYear();
    }

    static private void initRuleEnrolmentFP() {

        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            final Degree degree = dcp.getDegree();
            final DegreeType degreeType = degree.getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYear(configYear1, _0, 2);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _72, true);

            } else if (degreeType.isThirdCycle()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _60);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear3, _60);

            } else if (degreeType.isSpecializationCycle()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);

            } else {

                final int yearMax = Math.min(dcp.getDurationInYears(), 5);

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.createForSemester(configYear1, _30, 1 /* semester */);
                CreditsInEnrolmentPeriod.createForSemester(configYear1, _30, 2 /* semester */);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, 2, yearMax);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.createForSemester(configYear2, _42, 1 /* semester */);
                CreditsInEnrolmentPeriod.createForSemester(configYear2, _42, 2 /* semester */);
                CreditsInCurricularPeriod.createForYearInterval(configYear2, _0, 3, yearMax);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.createForSemester(configYear3, _42, 1 /* semester */);
                CreditsInEnrolmentPeriod.createForSemester(configYear3, _42, 2 /* semester */);
                CreditsInCurricularPeriod.createForYearInterval(configYear3, _0, 4, yearMax);

                final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
                if (configYear4 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.createForSemester(configYear4, _42, 1 /* semester */);
                CreditsInEnrolmentPeriod.createForSemester(configYear4, _42, 2 /* semester */);
                CreditsInCurricularPeriod.createForYearInterval(configYear4, _0, 5, yearMax);

                final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
                if (configYear5 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.createForSemester(configYear5, _42, 1 /* semester */);
                CreditsInEnrolmentPeriod.createForSemester(configYear5, _42, 2 /* semester */);
            }
        }
    }

    static private void initRuleEnrolmentIE() {

        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            final Degree degree = dcp.getDegree();
            final DegreeType degreeType = degree.getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {

                if (is_IE_Mest_12_atraso(degree)) {

                    final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                    if (configYear1 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.createForSemester(configYear1, _30, /* semester */ 1);
                    CreditsInEnrolmentPeriod.createForSemester(configYear1, _30, /* semester */ 2);
                    CreditsInCurricularPeriod.createForYear(configYear1, _0, 2);

                    final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                    if (configYear2 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.createForSemester(configYear2, _42, /* semester */ 1);
                    CreditsInEnrolmentPeriod.createForSemester(configYear2, _42, /* semester */ 2);
                }

                if (is_IE_Mest_15_atraso(degree)) {

                    final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                    if (configYear1 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.create(configYear1, _60, true);
                    CreditsInCurricularPeriod.createForYear(configYear1, _0, 2);

                    final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                    if (configYear2 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.create(configYear2, BigDecimal.valueOf(75), true);
                }

            } else if (degreeType.isThirdCycle()) {

                final int yearMax = Math.min(dcp.getDurationInYears(), 3);

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, 2, yearMax);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _60);
                CreditsInCurricularPeriod.createForYearInterval(configYear2, _0, 3, yearMax);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear3, _60);

            } else {

                final int yearMax = Math.min(dcp.getDurationInYears(), 3);

                if (is_IE_Lic_9026(degree)) {

                    final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                    if (configYear1 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.createForSemester(configYear1, _30, 1 /* semester */);
                    CreditsInEnrolmentPeriod.createForSemester(configYear1, _30, 2 /* semester */);
                    CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, 2, yearMax);

                    final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                    if (configYear2 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.createForSemester(configYear2, _42, 1 /* semester */);
                    CreditsInEnrolmentPeriod.createForSemester(configYear2, _42, 2 /* semester */);
                    CreditsInCurricularPeriod.createForYearInterval(configYear2, _0, 3, yearMax);

                    final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                    if (configYear3 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.createForSemester(configYear3, _42, 1 /* semester */);
                    CreditsInEnrolmentPeriod.createForSemester(configYear3, _42, 2 /* semester */);

                } else if (is_IE_Lic_40(degree)) {

                    final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                    if (configYear1 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.createForSemester(configYear1, _30, 1 /* semester */);
                    CreditsInEnrolmentPeriod.createForSemester(configYear1, _30, 2 /* semester */);
                    CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, 2, yearMax);

                    final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                    if (configYear2 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.createForSemester(configYear2, _45, 1 /* semester */);
                    CreditsInEnrolmentPeriod.createForSemester(configYear2, _45, 2 /* semester */);
                    CreditsInCurricularPeriod.createForYearInterval(configYear2, _0, 3, yearMax);

                    final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                    if (configYear3 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.createForSemester(configYear3, _45, 1 /* semester */);
                    CreditsInEnrolmentPeriod.createForSemester(configYear3, _45, 2 /* semester */);

                } else if (is_IE_PosGrad(degree)) {

                    final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                    if (configYear1 == null) {
                        continue;
                    }
                    CreditsInEnrolmentPeriod.create(configYear1, _60);
                }
            }
        }
    }

    static private boolean is_IE_Mest_12_atraso(final Degree degree) {
        return Lists.newArrayList("112", "113", "114", "115", "116", "117", "118", "469", "6031", "6436", "6449", "6451", "6452",
                "6453", "6457", "6459", "6460", "6461", "6959").contains(degree.getCode());
    }

    static private boolean is_IE_Mest_15_atraso(final Degree degree) {
        return Lists.newArrayList("734", "9281").contains(degree.getCode());
    }

    static private boolean is_IE_Lic_9026(final Degree degree) {
        return Lists.newArrayList("9026").contains(degree.getCode());
    }

    static private boolean is_IE_PosGrad(final Degree degree) {
        return Lists.newArrayList("3006").contains(degree.getCode());
    }

    static private boolean is_IE_Lic_40(final Degree degree) {
        return Lists.newArrayList("40").contains(degree.getCode());
    }

    static private void initRuleEnrolmentICS() {
        initRuleEnrolmentSecondYear();
    }

    static private void initRuleEnrolmentIGOT() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleEnrolment for {}", dcp.getPresentationName());

            final DegreeType degreeType = dcp.getDegree().getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear1, _60);
                CreditsInCurricularPeriod.createForYear(configYear1, _0, 2);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.create(configYear2, _72);

            } else {

                final int yearMax = Math.min(dcp.getDurationInYears(), 3);

                final CurricularPeriodConfiguration configYear1 = findOrCreateConfig(dcp, 1);
                if (configYear1 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.createForSemester(configYear1, _42, 1);
                CreditsInEnrolmentPeriod.createForSemester(configYear1, _42, 2);
                CreditsInCurricularPeriod.createForYearInterval(configYear1, _0, 2, yearMax);

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.createForSemester(configYear2, _42, 1);
                CreditsInEnrolmentPeriod.createForSemester(configYear2, _42, 2);
                CreditsInCurricularPeriod.createForYearInterval(configYear2, _0, 3, yearMax);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                CreditsInEnrolmentPeriod.createForSemester(configYear3, _42, 1);
                CreditsInEnrolmentPeriod.createForSemester(configYear3, _42, 2);
            }
        }
    }

    static private void initRuleEnrolmentFA() {
        initRuleEnrolmentSecondYear();
    }

    static private void initRuleTransitionFBA() {
        initRuleTransitionSecondYear();
    }

    static private void initRuleTransitionFCUL() {
        initRuleTransitionSecondYear();
    }

    static private void initRuleTransitionFD() {
        initRuleTransitionSecondYear();
    }

    static private void initRuleTransitionFM() {
        initRuleTransitionSecondYear();
    }

    static private void initRuleTransitionFMH() {
        initRuleTransitionSecondYear();
    }

    static private void initRuleTransitionFP() {

        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final DegreeType degreeType = dcp.getDegree().getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _12);

            } else if (degreeType.isThirdCycle()) {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _0);

            } else {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _24);
                FlunkedCredits.create(configYear2, _12).setSemester(1);
                FlunkedCredits.create(configYear2, _12).setSemester(2);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear3, _24);
                FlunkedCredits.create(configYear3, _12).setSemester(1);
                FlunkedCredits.create(configYear3, _12).setSemester(2);

                final CurricularPeriodConfiguration configYear4 = findOrCreateConfig(dcp, 4);
                if (configYear4 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear4, _24);
                FlunkedCredits.create(configYear4, _12).setSemester(1);
                FlunkedCredits.create(configYear4, _12).setSemester(2);

                final CurricularPeriodConfiguration configYear5 = findOrCreateConfig(dcp, 5);
                if (configYear5 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear5, _24);
                FlunkedCredits.create(configYear5, _12).setSemester(1);
                FlunkedCredits.create(configYear5, _12).setSemester(2);
                FlunkedCredits.createForYear(configYear5, _0, 1);
            }
        }
    }

    static private void initRuleTransitionIE() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final Degree degree = dcp.getDegree();
            if (is_IE_Mest_12_atraso(degree)) {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _12);

            } else if (is_IE_Mest_15_atraso(degree)) {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _15);

            } else if (degree.isThirdCycle()) {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _0);

            } else if (is_IE_Lic_9026(degree)) {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _24);
                FlunkedCredits.create(configYear2, _12).setSemester(1);
                FlunkedCredits.create(configYear2, _12).setSemester(2);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear3, _24);
                FlunkedCredits.create(configYear3, _12).setSemester(1);
                FlunkedCredits.create(configYear3, _12).setSemester(2);

            } else if (is_IE_Lic_40(degree)) {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                ApprovedCourses.createForSemester(configYear2, _4 /* approvals */, 1 /* semester */);
                ApprovedCourses.createForSemester(configYear2, _4 /* approvals */, 2 /* semester */);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                ApprovedCourses.createForSemester(configYear3, _8 /* approvals */, 1 /* semester */);
                ApprovedCourses.createForSemester(configYear3, _8 /* approvals */, 2 /* semester */);
            }

            for (int i = dcp.getDurationInYears(); i > 1; i--) {
                findOrCreateConfig(dcp, i);
            }
        }
    }

    static private void initRuleTransitionICS() {
        initRuleTransitionSecondYear();
    }

    static private void initRuleTransitionIGOT() {
        for (final DegreeCurricularPlan dcp : Bennu.getInstance().getDegreeCurricularPlansSet()) {
            logger.debug("Init RuleTransition for {}", dcp.getPresentationName());

            final DegreeType degreeType = dcp.getDegree().getDegreeType();
            if (degreeType.isSecondCycle() && degreeType.hasExactlyOneCycleType()) {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _12);

            } else {

                final CurricularPeriodConfiguration configYear2 = findOrCreateConfig(dcp, 2);
                if (configYear2 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear2, _24);

                final CurricularPeriodConfiguration configYear3 = findOrCreateConfig(dcp, 3);
                if (configYear3 == null) {
                    continue;
                }
                FlunkedCredits.create(configYear3, _24);
            }
        }
    }

    static private void initRuleTransitionFA() {
        initRuleTransitionSecondYear();
    }

}
