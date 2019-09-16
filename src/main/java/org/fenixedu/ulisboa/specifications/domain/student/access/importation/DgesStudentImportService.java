package org.fenixedu.ulisboa.specifications.domain.student.access.importation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.FenixEduAcademicConfiguration;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.StandByCandidacySituation;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.contacts.PhysicalAddressData;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.domain.student.access.DegreeCandidateDTO;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

public class DgesStudentImportService {

    /**
     * <pre>
     * tipo_linha       char(1)     valor '*'
     *
     * curso_sup        char(4)     codigo curso colocacao  (ver codigos no Guia)
     * num_bi           char(9)     numero de b.i. do aluno
     * loc_bi           char(2)     local emissao do b.i.  (ver codigos)
     * sexo             char(1)     sexo do aluno
     * data_nasc        char(8)     data de nascimento AAAAMMDD
     * conting          char(1)     contingente candidatura (ver codigos)
     * prefcol          char(1)     nr de opcao VALIDA a q corresp. colocacao (1-6)
     * etapcol          num(2)      etapa de colocacao (1-17)
     *
     * estado           char(2)     não utilizado
     * exclusao         char(1)     não utilizado
     * militar          char(1)     não utilizado
     * deficiente       char(1)     não utilizado
     * GAES             char(3)     não utilizado
     * numCand          num(5)      não utilizado
     *
     * pontos           num(5,1)    nota crit. seriacao para este curso (0.0-100.0)
     * nome             char(70)    nome (sem espacos a direita)
     *
     * ResideDistrito   char(2)     não utilizado
     * ResideConcelho   char(2)     não utilizado
     * ResidePais       char(2)     não utilizado
     * PaisNacional     char(2)     nacionalidade (código País de 2 caracteres)
     * ResidePortugal   char(1)     não utilizado
     * </pre>
     */
    private static final int[] MAIN_INFORMATION_LINE_FIELD_SPEC =
            new int[] { 1, 4, 9, 2, 1, 8, 1, 1, 2, 13, 5, 70, 2, 2, 2, 2, 1 };

    /**
     * <pre>
     * tipo_linha   char(1)     valor '#'
     *
     * morada1      char(40)    morada do aluno - parte 1
     * morada2      char(40)    morada do aluno - parte 2
     * codpos       char(4)     codigo postal
     * codlocal     char(40)    localidade
     * telefone     char(40)    telefone para contacto
     * </pre>
     */
    private static final int[] ADDRESS_AND_CONTACTS_LINE_FIELD_SPEC = new int[] { 1, 40, 40, 4, 40, 40 };

    static private final Logger logger = LoggerFactory.getLogger(DgesStudentImportService.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");

    private final ExecutionYear executionYear;

    private final Space space;

    private final EntryPhase entryPhase;

    private final List<String> reportMessages = new ArrayList<>();

    private final List<String> errorMessages = new ArrayList<>();

    private int processed = 0;

    private int personsCreated = 0;

    private int candidaciesCreated = 0;

    public DgesStudentImportService(final ExecutionYear executionYear, final Space space, final EntryPhase entryPhase) {
        super();
        this.executionYear = executionYear;
        this.space = space;
        this.entryPhase = entryPhase;
    }

    private void addToReport(final String message) {
        reportMessages.add(message);
    }

    private String transformReport() {
        StringBuilder sb = new StringBuilder();
        for (String reportMessage : reportMessages) {
            sb.append(reportMessage).append("\n");
        }
        return sb.toString();
    }

    public String importStudents(final byte[] file) {

        try {
            createRegistrations(parseFile(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addToReport("[INFO]Pessoas criadas :" + this.personsCreated);
        addToReport("[INFO]Candidaturas criadas :" + this.candidaciesCreated);
        addToReport("--------------//---------------");
        for (String repeatLine : this.errorMessages) {
            addToReport(repeatLine);
        }

        return transformReport();
    }

    protected List<DegreeCandidateDTO> parseFile(final byte[] content) throws IOException {

        final List<DegreeCandidateDTO> result = new ArrayList<>();

        final BufferedReader reader =
                new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content), Charset.forName("ISO-8859-15")));
        String personalDataLine = null;
        String addressDataLine = null;
        int lineNumber = 1;
        while ((personalDataLine = reader.readLine()) != null) {

            if (StringUtils.isBlank(personalDataLine)) {
                continue;
            }

            final DegreeCandidateDTO entry = new DegreeCandidateDTO(lineNumber);
            entry.setPersonalDataLine(personalDataLine);
            lineNumber += 2;

            loadMainInformation(entry, personalDataLine);
            addressDataLine = reader.readLine();
            entry.setAddressDataLine(addressDataLine);
            loadAddressAndContactsInformation(entry, addressDataLine);

            result.add(entry);
        }

        logger.info("Entries parsed for {} : {}", this.space.getName(), result.size());

        return result;

    }

    protected void loadMainInformation(final DegreeCandidateDTO entry, final String line) {

        final String[] fields = splitLine(line, MAIN_INFORMATION_LINE_FIELD_SPEC);
        entry.setEntryPhase(entryPhase);
        entry.setDegreeCode(fields[1].trim());
        entry.setDocumentIdNumber(fields[2].trim());
        entry.setDocumentIdIssuePlaceCode(fields[3].trim());
        entry.setGender(fields[4].trim());

        entry.setDateOfBirth(LocalDate.parse(fields[5].trim(), DATE_FORMATTER));

        entry.setContigent(fields[6].trim());
        entry.setPlacingOption(Integer.valueOf(fields[7].trim()));

        final BigDecimal entryGrade = new BigDecimal(fields[10].trim().replace(",", "."));
        entry.setEntryGrade(entryGrade.setScale(1, RoundingMode.HALF_EVEN).doubleValue());

        entry.setName(fields[11].trim());

        final Country nationality = Country.readByTwoLetterCode(fields[15].trim());
        if (nationality == null) {
            throw new RuntimeException(formatMessageWithLineNumber(entry,
                    "[ERROR]As letras do país da nacionalidade é desconhecido: " + fields[15].trim()));
        }
        entry.setNationality(nationality);
    }

    protected void loadAddressAndContactsInformation(final DegreeCandidateDTO entry, final String line) {

        if (StringUtils.isBlank(line)) {
            throw new RuntimeException(
                    formatMessageWithLineNumber(entry, "[ERROR]A linha com a informação da morada está vazia."));
        }

        final String[] fields = splitLine(line, ADDRESS_AND_CONTACTS_LINE_FIELD_SPEC);

        entry.setAddress(fields[1].trim() + " " + fields[2].trim());
        entry.setPhoneNumber(fields[5].trim());
    }

    private void createRegistrations(final List<DegreeCandidateDTO> toProcess) {

        for (final DegreeCandidateDTO entry : toProcess) {
            if (++processed % 150 == 0) {
                logger.info("Processed :" + processed + " / " + toProcess.size());
            }

            final Runnable runnable = () -> {
                try {
                    FenixFramework.atomic(() -> {
                        processEntry(entry);
                    });
                } catch (RuntimeException e) {
                    addToReport(e.getMessage());
                    addErrorMessages(entry.getPersonalDataLine(), entry.getAddressDataLine());
                } catch (Exception e) {
                    addToReport(formatMessageWithLineNumber(entry, e.getMessage()));
                    addErrorMessages(entry.getPersonalDataLine(), entry.getAddressDataLine());
                }
            };

            final Thread thread = new Thread(runnable);
            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    protected void processEntry(final DegreeCandidateDTO entry) {
        //TODO: adicionar a uma lista de erros os casos para se analisar depois pelo relatório

        final Degree degree = getDegreeConfiguration(entry).getDegree();

        final Person person = findOrCreatePersonAndUser(entry);

        if (person.getStudent() != null) {

            if (person.getStudent().getRegistrationsFor(degree).stream().anyMatch(
                    x -> x.getStudentCandidacy().getActiveCandidacySituationType() == CandidacySituationType.STAND_BY)) {
                addToReport(
                        formatMessageWithLineNumber(entry, "[ERROR]Já existe um candidatura a decorrer para o curso escolhido"));
                return;
            }

            if (person.getStudent().getRegistrationsFor(degree).stream().anyMatch(x -> x.isActive())) {
                addToReport(formatMessageWithLineNumber(entry,
                        "[ERROR]O aluno já tem uma matrícula activa no mesmo curso. Uma das matrículas deverá ser anulada / colocada como inactiva"));
            }

        }

        if (person.getTeacher() != null) {
            addToReport(formatMessageWithLineNumber(entry, "[ERROR]O aluno é docente da instituição"));
        }

        if (person.getStudent() == null) {
            new Student(person);
        }

        RoleType.CANDIDATE.actualGroup().grant(person.getUser());

        createRegistration(entry, person);

        candidaciesCreated++;

        StudentAccessServices.triggerSyncPersonToExternal(person);
    }

    private Person findOrCreatePersonAndUser(final DegreeCandidateDTO entry) {
        final Person person = findPerson(entry);
        if (person != null) {

            if (person.getUser() == null) {
                person.setUser(new User(person.getProfile()));
            }

            return person;
        }

        this.personsCreated++;
        addToReport(formatMessageWithLineNumber(entry, "[INFO]Criada nova pessoa"));
        if (!entry.getDocumentIdIssuePlaceCode().equals("91") && !entry.getDocumentIdIssuePlaceCode().equals("01")) {
            addToReport(formatMessageWithLineNumber(entry,
                    "[WARN]Código de emissão do BI/CC não conhecido: " + entry.getDocumentIdIssuePlaceCode()));
        }
        return entry.createPerson();
    }

    private Person findPerson(final DegreeCandidateDTO entry) {
        //TODO use other instead of id card
        final Collection<Person> persons = Person.readByDocumentIdNumber(entry.getDocumentIdNumber());

        if (persons.isEmpty()) {
            return null;
        }

        if (persons.size() > 1) {
            throw new RuntimeException(formatMessageWithLineNumber(entry,
                    "[ERROR]Existe pessoas no sistema com o mesmo documento de identificação que este(a) candidato(a)."));
        }

        final Person person = persons.iterator().next();

        if (person.getDateOfBirthYearMonthDay() != null && person.getDateOfBirthYearMonthDay()
                .compareTo(entry.getDateOfBirth().toDateTimeAtMidnight().toYearMonthDay()) != 0) {
            addToReport(formatMessageWithLineNumber(entry,
                    "[ERROR]A data de nascimento da pessoa já existente no sistema não é igual à data dada pela DGES"));
        }

        if (!person.getName().equalsIgnoreCase(entry.getName())) {
            addToReport(formatMessageWithLineNumber(entry,
                    "[ERROR]O nome da pessoa já existente no sistema não é igual ao nome dado pela DGES"));
        }

        // Find one address for default country and set it as default fiscal address
        Optional<PhysicalAddress> fiscalAddress = person.getAllPartyContacts(PhysicalAddress.class).stream()
                .map(PhysicalAddress.class::cast).filter(pa -> pa.getCountryOfResidence() == Country.readDefault()).findFirst();

        if (!fiscalAddress.isPresent()) {
            final PhysicalAddress createPhysicalAddress = PhysicalAddress.createPhysicalAddress(person,
                    new PhysicalAddressData(entry.getAddress(), entry.getAreaCode(), entry.getAreaOfAreaCode(), null, null, null,
                            null, Country.readDefault()),
                    PartyContactType.PERSONAL, true);
            createPhysicalAddress.setValid();

            fiscalAddress = Optional.of(createPhysicalAddress);
            addToReport(formatMessageWithLineNumber(entry, "[INFO]Foi adicionada a morada do ficheiro."));
        }

        String socialSecurityNumber = person.getSocialSecurityNumber();
        if (StringUtils.isBlank(socialSecurityNumber)) {
            socialSecurityNumber = FenixEduAcademicConfiguration.getConfiguration().getDefaultSocialSecurityNumber();
            addToReport(formatMessageWithLineNumber(entry, "[INFO]O contribuinte não está preenchido."));
        }

        person.editSocialSecurityNumber(socialSecurityNumber, fiscalAddress.get());

        addToReport(formatMessageWithLineNumber(entry, "[INFO]Pessoa já existe no sistema(Pessoa Encontrada)."));

        return person;
    }

    protected FirstYearRegistrationConfiguration getDegreeConfiguration(final DegreeCandidateDTO entry) {
        final Optional<FirstYearRegistrationConfiguration> degreeConfig =
                FirstYearRegistrationGlobalConfiguration.getInstance().getFirstYearRegistrationConfigurationsSet().stream()
                        .filter(x -> StringUtils.equals(x.getDegree().getMinistryCode(), entry.getDegreeCode())).findFirst();

        if (!degreeConfig.isPresent()) {
            throw new RuntimeException(formatMessageWithLineNumber(entry, "[ERROR]Não foi encontrado configuração para o curso ["
                    + entry.getDegreeCode()
                    + "]. Configurar o curso em Administração Sistema > Candidaturas > 1º ano 1ª vez > Configuração(Butão)"));
        }

        return degreeConfig.get();
    }

    private Registration createRegistration(final DegreeCandidateDTO entry, final Person person) {

        final StudentCandidacy studentCandidacy = createCandidacy(entry, person);
        new StandByCandidacySituation(studentCandidacy, AccessControl.getPerson());

        final Registration registration = new Registration(person, studentCandidacy.getDegreeCurricularPlan(), studentCandidacy,
                ULisboaSpecificationsRoot.getInstance().getDefaultRegistrationProtocol(), CycleType.FIRST_CYCLE, executionYear);

        final PrecedentDegreeInformation pdi = studentCandidacy.getPrecedentDegreeInformation() != null ? studentCandidacy
                .getPrecedentDegreeInformation() : new PrecedentDegreeInformation();
        pdi.setPersonalIngressionData(findOrCreatePersonalIngressionData(registration));
        pdi.setRegistration(registration);

        markRegistrationAsInactive(registration);

        addToReport(formatMessageWithLineNumber(entry, "[INFO]A candidatura foi criada com sucesso."));

        return registration;
    }

    private StudentCandidacy createCandidacy(final DegreeCandidateDTO entry, final Person person) {

        final DegreeCurricularPlan degreeCurricularPlan = getDegreeConfiguration(entry).getDegreeCurricularPlan();
        if (degreeCurricularPlan == null) {
            throw new RuntimeException(formatMessageWithLineNumber(entry,
                    "[ERROR]Não foi encontrado o plano curricular para o curso [" + entry.getDegreeCode()
                            + "]. Escolher o plano curricular em Administração Sistema > Candidaturas > 1º ano 1ª vez > Configuração(Butão)"));
        }

        final ExecutionDegree executionDegree = degreeCurricularPlan.getExecutionDegreeByYear(executionYear);

        //TODOJN: remove firsttimecandidacysubclass
        final StudentCandidacy candidacy =
                new FirstTimeCandidacy(person, executionDegree, AccessControl.getPerson(), entry.getEntryGrade(),
                        entry.getContigent(), entry.getIngression(), entry.getEntryPhase(), entry.getPlacingOption());

        candidacy.setFirstTimeCandidacy(true);

        return candidacy;
    }

    protected void markRegistrationAsInactive(final Registration registration) {
        registration.getActiveState().setStateDate(registration.getActiveState().getStateDate().minusMinutes(1));
        RegistrationState.createRegistrationState(registration, AccessControl.getPerson(), new DateTime(),
                RegistrationStateType.INACTIVE, executionYear.getFirstExecutionPeriod());
    }

    private PersonalIngressionData findOrCreatePersonalIngressionData(final Registration registration) {

        PersonalIngressionData result = registration.getStudent().getPersonalIngressionDataByExecutionYear(executionYear);

        if (result != null) {
            return result;
        }

        result = new PersonalIngressionData();
        result.setStudent(registration.getStudent());
        result.setExecutionYear(executionYear);

        return result;

    }

    private String[] splitLine(final String line, final int[] fieldSizeSpec) {
        String[] result = new String[fieldSizeSpec.length];
        int currentIndex = 0;

        for (int i = 0; i < fieldSizeSpec.length; i++) {
            int fieldSize = fieldSizeSpec[i];

            if (i == fieldSizeSpec.length - 1) {
                result[i] = line.substring(currentIndex);
            } else {
                result[i] = line.substring(currentIndex, currentIndex + fieldSize);
            }

            currentIndex = currentIndex + fieldSize;
        }

        return result;
    }

    private String formatMessageWithLineNumber(final DegreeCandidateDTO entry, final String message) {
        return "[" + entry.getLineNumber() + "][" + entry.getDocumentIdNumber() + "] " + message;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void addErrorMessages(final String... lines) {
        for (String line : lines) {
            this.errorMessages.add(line);
        }
    }

}
