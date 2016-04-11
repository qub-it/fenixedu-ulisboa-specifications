/**
 * 
 */
package org.fenixedu.ulisboa.specifications.ui.resourceAllocationManager;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.ShiftProfessorship;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.resourceAllocationManager.ExecutionPeriodDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;

/**
 * @author shezad
 *
 */
@Mapping(module = "resourceAllocationManager", path = "/manageShiftProfessorships", functionality = ExecutionPeriodDA.class)
@Forwards(@Forward(name = "manageShiftProfessorships", path = "/resourceAllocationManager/manageShiftProfessorships.jsp"))
public class ManageShiftProfessorshipsDA extends FenixDispatchAction {

    public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final Shift shift = getDomainObject(request, "shiftID");
        request.setAttribute("shift", shift);

        final Set<Professorship> existingShiftProfessorships = new HashSet<>(shift.getAssociatedShiftProfessorshipSet()).stream()
                .map(sp -> sp.getProfessorship()).collect(Collectors.toSet());
        final Set<Professorship> professorshipsToAdd = new HashSet<>(shift.getExecutionCourse().getProfessorshipsSet());
        professorshipsToAdd.removeAll(existingShiftProfessorships);

        request.setAttribute("professorshipsToAdd", professorshipsToAdd);

        return mapping.findForward("manageShiftProfessorships");
    }

    public ActionForward createShiftProfessorship(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final Shift shift = getDomainObject(request, "shiftID");
        final Professorship professorship = getDomainObject(request, "professorshipID");

        try {
            atomic(() -> {
                final ShiftProfessorship shiftProfessorship = new ShiftProfessorship();
                shiftProfessorship.setShift(shift);
                shiftProfessorship.setProfessorship(professorship);
            });
            addActionMessage("success", request, "message.manageShiftProfessorships.createShiftProfessorship.success");
        } catch (DomainException e) {
            addActionMessage("error", request, e.getMessage());
        }

        return prepare(mapping, form, request, response);
    }

    public ActionForward deleteShiftProfessorship(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final ShiftProfessorship shiftProfessorship = getDomainObject(request, "shiftProfessorshipID");
        try {
            atomic(() -> shiftProfessorship.delete());
            addActionMessage("success", request, "message.manageShiftProfessorships.deleteShiftProfessorship.success");
        } catch (DomainException e) {
            addActionMessage("error", request, e.getMessage());
        }

        return prepare(mapping, form, request, response);
    }

}
