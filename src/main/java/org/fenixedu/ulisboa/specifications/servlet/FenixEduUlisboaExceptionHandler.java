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
        // It will be much more interesting to actually know the true cause of the exception instead of 
        // exception most of the times we received (Such as ServletNestedException). So we'll perform 
        // exception unwrapping to find the true cause.
        //
        // 1 August 2017 - Paulo Abrantes
        Throwable unwrappedException = unwrapException(exception, 0);

        super.setExtraParameters(ctx, req, unwrappedException);
        ctx.put("exceptionFullQualifiedName", unwrappedException.getClass().getName());

        StackTraceElement[] stackTraceElements = unwrappedException.getStackTrace();
        if (stackTraceElements.length > 0) {
            StackTraceElement stackTraceElement = stackTraceElements[0];
            ctx.put("offendingClass", stackTraceElement.getClassName());
            ctx.put("offendingMethod", stackTraceElement.getMethodName());
            ctx.put("offendingLine", stackTraceElement.getLineNumber());
        } else {
            ctx.put("offendingClass", "noStackTracePresent");
            ctx.put("offendingMethod", "noStackTracePresent");
            ctx.put("offendingLine", "0");
        }
        ctx.put("exceptionMessage", unwrappedException.getMessage());
    }

    //
    // Just in case we end up in some sort of loop or huge exception chain
    // let's have a kill switch to make sure we don't spend an eternity 
    // unwrapping exceptions.
    //
    // 1 August 2017 - Paulo Abrantes

    public static final int EXCEPTION_CAUSE_LIMIT = 42;

    protected Throwable unwrapException(Throwable throwable, int counter) {
        Throwable cause = throwable.getCause();
        return (cause == null || cause == throwable || counter > EXCEPTION_CAUSE_LIMIT) ? throwable : unwrapException(cause,
                ++counter);
    }

}
