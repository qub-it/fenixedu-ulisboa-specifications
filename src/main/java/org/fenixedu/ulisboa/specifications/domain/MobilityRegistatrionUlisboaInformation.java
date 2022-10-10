package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class MobilityRegistatrionUlisboaInformation extends MobilityRegistatrionUlisboaInformation_Base {

    protected MobilityRegistatrionUlisboaInformation() {
        super();
        setBennu(Bennu.getInstance());
    }

//    @Atomic
//    public void edit(final MobilityForm bean) {
//        setBegin(bean.getBegin());
//        setBeginDate(bean.getBeginDate());
//        setEnd(bean.getEnd());
//        setEndDate(bean.getEndDate());
//        setMobilityProgramType(bean.getMobilityProgramType());
//        setMobilityActivityType(bean.getMobilityActivityType());
//        setOriginMobilityProgrammeLevel(bean.getOriginMobilityProgrammeLevel());
//        setOtherOriginMobilityProgrammeLevel(bean.getOtherOriginMobilityProgrammeLevel());
//        setProgramDuration(bean.getProgramDuration());
//
//        setMobilityScientificArea(bean.getMobilityScientificArea());
//        setIncomingMobilityProgrammeLevel(bean.getIncomingMobilityProgrammeLevel());
//        setOtherIncomingMobilityProgrammeLevel(bean.getOtherIncomingMobilityProgrammeLevel());
//
//        setIncomingCountry(bean.getIncomingCountry());
//        setOriginCountry(bean.getOriginCountry());
//
//        checkRules();
//    }

//    private void checkRules() {
//        if (getPersonUlisboaSpecification() == null) {
//            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.registration.required");
//        }
//
//        if (getBegin() == null) {
//            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.begin.required");
//        }
//
//        if (getEnd() == null) {
//            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.end.required");
//        }
//
//        if (getBegin().isAfter(getEnd())) {
//            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.end.must.be.after.begin");
//        }
//
//        if (getMobilityProgramType() == null) {
//            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.mobilityProgramType.required");
//        }
//
//        if (getMobilityActivityType() == null) {
//            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.mobilityActivityType.required");
//        }
//
//        if (getMobilityScientificArea() == null) {
//            throw new ULisboaSpecificationsDomainException(
//                    "error.MobilityRegistrationInformation.mobilityScientificArea.required");
//        }
//
//        if (getIncomingMobilityProgrammeLevel() == null) {
//            throw new ULisboaSpecificationsDomainException(
//                    "error.MobilityRegistrationInformation.incomingMobilityProgrammeLevel.required");
//        }
//
//        if (getOriginMobilityProgrammeLevel() == null) {
//            throw new ULisboaSpecificationsDomainException(
//                    "error.MobilityRegistrationInformation.originMobilityProgrammeLevel.required");
//        }
//    }

    @Atomic
    public void delete() {
        setPersonUlisboaSpecification(null);
        setBegin(null);
        setEnd(null);
        setMobilityProgramType(null);
        setMobilityActivityType(null);
        setMobilityScientificArea(null);
        setIncomingMobilityProgrammeLevel(null);
        setOriginMobilityProgrammeLevel(null);

        setBennu(null);

        setOriginCountry(null);
        setIncomingCountry(null);

        deleteDomainObject();
    }

//    @Atomic
//    public static MobilityRegistatrionUlisboaInformation create(final PersonUlisboaSpecifications personUlisboaSpecification,
//            final MobilityForm bean) {
//        final MobilityRegistatrionUlisboaInformation result = new MobilityRegistatrionUlisboaInformation();
//        result.setPersonUlisboaSpecification(personUlisboaSpecification);
//        result.edit(bean);
//
//        return result;
//    }

}
