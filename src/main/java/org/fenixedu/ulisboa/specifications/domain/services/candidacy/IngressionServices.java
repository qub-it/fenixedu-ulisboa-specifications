package org.fenixedu.ulisboa.specifications.domain.services.candidacy;

import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;

import com.google.common.base.Strings;

public class IngressionServices {

    public static boolean isCompletePrecedentDegreeInformationMissing(final Registration registration) {
        final PrecedentDegreeInformation pdi = registration.getStudentCandidacy().getPrecedentDegreeInformation();

        if (pdi.getSchoolLevel() != null && !pdi.getSchoolLevel().isHigherEducation()) {
            return false;
        }

        return pdi.getInstitution() == null || Strings.isNullOrEmpty(pdi.getDegreeDesignation());
    }

    public static boolean isUnfinishedSourceDegreeInformationMissing(final Registration registration) {

        if (!isUnfinishedSourceDegreeInformationRequired(registration)) {
            return false;
        }

        final PrecedentDegreeInformation pdi = registration.getStudentCandidacy().getPrecedentDegreeInformation();
        return pdi == null || pdi.getPrecedentInstitution() == null;
    }

    public static boolean isUnfinishedSourceDegreeInformationRequired(final Registration registration) {
        final IngressionType ingressionType = registration.getIngressionType();
        return ingressionType != null && ingressionType.getRequiresUnfinishedSourceDegreeInformation();
    }

}
