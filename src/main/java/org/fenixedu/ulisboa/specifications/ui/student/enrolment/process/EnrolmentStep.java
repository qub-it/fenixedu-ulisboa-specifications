package org.fenixedu.ulisboa.specifications.ui.student.enrolment.process;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.bennu.IBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.web.servlet.HandlerMapping;

import com.google.common.base.Strings;

public class EnrolmentStep implements IBean {

    private LocalizedString description;
    private String entryPointURL;

    private EnrolmentProcess process;
    private EnrolmentStep next;
    private EnrolmentStep previous;

    protected EnrolmentStep(final LocalizedString description, final String entryPointURL) {
        this.description = description;
        this.entryPointURL = entryPointURL;
    }

    protected EnrolmentStep(final AcademicEnrolmentPeriodBean input) {
        this(buildDescription(input), input.getEntryPointURL());
    }

    static private LocalizedString buildDescription(final AcademicEnrolmentPeriodBean input) {
        final LocalizedString.Builder builder = new LocalizedString.Builder();
        builder.append(ULisboaSpecificationsUtil.bundleI18N("label.AcademicEnrolmentPeriod.type"));
        builder.append(" ");
        builder.append(input.getEnrolmentPeriodType().getDescriptionI18N());
        return builder.build();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EnrolmentStep other = (EnrolmentStep) obj;
        if (!getDescription().getContent().equals(other.getDescription().getContent())) {
            // legidio, LocalizedString equals seems to fail somehow on it's internal map equals
            return false;
        }
        // legidio, no need to check for same entry URL:
        // we actually want to guarantee exactly only one type of step on each process
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + getDescription().getContent().hashCode();
        return result;
    }

    public EnrolmentProcess getProcess() {
        return process;
    }

    protected void setProcess(final EnrolmentProcess input) {
        this.process = input;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public EnrolmentStep getNext() {
        return next;
    }

    protected void setNext(final EnrolmentStep input) {
        this.next = input;
    }

    public EnrolmentStep getPrevious() {
        return previous;
    }

    protected void setPrevious(final EnrolmentStep input) {
        this.previous = input;
    }

    public boolean isEntryPointURL(final HttpServletRequest request) {
        // helper to determine if is a struts request
        final ActionMapping actionMapping = (ActionMapping) request.getAttribute(Globals.MAPPING_KEY);

        if (actionMapping == null) {
            // spring

            final String secret = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            return secret != null && secret.contains(getEntryPointURL());

        } else {
            // struts

            String method = "";
            if (getEntryPointURL().contains("method")) {
                String queryString = request.getQueryString();
                if (Strings.isNullOrEmpty(queryString)) {
                    // these are desperate times.
                    method = ".do?method=prepare";
                } else {
                    final int beginIndex = queryString.indexOf("method=");
                    final int end = queryString.contains("&") ? queryString.indexOf("&") : queryString.length();
                    method = ".do?" + queryString.substring(beginIndex, end);
                }
            }

            final String secret = actionMapping.getPath() + method;
            return getEntryPointURL().contains(secret);
        }
    }

    public String getEntryPointURL() {
        return this.entryPointURL;
    }

}
