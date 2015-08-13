/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: xpto@qub-it.com
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

import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping(HouseholdInformationFormController.CONTROLLER_URL)
public class HouseholdInformationFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/householdinformationform";

    private static final String _FILLHOUSEHOLDINFORMATION_URI = "/fillhouseholdinformation";
    public static final String FILLHOUSEHOLDINFORMATION_URL = CONTROLLER_URL + _FILLHOUSEHOLDINFORMATION_URI;

    @RequestMapping(value = _FILLHOUSEHOLDINFORMATION_URI, method = RequestMethod.GET)
    public String fillhouseholdinformation(Model model) {
        model.addAttribute("schoolLevelValues", SchoolLevelType.values());
        model.addAttribute("professionTypeValues", ProfessionType.values());
        model.addAttribute("professionalConditionValues", ProfessionalSituationConditionType.values());

        fillFormIfRequired(model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/householdinformationform/fillhouseholdinformation";
    }

    private void fillFormIfRequired(Model model) {
        if (!model.containsAttribute("householdInformationForm")) {
            HouseholdInformationForm form = new HouseholdInformationForm();
            PersonalIngressionData personalData =
                    PersonalInformationFormController.getOrCreatePersonalIngressionData(InstructionsController
                            .getStudentCandidacy().getPrecedentDegreeInformation());
            form.setFatherProfessionalCondition(personalData.getFatherProfessionalCondition());
            form.setFatherProfessionType(personalData.getFatherProfessionType());
            form.setFatherSchoolLevel(personalData.getFatherSchoolLevel());
            form.setMotherProfessionalCondition(personalData.getMotherProfessionalCondition());
            form.setMotherProfessionType(personalData.getMotherProfessionType());
            form.setMotherSchoolLevel(personalData.getMotherSchoolLevel());

            model.addAttribute("householdInformationForm", form);
        }
    }

    @RequestMapping(value = _FILLHOUSEHOLDINFORMATION_URI, method = RequestMethod.POST)
    public String fillhouseholdinformation(HouseholdInformationForm form, Model model, RedirectAttributes redirectAttributes) {
        if (!validate(form, model)) {
            return fillhouseholdinformation(model);
        }

        try {
            writeData(form);
            model.addAttribute("householdInformationForm", form);
            return redirect(
                    "/fenixedu-ulisboa-specifications/firsttimecandidacy/residenceinformationform/fillresidenceinformation/",
                    model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillhouseholdinformation(model);
        }
    }

    private boolean validate(HouseholdInformationForm form, Model model) {
        if (form.getFatherProfessionalCondition() == null || form.getFatherProfessionType() == null
                || form.getFatherSchoolLevel() == null) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"),
                    model);
            return false;
        }
        if (form.getMotherProfessionalCondition() == null || form.getMotherProfessionType() == null
                || form.getMotherSchoolLevel() == null) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"),
                    model);
            return false;
        }

        return true;
    }

    @Atomic
    private void writeData(HouseholdInformationForm form) {
        PersonalIngressionData personalData =
                PersonalInformationFormController.getOrCreatePersonalIngressionData(InstructionsController.getStudentCandidacy()
                        .getPrecedentDegreeInformation());
        personalData.setFatherProfessionalCondition(form.getFatherProfessionalCondition());
        personalData.setFatherProfessionType(form.getFatherProfessionType());
        personalData.setFatherSchoolLevel(form.getFatherSchoolLevel());
        personalData.setMotherProfessionalCondition(form.getMotherProfessionalCondition());
        personalData.setMotherProfessionType(form.getMotherProfessionType());
        personalData.setMotherSchoolLevel(form.getMotherSchoolLevel());
    }

    public static class HouseholdInformationForm {

        private SchoolLevelType motherSchoolLevel;

        private ProfessionType motherProfessionType;

        private ProfessionalSituationConditionType motherProfessionalCondition;

        private SchoolLevelType fatherSchoolLevel;

        private ProfessionType fatherProfessionType;

        private ProfessionalSituationConditionType fatherProfessionalCondition;

        public SchoolLevelType getMotherSchoolLevel() {
            return motherSchoolLevel;
        }

        public void setMotherSchoolLevel(SchoolLevelType motherSchoolLevel) {
            this.motherSchoolLevel = motherSchoolLevel;
        }

        public ProfessionType getMotherProfessionType() {
            return motherProfessionType;
        }

        public void setMotherProfessionType(ProfessionType motherProfessionType) {
            this.motherProfessionType = motherProfessionType;
        }

        public ProfessionalSituationConditionType getMotherProfessionalCondition() {
            return motherProfessionalCondition;
        }

        public void setMotherProfessionalCondition(ProfessionalSituationConditionType motherProfessionalCondition) {
            this.motherProfessionalCondition = motherProfessionalCondition;
        }

        public SchoolLevelType getFatherSchoolLevel() {
            return fatherSchoolLevel;
        }

        public void setFatherSchoolLevel(SchoolLevelType fatherSchoolLevel) {
            this.fatherSchoolLevel = fatherSchoolLevel;
        }

        public ProfessionType getFatherProfessionType() {
            return fatherProfessionType;
        }

        public void setFatherProfessionType(ProfessionType fatherProfessionType) {
            this.fatherProfessionType = fatherProfessionType;
        }

        public ProfessionalSituationConditionType getFatherProfessionalCondition() {
            return fatherProfessionalCondition;
        }

        public void setFatherProfessionalCondition(ProfessionalSituationConditionType fatherProfessionalCondition) {
            this.fatherProfessionalCondition = fatherProfessionalCondition;
        }

    }
}
