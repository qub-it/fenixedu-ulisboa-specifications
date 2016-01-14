package org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

public class CompetenceCourseMarkSheetChangeRequest extends CompetenceCourseMarkSheetChangeRequest_Base {

    static public Comparator<CompetenceCourseMarkSheetChangeRequest> COMPARATOR_BY_REQUEST_DATE = (x, y) ->
    {
        return x.getRequestDate().compareTo(y.getRequestDate());
    };

    protected CompetenceCourseMarkSheetChangeRequest() {
        super();
        setState(CompetenceCourseMarkSheetChangeRequestStateEnum.PENDING);
        setRequestDate(new DateTime());
    }

    protected void init(final CompetenceCourseMarkSheet markSheet, final Person requester, final String reason) {

        super.setCompetenceCourseMarkSheet(markSheet);
        super.setRequester(requester);
        super.setReason(reason);

        checkRules();
    }

    public boolean isPending() {
        return CompetenceCourseMarkSheetChangeRequestStateEnum.findPending() == getState();
    }

    public boolean isAuthorized() {
        return CompetenceCourseMarkSheetChangeRequestStateEnum.findAuthorized() == getState();
    }

    public boolean isClosed() {
        return CompetenceCourseMarkSheetChangeRequestStateEnum.findClosed() == getState();
    }

    private void checkRules() {

        if (getState() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetChangeRequest.state.required");
        }

        if (getRequestDate() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetChangeRequest.requestDate.required");
        }

        if (getCompetenceCourseMarkSheet() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CompetenceCourseMarkSheetChangeRequest.competenceCourseMarkSheet.required");
        }

        if (getRequester() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetChangeRequest.requester.required");
        }

        if (StringUtils.isBlank(getReason())) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetChangeRequest.reason.required");
        }

        if (!isPending()) {

            if (getResponder() == null) {
                throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetChangeRequest.responder.required");
            }

            if (getResponseDate() == null) {
                throw new ULisboaSpecificationsDomainException(
                        "error.CompetenceCourseMarkSheetChangeRequest.responseDate.required");
            }
        }

    }

    @Atomic
    static public CompetenceCourseMarkSheetChangeRequest create(final CompetenceCourseMarkSheet markSheet, final Person requester,
            final String reason) {
        final CompetenceCourseMarkSheetChangeRequest result = new CompetenceCourseMarkSheetChangeRequest();
        result.init(markSheet, requester, reason);

        return result;
    }

    @Atomic
    public void authorize(final Person responder, final String comments, final LocalDate markSheetExpireDate) {
        edit(responder, comments, new DateTime(), CompetenceCourseMarkSheetChangeRequestStateEnum.findAuthorized());
        getCompetenceCourseMarkSheet().editExpireDate(markSheetExpireDate);
        getCompetenceCourseMarkSheet().revertToEdition(false, comments);
    }

    @Atomic
    public void close(final Person responder, final String comments) {
        edit(responder, comments, new DateTime(), CompetenceCourseMarkSheetChangeRequestStateEnum.findClosed());
    }

    protected void edit(final Person responder, final String comments, final DateTime respondeDate,
            CompetenceCourseMarkSheetChangeRequestStateEnum state) {
        super.setResponder(responder);
        super.setComments(comments);
        super.setResponseDate(respondeDate);
        super.setState(state);

        checkRules();
    }

    public void delete() {
        
        super.setCompetenceCourseMarkSheet(null);
        super.setRequester(null);
        super.setResponder(null);

        super.deleteDomainObject();

    }

}
