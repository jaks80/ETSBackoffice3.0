package com.ets.fe.pnr.task;

import com.ets.fe.a_maintask.PnrSearchTask;
import com.ets.fe.pnr.model.Ticket;
import com.ets.fe.pnr.ws.PnrWSClient;
import com.ets.fe.pnr.ws.TicketWSClient;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Yusuf
 */
public class TicketTask extends SwingWorker<Ticket, Integer> {

    private Ticket ticket = null;
    private JXBusyLabel busyLabel;
    private String taskType = "";
    private Integer status = 0;

    public TicketTask(Ticket ticket, String taskType, JXBusyLabel busyLabel) {
        this.ticket = ticket;
        this.busyLabel = busyLabel;
        this.taskType = taskType;
    }

    @Override
    protected Ticket doInBackground() {

        TicketWSClient client = new TicketWSClient();

        if (taskType.equals("UPDATE")) {
            ticket = client.update(ticket);
        } else if (taskType.equals("DELETE")) {
            status = client.delete(ticket.getId());
        }

        return ticket;
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    public Integer getStatus() {
        return status;
    }
}
