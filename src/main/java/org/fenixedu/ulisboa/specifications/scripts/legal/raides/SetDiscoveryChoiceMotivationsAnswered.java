package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;

public class SetDiscoveryChoiceMotivationsAnswered extends CustomTask {

    @Override
    public void runTask() throws Exception {
        
        taskLog("Total Students: " + Bennu.getInstance().getStudentsSet().size());
        
        int answered = 0;
        for (final Student student : Bennu.getInstance().getStudentsSet()) {
            
            if(student.getPerson().getPersonUlisboaSpecifications() == null) {
                continue;
            }
            
            final PersonUlisboaSpecifications specifications = student.getPerson().getPersonUlisboaSpecifications();
            
            if(specifications.getUniversityDiscoveryMeansAnswersSet().isEmpty() && specifications.getUniversityDiscoveryMeansAnswersSet().isEmpty()) {
                continue;
            }
            
            specifications.setMotivationsExpectationsFormAnswered(true);
            
            answered++;
        }
        
        taskLog("Answered: " + answered);
        
    }

}
