/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.domain;

import java.io.Serializable;
import javax.persistence.*;
/**
 *
 * @author Yusuf
 */
@Entity
@PrimaryKeyJoinColumn(name = "VENDORID")
@Table(name = "vendor")
public class Vendor extends Contactable implements Serializable {
    
    private String name;
    private String web;
    private String companyRegNo;
    
    public Vendor(){
    
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getCompanyRegNo() {
        return companyRegNo;
    }

    public void setCompanyRegNo(String companyRegNo) {
        this.companyRegNo = companyRegNo;
    }    
}
