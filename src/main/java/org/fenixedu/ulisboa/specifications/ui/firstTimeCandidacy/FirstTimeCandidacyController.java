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
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.joda.time.LocalDate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.firstTimeCandidacy")
@RequestMapping(FirstTimeCandidacyController.CONTROLLER_URL)
public class FirstTimeCandidacyController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String FIRST_TIME_START_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy";

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/home";

    private static Predicate<StudentCandidacy> isCandidacyFirstTime = StudentCandidacy::getFirstTimeCandidacy;

    private static Predicate<StudentCandidacy> isCandidacyOpen = c -> CandidacySituationType.STAND_BY.equals(c.getState());

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

        return redirect("/dges/academicRequisition/?candidacy=" + getCandidacy(person).getExternalId() + "&isForward=true", model,
                redirectAttributes);
    }

    public static StudentCandidacy getCandidacy() {
        return getCandidacy(AccessControl.getPerson());
    }

    public static StudentCandidacy getCandidacy(final Person person) {
        return person.getCandidaciesSet().stream().filter(c -> c instanceof StudentCandidacy).map(StudentCandidacy.class::cast)
                .filter(isCandidacyFirstTime).filter(isCandidacyOpen).sorted(Comparator.comparing(StudentCandidacy::getStartDate))
                .findFirst().orElse(null);
    }

    public static List<String> isValidForFirstTimeCandidacy() {
        List<String> errorMessages = new ArrayList<>();
        Person person = AccessControl.getPerson();;
        if (person.getCandidaciesSet().stream().filter(c -> c instanceof StudentCandidacy).map(StudentCandidacy.class::cast)
                .noneMatch(isCandidacyFirstTime.and(isCandidacyOpen))) {
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
        StudentCandidacy candidacy = getCandidacy();
        if (candidacy == null) {
            return false;
        }
        List<AcademicEnrolmentPeriodBean> enrolmentPeriodsOpen =
                AcademicEnrolmentPeriodBean.getEnrolmentPeriodsOpenOrUpcoming(student, true, candidacy.getDegreeCurricularPlan())
                        .stream().filter(isOnlyOpen.and(isFirstTime)).collect(Collectors.toList());
        return !enrolmentPeriodsOpen.isEmpty();
    }

}
