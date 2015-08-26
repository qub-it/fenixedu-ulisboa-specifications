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

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleValidationType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/showselectedcourses")
public class ShowSelectedCoursesController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String showselectedcourses(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }

        Registration registration = FirstTimeCandidacyController.getCandidacy().getRegistration();
        if (registrationRequiresManualCourseEnrolment(registration)
                && !checkCourseEnrolments(registration, ExecutionYear.readCurrentExecutionYear())) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "label.firstTimeCandidacy.error.invalidNumberOfCourseEnrolments"), model);
            return new ChooseOptionalCoursesController().chooseoptionalcourses(model, redirectAttributes);
        }
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/showselectedcourses";
    }

    private boolean registrationRequiresManualCourseEnrolment(Registration registration) {
        return registration.getDegree().getFirstYearRegistrationConfiguration() != null
                && registration.getDegree().getFirstYearRegistrationConfiguration().getRequiresCoursesEnrolment();
    }

    @RequestMapping(value = "/continue")
    public String showselectedcoursesToContinue(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/scheduleclasses", model, redirectAttributes);
    }

    private boolean checkCourseEnrolments(Registration registration, ExecutionYear currentExecutionYear) {
        //Compare the number of semesters for which the student has enrolments with the number os expected semesters (1 ou 2 depending if is yearly)
        int numberOfSemestersInWhichHasEnrolments =
                registration.getEnrolments(currentExecutionYear).stream().map(en -> en.getExecutionPeriod()).distinct()
                        .collect(Collectors.toList()).size();
        int expectedNumberOfSemestersToHaveEnrolments =
                registration.getStudentCurricularPlan(currentExecutionYear).getDegreeCurricularPlan()
                        .getCurricularRuleValidationType() == CurricularRuleValidationType.YEAR ? 2 : 1;
        return expectedNumberOfSemestersToHaveEnrolments == numberOfSemestersInWhichHasEnrolments;
    }
}
