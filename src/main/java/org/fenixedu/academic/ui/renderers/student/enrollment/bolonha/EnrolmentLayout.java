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
package org.fenixedu.academic.ui.renderers.student.enrollment.bolonha;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.CreditsLimit;
import org.fenixedu.academic.domain.curricularRules.CurricularRule;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleValidationType;
import org.fenixedu.academic.domain.curricularRules.EnrolmentToBeApprovedByCoordinator;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.DegreeModuleToEnrol;
import org.fenixedu.academic.domain.enrolment.EnroledCurriculumModuleWrapper;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CycleCurriculumGroup;
import org.fenixedu.academic.dto.student.enrollment.bolonha.StudentCurriculumGroupBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.CompetenceCourseServices;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.ConditionedRoute;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.CurricularRuleServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumLineServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumModuleServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorEntry;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.StudentCurricularPlanLayout;

import com.google.common.collect.Maps;

import pt.ist.fenixWebFramework.renderers.components.HtmlActionLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBox;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.model.MetaObjectFactory;
import pt.ist.fenixWebFramework.renderers.schemas.Schema;

public class EnrolmentLayout extends BolonhaStudentEnrolmentLayout {

    static private String i18n(final String bundle, final String key, final String... args) {
        return BundleUtil.getString(bundle, key, args);
    }

    // qubExtension, don't SHOW empty groups
    private Map<CurriculumGroup, Boolean> emptyGroups = Maps.newHashMap();
    private boolean emptyGroupsCollapsible = true;

    private StudentCurricularPlan getStudentCurricularPlan() {
        return getBolonhaStudentEnrollmentBean().getStudentCurricularPlan();
    }

    private ExecutionSemester getExecutionSemester() {
        return getBolonhaStudentEnrollmentBean().getExecutionPeriod();
    }

    private ExecutionYear getExecutionYear() {
        return getExecutionSemester().getExecutionYear();
    }

    // qubExtension
    static private ExecutionInterval getExecutionInterval(final StudentCurricularPlan input, final ExecutionSemester semester) {
        return isToEvaluateRulesByYear(input) ? semester.getExecutionYear() : semester;
    }

    // qubExtension
    static private boolean isToEvaluateRulesByYear(final StudentCurricularPlan input) {
        return input.getDegreeCurricularPlan().getCurricularRuleValidationType() == CurricularRuleValidationType.YEAR;
    }

    @Override
    protected void generateGroup(final HtmlBlockContainer blockContainer, final StudentCurricularPlan studentCurricularPlan,
            final StudentCurriculumGroupBean studentCurriculumGroupBean, final ExecutionSemester executionSemester,
            final int depth) {

        if (isCycleExternal(studentCurriculumGroupBean)) {

            if (!getRenderer().isAllowedToEnrolInAffinityCycle()) {
                return;
            }

        }

        final HtmlTable groupTable = createGroupTable(blockContainer, depth);
        addGroupHeaderRow(groupTable, studentCurriculumGroupBean, executionSemester);

        // init
        setEmptyGroup(studentCurriculumGroupBean, !studentCurriculumGroupBean.getCurriculumModule().isBranchCurriculumGroup());

        // qubExtension, don't GENERATE concluded groups
        if (canPerformStudentEnrolments
                || !isConcluded(studentCurriculumGroupBean.getCurriculumModule(), executionSemester.getExecutionYear())) {

            if (getRenderer().isEncodeGroupRules()) {
                encodeCurricularRules(groupTable, studentCurriculumGroupBean.getCurriculumModule());
            }

            final HtmlTable coursesTable = createCoursesTable(blockContainer, depth);
            generateEnrolments(studentCurriculumGroupBean, coursesTable);
            generateCurricularCoursesToEnrol(coursesTable, studentCurriculumGroupBean, executionSemester);

            generateGroups(blockContainer, studentCurriculumGroupBean, studentCurricularPlan, executionSemester, depth);
        }

        // qubExtension, don't SHOW empty groups
        if (isEmptyGroup(studentCurriculumGroupBean)) {
            String keepExistingStyle = groupTable.getStyle();
            groupTable.setStyle(keepExistingStyle + " display: none");
            String keepExistingClasses = groupTable.getClasses();
            groupTable.setClasses(keepExistingClasses + " emptyGroup");
        }

        if (studentCurriculumGroupBean.isRoot()) {
            generateCycleCourseGroupsToEnrol(blockContainer, executionSemester, studentCurricularPlan, depth);
        }

    }

    private void setEmptyGroup(final StudentCurriculumGroupBean bean, final boolean value) {
        final CurriculumGroup key = bean.getCurriculumModule();

        if (!emptyGroups.containsKey(key)) {
            // init
            emptyGroups.put(key, emptyGroupsCollapsible);

        } else {

            // must check already previously calculated value 
            emptyGroups.put(key, isEmptyGroup(bean) && value);
        }
    }

    // qubExtension, don't SHOW empty groups
    private Boolean isEmptyGroup(final StudentCurriculumGroupBean bean) {
        final CurriculumGroup group = bean.getCurriculumModule();

        if (group.isRoot() || group.getCurriculumGroup().isRoot() || group.isBranchCurriculumGroup()) {
            return false;
        }

        final Boolean value = emptyGroups.get(group);
        return value != null && value;
    }

    // copy from super class
    static private boolean isCycleExternal(final StudentCurriculumGroupBean studentCurriculumGroupBean) {
        final CurriculumGroup curriculumModule = studentCurriculumGroupBean.getCurriculumModule();
        return curriculumModule.isCycleCurriculumGroup() && ((CycleCurriculumGroup) curriculumModule).isExternal();
    }

    @Override
    protected void addGroupHeaderRow(final HtmlTable groupTable, final StudentCurriculumGroupBean studentCurriculumGroupBean,
            final ExecutionSemester executionSemester) {

        final HtmlTableRow groupHeaderRow = groupTable.createRow();
        groupHeaderRow.setClasses(getRenderer().getGroupRowClasses());

        final HtmlTableCell titleCell = groupHeaderRow.createCell();
        if (studentCurriculumGroupBean.getCurriculumModule().isRoot()) {
            if (studentCurriculumGroupBean.getCurriculumModule().getDegreeCurricularPlanOfStudent().isEmpty()) {
                titleCell.setBody(new HtmlText(studentCurriculumGroupBean.getCurriculumModule().getName().getContent()));
            } else {
                titleCell.setBody(createDegreeCurricularPlanLink(studentCurriculumGroupBean));
            }
        } else if (studentCurriculumGroupBean.getCurriculumModule().isCycleCurriculumGroup()) {
            setTitleCellInformation(groupHeaderRow, titleCell, studentCurriculumGroupBean, executionSemester);

        } else {
            titleCell.setBody(new HtmlText(
                    buildCurriculumGroupLabel(studentCurriculumGroupBean.getCurriculumModule(), executionSemester), false));
        }

        final HtmlTableCell checkBoxCell = groupHeaderRow.createCell();
        checkBoxCell.setClasses("aright");

        final HtmlCheckBox checkBox = new HtmlCheckBox(true) {
            @Override
            public void setChecked(boolean checked) {
                if (isDisabled()) {
                    super.setChecked(true);
                } else {
                    super.setChecked(checked);
                }
            }
        };

        MetaObject enrolmentMetaObject = MetaObjectFactory.createObject(studentCurriculumGroupBean.getCurriculumModule(),
                new Schema(CurriculumGroup.class));
        checkBox.setName("enrolmentCheckBox" + studentCurriculumGroupBean.getCurriculumModule().getExternalId());
        checkBox.setUserValue(enrolmentMetaObject.getKey().toString());
        checkBoxCell.setBody(checkBox);

        if (isToDisableEnrolmentOption(studentCurriculumGroupBean)) {
            checkBox.setDisabled(true);
        } else {
            getEnrollmentsController().addCheckBox(checkBox);
        }
    }

    // qubExtension, more credits info
    @Override
    protected String buildCurriculumGroupLabel(final CurriculumGroup curriculumGroup, final ExecutionSemester executionSemester) {
        if (curriculumGroup.isNoCourseGroupCurriculumGroup()) {
            return curriculumGroup.getName().getContent();
        }

        final StringBuilder result = new StringBuilder();
        result.append("<span class=\"bold\">").append(curriculumGroup.getName().getContent()).append("</span>");
        result.append(" [");

        if (getRenderer().isEncodeGroupRules()) {
            addCreditsConcluded(curriculumGroup, executionSemester, result);
            addEnroledEcts(curriculumGroup, executionSemester, result);
            addSumEcts(curriculumGroup, executionSemester, result);
        } else {
            final CreditsLimit creditsLimit = (CreditsLimit) curriculumGroup
                    .getMostRecentActiveCurricularRule(CurricularRuleType.CREDITS_LIMIT, executionSemester);

            if (creditsLimit != null) {
                result.append(" <span title=\"");
                result.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.minCredits"));
                result.append(" \">m(");
                result.append(creditsLimit.getMinimumCredits());
                result.append(")</span>, ");
            }

            addCreditsConcluded(curriculumGroup, executionSemester, result);
            addEnroledEcts(curriculumGroup, executionSemester, result);
            addSumEcts(curriculumGroup, executionSemester, result);
            if (creditsLimit != null) {
                result.append(", <span title=\"");
                result.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.maxCredits"));
                result.append(" \">M(");
                result.append(creditsLimit.getMaximumCredits());
                result.append(")</span>");
            }
        }
        result.append(" ]");
        if (isConcluded(curriculumGroup, executionSemester.getExecutionYear())) {
            result.append(" - <span class=\"curriculumGroupConcluded\">")
                    .append(i18n(Bundle.APPLICATION, "label.curriculumGroup.concluded")).append("</span>");
        } else if (!isStudentLogged(curriculumGroup.getStudentCurricularPlan())
                && hasMinimumCredits(curriculumGroup, executionSemester)) {
            result.append(" - <span class=\"minimumCreditsConcludedInCurriculumGroup\">")
                    .append(i18n(Bundle.APPLICATION, "label.curriculumGroup.minimumCreditsConcluded")).append("</span>");
        }

        if (AcademicAccessRule.isProgramAccessibleToFunction(AcademicOperationType.ENROLMENT_WITHOUT_RULES,
                curriculumGroup.getStudentCurricularPlan().getDegree(), Authenticate.getUser())) {
            addCreditsDistributionMessage(curriculumGroup, executionSemester, result);
        }

        return result.toString();
    }

    // qubExtension, more credits info
    static public void addCreditsDistributionMessage(final CurriculumGroup group, final ExecutionSemester semester,
            final StringBuilder result) {

        final Double approved = group.getAprovedEctsCredits();
        final Double concluded = CurriculumModuleServices.getCreditsConcluded(group, semester.getExecutionYear()).doubleValue();

        if (approved.doubleValue() != concluded.doubleValue()) {
            result.append(" <span class=\"wrongCreditsDistributionError\" title=\"");
            result.append(i18n(Bundle.APPLICATION, "label.curriculumGroup.wrongCreditsDistribution", approved.toString(),
                    concluded.toString()));
            result.append(" \"> ! </span>");
        }
    }

    // qubExtension, more credits info
    static public boolean hasMinimumCredits(final CurriculumGroup group, final ExecutionSemester semester) {
        final CreditsLimit creditsRule =
                (CreditsLimit) group.getMostRecentActiveCurricularRule(CurricularRuleType.CREDITS_LIMIT, semester);
        return creditsRule != null && creditsRule.getMinimumCredits().doubleValue() > 0d
                && CurriculumModuleServices
                        .getCreditsConcluded(group, getExecutionInterval(group.getStudentCurricularPlan(), semester))
                        .doubleValue() >= creditsRule.getMinimumCredits().doubleValue();
    }

    // qubExtension, more credits info
    static public void addCreditsConcluded(final CurriculumGroup group, final ExecutionSemester semester,
            final StringBuilder result) {
        final ExecutionInterval interval = getExecutionInterval(group.getStudentCurricularPlan(), semester);

        result.append(" <span title=\"");
        result.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.creditsConcluded", interval.getQualifiedName()));
        result.append(" \"> " + i18n(Bundle.APPLICATION, "label.curriculum.credits.concludedCredits") + " (");
        result.append(CurriculumModuleServices.getCreditsConcluded(group, interval));
        result.append(")</span>");
    }

    // qubExtension, more credits info
    static public void addEnroledEcts(final CurriculumGroup group, final ExecutionSemester semester, final StringBuilder result) {
        final ExecutionInterval interval = getExecutionInterval(group.getStudentCurricularPlan(), semester);

        result.append(", <span title=\"");
        result.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.enroledCredits", interval.getQualifiedName()));
        result.append(" \"> " + i18n(Bundle.APPLICATION, "label.curriculum.credits.enroledCredits") + " (");
        result.append(CurriculumModuleServices.getEnroledAndNotApprovedEctsCreditsFor(group, interval).toPlainString());
        result.append(")</span>");
    }

    // qubExtension, more credits info
    static public void addSumEcts(final CurriculumGroup group, final ExecutionSemester semester, final StringBuilder result) {
        final ExecutionInterval interval = getExecutionInterval(group.getStudentCurricularPlan(), semester);

        final BigDecimal total = CurriculumModuleServices.getCreditsConcluded(group, interval)
                .add(CurriculumModuleServices.getEnroledAndNotApprovedEctsCreditsFor(group, interval));
        result.append(", <span title=\"");
        result.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.totalCredits", interval.getQualifiedName()));
        result.append(" \"> " + i18n(Bundle.APPLICATION, "label.curriculum.credits.totalCredits") + " (");
        result.append(total);
        result.append(")</span>");
    }

    // qubExtension, more credits info
    private boolean isConcluded(final CurriculumGroup group, final ExecutionYear year) {
        final CreditsLimit creditsRule =
                (CreditsLimit) group.getMostRecentActiveCurricularRule(CurricularRuleType.CREDITS_LIMIT, year);

        return StudentCurricularPlanLayout.isConcluded(group, year, creditsRule).value();
    }

    @Override
    protected void generateCurricularCoursesToEnrol(final HtmlTable groupTable,
            final StudentCurriculumGroupBean studentCurriculumGroupBean, final ExecutionSemester executionSemester) {

        final List<IDegreeModuleToEvaluate> coursesToEvaluate = filterCurricularCoursesToEvaluate(studentCurriculumGroupBean);

        // qubExtension, don't SHOW empty groups
        setEmptyGroup(studentCurriculumGroupBean, coursesToEvaluate.isEmpty());

        for (final IDegreeModuleToEvaluate degreeModuleToEvaluate : coursesToEvaluate) {

            HtmlTableRow htmlTableRow = groupTable.createRow();
            HtmlTableCell cellName = htmlTableRow.createCell();
            cellName.setClasses(getRenderer().getCurricularCourseToEnrolNameClasses());

            String degreeModuleName = degreeModuleToEvaluate.getDegreeModule().getNameI18N(executionSemester).getContent();

            if (degreeModuleToEvaluate.getDegreeModule().isLeaf() && !degreeModuleToEvaluate.isOptionalCurricularCourse()) {

                if (!StringUtils.isEmpty(degreeModuleToEvaluate.getDegreeModule().getCode())) {
                    degreeModuleName = degreeModuleToEvaluate.getDegreeModule().getCode() + " - " + degreeModuleName;
                }

                if (canPerformStudentEnrolments) {
                    final CurricularCourse curricularCourse = (CurricularCourse) degreeModuleToEvaluate.getDegreeModule();
                    final GradeScale gradeScaleChain = curricularCourse.getGradeScaleChain();
                    if (gradeScaleChain != GradeScale.TYPE20) {
                        degreeModuleName +=
                                " (" + i18n(Bundle.STUDENT, "label.grade.scale") + " - " + gradeScaleChain.getDescription() + ")";
                    }
                }
            }

            cellName.setBody(new HtmlText(degreeModuleName));

            // qubExtension, Aggregation Info
            final HtmlTableCell aggregationCell = htmlTableRow.createCell();
            aggregationCell.setBody(
                    generateAggregationInfo(degreeModuleToEvaluate.getContext(), getStudentCurricularPlan(), executionSemester));

            // qubExtension, Curricular Period
            final HtmlTableCell curricularPeriodCell = htmlTableRow.createCell();
            curricularPeriodCell.setClasses(getRenderer().getCurricularCourseToEnrolYearClasses());
            curricularPeriodCell.setColspan(1);
            curricularPeriodCell.setBody(new HtmlText(getCurricularPeriodLabel(degreeModuleToEvaluate)));

            if (!degreeModuleToEvaluate.isOptionalCurricularCourse()) {
                // Ects
                final HtmlTableCell ectsCell = htmlTableRow.createCell();
                ectsCell.setClasses(getRenderer().getCurricularCourseToEnrolEctsClasses());

                final StringBuilder ects = new StringBuilder();
                ects.append(degreeModuleToEvaluate.getEctsCredits()).append(" ")
                        .append(i18n(Bundle.STUDENT, "label.credits.abbreviation"));
                ectsCell.setBody(new HtmlText(ects.toString()));

                HtmlTableCell checkBoxCell = htmlTableRow.createCell();
                checkBoxCell.setClasses(getRenderer().getCurricularCourseToEnrolCheckBoxClasses());

                HtmlCheckBox checkBox = new HtmlCheckBox(false);
                checkBox.setName("degreeModuleToEnrolCheckBox" + degreeModuleToEvaluate.getKey());
                checkBox.setUserValue(degreeModuleToEvaluate.getKey());
                checkBox.setVisible(!isToDisableEnrolmentOption(degreeModuleToEvaluate));
                getDegreeModulesToEvaluateController().addCheckBox(checkBox);
                checkBoxCell.setBody(checkBox);
            } else {
                final HtmlTableCell cell = htmlTableRow.createCell();
                cell.setClasses(getRenderer().getCurricularCourseToEnrolEctsClasses());
                cell.setBody(new HtmlText(""));

                HtmlTableCell linkTableCell = htmlTableRow.createCell();
                linkTableCell.setClasses(getRenderer().getCurricularCourseToEnrolCheckBoxClasses());

                final HtmlActionLink actionLink = new HtmlActionLink();
                actionLink.setText(i18n(Bundle.STUDENT, "label.chooseOptionalCurricularCourse"));
                actionLink.setController(new OptionalCurricularCourseLinkController(degreeModuleToEvaluate));
                if (isToDisableEnrolmentOption(degreeModuleToEvaluate)) {
                    actionLink.setOnClick("function(){return false;}");
                    actionLink.setStyle("text-decoration: line-through; color: grey; border-bottom: none;");
                } else {
                    actionLink.setOnClick(
                            "$(this).closest('form').find('input[name=\\'method\\']').attr('value', 'prepareChooseOptionalCurricularCourseToEnrol');");
                }
                //actionLink.setOnClick("document.forms[2].method.value='prepareChooseOptionalCurricularCourseToEnrol';");
                actionLink.setName("optionalCurricularCourseLink" + degreeModuleToEvaluate.getCurriculumGroup().getExternalId()
                        + "_" + degreeModuleToEvaluate.getContext().getExternalId());
                linkTableCell.setBody(actionLink);
            }

            if (getRenderer().isEncodeCurricularRules()) {
                encodeCurricularRules(groupTable, degreeModuleToEvaluate);
            }
        }
    }

    // qubExtension, Curricular Period
    private String getCurricularPeriodLabel(final IDegreeModuleToEvaluate input) {
        if (input.getClass() == DegreeModuleToEnrol.class) {

            final Context context = input.getContext();
            final CurricularPeriod curricularPeriod;

            if (input.isAnnualCurricularCourse(getExecutionYear())) {
                curricularPeriod = context.getCurricularPeriod().getParent();
            } else {
                curricularPeriod = context.getCurricularPeriod();
            }

            return curricularPeriod.getFullLabel();
        }

        return input.getYearFullLabel();
    }

    /**
     * qubExtension, filter CurricularCourses with CompetenceCourse approved
     * Ideally this should be done on a StudentCurriculumGroupBean.buildCurricularCoursesToEnrol level, but we're limited by the
     * usual trunk constrains
     */
    private List<IDegreeModuleToEvaluate> filterCurricularCoursesToEvaluate(final StudentCurriculumGroupBean bean) {

        final List<IDegreeModuleToEvaluate> result = bean.getSortedDegreeModulesToEvaluate();

        if (isToFilterCurricularCoursesToEvaluate()) {

            for (final Iterator<IDegreeModuleToEvaluate> iterator = result.iterator(); iterator.hasNext();) {
                final DegreeModule degreeModule = iterator.next().getDegreeModule();
                if (degreeModule.isLeaf()) {
                    final CurricularCourse curricularCourse = (CurricularCourse) degreeModule;

                    if (filterByCompetenceCourse(curricularCourse) || filterByAggregationApproval(curricularCourse)) {
                        iterator.remove();
                    }
                }
            }
        }

        return result;
    }

    private boolean filterByCompetenceCourse(final CurricularCourse curricularCourse) {
        return ULisboaConfiguration.getConfiguration().getCurricularRulesApprovalsAwareOfCompetenceCourse()
                && CompetenceCourseServices.isCompetenceCourseApproved(getStudentCurricularPlan(), curricularCourse,
                        (ExecutionSemester) null);
    }

    private boolean filterByAggregationApproval(final CurricularCourse input) {
        final ExecutionSemester semester = getExecutionSemester();
        if (!CurriculumAggregatorServices.isAggregationsActive(semester.getExecutionYear())) {
            return false;
        }

        final StudentCurricularPlan scp = getStudentCurricularPlan();
        final Context context = CurriculumAggregatorServices.getContext(input, semester);
        final CurriculumAggregator aggregator =
                CurriculumAggregatorServices.getAggregationRoot(context, semester.getExecutionYear());

        return aggregator != null && aggregator.isAggregationConcluded(scp);
    }

    /**
     * Necessary, we don't want this behaviour on subclasses of this layout
     */
    protected boolean isToFilterCurricularCoursesToEvaluate() {
        return true;
    }

    private boolean isToDisableEnrolmentOption(final StudentCurriculumGroupBean input) {
        return input.isToBeDisabled()

                // qubExtension
                || (isStudentLogged() &&

                        (appliesAnyRules(input.getCurriculumModule(), true /* recursive */,
                                EnrolmentToBeApprovedByCoordinator.class)

                                || appliesAnyRules(input.getCurriculumModule(), false /* recursive */, ConditionedRoute.class)))

                // qubExtension
                || input.getCurriculumModule().getDegreeModule().getChildContextsSet().stream()
                        .anyMatch(i -> CurriculumAggregatorServices.getAggregationRoot(i, getExecutionYear()) != null)

        ;
    }

    private boolean isToDisableEnrolmentOption(final IDegreeModuleToEvaluate input) {
        if (isStudentLogged()) {

            if (appliesAnyRules(input, EnrolmentToBeApprovedByCoordinator.class)) {
                return true;
            }
        }

        // qubExtension
        if (isToDisableEnrolmentOptionBasedOnCurriculumAggregator()) {
            final Context context = input.getContext();
            if (CurriculumAggregatorServices.isToDisableEnrolmentOption(context, getExecutionYear())
                    // optional entries must be manually enroled
                    && !CurriculumAggregatorServices.isOptionalEntryRelated(context, getExecutionYear())) {
                return true;
            }
        }

        return false;
    }

    protected boolean isToDisableEnrolmentOptionBasedOnCurriculumAggregator() {
        return true;
    }

    @SafeVarargs
    final private boolean appliesAnyRules(final IDegreeModuleToEvaluate input,
            final Class<? extends CurricularRule>... curricularRuleClasses) {

        if (input != null && input.getContext() != null && curricularRuleClasses != null) {

            for (final Class<? extends CurricularRule> curricularRuleClass : curricularRuleClasses) {

                final CourseGroup parentCourseGroup = input.getCurriculumGroup().getDegreeModule();

                // check self rules
                final ExecutionSemester executionInterval = getExecutionSemester();
                List<? extends ICurricularRule> rules = CurricularRuleServices.getCurricularRules(input.getDegreeModule(),
                        parentCourseGroup, curricularRuleClass, executionInterval);

                if (!rules.isEmpty() && rules.iterator().next().appliesToContext(input.getContext())) {
                    return true;
                }

                // check parent group rules
                rules = CurricularRuleServices.getCurricularRules(parentCourseGroup, curricularRuleClass, executionInterval);
                if (!rules.isEmpty() && rules.iterator().next().appliesToContext(input.getContext())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean appliesAnyRules(final CurriculumGroup input, final boolean recursive,
            final Class<? extends CurricularRule>... curricularRuleClasses) {

        if (input != null && curricularRuleClasses != null) {

            for (final Class<? extends CurricularRule> curricularRuleClass : curricularRuleClasses) {

                final CurriculumGroup parentCurriculumGroup = input.getCurriculumGroup();
                final CourseGroup parentCourseGroup = parentCurriculumGroup.getDegreeModule();

                // check self rules
                final ExecutionSemester executionInterval = getExecutionSemester();
                List<? extends ICurricularRule> rules = CurricularRuleServices.getCurricularRules(input.getDegreeModule(),
                        parentCourseGroup, curricularRuleClass, executionInterval);

                if (!rules.isEmpty()) {
                    return true;
                }

                // recursion is configured by rule class
                if (recursive) {

                    // check parent group rules, recursively until root
                    if (!parentCurriculumGroup.isRoot()
                            && appliesAnyRules(parentCurriculumGroup, recursive, curricularRuleClasses)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    protected void encodeCurricularRules(final HtmlTable groupTable, final List<CurricularRule> curricularRules) {
        // qubExtension
        for (final Iterator<CurricularRule> iterator = curricularRules.iterator(); iterator.hasNext();) {
            final CurricularRule iter = iterator.next();
            if (!iter.isVisible()) {
                iterator.remove();
            }
        }

        if (!curricularRules.isEmpty()) {
            super.encodeCurricularRules(groupTable, curricularRules);
        }
    }

    @Override
    protected void generateEnrolments(final StudentCurriculumGroupBean studentCurriculumGroupBean, final HtmlTable groupTable) {

        // qubExtension, don't SHOW empty groups
        setEmptyGroup(studentCurriculumGroupBean, studentCurriculumGroupBean.getEnrolledCurriculumCourses().isEmpty());

        // qubExtension, show sorted enrolments
        studentCurriculumGroupBean.getEnrolledCurriculumCourses().sort((o1, o2) -> {
            return CurriculumLineServices.COMPARATOR.compare(o1.getCurriculumModule(), o2.getCurriculumModule());
        });

        super.generateEnrolments(studentCurriculumGroupBean, groupTable);
    }

    @Override
    protected void generateEnrolment(final HtmlTable groupTable, Enrolment enrolment, final String enrolmentNameClasses,
            final String enrolmentYearClasses, final String enrolmentSemesterClasses, final String enrolmentEctsClasses,
            final String enrolmentCheckBoxClasses) {

        final EnroledCurriculumModuleWrapper wrapper =
                new EnroledCurriculumModuleWrapper(enrolment, enrolment.getExecutionPeriod());

        HtmlTableRow htmlTableRow = groupTable.createRow();
        HtmlTableCell cellName = htmlTableRow.createCell();
        cellName.setClasses(enrolmentNameClasses);

        final String enrolmentName = getPresentationNameFor(enrolment);
        cellName.setBody(new HtmlText(enrolmentName));

        // qubExtension, Aggregation Info
        final HtmlTableCell aggregationCell = htmlTableRow.createCell();
        aggregationCell.setClasses(" se_enrolled ");
        aggregationCell.setBody(generateAggregationInfo(enrolment));

        // qubExtension, Curricular Period
        final HtmlTableCell curricularPeriodCell = htmlTableRow.createCell();
        curricularPeriodCell.setClasses(enrolmentYearClasses);

        final String curricularPeriod = StudentCurricularPlanLayout.getCurricularPeriodLabel(enrolment);
        curricularPeriodCell.setBody(new HtmlText(curricularPeriod));

        // Ects
        final HtmlTableCell ectsCell = htmlTableRow.createCell();
        ectsCell.setClasses(enrolmentEctsClasses);

        final StringBuilder ects = new StringBuilder();
        // qubExtensions, don't show accumulated ECTS for the given semester, show all
        final double ectsCredits = enrolment.getEctsCredits();
        ects.append(ectsCredits).append(" ").append(i18n(Bundle.STUDENT, "label.credits.abbreviation"));

        ectsCell.setBody(new HtmlText(ects.toString()));

        MetaObject enrolmentMetaObject = MetaObjectFactory.createObject(enrolment, new Schema(Enrolment.class));

        HtmlCheckBox checkBox = new HtmlCheckBox(true);
        checkBox.setName("enrolmentCheckBox" + enrolment.getExternalId());
        checkBox.setUserValue(enrolmentMetaObject.getKey().toString());

        // qubExtension, add checkbox if not disabled
        if (isToDisableEnrolmentOption(wrapper)) {
            checkBox.setDisabled(true);
        } else {
            getEnrollmentsController().addCheckBox(checkBox);
        }

        HtmlTableCell cellCheckBox = htmlTableRow.createCell();
        cellCheckBox.setClasses(enrolmentCheckBoxClasses);
        cellCheckBox.setBody(checkBox);
    }

    @Override
    protected void generateGroups(final HtmlBlockContainer container, final StudentCurriculumGroupBean bean,
            final StudentCurricularPlan plan, final ExecutionSemester executionSemester, final int depth) {

        // first enroled
        final List<StudentCurriculumGroupBean> enroledGroups = bean.getEnrolledCurriculumGroupsSortedByOrder(executionSemester);
        for (final StudentCurriculumGroupBean iter : enroledGroups) {
            generateGroup(container, plan, iter, executionSemester, depth + getRenderer().getWidthDecreasePerLevel());
        }

        // then available to enrol
        final List<IDegreeModuleToEvaluate> availableToEnrol = bean.getCourseGroupsToEnrolSortedByContext();
        for (final IDegreeModuleToEvaluate iter : availableToEnrol) {
            generateCourseGroupToEnroll(container, iter, plan, depth + getRenderer().getWidthDecreasePerLevel());
        }

        // qubExtension, don't SHOW empty groups
        // notice this must be tested after group generation
        setEmptyGroup(bean, enroledGroups.isEmpty() || enroledGroups.stream().allMatch(i -> isEmptyGroup(i)));
        setEmptyGroup(bean, availableToEnrol.isEmpty());
    }

    private boolean isStudentLogged() {
        return isStudentLogged(getStudentCurricularPlan());
    }

    static public boolean isStudentLogged(final StudentCurricularPlan studentCurricularPlan) {
        return Authenticate.getUser().getPerson() == studentCurricularPlan.getPerson();
    }

    static public HtmlInlineContainer generateAggregationInfo(final CurriculumLine line) {
        final CurriculumAggregator root = CurriculumAggregatorServices.getAggregationRoot(line);
        if (root != null) {

            final StudentCurricularPlan plan = line.getStudentCurricularPlan();
            final ExecutionSemester semester = line.getExecutionPeriod();
            return generateAggregationInfo(CurriculumAggregatorServices.getContext(line), plan, semester);
        }

        return new HtmlInlineContainer();
    }

    /**
     * qubExtension, Aggregation Info
     */
    static public HtmlInlineContainer generateAggregationInfo(final Context context, final StudentCurricularPlan scp,
            final ExecutionSemester semester) {

        final HtmlInlineContainer result = new HtmlInlineContainer();
        if (context != null) {

            final ExecutionYear year = semester.getExecutionYear();

            final CurriculumAggregatorEntry entry = CurriculumAggregatorServices.getAggregatorEntry(context, year);
            if (entry != null) {
                final CurriculumAggregator aggregator = entry.getAggregator();
                if (aggregator.isLegacy()) {
                    return result;
                }

                final HtmlInlineContainer span = new HtmlInlineContainer();
                span.addChild(new HtmlText(entry.getDescriptionFull()));
                span.setClasses(getAggregatorEntryStyle(entry, scp, semester));
                result.addChild(span);
            }

            final CurriculumAggregator aggregator = CurriculumAggregatorServices.getAggregator(context, year);
            if (aggregator != null) {
                if (aggregator.isLegacy()) {
                    return result;
                }

                final HtmlInlineContainer span = new HtmlInlineContainer();
                span.addChild(new HtmlText(aggregator.getDescriptionFull()));
                span.setClasses(getAggregatorStyle(aggregator, scp, semester));
                result.addChild(span);
            }
        }

        return result;
    }

    static private String getAggregatorEntryStyle(final CurriculumAggregatorEntry entry, final StudentCurricularPlan scp,
            final ExecutionSemester semester) {

        String result = "label label-";

        // we test for aggregator enrolment in order to avoid counting optionals on a UI method
        final CurriculumAggregator aggregator = entry.getAggregator();
        if (aggregator.isEnrolmentMaster()
                || CurriculumAggregatorServices.isAggregationEnroled(aggregator.getContext(), scp, semester)) {

            result += "default";

        } else if (aggregator.isEnrolmentSlave()) {

            if (aggregator.getEnrolmentMasterContexts().stream()
                    .anyMatch(i -> CurriculumAggregatorServices.isAggregationEnroled(i, scp, semester))) {

                result += "warning";

            } else {
                result += "default";
            }
        }

        return result;
    }

    static private String getAggregatorStyle(final CurriculumAggregator aggregator, final StudentCurricularPlan scp,
            final ExecutionSemester semester) {

        String result = "label label-";

        // we test for aggregator enrolment in order to avoid counting optionals on a UI method
        if (CurriculumAggregatorServices.isAggregationEnroled(aggregator.getContext(), scp, semester)) {
            result += "success";

        } else {

            if (aggregator.isEnrolmentMaster()) {

                result += "info";

            } else if (aggregator.isEnrolmentSlave()) {

                if (aggregator.getEnrolmentMasterContexts().stream()
                        .anyMatch(i -> CurriculumAggregatorServices.isAggregationEnroled(i, scp, semester))) {

                    result += "danger";
                } else {

                    result += "info";
                }
            }
        }

        return result;
    }

}
