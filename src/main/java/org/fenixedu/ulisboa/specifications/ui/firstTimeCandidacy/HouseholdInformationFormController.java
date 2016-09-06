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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.domain.ResidenceType;
import org.fenixedu.ulisboa.specifications.domain.SalarySpan;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public abstract class HouseholdInformationFormController extends FirstTimeCandidacyAbstractController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/OLD/firsttimecandidacy/{executionYearId}/householdinformationform";

    public static final String _FILLHOUSEHOLDINFORMATION_URI = "/fillhouseholdinformation";
    public static final String FILLHOUSEHOLDINFORMATION_URL = CONTROLLER_URL + _FILLHOUSEHOLDINFORMATION_URI;

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(urlWithExecutionYear(FiliationFormController.FILLFILIATION_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    protected HouseholdInformationForm createEmptyHouseholdInformationForm(final Student student, final Model model) {
        final HouseholdInformationForm form = new HouseholdInformationForm();

        PersonUlisboaSpecifications personUl = student.getPerson().getPersonUlisboaSpecifications();
        if (personUl != null) {
            form.setProfessionTimeType(personUl.getProfessionTimeType());
            form.setHouseholdSalarySpan(personUl.getHouseholdSalarySpan());
        }

        return form;
    }

    protected HouseholdInformationForm createHouseholdInformationForm(final Student student, final ExecutionYear executionYear,
            final boolean create) {
        final PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, create);
        
        final HouseholdInformationForm form = new HouseholdInformationForm();
        
        if(personalData != null) {
            form.setFatherProfessionalCondition(personalData.getFatherProfessionalCondition());
            form.setFatherProfessionType(personalData.getFatherProfessionType());
            form.setFatherSchoolLevel(personalData.getFatherSchoolLevel());
            form.setMotherProfessionalCondition(personalData.getMotherProfessionalCondition());
            form.setMotherProfessionType(personalData.getMotherProfessionType());
            form.setMotherSchoolLevel(personalData.getMotherSchoolLevel());
            
            form.setMaritalStatus(personalData.getMaritalStatus());
            form.setProfessionType(personalData.getProfessionType());
            form.setGrantOwnerType(personalData.getGrantOwnerType());
            
            Unit grantOwnerProvider = personalData.getGrantOwnerProvider();
            form.setGrantOwnerProvider(grantOwnerProvider != null ? grantOwnerProvider.getExternalId() : null);
            form.setProfessionalCondition(personalData.getProfessionalCondition());
            
            form.setDislocatedFromPermanentResidence(personalData.getDislocatedFromPermanentResidence() != null ? personalData.getDislocatedFromPermanentResidence() : false);
            form.setCountryOfResidence(personalData.getCountryOfResidence());
            form.setPermanentResidenceDistrict(personalData.getDistrictSubdivisionOfResidence() != null ? personalData
                    .getDistrictSubdivisionOfResidence().getDistrict() : null);
            form.setPermanentResidentDistrictSubdivision(personalData.getDistrictSubdivisionOfResidence());
        }
        
        if (form.getMaritalStatus() == null) {
            form.setMaritalStatus(MaritalStatus.SINGLE);
        }

        if (form.getProfessionType() == null) {
            form.setProfessionType(ProfessionType.OTHER);
        }
        if (form.getGrantOwnerType() == null) {
            form.setGrantOwnerType(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP);
        }
        
        if (form.getProfessionalCondition() == null) {
            form.setProfessionalCondition(ProfessionalSituationConditionType.STUDENT);
        }

        form.setProfession(student.getPerson().getProfession());

        PersonUlisboaSpecifications personUl = student.getPerson().getPersonUlisboaSpecifications();
        if (personUl != null) {
            form.setProfessionTimeType(personUl.getProfessionTimeType());
            form.setHouseholdSalarySpan(personUl.getHouseholdSalarySpan());
            form.setCountryHighSchool(personUl.getPerson().getCountryHighSchool());
            form.setDislocatedResidenceType(personUl.getDislocatedResidenceType());
        }

        return form;
    }

    protected String nextScreen(final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ResidenceInformationFormController.FILLRESIDENCEINFORMATION_URL, executionYear), model, redirectAttributes);
    }

    protected boolean validate(HouseholdInformationForm form, Model model) {
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
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }
        if (form.getMotherProfessionalCondition() == null || form.getMotherProfessionType() == null
                || form.getMotherSchoolLevel() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        if (form.getProfessionalCondition() == null || form.getProfessionType() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        if (form.getHouseholdSalarySpan() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        if (form.isStudentWorking()) {
            if (StringUtils.isEmpty(form.getProfession()) && isProfessionRequired()) {
                messages.add(
                        BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.profession.required"));
            }

            if (form.getProfessionTimeType() == null && isProfessionRequired()) {
                messages.add(BundleUtil.getString(BUNDLE,
                        "error.candidacy.workflow.PersonalInformationForm.professionTimeType.required"));
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

        if (isToFillCountryHighSchool() && form.getCountryHighSchool() == null) {
            messages.add(
                    BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.countryHighSchool.required"));
        }

        if (form.isDislocatedFromPermanentResidence() && form.getCountryOfResidence() == null) {
            messages.add(
                    BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.countryOfResidence.required"));
        }

        if (form.isDislocatedFromPermanentResidence() && form.getCountryOfResidence() != null
                && form.getCountryOfResidence().isDefaultCountry() && form.getPermanentResidentDistrictSubdivision() == null) {
            messages.add(BundleUtil.getString(BUNDLE,
                    "error.candidacy.workflow.PersonalInformationForm.permanentResidentDistrictSubdivision.required"));
        }

        if (form.isDislocatedFromPermanentResidence() && form.getDislocatedResidenceType() == null) {
            messages.add(BundleUtil.getString(BUNDLE,
                    "error.candidacy.workflow.PersonalInformationForm.dislocatedResidenceType.required"));
        }

        return messages;
    }

    @Atomic
    protected void writeData(final Student student, final ExecutionYear executionYear, final HouseholdInformationForm form,
            final Model model) {
        PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, true);
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(student.getPerson());

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
            if (grantOwnerProvider == null && (grantOwnerType == GrantOwnerType.OTHER_INSTITUTION_GRANT_OWNER
                    || grantOwnerType == GrantOwnerType.ORIGIN_COUNTRY_GRANT_OWNER)) {
                //We accept new institutions for these 2 cases
                grantOwnerProvider = Unit.createNewNoOfficialExternalInstitution(form.getGrantOwnerProvider());
            }
            personalData.setGrantOwnerProvider(grantOwnerProvider);
        } else {
            personalData.setGrantOwnerProvider(null);
        }

        personalData.getStudent().getPerson().setMaritalStatus(form.getMaritalStatus());
        personalData.setMaritalStatus(form.getMaritalStatus());

        personUlisboa.setHouseholdSalarySpan(form.getHouseholdSalarySpan());

        if (isToFillCountryHighSchool()) {
            personalData.getStudent().getPerson().setCountryHighSchool(form.getCountryHighSchool());
        }

        personalData.setDislocatedFromPermanentResidence(form.isDislocatedFromPermanentResidence());
        personalData.setCountryOfResidence(form.getCountryOfResidence());
        personalData.setDistrictSubdivisionOfResidence(form.getPermanentResidentDistrictSubdivision());
        personUlisboa.setDislocatedResidenceType(form.getDislocatedResidenceType());

    }

    protected boolean isProfessionRequired() {
        return true;
    }

    protected boolean isToFillCountryHighSchool() {
        return false;
    }

    public static class HouseholdInformationForm {

        private ExecutionYear executionYear;

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

        private MaritalStatus maritalStatus;

        private Country countryHighSchool;

        private boolean dislocatedFromPermanentResidence;

        private Country countryOfResidence;

        private District permanentResidenceDistrict;

        private DistrictSubdivision permanentResidentDistrictSubdivision;
        
        private ResidenceType dislocatedResidenceType;

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

        public MaritalStatus getMaritalStatus() {
            return maritalStatus;
        }

        public void setMaritalStatus(MaritalStatus maritalStatus) {
            this.maritalStatus = maritalStatus;
        }

        public Country getCountryHighSchool() {
            return countryHighSchool;
        }

        public void setCountryHighSchool(Country countryHighSchool) {
            this.countryHighSchool = countryHighSchool;
        }
        
        public ResidenceType getDislocatedResidenceType() {
            return dislocatedResidenceType;
        }
        
        public void setDislocatedResidenceType(ResidenceType dislocatedResidenceType) {
            this.dislocatedResidenceType = dislocatedResidenceType;
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

        public ExecutionYear getExecutionYear() {
            return executionYear;
        }

        public void setExecutionYear(ExecutionYear executionYear) {
            this.executionYear = executionYear;
        }

        public boolean isDislocatedFromPermanentResidence() {
            return dislocatedFromPermanentResidence;
        }

        public void setDislocatedFromPermanentResidence(boolean dislocatedFromPermanentResidence) {
            this.dislocatedFromPermanentResidence = dislocatedFromPermanentResidence;
        }

        public Country getCountryOfResidence() {
            return countryOfResidence;
        }

        public void setCountryOfResidence(Country countryOfResidence) {
            this.countryOfResidence = countryOfResidence;
        }

        public District getPermanentResidenceDistrict() {
            return permanentResidenceDistrict;
        }

        public void setPermanentResidenceDistrict(District permanentResidenceDistrict) {
            this.permanentResidenceDistrict = permanentResidenceDistrict;
        }

        public DistrictSubdivision getPermanentResidentDistrictSubdivision() {
            return permanentResidentDistrictSubdivision;
        }

        public void setPermanentResidentDistrictSubdivision(DistrictSubdivision permanentResidentDistrictSubdivision) {
            this.permanentResidentDistrictSubdivision = permanentResidentDistrictSubdivision;
        }
    }
}
