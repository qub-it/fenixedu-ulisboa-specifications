package org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.ExtraCurricularActivity;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.qubdocs.util.DocsStringUtils;
import org.fenixedu.academic.domain.degree.ExtendedDegreeInfo;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Function;
import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;

public class ConclusionInformationDataProvider implements IReportDataProvider {

    protected static final String KEY = "conclusionInformation";

    protected ConclusionInformation conclusionInformation;

    public ConclusionInformationDataProvider(final Registration registration, final ProgramConclusion programConclusion) {
        this.conclusionInformation = new ConclusionInformation(new RegistrationConclusionBean(registration, programConclusion));
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
        if (handleKey(key)) {
            return this.conclusionInformation;
        }

        return null;
    }

    private static Function<RegistrationConclusionBean, String> degreeEctsGradeProvider = conclusion -> "";

    public static void setDegreeEctsGradeProviderProvider(
            final Function<RegistrationConclusionBean, String> degreeEctsGradeProvider) {
        ConclusionInformationDataProvider.degreeEctsGradeProvider = degreeEctsGradeProvider;
    }

    public class ConclusionInformation {
        protected RegistrationConclusionBean conclusionBean;

        public ConclusionInformation(final RegistrationConclusionBean bean) {
            this.conclusionBean = bean;
        }

        public RegistrationConclusionBean getConclusionBean() {
            return conclusionBean;
        }

        public boolean isConclusionDateBefore(final String date) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            return getConclusionDate().isBefore(formatter.parseLocalDate(date));
        }

        public LocalDate getConclusionDate() {
            return conclusionBean.getConclusionDate().toLocalDate();
        }

        public DegreeInfo getDegreeInfo() {
            final ExecutionYear conclusionYear = ExecutionYear.getExecutionYearByDate(conclusionBean.getConclusionDate());
            return conclusionBean.getRegistration().getDegree().getMostRecentDegreeInfo(conclusionYear.getAcademicInterval());
        }

        public ExtendedDegreeInfo getExtendedDegreeInfo() {
            final ExecutionYear conclusionYear = ExecutionYear.getExecutionYearByDate(conclusionBean.getConclusionDate());
            return ExtendedDegreeInfo.getMostRecent(conclusionYear, conclusionBean.getRegistration().getDegree());
        }

        public String getAverage() {
            return conclusionBean.getRawGrade() != null ? conclusionBean.getRawGrade().getValue() : null;
        }

        public String getFinalAverage() {
            return conclusionBean.getFinalGrade() != null ? conclusionBean.getFinalGrade().getValue() : null;
        }

        public String getRoundedFinalAverage() {
            String finalAverage = getFinalAverage();
            if (finalAverage != null) {
                BigDecimal average = new BigDecimal(getFinalAverage());
                return average.setScale(0, RoundingMode.HALF_EVEN).toString();
            } else {
                return null;
            }
        }

        public boolean hasGraduationLevel() {
            return conclusionBean.getProgramConclusion() != null
                    && !conclusionBean.getProgramConclusion().getGraduationLevel().isEmpty();
        }

        public String getEctsGrade() {
            return degreeEctsGradeProvider.apply(conclusionBean);
        }

        public LocalizedString getFinalAverageDescription() {
            return getRoundedFinalAverage() != null ? DocsStringUtils
                    .capitalize(BundleUtil.getLocalizedString("resources.EnumerationResources", getRoundedFinalAverage())) : null;
        }

        public LocalizedString getQualitativeGrade() {
            if (conclusionBean.getDescriptiveGrade() != null && conclusionBean.getDescriptiveGrade().getExtendedValue() != null) {
                return conclusionBean.getDescriptiveGrade().getExtendedValue();
            }
            return new LocalizedString();
        }

        public Set<ExtraCurricularActivity> getExtraCurricularActivities() {
            return conclusionBean.getRegistration().getStudent().getExtraCurricularActivitySet().size() > 0 ? conclusionBean
                    .getRegistration().getStudent().getExtraCurricularActivitySet() : null;
        }

        public BigDecimal getDismissalCredits() {

            final CurriculumGroup curriculumGroup = this.conclusionBean.getCurriculumGroup();
            final StudentCurricularPlan studentCurricularPlan = curriculumGroup.getStudentCurricularPlan();
            final List<Dismissal> dismissals = studentCurricularPlan.getDismissals().stream()
                    .filter(d -> d.getCredits().isCredits() && curriculumGroup.hasCurriculumModule(d.getCurriculumGroup()))
                    .collect(Collectors.toList());

            BigDecimal sum = BigDecimal.ZERO;
            for (Dismissal dismissal : dismissals) {
                sum = sum.add(dismissal.getEctsCreditsForCurriculum());
            }

            return sum;
        }

        public boolean isDismissalCreditsGiven() {
            return getDismissalCredits().compareTo(BigDecimal.ZERO) > 0;
        }
    }

}
