package com.ets.fe.client.task;

import com.ets.fe.client.collection.Agents;
import com.ets.fe.client.ws.AgentWSClient;
import com.ets.fe.util.Enums;
import javax.swing.JProgressBar;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Yusuf
 */
public class AgentSearchTask extends ContactableSearchTask {

    private String name = null;
    private String officeID = null;
    private String postCode = null;
    private String keyword = null;
    private Enums.AgentType agentType;
    private JXBusyLabel busyLabel = null;
    private JProgressBar progressBar = null;

    public AgentSearchTask(String name, String postCode, String officeID,
            JXBusyLabel busyLabel, Enums.AgentType agentType) {
        this.name = name;
        this.officeID = officeID;
        this.postCode = postCode;
        this.busyLabel = busyLabel;
        this.agentType = agentType;
    }
    
    public AgentSearchTask(String name, String postCode, String officeID,
            JProgressBar progressBar, Enums.AgentType agentType) {
        this.name = name;
        this.officeID = officeID;
        this.postCode = postCode;
        this.progressBar = progressBar;
        this.agentType = agentType;
    }

    public AgentSearchTask() {
    }

    public AgentSearchTask(String keyword) {
        this.keyword = keyword;
    }

    public AgentSearchTask(JXBusyLabel busyLabel, Enums.AgentType agentType) {
        this.busyLabel = busyLabel;
        this.agentType = agentType;
    }

    @Override
    protected Agents doInBackground() {

        setProgress(10);
        if (busyLabel != null) {
            busyLabel.setBusy(true);
        }
        
        if(progressBar!=null){
         progressBar.setIndeterminate(true);
        }
        
        AgentWSClient client = new AgentWSClient();

        Agents agents = null;
        if (keyword != null) {
            agents = client.find(keyword);
        } else {
            if (agentType.equals(Enums.AgentType.ALL)) {
                agents = client.find(name, postCode, officeID);
            } else if (agentType.equals(Enums.AgentType.TICKETING_AGT)) {
                agents = client.findTicketingsAgent();
            }
        }

        return agents;
    }

    @Override
    protected void done() {
        if (busyLabel != null) {
            busyLabel.setBusy(false);
        }
        if(progressBar!=null){
         progressBar.setIndeterminate(false);
        }
        setProgress(100);
    }
}
