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

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = PersonalInformationFormController.class)
@RequestMapping(ResidenceInformationFormController.CONTROLLER_URL)
public class ResidenceInformationFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/residenceinformationform";

    private static final String _FILLRESIDENCEINFORMATION_URI = "/fillresidenceinformation";
    public static final String FILLRESIDENCEINFORMATION_URL = CONTROLLER_URL + _FILLRESIDENCEINFORMATION_URI;

    @RequestMapping(value = _FILLRESIDENCEINFORMATION_URI, method = RequestMethod.GET)
    public String fillresidenceinformation(Model model) {

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/residenceinformationform/fillresidenceinformation";
    }

    @RequestMapping(value = _FILLRESIDENCEINFORMATION_URI, method = RequestMethod.POST)
    public String fillresidenceinformation(ResidenceInformationForm residenceInformationForm, Model model,
            RedirectAttributes redirectAttributes) {

        try {

            model.addAttribute("residenceInformationForm", residenceInformationForm);
            return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/contactsform/fillcontacts/", model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillresidenceinformation(model);
        }
    }

    public static class ResidenceInformationForm {

        private String address;

        private String areaCode; // zip code

        private String areaOfAreaCode; // location of zip code

        private String area; // location

        private String parishOfResidence;

        private District districtOfResidence;

        private DistrictSubdivision districtSubdivisionOfResidence;

        private Boolean dislocatedFromPermanentResidence;

        private District schoolTimeDistrictOfResidence;

        private DistrictSubdivision schoolTimeDistrictSubdivisionOfResidence;

        private String schoolTimeAddress;

        private String schoolTimeAreaCode;

        private String schoolTimeAreaOfAreaCode;

        private String schoolTimeArea;

        private String schoolTimeParishOfResidence;

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

        public String getParishOfResidence() {
            return parishOfResidence;
        }

        public void setParishOfResidence(String parishOfResidence) {
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

        public String getSchoolTimeParishOfResidence() {
            return schoolTimeParishOfResidence;
        }

        public void setSchoolTimeParishOfResidence(String schoolTimeParishOfResidence) {
            this.schoolTimeParishOfResidence = schoolTimeParishOfResidence;
        }

        public Country getCountryOfResidence() {
            return countryOfResidence;
        }

        public void setCountryOfResidence(Country countryOfResidence) {
            this.countryOfResidence = countryOfResidence;
        }

    }
}
