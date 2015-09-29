package org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum;

import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.ui.renderers.student.curriculum.StudentCurricularPlanRenderer;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;

import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;

public class StudentCurricularPlanLayout extends StudentCurricularPlanRenderer.StudentCurricularPlanLayout {

    public StudentCurricularPlanLayout(StudentCurricularPlanRenderer renderer) {
        super(renderer);
    }

    @Override
    protected void generateSemesterCell(final HtmlTableRow row, final ICurriculumEntry entry) {

        final Integer curricularYear = getCurricularYearFor(entry);
        final String yearPart =
                curricularYear != null ? curricularYear + " " + BundleUtil.getString(Bundle.APPLICATION, "label.curricular.year")
                        + " " : "";

        final String semester;
        if (entry.hasExecutionPeriod()) {
            semester =
                    entry.getExecutionPeriod().getSemester().toString() + " "
                            + BundleUtil.getString(Bundle.APPLICATION, "label.semester.short");
        } else {
            semester = EMPTY_INFO;
        }

        generateCellWithText(row, yearPart + semester, this.renderer.getEnrolmentSemesterCellClass());

    }

    private Integer getCurricularYearFor(final ICurriculumEntry entry) {

        if (!(entry instanceof CurriculumLine)) {
            return null;
        }
        return CurricularPeriodServices.getCurricularYear((CurriculumLine) entry);
    }

    public static void registerStudentCurricularPlanLayout() {
        StudentCurricularPlanRenderer.setLayoutProvider(renderer -> new StudentCurricularPlanLayout(renderer));
    }
}
