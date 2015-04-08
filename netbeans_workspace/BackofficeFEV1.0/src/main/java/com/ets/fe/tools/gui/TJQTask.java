package com.ets.fe.tools.gui;

import com.ets.fe.Application;
import com.ets.fe.pnr.model.*;
import com.ets.fe.pnr.ws.TicketWSClient;
import com.ets.fe.tools.logic.TJQHelper;
import com.ets.fe.util.Enums;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Yusuf
 */
public class TJQTask extends SwingWorker<Void, Void> {

    private JProgressBar progressBar;
    private JTextArea txtStatus;
    private JXTable tblTicket;
    private JXTable tblTjq;
    private JTextArea txtMissing;

    private List<Ticket> tjqTickets = new ArrayList<>();
    private List<Ticket> dbTickets;
    private List<Ticket> missingInDBList;
    private List<Ticket> missingInTJQList;

    private Date from;
    private Date to;

    public TJQTask(JTextArea txtStatus, JProgressBar progressBar,
            JXTable tblTicket, JXTable tblTjq, JTextArea txtMissing, List<Ticket> tjqTickets, Date from, Date to) {
        this.progressBar = progressBar;
        this.txtStatus = txtStatus;
        this.tblTicket = tblTicket;
        this.tblTjq = tblTjq;
        this.txtMissing = txtMissing;
        
        this.tjqTickets = tjqTickets;
        this.from = from;
        this.to = to;
    }

    @Override
    public Void doInBackground() {
        txtStatus.setText("");        
        dbTickets = new ArrayList<>();
        missingInDBList = new ArrayList<>();
        missingInTJQList = new ArrayList<>();

        TicketWSClient client = new TicketWSClient();
        GDSSaleReport saleReport = client.gdsSaleReport(Enums.TicketingType.IATA, null, null,
                from, to, Application.getMainAgent().getOfficeID());

        dbTickets = saleReport.getList();

        //List total match first. We dont need this ones.
        Iterator<Ticket> tjqIt = getTjqTickets().iterator();

        while (tjqIt.hasNext()) {
            Ticket tjqticket = tjqIt.next();

            Iterator<Ticket> dbIt = getDbTickets().iterator();
            while (dbIt.hasNext()) {
                Ticket dbticket = dbIt.next();
                if (TJQHelper.totalMatch(tjqticket, dbticket)) {
                    dbIt.remove();
                    tjqIt.remove();
                }
            }
        }

        findDiscrepancy(this.tjqTickets, this.dbTickets);
        return null;
    }

    private void findDiscrepancy(List<Ticket> _tjqTickets, List<Ticket> _dbTickets) {
        DefaultTableModel ticketModel = (DefaultTableModel) tblTicket.getModel();
        ticketModel.getDataVector().removeAllElements();

        DefaultTableModel tjqModel = (DefaultTableModel) tblTjq.getModel();
        tjqModel.getDataVector().removeAllElements();

        //Missing in db list
        boolean exist = false;
        for (Ticket tjqt : _tjqTickets) {
            exist = false;
            for (Ticket dbt : _dbTickets) {
                if (dbt.getTicketNo().equals(tjqt.getTicketNo())
                        && dbt.getPnr().getGdsPnr().equals(tjqt.getPnr().getGdsPnr())) {
                    populateTblTjq(tjqModel, tjqt);
                    populateTblTicket(ticketModel, dbt);
                    exist = true;
                }
            }
            if (!exist) {
                this.missingInDBList.add(tjqt);
            }
        }
        txtStatus.append("Missing in BO: " + this.missingInDBList.size()+"\n");
        for (Ticket tjqt : missingInDBList) {
            populateTblTjq(tjqModel, tjqt);
        }

        //Mising in tjq list
        exist = false;
        for (Ticket dbt : _dbTickets) {
            for (Ticket tjqt : _tjqTickets) {
                if (tjqt.getTicketNo().equals(dbt.getTicketNo())
                        && dbt.getPnr().getGdsPnr().equals(tjqt.getPnr().getGdsPnr())) {
                    exist = true;
                }
            }

            if (!exist) {
                this.missingInTJQList.add(dbt);
            }
        }
        txtStatus.append("Excess in BO: " + this.missingInTJQList.size());
        for (Ticket dbt : missingInTJQList) {
            populateTblTicket(ticketModel, dbt);
        }
    }



    private void populateTblTjq(DefaultTableModel tjqModel, Ticket t) {

        int row = tjqModel.getRowCount();

        if (t != null) {
            tjqModel.insertRow(row, new Object[]{t.getTicketNo(), t.getPnr().getGdsPnr(), t.getBaseFare(),
                t.getTax(), t.getFee(), t.getCommission(), t.getTktStatus()});
        } else {
            tjqModel.insertRow(row, new Object[]{"", "", "", "", "", ""});
        }
    }

    private void populateTblTicket(DefaultTableModel ticketModel, Ticket t) {
        int row = ticketModel.getRowCount();

        if (t != null) {
            ticketModel.insertRow(row, new Object[]{t.getTicketNo(), t.getPnr().getGdsPnr(),
                t.getBaseFare(), t.getTax(), t.getFee(), t.getCommission(), t.getTktStatus()});
        } else {
            ticketModel.insertRow(row, new Object[]{"", "", "", "", "", "", ""});
        }
    }

    private void update(String message, int progress) {
        txtStatus.append(message);
        txtStatus.append("\n");
        setProgress(progress);
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    public List<Ticket> getTjqTickets() {
        return tjqTickets;
    }

    public List<Ticket> getDbTickets() {
        return dbTickets;
    }

    public List<Ticket> getMissingInDBList() {
        return missingInDBList;
    }

    public List<Ticket> getMissingInTJQList() {
        return missingInTJQList;
    }
}
