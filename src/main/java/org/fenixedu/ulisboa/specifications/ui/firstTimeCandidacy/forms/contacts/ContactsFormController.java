package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.contacts;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.fenixedu.academic.domain.EmergencyContact;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.contacts.MobilePhone;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.Phone;
import org.fenixedu.academic.domain.contacts.WebAddress;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.ResidenceInformationFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification.OriginInformationFormController;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(ContactsFormController.CONTROLLER_URL)
public class ContactsFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/contactsform";

    private static final String PHONE_PATTERN = "(\\d{4,15})";
    private static final String URL_PATTERN = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "contactsForm";
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        ContactsForm form = fillFormIfRequired(executionYear, model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillHouseHoldInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/contactsform/fillcontacts";
    }

    public ContactsForm fillFormIfRequired(ExecutionYear executionYear, Model model) {
        ContactsForm form = (ContactsForm) getForm(model);
        if (form == null) {
            form = createContactsForm(getStudent(model), executionYear);

            setForm(form, model);
        }
        return form;
    }

    private ContactsForm createContactsForm(final Student student, final ExecutionYear executionYear) {
        Person person = AccessControl.getPerson();
        ContactsForm form = new ContactsForm();

        Phone phone = getDefaultPersonalContact(person, Phone.class);
        if (phone != null) {
            form.setPhoneNumber(phone.getNumber());
        }

        MobilePhone mobilePhone = getDefaultPersonalContact(person, MobilePhone.class);
        if (mobilePhone != null) {
            form.setMobileNumber(mobilePhone.getNumber());
        }

        EmailAddress email = getDefaultPersonalContact(person, EmailAddress.class);
        if (email != null) {
            form.setPersonalEmail(email.getValue());
            form.setIsEmailAvailable(email.getVisibleToPublic());
        } else {
            form.setIsEmailAvailable(false);
        }

        WebAddress homepage = getDefaultPersonalContact(person, WebAddress.class);
        if (homepage != null) {
            form.setWebAddress(homepage.getUrl());
            form.setIsHomepageAvailable(homepage.getVisibleToPublic());
        } else {
            form.setIsHomepageAvailable(false);
        }

        form.setEmergencyContact(
                Optional.ofNullable(person.getProfile().getEmergencyContact()).map(EmergencyContact::getContact).orElse(null));

        return form;
    }

    public static <T extends PartyContact> T getDefaultPersonalContact(Person person, Class<T> partyContactClass) {
        T defaultContact = (T) person.getDefaultPartyContact(partyContactClass);
        if (defaultContact != null && defaultContact.getType().equals(PartyContactType.PERSONAL)) {
            return defaultContact;
        }

        Predicate<PartyContact> contactIsPersonal = address -> address.getType().equals(PartyContactType.PERSONAL);
        Predicate<PartyContact> contactIsToBeDefault =
                address -> !address.isActiveAndValid() && address.getPartyContactValidation().getToBeDefault();
        List<T> allContacts = (List<T>) person.getAllPartyContacts(partyContactClass);
        return allContacts.stream().filter(contactIsPersonal).filter(contactIsToBeDefault)
                .sorted(ResidenceInformationFormController.CONTACT_COMPARATOR_BY_MODIFIED_DATE).findFirst().orElse(null);
    }

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {
        StudentAccessServices.triggerSyncPersonToExternal(AccessControl.getPerson());
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof ContactsForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.ContactsFormController.wrong.form.type"), model);
        }

        return validate((ContactsForm) candidancyForm, model);
    }

    private boolean validate(ContactsForm form, Model model) {
        if (StringUtils.isEmpty(form.getPersonalEmail())) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.personalEmail.required"),
                    model);
            return false;
        }

        if (!StringUtils.isEmpty(form.getPhoneNumber()) && !form.getPhoneNumber().matches(PHONE_PATTERN)) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.phone"),
                    model);
            return false;
        }
        if (!StringUtils.isEmpty(form.getMobileNumber()) && !form.getMobileNumber().matches(PHONE_PATTERN)) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.phone"),
                    model);
            return false;
        }

        if (!StringUtils.isEmpty(form.getWebAddress()) && !form.getWebAddress().matches(URL_PATTERN)) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.webAddress"),
                    model);
            return false;
        }
        if (StringUtils.isEmpty(form.getEmergencyContact()) || !form.getEmergencyContact().matches(PHONE_PATTERN)) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.incorrect.emergencyContact"), model);
            return false;
        }
        return true;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData((ContactsForm) candidancyForm);
    }

    @Atomic
    protected void writeData(ContactsForm form) {
        Person person = AccessControl.getPerson();

        Phone phone = getDefaultPersonalContact(person, Phone.class);
        if (phone != null) {
            phone.setNumber(form.getPhoneNumber());
        } else {
            phone = Phone.createPhone(person, form.getPhoneNumber(), PartyContactType.PERSONAL, true);
        }

        MobilePhone mobilePhone = getDefaultPersonalContact(person, MobilePhone.class);
        if (mobilePhone != null) {
            mobilePhone.setNumber(form.getMobileNumber());
        } else {
            mobilePhone = MobilePhone.createMobilePhone(person, form.getMobileNumber(), PartyContactType.PERSONAL, true);
        }

        EmailAddress email = getDefaultPersonalContact(person, EmailAddress.class);
        if (email != null) {
            email.setValue(form.getPersonalEmail());
            email.setVisibleToPublic(form.getIsEmailAvailable());
        } else {
            email = EmailAddress.createEmailAddress(person, form.getPersonalEmail(), PartyContactType.PERSONAL, true);
            email.setVisibleToPublic(form.getIsEmailAvailable());
        }

        WebAddress homepage = getDefaultPersonalContact(person, WebAddress.class);
        if (homepage != null) {
            homepage.setUrl(form.getWebAddress());
            homepage.setVisibleToPublic(form.getIsHomepageAvailable());
        } else {
            if (!StringUtils.isEmpty(form.getWebAddress())) {
                homepage = WebAddress.createWebAddress(person, form.getWebAddress(), PartyContactType.PERSONAL, true);
                homepage.setVisibleToPublic(form.getIsHomepageAvailable());
            }
        }

        EmergencyContact.updateEmergencyContact(person.getProfile(), form.getEmergencyContact());
    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ResidenceInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(OriginInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
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
