package org.fenixedu.ulisboa.specifications.dto.student.mobility;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityActivityType;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityProgramType;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityProgrammeLevel;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityRegistrationInformation;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityScientificArea;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import com.google.common.collect.Sets;

@SuppressWarnings("serial")
public class MobilityRegistrationInformationBean implements Serializable, IBean {

    private static final Comparator<TupleDataSourceBean> COMPARE_BY_ID_AND_TEXT = new Comparator<TupleDataSourceBean>() {

        @Override
        public int compare(final TupleDataSourceBean o1, final TupleDataSourceBean o2) {
            if (o1.getId() == "") {
                return -1;
            } else if (o2.getId() == "") {
                return 1;
            }

            return TupleDataSourceBean.COMPARE_BY_TEXT.compare(o1, o2);
        }
    };

    protected Registration registration;
    protected ExecutionSemester begin;
    protected ExecutionSemester end;
    protected MobilityProgramType mobilityProgramType;
    protected MobilityActivityType mobilityActivityType;
    protected MobilityScientificArea mobilityScientificArea;
    protected MobilityProgrammeLevel incomingMobilityProgrammeLevel;
    protected MobilityProgrammeLevel originMobilityProgrammeLevel;
    protected Unit foreignInstitutionUnit;
    protected boolean incoming;
    protected SchoolPeriodDuration programDuration;
    
    protected String otherIncomingMobilityProgrammeLevel;
    protected String otherOriginMobilityProgrammeLevel;

    private List<TupleDataSourceBean> programDurationDataSource;
    private List<TupleDataSourceBean> beginDataSource;
    private List<TupleDataSourceBean> endDataSource;
    private List<TupleDataSourceBean> mobilityProgramTypeDataSource;
    private List<TupleDataSourceBean> mobilityActivityTypeDataSource;
    private List<TupleDataSourceBean> foreignInstitutionUnitDataSource;
    
    private List<TupleDataSourceBean> mobilityScientificAreaDataSource;
    private List<TupleDataSourceBean> mobilityProgrammeLevelDataSource;
    

    public MobilityRegistrationInformationBean(final Registration registration) {
        this.setRegistration(registration);
        this.incoming = true;
        this.programDuration = SchoolPeriodDuration.YEAR;

        loadDataProgramDurationDataSource();
        loadDataBeginDataSource();
        loadDataEndDataSource();
        loadDataMobilityProgramTypeDataSource();
        loadDataMobilityActivityTypeDataSource();
        loadDataForeignInstitutionUnitDataSource();
        loadMobilityScientificAreaDataSource();
        loadMobilityProgrammeLevelDataSource();
    }

    private void loadMobilityProgrammeLevelDataSource() {
        final List<TupleDataSourceBean> result = Bennu.getInstance().getMobilityProgrammeLevelsSet().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getName().getContent())).collect(Collectors.toList());

        result.add(Constants.SELECT_OPTION);

        mobilityProgrammeLevelDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadMobilityScientificAreaDataSource() {
        final List<TupleDataSourceBean> result = Bennu.getInstance().getMobilityScientificAreasSet().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getName().getContent())).collect(Collectors.toList());

        result.add(Constants.SELECT_OPTION);

        mobilityScientificAreaDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadDataForeignInstitutionUnitDataSource() {
        final List<TupleDataSourceBean> result = Unit.readAllUnits().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getNameI18n().getContent()))
                .collect(Collectors.toList());

        result.add(Constants.SELECT_OPTION);

        foreignInstitutionUnitDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadDataMobilityProgramTypeDataSource() {
        final List<TupleDataSourceBean> result = MobilityProgramType.findAllActive().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getName().getContent())).collect(Collectors.toList());

        result.add(Constants.SELECT_OPTION);

        mobilityProgramTypeDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadDataMobilityActivityTypeDataSource() {
        final List<TupleDataSourceBean> result = MobilityActivityType.findAllActive().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getName().getContent())).collect(Collectors.toList());

        result.add(Constants.SELECT_OPTION);

        mobilityActivityTypeDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadDataEndDataSource() {
        final Comparator<ExecutionSemester> comparatorBySemesterAndYear =
                Collections.reverseOrder(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR);
        
        final List<TupleDataSourceBean> result = ExecutionSemester.readNotClosedExecutionPeriods().stream()
                .sorted(comparatorBySemesterAndYear)
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getQualifiedName())).collect(Collectors.toList());

        result.add(Constants.SELECT_OPTION);

        endDataSource = result.stream().collect(Collectors.toList());
    }

    private void loadDataBeginDataSource() {
        final Comparator<ExecutionSemester> comparatorBySemesterAndYear =
                Collections.reverseOrder(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR);
        
        final List<TupleDataSourceBean> result = ExecutionSemester.readNotClosedExecutionPeriods().stream()
                .sorted(comparatorBySemesterAndYear)
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getQualifiedName())).collect(Collectors.toList());

        result.add(Constants.SELECT_OPTION);

        beginDataSource = result.stream().collect(Collectors.toList());
    }

    private void loadDataProgramDurationDataSource() {
        final List<TupleDataSourceBean> result =
                Arrays.asList(SchoolPeriodDuration.values()).stream()
                        .map(i -> new TupleDataSourceBean(i.name(),
                                ULisboaSpecificationsUtil.bundle("label.SchoolPeriodDuration." + i)))
                .collect(Collectors.toList());

        result.add(Constants.SELECT_OPTION);

        programDurationDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
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

    public List<TupleDataSourceBean> getProgramDurationDataSource() {
        return programDurationDataSource;
    }

    public List<TupleDataSourceBean> getBeginDataSource() {
        return beginDataSource;
    }

    public List<TupleDataSourceBean> getEndDataSource() {
        return endDataSource;
    }

    public List<TupleDataSourceBean> getMobilityActivityTypeDataSource() {
        return mobilityActivityTypeDataSource;
    }

    public List<TupleDataSourceBean> getMobilityProgramTypeDataSource() {
        return mobilityProgramTypeDataSource;
    }

    public List<TupleDataSourceBean> getForeignInstitutionUnitDataSource() {
        return foreignInstitutionUnitDataSource;
    }
    
    public MobilityScientificArea getMobilityScientificArea() {
        return mobilityScientificArea;
    }
    
    public void setMobilityScientificArea(MobilityScientificArea mobilityScientificArea) {
        this.mobilityScientificArea = mobilityScientificArea;
    }
    
    public MobilityProgrammeLevel getIncomingMobilityProgrammeLevel() {
        return incomingMobilityProgrammeLevel;
    }
    
    public void setIncomingMobilityProgrammeLevel(MobilityProgrammeLevel incomingMobilityProgrammeLevel) {
        this.incomingMobilityProgrammeLevel = incomingMobilityProgrammeLevel;
    }
    
    public MobilityProgrammeLevel getOriginMobilityProgrammeLevel() {
        return originMobilityProgrammeLevel;
    }
    
    public void setOriginMobilityProgrammeLevel(MobilityProgrammeLevel originMobilityProgrammeLevel) {
        this.originMobilityProgrammeLevel = originMobilityProgrammeLevel;
    }
    
    public String getOtherIncomingMobilityProgrammeLevel() {
        return otherIncomingMobilityProgrammeLevel;
    }
    
    public void setOtherIncomingMobilityProgrammeLevel(String otherIncomingMobilityProgrammeLevel) {
        this.otherIncomingMobilityProgrammeLevel = otherIncomingMobilityProgrammeLevel;
    }
    
    public String getOtherOriginMobilityProgrammeLevel() {
        return otherOriginMobilityProgrammeLevel;
    }
    
    public void setOtherOriginMobilityProgrammeLevel(String otherOriginMobilityProgrammeLevel) {
        this.otherOriginMobilityProgrammeLevel = otherOriginMobilityProgrammeLevel;
    }
}
