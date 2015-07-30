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

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.servlet.FenixeduUlisboaSpecificationsInitializer;

import pt.ist.fenixframework.Atomic;

abstract public class CurricularPeriodRule extends CurricularPeriodRule_Base {

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

    abstract protected DegreeCurricularPlan getDegreeCurricularPlan();

    public RuleResult createTrue() {
        return RuleResult.createTrue(getDegreeCurricularPlan().getRoot());
    }

    public RuleResult createFalse(final String key, final String... args) {
        final String literalMessage = BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE, key, args);
        return RuleResult.createFalse(getDegreeCurricularPlan().getRoot(), literalMessage);
    }

    public RuleResult createWarning(final String key, final String... args) {
        final String literalMessage = BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE, key, args);
        return RuleResult.createWarning(getDegreeCurricularPlan().getRoot(), literalMessage);
    }

    public RuleResult createNA() {
        return RuleResult.createNA(getDegreeCurricularPlan().getRoot());
    }

}
