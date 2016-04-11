/**
 * 
 */
package org.fenixedu.academic.ui.renderers.providers;

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Shift;
import org.fenixedu.ulisboa.specifications.domain.services.teacher.SummaryServices;

/**
 * @author shezad
 *
 */
public class ListShiftsToSummariesManagementProviderOverride extends ListShiftsToSummariesManagementProvider {

    @Override
    public Object provide(Object source, Object currentValue) {
        final Set<Shift> shifts = (Set<Shift>) super.provide(source, currentValue);

        return shifts.stream().filter(shit -> SummaryServices.isShiftSummariesManageableByLoggedPerson(shit))
                .sorted(Shift.SHIFT_COMPARATOR_BY_TYPE_AND_ORDERED_LESSONS).collect(Collectors.toList());
    }

}
