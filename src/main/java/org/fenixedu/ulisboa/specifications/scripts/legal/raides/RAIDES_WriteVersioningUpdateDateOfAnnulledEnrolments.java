package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.util.Constants;

public class RAIDES_WriteVersioningUpdateDateOfAnnulledEnrolments extends CustomTask {

    private static final String T = Constants.DATE_TIME_FORMAT_YYYY_MM_DD;

    @Override
    public void runTask() throws Exception {
        doIt();
        
        throw new RuntimeException("abort");
    }

    private void doIt() {
        final ExecutionYear ex = ExecutionYear.readCurrentExecutionYear();
        
        for (final ExecutionSemester es : ex.getExecutionPeriodsSet()) {
            for (final Enrolment enrolment : es.getEnrolmentsSet()) {
                if(!enrolment.isAnnulled()) {
                    continue;
                }
                
                taskLog("I\tANNULLED ENROLMENT\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n", 
                        enrolment.getExternalId(),
                        enrolment.getCode(),
                        enrolment.getEnrollmentState(),
                        enrolment.getName().getContent(),
                        enrolment.getExecutionPeriod().getQualifiedName(),
                        enrolment.getStudent().getNumber(),
                        enrolment.getRegistration().getDegree().getPresentationNameI18N().getContent(),
                        enrolment.getEctsCredits(),
                        enrolment.getAnnulmentDate() != null ? enrolment.getAnnulmentDate().toString(T) : "",                        
                        enrolment.getVersioningUpdateDate().getDate().toString(T));
                
                if(enrolment.getAnnulmentDate() == null) {
                    enrolment.setAnnulmentDate(enrolment.getVersioningUpdateDate().getDate());
                    
                    taskLog("C\tSET ANNULMENT DATE\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n", 
                            enrolment.getExternalId(),
                            enrolment.getCode(),
                            enrolment.getEnrollmentState(),
                            enrolment.getName().getContent(),
                            enrolment.getExecutionPeriod().getQualifiedName(),
                            enrolment.getStudent().getNumber(),
                            enrolment.getRegistration().getDegree().getPresentationNameI18N().getContent(),
                            enrolment.getEctsCredits(),
                            enrolment.getAnnulmentDate() != null ? enrolment.getAnnulmentDate().toString(T) : "",
                            enrolment.getVersioningUpdateDate().getDate().toString(T));
                }
            }
        }
    }
    
}
