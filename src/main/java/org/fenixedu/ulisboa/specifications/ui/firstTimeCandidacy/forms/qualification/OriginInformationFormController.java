package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import org.fenixedu.academic.domain.student.PersonalIngressionData;
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
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.contacts.ContactsFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.DisabilitiesFormController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(OriginInformationFormController.CONTROLLER_URL)
public class OriginInformationFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/origininformationform";

    private static final String GRADE_FORMAT = "\\d{2}";
    private static final String YEAR_FORMAT = "\\d{4}";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "originInformationForm";
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        Registration registration = getRegistration(executionYear, model);
        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("Invalid Request. Person mismatch");
        }

        OriginInformationForm form = fillFormIfRequired(registration, model);

        if (getForm(model) == null) {
            model.addAttribute("registration", registration);
            setForm(form, model);
        }

        addInfoMessage(ULisboaSpecificationsUtil.bundle("label.firstTimeCandidacy.fillOriginInformation.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/origininformationform/fillorigininformation";
    }

    public OriginInformationForm fillFormIfRequired(Registration registration, Model model) {
        OriginInformationForm form = (OriginInformationForm) getForm(model);
        if (form == null) {
            form = createOriginInformationForm(registration);

            model.addAttribute("registration", registration);
            setForm(form, model);
        }
        return form;
    }

    protected OriginInformationForm createOriginInformationForm(final Registration registration) {
        final OriginInformationForm form = new OriginInformationForm();

        form.setDistrictAndSubdivisionRequired(isDistrictAndSubdivisionRequired());

        final PrecedentDegreeInformation precedentDegreeInformation =
                registration.getStudentCandidacy().getPrecedentDegreeInformation();

        form.setSchoolLevel(precedentDegreeInformation.getSchoolLevel());
        if (form.getSchoolLevel() == SchoolLevelType.OTHER) {
            form.setOtherSchoolLevel(precedentDegreeInformation.getOtherSchoolLevel());
        }

        Unit institution = precedentDegreeInformation.getInstitution();
        if (institution != null) {
            form.setInstitutionOid(institution.getExternalId());
            form.setInstitutionName(institution.getName());
        }

        String degreeDesignationName = precedentDegreeInformation.getDegreeDesignation();
        if (form.getSchoolLevel() != null && form.getSchoolLevel().isHigherEducation()) {
            DegreeDesignation degreeDesignation;
            if (institution != null) {
                Predicate<DegreeDesignation> matchesName =
                        dd -> dd.getDescription().equalsIgnoreCase(degreeDesignationName) && form.getSchoolLevel()
                                .getEquivalentDegreeClassifications().contains(dd.getDegreeClassification().getCode());
                Optional<DegreeDesignation> degreeDesignationOption =
                        institution.getDegreeDesignationSet().stream().filter(matchesName).findFirst();
                if (degreeDesignationOption.isPresent()) {
                    degreeDesignation = degreeDesignationOption.get();
                    form.setRaidesDegreeDesignation(degreeDesignation);
                } else {
                    form.setDegreeDesignation(degreeDesignationName);
                }

            } else {
                degreeDesignation = DegreeDesignation.readByNameAndSchoolLevel(degreeDesignationName, form.getSchoolLevel());
                form.setRaidesDegreeDesignation(degreeDesignation);
            }
        } else {
            form.setDegreeDesignation(degreeDesignationName);
        }

        form.setConclusionGrade(precedentDegreeInformation.getConclusionGrade());
        form.setConclusionYear(precedentDegreeInformation.getConclusionYear());
        form.setCountryWhereFinishedPreviousCompleteDegree(precedentDegreeInformation.getCountry());
        if (form.getCountryWhereFinishedPreviousCompleteDegree() == null) {
            form.setCountryWhereFinishedPreviousCompleteDegree(Country.readDefault());
        }

        form.setDistrictWhereFinishedPreviousCompleteDegree(precedentDegreeInformation.getDistrict());
        form.setDistrictSubdivisionWhereFinishedPreviousCompleteDegree(precedentDegreeInformation.getDistrictSubdivision());

        form.setHighSchoolType(precedentDegreeInformation.getPersonalIngressionData().getHighSchoolType());

        if (!StringUtils.isEmpty(form.getInstitutionOid())) {
            DomainObject institutionObject = FenixFramework.getDomainObject(form.getInstitutionOid());
            if (institutionObject instanceof Unit && FenixFramework.isDomainObjectValid(institutionObject)) {
                form.setInstitutionName(((Unit) institutionObject).getName());
            } else {
                form.setInstitutionName(form.getInstitutionOid());
            }
        }

        return form;
    }

    public boolean isDistrictAndSubdivisionRequired() {
        return true;
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
        if (!(candidancyForm instanceof OriginInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.OriginInformationFormController.wrong.form.type"), model);
        }

        return validate(getRegistration(executionYear, model), (OriginInformationForm) candidancyForm, model);
    }

    /**
     * @see {@link com.qubit.qubEdu.module.candidacies.domain.academicCandidacy.config.screen.validation.LastCompletedQualificationScreenValidator.validate(WorkflowInstanceState,
     *      WorkflowScreen)}
     */
    protected boolean validate(final Registration registration, final OriginInformationForm form, final Model model) {

        /* -------
         * COUNTRY
         * -------
         */

        if (form.getCountryWhereFinishedPreviousCompleteDegree() == null) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.personalInformation.requiredCountry"), model);
            return false;
        }

        /* ------------------------
         * DISTRICT AND SUBDIVISION
         * ------------------------
         */

        if (form.getCountryWhereFinishedPreviousCompleteDegree().isDefaultCountry() && isDistrictAndSubdivisionRequired()) {
            if (form.getDistrictSubdivisionWhereFinishedPreviousCompleteDegree() == null
                    || form.getDistrictWhereFinishedPreviousCompleteDegree() == null) {
                addErrorMessage(
                        BundleUtil.getString(BUNDLE, "error.personalInformation.requiredDistrictAndSubdivisionForDefaultCountry"),
                        model);
                return false;
            }
        }

        /* ------------
         * SCHOOL LEVEL
         * ------------
         */

        if (form.getSchoolLevel() == null) {
            addErrorMessage(
                    BundleUtil.getString(BUNDLE, "error.candidacy.workflow.OriginInformationForm.schoolLevel.must.be.filled"),
                    model);
            return false;
        }

        if (form.getSchoolLevel() == SchoolLevelType.OTHER && StringUtils.isEmpty(form.getOtherSchoolLevel())) {
            addErrorMessage(BundleUtil.getString(BUNDLE,
                    "error.candidacy.workflow.OriginInformationForm.otherSchoolLevel.must.be.filled"), model);
            return false;
        }

        /* -----------
         * INSTITUTION
         * -----------
         */

        if (isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel()) {
            if (StringUtils.isEmpty(StringUtils.trim(form.getInstitutionOid()))) {
                addErrorMessage(
                        BundleUtil.getString(BUNDLE, "error.candidacy.workflow.OriginInformationForm.institution.must.be.filled"),
                        model);
                return false;
            }
        } else {
            if (form.getCountryWhereFinishedPreviousCompleteDegree().isDefaultCountry()
                    && form.getSchoolLevel().isHigherEducation()) {
                if (StringUtils.isEmpty(StringUtils.trim(form.getInstitutionOid()))) {
                    addErrorMessage(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.OriginInformationForm.institution.must.be.filled"), model);
                    return false;
                }
            }
        }

        /* ------------------
         * DEGREE DESIGNATION
         * ------------------
         */

        if (form.getCountryWhereFinishedPreviousCompleteDegree() != null
                && form.getCountryWhereFinishedPreviousCompleteDegree().isDefaultCountry()
                && form.getSchoolLevel().isHigherEducation()) {
            if (form.getRaidesDegreeDesignation() == null) {
                addErrorMessage(BundleUtil.getString(BUNDLE, "error.degreeDesignation.required"), model);
                return false;
            }
        } else {
            if (isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel()) {
                if (StringUtils.isEmpty(form.getDegreeDesignation())) {
                    addErrorMessage(BundleUtil.getString(BUNDLE, "error.degreeDesignation.required"), model);
                    return false;
                }
            }
        }

        /* ----------------
         * CONCLUSION GRADE
         * ----------------
         */

        if (!StringUtils.isEmpty(form.getConclusionGrade()) && !form.getConclusionGrade().matches(GRADE_FORMAT)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.incorrect.conclusionGrade"), model);
            return false;
        }

        /* ---------------
         * CONCLUSION YEAR
         * ---------------
         */

        if (form.getConclusionYear() == null || !form.getConclusionYear().toString().matches(YEAR_FORMAT)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.incorrect.conclusionYear"), model);
            return false;
        }

        LocalDate now = new LocalDate();
        if (now.getYear() < form.getConclusionYear()) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.personalInformation.year.after.current"), model);
            return false;
        }

        int birthYear = registration.getPerson().getDateOfBirthYearMonthDay().getYear();
        if (form.getConclusionYear() < birthYear) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.personalInformation.year.before.birthday"), model);
            return false;
        }

        if (form.getSchoolLevel().isHighSchoolOrEquivalent()) {
            if (form.getHighSchoolType() == null) {
                addErrorMessage(BundleUtil.getString(BUNDLE, "error.highSchoolType.required"), model);
                return false;
            }
        }

        return true;
    }

    protected boolean isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel() {
        return true;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData(getRegistration(executionYear, model), (OriginInformationForm) candidancyForm);
    }

    @Atomic
    protected void writeData(final Registration registration, final OriginInformationForm form) {
        final PrecedentDegreeInformation precedentDegreeInformation =
                registration.getStudentCandidacy().getPrecedentDegreeInformation();
        final PersonalIngressionData personalData = precedentDegreeInformation.getPersonalIngressionData();

        precedentDegreeInformation.setConclusionGrade(form.getConclusionGrade());
        precedentDegreeInformation.setDegreeDesignation(form.getDegreeDesignation());
        precedentDegreeInformation.setSchoolLevel(form.getSchoolLevel());
        if (form.getSchoolLevel() == SchoolLevelType.OTHER) {
            precedentDegreeInformation.setOtherSchoolLevel(form.getOtherSchoolLevel());
        }

        if (isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel()
                || !Strings.isNullOrEmpty(form.getInstitutionOid())) {
            String institution = form.getInstitutionOid();
            DomainObject institutionObject = FenixFramework.getDomainObject(institution);
            if (!(institutionObject instanceof Unit) || !FenixFramework.isDomainObjectValid(institutionObject)) {
                institutionObject = UnitUtils.readExternalInstitutionUnitByName(institution);
                if (institutionObject == null) {
                    Unit externalInstitutionUnit = Bennu.getInstance().getExternalInstitutionUnit();
                    Unit highschools = externalInstitutionUnit.getChildUnitByAcronym("highschools");
                    Unit adhocHighschools = highschools.getChildUnitByAcronym("adhoc-highschools");
                    institutionObject = Unit.createNewUnit(new MultiLanguageString(I18N.getLocale(), institution), null, null,
                            resolveAcronym(null, institution), new YearMonthDay(), null, adhocHighschools,
                            AccountabilityType.readByType(AccountabilityTypeEnum.ORGANIZATIONAL_STRUCTURE), null, null, null,
                            null, null);
                }
            }
            precedentDegreeInformation.setInstitution((Unit) institutionObject);
        }

        precedentDegreeInformation.setConclusionYear(form.getConclusionYear());
        Country country = form.getCountryWhereFinishedPreviousCompleteDegree();
        precedentDegreeInformation.setCountry(country);
        if (country.isDefaultCountry()) {
            precedentDegreeInformation.setDistrict(form.getDistrictWhereFinishedPreviousCompleteDegree());
            precedentDegreeInformation.setDistrictSubdivision(form.getDistrictSubdivisionWhereFinishedPreviousCompleteDegree());
        }
        if (form.getSchoolLevel() != null && form.getSchoolLevel().isHighSchoolOrEquivalent()) {
            precedentDegreeInformation.setCountryHighSchool(form.getCountryWhereFinishedPreviousCompleteDegree());
        }

        personalData.setHighSchoolType(form.getHighSchoolType());
    }

    public static String resolveAcronym(String acronym, String name) {
        final Unit externalInstitutionUnit = Bennu.getInstance().getExternalInstitutionUnit();
        final Unit highschools = externalInstitutionUnit.getChildUnitByAcronym("highschools");
        final List<String> takenAcronyms = new ArrayList<String>();
        String resolvedAcronym = acronym;
        for (Unit school : highschools.getChildUnitByAcronym("official-highschools").getSubUnits()) {
            takenAcronyms.add(school.getAcronym());
        }
        for (Unit school : highschools.getChildUnitByAcronym("adhoc-highschools").getSubUnits()) {
            takenAcronyms.add(school.getAcronym());
        }
        if (Strings.isNullOrEmpty(resolvedAcronym)) {
            resolvedAcronym = "";
            for (String letter : name.split("[^A-Z]+")) {
                resolvedAcronym += letter;
            }
        }
        if (takenAcronyms.contains(resolvedAcronym)) {
            int version = 0;
            String versionedAcronym = resolvedAcronym + String.format("%02d", version);
            while (takenAcronyms.contains(versionedAcronym)) {
                versionedAcronym = resolvedAcronym + String.format("%02d", ++version);
            }
            return versionedAcronym;
        }
        return resolvedAcronym;
    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ContactsFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        if (findCompletePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).isEmpty()) {
            if (isPreviousDegreeOriginInformationRequired()) {
                return redirect(urlWithExecutionYear(PreviousDegreeOriginInformationFormController.CONTROLLER_URL, executionYear),
                        model, redirectAttributes);
            }
            return redirect(urlWithExecutionYear(DisabilitiesFormController.CONTROLLER_URL, executionYear), model,
                    redirectAttributes);
        } else {
            return redirect(urlWithExecutionYear(CONTROLLER_URL, executionYear), model, redirectAttributes);
        }

    }

    protected boolean isPreviousDegreeOriginInformationRequired() {
        return false;
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
    }

}
