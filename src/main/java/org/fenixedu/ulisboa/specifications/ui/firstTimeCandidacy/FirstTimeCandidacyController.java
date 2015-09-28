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

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.EnrolmentPeriod;
import org.fenixedu.academic.domain.EnrolmentPeriodInCurricularCoursesCandidate;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.Candidacy;
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
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
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

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/home";

    @RequestMapping
    public String home(Model model) {
        Person person = AccessControl.getPerson();
        Stream<Candidacy> firstTimeCandidacies = person.getCandidaciesSet().stream().filter(arefirstTime);
        long count = firstTimeCandidacies.count();
        if (count == 0) {
            throw new RuntimeException(
                    "Students with no FirstTimeCandidacies are not supported in the first time registration flow");
        }
        if (count > 1) {
            throw new RuntimeException(
                    "Students with multiple FirstTimeCandidacies are not supported in the first time registration flow");
        }

        if (TreasuryBridgeAPIFactory.implementation().isAcademicalActsBlocked(person, new LocalDate())) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.academicalActsBlocked"),
                    model);
        }
        if (!isPeriodOpen()) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.firstTimeCandidacy.period.closed"), model);
        }
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/instructions";
    }

    @RequestMapping(value = "/continue")
    public String instructionsToContinue(Model model, RedirectAttributes redirectAttributes) {
        Person person = AccessControl.getPerson();
        StudentAccessServices.requestSyncPersonFromExternal(person);

        return redirect(PersonalInformationFormController.CONTROLLER_URL, model, redirectAttributes);
    }

    private static Predicate<Candidacy> arefirstTime = c -> (c instanceof FirstTimeCandidacy)
            && ((FirstTimeCandidacy) c).getExecutionYear().equals(ExecutionYear.readCurrentExecutionYear());

    public static FirstTimeCandidacy getCandidacy() {
        return getCandidacy(AccessControl.getPerson());
    }

    public static FirstTimeCandidacy getCandidacy(Person person) {
        Stream<Candidacy> firstTimeCandidacies = person.getCandidaciesSet().stream().filter(arefirstTime);
        return (FirstTimeCandidacy) firstTimeCandidacies.findAny().orElse(null);
    }

    @Atomic
    public static PersonalIngressionData getOrCreatePersonalIngressionData(PrecedentDegreeInformation precedentInformation) {
        PersonalIngressionData personalData = null;
        personalData = precedentInformation.getPersonalIngressionData();
        Student student = AccessControl.getPerson().getStudent();
        if (personalData == null) {
            personalData = student.getPersonalIngressionDataByExecutionYear(ExecutionYear.readCurrentExecutionYear());
            if (personalData != null) {
                //if the student already has a PID it will have another PDI associated, it's necessary to add the new PDI
                personalData.addPrecedentDegreesInformations(precedentInformation);
            } else {
                personalData = new PersonalIngressionData(ExecutionYear.readCurrentExecutionYear(), precedentInformation);
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

    public static EnrolmentPeriodInCurricularCoursesCandidate getCandidacyPeriod(DegreeCurricularPlan dcp) {
        Predicate<EnrolmentPeriod> isCandidateEnrolmentPeriod =
                ep -> ep instanceof EnrolmentPeriodInCurricularCoursesCandidate
                        && ep.getExecutionPeriod().equals(ExecutionSemester.readActualExecutionSemester());
        Stream<EnrolmentPeriod> periods = dcp.getEnrolmentPeriodsSet().stream().filter(isCandidateEnrolmentPeriod);
        long count = periods.count();
        if (count == 0) {
            return null;
        }
        if (count > 1) {
            throw new RuntimeException("Multiple configured periods (EnrolmentPeriodInCurricularCoursesCandidate) for the dcp: "
                    + dcp.getName() + " and the semester: " + ExecutionSemester.readActualExecutionSemester().getName());
        }

        // Repeat the asign to the periods stream, because the previous count() operation is "terminal"
        periods = dcp.getEnrolmentPeriodsSet().stream().filter(isCandidateEnrolmentPeriod);
        return (EnrolmentPeriodInCurricularCoursesCandidate) periods.findFirst().get();
    }

    public static boolean isPeriodOpen() {
        DegreeCurricularPlan dcp = getCandidacy().getExecutionDegree().getDegreeCurricularPlan();
        EnrolmentPeriodInCurricularCoursesCandidate period = getCandidacyPeriod(dcp);
        if (period == null) {
            throw new RuntimeException("No configured periods (EnrolmentPeriodInCurricularCoursesCandidate) for the dcp: "
                    + dcp.getName() + " and the semester: " + ExecutionSemester.readActualExecutionSemester().getName());
        }
        return period.isValid();
    }
}
