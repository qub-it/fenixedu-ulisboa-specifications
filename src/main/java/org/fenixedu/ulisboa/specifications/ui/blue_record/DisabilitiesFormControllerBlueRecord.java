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

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.DisabilitiesForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.DisabilitiesFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = BlueRecordEntryPoint.class)
@RequestMapping(DisabilitiesFormControllerBlueRecord.CONTROLLER_URL)
public class DisabilitiesFormControllerBlueRecord extends DisabilitiesFormController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/{executionYearId}/disabilitiesform";

    @Override
    public Optional<String> accessControlRedirect(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return Optional.empty();
    }

    @Override
    public String backScreen(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(
                urlWithExecutionYear(PreviousDegreeOriginInformationFormControllerBlueRecord.INVOKE_BACK_URL, executionYear),
                model, redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        final String url = MotivationsExpectationsFormControllerBlueRecord.CONTROLLER_URL
                + MotivationsExpectationsFormControllerBlueRecord._FILL_URI;
        return redirect(urlWithExecutionYear(url, executionYear), model, redirectAttributes);
    }

    private static final String _INVOKE_BACK_URI = "/invokeback";
    public static final String INVOKE_BACK_URL = CONTROLLER_URL + _INVOKE_BACK_URI;

    @RequestMapping(value = _INVOKE_BACK_URI, method = RequestMethod.GET)
    public String invokeBack(final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (isFormIsFilled(executionYear, model)) {
            return back(executionYear, model, redirectAttributes);
        }

        return redirect(urlWithExecutionYear(CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        final DisabilitiesForm form = createDisabilitiesForm(executionYear, student);

        if (!form.isFirstYearRegistration()) {
            return true;
        }

        return form.isAnswered();
    }

}
