package org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

public class ExtendedDegreeInfoBean implements IBean {

    private ExecutionYear executionYear;
    private List<TupleDataSourceBean> executionYearOptions;
    private Degree degree;
    private List<TupleDataSourceBean> degreeOptions;
    private String degreeAcron;
    private LocalizedString scientificAreas;
    private LocalizedString studyRegime;
    private LocalizedString studyProgrammeRequirements;
    private LocalizedString higherEducationAccess;
    private LocalizedString professionalStatus;
    private LocalizedString supplementExtraInformation;
    private LocalizedString supplementOtherSources;

    public ExtendedDegreeInfoBean() {
        setExecutionYear(ExecutionYear.readCurrentExecutionYear());
        setExecutionYearOptions(ExecutionYear.readNotClosedExecutionYears());

        Set<Degree> allDegrees = new TreeSet<Degree>(Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID);
        allDegrees.addAll(Bennu.getInstance().getDegreesSet());
        setDegree(allDegrees.stream().findFirst().orElse(null));
        setDegreeOptions(allDegrees);
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public List<TupleDataSourceBean> getExecutionYearOptions() {
        return executionYearOptions;
    }

    public void setExecutionYearOptions(Collection<ExecutionYear> executionYearOptions) {
        this.executionYearOptions = executionYearOptions.stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).map(ey -> {
            TupleDataSourceBean tupleDataSourceBean = new TupleDataSourceBean();
            tupleDataSourceBean.setId(ey.getExternalId());
            tupleDataSourceBean.setText(ey.getBeginCivilYear() + "/" + ey.getEndCivilYear());
            return tupleDataSourceBean;
        }).collect(Collectors.toList());
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public List<TupleDataSourceBean> getDegreeOptions() {
        return degreeOptions;
    }

    public void setDegreeOptions(Collection<Degree> degreeOptions) {
        this.degreeOptions = degreeOptions.stream().sorted(Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID).map(d -> {
            TupleDataSourceBean tupleDataSourceBean = new TupleDataSourceBean();
            tupleDataSourceBean.setId(d.getExternalId());
            tupleDataSourceBean.setText(d.getDegreeTypeName() + "-" + d.getNameI18N(getExecutionYear()).getContent());
            return tupleDataSourceBean;
        }).collect(Collectors.toList());
    }

    public String getDegreeAcron() {
        return degreeAcron;
    }

    public void setDegreeAcron(String degreeAcron) {
        this.degreeAcron = degreeAcron;
    }

    public LocalizedString getScientificAreas() {
        return scientificAreas;
    }

    public void setScientificAreas(LocalizedString scientificAreas) {
        this.scientificAreas = scientificAreas;
    }

    public LocalizedString getStudyRegime() {
        return studyRegime;
    }

    public void setStudyRegime(LocalizedString studyRegime) {
        this.studyRegime = studyRegime;
    }

    public LocalizedString getStudyProgrammeRequirements() {
        return studyProgrammeRequirements;
    }

    public void setStudyProgrammeRequirements(LocalizedString studyProgrammeRequirements) {
        this.studyProgrammeRequirements = studyProgrammeRequirements;
    }

    public LocalizedString getHigherEducationAccess() {
        return higherEducationAccess;
    }

    public void setHigherEducationAccess(LocalizedString higherEducationAccess) {
        this.higherEducationAccess = higherEducationAccess;
    }

    public LocalizedString getProfessionalStatus() {
        return professionalStatus;
    }

    public void setProfessionalStatus(LocalizedString professionalStatus) {
        this.professionalStatus = professionalStatus;
    }

    public LocalizedString getSupplementExtraInformation() {
        return supplementExtraInformation;
    }

    public void setSupplementExtraInformation(LocalizedString supplementExtraInformation) {
        this.supplementExtraInformation = supplementExtraInformation;
    }

    public LocalizedString getSupplementOtherSources() {
        return supplementOtherSources;
    }

    public void setSupplementOtherSources(LocalizedString supplementOtherSources) {
        this.supplementOtherSources = supplementOtherSources;
    }
}
