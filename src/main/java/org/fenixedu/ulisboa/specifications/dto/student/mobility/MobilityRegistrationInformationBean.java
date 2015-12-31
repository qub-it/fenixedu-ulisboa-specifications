package org.fenixedu.ulisboa.specifications.dto.student.mobility;

import java.io.Serializable;
import java.util.Set;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityActivityType;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityProgramType;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityRegistrationInformation;

import com.google.common.collect.Sets;


@SuppressWarnings("serial")
public class MobilityRegistrationInformationBean implements Serializable {

    protected Registration registration;
    protected ExecutionSemester begin;
    protected ExecutionSemester end;
    protected MobilityProgramType mobilityProgramType;
    protected MobilityActivityType mobilityActivityType;
    protected Unit foreignInstitutionUnit;
    protected boolean incoming;
    protected SchoolPeriodDuration programDuration;

    public MobilityRegistrationInformationBean() {
        this.incoming = true;
    }
    
    public SchoolPeriodDuration getProgramDuration() {
        return programDuration;
    }

    public void setProgramDuration(SchoolPeriodDuration programDuration) {
        this.programDuration = programDuration;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public MobilityRegistrationInformationBean(final Registration registration) {
        setRegistration(registration);
    }

    public MobilityRegistrationInformationBean(final MobilityRegistrationInformation mobilityRegistrationInformation) {
        setRegistration(mobilityRegistrationInformation.getRegistration());
        setBegin(mobilityRegistrationInformation.getBegin());
        setEnd(mobilityRegistrationInformation.getEnd());
        setMobilityProgramType(mobilityRegistrationInformation.getMobilityProgramType());
        setMobilityActivityType(mobilityRegistrationInformation.getMobilityActivityType());
        setForeignInstitutionUnit(mobilityRegistrationInformation.getForeignInstitutionUnit());
        setIncoming(mobilityRegistrationInformation.getIncoming());
        setProgramDuration(mobilityRegistrationInformation.getProgramDuration());
    }

    public Set<ExecutionSemester> getExecutionSemesterListProvider() {
        return Sets.newHashSet(ExecutionSemester.readNotClosedExecutionPeriods());
    }

    public Set<MobilityProgramType> getMobilityProgramTypeListProvider() {
        return MobilityProgramType.findAll();
    }

    public Set<MobilityActivityType> getMobilityActivityTypeListProvider() {
        return MobilityActivityType.findAllActive();
    }

    /*
     * GETTERS & SETTERS
     */

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public ExecutionSemester getBegin() {
        return begin;
    }

    public void setBegin(ExecutionSemester begin) {
        this.begin = begin;
    }

    public ExecutionSemester getEnd() {
        return end;
    }

    public void setEnd(ExecutionSemester end) {
        this.end = end;
    }

    public MobilityProgramType getMobilityProgramType() {
        return mobilityProgramType;
    }

    public void setMobilityProgramType(MobilityProgramType mobilityProgramType) {
        this.mobilityProgramType = mobilityProgramType;
    }

    public MobilityActivityType getMobilityActivityType() {
        return mobilityActivityType;
    }

    public void setMobilityActivityType(MobilityActivityType mobilityActivityType) {
        this.mobilityActivityType = mobilityActivityType;
    }

    public Unit getForeignInstitutionUnit() {
        return foreignInstitutionUnit;
    }

    public void setForeignInstitutionUnit(Unit foreignInstitutionUnit) {
        this.foreignInstitutionUnit = foreignInstitutionUnit;
    }

}
