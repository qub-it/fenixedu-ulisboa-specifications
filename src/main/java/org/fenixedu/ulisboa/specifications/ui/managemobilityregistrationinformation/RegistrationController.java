/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and ServiÃ§os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright Â© 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright Â© 2015 Universidade de Lisboa (after any Go-Live phase)
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
package org.fenixedu.ulisboa.specifications.ui.managemobilityregistrationinformation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.service.services.person.PersonSearcher;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Component("org.fenixedu.ulisboa.specifications.ui.manageMobilityRegistrationInformation")
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.manageMobilityRegistrationInformation", accessGroup = "logged")
@RequestMapping(RegistrationController.CONTROLLER_URL)
public class RegistrationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/managemobilityregistrationinformation/registration";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(value = "number", required = false) final Integer number,
            @RequestParam(value = "name", required = false) final String name, final Model model) {

        List<Registration> searchregistrationResultsDataSet = searchRegistrations(number, name);

        //add the results dataSet to the model
        model.addAttribute("searchregistrationResultsDataSet", searchregistrationResultsDataSet);
        return "fenixedu-ulisboa-specifications/managemobilityregistrationinformation/registration/search";
    }

    private List<Registration> searchRegistrations(Integer number, String name) {
        final Set<Registration> result = Sets.newHashSet();

        if (number != null) {
            result.addAll(Bennu.getInstance().getRegistrationsSet().stream()
                    .filter(registration -> number != null && number.equals(registration.getNumber()))
                    .collect(Collectors.toSet()));

            result.addAll(Bennu.getInstance().getStudentsSet().stream()
                    .filter(student -> number != null && number.equals(student.getNumber()))
                    .<Registration> flatMap(student -> student.getRegistrationsSet().stream()).collect(Collectors.toSet()));
        }

        if (!Strings.isNullOrEmpty(name)) {
            final PersonSearcher personSearcher = new PersonSearcher().name(name);
            result.addAll(personSearcher.search().filter(p -> p.getStudent() != null)
                    .<Registration> flatMap(p -> p.getStudent().getRegistrationsSet().stream()).collect(Collectors.toSet()));
        }

        return Lists.newArrayList(result);
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/search/view";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "/{oid}")
    public String processSearchToViewAction(@PathVariable("oid") final Registration registration, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(MobilityRegistrationInformationController.SEARCH_URL + "/" + registration.getExternalId(), model,
                redirectAttributes);
    }
}
