package org.fenixedu.ulisboa.specifications.domain.legal.raides.process;

import static org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides.formatArgs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Set;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.ulisboa.specifications.domain.legal.LegalReportContext;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblDiplomado;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionInformation;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionServices;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityRegistrationInformation;

import com.google.common.base.Strings;

public class DiplomadoService extends RaidesService {

    protected boolean valid = true;

    public DiplomadoService(final LegalReport report) {
        super(report);
    }

    public TblDiplomado create(final RaidesRequestParameter raidesRequestParameter, final ExecutionYear executionYear,
            final Registration registration) {
        final Unit institutionUnit = raidesRequestParameter.getInstitution();

        final TblDiplomado bean = new TblDiplomado();
        bean.setRegistration(registration);

        preencheInformacaoMatricula(report, bean, institutionUnit, executionYear, registration);

        bean.setAreaInvestigacao(registration.getResearchArea() != null ? registration.getResearchArea().getCode() : "");

        if (isTerminalConcluded(registration, executionYear)) {
            final RegistrationConclusionInformation terminalConclusionInfo =
                    terminalConclusionInformation(registration, executionYear);
            final RegistrationConclusionBean registrationConclusionBean = terminalConclusionInfo.getRegistrationConclusionBean();

            bean.setConcluiGrau(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
            bean.setAnoLectivo(terminalConclusionInfo.getConclusionYear().getQualifiedName());
            bean.setNumInscConclusao(
                    String.valueOf(Raides.getEnrolmentYearsIncludingPrecedentRegistrations(registration).size()));

            if (Raides.isDoctoralDegree(registration) && !registrationConclusionBean.isConclusionProcessed()) {
                LegalReportContext.addError("", i18n("error.Raides.validation.doctoral.degree.without.conclusion.process",
                        formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }

            if (registrationConclusionBean.getDescriptiveGrade() != null
                    && !registrationConclusionBean.getDescriptiveGrade().isEmpty() && Raides.isDoctoralDegree(registration)) {
                bean.setClassificacaoFinal(LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(finalGrade(registrationConclusionBean.getDescriptiveGrade().getValue())));
            } else if (registrationConclusionBean.getFinalGrade().isEmpty()) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.finalGrade.set.but.empty", formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            } else {
                bean.setClassificacaoFinal(LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(finalGrade(registrationConclusionBean.getFinalGrade().getValue())));
            }

            bean.setDataDiploma(registrationConclusionBean.getConclusionDate().toLocalDate());
        } else {
            bean.setConcluiGrau(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));
        }

        if (Raides.isMasterDegreeOrDoctoralDegree(registration) && isScholarPartConcluded(registration, executionYear)) {
            final RegistrationConclusionInformation scholarPartConclusionInfo =
                    scholarPartConclusionInformation(registration, executionYear);
            final RegistrationConclusionBean conclusionBean =
                    scholarPartConclusionInfo.getRegistrationConclusionBean();

            bean.setAnoLectivo(conclusionBean.getConclusionYear().getQualifiedName());
            bean.setConclusaoMd(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
            bean.setClassificacaoFinalMd(LegalMapping.find(report, LegalMappingType.GRADE)
                    .translate(finalGrade(conclusionBean.getFinalGrade().getValue())));
            
            if(Strings.isNullOrEmpty(bean.getClassificacaoFinalMd()) || "0".equals(bean.getClassificacaoFinalMd())) {
                if(Raides.isDoctoralDegree(registration) && conclusionBean.getDescriptiveGrade() != null) {
                    bean.setClassificacaoFinalMd(finalGrade(conclusionBean.getDescriptiveGrade().getValue()));
                }
            }
            
        } else if (Raides.isIntegratedMasterDegree(registration) && isScholarPartConcluded(registration, executionYear)
                && !isTerminalConcluded(registration, executionYear)) {
            bean.setConclusaoMd(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));

            if (((RaidesInstance) report).isToReportAllIntegratedMasterFirstCycleGraduatedStudents()
                    || (((RaidesInstance) report).isToReportIntegratedMasterFirstCycleGraduatedStudentsOnlyWithConclusionProcess()
                            && scholarPartConclusionInformation(registration, executionYear).getRegistrationConclusionBean()
                                    .isConclusionProcessed())) {

                final RegistrationConclusionInformation scholarPartConclusionInfo =
                        scholarPartConclusionInformation(registration, executionYear);
                final RegistrationConclusionBean scholarPartConclusionBean =
                        scholarPartConclusionInfo.getRegistrationConclusionBean();

                bean.setCurso(LegalMapping.find(report, LegalMappingType.INTEGRATED_MASTER_FIRST_CYCLE_CODES)
                        .translate(registration.getDegree()));
                bean.setConcluiGrau(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
                bean.setAnoLectivo(scholarPartConclusionBean.getConclusionYear().getQualifiedName());
                bean.setNumInscConclusao(
                        String.valueOf(Raides.getEnrolmentYearsIncludingPrecedentRegistrations(registration).size()));

                bean.setClassificacaoFinal(LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(finalGrade(scholarPartConclusionBean.getFinalGrade().getValue())));

                bean.setDataDiploma(scholarPartConclusionBean.getConclusionDate().toLocalDate());
            }

        } else {
            bean.setConclusaoMd(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));
        }

        bean.setMobilidadeCredito(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));

        if (MobilityRegistrationInformation.hasAnyInternationalOutgoingMobility(registration)) {

            final MobilityRegistrationInformation mobility = findOutgoingMobility(registration);

            bean.setMobilidadeCredito(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
            bean.setTipoMobilidadeCredito(LegalMapping.find(report, LegalMappingType.INTERNATIONAL_MOBILITY_ACTIVITY)
                    .translate(mobility.getMobilityActivityType()));
            bean.setProgMobilidadeCredito(LegalMapping.find(report, LegalMappingType.INTERNATIONAL_MOBILITY_PROGRAM)
                    .translate(mobility.getMobilityProgramType()));

            if (Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())
                    || Raides.ProgramaMobilidade.OUTRO_TRES.equals(bean.getProgMobilidadeCredito())) {
                bean.setOutroProgMobCredito(mobility.getMobilityProgramType().getName().getContent());
            }

            if (mobility.hasCountry()) {
                bean.setPaisMobilidadeCredito(mobility.getCountry().getCode());
            }
        }

        preencheGrauPrecedentCompleto(bean, institutionUnit, executionYear, registration);

        validaClassificacao(executionYear, registration, bean);
        validaMobilidadeCredito(executionYear, registration, bean);
        validaAreaInvestigacao(executionYear, registration, bean);

        return bean;
    }

    private MobilityRegistrationInformation findOutgoingMobility(Registration registration) {

        final MobilityRegistrationInformation mainInformation =
                MobilityRegistrationInformation.findMainInternationalOutgoingInformation(registration);
        if (mainInformation != null) {
            return mainInformation;
        }

        return MobilityRegistrationInformation.findInternationalOutgoingInformations(registration).stream()
                .sorted((x, y) -> x.getExternalId().compareTo(y.getExternalId())).findFirst().orElse(null);
    }

    private String finalGrade(final String value) {
        if (!Strings.isNullOrEmpty(value) && value.matches("\\d+\\.\\d+")) {
            return new BigDecimal(value).setScale(0, RoundingMode.HALF_UP).toString();
        }

        return value;
    }

    private RegistrationConclusionInformation scholarPartConclusionInformation(final Registration registration,
            final ExecutionYear executionYear) {
        final Set<RegistrationConclusionInformation> conclusionInfoSet =
                RegistrationConclusionServices.inferConclusion(registration);
        for (final RegistrationConclusionInformation rci : conclusionInfoSet) {
            if (!rci.isConcluded()) {
                continue;
            }

            if (!rci.isScholarPart()) {
                continue;
            }

            if (Raides.scholarPartConclusionYear(rci) != executionYear) {
                continue;
            }

            return rci;
        }

        return null;
    }

    private boolean isScholarPartConcluded(final Registration registration, final ExecutionYear executionYear) {
        return scholarPartConclusionInformation(registration, executionYear) != null;
    }

    private RegistrationConclusionInformation terminalConclusionInformation(final Registration registration,
            final ExecutionYear executionYear) {
        final Set<RegistrationConclusionInformation> conclusionInfoSet =
                RegistrationConclusionServices.inferConclusion(registration);
        for (final RegistrationConclusionInformation rci : conclusionInfoSet) {
            if (!rci.isConcluded()) {
                continue;
            }

            if (rci.isScholarPart()) {
                continue;
            }

            if (rci.getConclusionYear() != executionYear) {
                continue;
            }

            return rci;
        }

        return null;
    }

    private boolean isTerminalConcluded(final Registration registration, final ExecutionYear executionYear) {
        return terminalConclusionInformation(registration, executionYear) != null;
    }

    private String thesisFinalGrade(final Registration registration) {
        final Collection<Enrolment> dissertationEnrolments =
                registration.getLastStudentCurricularPlan().getDissertationEnrolments();

        for (final Enrolment enrolment : dissertationEnrolments) {
            if (enrolment.isApproved()) {
                return enrolment.getGradeValue();
            }
        }

        return null;
    }

    protected void validaMobilidadeCredito(final ExecutionYear executionYear, final Registration registration,
            final TblDiplomado bean) {

        if (!MobilityRegistrationInformation.hasAnyInternationalOutgoingMobility(registration)) {
            return;
        }

        if (MobilityRegistrationInformation.findInternationalOutgoingInformations(registration).size() > 1
                && MobilityRegistrationInformation.findMainInternationalOutgoingInformation(registration) == null) {
            LegalReportContext.addError("", i18n("error.Raides.validation.graduated.mobility.mainInformation.missing",
                    formatArgs(registration, executionYear)));
        }

        if (Strings.isNullOrEmpty(bean.getTipoMobilidadeCredito())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.graduated.mobility.credit.type.missing",
                    formatArgs(registration, executionYear)));
        }

        if (Strings.isNullOrEmpty(bean.getProgMobilidadeCredito())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.graduated.mobility.program.type.missing",
                    formatArgs(registration, executionYear)));
        }

        if ((Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())
                || Raides.ProgramaMobilidade.OUTRO_TRES.equals(bean.getProgMobilidadeCredito()))
                && Strings.isNullOrEmpty(bean.getOutroProgMobCredito())) {
            LegalReportContext.addError("", i18n("error.Raides.validation.graduated.mobility.other.program.type.missing",
                    formatArgs(registration, executionYear)));
        }

        if (Strings.isNullOrEmpty(bean.getPaisMobilidadeCredito())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.graduated.mobility.country.missing", formatArgs(registration, executionYear)));
        }

    }

    protected void validaClassificacao(final ExecutionYear executionYear, final Registration registration,
            final TblDiplomado bean) {

        if (isScholarPartConcluded(registration, executionYear) && Raides.isMasterDegreeOrDoctoralDegree(registration)) {
            if (Strings.isNullOrEmpty(bean.getClassificacaoFinalMd()) || "0".equals(bean.getClassificacaoFinalMd())) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.masterOrDoctoral.scholarpart.classification.empty.or.zero",
                                formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }
        }

        if (isTerminalConcluded(registration, executionYear) && Strings.isNullOrEmpty(bean.getClassificacaoFinal())
                || "0".equals(bean.getClassificacaoFinal())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.masterOrDoctoral.terminalpart.classification.empty.or.zero",
                            formatArgs(registration, executionYear)));
        }
    }

    private void validaAreaInvestigacao(ExecutionYear executionYear, Registration registration, TblDiplomado bean) {
        if (Raides.isDoctoralDegree(registration) && Strings.isNullOrEmpty(bean.getAreaInvestigacao())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.doctoral.requires.research.area", formatArgs(registration, executionYear)));
        }
    }

}
