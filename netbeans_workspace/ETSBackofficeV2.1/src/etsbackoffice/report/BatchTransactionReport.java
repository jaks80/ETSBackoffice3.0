/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.report;

import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.domain.Agent;
import etsbackoffice.domain.BatchBillingTransaction;
import etsbackoffice.domain.BatchTransaction;
import etsbackoffice.domain.MasterAgent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class BatchTransactionReport {

    private MasterAgent mAgent;
    private Agent agent;
    private BatchTransaction batch;
    private BatchBillingTransaction billingBatch;
    private int BatchType = 0;//1. Batch received 2. Batch Bill payment

    public BatchTransactionReport(BatchTransaction batch, Agent agent) {
        this.batch = batch;
        this.mAgent = AuthenticationBo.getmAgent();
        this.agent = agent;
    }

    public BatchTransactionReport(BatchBillingTransaction billingBatch, Agent agent) {
        this.billingBatch = billingBatch;
        this.mAgent = AuthenticationBo.getmAgent();
        this.agent = agent;
    }
        

    public MasterAgent getmAgent() {
        return mAgent;
    }

    public void setmAgent(MasterAgent mAgent) {
        this.mAgent = mAgent;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public int getBatchType() {
        return BatchType;
    }

    public void setBatchType(int BatchType) {
        this.BatchType = BatchType;
    }

    public BatchTransaction getBatch() {
        return batch;
    }

    public void setBatch(BatchTransaction batch) {
        this.batch = batch;
    }

    public BatchBillingTransaction getBillingBatch() {
        return billingBatch;
    }

    public void setBillingBatch(BatchBillingTransaction billingBatch) {
        this.billingBatch = billingBatch;
    }

    public void printBatchTReport() {
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        rptAcDoc.printBatchTransaction(invObject);
    }

    public void viewBatchTReport() {
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        rptAcDoc.viewBatchTransaction(invObject);
    }

    public void emailBatchTReport() {
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        String subject = AuthenticationBo.getmAgent().getName() + " :Batch transaction receipt";
        String body = "Batch transaction receipt from : " + AuthenticationBo.getmAgent().getName();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        rptAcDoc.emailBatchTransaction(this.agent.getEmail(), subject, body, invObject);
    }

    public void printBillingReport() {
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        rptAcDoc.printBillingReport(invObject);
    }

    public void viewBillingReport() {
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        rptAcDoc.viewBillingReport(invObject);
    }

    public void emailBillingReport() {
        BackofficeReporting rptAcDoc = new BackofficeReporting();
        String subject = AuthenticationBo.getmAgent().getName() + " :Billing receipt";
        String body = "Billing receipt from : " + AuthenticationBo.getmAgent().getName();
        List<Object> invObject = new ArrayList();

        invObject.add(this);
        rptAcDoc.emailBillingReport(this.agent.getEmail(), subject, body, invObject);
    }
}
