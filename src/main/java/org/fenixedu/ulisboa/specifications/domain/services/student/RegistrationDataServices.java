package org.fenixedu.ulisboa.specifications.domain.services.student;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.ulisboa.specifications.domain.student.RegistrationDataByExecutionYearExtendedInformation;

public class RegistrationDataServices {

    static public void setCurricularYear(final RegistrationDataByExecutionYear registrationDataByExecutionYear,
            final Integer curricularYear) {
        RegistrationDataByExecutionYearExtendedInformation.findOrCreate(registrationDataByExecutionYear)
                .setCurricularYear(curricularYear);
    }

    static private Integer getCurricularYear(final RegistrationDataByExecutionYear registrationDataByExecutionYear) {
        return registrationDataByExecutionYear == null
                || registrationDataByExecutionYear.getExtendedInformation() == null ? null : registrationDataByExecutionYear
                        .getExtendedInformation().getCurricularYear();
    }

    static public Integer getOverridenCurricularYear(Registration registration, ExecutionYear executionYear) {

        final RegistrationDataByExecutionYear dataByExecutionYear;
        if (executionYear != null) {
            dataByExecutionYear = getRegistrationData(registration, executionYear);
        } else {
            dataByExecutionYear = null;
        }

        final Integer curricularYear = getCurricularYear(dataByExecutionYear);
        if (curricularYear != null) {
            return curricularYear;
        }

        return null;
    }

    static public RegistrationDataByExecutionYear getRegistrationData(final Registration registration, final ExecutionYear year) {

        for (final RegistrationDataByExecutionYear iter : registration.getRegistrationDataByExecutionYearSet()) {
            if (iter.getExecutionYear() == year) {
                return iter;
            }
        }

        return null;
    }

    static public RegistrationDataByExecutionYear getFirstRegistrationData(final Registration registration) {
        return registration == null ? null : registration.getRegistrationDataByExecutionYearSet().stream()
                .min((i, j) -> i.getExecutionYear().compareTo(j.getExecutionYear())).orElse(null);
    }

    static public RegistrationDataByExecutionYear getLastRegistrationData(final Registration registration) {
        return registration == null ? null : registration.getRegistrationDataByExecutionYearSet().stream()
                .max((i, j) -> i.getExecutionYear().compareTo(j.getExecutionYear())).orElse(null);
    }

}
