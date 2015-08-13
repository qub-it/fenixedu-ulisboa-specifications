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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(FiliationFormController.CONTROLLER_URL)
public class FiliationFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/filiationform";

    private static final String _FILLFILIATION_URI = "/fillfiliation";
    public static final String FILLFILIATION_URL = CONTROLLER_URL + _FILLFILIATION_URI;

    @RequestMapping(value = _FILLFILIATION_URI, method = RequestMethod.GET)
    public String fillfiliation(Model model) {
        model.addAttribute("countries_options", Bennu.getInstance().getCountrysSet());
        model.addAttribute("districts_options", Bennu.getInstance().getDistrictsSet());
        fillFormIfRequired(model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/filiationform/fillfiliation";
    }

    private void fillFormIfRequired(Model model) {
        if (!model.containsAttribute("filiationForm")) {
            FiliationForm form = new FiliationForm();
            Person person = AccessControl.getPerson();
            form.setFatherName(person.getNameOfFather());
            form.setMotherName(person.getNameOfMother());

            District district = District.readByName(person.getDistrictOfBirth());
            if (district != null) {
                form.setDistrictOfBirth(district);
                form.setDistrictSubdivisionOfBirth(district.getDistrictSubdivisionByName(person.getDistrictSubdivisionOfBirth()));
            }
            form.setParishOfBirth(person.getParishOfBirth());
            form.setDateOfBirth(person.getDateOfBirthYearMonthDay().toLocalDate());
            form.setNationality(person.getCountry());
            if (form.getNationality() == null) {
                form.setNationality(Country.readDefault());
            }
            form.setCountryOfBirth(person.getCountryOfBirth());
            if (form.getCountryOfBirth() == null) {
                form.setCountryOfBirth(Country.readDefault());
            }

            model.addAttribute("filiationForm", form);
        }
    }

    @RequestMapping(value = _FILLFILIATION_URI, method = RequestMethod.POST)
    public String fillfiliation(FiliationForm form, Model model, RedirectAttributes redirectAttributes) {
        if (!validate(form, model)) {
            return fillfiliation(model);
        }

        try {
            writeFiliationData(form);
            model.addAttribute("filiationForm", form);
            return redirect(
                    "/fenixedu-ulisboa-specifications/firsttimecandidacy/householdinformationform/fillhouseholdinformation/",
                    model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillfiliation(model);
        }
    }

    private boolean validate(FiliationForm form, Model model) {
        if (form.getDateOfBirth() == null) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.birthDate.required"),
                    model);
            return false;
        }

        if (form.getCountryOfBirth().isDefaultCountry()) {
            if (form.getDistrictOfBirth() == null || form.getDistrictSubdivisionOfBirth() == null
                    || StringUtils.isEmpty(form.getParishOfBirth())) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.FiliationForm.zone.information.is.required.for.national.students"), model);
                return false;
            }
        }
        return true;
    }

    @Atomic
    private void writeFiliationData(FiliationForm form) {
        Person person = AccessControl.getPerson();
        person.setNameOfFather(form.getFatherName());
        person.setNameOfMother(form.getMotherName());

        person.setDistrictOfBirth(form.getDistrictOfBirth().getName());
        person.setDistrictSubdivisionOfBirth(form.getDistrictSubdivisionOfBirth().getName());
        person.setParishOfBirth(form.getParishOfBirth());

        person.setDateOfBirthYearMonthDay(new YearMonthDay(form.getDateOfBirth()));
        person.setCountry(form.getNationality());
        person.setCountryOfBirth(form.getCountryOfBirth());
    }

    @RequestMapping(value = "/district/{oid}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<DistrictSubdivisionBean> readDistrictSubdivisions(@PathVariable("oid") District district,
            Model model) {
        return district.getDistrictSubdivisionsSet().stream()
                .map(ds -> new DistrictSubdivisionBean(ds.getExternalId(), ds.getName())).collect(Collectors.toList());
    }

    public static class FiliationForm {

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateOfBirth;

        private Country nationality;

        private String parishOfBirth;

        private DistrictSubdivision districtSubdivisionOfBirth;

        private District districtOfBirth;

        private String fatherName;

        private String motherName;

        private Country countryOfBirth;

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(LocalDate dateOfBirth) {
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

        public DistrictSubdivision getDistrictSubdivisionOfBirth() {
            return districtSubdivisionOfBirth;
        }

        public void setDistrictSubdivisionOfBirth(DistrictSubdivision districtSubdivisionOfBirth) {
            this.districtSubdivisionOfBirth = districtSubdivisionOfBirth;
        }

        public District getDistrictOfBirth() {
            return districtOfBirth;
        }

        public void setDistrictOfBirth(District districtOfBirth) {
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

    public static class DistrictSubdivisionBean {
        String id;

        String text;

        public DistrictSubdivisionBean(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
