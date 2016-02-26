package org.fenixedu.ulisboa.specifications.domain.ects.tasks;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionInformation;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionServices;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class FindAllConclusions extends CustomTask {

    @Override
    public void runTask() throws Exception {
        int conclusions = 0;
        Set<Registration> batch = new HashSet<Registration>();
        for (Registration registration : Bennu.getInstance().getRegistrationsSet()) {
            if (registration.getStudentCurricularPlansSet().isEmpty()) {
                continue;
            }
            batch.add(registration);
            if (batch.size() < 500) {
                continue;
            }
            Callable<Integer> workerLogic = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int conclusions = 0;
                    for (Registration reg : batch) {
//                        for (StudentCurricularPlan scp : reg.getStudentCurricularPlansSet()) {
//                            for (ProgramConclusion pc : ProgramConclusion.conclusionsFor(scp).collect(Collectors.toSet())) {
//                                final RegistrationConclusionBean bean = new RegistrationConclusionBean(reg, pc);
//                                if (bean.isConcluded()) {
//                                    conclusions++;
//                                }
//                            }
//                        }
                        for (RegistrationConclusionInformation info : RegistrationConclusionServices.inferConclusion(reg)) {
                            if (info.isConcluded()) {
                                conclusions++;
                            }
                        }
                    }
                    return conclusions;
                }
            };
            final HarvesterWorker worker = new HarvesterWorker(workerLogic);
            worker.start();
            worker.join();
            conclusions += worker.getConclusions();
            batch.clear();
        }
        taskLog("#Conclusions: " + conclusions);
    }

    private class HarvesterWorker extends Thread {

        private Callable<Integer> logic;
        private int conclusions;

        public HarvesterWorker(Callable<Integer> logic) {
            this.logic = logic;
        }

        @Override
        public void run() {
            try {
                conclusions = FenixFramework.getTransactionManager().withTransaction(logic, new Atomic() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public boolean flattenNested() {
                        return true;
                    }

                    @Override
                    public TxMode mode() {
                        return TxMode.READ;
                    }
                });
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        int getConclusions() {
            return conclusions;
        }
    }

}
