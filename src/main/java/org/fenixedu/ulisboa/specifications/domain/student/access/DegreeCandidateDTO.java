/**
 *  Copyright © 2015 Universidade de Lisboa
 *
 *  This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 *  FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute
 *  it and/or modify it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FenixEdu fenixedu-ulisboa-specifications.
 *  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.fenixedu.ulisboa.specifications.domain.student.access;

import org.fenixedu.academic.FenixEduAcademicConfiguration;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.contacts.MobilePhone;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.Phone;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.contacts.PhysicalAddressData;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.HumanName;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.util.PhoneUtil;
import org.fenixedu.academic.util.StringFormatter;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;

public class DegreeCandidateDTO {

    private final int lineNumber;

    private String degreeCode;

    private String documentIdNumber;

    private String name;

    private String address;

    private String areaCode;

    private String areaOfAreaCode;

    private String phoneNumber;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String contigent;

    private Integer placingOption;

    private AcademicalInstitutionType highSchoolType;

    private Double entryGrade;

    private EntryPhase entryPhase;

    private Country nationality;

    private String personalDataLine;

    private String addressDataLine;

    public DegreeCandidateDTO(final int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getDegreeCode() {
        return degreeCode;
    }

    public void setDegreeCode(final String degreeCode) {
        this.degreeCode = degreeCode;
    }

    public String getDocumentIdNumber() {
        return documentIdNumber;
    }

    public void setDocumentIdNumber(final String documentIdNumber) {
        this.documentIdNumber = documentIdNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(final String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaOfAreaCode() {
        return areaOfAreaCode;
    }

    public void setAreaOfAreaCode(final String areaOfAreaCode) {
        this.areaOfAreaCode = areaOfAreaCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    public void setGender(final String gender) {
        if (gender.equals("M")) {
            setGender(Gender.MALE);
        } else if (gender.equals("F")) {
            setGender(Gender.FEMALE);
        } else {
            throw new RuntimeException("[" + getLineNumber() + "] Unknown gender: " + gender);
        }

    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContigent() {
        return contigent;
    }

    public void setContigent(final String contigent) {
        this.contigent = contigent;
    }

    public IngressionType getIngression() {
        final IngressionType ingression = ULisboaSpecificationsRoot.getInstance().getIngressionType(contigent);
        if (ingression == null) {
            throw new RuntimeException("[" + getLineNumber() + "] Contigent: " + contigent
                    + " is not mapped to any IngressionType. Please configure an IngressionType for contigent: " + contigent);
        }

        return ingression;
    }

    public Integer getPlacingOption() {
        return placingOption;
    }

    public void setPlacingOption(final Integer placingOption) {
        this.placingOption = placingOption;
    }

    public AcademicalInstitutionType getHighSchoolType() {
        return highSchoolType;
    }

    public void setHighSchoolType(final AcademicalInstitutionType highSchoolType) {
        this.highSchoolType = highSchoolType;
    }

    public Double getEntryGrade() {
        return entryGrade;
    }

    public void setEntryGrade(final Double entryGrade) {
        this.entryGrade = entryGrade;
    }

    public EntryPhase getEntryPhase() {
        return entryPhase;
    }

    public void setEntryPhase(final EntryPhase entryPhase) {
        this.entryPhase = entryPhase;
    }

    public Person createPerson() {
        HumanName split = HumanName.decompose(StringFormatter.prettyPrint(getName()), false);
        UserProfile profile = new UserProfile(split.getGivenNames(), split.getFamilyNames(), null, null, null);
        new User(profile);

        final Person person = new Person(profile);

        person.setGender(getGender());
        Country nationality = getNationality();
        person.setCountry(nationality);
        if (nationality != null && nationality.isDefaultCountry()) {
            person.setIdentification(getDocumentIdNumber(), IDDocumentType.IDENTITY_CARD);
        } else {
            person.setIdentification(getDocumentIdNumber(), IDDocumentType.OTHER);
            PersonUlisboaSpecifications.findOrCreate(person).setDgesTempIdCode(getDocumentIdNumber());
        }

        person.setMaritalStatus(MaritalStatus.SINGLE);
        person.setDateOfBirthYearMonthDay(YearMonthDay.fromDateFields(getDateOfBirth().toDate()));

        final PhysicalAddress createPhysicalAddress = PhysicalAddress.createPhysicalAddress(person,
                new PhysicalAddressData(getAddress(), getAreaCode(), getAreaOfAreaCode(), null), PartyContactType.PERSONAL, true);
        createPhysicalAddress.setValid();

        if (PhoneUtil.isMobileNumber(getPhoneNumber())) {
            final MobilePhone createMobilePhone =
                    MobilePhone.createMobilePhone(person, getPhoneNumber(), PartyContactType.PERSONAL, true);
            createMobilePhone.setValid();
        } else {
            final Phone createPhone = Phone.createPhone(person, getPhoneNumber(), PartyContactType.PERSONAL, true);
            createPhone.setValid();
        }

        person.editSocialSecurityNumber(Country.readDefault(),
                FenixEduAcademicConfiguration.getConfiguration().getDefaultSocialSecurityNumber());

        return person;
    }

    public ExecutionDegree getExecutionDegree(final ExecutionYear executionYear, final Space space) {
        return ExecutionDegree.readByDegreeCodeAndExecutionYearAndCampus(getDegreeCode(), executionYear, space);
    }

    public Country getNationality() {
        return nationality;
    }

    public void setNationality(final Country nationality) {
        this.nationality = nationality;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getPersonalDataLine() {
        return personalDataLine;
    }

    public void setPersonalDataLine(final String personalDataLine) {
        this.personalDataLine = personalDataLine;
    }

    public String getAddressDataLine() {
        return addressDataLine;
    }

    public void setAddressDataLine(final String addressDataLine) {
        this.addressDataLine = addressDataLine;
    }

}
