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
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.joda.time.YearMonthDay;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = PersonalInformationFormController.class)
@RequestMapping(FiliationFormController.CONTROLLER_URL)
public class FiliationFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/filiationform";

    private FiliationForm getFiliationForm(Model model) {
        return (FiliationForm) model.asMap().get("filiationForm");
    }

    private void setFiliationForm(FiliationForm filiationForm, Model model) {
        model.addAttribute("filiationForm", filiationForm);
    }

    @Atomic
    public void deleteFiliationForm(FiliationForm filiationForm) {
    }

    private static final String _FILLFILIATION_URI = "/fillfiliation";
    public static final String FILLFILIATION_URL = CONTROLLER_URL + _FILLFILIATION_URI;

    @RequestMapping(value = _FILLFILIATION_URI, method = RequestMethod.GET)
    public String fillfiliation(Model model) {

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/filiationform/fillfiliation";
    }

    @RequestMapping(value = _FILLFILIATION_URI, method = RequestMethod.POST)
    public String fillfiliation(FiliationForm filiationForm, Model model, RedirectAttributes redirectAttributes) {

        try {

            model.addAttribute("filiationForm", filiationForm);
            return redirect(
                    "/fenixedu-ulisboa-specifications/firsttimecandidacy/householdinformationform/fillhouseholdinformation/",
                    model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillfiliation(model);
        }
    }

    public static class FiliationForm {

        private YearMonthDay dateOfBirth;

        private Country nationality;

        private String parishOfBirth;

        private String districtSubdivisionOfBirth;

        private String districtOfBirth;

        private String fatherName;

        private String motherName;

        private Country countryOfBirth;

        public YearMonthDay getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(YearMonthDay dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public Country getNationality() {
            return nationality;
        }

        public void setNationality(Country nationality) {
            this.nationality = nationality;
        }

        public String getParishOfBirth() {
            return parishOfBirth;
        }

        public void setParishOfBirth(String parishOfBirth) {
            this.parishOfBirth = parishOfBirth;
        }

        public String getDistrictSubdivisionOfBirth() {
            return districtSubdivisionOfBirth;
        }

        public void setDistrictSubdivisionOfBirth(String districtSubdivisionOfBirth) {
            this.districtSubdivisionOfBirth = districtSubdivisionOfBirth;
        }

        public String getDistrictOfBirth() {
            return districtOfBirth;
        }

        public void setDistrictOfBirth(String districtOfBirth) {
            this.districtOfBirth = districtOfBirth;
        }

        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public String getMotherName() {
            return motherName;
        }

        public void setMotherName(String motherName) {
            this.motherName = motherName;
        }

        public Country getCountryOfBirth() {
            return countryOfBirth;
        }

        public void setCountryOfBirth(Country countryOfBirth) {
            this.countryOfBirth = countryOfBirth;
        }
    }
}
