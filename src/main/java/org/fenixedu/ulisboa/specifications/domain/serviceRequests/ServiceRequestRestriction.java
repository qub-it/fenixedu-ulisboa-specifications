package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

public class ServiceRequestRestriction extends ServiceRequestRestriction_Base {

    public ServiceRequestRestriction() {
        super();
    }

    public void delete() {
        setDegreeType(null);
        setDegree(null);
        setProgramConclusion(null);
        deleteDomainObject();
    }

    /*
     * TODO:
     *       Apagar o getDescription
     *       Implementar motor de validaçao das restrições.
     *       Modificar a logica dos provider de ServiceRequestTypes para respeitar as restriçoes
     */

    public String getDescription() {
        StringBuilder descBuilder = new StringBuilder();
        descBuilder.append("[");
        if (getDegreeType() != null) {
            descBuilder.append(getDegreeType().getCode());
        } else {
            descBuilder.append("_");
        }
        descBuilder.append("|");
        if (getDegree() != null) {
            descBuilder.append(getDegree().getCode());
        } else {
            descBuilder.append("_");
        }
        descBuilder.append("|");
        if (getProgramConclusion() != null) {
            descBuilder.append(getProgramConclusion().getCode());
        } else {
            descBuilder.append("_");
        }
        descBuilder.append("]");
        return descBuilder.toString();
    }

}
