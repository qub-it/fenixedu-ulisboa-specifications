package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Comparator;
import java.util.Set;
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
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.filiation.FiliationFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

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
    protected String fillGetScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {

        ResidenceInformationForm form = fillFormIfRequired(executionYear, model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillResidenceInformation.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/residenceinformationform/fillresidenceinformation";
    }

    private ResidenceInformationForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        ResidenceInformationForm form = (ResidenceInformationForm) getForm(model);
        if (form == null) {
            form = createResidenceInformationForm(getStudent(model), executionYear, false);

            setForm(form, model);
        }
        return form;
    }

    private ResidenceInformationForm createResidenceInformationForm(final Student student, final ExecutionYear executionYear,
            final boolean create) {
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
            
            if(!form.getCountryOfResidence().isDefaultCountry()) {
                form.setDistrictSubdivisionOfResidenceName(defaultPhysicalAddress.getDistrictSubdivisionOfResidence());
            }
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

    private PhysicalAddress getSchoolTimePhysicalAddress(final Person person) {
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

    @Override
    protected void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model,
            final RedirectAttributes redirectAttributes) {
        //nothing
    }

    @Override
    protected boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        if (!(candidancyForm instanceof ResidenceInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.ResidenceInformationForm.wrong.form.type"), model);
        }

        return validate((ResidenceInformationForm) candidancyForm, model);
    }

    private boolean validate(final ResidenceInformationForm form, final Model model) {
        final Set<String> result = validateForm(form, getStudent(model).getPerson());

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    private Set<String> validateForm(final ResidenceInformationForm form, final Person person) {
        final Set<String> result = Sets.newLinkedHashSet();

        if (!form.getCountryOfResidence().isDefaultCountry() && !form.getDislocatedFromPermanentResidence()) {
            result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.ResidenceInformationForm.non.nacional.students.should.select.dislocated.option.and.fill.address"));
        }
        if (form.getCountryOfResidence().isDefaultCountry() && !form.isResidenceInformationFilled()) {
            result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.ResidenceInformationForm.address.national.students.should.supply.complete.address.information"));
        }
        if (form.getCountryOfResidence().isDefaultCountry() && StringUtils.isEmpty(form.getAreaCode())) {
            result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.areaCode"));
        }
        
        if(!form.getCountryOfResidence().isDefaultCountry() && StringUtils.isEmpty(form.getDistrictSubdivisionOfResidenceName())) {
            result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.candidacy.workflow.ResidenceInformationForm.districtSubdivisionOfResidence.city.required"));
        }

        if (form.getDislocatedFromPermanentResidence()) {
            if (!form.isSchoolTimeRequiredInformationAddressFilled()) {
                result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.ResidenceInformationForm.address.information.is.required.for.dislocated.students"));
            } else {
                if ((form.isAnyFilled(form.getSchoolTimeAddress(), form.getSchoolTimeAreaCode())
                        || form.getSchoolTimeParishOfResidence() != null)
                        && (form.isAnyEmpty(form.getSchoolTimeAddress(), form.getSchoolTimeAreaCode())
                                || form.getSchoolTimeParishOfResidence() == null)) {
                    result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                            "error.candidacy.workflow.ResidenceInformationForm.school.time.address.must.be.filled.completly.otherwise.fill.minimun.required"));
                }
            }

            if (form.getSchoolTimeResidenceType() != null && form.getSchoolTimeResidenceType().isOther()
                    && StringUtils.isEmpty(form.getOtherSchoolTimeResidenceType())) {
                result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.ResidenceInformationForm.other.residence.type.required"));
            }
        }

        return result;
    }

    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        writeData(executionYear, (ResidenceInformationForm) candidancyForm);
    }

    @Atomic
    protected void writeData(final ExecutionYear executionYear, final ResidenceInformationForm form) {
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
        
        if(!form.getCountryOfResidence().isDefaultCountry()) {
            subdivision = form.getDistrictSubdivisionOfResidenceName();
        }
        
        PhysicalAddressData physicalAddressData;
        if (person.getDefaultPhysicalAddress() == null
                || !StringUtils.equals(form.getAddress(), person.getDefaultPhysicalAddress().getAddress())
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

            boolean defaultAddressIsFiscalAddress = person.getDefaultPhysicalAddress() != null && person.getDefaultPhysicalAddress().isFiscalAddress();
            boolean updateCountryOfResidence = person.getDefaultPhysicalAddress() != null && person.getDefaultPhysicalAddress().getCountryOfResidence() != form.getCountryOfResidence();
            if(defaultAddressIsFiscalAddress && updateCountryOfResidence) {
                // Mark the default address as not default address
                person.getDefaultPhysicalAddress().setDefaultContact(false);
            }
            
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
            if (form.getOtherSchoolTimeResidenceType() != null) {
                personUl.setOtherDislocatedResidenceType(form.getOtherSchoolTimeResidenceType());
            } else {
                personUl.setOtherDislocatedResidenceType("");
            }
        } else {
            personUl.setDislocatedResidenceType(null);
            personUl.setOtherDislocatedResidenceType("");
        }
    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(FiliationFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ContactsFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        return false;
    }

    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }

}
