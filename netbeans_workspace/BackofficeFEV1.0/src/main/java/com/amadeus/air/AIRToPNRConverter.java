package com.amadeus.air;

import com.ets.model.pnr.Itinerary;
import com.ets.model.pnr.Pnr;
import com.ets.model.pnr.Ticket;
import com.amadeus.util.DateUtil;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class AIRToPNRConverter {

    private final AIR air;

    public AIRToPNRConverter(AIR air) {
        this.air = air;
    }

    public Pnr airToPNR() throws ParseException {
        Pnr pnr = new Pnr();

        for (String s : air.getLines()) {
            if (s.startsWith("MUC")) {
                String[] vals = AIRLineParser.parseMUCLine(s);
                pnr.setGdsPNR(vals[0].substring(0, 6));
                pnr.setNoOfPax(Integer.valueOf(vals[1].substring(0, 2)));
                pnr.setBookingAgtOid(vals[2]);
                pnr.setTktingAgtOid(vals[8]);
                pnr.setVendorPNR(vals[vals.length - 1]);
                break;
            }
        }

        for (String s : air.getLines()) {
            if (s.startsWith("D-")) {
                String[] vals = AIRLineParser.parseDLine(s);
                pnr.setPnrCreationDate(DateUtil.yyMMddToDate(vals[0]));
                pnr.setAirCreationDate(DateUtil.yyMMddToDate(vals[2]));
                break;
            }
        }

        return pnr;
    }

    public List<Itinerary> airToItinerary() {

        List<Itinerary> segments = new ArrayList<>();
        List<String[]> _Hs = new ArrayList<>();

        for (String s : air.getLines()) {
            if (s.startsWith("H-") || s.startsWith("U-")) {
                _Hs.add(AIRLineParser.parseHLine(s));
            } else if (s.startsWith("K-")) {
                break;//Finished H line reading, so break loop
            }
        }

        for (String[] _H : _Hs) {
            Itinerary segment = lineToIninerary(_H);
            segments.add(segment);
        }

        return segments;
    }

    public List<Ticket> airToTicket() throws ParseException {
        List<Ticket> tickets = new ArrayList<>();

        String localCurrencyCode = null;
        String bfCurrencyCode = null;
        BigDecimal baseFare = new BigDecimal("0.00");
        BigDecimal totalFare = new BigDecimal("0.00");
        BigDecimal tax = new BigDecimal("0.00");
        Ticket ticket = null;

        for (String s : air.getLines()) {

            if (s.startsWith("K-") && s.length() > 4) {
                String[] data = AIRLineParser.parseKLine(s);

                localCurrencyCode = data[12].replaceAll("[^A-Z]", "");
                bfCurrencyCode = data[0].replaceAll("[^A-Z]", "").substring(1);

                totalFare = new BigDecimal(data[12].replaceAll("[a-zA-Z]", "").trim());
                if (totalFare.compareTo(BigDecimal.ONE) > 0) {
                    baseFare = new BigDecimal((data[0].replaceAll("[a-zA-Z]", "").trim()));
                }

                tax = totalFare.subtract(baseFare);

                if (tax.compareTo(BigDecimal.ONE) < 0) {
                    tax = new BigDecimal("0.00");
                }
            } else if (s.startsWith("I-")) {
                String[] data = AIRLineParser.parseILine(s);
                String[] name = data[1].substring(2).trim().split("/");

                ticket = new Ticket();
                ticket = getNameFormStringArray(data, ticket);
                ticket.setBaseFare(baseFare);
                ticket.setTax(tax);
                ticket.setTotalFare(totalFare);
                ticket.setCurrencyCode(bfCurrencyCode);
                tickets.add(ticket);
            } else if (s.startsWith("T-") && s.length() > 4) {
                String[] data = AIRLineParser.parseTLine(s);
                if (data[0] != null && data[0].length() > 3) {
                    ticket.setNumericAirLineCode(data[0].substring(1, 4).trim());
                }
                ticket.setTicketNo(data[1]);
                ticket.setTktStatus(2);
            } else if (s.startsWith("FE")) {
                String restrictions = s.substring(2);
                ticket.setRestrictions(restrictions);
            } else if (s.startsWith("FO") && s.length() > 4) {
                String[] data = AIRLineParser.parseFOLine(s);

                if (s.charAt(5) == '-') {
                    ticket.setOrginalTicketNo(s.substring(6, 16));
                } else {
                    ticket.setOrginalTicketNo(s.substring(5, 15));
                }
                if (air.getType().equals("TTP")) {
                    ticket.setTktStatus(3);
                }

            } else if (s.startsWith("TK")) {
                String[] data = AIRLineParser.parseTKLine(s);

                Date tkOKDate = DateUtil.ddmmToDate(data[0].substring(2));
                if (ticket.getTktStatus() == 2 || ticket.getTktStatus() == 3) {
                    if ("INV".equals(air.getType())) {
                        ticket.setDocIssuedate(tkOKDate);
                    } else {
                        ticket.setDocIssuedate(DateUtil.yyMMddToDate(air.getCreationDate()));
                    }
                }
            }
        }

        return tickets;
    }

    public List<Ticket> airToRefundedTicket() throws ParseException {
        List<Ticket> tickets = new ArrayList<>();

        BigDecimal baseFare = new BigDecimal("0.00");
        BigDecimal totalFare = new BigDecimal("0.00");
        BigDecimal tax = new BigDecimal("0.00");
        BigDecimal fee = new BigDecimal("0.00");
        Ticket ticket = null;

        for (String s : air.getLines()) {
            if (s.startsWith("RFD")) {
                String[] data = AIRLineParser.parseRFDLine(s);
                if (data[5] == null ? "" != null : !data[5].equals("")) {
                    baseFare = new BigDecimal(data[5]).negate();
                }

                if (data[8] == null ? "" != null : !data[8].equals("")) {
                    fee = new BigDecimal(data[8]);
                }

                if (data[11] == null ? "" != null : !data[11].equals("")) {
                    tax = new BigDecimal(data[11].substring(2)).negate();
                }

                if (data[12] == null ? "" != null : !data[12].equals("")) {
                    totalFare = new BigDecimal(data[12]).negate();
                }
            } else if (s.startsWith("I-")) {
                String[] data = AIRLineParser.parseILine(s);
                String[] name = data[1].substring(2).trim().split("/");

                ticket = new Ticket();
                ticket = getNameFormStringArray(data, ticket);                
                ticket.setBaseFare(baseFare);
                ticket.setTax(tax);
                ticket.setFee(fee);
                ticket.setTotalFare(totalFare);
                tickets.add(ticket);
            } else if (s.startsWith("T-") && s.length() > 4) {
                String[] data = AIRLineParser.parseTLine(s);
                if (data[0] != null && data[0].length() > 3) {
                    ticket.setNumericAirLineCode(data[0].substring(1, 4).trim());
                }
                ticket.setTicketNo(data[1]);
                ticket.setTktStatus(4);
            } else if (s.startsWith("R-")) {
                String[] data = AIRLineParser.parseRLine(s);
                ticket.setDocIssuedate(DateUtil.refundDate(data[1]));
            }
        }

        return tickets;
    }


    private Ticket getNameFormStringArray(String[] data, Ticket ticket) {
        String[] name = data[1].substring(2).trim().split("/");

        ticket.setPassengerNo(data[1].substring(0, 2));
        ticket.setPaxSurName(name[0].trim());
        ticket.setPaxForeName(name[1].trim());
        return ticket;
    }

    public List<Ticket> airToVoidTicket() throws ParseException {
        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket = null;

        for (String s : air.getLines()) {
            if (s.startsWith("I-")) {
                String[] data = AIRLineParser.parseILine(s);
                ticket = new Ticket();
                ticket = getNameFormStringArray(data, ticket);
                tickets.add(ticket);
            }else if (s.startsWith("T-") && s.length() > 4) {
                String[] data = AIRLineParser.parseTLine(s);
                if (data[0] != null && data[0].length() > 3) {
                    ticket.setNumericAirLineCode(data[0].substring(1, 4).trim());
                }
                ticket.setTicketNo(data[1]);
                ticket.setTktStatus(5);
            }
        }
        return tickets;
    }

    private Itinerary lineToIninerary(String[] _H) {

        Itinerary segment = new Itinerary();

        segment.setSegmentNo(_H[1].substring(0, 3));
        segment.setStopOver(_H[1].substring(3, 4));
        segment.setDeptFrom(_H[1].substring(4).trim());
        segment.setDeptTo(_H[3].trim());
        String[] temp = _H[5].split(" ");
        segment.setAirLineID(temp[0]);
        if (temp.length > 4) {
            segment.setFlightNo(temp[4]);
        }
        if (temp.length > 5) {
            segment.setTicketClass(temp[5]);
        }
        if (temp.length > 7) {
            segment.setDeptDate(temp[7].substring(0, 5));
        }
        if (temp.length > 7) {
            segment.setDeptTime(temp[7].substring(5, 9));
        }
        if (temp.length > 9) {
            segment.setArvDate(temp[9]);
        }
        if (temp.length > 8) {
            segment.setArvTime(temp[8]);
        }
        if (_H.length > 6 && _H[6].length() > 2) {
            segment.setTktStatus(_H[6].substring(0, 2));
        }
        if (_H.length > 8) {
            segment.setMealCode(_H[8].trim());
        }
        if (_H.length > 13) {
            segment.setBaggage(_H[13]);
        }
        if (_H.length > 14) {
            segment.setCheckInTerminal(_H[14]);
        }
        if (_H.length > 15) {
            segment.setCheckInTime(_H[15]);
        }
        if (_H.length > 17) {
            segment.setFlightDuration(_H[17]);
        }
        if (_H.length > 19) {
            segment.setMileage(_H[19]);
        }
        return segment;
    }
}
