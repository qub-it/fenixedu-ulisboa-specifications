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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.domain.SalarySpan;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(HouseholdInformationFormController.CONTROLLER_URL)
public class HouseholdInformationFormController extends FirstTimeCandidacyAbstractController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/householdinformationform";

    public static final String _FILLHOUSEHOLDINFORMATION_URI = "/fillhouseholdinformation";
    public static final String FILLHOUSEHOLDINFORMATION_URL = CONTROLLER_URL + _FILLHOUSEHOLDINFORMATION_URI;

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(Model model, RedirectAttributes redirectAttributes) {
        return redirect(FiliationFormController.FILLFILIATION_URL, model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @RequestMapping(value = _FILLHOUSEHOLDINFORMATION_URI, method = RequestMethod.GET)
    public String fillhouseholdinformation(Model model, RedirectAttributes redirectAttributes) {
        if(isFormIsFilled(model)) {
            return nextScreen(model, redirectAttributes);
        }
        
        Optional<String> accessControlRedirect = accessControlRedirect(model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        model.addAttribute("schoolLevelValues", SchoolLevelType.values());
        model.addAttribute("professionTypeValues", ProfessionType.values());
        model.addAttribute("professionalConditionValues", ProfessionalSituationConditionType.values());
        model.addAttribute("salarySpanValues", SalarySpan.readAll().collect(Collectors.toList()));
        model.addAttribute("professionTimeTypeValues", ProfessionTimeType.readAll().collect(Collectors.toList()));
        model.addAttribute("grantOwnerTypeValues", GrantOwnerType.values());
        
        fillFormIfRequired(model);
        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillHouseHoldInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/householdinformationform/fillhouseholdinformation";
    }

    protected void fillFormIfRequired(Model model) {
        if (!model.containsAttribute("householdInformationForm")) {
            HouseholdInformationForm form = createHouseholdInformationForm();

            model.addAttribute("householdInformationForm", form);
        }
    }

    protected HouseholdInformationForm createHouseholdInformationForm() {
        final HouseholdInformationForm form = new HouseholdInformationForm();
        PersonalIngressionData personalData = getPersonalIngressionData();
        form.setFatherProfessionalCondition(personalData.getFatherProfessionalCondition());
        form.setFatherProfessionType(personalData.getFatherProfessionType());
        form.setFatherSchoolLevel(personalData.getFatherSchoolLevel());
        form.setMotherProfessionalCondition(personalData.getMotherProfessionalCondition());
        form.setMotherProfessionType(personalData.getMotherProfessionType());
        form.setMotherSchoolLevel(personalData.getMotherSchoolLevel());

        form.setProfessionType(personalData.getProfessionType());
        if (form.getProfessionType() == null) {
            form.setProfessionType(ProfessionType.OTHER);
        }
        form.setGrantOwnerType(personalData.getGrantOwnerType());
        if (form.getGrantOwnerType() == null) {
            form.setGrantOwnerType(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP);
        }
        Unit grantOwnerProvider = personalData.getGrantOwnerProvider();
        form.setGrantOwnerProvider(grantOwnerProvider != null ? grantOwnerProvider.getExternalId() : null);
        form.setProfessionalCondition(personalData.getProfessionalCondition());
        if (form.getProfessionalCondition() == null) {
            form.setProfessionalCondition(ProfessionalSituationConditionType.STUDENT);
        }
        
        form.setProfession(personalData.getStudent().getPerson().getProfession());
        
        PersonUlisboaSpecifications personUl = AccessControl.getPerson().getPersonUlisboaSpecifications();
        if (personUl != null) {
            form.setProfessionTimeType(personUl.getProfessionTimeType());
            form.setHouseholdSalarySpan(personUl.getHouseholdSalarySpan());
        }
        return form;
    }

    @RequestMapping(value = _FILLHOUSEHOLDINFORMATION_URI, method = RequestMethod.POST)
    public String fillhouseholdinformation(HouseholdInformationForm form, Model model, RedirectAttributes redirectAttributes) {
        Optional<String> accessControlRedirect = accessControlRedirect(model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        if (!validate(form, model)) {
            model.addAttribute("householdInformationForm", form);
            return fillhouseholdinformation(model, redirectAttributes);
        }

        try {
            writeData(form);
            model.addAttribute("householdInformationForm", form);
            return nextScreen(model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
            de.printStackTrace();
            return fillhouseholdinformation(model, redirectAttributes);
        }
    }

    protected String nextScreen(Model model, RedirectAttributes redirectAttributes) {
        return redirect(ResidenceInformationFormController.FILLRESIDENCEINFORMATION_URL, model, redirectAttributes);
    }

    private boolean validate(HouseholdInformationForm form, Model model) {
        final Set<String> messages = validateHouseholdInformationForm(form);
        
        for (final String message : messages) {
            addErrorMessage(message, model);
        }
        
        return messages.isEmpty();
    }

    public Set<String> validateHouseholdInformationForm(HouseholdInformationForm form) {
        final Set<String> messages = Sets.newHashSet();
        
        if (form.getFatherProfessionalCondition() == null || form.getFatherProfessionType() == null
                || form.getFatherSchoolLevel() == null) {
            messages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }
        if (form.getMotherProfessionalCondition() == null || form.getMotherProfessionType() == null
                || form.getMotherSchoolLevel() == null) {
            messages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }
        
        if(form.getProfessionalCondition() == null || form.getProfessionType() == null) {
            messages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));            
        }
        
        if (form.getHouseholdSalarySpan() == null) {
            messages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }
        
        if (form.isStudentWorking()) {
            if (StringUtils.isEmpty(form.getProfession())) {
                messages.add(BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.profession.required"));
            }
            
            if (form.getProfessionTimeType() == null) {
                messages.add(BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.professionTimeType.required"));
            }
        }

        GrantOwnerType grantOwnerType = form.getGrantOwnerType();
        if (grantOwnerType.equals(GrantOwnerType.OTHER_INSTITUTION_GRANT_OWNER)
                || grantOwnerType.equals(GrantOwnerType.ORIGIN_COUNTRY_GRANT_OWNER)) {
            if (StringUtils.isEmpty(form.getGrantOwnerProvider())) {
                messages.add(BundleUtil.getString(BUNDLE, 
                        "error.candidacy.workflow.PersonalInformationForm.grant.owner.must.choose.granting.institution"));
            }
        }
        
        
        return messages;
    }

    @Atomic
    private void writeData(HouseholdInformationForm form) {
        PersonalIngressionData personalData = getPersonalIngressionData();
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(AccessControl.getPerson());

        personalData.setFatherProfessionalCondition(form.getFatherProfessionalCondition());
        personalData.setFatherProfessionType(form.getFatherProfessionType());
        personalData.setFatherSchoolLevel(form.getFatherSchoolLevel());
        personalData.setMotherProfessionalCondition(form.getMotherProfessionalCondition());
        personalData.setMotherProfessionType(form.getMotherProfessionType());
        personalData.setMotherSchoolLevel(form.getMotherSchoolLevel());
        personalData.setProfessionalCondition(form.getProfessionalCondition());
        personalData.getStudent().getPerson().setProfession(form.getProfession());
        personalData.setProfessionType(form.getProfessionType());
        personUlisboa.setProfessionTimeType(form.getProfessionTimeType());

        GrantOwnerType grantOwnerType = form.getGrantOwnerType();
        personalData.setGrantOwnerType(grantOwnerType);
        if (grantOwnerType != null && !grantOwnerType.equals(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP)) {
            Unit grantOwnerProvider = FenixFramework.getDomainObject(form.getGrantOwnerProvider());
            if (grantOwnerProvider == null
                    && (grantOwnerType == GrantOwnerType.OTHER_INSTITUTION_GRANT_OWNER || grantOwnerType == GrantOwnerType.ORIGIN_COUNTRY_GRANT_OWNER)) {
                //We accept new institutions for these 2 cases
                grantOwnerProvider = Unit.createNewNoOfficialExternalInstitution(form.getGrantOwnerProvider());
            }
            personalData.setGrantOwnerProvider(grantOwnerProvider);
        } else {
            personalData.setGrantOwnerProvider(null);
        }
        
        personUlisboa.setHouseholdSalarySpan(form.getHouseholdSalarySpan());
    }
    
    @Override
    protected boolean isFormIsFilled(final Model model) {
        return false;
    }

    public static class HouseholdInformationForm {

        private SchoolLevelType motherSchoolLevel;

        private ProfessionType motherProfessionType;

        private ProfessionalSituationConditionType motherProfessionalCondition;

        private SchoolLevelType fatherSchoolLevel;

        private ProfessionType fatherProfessionType;

        private ProfessionalSituationConditionType fatherProfessionalCondition;

        private SalarySpan householdSalarySpan;

        private ProfessionalSituationConditionType professionalCondition;
        
        private String profession;

        private ProfessionType professionType;

        private ProfessionTimeType professionTimeType;
        
        private GrantOwnerType grantOwnerType;
        
        private String grantOwnerProvider;
        
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

        public SalarySpan getHouseholdSalarySpan() {
            return householdSalarySpan;
        }

        public void setHouseholdSalarySpan(SalarySpan householdSalarySpan) {
            this.householdSalarySpan = householdSalarySpan;
        }
        
        public ProfessionType getProfessionType() {
            return professionType;
        }

        public void setProfessionType(ProfessionType professionType) {
            this.professionType = professionType;
        }

        public ProfessionalSituationConditionType getProfessionalCondition() {
            return professionalCondition;
        }

        public void setProfessionalCondition(ProfessionalSituationConditionType professionalCondition) {
            this.professionalCondition = professionalCondition;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }

        public GrantOwnerType getGrantOwnerType() {
            return grantOwnerType;
        }

        public void setGrantOwnerType(GrantOwnerType grantOwnerType) {
            this.grantOwnerType = grantOwnerType;
        }

        public String getGrantOwnerProvider() {
            return grantOwnerProvider;
        }

        public void setGrantOwnerProvider(String grantOwnerProvider) {
            this.grantOwnerProvider = grantOwnerProvider;
        }

        public ProfessionTimeType getProfessionTimeType() {
            return professionTimeType;
        }

        public void setProfessionTimeType(ProfessionTimeType professionTimeType) {
            this.professionTimeType = professionTimeType;
        }
        
        public boolean isStudentWorking() {
            if (isWorkingCondition()) {
                return true;
            }
            if (!StringUtils.isEmpty(getProfession())) {
                return true;
            }
            if (getProfessionTimeType() != null) {
                return true;
            }
            if (isWorkingProfessionType()) {
                return true;
            }
            return false;
        }

        private boolean isWorkingCondition() {
            switch (getProfessionalCondition()) {
            case WORKS_FOR_OTHERS:
                return true;
            case EMPLOYEER:
                return true;
            case INDEPENDENT_WORKER:
                return true;
            case WORKS_FOR_FAMILY_WITHOUT_PAYMENT:
                return true;
            case HOUSEWIFE:
                return true;
            case MILITARY_SERVICE:
                return true;
            default:
                return false;
            }
        }

        private boolean isWorkingProfessionType() {
            switch (getProfessionType()) {
            case UNKNOWN:
                return false;
            case OTHER:
                return false;
            default:
                return true;
            }
        }

        public String getGrantOwnerProviderName() {
            Unit unit = FenixFramework.getDomainObject(getGrantOwnerProvider());
            if (unit == null) {
                return getGrantOwnerProvider();
            } else {
                return unit.getName();
            }
        }
    }
}
