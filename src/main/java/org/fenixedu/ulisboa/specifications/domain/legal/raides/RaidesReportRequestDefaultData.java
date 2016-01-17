package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesPeriodInputType;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.report.RaidesRequestParameter;
import org.fenixedu.ulisboa.specifications.domain.legal.settings.LegalSettings;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class RaidesReportRequestDefaultData implements IRaidesReportRequestDefaultData {

    public static final Boolean IsRaidesStudentEditionActive() {
        return LegalSettings.getInstance().getRaidesFormActiveForStudent();
    }

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy");

    public RaidesReportRequestDefaultData() {
    }
    
    @Override
    public void fill(final RaidesRequestParameter raidesRequestParameter) {

        // Enrolment periods

        raidesRequestParameter.addPeriod(RaidesPeriodInputType.ENROLLED, ExecutionYear.readExecutionYearByName("2015/2016"),
                DATETIME_FORMATTER.parseDateTime("01/08/2015").toLocalDate(), DATETIME_FORMATTER.parseDateTime("31/12/2015")
                        .toLocalDate(), true, false, BigDecimal.ZERO, BigDecimal.ZERO, false, 0, 0);

        // Graduation periods
        raidesRequestParameter.addPeriod(RaidesPeriodInputType.GRADUATED, ExecutionYear.readExecutionYearByName("2014/2015"),
                DATETIME_FORMATTER.parseDateTime("01/01/2015").toLocalDate(), DATETIME_FORMATTER.parseDateTime("31/12/2015")
                        .toLocalDate(), true, false, BigDecimal.ZERO, BigDecimal.ZERO, false, 0, 0);

        // International Mobility
        raidesRequestParameter.addPeriod(RaidesPeriodInputType.INTERNATIONAL_MOBILITY,
                ExecutionYear.readExecutionYearByName("2015/2016"), DATETIME_FORMATTER.parseDateTime("01/08/2015").toLocalDate(),
                DATETIME_FORMATTER.parseDateTime("31/12/2015").toLocalDate(), true, true, new BigDecimal("15"), null, true, 0, 1);

        // Degrees
        raidesRequestParameter.getDegrees().addAll(RaidesInstance.getInstance().getDegreesToReportSet());

        // AgreementsForEnrolled
        raidesRequestParameter.getAgreementsForEnrolled().addAll(RaidesInstance.getInstance().getEnrolledAgreementsSet());

        // AgreementsForMobility
        raidesRequestParameter.getAgreementsForMobility().addAll(RaidesInstance.getInstance().getMobilityAgreementsSet());

        // IngressionsForDegreeChange
        raidesRequestParameter.getIngressionsForDegreeChange().addAll(RaidesInstance.getInstance().getDegreeChangeIngressionsSet());

        // IngressionsForDegreeTransfer
        raidesRequestParameter.getIngressionsForDegreeTransfer().addAll(RaidesInstance.getInstance().getDegreeTransferIngressionsSet());

        // IngressionsForGeneralAccessRegime
        raidesRequestParameter.getIngressionsForGeneralAccessRegime().addAll(RaidesInstance.getInstance().getGeneralAccessRegimeIngressionsSet());

        raidesRequestParameter.setInstitution(Bennu.getInstance().getInstitutionUnit());
        raidesRequestParameter.setInstitutionCode(Bennu.getInstance().getInstitutionUnit().getCode());
        raidesRequestParameter.setMoment("1");
        raidesRequestParameter.setInterlocutorName(Authenticate.getUser().getPerson().getName());
        raidesRequestParameter.setInterlocutorEmail(Authenticate.getUser().getPerson().getDefaultEmailAddressValue());
        raidesRequestParameter.setInterlocutorPhone(Authenticate.getUser().getPerson().getDefaultPhoneNumber());
        raidesRequestParameter.setFilterEntriesWithErrors(true);
    }

}
