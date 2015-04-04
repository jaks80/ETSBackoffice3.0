package com.ets.fe.a_maintask;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.ws.PnrWSClient;
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
public class PnrSearchTask extends SwingWorker< Void, Integer> {

    private List<Pnr> pnrs = new ArrayList<>();       
    private JXBusyLabel busyLabel;
    private JTable table;
    private String gdsPnr = null;
    private String invRef = null;
    private String name = null;
    private String searchType = null;

    public PnrSearchTask(String searchType, JXBusyLabel busyLabel, JTable table) {
        this.searchType = searchType;
        this.busyLabel = busyLabel;
        this.table = table;
    }

    public PnrSearchTask(String searchType, JXBusyLabel busyLabel, JTable table, String gdsPnr, String invRef, String name) {
        this.searchType = searchType;
        this.busyLabel = busyLabel;
        this.table = table;
        this.gdsPnr = gdsPnr;
        this.invRef = invRef;
        this.name = name;
    }

    @Override
    protected Void doInBackground() {
        busyLabel.setBusy(true);
        PnrWSClient client = new PnrWSClient();

        switch (searchType) {
            case "UNINVOICED_PNR":
                pnrs = new ArrayList<>();
                pnrs = client.getUninvoicedPnr();
                break;
            case "PNRTODAY":
                pnrs = new ArrayList<>();
                pnrs = client.getPnrsToday();
                break;    
            case "QUERY_SEARCH":
                pnrs = new ArrayList<>();
                if (gdsPnr != null && !gdsPnr.isEmpty()) {
                    pnrs = client.searchPnrByGdsPnr(gdsPnr);
                } else if (name != null && !name.isEmpty()) {
                    String[] names = PnrUtil.splitName(name);
                    pnrs = client.searchPnrByName(names[0], names[1]);
                } else if (invRef != null && !invRef.isEmpty()) {

                }
                break;
            default:
        }

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

        if (this.pnrs.size() > 0) {
            for (int i = 0; i < this.pnrs.size(); i++) {
                Pnr p = this.pnrs.get(i);
                tableModel.insertRow(i, new Object[]{i+1,p.getGdsPnr(), PnrUtil.calculatePartialName(PnrUtil.calculateLeadPaxName(p.getTickets())), p.getNoOfPax(),
                    p.getTicketingAgentSine(), p.getBookingAgtOid(), p.getTicketingAgtOid(), p.getAirLineCode()});
            }
        } else {
            tableModel.insertRow(0, new Object[]{});
        }
        table.setRowSelectionInterval(0, 0);
    }

    public List<Pnr> getPnrs() {
        return pnrs;
    }
}
