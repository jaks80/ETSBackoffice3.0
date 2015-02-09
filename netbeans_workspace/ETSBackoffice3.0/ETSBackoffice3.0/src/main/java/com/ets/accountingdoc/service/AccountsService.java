package com.ets.accountingdoc.service;

import com.ets.accountingdoc.dao.OtherSalesAcDocDAO;
import com.ets.accountingdoc.dao.TPurchaseAcDocDAO;
import com.ets.accountingdoc.dao.TSalesAcDocDAO;
import com.ets.accountingdoc.domain.OtherSalesAcDoc;
import com.ets.accountingdoc.domain.TicketingPurchaseAcDoc;
import com.ets.accountingdoc.domain.TicketingSalesAcDoc;
import com.ets.accountingdoc.model.AccountsReport;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("accountsService")
public class AccountsService {

    @Resource(name = "tSalesAcDocDAO")
    private TSalesAcDocDAO sales_dao;

    @Resource(name = "tPurchaseAcDocDAO")
    private TPurchaseAcDocDAO purchase_dao;
    
    @Resource(name = "otherSalesAcDocDAO")
    private OtherSalesAcDocDAO other_dao;


    public AccountsReport generateClientStatement(Enums.ClientType clienttype, Long clientid, Date from, Date to) {

        BigDecimal balance_brought_forward = sales_dao.getAccountBallanceToDate(clienttype, clientid, from);
        List<TicketingSalesAcDoc> docs = sales_dao.findAllDocuments(clienttype, clientid, from, to);

        AccountsReport report = new AccountsReport();
        report.setOpeningBalance(balance_brought_forward.toString());

        BigDecimal line_balance = balance_brought_forward;

        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal totalDMAmount = new BigDecimal("0.00");
        BigDecimal totalCMAmount = new BigDecimal("0.00");
        BigDecimal totalPayment = new BigDecimal("0.00");
        BigDecimal totalRefund = new BigDecimal("0.00");

        for (TicketingSalesAcDoc doc : docs) {

            AccountsReport.AccountsLine line = new AccountsReport.AccountsLine();

            line_balance = line_balance.add(doc.getDocumentedAmount());
            line.setLine_balance(line_balance.toString());

            if (Enums.AcDocType.INVOICE.equals(doc.getType())
                    || Enums.AcDocType.REFUND.equals(doc.getType())
                    || Enums.AcDocType.DEBITMEMO.equals(doc.getType())) {

                line.setDebit_amount(doc.getDocumentedAmount().abs().toString());
                line.setCredit_amount(" ");
            } else {
                line.setCredit_amount(doc.getDocumentedAmount().abs().toString());
                line.setDebit_amount(" ");
            }

            line.setId(doc.getId());
            line.setDate(DateUtil.dateToString(doc.getDocIssueDate()));
            line.setDocType(doc.getType().toString());

            StringBuilder sb = new StringBuilder();
            sb.append(doc.getReference())
                    .append(" ")
                    .append(doc.getPnr().getGdsPnr())
                    .append(" ")
                    .append(doc.getPnr().getAirLineCode())
                    .append(" ");

            if (doc.getRemark() != null) {
                sb.append(doc.getRemark()).append(" ");
            }

            if (Enums.AcDocType.PAYMENT.equals(doc.getType()) || Enums.AcDocType.REFUND.equals(doc.getType())) {
                sb.append(doc.getPayment().getRemark());
            }

            line.setLine_desc(sb.toString());
            report.addLine(line);

            if (Enums.AcDocType.INVOICE.equals(doc.getType())) {
                totalInvAmount = totalInvAmount.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.REFUND.equals(doc.getType())) {
                totalRefund = totalRefund.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.DEBITMEMO.equals(doc.getType())) {
                totalDMAmount = totalDMAmount.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.PAYMENT.equals(doc.getType())) {
                totalPayment = totalPayment.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.CREDITMEMO.equals(doc.getType())) {
                totalCMAmount = totalCMAmount.add(doc.getDocumentedAmount().abs());
            }
        }
        report.setTotalCMAmount(totalCMAmount.toString());
        report.setTotalDMAmount(totalDMAmount.toString());
        report.setTotalInvAmount(totalInvAmount.toString());
        report.setTotalPayment(totalPayment.toString());
        report.setTotalRefund(totalRefund.toString());

        report.setClosingBalance(line_balance.toString());
        return report;
    }

    public AccountsReport generateVendorStatement(Enums.ClientType clienttype, Long clientid, Date from, Date to) {

        BigDecimal balance_brought_forward = purchase_dao.getAccountBallanceToDate(clientid, from);
        List<TicketingPurchaseAcDoc> docs = purchase_dao.findAllDocuments(clientid, from, to);

        AccountsReport report = new AccountsReport();
        report.setOpeningBalance(balance_brought_forward.toString());

        BigDecimal line_balance = balance_brought_forward;

        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal totalDMAmount = new BigDecimal("0.00");
        BigDecimal totalCMAmount = new BigDecimal("0.00");
        BigDecimal totalPayment = new BigDecimal("0.00");
        BigDecimal totalRefund = new BigDecimal("0.00");

        for (TicketingPurchaseAcDoc doc : docs) {

            AccountsReport.AccountsLine line = new AccountsReport.AccountsLine();

            line_balance = line_balance.add(doc.getDocumentedAmount());
            line.setLine_balance(line_balance.toString());

            if (Enums.AcDocType.INVOICE.equals(doc.getType())
                    || Enums.AcDocType.REFUND.equals(doc.getType())
                    || Enums.AcDocType.DEBITMEMO.equals(doc.getType())) {

                line.setDebit_amount(doc.getDocumentedAmount().abs().toString());
                line.setCredit_amount(" ");
            } else {
                line.setCredit_amount(doc.getDocumentedAmount().abs().toString());
                line.setDebit_amount(" ");
            }

            line.setId(doc.getId());
            line.setDate(DateUtil.dateToString(doc.getDocIssueDate()));
            line.setDocType(doc.getType().toString());

            StringBuilder sb = new StringBuilder();
            sb.append(doc.getReference())
                    .append(" ")
                    .append(doc.getPnr().getGdsPnr())
                    .append(" ")
                    .append(doc.getPnr().getAirLineCode())
                    .append(" ");

            if (doc.getRemark() != null) {
                sb.append(doc.getRemark()).append(" ");
            }

            if (Enums.AcDocType.PAYMENT.equals(doc.getType()) || Enums.AcDocType.REFUND.equals(doc.getType())) {
                sb.append(doc.getPayment().getRemark());
            }

            line.setLine_desc(sb.toString());
            report.addLine(line);

            if (Enums.AcDocType.INVOICE.equals(doc.getType())) {
                totalInvAmount = totalInvAmount.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.REFUND.equals(doc.getType())) {
                totalRefund = totalRefund.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.DEBITMEMO.equals(doc.getType())) {
                totalDMAmount = totalDMAmount.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.PAYMENT.equals(doc.getType())) {
                totalPayment = totalPayment.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.CREDITMEMO.equals(doc.getType())) {
                totalCMAmount = totalCMAmount.add(doc.getDocumentedAmount().abs());
            }
        }
        report.setTotalCMAmount(totalCMAmount.toString());
        report.setTotalDMAmount(totalDMAmount.toString());
        report.setTotalInvAmount(totalInvAmount.toString());
        report.setTotalPayment(totalPayment.toString());
        report.setTotalRefund(totalRefund.toString());

        report.setClosingBalance(line_balance.toString());
        return report;
    }
    
    public AccountsReport generateClientStatementOther(Enums.ClientType clienttype, Long clientid, Date from, Date to) {

        BigDecimal balance_brought_forward = other_dao.getAccountBallanceToDate(clienttype, clientid, from);
        List<OtherSalesAcDoc> docs = other_dao.findAllDocuments(clienttype, clientid, from, to);

        AccountsReport report = new AccountsReport();
        report.setOpeningBalance(balance_brought_forward.toString());

        BigDecimal line_balance = balance_brought_forward;

        BigDecimal totalInvAmount = new BigDecimal("0.00");
        BigDecimal totalDMAmount = new BigDecimal("0.00");
        BigDecimal totalCMAmount = new BigDecimal("0.00");
        BigDecimal totalPayment = new BigDecimal("0.00");
        BigDecimal totalRefund = new BigDecimal("0.00");

        for (OtherSalesAcDoc doc : docs) {

            AccountsReport.AccountsLine line = new AccountsReport.AccountsLine();

            line_balance = line_balance.add(doc.getDocumentedAmount());
            line.setLine_balance(line_balance.toString());

            if (Enums.AcDocType.INVOICE.equals(doc.getType())
                    || Enums.AcDocType.REFUND.equals(doc.getType())
                    || Enums.AcDocType.DEBITMEMO.equals(doc.getType())) {

                line.setDebit_amount(doc.getDocumentedAmount().abs().toString());
                line.setCredit_amount(" ");
            } else {
                line.setCredit_amount(doc.getDocumentedAmount().abs().toString());
                line.setDebit_amount(" ");
            }

            line.setId(doc.getId());
            line.setDate(DateUtil.dateToString(doc.getDocIssueDate()));
            line.setDocType(doc.getType().toString());

            StringBuilder sb = new StringBuilder();
            sb.append(doc.getReference());                    ;

            if (doc.getRemark() != null) {
                sb.append(doc.getRemark()).append(" ");
            }

            if (Enums.AcDocType.PAYMENT.equals(doc.getType()) || Enums.AcDocType.REFUND.equals(doc.getType())) {
                sb.append(doc.getPayment().getRemark());
            }

            line.setLine_desc(sb.toString());
            report.addLine(line);

            if (Enums.AcDocType.INVOICE.equals(doc.getType())) {
                totalInvAmount = totalInvAmount.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.REFUND.equals(doc.getType())) {
                totalRefund = totalRefund.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.DEBITMEMO.equals(doc.getType())) {
                totalDMAmount = totalDMAmount.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.PAYMENT.equals(doc.getType())) {
                totalPayment = totalPayment.add(doc.getDocumentedAmount().abs());
            } else if (Enums.AcDocType.CREDITMEMO.equals(doc.getType())) {
                totalCMAmount = totalCMAmount.add(doc.getDocumentedAmount().abs());
            }
        }
        report.setTotalCMAmount(totalCMAmount.toString());
        report.setTotalDMAmount(totalDMAmount.toString());
        report.setTotalInvAmount(totalInvAmount.toString());
        report.setTotalPayment(totalPayment.toString());
        report.setTotalRefund(totalRefund.toString());

        report.setClosingBalance(line_balance.toString());
        return report;
    }    
}
