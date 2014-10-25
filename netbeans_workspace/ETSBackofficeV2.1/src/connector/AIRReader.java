package connector;

import etsbackoffice.businesslogic.DateFormat;
import etsbackoffice.domain.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class AIRReader {

    private PNR pnr;
    private Career career;
    private Ticket ticket;    
    private Itinerary segment;
    private TicketRefundDetails tktRfdDetails;
    private File file;
    //Air details
    private Date airCreationDate;
    private long airNo;
    private int airSectionNo;
    private int airTotalNo;
    private String fileType;
    private Date tkOKDate = null;
    private Set<PNRRemark> remarks = new LinkedHashSet<PNRRemark>();
    
    DateFormat df = new DateFormat();
    SimpleDateFormat dfInput = new SimpleDateFormat("yyMMdd");
    SimpleDateFormat dfOutput = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dfInput1 = new SimpleDateFormat("ddMMMyy");

    private BigDecimal baseFare = new BigDecimal("0.00");
    private BigDecimal tax = new BigDecimal("0.00");
    private BigDecimal totalFare = new BigDecimal("0.00");
    private BigDecimal nettFareBase = new BigDecimal("0.00");
    private BigDecimal nettFareTax = new BigDecimal("0.00");
    private BigDecimal nettTotalFare = new BigDecimal("0.00");

    private BigDecimal sellingFareBase = new BigDecimal("0.00");
    private BigDecimal sellingFareTax = new BigDecimal("0.00");
    private BigDecimal sellingTotalFare = new BigDecimal("0.00");

    public AIRReader(File airfile) {
        this.file = airfile;
    }

    public PNR getPnrDetails() {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(this.file));
            String line = null;
            this.pnr = new PNR();

            while ((line = bf.readLine()) != null) {
                if (line.startsWith("AIR")) {
                    setAIRLine(line);
                } else if (line.startsWith("AMD")) {
                    setAMDLine(line);
                } else if (line.startsWith("MUC")) {
                    setMUCLine(line);
                } else if (line.startsWith("B-")) {
                    setBLine(line);
                } else if (line.startsWith("D-")) {
                    setDLine(line);
                } else if(line.startsWith("RM")){
                    setRMLine(line);
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        return this.pnr;
    }

    public Career getCareer() {
        try {
            String line = null;
            String mucLine = null;
            String carCode = null;
            //this.career = null;
            BufferedReader bf = new BufferedReader(new FileReader(file));
            while ((line = bf.readLine()) != null) {
                if (line.startsWith("A-")) {                    
                    if (line.length() > 3) {
                        this.career = new Career();
                        setALine(line);
                    }
                } else if (line.startsWith("MUC")) {
                    mucLine = line;
                }
            }
            bf.close();

            if (this.career == null) {
                this.career = new Career();
                String[] data = mucLine.split(";");
                carCode = data[data.length - 1].substring(0, 2);
                this.career.setCode(carCode);
            }
        } catch (IOException ex) {
            Logger.getLogger(AIRReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.career;
    }

    public Set<Ticket> getTicketDetails() {
        Set<Ticket> tickets = new LinkedHashSet();

        if (this.fileType.equals("ttp")) {
            tickets = getTicketsFromTTPFile();
        } else if (this.fileType.equals("inv")) {
            tickets = getTicketsFromInvFile();
        } else if (this.fileType.equals("trfp")) {
            tickets = getTicketsFromTRFPFile();
        } else if (this.fileType.equals("bt") || this.fileType.equals("et")) {
            tickets = getTicketsFromBTFile();
        } else if (this.fileType.equals("void")) {
            tickets = getTicketsFromVoidFile();
        }
        return tickets;
    }

    public Set<Ticket> getTicketsFromTTPFile() {
        String line = null;
        Set<Ticket> tickets = new LinkedHashSet();
        
        try {
            BufferedReader bf = new BufferedReader(new FileReader(this.file));

            while ((line = bf.readLine()) != null) {

                if (line.startsWith("K-")) {
                    setKLine(line);
                    System.out.println("BFK: "+this.baseFare);
                } else if (line.startsWith("KN-")) {
                    setKNLine(line);
                } else if (line.startsWith("KS-")) {
                    //setKSLine(line);
                }else if (line.startsWith("I-")) {
                    this.ticket = new Ticket();
                    setILine(line);                    
                } else if (line.startsWith("T-")) {
                    setTLine(line);                
                }else if (line.startsWith("FE")) {
                    setFELine(line);
                } else if (line.startsWith("FO")) {
                    setFOLine(line);                    
                } else if (line.startsWith("TK")) {
                    //setTKLine(line);
                    tickets.add(this.ticket);
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tickets;
    }

    public Set<Ticket> getTicketsFromVoidFile() {
        String line = null;
        Set<Ticket> tickets = new LinkedHashSet();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));

            while ((line = bf.readLine()) != null) {

                if (line.startsWith("I-")) {
                    this.ticket = new Ticket();
                    setILine(line);
                } else if (line.startsWith("T-")) {
                    setTLine(line);
                    tickets.add(ticket);
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tickets;
    }

    public Set<Ticket> getTicketsFromInvFile() {
        String line = null;
        Set<Ticket> tickets = new LinkedHashSet();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));

            while ((line = bf.readLine()) != null) {

                if (line.startsWith("K-")) {
                    setKLine(line);
                } else if (line.startsWith("KN-")) {
                    setKNLine(line);
                } else if (line.startsWith("KS-")) {
                    setKSLine(line);
                } else if (line.startsWith("I-")) {
                    this.ticket = new Ticket();
                    setILine(line);                    
                } else if (line.startsWith("T-")) {
                    setTLine(line);
                }else if (line.startsWith("FE")) {
                    setFELine(line);
                } else if (line.startsWith("FO")) {
                    setFOLine(line);
                } else if (line.startsWith("TK")) {
                    setTKLine(line);                    
                    tickets.add(ticket);
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tickets;
    }

    public Set<Ticket> getTicketsFromBTFile() {
        String line = null;
        Set<Ticket> tickets = new LinkedHashSet();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));

            while ((line = bf.readLine()) != null) {

                if (line.startsWith("I-")) {
                    this.ticket = new Ticket();
                    if (this.pnr.getTicketingAgtOID().isEmpty()) {
                        ticket.setTktStatus(1);
                    } else {
                        ticket.setTktStatus(2);
                    }
                    setILine(line);
                } else if (line.startsWith("K-")) {
                    setKLine(line);
                    ticket.setBaseFare(this.baseFare);
                    ticket.setTax(this.tax);                                    
                } else if (line.startsWith("KN-")) {
                    setKNLine(line);
                    ticket.setBaseFareVendor(this.nettFareBase);
                    ticket.setTaxVendor(this.nettFareTax);
                } else if (line.startsWith("KS-")) {
                    setKSLine(line);
                    if (this.baseFare.compareTo(new BigDecimal("0.00")) == 0) {
                        ticket.setBaseFare(this.sellingFareBase);
                        ticket.setTax(this.sellingFareTax);
                    }
                } else if (line.startsWith("T-")) {
                    setTLine(line);
                } else if (line.startsWith("FE")) {
                    setFELine(line);
                } else if (line.startsWith("FO")) {
                    setFOLine(line);
                } else if (line.startsWith("TK")) {
                    setTKLine(line);
                    //if(this.pnr.gett)
                    tickets.add(ticket);
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tickets;
    }

     public Set<Ticket> getTicketsFromTRFPFile() {
        String line = null;
        Set<Ticket> tickets = new LinkedHashSet();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));
            while ((line = bf.readLine()) != null) {
                if (line.startsWith("RFD")) {                    
                    tktRfdDetails = new TicketRefundDetails();
                    setRFDLine(line);                    
                } else if (line.startsWith("I-")) {
                    ticket = new Ticket();
                    setILine(line);
                } else if (line.startsWith("T-")) {
                    setTLine(line);
                } else if (line.startsWith("R-")) {
                    setRLine(line);
                    ticket.setTktStatus(4);
                    //tktRfdDetails.setTicket(ticket);
                    ticket.setTicketRefundDetails(tktRfdDetails);
                    tickets.add(ticket);
                } else if (line.startsWith("FO")) {
                    setFOLine(line);                    
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tickets;
    }

    public Set<Itinerary> getSegments() {
        String line = null;
        Set<Itinerary> segments = new LinkedHashSet();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(this.file));

            while ((line = bf.readLine()) != null) {
                if (line.startsWith("H-")) {
                    segment = new Itinerary();
                    setHLine(line);
                    if(!segment.getAirLineID().equals("VOID")){//Avoiding void segment to enter database
                    segments.add(segment);
                    }
                } else if (line.startsWith("U-") && this.fileType.equals("bt")) {
                    segment = new Itinerary();
                    setHLine(line);
                    segments.add(segment);
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return segments;
    }

    private void setAIRLine(String line) {
        String temp = "";
        String[] data = line.split(";");
        temp = data[0].substring(0, 3);
        setAirNo(Long.parseLong(data[4]));

        if (temp.equals("AIR")) {
            GDS gds = new GDS();
            gds.setGdsId(1);
            this.pnr.setGds(gds);
        }
        //setAirNo(data[4]);
    }

    private void setAMDLine(String line) {
        try {
            String[] data = line.split(";");
            this.setAirSectionNo(Integer.parseInt(data[1].substring(0, 1)));
            this.setAirTotalNo(Integer.parseInt(data[1].substring(2, 3)));
            if (data[2].substring(0, 4) == null ? "VOID" == null : data[2].substring(0, 4).equals("VOID")) {
                this.setFileType("void");
            }
        } catch (Exception e) {
            System.out.println("Exception from AIR/setAMDLine: " + e);
        }
    }

    private void setMUCLine(String line) {
        try {
            String[] data = line.split(";");

            try {
                this.pnr.setGdsPNR(data[0].substring(6, 12));
                String noOfPax = data[1].substring(0, 2).trim();
                if(!noOfPax.isEmpty()){
                this.pnr.setNoOfPassenger(Integer.parseInt(noOfPax));
                }
            } catch (Exception e) {
                 System.out.println("Exception from AIR/setMUCLine: " + e);
            }
            this.pnr.setBookingAgtOID(data[2].trim());
            this.pnr.setBookingAGTIATANo(data[3].trim());
          
            this.pnr.setTicketingAgtOID(data[8].trim());
            this.pnr.setTicketingAGTIATANo(data[9].trim());            
            
            int length = data.length;
            this.pnr.setVendorPNR(data[length - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setMUCLine: " + e);
        }
    }

    private void setALine(String line) {
        try {
            String[] data = line.split(";");
            String[] temp = data[0].split("-");
            this.career.setName(temp[1]); //Change this to id
            this.career.setCode(data[1].substring(0, 2));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception in method (setALine): " + e);
        }
    }

    private void setBLine(String line) {
        if (line.startsWith("B-INV")) {
            this.pnr.setTicketingAGTIATANo(null);
            this.pnr.setTicketingAgtOID(null);
            this.setFileType("inv");
        } else if (line.startsWith("B-TTP")) {
            this.setFileType("ttp");
        } else if (line.startsWith("B-TRFP")) {
            this.setFileType("trfp");
        } else if (line.startsWith("B-BT")) {
            this.setFileType("bt");
        } else if (line.startsWith("B-ET")) {
            this.setFileType("et");
        }
    }

    private void setDLine(String line) {
        try {
            String[] data = line.split(";");
            String[] temp = data[0].split("-");

            try {
                String pnrCreationDate = dfOutput.format(dfInput.parse(data[0].substring(2)));
                Date date = dfOutput.parse(pnrCreationDate);
                pnr.setPnrCreationDate(date);
                pnr.setServicingCareer(this.career);
                setAirCreationDate(dfOutput.parse(dfOutput.format(dfInput.parse(data[2]))));
            } catch (ParseException e) {
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setDLine: " + e);
        }
    }

    /*public void setHLine(String line) {
        try {
            String[] data = line.split(";");
            segment.setSegmentNo(data[1].substring(0, 3));
            String stopOver = data[1].substring(3, 4);
            if (stopOver.equals("O")) {
                segment.setStopOver(true);
            } else {
                segment.setStopOver(false);
            }
            segment.setDeptFrom(data[1].substring(4).trim());
            segment.setDeptTo(data[3].trim());
            String[] temp = data[5].split(" ");
            segment.setAirLineID(temp[0]);
            segment.setFlightNo(temp[4]);
            segment.setTicketClass(temp[5]);
            segment.setDeptDate(temp[7].substring(0, 5));
            segment.setDeptTime(temp[7].substring(5, 9));
            segment.setArvDate(temp[9]);
            segment.setArvTime(temp[8]);
            segment.setTktStatus(data[6].substring(0, 2));
            segment.setMealCode(data[8].trim());
            segment.setBaggage(data[13]);
            segment.setCheckInTerminal(data[14]);
            segment.setCheckInTime(data[15]);
            segment.setFlightDuration(data[17]);
            segment.setMileage(Integer.parseInt(!data[19].trim().equals("") ? data[19] : "0"));

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setHLine: " + e);
        }
    }*/
    
    public void setHLine(String line) {
        try {
            String[] data = line.split(";");
            segment.setSegmentNo(data[1].substring(0, 3));
            String stopOver = data[1].substring(3, 4);
            if (stopOver.equals("O")) {
                segment.setStopOver(true);
            } else {
                segment.setStopOver(false);
            }
            segment.setDeptFrom(data[1].substring(4).trim());
            segment.setDeptTo(data[3].trim());
            String[] temp = data[5].split(" ");
            segment.setAirLineID(temp[0]);
            if(temp.length >4){
             segment.setFlightNo(temp[4]);
            }
            if(temp.length >5){
            segment.setTicketClass(temp[5]);
            }
            if(temp.length >7){
            segment.setDeptDate(temp[7].substring(0, 5));
            }
            if(temp.length >7){
            segment.setDeptTime(temp[7].substring(5, 9));
            }
            if(temp.length >9){
            segment.setArvDate(temp[9]);
            }
            if(temp.length >8){
            segment.setArvTime(temp[8]);
            }
            if(data.length > 6 && data[6].length() > 2){
            segment.setTktStatus(data[6].substring(0, 2));
            }
            if(data.length > 8){
            segment.setMealCode(data[8].trim());
            }
            if(data.length > 13){
            segment.setBaggage(data[13]);
            }
            if(data.length > 14){
            segment.setCheckInTerminal(data[14]);
            }
            if(data.length > 15){
            segment.setCheckInTime(data[15]);
            }
            if(data.length > 17){
            segment.setFlightDuration(data[17]);
            }
            if(data.length > 19){
            segment.setMileage(Integer.parseInt(!data[19].trim().equals("") ? data[19] : "0"));
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setHLine: " + e);
        }
    }

    public void setKLine(String line) {
        float com = 0;
        try {
            if (line != null && line.length() > 3) {
                String[] data = line.split(";");
                String[] temp = (data[0].trim()).split("-");
                String t = data[12].replaceAll("[a-zA-Z]", "");
                String localCurrencyCode = data[12].replaceAll("[^A-Z]", "");
                String bfCurrencyCode = temp[1].replaceAll("[^A-Z]", "").substring(1);                

                if (bfCurrencyCode.equals(localCurrencyCode)) {
                    this.baseFare = new BigDecimal((temp[1].replaceAll("[a-zA-Z]", "").trim()));
                } else {
                    if (!data[1].isEmpty()) {
                        String temp1 = data[1].replaceAll("[a-zA-Z]", "").trim();
                        if(temp1 != null && !temp1.isEmpty()){
                         this.baseFare = new BigDecimal(data[1].replaceAll("[a-zA-Z]", "").trim());
                        }                        
                    }
                }
                this.totalFare = new BigDecimal(data[12].replaceAll("[a-zA-Z]", "").trim());
                this.tax = this.totalFare.subtract(this.baseFare);

                if (this.totalFare.compareTo(new BigDecimal("0.00")) == 0) {
                    this.tax = new BigDecimal("0.00");
                    this.baseFare = new BigDecimal("0.00");
                }                
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setKLine: " + e);
        }
    }

    public void setKNLine(String line) {
        try {
            if (line != null && line.length() > 3) {
                String[] data = line.split(";");
                String[] temp = (data[0].trim()).split("-");
                //String t = data[12].replaceAll("[a-zA-Z]", "");

                this.nettFareBase = new BigDecimal((temp[1].replaceAll("[a-zA-Z]", "").trim()));
                this.nettTotalFare = new BigDecimal(data[12].replaceAll("[a-zA-Z]", "").trim());
                this.nettFareTax = this.nettTotalFare.subtract(this.nettFareBase);

                if (this.nettFareTax.compareTo(BigDecimal.ONE) < 0) {
                    this.nettFareTax = new BigDecimal("0.00");
                }
            }            
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setKLine: " + e);
        }
    }

    public void setKSLine(String line) {
        try {
            if (line != null && line.length() > 3) {
                String[] data = line.split(";");
                String[] temp = (data[0].trim()).split("-");
                String t = data[12].replaceAll("[a-zA-Z]", "");

                this.sellingFareBase = new BigDecimal((temp[1].replaceAll("[a-zA-Z]", "").trim()));
                this.sellingTotalFare = new BigDecimal(data[12].replaceAll("[a-zA-Z]", "").trim());
                this.sellingFareTax = this.sellingTotalFare.subtract(this.sellingFareBase);

                if (this.tax.compareTo(BigDecimal.ONE) < 0) {
                    this.tax = new BigDecimal("0.00");
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setKLine: " + e);
        }
    }

    public void setILine(String line) {
        try {
            if (line != null) {
                String[] data = line.split(";");
                String[] name = data[1].substring(2).trim().split("/");
                
                ticket.setDocIssuedate(airCreationDate);
                ticket.setPassengerNo(Integer.valueOf(data[1].substring(0, 2)));
                ticket.setPaxSurName(name[0].trim());
                ticket.setPaxForeName(name[1].trim());                
                ticket.setBaseFare(this.baseFare);
                ticket.setTax(this.tax);

                if(this.fileType.equals("ttp")){//Overriding fare with consolidate fare line
                 if(this.baseFare.compareTo(new BigDecimal("0.00"))==0){
                   ticket.setBaseFare(this.nettFareBase);
                   ticket.setTax(this.nettFareTax);
                   ticket.setGrossFare(this.sellingFareBase.add(this.sellingFareTax));
                 }
                }else if(this.fileType.equals("inv")){
                 if(this.baseFare.compareTo(new BigDecimal("0.00"))==0){
                   ticket.setBaseFare(this.sellingFareBase);
                   ticket.setTax(this.sellingFareTax);
                 }
                 ticket.setBaseFareVendor(this.nettFareBase);
                 ticket.setTaxVendor(this.nettFareTax);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setILine: " + e);
        }
    }

    public void setTLine(String line) {
        try {
            if (line != null && line.length() > 4) {
                String[] data = line.split("-");
                ticket.setNumericAirLineCode(data[1].substring(1, 4).trim());
                ticket.setTicketNo(data[2]);
                ticket.setTktStatus(2);
            }

            if (ticket.getTicketNo() == null) {
                ticket.setTktStatus(1);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setTLine: " + e);
        }
    }  

    public void setRLine(String line) {
        try {
            String[] data = line.split(";");
            String d = dfOutput.format(dfInput1.parse(data[1]));
            Date date = dfOutput.parse(d);
            ticket.setDocIssuedate(date);
        } catch (ParseException e) {
            System.out.println("Exception from AIR/setRLine: " + e);
        }
    }

    public void setRFDLine(String line) {
        try {
            String[] data = line.split(";");
            String temp;
            temp = data[3].replaceAll("[a-zA-Z]", "");
            tktRfdDetails.setFarePaid(new BigDecimal(temp));
            if (data[4] == null ? "" != null : !data[4].equals("")) {
                tktRfdDetails.setFareUsed(new BigDecimal(data[4]));
            }
            if (data[5] == null ? "" != null : !data[5].equals("")) {
                this.baseFare = new BigDecimal(data[5]).negate();
            }
            if (data[8] == null ? "" != null : !data[8].equals("")) {
                tktRfdDetails.setCancellationFee(new BigDecimal(data[8]));
            }
            if (data[9] == null ? "" != null : !data[9].equals("")) {
                tktRfdDetails.setCfCom(new BigDecimal(data[9]).negate());
            }
            if (data[10] == null ? "" != null : !data[10].equals("")) {
                tktRfdDetails.setCMiscFee(new BigDecimal(data[10]));
            }
            if (data[11] == null ? "" != null : !data[11].equals("")) {
                this.tax = new BigDecimal(data[11].substring(2)).negate();
            }
            if (data[12] == null ? "" != null : !data[12].equals("")) {
                this.totalFare = new BigDecimal(data[12]).negate();
            }
            
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setRFDLine: " + e);
        }
    }

    public void setFOLine(String line) {

        String airLineCode = "";
        String tktNo = "";
        try {
            airLineCode = line.substring(2, 5);
            if (line.charAt(5) == '-') {
                tktNo = line.substring(6, 16);
            } else {
                tktNo = line.substring(5, 15);
            }
            if(this.fileType.equals("ttp")){
            this.ticket.setTktStatus(3);
            }
            this.ticket.setOrginalTicketNo(tktNo);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setFOLine: " + e);
        }
    }

    public void setTKLine(String line) {
        try {
            String[] data = line.split("/");
            this.tkOKDate = df.getThirdPartyIssueDate(data[0].substring(4));
            if (this.ticket.getTktStatus()==2 || this.ticket.getTktStatus()==3) {
                this.pnr.setTicketingAgtOID(data[1].substring(0, 9));
                ticket.setDocIssuedate(tkOKDate);
            } else {
                ticket.setDocIssuedate(this.pnr.getPnrCreationDate());
                if (this.pnr.getTicketingAgtOID()!=null) {
                    this.pnr.setTicketingAgtOID(data[1].substring(0, 9));
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception from AIR/setTKLine: " + e);
        }
    }

    public void setFELine(String line) {
        String restrictions = line.substring(2);
        ticket.setRestrictions(restrictions);
    }

    public void setRMLine(String line) {        
        PNRRemark remark = new PNRRemark();
        remark.setText(line.substring(3));
        remark.setDateTime(null);
        this.getRemarks().add(remark);         
    }
    
    public Date getAirCreationDate() {
        return airCreationDate;
    }

    public void setAirCreationDate(Date airCreationDate) {
        this.airCreationDate = airCreationDate;
    }

    public long getAirNo() {
        return airNo;
    }

    public void setAirNo(long airNo) {
        this.airNo = airNo;
    }

    public int getAirSectionNo() {
        return airSectionNo;
    }

    public void setAirSectionNo(int airSectionNo) {
        this.airSectionNo = airSectionNo;
    }

    public int getAirTotalNo() {
        return airTotalNo;
    }

    public void setAirTotalNo(int airTotalNo) {
        this.airTotalNo = airTotalNo;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Set<PNRRemark> getRemarks() {
        return remarks;
    }
}
