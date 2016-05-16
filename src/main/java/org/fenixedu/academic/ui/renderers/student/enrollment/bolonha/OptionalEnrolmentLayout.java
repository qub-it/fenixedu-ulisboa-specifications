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

import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.AnyCurricularCourseExceptionsExecutorLogic;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.organizationalStructure.DepartmentUnit;
import org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentOptionalEnrollmentBean;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.CompetenceCourseServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;

import com.google.common.base.Strings;

import pt.ist.fenixWebFramework.renderers.components.HtmlActionLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.controllers.HtmlActionLinkController;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

/**
 * @see {@link org.fenixedu.academic.ui.renderers.student.enrollment.bolonha.BolonhaStudentOptionalEnrollmentInputRenderer.BolonhaStudentOptionalEnrolmentLayout}
 */
public class OptionalEnrolmentLayout extends Layout {

    private OptionalEnrolmentRenderer renderer;

    public OptionalEnrolmentRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(final OptionalEnrolmentRenderer input) {
        this.renderer = input;
    }

    private BolonhaStudentOptionalEnrollmentBean bolonhaStudentOptionalEnrollmentBean = null;

    @Override
    public HtmlComponent createComponent(Object object, Class type) {
        bolonhaStudentOptionalEnrollmentBean = (BolonhaStudentOptionalEnrollmentBean) object;

        if (bolonhaStudentOptionalEnrollmentBean == null) {
            return new HtmlText();
        }

        final HtmlBlockContainer container = new HtmlBlockContainer();

        generateCourseGroup(container, bolonhaStudentOptionalEnrollmentBean.getDegreeCurricularPlan().getRoot(), 0);

        return container;
    }

    // Bolonha Structure
    private void generateCourseGroup(HtmlBlockContainer blockContainer, CourseGroup courseGroup, int depth) {
        final HtmlTable groupTable = new HtmlTable();
        blockContainer.addChild(groupTable);
        groupTable.setClasses(getRenderer().getTablesClasses());
        groupTable.setStyle("width: " + (getRenderer().getInitialWidth() - depth) + "em; margin-left: " + depth + "em;");

        final HtmlTableRow htmlTableRow = groupTable.createRow();
        htmlTableRow.setClasses(getRenderer().getGroupRowClasses());
        // qubExtension
        htmlTableRow.createCell().setBody(new HtmlText(courseGroup.getNameI18N().getContent()));

        final List<Context> childCourseGroupContexts =
                courseGroup.getValidChildContexts(CourseGroup.class, bolonhaStudentOptionalEnrollmentBean.getExecutionPeriod());
        final List<Context> childCurricularCourseContexts = courseGroup.getValidChildContexts(CurricularCourse.class,
                bolonhaStudentOptionalEnrollmentBean.getExecutionPeriod());

        Collections.sort(childCourseGroupContexts, new BeanComparator("childOrder"));
        Collections.sort(childCurricularCourseContexts, new BeanComparator("childOrder"));

        generateCurricularCourses(blockContainer, childCurricularCourseContexts,
                depth + getRenderer().getWidthDecreasePerLevel());

        for (final Context context : childCourseGroupContexts) {
            generateCourseGroup(blockContainer, (CourseGroup) context.getChildDegreeModule(),
                    depth + getRenderer().getWidthDecreasePerLevel());
        }
    }

    private void generateCurricularCourses(HtmlBlockContainer blockContainer, List<Context> contexts, int depth) {

        final HtmlTable table = new HtmlTable();
        blockContainer.addChild(table);
        table.setClasses(getRenderer().getTablesClasses());
        table.setStyle("width: " + (getRenderer().getInitialWidth() - depth) + "em; margin-left: " + depth + "em;");

        for (final Context context : contexts) {
            final CurricularCourse curricularCourse = (CurricularCourse) context.getChildDegreeModule();
            if (!curricularCourse.isOptionalCurricularCourse()) {

                final HtmlTableRow htmlTableRow = table.createRow();
                HtmlTableCell cellName = htmlTableRow.createCell();
                cellName.setClasses(getRenderer().getCurricularCourseNameClasses());
                cellName.setBody(generateCurricularCourseNameComponent(curricularCourse,
                        this.bolonhaStudentOptionalEnrollmentBean.getExecutionPeriod()));

                // Year
                final HtmlTableCell yearCell = htmlTableRow.createCell();
                yearCell.setClasses(getRenderer().getCurricularCourseYearClasses());
                // qubExtension, TODO abbreviated
                final HtmlText yearCellBody = new HtmlText(context.getCurricularPeriod().getFullLabel());
                yearCellBody.setTitle(context.getCurricularPeriod().getFullLabel());
                yearCell.setBody(yearCellBody);

                // Ects
                final HtmlTableCell ectsCell = htmlTableRow.createCell();
                ectsCell.setClasses(getRenderer().getCurricularCourseEctsClasses());

                final StringBuilder ects = new StringBuilder();
                ects.append(curricularCourse.getEctsCredits()).append(" ")
                        .append(BundleUtil.getString(Bundle.STUDENT, "label.credits.abbreviation"));
                ectsCell.setBody(new HtmlText(ects.toString()));

                // Enrollment Link
                final HtmlTableCell linkTableCell = htmlTableRow.createCell();
                linkTableCell.setClasses(getRenderer().getCurricularCourseLinkClasses());

                final HtmlActionLink actionLink = new HtmlActionLink();
                actionLink.setText(BundleUtil.getString(Bundle.STUDENT, "label.enroll"));
                actionLink.setName(
                        "optionalCurricularCourseEnrolLink" + curricularCourse.getExternalId() + "_" + context.getExternalId());
                // qubExtension
                final String disableReason = isToDisableEnrolmentOption(context);
                if (!Strings.isNullOrEmpty(disableReason)) {
                    actionLink.setOnClick("function(){return false;}");
                    final String style = "text-decoration: line-through; color: grey;";
                    actionLink.setStyle(style);
                    cellName.setStyle(style);
                    actionLink.setText(disableReason);
                } else {
                    actionLink.setOnClick(
                            "$(this).closest('form').find('input[name=\\'method\\']').attr('value', 'enrolInOptionalCurricularCourse');");
                }
                actionLink.setController(new UpdateSelectedOptionalCurricularCourseController(curricularCourse));
                linkTableCell.setBody(actionLink);
            }
        }
    }

    // qubExtension
    static public HtmlBlockContainer generateCurricularCourseNameComponent(final CurricularCourse curricularCourse,
            final ExecutionSemester executionSemester) {

        final HtmlBlockContainer container = new HtmlBlockContainer();
        container.addChild(
                new HtmlText(curricularCourse.getCode() + " - " + curricularCourse.getNameI18N(executionSemester).getContent()));

        if (curricularCourse.getCompetenceCourse() != null) {

            String description = "";
            if (curricularCourse.getCompetenceCourse() != null) {
                final DepartmentUnit unit = curricularCourse.getCompetenceCourse().getDepartmentUnit();
                if (unit != null) {
                    description = unit.getName();
                }
            }

            if (StringUtils.isNotBlank(description)) {
                final HtmlText descriptionText = new HtmlText("\n" + description, false, true);
                descriptionText.setStyle("font-style: italic;");
                container.addChild(descriptionText);
            }
        }
        return container;
    }

    @SuppressWarnings("serial")
    private static class UpdateSelectedOptionalCurricularCourseController extends HtmlActionLinkController {

        private final CurricularCourse curricularCourse;

        public UpdateSelectedOptionalCurricularCourseController(final CurricularCourse curricularCourse) {
            this.curricularCourse = curricularCourse;
        }

        @Override
        protected boolean isToSkipUpdate() {
            return false;
        }

        @Override
        public void linkPressed(IViewState viewState, HtmlActionLink link) {
            ((BolonhaStudentOptionalEnrollmentBean) viewState.getMetaObject().getObject())
                    .setSelectedOptionalCurricularCourse(this.curricularCourse);
        }

    }

    // qubExtension
    private String isToDisableEnrolmentOption(final Context input) {

        final CurricularCourse course = (CurricularCourse) input.getChildDegreeModule();
        final DegreeCurricularPlan dcp = this.bolonhaStudentOptionalEnrollmentBean.getDegreeCurricularPlan();
        final StudentCurricularPlan scp = this.bolonhaStudentOptionalEnrollmentBean.getStudentCurricularPlan();

        if (AnyCurricularCourseExceptionsExecutorLogic.isException(course.getCompetenceCourse(), dcp, scp)) {
            return BundleUtil.getString("resources/FenixeduUlisboaSpecificationsResources",
                    "curricularRules.ruleExecutors.AnyCurricularCourseExceptions.not.offered.label");
        }

        if (CurriculumAggregatorServices.getSlaveContexts(input).contains(input)) {
            return "Tronco/Módulo";
        }

        if (CompetenceCourseServices.isCompetenceCourseApproved(scp, course)) {
            return BundleUtil.getString(Bundle.ENUMERATION, "approved");
        }

        return null;
    }

}
