package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.ulisboa.specifications.domain.DisabilityType;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

public class DisabilitiesForm implements CandidancyForm {

    private boolean hasDisabilities = false;
    private DisabilityType disabilityType;
    private String otherDisabilityType;
    private boolean isOtherDisabilityType;
    private Boolean needsDisabilitySupport = null;
    private boolean firstYearRegistration;
    private boolean answered;

    private List<TupleDataSourceBean> disabilityTypeValues;

    public DisabilitiesForm() {
        updateLists();
    }

    @Override
    public void updateLists() {
        setDisabilityTypeValues(DisabilityType.readAll().collect(Collectors.toList()));
        if (disabilityType != null) {
            setOtherDisabilityType(disabilityType.isOther());
        } else {
            setOtherDisabilityType(false);
        }
    }

    public boolean getHasDisabilities() {
        return hasDisabilities;
    }

    public void setHasDisabilities(boolean hasDisabilities) {
        this.hasDisabilities = hasDisabilities;
    }

    public DisabilityType getDisabilityType() {
        return disabilityType;
    }

    public void setDisabilityType(DisabilityType disabilityType) {
        this.disabilityType = disabilityType;
    }

    public String getOtherDisabilityType() {
        return otherDisabilityType;
    }

    public void setOtherDisabilityType(String otherDisabilityType) {
        this.otherDisabilityType = otherDisabilityType;
    }

    public Boolean getNeedsDisabilitySupport() {
        return needsDisabilitySupport;
    }

    public void setNeedsDisabilitySupport(Boolean needsDisabilitySupport) {
        this.needsDisabilitySupport = needsDisabilitySupport;
    }

    public boolean isFirstYearRegistration() {
        return firstYearRegistration;
    }

    public void setFirstYearRegistration(boolean firstYearRegistration) {
        this.firstYearRegistration = firstYearRegistration;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public List<TupleDataSourceBean> getDisabilityTypeValues() {
        return disabilityTypeValues;
    }

    public void setDisabilityTypeValues(List<DisabilityType> disabilityTypeValues) {
        this.disabilityTypeValues = disabilityTypeValues.stream().map(dt -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(dt.getExternalId());
            tuple.setText(dt.getDescription().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public boolean isOtherDisabilityType() {
        return isOtherDisabilityType;
    }

    public void setOtherDisabilityType(boolean isOtherDisabilityType) {
        this.isOtherDisabilityType = isOtherDisabilityType;
    }
}
