package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.filiation;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.HouseholdInformationFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo.PersonalInformationFormController;
import org.joda.time.YearMonthDay;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(FiliationFormController.CONTROLLER_URL)
public class FiliationFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/filiationform";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "filiationForm";
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {

        fillFormIfRequired(model);

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillFiliation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/filiationform/fillfiliation";
    }

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {
        StudentAccessServices.triggerSyncPersonToExternal(AccessControl.getPerson());
    }

    private void fillFormIfRequired(Model model) {
        if (!model.containsAttribute(getFormVariableName())) {
            FiliationForm form = new FiliationForm();
            Person person = AccessControl.getPerson();
            PersonUlisboaSpecifications personUl = person.getPersonUlisboaSpecifications();
            if (personUl != null) {
                form.setSecondNationality(personUl.getSecondNationality());
            }

            form.setDateOfBirth(person.getDateOfBirthYearMonthDay().toLocalDate());
            form.setCountryOfBirth(person.getCountryOfBirth());
            if (form.getCountryOfBirth() == null) {
                form.setCountryOfBirth(Country.readDefault());
            }
            District district = District.readByName(person.getDistrictOfBirth());
            if (district != null) {
                form.setDistrictOfBirth(district);
                DistrictSubdivision districtSubdivision =
                        district.getDistrictSubdivisionByName(person.getDistrictSubdivisionOfBirth());
                form.setDistrictSubdivisionOfBirth(districtSubdivision);
                if (districtSubdivision != null) {
                    form.setParishOfBirth(Parish.findByName(districtSubdivision, person.getParishOfBirth()).orElse(null));
                }
            }

            form.setFatherName(person.getNameOfFather());
            form.setMotherName(person.getNameOfMother());

            setForm(form, model);
        }
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof FiliationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.FiliationFormController.wrong.form.type"), model);
        }
        return validate((FiliationForm) candidancyForm, model);
    }

    private boolean validate(FiliationForm form, Model model) {
        if (StringUtils.isEmpty(form.getFatherName()) || StringUtils.isEmpty(form.getMotherName())) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.parentsName.required"), model);
            return false;
        }
        if (form.getDateOfBirth() == null) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.birthDate.required"), model);
            return false;
        }

        if (form.getCountryOfBirth().isDefaultCountry()) {
            if (form.getDistrictOfBirth() == null || form.getDistrictSubdivisionOfBirth() == null
                    || form.getParishOfBirth() == null) {
                addErrorMessage(
                        BundleUtil.getString(BUNDLE,
                                "error.candidacy.workflow.FiliationForm.zone.information.is.required.for.national.students"),
                        model);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData((FiliationForm) candidancyForm);
    }

    @Atomic
    private void writeData(FiliationForm form) {
        Person person = AccessControl.getPerson();
        PersonUlisboaSpecifications personUl = PersonUlisboaSpecifications.findOrCreate(person);
        personUl.setSecondNationality(form.getSecondNationality());

        person.setDateOfBirthYearMonthDay(new YearMonthDay(form.getDateOfBirth()));
        person.setCountryOfBirth(form.getCountryOfBirth());
        if (person.getCountryOfBirth().isDefaultCountry()) {
            person.setDistrictOfBirth(form.getDistrictOfBirth().getName());
            person.setDistrictSubdivisionOfBirth(form.getDistrictSubdivisionOfBirth().getName());
            person.setParishOfBirth(form.getParishOfBirth().getName());
        } else {
            person.setDistrictOfBirth(null);
            person.setDistrictSubdivisionOfBirth(null);
            person.setParishOfBirth(null);
        }

        person.setNameOfFather(form.getFatherName());
        person.setNameOfMother(form.getMotherName());
    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(PersonalInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(HouseholdInformationFormController.CONTROLLER_URL, executionYear), model,
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
