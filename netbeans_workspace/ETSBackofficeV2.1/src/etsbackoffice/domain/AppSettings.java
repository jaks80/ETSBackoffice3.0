package etsbackoffice.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author Yusuf
 * Ref for image handling http://stackoverflow.com/questions/1177702/store-and-retrieve-image-by-hibernate
 */
@Entity
@Table(name = "appsettings")
public class AppSettings implements Serializable {

    private long settingsId;
    private String tInvTAndC;
    private String oInvTAndC;
    private String tInvFooter;
    private String oInvFooter;
    
    public AppSettings() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tandcid")
    @TableGenerator(name = "tandcid", table = "tandcidpktb",
    pkColumnName = "tandcidkey", pkColumnValue = "tandcidvalue", allocationSize = 1)
    public long getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(long settingsId) {
        this.settingsId = settingsId;
    }

    @Column(length=1000)
    public String gettInvTAndC() {
        return tInvTAndC;
    }

    public void settInvTAndC(String tInvTAndC) {
        this.tInvTAndC = tInvTAndC;
    }

    @Column(length=1000)
    public String getoInvTAndC() {
        return oInvTAndC;
    }

    public void setoInvTAndC(String oInvTAndC) {
        this.oInvTAndC = oInvTAndC;
    }

    @Column(length=500)
    public String gettInvFooter() {
        return tInvFooter;
    }

    public void settInvFooter(String tInvFooter) {
        this.tInvFooter = tInvFooter;
    }

    @Column(length=500)
    public String getoInvFooter() {
        return oInvFooter;
    }

    public void setoInvFooter(String oInvFooter) {
        this.oInvFooter = oInvFooter;
    }
}
