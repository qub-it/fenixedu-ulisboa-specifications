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
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.YearMonthDay;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

public abstract class PreviousDegreeOriginInformationFormController extends FirstTimeCandidacyAbstractController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/OLD/firsttimecandidacy/{executionYearId}/previousdegreeorigininformationform";

    public static final String _FILLPREVIOUSDEGREEINFORMATION_URI = "/fillpreviousdegreeinformation";
    public static final String FILLPREVIOUSDEGREEINFORMATION_URL = CONTROLLER_URL + _FILLPREVIOUSDEGREEINFORMATION_URI;

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(urlWithExecutionYear(ContactsFormController.FILLCONTACTS_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @RequestMapping(value = _FILLPREVIOUSDEGREEINFORMATION_URI, method = RequestMethod.GET)
    public String fillpreviousdegreeinformation(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        String url = getControllerURL() + _FILLPREVIOUSDEGREEINFORMATION_URI;
        return redirect(urlWithExecutionYear(url, executionYear)
                + "/" + findPreviousDegreePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).get(0).getRegistration().getExternalId(),
                model, redirectAttributes);
    }

    private static final String JSP_PATH =
            "fenixedu-ulisboa-specifications/firsttimecandidacy/previousdegreeorigininformationform";

    @RequestMapping(value = _FILLPREVIOUSDEGREEINFORMATION_URI + "/{registrationId}", method = RequestMethod.GET)
    public String fillpreviousdegreeinformation(
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("registrationId") final Registration registration,
            final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);

        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("invalid request");
        }

        model.addAttribute("schoolLevelValues", schoolLevelTypeValues());
        model.addAttribute("countries", Bennu.getInstance().getCountrysSet());

        fillFormIfRequired(executionYear, registration, model);

        addInfoMessage(ULisboaSpecificationsUtil.bundle("label.firstTimeCandidacy.fillPreviousDegreeInformation.info"), model);

        return jspPage("fillpreviousdegreeinformation");
    }

    protected List<SchoolLevelType> schoolLevelTypeValues() {
        final List<SchoolLevelType> result = Lists.newArrayList();

        result.add(SchoolLevelType.BACHELOR_DEGREE);
        result.add(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.DEGREE);
        result.add(SchoolLevelType.DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.DOCTORATE_DEGREE);
        result.add(SchoolLevelType.DOCTORATE_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.MASTER_DEGREE);
        result.add(SchoolLevelType.MASTER_DEGREE_INTEGRATED);
        result.add(SchoolLevelType.BACHELOR_DEGREE_PRE_BOLOGNA);
        result.add(SchoolLevelType.OTHER);

        return result;
    }

    protected void fillFormIfRequired(final ExecutionYear executionYear, final Registration registration, Model model) {
        model.addAttribute("registration", registration);

        PreviousDegreeInformationForm form = (PreviousDegreeInformationForm) model.asMap().get("previousDegreeInformationForm");

        if (form == null) {
            form = createPreviousDegreeInformationForm(executionYear, registration);
        }

        if (!StringUtils.isEmpty(form.getPrecedentInstitutionOid())) {
            DomainObject institutionObject = FenixFramework.getDomainObject(form.getPrecedentInstitutionOid());
            if (institutionObject instanceof Unit && FenixFramework.isDomainObjectValid(institutionObject)) {
                form.setPrecedentInstitutionName(((Unit) institutionObject).getName());
            } else {
                form.setPrecedentInstitutionName(form.getPrecedentInstitutionOid());
            }
        }

        model.addAttribute("previousDegreeInformationForm", form);
    }

    protected PreviousDegreeInformationForm createPreviousDegreeInformationForm(final ExecutionYear executionYear, final Registration registration) {
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
        if ((form.getPrecedentSchoolLevel() != null) && form.getPrecedentCountry() != null
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

    @RequestMapping(value = _FILLPREVIOUSDEGREEINFORMATION_URI + "/{registrationId}", method = RequestMethod.POST)
    public String fillpreviousinformation(PreviousDegreeInformationForm form,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("registrationId") final Registration registration, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (registration.getPerson() != getStudent(model).getPerson()) {
            throw new RuntimeException("invalid request");
        }

        final Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        if (!validate(executionYear, registration, form, model)) {
            return fillpreviousdegreeinformation(executionYear, registration, model, redirectAttributes);
        }

        try {
            writeData(registration, form);

            if (findCompletePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).isEmpty()) {
                return nextScreen(executionYear, model, redirectAttributes);
            }

            String url = getControllerURL() + _FILLPREVIOUSDEGREEINFORMATION_URI;
            return redirect(urlWithExecutionYear(url, executionYear)
                    + "/" + findCompletePrecedentDegreeInformationsToFill(executionYear, getStudent(model)).get(0).getRegistration().getExternalId(),
                    model, redirectAttributes);

        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.error.create") + de.getLocalizedMessage(), model);
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + getStudent(model).getPerson().getUsername());
            de.printStackTrace();
            return fillpreviousdegreeinformation(executionYear, model, redirectAttributes);
        }
    }

    protected String nextScreen(final ExecutionYear executionYear, final Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(DisabilitiesFormController.FILLDISABILITIES_URL, executionYear), model, redirectAttributes);
    }

    /**
     * @see {@link com.qubit.qubEdu.module.candidacies.domain.academicCandidacy.config.screen.validation.PreviousQualificationScreenValidator.validate(WorkflowInstanceState,
     *      WorkflowScreen)}
     */
    protected boolean validate(final ExecutionYear executionYear, final Registration registration, PreviousDegreeInformationForm form, Model model) {

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

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    protected boolean isDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel() {
        return true;
    }

    public static class PreviousDegreeInformationForm {

        private SchoolLevelType precedentSchoolLevel;

        private String otherPrecedentSchoolLevel;

        private String precedentDegreeDesignation;

        private String precedentInstitutionOid;

        private String precedentInstitutionName;

        private DegreeDesignation raidesPrecedentDegreeDesignation;

        private Country precedentCountry;

        private int numberOfEnrolmentsInPreviousDegrees;

        public SchoolLevelType getPrecedentSchoolLevel() {
            return precedentSchoolLevel;
        }

        public void setPrecedentSchoolLevel(SchoolLevelType precedentSchoolLevel) {
            this.precedentSchoolLevel = precedentSchoolLevel;
        }

        public String getOtherPrecedentSchoolLevel() {
            return otherPrecedentSchoolLevel;
        }

        public void setOtherPrecedentSchoolLevel(String otherPrecedentSchoolLevel) {
            this.otherPrecedentSchoolLevel = otherPrecedentSchoolLevel;
        }

        public String getPrecedentInstitutionOid() {
            return precedentInstitutionOid;
        }

        public void setPrecedentInstitutionOid(String precedentInstitutionOid) {
            this.precedentInstitutionOid = precedentInstitutionOid;
        }

        public String getPrecedentInstitutionName() {
            return precedentInstitutionName;
        }

        public void setPrecedentInstitutionName(String precedentInstitutionName) {
            this.precedentInstitutionName = precedentInstitutionName;
        }

        public DegreeDesignation getRaidesPrecedentDegreeDesignation() {
            return raidesPrecedentDegreeDesignation;
        }

        public void setRaidesPrecedentDegreeDesignation(DegreeDesignation raidesPrecedentDegreeDesignation) {
            this.raidesPrecedentDegreeDesignation = raidesPrecedentDegreeDesignation;
        }

        public Country getPrecedentCountry() {
            return precedentCountry;
        }

        public void setPrecedentCountry(Country precedentCountry) {
            this.precedentCountry = precedentCountry;
        }

        public void setPrecedentDegreeDesignation(String precedentDegreeDesignation) {
            this.precedentDegreeDesignation = precedentDegreeDesignation;
        }

        public String getPrecedentDegreeDesignation() {
            if ((getPrecedentSchoolLevel() != null) && getPrecedentSchoolLevel().isHigherEducation()
                    && (getRaidesPrecedentDegreeDesignation() != null)) {
                return getRaidesPrecedentDegreeDesignation().getDescription();
            }

            return precedentDegreeDesignation;
        }

        public int getNumberOfEnrolmentsInPreviousDegrees() {
            return numberOfEnrolmentsInPreviousDegrees;
        }

        public void setNumberOfEnrolmentsInPreviousDegrees(int numberOfEnrolmentsInPreviousDegrees) {
            this.numberOfEnrolmentsInPreviousDegrees = numberOfEnrolmentsInPreviousDegrees;
        }
    }

}
