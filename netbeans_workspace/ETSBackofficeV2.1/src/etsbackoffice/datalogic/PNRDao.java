package etsbackoffice.datalogic;

import etsbackoffice.domain.*;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public interface PNRDao {

    public void save(PNR pnr);

    public void deletePnr(PNR pnr);   

    public void deleteService(Services s);
    
    public PNR loadPNR(String gdsPnr, Date pnrCreationDate);

    public List<Object> loadPNR(String tktNo, String surName);

    public PNR findCompletePNR(long pnrID);

    public List<PNR> findUninvoicedPnr();

    public List<PNR> pnrsToday();

    public List<PNR> bookedPnrs();

    public List<PNR> searchByGdsPnr(String gdsPnr);    
    
    public List<PNR> searchByTktNo(String tktNo); 

    public List<PNR> searchByPaxName(String surName, String foreName);

    public List<PNRRemark> loadRemarks(PNR pnr);

    public void saveRemark(PNRRemark remark);

    public void saveBulkRemark(List<PNRRemark> remark);
    
    //public PNR loadPnrSubClass(PNR pnr);
}
