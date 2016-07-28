package org.fenixedu.ulisboa.specifications.domain.services.student;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.ulisboa.specifications.domain.student.RegistrationDataByExecutionYearExtendedInformation;

public class RegistrationDataByExecutionYearServices {

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
            dataByExecutionYear = getRegistrationDataByExecutionYear(registration, executionYear);
        } else {
            dataByExecutionYear = null;
        }

        final Integer curricularYear = RegistrationDataByExecutionYearServices.getCurricularYear(dataByExecutionYear);
        if (curricularYear != null) {
            return curricularYear;
        }

        return null;
    }

    static private RegistrationDataByExecutionYear getRegistrationDataByExecutionYear(Registration registration,
            ExecutionYear year) {
        for (RegistrationDataByExecutionYear registrationData : registration.getRegistrationDataByExecutionYearSet()) {
            if (registrationData.getExecutionYear().equals(year)) {
                return registrationData;
            }
        }
        return null;
    }

}
