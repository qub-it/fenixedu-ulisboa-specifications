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

import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.DgesStudentImportationProcess;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/chooseoptionalcourses")
public class ChooseOptionalCoursesController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String chooseoptionalcourses(Model model, RedirectAttributes redirectAttributes) {
        Predicate<? super Registration> hasDgesImportationProcessForCurrentYear =
                DgesStudentImportationProcess.registrationHasDgesImportationProcessForCurrentYear();
        Optional<Registration> findAny =
                AccessControl.getPerson().getStudent().getRegistrationsSet().stream()
                        .filter(hasDgesImportationProcessForCurrentYear).findAny();
        if (findAny.isPresent()) {
            Registration registration = findAny.get();

            Degree degree = registration.getDegree();
            if (degree.getFirstYearRegistrationConfiguration() == null
                    || !degree.getFirstYearRegistrationConfiguration().getRequiresCoursesEnrolment()) {
                //School does not require first year course enrolment
                return chooseoptionalcoursesToContinue(model, redirectAttributes);
            }
        } else {
            //This should never happen, but strange things happen
            throw new RuntimeException("Functionality only provided for candidates with current dges process");
        }
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/chooseoptionalcourses";
    }

    @RequestMapping(value = "/opencourseenrollments")
    public String chooseoptionalcoursesToOpenCourseEnrollments(Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        ExecutionSemester executionSemester = ExecutionSemester.readActualExecutionSemester();

        Predicate<? super Registration> hasDgesImportationProcessForCurrentYear =
                DgesStudentImportationProcess.registrationHasDgesImportationProcessForCurrentYear();
        Optional<Registration> findAny =
                AccessControl.getPerson().getStudent().getRegistrationsSet().stream()
                        .filter(hasDgesImportationProcessForCurrentYear).findAny();
        if (findAny.isPresent()) {
            Registration registration = findAny.get();
            String link = "/student/bolonhaStudentEnrollment.do?method=prepare&executionSemesterID=%s&registrationOid=%s";
            String format = String.format(link, executionSemester.getExternalId(), registration.getExternalId());

            //request
            String injectChecksumInUrl =
                    GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), format, request.getSession());
            return redirect(injectChecksumInUrl, model, redirectAttributes);
        } else {
            //This should never happen, but strange things happen
            throw new RuntimeException("Functionality only provided for candidates with current dges process");
        }
    }

    @RequestMapping(value = "/continue")
    public String chooseoptionalcoursesToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/showselectedcourses", model, redirectAttributes);
    }
}
