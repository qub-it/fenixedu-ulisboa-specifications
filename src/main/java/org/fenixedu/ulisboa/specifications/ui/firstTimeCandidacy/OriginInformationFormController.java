/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: joao.roxo@qub-it.com 
 *               nuno.pinheiro@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
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
package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.AccountabilityType;
import org.fenixedu.academic.domain.organizationalStructure.AccountabilityTypeEnum;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitUtils;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public abstract class OriginInformationFormController extends FirstTimeCandidacyAbstractController {

    private static final String GRADE_FORMAT = "\\d{2}";

    private static final String YEAR_FORMAT = "\\d{4}";

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/OLD/firsttimecandidacy/{executionYearId}/origininformationform";

    public static final String _FILLORIGININFORMATION_URI = "/fillorigininformation";
    public static final String FILLORIGININFORMATION_URL = CONTROLLER_URL + _FILLORIGININFORMATION_URI;

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(urlWithExecutionYear(ContactsFormController.FILLCONTACTS_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @RequestMapping(value = _FILLORIGININFORMATION_URI, method = RequestMethod.GET)
    public String fillorigininformation(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        String url = getControllerURL() + _FILLORIGININFORMATION_URI;
        return redirect(urlWithExecutionYear(url, executionYear) + "/" 
                + findCompletePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).get(0).getRegistration().getExternalId(),
                model, redirectAttributes);
    }

    @RequestMapping(value = _FILLORIGININFORMATION_URI + "/{registrationId}", method = RequestMethod.GET)
    public String fillorigininformation(@PathVariable("executionYearId") final ExecutionYear executionYear, @PathVariable("registrationId") final Registration registration, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("invalid request");
        }

        model.addAttribute("schoolLevelValues", schoolLevelTypeValues());
        model.addAttribute("highSchoolTypeValues", AcademicalInstitutionType.getHighSchoolTypes());
        model.addAttribute("countries", Bennu.getInstance().getCountrysSet());
        model.addAttribute("districts_options", Bennu.getInstance().getDistrictsSet());

        fillFormIfRequired(registration, model);

        addInfoMessage(ULisboaSpecificationsUtil.bundle("label.firstTimeCandidacy.fillOriginInformation.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/origininformationform/fillorigininformation";
    }

    protected Object schoolLevelTypeValues() {
        final List<SchoolLevelType> result = Lists.newArrayList();

        result.add(SchoolLevelType.BACHELOR_DEGREE);
        result.add(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.MASTER_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.DEGREE);
        result.add(SchoolLevelType.DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.DOCTORATE_DEGREE);
        result.add(SchoolLevelType.DOCTORATE_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.MASTER_DEGREE);
        result.add(SchoolLevelType.MASTER_DEGREE_INTEGRATED);
        result.add(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.OTHER);
        result.add(SchoolLevelType.HIGH_SCHOOL_OR_EQUIVALENT);
        result.add(SchoolLevelType.MEDIUM_EDUCATION);
        result.add(SchoolLevelType.TECHNICAL_SPECIALIZATION);

        return result;
    }

    private void fillFormIfRequired(final Registration registration, Model model) {
        model.addAttribute("registration", registration);
        model.addAttribute("districtAndSubdivisionRequired", isDistrictAndSubdivisionRequired());

        if (!model.containsAttribute("originInformationForm")) {
            model.addAttribute("originInformationForm", createOriginInformationForm(registration));
        }

        final OriginInformationForm form = (OriginInformationForm) model.asMap().get("originInformationForm");
        if (!StringUtils.isEmpty(form.getInstitutionOid())) {
            DomainObject institutionObject = FenixFramework.getDomainObject(form.getInstitutionOid());
            if (institutionObject instanceof Unit && FenixFramework.isDomainObjectValid(institutionObject)) {
                form.setInstitutionName(((Unit) institutionObject).getName());
            } else {
                form.setInstitutionName(form.getInstitutionOid());
            }
        }
    }

    protected OriginInformationForm createOriginInformationForm(final Registration registration) {
        final OriginInformationForm form = new OriginInformationForm();

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
        if ((form.getSchoolLevel() != null) && form.getSchoolLevel().isHigherEducation()) {
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

        return form;
    }

    @RequestMapping(value = _FILLORIGININFORMATION_URI + "/{registrationId}", method = RequestMethod.POST)
    public String fillorigininformation(OriginInformationForm form,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("registrationId") final Registration registration, Model model, RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("invalid request");
        }

        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        if (!validate(registration, form, model)) {
            return fillorigininformation(executionYear, registration, model, redirectAttributes);
        }

        try {
            writeData(registration, form);

            if (findCompletePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).isEmpty()) {
                return nextScreen(executionYear, model, redirectAttributes);
            }

            final String url = getControllerURL() + _FILLORIGININFORMATION_URI;
            return redirect(urlWithExecutionYear(url, executionYear)
                    + "/" + findCompletePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).get(0).getRegistration().getExternalId(),
                    model, redirectAttributes);

        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.error.create") + de.getLocalizedMessage(), model);
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + getStudent(model).getPerson().getUsername());
            de.printStackTrace();
            return fillorigininformation(executionYear, model, redirectAttributes);
        }
    }

    protected String nextScreen(final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(PreviousDegreeOriginInformationFormController.FILLPREVIOUSDEGREEINFORMATION_URL, executionYear), 
                model, redirectAttributes);
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
            addErrorMessage(BundleUtil.getString(BUNDLE,
                    "error.candidacy.workflow.OriginInformationForm.schoolLevel.must.be.filled"), model);
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
        if ((form.getSchoolLevel() != null) && form.getSchoolLevel().isHighSchoolOrEquivalent()) {
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

    public boolean isDistrictAndSubdivisionRequired() {
        return true;
    }

    protected boolean isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel() {
        return true;
    }

    public static class OriginInformationForm {

        private static final long serialVersionUID = 1L;

        private SchoolLevelType schoolLevel;

        private String otherSchoolLevel;

        private String conclusionGrade;

        private String degreeDesignation;

        private Integer conclusionYear;

        private String institutionOid;

        private String institutionName;

        private DegreeDesignation raidesDegreeDesignation;

        private Country countryWhereFinishedPreviousCompleteDegree;
        private District districtWhereFinishedPreviousCompleteDegree;
        private DistrictSubdivision districtSubdivisionWhereFinishedPreviousCompleteDegree;

        private AcademicalInstitutionType highSchoolType;

        public SchoolLevelType getSchoolLevel() {
            return schoolLevel;
        }

        public void setSchoolLevel(SchoolLevelType schoolLevel) {
            this.schoolLevel = schoolLevel;
        }

        public String getOtherSchoolLevel() {
            return otherSchoolLevel;
        }

        public void setOtherSchoolLevel(String otherSchoolLevel) {
            this.otherSchoolLevel = otherSchoolLevel;
        }

        public String getConclusionGrade() {
            return conclusionGrade;
        }

        public void setConclusionGrade(String conclusionGrade) {
            this.conclusionGrade = conclusionGrade;
        }

        public String getDegreeDesignation() {
            if ((getSchoolLevel() != null) && getSchoolLevel().isHigherEducation() && (getRaidesDegreeDesignation() != null)) {
                return getRaidesDegreeDesignation().getDescription();
            }
            return degreeDesignation;
        }

        public void setDegreeDesignation(String degreeDesignation) {
            this.degreeDesignation = degreeDesignation;
        }

        public Integer getConclusionYear() {
            return conclusionYear;
        }

        public void setConclusionYear(Integer conclusionYear) {
            this.conclusionYear = conclusionYear;
        }

        public String getInstitutionOid() {
            return institutionOid;
        }

        public void setInstitutionOid(String institutionOid) {
            this.institutionOid = institutionOid;
        }

        public DegreeDesignation getRaidesDegreeDesignation() {
            return raidesDegreeDesignation;
        }

        public void setRaidesDegreeDesignation(DegreeDesignation raidesDegreeDesignation) {
            this.raidesDegreeDesignation = raidesDegreeDesignation;
        }

        public Country getCountryWhereFinishedPreviousCompleteDegree() {
            return countryWhereFinishedPreviousCompleteDegree;
        }

        public void setCountryWhereFinishedPreviousCompleteDegree(Country countryWhereFinishedPreviousCompleteDegree) {
            this.countryWhereFinishedPreviousCompleteDegree = countryWhereFinishedPreviousCompleteDegree;
        }

        public AcademicalInstitutionType getHighSchoolType() {
            if ((getSchoolLevel() != null) && (getSchoolLevel().isHighSchoolOrEquivalent())) {
                return highSchoolType;
            }
            return null;
        }

        public void setHighSchoolType(AcademicalInstitutionType highSchoolType) {
            this.highSchoolType = highSchoolType;
        }

        public static long getSerialversionuid() {
            return serialVersionUID;
        }

        public String getInstitutionName() {
            return institutionName;
        }

        public void setInstitutionName(String institutionName) {
            this.institutionName = institutionName;
        }

        public District getDistrictWhereFinishedPreviousCompleteDegree() {
            return districtWhereFinishedPreviousCompleteDegree;
        }

        public void setDistrictWhereFinishedPreviousCompleteDegree(District districtWhereFinishedPreviousCompleteDegree) {
            this.districtWhereFinishedPreviousCompleteDegree = districtWhereFinishedPreviousCompleteDegree;
        }

        public DistrictSubdivision getDistrictSubdivisionWhereFinishedPreviousCompleteDegree() {
            return districtSubdivisionWhereFinishedPreviousCompleteDegree;
        }

        public void setDistrictSubdivisionWhereFinishedPreviousCompleteDegree(
                DistrictSubdivision districtSubdivisionWhereFinishedPreviousCompleteDegree) {
            this.districtSubdivisionWhereFinishedPreviousCompleteDegree = districtSubdivisionWhereFinishedPreviousCompleteDegree;
        }

    }
}
