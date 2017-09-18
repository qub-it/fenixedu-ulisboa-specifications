package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
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
    protected String fillGetScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        Registration registration = getRegistration(executionYear, model);
        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("Invalid Request. Person mismatch");
        }

        if (!model.containsAttribute("postAction")) {
            model.addAttribute("postAction", "fill");
        }

        OriginInformationForm form = fillFormIfRequired(registration, model);

        if (getForm(model) == null) {
            model.addAttribute("registration", registration);
            setForm(form, model);
        }

        addInfoMessage(ULisboaSpecificationsUtil.bundle("label.firstTimeCandidacy.fillOriginInformation.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/origininformationform/fillorigininformation";
    }

    public OriginInformationForm fillFormIfRequired(final Registration registration, final Model model) {
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
        }

        String degreeDesignationName = precedentDegreeInformation.getDegreeDesignation();
        if (form.getSchoolLevel() != null && form.getSchoolLevel().isHigherEducation()
                && form.getCountryWhereFinishedPreviousCompleteDegree() != null
                && form.getCountryWhereFinishedPreviousCompleteDegree() == Country.readDefault()) {
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
        form.setConclusionYear(precedentDegreeInformation.getConclusionYear() == null ? "" : ""
                + precedentDegreeInformation.getConclusionYear());
        form.setCountryWhereFinishedPreviousCompleteDegree(precedentDegreeInformation.getCountry());
        if (form.getCountryWhereFinishedPreviousCompleteDegree() == null) {
            form.setCountryWhereFinishedPreviousCompleteDegree(Country.readDefault());
        }

        form.setDistrictWhereFinishedPreviousCompleteDegree(precedentDegreeInformation.getDistrict());
        form.setDistrictSubdivisionWhereFinishedPreviousCompleteDegree(precedentDegreeInformation.getDistrictSubdivision());

        form.setHighSchoolType(precedentDegreeInformation.getPersonalIngressionData().getHighSchoolType());

        return form;
    }

    public boolean isDistrictAndSubdivisionRequired() {
        return true;
    }

    @Override
    protected void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model,
            final RedirectAttributes redirectAttributes) {
        Registration registration = getRegistration(executionYear, model);
        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("Invalid Request. Person mismatch");
        }

    }

    @Override
    protected boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        if (!(candidancyForm instanceof OriginInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.OriginInformationFormController.wrong.form.type"), model);
        }

        return validate(getRegistration(executionYear, model), (OriginInformationForm) candidancyForm, model);
    }

    protected boolean validate(final Registration registration, final OriginInformationForm form, final Model model) {
        final Set<String> result = validateForm(registration, form, model);

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    /**
     * @see {@link com.qubit.qubEdu.module.candidacies.domain.academicCandidacy.config.screen.validation.LastCompletedQualificationScreenValidator.validate(WorkflowInstanceState,
     *      WorkflowScreen)}
     */
    private Set<String> validateForm(final Registration registration, final OriginInformationForm form, final Model model) {
        final Set<String> result = Sets.newLinkedHashSet();

        /* -------
         * COUNTRY
         * -------
         */

        if (form.getCountryWhereFinishedPreviousCompleteDegree() == null) {
            result.add(BundleUtil.getString(BUNDLE, "error.personalInformation.requiredCountry"));
        }

        /* ------------------------
         * DISTRICT AND SUBDIVISION
         * ------------------------
         */

        if (form.getCountryWhereFinishedPreviousCompleteDegree() != null
                && form.getCountryWhereFinishedPreviousCompleteDegree().isDefaultCountry()
                && isDistrictAndSubdivisionRequired()) {
            if (form.getDistrictSubdivisionWhereFinishedPreviousCompleteDegree() == null
                    || form.getDistrictWhereFinishedPreviousCompleteDegree() == null) {
                result.add(BundleUtil.getString(BUNDLE,
                        "error.personalInformation.requiredDistrictAndSubdivisionForDefaultCountry"));
            }
        }

        /* ------------
         * SCHOOL LEVEL
         * ------------
         */

        if (form.getSchoolLevel() == null) {
            result.add(BundleUtil.getString(BUNDLE, "error.candidacy.workflow.OriginInformationForm.schoolLevel.must.be.filled"));
        }

        if (form.getSchoolLevel() == SchoolLevelType.OTHER && Strings.isNullOrEmpty(form.getOtherSchoolLevel())) {
            result.add(BundleUtil.getString(BUNDLE,
                    "error.candidacy.workflow.OriginInformationForm.otherSchoolLevel.must.be.filled"));
        }

        /* -----------
         * INSTITUTION
         * -----------
         */
        //TODO redo this verification
        if (isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel()) {
            if (form.getCountryWhereFinishedPreviousCompleteDegree() != null
                    && form.getCountryWhereFinishedPreviousCompleteDegree().isDefaultCountry()) {
                if (form.getSchoolLevel() != null && form.getSchoolLevel().isHigherEducation()) {
                    if (Strings.isNullOrEmpty(StringUtils.trim(form.getInstitutionOid()))
                            && Strings.isNullOrEmpty(StringUtils.trim(form.getInstitutionName()))) {
                        result.add(BundleUtil.getString(BUNDLE,
                                "error.candidacy.workflow.OriginInformationForm.institution.must.be.filled"));
                    }

                    if (form.getSchoolLevel().isHigherEducation()
                            && Strings.isNullOrEmpty(StringUtils.trim(form.getInstitutionOid()))) {
                        result.add(BundleUtil.getString(BUNDLE,
                                "error.candidacy.workflow.OriginInformationForm.institution.must.be.in.the.system"));
                    }
                } else {
                    if (Strings.isNullOrEmpty(StringUtils.trim(form.getInstitutionOid()))
                            && Strings.isNullOrEmpty(StringUtils.trim(form.getInstitutionName()))) {
                        result.add(BundleUtil.getString(BUNDLE,
                                "error.candidacy.workflow.OriginInformationForm.institution.must.be.filled"));
                    }

                }
            } else {
                if (Strings.isNullOrEmpty(StringUtils.trim(form.getInstitutionOid()))
                        && Strings.isNullOrEmpty(StringUtils.trim(form.getInstitutionName()))) {
                    result.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.OriginInformationForm.institution.must.be.filled"));
                }
            }
        } else {
            if (form.getCountryWhereFinishedPreviousCompleteDegree() != null
                    && form.getCountryWhereFinishedPreviousCompleteDegree().isDefaultCountry()
                    && form.getSchoolLevel().isHigherEducation()) {
                if (Strings.isNullOrEmpty(StringUtils.trim(form.getInstitutionOid()))) {
                    result.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.OriginInformationForm.institution.must.be.filled"));
                }
            }
        }

        /* ------------------
         * DEGREE DESIGNATION
         * ------------------
         */

        if (form.getCountryWhereFinishedPreviousCompleteDegree() != null
                && form.getCountryWhereFinishedPreviousCompleteDegree().isDefaultCountry() && form.getSchoolLevel() != null
                && form.getSchoolLevel().isHigherEducation()) {
            if (form.getRaidesDegreeDesignation() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.degreeDesignation.required"));
            }
        } else {
            if (isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel()) {
                if (Strings.isNullOrEmpty(form.getDegreeDesignation())) {
                    result.add(BundleUtil.getString(BUNDLE, "error.degreeDesignation.required"));
                }
            }
        }

        /* ----------------
         * CONCLUSION GRADE
         * ----------------
         */

        if (StringUtils.isNotBlank(form.getConclusionGrade()) && !form.getConclusionGrade().matches(GRADE_FORMAT)) {
            result.add(BundleUtil.getString(BUNDLE, "error.incorrect.conclusionGrade"));
        }

        /* ---------------
         * CONCLUSION YEAR
         * ---------------
         */

        if (StringUtils.isBlank(form.getConclusionYear()) || !form.getConclusionYear().matches(YEAR_FORMAT)) {
            result.add(BundleUtil.getString(BUNDLE, "error.incorrect.conclusionYear"));

        } else {

            final LocalDate now = new LocalDate();
            int conclusionYear = Integer.valueOf(form.getConclusionYear());
            if (now.getYear() < conclusionYear) {
                result.add(BundleUtil.getString(BUNDLE, "error.personalInformation.year.after.current"));
            }

            int birthYear = registration.getPerson().getDateOfBirthYearMonthDay().getYear();
            if (conclusionYear < birthYear) {
                result.add(BundleUtil.getString(BUNDLE, "error.personalInformation.year.before.birthday"));
            }
        }

        if (form.getSchoolLevel() != null && form.getSchoolLevel().isHighSchoolOrEquivalent()) {
            if (form.getHighSchoolType() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.highSchoolType.required"));
            }
        }

        return result;
    }

    protected boolean isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel() {
        return true;
    }

    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
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
                || !Strings.isNullOrEmpty(form.getInstitutionOid()) || !Strings.isNullOrEmpty(form.getInstitutionName())) {
            Unit institution = FenixFramework.getDomainObject(form.getInstitutionOid());

            if (institution == null && !form.getSchoolLevel().isHigherEducation()) {
                institution = UnitUtils.readExternalInstitutionUnitByName(form.getInstitutionName());
                if (institution == null) {
                    Unit externalInstitutionUnit = Bennu.getInstance().getExternalInstitutionUnit();
                    Unit highschools = externalInstitutionUnit.getChildUnitByAcronym("highschools");
                    Unit adhocHighschools = highschools.getChildUnitByAcronym("adhoc-highschools");
                    institution = Unit.createNewUnit(new MultiLanguageString(I18N.getLocale(), form.getInstitutionName()), null,
                            null, resolveAcronym(null, form.getInstitutionName()), new YearMonthDay(), null, adhocHighschools,
                            AccountabilityType.readByType(AccountabilityTypeEnum.ORGANIZATIONAL_STRUCTURE), null, null, null,
                            null, null);
                }
            }

            Country concludedCountry = form.getCountryWhereFinishedPreviousCompleteDegree();
            if (institution == null && form.getSchoolLevel().isHigherEducation() && concludedCountry != null
                    && !concludedCountry.isDefaultCountry()) {
                //TODO create new child to get all other higher schools
                Unit externalInstitutionUnit = Bennu.getInstance().getExternalInstitutionUnit();
                Unit highschools = externalInstitutionUnit.getChildUnitByAcronym("highschools");
                Unit adhocHighschools = highschools.getChildUnitByAcronym("adhoc-highschools");
                institution = Unit.createNewUnit(new MultiLanguageString(I18N.getLocale(), form.getInstitutionName()), null, null,
                        resolveAcronym(null, form.getInstitutionName()), new YearMonthDay(), null, adhocHighschools,
                        AccountabilityType.readByType(AccountabilityTypeEnum.ORGANIZATIONAL_STRUCTURE), null, null, null, null,
                        null);
            }

            precedentDegreeInformation.setInstitution(institution);
        }

        precedentDegreeInformation.setConclusionYear(Integer.valueOf(form.getConclusionYear()));
        Country country = form.getCountryWhereFinishedPreviousCompleteDegree();
        precedentDegreeInformation.setCountry(country);
        if (country.isDefaultCountry()) {
            precedentDegreeInformation.setDistrict(form.getDistrictWhereFinishedPreviousCompleteDegree());
            precedentDegreeInformation.setDistrictSubdivision(form.getDistrictSubdivisionWhereFinishedPreviousCompleteDegree());
        } else {
            precedentDegreeInformation.setDistrict(null);
            precedentDegreeInformation.setDistrictSubdivision(null);
        }
        if (form.getSchoolLevel() != null && form.getSchoolLevel().isHighSchoolOrEquivalent()) {
            precedentDegreeInformation.setCountryHighSchool(form.getCountryWhereFinishedPreviousCompleteDegree());
        }

        personalData.setHighSchoolType(form.getHighSchoolType());
    }

    public static String resolveAcronym(final String acronym, final String name) {
        final Unit externalInstitutionUnit = Bennu.getInstance().getExternalInstitutionUnit();
        final Unit highschools = externalInstitutionUnit.getChildUnitByAcronym("highschools");
        final List<String> takenAcronyms = new ArrayList<>();
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
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ContactsFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
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
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        return false;
    }

    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }

    protected Registration getRegistration(final ExecutionYear executionYear, final Model model) {
        return FirstTimeCandidacyController.getCandidacy().getRegistration();
    }

}
