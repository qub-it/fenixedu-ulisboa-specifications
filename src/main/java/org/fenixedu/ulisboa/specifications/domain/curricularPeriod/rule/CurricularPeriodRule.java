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
package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule;

import java.math.BigDecimal;
import java.util.Collections;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResultMessage;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.servlet.FenixeduUlisboaSpecificationsInitializer;

import pt.ist.fenixframework.Atomic;

abstract public class CurricularPeriodRule extends CurricularPeriodRule_Base {

    static protected String MODULE_BUNDLE = FenixeduUlisboaSpecificationsInitializer.BUNDLE;

    protected CurricularPeriodRule() {
        super();
    }

    public void init(final BigDecimal credits) {
        setCredits(credits);
        checkRules();
    }

    private void checkRules() {
        //
        //CHANGE_ME add more busines validations
        //
        if (getCredits() == null) {
            throw new DomainException("error." + this.getClass().getSimpleName() + ".credits.required");
        }
    }

    @Atomic
    public void delete() {
        DomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        deleteDomainObject();
    }

    abstract protected CurricularPeriodConfiguration getConfiguration();

    /**
     * If true, an executive rule overrides all other (possibly false) rules
     */
    public boolean isExecutive() {
        return false;
    }

    abstract protected DegreeCurricularPlan getDegreeCurricularPlan();

    public abstract String getLabel();

    protected DegreeModule getDegreeModule() {
        return getDegreeCurricularPlan().getRoot();
    }

    public RuleResult createTrue() {
        return RuleResult.createTrue(getDegreeModule());
    }

    public RuleResult createFalseConfiguration() {
        return createFalseConfiguration(getDegreeModule(), getMessagesPrefix());
    }

    static public RuleResult createFalseConfiguration(final DegreeModule degreeModule,
            final CurricularPeriodConfiguration configuration) {
        return createFalseConfiguration(degreeModule, getMessagesPrefix(configuration));
    }

    static private RuleResult createFalseConfiguration(final DegreeModule degreeModule, final String prefix) {
        final String literalMessage =
                prefix
                        + BundleUtil.getString(Bundle.APPLICATION, "curricularRules.ruleExecutors.logic.unavailable",
                                BundleUtil.getString(Bundle.BOLONHA, "label.enrolmentPeriodRestrictions"));
        return RuleResult.createFalseWithLiteralMessage(degreeModule, literalMessage);
    }

    public RuleResult createFalseLabelled(final BigDecimal suffix) {
        return createFalseLabelled(getMessagesSuffix(suffix));
    }

    public RuleResult createFalseLabelled(final String suffix) {
        final String literalMessage = getMessagesPrefix() + getLabel() + suffix;
        return RuleResult.createFalseWithLiteralMessage(getDegreeModule(), literalMessage);
    }

    public RuleResult createWarningLabelled(final BigDecimal suffix) {
        final String literalMessage = getMessagesPrefix() + getLabel() + getMessagesSuffix(suffix);
        return RuleResult.createWarning(getDegreeModule(), Collections.singleton(new RuleResultMessage(literalMessage, false)));
    }

    public RuleResult createNA() {
        return RuleResult.createNA(getDegreeModule());
    }

    private String getMessagesPrefix() {
        return getMessagesPrefix(getConfiguration());
    }

    static private String getMessagesPrefix(final CurricularPeriodConfiguration configuration) {
        return configuration == null ? "" : (BundleUtil.getString(MODULE_BUNDLE, "label.CurricularPeriodRule.prefix",
                configuration.getCurricularPeriod().getFullLabel()) + " ");
    }

    static private String getMessagesSuffix(final BigDecimal total) {
        return total == null ? "" : (" " + BundleUtil.getString(MODULE_BUNDLE, "label.CurricularPeriodRule.suffix",
                total.toString()));
    }

}
