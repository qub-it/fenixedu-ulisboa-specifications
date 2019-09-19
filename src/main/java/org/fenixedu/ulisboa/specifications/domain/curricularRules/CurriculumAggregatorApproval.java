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
package org.fenixedu.ulisboa.specifications.domain.curricularRules;

import java.util.List;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curricularRules.executors.verifyExecutors.VerifyRuleExecutor;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.dto.GenericPair;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.executors.ruleExecutors.CurriculumAggregatorApprovalExecutor;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.executors.verifyExecutors.CurriculumAggregatorApprovalVerifier;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Lists;

public class CurriculumAggregatorApproval extends CurriculumAggregatorApproval_Base {

    protected CurriculumAggregatorApproval() {
        super();
    }

    public CurriculumAggregatorApproval(final DegreeModule toApplyRule, final CourseGroup contextCourseGroup,
            final ExecutionInterval begin, final ExecutionInterval end) {

        this();
        init(toApplyRule, contextCourseGroup, begin, end, CurricularRuleType.CUSTOM);
    }

    public void edit(CourseGroup contextCourseGroup) {
        setContextCourseGroup(contextCourseGroup);
    }

    @Override
    public RuleResult evaluate(IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, EnrolmentContext enrolmentContext) {
        return new CurriculumAggregatorApprovalExecutor().execute(this, sourceDegreeModuleToEvaluate, enrolmentContext);
    }

    @Override
    public VerifyRuleExecutor createVerifyRuleExecutor() {
        return new CurriculumAggregatorApprovalVerifier();
    }

    @Override
    protected void removeOwnParameters() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GenericPair<Object, Boolean>> getLabel() {
        final StringBuilder label = new StringBuilder();
        label.append(BundleUtil.getString(ULisboaConstants.BUNDLE, "label.CurriculumAggregatorApproval"));

        return Lists.newArrayList(new GenericPair<Object, Boolean>(label, false));
    }

    @Override
    public boolean isVisible() {
        return false;
    }

}
