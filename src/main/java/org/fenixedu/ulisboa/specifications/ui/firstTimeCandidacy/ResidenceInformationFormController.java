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

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.contacts.PhysicalAddressData;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FiliationFormController.DistrictSubdivisionBean;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(ResidenceInformationFormController.CONTROLLER_URL)
public class ResidenceInformationFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/residenceinformationform";

    private static final String _FILLRESIDENCEINFORMATION_URI = "/fillresidenceinformation";
    public static final String FILLRESIDENCEINFORMATION_URL = CONTROLLER_URL + _FILLRESIDENCEINFORMATION_URI;

    @RequestMapping(value = _FILLRESIDENCEINFORMATION_URI, method = RequestMethod.GET)
    public String fillresidenceinformation(Model model) {
        model.addAttribute("countries_options", Bennu.getInstance().getCountrysSet());
        model.addAttribute("districts_options", Bennu.getInstance().getDistrictsSet());
        fillFormIfRequired(model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/residenceinformationform/fillresidenceinformation";
    }

    private void fillFormIfRequired(Model model) {
        if (!model.containsAttribute("residenceInformationForm")) {
            StudentCandidacy candidacy = FirstTimeCandidacyController.getStudentCandidacy();
            PersonalIngressionData personalData =
                    FirstTimeCandidacyController.getOrCreatePersonalIngressionData(candidacy.getPrecedentDegreeInformation());
            Person person = AccessControl.getPerson();

            ResidenceInformationForm form = new ResidenceInformationForm();
            form.setCountryOfResidence(personalData.getCountryOfResidence());
            if (form.getCountryOfResidence() == null) {
                form.setCountryOfResidence(Country.readDefault());
            }
            form.setAddress(person.getDefaultPhysicalAddress().getAddress());
            form.setAreaOfAreaCode(person.getDefaultPhysicalAddress().getAreaOfAreaCode());
            form.setAreaCode(person.getDefaultPhysicalAddress().getAreaCode());
            form.setArea(person.getDefaultPhysicalAddress().getArea());
            District district =
                    personalData.getDistrictSubdivisionOfResidence() != null ? personalData.getDistrictSubdivisionOfResidence()
                            .getDistrict() : null;
            form.setDistrictOfResidence(district);
            DistrictSubdivision districtSubdivisionOfResidence = personalData.getDistrictSubdivisionOfResidence();
            form.setDistrictSubdivisionOfResidence(districtSubdivisionOfResidence);
            form.setParishOfResidence(Parish.findByName(districtSubdivisionOfResidence,
                    person.getDefaultPhysicalAddress().getParishOfResidence()).orElse(null));

            form.setDislocatedFromPermanentResidence(personalData.getDislocatedFromPermanentResidence());
            if (personalData.getDislocatedFromPermanentResidence() != null && personalData.getDislocatedFromPermanentResidence()) {
                PhysicalAddress addressSchoolTime = getSchoolTimePhysicalAddress(person);
                form.setSchoolTimeAddress(addressSchoolTime.getAddress());
                form.setSchoolTimeAreaCode(addressSchoolTime.getAreaCode());
                form.setSchoolTimeAreaOfAreaCode(addressSchoolTime.getAreaOfAreaCode());
                form.setSchoolTimeArea(addressSchoolTime.getArea());
                district =
                        personalData.getSchoolTimeDistrictSubDivisionOfResidence() != null ? personalData
                                .getSchoolTimeDistrictSubDivisionOfResidence().getDistrict() : null;
                form.setSchoolTimeDistrictOfResidence(district);
                DistrictSubdivision schoolTimeDistrictSubDivisionOfResidence =
                        personalData.getSchoolTimeDistrictSubDivisionOfResidence();
                form.setSchoolTimeDistrictSubdivisionOfResidence(schoolTimeDistrictSubDivisionOfResidence);
                form.setSchoolTimeParishOfResidence(Parish.findByName(schoolTimeDistrictSubDivisionOfResidence,
                        addressSchoolTime.getParishOfResidence()).orElse(null));
            }

            model.addAttribute("residenceInformationForm", form);
        }
    }

    @RequestMapping(value = _FILLRESIDENCEINFORMATION_URI, method = RequestMethod.POST)
    public String fillresidenceinformation(ResidenceInformationForm form, Model model, RedirectAttributes redirectAttributes) {
        if (!validate(form, model)) {
            return fillresidenceinformation(model);
        }

        try {
            writeData(form);
            model.addAttribute("residenceInformationForm", form);
            return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/contactsform/fillcontacts/", model,
                    redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillresidenceinformation(model);
        }
    }

    private boolean validate(ResidenceInformationForm form, Model model) {
        if (!form.getCountryOfResidence().isDefaultCountry() && !form.getDislocatedFromPermanentResidence()) {
            addErrorMessage(
                    BundleUtil
                            .getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                                    "error.candidacy.workflow.ResidenceInformationForm.non.nacional.students.should.select.dislocated.option.and.fill.address"),
                    model);
            return false;
        }
        if (form.getCountryOfResidence().isDefaultCountry() && !form.isResidenceInformationFilled()) {
            addErrorMessage(
                    BundleUtil
                            .getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                                    "error.candidacy.workflow.ResidenceInformationForm.address.national.students.should.supply.complete.address.information"),
                    model);
            return false;
        }
        if (form.isAnySchoolTimeAddressInformationFilled() && !form.getDislocatedFromPermanentResidence()) {
            addErrorMessage(
                    BundleUtil
                            .getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                                    "error.candidacy.workflow.ResidenceInformationForm.only.dislocated.students.should.fill.school.time.address.information"),
                    model);
            return false;
        }

        if (form.getDislocatedFromPermanentResidence()) {
            if (!form.isSchoolTimeRequiredInformationAddressFilled()) {
                addErrorMessage(
                        BundleUtil
                                .getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                                        "error.candidacy.workflow.ResidenceInformationForm.address.information.is.required.for.dislocated.students"),
                        model);
                return false;
            } else {
                if ((form.isAnyFilled(form.getSchoolTimeAddress(), form.getSchoolTimeAreaCode(),
                        form.getSchoolTimeAreaOfAreaCode(), form.getSchoolTimeArea()) || form.getSchoolTimeParishOfResidence() != null)
                        && (form.isAnyEmpty(form.getSchoolTimeAddress(), form.getSchoolTimeAreaCode(),
                                form.getSchoolTimeAreaOfAreaCode(), form.getSchoolTimeArea()) || form
                                .getSchoolTimeParishOfResidence() != null)) {
                    addErrorMessage(
                            BundleUtil
                                    .getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                                            "error.candidacy.workflow.ResidenceInformationForm.school.time.address.must.be.filled.completly.otherwise.fill.minimun.required"),
                            model);
                    return false;
                }
            }

        }

        return true;
    }

    @Atomic
    protected void writeData(ResidenceInformationForm form) {
        Person person = AccessControl.getPerson();
        StudentCandidacy candidacy = FirstTimeCandidacyController.getStudentCandidacy();
        PersonalIngressionData personalData =
                FirstTimeCandidacyController.getOrCreatePersonalIngressionData(candidacy.getPrecedentDegreeInformation());
        personalData.setCountryOfResidence(form.getCountryOfResidence());
        personalData.setDistrictSubdivisionOfResidence(form.getDistrictSubdivisionOfResidence());
        personalData.setDislocatedFromPermanentResidence(form.getDislocatedFromPermanentResidence());

        if (form.getDislocatedFromPermanentResidence()) {
            personalData.setSchoolTimeDistrictSubDivisionOfResidence(form.getSchoolTimeDistrictSubdivisionOfResidence());
        }

        String district =
                form.getDistrictSubdivisionOfResidence().getDistrict() != null ? form.getDistrictSubdivisionOfResidence()
                        .getDistrict().getName() : null;
        String subdivision =
                form.getDistrictSubdivisionOfResidence() != null ? form.getDistrictSubdivisionOfResidence().getName() : null;
        PhysicalAddressData physicalAddressData;
        if (!StringUtils.equals(form.getAddress(), person.getDefaultPhysicalAddress().getAddress())
                || !StringUtils.equals(form.getAreaCode(), person.getDefaultPhysicalAddress().getAreaCode())
                || !StringUtils.equals(form.getAreaOfAreaCode(), person.getDefaultPhysicalAddress().getAreaOfAreaCode())
                || !StringUtils.equals(form.getArea(), person.getDefaultPhysicalAddress().getArea())
                || !StringUtils.equals(form.getParishOfResidence().getName(), person.getDefaultPhysicalAddress()
                        .getParishOfResidence())
                || !StringUtils.equals(subdivision, person.getDefaultPhysicalAddress().getDistrictSubdivisionOfResidence())
                || !StringUtils.equals(district, person.getDefaultPhysicalAddress().getDistrictOfResidence())
                || (form.getCountryOfResidence() != person.getDefaultPhysicalAddress().getCountryOfResidence())) {
            physicalAddressData =
                    new PhysicalAddressData(form.getAddress(), form.getAreaCode(), form.getAreaOfAreaCode(), form.getArea(), form
                            .getParishOfResidence().getName(), subdivision, district, form.getCountryOfResidence());
            person.setDefaultPhysicalAddressData(physicalAddressData, true);
        }

        if (form.getDislocatedFromPermanentResidence() && form.isSchoolTimeAddressComplete()) {
            district =
                    form.getSchoolTimeDistrictSubdivisionOfResidence().getDistrict() != null ? form
                            .getSchoolTimeDistrictSubdivisionOfResidence().getDistrict().getName() : null;
            subdivision =
                    form.getSchoolTimeDistrictSubdivisionOfResidence() != null ? form
                            .getSchoolTimeDistrictSubdivisionOfResidence().getName() : null;
            PhysicalAddress schoolTimeAddress = getSchoolTimePhysicalAddress(person);
            if (schoolTimeAddress == null
                    || !StringUtils.equals(form.getSchoolTimeAddress(), schoolTimeAddress.getAddress())
                    || !StringUtils.equals(form.getSchoolTimeAreaCode(), schoolTimeAddress.getAreaCode())
                    || !StringUtils.equals(form.getSchoolTimeAreaOfAreaCode(), schoolTimeAddress.getAreaOfAreaCode())
                    || !StringUtils.equals(form.getSchoolTimeArea(), schoolTimeAddress.getArea())
                    || !StringUtils.equals(form.getSchoolTimeParishOfResidence().getName(),
                            schoolTimeAddress.getParishOfResidence())
                    || !StringUtils.equals(subdivision, schoolTimeAddress.getDistrictSubdivisionOfResidence())
                    || !StringUtils.equals(district, schoolTimeAddress.getDistrictOfResidence())) {
                physicalAddressData =
                        new PhysicalAddressData(form.getSchoolTimeAddress(), form.getSchoolTimeAreaCode(),
                                form.getSchoolTimeAreaOfAreaCode(), form.getSchoolTimeArea(), form
                                        .getSchoolTimeParishOfResidence().getName(), form
                                        .getSchoolTimeDistrictSubdivisionOfResidence().getName(), form
                                        .getSchoolTimeDistrictSubdivisionOfResidence().getDistrict().getName(),
                                Country.readDefault());

                if (schoolTimeAddress != null) {
                    schoolTimeAddress.edit(physicalAddressData);
                    schoolTimeAddress.setValid();
                } else {
                    schoolTimeAddress =
                            PhysicalAddress.createPhysicalAddress(person, physicalAddressData, PartyContactType.PERSONAL, false);
                    schoolTimeAddress.setValid();
                }
            }
        }
    }

    private PhysicalAddress getSchoolTimePhysicalAddress(Person person) {
        Predicate<PhysicalAddress> addressIsSchoolTime =
                address -> !address.isDefault() && address.isValid() && address.getType().equals(PartyContactType.PERSONAL);
        return person.getPhysicalAddresses().stream().filter(addressIsSchoolTime).sorted(CONTACT_COMPARATOR_BY_MODIFIED_DATE)
                .findFirst().orElse(null);
    }

    public static Comparator<PartyContact> CONTACT_COMPARATOR_BY_MODIFIED_DATE = new Comparator<PartyContact>() {
        @Override
        public int compare(PartyContact contact, PartyContact otherContact) {
            int result = contact.getLastModifiedDate().compareTo(otherContact.getLastModifiedDate());
            return (result == 0) ? DomainObjectUtil.COMPARATOR_BY_ID.compare(contact, otherContact) : result;
        }
    };

    @RequestMapping(value = "/district/{oid}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<DistrictSubdivisionBean> readDistrictSubdivisions(@PathVariable("oid") District district,
            Model model) {
        return district.getDistrictSubdivisionsSet().stream()
                .map(ds -> new DistrictSubdivisionBean(ds.getExternalId(), ds.getName())).collect(Collectors.toList());
    }

    @RequestMapping(value = "/districtSubdivision/{oid}", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody List<DistrictSubdivisionBean> readParish(@PathVariable("oid") DistrictSubdivision districtSubdivision,
            Model model) {
        return districtSubdivision.getParishSet().stream()
                .map(ds -> new DistrictSubdivisionBean(ds.getExternalId(), ds.getName())).collect(Collectors.toList());
    }

    public static class ResidenceInformationForm {

        private String address;

        private String areaCode; // zip code

        private String areaOfAreaCode; // location of zip code

        private String area; // location

        private Parish parishOfResidence;

        private District districtOfResidence;

        private DistrictSubdivision districtSubdivisionOfResidence;

        private Boolean dislocatedFromPermanentResidence;

        private District schoolTimeDistrictOfResidence;

        private DistrictSubdivision schoolTimeDistrictSubdivisionOfResidence;

        private String schoolTimeAddress;

        private String schoolTimeAreaCode;

        private String schoolTimeAreaOfAreaCode;

        private String schoolTimeArea;

        private Parish schoolTimeParishOfResidence;

        private Country countryOfResidence;

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

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public Parish getParishOfResidence() {
            return parishOfResidence;
        }

        public void setParishOfResidence(Parish parishOfResidence) {
            this.parishOfResidence = parishOfResidence;
        }

        public District getDistrictOfResidence() {
            return districtOfResidence;
        }

        public void setDistrictOfResidence(District districtOfResidence) {
            this.districtOfResidence = districtOfResidence;
        }

        public DistrictSubdivision getDistrictSubdivisionOfResidence() {
            return districtSubdivisionOfResidence;
        }

        public void setDistrictSubdivisionOfResidence(DistrictSubdivision districtSubdivisionOfResidence) {
            this.districtSubdivisionOfResidence = districtSubdivisionOfResidence;
        }

        public Boolean getDislocatedFromPermanentResidence() {
            return dislocatedFromPermanentResidence;
        }

        public void setDislocatedFromPermanentResidence(Boolean dislocatedFromPermanentResidence) {
            this.dislocatedFromPermanentResidence = dislocatedFromPermanentResidence;
        }

        public District getSchoolTimeDistrictOfResidence() {
            return schoolTimeDistrictOfResidence;
        }

        public void setSchoolTimeDistrictOfResidence(District schoolTimeDistrictOfResidence) {
            this.schoolTimeDistrictOfResidence = schoolTimeDistrictOfResidence;
        }

        public DistrictSubdivision getSchoolTimeDistrictSubdivisionOfResidence() {
            return schoolTimeDistrictSubdivisionOfResidence;
        }

        public void setSchoolTimeDistrictSubdivisionOfResidence(DistrictSubdivision schoolTimeDistrictSubdivisionOfResidence) {
            this.schoolTimeDistrictSubdivisionOfResidence = schoolTimeDistrictSubdivisionOfResidence;
        }

        public String getSchoolTimeAddress() {
            return schoolTimeAddress;
        }

        public void setSchoolTimeAddress(String schoolTimeAddress) {
            this.schoolTimeAddress = schoolTimeAddress;
        }

        public String getSchoolTimeAreaCode() {
            return schoolTimeAreaCode;
        }

        public void setSchoolTimeAreaCode(String schoolTimeAreaCode) {
            this.schoolTimeAreaCode = schoolTimeAreaCode;
        }

        public String getSchoolTimeAreaOfAreaCode() {
            return schoolTimeAreaOfAreaCode;
        }

        public void setSchoolTimeAreaOfAreaCode(String schoolTimeAreaOfAreaCode) {
            this.schoolTimeAreaOfAreaCode = schoolTimeAreaOfAreaCode;
        }

        public String getSchoolTimeArea() {
            return schoolTimeArea;
        }

        public void setSchoolTimeArea(String schoolTimeArea) {
            this.schoolTimeArea = schoolTimeArea;
        }

        public Parish getSchoolTimeParishOfResidence() {
            return schoolTimeParishOfResidence;
        }

        public void setSchoolTimeParishOfResidence(Parish schoolTimeParishOfResidence) {
            this.schoolTimeParishOfResidence = schoolTimeParishOfResidence;
        }

        public Country getCountryOfResidence() {
            return countryOfResidence;
        }

        public void setCountryOfResidence(Country countryOfResidence) {
            this.countryOfResidence = countryOfResidence;
        }

        private boolean isSchoolTimeAddressComplete() {
            return isSchoolTimeRequiredInformationAddressFilled()
                    && !isAnyEmpty(schoolTimeAddress, schoolTimeAreaCode, schoolTimeAreaOfAreaCode, schoolTimeArea)
                    && schoolTimeParishOfResidence != null;
        }

        private boolean isAnyEmpty(String... fields) {
            for (String each : fields) {
                if (StringUtils.isEmpty(each)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isSchoolTimeRequiredInformationAddressFilled() {
            return getSchoolTimeDistrictOfResidence() != null && getSchoolTimeDistrictSubdivisionOfResidence() != null;
        }

        private boolean isResidenceInformationFilled() {
            return !(getDistrictOfResidence() == null || getDistrictSubdivisionOfResidence() == null || parishOfResidence == null
                    || StringUtils.isEmpty(address) || StringUtils.isEmpty(areaCode) || StringUtils.isEmpty(areaOfAreaCode) || StringUtils
                        .isEmpty(area));
        }

        private boolean isAnySchoolTimeAddressInformationFilled() {
            return getSchoolTimeDistrictOfResidence() != null || getSchoolTimeDistrictSubdivisionOfResidence() != null
                    || isAnyFilled(schoolTimeAddress, schoolTimeAreaCode, schoolTimeAreaOfAreaCode, schoolTimeArea)
                    || schoolTimeParishOfResidence != null;
        }

        private boolean isAnyFilled(final String... fields) {
            for (final String each : fields) {
                if (!StringUtils.isEmpty(each)) {
                    return true;
                }
            }

            return false;
        }
    }
}
