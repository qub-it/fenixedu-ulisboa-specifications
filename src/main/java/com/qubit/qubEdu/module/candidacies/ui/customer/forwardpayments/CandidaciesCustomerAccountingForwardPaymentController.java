/**
 * OMNIS.CLOUD SOFTWARE LICENSE
 *
 * Copyright (C) 2008-2021 Quorum Born IT <https://www.qub-it.com>
 * All rights reserved.
 *
 * 1. This software and all its associated and derivative rights belong
 * to Quorum Born IT (Quorum Born IT).
 *
 * 2. Copying, redistribution and use of this software, in source code
 * and/or binary forms are exclusive to Quorum Born IT and/or its partners.
 * The terms of the agreements between Quorum Born IT and its partners must
 * be defined in a written and valid partnership agreement, signed by both
 * entities.
 *
 * 3. Commercial use in source code and/or binary forms are exclusive to
 * Quorum Born IT, being licensed and not sold.
 *
 * 4. Quorum Born IT may grant a limited and controlled license to other
 * entities to use this software, following established partner agreements,
 * but commercial exploration and/or resale and/or sublicensing are not
 * allowed in any circunstances.
 *
 * 5. Quorum Born IT might authorize read-only access to this software in
 * the context of non-commercial use and/or within academic research and/or
 * educational contexts, provided that the following conditions are met:
 * 	a) All the individuals that will access the software must receive an
 * 	explicit authorization for it by Quorum Born IT, configured via
 * 	source-control system or similar mechanisms.
 * 	b) Quorum Born IT must be informed of the dates to end the access.
 * 	c) The software, being in source code and/or binary forms, is not
 * 	to be incorporated into proprietary software (unless belonging to
 * 	Quorum Born IT).
 *
 * 6. Modifications to this software by Quorum Born IT partners are permitted
 * provided that the following conditions are met:
 * 	a) Redistributions of source code must retain this license file.
 * 	b) Redistributions in binary form must reproduce this license file
 * 	in the documentation and/or other materials within the correspondent
 * 	distribution package.
 * 	c) Submitting back to Quorum Born IT all the modified versions of this
 * 	software source code.
 * 	d) Never to modify any binary forms of this software.
 * 	e) Unless it is Quorum Born IT incorporating received source code
 * 	modifications of this software in its base version, followed it with
 * 	the associated release, there is an explicit renounce to any Quorum
 * 	Born IT liability regarding the proper functioning of this software
 * 	and/or other software with which it interacts and/or integrates,
 * 	when modified by entities other than Quorum Born IT.
 *
 * 7. Activities involving the manipulation of this software not described in
 * this license, being in source code and/or binary forms, are outside its
 * scope and therefore are not allowed.
 *
 * 8. This software may not be error free, being licensed "as is" and
 * Quorum Born IT gives no express warranties, guarantees or conditions.
 * 	a) To the extent permitted under applicable laws, Quorum Born IT
 * 	excludes all implied warranties, including merchantability, fitness
 * 	for a particular purpose, and non-infringement.
 * 	b) Quorum Born IT shall not be liable for any lost profits, lost
 * 	revenues, lost opportunities, downtime, or any consequential damages
 * 	or costs, resulting from any claim or cause of action based on breach
 * 	of warranty, breach of contract, negligence, or any other legal theory,
 * 	that arises from the use of this software by others.
 *
 * 9. Everyone is permitted to copy and distribute verbatim copies of this
 * license document, but changes to it are not allowed.
 *
 *
 * Last updated: 13 July 2021
 */
package com.qubit.qubEdu.module.candidacies.ui.customer.forwardpayments;

import static org.fenixedu.treasury.util.TreasuryConstants.treasuryBundle;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
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
    protected String redirectToDebtAccountUrl(final DebtAccount debtAccount, final Model model, final RedirectAttributes redirectAttributes) {
        return redirect(readDebtAccountUrl() + activeDebtAccount(debtAccount).getExternalId(), model, redirectAttributes);
    }

    private DebtAccount activeDebtAccount(final DebtAccount debtAccount) {
        return DebtAccount.findUnique(debtAccount.getFinantialInstitution(), ((PersonCustomer) debtAccount.getCustomer()).getActiveCustomer()).get();
    }

    @RequestMapping(value = CHOOSE_INVOICE_ENTRIES_URI + "{debtAccountId}/{digitalPaymentPlatformId}")
    @Override
    public String chooseInvoiceEntries(
            @PathVariable(value = "debtAccountId") DebtAccount debtAccount,
            @PathVariable("digitalPaymentPlatformId") DigitalPaymentPlatform digitalPaymentPlatform,
            @RequestParam(value = "bean", required = false) SettlementNoteBean bean, 
            Model model, 
            final RedirectAttributes redirectAttributes) {
        return super.chooseInvoiceEntries(debtAccount, digitalPaymentPlatform, bean, model, redirectAttributes);
    }

    @RequestMapping(value = CHOOSE_INVOICE_ENTRIES_URI, method = RequestMethod.POST)
    @Override
    public String chooseInvoiceEntries(@RequestParam(value = "bean", required = true) SettlementNoteBean bean, Model model, final RedirectAttributes redirectAttributes) {
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
    public String forwardpaymentsuccess(@PathVariable("forwardPaymentId") ForwardPaymentRequest forwardPayment,
            Model model) {
        return super.forwardpaymentsuccess(forwardPayment, model);
    }

    private static final String FORWARD_PAYMENT_INSUCCESS_URI = "/forwardpaymentinsuccess";
    public static final String FORWARD_PAYMENT_INSUCCESS_URL = CONTROLLER_URL + FORWARD_PAYMENT_INSUCCESS_URI;

    @RequestMapping(value = FORWARD_PAYMENT_INSUCCESS_URI + "/{forwardPaymentId}", method = RequestMethod.GET)
    public String forwardpaymentinsuccess(@PathVariable("forwardPaymentId") ForwardPaymentRequest forwardPayment,
            Model model) {
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
