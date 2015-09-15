/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;

public class StudentActive {

    public static boolean isActiveStudent(Student student) {
        boolean hasActiveRegistrationsWithEnrolments = false;
        boolean activeRegistrationCreatedInTheLastMonth = false;
        boolean isFirstYearFirstTime = false;
//        boolean isOtherKindOfCandidate = false;

        if (student != null) {
            ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
            ExecutionYear previousExecutionYear = currentExecutionYear.getPreviousExecutionYear();
            Predicate<? super Registration> registrationHasEnrolmentsInLast2Years =
                    registration -> !registration.getEnrolments(currentExecutionYear).isEmpty()
                            || !registration.getEnrolments(previousExecutionYear).isEmpty();
            List<Registration> activeRegistrations = student.getActiveRegistrations();
            hasActiveRegistrationsWithEnrolments =
                    !activeRegistrations.stream().filter(registrationHasEnrolmentsInLast2Years).collect(Collectors.toList())
                            .isEmpty();

            if (!hasActiveRegistrationsWithEnrolments) {
                // There are special kinds of students, for example in protocols, where they are created in Fenix,
                // without a candidacy and where they have no enrolments. Although we still want them to be considered 
                // active students so the attribute ULStudentActive is sent to LDAP with TRUE so the campus account is 
                // created.
                //
                // To take into account these special cases we'll check active registrations for the student and
                // see if any of them were created in the last month, if so we'll consider it's an active student.
                //
                // This solution was discussed with João Rafael and Daniela Mendes from ULisboa.
                //
                // 21 August 2015 - Paulo Abrantes
                //
                // UPDATE UPDATE UPDATE
                // 
                // There was a modification in the code so that registrationYear is a setter property, and we'll
                // now start using that slot instead of looking at the last active state.
                //
                // 15 September 2015 - Paulo Abrantes
//                LocalDate today = new LocalDate();
//                DateTime lastMonth = today.toDateTimeAtStartOfDay().minusMonths(1);
                for (Registration activeRegistration : activeRegistrations) {
                    activeRegistrationCreatedInTheLastMonth = activeRegistration.getRegistrationYear() == currentExecutionYear;

//                    RegistrationState lastRegistrationState = activeRegistration.getLastRegistrationState(currentExecutionYear);
//                    activeRegistrationCreatedInTheLastMonth =
//                            lastRegistrationState != null && lastRegistrationState.isActive()
//                                    && lastRegistrationState.getStateDate() != null
//                                    && lastRegistrationState.getStateDate().isAfter(lastMonth);

                    if (activeRegistrationCreatedInTheLastMonth) {
                        break;
                    }
                }
            }
            //
            // Detect if it's 1st year, 1st time
            //
            if (!hasActiveRegistrationsWithEnrolments && !activeRegistrationCreatedInTheLastMonth) {
                isFirstYearFirstTime =
                        student.getPerson()
                                .getCandidaciesSet()
                                .stream()
                                .filter(candidacy -> candidacy instanceof StudentCandidacy)
                                .map(StudentCandidacy.class::cast)
                                .anyMatch(
                                        studentCandidacy -> studentCandidacy.isActive()
                                                && studentCandidacy.getEntryPhase() != null
                                                && studentCandidacy.getExecutionYear() == currentExecutionYear);
            }

            // Removing detecion of other kind of candidate (still leaving the code just in case) 
            // Ana Rute asked us to not send other candidates has active students, only 1stYearFirstTime.
            // This will be sent with the student flag active (and then synchronized with IDM) when they 
            // finalize their candidacy situation and become an actual student
            //
            // 27 July 2015 - Paulo Abrantes
//            isOtherKindOfCandidate =
//                    student.getPerson()
//                            .getCandidaciesSet()
//                            .stream()
//                            .filter(candidacy -> candidacy instanceof StudentCandidacy)
//                            .map(StudentCandidacy.class::cast)
//                            .anyMatch(
//                                    candidacy -> candidacy.getRegistration() != null && candidacy.getRegistration().isActive()
//                                            && candidacy.getExecutionYear() == currentExecutionYear);
        }

        return hasActiveRegistrationsWithEnrolments || isFirstYearFirstTime || activeRegistrationCreatedInTheLastMonth;
    }
}
