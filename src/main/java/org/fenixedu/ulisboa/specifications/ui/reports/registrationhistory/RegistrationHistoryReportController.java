package org.fenixedu.ulisboa.specifications.ui.reports.registrationhistory;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.opensagres.xdocreport.core.io.internal.ByteArrayOutputStream;

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
        setResults(generateReport(bean), model);

        return jspPage("registrationhistoryreport");
    }

    private void setResults(Collection<RegistrationHistoryReport> results, Model model) {
        model.addAttribute("results", results);
    }

    @RequestMapping(value = "/exportresult")
    public void exportResult(@RequestParam("bean") RegistrationHistoryReportParametersBean bean, Model model,
            RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {
        writeFile(response, Registration.class.getSimpleName() + new DateTime().toString("yyyy-MM-dd_HH-mm-ss") + ".xls",
                "application/vnd.ms-excel", exportResultToXLS(generateReport(bean)));
    }

    protected Collection<RegistrationHistoryReport> generateReport(RegistrationHistoryReportParametersBean bean) {
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

        final Comparator<RegistrationHistoryReport> comparator =
                (x, y) -> ExecutionYear.COMPARATOR_BY_BEGIN_DATE.compare(x.getExecutionYear(), y.getExecutionYear());
        comparator.thenComparing((x, y) -> x.getRegistration().getDegreeType().compareTo(y.getRegistration().getDegreeType()));
        comparator.thenComparing((x, y) -> x.getRegistration().getDegree().compareTo(y.getRegistration().getDegree()));
        comparator.thenComparing(
                (x, y) -> x.getStudentCurricularPlan().getName().compareTo(y.getStudentCurricularPlan().getName()));

        return service.generateReport().stream().sorted(comparator).collect(Collectors.toList());
    }

    private byte[] exportResultToXLS(Collection<RegistrationHistoryReport> toExport) throws IOException {

        final SpreadsheetBuilder builder = new SpreadsheetBuilder();
        builder.addSheet("Registrations", new SheetData<RegistrationHistoryReport>(toExport) {

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
                addData("Registration.registrationYear", registration.getRegistrationYear().getQualifiedName());
                addData("RegistrationHistoryReport.studentCurricularPlan", report.getStudentCurricularPlan().getName());
                addData("RegistrationHistoryReport.isReingression", booleanString(report.isReingression()));
                addData("RegistrationHistoryReport.curricularYear", report.getCurricularYear().toString());
                addData("RegistrationHistoryReport.ectsCredits", report.getEctsCredits().toPlainString());
                addData("RegistrationHistoryReport.average", report.getAverage() != null ? report.getAverage().getValue() : null);
                addData("RegistrationHistoryReport.enrolmentDate", report.getEnrolmentDate());

                final ExecutionYear lastEnrolmentExecutionYear = registration.getLastEnrolmentExecutionYear();
                addData("Registration.lastEnrolmentExecutionYear",
                        lastEnrolmentExecutionYear != null ? lastEnrolmentExecutionYear.getQualifiedName() : "");

                addData("RegistrationHistoryReport.primaryBranch", report.getPrimaryBranchName());
                addData("RegistrationHistoryReport.secondaryBranch", report.getSecondaryBranchName());
                addData("RegistrationHistoryReport.statutes",
                        report.getStatuteTypes().stream().map(s -> s.getName().getContent()).collect(Collectors.joining(", ")));
                addData("RegistrationHistoryReport.regimeType", report.getRegimeType().getLocalizedName());
                addData("RegistrationHistoryReport.enrolmentsWithoutShifts", booleanString(report.hasEnrolmentsWithoutShifts()));
                addData("RegistrationHistoryReport.inactiveRegistrationStateForYear",
                        booleanString(report.hasAnyInactiveRegistrationStateForYear()));
                addData("RegistrationHistoryReport.lastRegistrationState",
                        report.getLastRegistrationState() != null ? report.getLastRegistrationState().getDescription() : null);
                addData("RegistrationHistoryReport.dismissals", booleanString(report.hasDismissals()));
                addData("RegistrationHistoryReport.enroledInImprovement", booleanString(report.hasImprovementEvaluations()));
                addData("RegistrationHistoryReport.annulledEnrolments", booleanString(report.hasAnnulledEnrolments()));
                addData("RegistrationHistoryReport.enrolmentsCount", report.getEnrolmentsCount());
                addData("RegistrationHistoryReport.enrolmentsCredits", report.getEnrolmentsCredits());
                addData("RegistrationHistoryReport.extraCurricularEnrolmentsCount", report.getExtraCurricularEnrolmentsCount());
                addData("RegistrationHistoryReport.extraCurricularEnrolmentsCredits",
                        report.getExtraCurricularEnrolmentsCredits());
                addData("RegistrationHistoryReport.standaloneEnrolmentsCount", report.getStandaloneEnrolmentsCount());
                addData("RegistrationHistoryReport.standaloneEnrolmentsCredits", report.getStandaloneEnrolmentsCredits());
                addData("RegistrationHistoryReport.executionYearSimpleAverage", report.getExecutionYearSimpleAverage());
                addData("RegistrationHistoryReport.executionYearWeightedAverage", report.getExecutionYearWeightedAverage());

                addData("Registration.registrationObservations",
                        registration.getRegistrationObservationsSet().stream()
                                .map(o -> o.getVersioningUpdatedBy().getUsername() + ":" + o.getValue())
                                .collect(Collectors.joining(" \n --------------\n ")));

                addConclusionData(report);

            }

            private void addConclusionData(RegistrationHistoryReport report) {

                //TODO: program conclusions should already be sorted
                final List<ProgramConclusion> sortedProgramConclusions = report.getProgramConclusions()
                        .stream().sorted(Comparator.comparing(ProgramConclusion::getName)
                                .thenComparing(ProgramConclusion::getDescription).thenComparing(ProgramConclusion::getExternalId))
                        .collect(Collectors.toList());

                for (final ProgramConclusion programConclusion : sortedProgramConclusions) {

                    final RegistrationConclusionBean bean = report.getConclusionReportFor(programConclusion);

                    final String concluded = bean == null ? null : booleanString(bean.isConcluded());
                    addCell(labelFor(programConclusion, "concluded"), concluded);

                    final String conclusionProcessed = bean == null ? null : booleanString(bean.isConclusionProcessed());
                    addCell(labelFor(programConclusion, "conclusionProcessed"), conclusionProcessed);

                    final String rawGrade = bean == null || bean.getRawGrade() == null ? null : bean.getRawGrade().getValue();
                    addCell(labelFor(programConclusion, "rawGrade"), rawGrade);

                    final String finalGrade =
                            bean == null || bean.getFinalGrade() == null ? null : bean.getFinalGrade().getValue();
                    addCell(labelFor(programConclusion, "finalGrade"), finalGrade);

                    final String descriptiveGrade =
                            bean == null || bean.getDescriptiveGrade() == null ? null : bean.getDescriptiveGradeExtendedValue()
                                    + " (" + bean.getDescriptiveGrade().getValue() + ")";
                    addCell(labelFor(programConclusion, "descriptiveGrade"), descriptiveGrade);

                    final YearMonthDay conclusionDate =
                            bean == null || bean.getConclusionDate() == null ? null : bean.getConclusionDate();
                    addCell(labelFor(programConclusion, "conclusionDate"), conclusionDate);

                    final String conclusionYear =
                            bean == null || bean.getConclusionYear() == null ? null : bean.getConclusionYear().getQualifiedName();
                    addCell(labelFor(programConclusion, "conclusionYear"), conclusionYear);

                    final String ectsCredits = bean == null ? null : String.valueOf(bean.getEctsCredits());
                    addCell(labelFor(programConclusion, "ectsCredits"), ectsCredits);

                }

            }

            private String labelFor(ProgramConclusion programConclusion, String field) {
                final String programConclusionPrefix =
                        programConclusion.getName().getContent() + " - " + programConclusion.getDescription().getContent() + ": ";

                return programConclusionPrefix + bundle("label.RegistrationConclusionBean." + field);

            }

            private void addSecondaryData(RegistrationHistoryReport registrationHistoryReport) {

                final Person person = registrationHistoryReport.getRegistration().getPerson();

                addData("Person.idDocumentType", person.getIdDocumentType().getLocalizedName());
                addData("Person.idDocumentNumber", person.getDocumentIdNumber());
                addData("Person.dateOfBirth", person.getDateOfBirthYearMonthDay());
                addData("Person.nameOfFather", person.getNameOfFather());
                addData("Person.nameOfMother", person.getNameOfMother());
                addData("Person.nationality", person.getCountry() != null ? person.getCountry().getName() : "");
                addData("Person.countryOfBirth", person.getCountryOfBirth() != null ? person.getCountryOfBirth().getName() : "");
                addData("Person.socialSecurityNumber", person.getSocialSecurityNumber());
                addData("Person.districtOfBirth", person.getDistrictOfBirth());
                addData("Person.districtSubdivisionOfBirth", person.getDistrictSubdivisionOfBirth());
                addData("Person.parishOfBirth", person.getParishOfBirth());

                addData("Student.studentPersonalDataAuthorizationChoice",
                        registrationHistoryReport.getStudentPersonalDataAuthorizationChoice() != null ? registrationHistoryReport
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
                    addData("PhysicalAddress.districtSubdivisionOfResidence", address.getDistrictSubdivisionOfResidence());
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
        builder.build(WorkbookExportFormat.EXCEL, result);

        return result.toByteArray();

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
}
