package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class SetCountryHighSchool extends CustomTask {

    @Override
    public void runTask() throws Exception {
        
        taskLog("TOTAL PDI: " + Bennu.getInstance().getPrecedentDegreeInformationSet().size());
        
        
        int changeByPDISchoolLevelCountry = 0;
        for (final PrecedentDegreeInformation pdi : Bennu.getInstance().getPrecedentDegreeInformationSet()) {
            if(pdi.getRegistration() != null) {
                if(pdi.getRegistration() .getStudent().getPerson().getCountryHighSchool() != null) {
                    continue;
                } 
            } else if(pdi.getStudentCandidacy() != null) {
                if(pdi.getStudentCandidacy().getPerson().getCountryHighSchool() != null) {
                    continue;
                }                 
            }
            
            if(pdi.getSchoolLevel() == null || pdi.getSchoolLevel() != SchoolLevelType.HIGH_SCHOOL_OR_EQUIVALENT) {
                continue;
            }
            
            if(pdi.getCountry() == null) {
                continue;
            }
            
            if(pdi.getRegistration() != null) {
                pdi.getRegistration().getStudent().getPerson().setCountryHighSchool(pdi.getCountry());
            } else if(pdi.getStudentCandidacy() != null) {
                pdi.getStudentCandidacy().getPerson().setCountryHighSchool(pdi.getCountry());
            }
            
            
            changeByPDISchoolLevelCountry++;
        }
        
        taskLog("CHANGE BY PDI SCHOOL LEVEL COUNTRY: " + changeByPDISchoolLevelCountry);
        
        
        int changeByPDICountryHighSchool = 0;
        for (final PrecedentDegreeInformation pdi : Bennu.getInstance().getPrecedentDegreeInformationSet()) {
            if(pdi.getRegistration() != null) {
                if(pdi.getRegistration() .getStudent().getPerson().getCountryHighSchool() != null) {
                    continue;
                } 
            } else if(pdi.getStudentCandidacy() != null) {
                if(pdi.getStudentCandidacy().getPerson().getCountryHighSchool() != null) {
                    continue;
                }                 
            }
            
            if(pdi.getCountryHighSchool() == null) {
                continue;
            }

            if(pdi.getRegistration() != null) {
                pdi.getRegistration().getStudent().getPerson().setCountryHighSchool(pdi.getCountryHighSchool());
            } else if(pdi.getStudentCandidacy() != null) {
                pdi.getStudentCandidacy().getPerson().setCountryHighSchool(pdi.getCountryHighSchool());
            }
            
            changeByPDICountryHighSchool++;
        }
        
        taskLog("CHANGE BY PDI COUNTRY HIGH SCHOOL: " + changeByPDICountryHighSchool);
    }

}
