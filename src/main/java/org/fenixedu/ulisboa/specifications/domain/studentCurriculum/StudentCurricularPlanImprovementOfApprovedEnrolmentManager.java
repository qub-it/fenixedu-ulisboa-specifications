/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
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
package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.joda.time.LocalDate;

public class StudentCurricularPlanImprovementOfApprovedEnrolmentManager
        extends org.fenixedu.academic.domain.studentCurriculum.StudentCurricularPlanImprovementOfApprovedEnrolmentManager {

    public StudentCurricularPlanImprovementOfApprovedEnrolmentManager(final EnrolmentContext enrolmentContext) {
        super(enrolmentContext);
    }

    @Override
    protected void assertEnrolmentPreConditions() {
        if (isResponsiblePersonManager()) {
            return;
        }

// qubExtension, removed check - should be replaced somehow?
//        if (!hasRegistrationInValidState()) {
//            throw new DomainException("error.StudentCurricularPlan.cannot.enrol.with.registration.inactive");
//        }

        if (ULisboaConfiguration.getConfiguration().getEnrolmentsInEvaluationsDependOnAcademicalActsBlocked()
                && TreasuryBridgeAPIFactory.implementation().isAcademicalActsBlocked(getPerson(), new LocalDate())) {
            throw new DomainException("error.StudentCurricularPlan.cannot.enrol.with.debts.for.previous.execution.years");
        }

        if (areModifiedCyclesConcluded()) {
            checkUpdateRegistrationAfterConclusion();
        }
    }

}
