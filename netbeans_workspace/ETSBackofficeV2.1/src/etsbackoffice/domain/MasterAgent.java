package etsbackoffice.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 */
@Entity
//@PrimaryKeyJoinColumn(name = "MAGTID")
@Table(name = "masteragent")
public class MasterAgent extends Agent implements Serializable {

    private String mAgentCode;
    private String emailPassword;
    private String emailHost;
    private int port;

    public MasterAgent(){
    super();
    }    

    @Basic
    @Column(name = "EMAILPASSWORD")
    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    @Basic
    @Column(name = "EMAILHOST")
    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    @Basic
    @Column(name = "EMAILPORT")
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getmAgentCode() {
        return mAgentCode;
    }

    public void setmAgentCode(String mAgentCode) {
        this.mAgentCode = mAgentCode;
    }    
}
