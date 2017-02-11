package org.fenixedu.ulisboa.specifications.domain.legal.raides.process;

import static org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides.formatArgs;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.LegalReportContext;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.TblIdentificacao;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.fenixedu.ulisboa.specifications.util.IdentityCardUtils;

import com.google.common.base.Strings;

public class IdentificacaoService extends RaidesService {

    public IdentificacaoService(final LegalReport report) {
        super(report);
    }

    public TblIdentificacao create(final Unit institution, final Student student, final Registration registration,
            final ExecutionYear executionYear) {
        TblIdentificacao bean = new TblIdentificacao();

        bean.setIdAluno(registration.getStudent().getNumber());
        bean.setNome(student.getName());

        bean.setNumId(student.getPerson().getDocumentIdNumber());

        if (student.getPerson().getIdDocumentType() != null) {
            bean.setTipoId(LegalMapping.find(report, LegalMappingType.ID_DOCUMENT_TYPE)
                    .translate(student.getPerson().getIdDocumentType()));
        }

        if (Raides.DocumentoIdentificacao.OUTRO.equals(student.getPerson().getIdDocumentType())) {
            bean.setTipoIdDescr(student.getPerson().getIdDocumentType().getLocalizedName());
        }

        bean.setCheckDigitId(student.getPerson().getIdentificationDocumentSeriesNumberValue());

        if (Strings.isNullOrEmpty(bean.getCheckDigitId())) {
            bean.setCheckDigitId(student.getPerson().getIdentificationDocumentExtraDigitValue());
        }

        if (Strings.isNullOrEmpty(bean.getCheckDigitId())
                && student.getPerson().getIdDocumentType() == IDDocumentType.IDENTITY_CARD) {
            // Try to generate digitControl from identity card
            try {
                int digitControl =
                        IdentityCardUtils.generateBilheteIdentidadeDigitControl(student.getPerson().getDocumentIdNumber());
                bean.setCheckDigitId(String.valueOf(digitControl));

                LegalReportContext.addWarn("",
                        i18n("warn.Raides.identity.card.digit.control.generated", formatArgs(registration, null)));
            } catch (NumberFormatException | ULisboaSpecificationsDomainException e) {
                LegalReportContext.addError("",
                        i18n("error.Raides.validation.cannot.generate.digit.control", formatArgs(registration, null)));
                bean.markAsInvalid();
            }
        }

        if (student.getPerson().getDateOfBirthYearMonthDay() != null) {
            bean.setDataNasc(student.getPerson().getDateOfBirthYearMonthDay().toLocalDate());
        }

        if (student.getPerson().getGender() != null) {
            bean.setSexo(LegalMapping.find(report, LegalMappingType.GENDER).translate(student.getPerson().getGender()));
        }

        if (student.getPerson().getCountry() != null) {
            bean.setNacionalidade(student.getPerson().getCountry().getCode());
        }

        final Country countryOfResidence = Raides.countryOfResidence(registration, executionYear);
        if (countryOfResidence != null) {
            bean.setResidePais(countryOfResidence.getCode());
        }

        bean.setPaisEnsinoSecundario(countryHighSchool(registration));

        validaPais(bean, institution, student, registration, executionYear);
        validaDocumentoIdentificacao(bean, institution, student, registration, executionYear);
        validaDataNascimento(bean, institution, student, registration, executionYear);

        return bean;
    }

    protected String countryHighSchool(final Registration registration) {
        final PrecedentDegreeInformation pid = registration.getStudentCandidacy().getPrecedentDegreeInformation();

        if (pid != null && pid.getSchoolLevel() == SchoolLevelType.HIGH_SCHOOL_OR_EQUIVALENT && pid.getCountry() != null) {
            return pid.getCountry().getCode();
        }

        if (registration.getPerson().getCountryHighSchool() != null) {
            return registration.getPerson().getCountryHighSchool().getCode();
        }

        if (registration.getStudentCandidacy().getPrecedentDegreeInformation().getCountryHighSchool() != null) {
            return registration.getStudentCandidacy().getPrecedentDegreeInformation().getCountryHighSchool().getCode();
        }

        if (registration.getPerson().getCountry() != null) {
            LegalReportContext.addWarn("",
                    i18n("warn.Raides.countryHighSchool.retrived.from.nationality", formatArgs(registration, null)));
            return registration.getPerson().getCountry().getCode();
        }

        return null;
    }

    protected void validaDataNascimento(final TblIdentificacao bean, final Unit institution, final Student student,
            final Registration registration, final ExecutionYear executionYear) {
        if (bean.getDataNasc() == null) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.birth.date.missing", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }
    }

    protected void validaDocumentoIdentificacao(final TblIdentificacao bean, final Unit institution, final Student student,
            final Registration registration, final ExecutionYear executionYear) {

        if (Strings.isNullOrEmpty(bean.getNumId())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.missing.document.id", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        } else if (bean.getNumId().matches(".*\\s.*")) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.document.id.contains.spaces", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        } else if (IDDocumentType.IDENTITY_CARD == registration.getPerson().getIdDocumentType()
                || IDDocumentType.CITIZEN_CARD == registration.getPerson().getIdDocumentType()) {
            if (!bean.getNumId().matches("\\d+")) {
                LegalReportContext.addError("", i18n("error.Raides.validation.national.document.id.contains.other.than.spaces",
                        formatArgs(registration, executionYear)));
                bean.markAsInvalid();
            }
        }
    }

    protected void validaPais(final TblIdentificacao bean, final Unit institution, final Student student,
            final Registration registration, final ExecutionYear executionYear) {
        if (Strings.isNullOrEmpty(bean.getResidePais())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.country.of.residence.incomplete", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getNacionalidade())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.nationality.incomplete", formatArgs(registration, executionYear)));
            bean.markAsInvalid();
        }

        if (Strings.isNullOrEmpty(bean.getPaisEnsinoSecundario())) {
            LegalReportContext.addError("",
                    i18n("error.Raides.validation.high.school.country.missing", formatArgs(registration, executionYear)));
        }
    }
}
