package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.importation;

import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.ContingentToIngression;

public class ContingentMappingBean implements IBean {

    private String contingent;
    private IngressionType ingressionType;
    private ContingentToIngression contingentToIngression;

    public ContingentMappingBean() {

    }

    public ContingentMappingBean(final ContingentToIngression contingentMap) {
        this();
        setContingent(contingentMap.getContingent());
        setIngressionType(contingentMap.getIngressionType());
        setContingentToIngression(contingentMap);
    }

    public IngressionType getIngressionType() {
        return ingressionType;
    }

    public void setIngressionType(final IngressionType ingressionType) {
        this.ingressionType = ingressionType;
    }

    public String getContingent() {
        return contingent;
    }

    public void setContingent(final String contingent) {
        this.contingent = contingent;
    }

    public ContingentToIngression getContingentToIngression() {
        return contingentToIngression;
    }

    public void setContingentToIngression(final ContingentToIngression contingentToIngression) {
        this.contingentToIngression = contingentToIngression;
    }
}
