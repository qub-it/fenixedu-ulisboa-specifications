package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.DomainObjectLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.EnumerationLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.StringLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.IntegratedMasterFirstCycleGraduatedReportOption;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityActivityType;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityProgramType;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Sets;

public class CreateRaidesInstanceCustomTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        createRaidesReport();
        createAnoCurricularMapping();
        createBooleanoMapping();
        createEscolaridadeAnteriorMapping();
        createEstadoCivilMapping();
        createGeneroMapping();
        createIngressionMapping();
        createNivelFormacaoCursoEstrangeiroMapping();
        createNivelEscolarMapping();
        createNotaMapping();
        createPeriodoDeEstudosMapping();
        createProgramaMobilidadeMapping();
        createProgramaMobilidadeMapeadoAcordoMapping();
        createRegimeFrequenciaMapping();
        createSituacaoProfissionalMapping();
        createTipoDocumentoMapping();
        createInstituicaoEnsinoMapping();
        createTipoProfissaoMapping();
        createTipoProgramaMobilidadeMapping();
        createTipoBolsaMapping();
    }

    private void createTipoBolsaMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.GRANT_OWNER_TYPE) == null) {
            EnumerationLegalMapping legalMapping = (EnumerationLegalMapping) LegalMapping
                    .create(LegalMappingType.GRANT_OWNER_TYPE, RaidesInstance.getInstance());

            legalMapping.addEntry(GrantOwnerType.FCT_GRANT_OWNER, "12");
            legalMapping.addEntry(GrantOwnerType.HIGHER_EDUCATION_NOT_SAS_GRANT_OWNER, "16");
            legalMapping.addEntry(GrantOwnerType.HIGHER_EDUCATION_SAS_GRANT_OWNER, "15");
            legalMapping.addEntry(GrantOwnerType.HIGHER_EDUCATION_SAS_GRANT_OWNER_CANDIDATE, "11");
            legalMapping.addEntry(GrantOwnerType.ORIGIN_COUNTRY_GRANT_OWNER, "13");
            legalMapping.addEntry(GrantOwnerType.OTHER_INSTITUTION_GRANT_OWNER, "14");
            legalMapping.addEntry(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP, "10");
        }
    }

    private void createTipoProgramaMobilidadeMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.INTERNATIONAL_MOBILITY_ACTIVITY) == null) {
            final DomainObjectLegalMapping legalMapping = (DomainObjectLegalMapping) LegalMapping
                    .create(LegalMappingType.INTERNATIONAL_MOBILITY_ACTIVITY, RaidesInstance.getInstance());

            legalMapping.addEntry(MobilityActivityType.findByCode("1"), "1");
            legalMapping.addEntry(MobilityActivityType.findByCode("2"), "2");
        }
    }

    private void createTipoProfissaoMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.PROFESSION_TYPE) == null) {
            final EnumerationLegalMapping legalMapping =
                    (EnumerationLegalMapping) LegalMapping.create(LegalMappingType.PROFESSION_TYPE, RaidesInstance.getInstance());

            legalMapping.addEntry(ProfessionType.ADMINISTRATIVE_STAFF_AND_SIMMILAR, "13");
            legalMapping.addEntry(ProfessionType.CIENTIFIC_AND_INTELECTUAL_PROFESSION_SPECIALIST, "11");
            legalMapping.addEntry(ProfessionType.FARMERS_AND_AGRICULTURE_AND_FISHING_QUALIFIED_WORKERS, "15");
            legalMapping.addEntry(ProfessionType.INSTALLATION_AND_MACHINE_WORKERS_AND_LINE_ASSEMBLY_WORKERS, "17");
            legalMapping.addEntry(ProfessionType.INTERMEDIATE_LEVEL_TECHNICALS_AND_PROFESSIONALS, "12");
            legalMapping.addEntry(ProfessionType.MILITARY_MEMBER, "19");
            legalMapping.addEntry(ProfessionType.NON_QUALIFIED_WORKERS, "18");
            legalMapping.addEntry(ProfessionType.OTHER, "20");
            legalMapping.addEntry(ProfessionType.PUBLIC_ADMINISTRATION_BOARD_OR_DIRECTOR_AND_BOARD_OF_COMPANIES, "10");
            legalMapping.addEntry(ProfessionType.SALES_AND_SERVICE_STAFF, "14");
            legalMapping.addEntry(ProfessionType.UNKNOWN, "21");
            legalMapping.addEntry(ProfessionType.WORKERS_CRAFTSMEN_AND_SIMMILAR, "16");
        }
    }

    private void createInstituicaoEnsinoMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.HIGH_SCHOOL_TYPE) == null) {
            final EnumerationLegalMapping legalMapping = (EnumerationLegalMapping) LegalMapping
                    .create(LegalMappingType.HIGH_SCHOOL_TYPE, RaidesInstance.getInstance());

            legalMapping.addEntry(AcademicalInstitutionType.NATIONAL_PRIVATE_INSTITUTION, "1");
            legalMapping.addEntry(AcademicalInstitutionType.PRIVATE_AND_PUBLIC_HIGH_SCHOOL, "3");
            legalMapping.addEntry(AcademicalInstitutionType.PRIVATE_HIGH_SCHOOL, "2");
        }
    }

    private void createTipoDocumentoMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.ID_DOCUMENT_TYPE) == null) {
            final EnumerationLegalMapping legalMapping = (EnumerationLegalMapping) LegalMapping
                    .create(LegalMappingType.ID_DOCUMENT_TYPE, RaidesInstance.getInstance());

            legalMapping.addEntry(IDDocumentType.AIR_FORCE_IDENTITY_CARD, "7");
            legalMapping.addEntry(IDDocumentType.CITIZEN_CARD, "1");
            legalMapping.addEntry(IDDocumentType.EXTERNAL, "7");
            legalMapping.addEntry(IDDocumentType.FOREIGNER_IDENTITY_CARD, "4");
            legalMapping.addEntry(IDDocumentType.IDENTITY_CARD, "1");
            legalMapping.addEntry(IDDocumentType.MILITARY_IDENTITY_CARD, "7");
            legalMapping.addEntry(IDDocumentType.NATIVE_COUNTRY_IDENTITY_CARD, "4");
            legalMapping.addEntry(IDDocumentType.NAVY_IDENTITY_CARD, "7");
            legalMapping.addEntry(IDDocumentType.OTHER, "7");
            legalMapping.addEntry(IDDocumentType.PASSPORT, "2");
            legalMapping.addEntry(IDDocumentType.RESIDENCE_AUTHORIZATION, "3");
        }
    }

    private void createSituacaoProfissionalMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.PROFESSIONAL_SITUATION_CONDITION) == null) {
            final EnumerationLegalMapping legalMapping = (EnumerationLegalMapping) LegalMapping
                    .create(LegalMappingType.PROFESSIONAL_SITUATION_CONDITION, RaidesInstance.getInstance());

            legalMapping.addEntry(ProfessionalSituationConditionType.EMPLOYEER, "11");
            legalMapping.addEntry(ProfessionalSituationConditionType.GRANT_HOLDER, "18");
            legalMapping.addEntry(ProfessionalSituationConditionType.HOUSEWIFE, "16");
            legalMapping.addEntry(ProfessionalSituationConditionType.INDEPENDENT_WORKER, "12");
            legalMapping.addEntry(ProfessionalSituationConditionType.INTERN, "18");
            legalMapping.addEntry(ProfessionalSituationConditionType.MILITARY_SERVICE, "18");
            legalMapping.addEntry(ProfessionalSituationConditionType.OTHER, "18");
            legalMapping.addEntry(ProfessionalSituationConditionType.RETIRED, "14");
            legalMapping.addEntry(ProfessionalSituationConditionType.STUDENT, "17");
            legalMapping.addEntry(ProfessionalSituationConditionType.UNEMPLOYED, "15");
            legalMapping.addEntry(ProfessionalSituationConditionType.UNKNOWN, "18");
            legalMapping.addEntry(ProfessionalSituationConditionType.WORKS_FOR_FAMILY_WITHOUT_PAYMENT, "13");
            legalMapping.addEntry(ProfessionalSituationConditionType.WORKS_FOR_OTHERS, "10");
        }
    }

    private void createRegimeFrequenciaMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.REGIME_FREQUENCIA) == null) {
            final StringLegalMapping legalMapping =
                    (StringLegalMapping) LegalMapping.create(LegalMappingType.REGIME_FREQUENCIA, RaidesInstance.getInstance());

            legalMapping.addEntry(Raides.RegimeFrequencia.ETD_CODE, "16");
        }
    }

    private void createProgramaMobilidadeMapeadoAcordoMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.INTERNATIONAL_MOBILITY_PROGRAM_AGREEMENT) == null) {
            final DomainObjectLegalMapping legalMapping = (DomainObjectLegalMapping) LegalMapping
                    .create(LegalMappingType.INTERNATIONAL_MOBILITY_PROGRAM_AGREEMENT, RaidesInstance.getInstance());

            //legacy: mobility information now has programtype
//            legalMapping.addEntry(Raides.findRegistrationProtocolByCode("ERASMUS"), "1");
        }
    }

    private void createProgramaMobilidadeMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.INTERNATIONAL_MOBILITY_PROGRAM) == null) {
            final DomainObjectLegalMapping legalMapping = (DomainObjectLegalMapping) LegalMapping
                    .create(LegalMappingType.INTERNATIONAL_MOBILITY_PROGRAM, RaidesInstance.getInstance());

            legalMapping.addEntry(MobilityProgramType.findByCode("ERASMUS"), "1");
        }
    }

    private void createPeriodoDeEstudosMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.SCHOOL_PERIOD_DURATION) == null) {
            final EnumerationLegalMapping legalMapping = (EnumerationLegalMapping) LegalMapping
                    .create(LegalMappingType.SCHOOL_PERIOD_DURATION, RaidesInstance.getInstance());

            legalMapping.addEntry(SchoolPeriodDuration.SEMESTER, "S");
            legalMapping.addEntry(SchoolPeriodDuration.TRIMESTER, "T");
            legalMapping.addEntry(SchoolPeriodDuration.YEAR, "A");
        }
    }

    private void createNotaMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.GRADE) == null) {
            final StringLegalMapping legalMapping =
                    (StringLegalMapping) LegalMapping.create(LegalMappingType.GRADE, RaidesInstance.getInstance());

            legalMapping.addEntry("10", "10");
            legalMapping.addEntry("11", "11");
            legalMapping.addEntry("12", "12");
            legalMapping.addEntry("13", "13");
            legalMapping.addEntry("14", "14");
            legalMapping.addEntry("15", "15");
            legalMapping.addEntry("16", "16");
            legalMapping.addEntry("17", "17");
            legalMapping.addEntry("18", "18");
            legalMapping.addEntry("19", "19");
            legalMapping.addEntry("20", "20");

            LETRAS_QualitativeGrades(legalMapping);
            FARMACIA_QualitativeGrades(legalMapping);
            VETERINARIA_QualitativeGrades(legalMapping);
            DENTARIA_QualitativeGrades(legalMapping);
            REITORIA_QualitativeGrades(legalMapping);

        }
    }

    private void REITORIA_QualitativeGrades(final StringLegalMapping legalMapping) {
        final FinantialInstitution finantialInstitution = FinantialInstitution.findAll().findFirst().get();
        if (!"510739024".equals(finantialInstitution.getFiscalNumber())) {
            return;
        }

        final String BOM_DISTINCAO = "BD";
        final String DISTINCAO = "D";
        final String BOM = "B";
        final String MUITO_BOM = "MB";
        final String DISTINCAO_LOUVOR = "DL";
        final String APROVADO = "A";
        final String SUFICIENTE = "SU";
        final String EXCELENTE = "E";
        final String CREDITACAO = "C";

        legalMapping.addEntry(BOM, "21");
        legalMapping.addEntry(BOM_DISTINCAO, "22");
        legalMapping.addEntry(MUITO_BOM, "23");
        legalMapping.addEntry(EXCELENTE, "24");
        legalMapping.addEntry(APROVADO, "25");
        legalMapping.addEntry(DISTINCAO, "26");
        legalMapping.addEntry(DISTINCAO_LOUVOR, "27");
        legalMapping.addEntry(SUFICIENTE, "28");

    }

    private void DENTARIA_QualitativeGrades(final StringLegalMapping legalMapping) {
        final FinantialInstitution finantialInstitution = FinantialInstitution.findAll().findFirst().get();
        if (!"503013366".equals(finantialInstitution.getFiscalNumber())) {
            return;
        }

        final String NAO_APTO = "NAPT";
        final String APTO = "APT";
        final String INSUFICIENTE = "I";
        final String SUFICIENTE = "SU";
        final String BOM = "B";
        final String MUITO_BOM = "MB";
        final String BOM_COM_DISTINCAO = "BD";
        final String COM_APROVEITAMENTO = "CA";
        final String DISTINCAO_LOUVOR = "DL";
        final String APROVADO = "A";

        legalMapping.addEntry(BOM, "21");
        legalMapping.addEntry(BOM_COM_DISTINCAO, "22");
        legalMapping.addEntry(MUITO_BOM, "23");
        legalMapping.addEntry(APROVADO, "25");
        legalMapping.addEntry(DISTINCAO_LOUVOR, "27");
        legalMapping.addEntry(SUFICIENTE, "28");
    }

    private void VETERINARIA_QualitativeGrades(final StringLegalMapping legalMapping) {
        final FinantialInstitution finantialInstitution = FinantialInstitution.findAll().findFirst().get();
        if (!"502286326".equals(finantialInstitution.getFiscalNumber())) {
            return;
        }

        final String BOM = "B";
        final String MUITO_BOM = "MB";
        final String SUFICIENTE = "SU";
        final String BOM_COM_DISTINCAO = "BD";
        final String MUITO_BOM_DISTINCAO = "MBD";
        final String APROVADO_MUITO_BOM_DISTINCAO = "AMBD";
        final String APROVADO_MUITO_BOM = "AMB";
        final String APROVADO = "A";
        final String APROVADO_DISTINCAO_LOUVOR = "ADL";

        legalMapping.addEntry(BOM, "21");
        legalMapping.addEntry(BOM_COM_DISTINCAO, "22");
        legalMapping.addEntry(MUITO_BOM, "23");
        legalMapping.addEntry(APROVADO, "25");
        legalMapping.addEntry(APROVADO_DISTINCAO_LOUVOR, "27");
        legalMapping.addEntry(SUFICIENTE, "28");
    }

    private void FARMACIA_QualitativeGrades(final StringLegalMapping legalMapping) {
        final FinantialInstitution finantialInstitution = FinantialInstitution.findAll().findFirst().get();
        if (!"502659807".equals(finantialInstitution.getFiscalNumber())) {
            return;
        }

        final String INSUFICIENTE = "I";
        final String SUFICIENTE = "SU";
        final String BOM = "B";
        final String MUITO_BOM = "MB";
        final String MUITO_BOM_DISTINCAO_LOUVOR = "MBDL";
        final String BOM_COM_DISTINCAO = "BD";
        final String EXCELENTE = "E";
        final String RECUSADO = "R";
        final String APROVADO_COM_DISTINCAO = "AD";
        final String APROVADO_COM_DISTINCAO_LOUVOR = "ADL";
        final String APROVADO = "A";
        final String APROVADO_COM_MUITO_BOM = "AMB";

        legalMapping.addEntry(BOM, "21");
        legalMapping.addEntry(BOM_COM_DISTINCAO, "22");
        legalMapping.addEntry(MUITO_BOM, "23");
        legalMapping.addEntry(EXCELENTE, "24");
        legalMapping.addEntry(APROVADO, "25");
        legalMapping.addEntry(APROVADO_COM_DISTINCAO, "26");
        legalMapping.addEntry(APROVADO_COM_DISTINCAO_LOUVOR, "27");
        legalMapping.addEntry(SUFICIENTE, "28");
        legalMapping.addEntry(MUITO_BOM_DISTINCAO_LOUVOR, "29");
    }

    private void LETRAS_QualitativeGrades(final StringLegalMapping legalMapping) {
        final FinantialInstitution finantialInstitution = FinantialInstitution.findAll().findFirst().get();
        if (!"502657456".equals(finantialInstitution.getFiscalNumber())) {
            return;
        }

        final String INSUFIENTE = "I";
        final String SUFICIENTE = "SU";
        final String BOM = "B";
        final String MUITO_BOM = "MB";
        final String EXCELENTE = "E";
        final String NOTA_QUALITATIVA = "NQ";
        final String MAU = "MA";
        final String MEDIOCRE = "ME";
        final String SATISFAZ = "SA";
        final String BOM_COM_DISTINCAO = "BD";
        final String APROVADO = "A";
        final String REPROVADO = "RE";
        final String APROVADO_COM_DISTINCAO_E_LOUVOR = "ADL";
        final String APROVADO_COM_DISTINCAO = "AD";

        legalMapping.addEntry(BOM, "21");
        legalMapping.addEntry(BOM_COM_DISTINCAO, "22");
        legalMapping.addEntry(MUITO_BOM, "23");
        legalMapping.addEntry(EXCELENTE, "24");
        legalMapping.addEntry(APROVADO, "25");
        legalMapping.addEntry(APROVADO_COM_DISTINCAO, "26");
        legalMapping.addEntry(APROVADO_COM_DISTINCAO_E_LOUVOR, "27");
        legalMapping.addEntry(SUFICIENTE, "28");

    }

    private void createNivelEscolarMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.SCHOOL_LEVEL) == null) {
            final EnumerationLegalMapping legalMapping =
                    (EnumerationLegalMapping) LegalMapping.create(LegalMappingType.SCHOOL_LEVEL, RaidesInstance.getInstance());

            legalMapping.addEntry(SchoolLevelType.BACHELOR_DEGREE, "18");
            legalMapping.addEntry(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA, "18");
            legalMapping.addEntry(SchoolLevelType.DEGREE, "19");
            legalMapping.addEntry(SchoolLevelType.DEGREE_PRE_BOLOGNA, "19");
            legalMapping.addEntry(SchoolLevelType.DEGREE_TERMINAL_PART, "19");
            legalMapping.addEntry(SchoolLevelType.DOCTORATE_DEGREE, "21");
            legalMapping.addEntry(SchoolLevelType.DOCTORATE_DEGREE_PRE_BOLOGNA, "21");
            legalMapping.addEntry(SchoolLevelType.DONT_KNOW_HOW_TO_READ_OR_WRITE, "10");
            legalMapping.addEntry(SchoolLevelType.FIRST_CYCLE_BASIC_SCHOOL, "12");
            legalMapping.addEntry(SchoolLevelType.HIGH_SCHOOL_OR_EQUIVALENT, "15");
            legalMapping.addEntry(SchoolLevelType.KNOWS_HOW_TO_READ_WITHOUT_OLD_FOURTH_YEAR, "11");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE, "20");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE_INTEGRATED, "20");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE_PRE_BOLOGNA, "20");
            legalMapping.addEntry(SchoolLevelType.MEDIUM_EDUCATION, "16");
            legalMapping.addEntry(SchoolLevelType.OTHER, "22");
            legalMapping.addEntry(SchoolLevelType.OTHER_SITUATION, "22");
            legalMapping.addEntry(SchoolLevelType.SECOND_CYCLE_BASIC_SCHOOL, "13");
            legalMapping.addEntry(SchoolLevelType.TECHNICAL_SPECIALIZATION, "17");
            legalMapping.addEntry(SchoolLevelType.THIRD_CYCLE_BASIC_SCHOOL, "14");
            legalMapping.addEntry(SchoolLevelType.UNKNOWN, "22");
        }
    }

    private void createNivelFormacaoCursoEstrangeiroMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.MOBILITY_SCHOOL_LEVEL) == null) {
            final EnumerationLegalMapping legalMapping = (EnumerationLegalMapping) LegalMapping
                    .create(LegalMappingType.MOBILITY_SCHOOL_LEVEL, RaidesInstance.getInstance());

            legalMapping.addEntry(SchoolLevelType.BACHELOR_DEGREE, "4");
            legalMapping.addEntry(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA, "4");
            legalMapping.addEntry(SchoolLevelType.DEGREE, "1");
            legalMapping.addEntry(SchoolLevelType.DEGREE_PRE_BOLOGNA, "4");
            legalMapping.addEntry(SchoolLevelType.DEGREE_TERMINAL_PART, "4");
            legalMapping.addEntry(SchoolLevelType.DOCTORATE_DEGREE, "4");
            legalMapping.addEntry(SchoolLevelType.DOCTORATE_DEGREE_PRE_BOLOGNA, "3");
            legalMapping.addEntry(SchoolLevelType.DONT_KNOW_HOW_TO_READ_OR_WRITE, "4");
            legalMapping.addEntry(SchoolLevelType.FIRST_CYCLE_BASIC_SCHOOL, "4");
            legalMapping.addEntry(SchoolLevelType.HIGH_SCHOOL_OR_EQUIVALENT, "4");
            legalMapping.addEntry(SchoolLevelType.KNOWS_HOW_TO_READ_WITHOUT_OLD_FOURTH_YEAR, "4");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE, "2");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE_INTEGRATED, "2");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE_PRE_BOLOGNA, "4");
            legalMapping.addEntry(SchoolLevelType.MEDIUM_EDUCATION, "4");
            legalMapping.addEntry(SchoolLevelType.OTHER, "4");
            legalMapping.addEntry(SchoolLevelType.OTHER_SITUATION, "4");
            legalMapping.addEntry(SchoolLevelType.SECOND_CYCLE_BASIC_SCHOOL, "4");
            legalMapping.addEntry(SchoolLevelType.TECHNICAL_SPECIALIZATION, "4");
            legalMapping.addEntry(SchoolLevelType.THIRD_CYCLE_BASIC_SCHOOL, "4");
            legalMapping.addEntry(SchoolLevelType.UNKNOWN, "4");
        }
    }

    private void createIngressionMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.REGISTRATION_INGRESSION_TYPE) == null) {
            final DomainObjectLegalMapping legalMapping = (DomainObjectLegalMapping) LegalMapping
                    .create(LegalMappingType.REGISTRATION_INGRESSION_TYPE, RaidesInstance.getInstance());

            for (final IngressionType ingressionType : Bennu.getInstance().getIngressionTypesSet()) {
                if (ingressionType.isDirectAccessFrom1stCycle() || ingressionType.isInternal2ndCycleAccess()
                        || ingressionType.isMiddleAndSuperiorCourses()) {
                    legalMapping.addEntry(ingressionType.getExternalId(), "14");
                } else if (ingressionType.isExternalDegreeChange()) {
                    legalMapping.addEntry(ingressionType.getExternalId(), "12");
                } else if (ingressionType.isTransfer()) {
                    legalMapping.addEntry(ingressionType.getExternalId(), "11");
                } else if (ingressionType.isOver23()) {
                    legalMapping.addEntry(ingressionType.getExternalId(), "16");
                } else {
                    legalMapping.addEntry(ingressionType.getExternalId(), "10");
                }
            }
        }
    }

    private void createGeneroMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.GENDER) == null) {
            final EnumerationLegalMapping legalMapping =
                    (EnumerationLegalMapping) LegalMapping.create(LegalMappingType.GENDER, RaidesInstance.getInstance());

            legalMapping.addEntry(Gender.FEMALE, "F");
            legalMapping.addEntry(Gender.MALE, "M");
        }
    }

    private void createEstadoCivilMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.MARITAL_STATUS) == null) {
            final EnumerationLegalMapping legalMapping =
                    (EnumerationLegalMapping) LegalMapping.create(LegalMappingType.MARITAL_STATUS, RaidesInstance.getInstance());

            legalMapping.addEntry(MaritalStatus.CIVIL_UNION, "3");
            legalMapping.addEntry(MaritalStatus.DIVORCED, "4");
            legalMapping.addEntry(MaritalStatus.MARRIED, "2");
            legalMapping.addEntry(MaritalStatus.SEPARATED, "5");
            legalMapping.addEntry(MaritalStatus.SINGLE, "1");
            // legalMapping.addEntry(MaritalStatus.UNKNOWN, "");
            legalMapping.addEntry(MaritalStatus.WIDOWER, "6");
        }
    }

    private void createEscolaridadeAnteriorMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.PRECEDENT_SCHOOL_LEVEL) == null) {
            final EnumerationLegalMapping legalMapping = (EnumerationLegalMapping) LegalMapping
                    .create(LegalMappingType.PRECEDENT_SCHOOL_LEVEL, RaidesInstance.getInstance());

            legalMapping.addEntry(SchoolLevelType.BACHELOR_DEGREE, "15");
            legalMapping.addEntry(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA, "16");
            legalMapping.addEntry(SchoolLevelType.DEGREE, "16");
            legalMapping.addEntry(SchoolLevelType.DEGREE_PRE_BOLOGNA, "16");
            legalMapping.addEntry(SchoolLevelType.DEGREE_TERMINAL_PART, "16");
            legalMapping.addEntry(SchoolLevelType.DOCTORATE_DEGREE, "18");
            legalMapping.addEntry(SchoolLevelType.DOCTORATE_DEGREE_PRE_BOLOGNA, "18");
            legalMapping.addEntry(SchoolLevelType.DONT_KNOW_HOW_TO_READ_OR_WRITE, "19");
            legalMapping.addEntry(SchoolLevelType.FIRST_CYCLE_BASIC_SCHOOL, "10");
            legalMapping.addEntry(SchoolLevelType.HIGH_SCHOOL_OR_EQUIVALENT, "13");
            legalMapping.addEntry(SchoolLevelType.KNOWS_HOW_TO_READ_WITHOUT_OLD_FOURTH_YEAR, "19");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE, "17");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE_INTEGRATED, "17");
            legalMapping.addEntry(SchoolLevelType.MASTER_DEGREE_PRE_BOLOGNA, "17");
            legalMapping.addEntry(SchoolLevelType.MEDIUM_EDUCATION, "19");
            legalMapping.addEntry(SchoolLevelType.OTHER, "19");
            legalMapping.addEntry(SchoolLevelType.OTHER_SITUATION, "19");
            legalMapping.addEntry(SchoolLevelType.SECOND_CYCLE_BASIC_SCHOOL, "11");
            legalMapping.addEntry(SchoolLevelType.TECHNICAL_SPECIALIZATION, "20");
            legalMapping.addEntry(SchoolLevelType.THIRD_CYCLE_BASIC_SCHOOL, "12");
            legalMapping.addEntry(SchoolLevelType.UNKNOWN, "19");
        }
    }

    private void createBooleanoMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.BOOLEAN) == null) {
            final StringLegalMapping legalMapping =
                    (StringLegalMapping) LegalMapping.create(LegalMappingType.BOOLEAN, RaidesInstance.getInstance());

            legalMapping.addEntry(Boolean.TRUE.toString(), "true");
            legalMapping.addEntry(Boolean.FALSE.toString(), "false");
        }
    }

    private void createAnoCurricularMapping() {
        if (LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.CURRICULAR_YEAR) == null) {
            final StringLegalMapping legalMapping =
                    (StringLegalMapping) LegalMapping.create(LegalMappingType.CURRICULAR_YEAR, RaidesInstance.getInstance());

            legalMapping.addEntry("1", "10");
            legalMapping.addEntry("2", "11");
            legalMapping.addEntry("3", "12");
            legalMapping.addEntry("4", "13");
            legalMapping.addEntry("5", "14");
            legalMapping.addEntry("6", "15");
            legalMapping.addEntry("7", "16");
            legalMapping.addEntry(Raides.AnoCurricular.ESTAGIO_FINAL_CODE, "17");
            legalMapping.addEntry(Raides.AnoCurricular.TRABALHO_PROJECTO_CODE, "18");
            legalMapping.addEntry(Raides.AnoCurricular.DISSERTACAO_CODE, "19");
            legalMapping.addEntry(Raides.AnoCurricular.NAO_APLICAVEL_CODE, "20");
        }
    }

    private void createRaidesReport() {
        if (RaidesInstance.getInstance() == null) {
            LegalReport.createReport(RaidesInstance.class);
            RaidesInstance.getInstance().edit(new LocalizedString(ULisboaConstants.DEFAULT_LOCALE, "RAIDES"),
                    Group.parse("#academicAdmOffice").toPersistentGroup(), false, true, "CSgPqCgfupvKqqFQsk6J", Sets.newHashSet(),
                    Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet(), false, new LocalizedString(), "",
                    "", IntegratedMasterFirstCycleGraduatedReportOption.ALL, null);
        }
    }

}
