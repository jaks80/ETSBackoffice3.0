package com.ets.fe.pnr.task;

import com.ets.fe.pnr.model.Pnr;
import com.ets.fe.pnr.ws.PnrWSClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.SwingWorker;

public class PnrHistoryTask extends SwingWorker<Void, Integer> {

    private List<Pnr> list = new ArrayList<>();
    private String bookingAgt;
    private String ticketingAgt;
    private Date from;
    private Date to;

    public PnrHistoryTask(String bookingAgt, String ticketingAgt, Date from, Date to) {
        this.bookingAgt = bookingAgt;
        this.ticketingAgt = ticketingAgt;
        this.from = from;
        this.to = to;
    }


    @Override
    protected Void doInBackground() {

        PnrWSClient client = new PnrWSClient();
        list = client.searchPnrHistory(bookingAgt, ticketingAgt, from, to);
        setProgress(25);

        return null;
    }

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
