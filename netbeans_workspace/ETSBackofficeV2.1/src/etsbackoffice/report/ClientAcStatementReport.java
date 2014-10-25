/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.report;

import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.domain.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Yusuf
 */
public class ClientAcStatementReport {

    private MasterAgent mAgent;
    
    private Date from = null;
    private Date to = null;
    private BigDecimal openingBalance = new BigDecimal("0.00");
    private BigDecimal closingBalance = new BigDecimal("0.00");
    
    private BigDecimal totalInvAmount = new BigDecimal("0.00");
    private BigDecimal totalCNoteAmount = new BigDecimal("0.00");
    private BigDecimal moneyIn = new BigDecimal("0.00");
    private BigDecimal moneyOut = new BigDecimal("0.00");
    private Agent agent;
    private Customer customer;
    private List<Accounts> statementLines = new ArrayList<Accounts>();
    
    public ClientAcStatementReport(List<Accounts> statementLines,Agent agent, Customer customer){
     this.statementLines = statementLines;
     this.customer = customer;
     this.agent = agent;
     this.mAgent = AuthenticationBo.getmAgent();
     
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public BigDecimal getTotalInvAmount() {
        return totalInvAmount;
    }

    public void setTotalInvAmount(BigDecimal totalInvAmount) {
        this.totalInvAmount = totalInvAmount;
    }

    public BigDecimal getTotalCNoteAmount() {
        return totalCNoteAmount;
    }

    public void setTotalCNoteAmount(BigDecimal totalCNoteAmount) {
        this.totalCNoteAmount = totalCNoteAmount;
    }

    public BigDecimal getMoneyIn() {
        return moneyIn;
    }

    public void setMoneyIn(BigDecimal moneyIn) {
        this.moneyIn = moneyIn;
    }

    public BigDecimal getMoneyOut() {
        return moneyOut;
    }

    public void setMoneyOut(BigDecimal moneyOut) {
        this.moneyOut = moneyOut;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Accounts> getStatementLines() {
        return statementLines;
    }

    public void setStatementLines(List<Accounts> statementLines) {
        this.statementLines = statementLines;
    }

    public MasterAgent getmAgent() {
        return mAgent;
    }

    public void setmAgent(MasterAgent mAgent) {
        this.mAgent = mAgent;
    }
    
    public void viewClientAcStatementReport() {
        Accounts a = new Accounts();
        a.setLineDdesc("Opening Balance");
        a.setBalance(openingBalance);
        //a.setcTransAmount(new BigDecimal("0.00"));
        this.statementLines.add(0,a);
        
        Accounts b = new Accounts();
        b.setLineDdesc("Closing Balance");
        b.setBalance(closingBalance);
        int lastIndex = this.statementLines.size();
        this.statementLines.add(lastIndex, b);
        
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        rptAcDoc.viewClientAcStatement(invObject);
    }
    
    public void emailClientAcStatementReport() {
        Accounts a = new Accounts();
        a.setLineDdesc("Opening Balance");
        a.setBalance(openingBalance);
        //a.setcTransAmount(new BigDecimal("0.00"));
        this.statementLines.add(0, a);

        Accounts b = new Accounts();
        b.setLineDdesc("Closing Balance");
        b.setBalance(closingBalance);
        int lastIndex = this.statementLines.size();
        this.statementLines.add(lastIndex, b);

        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        String subject = AuthenticationBo.getmAgent().getName() + " :accounts statement";
        String body = "Accounts statement from : " + AuthenticationBo.getmAgent().getName();
        
        String address = "";
        if (this.agent != null) {
            address = this.agent.getEmail();
        } else {
            address = this.customer.getEmail();
        }
        
        if (!address.isEmpty()) {
            rptAcDoc.emailClientAcStatement(address, subject, body, invObject);
        } else {
            JOptionPane.showMessageDialog(null, "No email address found", "Email statement", JOptionPane.WARNING_MESSAGE);
        }
    }
   
   public void printClientAcStatementReport(){
   Accounts a = new Accounts();
        a.setLineDdesc("Opening Balance");
        a.setBalance(openingBalance);
        //a.setcTransAmount(new BigDecimal("0.00"));
        this.statementLines.add(0,a);
        
        Accounts b = new Accounts();
        b.setLineDdesc("Closing Balance");
        b.setBalance(closingBalance);
        int lastIndex = this.statementLines.size();
        this.statementLines.add(lastIndex, b);
        
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        rptAcDoc.printClientAcStatement(invObject);
   }
}
