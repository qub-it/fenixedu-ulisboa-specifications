package org.fenixedu.ulisboa.specifications.dto.bluerecord.dges;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.BranchType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.MobilityRegistatrionUlisboaInformation;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecificationsByExecutionYear;
import org.fenixedu.ulisboa.specifications.domain.UniversityChoiceMotivationAnswer;
import org.fenixedu.ulisboa.specifications.domain.UniversityDiscoveryMeansAnswer;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;

public class RegistrationDGESStateBean {

    public static Comparator<PartyContact> CONTACT_COMPARATOR_BY_MODIFIED_DATE = (contact, otherContact) -> {
        int result = contact.getLastModifiedDate().compareTo(otherContact.getLastModifiedDate());
        return result == 0 ? DomainObjectUtil.COMPARATOR_BY_ID.compare(contact, otherContact) : result;
    };

    private String executionYear;
    private String candidacyId;
    private String candidacyGrade;
    private String degreeTypeName;
    private String degreeCode;
    private String degreeName;
    private String cycleName;
    private String curricularYear;
    private String degreeLevel;
    private String degreeBranch;
    private String regimeType;
    private String institutionName;
    private String studentNumber;
    private String idNumber;
    private String idType;
    private String fiscalCountry;
    private String socialSecurityNumber;
    private String expirationDateOfIdDoc;
    private String emissionLocationOfIdDoc;
    private String candidacyState;
    private String name;
    private String maritalStatus;
    private String registrationState;
    private String numberOfUCsEnrolled;
    private String nationality;
    private String secondNationality;
    private String birthYear;
    private String countryOfBirth;
    private String districtOfBirth;
    private String districtSubdivisionOfBirth;
    private String parishOfBirth;
    private String gender;
    private String ingressionType;
    private Integer placingOption;
    private String firstOptionDegree;
    private String firstOptionInstitution;
    private String countryOfResidence;
    private String districtOfResidence;
    private String districtSubdivisionOfResidence;
    private String parishOfResidence;
    private String addressOfResidence;
    private String areaCodeOfResidence;
    private String isDislocated;
    private String dislocatedResidenceType;
    private String countryOfDislocated;
    private String districtOfDislocated;
    private String districtSubdivisionOfDislocated;
    private String parishOfDislocated;
    private String addressOfDislocated;
    private String areaCodeOfDislocated;
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
    private String precedentCountry;
    private String precedentDistrict;
    private String precedentDistrictSubdivision;
    private String precedentSchoolLevel;
    private String precedentInstitution;
    private String precedentDegreeDesignation;
    private String precedentConclusionGrade;
    private String precedentConclusionYear;
    private String precedentHighSchoolType;
    private String precendentDegreeCycle;
    private String institutionalEmail;
    private String defaultEmail;
    private String phone;
    private String telephone;
    private String vaccinationValidity;
    private String grantOwnerType;
    private String grantOwnerProvider;
    private String flunkedPreHighSchool;
    private String flunkedPreHighSchoolTimes;
    private String flunkedHighSchool;
    private String flunkedHighSchoolTimes;
    private String socialBenefitsInHighSchool;
    private String socialBenefitsInHighSchoolDescription;
    private String firstTimeInPublicUniv;
    private String firstTimeInUlisboa;
    private String publicUnivCandidacies;
    private String bestQualitiesInThisCicle;
    private String remuneratedActivityInPast;
    private String remuneratedActivityInPastDescription;
    private String flunkedUniversity;
    private String flunkedUniversityTimes;
    private String livesAlone;
    private String livesWithMother;
    private String livesWithFather;
    private String livesWithStepFather;
    private String livesWithStepMother;
    private String livesWithBrothers;
    private String livesWithChildren;
    private String livesWithLifemate;
    private String livesWithOthers;
    private String livesWithOthersDesc;
    private String numBrothers;
    private String numChildren;
    private String mobilityInformationBegin;
    private String mobilityInformationBeginDate;
    private String mobilityInformationEnd;
    private String mobilityInformationEndDate;
    private String mobilityInformationProgramType;
    private String mobilityInformationActivityType;
    private String mobilityInformationScientificArea;
    private String mobilityInformationProgramDuration;
    private String mobilityInformationOriginProgrammeLevel;
    private String mobilityInformationIncomingProgrammeLevel;
    private String mobilityInformationOtherIncomingProgrammeLevel;
    private String mobilityInformationOtherOriginProgrammeLevel;
    private String mobilityInformationOriginCountry;
    private String mobilityInformationIncomingCountry;

    public RegistrationDGESStateBean(final String executionYear, final String candidacyId, final String candidacyGrade,
            final String degreeTypeName, final String degreeCode, final String degreeName, final String cycleName,
            final String curricularYear, final String degreeLevel, final String degreeBranch, final String regimeType,
            final String institutionName, final String studentNumber, final String idNumber, final String idType,
            final String fiscalCountry, final String socialSecurityNumber, final String expirationDateOfIdDoc,
            final String emissionLocationOfIdDoc, final String candidacyState, final String name, final String maritalStatus,
            final String registrationState, final String numberOfUCsEnrolled, final String nationality,
            final String secondNationality, final String birthYear, final String countryOfBirth, final String districtOfBirth,
            final String districtSubdivisionOfBirth, final String parishOfBirth, final String gender, final String ingressionType,
            final Integer placingOption, final String firstOptionDegree, final String firstOptionInstitution,
            final String isDislocated, final String dislocatedResidenceType, final String countryOfResidence,
            final String districtOfResidence, final String districtSubdivisionOfResidence, final String parishOfResidence,
            final String addressOfResidence, final String areaCodeOfResidence, final String countryOfDislocated,
            final String districtOfDislocated, final String districtSubdivisionOfDislocated, final String parishOfDislocated,
            final String addressOfDislocated, final String areaCodeOfDislocated, final String profession,
            final String professionTimeType, final String professionalCondition, final String professionType,
            final String fatherName, final String fatherSchoolLevel, final String fatherProfessionalCondition,
            final String fatherProfessionType, final String motherName, final String motherSchoolLevel,
            final String motherProfessionalCondition, final String motherProfessionType, final String salarySpan,
            final String disabilityType, final String needsDisabilitySupport, final String universityDiscoveryString,
            final String universityChoiceString, final String precedentCountry, final String precedentDistrict,
            final String precedentDistrictSubdivision, final String precedentSchoolLevel, final String precedentInstitution,
            final String precedentDegreeDesignation, final String precedentConclusionGrade, final String precedentConclusionYear,
            final String precedentHighSchoolType, final String precendentDegreeCycle, final String institutionalEmail,
            final String defaultEmail, final String phone, final String telephone, final String vaccinationValidity,
            final String grantOwnerType, final String grantOwnerProvider, final String flunkedPreHighSchool,
            final String flunkedPreHighSchoolTimes, final String flunkedHighSchool, final String flunkedHighSchoolTimes,
            final String socialBenefitsInHighSchool, final String socialBenefitsInHighSchoolDescription,
            final String firstTimeInPublicUniv, final String firstTimeInUlisboa, final String publicUnivCandidacies,
            final String bestQualitiesInThisCicle, final String remuneratedActivityInPast,
            final String remuneratedActivityInPastDescription, final String flunkedUniversity,
            final String flunkedUniversityTimes, final String livesAlone, final String livesWithMother,
            final String livesWithFather, final String livesWithStepFather, final String livesWithStepMother,
            final String livesWithBrothers, final String livesWithChildren, final String livesWithLifemate,
            final String livesWithOthers, final String livesWithOthersDesc, final String numBrothers, final String numChildren,
            final String mobilityInformationBegin, final String mobilityInformationBeginDate, final String mobilityInformationEnd,
            final String mobilityInformationEndDate, final String mobilityInformationProgramType,
            final String mobilityInformationActivityType, final String mobilityInformationScientificArea,
            final String mobilityInformationProgramDuration, final String mobilityInformationOriginProgrammeLevel,
            final String mobilityInformationIncomingProgrammeLevel, final String mobilityInformationOtherIncomingProgrammeLevel,
            final String mobilityInformationOtherOriginProgrammeLevel, final String mobilityInformationOriginCountry,
            final String mobilityInformationIncomingCountry) {
        super();
        this.setExecutionYear(StringUtils.trim(executionYear));
        this.candidacyId = StringUtils.trim(candidacyId);
        this.setDegreeTypeName(StringUtils.trim(degreeTypeName));
        this.degreeCode = StringUtils.trim(degreeCode);
        this.setDegreeName(StringUtils.trim(degreeName));
        this.cycleName = StringUtils.trim(cycleName);
        this.curricularYear = StringUtils.trim(curricularYear);
        this.degreeLevel = StringUtils.trim(degreeLevel);
        this.degreeBranch = StringUtils.trim(degreeBranch);
        this.regimeType = StringUtils.trim(regimeType);
        this.institutionName = StringUtils.trim(institutionName);
        this.studentNumber = StringUtils.trim(studentNumber);
        this.idNumber = StringUtils.trim(idNumber);
        this.idType = StringUtils.trim(idType);
        this.fiscalCountry = StringUtils.trim(fiscalCountry);
        this.socialSecurityNumber = StringUtils.trim(socialSecurityNumber);
        this.expirationDateOfIdDoc = StringUtils.trim(expirationDateOfIdDoc);
        this.emissionLocationOfIdDoc = StringUtils.trim(emissionLocationOfIdDoc);
        this.candidacyState = StringUtils.trim(candidacyState);
        this.name = StringUtils.trim(name);
        this.maritalStatus = StringUtils.trim(maritalStatus);
        this.registrationState = StringUtils.trim(registrationState);
        this.numberOfUCsEnrolled = StringUtils.trim(numberOfUCsEnrolled);
        this.nationality = StringUtils.trim(nationality);
        this.secondNationality = StringUtils.trim(secondNationality);
        this.setBirthYear(StringUtils.trim(birthYear));
        this.setGender(StringUtils.trim(gender));
        this.countryOfBirth = StringUtils.trim(countryOfBirth);
        this.districtOfBirth = StringUtils.trim(districtOfBirth);
        this.districtSubdivisionOfBirth = StringUtils.trim(districtSubdivisionOfBirth);
        this.parishOfBirth = StringUtils.trim(parishOfBirth);
        this.ingressionType = StringUtils.trim(ingressionType);
        this.placingOption = placingOption;
        this.firstOptionDegree = StringUtils.trim(firstOptionDegree);
        this.firstOptionInstitution = StringUtils.trim(firstOptionInstitution);
        this.isDislocated = StringUtils.trim(isDislocated);
        this.dislocatedResidenceType = StringUtils.trim(dislocatedResidenceType);
        this.countryOfResidence = StringUtils.trim(countryOfResidence);
        this.districtOfResidence = StringUtils.trim(districtOfResidence);
        this.districtSubdivisionOfResidence = StringUtils.trim(districtSubdivisionOfResidence);
        this.parishOfResidence = StringUtils.trim(parishOfResidence);
        this.addressOfResidence = StringUtils.trim(addressOfResidence);
        this.areaCodeOfResidence = StringUtils.trim(areaCodeOfResidence);
        this.countryOfDislocated = StringUtils.trim(countryOfDislocated);
        this.districtOfDislocated = StringUtils.trim(districtOfDislocated);
        this.districtSubdivisionOfDislocated = StringUtils.trim(districtSubdivisionOfDislocated);
        this.parishOfDislocated = StringUtils.trim(parishOfDislocated);
        this.addressOfDislocated = StringUtils.trim(addressOfDislocated);
        this.areaCodeOfDislocated = StringUtils.trim(areaCodeOfDislocated);
        this.profession = StringUtils.trim(profession);
        this.professionTimeType = StringUtils.trim(professionTimeType);
        this.professionalCondition = StringUtils.trim(professionalCondition);
        this.professionType = StringUtils.trim(professionType);
        this.fatherName = StringUtils.trim(fatherName);
        this.fatherSchoolLevel = StringUtils.trim(fatherSchoolLevel);
        this.fatherProfessionalCondition = StringUtils.trim(fatherProfessionalCondition);
        this.fatherProfessionType = StringUtils.trim(fatherProfessionType);
        this.motherName = StringUtils.trim(motherName);
        this.motherSchoolLevel = StringUtils.trim(motherSchoolLevel);
        this.motherProfessionalCondition = StringUtils.trim(motherProfessionalCondition);
        this.motherProfessionType = StringUtils.trim(motherProfessionType);
        this.salarySpan = StringUtils.trim(salarySpan);
        this.disabilityType = StringUtils.trim(disabilityType);
        this.needsDisabilitySupport = StringUtils.trim(needsDisabilitySupport);
        this.universityDiscoveryString = StringUtils.trim(universityDiscoveryString);
        this.universityChoiceString = StringUtils.trim(universityChoiceString);
        this.precedentCountry = StringUtils.trim(precedentCountry);
        this.precedentDistrict = StringUtils.trim(precedentDistrict);
        this.precedentDistrictSubdivision = StringUtils.trim(precedentDistrictSubdivision);
        this.precedentSchoolLevel = StringUtils.trim(precedentSchoolLevel);
        this.precedentInstitution = StringUtils.trim(precedentInstitution);
        this.precedentDegreeDesignation = StringUtils.trim(precedentDegreeDesignation);
        this.precedentConclusionGrade = StringUtils.trim(precedentConclusionGrade);
        this.precedentConclusionYear = StringUtils.trim(precedentConclusionYear);
        this.precedentHighSchoolType = StringUtils.trim(precedentHighSchoolType);
        this.precendentDegreeCycle = StringUtils.trim(precendentDegreeCycle);
        this.institutionalEmail = StringUtils.trim(institutionalEmail);
        this.defaultEmail = StringUtils.trim(defaultEmail);
        this.phone = StringUtils.trim(phone);
        this.telephone = StringUtils.trim(telephone);
        this.vaccinationValidity = StringUtils.trim(vaccinationValidity);
        this.grantOwnerType = StringUtils.trim(grantOwnerType);
        this.grantOwnerProvider = StringUtils.trim(grantOwnerProvider);
        this.flunkedPreHighSchool = StringUtils.trim(flunkedPreHighSchool);
        this.flunkedPreHighSchoolTimes = StringUtils.trim(flunkedPreHighSchoolTimes);
        this.flunkedHighSchool = StringUtils.trim(flunkedHighSchool);
        this.flunkedHighSchoolTimes = StringUtils.trim(flunkedHighSchoolTimes);
        this.socialBenefitsInHighSchool = StringUtils.trim(socialBenefitsInHighSchool);
        this.socialBenefitsInHighSchoolDescription = StringUtils.trim(socialBenefitsInHighSchoolDescription);
        this.firstTimeInPublicUniv = StringUtils.trim(firstTimeInPublicUniv);
        this.firstTimeInUlisboa = StringUtils.trim(firstTimeInUlisboa);
        this.publicUnivCandidacies = StringUtils.trim(publicUnivCandidacies);
        this.bestQualitiesInThisCicle = StringUtils.trim(bestQualitiesInThisCicle);
        this.remuneratedActivityInPast = StringUtils.trim(remuneratedActivityInPast);
        this.remuneratedActivityInPastDescription = StringUtils.trim(remuneratedActivityInPastDescription);
        this.flunkedUniversity = StringUtils.trim(flunkedUniversity);
        this.flunkedUniversityTimes = StringUtils.trim(flunkedUniversityTimes);
        this.livesAlone = StringUtils.trim(livesAlone);
        this.livesWithMother = StringUtils.trim(livesWithMother);
        this.livesWithFather = StringUtils.trim(livesWithFather);
        this.livesWithStepFather = StringUtils.trim(livesWithStepFather);
        this.livesWithStepMother = StringUtils.trim(livesWithStepMother);
        this.livesWithBrothers = StringUtils.trim(livesWithBrothers);
        this.livesWithChildren = StringUtils.trim(livesWithChildren);
        this.livesWithLifemate = StringUtils.trim(livesWithLifemate);
        this.livesWithOthers = StringUtils.trim(livesWithOthers);
        this.livesWithOthersDesc = StringUtils.trim(livesWithOthersDesc);
        this.numBrothers = StringUtils.trim(numBrothers);
        this.numChildren = StringUtils.trim(numChildren);
        this.mobilityInformationBegin = StringUtils.trim(mobilityInformationBegin);
        this.mobilityInformationBeginDate = StringUtils.trim(mobilityInformationBeginDate);
        this.mobilityInformationEnd = StringUtils.trim(mobilityInformationEnd);
        this.mobilityInformationEndDate = StringUtils.trim(mobilityInformationEndDate);
        this.mobilityInformationProgramType = StringUtils.trim(mobilityInformationProgramType);
        this.mobilityInformationActivityType = StringUtils.trim(mobilityInformationActivityType);
        this.mobilityInformationScientificArea = StringUtils.trim(mobilityInformationScientificArea);
        this.mobilityInformationProgramDuration = StringUtils.trim(mobilityInformationProgramDuration);
        this.mobilityInformationOriginProgrammeLevel = StringUtils.trim(mobilityInformationOriginProgrammeLevel);
        this.mobilityInformationIncomingProgrammeLevel = StringUtils.trim(mobilityInformationIncomingProgrammeLevel);
        this.mobilityInformationOtherIncomingProgrammeLevel = StringUtils.trim(mobilityInformationOtherIncomingProgrammeLevel);
        this.mobilityInformationOtherOriginProgrammeLevel = StringUtils.trim(mobilityInformationOtherOriginProgrammeLevel);
        this.mobilityInformationOriginCountry = StringUtils.trim(mobilityInformationOriginCountry);
        this.mobilityInformationIncomingCountry = StringUtils.trim(mobilityInformationIncomingCountry);
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(final String idNumber) {
        this.idNumber = idNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(final String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getCandidacyGrade() {
        return candidacyGrade;
    }

    public void setCandidacyGrade(final String candidacyGrade) {
        this.candidacyGrade = candidacyGrade;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRegistrationState() {
        return registrationState;
    }

    public void setRegistrationState(final String registrationState) {
        this.registrationState = registrationState;
    }

    public String getNumberOfUCsEnrolled() {
        return numberOfUCsEnrolled;
    }

    public void setNumberOfUCsEnrolled(String numberOfUCsEnrolled) {
        this.numberOfUCsEnrolled = numberOfUCsEnrolled;
    }

    public String getDegreeCode() {
        return degreeCode;
    }

    public void setDegreeCode(final String degreeCode) {
        this.degreeCode = degreeCode;
    }

    public String getCandidacyState() {
        return candidacyState;
    }

    public void setCandidacyState(final String candidacyState) {
        this.candidacyState = candidacyState;
    }

    public String getCandidacyId() {
        return candidacyId;
    }

    public void setCandidacyId(final String candidacyId) {
        this.candidacyId = candidacyId;
    }

    public Integer getPlacingOption() {
        return placingOption;
    }

    public void setPlacingOption(final Integer placingOption) {
        this.placingOption = placingOption;
    }

    public String getFirstOptionDegree() {
        return firstOptionDegree;
    }

    public void setFirstOptionDegree(final String firstOptionDegree) {
        this.firstOptionDegree = firstOptionDegree;
    }

    public String getFirstOptionInstitution() {
        return firstOptionInstitution;
    }

    public void setFirstOptionInstitution(final String firstOptionInstitution) {
        this.firstOptionInstitution = firstOptionInstitution;
    }

    public String getIngressionType() {
        return ingressionType;
    }

    public void setIngressionType(final String ingressionType) {
        this.ingressionType = ingressionType;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(final String nationality) {
        this.nationality = nationality;
    }

    public String getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(final String secondNationality) {
        this.secondNationality = secondNationality;
    }

    public String getDislocatedResidenceType() {
        return dislocatedResidenceType;
    }

    public void setDislocatedResidenceType(final String dislocatedResidenceType) {
        this.dislocatedResidenceType = dislocatedResidenceType;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(final String profession) {
        this.profession = profession;
    }

    public String getProfessionTimeType() {
        return professionTimeType;
    }

    public void setProfessionTimeType(final String professionTimeType) {
        this.professionTimeType = professionTimeType;
    }

    public String getProfessionalCondition() {
        return professionalCondition;
    }

    public void setProfessionalCondition(final String professionalCondition) {
        this.professionalCondition = professionalCondition;
    }

    public String getProfessionType() {
        return professionType;
    }

    public void setProfessionType(final String professionType) {
        this.professionType = professionType;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(final String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherSchoolLevel() {
        return fatherSchoolLevel;
    }

    public void setFatherSchoolLevel(final String fatherSchoolLevel) {
        this.fatherSchoolLevel = fatherSchoolLevel;
    }

    public String getFatherProfessionalCondition() {
        return fatherProfessionalCondition;
    }

    public void setFatherProfessionalCondition(final String fatherProfessionalCondition) {
        this.fatherProfessionalCondition = fatherProfessionalCondition;
    }

    public String getFatherProfessionType() {
        return fatherProfessionType;
    }

    public void setFatherProfessionType(final String fatherProfessionType) {
        this.fatherProfessionType = fatherProfessionType;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(final String motherName) {
        this.motherName = motherName;
    }

    public String getMotherSchoolLevel() {
        return motherSchoolLevel;
    }

    public void setMotherSchoolLevel(final String motherSchoolLevel) {
        this.motherSchoolLevel = motherSchoolLevel;
    }

    public String getMotherProfessionalCondition() {
        return motherProfessionalCondition;
    }

    public void setMotherProfessionalCondition(final String motherProfessionalCondition) {
        this.motherProfessionalCondition = motherProfessionalCondition;
    }

    public String getMotherProfessionType() {
        return motherProfessionType;
    }

    public void setMotherProfessionType(final String motherProfessionType) {
        this.motherProfessionType = motherProfessionType;
    }

    public String getSalarySpan() {
        return salarySpan;
    }

    public void setSalarySpan(final String salarySpan) {
        this.salarySpan = salarySpan;
    }

    public String getDisabilityType() {
        return disabilityType;
    }

    public void setDisabilityType(final String disabilityType) {
        this.disabilityType = disabilityType;
    }

    public String getNeedsDisabilitySupport() {
        return needsDisabilitySupport;
    }

    public void setNeedsDisabilitySupport(final String needsDisabilitySupport) {
        this.needsDisabilitySupport = needsDisabilitySupport;
    }

    public String getUniversityDiscoveryString() {
        return universityDiscoveryString;
    }

    public void setUniversityDiscoveryString(final String universityDiscoveryString) {
        this.universityDiscoveryString = universityDiscoveryString;
    }

    public String getUniversityChoiceString() {
        return universityChoiceString;
    }

    public void setUniversityChoiceString(final String universityChoiceString) {
        this.universityChoiceString = universityChoiceString;
    }

    public String getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(final String executionYear) {
        this.executionYear = executionYear;
    }

    public String getDegreeTypeName() {
        return degreeTypeName;
    }

    public void setDegreeTypeName(final String degreeTypeName) {
        this.degreeTypeName = degreeTypeName;
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(final String degreeName) {
        this.degreeName = degreeName;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(final String birthYear) {
        this.birthYear = birthYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(final String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public String getDistrictOfBirth() {
        return districtOfBirth;
    }

    public void setDistrictOfBirth(final String districtOfBirth) {
        this.districtOfBirth = districtOfBirth;
    }

    public String getDistrictSubdivisionOfBirth() {
        return districtSubdivisionOfBirth;
    }

    public void setDistrictSubdivisionOfBirth(final String districtSubdivisionOfBirth) {
        this.districtSubdivisionOfBirth = districtSubdivisionOfBirth;
    }

    public String getParishOfBirth() {
        return parishOfBirth;
    }

    public void setParishOfBirth(final String parishOfBirth) {
        this.parishOfBirth = parishOfBirth;
    }

    public String getIsDislocated() {
        return isDislocated;
    }

    public void setIsDislocated(final String isDislocated) {
        this.isDislocated = isDislocated;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(final String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public String getDistrictOfResidence() {
        return districtOfResidence;
    }

    public void setDistrictOfResidence(final String districtOfResidence) {
        this.districtOfResidence = districtOfResidence;
    }

    public String getDistrictSubdivisionOfResidence() {
        return districtSubdivisionOfResidence;
    }

    public void setDistrictSubdivisionOfResidence(final String districtSubdivisionOfResidence) {
        this.districtSubdivisionOfResidence = districtSubdivisionOfResidence;
    }

    public String getParishOfResidence() {
        return parishOfResidence;
    }

    public void setParishOfResidence(final String parishOfResidence) {
        this.parishOfResidence = parishOfResidence;
    }

    public String getPrecedentCountry() {
        return precedentCountry;
    }

    public void setPrecedentCountry(final String precedentCountry) {
        this.precedentCountry = precedentCountry;
    }

    public String getPrecedentDistrict() {
        return precedentDistrict;
    }

    public void setPrecedentDistrict(final String precedentDistrict) {
        this.precedentDistrict = precedentDistrict;
    }

    public String getPrecedentDistrictSubdivision() {
        return precedentDistrictSubdivision;
    }

    public void setPrecedentDistrictSubdivision(final String precedentDistrictSubdivision) {
        this.precedentDistrictSubdivision = precedentDistrictSubdivision;
    }

    public String getPrecedentSchoolLevel() {
        return precedentSchoolLevel;
    }

    public void setPrecedentSchoolLevel(final String precedentSchoolLevel) {
        this.precedentSchoolLevel = precedentSchoolLevel;
    }

    public String getPrecedentInstitution() {
        return precedentInstitution;
    }

    public void setPrecedentInstitution(final String precedentInstitution) {
        this.precedentInstitution = precedentInstitution;
    }

    public String getPrecedentDegreeDesignation() {
        return precedentDegreeDesignation;
    }

    public void setPrecedentDegreeDesignation(final String precedentDegreeDesignation) {
        this.precedentDegreeDesignation = precedentDegreeDesignation;
    }

    public String getPrecedentConclusionGrade() {
        return precedentConclusionGrade;
    }

    public void setPrecedentConclusionGrade(final String precedentConclusionGrade) {
        this.precedentConclusionGrade = precedentConclusionGrade;
    }

    public String getPrecedentConclusionYear() {
        return precedentConclusionYear;
    }

    public void setPrecedentConclusionYear(final String precedentConclusionYear) {
        this.precedentConclusionYear = precedentConclusionYear;
    }

    public String getPrecedentHighSchoolType() {
        return precedentHighSchoolType;
    }

    public void setPrecedentHighSchoolType(final String precedentHighSchoolType) {
        this.precedentHighSchoolType = precedentHighSchoolType;
    }

    public String getAddressOfResidence() {
        return addressOfResidence;
    }

    public void setAddressOfResidence(final String addressOfResidence) {
        this.addressOfResidence = addressOfResidence;
    }

    public String getAreaCodeOfResidence() {
        return areaCodeOfResidence;
    }

    public void setAreaCodeOfResidence(final String areaCodeOfResidence) {
        this.areaCodeOfResidence = areaCodeOfResidence;
    }

    public String getCountryOfDislocated() {
        return countryOfDislocated;
    }

    public void setCountryOfDislocated(final String countryOfDislocated) {
        this.countryOfDislocated = countryOfDislocated;
    }

    public String getDistrictOfDislocated() {
        return districtOfDislocated;
    }

    public void setDistrictOfDislocated(final String districtOfDislocated) {
        this.districtOfDislocated = districtOfDislocated;
    }

    public String getDistrictSubdivisionOfDislocated() {
        return districtSubdivisionOfDislocated;
    }

    public void setDistrictSubdivisionOfDislocated(final String districtSubdivisionOfDislocated) {
        this.districtSubdivisionOfDislocated = districtSubdivisionOfDislocated;
    }

    public String getParishOfDislocated() {
        return parishOfDislocated;
    }

    public void setParishOfDislocated(final String parishOfDislocated) {
        this.parishOfDislocated = parishOfDislocated;
    }

    public String getAddressOfDislocated() {
        return addressOfDislocated;
    }

    public void setAddressOfDislocated(final String addressOfDislocated) {
        this.addressOfDislocated = addressOfDislocated;
    }

    public String getAreaCodeOfDislocated() {
        return areaCodeOfDislocated;
    }

    public void setAreaCodeOfDislocated(final String areaCodeOfDislocated) {
        this.areaCodeOfDislocated = areaCodeOfDislocated;
    }

    public String getExpirationDateOfIdDoc() {
        return expirationDateOfIdDoc;
    }

    public void setExpirationDateOfIdDoc(final String expirationDateOfIdDoc) {
        this.expirationDateOfIdDoc = expirationDateOfIdDoc;
    }

    public String getEmissionLocationOfIdDoc() {
        return emissionLocationOfIdDoc;
    }

    public void setEmissionLocationOfIdDoc(final String emissionLocationOfIdDoc) {
        this.emissionLocationOfIdDoc = emissionLocationOfIdDoc;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(final String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(final String institutionalEmail) {
        this.institutionalEmail = institutionalEmail;
    }

    public String getDefaultEmail() {
        return defaultEmail;
    }

    public void setDefaultEmail(final String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(final String telephone) {
        this.telephone = telephone;
    }

    public String getVaccinationValidity() {
        return vaccinationValidity;
    }

    public void setVaccinationValidity(final String vaccinationValidity) {
        this.vaccinationValidity = vaccinationValidity;
    }

    public String getGrantOwnerType() {
        return grantOwnerType;
    }

    public void setGrantOwnerType(final String grantOwnerType) {
        this.grantOwnerType = grantOwnerType;
    }

    public String getGrantOwnerProvider() {
        return grantOwnerProvider;
    }

    public void setGrantOwnerProvider(final String grantOwnerProvider) {
        this.grantOwnerProvider = grantOwnerProvider;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(final String cycleName) {
        this.cycleName = cycleName;
    }

    public String getCurricularYear() {
        return curricularYear;
    }

    public void setCurricularYear(final String curricularYear) {
        this.curricularYear = curricularYear;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(final String institutionName) {
        this.institutionName = institutionName;
    }

    public String getPrecendentDegreeCycle() {
        return precendentDegreeCycle;
    }

    public void setPrecendentDegreeCycle(final String precendentDegreeCycle) {
        this.precendentDegreeCycle = precendentDegreeCycle;
    }

    public String getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(final String degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public String getDegreeBranch() {
        return degreeBranch;
    }

    public void setDegreeBranch(final String degreeBranch) {
        this.degreeBranch = degreeBranch;
    }

    public String getRegimeType() {
        return regimeType;
    }

    public void setRegimeType(final String regimeType) {
        this.regimeType = regimeType;
    }

    public String getFlunkedPreHighSchool() {
        return flunkedPreHighSchool;
    }

    public void setFlunkedPreHighSchool(final String flunkedPreHighSchool) {
        this.flunkedPreHighSchool = flunkedPreHighSchool;
    }

    public String getFlunkedPreHighSchoolTimes() {
        return flunkedPreHighSchoolTimes;
    }

    public void setFlunkedPreHighSchoolTimes(final String flunkedPreHighSchoolTimes) {
        this.flunkedPreHighSchoolTimes = flunkedPreHighSchoolTimes;
    }

    public String getFlunkedHighSchool() {
        return flunkedHighSchool;
    }

    public void setFlunkedHighSchool(final String flunkedHighSchool) {
        this.flunkedHighSchool = flunkedHighSchool;
    }

    public String getFlunkedHighSchoolTimes() {
        return flunkedHighSchoolTimes;
    }

    public void setFlunkedHighSchoolTimes(final String flunkedHighSchoolTimes) {
        this.flunkedHighSchoolTimes = flunkedHighSchoolTimes;
    }

    public String getSocialBenefitsInHighSchool() {
        return socialBenefitsInHighSchool;
    }

    public void setSocialBenefitsInHighSchool(final String socialBenefitsInHighSchool) {
        this.socialBenefitsInHighSchool = socialBenefitsInHighSchool;
    }

    public String getSocialBenefitsInHighSchoolDescription() {
        return socialBenefitsInHighSchoolDescription;
    }

    public void setSocialBenefitsInHighSchoolDescription(final String socialBenefitsInHighSchoolDescription) {
        this.socialBenefitsInHighSchoolDescription = socialBenefitsInHighSchoolDescription;
    }

    public String getFirstTimeInPublicUniv() {
        return firstTimeInPublicUniv;
    }

    public void setFirstTimeInPublicUniv(final String firstTimeInPublicUniv) {
        this.firstTimeInPublicUniv = firstTimeInPublicUniv;
    }

    public String getFirstTimeInUlisboa() {
        return firstTimeInUlisboa;
    }

    public void setFirstTimeInUlisboa(final String firstTimeInUlisboa) {
        this.firstTimeInUlisboa = firstTimeInUlisboa;
    }

    public String getPublicUnivCandidacies() {
        return publicUnivCandidacies;
    }

    public void setPublicUnivCandidacies(final String publicUnivCandidacies) {
        this.publicUnivCandidacies = publicUnivCandidacies;
    }

    public String getBestQualitiesInThisCicle() {
        return bestQualitiesInThisCicle;
    }

    public void setBestQualitiesInThisCicle(final String bestQualitiesInThisCicle) {
        this.bestQualitiesInThisCicle = bestQualitiesInThisCicle;
    }

    public String getRemuneratedActivityInPast() {
        return remuneratedActivityInPast;
    }

    public void setRemuneratedActivityInPast(final String remuneratedActivityInPast) {
        this.remuneratedActivityInPast = remuneratedActivityInPast;
    }

    public String getRemuneratedActivityInPastDescription() {
        return remuneratedActivityInPastDescription;
    }

    public void setRemuneratedActivityInPastDescription(final String remuneratedActivityInPastDescription) {
        this.remuneratedActivityInPastDescription = remuneratedActivityInPastDescription;
    }

    public String getFlunkedUniversity() {
        return flunkedUniversity;
    }

    public void setFlunkedUniversity(final String flunkedUniversity) {
        this.flunkedUniversity = flunkedUniversity;
    }

    public String getFlunkedUniversityTimes() {
        return flunkedUniversityTimes;
    }

    public void setFlunkedUniversityTimes(final String flunkedUniversityTimes) {
        this.flunkedUniversityTimes = flunkedUniversityTimes;
    }

    public String getLivesAlone() {
        return livesAlone;
    }

    public void setLivesAlone(final String livesAlone) {
        this.livesAlone = livesAlone;
    }

    public String getLivesWithParents() {
        return livesWithMother;
    }

    public String getLivesWithMother() {
        return livesWithMother;
    }

    public void setLivesWithMother(final String livesWithMother) {
        this.livesWithMother = livesWithMother;
    }

    public String getLivesWithFather() {
        return livesWithFather;
    }

    public void setLivesWithFather(final String livesWithFather) {
        this.livesWithFather = livesWithFather;
    }

    public String getLivesWithStepFather() {
        return livesWithStepFather;
    }

    public void setLivesWithStepFather(final String livesWithStepFather) {
        this.livesWithStepFather = livesWithStepFather;
    }

    public String getLivesWithStepMother() {
        return livesWithStepMother;
    }

    public void setLivesWithStepMother(final String livesWithStepMother) {
        this.livesWithStepMother = livesWithStepMother;
    }

    public String getLivesWithBrothers() {
        return livesWithBrothers;
    }

    public void setLivesWithBrothers(final String livesWithBrothers) {
        this.livesWithBrothers = livesWithBrothers;
    }

    public String getLivesWithChildren() {
        return livesWithChildren;
    }

    public void setLivesWithChildren(final String livesWithChildren) {
        this.livesWithChildren = livesWithChildren;
    }

    public String getLivesWithLifemate() {
        return livesWithLifemate;
    }

    public void setLivesWithLifemate(final String livesWithLifemate) {
        this.livesWithLifemate = livesWithLifemate;
    }

    public String getLivesWithOthers() {
        return livesWithOthers;
    }

    public void setLivesWithOthers(final String livesWithOthers) {
        this.livesWithOthers = livesWithOthers;
    }

    public String getLivesWithOthersDesc() {
        return livesWithOthersDesc;
    }

    public void setLivesWithOthersDesc(final String livesWithOthersDesc) {
        this.livesWithOthersDesc = livesWithOthersDesc;
    }

    public String getNumBrothers() {
        return numBrothers;
    }

    public void setNumBrothers(final String numBrothers) {
        this.numBrothers = numBrothers;
    }

    public String getNumChildren() {
        return numChildren;
    }

    public void setNumChildren(final String numChildren) {
        this.numChildren = numChildren;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getFiscalCountry() {
        return fiscalCountry;
    }

    public void setFiscalCountry(String fiscalCountry) {
        this.fiscalCountry = fiscalCountry;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getMobilityInformationBegin() {
        return mobilityInformationBegin;
    }

    public void setMobilityInformationBegin(String mobilityInformationBegin) {
        this.mobilityInformationBegin = mobilityInformationBegin;
    }

    public String getMobilityInformationBeginDate() {
        return mobilityInformationBeginDate;
    }

    public void setMobilityInformationBeginDate(String mobilityInformationBeginDate) {
        this.mobilityInformationBeginDate = mobilityInformationBeginDate;
    }

    public String getMobilityInformationEnd() {
        return mobilityInformationEnd;
    }

    public void setMobilityInformationEnd(String mobilityInformationEnd) {
        this.mobilityInformationEnd = mobilityInformationEnd;
    }

    public String getMobilityInformationEndDate() {
        return mobilityInformationEndDate;
    }

    public void setMobilityInformationEndDate(String mobilityInformationEndDate) {
        this.mobilityInformationEndDate = mobilityInformationEndDate;
    }

    public String getMobilityInformationProgramType() {
        return mobilityInformationProgramType;
    }

    public void setMobilityInformationProgramType(String mobilityInformationProgramType) {
        this.mobilityInformationProgramType = mobilityInformationProgramType;
    }

    public String getMobilityInformationActivityType() {
        return mobilityInformationActivityType;
    }

    public void setMobilityInformationActivityType(String mobilityInformationActivityType) {
        this.mobilityInformationActivityType = mobilityInformationActivityType;
    }

    public String getMobilityInformationScientificArea() {
        return mobilityInformationScientificArea;
    }

    public void setMobilityInformationScientificArea(String mobilityInformationScientificArea) {
        this.mobilityInformationScientificArea = mobilityInformationScientificArea;
    }

    public String getMobilityInformationProgramDuration() {
        return mobilityInformationProgramDuration;
    }

    public void setMobilityInformationProgramDuration(String mobilityInformationProgramDuration) {
        this.mobilityInformationProgramDuration = mobilityInformationProgramDuration;
    }

    public String getMobilityInformationOriginProgrammeLevel() {
        return mobilityInformationOriginProgrammeLevel;
    }

    public void setMobilityInformationOriginProgrammeLevel(String mobilityInformationOriginProgrammeLevel) {
        this.mobilityInformationOriginProgrammeLevel = mobilityInformationOriginProgrammeLevel;
    }

    public String getMobilityInformationIncomingProgrammeLevel() {
        return mobilityInformationIncomingProgrammeLevel;
    }

    public void setMobilityInformationIncomingProgrammeLevel(String mobilityInformationIncomingProgrammeLevel) {
        this.mobilityInformationIncomingProgrammeLevel = mobilityInformationIncomingProgrammeLevel;
    }

    public String getMobilityInformationOtherIncomingProgrammeLevel() {
        return mobilityInformationOtherIncomingProgrammeLevel;
    }

    public void setMobilityInformationOtherIncomingProgrammeLevel(String mobilityInformationOtherIncomingProgrammeLevel) {
        this.mobilityInformationOtherIncomingProgrammeLevel = mobilityInformationOtherIncomingProgrammeLevel;
    }

    public String getMobilityInformationOtherOriginProgrammeLevel() {
        return mobilityInformationOtherOriginProgrammeLevel;
    }

    public void setMobilityInformationOtherOriginProgrammeLevel(String mobilityInformationOtherOriginProgrammeLevel) {
        this.mobilityInformationOtherOriginProgrammeLevel = mobilityInformationOtherOriginProgrammeLevel;
    }

    public String getMobilityInformationOriginCountry() {
        return mobilityInformationOriginCountry;
    }

    public void setMobilityInformationOriginCountry(String mobilityInformationOriginCountry) {
        this.mobilityInformationOriginCountry = mobilityInformationOriginCountry;
    }

    public String getMobilityInformationIncomingCountry() {
        return mobilityInformationIncomingCountry;
    }

    public void setMobilityInformationIncomingCountry(String mobilityInformationIncomingCountry) {
        this.mobilityInformationIncomingCountry = mobilityInformationIncomingCountry;
    }

    /* TODO: Remove to DTO with RDGESSBean */
    public static RegistrationDGESStateBean populateBean(final StudentCandidacy studentCandidacy) {
        ExecutionYear executionYear = studentCandidacy.getRegistration().getStartExecutionYear();
        String executionYearName = executionYear.getQualifiedName();
        Person person = studentCandidacy.getPerson();
        StudentCurricularPlan studentCurricularPlan = studentCandidacy.getRegistration().getStudentCurricularPlan(executionYear);
        String degreeTypeName = studentCandidacy.getDegreeCurricularPlan().getDegree().getDegreeTypeName();
        String degreeCode = studentCandidacy.getDegreeCurricularPlan().getDegree().getMinistryCode();
        String degreeName = studentCandidacy.getDegreeCurricularPlan().getDegree().getNameI18N().getContent();
        // TODO send to a bundle and to a method in Util
        String degreeLevel = "";
        if (degreeTypeName.contains("Licenciatura")) {
            degreeLevel = "Licenciado";
        } else if (degreeTypeName.contains("Mestrado")) {
            degreeLevel = "Mestre";
        } else if (degreeTypeName.contains("Doutoramento")) {
            degreeLevel = "Doutor";
        } else {
            degreeLevel = "Desconhecido";
        }

        String entryGrade = "";
        if (studentCandidacy.getEntryGrade() != null) {
            entryGrade = studentCandidacy.getEntryGrade().toString();
        }

        String degreeBranch = "";
        if (studentCurricularPlan != null) {
            degreeBranch = getPrimaryBranchName(studentCurricularPlan);
        }

        RegistrationRegimeType regimeTypeObj = studentCandidacy.getRegistration().getRegimeType(executionYear);
        String regimeType = "";
        if (regimeTypeObj != null) {
            regimeType = regimeTypeObj.getLocalizedName();
        }

        String studentNumber = person.getStudent().getNumber().toString();
        String documentIdNumber = person.getDocumentIdNumber();
        String documentIdType = person.getIdDocumentType().getLocalizedName();

        String fiscalCountry = "";
        String socialSecurityNumber = "";

        if (person.getFiscalCountry() != null) {
            fiscalCountry = person.getFiscalCountry().getLocalizedName().getContent();
        }
        if (person.getSocialSecurityNumber() != null) {
            socialSecurityNumber = person.getSocialSecurityNumber();
        }

        String candidacyState = BundleUtil.getString("resources/EnumerationResources",
                studentCandidacy.getActiveCandidacySituationType().getQualifiedName());
        String name = person.getName();
        String registrationStatus = "";
        if (studentCandidacy.getActiveCandidacySituationType().equals(CandidacySituationType.REGISTERED)) {
            registrationStatus = BundleUtil.getString(BUNDLE, "label.true");
        } else {
            registrationStatus = BundleUtil.getString(BUNDLE, "label.false");
        }

        int enrolledUCs = studentCandidacy.getRegistration().getEnrolments(executionYear).size();
        String numberOfUCsEnrolled = "" + enrolledUCs;

        Country nat = person.getCountry();
        String nationality = "";
        if (nat != null) {
            nationality = nat.getCountryNationality().getContent();
        }
        String secondNationality = "";
        String birthYear = "";
        if (person.getDateOfBirthYearMonthDay() != null) {
            birthYear += person.getDateOfBirthYearMonthDay().toString("dd-MM-yyyy");
        }

        Country cOfBirth = person.getCountryOfBirth();
        String countryOfBirth = "";
        String districtOfBirth = "";
        String districtSubdivisionOfBirth = "";
        String parishOfBirth = "";
        if (cOfBirth != null) {
            countryOfBirth = cOfBirth.getLocalizedName().getContent();
            if (cOfBirth.isDefaultCountry()) {
                districtOfBirth = person.getDistrictOfBirth();
                districtSubdivisionOfBirth = person.getDistrictSubdivisionOfBirth();
                parishOfBirth = person.getParishOfBirth();
            }
        }

        String countryOfResidence = "";
        String districtOfResidence = "";
        String districtSubdivisionOfResidence = "";
        String parishOfResidence = "";
        String addressOfResidence = "";
        String areaCodeOfResidence = "";
        if (person.getDefaultPhysicalAddress() != null) {
            countryOfResidence = person.getDefaultPhysicalAddress().getCountryOfResidenceName();
            districtOfResidence = person.getDefaultPhysicalAddress().getDistrictOfResidence();
            districtSubdivisionOfResidence = person.getDefaultPhysicalAddress().getDistrictSubdivisionOfResidence();
            parishOfResidence = person.getDefaultPhysicalAddress().getParishOfResidence();
            addressOfResidence = person.getDefaultPhysicalAddress().getAddress();
            areaCodeOfResidence = person.getDefaultPhysicalAddress().getAreaCode();
        }

        String countryOfDislocated = "";
        String districtOfDislocated = "";
        String districtSubdivisionOfDislocated = "";
        String parishOfDislocated = "";
        String addressOfDislocated = "";
        String areaCodeOfDislocated = "";
        PhysicalAddress dislocatedAddress = getSchoolTimePhysicalAddress(person);
        if (dislocatedAddress != null) {
            countryOfDislocated = dislocatedAddress.getCountryOfResidenceName();
            districtOfDislocated = dislocatedAddress.getDistrictOfResidence();
            districtSubdivisionOfDislocated = dislocatedAddress.getDistrictSubdivisionOfResidence();
            parishOfDislocated = dislocatedAddress.getParishOfResidence();
            addressOfDislocated = dislocatedAddress.getAddress();
            areaCodeOfDislocated = dislocatedAddress.getAreaCode();
        }

        String gender = person.getGender().getLocalizedName();
        String ingressionType = "";
        if (studentCandidacy.getIngressionType() != null) {
            ingressionType = studentCandidacy.getIngressionType().getLocalizedName();
        }
        Integer placingOption = studentCandidacy.getPlacingOption();
        String firstOptionDegree = "";
        String firstOptionInstitution = "";
        String isDislocated = "";
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

        String flunkedPreHighSchool = "";
        String flunkedPreHighSchoolTimes = "";
        String flunkedHighSchool = "";
        String flunkedHighSchoolTimes = "";
        String socialBenefitsInHighSchool = "";
        String socialBenefitsInHighSchoolDescription = "";
        String firstTimeInPublicUniv = "";
        String firstTimeInUlisboa = "";
        String publicUnivCandidacies = "";
        String bestQualitiesInThisCicle = "";
        String remuneratedActivityInPast = "";
        String remuneratedActivityInPastDescription = "";
        String flunkedUniversity = "";
        String flunkedUniversityTimes = "";
        String livesAlone = "";
        String livesWithMother = "";
        String livesWithFather = "";
        String livesWithStepFather = "";
        String livesWithStepMother = "";
        String livesWithBrothers = "";
        String livesWithChildren = "";
        String livesWithLifemate = "";
        String livesWithOthers = "";
        String livesWithOthersDesc = "";
        String numBrothers = "";
        String numChildren = "";
        String grantOwnerType = "";
        String grantOwnerProvider = "";
        String mobilityInformationBegin = "";
        String mobilityInformationBeginDate = "";
        String mobilityInformationEnd = "";
        String mobilityInformationEndDate = "";
        String mobilityInformationProgramType = "";
        String mobilityInformationActivityType = "";
        String mobilityInformationScientificArea = "";
        String mobilityInformationProgramDuration = "";
        String mobilityInformationOriginProgrammeLevel = "";
        String mobilityInformationIncomingProgrammeLevel = "";
        String mobilityInformationOtherIncomingProgrammeLevel = "";
        String mobilityInformationOtherOriginProgrammeLevel = "";
        String mobilityInformationOriginCountry = "";
        String mobilityInformationIncomingCountry = "";

        //Get the last PersonalIngressionData that was already filled by the student.
        // GrantOnwerType is a required question. Therefore if it is answered, the student
        // has answered the rest of the data.
        Comparator<PersonalIngressionData> comparator =
                Collections.reverseOrder(PersonalIngressionData.COMPARATOR_BY_EXECUTION_YEAR);
        PersonalIngressionData pid = null;
        List<PersonalIngressionData> personalIngressionDataCollection =
                person.getStudent().getPersonalIngressionsDataSet().stream().sorted(comparator).collect(Collectors.toList());
        for (PersonalIngressionData personalIngressionData : personalIngressionDataCollection) {
            if (personalIngressionData.getGrantOwnerType() != null) {
                pid = personalIngressionData;
                break;
            }
        }

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
            if (pid.getGrantOwnerType() != null) {
                grantOwnerType = BundleUtil.getString(BUNDLE, pid.getGrantOwnerType().getQualifiedName());
            }
            if (pid.getGrantOwnerProvider() != null) {
                grantOwnerProvider = pid.getGrantOwnerProvider().getName();
            }

        }
        if (person.getSecondNationality() != null) {
            secondNationality = person.getSecondNationality().getCountryNationality().getContent();
        }

        PersonUlisboaSpecifications personUl = person.getPersonUlisboaSpecifications();
        if (personUl != null) {

            for (PersonalIngressionData checkDislocatedPersonalIngressionData : personalIngressionDataCollection) {
                if (checkDislocatedPersonalIngressionData.getDislocatedFromPermanentResidence() != null) {
                    Boolean isDislocatedFromResidence =
                            checkDislocatedPersonalIngressionData.getDislocatedFromPermanentResidence();
                    isDislocated = isDislocatedFromResidence ? BundleUtil.getString(BUNDLE, "label.true") : BundleUtil
                            .getString(BUNDLE, "label.false");

                    if (!isDislocatedFromResidence) {
                        //if it is not dislocated, then reset the dislocated address
                        countryOfDislocated = "";
                        districtOfDislocated = "";
                        districtSubdivisionOfDislocated = "";
                        parishOfDislocated = "";
                        addressOfDislocated = "";
                        areaCodeOfDislocated = "";
                    }

                    break;
                }
            }

            if (personUl.getDislocatedResidenceType() != null) {
                dislocatedResidenceType = personUl.getDislocatedResidenceType().getLocalizedName();
                if (personUl.getDislocatedResidenceType().isOther()) {
                    dislocatedResidenceType = personUl.getOtherDislocatedResidenceType();
                }
            }

            firstOptionDegree = personUl.getFirstOptionDegreeDesignation();
            if (personUl.getFirstOptionInstitution() != null) {
                firstOptionInstitution = personUl.getFirstOptionInstitution().getName();
            }

            if (personUl.getHasDisabilities()) {
                if (personUl.getDisabilityType().isOther()) {
                    disabilityType = personUl.getOtherDisabilityType();
                } else {
                    disabilityType = personUl.getDisabilityType().getLocalizedName();
                }

                needsDisabilitySupport = BundleUtil.getString("resources/FenixeduUlisboaSpecificationsResources",
                        "label." + personUl.getNeedsDisabilitySupport().toString());
            } else {
                disabilityType = BundleUtil.getString(BUNDLE, "label.false");
                needsDisabilitySupport = BundleUtil.getString(BUNDLE, "label.false");
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

            PersonUlisboaSpecificationsByExecutionYear personUlExecutionYear =
                    personUl.getPersonUlisboaSpecificationsByExcutionYear(executionYear);
            if (personUlExecutionYear != null) {

                if (personUlExecutionYear.getProfessionTimeType() != null) {
                    professionTimeType = personUlExecutionYear.getProfessionTimeType().getLocalizedName();
                }
                if (personUlExecutionYear.getHouseholdSalarySpan() != null) {
                    salarySpan = personUlExecutionYear.getHouseholdSalarySpan().getLocalizedName();
                }

                if (personUlExecutionYear.getRemuneratedActivityInPast() != null) {
                    remuneratedActivityInPast = personUlExecutionYear.getRemuneratedActivityInPast() == Boolean.TRUE ? BundleUtil
                            .getString(BUNDLE, "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getRemuneratedActivityInPastDescription() != null) {
                    remuneratedActivityInPastDescription = personUlExecutionYear.getRemuneratedActivityInPastDescription();
                }

                if (personUlExecutionYear.getFlunkedUniversity() != null) {
                    flunkedUniversity = personUlExecutionYear.getFlunkedUniversity() == Boolean.TRUE ? BundleUtil
                            .getString(BUNDLE, "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                    if (personUlExecutionYear.getFlunkedUniversityTimes() != null
                            && personUlExecutionYear.getFlunkedUniversity()) {
                        flunkedUniversityTimes = personUlExecutionYear.getFlunkedUniversityTimes().toString();
                    }
                }

                if (personUlExecutionYear.getLivesAlone() != null) {
                    livesAlone = personUlExecutionYear.getLivesAlone() == Boolean.TRUE ? BundleUtil.getString(BUNDLE,
                            "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithMother() != null) {
                    livesWithMother = personUlExecutionYear.getLivesWithMother() == Boolean.TRUE ? BundleUtil.getString(BUNDLE,
                            "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithFather() != null) {
                    livesWithFather = personUlExecutionYear.getLivesWithFather() == Boolean.TRUE ? BundleUtil.getString(BUNDLE,
                            "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithStepFather() != null) {
                    livesWithStepFather = personUlExecutionYear.getLivesWithStepFather() == Boolean.TRUE ? BundleUtil
                            .getString(BUNDLE, "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithStepMother() != null) {
                    livesWithStepMother = personUlExecutionYear.getLivesWithStepMother() == Boolean.TRUE ? BundleUtil
                            .getString(BUNDLE, "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithBrothers() != null) {
                    livesWithBrothers = personUlExecutionYear.getLivesWithBrothers() == Boolean.TRUE ? BundleUtil
                            .getString(BUNDLE, "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithChildren() != null) {
                    livesWithChildren = personUlExecutionYear.getLivesWithChildren() == Boolean.TRUE ? BundleUtil
                            .getString(BUNDLE, "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithLifemate() != null) {
                    livesWithLifemate = personUlExecutionYear.getLivesWithLifemate() == Boolean.TRUE ? BundleUtil
                            .getString(BUNDLE, "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithOthers() != null) {
                    livesWithOthers = personUlExecutionYear.getLivesWithOthers() == Boolean.TRUE ? BundleUtil.getString(BUNDLE,
                            "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                }

                if (personUlExecutionYear.getLivesWithOthersDesc() != null) {
                    livesWithOthersDesc = personUlExecutionYear.getLivesWithOthersDesc();
                }

                if (personUlExecutionYear.getNumBrothers() != null) {
                    numBrothers = personUlExecutionYear.getNumBrothers().toString();
                }

                if (personUlExecutionYear.getNumChildren() != null) {
                    numChildren = personUlExecutionYear.getNumChildren().toString();
                }
            }

            if (personUl.getFlunkedPreHighSchool() != null) {
                flunkedPreHighSchool = personUl.getFlunkedPreHighSchool() == Boolean.TRUE ? BundleUtil.getString(BUNDLE,
                        "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                if (personUl.getFlunkedPreHighSchoolTimes() != null && personUl.getFlunkedPreHighSchool()) {
                    flunkedPreHighSchoolTimes = personUl.getFlunkedPreHighSchoolTimes().toString();
                }
            }

            if (personUl.getFlunkedHighSchool() != null) {
                flunkedHighSchool = personUl.getFlunkedHighSchool() == Boolean.TRUE ? BundleUtil.getString(BUNDLE,
                        "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                if (personUl.getFlunkedHighSchoolTimes() != null && personUl.getFlunkedHighSchool()) {
                    flunkedHighSchoolTimes = personUl.getFlunkedHighSchoolTimes().toString();
                }
            }

            if (personUl.getSocialBenefitsInHighSchool() != null) {
                socialBenefitsInHighSchool = personUl.getSocialBenefitsInHighSchool() == Boolean.TRUE ? BundleUtil
                        .getString(BUNDLE, "label.true") : BundleUtil.getString(BUNDLE, "label.false");
                if (personUl.getSocialBenefitsInHighSchool()) {
                    socialBenefitsInHighSchoolDescription = personUl.getSocialBenefitsInHighSchoolDescription();
                }
            }

            if (personUl.getFirstTimeInPublicUniv() != null) {
                firstTimeInPublicUniv = personUl.getFirstTimeInPublicUniv() == Boolean.TRUE ? BundleUtil.getString(BUNDLE,
                        "label.true") : BundleUtil.getString(BUNDLE, "label.false");
            }

            if (personUl.getPublicUnivCandidacies() != null) {
                publicUnivCandidacies = personUl.getPublicUnivCandidacies().toString();
            }

            if (personUl.getFirstTimeInUlisboa() != null) {
                firstTimeInUlisboa = personUl.getFirstTimeInUlisboa() == Boolean.TRUE ? BundleUtil.getString(BUNDLE,
                        "label.true") : BundleUtil.getString(BUNDLE, "label.false");
            }

            if (personUl.getBestQualitiesInThisCicle() != null) {
                bestQualitiesInThisCicle = personUl.getBestQualitiesInThisCicle();
            }

            MobilityRegistatrionUlisboaInformation mobilityInformation = personUl.getMobilityRegistatrionUlisboaInformation();
            if (mobilityInformation != null) {
                if (mobilityInformation.getBegin() != null) {
                    mobilityInformationBegin = mobilityInformation.getBegin().getQualifiedName();
                }
                if (mobilityInformation.getBeginDate() != null) {
                    mobilityInformationBeginDate = mobilityInformation.getBeginDate().toString("dd-MM-yyyy");
                }
                if (mobilityInformation.getEnd() != null) {
                    mobilityInformationEnd = mobilityInformation.getEnd().getQualifiedName();
                }
                if (mobilityInformation.getEndDate() != null) {
                    mobilityInformationEndDate = mobilityInformation.getEndDate().toString("dd-MM-yyyy");
                }
                if (mobilityInformation.getMobilityProgramType() != null) {
                    mobilityInformationProgramType = mobilityInformation.getMobilityProgramType().getName().getContent();
                }
                if (mobilityInformation.getMobilityActivityType() != null) {
                    mobilityInformationActivityType = mobilityInformation.getMobilityActivityType().getName().getContent();
                }
                if (mobilityInformation.getMobilityScientificArea() != null) {
                    mobilityInformationScientificArea = mobilityInformation.getMobilityScientificArea().getName().getContent();
                }
                if (mobilityInformation.getProgramDuration() != null) {
                    mobilityInformationProgramDuration = ULisboaSpecificationsUtil
                            .bundle("label.SchoolPeriodDuration." + mobilityInformation.getProgramDuration());
                }
                if (mobilityInformation.getOriginMobilityProgrammeLevel() != null) {
                    mobilityInformationOriginProgrammeLevel =
                            mobilityInformation.getOriginMobilityProgrammeLevel().getName().getContent();
                }
                if (mobilityInformation.getIncomingMobilityProgrammeLevel() != null) {
                    mobilityInformationIncomingProgrammeLevel =
                            mobilityInformation.getIncomingMobilityProgrammeLevel().getName().getContent();
                }
                if (mobilityInformation.getOtherIncomingMobilityProgrammeLevel() != null) {
                    mobilityInformationOtherIncomingProgrammeLevel = mobilityInformation.getOtherIncomingMobilityProgrammeLevel();
                }
                if (mobilityInformation.getOtherOriginMobilityProgrammeLevel() != null) {
                    mobilityInformationOtherOriginProgrammeLevel = mobilityInformation.getOtherOriginMobilityProgrammeLevel();
                }
                if (mobilityInformation.getOriginCountry() != null) {
                    mobilityInformationOriginCountry = mobilityInformation.getOriginCountry().getLocalizedName().getContent();
                }
                if (mobilityInformation.getIncomingCountry() != null) {
                    mobilityInformationIncomingCountry = mobilityInformation.getIncomingCountry().getLocalizedName().getContent();
                }
            }

        }

        String precedentCountry = "";
        String precedentDistrict = "";
        String precedentDistrictSubdivision = "";
        String precedentSchoolLevel = "";
        String precedentInstitution = "";
        String precedentDegreeDesignation = "";
        String precedentConclusionGrade = "";
        String precedentConclusionYear = "";
        String precedentHighSchoolType = "";
        if (studentCandidacy.getCompletedDegreeInformation() != null) {
            PrecedentDegreeInformation information = studentCandidacy.getCompletedDegreeInformation();
            if (information.getCountry() != null) {
                precedentCountry = information.getCountry().getLocalizedName().getContent();
            }
            District district = information.getDistrict();
            if (district != null) {
                precedentDistrict = district.getName();
            }
            DistrictSubdivision districtSubdivision = information.getDistrictSubdivision();
            if (districtSubdivision != null) {
                precedentDistrictSubdivision = districtSubdivision.getName();
            }
            SchoolLevelType schoolLevelType = information.getSchoolLevel();
            if (schoolLevelType != null) {
                precedentSchoolLevel = schoolLevelType.getLocalizedName();
                if (schoolLevelType.isOther()) {
                    precedentSchoolLevel = information.getOtherSchoolLevel();
                }
                //Inof other can be null or schoolLevelType without defined label
                if (precedentSchoolLevel == null) {
                    precedentSchoolLevel = "";
                }

            }
            precedentInstitution = information.getInstitutionName();
            precedentDegreeDesignation = information.getDegreeDesignation();

            precedentConclusionGrade = information.getConclusionGrade();
            precedentConclusionYear = "" + information.getConclusionYear();
            if (information.getInstitutionType() != null) {
                AcademicalInstitutionType highSchoolType = information.getInstitutionType();
                precedentHighSchoolType = BundleUtil.getString(BUNDLE, highSchoolType.getName());
            }
        }

        Degree studentCandidacyDegree = studentCandidacy.getDegreeCurricularPlan().getDegree();
        String institutionName = "";
        Unit institutionUnit = Bennu.getInstance().getInstitutionUnit();
        if (institutionUnit != null) {
            institutionName = institutionUnit.getName();
        }
        String cycleName = "";
        if (studentCandidacyDegree != null) {
            DegreeType degreeType = studentCandidacyDegree.getDegreeType();
            if (degreeType != null) {
                CycleType firstOrderedCycleType = degreeType.getFirstOrderedCycleType();
                if (firstOrderedCycleType != null) {
                    cycleName = firstOrderedCycleType.getDescription();
                }
            }
        }

        String institutionalEmail = studentCandidacy.getPerson().getInstitutionalEmailAddressValue();
        String defaultEmail = "";
        if (!studentCandidacy.getPerson().getDefaultEmailAddressValue().equals(institutionalEmail)) {
            defaultEmail = studentCandidacy.getPerson().getDefaultEmailAddressValue();
        }

        String phone = studentCandidacy.getPerson().getDefaultPhoneNumber();
        String telephone = studentCandidacy.getPerson().getDefaultMobilePhoneNumber();

        String maritalStatus = studentCandidacy.getPerson().getMaritalStatus().getLocalizedName();

        String expirationDateOfIdDoc = "";
        if (studentCandidacy.getPerson().getExpirationDateOfDocumentIdYearMonthDay() != null) {
            expirationDateOfIdDoc =
                    studentCandidacy.getPerson().getExpirationDateOfDocumentIdYearMonthDay().toString("dd-MM-yyyy");
        }
        String emissionLocationOfIdDoc = "";
        if (studentCandidacy.getPerson().getEmissionLocationOfDocumentId() != null) {
            emissionLocationOfIdDoc = studentCandidacy.getPerson().getEmissionLocationOfDocumentId();
        }

        String vaccinationValidity = "";
        if (personUl != null && personUl.getVaccinationValidity() != null) {
            vaccinationValidity = personUl.getVaccinationValidity().toString("dd-MM-yyyy");
        }

        String curricularYear = "";
        StudentCurricularPlan scpForCurricularYear =
                RegistrationServices.getStudentCurricularPlan(studentCandidacy.getRegistration(), executionYear);
        if (scpForCurricularYear != null) {
            Curriculum cForCurricularYear = scpForCurricularYear.getCurriculum(new DateTime(), executionYear);
            if (cForCurricularYear != null) {
                Integer curricularYearValue = cForCurricularYear.getCurricularYear();
                if (curricularYearValue != null) {
                    curricularYear += curricularYearValue;
                }
            }
        }

        String precendentDegreeCycle = "";
        if (precedentSchoolLevel.contains("Bacharelato") || precedentSchoolLevel.contains("Licenciatura")) {
            precendentDegreeCycle = CycleType.FIRST_CYCLE.getDescription();
        } else if (precedentSchoolLevel.contains("Mestrado")) {
            precendentDegreeCycle = CycleType.SECOND_CYCLE.getDescription();
        } else {
            precendentDegreeCycle = "----";
        }

        return new RegistrationDGESStateBean(executionYearName, studentCandidacy.getExternalId(), entryGrade, degreeTypeName,
                degreeCode, degreeName, cycleName, curricularYear, degreeLevel, degreeBranch, regimeType, institutionName,
                studentNumber, documentIdNumber, documentIdType, fiscalCountry, socialSecurityNumber, expirationDateOfIdDoc,
                emissionLocationOfIdDoc, candidacyState, name, maritalStatus, registrationStatus, numberOfUCsEnrolled,
                nationality, secondNationality, birthYear, countryOfBirth, districtOfBirth, districtSubdivisionOfBirth,
                parishOfBirth, gender, ingressionType, placingOption, firstOptionDegree, firstOptionInstitution, isDislocated,
                dislocatedResidenceType, countryOfResidence, districtOfResidence, districtSubdivisionOfResidence,
                parishOfResidence, addressOfResidence, areaCodeOfResidence, countryOfDislocated, districtOfDislocated,
                districtSubdivisionOfDislocated, parishOfDislocated, addressOfDislocated, areaCodeOfDislocated, profession,
                professionTimeType, professionalCondition, professionType, fatherName, fatherSchoolLevel,
                fatherProfessionalCondition, fatherProfessionType, motherName, motherSchoolLevel, motherProfessionalCondition,
                motherProfessionType, salarySpan, disabilityType, needsDisabilitySupport, universityDiscoveryString,
                universityChoiceString, precedentCountry, precedentDistrict, precedentDistrictSubdivision, precedentSchoolLevel,
                precedentInstitution, precedentDegreeDesignation, precedentConclusionGrade, precedentConclusionYear,
                precedentHighSchoolType, precendentDegreeCycle, institutionalEmail, defaultEmail, phone, telephone,
                vaccinationValidity, grantOwnerType, grantOwnerProvider, flunkedPreHighSchool, flunkedPreHighSchoolTimes,
                flunkedHighSchool, flunkedHighSchoolTimes, socialBenefitsInHighSchool, socialBenefitsInHighSchoolDescription,
                firstTimeInPublicUniv, firstTimeInUlisboa, publicUnivCandidacies, bestQualitiesInThisCicle,
                remuneratedActivityInPast, remuneratedActivityInPastDescription, flunkedUniversity, flunkedUniversityTimes,
                livesAlone, livesWithMother, livesWithFather, livesWithStepFather, livesWithStepMother, livesWithBrothers,
                livesWithChildren, livesWithLifemate, livesWithOthers, livesWithOthersDesc, numBrothers, numChildren,
                mobilityInformationBegin, mobilityInformationBeginDate, mobilityInformationEnd, mobilityInformationEndDate,
                mobilityInformationProgramType, mobilityInformationActivityType, mobilityInformationScientificArea,
                mobilityInformationProgramDuration, mobilityInformationOriginProgrammeLevel,
                mobilityInformationIncomingProgrammeLevel, mobilityInformationOtherIncomingProgrammeLevel,
                mobilityInformationOtherOriginProgrammeLevel, mobilityInformationOriginCountry,
                mobilityInformationIncomingCountry);
    }

    public static String getPrimaryBranchName(final StudentCurricularPlan studentCurricularPlan) {
        return studentCurricularPlan.getBranchCurriculumGroups().stream()
                .filter(b -> b.getDegreeModule().getBranchType() == BranchType.MAJOR).map(b -> b.getName().getContent())
                .collect(Collectors.joining(","));
    }

    // TODO remove to DTO with RDGESSBean
    public static PhysicalAddress getSchoolTimePhysicalAddress(final Person person) {
        Predicate<PhysicalAddress> addressIsSchoolTime =
                address -> !address.isDefault() && address.isValid() && address.getType().equals(PartyContactType.PERSONAL);
        return person.getPhysicalAddresses().stream().filter(addressIsSchoolTime).sorted(CONTACT_COMPARATOR_BY_MODIFIED_DATE)
                .findFirst().orElse(null);
    }

}