package connector;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.businesslogic.*;
import etsbackoffice.datalogic.ItineraryDao;
import etsbackoffice.datalogic.TicketDao;
import etsbackoffice.domain.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Yusuf
 */
public class FileHandler {

    File file;
    private PNRBo pnrBo = (PNRBo) ETSBackofficeApp.getApplication().ctx.getBean("pnrBo");
    private AgentBo agentBo = (AgentBo) ETSBackofficeApp.getApplication().ctx.getBean("agentBo");
    private CareerBo careerBo = (CareerBo) ETSBackofficeApp.getApplication().ctx.getBean("careerBo");
    private ItineraryDao itineraryDao = (ItineraryDao) ETSBackofficeApp.getApplication().ctx.getBean("itineraryDao");
    private TicketDao ticketDao = (TicketDao) ETSBackofficeApp.getApplication().ctx.getBean("ticketDao");
    private AccountsBo accountsBo = (AccountsBo) ETSBackofficeApp.getApplication().ctx.getBean("accountsBo");
    private Career careerInDb;
    private PNR pnrInDB;
    private PNR newPnr;
    private Career newCareer;
    private AIRReader airReader;
    private Set<PNRRemark> newRemarks = new LinkedHashSet<PNRRemark>();
    private Set<Ticket> newTickets = new LinkedHashSet<Ticket>();
    private Set<Itinerary> newSegments = new LinkedHashSet<Itinerary>();
    private Set<PurchaseAccountingDocument> newPurchaseAcDocs = new LinkedHashSet<PurchaseAccountingDocument>();

    public FileHandler(File file) {
        this.file = file;
    }

    public void start() {


        airReader = new AIRReader(file);
        
        newPnr = new PNR();
        newRemarks = new LinkedHashSet();
        newTickets = new LinkedHashSet();
        newSegments = new LinkedHashSet();
        newPurchaseAcDocs = new LinkedHashSet();
        newCareer = new Career();
        newCareer = airReader.getCareer();
        newPnr = airReader.getPnrDetails();
        newRemarks = airReader.getRemarks();
        newSegments = airReader.getSegments();//Collecting segments

        airReader = new AIRReader(file);
        newPnr = airReader.getPnrDetails();
        newTickets.addAll(airReader.getTicketDetails());         
        
        newPnr.setRemarks(initPNRInRemark(newPnr, newRemarks));
        //------------------------------------------------------------------

        //Loading pnr from db per criteria----------------------------------
        if (!airReader.getFileType().equals("trfp")) {
            this.pnrInDB = pnrBo.loadPNR(newPnr.getGdsPNR(), newPnr.getPnrCreationDate());
            if (newCareer.getCode() != null) {
                insertCareer(newCareer);
            }
        } else if (airReader.getFileType().equals("trfp")) {
            
            for (Ticket t : newTickets) {
                try {
                    if (ticketDao.isExistInDb(t.getTicketNo(), t.getPaxSurName(), t.getTktStatus())) {
                        newTickets.remove(t);
                    }
                } catch (Exception e) {
                    System.out.println("Exception in checking: " + e);
                }
            }
            if(!newTickets.isEmpty()){
            
                   
            Ticket tkt = newTickets.iterator().next();
            List<Object> rfdElement = new ArrayList();
            if (tkt.getOrginalTicketNo() != null) {                
                rfdElement = pnrBo.loadPNR(tkt.getOrginalTicketNo(), tkt.getPaxSurName());
            } else {
                rfdElement = pnrBo.loadPNR(tkt.getTicketNo(), tkt.getPaxSurName());
            }

            Ticket oldTicket = null;
            Set<Itinerary> oldSegments = new LinkedHashSet<Itinerary>();
            Set<Ticket> oldTickets = new LinkedHashSet<Ticket>();
            for (int j = 0; j < rfdElement.size(); j++) {
                Object[] objects = (Object[]) rfdElement.get(j);
                oldTicket = (Ticket) objects[0];
                pnrInDB = (PNR) objects[1];
                careerInDb = pnrInDB.getServicingCareer();
                Itinerary seg = (Itinerary) objects[2];
                newPurchaseAcDocs.clear();
                newPurchaseAcDocs.add((PurchaseAccountingDocument) objects[3]);
                pnrInDB.setPurchaseAccountingDocuments(newPurchaseAcDocs);
                oldSegments.add(seg);
                oldTickets.add(oldTicket);
            }
            if (pnrInDB != null) {
                pnrInDB.setTickets(oldTickets);
                newSegments = oldSegments;
                pnrInDB.setSegments(oldSegments);
            }
        }
        }
        //------------------------------------------------------------------            

        setBspCom(newTickets, this.careerInDb);


        if (this.pnrInDB == null
                && !airReader.getFileType().equals("void")
                && !airReader.getFileType().equals("trfp")) {//Inserting new pnr
            intiItineraryInTickets(newSegments, newTickets);
            newPnr.setServicingCareer(careerBo.getCareer());
            newPnr.setSegments(initPnrInSegments(newPnr, newSegments));//Init OneToMany
            newPnr.setTickets(initPnrInTickets(newPnr, newTickets));//Init OneToMany               

            newPnr = orderAscTicket(newPnr);//Order ticket by paxNo
            newPnr.setTicketingAgt(agentBo.searchAgent(newPnr.getTicketingAgtOID()));
            newPurchaseAcDocs.add(createNewPurchaseAcDoc(newPnr));
            newPnr.setPurchaseAccountingDocuments(newPurchaseAcDocs);
            // newPnr.addLog(pnrBo.newLog(newPnr, "PNR Inserted from GDS"));
            savePnr(newPnr);
        } else if (this.pnrInDB != null) {//Iserting Issue,Reissue and Refund                

            if (pnrInDB.getTicketingAgtOID() == null) {//Update PNR if Booking to Issue
                itineraryDao.removeItinerary(pnrInDB.getSegments());
                pnrInDB = updatePnr(this.pnrInDB, newPnr);
                pnrInDB.setTicketingAgt(agentBo.searchAgent(pnrInDB.getTicketingAgtOID()));
            } else {
                newSegments = updateSegments(this.pnrInDB.getSegments(), newSegments);
            }

            intiItineraryInTickets(newSegments, newTickets);
            pnrInDB.setSegments(initPnrInSegments(pnrInDB, newSegments));
            newTickets = initPnrInTickets(pnrInDB, newTickets);
            pnrInDB.setTickets(updateTickets(this.pnrInDB.getTickets(), newTickets));
            PurchaseAccountingDocument pAcDoc = createNewPurchaseAcDoc(pnrInDB);
            //if(pAcDoc!=null ||pnrInDB.getTicketingAgtOID()==null){
            //newPurchaseAcDocs.add(pAcDoc);
            pnrInDB.addPurchaseAcDoc(pAcDoc);
            savePnr(pnrInDB);
            //}

            //--------------------------------008801712242888------------------------------

        } else if (airReader.getFileType().equals("void")) {
            for (Ticket t : newTickets) {
                ticketDao.voidTicket(newPnr.getGdsPNR(), t.getTicketNo(), t.getPaxSurName());

            }
        }
    }

    private File[] checkAirDirectory() {
        File[] airFiles = new File("C://AIR").listFiles();

        return airFiles;
    }

    private void backupAir(File airFile) {
        File dir = new File("C://BKUP_AIR");
        if (!dir.exists()) {
            dir.mkdir();
        }
        airFile.renameTo(new File("C://BKUP_AIR", airReader.getAirNo() + ".txt"));
    }

    private void insertCareer(Career newCareer) {

        careerBo.find(newCareer.getCode());
        careerInDb = careerBo.getCareer();

        if (careerInDb == null) {
            this.careerBo.setCareer(newCareer);
            this.careerBo.store();
        } else if (careerInDb != null) {
            if (careerInDb.getName() == null) {
                this.careerBo.setCareer(newCareer);
                this.careerBo.store();
            }
        }
    }

    private void setBspCom(Set<Ticket> tickets, Career career) {
        if (career != null) {
            for (Ticket t : tickets) {
                if (t.getTktStatus() != 1 && t.getTktStatus() != 4) {
                    if (career.getBspComFixed().compareTo(new BigDecimal("0.00")) > 0 && t.getBaseFare().compareTo(new BigDecimal("0.00")) != 0) {
                        t.setBspCom(career.getBspComFixed().negate());
                    } else {
                        t.setBspCom(career.getBspComPercentage().multiply(t.getBaseFare()).divide(new BigDecimal("100.00")).negate());
                    }
                }
            }
        }
    }

    private void intiItineraryInTickets(Set<Itinerary> segments, Set<Ticket> tickets) {

        for (Ticket ticket : tickets) {
            ticket.setSegments(segments);
        }
    }

    private Set<PNRRemark> initPNRInRemark(PNR pnr, Set<PNRRemark> remarks) {

        for (PNRRemark rm : remarks) {
            rm.setPnr(pnr);
        }
        return remarks;
    }

    private Set<Ticket> initPnrInTickets(PNR pnr, Set<Ticket> tickets) {
        Set<Ticket> tempTickets = new LinkedHashSet();
        for (Ticket ticket : tickets) {
            ticket.setPnr(pnr);
            tempTickets.add(ticket);
        }
        return tempTickets;
    }

    private Set<Itinerary> initPnrInSegments(PNR pnr, Set<Itinerary> segments) {
        for (Itinerary segment : segments) {
            segment.setPnr(pnr);
        }
        return segments;
    }

    private PNR orderAscTicket(PNR pnr) {
        Set<Ticket> sortedTickets = new LinkedHashSet<Ticket>();
        List paxNoList = new ArrayList();

        for (Ticket t : pnr.getTickets()) {
            paxNoList.add(t.getPassengerNo());
        }
        Collections.sort(paxNoList);
        Iterator it = paxNoList.iterator();

        while (it.hasNext()) {
            int paxNo = (Integer) it.next();
            for (Ticket t : pnr.getTickets()) {
                if (paxNo == t.getPassengerNo()) {
                    sortedTickets.add(t);
                }
            }
        }
        pnr.setTickets(sortedTickets);
        return pnr;
    }

    private void savePnr(PNR pnr) {
        pnrBo.setPnr(pnr);
        pnrBo.savePnr();
    }

    private PNR updatePnr(PNR oldPnr, PNR newPnr) {
        oldPnr.setNoOfPassenger(newPnr.getNoOfPassenger());
        oldPnr.setBookingAgtOID(newPnr.getBookingAgtOID());
        oldPnr.setBookingAGTIATANo(newPnr.getBookingAGTIATANo());
        oldPnr.setTicketingAgtOID(newPnr.getTicketingAgtOID());
        oldPnr.setTicketingAGTIATANo(newPnr.getTicketingAGTIATANo());
        oldPnr.setPnrCreationDate(newPnr.getPnrCreationDate());
        oldPnr.setVendorPNR(newPnr.getVendorPNR());
        oldPnr.setRemarks(initPNRInRemark(oldPnr, newPnr.getRemarks()));
        oldPnr.setGds(newPnr.getGds());
        return oldPnr;
    }

    private Set<Ticket> updateTickets(Set<Ticket> oldTickets, Set<Ticket> newTickets) {

        for (Ticket newTkt : newTickets) {
            boolean exist = false;

            for (Ticket t : oldTickets) {
                if ((newTkt.getTicketNo() == null ? t.getTicketNo() == null : newTkt.getTicketNo().equals(t.getTicketNo()))
                        && newTkt.getTktStatus() == t.getTktStatus()
                        && t.getPaxSurName().equals(newTkt.getPaxSurName())
                        && t.getForeNameNoSuffix().equals(newTkt.getForeNameNoSuffix())) {
                    exist = true;

                } else if ((t.getTktStatus() == 1 || t.getTktStatus() == 5)
                        && t.getPaxSurName().equals(newTkt.getPaxSurName())
                        && t.getForeNameNoSuffix().equals(newTkt.getForeNameNoSuffix())) {

                    t.setPaxForeName(newTkt.getPaxForeName());
                    t.setTktStatus(newTkt.getTktStatus());
                    t.setNumericAirLineCode(newTkt.getNumericAirLineCode());
                    t.setTicketNo(newTkt.getTicketNo());
                    t.setBaseFare(newTkt.getBaseFare());
                    t.setTax(newTkt.getTax());
                    t.setBspCom(newTkt.getBspCom());

                    t.setSegments(newTkt.getSegments());
                    t.setDocIssuedate(newTkt.getDocIssuedate());
                    exist = true;
                }
            }
            if (!exist) {
                oldTickets.add(newTkt);
            }
        }
        return oldTickets;
    }

    private Set<Itinerary> updateSegments(Set<Itinerary> oldSegs, Set<Itinerary> newSegs) {
        Set<Itinerary> finalSegments = new LinkedHashSet();
        Itinerary tempOSeg = new Itinerary();

        for (Itinerary newSeg : newSegs) {
            boolean exist = false;
            loop:
            for (Itinerary oldSeg : oldSegs) {
                tempOSeg = new Itinerary();
                tempOSeg = oldSeg;
                if (oldSeg.getSegmentNo().equals(newSeg.getSegmentNo())) {
                    exist = true;
                    break loop;
                }
            }
            if (exist) {
                finalSegments.add(tempOSeg);
            } else {
                finalSegments.add(newSeg);
            }
        }
        return finalSegments;
    }

    private PurchaseAccountingDocument createNewPurchaseAcDoc(PNR pnr) {

        PurchaseAccountingDocument newPAcDoc = new PurchaseAccountingDocument();
        PurchaseAccountingDocument pInvoice = new PurchaseAccountingDocument();
        PurchaseAccountingDocumentLine purchaseAcDocLine = new PurchaseAccountingDocumentLine();
        BigDecimal totalPurchaseCost = new BigDecimal("0.00");

        int noInvoice = 0;
        if (pnr.getPurchaseAccountingDocuments().size() > 0) {
            for (PurchaseAccountingDocument pAcDoc : pnr.getPurchaseAccountingDocuments()) {
                if (pAcDoc.getAcDoctype() == 1) {
                    noInvoice++;
                }
            }
        }

        String acDocRef = pnr.getGdsPNR().concat(String.valueOf(pnr.getPnrId()).concat(String.valueOf(++noInvoice)));
        //Create new document
        newPAcDoc.setAcDoctype(1);
        newPAcDoc.setRecipientRef(acDocRef);
        newPAcDoc.setIssueDate(this.airReader.getAirCreationDate());
        newPAcDoc.setPnr(pnr);

        for (Ticket t : pnr.getTickets()) {
            if (t.getTktStatus() != 1 && t.getPurchaseAccountingDocumentLine().isEmpty()) {
                totalPurchaseCost = totalPurchaseCost.add(t.getNetBillable());
                if (this.airReader.getFileType().equals("inv")) {
                    newPAcDoc.setIssueDate(t.getDocIssuedate());
                }
                if (t.getTktStatus() == 4) {
                    pInvoice = pnr.getPurchaseAccountingDocuments().iterator().next();
                    newPAcDoc.setAcDoctype(2);
                    newPAcDoc.setRecipientRef(pnr.getPurchaseAccountingDocuments().iterator().next().getRecipientRef());
                    newPAcDoc.setPurchaseAccountingDocument(pInvoice);//Linking cnote with invocie
                }
                if (!t.getAccountingDocumentLine().isEmpty()) {
                    newPAcDoc.addaccountingDocument(t.getAccountingDocumentLine().iterator().next().getAccountingDocument());
                }
                //t.addPAcDocLine(purchaseAcDocLine);
                purchaseAcDocLine.addTicket(t);
            }
        }
        purchaseAcDocLine.setType(1);
        purchaseAcDocLine.setPurchaseAccountingDocument(newPAcDoc);
        newPAcDoc.getPurchaseAcDocLines().add(purchaseAcDocLine);

        newPAcDoc.addAcStatement(accountsBo.newAccountsTransactionFromPAcDoc(newPAcDoc));
        if (newPAcDoc.getTickets().size() > 0) {
            return newPAcDoc;
        } else {
            return null;
        }
    }
}
