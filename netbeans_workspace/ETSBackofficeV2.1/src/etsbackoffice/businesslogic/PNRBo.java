package etsbackoffice.businesslogic;

import etsbackoffice.datalogic.PNRDao;
import etsbackoffice.datalogic.TicketDao;
import etsbackoffice.domain.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 *
 * @author Yusuf
 */
public class PNRBo {

    private PNR pnr;
    private PNRDao pnrDao;
    private TicketDao ticketDao;
    // private List uninvoicedPnr;

    public PNRBo() {
    }

    public void savePnr() {
        pnrDao.save(this.pnr);
    }

    public void deletePNR(){
     pnrDao.deletePnr(this.pnr);
    }

    public void removeService(Services s) {
        pnrDao.deleteService(s);
    }
        
    public void deleteTicket(Ticket ticket) {
        ticket.setPnr(null);
        ticketDao.save(ticket);
        ticketDao.deleteTicket(ticket);
    }

    public List<PNR> uninvoicedPnrs() {
        List uninvoicedPnr = new ArrayList();
        uninvoicedPnr = pnrDao.findUninvoicedPnr();
        return uninvoicedPnr;
    }

    public List<PNR> pnrsToday() {        
        return pnrDao.pnrsToday();
    }

    public List<PNR> bookedPnrs() {
        return pnrDao.bookedPnrs();
    }

    public List<PNR> searchByGDSPnr(String gdsPnr) {
        return pnrDao.searchByGdsPnr(gdsPnr);
    }

    public List<PNR> searchByTktNo(String tktNo) {
        return pnrDao.searchByTktNo(tktNo);
    }
 
    public List<PNR> searchByPaxName(String name) {
        String surName, foreName;

        if (name.contains("/")) {
            String[] namePart = name.split("/");
            surName = namePart[0];
            foreName = namePart[1];
        } else {
            surName = name;
            foreName = "";
        }
        return pnrDao.searchByPaxName(surName, foreName);
    }

    public PNR loadPNR(String gdsPnr, Date pnrCreationDate){
    return pnrDao.loadPNR(gdsPnr, pnrCreationDate);
    }

    public void loadCompletePNR(long pnrID) {
        this.pnr = pnrDao.findCompletePNR(pnrID);
    }

    public List<Object> loadPNR(String tktNo, String surName) {

        return pnrDao.loadPNR(tktNo, surName);
    }
    

    public List<Ticket> getTickets() {        
        if (this.pnr != null) {
            return new ArrayList(this.pnr.getTickets());
        } else {
            return null;
        }
    }

    public List<Services> getOtherServices() {
        List<Services> oServices = new ArrayList();        
            for (Services s : this.pnr.getServices()) {
                if (s.getServiceType() == 1) {
                    oServices.add(s);
                }                        
        }
        return oServices;
    }
        
    public void setTickets(List<Ticket> tickets) {
        this.pnr.setTickets(new HashSet(tickets));
    }

    public List<Itinerary> getSegments() {
        if (this.pnr != null) {
            return new ArrayList(this.pnr.getSegments());
        } else {
            return null;
        }
    }

    public void setSegments(List<Itinerary> segments) {
        this.pnr.setSegments(new HashSet(segments));
    }

    public List<AccountingDocument> getAccountingDocuments() {
        if (this.pnr != null) {
            return new ArrayList(this.pnr.getAccountingDocuments());
        } else {
            return null;
        }
    }

    public List<PurchaseAccountingDocument> getPAccountingDocuments() {
        if (this.pnr != null) {
            return new ArrayList(this.pnr.getPurchaseAccountingDocuments());
        } else {
            return null;
        }
    }

    public List<PNRRemark> loadRemarks(PNR pnr){
     return pnrDao.loadRemarks(pnr);
    }

    public PNRLog newLog(PNR p, String log) {
        DateFormat df = new DateFormat();        
        User user = AuthenticationBo.getLoggedOnUser();

        PNRLog pnrLog = new PNRLog();
        pnrLog.setPnr(p);
        pnrLog.setUser(user);
        pnrLog.setLogDetails(log);
        pnrLog.setInstance(df.getCurrentTimeStamp());
        return pnrLog;
    }

    public void saveRemark(PNRRemark remark){
    pnrDao.saveRemark(remark);
    }

    public void saveBulkRemark(List<PNRRemark> remarks){
     pnrDao.saveBulkRemark(remarks);
    }

    public void setAccountingDocuments(List<AccountingDocument> acDocs) {
        this.pnr.setAccountingDocuments(new HashSet(acDocs));
    }

    public void setPnrDao(PNRDao pnrDao) {
        this.pnrDao = pnrDao;
    }

    public void setPnr(PNR pnr) {
        this.pnr = pnr;
    }

    public PNR getPnr() {
        return pnr;
    }
    
    public void setTicketDao(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }
}
