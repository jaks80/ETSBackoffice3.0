package com.ets.fe.a_maintask;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.ws.PnrWSClient;
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
public class UnInvoicedPnrTask extends SwingWorker< Void, Integer> {

    private List<Pnr> unInvoicedPnrs = new ArrayList<>();
    private JXBusyLabel busyLabel;
    private JTable table;
    
    public UnInvoicedPnrTask(JXBusyLabel busyLabel, JTable table) {
        this.busyLabel = busyLabel;
        this.table = table;
    }

    @Override
    protected Void doInBackground() {
        busyLabel.setBusy(true);
        PnrWSClient client = new PnrWSClient();
    
        unInvoicedPnrs = client.getUninvoicedPnr();
        return null;
    }

    @Override
    protected void done() {
        busyLabel.setBusy(false);
        populateTable();
    }

    private void populateTable() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.getDataVector().removeAllElements();

        if (this.getUnInvoicedPnrs().size() > 0) {
            for (int i = 0; i < this.getUnInvoicedPnrs().size(); i++) {
                Pnr p = this.getUnInvoicedPnrs().get(i);
                tableModel.insertRow(i, new Object[]{p.getGdsPnr(), p.getLeadPaxName(), p.getTicketingAgentSine(), p.getBookingAgtOid(), p.getTicketingAgtOid(), p.getAirLineCode()});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
    }

    public List<Pnr> getUnInvoicedPnrs() {
        return unInvoicedPnrs;
    }
}
