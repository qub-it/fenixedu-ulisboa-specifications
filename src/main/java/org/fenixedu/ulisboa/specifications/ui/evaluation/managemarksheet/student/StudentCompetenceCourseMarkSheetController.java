/**
 * This file was created on 1 October, 2018
 * 
 * - Fábio Ferreira - Núcleo de Desenvolvimento de Software da Reitoria da Universidade de Lisboa (desenvolvimento.di@reitoria.ulisboa.pt)
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
package org.fenixedu.ulisboa.specifications.ui.evaluation.managemarksheet.student;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.academic.ui.struts.action.student.CurriculumDispatchActionForStudent;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.bennu.portal.servlet.BennuPortalDispatcher;
import org.fenixedu.bennu.struts.portal.RenderersAnnotationProcessor;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.CompetenceCourseMarkSheetBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component("org.fenixedu.ulisboa.specifications.evaluation.manageMarkSheet.student")
@RequestMapping(StudentCompetenceCourseMarkSheetController.CONTROLLER_URL)
public class StudentCompetenceCourseMarkSheetController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/evaluation/managemarksheet/student/competencecoursemarksheet";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private void setCompetenceCourseMarkSheetBean(final CompetenceCourseMarkSheetBean bean, final Model model) {
        model.addAttribute("competenceCourseMarkSheetBean", bean);
    }

    private void setCompetenceCourseMarkSheet(final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model) {
        model.addAttribute("competenceCourseMarkSheet", competenceCourseMarkSheet);
    }

    @ModelAttribute
    private void setFunctionalityContext(final Model model, final HttpServletRequest request) {

        final Functionality functionality =
                RenderersAnnotationProcessor.getFunctionalityForType(CurriculumDispatchActionForStudent.class);
        final MenuFunctionality menuItem =
                MenuFunctionality.findFunctionality(functionality.getProvider(), functionality.getKey());
        if (menuItem == null || !menuItem.isAvailableForCurrentUser()) {
            throw AuthorizationException.unauthorized();
        }

        BennuPortalDispatcher.selectFunctionality(request, menuItem);
    }

    private static final String _READGRADES_URI = "/readgrades/";
    public static final String READGRADES_URL = CONTROLLER_URL + _READGRADES_URI;

    @RequestMapping(value = _READGRADES_URI + "{oid}", method = RequestMethod.GET)
    public String readGrades(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet);

        this.setCompetenceCourseMarkSheetBean(bean, model);

        if (!bean.hasStudent(Authenticate.getUser().getPerson().getStudent())) {
            addErrorMessage(
                    ULisboaSpecificationsUtil.bundle("error.StudentCompetenceCourseMarkSheetController.student.not.authorized"),
                    model);
        }

        return jspPage("readgrades");
    }

}
