package org.fenixedu.ulisboa.specifications.domain.legal.raides.process;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.ulisboa.specifications.domain.legal.LegalReportContext;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblInscrito;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class InscritoService extends RaidesService {

    private static final BigDecimal MIN_NOTA_INGRESSO = new BigDecimal(95);
    private static final BigDecimal MAX_NOTA_INGRESSO = new BigDecimal(200);

    public InscritoService(final LegalReport report) {
        super(report);
    }

    public TblInscrito create(final RaidesRequestParameter raidesRequestParameter, final ExecutionYear executionYear,
            final Registration registration) {
        final Unit institutionUnit = raidesRequestParameter.getInstitution();

        final TblInscrito bean = new TblInscrito();
        bean.setRegistration(registration);

        preencheInformacaoMatricula(report, bean, institutionUnit, executionYear, registration);

        bean.setAnoCurricular(anoCurricular(registration, executionYear));
        bean.setPrimeiraVez(
                LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(isFirstTimeOnDegree(registration, executionYear)));

        bean.setRegimeFrequencia(regimeFrequencia(registration, executionYear));

        if (Raides.isDoctoralDegree(registration)) {
            bean.setEctsInscricao(doctoralEnrolledEcts(executionYear, registration));
        } else {
            bean.setEctsInscricao(enrolledEcts(executionYear, registration));
        }

        bean.setEctsAcumulados(registration.getStudentCurricularPlan(executionYear).getRoot().getCurriculum(executionYear)
                .getSumEctsCredits().setScale(1));

        bean.setTempoParcial(
                LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(isInPartialRegime(executionYear, registration)));
        bean.setBolseiro(bolseiro(registration, executionYear));

        if (isFirstCycle(executionYear, registration) && isFirstTimeOnDegree(registration, executionYear)) {
            bean.setFormaIngresso(LegalMapping.find(report, LegalMappingType.REGISTRATION_INGRESSION_TYPE)
                    .translate(registration.getIngressionType()));

            if (isDegreeChangeOrTransfer(raidesRequestParameter, registration)) {
                final PrecedentDegreeInformation precedentQualification =
                        registration.getStudentCandidacy().getPrecedentDegreeInformation();
                if (precedentQualification != null && precedentQualification.getPrecedentInstitution() != null
                        && !Strings.isNullOrEmpty(precedentQualification.getPrecedentInstitution().getCode())) {
                    bean.setEstabInscricaoAnt(precedentQualification.getPrecedentInstitution().getCode());
                } else if (precedentQualification != null && precedentQualification.getPrecedentInstitution() != null) {
                    bean.setEstabInscricaoAnt(Raides.Cursos.OUTRO);
                    bean.setOutroEstabInscAnt(precedentQualification.getPrecedentInstitution().getNameI18n().getContent());
                }

                bean.setNumInscCursosAnt(precedentQualification.getNumberOfEnrolmentsInPreviousDegrees());

            } else if (isGeneralAccessRegime(raidesRequestParameter, registration)) {
                if (registration.getStudentCandidacy().getEntryGrade() != null) {
                    bean.setNotaIngresso(registration.getStudentCandidacy().getEntryGrade().toString());
                }

                if (registration.getStudentCandidacy().getPlacingOption() != null) {
                    bean.setOpcaoIngresso(registration.getStudentCandidacy().getPlacingOption().toString());
                }
            }
        }

        if (!registration.isFirstTime(executionYear)) {
            final Collection<ExecutionYear> enrolmentsExecutionYears =
                    Lists.newArrayList(registration.getEnrolmentsExecutionYears());

            for (ExecutionYear it = executionYear; it != null; it = it.getNextExecutionYear()) {
                enrolmentsExecutionYears.remove(it);
            }

            if (enrolmentsExecutionYears.size() >= 1) {
                bean.setAnoUltimaInscricao(
                        Collections.max(enrolmentsExecutionYears, ExecutionYear.COMPARATOR_BY_YEAR).getQualifiedName());
            }

            bean.setNumInscNesteCurso(numberOfYearsEnrolled(executionYear, registration));
        }

        bean.setEstudanteTrabalhador(
                LegalMapping.find(report, LegalMappingType.BOOLEAN).translate(isWorkingStudent(registration, executionYear)));

        preencheInformacaoPessoal(executionYear, registration, bean);
        preencheGrauPrecedentCompleto(bean, institutionUnit, executionYear, registration);

        validaInformacaoMudancaCursoTransferencia(raidesRequestParameter, bean, institutionUnit, executionYear, registration);
        validaInformacaoRegimeGeralAcesso(raidesRequestParameter, bean, institutionUnit, executionYear, registration);
        validaNumInscricoesNoCurso(raidesRequestParameter, bean, institutionUnit, executionYear, registration);
        
        return bean;
    }

    /*
     * VALIDACOES
     */

    private void validaNumInscricoesNoCurso(final RaidesRequestParameter raidesRequestParameter, final TblInscrito bean, final Unit institutionUnit,
            final ExecutionYear executionYear, final Registration registration) {
        
        if(!isFirstTimeOnDegree(registration, executionYear) && new Integer(0).equals(bean.getNumInscNesteCurso())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.is.not.first.time.student.but.number.previous.enrolments.in.registration.is.zero",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));            
            bean.markAsInvalid();
        }
        
    }

    protected void validaInformacaoRegimeGeralAcesso(final RaidesRequestParameter raidesRequestParameter, final TblInscrito bean,
            final Unit institutionUnit, final ExecutionYear executionYear, final Registration registration) {
        if (!isFirstCycle(executionYear, registration) || !isFirstTimeOnDegree(registration, executionYear)) {
            return;
        }

        if (!isGeneralAccessRegime(raidesRequestParameter, registration)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getNotaIngresso()) || Strings.isNullOrEmpty(bean.getOpcaoIngresso())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.general.access.regime.incomplete",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));
            bean.markAsInvalid();
            return;
        }
        
        if(!Strings.isNullOrEmpty(bean.getNotaIngresso())) {
            try {
                final BigDecimal value = new BigDecimal(bean.getNotaIngresso());
                if(value.compareTo(MIN_NOTA_INGRESSO) < 0 || value.compareTo(MAX_NOTA_INGRESSO) > 0) {
                    LegalReportContext.addError("",
                            i18n("error.Raides.validation.general.access.regime.notaIngresso.in.wrong.interval",
                                    String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                                    executionYear.getQualifiedName()));                    
                    bean.markAsInvalid();
                    return;                
                }
            } catch(NumberFormatException e) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.general.access.regime.notaIngresso.wrong.format",
                                String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                                executionYear.getQualifiedName()));
                bean.markAsInvalid();
                return;                
            }
        }
    }

    protected void validaInformacaoMudancaCursoTransferencia(final RaidesRequestParameter raidesRequestParameter,
            final TblInscrito bean, final Unit institutionUnit, final ExecutionYear executionYear,
            final Registration registration) {
        if (!isFirstCycle(executionYear, registration) || !isFirstTimeOnDegree(registration, executionYear)) {
            return;
        }

        if (!isDegreeChangeOrTransfer(raidesRequestParameter, registration)) {
            return;
        }

        if (Strings.isNullOrEmpty(bean.getEstabInscricaoAnt())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.degree.change.or.transfer.requires.information",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));
            bean.markAsInvalid();
        } else if (Raides.Estabelecimentos.OUTRO.equals(bean.getEstabInscricaoAnt())
                && Strings.isNullOrEmpty(bean.getOutroEstabInscAnt())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.degree.change.or.transfer.requires.information",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));
            bean.markAsInvalid();
        }

        if (bean.getNumInscCursosAnt() == null) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.degree.change.or.transfer.requires.information",
                            String.valueOf(registration.getStudent().getNumber()), registration.getDegreeNameWithDescription(),
                            executionYear.getQualifiedName()));
            bean.markAsInvalid();
        }
    }

    protected boolean isFirstCycle(final ExecutionYear executionYear, final Registration registration) {
        return registration.getCycleType(executionYear) == CycleType.FIRST_CYCLE;
    }

    protected String bolseiro(final Registration registration, final ExecutionYear executionYear) {
        final PersonalIngressionData pid = registration.getStudent().getPersonalIngressionDataByExecutionYear(executionYear);
        if (pid == null) {
            return LegalMapping.find(report, LegalMappingType.GRANT_OWNER_TYPE).translate(Raides.Bolseiro.NAO_BOLSEIRO);
        }
        
        if(pid.getGrantOwnerType() == null) {
            return LegalMapping.find(report, LegalMappingType.GRANT_OWNER_TYPE).translate(Raides.Bolseiro.NAO_BOLSEIRO);
        }

        return LegalMapping.find(report, LegalMappingType.GRANT_OWNER_TYPE).translate(pid.getGrantOwnerType());
    }

    protected boolean isWorkingStudent(final Registration registration, final ExecutionYear executionYear) {
        boolean result = false;

        for (final ExecutionSemester executionSemester : executionYear.getExecutionPeriodsSet()) {
            result |= registration.getStudent().hasWorkingStudentStatuteInPeriod(executionSemester);
        }

        return result;
    }

    protected boolean isInPartialRegime(final ExecutionYear executionYear, final Registration registration) {
        return registration.isPartialRegime(executionYear);
    }

    private boolean isGeneralAccessRegime(final RaidesRequestParameter raidesRequestParameter, final Registration registration) {
        return Raides.isGeneralAccessRegime(raidesRequestParameter, registration.getIngressionType());
    }

    protected boolean isDegreeChangeOrTransfer(final RaidesRequestParameter raidesRequestParameter,
            final Registration registration) {
        return Raides.isDegreeChange(raidesRequestParameter, registration.getIngressionType())
                || Raides.isDegreeTransfer(raidesRequestParameter, registration.getIngressionType());
    }

    protected Integer numberOfYearsEnrolled(final ExecutionYear executionYear, final Registration registration) {
        return Raides.getEnrolmentYearsIncludingPrecedentRegistrations(registration, executionYear.getPreviousExecutionYear())
                .size();
    }

    protected boolean deliveredDissertation(final ExecutionYear executionYear, final Registration registration) {
        final StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);

        Collection<Enrolment> dissertationEnrolments = studentCurricularPlan.getDissertationEnrolments();

        for (final Enrolment enrolment : dissertationEnrolments) {
            if (enrolment.isValid(executionYear) && enrolment.isApproved()) {
                return true;
            }
        }

        return false;
    }

}
