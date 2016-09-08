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

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(ShowScheduledClassesController.CONTROLLER_URL)
public class ShowScheduledClassesController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/showscheduledclasses";

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(Model model, RedirectAttributes redirectAttributes) {
        if (!ScheduleClassesController.shouldBeSkipped()) {
            return redirect(ScheduleClassesController.CONTROLLER_URL, model, redirectAttributes);
        } else {
            return redirect(ShowSelectedCoursesController.CONTROLLER_URL, model, redirectAttributes);
        }
    }

    @RequestMapping
    public String showscheduledclasses(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }

        Registration registration = FirstTimeCandidacyController.getCandidacy().getRegistration();
        if (registrationRequiresManualClassEnrolment(registration)
                && !checkClassEnrolments(registration, ExecutionYear.readCurrentExecutionYear())) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "label.firstTimeCandidacy.error.invalidNumberOfShiftsEnrolments"), model);
            return new ScheduleClassesController().scheduleclasses(model, redirectAttributes);
        }
        if (shouldBeSkipped()) {
            return showscheduledclassesToContinue(model, redirectAttributes);
        }

        // Add attributes to enable ajax schedule reading
        // For now we will only present the schedule for the current semester, we may be required in the future to present it for both semesters
        model.addAttribute("registration", registration);
        model.addAttribute("semester", ExecutionSemester.readActualExecutionSemester());

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.showScheduledClasses.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/showscheduledclasses";
    }

    @RequestMapping(value = "/continue")
    public String showscheduledclassesToContinue(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        return redirect(ShowTuitionController.CONTROLLER_URL, model, redirectAttributes);
    }

    private boolean registrationRequiresManualClassEnrolment(Registration registration) {
//        FirstYearRegistrationConfiguration firstYearRegistrationConfiguration =
//                registration.getDegree().getFirstYearRegistrationConfiguration();
//        return firstYearRegistrationConfiguration != null && (firstYearRegistrationConfiguration.getRequiresClassesEnrolment()
//                || firstYearRegistrationConfiguration.getRequiresShiftsEnrolment());
        return false;
    }

    private boolean checkClassEnrolments(Registration registration, ExecutionYear currentExecutionYear) {
        //Compare the number of semesters for which the student has enrolments with the number os expected semesters (1 ou 2 depending if it has opened periods)
        Set<ExecutionSemester> executionPeriodsSet = currentExecutionYear.getExecutionPeriodsSet();
        int numberOfExpectedSemestersToBeEnroled = registration.getStudentCurricularPlan(currentExecutionYear)
                .getDegreeCurricularPlan().getAcademicEnrolmentPeriodsSet().stream()
                .filter(ep -> executionPeriodsSet.contains(ep.getExecutionSemester()) && ep.getFirstTimeRegistration())
                .collect(Collectors.toSet()).size();
        int numberOfEnroledSemesters = registration.getShiftsSet().stream().map(shift -> shift.getExecutionPeriod())
                .filter(ep -> executionPeriodsSet.contains(ep)).collect(Collectors.toSet()).size();
        return numberOfEnroledSemesters == numberOfExpectedSemestersToBeEnroled;
    }

    public static boolean shouldBeSkipped() {
        if (isManualEnrolments() || currentPersonHasEnrolments()) {
            return false;
        }
        return true;
    }

    public static boolean isManualEnrolments() {
        Degree degree = FirstTimeCandidacyController.getCandidacy().getDegreeCurricularPlan().getDegree();
//        FirstYearRegistrationConfiguration registrationConfiguration = degree.getFirstYearRegistrationConfiguration();
//        return registrationConfiguration != null && (degree.getFirstYearRegistrationConfiguration().getRequiresClassesEnrolment()
//                || degree.getFirstYearRegistrationConfiguration().getRequiresShiftsEnrolment());
        return false;
    }

    public static boolean currentPersonHasEnrolments() {
        Registration registration = FirstTimeCandidacyController.getCandidacy().getRegistration();
        return !registration.getShiftsForCurrentExecutionPeriod().isEmpty();
    }
}
