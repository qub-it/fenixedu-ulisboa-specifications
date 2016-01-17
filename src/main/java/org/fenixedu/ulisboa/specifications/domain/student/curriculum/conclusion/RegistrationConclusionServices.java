package org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion;

import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RegistrationConclusionServices {

    public static Set<RegistrationConclusionInformation> inferConclusion(final Registration registration) {
        final Set<RegistrationConclusionInformation> result = Sets.newHashSet();
        
        for (final StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
            for (final ProgramConclusion programConclusion : getProgramConclusions(studentCurricularPlan)) {
                final RegistrationConclusionBean conclusionBean = new RegistrationConclusionBean(studentCurricularPlan, programConclusion);
                
                if(conclusionBean.isConcluded()) {
                    result.add(new RegistrationConclusionInformation(conclusionBean));
                }
            }
        }
        
        return result;
    }
    
    public static Map<Registration, Set<RegistrationConclusionInformation>> inferConclusion(final Set<Registration> registrationsSet) {
        final Map<Registration, Set<RegistrationConclusionInformation>> mapResult = Maps.newHashMap();
        
        for (final Registration registration : registrationsSet) {
            final Set<RegistrationConclusionInformation> result = Sets.newHashSet();
            
            for (final StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
                for (final ProgramConclusion programConclusion : getProgramConclusions(studentCurricularPlan)) {
                    final RegistrationConclusionBean conclusionBean = new RegistrationConclusionBean(studentCurricularPlan, programConclusion);
                    
                    if(conclusionBean.isConcluded()) {
                        result.add(new RegistrationConclusionInformation(conclusionBean));
                    }
                }
            }
            
            mapResult.put(registration, result);
        }
        
        
        return mapResult;
    }
    
    private static Set<ProgramConclusion> getProgramConclusions(final StudentCurricularPlan studentCurricularPlan) {
        final Set<ProgramConclusion> result = Sets.newHashSet();
        
        final Set<CurriculumGroup> allCurriculumGroups = Sets.newHashSet(studentCurricularPlan.getAllCurriculumGroups());
        allCurriculumGroups.add(studentCurricularPlan.getRoot());
        
        for (final CurriculumGroup curriculumGroup : allCurriculumGroups) {
            if(curriculumGroup.getDegreeModule() == null) {
                continue;
            }
            
            if(curriculumGroup.getDegreeModule().getProgramConclusion() != null) {
                result.add(curriculumGroup.getDegreeModule().getProgramConclusion());
            }
        }
        
        return result;
    }

}
