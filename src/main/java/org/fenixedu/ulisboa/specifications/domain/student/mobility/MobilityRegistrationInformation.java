package org.fenixedu.ulisboa.specifications.domain.student.mobility;

import java.util.Collection;
import java.util.Set;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
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
import pt.ist.fenixframework.consistencyPredicates.ConsistencyPredicate;

public class MobilityRegistrationInformation extends MobilityRegistrationInformation_Base {

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

        if (getEnd() != null && getBegin().isAfter(getEnd())) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.end.must.be.after.begin");
        }

        if (getMobilityProgramType() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.mobilityProgramType.required");
        }

        if (getMobilityActivityType() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.mobilityActivityType.required");
        }

    }

    // Due to the joint of incoming and outgoing mobility students in the same class
    // We have different requirements acording to their situation
    // 10-03-2014 - Nuno Pinheiro
    protected void checkRulesForOutgoing() {

        if (getCountryUnit() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.countryUnit.required");
        }

        if (getForeignInstitutionUnit() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.MobilityRegistrationInformation.foreignInstitutionUnit.required");
        }

        for (final MobilityRegistrationInformation information : MobilityRegistrationInformation.readAll(getRegistration())) {
            if (information == this) {
                continue;
            }

            // Incoming mobility students do not have begin and end values
            if (information.getIncoming() == Boolean.FALSE && overlaps(information.getBegin(), information.getEnd())) {
                throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.period.overlaps");
            }
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

        // Check if there is only one incoming mobility information
        MobilityRegistrationInformation.readIncomingInformation(getRegistration());
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

    public static MobilityRegistrationInformation findIncomingInformation(final Registration registration) {
        return registration.getMobilityRegistrationInformationsSet().stream().filter(m -> m.isIncoming()).findAny().orElse(null);
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

    /*
     * OTHER METHODS
     */
    protected boolean overlaps(final ExecutionSemester begin, final ExecutionSemester end) {
        if (getEnd() != null && end != null && !(getBegin().isAfter(end) || getEnd().isBefore(begin))) {
            return true;
        }
        if (getEnd() == null && getBegin().isBefore(begin)) {
            return true;
        }
        if (end == null && begin.isBefore(begin)) {
            return true;
        }
        return false;
    }

    protected boolean insidePeriod(final ExecutionInterval executionInterval) {
        if (executionInterval instanceof ExecutionYear) {
            final ExecutionYear executionYear =
                    ExecutionInterval.assertExecutionIntervalType(ExecutionYear.class, executionInterval);
            final Set<ExecutionSemester> executionPeriodsSet = executionYear.getExecutionPeriodsSet();
            for (final ExecutionSemester executionSemester : executionPeriodsSet) {
                if (insidePeriod(executionSemester)) {
                    return true;
                }
            }
            return false;
        }
        return insidePeriod(ExecutionInterval.assertExecutionIntervalType(ExecutionSemester.class, executionInterval));
    }

    protected boolean insidePeriod(final ExecutionSemester executionSemester) {
        return getBegin().isBeforeOrEquals(executionSemester)
                && (getEnd() == null || getEnd().isAfterOrEquals(executionSemester));
    }

    // Due to the migration of the legacy PrecedentDegreeInformation, there were MobilityRegistrationInformation created for incoming mobility students 
    // that did not have information about the origin institution
    // This consistency predicate assusres the 1..1 multiplicity of the foreignInstitution in the case on outgoing mantaining the previous semantics
    //TODO check this same condition for other relations
    @ConsistencyPredicate
    protected boolean checkOutgoingForeignInstitution() {
        return getIncoming() || getForeignInstitutionUnit() != null;
    }

    public static MobilityRegistrationInformation readIncomingInformation(final Registration registration) {
        MobilityRegistrationInformation result = null;

        for (final MobilityRegistrationInformation iter : registration.getMobilityRegistrationInformationsSet()) {
            if (!iter.isIncoming()) {
                continue;
            }

            if (result != null) {
                throw new ULisboaSpecificationsDomainException(
                        "error.MobilityRegistrationInformation.more.than.one.incoming.information.on.registration");
            }

            result = iter;
        }

        return result;
    }

    // Creates a mobility registration for an internal student which is going to other institution
    @Atomic
    public static MobilityRegistrationInformation create(final MobilityRegistrationInformationBean bean) {
        final MobilityRegistrationInformation result = new MobilityRegistrationInformation();
        result.edit(bean);

        return result;
    }

    public static Set<MobilityRegistrationInformation> readAll(final Registration registration) {
        return registration.getMobilityRegistrationInformationsSet();
    }

    public static MobilityRegistrationInformation findMobilityRegistrationInformation(final Registration registration,
            final ExecutionInterval executionInterval) {
        final Set<MobilityRegistrationInformation> mobilityInformations = readAll(registration);
        for (final MobilityRegistrationInformation mobilityRegistrationInformation : mobilityInformations) {
            if (mobilityRegistrationInformation.insidePeriod(executionInterval)) {
                return mobilityRegistrationInformation;
            }
        }
        return null;
    }

    public static boolean hasBeenInMobility(final Registration registration, final ExecutionYear executionYear) {
        return findMobilityRegistrationInformation(registration, executionYear) != null;
    }

    
}
