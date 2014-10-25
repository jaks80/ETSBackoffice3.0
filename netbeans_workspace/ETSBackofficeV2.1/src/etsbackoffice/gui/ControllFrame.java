/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package etsbackoffice.gui;

import etsbackoffice.ETSBackofficeApp;
import etsbackoffice.domain.OAccountingDocument;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Yusuf
 */
public class ControllFrame {

    private JFrame mainFrame = ETSBackofficeApp.getApplication().getMainFrame();
    private FrameConsole frameConsole;
    private FrameAgents frameAgents;
    private FrameCustomers frameCustomers;
    private FrameUsers frameUsers;
    private FrameSAcDocHistory frameSAcDocHistory;
    private FrameSOAcDocHistory frameSOAcDocHistory;
    private FrameSOAcDocOutstanding frameSOAcDocOutstanding;
    private FramePAcDocHistory framePAcDocHistory;
    private FrameRevenueSummery frameAccounts;
    private FrameOutstandingSInvoice frameOutInv;
    private FrameOutstandingSCNote frameOutRfd;
    private FrameBatchTransaction frameBCollection;
    private FrameCollectionReport frameBCollectionRpt;
    private FrameCashBook frameCashBook;
    private FrameBPThirdParty frameBPThirdParty;
    private FrameBPThirdPartyRpt frameBPThirdPartyRpt;
    private FrameBPBsp frameBPBsp;
    private FrameOutstandingPInv3P frameOutPInv;
    private FrameOutstandingPCNote3P frameOutPCNote;
    private FrameSale frameSale;
    private FrameSaleRptWithRevenue frameSaleWithRev;
    private FrameItinerary frameItinerary;
    private FrameOInvMain frameOInvMain;
    private FrameOAcDoc frameOAcDoc;
    private FrameOServiceManager frameOServiceManager;
    private FrameAddServiceManager frameAddServiceManager;
    private FrameClientAccounts frameCAccounts;
    private FrameVendorAccounts frameVAccounts;

    public void frameConsole() {
        if (frameConsole == null) {
            try {
                frameConsole = new FrameConsole(mainFrame);
            } catch (IOException ex) {
                Logger.getLogger(ControllFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ETSBackofficeApp.getApplication().show(frameOutRfd);
    }

    public void userSummery() {
        if (frameUsers == null) {
            frameUsers = new FrameUsers(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameUsers);
    }

    public void agentSummery() {
        if (frameAgents == null) {
            frameAgents = new FrameAgents(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameAgents);
    }

    public void frameSAcDocHistory() {        
        frameSAcDocHistory = new FrameSAcDocHistory(mainFrame);        
        ETSBackofficeApp.getApplication().show(frameSAcDocHistory);      
    }

    public void frameSOAcDocHistory() {
        if (frameSOAcDocHistory == null) {
            frameSOAcDocHistory = new FrameSOAcDocHistory(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameSOAcDocHistory);
    }

    public void frameSOAcDocOutstanding() {
        if (frameSOAcDocOutstanding == null) {
            frameSOAcDocOutstanding = new FrameSOAcDocOutstanding(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameSOAcDocOutstanding);
    }

    public void framePAcDocHistory() {
        if (framePAcDocHistory == null) {
            framePAcDocHistory = new FramePAcDocHistory(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(framePAcDocHistory);
    }

    public void frameCashBook() {
        if (frameCashBook == null) {
            frameCashBook = new FrameCashBook(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameCashBook);
    }

    public void outInvoiceSummery() {       
        frameOutInv = new FrameOutstandingSInvoice(mainFrame);        
        ETSBackofficeApp.getApplication().show(frameOutInv);
    }

    public void outPInv() {
        if (frameOutPInv == null) {
            frameOutPInv = new FrameOutstandingPInv3P(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameOutPInv);
    }

    public void outPCNote() {
        if (frameOutPCNote == null) {
            frameOutPCNote = new FrameOutstandingPCNote3P(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameOutPCNote);
    }

    public void outRefundSummery() {
        if (frameOutRfd == null) {
            frameOutRfd = new FrameOutstandingSCNote(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameOutRfd);
    }

    public void framePCollection() {
        if (frameBCollection == null) {
            frameBCollection = new FrameBatchTransaction(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameBCollection);
    }

    public void framePCollectionRpt() {
        if (frameBCollectionRpt == null) {
            frameBCollectionRpt = new FrameCollectionReport(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameBCollectionRpt);
    }

    public void frameBPThirdParty() {
        if (frameBPThirdParty == null) {
            frameBPThirdParty = new FrameBPThirdParty(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameBPThirdParty);
    }

    public void frameBPThirdPartyRpt() {
        if (frameBPThirdPartyRpt == null) {
            frameBPThirdPartyRpt = new FrameBPThirdPartyRpt(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameBPThirdPartyRpt);
    }

    public void frameBPBsp() {
        if (frameBPBsp == null) {
            frameBPBsp = new FrameBPBsp(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameBPBsp);
    }

    public void frameSale() {
        if (frameSale == null) {
            frameSale = new FrameSale(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameSale);
    }

        public void frameSaleWithRev() {
        if (frameSaleWithRev == null) {
            frameSaleWithRev = new FrameSaleRptWithRevenue(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameSaleWithRev);
    }
        
    public void frameItinerary() {
        if (frameItinerary == null) {
            frameItinerary = new FrameItinerary(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameItinerary);
    }

    public void customerSummery() {
        if (frameCustomers == null) {
            frameCustomers = new FrameCustomers(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameCustomers);
    }

    public void frameAccounts() {
        if (frameAccounts == null) {
            frameAccounts = new FrameRevenueSummery(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameAccounts);
    }

    public void frameOInvoiceMain() {
        if (frameOInvMain == null) {
            frameOInvMain = new FrameOInvMain(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameOInvMain);
    }

    public void frameOAcDoc(OAccountingDocument oAcDoc) {
        
            frameOAcDoc = new FrameOAcDoc(mainFrame);
            frameOAcDoc.loadObjects(oAcDoc);
        
        ETSBackofficeApp.getApplication().show(frameOAcDoc);
    }    

    public void frameOServiceManager() {
        if (frameOServiceManager == null) {
            frameOServiceManager = new FrameOServiceManager(mainFrame);
            ETSBackofficeApp.getApplication().show(frameOServiceManager);
        } else {
            ETSBackofficeApp.getApplication().show(frameOServiceManager);
        }
    }

    public void frameAddServiceManager() {
        if (frameAddServiceManager == null) {
            frameAddServiceManager = new FrameAddServiceManager(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameAddServiceManager);
    }

    public void frameCAccounts() {
        if (frameCAccounts == null) {
            frameCAccounts = new FrameClientAccounts(mainFrame);
        }
        ETSBackofficeApp.getApplication().show(frameCAccounts);
    }

    public void frameVAccounts() {
        if (frameVAccounts == null) {
            frameVAccounts = new FrameVendorAccounts();
        }
        ETSBackofficeApp.getApplication().show(frameVAccounts);
    }
}
