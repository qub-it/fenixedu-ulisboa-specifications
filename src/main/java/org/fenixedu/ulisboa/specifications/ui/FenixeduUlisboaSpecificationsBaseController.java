package org.fenixedu.ulisboa.specifications.ui;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class FenixeduUlisboaSpecificationsBaseController {
    private static final String ERROR_MESSAGES = "errorMessages";
    private static final String WARNING_MESSAGES = "warningMessages";
    private static final String INFO_MESSAGES = "infoMessages";

    //The HTTP Request that can be used internally in the controller
    protected @Autowired HttpServletRequest request;

    //The entity in the Model

    // The list of INFO messages that can be showed on View
    protected void addInfoMessage(String message, Model model) {
        ((List<String>) model.asMap().get(INFO_MESSAGES)).add(message);
    }

    // The list of WARNING messages that can be showed on View
    protected void addWarningMessage(String message, Model model) {
        ((List<String>) model.asMap().get(WARNING_MESSAGES)).add(message);
    }

    // The list of ERROR messages that can be showed on View
    protected void addErrorMessage(String message, Model model) {
        ((List<String>) model.asMap().get(ERROR_MESSAGES)).add(message);
    }

    protected void clearMessages(Model model) {
        model.addAttribute(INFO_MESSAGES, new ArrayList<String>());
        model.addAttribute(WARNING_MESSAGES, new ArrayList<String>());
        model.addAttribute(ERROR_MESSAGES, new ArrayList<String>());
    }

    @ModelAttribute
    protected void addModelProperties(Model model) {
        model.addAttribute(INFO_MESSAGES, new ArrayList<String>());
        model.addAttribute(WARNING_MESSAGES, new ArrayList<String>());
        model.addAttribute(ERROR_MESSAGES, new ArrayList<String>());

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
        //Add here more attributes to the Model
        //model.addAttribute(<attr1Key>, <attr1Value>);
        //....
    }

    protected String redirect(String destinationAction, Model model, RedirectAttributes redirectAttributes) {
        if (model.containsAttribute(INFO_MESSAGES)) {
            redirectAttributes.addFlashAttribute(INFO_MESSAGES, model.asMap().get(INFO_MESSAGES));
        }
        if (model.containsAttribute(WARNING_MESSAGES)) {
            redirectAttributes.addFlashAttribute(WARNING_MESSAGES, model.asMap().get(WARNING_MESSAGES));
        }
        if (model.containsAttribute(ERROR_MESSAGES)) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGES, model.asMap().get(ERROR_MESSAGES));
        }

        return "redirect:" + destinationAction;
    }

}
