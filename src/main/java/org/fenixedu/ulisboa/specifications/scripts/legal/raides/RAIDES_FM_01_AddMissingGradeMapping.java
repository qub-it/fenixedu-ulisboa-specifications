package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.LegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.StringLegalMapping;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;

public class RAIDES_FM_01_AddMissingGradeMapping extends CustomTask {

    @Override
    public void runTask() throws Exception {
        addNotaMapping();
        
        throw new RuntimeException("Abort");
    }

    private void addNotaMapping() {
        final String APROVADO_DISTINCAO = "AD";

        final StringLegalMapping legalMapping =
                (StringLegalMapping) LegalMapping.find(RaidesInstance.getInstance(), LegalMappingType.GRADE);
        
        taskLog("B\t%d\n", legalMapping.getLegalMappingEntriesSet().size());

        legalMapping.addEntry(APROVADO_DISTINCAO, "26");

        taskLog("A\t%d\n", legalMapping.getLegalMappingEntriesSet().size());
    }

}
