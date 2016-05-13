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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.EnroledCurriculumModuleWrapper;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.dto.student.enrollment.bolonha.StudentCurriculumGroupBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.CompetenceCourseServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorEntry;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;

import pt.ist.fenixWebFramework.renderers.components.HtmlActionLink;
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

    @Override
    protected void generateCurricularCoursesToEnrol(final HtmlTable groupTable,
            final StudentCurriculumGroupBean studentCurriculumGroupBean, final ExecutionSemester executionSemester) {

        final List<IDegreeModuleToEvaluate> coursesToEvaluate = filterCurricularCoursesToEvaluate(studentCurriculumGroupBean);

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
                        degreeModuleName += " (" + BundleUtil.getString(Bundle.STUDENT, "label.grade.scale") + " - "
                                + gradeScaleChain.getDescription() + ")";
                    }
                }
            }

            cellName.setBody(new HtmlText(degreeModuleName));

            // qubExtension, Aggregation Info
            final HtmlTableCell aggregationCell = htmlTableRow.createCell();
            aggregationCell.setClasses(" center ");
            aggregationCell.setBody(generateAggregationInfo(degreeModuleToEvaluate.getContext(),
                    getBolonhaStudentEnrollmentBean().getStudentCurricularPlan(), executionSemester));

            // Year
            final HtmlTableCell yearCell = htmlTableRow.createCell();
            yearCell.setClasses(getRenderer().getCurricularCourseToEnrolYearClasses());
            yearCell.setColspan(1);
            yearCell.setBody(new HtmlText(degreeModuleToEvaluate.getYearFullLabel()));

            if (!degreeModuleToEvaluate.isOptionalCurricularCourse()) {
                // Ects
                final HtmlTableCell ectsCell = htmlTableRow.createCell();
                ectsCell.setClasses(getRenderer().getCurricularCourseToEnrolEctsClasses());

                final StringBuilder ects = new StringBuilder();
                ects.append(degreeModuleToEvaluate.getEctsCredits()).append(" ")
                        .append(BundleUtil.getString(Bundle.STUDENT, "label.credits.abbreviation"));
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
                actionLink.setText(BundleUtil.getString(Bundle.STUDENT, "label.chooseOptionalCurricularCourse"));
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

                    if (CompetenceCourseServices.isCompetenceCourseApproved(
                            getBolonhaStudentEnrollmentBean().getStudentCurricularPlan(), curricularCourse)) {
                        iterator.remove();
                    }
                }
            }
        }

        return result;
    }

    /**
     * Necessary, we don't want this behaviour on subclasses of this layout
     */
    protected boolean isToFilterCurricularCoursesToEvaluate() {
        return ULisboaConfiguration.getConfiguration().getCurricularRulesApprovalsAwareOfCompetenceCourse();
    }

    private boolean isToDisableEnrolmentOption(final StudentCurriculumGroupBean input) {
        return input.isToBeDisabled() || (isStudentLogged()
                && appliesAnyRules(input.getCurriculumModule(), CurricularRuleType.ENROLMENT_TO_BE_APPROVED_BY_COORDINATOR));
    }

    private boolean isToDisableEnrolmentOption(final IDegreeModuleToEvaluate input) {
        if (isStudentLogged()) {

            if (appliesAnyRules(input, CurricularRuleType.ENROLMENT_TO_BE_APPROVED_BY_COORDINATOR)) {
                return true;
            }
        }

        final Context context = input.getContext();
        if (CurriculumAggregatorServices.getSlaveContexts(context).contains(context)) {
            return true;
        }

        return false;
    }

    private boolean appliesAnyRules(final IDegreeModuleToEvaluate input, final CurricularRuleType... curricularRuleTypes) {

        if (input != null && input.getContext() != null && curricularRuleTypes != null) {

            for (final CurricularRuleType curricularRuleType : curricularRuleTypes) {

                final CourseGroup parentCourseGroup = input.getCurriculumGroup().getDegreeModule();

                // check self rules
                final ExecutionSemester executionInterval = this.getBolonhaStudentEnrollmentBean().getExecutionPeriod();
                List<? extends ICurricularRule> rules =
                        input.getDegreeModule().getCurricularRules(curricularRuleType, parentCourseGroup, executionInterval);

                if (!rules.isEmpty() && rules.iterator().next().appliesToContext(input.getContext())) {
                    return true;
                }

                // check parent group rules
                rules = parentCourseGroup.getCurricularRules(curricularRuleType, executionInterval);
                if (!rules.isEmpty() && rules.iterator().next().appliesToContext(input.getContext())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean appliesAnyRules(final CurriculumGroup input, final CurricularRuleType... curricularRuleTypes) {

        if (input != null && curricularRuleTypes != null) {

            for (final CurricularRuleType curricularRuleType : curricularRuleTypes) {

                final CurriculumGroup parentCurriculumGroup = input.getCurriculumGroup();
                final CourseGroup parentCourseGroup = parentCurriculumGroup.getDegreeModule();

                // check self rules
                final ExecutionSemester executionInterval = this.getBolonhaStudentEnrollmentBean().getExecutionPeriod();
                List<? extends ICurricularRule> rules =
                        input.getDegreeModule().getCurricularRules(curricularRuleType, parentCourseGroup, executionInterval);

                if (!rules.isEmpty()) {
                    return true;
                }

                // check parent group rules, recursively until root
                if (!parentCurriculumGroup.isRoot() && appliesAnyRules(parentCurriculumGroup, curricularRuleTypes)) {
                    return true;
                }
            }
        }

        return false;
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
        aggregationCell.setClasses(" center se_enrolled ");
        aggregationCell.setBody(generateAggregationInfo(CurriculumAggregatorServices.getContext(enrolment),
                getBolonhaStudentEnrollmentBean().getStudentCurricularPlan(), enrolment.getExecutionPeriod()));

        // qubExtension, Year and Semester
        final HtmlTableCell yearAndSemesterCell = htmlTableRow.createCell();
        yearAndSemesterCell.setClasses(enrolmentYearClasses);

        final String yearAndSemester = CurricularPeriodServices.getCurricularYear(enrolment) + " "
                + BundleUtil.getString(Bundle.ENUMERATION, "YEAR") + ", " + enrolment.getExecutionPeriod().getQualifiedName();
        yearAndSemesterCell.setBody(new HtmlText(yearAndSemester));

        // Ects
        final HtmlTableCell ectsCell = htmlTableRow.createCell();
        ectsCell.setClasses(enrolmentEctsClasses);

        final StringBuilder ects = new StringBuilder();
        final double ectsCredits =
                (enrolment.isBolonhaDegree() && getBolonhaStudentEnrollmentBean().getCurricularRuleLevel().isNormal()) ? enrolment
                        .getAccumulatedEctsCredits(enrolment.getExecutionPeriod()) : enrolment.getEctsCredits();
        ects.append(ectsCredits).append(" ").append(BundleUtil.getString(Bundle.STUDENT, "label.credits.abbreviation"));

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

    private boolean isStudentLogged() {
        return isStudentLogged(getBolonhaStudentEnrollmentBean().getStudentCurricularPlan());
    }

    private boolean isStudentLogged(final StudentCurricularPlan studentCurricularPlan) {
        return Authenticate.getUser().getPerson() == studentCurricularPlan.getPerson();
    }

    /**
     * qubExtension, Aggregation Info
     */
    static public HtmlInlineContainer generateAggregationInfo(final Context context, final StudentCurricularPlan scp,
            final ExecutionSemester semester) {

        final HtmlInlineContainer result = new HtmlInlineContainer();

        if (context != null) {

            final CurriculumAggregatorEntry aggregatorEntry = context.getCurriculumAggregatorEntry();
            CurriculumAggregator aggregator = context.getCurriculumAggregator();

            String text = "";
            String spanStyleClasses = "label label-";

            if (aggregator != null) {

                text = aggregator.getDescription();

                if (aggregator.isMaster()) {
                    spanStyleClasses +=
                            CurriculumAggregatorServices.isAggregationEnroled(context, scp, semester) ? "success" : "info";

                } else if (aggregator.isSlave()) {

                    final Set<Context> masterContexts = aggregator.getMasterContexts();
                    final long count = masterContexts.stream()
                            .filter(i -> CurriculumAggregatorServices.isAggregationEnroled(i, scp, semester)).count();

                    if (count == 0) {
                        spanStyleClasses += "info";

                    } else if (count != masterContexts.size()) {
                        text += " [!]";
                        spanStyleClasses += "info";

// TODO legidio, check with school the best view pattern
//                        text += " Incompleto";
//                        spanStyleClasses += "danger";

                    } else {
                        spanStyleClasses += "success";
                    }
                }
            }

            if (aggregatorEntry != null) {
                aggregator = aggregatorEntry.getAggregator();

                text = aggregatorEntry.getDescription();

                if (aggregator.isMaster()) {
                    spanStyleClasses += "default";

// TODO legidio, check with school the best view pattern
//                    spanStyleClasses +=
//                            CurriculumAggregatorServices.isAggregationEnroled(context, scp, semester) ? "primary" : "default";

                } else if (aggregator.isSlave()) {

                    final Set<Context> masterContexts = aggregator.getMasterContexts();
                    final long count = masterContexts.stream()
                            .filter(i -> CurriculumAggregatorServices.isAggregationEnroled(i, scp, semester)).count();

                    if (count == 0) {
                        spanStyleClasses += "default";

                    } else if (count != masterContexts.size()) {
                        spanStyleClasses += "warning";

                    } else {
                        spanStyleClasses += "default";

// TODO legidio, check with school the best view pattern
//                        spanStyleClasses += "primary";
                    }
                }
            }

            result.addChild(new HtmlText(text));
            result.setClasses(spanStyleClasses);
        }

        return result;
    }

}
