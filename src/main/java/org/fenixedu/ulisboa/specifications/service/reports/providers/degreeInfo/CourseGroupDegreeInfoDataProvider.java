package org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.conclusion.RegistrationConclusionServices;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.Map;
import java.util.Optional;

public class CourseGroupDegreeInfoDataProvider implements IReportDataProvider {

    protected static final String KEY = "degreeDocumentInfo";

    protected CourseGroupDegreeInfoBean courseGroupDegreeInfo;

    public CourseGroupDegreeInfoDataProvider(final Registration registration, ExecutionYear executionYear,
            final ProgramConclusion programConclusion) {

        courseGroupDegreeInfo = findConclusionTitle(registration, programConclusion).filter(title -> !title.isEmpty())
                .map(title -> new CourseGroupDegreeInfoBean(title)).orElse(null);
    }

    private static Optional<LocalizedString> findConclusionTitle(Registration registration, ProgramConclusion programConclusion) {
        final Map<ProgramConclusion, RegistrationConclusionBean> conclusions =
                RegistrationConclusionServices.getConclusions(registration);

        if (programConclusion != null) {
            final RegistrationConclusionBean conclusionBean = conclusions.get(programConclusion);
            return Optional.ofNullable(conclusionBean).map(cb -> cb.getCurriculumGroup().getDegreeModule().getConclusionTitle());
        } else {
            return conclusions.entrySet().stream().filter(e -> e.getKey().isTerminal())
                    .map(e -> e.getValue().getCurriculumGroup().getDegreeModule().getConclusionTitle()).findAny();
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

    public static class CourseGroupDegreeInfoBean {

        private LocalizedString degreeName;

        public CourseGroupDegreeInfoBean(final LocalizedString degreeName) {
            this.degreeName = degreeName;
        }

        public LocalizedString getDegreeName() {
            return degreeName;
        }
    }

}
