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

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(CgdDataAuthorizationController.CONTROLLER_URL)
public class CgdDataAuthorizationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization";

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(Model model, RedirectAttributes redirectAttributes) {
        return redirect(ShowTuitionController.CONTROLLER_URL, model, redirectAttributes);
    }

    @RequestMapping
    public String cgddataauthorization(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization";
    }

    @RequestMapping(value = "/authorize")
    public String cgddataauthorizationToAuthorize(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        authorizeSharingDataWithCGD(true);
        Registration registration = FirstTimeCandidacyController.getCandidacy().getRegistration();
        boolean wsCallSuccess = StudentAccessServices.triggerSyncRegistrationToExternal(registration);
        if (wsCallSuccess) {
            return redirect(DocumentsPrintController.CONTROLLER_URL, model, redirectAttributes);
        } else {
            return redirect(DocumentsPrintController.WITH_MODEL43_URL, model, redirectAttributes);
        }
    }

    @RequestMapping(value = "/unauthorize")
    public String cgddataauthorizationToUnauthorize(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        authorizeSharingDataWithCGD(false);
        return redirect(DocumentsPrintController.WITH_MODEL43_URL, model, redirectAttributes);
    }

    @Atomic
    private void authorizeSharingDataWithCGD(boolean authorize) {
        PersonUlisboaSpecifications.findOrCreate(AccessControl.getPerson()).setAuthorizeSharingDataWithCGD(authorize);
    }
}
