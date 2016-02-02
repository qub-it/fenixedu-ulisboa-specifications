package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.DomainObjectLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;

import pt.ist.fenixframework.FenixFramework;

public class SetformaIngressoTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        final DomainObjectLegalMapping legalMapping = (DomainObjectLegalMapping) LegalMapping.find(RaidesInstance.getInstance(),
                LegalMappingType.REGISTRATION_INGRESSION_TYPE);

        // Transfer
        legalMapping.addEntry((IngressionType) FenixFramework.getDomainObject("285250252963842"), "11");

        // Degree Change
        legalMapping.addEntry((IngressionType) FenixFramework.getDomainObject("285250252963843"), "12");

        // Cursos Superiores
        //legalMapping.addEntry((IngressionType) FenixFramework.getDomainObject("285250252963847"), "14");

    }

}
