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

import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.PreviousDegreeOriginInformationFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = BlueRecordEntryPoint.class)
@RequestMapping(PreviousDegreeOriginInformationFormControllerBlueRecord.CONTROLLER_URL)
public class PreviousDegreeOriginInformationFormControllerBlueRecord extends PreviousDegreeOriginInformationFormController {
    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/previousdegreeorigininformationform";

    @Override
    protected String nextScreen(Model model, RedirectAttributes redirectAttributes) {
        return redirect(
                DisabilitiesFormControllerBlueRecord.CONTROLLER_URL + DisabilitiesFormControllerBlueRecord._FILLDISABILITIES_URI,
                model, redirectAttributes);
    }

    @Override
    public Optional<String> accessControlRedirect(final Model model, final RedirectAttributes redirectAttributes) {
        return Optional.empty();
    }

    @Override
    public String back(final Model model, final RedirectAttributes redirectAttributes) {
        return redirect(OriginInformationFormControllerBlueRecord.INVOKE_BACK_URL, model, redirectAttributes);
    }

    private static final String _INVOKE_BACK_URI = "/invokeback";
    public static final String INVOKE_BACK_URL = CONTROLLER_URL + _INVOKE_BACK_URI;

    @RequestMapping(value = _INVOKE_BACK_URI, method = RequestMethod.GET)
    public String invokeBack(final Model model, final RedirectAttributes redirectAttributes) {
        if (isFormIsFilled(model)) {
            return back(model, redirectAttributes);
        }

        return redirect(PreviousDegreeOriginInformationFormControllerBlueRecord.CONTROLLER_URL, model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }
    
    @Override
    public boolean isFormIsFilled(final Student student) {
        return findPreviousDegreePrecedentDegreeInformationsToFill(student).isEmpty();
    }
    
    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }
}
