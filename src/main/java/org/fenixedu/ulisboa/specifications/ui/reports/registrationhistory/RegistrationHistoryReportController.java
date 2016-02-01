package org.fenixedu.ulisboa.specifications.ui.reports.registrationhistory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.file.ULisboaSpecificationsTemporaryFile;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.dto.report.registrationhistory.RegistrationHistoryReportParametersBean;
import org.fenixedu.ulisboa.specifications.service.report.registrationhistory.RegistrationHistoryReport;
import org.fenixedu.ulisboa.specifications.service.report.registrationhistory.RegistrationHistoryReportService;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import fr.opensagres.xdocreport.core.io.internal.ByteArrayOutputStream;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.registrationHistoryReport")
@RequestMapping(RegistrationHistoryReportController.CONTROLLER_URL)
public class RegistrationHistoryReportController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/reports/registrationhistory/registrationhistoryreport";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private RegistrationHistoryReportParametersBean getParametersBean(Model model) {
        return (RegistrationHistoryReportParametersBean) model.asMap().get("bean");
    }

    private void setParametersBean(RegistrationHistoryReportParametersBean bean, Model model) {
        model.addAttribute("beanJson", getBeanJson(bean));
        model.addAttribute("bean", bean);
    }

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(CONTROLLER_URL + "/search", model, redirectAttributes);
    }

    @RequestMapping(value = "/search")
    public String search(Model model, RedirectAttributes redirectAttributes) {
        setParametersBean(new RegistrationHistoryReportParametersBean(), model);
        return jspPage("registrationhistoryreport");
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam("bean") RegistrationHistoryReportParametersBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        setParametersBean(bean, model);
        setResults(generateReport(bean, false), model);

        return jspPage("registrationhistoryreport");
    }

    private void setResults(Collection<RegistrationHistoryReport> results, Model model) {
        model.addAttribute("results", results);
    }

    @RequestMapping(value = "/exportregistrations", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> exportRegistrations(
            @RequestParam(value = "bean", required = false) final RegistrationHistoryReportParametersBean bean,
            final Model model) {

        final String reportId = UUID.randomUUID().toString();
        new Thread(() -> processReport(this::exportRegistrationsToXLS, bean, reportId)).start();

        return new ResponseEntity<String>(reportId, HttpStatus.OK);
    }

    @Atomic(mode = TxMode.READ)
    protected void processReport(final Function<RegistrationHistoryReportParametersBean, byte[]> reportProcessor,
            final RegistrationHistoryReportParametersBean bean, final String reportId) {

        byte[] content = null;
        try {
            content = reportProcessor.apply(bean);
        } catch (Throwable e) {
            content = createXLSWithError(
                    (e instanceof ULisboaSpecificationsDomainException) ? ((ULisboaSpecificationsDomainException) e)
                            .getLocalizedMessage() : ExceptionUtils.getFullStackTrace(e));
        }

        ULisboaSpecificationsTemporaryFile.create(reportId, content, Authenticate.getUser());
    }

    @RequestMapping(value = "/exportstatus/{reportId}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> exportStatus(@PathVariable(value = "reportId") final String reportId,
            final Model model) {
        return new ResponseEntity<String>(
                String.valueOf(
                        ULisboaSpecificationsTemporaryFile.findByUserAndFilename(Authenticate.getUser(), reportId).isPresent()),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/downloadreport/{reportId}", method = RequestMethod.GET)
    public void downloadReport(@PathVariable("reportId") String reportId, final Model model,
            RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {
        final Optional<ULisboaSpecificationsTemporaryFile> temporaryFile =
                ULisboaSpecificationsTemporaryFile.findByUserAndFilename(Authenticate.getUser(), reportId);
        writeFile(response, "Report_" + new DateTime().toString("yyyy-MM-dd_HH-mm-ss") + ".xls", "application/vnd.ms-excel",
                temporaryFile.get().getContent());
    }

    protected Collection<RegistrationHistoryReport> generateReport(RegistrationHistoryReportParametersBean bean,
            boolean detailed) {
        final RegistrationHistoryReportService service = new RegistrationHistoryReportService();
        service.filterExecutionYears(bean.getExecutionYears());
        service.filterDegrees(bean.getDegrees());
        service.filterDegreeTypes(bean.getDegreeTypes());
        service.filterIngressionTypes(bean.getIngressionTypes());
        service.filterRegimeTypes(bean.getRegimeTypes());
        service.filterRegistrationProtocols(bean.getRegistrationProtocols());
        service.filterRegistrationStateTypes(bean.getRegistrationStateTypes());
        service.filterStatuteTypes(bean.getStatuteTypes());
        service.filterFirstTimeOnly(bean.getFirstTimeOnly());
        service.filterDismissalsOnly(bean.getDismissalsOnly());
        service.filterImprovementEnrolmentsOnly(bean.getImprovementEnrolmentsOnly());
        service.setDetailed(detailed);

        final Comparator<RegistrationHistoryReport> byYear =
                (x, y) -> ExecutionYear.COMPARATOR_BY_BEGIN_DATE.compare(x.getExecutionYear(), y.getExecutionYear());
        final Comparator<RegistrationHistoryReport> byDegreeType =
                (x, y) -> x.getRegistration().getDegreeType().compareTo(y.getRegistration().getDegreeType());
        final Comparator<RegistrationHistoryReport> byDegree =
                (x, y) -> Degree.COMPARATOR_BY_NAME.compare(x.getRegistration().getDegree(), y.getRegistration().getDegree());
        final Comparator<RegistrationHistoryReport> byDegreeCurricularPlan =
                (x, y) -> x.getDegreeCurricularPlan().getName().compareTo(y.getDegreeCurricularPlan().getName());

        return service.generateReport().stream()
                .sorted(byYear.thenComparing(byDegreeType).thenComparing(byDegree).thenComparing(byDegreeCurricularPlan))
                .collect(Collectors.toList());
    }

    private byte[] exportRegistrationsToXLS(RegistrationHistoryReportParametersBean bean) {

        final Collection<RegistrationHistoryReport> toExport = generateReport(bean, true);

        final SpreadsheetBuilder builder = new SpreadsheetBuilder();
        builder.addSheet(ULisboaSpecificationsUtil.bundle("label.reports.registrationHistory.students"),
                new SheetData<RegistrationHistoryReport>(toExport) {

                    @Override
                    protected void makeLine(RegistrationHistoryReport report) {
                        addPrimaryData(report);
                        addSecondaryData(report);
                    }

                    protected void addPrimaryData(final RegistrationHistoryReport report) {
                        final Registration registration = report.getRegistration();
                        final Person person = registration.getPerson();
                        final Degree degree = registration.getDegree();

                        addData("RegistrationHistoryReport.executionYear", report.getExecutionYear().getQualifiedName());
                        addData("Student.number", registration.getStudent().getNumber().toString());
                        addData("Registration.number", registration.getNumber().toString());
                        addData("Person.username", person.getUsername());
                        addData("Person.name", person.getName());
                        addData("Person.gender", person.getGender().getLocalizedName());
                        addData("Degree.code", degree.getCode());
                        addData("Degree.ministryCode", degree.getMinistryCode());
                        addData("Degree.degreeType", degree.getDegreeType().getName());
                        addData("Degree.name", degree.getNameI18N().getContent());
                        addData("Degree.presentationName", degree.getPresentationName());
                        addData("Registration.ingressionType", registration.getIngressionType().getDescription().getContent());
                        addData("Registration.registrationProtocol",
                                registration.getRegistrationProtocol().getDescription().getContent());
                        addData("Registration.startDate", registration.getStartDate());

                        final RegistrationState firstState = registration.getFirstRegistrationState();
                        addData("Registration.firstStateDate", firstState != null ? firstState.getStateDate().toLocalDate() : "");
                        addData("Registration.registrationYear", registration.getRegistrationYear().getQualifiedName());
                        addData("RegistrationHistoryReport.studentCurricularPlan", report.getStudentCurricularPlan().getName());
                        addData("RegistrationHistoryReport.isReingression", booleanString(report.isReingression()));
                        addData("RegistrationHistoryReport.curricularYear", report.getCurricularYear().toString());
                        addData("RegistrationHistoryReport.ectsCredits", report.getEctsCredits());
                        addData("RegistrationHistoryReport.average",
                                report.getAverage() != null ? report.getAverage().getValue() : null);
                        addData("RegistrationHistoryReport.enrolmentDate", report.getEnrolmentDate());

                        final ExecutionYear lastEnrolmentExecutionYear = registration.getLastEnrolmentExecutionYear();
                        addData("Registration.lastEnrolmentExecutionYear",
                                lastEnrolmentExecutionYear != null ? lastEnrolmentExecutionYear.getQualifiedName() : "");

                        addData("RegistrationHistoryReport.primaryBranch", report.getPrimaryBranchName());
                        addData("RegistrationHistoryReport.secondaryBranch", report.getSecondaryBranchName());
                        addData("RegistrationHistoryReport.statutes", report.getStatuteTypes().stream()
                                .map(s -> s.getName().getContent()).collect(Collectors.joining(", ")));
                        addData("RegistrationHistoryReport.regimeType", report.getRegimeType().getLocalizedName());
                        addData("RegistrationHistoryReport.enrolmentsWithoutShifts",
                                booleanString(report.hasEnrolmentsWithoutShifts()));
                        addData("RegistrationHistoryReport.inactiveRegistrationStateForYear",
                                booleanString(report.hasAnyInactiveRegistrationStateForYear()));
                        addData("RegistrationHistoryReport.lastRegistrationState",
                                report.getLastRegistrationState() != null ? report.getLastRegistrationState()
                                        .getDescription() : null);
                        addData("RegistrationHistoryReport.firstTime", booleanString(report.isFirstTime()));
                        addData("RegistrationHistoryReport.dismissals", booleanString(report.hasDismissals()));
                        addData("RegistrationHistoryReport.enroledInImprovement",
                                booleanString(report.hasImprovementEvaluations()));
                        addData("RegistrationHistoryReport.annulledEnrolments", booleanString(report.hasAnnulledEnrolments()));
                        addData("RegistrationHistoryReport.enrolmentsCount", report.getEnrolmentsCount());
                        addData("RegistrationHistoryReport.enrolmentsCredits", report.getEnrolmentsCredits());
                        addData("RegistrationHistoryReport.extraCurricularEnrolmentsCount",
                                report.getExtraCurricularEnrolmentsCount());
                        addData("RegistrationHistoryReport.extraCurricularEnrolmentsCredits",
                                report.getExtraCurricularEnrolmentsCredits());
                        addData("RegistrationHistoryReport.standaloneEnrolmentsCount", report.getStandaloneEnrolmentsCount());
                        addData("RegistrationHistoryReport.standaloneEnrolmentsCredits", report.getStandaloneEnrolmentsCredits());
                        addData("RegistrationHistoryReport.executionYearSimpleAverage", report.getExecutionYearSimpleAverage());
                        addData("RegistrationHistoryReport.executionYearWeightedAverage",
                                report.getExecutionYearWeightedAverage());

                        addData("Registration.registrationObservations",
                                registration.getRegistrationObservationsSet().stream()
                                        .map(o -> o.getVersioningUpdatedBy().getUsername() + ":" + o.getValue())
                                        .collect(Collectors.joining(" \n --------------\n ")));

                        addConclusionData(report);

                    }

                    private void addConclusionData(RegistrationHistoryReport report) {

                        //TODO: program conclusions should already be sorted
                        final List<ProgramConclusion> sortedProgramConclusions = report.getProgramConclusions().stream()
                                .sorted(Comparator.comparing(ProgramConclusion::getName)
                                        .thenComparing(ProgramConclusion::getDescription)
                                        .thenComparing(ProgramConclusion::getExternalId))
                                .collect(Collectors.toList());

                        for (final ProgramConclusion programConclusion : sortedProgramConclusions) {

                            final RegistrationConclusionBean bean = report.getConclusionReportFor(programConclusion);

                            final String concluded = bean == null ? null : booleanString(bean.isConcluded());
                            addCell(labelFor(programConclusion, "concluded"), concluded);

                            final String conclusionProcessed = bean == null ? null : booleanString(bean.isConclusionProcessed());
                            addCell(labelFor(programConclusion, "conclusionProcessed"), conclusionProcessed);

                            final String rawGrade =
                                    bean == null || bean.getRawGrade() == null ? null : bean.getRawGrade().getValue();
                            addCell(labelFor(programConclusion, "rawGrade"), rawGrade);

                            final String finalGrade =
                                    bean == null || bean.getFinalGrade() == null ? null : bean.getFinalGrade().getValue();
                            addCell(labelFor(programConclusion, "finalGrade"), finalGrade);

                            final String descriptiveGrade = bean == null
                                    || bean.getDescriptiveGrade() == null ? null : bean.getDescriptiveGradeExtendedValue() + " ("
                                            + bean.getDescriptiveGrade().getValue() + ")";
                            addCell(labelFor(programConclusion, "descriptiveGrade"), descriptiveGrade);

                            final YearMonthDay conclusionDate =
                                    bean == null || bean.getConclusionDate() == null ? null : bean.getConclusionDate();
                            addCell(labelFor(programConclusion, "conclusionDate"), conclusionDate);

                            final String conclusionYear = bean == null || bean.getConclusionYear() == null ? null : bean
                                    .getConclusionYear().getQualifiedName();
                            addCell(labelFor(programConclusion, "conclusionYear"), conclusionYear);

                            final String ectsCredits = bean == null ? null : String.valueOf(bean.getEctsCredits());
                            addCell(labelFor(programConclusion, "ectsCredits"), ectsCredits);

                        }

                    }

                    private String labelFor(ProgramConclusion programConclusion, String field) {
                        final String programConclusionPrefix = programConclusion.getName().getContent() + " - "
                                + programConclusion.getDescription().getContent() + ": ";

                        return programConclusionPrefix + bundle("label.RegistrationConclusionBean." + field);

                    }

                    private void addSecondaryData(RegistrationHistoryReport registrationHistoryReport) {

                        addPrecedentDegreeInformation(registrationHistoryReport.getRegistration());
                        addPersonalData(registrationHistoryReport);

                    }

                    private void addPrecedentDegreeInformation(Registration registration) {
                        final PrecedentDegreeInformation information =
                                registration.getStudentCandidacy().getPrecedentDegreeInformation();

                        if (information != null) {
                            addData("PrecedentDegreeInformation.institutionUnit", information.getInstitutionName());
                            addData("PrecedentDegreeInformation.schoolLevel",
                                    information.getSchoolLevel() != null ? information.getSchoolLevel().getLocalizedName() : "");
                            addData("PrecedentDegreeInformation.degreeDesignation", information.getDegreeDesignation());
                            addData("PrecedentDegreeInformation.precedentInstitution",
                                    information.getPrecedentInstitution() != null ? information.getPrecedentInstitution()
                                            .getName() : "");
                            addData("PrecedentDegreeInformation.precedentSchoolLevel",
                                    information.getPrecedentSchoolLevel() != null ? information.getPrecedentSchoolLevel()
                                            .getLocalizedName() : "");
                            addData("PrecedentDegreeInformation.precedentDegreeDesignation",
                                    information.getPrecedentDegreeDesignation());
                        } else {
                            addData("PrecedentDegreeInformation.institutionUnit", "");
                            addData("PrecedentDegreeInformation.schoolLevel", "");
                            addData("PrecedentDegreeInformation.degreeDesignation", "");
                            addData("PrecedentDegreeInformation.precedentInstitution", "");
                            addData("PrecedentDegreeInformation.precedentSchoolLevel", "");
                            addData("PrecedentDegreeInformation.precedentDegreeDesignation", "");
                        }

                    }

                    protected void addPersonalData(RegistrationHistoryReport registrationHistoryReport) {

                        final Person person = registrationHistoryReport.getRegistration().getPerson();

                        addData("Person.idDocumentType", person.getIdDocumentType().getLocalizedName());
                        addData("Person.idDocumentNumber", person.getDocumentIdNumber());
                        addData("Person.dateOfBirth", person.getDateOfBirthYearMonthDay());
                        addData("Person.nameOfFather", person.getNameOfFather());
                        addData("Person.nameOfMother", person.getNameOfMother());
                        addData("Person.nationality", person.getCountry() != null ? person.getCountry().getName() : "");
                        addData("Person.countryOfBirth",
                                person.getCountryOfBirth() != null ? person.getCountryOfBirth().getName() : "");
                        addData("Person.socialSecurityNumber", person.getSocialSecurityNumber());
                        addData("Person.districtOfBirth", person.getDistrictOfBirth());
                        addData("Person.districtSubdivisionOfBirth", person.getDistrictSubdivisionOfBirth());
                        addData("Person.parishOfBirth", person.getParishOfBirth());

                        addData("Student.studentPersonalDataAuthorizationChoice",
                                registrationHistoryReport
                                        .getStudentPersonalDataAuthorizationChoice() != null ? registrationHistoryReport
                                                .getStudentPersonalDataAuthorizationChoice().getDescription() : "");

                        addContactsData(person);
                    }

                    protected void addContactsData(Person person) {
                        addData("Person.defaultEmailAddress", person.getDefaultEmailAddressValue());
                        addData("Person.institutionalEmailAddress", person.getInstitutionalEmailAddressValue());
                        addData("Person.otherEmailAddresses",
                                person.getEmailAddresses().stream().map(e -> e.getValue()).collect(Collectors.joining(",")));
                        addData("Person.defaultPhone", person.getDefaultPhoneNumber());
                        addData("Person.defaultMobilePhone", person.getDefaultMobilePhoneNumber());

                        if (person.hasDefaultPhysicalAddress()) {
                            final PhysicalAddress address = person.getDefaultPhysicalAddress();
                            addData("PhysicalAddress.address", address.getAddress());
                            addData("PhysicalAddress.districtOfResidence", address.getDistrictOfResidence());
                            addData("PhysicalAddress.districtSubdivisionOfResidence",
                                    address.getDistrictSubdivisionOfResidence());
                            addData("PhysicalAddress.parishOfResidence", address.getParishOfResidence());
                            addData("PhysicalAddress.area", address.getArea());
                            addData("PhysicalAddress.areaCode", address.getAreaCode());
                            addData("PhysicalAddress.areaOfAreaCode", address.getAreaOfAreaCode());
                            addData("PhysicalAddress.countryOfResidence", address.getCountryOfResidenceName());

                        } else {
                            addData("PhysicalAddress.address", "");
                            addData("PhysicalAddress.districtOfResidence", "");
                            addData("PhysicalAddress.districtSubdivisionOfResidence", "");
                            addData("PhysicalAddress.parishOfResidence", "");
                            addData("PhysicalAddress.area", "");
                            addData("PhysicalAddress.areaCode", "");
                            addData("PhysicalAddress.areaOfAreaCode", "");
                            addData("PhysicalAddress.countryOfResidence", "");

                        }

                    }

                    private void addData(String bundleKey, Object value) {
                        addCell(bundle("label." + bundleKey), value);
                    }

                    private String booleanString(boolean value) {
                        return value ? bundle("label.yes") : bundle("label.no");
                    }

                    private String bundle(String key) {
                        return ULisboaSpecificationsUtil.bundle(key);
                    }

                });

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            builder.build(WorkbookExportFormat.EXCEL, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result.toByteArray();

    }

    private byte[] createXLSWithError(String error) {

        try {

            final SpreadsheetBuilder builder = new SpreadsheetBuilder();
            builder.addSheet("Registrations", new SheetData<String>(Collections.singleton(error)) {
                @Override
                protected void makeLine(String item) {
                    addCell(ULisboaSpecificationsUtil.bundle("label.unexpected.error.occured"), item);
                }
            });

            final ByteArrayOutputStream result = new ByteArrayOutputStream();
            builder.build(WorkbookExportFormat.EXCEL, result);

            return result.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String _POSTBACK_URI = "/postback";
    public static final String POSTBACK_URL = CONTROLLER_URL + _POSTBACK_URI;

    @RequestMapping(value = _POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> postback(
            @RequestParam(value = "bean", required = false) final RegistrationHistoryReportParametersBean bean,
            final Model model) {

        bean.updateData();

        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping(value = "/exportapprovals", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> exportApprovals(
            @RequestParam(value = "bean", required = false) final RegistrationHistoryReportParametersBean bean,
            final Model model) {

        final String reportId = UUID.randomUUID().toString();
        new Thread(() -> processReport(this::exportApprovalsToXLS, bean, reportId)).start();

        return new ResponseEntity<String>(reportId, HttpStatus.OK);
    }

    private byte[] exportApprovalsToXLS(RegistrationHistoryReportParametersBean bean) {

        final Collection<RegistrationHistoryReport> reports = generateReport(bean, false);

        final Collection<Curriculum> curriculums =
                reports.stream().map(r -> r.getRegistration().getLastStudentCurricularPlan().getCurriculum(new DateTime(), null))
                        .sorted((x, y) -> x.getStudentCurricularPlan().getRegistration().getNumber()
                                .compareTo(x.getStudentCurricularPlan().getRegistration().getNumber()))
                        .distinct().collect(Collectors.toList());

        final Multimap<Curriculum, ICurriculumEntry> approvalsByCurriculum = HashMultimap.create();
        curriculums.stream().forEach(c -> approvalsByCurriculum.putAll(c, c.getCurriculumEntries()));

        final ExecutionYear executionYearForCurricularYear =
                bean.getExecutionYears().stream().max(ExecutionYear.COMPARATOR_BY_BEGIN_DATE).get();

        final SpreadsheetBuilder builder = new SpreadsheetBuilder();
        builder.addSheet(ULisboaSpecificationsUtil.bundle("label.reports.registrationHistory.approvals"),
                new SheetData<Map.Entry<Curriculum, ICurriculumEntry>>(approvalsByCurriculum.entries()) {

                    @Override
                    protected void makeLine(Entry<Curriculum, ICurriculumEntry> entry) {
                        final Registration registration = entry.getKey().getStudentCurricularPlan().getRegistration();
                        final ICurriculumEntry curriculumEntry = entry.getValue();

                        addData("Student.number", registration.getStudent().getNumber());
                        addData("Registration.number", registration.getNumber().toString());
                        addData("Person.name", registration.getStudent().getPerson().getName());
                        addData("Degree.code", registration.getDegree().getCode());
                        addData("Degree.presentationName", registration.getDegree().getPresentationName());
                        addData("RegistrationHistoryReport.curricularYear",
                                registration.getCurricularYear(executionYearForCurricularYear));
                        addData("ICurriculumEntry.code", curriculumEntry.getCode());
                        addData("ICurriculumEntry.name", curriculumEntry.getName().getContent());
                        addData("ICurriculumEntry.grade", curriculumEntry.getGradeValue());
                        addData("ICurriculumEntry.ectsCreditsForCurriculum", curriculumEntry.getEctsCreditsForCurriculum());
                        addData("ICurriculumEntry.executionPeriod", curriculumEntry.getExecutionPeriod().getQualifiedName());
                        addData("ICurriculumEntry.dismissal", ULisboaSpecificationsUtil.bundle(entry.getKey()
                                .getDismissalRelatedEntries().contains(entry.getValue()) ? "label.yes" : "label.no"));
                        addData("Curriculum.totalApprovals", entry.getKey().getCurriculumEntries().size());
                        addData("Curriculum.simpleAverage",
                                entry.getKey().getCurriculumEntries().stream().filter(e -> e.getGrade().isNumeric())
                                        .map(e -> e.getGrade().getNumericValue()).mapToDouble(v -> v.doubleValue()).average()
                                        .getAsDouble());

                    }

                    private void addData(String key, Object data) {
                        addCell(ULisboaSpecificationsUtil.bundle("label." + key), data);
                    }

                });

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            builder.build(WorkbookExportFormat.EXCEL, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result.toByteArray();
    }

    @RequestMapping(value = "/exportenrolments", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> exportEnrolments(
            @RequestParam(value = "bean", required = false) final RegistrationHistoryReportParametersBean bean,
            final Model model) {

        final String reportId = UUID.randomUUID().toString();
        new Thread(() -> processReport(this::exportEnrolmentsToXLS, bean, reportId)).start();

        return new ResponseEntity<String>(reportId, HttpStatus.OK);
    }

    private byte[] exportEnrolmentsToXLS(RegistrationHistoryReportParametersBean bean) {

        final Collection<RegistrationHistoryReport> reports = generateReport(bean, true);
        final Multimap<RegistrationHistoryReport, Enrolment> enrolments = HashMultimap.create();
        reports.stream().forEach(r -> enrolments.putAll(r, r.getRegistration().getEnrolments(r.getExecutionYear())));

        final Map<Enrolment, ExecutionSemester> improvementsOnly = Maps.newHashMap();
        reports.stream().forEach(r ->
        {
            RegistrationServices.getImprovementEvaluations(r.getRegistration(), r.getExecutionYear()).forEach(ev ->
            {
                enrolments.put(r, ev.getEnrolment());

                if (ev.getExecutionPeriod() != ev.getEnrolment().getExecutionPeriod()) {
                    improvementsOnly.put(ev.getEnrolment(), ev.getExecutionPeriod());
                }

            });
        });

        final SpreadsheetBuilder builder = new SpreadsheetBuilder();
        builder.addSheet(ULisboaSpecificationsUtil.bundle("label.reports.registrationHistory.enrolments"),
                new SheetData<Map.Entry<RegistrationHistoryReport, Enrolment>>(enrolments.entries()) {

                    @Override
                    protected void makeLine(Entry<RegistrationHistoryReport, Enrolment> entry) {

                        final RegistrationHistoryReport report = entry.getKey();
                        final Registration registration = report.getRegistration();
                        final Enrolment enrolment = entry.getValue();

                        final boolean improvementOnly = improvementsOnly.containsKey(enrolment);
                        final ExecutionSemester enrolmentPeriod =
                                improvementOnly ? improvementsOnly.get(enrolment) : enrolment.getExecutionPeriod();

                        addData("Student.number", registration.getStudent().getNumber());
                        addData("Registration.number", registration.getNumber().toString());
                        addData("Person.name", registration.getStudent().getPerson().getName());
                        addData("Degree.code", registration.getDegree().getCode());
                        addData("Degree.presentationName", registration.getDegree().getPresentationName());
                        addData("RegistrationHistoryReport.curricularYear", report.getCurricularYear().toString());
                        addData("Enrolment.code", enrolment.getCode());
                        addData("Enrolment.name", enrolment.getPresentationName().getContent());
                        addData("Enrolment.ectsCreditsForCurriculum", enrolment.getEctsCreditsForCurriculum());
                        addData("Enrolment.executionPeriod", enrolmentPeriod.getQualifiedName());
                        addData("Enrolment.improvementOnly",
                                ULisboaSpecificationsUtil.bundle(improvementOnly ? "label.yes" : "label.no"));
                        addData("Enrolment.shifts", EnrolmentServices.getShiftsDescription(enrolment, enrolmentPeriod));
                        addData("Enrolment.curriculumGroup", enrolment.getCurriculumGroup().getFullPath());
                    }

                    private void addData(String key, Object data) {
                        addCell(ULisboaSpecificationsUtil.bundle("label." + key), data);
                    }

                });

        final List<EnrolmentEvaluation> evaluations =
                enrolments.entries().stream().flatMap(e -> e.getValue().getEvaluationsSet().stream())
                        .filter(e -> EvaluationSeasonServices.isRequiredEnrolmentEvaluation(e.getEvaluationSeason()))
                        .sorted(EnrolmentEvaluation.SORT_BY_STUDENT_NUMBER.thenComparing(DomainObjectUtil.COMPARATOR_BY_ID))
                        .collect(Collectors.toList());
        builder.addSheet(ULisboaSpecificationsUtil.bundle("label.reports.registrationHistory.evaluations"),
                new SheetData<EnrolmentEvaluation>(evaluations) {

                    @Override
                    protected void makeLine(EnrolmentEvaluation item) {

                        final Registration registration = item.getRegistration();
                        final Enrolment enrolment = item.getEnrolment();

                        addData("Student.number", registration.getStudent().getNumber());
                        addData("Registration.number", registration.getNumber().toString());
                        addData("Person.name", registration.getStudent().getPerson().getName());
                        addData("Degree.code", registration.getDegree().getCode());
                        addData("Degree.presentationName", registration.getDegree().getPresentationName());
                        addData("Enrolment.code", enrolment.getCode());
                        addData("Enrolment.name", enrolment.getPresentationName().getContent());
                        addData("Enrolment.executionPeriod", item.getExecutionPeriod().getQualifiedName());
                        addData("EnrolmentEvaluation.grade", item.getGradeValue());
                        addData("EnrolmentEvaluation.season", item.getEvaluationSeason().getName().getContent());
                    }

                    private void addData(String key, Object data) {
                        addCell(ULisboaSpecificationsUtil.bundle("label." + key), data);
                    }

                });

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            builder.build(WorkbookExportFormat.EXCEL, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result.toByteArray();
    }

}
