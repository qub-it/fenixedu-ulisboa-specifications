package com.qubit.qubEdu.module.candidacies.ui.customer;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.FinantialDocument;
import org.fenixedu.treasury.domain.document.SettlementNote;
import org.fenixedu.treasury.domain.payments.integration.DigitalPaymentPlatform;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qubit.qubEdu.module.candidacies.ui.CandidaciesExtensionsController;
import com.qubit.qubEdu.module.candidacies.ui.customer.forwardpayments.CandidaciesCustomerAccountingForwardPaymentController;

@SpringFunctionality(app = CandidaciesExtensionsController.class, title = "label.CandidaciesCustomerAccounting.functionality",
        accessGroup = "logged")
@RequestMapping(CandidaciesCustomerAccountingController.CONTROLLER_URL)
public class CandidaciesCustomerAccountingController
        extends org.fenixedu.academictreasury.ui.customer.CustomerAccountingController {

    public static final String CONTROLLER_URL = "/candidacies/customer/viewaccount";
    private static final String READ_CUSTOMER_URI = "/customer/read/";
    private static final String READ_CUSTOMER_URL = CONTROLLER_URL + READ_CUSTOMER_URI;
    private static final String READ_ACCOUNT_URI = "/account/read/";
    public static final String READ_ACCOUNT_URL = CONTROLLER_URL + READ_ACCOUNT_URI;

    @Override
    public String getReadCustomerUrl() {
        return READ_CUSTOMER_URL;
    }

    @Override
    public String getReadAccountUrl() {
        return READ_ACCOUNT_URL;
    }

    @Override
    protected String getForwardPaymentUrl(DebtAccount debtAccount, DigitalPaymentPlatform digitalPaymentPlatform) {
        return FORWARD_PAYMENT_URL(debtAccount, digitalPaymentPlatform);
    }

    public static String FORWARD_PAYMENT_URL(DebtAccount debtAccount, DigitalPaymentPlatform digitalPaymentPlatform) {
        return String.format(CONTROLLER_URL + "/read/%s/forwardpayment/%s", debtAccount.getExternalId(),
                digitalPaymentPlatform.getExternalId());
    }

    @Override
    protected String getPrintSettlementNote() {
        return PRINT_SETTLEMENT_NOTE_URL;
    }

    protected String getDownloadCertifiedDocumentPrintUrl() {
        return DOWNLOAD_CERTIFIED_DOCUMENT_PRINT_URL;
    }

    @RequestMapping
    @Override
    public String home(Model model) {
        return super.home(model);
    }

    @RequestMapping(value = READ_CUSTOMER_URI)
    @Override
    public String readCustomer(Model model, RedirectAttributes redirectAttributes) {
        return super.readCustomer(model, redirectAttributes);
    }

    @RequestMapping(value = READ_ACCOUNT_URI + "{oid}")
    @Override
    public String readAccount(@PathVariable(value = "oid") DebtAccount debtAccount, Model model,
            final RedirectAttributes redirectAttributes) {
        return super.readAccount(debtAccount, model, redirectAttributes);
    }

    @RequestMapping(value = "/read/{oid}/forwardpayment/{digitalPaymentPlatformId}")
    public String processReadToForwardPayment(@PathVariable("oid") DebtAccount debtAccount,
            @PathVariable("digitalPaymentPlatformId") DigitalPaymentPlatform digitalPaymentPlatform, Model model,
            RedirectAttributes redirectAttributes) {
        return redirect(CandidaciesCustomerAccountingForwardPaymentController.CHOOSE_INVOICE_ENTRIES_URL
                + debtAccount.getExternalId() + "/" + digitalPaymentPlatform.getExternalId(), model, redirectAttributes);
    }

    private static final String CUSTOMER_NOT_CREATED_URI = "/customernotcreated";
    public static final String CUSTOMER_NOT_CREATED_URL = CONTROLLER_URL + CUSTOMER_NOT_CREATED_URI;

    @Override
    protected String getCustomerNotCreatedUrl() {
        return CUSTOMER_NOT_CREATED_URL;
    }

    @RequestMapping(value = CUSTOMER_NOT_CREATED_URI)
    public String customernotcreated(final Model model) {
        return super.customernotcreated(model);
    }

    private static final String PRINT_SETTLEMENT_NOTE_URI = "/printsettlementnote";
    public static final String PRINT_SETTLEMENT_NOTE_URL = CONTROLLER_URL + PRINT_SETTLEMENT_NOTE_URI;

    @RequestMapping(value = PRINT_SETTLEMENT_NOTE_URI + "/{settlementNoteId}", produces = "application/pdf")
    @ResponseBody
    @Override
    public Object printsettlementnote(@PathVariable("settlementNoteId") final SettlementNote settlementNote, final Model model,
            final RedirectAttributes redirectAttributes) {
        return super.printsettlementnote(settlementNote, model, redirectAttributes);
    }

    private static final String _DOWNLOAD_CERTIFIED_DOCUMENT_PRINT_URI = "/downloadcertifieddocumentprint";
    public static final String DOWNLOAD_CERTIFIED_DOCUMENT_PRINT_URL = CONTROLLER_URL + _DOWNLOAD_CERTIFIED_DOCUMENT_PRINT_URI;

    @RequestMapping(value = _DOWNLOAD_CERTIFIED_DOCUMENT_PRINT_URI + "/{oid}", method = RequestMethod.GET)
    public String downloadcertifieddocumentprint(@PathVariable("oid") final FinantialDocument finantialDocument,
            final Model model, final RedirectAttributes redirectAttributes, final HttpServletResponse response) {
        return super.downloadcertifieddocumentprint(finantialDocument, model, redirectAttributes, response);
    }

}
