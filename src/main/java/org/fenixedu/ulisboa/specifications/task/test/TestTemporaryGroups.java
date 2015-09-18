package org.fenixedu.ulisboa.specifications.task.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.accessControl.SpecialIngressionsAndOtherCyclesGroup;

@Deprecated
public class TestTemporaryGroups extends CustomTask {

    @Override
    public void runTask() throws Exception {
        Group parse = Group.parse(SpecialIngressionsAndOtherCyclesGroup.GROUP_OPERATOR);
        Map<DegreeType, Integer> counters = new HashMap<>();
        Set<User> members = parse.getMembers();
        taskLog("Found " + members.size());
        members.forEach(user -> {
            String username = user.getPerson().getUsername();
            Registration lastActiveRegistration = user.getPerson().getStudent().getLastActiveRegistration();

            taskLog("----------------");
            taskLog(username);
            if (lastActiveRegistration == null) {
                taskLog("no registration?");
                return;
            }
            taskLog(lastActiveRegistration.getDegree().getName());
            try {
                IngressionType ingressionType = lastActiveRegistration.getIngressionType();
                taskLog(ingressionType.getLocalizedName());
            } catch (Exception e) {

            }
            taskLog(lastActiveRegistration.getStartDate() + "");
            DegreeType degreeType = lastActiveRegistration.getDegreeType();
            Integer integer = counters.get(degreeType);
            if (integer == null) {
                counters.put(degreeType, 1);
            } else {
                counters.put(degreeType, integer + 1);
            }
            taskLog(degreeType.getName().getContent());

        });
        for (Entry<DegreeType, Integer> counter : counters.entrySet()) {
            taskLog(counter.getKey().getName().getContent() + counter.getValue());
        }
    }

}
