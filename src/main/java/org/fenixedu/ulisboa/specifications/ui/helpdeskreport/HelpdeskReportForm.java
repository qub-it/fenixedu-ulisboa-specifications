package org.fenixedu.ulisboa.specifications.ui.helpdeskreport;

import java.io.Serializable;
import java.util.Collection;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

import com.google.common.base.Strings;

import pt.ist.fenixframework.FenixFramework;

public class HelpdeskReportForm implements Serializable {

    private static final long serialVersionUID = 1L;
    private String subject;
    private String description;
    private String type = "exception";
    private String exceptionInfo;
    private String functionality;
    private String referer;
    private String userAgent;
    private Collection<Attachment> attachments;

    @Deprecated
    private String attachment;
    @Deprecated
    private String fileName;
    private String mimeType;
    private String email;
    private String priority;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    public String getFunctionality() {
        return functionality;
    }

    public void setFunctionality(String functionality) {
        this.functionality = functionality;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Deprecated
    public String getAttachment() {
        return attachment;
    }

    @Deprecated
    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    @Deprecated
    public String getFileName() {
        return fileName;
    }

    @Deprecated
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Collection<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Collection<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getEmail() {
        if (!Strings.isNullOrEmpty(email) && !email.equals("null")) {
            return email;
        } else if (Authenticate.isLogged()) {
            return Authenticate.getUser().getEmail();
        } else {
            return PortalConfiguration.getInstance().getSupportEmailAddress();
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MenuFunctionality getMenuFunctionality() {
        return Strings.isNullOrEmpty(functionality) ? null : FenixFramework.getDomainObject(functionality);
    }

    public static class Attachment {
        String name;
        String content;

        public Attachment() {
        }

        public Attachment(String name, String content) {
            this.name = name;
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }
}
