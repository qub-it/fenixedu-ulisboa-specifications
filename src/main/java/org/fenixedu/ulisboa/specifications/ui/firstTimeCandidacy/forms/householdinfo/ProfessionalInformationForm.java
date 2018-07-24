package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

public class ProfessionalInformationForm implements CandidancyForm {

    private ExecutionYear executionYear;
    private ProfessionalSituationConditionType professionalCondition;
    private String profession;
    private ProfessionType professionType;
    private ProfessionTimeType professionTimeType;
    private List<TupleDataSourceBean> professionalConditionValues;
    private List<TupleDataSourceBean> professionTypeValues;
    private List<TupleDataSourceBean> professionTimeTypeValues;

    public ProfessionalInformationForm() {
        setProfessionalConditionValues(Arrays.asList(ProfessionalSituationConditionType.values()));
        setProfessionTypeValues(Arrays.asList(ProfessionType.values()));
        setProfessionTimeTypeValues(ProfessionTimeType.readAll().collect(Collectors.toList()));

        updateLists();
    }

    @Override
    public void updateLists() {

    }

    public ProfessionType getProfessionType() {
        return professionType;
    }

    public void setProfessionType(final ProfessionType professionType) {
        this.professionType = professionType;
    }

    public ProfessionalSituationConditionType getProfessionalCondition() {
        return professionalCondition;
    }

    public void setProfessionalCondition(final ProfessionalSituationConditionType professionalCondition) {
        this.professionalCondition = professionalCondition;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(final String profession) {
        this.profession = profession;
    }

    public ProfessionTimeType getProfessionTimeType() {
        return professionTimeType;
    }

    public void setProfessionTimeType(final ProfessionTimeType professionTimeType) {
        this.professionTimeType = professionTimeType;
    }

    public boolean isStudentWorking() {
        if (isWorkingCondition()) {
            return true;
        }
        if (!StringUtils.isEmpty(getProfession())) {
            return true;
        }
        if (getProfessionTimeType() != null) {
            return true;
        }
        if (isWorkingProfessionType()) {
            return true;
        }
        return false;
    }

    private boolean isWorkingCondition() {
        if (getProfessionalCondition() == null) {
            return false;
        }
        switch (getProfessionalCondition()) {
        case WORKS_FOR_OTHERS:
            return true;
        case EMPLOYEER:
            return true;
        case INDEPENDENT_WORKER:
            return true;
        case WORKS_FOR_FAMILY_WITHOUT_PAYMENT:
            return true;
        case HOUSEWIFE:
            return true;
        case MILITARY_SERVICE:
            return true;
        default:
            return false;
        }
    }

    private boolean isWorkingProfessionType() {
        if (getProfessionType() == null) {
            return false;
        }
        switch (getProfessionType()) {
        case UNKNOWN:
            return false;
        case OTHER:
            return false;
        default:
            return true;
        }
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(final ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public List<TupleDataSourceBean> getProfessionalConditionValues() {
        return professionalConditionValues;
    }

    public void setProfessionalConditionValues(final List<ProfessionalSituationConditionType> professionalConditionValues) {
        this.professionalConditionValues = professionalConditionValues.stream()
                .map(psct -> new TupleDataSourceBean(psct.toString(), psct.getLocalizedName())).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getProfessionTypeValues() {
        return professionTypeValues;
    }

    public void setProfessionTypeValues(final List<ProfessionType> professionTypeValues) {
        this.professionTypeValues = professionTypeValues.stream()
                .map(pt -> new TupleDataSourceBean(pt.toString(), pt.getLocalizedName())).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getProfessionTimeTypeValues() {
        return professionTimeTypeValues;
    }

    public void setProfessionTimeTypeValues(final List<ProfessionTimeType> professionTimeTypeValues) {
        this.professionTimeTypeValues = professionTimeTypeValues.stream()
                .map(ptt -> new TupleDataSourceBean(ptt.getExternalId(), ptt.getDescription().getContent()))
                .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

}
