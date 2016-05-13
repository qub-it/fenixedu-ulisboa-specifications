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

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentEnrollmentBean;
import org.fenixedu.academic.dto.student.enrollment.bolonha.StudentCurriculumGroupBean;

import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyArrayConverter;
import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlMultipleHiddenField;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;

/**
 * Layout without curriculum group restrictions
 */
public class ImprovementEnrolmentLayout extends EnrolmentLayout {

    /**
     * False, since we defined our own AcademicAdminOfficeImprovementBolonhaStudentEnrolmentDA's
     * StudentCurriculumGroupBean.buildCurricularCoursesToEnrol method
     */
    @Override
    public boolean isToFilterCurricularCoursesToEvaluate() {
        return false;
    }

    @Override
    public HtmlComponent createComponent(Object object, Class type) {
        setBolonhaStudentEnrollmentBean((BolonhaStudentEnrollmentBean) object);

        if (getBolonhaStudentEnrollmentBean() == null) {
            return new HtmlText();
        }

        final HtmlBlockContainer container = new HtmlBlockContainer();

        HtmlMultipleHiddenField hiddenEnrollments = new HtmlMultipleHiddenField();
        hiddenEnrollments.bind(getRenderer().getInputContext().getMetaObject(), "curriculumModulesToRemove");
        hiddenEnrollments.setConverter(new DomainObjectKeyArrayConverter());
        hiddenEnrollments.setController(getEnrollmentsController());

        HtmlMultipleHiddenField hiddenDegreeModulesToEvaluate = new HtmlMultipleHiddenField();
        hiddenDegreeModulesToEvaluate.bind(getRenderer().getInputContext().getMetaObject(), "degreeModulesToEvaluate");
        hiddenDegreeModulesToEvaluate.setConverter(getBolonhaStudentEnrollmentBean().getDegreeModulesToEvaluateConverter());
        hiddenDegreeModulesToEvaluate.setController(getDegreeModulesToEvaluateController());

        container.addChild(hiddenEnrollments);
        container.addChild(hiddenDegreeModulesToEvaluate);

        generate(container, getBolonhaStudentEnrollmentBean().getStudentCurricularPlan(),
                getBolonhaStudentEnrollmentBean().getRootStudentCurriculumGroupBean(),
                getBolonhaStudentEnrollmentBean().getExecutionPeriod(), 0);

        return container;
    }

    private void generate(final HtmlBlockContainer blockContainer, final StudentCurricularPlan studentCurricularPlan,
            final StudentCurriculumGroupBean studentCurriculumGroupBean, final ExecutionSemester executionSemester,
            final int depth) {

        // createTable
        final HtmlTable table = new HtmlTable();
        table.setClasses(getRenderer().getTablesClasses());
        table.setStyle("width: " + (getRenderer().getInitialWidth() - depth) + "em; margin-left: " + depth + "em;");

        blockContainer.addChild(table);

        // addHeaderRow
        final HtmlTableRow headerRow = table.createRow();
        headerRow.setClasses(getRenderer().getGroupRowClasses());
        final HtmlTableCell titleCell = headerRow.createCell();
        if (studentCurriculumGroupBean.getCurriculumModule().getDegreeCurricularPlanOfStudent().isEmpty()) {
            titleCell.setBody(new HtmlText(studentCurriculumGroupBean.getCurriculumModule().getName().getContent()));
        } else {
            titleCell.setBody(createDegreeCurricularPlanLink(studentCurriculumGroupBean));
        }

        final HtmlTable coursesTable = createCoursesTable(blockContainer, depth);
        generateAllEnrolments(coursesTable, studentCurriculumGroupBean, executionSemester);
        generateAllCurricularCoursesToEnrol(coursesTable, studentCurriculumGroupBean, executionSemester);

        generateCurricularCoursesToEnrol(coursesTable, studentCurriculumGroupBean, executionSemester);
    }

    private void generateAllEnrolments(final HtmlTable coursesTable, final StudentCurriculumGroupBean bean,
            final ExecutionSemester executionSemester) {

        for (final StudentCurriculumGroupBean iter : bean.getEnrolledCurriculumGroupsSortedByOrder(executionSemester)) {
            generateEnrolments(iter, coursesTable);
            generateAllEnrolments(coursesTable, iter, executionSemester);
        }
    }

    private void generateAllCurricularCoursesToEnrol(final HtmlTable coursesTable, final StudentCurriculumGroupBean bean,
            final ExecutionSemester executionSemester) {

        for (final StudentCurriculumGroupBean iter : bean.getEnrolledCurriculumGroupsSortedByOrder(executionSemester)) {
            generateCurricularCoursesToEnrol(coursesTable, iter, executionSemester);
            generateAllCurricularCoursesToEnrol(coursesTable, iter, executionSemester);
        }
    }

}
