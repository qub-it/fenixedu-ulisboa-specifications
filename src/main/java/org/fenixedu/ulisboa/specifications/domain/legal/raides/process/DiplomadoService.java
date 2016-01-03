package org.fenixedu.ulisboa.specifications.domain.legal.raides.process;

import java.util.Collection;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.ulisboa.specifications.domain.legal.LegalReportContext;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblDiplomado;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityRegistrationInformation;

import com.google.common.base.Strings;

import net.fortuna.ical4j.model.parameter.Language;

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
        final RegistrationConclusionBean registrationConclusionBean = new RegistrationConclusionBean(registration);

        if (registrationConclusionBean.isConcluded()) {
            bean.setAnoLectivo(registrationConclusionBean.getConclusionYear().getQualifiedName());
            bean.setConcluiGrau(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));
            bean.setConclusaoMd(LegalMapping.find(report, LegalMappingType.BOOLEAN)
                    .translate(Raides.isMasterDegreeOrDoctoralDegree(registration)));
            bean.setNumInscConclusao(
                    String.valueOf(Raides.getEnrolmentYearsIncludingPrecedentRegistrations(registration).size()));

            if (Raides.isDoctoralDegree(registration)) {
                bean.setClassificacaoFinal(
                        LegalMapping.find(report, LegalMappingType.GRADE).translate(thesisFinalGrade(registration)));

            } else {
                bean.setClassificacaoFinal(LegalMapping.find(report, LegalMappingType.GRADE)
                        .translate(registrationConclusionBean.getCalculatedFinalGrade() != null ? registrationConclusionBean
                                .getCalculatedFinalGrade().getValue() : null));
            }

            bean.setDataDiploma(registrationConclusionBean.getConclusionDate().toLocalDate());

            // TODO
//            if (Raides.isMasterDegreeOrDoctoralDegree(registration)) {
//                bean.setClassificacaoFinalMd(LegalMapping.find(report, LegalMappingType.GRADE)
//                        .translate(((AverageCurriculum) registration.getCurriculum()).getScholarPartRoundedAverage().toString()));
//            }
            
        } else if (Raides.isMasterDegreeOrDoctoralDegree(registration)
                && Raides.hadScholarPartApprovement(registration, registration.getLastApprovementExecutionYear())) {
            bean.setAnoLectivo(registration.getLastApprovementExecutionYear().getQualifiedName());
            bean.setConcluiGrau(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(false));
            bean.setConclusaoMd(LegalMapping.find(report, LegalMappingType.BOOLEAN)
                    .translate(registration.isMasterDegreeOrBolonhaMasterDegree()));
            // TODO
//            bean.setClassificacaoFinalMd(LegalMapping.find(report, LegalMappingType.GRADE)
//                    .translate(((AverageCurriculum) registration.getCurriculum()).getScholarPartRoundedAverage().toString()));
        }

        bean.setMobilidadeCredito(LegalMapping.find(report, LegalMappingType.BOOLEAN)
                .translate(MobilityRegistrationInformation.hasBeenInMobility(registration, executionYear)));

        if (hasBeenInMobility(registration, executionYear)) {
            final MobilityRegistrationInformation mobilityRegistrationInformation =
                    MobilityRegistrationInformation.readAll(registration).iterator().next();
            bean.setMobilidadeCredito(LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(true));

            bean.setTipoMobilidadeCredito(LegalMapping.find(report, LegalMappingType.INTERNATIONAL_MOBILITY_ACTIVITY)
                    .translate(mobilityRegistrationInformation.getMobilityActivityType()));
            bean.setProgMobilidadeCredito(LegalMapping.find(report, LegalMappingType.INTERNATIONAL_MOBILITY_PROGRAM)
                    .translate(mobilityRegistrationInformation.getMobilityProgramType()));

            if (Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())
                    || Raides.ProgramaMobilidade.OUTRO_TRES.equals(bean.getProgMobilidadeCredito())) {
                bean.setOutroProgMobCredito(mobilityRegistrationInformation.getMobilityProgramType().getName().getContent());
            }

            if (mobilityRegistrationInformation.hasCountry()) {
                bean.setPaisMobilidadeCredito(mobilityRegistrationInformation.getCountry().getCode());
            }
        }

        preencheGrauPrecedentCompleto(bean, institutionUnit, executionYear, registration);

        validaClassificacao(executionYear, registration, bean);
        validaMobilidadeCredito(executionYear, registration, bean);

        return bean;
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

    private boolean hasBeenInMobility(Registration registration, ExecutionYear executionYear) {
        return !MobilityRegistrationInformation.readAll(registration).isEmpty();
    }

    protected void validaMobilidadeCredito(final ExecutionYear executionYear, final Registration registration,
            final TblDiplomado bean) {
        if (!MobilityRegistrationInformation.hasBeenInMobility(registration, executionYear)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getTipoMobilidadeCredito())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.graduated.mobility.credit.type.missing",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));
        }

        if (Strings.isNullOrEmpty(bean.getProgMobilidadeCredito())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.graduated.mobility.program.type.missing",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));
        }

        if ((Raides.ProgramaMobilidade.OUTRO_DOIS.equals(bean.getProgMobilidadeCredito())
                || Raides.ProgramaMobilidade.OUTRO_TRES.equals(bean.getProgMobilidadeCredito()))
                && Strings.isNullOrEmpty(bean.getOutroProgMobCredito())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.graduated.mobility.other.program.type.missing",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));
        }

        if (Strings.isNullOrEmpty(bean.getPaisMobilidadeCredito())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.graduated.mobility.country.missing",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));
        }
    }

    protected void validaClassificacao(final ExecutionYear executionYear, final Registration registration,
            final TblDiplomado bean) {
        final RegistrationConclusionBean registrationConclusionBean = new RegistrationConclusionBean(registration);

        if (registrationConclusionBean.isConcluded() && Raides.isMasterDegreeOrDoctoralDegree(registration)) {
            if (Strings.isNullOrEmpty(bean.getClassificacaoFinalMd()) || "0".equals(bean.getClassificacaoFinalMd())) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.masterOrDoctoral.scholarpart.classification.empty.or.zero",
                                String.valueOf(registration.getStudent().getNumber()),
                                registration.getDegreeNameWithDescription(), executionYear.getQualifiedName()));
                bean.markAsInvalid();
            }
        }
    }

}
