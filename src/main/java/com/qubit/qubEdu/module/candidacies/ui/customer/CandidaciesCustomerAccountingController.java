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
package com.qubit.qubEdu.module.candidacies.ui.customer;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academictreasury.ui.customer.forwardpayments.CustomerAccountingForwardPaymentController;
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
        return String.format(CONTROLLER_URL + "/read/%s/forwardpayment/%s", debtAccount.getExternalId(), digitalPaymentPlatform.getExternalId());
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
    public String readAccount(@PathVariable(value = "oid") DebtAccount debtAccount, Model model, final RedirectAttributes redirectAttributes) {
        return super.readAccount(debtAccount, model, redirectAttributes);
    }

    @RequestMapping(value = "/read/{oid}/forwardpayment/{digitalPaymentPlatformId}")
    public String processReadToForwardPayment(
            @PathVariable("oid") DebtAccount debtAccount,
            @PathVariable("digitalPaymentPlatformId") DigitalPaymentPlatform digitalPaymentPlatform,
            Model model,
            RedirectAttributes redirectAttributes) {
        return redirect(CandidaciesCustomerAccountingForwardPaymentController.CHOOSE_INVOICE_ENTRIES_URL + debtAccount.getExternalId() +
                "/" + digitalPaymentPlatform.getExternalId(),
                model, redirectAttributes);
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
