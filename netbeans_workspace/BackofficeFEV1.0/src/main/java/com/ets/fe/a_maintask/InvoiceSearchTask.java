package com.ets.fe.a_maintask;

import com.ets.fe.acdoc.model.report.InvoiceReport;
import com.ets.fe.acdoc.model.report.TktingInvoiceSummery;
import com.ets.fe.acdoc.ws.TicketingSAcDocWSClient;
import com.ets.fe.acdoc_o.model.InvoiceReportOther;
import com.ets.fe.acdoc_o.model.OtherInvoiceSummery;
import com.ets.fe.acdoc_o.ws.OtherSAcDocWSClient;
import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.util.PnrUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Yusuf
 */
public class InvoiceSearchTask extends SwingWorker< Void, Integer> {

    private List<TktingInvoiceSummery> tsinv_today = new ArrayList<>();
    private List<OtherInvoiceSummery> osinv_today = new ArrayList<>();

    private JXBusyLabel busyLabel;
    private JTable table;
    private String searchType = "";

    public InvoiceSearchTask(String searchType, JXBusyLabel busyLabel, JTable table) {
        this.searchType = searchType;
        this.busyLabel = busyLabel;
        this.table = table;
    }

    @Override
    protected Void doInBackground() throws Exception {
        busyLabel.setBusy(true);

        switch (searchType) {
            case "TSINVOICE_TODAY":
                TicketingSAcDocWSClient client = new TicketingSAcDocWSClient();
                tsinv_today = new ArrayList<>();
                InvoiceReport report = client.documentHistoryReport(null, null, new java.util.Date(), new java.util.Date());
                tsinv_today = report.getInvoices();
                populateTktTable();
                break;
            case "OSINVOICE_TODAY":
                OtherSAcDocWSClient client1 = new OtherSAcDocWSClient();
                osinv_today = new ArrayList<>();
                InvoiceReportOther report1 = client1.documentHistoryReport(null, null, new java.util.Date(), new java.util.Date());
                osinv_today = report1.getInvoices();
                populateOtherTable();
                break;
            default:
        }

        return null;
    }

    @Override
    protected void done() {
        busyLabel.setBusy(false);
    }

    private void populateTktTable() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.getDataVector().removeAllElements();

        if (this.getTsinv_today().size() > 0) {
            for (int i = 0; i < this.getTsinv_today().size(); i++) {
                TktingInvoiceSummery p = this.getTsinv_today().get(i);
                tableModel.insertRow(i, new Object[]{i+1,p.getType(), p.getReference(), p.getClientName(),
                    p.getAirLine(), p.getDocumentedAmount(), PnrUtil.calculatePartialName(p.getInvBy())});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        table.setRowSelectionInterval(0, 0);
    }

    private void populateOtherTable() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.getDataVector().removeAllElements();

        if (this.getOsinv_today().size() > 0) {
            for (int i = 0; i < this.getOsinv_today().size(); i++) {
                OtherInvoiceSummery p = this.getOsinv_today().get(i);
                tableModel.insertRow(i, new Object[]{i+1,p.getType(), p.getReference(), p.getClientName(),
                    p.getDocumentedAmount(), PnrUtil.calculatePartialName(p.getInvBy())});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        table.setRowSelectionInterval(0, 0);
    }

    public List<TktingInvoiceSummery> getTsinv_today() {
        return tsinv_today;
    }

    public List<OtherInvoiceSummery> getOsinv_today() {
        return osinv_today;
    }
}
