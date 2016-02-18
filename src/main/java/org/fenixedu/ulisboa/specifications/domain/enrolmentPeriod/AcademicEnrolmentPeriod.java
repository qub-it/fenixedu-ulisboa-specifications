package org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod;

import java.util.Collection;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class AcademicEnrolmentPeriod extends AcademicEnrolmentPeriod_Base {

    protected AcademicEnrolmentPeriod() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected AcademicEnrolmentPeriod(final DateTime startDate, final DateTime endDate, final Boolean specialSeason,
            final Boolean firstTimeRegistration, final Boolean restrictToSelectedStatutes, final Integer minStudentNumber,
            final Integer maxStudentNumber, final Integer curricularYear, final AcademicEnrolmentPeriodType enrolmentPeriodType,
            final ExecutionSemester executionSemester) {
        this();
        setStartDate(startDate);
        setEndDate(endDate);
        setSpecialSeason(specialSeason);
        setFirstTimeRegistration(firstTimeRegistration);
        setRestrictToSelectedStatutes(restrictToSelectedStatutes);
        setMinStudentNumber(minStudentNumber);
        setMaxStudentNumber(maxStudentNumber);
        setCurricularYear(curricularYear);
        setEnrolmentPeriodType(enrolmentPeriodType);
        setExecutionSemester(executionSemester);

        checkRules();
    }

    private void checkRules() {

        if (getStartDate() == null) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.startDate.required");
        }

        if (getEndDate() == null) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.endDate.required");
        }

        if (getEnrolmentPeriodType() == null) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.enrolmentPeriodType.required");
        }

        if (getExecutionSemester() == null) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.executionSemester.required");
        }

    }

    @Atomic
    public void edit(final DateTime startDate, final DateTime endDate, final Boolean specialSeason,
            final Boolean firstTimeRegistration, final Boolean restrictToSelectedStatutes, final Integer minStudentNumber,
            final Integer maxStudentNumber, final Integer curricularYear, final AcademicEnrolmentPeriodType enrolmentPeriodType,
            final ExecutionSemester executionSemester) {
        setStartDate(startDate);
        setEndDate(endDate);
        setSpecialSeason(specialSeason);
        setFirstTimeRegistration(firstTimeRegistration);
        setRestrictToSelectedStatutes(restrictToSelectedStatutes);
        setMinStudentNumber(minStudentNumber);
        setMaxStudentNumber(maxStudentNumber);
        setCurricularYear(curricularYear);
        setEnrolmentPeriodType(enrolmentPeriodType);
        setExecutionSemester(executionSemester);

        checkRules();
    }

    @Atomic
    public void edit(final AcademicEnrolmentPeriodBean bean) {
        edit(bean.getStartDate(), bean.getEndDate(), bean.getSpecialSeason(), bean.getFirstTimeRegistration(),
                bean.getRestrictToSelectedStatutes(), bean.getMinStudentNumber(), bean.getMaxStudentNumber(),
                bean.getCurricularYear(), bean.getEnrolmentPeriodType(), bean.getExecutionSemester());
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setBennu(null);
        setExecutionSemester(null);
        getDegreeCurricularPlansSet().clear();
        getStatuteTypesSet().clear();

        deleteDomainObject();
    }

    public static Stream<AcademicEnrolmentPeriod> readAll() {
        return Bennu.getInstance().getAcademicEnrolmentPeriodsSet().stream();
    }

    public static Stream<AcademicEnrolmentPeriod> findByType(AcademicEnrolmentPeriodType type) {
        return readAll().filter(p -> p.getEnrolmentPeriodType() == type);
    }

    public static AcademicEnrolmentPeriod create(final DateTime startDate, final DateTime endDate, final Boolean specialSeason,
            final Boolean firstTimeRegistration, final Boolean restrictToSelectedStatutes, final Integer minStudentNumber,
            final Integer maxStudentNumber, final Integer curricularYear, final AcademicEnrolmentPeriodType enrolmentPeriodType,
            final ExecutionSemester executionSemester) {
        AcademicEnrolmentPeriod period =
                new AcademicEnrolmentPeriod(startDate, endDate, specialSeason, firstTimeRegistration, restrictToSelectedStatutes,
                        minStudentNumber, maxStudentNumber, curricularYear, enrolmentPeriodType, executionSemester);

        return period;
    }

    public static AcademicEnrolmentPeriod create(final AcademicEnrolmentPeriodBean bean) {
        return create(bean.getStartDate(), bean.getEndDate(), bean.getSpecialSeason(), bean.getFirstTimeRegistration(),
                bean.getRestrictToSelectedStatutes(), bean.getMinStudentNumber(), bean.getMaxStudentNumber(),
                bean.getCurricularYear(), bean.getEnrolmentPeriodType(), bean.getExecutionSemester());
    }

}
