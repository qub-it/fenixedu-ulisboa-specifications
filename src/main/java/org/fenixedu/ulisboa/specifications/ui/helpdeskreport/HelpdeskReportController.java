package org.fenixedu.ulisboa.specifications.ui.helpdeskreport;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.validator.EmailValidator;
import org.fenixedu.academic.FenixEduAcademicConfiguration;
import org.fenixedu.academic.domain.Installation;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.helpdesk.HelpdeskConfigurations;
import org.fenixedu.ulisboa.specifications.domain.helpdesk.HelpdeskRecipient;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.helpdeskreport.HelpdeskReportForm.Attachment;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import edu.emory.mathcs.backport.java.util.Arrays;

@Controller
@RequestMapping(HelpdeskReportController.CONTROLLER_URL)
public class HelpdeskReportController extends FenixeduUlisboaSpecificationsBaseController {

    public static final Collection<HelpdeskHandler> helpdeskHandlers = new ArrayList<>();

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/helpdeskreport";

    private static final Logger logger = LoggerFactory.getLogger(HelpdeskReportController.class);

    private static final String SEPARATOR =
            "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n";

    private static final String hostname = getHostName();

    private static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "<Host name unknown>";
        }
    }

    @RequestMapping(value = "/submitReport", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void submitReport(@RequestBody final HelpdeskReportForm jsonReportForm) {
        List<String> recipients = getReportRecipients();
        List<String> carbonCopies = getCCs();
        List<String> blindCarnonCopies = getBCCs();
        String submitterEmail = jsonReportForm.getEmail();
        String mailSubject = generateEmailSubject(jsonReportForm);
        String mailBody = generateEmailBody(jsonReportForm);

        if (CoreConfiguration.getConfiguration().developmentMode()) {
            logger.warn("Submitted error form\n\nFrom: {}\nTo: {}\nCC: {}\nBCC: {}\nSubject: '{}'\n{}", submitterEmail,
                    recipients.stream().reduce("", (a, b) -> a + " " + b).trim(),
                    carbonCopies.stream().reduce("", (a, b) -> a + " " + b).trim(),
                    blindCarnonCopies.stream().reduce("", (a, b) -> a + " " + b).trim(), mailSubject, mailBody);
        } else {
            sendEmail(
                    EmailValidator.getInstance().isValid(submitterEmail) ? submitterEmail : PortalConfiguration.getInstance()
                            .getSupportEmailAddress(),
                    recipients, carbonCopies, blindCarnonCopies, mailSubject, mailBody, jsonReportForm);
        }
        for (HelpdeskHandler handler : helpdeskHandlers) {
            try {
                handler.process(jsonReportForm);
            } catch (Throwable e) {
                //Avoid exceptions to affect other handlers or the request result
                logger.error("Handler " + handler.getClass().getName() + " threw an error: " + e.getClass().getName());
            }
        }
    }

    private List<String> getReportRecipients() {
        List<String> recipients = new ArrayList<String>();
        if (PortalConfiguration.getInstance().getSupportEmailAddress() != null) {
            recipients.add(PortalConfiguration.getInstance().getSupportEmailAddress());
        }
        for (HelpdeskRecipient recipient : HelpdeskConfigurations.getInstance().getRecipientsSet()) {
            recipients.add(recipient.getEmail());
        }
        return recipients;
    }

    private List<String> getCCs() {
        List<String> ccs = new ArrayList<String>();
        if (Installation.getInstance().getAcademicEmailAddress() != null) {
            ccs.add(Installation.getInstance().getAcademicEmailAddress());
        }
        for (HelpdeskRecipient cc : HelpdeskConfigurations.getInstance().getCCsSet()) {
            ccs.add(cc.getEmail());
        }
        return ccs;
    }

    private List<String> getBCCs() {
        List<String> bccs = new ArrayList<String>();
        for (HelpdeskRecipient bcc : HelpdeskConfigurations.getInstance().getBCCsSet()) {
            bccs.add(bcc.getEmail());
        }
        return bccs;
    }

    private String generateEmailSubject(HelpdeskReportForm bean) {
        StringBuilder builder = new StringBuilder();
        MenuFunctionality functionality = bean.getMenuFunctionality();
        builder.append("[Fenix ");
        builder.append(Unit.getInstitutionAcronym());
        builder.append("] [");
        builder.append(functionality != null ? functionality.getPathFromRoot().get(0).getTitle().getContent() : "").append("] ");
        builder.append('[').append(bean.getType().toUpperCase()).append("] ");
        builder.append(bean.getSubject());
        return builder.toString();
    }

    private String generateEmailBody(HelpdeskReportForm bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(SEPARATOR);
        appendHeader(builder, bean);
        appendBody(builder, bean);
        return builder.toString();
    }

    private void appendBody(StringBuilder builder, HelpdeskReportForm bean) {
        builder.append(SEPARATOR).append("\n");
        builder.append(bean.getDescription()).append("\n\n\n\n");
        if (!Strings.isNullOrEmpty(bean.getExceptionInfo())) {
            builder.append(SEPARATOR).append(bean.getExceptionInfo());
        }
    }

    private void appendHeader(StringBuilder builder, HelpdeskReportForm bean) {
        generateLabel(builder, "Roles").append('[');
        Person person = AccessControl.getPerson();
        if (person != null) {
            List<RoleType> roles = Arrays.asList(RoleType.values());
            builder.append(roles.stream().filter(rt -> rt.isMember(person.getUser())).map(rt -> rt.getLocalizedName())
                    .reduce("", (a, b) -> a + " " + b).trim());
        }
        builder.append("]\n");

        generateLabel(builder, "Name");
        if (person != null) {
            builder.append("[").append(person.getName()).append("]\n");
            generateLabel(builder, "Username");
            builder.append("[").append(person.getUsername()).append("]\n");
        } else {
            builder.append(BundleUtil.getString(Bundle.APPLICATION, "support.mail.session.error")).append('\n');
        }

        generateLabel(builder, "Email").append("[").append(bean.getEmail()).append("]\n");

        // Portal
        MenuFunctionality functionality = bean.getMenuFunctionality();
        generateLabel(builder, "Portal").append("[");
        if (functionality != null) {
            builder.append(functionality.getPathFromRoot().stream().map(item -> item.getTitle().getContent())
                    .collect(Collectors.joining(" > ")));
        }
        builder.append("]\n");

        generateLabel(builder, "Referer").append('[').append(bean.getReferer()).append("]\n");
        generateLabel(builder, "Browser/OS").append("[").append(bean.getUserAgent()).append("]\n");

        generateLabel(builder, "Type").append('[').append(bean.getType().toUpperCase()).append("]\n");

        if (bean.getPriority() != null) {
            generateLabel(builder, "Priority").append('[').append(bean.getPriority().toUpperCase()).append("]\n");
        }

        // Extra Info
        generateLabel(builder, "When").append('[').append(DateTime.now()).append("]\n");
        generateLabel(builder, "Host").append('[').append(hostname).append("]\n");
    }

    private StringBuilder generateLabel(StringBuilder builder, String label) {
        builder.append(label);
        for (int i = label.length(); i <= 15; i++) {
            builder.append('_');
        }
        return builder;
    }

    private void sendEmail(String from, List<String> to, List<String> cc, List<String> bcc, String subject, String body,
            HelpdeskReportForm bean) {
        Properties props = new Properties();
        props.put("mail.smtp.host",
                Objects.firstNonNull(FenixEduAcademicConfiguration.getConfiguration().getMailSmtpHost(), "localhost"));
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            for (String recipient : to) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }
            for (String recipient : cc) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(recipient));
            }
            for (String recipient : bcc) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
            }
            message.setSubject(subject);
            message.setText(body);

            Multipart multipart = new MimeMultipart();
            {
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(body);
                multipart.addBodyPart(messageBodyPart);
            }

            if (bean.getAttachments() != null) {
                for (Attachment attachment : bean.getAttachments()) {
                    BodyPart messageBodyPart = new MimeBodyPart();
                    String content = attachment.getContent();
                    MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

                    // only by file name
                    String fileName = attachment.getName();
                    String mimeType = mimeTypesMap.getContentType(fileName);
                    messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(Base64.getDecoder().decode(content),
                            mimeType != null ? mimeType : "text/plain")));
                    messageBodyPart.setFileName(fileName);
                    multipart.addBodyPart(messageBodyPart);
                }
            }
            if (!Strings.isNullOrEmpty(bean.getAttachment())) {
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setDataHandler(new DataHandler(
                        new ByteArrayDataSource(Base64.getDecoder().decode(bean.getAttachment()), bean.getMimeType())));
                messageBodyPart.setFileName(bean.getFileName());
                multipart.addBodyPart(messageBodyPart);
            }

            message.setContent(multipart);

            Transport.send(message);
        } catch (Exception e) {
            logger.error("Could not send support email! Original message was: " + body, e);
        }
    }

    public static void registerHelpdeskHandler(HelpdeskHandler handler) {
        helpdeskHandlers.add(handler);
    }

    //Enables other communication channels to send helpdesk submissions (e.g IRC)
    public static interface HelpdeskHandler {
        public void process(HelpdeskReportForm form);
    }
}
