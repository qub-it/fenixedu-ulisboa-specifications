package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.Candidacy;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public abstract class FirstTimeCandidacyAbstractController extends FenixeduUlisboaSpecificationsBaseController {

    public Optional<String> accessControlRedirect(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return Optional.of(redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes));
        }
        return Optional.empty();
    }

    @Atomic
    protected PersonalIngressionData getOrCreatePersonalIngressionDataForCurrentExecutionYear(final Student student) {
        return getPersonalIngressionData(student, ExecutionYear.readCurrentExecutionYear(), true);
    }

    protected PersonalIngressionData getPersonalIngressionData(final Student student, final ExecutionYear executionYear, final boolean create) {
        PersonalIngressionData personalData = student.getPersonalIngressionDataByExecutionYear(executionYear);
        
        if(personalData != null) {
            // Found one
            return personalData;
        }
        
        if(!create) {
            return null;
        }
        
        // Create personal ingression data with one precedentDegreeInformation from candidacy if possible
        final Set<PrecedentDegreeInformation> pdiSetFromCandidacy = Sets.newHashSet();
        for (final Candidacy candidacy : student.getPerson().getCandidaciesSet()) {
            if(!candidacy.getActiveCandidacySituation().getCandidacySituationType().isActive()) {
                continue;
            }
            
            if(!(candidacy instanceof StudentCandidacy)) {
                continue;
            }
            
            final StudentCandidacy studentCandidacy = (StudentCandidacy) candidacy;
            
            if(studentCandidacy.getExecutionYear() != executionYear) {
                continue;
            }
            
            pdiSetFromCandidacy.add(studentCandidacy.getPrecedentDegreeInformation());
         }
        
        if(!pdiSetFromCandidacy.isEmpty()) {
            return new PersonalIngressionData(student, executionYear, pdiSetFromCandidacy.iterator().next());
        }
        
        if(!student.getRegistrationsSet().isEmpty()) {
            final PrecedentDegreeInformation pid = new PrecedentDegreeInformation();
            pid.setRegistration(student.getRegistrationsSet().iterator().next());
            
            return new PersonalIngressionData(student, executionYear, pid);
        }
        
        // Cannot create personal ingression data
        return null;
    }

    protected List<PrecedentDegreeInformation> findCompletePrecedentDegreeInformationsToFill(final Student student) {
        final List<Registration> activeRegistrationsWithEnrolments =
                Raides.findActiveRegistrationsWithEnrolments(student);

        final List<PrecedentDegreeInformation> result = Lists.newArrayList();
        for (final Registration registration : activeRegistrationsWithEnrolments) {
            if (Raides.isCompletePrecedentDegreeInformationFieldsToBeFilledByStudent(registration)) {
                result.add(registration.getStudentCandidacy().getPrecedentDegreeInformation());
            }
        }

        return result;
    }

    protected List<PrecedentDegreeInformation> findPreviousDegreePrecedentDegreeInformationsToFill(final Student student) {
        final List<Registration> activeRegistrationsWithEnrolments =
                Raides.findActiveRegistrationsWithEnrolments(student);
        final List<PrecedentDegreeInformation> result = Lists.newArrayList();

        for (final Registration registration : activeRegistrationsWithEnrolments) {
            if (!Raides.isDegreeChangeOrTransfer(RaidesInstance.getInstance(), registration.getIngressionType())) {
                continue;
            }

            if (Raides.isPreviousDegreePrecedentDegreeInformationFieldsToBeFilledByStudent(RaidesInstance.getInstance(),
                    registration)) {
                result.add(registration.getStudentCandidacy().getPrecedentDegreeInformation());
            }
        }

        return result;
    }

    @Override
    protected void addModelProperties(Model model) {
        super.addModelProperties(model);
        model.addAttribute("controllerURL", getControllerURL());
    }

    protected abstract String getControllerURL();

    protected boolean isFormIsFilled(final Model model) {
        return isFormIsFilled(getStudent(model));
    }
    
    public abstract boolean isFormIsFilled(final Student student);
    
    protected abstract Student getStudent(final Model model);
    
}
