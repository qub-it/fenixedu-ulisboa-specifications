package org.fenixedu.ulisboa.specifications.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.servlets.ExceptionHandlerFilter.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FenixEduUlisboaExceptionHandler implements ExceptionHandler {

    ExceptionHandler exceptionHandler;
    Logger logger = LoggerFactory.getLogger(FenixEduUlisboaExceptionHandler.class);

    public FenixEduUlisboaExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public boolean handle(ServletRequest request, ServletResponse response, Throwable throwable) throws ServletException,
            IOException {
        if (AccessControl.getPerson() == null) {
            logger.error("Exception thrown for anonymous user");
        } else {
            logger.error("Exception thrown for " + AccessControl.getPerson().getUsername());

        }
        return exceptionHandler.handle(request, response, throwable);
    }

}
