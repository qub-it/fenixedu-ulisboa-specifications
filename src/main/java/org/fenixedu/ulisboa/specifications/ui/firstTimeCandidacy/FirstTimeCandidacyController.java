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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.Candidacy;
import org.fenixedu.academic.domain.enrolment.period.AcademicEnrolmentPeriod;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo.PersonalInformationFormController;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.firstTimeCandidacy")
@RequestMapping(FirstTimeCandidacyController.CONTROLLER_URL)
public class FirstTimeCandidacyController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String FIRST_TIME_START_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy";

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/home";

    @RequestMapping
    public String home(final Model model) {
        List<String> errorMessages = isValidForFirstTimeCandidacy();

        for (String errorMessage : errorMessages) {
            addErrorMessage(errorMessage, model);
        }

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/instructions";
    }

    @RequestMapping(value = "/continue")
    public String instructionsToContinue(final Model model, final RedirectAttributes redirectAttributes) {
        Person person = AccessControl.getPerson();
        StudentAccessServices.requestSyncPersonFromExternal(person);

        final ExecutionYear executionYear = getCandidacy(person).getExecutionYear();
        return redirect(FirstTimeCandidacyAbstractController.urlWithExecutionYear(
                PersonalInformationFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    public static FirstTimeCandidacy getCandidacy() {
        return getCandidacy(AccessControl.getPerson());
    }

    public static FirstTimeCandidacy getCandidacy(final Person person) {
        Set<FirstTimeCandidacy> candidacies = new HashSet<>();
        for (Candidacy candidacy : person.getCandidaciesSet()) {
            if (candidacy instanceof FirstTimeCandidacy) {
                candidacies.add((FirstTimeCandidacy) candidacy);
            }
        }

        Stream<FirstTimeCandidacy> firstTimeCandidacies = candidacies.stream().filter(FirstTimeCandidacy.isFirstTime)
                .filter(FirstTimeCandidacy.isOpen).sorted(FirstTimeCandidacy.COMPARATOR_BY_DATE);
        return firstTimeCandidacies.findFirst().orElse(null);
    }

    public static List<String> isValidForFirstTimeCandidacy() {
        List<String> errorMessages = new ArrayList<>();
        Person person = AccessControl.getPerson();
        Stream<Candidacy> firstTimeCandidacies =
                person.getCandidaciesSet().stream().filter(FirstTimeCandidacy.isFirstTime).filter(FirstTimeCandidacy.isOpen);
        long count = firstTimeCandidacies.count();
        if (count == 0) {
            errorMessages.add("Students with no open FirstTimeCandidacies are not supported in the first time registration flow");
        }

        if (TreasuryBridgeAPIFactory.implementation().isAcademicalActsBlocked(person, new LocalDate())) {
            errorMessages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.academicalActsBlocked"));
        }
        if (!isPeriodOpen()) {
            errorMessages.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.firstTimeCandidacy.period.closed"));
        }
        return errorMessages;
    }

    public static boolean isPeriodOpen() {
        Predicate<AcademicEnrolmentPeriodBean> isOnlyOpen = p -> p.isOpen();
        Predicate<AcademicEnrolmentPeriodBean> isFirstTime = p -> p.isFirstTimeRegistration();

        Student student = AccessControl.getPerson().getStudent();
        FirstTimeCandidacy candidacy = getCandidacy();
        if (candidacy == null) {
            return false;
        }
        List<AcademicEnrolmentPeriodBean> enrolmentPeriodsOpen =
                AcademicEnrolmentPeriodBean.getEnrolmentPeriodsOpenOrUpcoming(student, true, candidacy.getDegreeCurricularPlan())
                        .stream().filter(isOnlyOpen.and(isFirstTime)).collect(Collectors.toList());
        return !enrolmentPeriodsOpen.isEmpty();
    }

}
