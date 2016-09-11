package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Comparator;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.contacts.PhysicalAddressData;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.contacts.ContactsFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;
import pt.ist.standards.geographic.Planet;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(ResidenceInformationFormController.CONTROLLER_URL)
public class ResidenceInformationFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/residenceinformationform";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "residenceInformationForm";
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {

        ResidenceInformationForm form = fillFormIfRequired(executionYear, model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillResidenceInformation.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/residenceinformationform/fillresidenceinformation";
    }

    private ResidenceInformationForm fillFormIfRequired(ExecutionYear executionYear, Model model) {
        ResidenceInformationForm form = (ResidenceInformationForm) getForm(model);
        if (form == null) {
            form = createResidenceInformationForm(getStudent(model), executionYear, false);

            setForm(form, model);
        }
        return form;
    }

    private ResidenceInformationForm createResidenceInformationForm(Student student, ExecutionYear executionYear,
            boolean create) {
        StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        final PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, create);
        Person person = AccessControl.getPerson();
        PersonUlisboaSpecifications personUl = person.getPersonUlisboaSpecifications();

        ResidenceInformationForm form = new ResidenceInformationForm();
        form.setCountryOfResidence(personalData.getCountryOfResidence());
        if (form.getCountryOfResidence() == null) {
            form.setCountryOfResidence(Country.readDefault());
        }

        District district = personalData.getDistrictSubdivisionOfResidence() != null ? personalData
                .getDistrictSubdivisionOfResidence().getDistrict() : null;
        form.setDistrictOfResidence(district);

        DistrictSubdivision districtSubdivisionOfResidence = personalData.getDistrictSubdivisionOfResidence();
        form.setDistrictSubdivisionOfResidence(districtSubdivisionOfResidence);

        PhysicalAddress defaultPhysicalAddress = person.getDefaultPhysicalAddress();
        if (defaultPhysicalAddress != null) {
            form.setAddress(defaultPhysicalAddress.getAddress());
            form.setAreaCode(defaultPhysicalAddress.getAreaCode() + " " + defaultPhysicalAddress.getAreaOfAreaCode());
            form.setArea(defaultPhysicalAddress.getArea());
            form.setParishOfResidence(
                    Parish.findByName(districtSubdivisionOfResidence, person.getDefaultPhysicalAddress().getParishOfResidence())
                            .orElse(null));
        }

        form.setDislocatedFromPermanentResidence(personalData.getDislocatedFromPermanentResidence());
        if (personalData.getDislocatedFromPermanentResidence() == Boolean.TRUE) {
            district = personalData.getSchoolTimeDistrictSubDivisionOfResidence() != null ? personalData
                    .getSchoolTimeDistrictSubDivisionOfResidence().getDistrict() : null;
            form.setSchoolTimeDistrictOfResidence(district);
            DistrictSubdivision schoolTimeDistrictSubDivisionOfResidence =
                    personalData.getSchoolTimeDistrictSubDivisionOfResidence();
            form.setSchoolTimeDistrictSubdivisionOfResidence(schoolTimeDistrictSubDivisionOfResidence);
            if (getSchoolTimePhysicalAddress(person) != null) {
                PhysicalAddress addressSchoolTime = getSchoolTimePhysicalAddress(person);
                form.setSchoolTimeAddress(addressSchoolTime.getAddress());
                form.setSchoolTimeAreaCode(addressSchoolTime.getAreaCode() + " " + addressSchoolTime.getAreaOfAreaCode());
                form.setSchoolTimeArea(addressSchoolTime.getArea());
                form.setSchoolTimeParishOfResidence(
                        Parish.findByName(schoolTimeDistrictSubDivisionOfResidence, addressSchoolTime.getParishOfResidence())
                                .orElse(null));
            }
            if (personUl != null) {
                form.setSchoolTimeResidenceType(personUl.getDislocatedResidenceType());
                form.setOtherSchoolTimeResidenceType(personUl.getOtherDislocatedResidenceType());
            }
        }
        return form;
    }

    private PhysicalAddress getSchoolTimePhysicalAddress(Person person) {
        Predicate<PhysicalAddress> addressIsSchoolTime =
                address -> !address.isDefault() && address.isValid() && address.getType().equals(PartyContactType.PERSONAL);
        return person.getPhysicalAddresses().stream().filter(addressIsSchoolTime).sorted(CONTACT_COMPARATOR_BY_MODIFIED_DATE)
                .findFirst().orElse(null);
    }

    public static Comparator<PartyContact> CONTACT_COMPARATOR_BY_MODIFIED_DATE = new Comparator<PartyContact>() {
        @Override
        public int compare(PartyContact contact, PartyContact otherContact) {
            int result = contact.getLastModifiedDate().compareTo(otherContact.getLastModifiedDate());
            return result == 0 ? DomainObjectUtil.COMPARATOR_BY_ID.compare(contact, otherContact) : result;
        }
    };

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {
        //nothing
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof ResidenceInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.ResidenceInformationForm.wrong.form.type"), model);
        }

        return validate((ResidenceInformationForm) candidancyForm, model);
    }

    private boolean validate(ResidenceInformationForm form, Model model) {
        if (!form.getCountryOfResidence().isDefaultCountry() && !form.getDislocatedFromPermanentResidence()) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                            "error.candidacy.workflow.ResidenceInformationForm.non.nacional.students.should.select.dislocated.option.and.fill.address"),
                    model);
            return false;
        }
        if (form.getCountryOfResidence().isDefaultCountry() && !form.isResidenceInformationFilled()) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                            "error.candidacy.workflow.ResidenceInformationForm.address.national.students.should.supply.complete.address.information"),
                    model);
            return false;
        }
        if (form.getCountryOfResidence().isDefaultCountry() && StringUtils.isEmpty(form.getAreaCode())) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.areaCode"),
                    model);
            return false;
        }

        if (form.getDislocatedFromPermanentResidence()) {
            if (!form.isSchoolTimeRequiredInformationAddressFilled()) {
                addErrorMessage(
                        BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                                "error.candidacy.workflow.ResidenceInformationForm.address.information.is.required.for.dislocated.students"),
                        model);
                return false;
            } else {
                if ((form.isAnyFilled(form.getSchoolTimeAddress(), form.getSchoolTimeAreaCode(), form.getSchoolTimeArea())
                        || form.getSchoolTimeParishOfResidence() != null || form.getSchoolTimeResidenceType() != null)
                        && (form.isAnyEmpty(form.getSchoolTimeAddress(), form.getSchoolTimeAreaCode(), form.getSchoolTimeArea())
                                || form.getSchoolTimeParishOfResidence() == null || form.getSchoolTimeResidenceType() == null)) {
                    addErrorMessage(
                            BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                                    "error.candidacy.workflow.ResidenceInformationForm.school.time.address.must.be.filled.completly.otherwise.fill.minimun.required"),
                            model);
                    return false;
                }
            }

            if (form.getSchoolTimeResidenceType() != null && form.getSchoolTimeResidenceType().isOther()
                    && StringUtils.isEmpty(form.getOtherSchoolTimeResidenceType())) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.ResidenceInformationForm.other.residence.type.required"), model);
                return false;
            }
        }

        return true;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData(executionYear, (ResidenceInformationForm) candidancyForm);
    }

    @Atomic
    protected void writeData(final ExecutionYear executionYear, ResidenceInformationForm form) {
        Person person = AccessControl.getPerson();
        Student student = person.getStudent();
        PersonUlisboaSpecifications personUl = PersonUlisboaSpecifications.findOrCreate(person);
        StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, false);
        personalData.setCountryOfResidence(form.getCountryOfResidence());
        personalData.setDistrictSubdivisionOfResidence(form.getDistrictSubdivisionOfResidence());
        personalData.setDislocatedFromPermanentResidence(form.getDislocatedFromPermanentResidence());

        if (form.getDislocatedFromPermanentResidence()) {
            personalData.setSchoolTimeDistrictSubDivisionOfResidence(form.getSchoolTimeDistrictSubdivisionOfResidence());
        }

        String district = form.getDistrictSubdivisionOfResidence() != null ? form.getDistrictSubdivisionOfResidence()
                .getDistrict().getName() : null;
        String subdivision =
                form.getDistrictSubdivisionOfResidence() != null ? form.getDistrictSubdivisionOfResidence().getName() : null;
        PhysicalAddressData physicalAddressData;
        if (!StringUtils.equals(form.getAddress(), person.getDefaultPhysicalAddress().getAddress())
                || !StringUtils.equals(form.getAreaCode(), person.getDefaultPhysicalAddress().getAreaCode())
                || !StringUtils.equals(form.getArea(), person.getDefaultPhysicalAddress().getArea())
                || !StringUtils.equals(form.getParishOfResidence().getName(),
                        person.getDefaultPhysicalAddress().getParishOfResidence())
                || !StringUtils.equals(subdivision, person.getDefaultPhysicalAddress().getDistrictSubdivisionOfResidence())
                || !StringUtils.equals(district, person.getDefaultPhysicalAddress().getDistrictOfResidence())
                || form.getCountryOfResidence() != person.getDefaultPhysicalAddress().getCountryOfResidence()) {
            Planet.getEarth().getPlace("PRT").getPostalCode(form.getAreaCode());
            String areaCode = "";
            String areaOfAreaCode = "";
            if (form.getAreaCode() != null) {
                areaCode = form.getAreaCode().substring(0, 8);
                areaOfAreaCode = form.getAreaCode().substring(9);
            }
            Parish parishOfResidence = form.getParishOfResidence();
            physicalAddressData = new PhysicalAddressData(form.getAddress(), areaCode, areaOfAreaCode, form.getArea(),
                    parishOfResidence != null ? parishOfResidence.getName() : "", subdivision, district,
                    form.getCountryOfResidence());

            person.setDefaultPhysicalAddressData(physicalAddressData, true);
        }

        if (form.getDislocatedFromPermanentResidence() && form.isSchoolTimeAddressComplete()) {
            district = form.getSchoolTimeDistrictSubdivisionOfResidence().getDistrict() != null ? form
                    .getSchoolTimeDistrictSubdivisionOfResidence().getDistrict().getName() : null;
            subdivision = form.getSchoolTimeDistrictSubdivisionOfResidence() != null ? form
                    .getSchoolTimeDistrictSubdivisionOfResidence().getName() : null;
            PhysicalAddress schoolTimeAddress = getSchoolTimePhysicalAddress(person);
            if (schoolTimeAddress == null || !StringUtils.equals(form.getSchoolTimeAddress(), schoolTimeAddress.getAddress())
                    || !StringUtils.equals(form.getSchoolTimeAreaCode(), schoolTimeAddress.getAreaCode())
                    || !StringUtils.equals(form.getSchoolTimeArea(), schoolTimeAddress.getArea())
                    || !StringUtils.equals(form.getSchoolTimeParishOfResidence().getName(),
                            schoolTimeAddress.getParishOfResidence())
                    || !StringUtils.equals(subdivision, schoolTimeAddress.getDistrictSubdivisionOfResidence())
                    || !StringUtils.equals(district, schoolTimeAddress.getDistrictOfResidence())) {

                String schoolTimeAreaCode = form.getSchoolTimeAreaCode().substring(0, 8);
                String schoolTimeAreaOfAreaCode = form.getSchoolTimeAreaCode().substring(9);
                physicalAddressData = new PhysicalAddressData(form.getSchoolTimeAddress(), schoolTimeAreaCode,
                        schoolTimeAreaOfAreaCode, form.getSchoolTimeArea(), form.getSchoolTimeParishOfResidence().getName(),
                        form.getSchoolTimeDistrictSubdivisionOfResidence().getName(),
                        form.getSchoolTimeDistrictSubdivisionOfResidence().getDistrict().getName(), Country.readDefault());

                if (schoolTimeAddress != null) {
                    schoolTimeAddress.edit(physicalAddressData);
                    schoolTimeAddress.setValid();
                } else {
                    schoolTimeAddress =
                            PhysicalAddress.createPhysicalAddress(person, physicalAddressData, PartyContactType.PERSONAL, false);
                    schoolTimeAddress.setValid();
                }
            }

            personUl.setDislocatedResidenceType(form.getSchoolTimeResidenceType());
            personUl.setOtherDislocatedResidenceType(form.getOtherSchoolTimeResidenceType());
        } else {
            personUl.setDislocatedResidenceType(null);
            personUl.setOtherDislocatedResidenceType("");
        }
    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(HouseholdInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ContactsFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        return false;
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

}
