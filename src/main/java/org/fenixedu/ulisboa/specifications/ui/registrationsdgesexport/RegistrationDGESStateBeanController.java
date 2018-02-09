/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: nuno.pinheiro@qub-it.com
 *
 *
 * This file is part of FenixEdu ULisboaSpecifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui.registrationsdgesexport;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.AdmittedCandidacySituation;
import org.fenixedu.academic.domain.candidacy.CancelledCandidacySituation;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.candidacy.RegisteredCandidacySituation;
import org.fenixedu.academic.domain.candidacy.StandByCandidacySituation;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecificationsByExecutionYear;
import org.fenixedu.ulisboa.specifications.domain.UniversityChoiceMotivationAnswer;
import org.fenixedu.ulisboa.specifications.domain.UniversityDiscoveryMeansAnswer;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.registrationsDgesExport",
        accessGroup = "logged")
@RequestMapping(RegistrationDGESStateBeanController.CONTROLLER_URL)
public class RegistrationDGESStateBeanController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/list/student/registrationsdgesexport";

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping
    public String home(final Model model, final RedirectAttributes redirectAttributes) {
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(required = false) ExecutionYear executionYear,
            @RequestParam(required = false) Integer phase,
            @RequestParam(required = false) CandidacySituationType candidacySituationType,
            @RequestParam(required = false) IngressionType ingressType, @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate, @RequestParam(required = false) Boolean exportStatistics,
            final Model model, final RedirectAttributes redirectAttributes) {
        Map<String, ?> flashAttributes = redirectAttributes.getFlashAttributes();

        if (flashAttributes.get("executionYear") != null) {
            executionYear = (ExecutionYear) flashAttributes.get("executionYear");
        }
        if (flashAttributes.get("phase") != null) {
            phase = (Integer) flashAttributes.get("phase");
        }
        if (flashAttributes.get("candidacySituationType") != null) {
            candidacySituationType = (CandidacySituationType) flashAttributes.get("candidacySituationType");
        }
        if (flashAttributes.get("ingressType") != null) {
            ingressType = (IngressionType) flashAttributes.get("ingressType");
        }
        if (flashAttributes.get("beginDate") != null) {
            beginDate = (String) flashAttributes.get("beginDate");
        }
        if (flashAttributes.get("endDate") != null) {
            endDate = (String) flashAttributes.get("endDate");
        }
        if (flashAttributes.get("exportStatistics") != null) {
            exportStatistics = (Boolean) flashAttributes.get("exportStatistics");
        }
        LocalDate bd = null;
        LocalDate ed = null;
        if (StringUtils.isNotBlank(beginDate)) {
            bd = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(beginDate);
        }
        if (StringUtils.isNotBlank(endDate)) {
            ed = DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(endDate);
        }

        if (executionYear == null) {
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE,
                    "info.RegistrationDGESStateBeanController.executionYear.necessary.to.filter"), model);
        }
        List<RegistrationDGESStateBean> searchregistrationdgesstatebeanResultsDataSet =
                filterSearchRegistrationDGESStateBean(executionYear, phase, candidacySituationType, ingressType, bd, ed);

        model.addAttribute("searchregistrationdgesstatebeanResultsDataSet", searchregistrationdgesstatebeanResultsDataSet);
        model.addAttribute("executionYears", getExecutionYearDataSource());
        model.addAttribute("phases", getPhases());
        model.addAttribute("candidacyStates", getCandidacySituationTypeDataSource());
        model.addAttribute("ingressTypes", getIngressTypeDataSource());

        CandidacyToProcessBean bean = new CandidacyToProcessBean();
        model.addAttribute("objectBean", bean);
        model.addAttribute("objectBeanJson", getBeanJson(bean));

        model.addAttribute("selectedExecutionYear", executionYear);
        model.addAttribute("selectedPhase", phase);
        model.addAttribute("selectedCandidacyState", candidacySituationType);
        model.addAttribute("selectedIngressType", ingressType);
        model.addAttribute("exportStatistics", exportStatistics);
        return "registrationsdgesexport/search";
    }

    private List<RegistrationDGESStateBean> filterSearchRegistrationDGESStateBean(final ExecutionYear executionYear,
            final Integer phase, final CandidacySituationType candidacySituationType, final IngressionType ingressType,
            final LocalDate beginDate, final LocalDate endDate) {
        Predicate<? super StudentCandidacy> hasDgesImportationForCurrentPhase =
                sc -> phase == null || sc.getEntryPhase() != null && sc.getEntryPhase().getPhaseNumber() == phase;
        Predicate<? super StudentCandidacy> hasDgesImportationForCurrentState = sc -> candidacySituationType == null
                || sc.getActiveCandidacySituationType() != null && sc.getActiveCandidacySituationType() == candidacySituationType;
        Predicate<? super StudentCandidacy> hasDgesImportationForCurrentIngress =
                sc -> ingressType == null || sc.getIngressionType() != null && sc.getIngressionType().equals(ingressType);
        Predicate<? super StudentCandidacy> hasDgesImportationForBeforeEnd = sc -> endDate == null
                || sc.getCandidacyDate().toLocalDate().isEqual(endDate) || sc.getCandidacyDate().toLocalDate().isBefore(endDate);
        Predicate<? super StudentCandidacy> hasDgesImportationForAfterBegin =
                sc -> beginDate == null || sc.getCandidacyDate().toLocalDate().isEqual(beginDate)
                        || sc.getCandidacyDate().toLocalDate().isAfter(beginDate);
        Predicate<? super StudentCandidacy> hasDgesImportationForCurrentExecutionYear =
                sc -> executionYear == null || sc.getExecutionYear() == executionYear;
        return getAllStudentCandidacies(executionYear).stream().filter(hasDgesImportationForCurrentExecutionYear)
                .filter(hasDgesImportationForCurrentPhase).filter(hasDgesImportationForCurrentState)
                .filter(hasDgesImportationForCurrentIngress).filter(hasDgesImportationForAfterBegin)
                .filter(hasDgesImportationForBeforeEnd).map(sc -> populateBean(sc)).collect(Collectors.toList());
    }

    private Collection<Integer> getPhases() {
        EntryPhase[] values = EntryPhase.values();
        List<Integer> phases = new ArrayList<>();
        for (EntryPhase phase : values) {
            phases.add(phase.getPhaseNumber());
        }
        return phases;
    }

    private List<StudentCandidacy> getAllStudentCandidacies(final ExecutionYear executionYear) {
        if (executionYear == null) {
            return Collections.emptyList();
        }
        return Bennu.getInstance().getCandidaciesSet().stream().filter(c -> c instanceof FirstTimeCandidacy)
                .map(StudentCandidacy.class::cast).filter(c -> c.getRegistration() != null).collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> getExecutionYearDataSource() {
        return Bennu.getInstance().getExecutionYearsSet().stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).map(x -> {
            TupleDataSourceBean bean = new TupleDataSourceBean();
            bean.setId(x.getExternalId());
            bean.setText(x.getName());
            return bean;
        }).collect(Collectors.toList());
    }

    public List<CandidacySituationType> getCandidacySituationTypes() {
        List<CandidacySituationType> result = new ArrayList<>();

        result.add(CandidacySituationType.CANCELLED);
        result.add(CandidacySituationType.STAND_BY);
        result.add(CandidacySituationType.REGISTERED);

        return result;
    }

    public List<TupleDataSourceBean> getCandidacySituationTypeDataSource() {
        return getCandidacySituationTypes().stream().map(c -> {
            TupleDataSourceBean bean = new TupleDataSourceBean();
            bean.setId(c.toString());
            bean.setText(BundleUtil.getString("resources/EnumerationResources", c.getQualifiedName()));
            return bean;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getIngressTypeDataSource() {
        return Bennu.getInstance().getIngressionTypesSet().stream().map(x -> {
            TupleDataSourceBean bean = new TupleDataSourceBean();
            bean.setId(x.getExternalId());
            bean.setText(x.getLocalizedName());
            return bean;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public static final String CANCEL_URL = CONTROLLER_URL + "/cancel";

    @RequestMapping(value = "/cancel")
    public String cancelCandidacy(@RequestParam(value = "bean", required = true) final CandidacyToProcessBean bean,
            @RequestParam(required = false) final ExecutionYear executionYear,
            @RequestParam(required = false) final Integer phase,
            @RequestParam(required = false) final CandidacySituationType candidacySituationType,
            @RequestParam(required = false) final IngressionType ingressType,
            @RequestParam(required = false) final String beginDate, @RequestParam(required = false) final String endDate,
            @RequestParam(required = false) final Boolean exportStatistics, final Model model,
            final RedirectAttributes redirectAttributes) {

        List<String> errors = cancelCandidacies(bean.getCandidaciesToCancel());
        for (String error : errors) {
            addErrorMessage(error, model);
        }

        populateModel(executionYear, phase, candidacySituationType, ingressType, beginDate, endDate, exportStatistics,
                redirectAttributes);
        return redirect(CONTROLLER_URL, model, redirectAttributes);
    }

    @Atomic
    public List<String> cancelCandidacies(final List<StudentCandidacy> candidacies) {
        List<String> errors = new ArrayList<>();

        for (StudentCandidacy candidacy : candidacies) {
            String error = cancelCandidacy(candidacy);
            if (error != null) {
                errors.add(error);
            }
        }

        if (errors.isEmpty()) {
            for (StudentCandidacy candidacy : candidacies) {
                Registration registration = candidacy.getRegistration();
                if (registration != null) {
                    if (!registration.getActiveState().getStateType().equals(RegistrationStateType.INACTIVE)) {
                        RegistrationState registeredState = RegistrationState.createRegistrationState(registration,
                                AccessControl.getPerson(), new DateTime(), RegistrationStateType.INACTIVE);
                        registeredState.setRemarks(
                                BundleUtil.getString(BUNDLE, "label.RegistrationDGESState.registrationState.remarks"));
                    }
                }

                new CancelledCandidacySituation(candidacy);
            }
        }

        return errors;
    }

    private String cancelCandidacy(final StudentCandidacy candidacy) {
        if (candidacy.getActiveCandidacySituationType().equals(CandidacySituationType.CANCELLED)) {
            return BundleUtil.getString(BUNDLE, "error.RegistrationDGESState.already.cancelled",
                    candidacy.getPerson().getPresentationName());
        }

        Registration registration = candidacy.getRegistration();
        if (registration != null) {
            if (!registration.getEnrolments(candidacy.getExecutionYear()).isEmpty()) {
                return BundleUtil.getString(BUNDLE, "error.RegistrationDGESState.cannot.cancel.have.enrolments",
                        "" + registration.getNumber(), candidacy.getPerson().getName());
            }

            if (!registration.getShiftEnrolmentsSet().isEmpty()) {
                return BundleUtil.getString(BUNDLE, "error.RegistrationDGESState.cannot.cancel.have.shifts",
                        "" + registration.getNumber(), candidacy.getPerson().getName());
            }

            if (!registration.getShiftsSet().isEmpty()) {
                return BundleUtil.getString(BUNDLE, "error.RegistrationDGESState.cannot.cancel.have.shifts",
                        "" + registration.getNumber(), candidacy.getPerson().getName());
            }
        }

        return null;
    }

    private void populateModel(final ExecutionYear executionYear, final Integer phase,
            final CandidacySituationType candidacySituationType, final IngressionType ingressType, final String beginDate,
            final String endDate, final Boolean exportStatistics, final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("executionYear", executionYear);
        redirectAttributes.addFlashAttribute("phase", phase);
        redirectAttributes.addFlashAttribute("candidacySituationType", candidacySituationType);
        redirectAttributes.addFlashAttribute("ingressType", ingressType);
        redirectAttributes.addFlashAttribute("beginDate", beginDate);
        redirectAttributes.addFlashAttribute("endDate", endDate);
        redirectAttributes.addFlashAttribute("exportStatistics", exportStatistics);
    }

    public static final String REACTIVATE_URL = CONTROLLER_URL + "/reactivate";

    @RequestMapping(value = "/reactivate")
    public String reactivateCandidacy(@RequestParam("bean") final CandidacyToProcessBean bean,
            @RequestParam(required = false) final ExecutionYear executionYear,
            @RequestParam(required = false) final Integer phase,
            @RequestParam(required = false) final CandidacySituationType candidacySituationType,
            @RequestParam(required = false) final IngressionType ingressType,
            @RequestParam(required = false) final String beginDate, @RequestParam(required = false) final String endDate,
            @RequestParam(required = false) final Boolean exportStatistics, final Model model,
            final RedirectAttributes redirectAttributes) {

        List<String> errors = reactivateCandidacies(bean.getCandidaciesToReactivate());

        for (String error : errors) {
            addErrorMessage(error, model);
        }
        populateModel(executionYear, phase, candidacySituationType, ingressType, beginDate, endDate, exportStatistics,
                redirectAttributes);
        return redirect(CONTROLLER_URL, model, redirectAttributes);
    }

    @Atomic
    public List<String> reactivateCandidacies(final List<StudentCandidacy> candidacies) {
        List<String> errors = new ArrayList<>();

        for (StudentCandidacy candidacy : candidacies) {
            String result = reactivate(candidacy);
            if (result != null) {
                errors.add(result);
            }
        }

        if (errors.isEmpty()) {
            for (StudentCandidacy candidacy : candidacies) {
                new StandByCandidacySituation(candidacy);
            }
        }

        return errors;
    }

    public String reactivate(final StudentCandidacy candidacy) {
        if (candidacy.getActiveCandidacySituationType().equals(CandidacySituationType.STAND_BY)) {
            return BundleUtil.getString(BUNDLE, "error.RegistrationDGESState.already.standBy", candidacy.getPerson().getName());
        }

        return null;
    }

    public static final String REGISTER_URL = CONTROLLER_URL + "/register";

    @RequestMapping(value = "/register")
    public String registerCandidacy(@RequestParam("bean") final CandidacyToProcessBean bean,
            @RequestParam(required = false) final ExecutionYear executionYear,
            @RequestParam(required = false) final Integer phase,
            @RequestParam(required = false) final CandidacySituationType candidacySituationType,
            @RequestParam(required = false) final IngressionType ingressType,
            @RequestParam(required = false) final String beginDate, @RequestParam(required = false) final String endDate,
            @RequestParam(required = false) final Boolean exportStatistics, final Model model,
            final RedirectAttributes redirectAttributes) {

        List<String> errors = registerCandidacies(bean.getCandidaciesToRegister());
        for (String error : errors) {
            addErrorMessage(error, model);
        }
        populateModel(executionYear, phase, candidacySituationType, ingressType, beginDate, endDate, exportStatistics,
                redirectAttributes);
        return redirect(CONTROLLER_URL, model, redirectAttributes);

    }

    @Atomic
    public List<String> registerCandidacies(final List<StudentCandidacy> candidacies) {
        List<String> errors = new ArrayList<>();

        for (StudentCandidacy candidacy : candidacies) {
            String result = register(candidacy);
            if (result != null) {
                errors.add(result);
            }
        }

        if (errors.isEmpty()) {
            for (StudentCandidacy candidacy : candidacies) {
                AdmittedCandidacySituation situation = new AdmittedCandidacySituation(candidacy, AccessControl.getPerson());
                situation.setSituationDate(situation.getSituationDate().minusMinutes(1));

                new RegisteredCandidacySituation(candidacy, AccessControl.getPerson());
            }
        }

        return errors;
    }

    public String register(final StudentCandidacy candidacy) {
        if (candidacy.getActiveCandidacySituationType().equals(CandidacySituationType.REGISTERED)) {
            return BundleUtil.getString(BUNDLE, "error.RegistrationDGESState.already.registered",
                    candidacy.getPerson().getName());
        }

        Registration registration = candidacy.getRegistration();
        if (registration != null) {
            RegistrationState state = registration.getActiveState();
            if (!state.getStateType().equals(RegistrationStateType.REGISTERED)) {
                return BundleUtil.getString(BUNDLE, "error.RegistrationDGESState.not.registered",
                        "" + candidacy.getRegistration().getNumber(), candidacy.getPerson().getPresentationName(),
                        state.getStateType().getDescription());
            }
        }

        return null;
    }

    // TODO remove to DTO with RDGESSBean
    public static PhysicalAddress getSchoolTimePhysicalAddress(final Person person) {
        Predicate<PhysicalAddress> addressIsSchoolTime =
                address -> !address.isDefault() && address.isValid() && address.getType().equals(PartyContactType.PERSONAL);
        return person.getPhysicalAddresses().stream().filter(addressIsSchoolTime).sorted(CONTACT_COMPARATOR_BY_MODIFIED_DATE)
                .findFirst().orElse(null);
    }

    public static Comparator<PartyContact> CONTACT_COMPARATOR_BY_MODIFIED_DATE = new Comparator<PartyContact>() {
        @Override
        public int compare(final PartyContact contact, final PartyContact otherContact) {
            int result = contact.getLastModifiedDate().compareTo(otherContact.getLastModifiedDate());
            return result == 0 ? DomainObjectUtil.COMPARATOR_BY_ID.compare(contact, otherContact) : result;
        }
    };

    public static String getPrimaryBranchName(final StudentCurricularPlan studentCurricularPlan) {
        return studentCurricularPlan.getMajorBranchCurriculumGroups().stream().map(b -> b.getName().getContent())
                .collect(Collectors.joining(","));
    }

    /* TODO: Remove to DTO with RDGESSBean */
    public static RegistrationDGESStateBean populateBean(final StudentCandidacy studentCandidacy) {
        String executionYear = studentCandidacy.getExecutionYear().getQualifiedName();
        Person person = studentCandidacy.getPerson();
        StudentCurricularPlan studentCurricularPlan =
                studentCandidacy.getRegistration().getStudentCurricularPlan(studentCandidacy.getExecutionYear());
        String degreeTypeName = studentCandidacy.getDegreeCurricularPlan().getDegree().getDegreeTypeName();
        String degreeCode = studentCandidacy.getDegreeCurricularPlan().getDegree().getMinistryCode();
        String degreeName = studentCandidacy.getDegreeCurricularPlan().getDegree().getNameI18N().getContent();
        // TODO send to a bundle and to a method in Util
        String degreeLevel = "";
        if (degreeTypeName.contains("Licenciatura")) {
            degreeLevel = "Licenciado";
        } else if (degreeTypeName.contains("Mestrado")) {
            degreeLevel = "Mestre";
        } else if (degreeTypeName.contains("Doutoramento")) {
            degreeLevel = "Doutor";
        } else {
            degreeLevel = "Desconhecido";
        }

        String degreeBranch = "";
        if (studentCurricularPlan != null) {
            degreeBranch = getPrimaryBranchName(studentCurricularPlan);
        }

        RegistrationRegimeType regimeTypeObj =
                studentCandidacy.getRegistration().getRegimeType(studentCandidacy.getExecutionYear());
        String regimeType = "";
        if (regimeTypeObj != null) {
            regimeType = regimeTypeObj.getLocalizedName();
        }

        String documentIdNumber = person.getDocumentIdNumber();
        String candidacyState = BundleUtil.getString("resources/EnumerationResources",
                studentCandidacy.getActiveCandidacySituationType().getQualifiedName());
        String name = person.getName();
        String registrationStatus = "";
        if (studentCandidacy.getActiveCandidacySituationType().equals(CandidacySituationType.REGISTERED)) {
            registrationStatus = BundleUtil.getString(BUNDLE, "label.true");
        } else {
            registrationStatus = BundleUtil.getString(BUNDLE, "label.false");
        }

        Country nat = person.getCountry();
        String nationality = "";
        if (nat != null) {
            nationality = nat.getCountryNationality().getContent();
        }
        String secondNationality = "";
        String birthYear = "";
        if (person.getDateOfBirthYearMonthDay() != null) {
            birthYear += person.getDateOfBirthYearMonthDay().toString("dd-MM-yyyy");
        }

        Country cOfBirth = person.getCountryOfBirth();
        String countryOfBirth = "";
        String districtOfBirth = "";
        String districtSubdivisionOfBirth = "";
        String parishOfBirth = "";
        if (cOfBirth != null) {
            countryOfBirth = cOfBirth.getLocalizedName().getContent();
            if (cOfBirth.isDefaultCountry()) {
                districtOfBirth = person.getDistrictOfBirth();
                districtSubdivisionOfBirth = person.getDistrictSubdivisionOfBirth();
                parishOfBirth = person.getParishOfBirth();
            }
        }

        String countryOfResidence = "";
        String districtOfResidence = "";
        String districtSubdivisionOfResidence = "";
        String parishOfResidence = "";
        String addressOfResidence = "";
        String areaCodeOfResidence = "";
        if (person.getDefaultPhysicalAddress() != null) {
            countryOfResidence = person.getDefaultPhysicalAddress().getCountryOfResidenceName();
            districtOfResidence = person.getDefaultPhysicalAddress().getDistrictOfResidence();
            districtSubdivisionOfResidence = person.getDefaultPhysicalAddress().getDistrictSubdivisionOfResidence();
            parishOfResidence = person.getDefaultPhysicalAddress().getParishOfResidence();
            addressOfResidence = person.getDefaultPhysicalAddress().getAddress();
            areaCodeOfResidence = person.getDefaultPhysicalAddress().getAreaCode();
        }

        String countryOfDislocated = "";
        String districtOfDislocated = "";
        String districtSubdivisionOfDislocated = "";
        String parishOfDislocated = "";
        String addressOfDislocated = "";
        String areaCodeOfDislocated = "";
        PhysicalAddress dislocatedAddress = getSchoolTimePhysicalAddress(person);
        if (dislocatedAddress != null) {
            countryOfDislocated = dislocatedAddress.getCountryOfResidenceName();
            districtOfDislocated = dislocatedAddress.getDistrictOfResidence();
            districtSubdivisionOfDislocated = dislocatedAddress.getDistrictSubdivisionOfResidence();
            parishOfDislocated = dislocatedAddress.getParishOfResidence();
            addressOfDislocated = dislocatedAddress.getAddress();
            areaCodeOfDislocated = dislocatedAddress.getAreaCode();
        }

        String gender = person.getGender().getLocalizedName();
        String ingressionType = studentCandidacy.getIngressionType().getLocalizedName();
        Integer placingOption = studentCandidacy.getPlacingOption();
        String firstOptionDegree = "";
        String firstOptionInstitution = "";
        String isDislocated = "";
        String dislocatedResidenceType = "";
        String profession = person.getProfession();
        String professionTimeType = "";
        String professionalCondition = "";
        String professionType = "";
        String fatherName = person.getNameOfFather();
        String fatherSchoolLevel = "";
        String fatherProfessionalCondition = "";
        String fatherProfessionType = "";
        String motherName = person.getNameOfMother();
        String motherSchoolLevel = "";
        String motherProfessionalCondition = "";
        String motherProfessionType = "";
        String salarySpan = "";
        String disabilityType = "";
        String needsDisabilitySupport = "";
        String universityDiscoveryString = "";
        String universityChoiceString = "";

        Comparator<PersonalIngressionData> comparator =
                Collections.reverseOrder(PersonalIngressionData.COMPARATOR_BY_EXECUTION_YEAR);
        PersonalIngressionData pid =
                person.getStudent().getPersonalIngressionsDataSet().stream().sorted(comparator).findFirst().orElse(null);
        if (pid != null) {
            if (pid.getProfessionalCondition() != null) {
                professionalCondition = pid.getProfessionalCondition().getLocalizedName();
            }
            if (pid.getProfessionType() != null) {
                professionType = pid.getProfessionType().getLocalizedName();
            }

            if (pid.getFatherSchoolLevel() != null) {
                fatherSchoolLevel = pid.getFatherSchoolLevel().getLocalizedName();
            }
            if (pid.getFatherProfessionalCondition() != null) {
                fatherProfessionalCondition = pid.getFatherProfessionalCondition().getLocalizedName();
            }
            if (pid.getFatherProfessionType() != null) {
                fatherProfessionType = pid.getFatherProfessionType().getLocalizedName();
            }

            if (pid.getMotherSchoolLevel() != null) {
                motherSchoolLevel = pid.getMotherSchoolLevel().getLocalizedName();
            }
            if (pid.getMotherProfessionalCondition() != null) {
                motherProfessionalCondition = pid.getMotherProfessionalCondition().getLocalizedName();
            }
            if (pid.getMotherProfessionType() != null) {
                motherProfessionType = pid.getMotherProfessionType().getLocalizedName();
            }
        }
        PersonUlisboaSpecifications personUl = person.getPersonUlisboaSpecifications();
        if (personUl != null) {
            if (personUl.getSecondNationality() != null) {
                secondNationality = personUl.getSecondNationality().getCountryNationality().getContent();
            }

            if (personUl.getDislocatedResidenceType() != null) {
                dislocatedResidenceType = personUl.getDislocatedResidenceType().getLocalizedName();
                if (personUl.getDislocatedResidenceType().isOther()) {
                    dislocatedResidenceType = personUl.getOtherDislocatedResidenceType();
                }
                isDislocated = BundleUtil.getString(BUNDLE, "label.true");
            } else {
                isDislocated = BundleUtil.getString(BUNDLE, "label.false");
            }

            firstOptionDegree = personUl.getFirstOptionDegreeDesignation();
            if (personUl.getFirstOptionInstitution() != null) {
                firstOptionInstitution = personUl.getFirstOptionInstitution().getName();
            }

            if (personUl.getHasDisabilities()) {
                if (personUl.getDisabilityType().isOther()) {
                    disabilityType = personUl.getOtherDisabilityType();
                } else {
                    disabilityType = personUl.getDisabilityType().getLocalizedName();
                }

                needsDisabilitySupport = BundleUtil.getString("resources/FenixeduUlisboaSpecificationsResources",
                        "label." + personUl.getNeedsDisabilitySupport().toString());
            } else {
                disabilityType = BundleUtil.getString(BUNDLE, "label.false");
                needsDisabilitySupport = BundleUtil.getString(BUNDLE, "label.false");
            }

            for (UniversityDiscoveryMeansAnswer universityDiscovery : personUl.getUniversityDiscoveryMeansAnswersSet()) {
                universityDiscoveryString += universityDiscovery.getDescription().getContent() + "; ";
            }
            if (personUl.getOtherUniversityDiscoveryMeans() != null) {
                universityDiscoveryString += personUl.getOtherUniversityDiscoveryMeans();
            }

            for (UniversityChoiceMotivationAnswer universityChoice : personUl.getUniversityChoiceMotivationAnswersSet()) {
                universityChoiceString += universityChoice.getDescription().getContent() + "; ";
            }
            if (personUl.getOtherUniversityChoiceMotivation() != null) {
                universityChoiceString += personUl.getOtherUniversityChoiceMotivation();
            }

            PersonUlisboaSpecificationsByExecutionYear personUlExecutionYear =
                    personUl.getPersonUlisboaSpecificationsByExcutionYear(studentCandidacy.getExecutionYear());
            if (personUlExecutionYear != null) {

                if (personUlExecutionYear.getProfessionTimeType() != null) {
                    professionTimeType = personUlExecutionYear.getProfessionTimeType().getLocalizedName();
                }
                if (personUlExecutionYear.getHouseholdSalarySpan() != null) {
                    salarySpan = personUlExecutionYear.getHouseholdSalarySpan().getLocalizedName();
                }
            }

        }

        String precedentCountry = "";
        String precedentDistrict = "";
        String precedentDistrictSubdivision = "";
        String precedentSchoolLevel = "";
        String precedentInstitution = "";
        String precedentDegreeDesignation = "";
        String precedentConclusionGrade = "";
        String precedentConclusionYear = "";
        String precedentHighSchoolType = "";
        if (studentCandidacy.getPrecedentDegreeInformation() != null) {
            PrecedentDegreeInformation information = studentCandidacy.getPrecedentDegreeInformation();
            if (information.getCountry() != null) {
                precedentCountry = information.getCountry().getLocalizedName().getContent();
            }
            District district = information.getDistrict();
            if (district != null) {
                precedentDistrict = district.getName();
            }
            DistrictSubdivision districtSubdivision = information.getDistrictSubdivision();
            if (districtSubdivision != null) {
                precedentDistrictSubdivision = districtSubdivision.getName();
            }
            SchoolLevelType schoolLevelType = information.getSchoolLevel();
            if (schoolLevelType != null) {
                precedentSchoolLevel = schoolLevelType.getLocalizedName();
                if (schoolLevelType.isOther()) {
                    precedentSchoolLevel = information.getOtherSchoolLevel();
                }
            }
            precedentInstitution = information.getInstitutionName();
            precedentDegreeDesignation = information.getDegreeDesignation();

            precedentConclusionGrade = information.getConclusionGrade();
            precedentConclusionYear = "" + information.getConclusionYear();
            if (information.getPersonalIngressionData() != null
                    && information.getPersonalIngressionData().getHighSchoolType() != null) {
                AcademicalInstitutionType highSchoolType = information.getPersonalIngressionData().getHighSchoolType();
                precedentHighSchoolType = BundleUtil.getString(BUNDLE, highSchoolType.getName());
            }
        }

        Degree studentCandidacyDegree = studentCandidacy.getDegreeCurricularPlan().getDegree();
        String institutionName = "";
        String cycleName = "";
        if (studentCandidacyDegree != null) {
            Unit unit = studentCandidacyDegree.getUnit();
            if (unit != null) {
                List<Unit> units = studentCandidacyDegree.getUnit().getParentUnitsPath();
                if (units.size() != 0) {
                    institutionName = units.get(0).getName();
                }
            }
            cycleName = studentCandidacyDegree.getDegreeType().getFirstOrderedCycleType().getDescription();
        }

        String institutionalEmail = studentCandidacy.getPerson().getInstitutionalEmailAddressValue();
        String defaultEmail = "";
        if (!studentCandidacy.getPerson().getDefaultEmailAddressValue().equals(institutionalEmail)) {
            defaultEmail = studentCandidacy.getPerson().getDefaultEmailAddressValue();
        }

        String phone = studentCandidacy.getPerson().getDefaultPhoneNumber();
        String telephone = studentCandidacy.getPerson().getDefaultMobilePhoneNumber();

        String maritalStatus = studentCandidacy.getPerson().getMaritalStatus().getLocalizedName();

        String expirationDateOfIdDoc = "";
        if (studentCandidacy.getPerson().getExpirationDateOfDocumentIdYearMonthDay() != null) {
            expirationDateOfIdDoc =
                    studentCandidacy.getPerson().getExpirationDateOfDocumentIdYearMonthDay().toString("dd-MM-yyyy");
        }
        String emissionLocationOfIdDoc = "";
        if (studentCandidacy.getPerson().getEmissionLocationOfDocumentId() != null) {
            emissionLocationOfIdDoc = studentCandidacy.getPerson().getEmissionLocationOfDocumentId();
        }

        String vaccinationValidity = "";
        if (personUl != null && personUl.getVaccinationValidity() != null) {
            vaccinationValidity = personUl.getVaccinationValidity().toString("dd-MM-yyyy");
        }

        String curricularYear = "" + studentCandidacy.getRegistration().getCurricularYear(studentCandidacy.getExecutionYear());

        String grantOwnerType = "";
        if (studentCandidacy.getGrantOwnerType() != null) {
            grantOwnerType = BundleUtil.getString(BUNDLE, studentCandidacy.getGrantOwnerType().getQualifiedName());
        }
        String grantOwnerProvider = "";
        if (studentCandidacy.getGrantOwnerProvider() != null) {
            grantOwnerProvider = studentCandidacy.getGrantOwnerProvider().getName();
        }

        String precendentDegreeCycle = "";
        if (precedentSchoolLevel.contains("Bacharelato") || precedentSchoolLevel.contains("Licenciatura")) {
            precendentDegreeCycle = CycleType.FIRST_CYCLE.getDescription();
        } else if (precedentSchoolLevel.contains("Mestrado")) {
            precendentDegreeCycle = CycleType.SECOND_CYCLE.getDescription();
        } else {
            precendentDegreeCycle = "----";
        }

        return new RegistrationDGESStateBean(executionYear, studentCandidacy.getExternalId(), degreeTypeName, degreeCode,
                degreeName, cycleName, curricularYear, degreeLevel, degreeBranch, regimeType, institutionName, documentIdNumber,
                expirationDateOfIdDoc, emissionLocationOfIdDoc, candidacyState, name, maritalStatus, registrationStatus,
                nationality, secondNationality, birthYear, countryOfBirth, districtOfBirth, districtSubdivisionOfBirth,
                parishOfBirth, gender, ingressionType, placingOption, firstOptionDegree, firstOptionInstitution, isDislocated,
                dislocatedResidenceType, countryOfResidence, districtOfResidence, districtSubdivisionOfResidence,
                parishOfResidence, addressOfResidence, areaCodeOfResidence, countryOfDislocated, districtOfDislocated,
                districtSubdivisionOfDislocated, parishOfDislocated, addressOfDislocated, areaCodeOfDislocated, profession,
                professionTimeType, professionalCondition, professionType, fatherName, fatherSchoolLevel,
                fatherProfessionalCondition, fatherProfessionType, motherName, motherSchoolLevel, motherProfessionalCondition,
                motherProfessionType, salarySpan, disabilityType, needsDisabilitySupport, universityDiscoveryString,
                universityChoiceString, precedentCountry, precedentDistrict, precedentDistrictSubdivision, precedentSchoolLevel,
                precedentInstitution, precedentDegreeDesignation, precedentConclusionGrade, precedentConclusionYear,
                precedentHighSchoolType, precendentDegreeCycle, institutionalEmail, defaultEmail, phone, telephone,
                vaccinationValidity, grantOwnerType, grantOwnerProvider);
    }

    public static class CandidacyToProcessBean implements IBean {
        private List<StudentCandidacy> candidaciesToCancel;
        private List<StudentCandidacy> candidaciesToReactivate;
        private List<StudentCandidacy> candidaciesToRegister;

        public CandidacyToProcessBean() {

        }

        public List<StudentCandidacy> getCandidaciesToCancel() {
            return candidaciesToCancel;
        }

        public void setCandidaciesToCancel(final List<StudentCandidacy> candidaciesToCancel) {
            this.candidaciesToCancel = candidaciesToCancel;
        }

        public List<StudentCandidacy> getCandidaciesToReactivate() {
            return candidaciesToReactivate;
        }

        public void setCandidaciesToReactivate(final List<StudentCandidacy> candidaciesToReactivate) {
            this.candidaciesToReactivate = candidaciesToReactivate;
        }

        public List<StudentCandidacy> getCandidaciesToRegister() {
            return candidaciesToRegister;
        }

        public void setCandidaciesToRegister(final List<StudentCandidacy> candidaciesToRegister) {
            this.candidaciesToRegister = candidaciesToRegister;
        }
    }

    /* TODO: Extract to a file */
    public static class RegistrationDGESStateBean {
        private String executionYear;
        private String candidacyId;
        private String degreeTypeName;
        private String degreeCode;
        private String degreeName;
        private String cycleName;
        private String curricularYear;
        private String degreeLevel;
        private String degreeBranch;
        private String regimeType;
        private String institutionName;
        private String idNumber;
        private String expirationDateOfIdDoc;
        private String emissionLocationOfIdDoc;
        private String candidacyState;
        private String name;
        private String maritalStatus;
        private String registrationState;
        private String nationality;
        private String secondNationality;
        private String birthYear;
        private String countryOfBirth;
        private String districtOfBirth;
        private String districtSubdivisionOfBirth;
        private String parishOfBirth;
        private String gender;
        private String ingressionType;
        private Integer placingOption;
        private String firstOptionDegree;
        private String firstOptionInstitution;
        private String countryOfResidence;
        private String districtOfResidence;
        private String districtSubdivisionOfResidence;
        private String parishOfResidence;
        private String addressOfResidence;
        private String areaCodeOfResidence;
        private String isDislocated;
        private String dislocatedResidenceType;
        private String countryOfDislocated;
        private String districtOfDislocated;
        private String districtSubdivisionOfDislocated;
        private String parishOfDislocated;
        private String addressOfDislocated;
        private String areaCodeOfDislocated;
        private String profession;
        private String professionTimeType;
        private String professionalCondition;
        private String professionType;
        private String fatherName;
        private String fatherSchoolLevel;
        private String fatherProfessionalCondition;
        private String fatherProfessionType;
        private String motherName;
        private String motherSchoolLevel;
        private String motherProfessionalCondition;
        private String motherProfessionType;
        private String salarySpan;
        private String disabilityType;
        private String needsDisabilitySupport;
        private String universityDiscoveryString;
        private String universityChoiceString;
        private String precedentCountry;
        private String precedentDistrict;
        private String precedentDistrictSubdivision;
        private String precedentSchoolLevel;
        private String precedentInstitution;
        private String precedentDegreeDesignation;
        private String precedentConclusionGrade;
        private String precedentConclusionYear;
        private String precedentHighSchoolType;
        private String precendentDegreeCycle;
        private String institutionalEmail;
        private String defaultEmail;
        private String phone;
        private String telephone;
        private String vaccinationValidity;
        private String grantOwnerType;
        private String grantOwnerProvider;

        public RegistrationDGESStateBean(final String executionYear, final String candidacyId, final String degreeTypeName,
                final String degreeCode, final String degreeName, final String cycleName, final String curricularYear,
                final String degreeLevel, final String degreeBranch, final String regimeType, final String institutionName,
                final String idNumber, final String expirationDateOfIdDoc, final String emissionLocationOfIdDoc,
                final String candidacyState, final String name, final String maritalStatus, final String registrationState,
                final String nationality, final String secondNationality, final String birthYear, final String countryOfBirth,
                final String districtOfBirth, final String districtSubdivisionOfBirth, final String parishOfBirth,
                final String gender, final String ingressionType, final Integer placingOption, final String firstOptionDegree,
                final String firstOptionInstitution, final String isDislocated, final String dislocatedResidenceType,
                final String countryOfResidence, final String districtOfResidence, final String districtSubdivisionOfResidence,
                final String parishOfResidence, final String addressOfResidence, final String areaCodeOfResidence,
                final String countryOfDislocated, final String districtOfDislocated, final String districtSubdivisionOfDislocated,
                final String parishOfDislocated, final String addressOfDislocated, final String areaCodeOfDislocated,
                final String profession, final String professionTimeType, final String professionalCondition,
                final String professionType, final String fatherName, final String fatherSchoolLevel,
                final String fatherProfessionalCondition, final String fatherProfessionType, final String motherName,
                final String motherSchoolLevel, final String motherProfessionalCondition, final String motherProfessionType,
                final String salarySpan, final String disabilityType, final String needsDisabilitySupport,
                final String universityDiscoveryString, final String universityChoiceString, final String precedentCountry,
                final String precedentDistrict, final String precedentDistrictSubdivision, final String precedentSchoolLevel,
                final String precedentInstitution, final String precedentDegreeDesignation, final String precedentConclusionGrade,
                final String precedentConclusionYear, final String precedentHighSchoolType, final String precendentDegreeCycle,
                final String institutionalEmail, final String defaultEmail, final String phone, final String telephone,
                final String vaccinationValidity, final String grantOwnerType, final String grantOwnerProvider) {
            super();
            this.setExecutionYear(StringUtils.trim(executionYear));
            this.candidacyId = StringUtils.trim(candidacyId);
            this.setDegreeTypeName(StringUtils.trim(degreeTypeName));
            this.degreeCode = StringUtils.trim(degreeCode);
            this.setDegreeName(StringUtils.trim(degreeName));
            this.cycleName = StringUtils.trim(cycleName);
            this.curricularYear = StringUtils.trim(curricularYear);
            this.degreeLevel = StringUtils.trim(degreeLevel);
            this.degreeBranch = StringUtils.trim(degreeBranch);
            this.regimeType = StringUtils.trim(regimeType);
            this.institutionName = StringUtils.trim(institutionName);
            this.idNumber = StringUtils.trim(idNumber);
            this.expirationDateOfIdDoc = StringUtils.trim(expirationDateOfIdDoc);
            this.emissionLocationOfIdDoc = StringUtils.trim(emissionLocationOfIdDoc);
            this.candidacyState = StringUtils.trim(candidacyState);
            this.name = StringUtils.trim(name);
            this.maritalStatus = StringUtils.trim(maritalStatus);
            this.registrationState = StringUtils.trim(registrationState);
            this.nationality = StringUtils.trim(nationality);
            this.secondNationality = StringUtils.trim(secondNationality);
            this.setBirthYear(StringUtils.trim(birthYear));
            this.setGender(StringUtils.trim(gender));
            this.countryOfBirth = StringUtils.trim(countryOfBirth);
            this.districtOfBirth = StringUtils.trim(districtOfBirth);
            this.districtSubdivisionOfBirth = StringUtils.trim(districtSubdivisionOfBirth);
            this.parishOfBirth = StringUtils.trim(parishOfBirth);
            this.ingressionType = StringUtils.trim(ingressionType);
            this.placingOption = placingOption;
            this.firstOptionDegree = StringUtils.trim(firstOptionDegree);
            this.firstOptionInstitution = StringUtils.trim(firstOptionInstitution);
            this.isDislocated = StringUtils.trim(isDislocated);
            this.dislocatedResidenceType = StringUtils.trim(dislocatedResidenceType);
            this.countryOfResidence = StringUtils.trim(countryOfResidence);
            this.districtOfResidence = StringUtils.trim(districtOfResidence);
            this.districtSubdivisionOfResidence = StringUtils.trim(districtSubdivisionOfResidence);
            this.parishOfResidence = StringUtils.trim(parishOfResidence);
            this.addressOfResidence = StringUtils.trim(addressOfResidence);
            this.areaCodeOfResidence = StringUtils.trim(areaCodeOfResidence);
            this.countryOfDislocated = StringUtils.trim(countryOfDislocated);
            this.districtOfDislocated = StringUtils.trim(districtOfDislocated);
            this.districtSubdivisionOfDislocated = StringUtils.trim(districtSubdivisionOfDislocated);
            this.parishOfDislocated = StringUtils.trim(parishOfDislocated);
            this.addressOfDislocated = StringUtils.trim(addressOfDislocated);
            this.areaCodeOfDislocated = StringUtils.trim(areaCodeOfDislocated);
            this.profession = StringUtils.trim(profession);
            this.professionTimeType = StringUtils.trim(professionTimeType);
            this.professionalCondition = StringUtils.trim(professionalCondition);
            this.professionType = StringUtils.trim(professionType);
            this.fatherName = StringUtils.trim(fatherName);
            this.fatherSchoolLevel = StringUtils.trim(fatherSchoolLevel);
            this.fatherProfessionalCondition = StringUtils.trim(fatherProfessionalCondition);
            this.fatherProfessionType = StringUtils.trim(fatherProfessionType);
            this.motherName = StringUtils.trim(motherName);
            this.motherSchoolLevel = StringUtils.trim(motherSchoolLevel);
            this.motherProfessionalCondition = StringUtils.trim(motherProfessionalCondition);
            this.motherProfessionType = StringUtils.trim(motherProfessionType);
            this.salarySpan = StringUtils.trim(salarySpan);
            this.disabilityType = StringUtils.trim(disabilityType);
            this.needsDisabilitySupport = StringUtils.trim(needsDisabilitySupport);
            this.universityDiscoveryString = StringUtils.trim(universityDiscoveryString);
            this.universityChoiceString = StringUtils.trim(universityChoiceString);
            this.precedentCountry = StringUtils.trim(precedentCountry);
            this.precedentDistrict = StringUtils.trim(precedentDistrict);
            this.precedentDistrictSubdivision = StringUtils.trim(precedentDistrictSubdivision);
            this.precedentSchoolLevel = StringUtils.trim(precedentSchoolLevel);
            this.precedentInstitution = StringUtils.trim(precedentInstitution);
            this.precedentDegreeDesignation = StringUtils.trim(precedentDegreeDesignation);
            this.precedentConclusionGrade = StringUtils.trim(precedentConclusionGrade);
            this.precedentConclusionYear = StringUtils.trim(precedentConclusionYear);
            this.precedentHighSchoolType = StringUtils.trim(precedentHighSchoolType);
            this.precendentDegreeCycle = StringUtils.trim(precendentDegreeCycle);
            this.institutionalEmail = StringUtils.trim(institutionalEmail);
            this.defaultEmail = StringUtils.trim(defaultEmail);
            this.phone = StringUtils.trim(phone);
            this.telephone = StringUtils.trim(telephone);
            this.vaccinationValidity = StringUtils.trim(vaccinationValidity);
            this.grantOwnerType = StringUtils.trim(grantOwnerType);
            this.grantOwnerProvider = StringUtils.trim(grantOwnerProvider);
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(final String idNumber) {
            this.idNumber = idNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getRegistrationState() {
            return registrationState;
        }

        public void setRegistrationState(final String registrationState) {
            this.registrationState = registrationState;
        }

        public String getDegreeCode() {
            return degreeCode;
        }

        public void setDegreeCode(final String degreeCode) {
            this.degreeCode = degreeCode;
        }

        public String getCandidacyState() {
            return candidacyState;
        }

        public void setCandidacyState(final String candidacyState) {
            this.candidacyState = candidacyState;
        }

        public String getCandidacyId() {
            return candidacyId;
        }

        public void setCandidacyId(final String candidacyId) {
            this.candidacyId = candidacyId;
        }

        public Integer getPlacingOption() {
            return placingOption;
        }

        public void setPlacingOption(final Integer placingOption) {
            this.placingOption = placingOption;
        }

        public String getFirstOptionDegree() {
            return firstOptionDegree;
        }

        public void setFirstOptionDegree(final String firstOptionDegree) {
            this.firstOptionDegree = firstOptionDegree;
        }

        public String getFirstOptionInstitution() {
            return firstOptionInstitution;
        }

        public void setFirstOptionInstitution(final String firstOptionInstitution) {
            this.firstOptionInstitution = firstOptionInstitution;
        }

        public String getIngressionType() {
            return ingressionType;
        }

        public void setIngressionType(final String ingressionType) {
            this.ingressionType = ingressionType;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(final String nationality) {
            this.nationality = nationality;
        }

        public String getSecondNationality() {
            return secondNationality;
        }

        public void setSecondNationality(final String secondNationality) {
            this.secondNationality = secondNationality;
        }

        public String getDislocatedResidenceType() {
            return dislocatedResidenceType;
        }

        public void setDislocatedResidenceType(final String dislocatedResidenceType) {
            this.dislocatedResidenceType = dislocatedResidenceType;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(final String profession) {
            this.profession = profession;
        }

        public String getProfessionTimeType() {
            return professionTimeType;
        }

        public void setProfessionTimeType(final String professionTimeType) {
            this.professionTimeType = professionTimeType;
        }

        public String getProfessionalCondition() {
            return professionalCondition;
        }

        public void setProfessionalCondition(final String professionalCondition) {
            this.professionalCondition = professionalCondition;
        }

        public String getProfessionType() {
            return professionType;
        }

        public void setProfessionType(final String professionType) {
            this.professionType = professionType;
        }

        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(final String fatherName) {
            this.fatherName = fatherName;
        }

        public String getFatherSchoolLevel() {
            return fatherSchoolLevel;
        }

        public void setFatherSchoolLevel(final String fatherSchoolLevel) {
            this.fatherSchoolLevel = fatherSchoolLevel;
        }

        public String getFatherProfessionalCondition() {
            return fatherProfessionalCondition;
        }

        public void setFatherProfessionalCondition(final String fatherProfessionalCondition) {
            this.fatherProfessionalCondition = fatherProfessionalCondition;
        }

        public String getFatherProfessionType() {
            return fatherProfessionType;
        }

        public void setFatherProfessionType(final String fatherProfessionType) {
            this.fatherProfessionType = fatherProfessionType;
        }

        public String getMotherName() {
            return motherName;
        }

        public void setMotherName(final String motherName) {
            this.motherName = motherName;
        }

        public String getMotherSchoolLevel() {
            return motherSchoolLevel;
        }

        public void setMotherSchoolLevel(final String motherSchoolLevel) {
            this.motherSchoolLevel = motherSchoolLevel;
        }

        public String getMotherProfessionalCondition() {
            return motherProfessionalCondition;
        }

        public void setMotherProfessionalCondition(final String motherProfessionalCondition) {
            this.motherProfessionalCondition = motherProfessionalCondition;
        }

        public String getMotherProfessionType() {
            return motherProfessionType;
        }

        public void setMotherProfessionType(final String motherProfessionType) {
            this.motherProfessionType = motherProfessionType;
        }

        public String getSalarySpan() {
            return salarySpan;
        }

        public void setSalarySpan(final String salarySpan) {
            this.salarySpan = salarySpan;
        }

        public String getDisabilityType() {
            return disabilityType;
        }

        public void setDisabilityType(final String disabilityType) {
            this.disabilityType = disabilityType;
        }

        public String getNeedsDisabilitySupport() {
            return needsDisabilitySupport;
        }

        public void setNeedsDisabilitySupport(final String needsDisabilitySupport) {
            this.needsDisabilitySupport = needsDisabilitySupport;
        }

        public String getUniversityDiscoveryString() {
            return universityDiscoveryString;
        }

        public void setUniversityDiscoveryString(final String universityDiscoveryString) {
            this.universityDiscoveryString = universityDiscoveryString;
        }

        public String getUniversityChoiceString() {
            return universityChoiceString;
        }

        public void setUniversityChoiceString(final String universityChoiceString) {
            this.universityChoiceString = universityChoiceString;
        }

        public String getExecutionYear() {
            return executionYear;
        }

        public void setExecutionYear(final String executionYear) {
            this.executionYear = executionYear;
        }

        public String getDegreeTypeName() {
            return degreeTypeName;
        }

        public void setDegreeTypeName(final String degreeTypeName) {
            this.degreeTypeName = degreeTypeName;
        }

        public String getDegreeName() {
            return degreeName;
        }

        public void setDegreeName(final String degreeName) {
            this.degreeName = degreeName;
        }

        public String getBirthYear() {
            return birthYear;
        }

        public void setBirthYear(final String birthYear) {
            this.birthYear = birthYear;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(final String gender) {
            this.gender = gender;
        }

        public String getCountryOfBirth() {
            return countryOfBirth;
        }

        public void setCountryOfBirth(final String countryOfBirth) {
            this.countryOfBirth = countryOfBirth;
        }

        public String getDistrictOfBirth() {
            return districtOfBirth;
        }

        public void setDistrictOfBirth(final String districtOfBirth) {
            this.districtOfBirth = districtOfBirth;
        }

        public String getDistrictSubdivisionOfBirth() {
            return districtSubdivisionOfBirth;
        }

        public void setDistrictSubdivisionOfBirth(final String districtSubdivisionOfBirth) {
            this.districtSubdivisionOfBirth = districtSubdivisionOfBirth;
        }

        public String getParishOfBirth() {
            return parishOfBirth;
        }

        public void setParishOfBirth(final String parishOfBirth) {
            this.parishOfBirth = parishOfBirth;
        }

        public String getIsDislocated() {
            return isDislocated;
        }

        public void setIsDislocated(final String isDislocated) {
            this.isDislocated = isDislocated;
        }

        public String getCountryOfResidence() {
            return countryOfResidence;
        }

        public void setCountryOfResidence(final String countryOfResidence) {
            this.countryOfResidence = countryOfResidence;
        }

        public String getDistrictOfResidence() {
            return districtOfResidence;
        }

        public void setDistrictOfResidence(final String districtOfResidence) {
            this.districtOfResidence = districtOfResidence;
        }

        public String getDistrictSubdivisionOfResidence() {
            return districtSubdivisionOfResidence;
        }

        public void setDistrictSubdivisionOfResidence(final String districtSubdivisionOfResidence) {
            this.districtSubdivisionOfResidence = districtSubdivisionOfResidence;
        }

        public String getParishOfResidence() {
            return parishOfResidence;
        }

        public void setParishOfResidence(final String parishOfResidence) {
            this.parishOfResidence = parishOfResidence;
        }

        public String getPrecedentCountry() {
            return precedentCountry;
        }

        public void setPrecedentCountry(final String precedentCountry) {
            this.precedentCountry = precedentCountry;
        }

        public String getPrecedentDistrict() {
            return precedentDistrict;
        }

        public void setPrecedentDistrict(final String precedentDistrict) {
            this.precedentDistrict = precedentDistrict;
        }

        public String getPrecedentDistrictSubdivision() {
            return precedentDistrictSubdivision;
        }

        public void setPrecedentDistrictSubdivision(final String precedentDistrictSubdivision) {
            this.precedentDistrictSubdivision = precedentDistrictSubdivision;
        }

        public String getPrecedentSchoolLevel() {
            return precedentSchoolLevel;
        }

        public void setPrecedentSchoolLevel(final String precedentSchoolLevel) {
            this.precedentSchoolLevel = precedentSchoolLevel;
        }

        public String getPrecedentInstitution() {
            return precedentInstitution;
        }

        public void setPrecedentInstitution(final String precedentInstitution) {
            this.precedentInstitution = precedentInstitution;
        }

        public String getPrecedentDegreeDesignation() {
            return precedentDegreeDesignation;
        }

        public void setPrecedentDegreeDesignation(final String precedentDegreeDesignation) {
            this.precedentDegreeDesignation = precedentDegreeDesignation;
        }

        public String getPrecedentConclusionGrade() {
            return precedentConclusionGrade;
        }

        public void setPrecedentConclusionGrade(final String precedentConclusionGrade) {
            this.precedentConclusionGrade = precedentConclusionGrade;
        }

        public String getPrecedentConclusionYear() {
            return precedentConclusionYear;
        }

        public void setPrecedentConclusionYear(final String precedentConclusionYear) {
            this.precedentConclusionYear = precedentConclusionYear;
        }

        public String getPrecedentHighSchoolType() {
            return precedentHighSchoolType;
        }

        public void setPrecedentHighSchoolType(final String precedentHighSchoolType) {
            this.precedentHighSchoolType = precedentHighSchoolType;
        }

        public String getAddressOfResidence() {
            return addressOfResidence;
        }

        public void setAddressOfResidence(final String addressOfResidence) {
            this.addressOfResidence = addressOfResidence;
        }

        public String getAreaCodeOfResidence() {
            return areaCodeOfResidence;
        }

        public void setAreaCodeOfResidence(final String areaCodeOfResidence) {
            this.areaCodeOfResidence = areaCodeOfResidence;
        }

        public String getCountryOfDislocated() {
            return countryOfDislocated;
        }

        public void setCountryOfDislocated(final String countryOfDislocated) {
            this.countryOfDislocated = countryOfDislocated;
        }

        public String getDistrictOfDislocated() {
            return districtOfDislocated;
        }

        public void setDistrictOfDislocated(final String districtOfDislocated) {
            this.districtOfDislocated = districtOfDislocated;
        }

        public String getDistrictSubdivisionOfDislocated() {
            return districtSubdivisionOfDislocated;
        }

        public void setDistrictSubdivisionOfDislocated(final String districtSubdivisionOfDislocated) {
            this.districtSubdivisionOfDislocated = districtSubdivisionOfDislocated;
        }

        public String getParishOfDislocated() {
            return parishOfDislocated;
        }

        public void setParishOfDislocated(final String parishOfDislocated) {
            this.parishOfDislocated = parishOfDislocated;
        }

        public String getAddressOfDislocated() {
            return addressOfDislocated;
        }

        public void setAddressOfDislocated(final String addressOfDislocated) {
            this.addressOfDislocated = addressOfDislocated;
        }

        public String getAreaCodeOfDislocated() {
            return areaCodeOfDislocated;
        }

        public void setAreaCodeOfDislocated(final String areaCodeOfDislocated) {
            this.areaCodeOfDislocated = areaCodeOfDislocated;
        }

        public String getExpirationDateOfIdDoc() {
            return expirationDateOfIdDoc;
        }

        public void setExpirationDateOfIdDoc(final String expirationDateOfIdDoc) {
            this.expirationDateOfIdDoc = expirationDateOfIdDoc;
        }

        public String getEmissionLocationOfIdDoc() {
            return emissionLocationOfIdDoc;
        }

        public void setEmissionLocationOfIdDoc(final String emissionLocationOfIdDoc) {
            this.emissionLocationOfIdDoc = emissionLocationOfIdDoc;
        }

        public String getMaritalStatus() {
            return maritalStatus;
        }

        public void setMaritalStatus(final String maritalStatus) {
            this.maritalStatus = maritalStatus;
        }

        public String getInstitutionalEmail() {
            return institutionalEmail;
        }

        public void setInstitutionalEmail(final String institutionalEmail) {
            this.institutionalEmail = institutionalEmail;
        }

        public String getDefaultEmail() {
            return defaultEmail;
        }

        public void setDefaultEmail(final String defaultEmail) {
            this.defaultEmail = defaultEmail;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(final String phone) {
            this.phone = phone;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(final String telephone) {
            this.telephone = telephone;
        }

        public String getVaccinationValidity() {
            return vaccinationValidity;
        }

        public void setVaccinationValidity(final String vaccinationValidity) {
            this.vaccinationValidity = vaccinationValidity;
        }

        public String getGrantOwnerType() {
            return grantOwnerType;
        }

        public void setGrantOwnerType(final String grantOwnerType) {
            this.grantOwnerType = grantOwnerType;
        }

        public String getGrantOwnerProvider() {
            return grantOwnerProvider;
        }

        public void setGrantOwnerProvider(final String grantOwnerProvider) {
            this.grantOwnerProvider = grantOwnerProvider;
        }

        public String getCycleName() {
            return cycleName;
        }

        public void setCycleName(final String cycleName) {
            this.cycleName = cycleName;
        }

        public String getCurricularYear() {
            return curricularYear;
        }

        public void setCurricularYear(final String curricularYear) {
            this.curricularYear = curricularYear;
        }

        public String getInstitutionName() {
            return institutionName;
        }

        public void setInstitutionName(final String institutionName) {
            this.institutionName = institutionName;
        }

        public String getPrecendentDegreeCycle() {
            return precendentDegreeCycle;
        }

        public void setPrecendentDegreeCycle(final String precendentDegreeCycle) {
            this.precendentDegreeCycle = precendentDegreeCycle;
        }

        public String getDegreeLevel() {
            return degreeLevel;
        }

        public void setDegreeLevel(final String degreeLevel) {
            this.degreeLevel = degreeLevel;
        }

        public String getDegreeBranch() {
            return degreeBranch;
        }

        public void setDegreeBranch(final String degreeBranch) {
            this.degreeBranch = degreeBranch;
        }

        public String getRegimeType() {
            return regimeType;
        }

        public void setRegimeType(final String regimeType) {
            this.regimeType = regimeType;
        }
    }
}
