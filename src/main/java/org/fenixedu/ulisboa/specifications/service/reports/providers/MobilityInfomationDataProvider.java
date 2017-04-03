package org.fenixedu.ulisboa.specifications.service.reports.providers;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityRegistrationInformation;
import org.joda.time.LocalDate;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;

public class MobilityInfomationDataProvider implements IReportDataProvider {

    protected static final String KEY = "mobilityInformation";
    protected static final String KEY_BEGIN_DATE = "mobilityBeginDate";
    protected static final String KEY_END_DATE = "mobilityEndDate";
    protected static final String KEY_PROGRAM = "mobilityProgram";

    private final MobilityRegistrationInformation information;

    public MobilityInfomationDataProvider(final Registration registration, final ExecutionYear executionYear) {
        information = MobilityRegistrationInformation.findInternationalIncomingInformation(registration, executionYear);
    }

    @Override
    public void registerFieldsAndImages(final IDocumentFieldsData documentFieldsData) {
    }

    @Override
    public boolean handleKey(final String key) {
        return KEY.equals(key) || KEY_BEGIN_DATE.equals(key) || KEY_END_DATE.equals(key) || KEY_PROGRAM.equals(key);
    }

    @Override
    public Object valueForKey(final String key) {
        if (KEY.equals(key)) {
            return information;
        } else if (KEY_BEGIN_DATE.equals(key)) {
            return getBeginDate();
        } else if (KEY_END_DATE.equals(key)) {
            return getEndDate();
        } else if (KEY_PROGRAM.equals(key)) {
            return getProgram();
        }
        return null;
    }

    private LocalDate getBeginDate() {
        if (information != null) {
            return information.getBeginDate();
        }
        return null;
    }

    private Object getEndDate() {
        if (information != null) {
            return information.getEndDate();
        }
        return null;
    }

    private LocalizedString getProgram() {
        if (information != null) {
            return information.getMobilityProgramType().getName();
        }
        return null;
    }

}
