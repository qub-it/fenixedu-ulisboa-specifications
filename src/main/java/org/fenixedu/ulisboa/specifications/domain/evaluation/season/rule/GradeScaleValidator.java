package org.fenixedu.ulisboa.specifications.domain.evaluation.season.rule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class GradeScaleValidator extends GradeScaleValidator_Base {

    public GradeScaleValidator() {
        super();
    }

    @Atomic
    static public EvaluationSeasonRule create(final EvaluationSeason season, final GradeScale gradeScale,
            final String gradeValues, final LocalizedString description, final Set<DegreeType> degreeTypes) {

        final GradeScaleValidator result = new GradeScaleValidator();
        result.init(season, gradeScale, gradeValues, description, degreeTypes);
        return result;
    }

    private void init(final EvaluationSeason season, final GradeScale gradeScale, final String gradeValues,
            final LocalizedString description, final Set<DegreeType> degreeTypes) {

        super.init(season);
        setGradeScale(gradeScale);
        setGradeValues(gradeValues);
        setRuleDescription(description);
        getDegreeTypeSet().clear();
        getDegreeTypeSet().addAll(degreeTypes);

        checkRules();
    }

    private void checkRules() {
        if (getGradeScale() == null) {
            throw new ULisboaSpecificationsDomainException("error.GradeScaleValidator.gradeScale.required");
        }

        if (StringUtils.isBlank(getGradeValues())) {
            throw new ULisboaSpecificationsDomainException("error.GradeScaleValidator.gradeValues.required");
        }

        if (getRuleDescription() == null || getRuleDescription().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.GradeScaleValidator.description.required");
        }

        if (getDegreeTypeSet().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.GradeScaleValidator.degreeTypes.required");
        }
    }

    @Override
    protected Predicate<? super EvaluationSeasonRule> checkDuplicate() {
        return i -> {

            if (i == this || !(i instanceof GradeScaleValidator)) {
                return false;
            }

            final GradeScaleValidator o = (GradeScaleValidator) i;
            return o.getGradeScale() == getGradeScale() && !Sets.intersection(o.getDegreeTypeSet(), getDegreeTypeSet()).isEmpty();
        };
    }

    @Atomic
    public void edit(final GradeScale gradeScale, final String gradeValues, final LocalizedString description,
            final Set<DegreeType> degreeTypes) {

        init(getSeason(), gradeScale, gradeValues, description, degreeTypes);
    }

    @Override
    public boolean isUpdatable() {
        return true;
    }

    @Override
    public LocalizedString getDescriptionI18N() {
        return ULisboaSpecificationsUtil.bundleI18N(getClass().getSimpleName());
    }

    public boolean isGradeValueAccepted(final String input) {
        if (getGradeScale().belongsTo(input)) {
            for (final String iter : getGradeValues().split(" ")) {

                if (iter.contains(input)) {
                    return true;
                }

                final List<String> limits = Lists.newArrayList(iter.split("-"));
                if (limits.size() == 2) {
                    final BigDecimal value = BigDecimalValidator.getInstance().validate(input.replace(".", ","));
                    final BigDecimal min = BigDecimalValidator.getInstance().validate(limits.get(0).replace(".", ","));
                    final BigDecimal max = BigDecimalValidator.getInstance().validate(limits.get(1).replace(".", ","));
                    return value != null && min != null && max != null
                            && BigDecimalValidator.getInstance().isInRange(value, min, max);
                }
            }
        }

        return false;
    }

    @Override
    @Atomic
    public void delete() {
        getDegreeTypeSet().clear();
        super.delete();
    }

}
