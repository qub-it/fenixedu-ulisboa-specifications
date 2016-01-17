package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;

public class Raides {

    /**
     * Check if the previous completed qualification fields are filled
     */
    public static Set<String> verifyCompletePrecedentDegreeInformationFieldsFilled(final Registration registration) {
        final PrecedentDegreeInformation pid = registration.getStudentCandidacy().getPrecedentDegreeInformation();

        final Set<String> result = new HashSet<String>();
        
        if(Strings.isNullOrEmpty(pid.getConclusionGrade())) {
            result.add("error.Raides.verifyCompletePrecedentDegreeInformationFieldsFilled.conclusion.grade.required");
        }
        
        if(pid.getConclusionYear() == null) {
            result.add("error.Raides.verifyCompletePrecedentDegreeInformationFieldsFilled.conclusion.year.required");            
        }
        
        if(pid.getSchoolLevel() == null) {
            result.add("error.Raides.verifyCompletePrecedentDegreeInformationFieldsFilled.school.level.required");
        }
        
        if(pid.getSchoolLevel() == SchoolLevelType.OTHER && Strings.isNullOrEmpty(pid.getOtherSchoolLevel())) {
            result.add("error.Raides.verifyCompletePrecedentDegreeInformationFieldsFilled.school.level.required");            
        }
        
        if(pid.getCountry() == null) {
            result.add("error.Raides.verifyCompletePrecedentDegreeInformationFieldsFilled.country.required");
        }
        
        if(pid.getInstitution() == null) {
            result.add("error.Raides.verifyCompletePrecedentDegreeInformationFieldsFilled.institution.required");            
        }
        
        return result;
    }
    
    public static boolean isCompletePrecedentDegreeInformationFieldsFilled(final Registration registration) {
        return verifyCompletePrecedentDegreeInformationFieldsFilled(registration).isEmpty();
    }
    
    // TODO: Use mappings to infer is previous degree information is required
    public static boolean isPreviousDegreePrecedentDegreeInformationRequired(final Registration registration) {
        // For now use degree transfer
        return registration.getIngressionType().isExternalDegreeChange();
    }
    
    /**
     * In case of degree transfer, degree change and mobility check if previous degree or origin
     * information is filled
     */
    
    public static Set<String> verifyPreviousDegreePrecedentDegreeInformationFieldsFilled(final Registration registration) {
        final Set<String> result = new HashSet<String>();

        if(isPreviousDegreePrecedentDegreeInformationRequired(registration)) {
            return result;
        }
        
        final PrecedentDegreeInformation pid = registration.getStudentCandidacy().getPrecedentDegreeInformation();

        
        if(Strings.isNullOrEmpty(pid.getPrecedentDegreeDesignation())) {
            result.add("error.Raides.verifyPreviousDegreePrecedentDegreeInformationFieldsFilled.precedentDegreeDesignation.required");
        }
        
        if(pid.getPrecedentSchoolLevel() == null) {
            result.add("error.Raides.verifyPreviousDegreePrecedentDegreeInformationFieldsFilled.precedentSchoolLevel.required");
        }
        
        if(pid.getPrecedentInstitution() == null) {
            result.add("error.Raides.verifyPreviousDegreePrecedentDegreeInformationFieldsFilled.precedentInstitution.required");
        }
        
        if(pid.getPrecedentCountry() == null) {
            result.add("error.Raides.verifyPreviousDegreePrecedentDegreeInformationFieldsFilled.precedentCountry.required");
        }
        
        return result;
    }

    public static List<Registration> findActiveRegistrationsWithEnrolments(final Student student) {
        final List<Registration> result = Lists.newArrayList();
        
        for (final Registration registration : student.getRegistrationsSet()) {
            if(!registration.isActive()) {
                continue;
            }
            
            if(registration.getEnrolments(ExecutionYear.readCurrentExecutionYear()).isEmpty()) {
                continue;
            }
            
            result.add(registration);
        }
        
        Collections.sort(result, Registration.COMPARATOR_BY_START_DATE);
        
        return result;
    }
    
    public static boolean isPreviousDegreePrecedentDegreeInformationFieldsFilled(final Registration registration) {
        return verifyPreviousDegreePrecedentDegreeInformationFieldsFilled(registration).isEmpty();
    }

}
