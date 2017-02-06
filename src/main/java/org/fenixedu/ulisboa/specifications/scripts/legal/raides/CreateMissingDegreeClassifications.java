package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.raides.DegreeClassification;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class CreateMissingDegreeClassifications extends CustomTask {

    @Override
    public void runTask() throws Exception {

        if (DegreeClassification.readByCode("EL") == null) {
            taskLog("Creating EL");
            new DegreeClassification("EL", "EL", "EL", "EL");
        }

        if (DegreeClassification.readByCode("DL") == null) {
            taskLog("Creating DL");
            new DegreeClassification("DL", "DL", "DL", "DL");
        }

        if (DegreeClassification.readByCode("DD") == null) {
            taskLog("Creating DD");
            new DegreeClassification("DD", "DD", "DD", "DD");
        }

        if (DegreeClassification.readByCode("MD") == null) {
            taskLog("Creating MD");
            new DegreeClassification("MD", "MD", "MD", "MD");
        }

        if (DegreeClassification.readByCode("EB") == null) {
            taskLog("Creating EB");
            new DegreeClassification("EB", "EB", "EB", "EB");
        }

        if (DegreeClassification.readByCode("DB") == null) {
            taskLog("Creating DB");
            new DegreeClassification("DB", "DB", "DB", "DB");
        }

        if (DegreeClassification.readByCode("BC") == null) {
            taskLog("Creating BC");
            new DegreeClassification("BC", "BC", "BC", "BC");
        }
    }

}
