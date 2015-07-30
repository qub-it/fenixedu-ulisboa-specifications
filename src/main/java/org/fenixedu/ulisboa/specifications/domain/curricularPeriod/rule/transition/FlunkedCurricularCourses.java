package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Sets;

public class FlunkedCurricularCourses extends FlunkedCurricularCourses_Base {

    static private final Logger logger = LoggerFactory.getLogger(FlunkedCurricularCourses.class);

    private Set<CompetenceCourse> competenceCourses;

    protected FlunkedCurricularCourses() {
        super();
    }

    @Atomic
    static public FlunkedCurricularCourses create(final CurricularPeriodConfiguration configuration, final BigDecimal credits,
            final Integer year, final String codesCSV) {

        final FlunkedCurricularCourses result = new FlunkedCurricularCourses();
        result.init(configuration, credits, year, codesCSV);
        return result;
    }

    private void init(final CurricularPeriodConfiguration configuration, final BigDecimal credits, final Integer year,
            final String codesCSV) {
        super.init(configuration, credits, year, year);
        setCodesCSV(codesCSV);
        checkRules();
    }

    private void checkRules() {
        getCompetenceCourses();
    }

    private Set<CompetenceCourse> getCompetenceCourses() {
        if (this.competenceCourses == null) {

            final Set<CompetenceCourse> result = Sets.newHashSet();

            if (StringUtils.isNotBlank(getCodesCSV())) {

                for (final String code : getCodesCSV().split(",")) {

                    final CompetenceCourse competenceCourse = CompetenceCourse.find(code);
                    if (competenceCourse == null) {
                        logger.warn("[{}], for DCP [{}]: unable to find Competence Course [{}]", this, getDegreeCurricularPlan()
                                .getPresentationName(), code);
                    }
                }
            }

            this.competenceCourses = result;
        }

        return this.competenceCourses;
    }

    @Override
    public RuleResult execute(final Curriculum curriculum) {
        // TODO legidio
        return RuleResult.createInitialFalse();
    }

}
