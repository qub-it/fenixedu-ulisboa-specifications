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
package org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.dto.student.IStudentCurricularPlanBean;
import org.fenixedu.academic.ui.renderers.providers.ExecutionPeriodsForDismissalsStudentCurricularPlanProvider;

import pt.ist.fenixWebFramework.rendererExtensions.converters.DomainObjectKeyConverter;
import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;

/**
 * @see
 *      {@link ExecutionPeriodsForDismissalsStudentCurricularPlanProvider}
 * 
 */
public class ExecutionPeriodsForEnrolmentProvider implements DataProvider {

    @Override
    public Converter getConverter() {
        return new DomainObjectKeyConverter();
    }

    @Override
    public Object provide(Object source, Object currentValue) {

        final StudentCurricularPlan studentCurricularPlan = ((IStudentCurricularPlanBean) source).getStudentCurricularPlan();

        final Set<ExecutionInterval> result = new TreeSet<ExecutionInterval>(Collections.reverseOrder());
        ExecutionYear executionYear = studentCurricularPlan.getStartExecutionYear();
        result.addAll(executionYear.getExecutionPeriodsSet());
        while (executionYear.getNextExecutionYear() != null) {
            result.addAll(executionYear.getNextExecutionYear().getExecutionPeriodsSet());
            executionYear = executionYear.getNextExecutionYear();
        }

        return result;
    }

}
