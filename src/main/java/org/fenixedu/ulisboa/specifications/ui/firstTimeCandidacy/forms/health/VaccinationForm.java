package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health;

import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class VaccinationForm implements CandidancyForm {

    private static final long serialVersionUID = 1L;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate vaccinationValidity;

    public VaccinationForm() {
        updateLists();
    }

    @Override
    public void updateLists() {
    }

    public LocalDate getVaccinationValidity() {
        return vaccinationValidity;
    }

    public void setVaccinationValidity(LocalDate vaccinationValidity) {
        this.vaccinationValidity = vaccinationValidity;
    }

}
