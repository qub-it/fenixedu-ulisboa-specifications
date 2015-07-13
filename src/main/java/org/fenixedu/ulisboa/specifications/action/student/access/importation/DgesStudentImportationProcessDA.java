/**
 *  Copyright © 2015 Universidade de Lisboa
 *  
 *  This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *  
 *  FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute 
 *  it and/or modify it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FenixEdu fenixedu-ulisboa-specifications.
 *  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.fenixedu.ulisboa.specifications.action.student.access.importation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.QueueJob;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.ui.struts.action.academicAdministration.AcademicAdministrationApplication.AcademicAdminCandidaciesApp;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.DgesStudentImportationFile;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.DgesStudentImportationProcess;

import pt.ist.fenixWebFramework.renderers.DataProvider;
import pt.ist.fenixWebFramework.renderers.components.converters.Converter;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixframework.Atomic;

@StrutsFunctionality(app = AcademicAdminCandidaciesApp.class, path = "dges-student-importation-process",
        titleKey = "link.dgesStudentImportationProcess",
        accessGroup = "(academic(MANAGE_CANDIDACY_PROCESSES) | academic(MANAGE_INDIVIDUAL_CANDIDACIES))",
        bundle = "FenixeduUlisboaSpecificationsResources")
@Mapping(path = "/dgesStudentImportationProcess", module = "ulisboa-specifications")
@Forwards({ @Forward(name = "list", path = "/candidacy/dges/student/importation/list.jsp"),
        @Forward(name = "prepare-create-new-process", path = "/candidacy/dges/student/importation/prepareCreateNewProcess.jsp"),
        @Forward(name = "configuration", path = "/candidacy/dges/student/importation/configuration.jsp") })
public class DgesStudentImportationProcessDA extends FenixDispatchAction {

    @EntryPoint
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        DgesBaseProcessBean bean = getRenderedBean();
        if (bean == null) {
            bean = new DgesBaseProcessBean(ExecutionYear.readCurrentExecutionYear());
        }

        RenderUtils.invalidateViewState("importation.bean");
        request.setAttribute("importationBean", bean);

        List<DgesStudentImportationProcess> importationJobsDone = new ArrayList<>();
        List<DgesStudentImportationProcess> importationJobsPending = new ArrayList<>();
        if (bean.getExecutionYear() != null) {
            importationJobsDone = DgesStudentImportationProcess.readDoneJobs(bean.getExecutionYear());
            importationJobsPending = DgesStudentImportationProcess.readUndoneJobs(bean.getExecutionYear());
        }
        request.setAttribute("importationJobsDone", importationJobsDone);
        request.setAttribute("importationJobsPending", importationJobsPending);
        request.setAttribute("canRequestJobImportationProcess", DgesStudentImportationProcess.canRequestJob());

        return mapping.findForward("list");
    }

    public ActionForward configuration(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        request.setAttribute("contingentToIngressionBean", new ContingentToIngressionBean());
        request.setAttribute("uLisboaSpecificationsRoot", ULisboaSpecificationsRoot.getInstance());
        return mapping.findForward("configuration");
    }

    private DgesBaseProcessBean getRenderedBean() {
        return getRenderedObject("importation.bean");
    }

    public ActionForward deleteContingentToIngression(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        String contingent = request.getParameter("contingent");
        ULisboaSpecificationsRoot.getInstance().deleteIngressionType(contingent);

        return configuration(mapping, form, request, response);
    }

    public ActionForward addContingentToIngression(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        ContingentToIngressionBean bean = getRenderedObject("contingentToIngressionBeanToAdd");
        ULisboaSpecificationsRoot.getInstance().setIngressionType(bean.getContingent(), bean.getIngressionType());

        return configuration(mapping, form, request, response);
    }

    public static class ContingentToIngressionBean implements Serializable {
        private String contingent;
        private IngressionType ingressionType;

        public IngressionType getIngressionType() {
            return ingressionType;
        }

        public void setIngressionType(IngressionType ingressionType) {
            this.ingressionType = ingressionType;
        }

        public String getContingent() {
            return contingent;
        }

        public void setContingent(String contingent) {
            this.contingent = contingent;
        }
    }

    public ActionForward prepareCreateNewImportationProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        DgesBaseProcessBean bean = getRenderedObject("importation.bean");
        if (bean == null) {
            bean = new DgesBaseProcessBean(ExecutionYear.readCurrentExecutionYear());
        }

        RenderUtils.invalidateViewState("importation.bean");
        RenderUtils.invalidateViewState("importation.bean.edit");

        request.setAttribute("importationBean", bean);

        return mapping.findForward("prepare-create-new-process");
    }

    public ActionForward createNewImportationProcess(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DgesBaseProcessBean bean = getRenderedObject("importation.bean");

        RenderUtils.invalidateViewState("importation.bean");
        RenderUtils.invalidateViewState("importation.bean.edit");

        byte[] contents = bean.consumeStream();

        DgesStudentImportationFile file =
                DgesStudentImportationFile.create(contents, bean.getFilename(), bean.getExecutionYear(), bean.getSpace(),
                        bean.getPhase());
        launchImportation(bean.getExecutionYear(), bean.getSpace(), bean.getPhase(), file);

        return list(mapping, form, request, response);
    }

    @Atomic
    protected DgesStudentImportationProcess launchImportation(final ExecutionYear executionYear, Space space,
            final EntryPhase phase, DgesStudentImportationFile file) {
        return new DgesStudentImportationProcess(executionYear, space, phase, file);
    }

    public ActionForward createNewImportationProcessInvalid(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        addActionMessage(request, "error", "error.dges.importation.file");
        return prepareCreateNewImportationProcess(mapping, form, request, response);
    }

    public ActionForward cancelJob(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        QueueJob job = getDomainObject(request, "queueJobId");
        job.cancel();

        return list(mapping, form, request, response);
    }

    public static class DgesBaseProcessBean implements Serializable {
        private InputStream stream;
        private String filename;
        private Long filesize;

        private ExecutionYear executionYear;
        private Space space;
        private EntryPhase phase;

        public DgesBaseProcessBean(ExecutionYear executionYear) {
            this.executionYear = executionYear;
        }

        public InputStream getStream() {
            return stream;
        }

        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public Long getFilesize() {
            return filesize;
        }

        public void setFilesize(Long filesize) {
            this.filesize = filesize;
        }

        public ExecutionYear getExecutionYear() {
            return executionYear;
        }

        public void setExecutionYear(ExecutionYear executionYear) {
            this.executionYear = executionYear;
        }

        public Space getSpace() {
            return space;
        }

        public void setSpace(Space space) {
            this.space = space;
        }

        public EntryPhase getPhase() {
            return phase;
        }

        public void setPhase(final EntryPhase phase) {
            this.phase = phase;
        }

        public byte[] consumeStream() throws IOException {
            byte[] data = new byte[getFilesize().intValue()];

            getStream().read(data);

            return data;
        }
    }

    public static class EntryPhaseProvider implements DataProvider {

        @Override
        public Object provide(Object source, Object currentValue) {
            return Arrays.asList(EntryPhase.values());
        }

        @Override
        public Converter getConverter() {
            return null;
        }
    }
}
