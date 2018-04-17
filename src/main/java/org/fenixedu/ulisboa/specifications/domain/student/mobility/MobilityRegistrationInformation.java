package org.fenixedu.ulisboa.specifications.domain.student.mobility;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.organizationalStructure.AccountabilityTypeEnum;
import org.fenixedu.academic.domain.organizationalStructure.CountryUnit;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.dto.student.mobility.MobilityRegistrationInformationBean;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;

public class MobilityRegistrationInformation extends MobilityRegistrationInformation_Base {

    private static final Comparator<MobilityRegistrationInformation> COMPARATOR_BY_BEGIN = (x, y) -> {

        if (x.getBegin() != null && y.getBegin() != null) {
            return x.getBegin().compareTo(y.getBegin());
        }

        if (x.getBegin() == null && y.getBegin() == null) {
            return 0;
        }

        return x.getBegin() != null ? 1 : 0;
    };

    private static final Comparator<MobilityRegistrationInformation> COMPARATOR_BY_BEGIN_DATE = (x, y) -> {

        if (x.getBeginDate() != null && y.getBeginDate() != null) {
            return x.getBeginDate().compareTo(y.getBeginDate());
        }

        if (x.getBeginDate() == null && y.getBeginDate() == null) {
            return 0;
        }

        return x.getBeginDate() != null ? 1 : 0;

    };

    public static final Comparator<MobilityRegistrationInformation> COMPARATOR_BY_MOST_RECENT = COMPARATOR_BY_BEGIN
            .thenComparing(COMPARATOR_BY_BEGIN_DATE).thenComparing(DomainObjectUtil.COMPARATOR_BY_ID).reversed();

    protected MobilityRegistrationInformation() {
        super();
        setBennu(Bennu.getInstance());
    }

    @Atomic
    public void edit(final MobilityRegistrationInformationBean bean) {
        setIncoming(bean.isIncoming());
        setRegistration(bean.getRegistration());
        setBegin(bean.getBegin());
        setBeginDate(bean.getBeginDate());
        setEnd(bean.getEnd());
        setEndDate(bean.getEndDate());
        setMobilityProgramType(bean.getMobilityProgramType());
        setMobilityActivityType(bean.getMobilityActivityType());
        setCountryUnit(bean.getCountryUnit());
        setForeignInstitutionUnit(bean.getForeignInstitutionUnit());
        setRemarks(bean.getRemarks());
        setOriginMobilityProgrammeLevel(bean.getOriginMobilityProgrammeLevel());
        setOtherOriginMobilityProgrammeLevel(bean.getOtherOriginMobilityProgrammeLevel());
        setProgramDuration(bean.getProgramDuration());
        setDegreeBased(bean.isDegreeBased());
        setNational(bean.isNational());
        setRemarks(bean.getRemarks());

        if (bean.isDegreeBased()) {
            setDegree(bean.getDegree());
            setDegreeCurricularPlan(bean.getDegreeCurricularPlan());
            setBranchCourseGroup(bean.getBranchCourseGroup());

            setMobilityScientificArea(null);
            setIncomingMobilityProgrammeLevel(null);
            setOtherIncomingMobilityProgrammeLevel(null);

        } else {
            setDegree(null);
            setDegreeCurricularPlan(null);
            setBranchCourseGroup(null);

            setMobilityScientificArea(bean.getMobilityScientificArea());
            setIncomingMobilityProgrammeLevel(bean.getIncomingMobilityProgrammeLevel());
            setOtherIncomingMobilityProgrammeLevel(bean.getOtherIncomingMobilityProgrammeLevel());
        }

        checkRules();

        if (isIncoming()) {
            checkRulesForIncoming();
        } else {
            checkRulesForOutgoing();
        }
    }

    private void checkRules() {
        if (getRegistration() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.registration.required");
        }

        if (getBegin() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.begin.required");
        }

        if (getEnd() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.end.required");
        }

        if (getBegin().isAfter(getEnd())) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.end.must.be.after.begin");
        }

        if (getBegin().getExecutionYear().isBefore(getRegistration().getStartExecutionYear())
                || getEnd().getExecutionYear().isBefore(getRegistration().getStartExecutionYear())) {
            throw new ULisboaSpecificationsDomainException(
                    "error.MobilityRegistrationInformation.begin.and.end.must.be.after.registration.begin");
        }

        if (getMobilityProgramType() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.mobilityProgramType.required");
        }

        if (getMobilityActivityType() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.mobilityActivityType.required");
        }

        checkOverlaps();

    }

    private void checkOverlaps() {

        for (final MobilityRegistrationInformation information : MobilityRegistrationInformation.findAll(getRegistration())) {

            if (information == this) {
                continue;
            }

            if (information.getBegin() == null || information.getEnd() == null) {
                throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.period.overlaps");
            }

            if (getBegin().isAfter(information.getEnd()) || getEnd().isBefore(information.getBegin())) {
                continue;
            }

            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.period.overlaps");

        }

    }

    protected void checkRulesForOutgoing() {

        if (getCountryUnit() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.countryUnit.required");
        }

        if (getForeignInstitutionUnit() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.MobilityRegistrationInformation.foreignInstitutionUnit.required");
        }

    }

    private void checkRulesForIncoming() {

        if (getProgramDuration() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.programDuration.required");
        }

        if (getDegreeBased()) {

            if (getDegree() == null) {
                throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.degree.required");
            }

            if (getDegreeCurricularPlan() == null) {
                throw new ULisboaSpecificationsDomainException(
                        "error.MobilityRegistrationInformation.degreeCurricularPlan.required");
            }

        } else {

            if (getMobilityScientificArea() == null) {
                throw new ULisboaSpecificationsDomainException(
                        "error.MobilityRegistrationInformation.mobilityScientificArea.required");
            }

            if (getIncomingMobilityProgrammeLevel() == null) {
                throw new ULisboaSpecificationsDomainException(
                        "error.MobilityRegistrationInformation.incomingMobilityProgrammeLevel.required");
            }

        }

        if (getOriginMobilityProgrammeLevel() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.MobilityRegistrationInformation.originMobilityProgrammeLevel.required");
        }

    }

    public boolean isDeletable() {
        return true;
    }

    public boolean isIncoming() {
        return getIncoming();
    }

    @Atomic
    public void delete() {
        setRegistration(null);
        setBegin(null);
        setEnd(null);
        setMobilityProgramType(null);
        setMobilityActivityType(null);
        setCountryUnit(null);
        setForeignInstitutionUnit(null);
        setMobilityScientificArea(null);
        setIncomingMobilityProgrammeLevel(null);
        setOriginMobilityProgrammeLevel(null);

        setDegree(null);
        setDegreeCurricularPlan(null);
        setBranchCourseGroup(null);

        setBennu(null);

        deleteDomainObject();
    }

    public Country getCountry() {
        return getCountryUnit() != null ? Country
                .readByTwoLetterCode(getCountryUnit().getAcronym()) : getCountryByForeignInstitutionUnit();
    }

    @Deprecated
    private Country getCountryByForeignInstitutionUnit() {

        //legacy
        if (getForeignInstitutionUnit() == null) {
            return null;
        }

        Collection<? extends Party> parentParties =
                getForeignInstitutionUnit().getParentParties(AccountabilityTypeEnum.GEOGRAPHIC, CountryUnit.class);

        if (parentParties.size() > 1) {
            throw new ULisboaSpecificationsDomainException(
                    "error.MobilityRegistrationInformation.found.more.than.one.parent.country.of.foreign.unit");
        }

        if (parentParties.size() == 1) {
            if (((CountryUnit) parentParties.iterator().next()).getCountry() != null) {
                return ((CountryUnit) parentParties.iterator().next()).getCountry();
            } else if (!Strings.isNullOrEmpty(((CountryUnit) parentParties.iterator().next()).getAcronym())) {
                return Country.readByTwoLetterCode(((CountryUnit) parentParties.iterator().next()).getAcronym());
            }
        }

        return null;
    }

    public boolean hasCountry() {
        return getCountry() != null;
    }

    public boolean isIncomingStudent() {
        return getIncoming();
    }

    public LocalizedString getMobilityStudentTypeDescription() {
        if (isIncomingStudent()) {
            return ULisboaSpecificationsUtil.bundleI18N(
                    "label.org.fenixedu.ulisboa.specifications.dto.student.mobility.MobilityRegistrationInformationBean.incomingStudent");
        }

        return ULisboaSpecificationsUtil.bundleI18N(
                "label.org.fenixedu.ulisboa.specifications.dto.student.mobility.MobilityRegistrationInformationBean.outgoingStudent");
    }

    public boolean isValid(final ExecutionYear executionYear) {

        //Legacy - compatibility reasons only
        if (getBegin() == null || getEnd() == null) {
            return true;
        }

        return !executionYear.isAfter(getEnd().getExecutionYear()) && !executionYear.isBefore(getBegin().getExecutionYear());

    }

    private boolean isBeforeOrEquals(final ExecutionYear executionYear) {

        //Legacy - compatibility reasons only
        if (getBegin() == null || getEnd() == null) {
            return true;
        }

        return getEnd().getExecutionYear().isBeforeOrEquals(executionYear);
    }

    @Atomic
    public void markAsMainInformation() {
        getRegistration().getMobilityRegistrationInformationsSet().stream()
                .filter(m -> m.isIncoming() == isIncoming() && m.getNational() == getNational())
                .forEach(m -> m.setMainInformation(false));
        setMainInformation(true);
    }

    // Creates a mobility registration for an internal student which is going to other institution
    @Atomic
    public static MobilityRegistrationInformation create(final MobilityRegistrationInformationBean bean) {
        final MobilityRegistrationInformation result = new MobilityRegistrationInformation();
        result.edit(bean);

        return result;
    }

    public static Set<MobilityRegistrationInformation> findAll(final Registration registration) {
        return registration.getMobilityRegistrationInformationsSet();
    }

    public static boolean hasAnyInternationalOutgoingMobility(final Registration registration) {
        return hasAnyInternationalOutgoingMobilityUntil(registration, null);
    }

    public static boolean hasAnyInternationalOutgoingMobilityUntil(final Registration registration, final ExecutionYear until) {
        return registration.getMobilityRegistrationInformationsSet().stream()
                .filter(m -> until == null || m.isBeforeOrEquals(until)).anyMatch(m -> !m.isIncoming() && !m.getNational());
    }

    public static MobilityRegistrationInformation findMainInternationalOutgoingInformation(Registration registration) {
        return findMainInternationalOutgoingInformationUntil(registration, null);
    }

    public static MobilityRegistrationInformation findMainInternationalOutgoingInformationUntil(Registration registration,
            ExecutionYear until) {
        return findInternationalOutgoingInformationsUntil(registration, until).stream().filter(m -> m.getMainInformation())
                .findFirst().orElse(null);
    }

    public static Collection<MobilityRegistrationInformation> findInternationalOutgoingInformations(Registration registration) {
        return findInternationalOutgoingInformationsUntil(registration, null);

    }

    public static Collection<MobilityRegistrationInformation> findInternationalOutgoingInformationsUntil(
            Registration registration, ExecutionYear until) {
        return registration.getMobilityRegistrationInformationsSet().stream()
                .filter(m -> (until == null || m.isBeforeOrEquals(until)) && !m.isIncoming() && !m.getNational())
                .collect(Collectors.toSet());
    }

    public static MobilityRegistrationInformation findInternationalIncomingInformation(final Registration registration,
            final ExecutionYear executionYear) {
        return registration.getMobilityRegistrationInformationsSet().stream()
                .filter(m -> m.isIncoming() && !m.getNational() && m.isValid(executionYear)).sorted(COMPARATOR_BY_MOST_RECENT)
                .findFirst().orElse(null);
    }

}
