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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.organizationalStructure.AcademicalInstitutionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitName;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.UnitBean;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping(OriginInformationFormController.CONTROLLER_URL)
public class OriginInformationFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/origininformationform";

    private static final String _FILLORIGININFORMATION_URI = "/fillorigininformation";
    public static final String FILLORIGININFORMATION_URL = CONTROLLER_URL + _FILLORIGININFORMATION_URI;

    @RequestMapping(value = _FILLORIGININFORMATION_URI, method = RequestMethod.GET)
    public String fillorigininformation(Model model) {
        model.addAttribute("schoolLevelValues", SchoolLevelType.values());
        model.addAttribute("highSchoolTypeValues", AcademicalInstitutionType.values());
        model.addAttribute("countries", Bennu.getInstance().getCountrysSet());
        fillFormIfRequired(model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/origininformationform/fillorigininformation";
    }

    private void fillFormIfRequired(Model model) {
        if (!model.containsAttribute("originInformationForm")) {
            OriginInformationForm originInformationForm = new OriginInformationForm();
            originInformationForm.setCountryWhereFinishedPreviousCompleteDegree(Country.readDefault());

            Optional<String> conclusionGrade =
                    AccessControl.getPerson().getCandidaciesSet().stream().filter(c -> c instanceof StudentCandidacy)
                            .map(StudentCandidacy.class::cast).map(sc -> sc.getPrecedentDegreeInformation().getConclusionGrade())
                            .filter(grade -> grade != null).findFirst();
            if (conclusionGrade.isPresent()) {
                originInformationForm.setConclusionGrade(conclusionGrade.get());
            }

            model.addAttribute("originInformationForm", originInformationForm);
        }
    }

    @RequestMapping(value = _FILLORIGININFORMATION_URI, method = RequestMethod.POST)
    public String fillorigininformation(OriginInformationForm originInformationForm, Model model,
            RedirectAttributes redirectAttributes) {

        try {

            model.addAttribute("originInformationForm", originInformationForm);
            return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/schoolspecificdata/create/", model,
                    redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillorigininformation(model);
        }
    }

    @RequestMapping(value = "/raidesUnit", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<UnitBean> readRaidesUnits(@RequestParam("namePart") String namePart, Model model) {
        List<UnitBean> collect =
                UnitName.findExternalAcademicUnit(namePart, 50).stream()
                        .map(un -> new UnitBean(un.getUnit().getExternalId(), un.getUnit().getName()))
                        .collect(Collectors.toList());
        return collect;
    }

    //Adds a false unit with OID=Name to enable the user adding new units
    @RequestMapping(value = "/externalUnit", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<UnitBean> readExternalUnits(@RequestParam("namePart") String namePart, Model model) {
        List<UnitBean> collect =
                UnitName.findExternalUnit(namePart, 50).stream()
                        .map(un -> new UnitBean(un.getUnit().getExternalId(), un.getUnit().getName()))
                        .collect(Collectors.toList());
        collect.add(0, new UnitBean(namePart, namePart));
        return collect;
    }

    @RequestMapping(value = "/degreeDesignation/{unit}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody Collection<DegreeDesignationBean> readExternalUnits(@PathVariable("unit") String unitOid,
            @RequestParam("namePart") String namePart, Model model) {
        Unit unit = null;
        try {
            unit = FenixFramework.getDomainObject(unitOid);
        } catch (Exception e) {
            //Not a unit, so it is a custom value, ignore
        }

        Collection<DegreeDesignation> possibleDesignations;
        if (unit == null) {
            possibleDesignations = Bennu.getInstance().getDegreeDesignationsSet();
        } else {
            possibleDesignations = unit.getDegreeDesignationSet();
        }
        return possibleDesignations.stream()
                .filter(dd -> StringNormalizer.normalize(dd.getDescription()).contains(StringNormalizer.normalize(namePart)))
                .map(dd -> new DegreeDesignationBean(dd.getDescription(), dd.getExternalId())).limit(50)
                .collect(Collectors.toList());
    }

    public static class DegreeDesignationBean {
        private final String degreeDesignationText;
        private final String degreeDesignationId;

        public DegreeDesignationBean(String degreeDesignationText, String degreeDesignationId) {
            super();
            this.degreeDesignationText = degreeDesignationText;
            this.degreeDesignationId = degreeDesignationId;
        }

        public String getDegreeDesignationText() {
            return degreeDesignationText;
        }

        public String getDegreeDesignationId() {
            return degreeDesignationId;
        }

    }

    public static class OriginInformationForm {

        private static final long serialVersionUID = 1L;

        private SchoolLevelType schoolLevel;

        private String otherSchoolLevel;

        private String conclusionGrade;

        private String degreeDesignation;

        private Integer conclusionYear;

        private String institutionOid;

        private DegreeDesignation raidesDegreeDesignation;

        private Country countryWhereFinishedPreviousCompleteDegree;

        private AcademicalInstitutionType highSchoolType;

        public SchoolLevelType getSchoolLevel() {
            return schoolLevel;
        }

        public void setSchoolLevel(SchoolLevelType schoolLevel) {
            this.schoolLevel = schoolLevel;
        }

        public String getOtherSchoolLevel() {
            return otherSchoolLevel;
        }

        public void setOtherSchoolLevel(String otherSchoolLevel) {
            this.otherSchoolLevel = otherSchoolLevel;
        }

        public String getConclusionGrade() {
            return conclusionGrade;
        }

        public void setConclusionGrade(String conclusionGrade) {
            this.conclusionGrade = conclusionGrade;
        }

        public String getDegreeDesignation() {
            return degreeDesignation;
        }

        public void setDegreeDesignation(String degreeDesignation) {
            this.degreeDesignation = degreeDesignation;
        }

        public Integer getConclusionYear() {
            return conclusionYear;
        }

        public void setConclusionYear(Integer conclusionYear) {
            this.conclusionYear = conclusionYear;
        }

        public String getInstitutionOid() {
            return institutionOid;
        }

        public void setInstitutionOid(String institutionOid) {
            this.institutionOid = institutionOid;
        }

        public DegreeDesignation getRaidesDegreeDesignation() {
            return raidesDegreeDesignation;
        }

        public void setRaidesDegreeDesignation(DegreeDesignation raidesDegreeDesignation) {
            this.raidesDegreeDesignation = raidesDegreeDesignation;
        }

        public Country getCountryWhereFinishedPreviousCompleteDegree() {
            return countryWhereFinishedPreviousCompleteDegree;
        }

        public void setCountryWhereFinishedPreviousCompleteDegree(Country countryWhereFinishedPreviousCompleteDegree) {
            this.countryWhereFinishedPreviousCompleteDegree = countryWhereFinishedPreviousCompleteDegree;
        }

        public AcademicalInstitutionType getHighSchoolType() {
            return highSchoolType;
        }

        public void setHighSchoolType(AcademicalInstitutionType highSchoolType) {
            this.highSchoolType = highSchoolType;
        }

        public static long getSerialversionuid() {
            return serialVersionUID;
        }

    }
}
