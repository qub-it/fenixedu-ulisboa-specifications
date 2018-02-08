package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(HouseholdInformationUlisboaFormController.CONTROLLER_URL)
public class HouseholdInformationUlisboaFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/householdinformationulisboaform";

    public static final String FILL_URL = CONTROLLER_URL + _FILL_URI;

    protected static final String SHOW_FIRST_OPTION_DESCRITPION = "notFirstOption";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "householdInformationByExecutionYearForm";
    }

    @Override
    protected String fillGetScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {

        HouseholdInformationUlisboaForm form = fillFormIfRequired(model);

        if (!model.containsAttribute("postAction")) {
            model.addAttribute("postAction", "fill");
        }

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillHouseHoldInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/householdinformationulisboaform/fillhouseholdinformationulisboa";
    }

    public HouseholdInformationUlisboaForm fillFormIfRequired(final Model model) {
        HouseholdInformationUlisboaForm form = (HouseholdInformationUlisboaForm) getForm(model);
        if (form == null) {
            form = createHouseholdInformationForm(getStudent(model));

            setForm(form, model);
        }
        return form;
    }

    protected HouseholdInformationUlisboaForm createEmptyHouseholdInformationForm(final Student student,
            final ExecutionYear executionYear, final Model model) {
        final HouseholdInformationUlisboaForm form = new HouseholdInformationUlisboaForm();

        return form;
    }

    protected HouseholdInformationUlisboaForm createHouseholdInformationForm(final Student student) {
        final HouseholdInformationUlisboaForm form = new HouseholdInformationUlisboaForm();

        PersonUlisboaSpecifications personUl = student.getPerson().getPersonUlisboaSpecifications();
        if (personUl != null) {
            form.setBestQualitiesInThisCicle(personUl.getBestQualitiesInThisCicle());

            form.setFlunkedBeforeUniversity(personUl.getFlunkedBeforeUniversity());
            form.setFlunkedHighSchool(personUl.getFlunkedHighSchool());
            form.setFlunkedHighSchoolTimes(personUl.getFlunkedHighSchoolTimes());
            form.setFlunkedPreHighSchool(personUl.getFlunkedPreHighSchool());
            form.setFlunkedPreHighSchoolTimes(personUl.getFlunkedPreHighSchoolTimes());

            form.setSocialBenefitsInHighSchool(personUl.getSocialBenefitsInHighSchool());
            form.setSocialBenefitsInHighSchoolDescription(personUl.getSocialBenefitsInHighSchoolDescription());

            form.setFirstTimeInPublicUniv(personUl.getFirstTimeInPublicUniv());
            form.setPublicUnivCandidacies(personUl.getPublicUnivCandidacies());
            form.setFirstTimeInUlisboa(personUl.getFirstTimeInUlisboa());

        }

        return form;
    }

    @Override
    protected void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model,
            final RedirectAttributes redirectAttributes) {
        //nothing
    }

    @Override
    protected boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        if (!(candidancyForm instanceof HouseholdInformationUlisboaForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.HouseholdInformationUlisboaFormController.wrong.form.type"),
                    model);
        }

        return validate((HouseholdInformationUlisboaForm) candidancyForm, model);
    }

    protected boolean validate(final HouseholdInformationUlisboaForm form, final Model model) {
        final Set<String> messages = validateForm(form, model);

//        for (final String message : messages) {
//            addErrorMessage(message, model);
//        }
//
//        return messages.isEmpty();
        return true;
    }

    protected Set<String> validateForm(final HouseholdInformationUlisboaForm form, final Model model) {
        final Set<String> messages = Sets.newLinkedHashSet();

        //FLUNKED BEFORE UNIV

        if (form.getFlunkedBeforeUniversity() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        } else {
            if (form.getFlunkedBeforeUniversity() == Boolean.TRUE) {

                if (form.getFlunkedHighSchool() == null) {
                    messages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                            "error.all.fields.required"));
                } else if (form.getFlunkedHighSchool() && form.getFlunkedHighSchoolTimes() == null) {
                    messages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                            "error.all.fields.required"));
                } else if (form.getFlunkedHighSchool() && form.getFlunkedHighSchoolTimes() == 0) {
                    messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.flunkedHighSchoolTimes"));
                } else if (!form.getFlunkedHighSchool()) {
                    form.setFlunkedHighSchoolTimes(0);
                }

                if (form.getFlunkedPreHighSchool() == null) {
                    messages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                            "error.all.fields.required"));
                } else if (form.getFlunkedPreHighSchool() && form.getFlunkedPreHighSchoolTimes() == null) {
                    messages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                            "error.all.fields.required"));

                } else if (form.getFlunkedPreHighSchool() && form.getFlunkedPreHighSchoolTimes() == 0) {
                    messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.flunkedHighSchoolTimes"));
                } else if (!form.getFlunkedPreHighSchool()) {
                    form.setFlunkedPreHighSchoolTimes(0);
                }

            } else {
                form.setFlunkedHighSchool(Boolean.FALSE);
                form.setFlunkedPreHighSchool(Boolean.FALSE);
                form.setFlunkedHighSchoolTimes(0);
                form.setFlunkedPreHighSchoolTimes(0);
            }
        }

        // SOCIAL BENEFITS

        if (form.getSocialBenefitsInHighSchool() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        } else if (form.getSocialBenefitsInHighSchool() && StringUtils.isBlank(form.getSocialBenefitsInHighSchoolDescription())) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        } else if (!form.getSocialBenefitsInHighSchool()) {
            form.setSocialBenefitsInHighSchoolDescription("");
        }

        // FIRST TIME APPLYING

        if (form.getFirstTimeInPublicUniv() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        } else if (!form.getFirstTimeInPublicUniv() && form.getPublicUnivCandidacies() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        } else if (!form.getFirstTimeInPublicUniv() && form.getPublicUnivCandidacies() == 0) {
            messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.publicUnivCandidacies"));
        } else if (form.getFirstTimeInPublicUniv()) {
            form.setPublicUnivCandidacies(0);
        }

        if (form.getFirstTimeInUlisboa() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        if (StringUtils.isBlank(form.getBestQualitiesInThisCicle())) {
            messages.add(BundleUtil.getString(BUNDLE, "error.all.fields.required"));
        }

        return messages;
    }

    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        writeData(getStudent(model), executionYear, (HouseholdInformationUlisboaForm) candidancyForm, model);
    }

    @Atomic
    protected void writeData(final Student student, final ExecutionYear executionYear, final HouseholdInformationUlisboaForm form,
            final Model model) {
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(student.getPerson());

        if (form.getFlunkedHighSchool() != null) {
            personUlisboa.setFlunkedHighSchool(form.getFlunkedHighSchool());
        }
        if (form.getFlunkedHighSchoolTimes() != null) {
            personUlisboa.setFlunkedHighSchoolTimes(form.getFlunkedHighSchoolTimes());
        }
        if (form.getFlunkedPreHighSchool() != null) {
            personUlisboa.setFlunkedPreHighSchool(form.getFlunkedPreHighSchool());
        }
        if (form.getFlunkedPreHighSchoolTimes() != null) {
            personUlisboa.setFlunkedPreHighSchoolTimes(form.getFlunkedPreHighSchoolTimes());
        }

        if (form.getSocialBenefitsInHighSchool() != null) {
            personUlisboa.setSocialBenefitsInHighSchool(form.getSocialBenefitsInHighSchool());
        }
        if (form.getSocialBenefitsInHighSchoolDescription() != null) {
            personUlisboa.setSocialBenefitsInHighSchoolDescription(form.getSocialBenefitsInHighSchoolDescription());
        }

        if (form.getFirstTimeInPublicUniv() != null) {
            personUlisboa.setFirstTimeInPublicUniv(form.getFirstTimeInPublicUniv());
        }
        if (form.getPublicUnivCandidacies() != null) {
            personUlisboa.setPublicUnivCandidacies(form.getPublicUnivCandidacies());
        }
        if (form.getFirstTimeInUlisboa() != null) {
            personUlisboa.setFirstTimeInUlisboa(form.getFirstTimeInUlisboa());
        }

        if (form.getBestQualitiesInThisCicle() != null) {
            personUlisboa.setBestQualitiesInThisCicle(form.getBestQualitiesInThisCicle());
        }

    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(HouseholdInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ResidenceInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
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
