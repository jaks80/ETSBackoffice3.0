package com.ets.fe.pnr.task;

//package com.ets.fe.pnr.gui.task;
//
//import com.ets.fe.client.collection.Agents;
//import com.ets.fe.client.ws.AgentWSClient;
//import javax.swing.SwingWorker;
//import org.jdesktop.swingx.JXBusyLabel;
//
///**
// *
// * @author Yusuf
// */
//public class TicketingAgentTask extends SwingWorker<Object, Integer> {
//
//    private final JXBusyLabel busyLabel;
//
//    public TicketingAgentTask(JXBusyLabel busyLabel) {
//        this.busyLabel = busyLabel;
//    }
//
//    @Override
//    protected Agents doInBackground() {
//        busyLabel.setBusy(true);
//        AgentWSClient client = new AgentWSClient();
//
//        Agents agents = client.findTicketingsAgent();
//
//        return agents;
//    }
//
//    @Override
//    protected void done() {
//        busyLabel.setBusy(false);
//        setProgress(100);
//    }
//}
