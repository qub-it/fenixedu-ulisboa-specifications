package org.fenixedu.ulisboa.specifications.service.reports.providers;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.ulisboa.specifications.domain.CourseGroupDegreeInfo;
import org.fenixedu.ulisboa.specifications.domain.ExtendedDegreeInfo;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;

public class CourseGroupDegreeInfoDataProvider implements IReportDataProvider {

    protected static final String KEY = "degreeDocumentInfo";

    protected CourseGroupDegreeInfo courseGroupDegreeInfo;

    public CourseGroupDegreeInfoDataProvider(Registration registration, ExecutionYear executionYear,
            ProgramConclusion programConclusion) {
        Degree degree = registration.getDegree();
        DegreeInfo degreeInfo = degree.getMostRecentDegreeInfo(executionYear.getAcademicInterval());
        ExtendedDegreeInfo extendedDegreeInfo = null;
        if (degreeInfo != null) {
            extendedDegreeInfo = degreeInfo.getExtendedDegreeInfo();
        }

        if (extendedDegreeInfo != null) {

            if (programConclusion == null) {
                courseGroupDegreeInfo = extendedDegreeInfo.getCourseGroupDegreeInfosSet().stream()
                        .filter(di -> di.getCourseGroup().getProgramConclusion().isTerminal()).findFirst().orElse(null);
            } else {
                courseGroupDegreeInfo = extendedDegreeInfo.getCourseGroupDegreeInfosSet().stream()
                        .filter(di -> di.getCourseGroup().getProgramConclusion() == programConclusion).findFirst().orElse(null);
            }

        }

    }

    @Override
    public void registerFieldsAndImages(IDocumentFieldsData documentFieldsData) {

    }

    @Override
    public boolean handleKey(String key) {
        return KEY.equals(key);
    }

    @Override
    public Object valueForKey(String key) {
        return handleKey(key) ? courseGroupDegreeInfo : null;
    }

}
