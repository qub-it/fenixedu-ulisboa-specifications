package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.AccountabilityType;
import org.fenixedu.academic.domain.organizationalStructure.AccountabilityTypeEnum;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitUtils;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.DisabilitiesFormController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.YearMonthDay;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(PreviousDegreeOriginInformationFormController.CONTROLLER_URL)
public class PreviousDegreeOriginInformationFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/previousdegreeorigininformationform";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "previousDegreeInformationForm";
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        Registration registration = getRegistration(executionYear, model);
        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("Invalid Request. Person mismatch");
        }

        PreviousDegreeInformationForm form = fillFormIfRequired(executionYear, registration, model);

        if (getForm(model) == null) {
            model.addAttribute("registration", registration);
            setForm(form, model);
        }

        addInfoMessage(ULisboaSpecificationsUtil.bundle("label.firstTimeCandidacy.fillPreviousDegreeInformation.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/previousdegreeorigininformationform/fillpreviousdegreeinformation";
    }

    public PreviousDegreeInformationForm fillFormIfRequired(ExecutionYear executionYear, Registration registration, Model model) {
        PreviousDegreeInformationForm form = (PreviousDegreeInformationForm) getForm(model);
        if (form == null) {
            form = createPreviousDegreeInformationForm(executionYear, registration);

            model.addAttribute("registration", registration);
            setForm(form, model);
        }
        return form;
    }

    protected PreviousDegreeInformationForm createPreviousDegreeInformationForm(final ExecutionYear executionYear,
            final Registration registration) {
        final PreviousDegreeInformationForm form = new PreviousDegreeInformationForm();

        final PrecedentDegreeInformation precedentDegreeInformation =
                registration.getStudentCandidacy().getPrecedentDegreeInformation();

        form.setPrecedentSchoolLevel(precedentDegreeInformation.getPrecedentSchoolLevel());
        if (form.getPrecedentSchoolLevel() == SchoolLevelType.OTHER) {
            form.setOtherPrecedentSchoolLevel(precedentDegreeInformation.getOtherPrecedentSchoolLevel());
        }

        Unit institution = precedentDegreeInformation.getPrecedentInstitution();
        if (institution != null) {
            form.setPrecedentInstitutionOid(institution.getExternalId());
            form.setPrecedentInstitutionName(institution.getName());
        }

        String precedentDegreeDesignationName = precedentDegreeInformation.getPrecedentDegreeDesignation();
        if (form.getPrecedentSchoolLevel() != null && form.getPrecedentCountry() != null
                && form.getPrecedentCountry().isDefaultCountry() && form.getPrecedentSchoolLevel().isHigherEducation()) {
            DegreeDesignation precedentDegreeDesignation;
            if (institution != null) {
                Predicate<DegreeDesignation> matchesName =
                        dd -> dd.getDescription().equalsIgnoreCase(precedentDegreeDesignationName);
                precedentDegreeDesignation =
                        institution.getDegreeDesignationSet().stream().filter(matchesName).findFirst().orElse(null);
                form.setRaidesPrecedentDegreeDesignation(precedentDegreeDesignation);
            } else {
                precedentDegreeDesignation = DegreeDesignation.readByNameAndSchoolLevel(precedentDegreeDesignationName,
                        form.getPrecedentSchoolLevel());
                form.setRaidesPrecedentDegreeDesignation(precedentDegreeDesignation);
            }
        } else {
            form.setPrecedentDegreeDesignation(precedentDegreeDesignationName);
        }

        form.setPrecedentCountry(precedentDegreeInformation.getPrecedentCountry());
        if (form.getPrecedentCountry() == null) {
            form.setPrecedentCountry(Country.readDefault());
        }

        form.setNumberOfEnrolmentsInPreviousDegrees(
                precedentDegreeInformation.getNumberOfEnrolmentsInPreviousDegrees() != null ? precedentDegreeInformation
                        .getNumberOfEnrolmentsInPreviousDegrees() : 0);

        if (!StringUtils.isEmpty(form.getPrecedentInstitutionOid())) {
            DomainObject institutionObject = FenixFramework.getDomainObject(form.getPrecedentInstitutionOid());
            if (institutionObject instanceof Unit && FenixFramework.isDomainObjectValid(institutionObject)) {
                form.setPrecedentInstitutionName(((Unit) institutionObject).getName());
            } else {
                form.setPrecedentInstitutionName(form.getPrecedentInstitutionOid());
            }
        }

        return form;
    }

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {
        Registration registration = getRegistration(executionYear, model);
        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("Invalid Request. Person mismatch");
        }
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof PreviousDegreeInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.PreviousDegreeInformationFormController.wrong.form.type"), model);
        }

        return validate(executionYear, getRegistration(executionYear, model), (PreviousDegreeInformationForm) candidancyForm,
                model);
    }

    /**
     * @see {@link com.qubit.qubEdu.module.candidacies.domain.academicCandidacy.config.screen.validation.PreviousQualificationScreenValidator.validate(WorkflowInstanceState,
     *      WorkflowScreen)}
     */
    protected boolean validate(final ExecutionYear executionYear, final Registration registration,
            PreviousDegreeInformationForm form, Model model) {

        /* -------
         * COUNTRY
         * -------
         */

        if (form.getPrecedentCountry() == null) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.PreviousDegreeOriginInformationForm.requiredCountry"), model);
            return false;
        }

        /* ------------
         * SCHOOL LEVEL
         * ------------
         */

        if (form.getPrecedentSchoolLevel() == null) {
            addErrorMessage(
                    ULisboaSpecificationsUtil.bundle("error.PreviousDegreeOriginInformationForm.precedentSchoolLevel.required"),
                    model);
            return false;
        }

        if (form.getPrecedentSchoolLevel() == SchoolLevelType.OTHER && StringUtils.isEmpty(form.getOtherPrecedentSchoolLevel())) {
            addErrorMessage(
                    BundleUtil.getString(BUNDLE, "error.PreviousDegreeOriginInformationForm.otherPrecedentSchoolLevel.required"),
                    model);
            return false;
        }

        /* -----------
         * INSTITUTION
         * -----------
         */

        if (StringUtils.isEmpty(StringUtils.trim(form.getPrecedentInstitutionOid()))) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.PreviousDegreeOriginInformationForm.institution.must.be.filled"),
                    model);
            return false;
        }

        /* ------------------
         * DEGREE DESIGNATION
         * ------------------
         */

        if (form.getPrecedentCountry() != null && form.getPrecedentCountry().isDefaultCountry()
                && form.getPrecedentSchoolLevel().isHigherEducation()) {
            if (form.getRaidesPrecedentDegreeDesignation() == null) {
                addErrorMessage(BundleUtil.getString(BUNDLE, "error.degreeDesignation.required"), model);
                return false;
            }
        } else {
            if (isDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel()) {
                if (StringUtils.isEmpty(form.getPrecedentDegreeDesignation())) {
                    addErrorMessage(BundleUtil.getString(BUNDLE, "error.degreeDesignation.required"), model);
                    return false;
                }
            }
        }

        /* --------------------
         * NUMBER OF ENROLMENTS
         * --------------------
         */

        if (form.getNumberOfEnrolmentsInPreviousDegrees() == 0) {
            addErrorMessage(BundleUtil.getString(BUNDLE,
                    "error.PreviousDegreeInformationForm.numberOfEnrolmentsInPreviousDegrees.required"), model);
            return false;
        }

        return true;
    }

    protected boolean isDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel() {
        return true;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData(getRegistration(executionYear, model), (PreviousDegreeInformationForm) candidancyForm);
    }

    @Atomic
    protected void writeData(final Registration registration, final PreviousDegreeInformationForm form) {
        final PrecedentDegreeInformation precedentDegreeInformation =
                registration.getStudentCandidacy().getPrecedentDegreeInformation();

        precedentDegreeInformation.setPrecedentDegreeDesignation(form.getPrecedentDegreeDesignation());
        precedentDegreeInformation.setPrecedentSchoolLevel(form.getPrecedentSchoolLevel());
        if (form.getPrecedentSchoolLevel() == SchoolLevelType.OTHER) {
            precedentDegreeInformation.setOtherPrecedentSchoolLevel(form.getOtherPrecedentSchoolLevel());
        }

        String institution = form.getPrecedentInstitutionOid();
        DomainObject institutionObject = FenixFramework.getDomainObject(institution);
        if (!(institutionObject instanceof Unit) || !FenixFramework.isDomainObjectValid(institutionObject)) {
            institutionObject = UnitUtils.readExternalInstitutionUnitByName(institution);
            if (institutionObject == null) {
                Unit externalInstitutionUnit = Bennu.getInstance().getExternalInstitutionUnit();
                Unit highschools = externalInstitutionUnit.getChildUnitByAcronym("highschools");
                Unit adhocHighschools = highschools.getChildUnitByAcronym("adhoc-highschools");
                institutionObject = Unit.createNewUnit(new MultiLanguageString(I18N.getLocale(), institution), null, null,
                        OriginInformationFormController.resolveAcronym(null, institution), new YearMonthDay(), null,
                        adhocHighschools, AccountabilityType.readByType(AccountabilityTypeEnum.ORGANIZATIONAL_STRUCTURE), null,
                        null, null, null, null);
            }
        }
        precedentDegreeInformation.setPrecedentInstitution((Unit) institutionObject);

        precedentDegreeInformation.setPrecedentCountry(form.getPrecedentCountry());
        precedentDegreeInformation.setNumberOfEnrolmentsInPreviousDegrees(form.getNumberOfEnrolmentsInPreviousDegrees());

    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(OriginInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        if (findCompletePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).isEmpty()) {
            return redirect(urlWithExecutionYear(DisabilitiesFormController.CONTROLLER_URL, executionYear), model,
                    redirectAttributes);
        } else {
            return redirect(urlWithExecutionYear(CONTROLLER_URL, executionYear), model, redirectAttributes);
        }
    }

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        return false;
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

    protected Registration getRegistration(ExecutionYear executionYear, Model model) {
        return FirstTimeCandidacyController.getCandidacy().getRegistration();
        //TODOJN - ver com o anil
        //        return findCompletePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).get(0).getRegistration();
    }

}
