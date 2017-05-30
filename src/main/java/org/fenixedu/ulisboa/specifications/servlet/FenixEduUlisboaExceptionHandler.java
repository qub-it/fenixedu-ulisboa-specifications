package org.fenixedu.ulisboa.specifications.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.servlets.ExceptionHandlerFilter.ExceptionHandler;
import org.fenixedu.bennu.portal.servlet.PortalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FenixEduUlisboaExceptionHandler extends PortalExceptionHandler implements ExceptionHandler {

    Logger logger = LoggerFactory.getLogger(FenixEduUlisboaExceptionHandler.class);

    public FenixEduUlisboaExceptionHandler(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    public boolean handle(ServletRequest request, ServletResponse response, Throwable throwable)
            throws ServletException, IOException {
        if (AccessControl.getPerson() == null) {
            logger.error("Exception thrown for anonymous user");
        } else {
            logger.error("Exception thrown for " + AccessControl.getPerson().getUsername());

        }
        return super.handle(request, response, throwable);
    }

    @Override
    protected void setExtraParameters(Map<String, Object> ctx, HttpServletRequest req, Throwable exception) {
        super.setExtraParameters(ctx, req, exception);
        ctx.put("exceptionFullQualifiedName", exception.getClass().getName());
        StackTraceElement stackTraceElement = exception.getStackTrace()[0];
        ctx.put("offendingClass", stackTraceElement.getClassName());
        ctx.put("offendingMethod", stackTraceElement.getMethodName());
        ctx.put("offendingLine", stackTraceElement.getLineNumber());
        ctx.put("exceptionMessage", exception.getMessage());
    }

}
