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
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
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
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
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
    public String home(Model model) {
        List<String> errorMessages = isValidForFirstTimeCandidacy();

        for (String errorMessage : errorMessages) {
            addErrorMessage(errorMessage, model);
        }

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/instructions";
    }

    @RequestMapping(value = "/continue")
    public String instructionsToContinue(Model model, RedirectAttributes redirectAttributes) {
        Person person = AccessControl.getPerson();
        StudentAccessServices.requestSyncPersonFromExternal(person);

        final ExecutionYear executionYear = getCandidacy(person).getExecutionYear();
        return redirect(FirstTimeCandidacyAbstractController.urlWithExecutionYear(
                PersonalInformationFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    private static Predicate<Candidacy> isFirstTime = c -> (c instanceof FirstTimeCandidacy);
    private static Predicate<Candidacy> isOpen = c -> CandidacySituationType.STAND_BY.equals(c.getActiveCandidacySituationType());

    public static FirstTimeCandidacy getCandidacy() {
        return getCandidacy(AccessControl.getPerson());
    }

    public static FirstTimeCandidacy getCandidacy(Person person) {
        Set<FirstTimeCandidacy> candidacies = new HashSet<>();
        for (Candidacy candidacy : person.getCandidaciesSet()) {
            if (candidacy instanceof FirstTimeCandidacy) {
                candidacies.add((FirstTimeCandidacy) candidacy);
            }
        }

        Stream<FirstTimeCandidacy> firstTimeCandidacies =
                candidacies.stream().filter(isFirstTime).filter(isOpen).sorted(FirstTimeCandidacy.COMPARATOR_BY_DATE);
        return firstTimeCandidacies.findFirst().orElse(null);
    }

    @Atomic
    public static PersonalIngressionData getOrCreatePersonalIngressionData(final ExecutionYear executionYear,
            PrecedentDegreeInformation precedentInformation) {
        PersonalIngressionData personalData = null;
        personalData = precedentInformation.getPersonalIngressionData();
        Student student = AccessControl.getPerson().getStudent();
        if (personalData == null) {
            personalData = student.getPersonalIngressionDataByExecutionYear(executionYear);
            if (personalData != null) {
                //if the student already has a PID it will have another PDI associated, it's necessary to add the new PDI
                personalData.addPrecedentDegreesInformations(precedentInformation);
            } else {
                personalData = new PersonalIngressionData(executionYear, precedentInformation);
            }
        }

        // It is necessary to create an early Registration so that the RAIDES objects are consistent
        // see PrecedentDegreeInformation.checkHasAllRegistrationOrPhdInformation()
        getOrCreateRegistration();

        return personalData;
    }

    private static Registration getOrCreateRegistration() {
        FirstTimeCandidacy studentCandidacy = FirstTimeCandidacyController.getCandidacy();
        Registration registration = studentCandidacy.getRegistration();
        if (registration != null) {
            return registration;
        }
        registration = new Registration(studentCandidacy.getPerson(), studentCandidacy);

        PrecedentDegreeInformation pdi = studentCandidacy.getPrecedentDegreeInformation();
        pdi.setRegistration(registration);
        pdi.getPersonalIngressionData().setStudent(studentCandidacy.getPerson().getStudent());

        DegreeCurricularPlan curricularPlan = studentCandidacy.getExecutionDegree().getDegreeCurricularPlan();
        ExecutionSemester semester = ExecutionSemester.readActualExecutionSemester();
        StudentCurricularPlan.createBolonhaStudentCurricularPlan(registration, curricularPlan, new YearMonthDay(), semester);

        RegistrationState registeredState = registration.getActiveState();
        registeredState.setStateDate(registeredState.getStateDate().minusMinutes(1));
        RegistrationState.createRegistrationState(registration, AccessControl.getPerson(), new DateTime(),
                RegistrationStateType.INACTIVE);

        return registration;
    }

    public static List<String> isValidForFirstTimeCandidacy() {
        List<String> errorMessages = new ArrayList<>();
        Person person = AccessControl.getPerson();
        Stream<Candidacy> firstTimeCandidacies = person.getCandidaciesSet().stream().filter(isFirstTime).filter(isOpen);
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
        List<AcademicEnrolmentPeriodBean> enrolmentPeriodsOpen = AcademicEnrolmentPeriod
                .getEnrolmentPeriodsOpenOrUpcoming(AccessControl.getPerson().getStudent(), true,
                        getCandidacy().getDegreeCurricularPlan())
                .stream().filter(isOnlyOpen.and(isFirstTime)).collect(Collectors.toList());
        return !enrolmentPeriodsOpen.isEmpty();
    }

}
