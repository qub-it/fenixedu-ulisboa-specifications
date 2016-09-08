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
package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations.MotivationsExpectationsFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(CurricularCoursesController.CONTROLLER_URL)
public class CurricularCoursesController extends FirstTimeCandidacyAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/curricularCourses";

    @RequestMapping
    public String home(@PathVariable("executionYearId") final ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return redirect(urlWithExecutionYear(SHOW_URL, executionYear), model, redirectAttributes);
    }

    protected static final String _BACK_URI = "/back";

    @RequestMapping(value = _BACK_URI, method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return backScreen(executionYear, model, redirectAttributes);
    }

    public String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        if (!EnrolmentsController.shouldBeSkipped(executionYear)) {
            return redirect(urlWithExecutionYear(EnrolmentsController.CONTROLLER_URL, executionYear), model, redirectAttributes);
        } else {
            return redirect(urlWithExecutionYear(MotivationsExpectationsFormController.CONTROLLER_URL, executionYear), model,
                    redirectAttributes);
        }
    }

    protected static final String _SHOW_URI = "/show";
    public static final String SHOW_URL = CONTROLLER_URL + _SHOW_URI;

    @RequestMapping(value = _SHOW_URI, method = RequestMethod.GET)
    public String show(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes, HttpServletRequest request) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return showScreen(executionYear, model, redirectAttributes, request);
    }

    protected String showScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        Registration registration = candidacy.getRegistration();
        StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(candidacy.getDegreeCurricularPlan());

        if (!validUCsEnrolment(studentCurricularPlan, executionYear)) {
            redirect(EnrolmentsController.ENROL_URL, model, redirectAttributes);
        }

        ExecutionSemester firstSemester = executionYear.getFirstExecutionPeriod();
        ExecutionSemester secondSemester = firstSemester.getNextExecutionPeriod();
        Collection<Enrolment> firstSemEnrolments = studentCurricularPlan.getEnrolmentsByExecutionPeriod(firstSemester).stream()
                .filter(e -> e.getEctsCredits() > 0).collect(Collectors.toList());
        Double firstSemCredits = 0d;
        for (Enrolment enrolment : firstSemEnrolments) {
            firstSemCredits += enrolment.getEctsCredits();
        }
        Collection<Enrolment> secondSemEnrolments = studentCurricularPlan.getEnrolmentsByExecutionPeriod(secondSemester).stream()
                .filter(e -> e.getEctsCredits() > 0).collect(Collectors.toList());
        Double secondSemCredits = 0d;
        for (Enrolment enrolment : secondSemEnrolments) {
            secondSemCredits += enrolment.getEctsCredits();
        }

        model.addAttribute("currentYear", ExecutionYear.readCurrentExecutionYear().getYear());
        model.addAttribute("firstSemesterEnrolments", firstSemEnrolments);
        model.addAttribute("firstSemesterCredits", firstSemCredits);
        model.addAttribute("secondSemesterEnrolments", secondSemEnrolments);
        model.addAttribute("secondSemesterCredits", secondSemCredits);

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.showSelectedCourses.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/showenrolments/showselectedcourses";
    }

    private boolean validUCsEnrolment(StudentCurricularPlan studentCurricularPlan, ExecutionYear executionYear) {
        Double ectsEnrolled = studentCurricularPlan.getEnrolmentsByExecutionYear(executionYear).stream()
                .map(e -> e.getEctsCredits()).reduce(Double::sum).orElse(0d);
        return ectsEnrolled > 30d;
    }

    protected static final String _CONTINUE_URI = "/continue";
    public static final String CONTINUE_URL = CONTROLLER_URL + _CONTINUE_URI;

    @RequestMapping(value = _CONTINUE_URI, method = RequestMethod.GET)
    public String next(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return nextScreen(executionYear, model, redirectAttributes);
    }

    protected String nextScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(SchoolClassesController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        throw new RuntimeException("Error you should not call this method.");
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

}
