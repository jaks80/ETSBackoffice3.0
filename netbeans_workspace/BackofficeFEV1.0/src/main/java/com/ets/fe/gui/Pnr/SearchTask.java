package com.ets.fe.gui.Pnr;

import com.ets.fe.model.pnr.Pnr;
import com.ets.fe.ws.pnr.PnrWSClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class SearchTask extends SwingWorker<Void, Integer> {

    private List<Pnr> list = new ArrayList<>();
    private String bookingAgt;
    private String ticketingAgt;
    private Date from;
    private Date to;

    public SearchTask(String bookingAgt, String ticketingAgt, Date from, Date to) {
        this.bookingAgt = bookingAgt;
        this.ticketingAgt = ticketingAgt;
        this.from = from;
        this.to = to;
    }

    /**
     * Executed in background thread
     */
    @Override
    protected Void doInBackground() {

        PnrWSClient client = new PnrWSClient();
        list = client.searchPnrHistory(bookingAgt, ticketingAgt, from, to);
        setProgress(25);

        return null;
    }

    /**
     * Executed in Swing's event dispatching thread
     */
    @Override
    protected void done() {
        setProgress(100);
        if (!isCancelled()) {
            
        }
    }

    public List<Pnr> getList() {
        return list;
    }
}
