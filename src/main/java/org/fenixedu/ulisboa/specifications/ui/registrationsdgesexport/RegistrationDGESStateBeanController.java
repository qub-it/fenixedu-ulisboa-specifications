/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: nuno.pinheiro@qub-it.com
 *
 * 
 * This file is part of FenixEdu ULisboaSpecifications.
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
package org.fenixedu.ulisboa.specifications.ui.registrationsdgesexport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.UniversityChoiceMotivationAnswer;
import org.fenixedu.ulisboa.specifications.domain.UniversityDiscoveryMeansAnswer;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.FenixFramework;
import edu.emory.mathcs.backport.java.util.Collections;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.registrationsDgesExport",
        accessGroup = "logged")
@RequestMapping(RegistrationDGESStateBeanController.CONTROLLER_URL)
public class RegistrationDGESStateBeanController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/registrationsdgesexport";

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping
    public String search(Model model, @RequestParam(required = false) ExecutionYear executionYear,
            @RequestParam(required = false) Integer phase, @RequestParam(required = false) Boolean exportStatistics) {
        executionYear = executionYear != null ? executionYear : ExecutionYear.readCurrentExecutionYear();
        phase = phase != null ? phase : 1;
        List<RegistrationDGESStateBean> searchregistrationdgesstatebeanResultsDataSet =
                filterSearchRegistrationDGESStateBean(executionYear, phase);

        model.addAttribute("searchregistrationdgesstatebeanResultsDataSet", searchregistrationdgesstatebeanResultsDataSet);
        model.addAttribute("executionYears",
                Bennu.getInstance().getExecutionYearsSet().stream().sorted().collect(Collectors.toList()));
        model.addAttribute("phases", getPhases());
        model.addAttribute("selectedExecutionYear", executionYear);
        model.addAttribute("selectedPhase", phase);
        model.addAttribute("exportStatistics", exportStatistics);
        return "registrationsdgesexport/search";
    }

    @RequestMapping(value = "/reactivate/{candidacyId}")
    public String reActivateCandidacy(Model model, @PathVariable("candidacyId") String candidacyId,
            RedirectAttributes redirectAttributes) {
        FirstTimeCandidacy candidacy = FenixFramework.getDomainObject(candidacyId);
        if (!FenixFramework.isDomainObjectValid(candidacy)) {
            throw new RuntimeException("Invalid externalId: " + candidacyId + " is not a FirstTimeCandidacy");
        }

        candidacy.revertCancel();
        return redirect(CONTROLLER_URL, model, redirectAttributes);
    }

    private Collection<Integer> getPhases() {
        EntryPhase[] values = EntryPhase.values();
        List<Integer> phases = new ArrayList<>();
        for (EntryPhase phase : values) {
            phases.add(phase.getPhaseNumber());
        }
        return phases;
    }

    private List<RegistrationDGESStateBean> filterSearchRegistrationDGESStateBean(ExecutionYear executionYear, int phase) {
        Predicate<? super StudentCandidacy> hasDgesImportationForCurrentPhase =
                sc -> sc.getDgesStudentImportationProcess() != null
                        && sc.getDgesStudentImportationProcess().getEntryPhase().getPhaseNumber() == phase;
        return executionYear.getStudentCandidacies().stream().filter(hasDgesImportationForCurrentPhase)
                .map(sc -> populateBean(sc)).collect(Collectors.toList());
    }

    private RegistrationDGESStateBean populateBean(StudentCandidacy studentCandidacy) {
        Person person = studentCandidacy.getPerson();
        String degreeCode = studentCandidacy.getDegreeCurricularPlan().getDegree().getMinistryCode();
        String documentIdNumber = person.getDocumentIdNumber();
        String candidacyState =
                BundleUtil.getString("resources/EnumerationResources", studentCandidacy.getActiveCandidacySituationType()
                        .getQualifiedName());
        String name = person.getName();
        String registrationStatus = "";
        if (studentCandidacy.getActiveCandidacySituationType().equals(CandidacySituationType.REGISTERED)) {
            registrationStatus = "Sim";
        } else {
            registrationStatus = "Não";
        }

        String nationality = person.getCountry().getLocalizedName().getContent();
        String secondNationality = "";
        String ingressionType = ((FirstTimeCandidacy) studentCandidacy).getIngressionType().getLocalizedName();
        Integer placingOption = ((FirstTimeCandidacy) studentCandidacy).getPlacingOption();
        String firstOptionDegree = "";
        String firstOptionInstitution = "";
        String dislocatedResidenceType = "";
        String profession = person.getProfession();
        String professionTimeType = "";
        String professionalCondition = "";
        String professionType = "";
        String fatherName = person.getNameOfFather();
        String fatherSchoolLevel = "";
        String fatherProfessionalCondition = "";
        String fatherProfessionType = "";
        String motherName = person.getNameOfMother();
        String motherSchoolLevel = "";
        String motherProfessionalCondition = "";
        String motherProfessionType = "";
        String salarySpan = "";
        String disabilityType = "";
        String needsDisabilitySupport = "";
        String universityDiscoveryString = "";
        String universityChoiceString = "";

        Comparator<PersonalIngressionData> comparator =
                Collections.reverseOrder(PersonalIngressionData.COMPARATOR_BY_EXECUTION_YEAR);
        PersonalIngressionData pid =
                person.getStudent().getPersonalIngressionsDataSet().stream().sorted(comparator).findFirst().orElse(null);
        if (pid != null) {
            if (pid.getProfessionalCondition() != null) {
                professionalCondition = pid.getProfessionalCondition().getLocalizedName();
            }
            if (pid.getProfessionType() != null) {
                professionType = pid.getProfessionType().getLocalizedName();
            }

            if (pid.getFatherSchoolLevel() != null) {
                fatherSchoolLevel = pid.getFatherSchoolLevel().getLocalizedName();
            }
            if (pid.getFatherProfessionalCondition() != null) {
                fatherProfessionalCondition = pid.getFatherProfessionalCondition().getLocalizedName();
            }
            if (pid.getFatherProfessionType() != null) {
                fatherProfessionType = pid.getFatherProfessionType().getLocalizedName();
            }

            if (pid.getMotherSchoolLevel() != null) {
                motherSchoolLevel = pid.getMotherSchoolLevel().getLocalizedName();
            }
            if (pid.getMotherProfessionalCondition() != null) {
                motherProfessionalCondition = pid.getMotherProfessionalCondition().getLocalizedName();
            }
            if (pid.getMotherProfessionType() != null) {
                motherProfessionType = pid.getMotherProfessionType().getLocalizedName();
            }
        }
        PersonUlisboaSpecifications personUl = person.getPersonUlisboaSpecifications();
        if (personUl != null) {
            if (personUl.getSecondNationality() != null) {
                secondNationality = personUl.getSecondNationality().getLocalizedName().getContent();
            }
            if (personUl.getDislocatedResidenceType() != null) {
                dislocatedResidenceType = personUl.getDislocatedResidenceType().getLocalizedName();
            }
            if (personUl.getProfessionTimeType() != null) {
                professionTimeType = personUl.getProfessionTimeType().getLocalizedName();
            }

            firstOptionDegree = personUl.getFirstOptionDegreeDesignation();
            if (personUl.getFirstOptionInstitution() != null) {
                firstOptionInstitution = personUl.getFirstOptionInstitution().getName();
            }

            if (personUl.getHouseholdSalarySpan() != null) {
                salarySpan = personUl.getHouseholdSalarySpan().getLocalizedName();
            }

            if (personUl.getHasDisabilities()) {
                if (personUl.getDisabilityType().isOther()) {
                    disabilityType = personUl.getOtherDisabilityType();
                } else {
                    disabilityType = personUl.getDisabilityType().getLocalizedName();
                }

                needsDisabilitySupport =
                        BundleUtil.getString("resources/FenixeduUlisboaSpecificationsResources", "label."
                                + personUl.getNeedsDisabilitySupport().toString());
            }

            for (UniversityDiscoveryMeansAnswer universityDiscovery : personUl.getUniversityDiscoveryMeansAnswersSet()) {
                universityDiscoveryString += universityDiscovery.getDescription().getContent() + "; ";
            }
            if (personUl.getOtherUniversityDiscoveryMeans() != null) {
                universityDiscoveryString += personUl.getOtherUniversityDiscoveryMeans();
            }

            for (UniversityChoiceMotivationAnswer universityChoice : personUl.getUniversityChoiceMotivationAnswersSet()) {
                universityChoiceString += universityChoice.getDescription().getContent() + "; ";
            }
            if (personUl.getOtherUniversityChoiceMotivation() != null) {
                universityChoiceString += personUl.getOtherUniversityChoiceMotivation();
            }
        }

        return new RegistrationDGESStateBean(studentCandidacy.getExternalId(), degreeCode, documentIdNumber, candidacyState,
                name, registrationStatus, nationality, secondNationality, ingressionType, placingOption, firstOptionDegree,
                firstOptionInstitution, dislocatedResidenceType, profession, professionTimeType, professionalCondition,
                professionType, fatherName, fatherSchoolLevel, fatherProfessionalCondition, fatherProfessionType, motherName,
                motherSchoolLevel, motherProfessionalCondition, motherProfessionType, salarySpan, disabilityType,
                needsDisabilitySupport, universityDiscoveryString, universityChoiceString);
    }

    public static class RegistrationDGESStateBean {
        private String candidacyId;
        private String degreeCode;
        private String idNumber;
        private String candidacyState;
        private String name;
        private String registrationState;
        private String nationality;
        private String secondNationality;
        private String ingressionType;
        private Integer placingOption;
        private String firstOptionDegree;
        private String firstOptionInstitution;
        private String dislocatedResidenceType;
        private String profession;
        private String professionTimeType;
        private String professionalCondition;
        private String professionType;
        private String fatherName;
        private String fatherSchoolLevel;
        private String fatherProfessionalCondition;
        private String fatherProfessionType;
        private String motherName;
        private String motherSchoolLevel;
        private String motherProfessionalCondition;
        private String motherProfessionType;
        private String salarySpan;
        private String disabilityType;
        private String needsDisabilitySupport;
        private String universityDiscoveryString;
        private String universityChoiceString;

        public RegistrationDGESStateBean(String candidacyId, String degreeCode, String idNumber, String candidacyState,
                String name, String registrationState, String nationality, String secondNationality, String ingressionType,
                Integer placingOption, String firstOptionDegree, String firstOptionInstitution, String dislocatedResidenceType,
                String profession, String professionTimeType, String professionalCondition, String professionType,
                String fatherName, String fatherSchoolLevel, String fatherProfessionalCondition, String fatherProfessionType,
                String motherName, String motherSchoolLevel, String motherProfessionalCondition, String motherProfessionType,
                String salarySpan, String disabilityType, String needsDisabilitySupport, String universityDiscoveryString,
                String universityChoiceString) {
            super();
            this.candidacyId = candidacyId;
            this.degreeCode = degreeCode;
            this.idNumber = idNumber;
            this.candidacyState = candidacyState;
            this.name = name;
            this.registrationState = registrationState;
            this.nationality = nationality;
            this.secondNationality = secondNationality;
            this.ingressionType = ingressionType;
            this.placingOption = placingOption;
            this.firstOptionDegree = firstOptionDegree;
            this.firstOptionInstitution = firstOptionInstitution;
            this.dislocatedResidenceType = dislocatedResidenceType;
            this.profession = profession;
            this.professionTimeType = professionTimeType;
            this.professionalCondition = professionalCondition;
            this.professionType = professionType;
            this.fatherName = fatherName;
            this.fatherSchoolLevel = fatherSchoolLevel;
            this.fatherProfessionalCondition = fatherProfessionalCondition;
            this.fatherProfessionType = fatherProfessionType;
            this.motherName = motherName;
            this.motherSchoolLevel = motherSchoolLevel;
            this.motherProfessionalCondition = motherProfessionalCondition;
            this.motherProfessionType = motherProfessionType;
            this.salarySpan = salarySpan;
            this.disabilityType = disabilityType;
            this.needsDisabilitySupport = needsDisabilitySupport;
            this.universityDiscoveryString = universityDiscoveryString;
            this.universityChoiceString = universityChoiceString;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRegistrationState() {
            return registrationState;
        }

        public void setRegistrationState(String registrationState) {
            this.registrationState = registrationState;
        }

        public String getDegreeCode() {
            return degreeCode;
        }

        public void setDegreeCode(String degreeCode) {
            this.degreeCode = degreeCode;
        }

        public String getCandidacyState() {
            return candidacyState;
        }

        public void setCandidacyState(String candidacyState) {
            this.candidacyState = candidacyState;
        }

        public String getCandidacyId() {
            return candidacyId;
        }

        public void setCandidacyId(String candidacyId) {
            this.candidacyId = candidacyId;
        }

        public Integer getPlacingOption() {
            return placingOption;
        }

        public void setPlacingOption(Integer placingOption) {
            this.placingOption = placingOption;
        }

        public String getFirstOptionDegree() {
            return firstOptionDegree;
        }

        public void setFirstOptionDegree(String firstOptionDegree) {
            this.firstOptionDegree = firstOptionDegree;
        }

        public String getFirstOptionInstitution() {
            return firstOptionInstitution;
        }

        public void setFirstOptionInstitution(String firstOptionInstitution) {
            this.firstOptionInstitution = firstOptionInstitution;
        }

        public String getIngressionType() {
            return ingressionType;
        }

        public void setIngressionType(String ingressionType) {
            this.ingressionType = ingressionType;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getSecondNationality() {
            return secondNationality;
        }

        public void setSecondNationality(String secondNationality) {
            this.secondNationality = secondNationality;
        }

        public String getDislocatedResidenceType() {
            return dislocatedResidenceType;
        }

        public void setDislocatedResidenceType(String dislocatedResidenceType) {
            this.dislocatedResidenceType = dislocatedResidenceType;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }

        public String getProfessionTimeType() {
            return professionTimeType;
        }

        public void setProfessionTimeType(String professionTimeType) {
            this.professionTimeType = professionTimeType;
        }

        public String getProfessionalCondition() {
            return professionalCondition;
        }

        public void setProfessionalCondition(String professionalCondition) {
            this.professionalCondition = professionalCondition;
        }

        public String getProfessionType() {
            return professionType;
        }

        public void setProfessionType(String professionType) {
            this.professionType = professionType;
        }

        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public String getFatherSchoolLevel() {
            return fatherSchoolLevel;
        }

        public void setFatherSchoolLevel(String fatherSchoolLevel) {
            this.fatherSchoolLevel = fatherSchoolLevel;
        }

        public String getFatherProfessionalCondition() {
            return fatherProfessionalCondition;
        }

        public void setFatherProfessionalCondition(String fatherProfessionalCondition) {
            this.fatherProfessionalCondition = fatherProfessionalCondition;
        }

        public String getFatherProfessionType() {
            return fatherProfessionType;
        }

        public void setFatherProfessionType(String fatherProfessionType) {
            this.fatherProfessionType = fatherProfessionType;
        }

        public String getMotherName() {
            return motherName;
        }

        public void setMotherName(String motherName) {
            this.motherName = motherName;
        }

        public String getMotherSchoolLevel() {
            return motherSchoolLevel;
        }

        public void setMotherSchoolLevel(String motherSchoolLevel) {
            this.motherSchoolLevel = motherSchoolLevel;
        }

        public String getMotherProfessionalCondition() {
            return motherProfessionalCondition;
        }

        public void setMotherProfessionalCondition(String motherProfessionalCondition) {
            this.motherProfessionalCondition = motherProfessionalCondition;
        }

        public String getMotherProfessionType() {
            return motherProfessionType;
        }

        public void setMotherProfessionType(String motherProfessionType) {
            this.motherProfessionType = motherProfessionType;
        }

        public String getSalarySpan() {
            return salarySpan;
        }

        public void setSalarySpan(String salarySpan) {
            this.salarySpan = salarySpan;
        }

        public String getDisabilityType() {
            return disabilityType;
        }

        public void setDisabilityType(String disabilityType) {
            this.disabilityType = disabilityType;
        }

        public String getNeedsDisabilitySupport() {
            return needsDisabilitySupport;
        }

        public void setNeedsDisabilitySupport(String needsDisabilitySupport) {
            this.needsDisabilitySupport = needsDisabilitySupport;
        }

        public String getUniversityDiscoveryString() {
            return universityDiscoveryString;
        }

        public void setUniversityDiscoveryString(String universityDiscoveryString) {
            this.universityDiscoveryString = universityDiscoveryString;
        }

        public String getUniversityChoiceString() {
            return universityChoiceString;
        }

        public void setUniversityChoiceString(String universityChoiceString) {
            this.universityChoiceString = universityChoiceString;
        }
    }
}
