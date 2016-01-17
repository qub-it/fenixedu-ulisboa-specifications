package test.not.commit;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.Callable;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionInformation;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionServices;
import org.joda.time.YearMonthDay;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class CheckAllRegistrationsForConclusion extends CustomTask {
    
    private static int LINE = 0;
    
    @Override
    public void runTask() throws Exception {
        
        final Set<Registration> registrationsSet = Bennu.getInstance().getRegistrationsSet();
        
        taskLog(String.format("Running conclusion for %d registrations", registrationsSet.size()));
        int total = 0;
        Set<String> registrationsIdsSet = Sets.newHashSet();
        for (final Registration registration : registrationsSet) {
//            if(registration.getStudent().getNumber() != 63241) {
//                continue;
//            }
            
            registrationsIdsSet.add(registration.getExternalId());
            total++;
            
            if(total %  100 == 0) {
                final InferConclusionThread inferConclusionThread = new InferConclusionThread(this, registrationsIdsSet);
                
                try {
                    inferConclusionThread.start();
                    inferConclusionThread.join();
                } catch(InterruptedException e) {
                }
                
                registrationsIdsSet = Sets.newHashSet();
            }
        }
        
        if(!registrationsIdsSet.isEmpty()) {
            final InferConclusionThread inferConclusionThread = new InferConclusionThread(this, registrationsIdsSet);
            
            try {
                inferConclusionThread.start();
                inferConclusionThread.join();
            } catch(InterruptedException e) {
            }
        }
    }
    
    private class InferConclusionThread extends Thread {

        private CheckAllRegistrationsForConclusion task;
        private Set<String> registrationsIdsSet;
        
        public InferConclusionThread(CheckAllRegistrationsForConclusion task, final Set<String> registrationsIdsSet) {
            this.task = task;
            this.registrationsIdsSet = registrationsIdsSet;
        }
        
        @Override
        // @Atomic(mode = TxMode.READ)
        public void run() {
            try {
                FenixFramework.getTransactionManager().withTransaction(new Callable() {

                    @Override
                    public Object call() throws Exception {
                        for (final String externalId: registrationsIdsSet) {
                            final Registration registration = FenixFramework.getDomainObject(externalId);
                            
                            try {
                                for (final RegistrationConclusionInformation registrationConclusionInformation : RegistrationConclusionServices.inferConclusion(registration)) {
                                    final Integer studentNumber = registrationConclusionInformation.getStudentCurricularPlan().getRegistration().getStudent().getNumber();
                                    final String name = registrationConclusionInformation.getStudentCurricularPlan().getRegistration().getStudent().getName();
                                    final String degreeName = registrationConclusionInformation.getStudentCurricularPlan().getDegree().getPresentationName();
                                    final String groupName = registrationConclusionInformation.getCurriculumGroup().getName().getContent();
                                    final boolean scholarPart = registrationConclusionInformation.isScholarPart();
                                    final ExecutionYear conclusionYear = registrationConclusionInformation.getConclusionYear();
                                    YearMonthDay conclusionDate = registrationConclusionInformation.getRegistrationConclusionBean().getConclusionDate();
                                    final Grade finalGrade = registrationConclusionInformation.getRegistrationConclusionBean().getFinalGrade();
                                    final boolean hasConclusionProcess = registrationConclusionInformation.getRegistrationConclusionBean().getConclusionProcess() != null;
                                    
                                    task.taskLog(String.format("%d;%s;%s;%s;%s;%s;%s;%s;%s;%s", 
                                            ++LINE, studentNumber, name, degreeName, groupName, scholarPart, conclusionYear.getQualifiedName(), conclusionDate.toString(), finalGrade.getValue(), hasConclusionProcess));

                                }
                                
                            } catch(final DomainException e) {
                            } catch(final Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                                //taskLog(String.format("[ERROR][%s]: %s", registration.getNumber(), e.getMessage()));
                            }
                        }
                        
                        return null;
                    }
                }, new Atomic() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return null;
                    }

                    @Override
                    public boolean flattenNested() {
                        return false;
                    }

                    @Override
                    public TxMode mode() {
                        return TxMode.READ;
                    }
                });
            } catch (final Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        
        
    }
}
