package org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.ulisboa.specifications.domain.CourseGroupDegreeInfo;
import org.fenixedu.ulisboa.specifications.domain.ExtendedDegreeInfo;
import org.joda.time.YearMonthDay;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;

public class CourseGroupDegreeInfoDataProvider implements IReportDataProvider {

    protected static final String KEY = "degreeDocumentInfo";

    protected CourseGroupDegreeInfo courseGroupDegreeInfo;

    public CourseGroupDegreeInfoDataProvider(final Registration registration, ExecutionYear executionYear,
            final ProgramConclusion programConclusion) {
        final Degree degree = registration.getDegree();

        YearMonthDay conclusionDate = registration.getConclusionDate();
        if (conclusionDate != null) {
            executionYear = ExecutionYear.readByDateTime(conclusionDate.toLocalDate());
        }

        final ExtendedDegreeInfo extendedDegreeInfo = ExtendedDegreeInfo.getMostRecent(executionYear, degree);
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
