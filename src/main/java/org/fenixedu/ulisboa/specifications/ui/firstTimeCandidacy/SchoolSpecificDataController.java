/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: joao.roxo@qub-it.com 
 *               nuno.pinheiro@qub-it.com
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

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@Component("org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.SchoolSpecificDataController")
@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(SchoolSpecificDataController.CONTROLLER_URL)
public class SchoolSpecificDataController extends FenixeduUlisboaSpecificationsBaseController {

    private static final String VACCINATION_VALIDITY = "vaccinationValidity";

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/OLD/firsttimecandidacy/schoolspecificdata";

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(Model model, RedirectAttributes redirectAttributes) {
        return redirect(MotivationsExpectationsFormController.FILLMOTIVATIONSEXPECTATIONS_URL, model, redirectAttributes);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }

        if (shouldBeSkipped()) {
            return redirect(ChooseOptionalCoursesController.CONTROLLER_URL, model, redirectAttributes);
        }

        fillFormIfRequired(model);
        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.createSchoolSpecificData.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/schoolspecificdata/create";
    }

    public static boolean shouldBeSkipped() {
        Degree degree = FirstTimeCandidacyController.getCandidacy().getDegreeCurricularPlan().getDegree();
//        return degree.getFirstYearRegistrationConfiguration() == null
//                || !degree.getFirstYearRegistrationConfiguration().getRequiresVaccination();
        return true;
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
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        try {
            if (vaccinationValidity == null) {
                throw new RuntimeException(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "label.error.fillVaccionatioValidity"));
            }
            createSchoolSpecificData(vaccinationValidity);
            return redirect(ChooseOptionalCoursesController.CONTROLLER_URL, model, redirectAttributes);
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
