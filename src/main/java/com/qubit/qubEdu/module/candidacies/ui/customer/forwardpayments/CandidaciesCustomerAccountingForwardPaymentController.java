package com.qubit.qubEdu.module.candidacies.ui.customer.forwardpayments;

import static org.fenixedu.treasury.util.TreasuryConstants.treasuryBundle;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.Customer;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.SettlementNote;
import org.fenixedu.treasury.domain.forwardpayments.ForwardPaymentRequest;
import org.fenixedu.treasury.domain.payments.integration.DigitalPaymentPlatform;
import org.fenixedu.treasury.dto.SettlementNoteBean;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qubit.qubEdu.module.candidacies.ui.customer.CandidaciesCustomerAccountingController;

@BennuSpringController(CandidaciesCustomerAccountingController.class)
@RequestMapping(CandidaciesCustomerAccountingForwardPaymentController.CONTROLLER_URL)
public class CandidaciesCustomerAccountingForwardPaymentController
        extends org.fenixedu.treasury.ui.document.forwardpayments.ForwardPaymentController {

    public static final String CONTROLLER_URL = "/candidacies/customer/forwardpayments/forwardpayment";

    private static final String CHOOSE_INVOICE_ENTRIES_URI = "/chooseInvoiceEntries/";
    public static final String CHOOSE_INVOICE_ENTRIES_URL = CONTROLLER_URL + CHOOSE_INVOICE_ENTRIES_URI;

    @Override
    protected String readChooseInvoiceEntriesUrl() {
        return CHOOSE_INVOICE_ENTRIES_URL;
    }

    @Override
    protected void checkPermissions(DebtAccount debtAccount, Model model) {
        if (Authenticate.getUser().getPerson() != ((PersonCustomer) debtAccount.getCustomer()).getPerson()) {
            addErrorMessage(treasuryBundle("error.authorization.not.allow.to.modify.settlements"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.allow.to.modify.settlements"));
        }
    }

    @Override
    protected String redirectToDebtAccountUrl(final DebtAccount debtAccount, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(readDebtAccountUrl() + activeDebtAccount(debtAccount).getExternalId(), model, redirectAttributes);
    }

    private DebtAccount activeDebtAccount(final DebtAccount debtAccount) {
        Customer activeCustomer = debtAccount.getCustomer().getActiveCustomer();

        if (activeCustomer == null) {
            throw new AcademicTreasuryDomainException(
                    "error.CustomerAccountingForwardPaymentController.redirectToDebtAccountUrl.missing.active.customer");
        }

        return DebtAccount.findUnique(debtAccount.getFinantialInstitution(), activeCustomer).get();
    }

    @RequestMapping(value = CHOOSE_INVOICE_ENTRIES_URI + "{debtAccountId}/{digitalPaymentPlatformId}")
    @Override
    public String chooseInvoiceEntries(@PathVariable(value = "debtAccountId") DebtAccount debtAccount,
            @PathVariable("digitalPaymentPlatformId") DigitalPaymentPlatform digitalPaymentPlatform,
            @RequestParam(value = "bean", required = false) SettlementNoteBean bean, Model model,
            final RedirectAttributes redirectAttributes) {
        return super.chooseInvoiceEntries(debtAccount, digitalPaymentPlatform, bean, model, redirectAttributes);
    }

    @RequestMapping(value = CHOOSE_INVOICE_ENTRIES_URI, method = RequestMethod.POST)
    @Override
    public String chooseInvoiceEntries(@RequestParam(value = "bean", required = true) SettlementNoteBean bean, Model model,
            final RedirectAttributes redirectAttributes) {
        return super.chooseInvoiceEntries(bean, model, redirectAttributes);
    }

    private static final String SUMMARY_URI = "/summary/";
    public static final String SUMMARY_URL = CONTROLLER_URL + SUMMARY_URI;

    @Override
    protected String readSummaryUrl() {
        return SUMMARY_URL;
    }

    @RequestMapping(value = SUMMARY_URI, method = RequestMethod.POST)
    @Override
    public String summary(@RequestParam(value = "bean", required = true) SettlementNoteBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        return super.summary(bean, model, redirectAttributes);
    }

    private static final String PROCESS_FORWARD_PAYMENT_URI = "/processforwardpayment";
    public static final String PROCESS_FORWARD_PAYMENT_URL = CONTROLLER_URL + PROCESS_FORWARD_PAYMENT_URI;

    @Override
    public String readProcessForwardPaymentUrl() {
        return PROCESS_FORWARD_PAYMENT_URL;
    }

    @RequestMapping(value = PROCESS_FORWARD_PAYMENT_URI + "/{forwardPayment}", method = RequestMethod.GET)
    @Override
    public String processforwardpayment(@PathVariable("forwardPayment") ForwardPaymentRequest forwardPayment, Model model,
            HttpServletResponse response, HttpSession session) {
        return super.processforwardpayment(forwardPayment, model, response, session);
    }

    @Override
    protected String readDebtAccountUrl() {
        return CandidaciesCustomerAccountingController.READ_ACCOUNT_URL;
    }

    @Override
    protected String forwardPaymentInsuccessUrl(ForwardPaymentRequest forwardPayment) {
        return FORWARD_PAYMENT_INSUCCESS_URL + "/" + forwardPayment.getExternalId();
    }

    @Override
    protected String forwardPaymentSuccessUrl(ForwardPaymentRequest forwardPayment) {
        return FORWARD_PAYMENT_SUCCESS_URL + "/" + forwardPayment.getExternalId();
    }

    private static final String FORWARD_PAYMENT_SUCCESS_URI = "/forwardpaymentsuccess";
    public static final String FORWARD_PAYMENT_SUCCESS_URL = CONTROLLER_URL + FORWARD_PAYMENT_SUCCESS_URI;

    @RequestMapping(value = FORWARD_PAYMENT_SUCCESS_URI + "/{forwardPaymentId}", method = RequestMethod.GET)
    public String forwardpaymentsuccess(@PathVariable("forwardPaymentId") ForwardPaymentRequest forwardPayment, Model model) {
        return super.forwardpaymentsuccess(forwardPayment, model);
    }

    private static final String FORWARD_PAYMENT_INSUCCESS_URI = "/forwardpaymentinsuccess";
    public static final String FORWARD_PAYMENT_INSUCCESS_URL = CONTROLLER_URL + FORWARD_PAYMENT_INSUCCESS_URI;

    @RequestMapping(value = FORWARD_PAYMENT_INSUCCESS_URI + "/{forwardPaymentId}", method = RequestMethod.GET)
    public String forwardpaymentinsuccess(@PathVariable("forwardPaymentId") ForwardPaymentRequest forwardPayment, Model model) {
        return super.forwardpaymentinsuccess(forwardPayment, model);
    }

    private static final String PRINT_SETTLEMENT_NOTE_URI = "/printsettlementnote";
    public static final String PRINT_SETTLEMENT_NOTE_URL = CONTROLLER_URL + PRINT_SETTLEMENT_NOTE_URI;

    @Override
    public String readPrintSettlementNoteUrl() {
        return PRINT_SETTLEMENT_NOTE_URL;
    }

    @RequestMapping(value = PRINT_SETTLEMENT_NOTE_URI + "/{settlementNoteId}", produces = "application/pdf")
    @ResponseBody
    @Override
    public Object printsettlementnote(@PathVariable("settlementNoteId") SettlementNote settlementNote, Model model,
            RedirectAttributes redirectAttributes) {
        return super.printsettlementnote(settlementNote, model, redirectAttributes);
    }

}
