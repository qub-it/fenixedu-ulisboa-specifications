package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.FenixFramework;

public class RemoveNonBolonhaDegrees extends CustomTask {

    private static final String[] OIDS = { "285585260412935", "285585260412936", "285585260412937", "285585260412938", 
            "285585260412939", "285585260412940", "285585260412941"};

    @Override
    public void runTask() throws Exception {
        
        for(Degree d : Sets.newHashSet(RaidesInstance.getInstance().getDegreesToReportSet())) {
            RaidesInstance.getInstance().removeDegreesToReport(d);
        }

        for (final String id : OIDS) {
            RaidesInstance.getInstance().getDegreesToReportSet()
                    .addAll(((DegreeType) FenixFramework.getDomainObject(id)).getDegreeSet());
        }
    }

}
