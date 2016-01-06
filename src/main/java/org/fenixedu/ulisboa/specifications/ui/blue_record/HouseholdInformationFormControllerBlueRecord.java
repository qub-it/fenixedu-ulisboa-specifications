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
package org.fenixedu.ulisboa.specifications.ui.blue_record;

import java.util.Optional;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.HouseholdInformationFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.HouseholdInformationFormController.HouseholdInformationForm;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = BlueRecordEntryPoint.class)
@RequestMapping(HouseholdInformationFormControllerBlueRecord.CONTROLLER_URL)
public class HouseholdInformationFormControllerBlueRecord extends HouseholdInformationFormController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/householdinformationform";

    @Override
    protected String nextScreen(Model model, RedirectAttributes redirectAttributes) {
        return redirect(OriginInformationFormControllerBlueRecord.CONTROLLER_URL
                + OriginInformationFormControllerBlueRecord._FILLORIGININFORMATION_URI, model, redirectAttributes);
    }

    @Override
    public Optional<String> accessControlRedirect(Model model, RedirectAttributes redirectAttributes) {
        return Optional.empty();
    }

    @Override
    public String back(Model model, RedirectAttributes redirectAttributes) {
        return redirect(PersonalInformationFormControllerBlueRecord.CONTROLLER_URL, model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    public String fillhouseholdinformation(Model model, RedirectAttributes redirectAttributes) {
        // First check if is filled
        /*
        if (validateHouseholdInformationForm(createHouseholdInformationForm()).isEmpty()) {
            return "forward:" + OriginInformationFormControllerBlueRecord.CONTROLLER_URL
                    + OriginInformationFormControllerBlueRecord._FILLORIGININFORMATION_URI;
        }
        */

        return super.fillhouseholdinformation(model, redirectAttributes);
    }

}
