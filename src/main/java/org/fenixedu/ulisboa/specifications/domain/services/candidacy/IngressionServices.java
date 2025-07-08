package org.fenixedu.ulisboa.specifications.domain.services.candidacy;

import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;

import com.google.common.base.Strings;

public class IngressionServices {

    public static boolean isCompletePrecedentDegreeInformationMissing(final Registration registration) {
        final PrecedentDegreeInformation pdi = registration.getCompletedDegreeInformation();

        if (pdi.getEducationLevelType() != null && !pdi.getEducationLevelType().getHigherEducation()) {
            return false;
        }

        return pdi.getInstitution() == null || Strings.isNullOrEmpty(pdi.getDegreeDesignation());
    }

    public static boolean isUnfinishedSourceDegreeInformationMissing(final Registration registration) {

        if (!isUnfinishedSourceDegreeInformationRequired(registration)) {
            return false;
        }

        final PrecedentDegreeInformation pdi = registration.getPreviousDegreeInformation();
        return pdi == null || pdi.getInstitution() == null;
    }

    public static boolean isUnfinishedSourceDegreeInformationRequired(final Registration registration) {
        final IngressionType ingressionType = registration.getIngressionType();
        return ingressionType != null && ingressionType.getRequiresUnfinishedSourceDegreeInformation();
    }

}
