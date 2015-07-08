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
package org.fenixedu.ulisboa.specifications.domain.student.importation;

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
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.dto.person.PersonBean;
import org.fenixedu.academic.util.PhoneUtil;
import org.fenixedu.spaces.domain.Space;
import org.joda.time.YearMonthDay;

public class DegreeCandidateDTO {

    private String degreeCode;

    private String documentIdNumber;

    private String name;

    private String address;

    private String areaCode;

    private String areaOfAreaCode;

    private String phoneNumber;

    private Gender gender;

    private YearMonthDay dateOfBirth;

    private String contigent;

    private IngressionType ingression;

    private Integer placingOption;

    private String highSchoolFinalGrade;

    private AcademicalInstitutionType highSchoolType;

    private Double entryGrade;

    private EntryPhase entryPhase;

    public String getDegreeCode() {
        return degreeCode;
    }

    public void setDegreeCode(String degreeCode) {
        this.degreeCode = degreeCode;
    }

    public String getDocumentIdNumber() {
        return documentIdNumber;
    }

    public void setDocumentIdNumber(String documentIdNumber) {
        this.documentIdNumber = documentIdNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaOfAreaCode() {
        return areaOfAreaCode;
    }

    public void setAreaOfAreaCode(String areaOfAreaCode) {
        this.areaOfAreaCode = areaOfAreaCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public YearMonthDay getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(YearMonthDay dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContigent() {
        return contigent;
    }

    public void setContigent(String contigent) {
        this.contigent = contigent;
    }

    public IngressionType getIngression() {
        return ingression;
    }

    public void setIngression(IngressionType ingression) {
        this.ingression = ingression;
    }

    public Integer getPlacingOption() {
        return placingOption;
    }

    public void setPlacingOption(Integer placingOption) {
        this.placingOption = placingOption;
    }

    public String getHighSchoolFinalGrade() {
        return highSchoolFinalGrade;
    }

    public void setHighSchoolFinalGrade(String highSchoolFinalGrade) {
        this.highSchoolFinalGrade = highSchoolFinalGrade;
    }

    public AcademicalInstitutionType getHighSchoolType() {
        return highSchoolType;
    }

    public void setHighSchoolType(AcademicalInstitutionType highSchoolType) {
        this.highSchoolType = highSchoolType;
    }

    public Double getEntryGrade() {
        return entryGrade;
    }

    public void setEntryGrade(Double entryGrade) {
        this.entryGrade = entryGrade;
    }

    public EntryPhase getEntryPhase() {
        return entryPhase;
    }

    public void setEntryPhase(EntryPhase entryPhase) {
        this.entryPhase = entryPhase;
    }

    public Person createPerson() {
        PersonBean personBean = new PersonBean(getName(), getDocumentIdNumber(), IDDocumentType.IDENTITY_CARD, getDateOfBirth());
        personBean.setGender(getGender());
        Person person = new Person(personBean);

        person.setMaritalStatus(MaritalStatus.SINGLE);
        person.setDateOfBirthYearMonthDay(getDateOfBirth());

        PhysicalAddress createPhysicalAddress =
                PhysicalAddress.createPhysicalAddress(person, new PhysicalAddressData(getAddress(), getAreaCode(),
                        getAreaOfAreaCode(), null), PartyContactType.PERSONAL, true);
        createPhysicalAddress.setValid();

        if (PhoneUtil.isMobileNumber(getPhoneNumber())) {
            MobilePhone createMobilePhone =
                    MobilePhone.createMobilePhone(person, getPhoneNumber(), PartyContactType.PERSONAL, true);
            createMobilePhone.setValid();
        } else {
            Phone createPhone = Phone.createPhone(person, getPhoneNumber(), PartyContactType.PERSONAL, true);
            createPhone.setValid();
        }

        return person;
    }

    public ExecutionDegree getExecutionDegree(final ExecutionYear executionYear, Space space) {
        return ExecutionDegree.readByDegreeCodeAndExecutionYearAndCampus(getDegreeCode(), executionYear, space);
    }

    public static abstract class MatchingPersonException extends Exception {
    }

    public static class NotFoundPersonException extends MatchingPersonException {
    }

    public static class TooManyMatchedPersonsException extends MatchingPersonException {
    }
}
