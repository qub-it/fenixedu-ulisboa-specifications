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

import java.util.Optional;
import java.util.function.Predicate;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.DgesStudentImportationProcess;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping(SchoolSpecificDataController.CONTROLLER_URL)
public class SchoolSpecificDataController extends FenixeduUlisboaSpecificationsBaseController {

    private static final String VACCINATION_VALIDITY = "vaccinationValidity";

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/schoolspecificdata";

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(Model model, RedirectAttributes redirectAttributes) {
        Predicate<? super Registration> hasDgesImportationProcessForCurrentYear =
                DgesStudentImportationProcess.registrationHasDgesImportationProcessForCurrentYear();
        Optional<Registration> findAny =
                AccessControl.getPerson().getStudent().getRegistrationsSet().stream()
                        .filter(hasDgesImportationProcessForCurrentYear).findAny();
        if (findAny.isPresent()) {
            Registration registration = findAny.get();
            Degree degree = registration.getDegree();
            //TODO refactor code : School specific data could be more than vaccination but
            //since vaccination is the only "school specific data" for now, this is complies with the requirements
            if (degree.getFirstYearRegistrationConfiguration() == null
                    || !degree.getFirstYearRegistrationConfiguration().getRequiresVaccination()) {
                //School does not require specific data
                return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/chooseoptionalcourses/", model,
                        redirectAttributes);
            }

        } else {
            //This should never happen, but strange things happen
            throw new RuntimeException("Functionality only provided for candidates with current dges process");
        }
        fillFormIfRequired(model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/schoolspecificdata/create";
    }

    private void fillFormIfRequired(Model model) {
        Person person = AccessControl.getPerson();
        if (!model.containsAttribute(VACCINATION_VALIDITY) && person.getPersonUlisboaSpecifications() != null) {
            model.addAttribute(VACCINATION_VALIDITY, person.getPersonUlisboaSpecifications().getVaccinationValidity());
        }
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(
            @RequestParam(value = VACCINATION_VALIDITY) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate vaccinationValidity,
            Model model, RedirectAttributes redirectAttributes) {
        try {
            if (vaccinationValidity == null) {
                throw new RuntimeException(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "label.error.fillVaccionatioValidity"));
            }
            createSchoolSpecificData(vaccinationValidity);
            return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/chooseoptionalcourses/", model,
                    redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return create(model, redirectAttributes);
        }
    }

    @Atomic
    public void createSchoolSpecificData(LocalDate vaccinationValidity) {
        PersonUlisboaSpecifications.findOrCreate(AccessControl.getPerson()).setVaccinationValidity(vaccinationValidity);
    }
}
