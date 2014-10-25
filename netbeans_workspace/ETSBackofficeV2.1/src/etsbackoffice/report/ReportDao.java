package etsbackoffice.report;

import etsbackoffice.domain.*;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface ReportDao {    

    public List<OfficeID> findthirdPartyVendors();    

    public List<Object> saleReportValueOnly();

    public List<AccountingDocument> revenueSummeryReport(Date from, Date to,String careerId,
            int contactableType, Long contactableId);
    
    public List<AccountingDocument> netTRevenueReport(Date from, Date to,String careerId,
            int contactableType, Long contactableId);
}
