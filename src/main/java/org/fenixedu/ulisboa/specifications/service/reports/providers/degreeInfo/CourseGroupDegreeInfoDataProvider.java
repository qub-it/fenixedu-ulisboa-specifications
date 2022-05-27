package org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.ExtendedDegreeInfo;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.ConclusionProcess;
import org.fenixedu.ulisboa.specifications.domain.CourseGroupDegreeInfo;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;

public class CourseGroupDegreeInfoDataProvider implements IReportDataProvider {

    protected static final String KEY = "degreeDocumentInfo";

    protected CourseGroupDegreeInfo courseGroupDegreeInfo;

    public CourseGroupDegreeInfoDataProvider(final Registration registration, ExecutionYear executionYear,
            final ProgramConclusion programConclusion) {
        final Degree degree = registration.getDegree();

        final ExecutionYear conclusionYear = ProgramConclusion.getConclusionProcess(registration.getLastStudentCurricularPlan())
                .map(ConclusionProcess::getConclusionYear).orElse(null);
        if (conclusionYear != null) {
            executionYear = conclusionYear;
        }

        //Try to get last custom name if there is one for combination degree+programConclusion
        while (courseGroupDegreeInfo == null && executionYear.isAfterOrEquals(registration.getStartExecutionYear())) {
            final ExtendedDegreeInfo extendedDegreeInfo = ExtendedDegreeInfo.getMostRecent(executionYear, degree);
            if (extendedDegreeInfo != null) {

                if (programConclusion == null) {
                    courseGroupDegreeInfo = extendedDegreeInfo.getCourseGroupDegreeInfosSet().stream()
                            .filter(di -> di.getCourseGroup().getProgramConclusion().isTerminal()).findFirst().orElse(null);
                } else {
                    courseGroupDegreeInfo = extendedDegreeInfo.getCourseGroupDegreeInfosSet().stream()
                            .filter(di -> di.getCourseGroup().getProgramConclusion() == programConclusion).findFirst()
                            .orElse(null);
                }

            }
            executionYear = executionYear.getPreviousExecutionYear();
        }

    }

    @Override
    public void registerFieldsAndImages(final IDocumentFieldsData documentFieldsData) {

    }

    @Override
    public boolean handleKey(final String key) {
        return KEY.equals(key);
    }

    @Override
    public Object valueForKey(final String key) {
        return handleKey(key) ? courseGroupDegreeInfo : null;
    }

}
