package org.fenixedu.ulisboa.specifications.dto.student.mobility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.organizationalStructure.PartyTypeEnum;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitUtils;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.mobility.MobilityActivityType;
import org.fenixedu.academic.domain.student.mobility.MobilityProgramType;
import org.fenixedu.academic.domain.student.mobility.MobilityProgrammeLevel;
import org.fenixedu.academic.domain.student.mobility.MobilityRegistrationInformation;
import org.fenixedu.academic.domain.student.mobility.MobilityScientificArea;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;

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
    protected MobilityRegistrationInformation mobilityRegistrationInformation;

    protected ExecutionInterval begin;
    protected ExecutionInterval end;
    protected LocalDate beginDate;
    protected LocalDate endDate;
    protected MobilityProgramType mobilityProgramType;
    protected MobilityActivityType mobilityActivityType;
    protected MobilityScientificArea mobilityScientificArea;
    protected MobilityProgrammeLevel incomingMobilityProgrammeLevel;
    protected MobilityProgrammeLevel originMobilityProgrammeLevel;
    protected boolean incoming;
    protected SchoolPeriodDuration programDuration;

    protected String otherIncomingMobilityProgrammeLevel;
    protected String otherOriginMobilityProgrammeLevel;

    protected Degree degree;
    protected DegreeCurricularPlan degreeCurricularPlan;
    protected CourseGroup branchCourseGroup;

    protected String remarks;
    protected Unit countryUnit;
    protected Unit foreignInstitutionUnit;

    protected boolean degreeBased;
    protected boolean national;

    private List<TupleDataSourceBean> programDurationDataSource;
    private List<TupleDataSourceBean> beginDataSource;
    private List<TupleDataSourceBean> endDataSource;
    private List<TupleDataSourceBean> mobilityProgramTypeDataSource;
    private List<TupleDataSourceBean> mobilityActivityTypeDataSource;
    private List<TupleDataSourceBean> foreignInstitutionUnitDataSource;

    private List<TupleDataSourceBean> mobilityScientificAreaDataSource;
    private List<TupleDataSourceBean> mobilityProgrammeLevelDataSource;

    private List<TupleDataSourceBean> degreeDataSource;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource;
    private List<TupleDataSourceBean> branchCourseGroupDataSource;
    private List<TupleDataSourceBean> countryUnitDataSource;

    public MobilityRegistrationInformationBean(final Registration registration) {
        this.setRegistration(registration);
        this.incoming = true;
        this.programDuration = SchoolPeriodDuration.YEAR;

        update();
    }

    public MobilityRegistrationInformationBean(final MobilityRegistrationInformation mobilityRegistrationInformation) {

        setRegistration(mobilityRegistrationInformation.getRegistration());
        setMobilityRegistrationInformation(mobilityRegistrationInformation);
        setIncoming(mobilityRegistrationInformation.getIncoming());
        setProgramDuration(mobilityRegistrationInformation.getProgramDuration());
        setBegin(mobilityRegistrationInformation.getBegin());
        setEnd(mobilityRegistrationInformation.getEnd());
        setBeginDate(mobilityRegistrationInformation.getBeginDate());
        setEndDate(mobilityRegistrationInformation.getEndDate());
        setMobilityProgramType(mobilityRegistrationInformation.getMobilityProgramType());
        setMobilityActivityType(mobilityRegistrationInformation.getMobilityActivityType());
        setMobilityScientificArea(mobilityRegistrationInformation.getMobilityScientificArea());
        setIncomingMobilityProgrammeLevel(mobilityRegistrationInformation.getIncomingMobilityProgrammeLevel());
        setOtherIncomingMobilityProgrammeLevel(mobilityRegistrationInformation.getOtherIncomingMobilityProgrammeLevel());
        setOriginMobilityProgrammeLevel(mobilityRegistrationInformation.getOriginMobilityProgrammeLevel());
        setOtherOriginMobilityProgrammeLevel(mobilityRegistrationInformation.getOtherOriginMobilityProgrammeLevel());

        setCountryUnit(mobilityRegistrationInformation.getCountryUnit());
        setForeignInstitutionUnit(mobilityRegistrationInformation.getForeignInstitutionUnit());

        setDegree(mobilityRegistrationInformation.getDegree());
        setDegreeCurricularPlan(mobilityRegistrationInformation.getDegreeCurricularPlan());
        setBranchCourseGroup(mobilityRegistrationInformation.getBranchCourseGroup());

        setRemarks(mobilityRegistrationInformation.getRemarks());
        setDegreeBased(mobilityRegistrationInformation.getDegreeBased());
        setNational(mobilityRegistrationInformation.getNational());

        update();

    }

    public void update() {
        loadDataProgramDurationDataSource();
        loadDataBeginDataSource();
        loadDataEndDataSource();
        loadDataMobilityProgramTypeDataSource();
        loadDataMobilityActivityTypeDataSource();
        loadMobilityScientificAreaDataSource();
        loadMobilityProgrammeLevelDataSource();
        loadDegreeDataSource();
        loadDegreeCurricularPlanDataSource();
        loadBranchCourseGroupDataSource();
        loadCountryUnitDataSource();
        loadDataForeignInstitutionUnitDataSource();
    }

    private void loadMobilityProgrammeLevelDataSource() {
        final List<TupleDataSourceBean> result = Bennu.getInstance().getMobilityProgrammeLevelsSet().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getName().getContent())).collect(Collectors.toList());

        mobilityProgrammeLevelDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadMobilityScientificAreaDataSource() {
        final List<TupleDataSourceBean> result = Bennu.getInstance().getMobilityScientificAreasSet().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getName().getContent())).collect(Collectors.toList());

        mobilityScientificAreaDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadDataForeignInstitutionUnitDataSource() {

        if (countryUnit != null) {
            final List<TupleDataSourceBean> result =
                    countryUnit.getAllSubUnits().stream()
                            .filter(su -> su.getPartyType() != null && (su.getPartyType().getType() == PartyTypeEnum.SCHOOL
                                    || su.getPartyType().getType() == PartyTypeEnum.UNIVERSITY))
                            .sorted(Unit.COMPARATOR_BY_NAME_AND_ID)
                            .map(su -> new TupleDataSourceBean(su.getExternalId(),
                                    (su.getCode() != null ? "[" + su.getCode() + "] " : "") + su.getName()))
                            .collect(Collectors.toList());

            foreignInstitutionUnitDataSource = result;

        } else {
            foreignInstitutionUnitDataSource = Collections.emptyList();
        }

    }

    private void loadDataMobilityProgramTypeDataSource() {
        final List<TupleDataSourceBean> result = MobilityProgramType.findAllActive().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getName().getContent())).collect(Collectors.toList());

        mobilityProgramTypeDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadDataMobilityActivityTypeDataSource() {
        final List<TupleDataSourceBean> result = MobilityActivityType.findAllActive().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getName().getContent())).collect(Collectors.toList());

        mobilityActivityTypeDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadDataEndDataSource() {
        final Comparator<ExecutionInterval> comparatorBySemesterAndYear = ExecutionInterval.COMPARATOR_BY_BEGIN_DATE.reversed();

        final List<TupleDataSourceBean> result = ExecutionInterval.findActiveChilds().stream().sorted(comparatorBySemesterAndYear)
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getQualifiedName())).collect(Collectors.toList());

        endDataSource = result.stream().collect(Collectors.toList());
    }

    private void loadDataBeginDataSource() {
        final Comparator<ExecutionInterval> comparatorBySemesterAndYear = ExecutionInterval.COMPARATOR_BY_BEGIN_DATE.reversed();

        final List<TupleDataSourceBean> result = ExecutionInterval.findActiveChilds().stream().sorted(comparatorBySemesterAndYear)
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getQualifiedName())).collect(Collectors.toList());

        beginDataSource = result.stream().collect(Collectors.toList());
    }

    private void loadDataProgramDurationDataSource() {
        final List<TupleDataSourceBean> result = Arrays.asList(SchoolPeriodDuration.values()).stream()
                .map(i -> new TupleDataSourceBean(i.name(), ULisboaSpecificationsUtil.bundle("label.SchoolPeriodDuration." + i)))
                .collect(Collectors.toList());

        programDurationDataSource = result.stream().sorted(COMPARE_BY_ID_AND_TEXT).collect(Collectors.toList());
    }

    private void loadDegreeDataSource() {

        final List<TupleDataSourceBean> result = new ArrayList<>(Bennu.getInstance().getDegreesSet().stream()
                .sorted(Degree.COMPARATOR_BY_NAME_AND_ID)
                .map((Degree d) -> new TupleDataSourceBean(d.getExternalId(), "[" + d.getCode() + "] " + d.getPresentationName()))
                .collect(Collectors.toList()));

        degreeDataSource = result;

    }

    private void loadDegreeCurricularPlanDataSource() {

        if (getDegree() != null) {
            final List<TupleDataSourceBean> result = new ArrayList<>(
                    getDegree().getDegreeCurricularPlansSet().stream().sorted(DegreeCurricularPlan.COMPARATOR_BY_NAME)
                            .map((DegreeCurricularPlan d) -> new TupleDataSourceBean(d.getExternalId(), d.getName()))
                            .collect(Collectors.toList()));

            degreeCurricularPlanDataSource = result;

        } else {
            degreeCurricularPlanDataSource = Collections.emptyList();
        }

    }

    private void loadBranchCourseGroupDataSource() {
        if (getDegree() != null && getDegreeCurricularPlan() != null) {
            final List<TupleDataSourceBean> result =
                    new ArrayList<>(getDegreeCurricularPlan().getAllBranches().stream().sorted(CourseGroup.COMPARATOR_BY_NAME)
                            .map((CourseGroup c) -> new TupleDataSourceBean(c.getExternalId(), c.getName()))
                            .collect(Collectors.toList()));

            branchCourseGroupDataSource = result;

        } else {
            branchCourseGroupDataSource = Collections.emptyList();
        }
    }

    private void loadCountryUnitDataSource() {
        final List<TupleDataSourceBean> result = new ArrayList<>(
                UnitUtils.readAllActiveUnitsByType(PartyTypeEnum.COUNTRY).stream().sorted(Unit.COMPARATOR_BY_NAME_AND_ID)
                        .map(c -> new TupleDataSourceBean(c.getExternalId(), c.getName())).collect(Collectors.toList()));

        countryUnitDataSource = result;

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

    public Set<ExecutionInterval> getExecutionSemesterListProvider() {
        return Sets.newHashSet(ExecutionInterval.findActiveChilds());
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

    public MobilityRegistrationInformation getMobilityRegistrationInformation() {
        return mobilityRegistrationInformation;
    }

    public void setMobilityRegistrationInformation(MobilityRegistrationInformation mobilityRegistrationInformation) {
        this.mobilityRegistrationInformation = mobilityRegistrationInformation;
    }

    public ExecutionInterval getBegin() {
        return begin;
    }

    public void setBegin(ExecutionInterval begin) {
        this.begin = begin;
    }

    public ExecutionInterval getEnd() {
        return end;
    }

    public void setEnd(ExecutionInterval end) {
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

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Degree getDegree() {
        return degree;
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return degreeCurricularPlan;
    }

    public CourseGroup getBranchCourseGroup() {
        return branchCourseGroup;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public void setDegreeCurricularPlan(DegreeCurricularPlan degreeCurricularPlan) {
        this.degreeCurricularPlan = degreeCurricularPlan;
    }

    public void setBranchCourseGroup(CourseGroup branchCourseGroup) {
        this.branchCourseGroup = branchCourseGroup;
    }

    public boolean isDegreeBased() {
        return degreeBased;
    }

    public void setDegreeBased(boolean degreeBased) {
        this.degreeBased = degreeBased;
    }

    public Unit getCountryUnit() {
        return countryUnit;
    }

    public void setCountryUnit(Unit countryUnit) {
        this.countryUnit = countryUnit;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isNational() {
        return national;
    }

    public void setNational(boolean national) {
        this.national = national;
    }

}
