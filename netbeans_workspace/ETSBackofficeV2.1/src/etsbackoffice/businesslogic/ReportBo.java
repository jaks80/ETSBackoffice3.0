package etsbackoffice.businesslogic;

import etsbackoffice.domain.AccountingDocument;
import etsbackoffice.domain.OfficeID;
import etsbackoffice.domain.Ticket;
import etsbackoffice.report.ReportDao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class ReportBo {

    private ReportDao reportDao;
    private List<Ticket> soldTickets = new ArrayList();
    private List<OfficeID> thirdPartyVandors = new ArrayList();
    private List<Object> objects = new ArrayList();
    
    public ReportBo() {
    }

    public void findthirdPartyVendors(){
        setThirdPartyVandors(reportDao.findthirdPartyVendors());
    }    

    public void setReportDao(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public List<Ticket> getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(List<Ticket> soldTickets) {
        this.soldTickets = soldTickets;
    }

    public List<OfficeID> getThirdPartyVandors() {
        return thirdPartyVandors;
    }

    public void setThirdPartyVandors(List<OfficeID> thirdPartyVandors) {
        this.thirdPartyVandors = thirdPartyVandors;
    }    

    public List<AccountingDocument> revenueSummeryReport(Date from, Date to,String careerId,
           int clientType, Long contactableId) {
       return reportDao.revenueSummeryReport(from, to, careerId, clientType, contactableId);
    } 
    
    public List<AccountingDocument> netTRevenueReport(Date from, Date to,String careerId,
           int clientType, Long contactableId) {
       return reportDao.netTRevenueReport(from, to, careerId, clientType, contactableId);
    } 
}
