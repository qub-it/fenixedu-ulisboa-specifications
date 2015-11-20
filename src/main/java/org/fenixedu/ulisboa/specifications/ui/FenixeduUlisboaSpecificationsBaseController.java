package org.fenixedu.ulisboa.specifications.ui;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.spring.FenixEDUBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

public class FenixeduUlisboaSpecificationsBaseController extends FenixEDUBaseController {

    //The HTTP Request that can be used internally in the controller
    protected @Autowired HttpServletRequest request;

    @ModelAttribute
    protected void addModelProperties(Model model) {
        super.addModelProperties(model, request);

        String infoMessages = request.getParameter(INFO_MESSAGES);
        if (infoMessages != null) {
            addInfoMessage(infoMessages, model);
        }
        String warningMessages = request.getParameter(WARNING_MESSAGES);
        if (warningMessages != null) {
            addWarningMessage(warningMessages, model);
        }
        String errorMessages = request.getParameter(ERROR_MESSAGES);
        if (errorMessages != null) {
            addErrorMessage(errorMessages, model);
        }

    }

    protected void writeFile(final HttpServletResponse response, final String filename, final String contentType,
            final byte[] content) throws IOException {

        response.setContentLength(content.length);
        response.setContentType(contentType);
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(content);
            outputStream.flush();
            response.flushBuffer();
        }
    }

}
