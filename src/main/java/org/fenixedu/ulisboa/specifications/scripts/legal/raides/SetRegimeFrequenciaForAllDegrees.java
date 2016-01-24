package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;

public class SetRegimeFrequenciaForAllDegrees extends CustomTask {

    @Override
    public void runTask() throws Exception {
        
        final LegalMapping legalMapping = LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.REGIME_FREQUENCIA);
        for (Degree degree : Bennu.getInstance().getDegreesSet()) {
            legalMapping.addEntry(degree.getExternalId(), "10");
        }
    }
}
