package etsbackoffice.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 *
 * @author Yusuf
 */
@Entity
@Table(name = "userrole")
public class UserRole implements Serializable {

    private long roleId;    
    private boolean administrator;
    private boolean newUserAllowed;
    private boolean userListAllowed;
    private boolean officeProfileAllowed;
    private boolean bspSettingAllowed;
    private boolean saleReportAllowed;
    private boolean segReportAllowed;
    private boolean billingBspAllowed;
    private boolean billingTPartyAllowed;
    private boolean outstandingBSPAllowed;
    private boolean outstandingTPAllowed;
    private boolean outstandingSInvAllowed;
    private boolean outstandingSRefundAllowed;
    private boolean sInvHistoryAllowed;
    private boolean batchTransactionAllowed;
    private boolean singleTransactionAllowed;
    private boolean editTransactionAllowed;
    private boolean voidtransactionAllowed;
    private boolean debitInTransAllowed;
    private boolean creditInTransAllowed;
    private boolean paymentAllowed;
    private boolean cashBookAllowed;
    private boolean accountsAllowed;
    private boolean deletePnrAllowed;
    private boolean deleteInvoiceAllowed;
    private boolean tktCNoteAllowed;
    private boolean cNoteAllowed;
    
    private boolean manualTktRefundlAllowed;
    private boolean manualTktVoidAllowed;
    private boolean deleteTktAllowed;
    private boolean deleteSegmentAllowed;
    private boolean fareLineAllowed;
    

    public UserRole(){

    }

@Id
    @TableGenerator(name = "roleid", table = "rolepktb",
    pkColumnName = "rolekey", pkColumnValue = "rolevalue", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "roleid")    
    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    /**
     * @return the newUserAllowed
     */
    public boolean isNewUserAllowed() {
        return newUserAllowed;
    }

    /**
     * @param newUserAllowed the newUserAllowed to set
     */
    public void setNewUserAllowed(boolean newUserAllowed) {
        this.newUserAllowed = newUserAllowed;
    }

    /**
     * @return the administrator
     */
    public boolean isAdministrator() {
        return administrator;
    }

    /**
     * @param administrator the administrator to set
     */
    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    /**
     * @return the userListAllowed
     */
    public boolean isUserListAllowed() {
        return userListAllowed;
    }

    /**
     * @param userListAllowed the userListAllowed to set
     */
    public void setUserListAllowed(boolean userListAllowed) {
        this.userListAllowed = userListAllowed;
    }

    /**
     * @return the officeProfileAllowed
     */
    public boolean isOfficeProfileAllowed() {
        return officeProfileAllowed;
    }

    /**
     * @param officeProfileAllowed the officeProfileAllowed to set
     */
    public void setOfficeProfileAllowed(boolean officeProfileAllowed) {
        this.officeProfileAllowed = officeProfileAllowed;
    }

    /**
     * @return the bspSettingAllowed
     */
    public boolean isBspSettingAllowed() {
        return bspSettingAllowed;
    }

    /**
     * @param bspSettingAllowed the bspSettingAllowed to set
     */
    public void setBspSettingAllowed(boolean bspSettingAllowed) {
        this.bspSettingAllowed = bspSettingAllowed;
    }

    /**
     * @return the saleReportAllowed
     */
    public boolean isSaleReportAllowed() {
        return saleReportAllowed;
    }

    /**
     * @param saleReportAllowed the saleReportAllowed to set
     */
    public void setSaleReportAllowed(boolean saleReportAllowed) {
        this.saleReportAllowed = saleReportAllowed;
    }

    /**
     * @return the segReportAllowed
     */
    public boolean isSegReportAllowed() {
        return segReportAllowed;
    }

    /**
     * @param segReportAllowed the segReportAllowed to set
     */
    public void setSegReportAllowed(boolean segReportAllowed) {
        this.segReportAllowed = segReportAllowed;
    }

    /**
     * @return the billingBspAllowed
     */
    public boolean isBillingBspAllowed() {
        return billingBspAllowed;
    }

    /**
     * @param billingBspAllowed the billingBspAllowed to set
     */
    public void setBillingBspAllowed(boolean billingBspAllowed) {
        this.billingBspAllowed = billingBspAllowed;
    }

    /**
     * @return the billingTPartyAllowed
     */
    public boolean isBillingTPartyAllowed() {
        return billingTPartyAllowed;
    }

    /**
     * @param billingTPartyAllowed the billingTPartyAllowed to set
     */
    public void setBillingTPartyAllowed(boolean billingTPartyAllowed) {
        this.billingTPartyAllowed = billingTPartyAllowed;
    }

    /**
     * @return the outstandingBSPAllowed
     */
    public boolean isOutstandingBSPAllowed() {
        return outstandingBSPAllowed;
    }

    /**
     * @param outstandingBSPAllowed the outstandingBSPAllowed to set
     */
    public void setOutstandingBSPAllowed(boolean outstandingBSPAllowed) {
        this.outstandingBSPAllowed = outstandingBSPAllowed;
    }

    /**
     * @return the outstandingTPAllowed
     */
    public boolean isOutstandingTPAllowed() {
        return outstandingTPAllowed;
    }

    /**
     * @param outstandingTPAllowed the outstandingTPAllowed to set
     */
    public void setOutstandingTPAllowed(boolean outstandingTPAllowed) {
        this.outstandingTPAllowed = outstandingTPAllowed;
    }

    /**
     * @return the outstandingSInvAllowed
     */
    public boolean isOutstandingSInvAllowed() {
        return outstandingSInvAllowed;
    }

    /**
     * @param outstandingSInvAllowed the outstandingSInvAllowed to set
     */
    public void setOutstandingSInvAllowed(boolean outstandingSInvAllowed) {
        this.outstandingSInvAllowed = outstandingSInvAllowed;
    }

    /**
     * @return the outstandingSRefundAllowed
     */
    public boolean isOutstandingSRefundAllowed() {
        return outstandingSRefundAllowed;
    }

    /**
     * @param outstandingSRefundAllowed the outstandingSRefundAllowed to set
     */
    public void setOutstandingSRefundAllowed(boolean outstandingSRefundAllowed) {
        this.outstandingSRefundAllowed = outstandingSRefundAllowed;
    }

    /**
     * @return the sInvHistoryAllowed
     */
    public boolean issInvHistoryAllowed() {
        return sInvHistoryAllowed;
    }

    /**
     * @param sInvHistoryAllowed the sInvHistoryAllowed to set
     */
    public void setsInvHistoryAllowed(boolean sInvHistoryAllowed) {
        this.sInvHistoryAllowed = sInvHistoryAllowed;
    }

    /**
     * @return the batchTransactionAllowed
     */
    public boolean isBatchTransactionAllowed() {
        return batchTransactionAllowed;
    }

    /**
     * @param batchTransactionAllowed the batchTransactionAllowed to set
     */
    public void setBatchTransactionAllowed(boolean batchTransactionAllowed) {
        this.batchTransactionAllowed = batchTransactionAllowed;
    }

    /**
     * @return the singleTransactionAllowed
     */
    public boolean isSingleTransactionAllowed() {
        return singleTransactionAllowed;
    }

    /**
     * @param singleTransactionAllowed the singleTransactionAllowed to set
     */
    public void setSingleTransactionAllowed(boolean singleTransactionAllowed) {
        this.singleTransactionAllowed = singleTransactionAllowed;
    }

    /**
     * @return the paymentAllowed
     */
    public boolean isPaymentAllowed() {
        return paymentAllowed;
    }

    /**
     * @param paymentAllowed the paymentAllowed to set
     */
    public void setPaymentAllowed(boolean paymentAllowed) {
        this.paymentAllowed = paymentAllowed;
    }

    /**
     * @return the cashBookAllowed
     */
    public boolean isCashBookAllowed() {
        return cashBookAllowed;
    }

    public void setCashBookAllowed(boolean cashBookAllowed) {
        this.cashBookAllowed = cashBookAllowed;
    }

    public boolean isAccountsAllowed() {
        return accountsAllowed;
    }

    public void setAccountsAllowed(boolean accountsAllowed) {
        this.accountsAllowed = accountsAllowed;
    }

    public boolean isDeletePnrAllowed() {
        return deletePnrAllowed;
    }

    /**
     * @param deletePnrAllowed the deletePnrAllowed to set
     */
    public void setDeletePnrAllowed(boolean deletePnrAllowed) {
        this.deletePnrAllowed = deletePnrAllowed;
    }

    /**
     * @return the deleteInvoiceAllowed
     */
    public boolean isDeleteInvoiceAllowed() {
        return deleteInvoiceAllowed;
    }

    /**
     * @param deleteInvoiceAllowed the deleteInvoiceAllowed to set
     */
    public void setDeleteInvoiceAllowed(boolean deleteInvoiceAllowed) {
        this.deleteInvoiceAllowed = deleteInvoiceAllowed;
    }

    /**
     * @return the tktCNoteAllowed
     */
    public boolean isTktCNoteAllowed() {
        return tktCNoteAllowed;
    }

    /**
     * @param tktCNoteAllowed the tktCNoteAllowed to set
     */
    public void setTktCNoteAllowed(boolean tktCNoteAllowed) {
        this.tktCNoteAllowed = tktCNoteAllowed;
    }

    /**
     * @return the cNoteAllowed
     */
    public boolean iscNoteAllowed() {
        return cNoteAllowed;
    }

    /**
     * @param cNoteAllowed the cNoteAllowed to set
     */
    public void setcNoteAllowed(boolean cNoteAllowed) {
        this.cNoteAllowed = cNoteAllowed;
    }

    /**
     * @return the voidtransactionAllowed
     */
    public boolean isVoidtransactionAllowed() {
        return voidtransactionAllowed;
    }

    /**
     * @param voidtransactionAllowed the voidtransactionAllowed to set
     */
    public void setVoidtransactionAllowed(boolean voidtransactionAllowed) {
        this.voidtransactionAllowed = voidtransactionAllowed;
    }

    /**
     * @return the manualTktRefundlAllowed
     */
    public boolean isManualTktRefundlAllowed() {
        return manualTktRefundlAllowed;
    }

    /**
     * @param manualTktRefundlAllowed the manualTktRefundlAllowed to set
     */
    public void setManualTktRefundlAllowed(boolean manualTktRefundlAllowed) {
        this.manualTktRefundlAllowed = manualTktRefundlAllowed;
    }

    /**
     * @return the manualTktVoidAllowed
     */
    public boolean isManualTktVoidAllowed() {
        return manualTktVoidAllowed;
    }

    /**
     * @param manualTktVoidAllowed the manualTktVoidAllowed to set
     */
    public void setManualTktVoidAllowed(boolean manualTktVoidAllowed) {
        this.manualTktVoidAllowed = manualTktVoidAllowed;
    }

    /**
     * @return the deleteTktAllowed
     */
    public boolean isDeleteTktAllowed() {
        return deleteTktAllowed;
    }

    /**
     * @param deleteTktAllowed the deleteTktAllowed to set
     */
    public void setDeleteTktAllowed(boolean deleteTktAllowed) {
        this.deleteTktAllowed = deleteTktAllowed;
    }

    /**
     * @return the deleteSegmentAllowed
     */
    public boolean isDeleteSegmentAllowed() {
        return deleteSegmentAllowed;
    }

    /**
     * @param deleteSegmentAllowed the deleteSegmentAllowed to set
     */
    public void setDeleteSegmentAllowed(boolean deleteSegmentAllowed) {
        this.deleteSegmentAllowed = deleteSegmentAllowed;
    }

    /**
     * @return the fareLineAllowed
     */
    public boolean isFareLineAllowed() {
        return fareLineAllowed;
    }

    /**
     * @param fareLineAllowed the fareLineAllowed to set
     */
    public void setFareLineAllowed(boolean fareLineAllowed) {
        this.fareLineAllowed = fareLineAllowed;
    }

    /**
     * @return the editTransactionAllowed
     */
    public boolean isEditTransactionAllowed() {
        return editTransactionAllowed;
    }

    /**
     * @param editTransactionAllowed the editTransactionAllowed to set
     */
    public void setEditTransactionAllowed(boolean editTransactionAllowed) {
        this.editTransactionAllowed = editTransactionAllowed;
    }

    /**
     * @return the debitInTransAllowed
     */
    public boolean isDebitInTransAllowed() {
        return debitInTransAllowed;
    }

    /**
     * @param debitInTransAllowed the debitInTransAllowed to set
     */
    public void setDebitInTransAllowed(boolean debitInTransAllowed) {
        this.debitInTransAllowed = debitInTransAllowed;
    }

    /**
     * @return the creditInTransAllowed
     */
    public boolean isCreditInTransAllowed() {
        return creditInTransAllowed;
    }

    /**
     * @param creditInTransAllowed the creditInTransAllowed to set
     */
    public void setCreditInTransAllowed(boolean creditInTransAllowed) {
        this.creditInTransAllowed = creditInTransAllowed;
    }


}
