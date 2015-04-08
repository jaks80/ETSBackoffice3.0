package com.ets.fe.tools.gui;

import com.amadeus.air.FileToTJQConverter;
import com.amadeus.air.TJQ;
import com.ets.fe.pnr.model.*;
import com.ets.fe.pnr.ws.TicketWSClient;
import com.ets.fe.util.DateUtil;
import com.ets.fe.util.Enums;
import com.ets.fe.util.PnrUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXTable;

/**
 * This class is the starting point of reading TJQ file.
 *
 * @author Yusuf
 */
public class TJQTask1 extends SwingWorker<Void, Void> {

    private JProgressBar progressBar;
    private JTextArea txtStatus;
    private JXTable tblTicket;
    private JXTable tblTjq;
    private JTextArea txtMissing;

    File tjq;
    File[] files;

    public TJQTask1(JTextArea txtStatus, JProgressBar progressBar, JXTable tblTicket, JXTable tblTjq, JTextArea txtMissing) {
        this.progressBar = progressBar;
        this.txtStatus = txtStatus;
        this.tblTicket = tblTicket;
        this.tblTjq = tblTjq;
        this.txtMissing = txtMissing;
    }

    @Override
    public Void doInBackground() {

        tjq = new File("C://Amadeus_Documents");
        if (!tjq.exists()) {
            update("Pro printer not configured for TJQ analysis.", 0);
        } else {
            files = tjq.listFiles();
            if (tjq.listFiles().length > 0) {
                for (File file : tjq.listFiles()) {
                    if (isValidFile(file)) {
                        FileToTJQConverter converter = new FileToTJQConverter();
                        TJQ _tjq = converter.convert(file);
                        if (_tjq == null) {
                            update("No TJP report found", 0);
                            return null;
                        }
                        update("Task started\n----------------\n\n"
                                + "Date from:" + _tjq.getDateStart()
                                + "\nDate to  :" + _tjq.getDateEnd(), 5);

                        TicketWSClient client = new TicketWSClient();
                        GDSSaleReport saleReport = client.gdsSaleReport(Enums.TicketingType.IATA,null, null,
                                DateUtil.ddmmToDate(_tjq.getDateStart()),
                                DateUtil.ddmmToDate(_tjq.getDateEnd()),
                                _tjq.getOfficeId());

                        match(_tjq, saleReport);
                    } else {
                        update("No TJP report found", 0);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Status MATC, DUPL,UMAT Logic: 1.Get tjq and system tickets collection.
     * 2.Iterate through tjq lines and find matching tickets. 3.If all fields
     * matching its a MATC, If all matcing but already exit its a DUPL and add
     * it to DUPL list 4.Else if Tktno and tktStatus is matching but other are
     * not matching its a UMAT 5.If does not exit in system tickets its a
     * missing ticket. Add it to missing list. 6.Check ticket available in
     * system but not in tjq report.
     *
     * @param tjq
     * @param saleReport
     */
    private void match(TJQ tjq, GDSSaleReport saleReport) {

        List<String> tjq_lines = tjq.getLines();
        List<Ticket> db_tickets = saleReport.getList();

        List<Ticket> matching_tickets = new ArrayList<>();
        List<Ticket> duplicate_tickets = new ArrayList<>();//Inserted more than once in DB
        List<Ticket> unmatched_tickets = new ArrayList<>();// Ticket exist but figures dont match
        List<Ticket> excess_tickets = new ArrayList<>();   //Ticket exist in DB but not in TJQ
        List<String> missing_tickets = new ArrayList<>();  //Ticket is missing in DB

        List<String> unreadlines = new ArrayList<>();

        update("TJQ Tkts: " + tjq_lines.size(), 6);
        update("Sys Tkts: " + db_tickets.size(), 6);

        DefaultTableModel ticketModel = (DefaultTableModel) tblTicket.getModel();
        ticketModel.getDataVector().removeAllElements();

        DefaultTableModel tjqModel = (DefaultTableModel) tblTjq.getModel();
        tjqModel.getDataVector().removeAllElements();

        for (String line : tjq_lines) {
            Map<String, String> map = TJQ.getLinesValues(line);

            String tktno = map.get("DOC NUMBER");
            String name = map.get("PAX NAME");
            String _pnr = map.get("RLOC");
            String status = map.get("TRNC");
            String _totalfare = map.get("TOTAL DOC");
            String _tax = map.get("TAX");
            String _fees = map.get("FEE");
            String _com = map.get("COMM");

            BigDecimal totalfare = new BigDecimal("0.00");
            BigDecimal basefare = new BigDecimal("0.00");
            BigDecimal tax = new BigDecimal("0.00");
            BigDecimal fees = new BigDecimal("0.00");
            BigDecimal com = new BigDecimal("0.00");

            try {
                totalfare = new BigDecimal(_totalfare);
            } catch (Exception e) {}

            try {
                tax = new BigDecimal(_tax);
            } catch (Exception e) {}
            try {
                fees = new BigDecimal(_fees);
            } catch (Exception e) {}
            try {
                com = new BigDecimal(_com);
            } catch (Exception e) {}

            basefare = totalfare.subtract(tax).subtract(fees);
            
            boolean exit = false;
            Ticket matchingTkt = null;

            for (Ticket t : db_tickets) {
                if (t.getTicketNo().equals(tktno)
                        && t.getTktStatus().equals(PnrUtil.tjqStatusConverter(status))
                        && t.getBaseFare().compareTo(basefare) == 0
                        && t.getTax().compareTo(tax) == 0
                        && t.getFee().compareTo(fees) == 0
                        && t.getCommission().compareTo(com) == 0) {//Every field is matching

                    if (!exit) {
                        exit = true;
                    } else {
                        duplicate_tickets.add(t);
                        populateTblTicket(ticketModel, t, "EXCESS");
                        populateTblTjq(tjqModel, tktno, name, basefare.toString(), tax.toString(), 
                                fees.toString(), com.toString());
                    }
                } else if ((t.getTicketNo().equals(tktno)
                        && t.getTktStatus().equals(PnrUtil.tjqStatusConverter(status)))
                        && (t.getBaseFare().compareTo(basefare) != 0
                        || t.getTax().compareTo(tax) != 0
                        || t.getFee().compareTo(fees) != 0
                        || t.getCommission().compareTo(com) != 0)) {

                    exit = true;
                    matching_tickets.add(t);
                    populateTblTjq(tjqModel, tktno, name, basefare.toString(), tax.toString(), 
                            fees.toString(), com.toString());
                    populateTblTicket(ticketModel, t, "UMATCH");
                }
            }
            if (!exit) {
                missing_tickets.add(tktno + "/" + _pnr);
                populateTblTjq(tjqModel, tktno, name, basefare.toString(), tax.toString(), 
                        fees.toString(), com.toString());
                populateTblTicket(ticketModel, null, "MISING");
            }
        }

        if (db_tickets.size() > tjq_lines.size()) {
            excess_tickets = db_tickets;
            excess_tickets.removeAll(matching_tickets);
            excess_tickets.removeAll(duplicate_tickets);
            excess_tickets.removeAll(unmatched_tickets);
        }

        for (Ticket t : excess_tickets) {
            populateTblTjq(tjqModel, "", "", "", "", "", "");
            populateTblTicket(ticketModel, t, "EXCESS");
        }
        update("Result... ", 10);
        update("----------------", 10);
        update("Missing   : " + missing_tickets.size(), 10);
        update("Excess    : " + excess_tickets.size(), 10);
        update("Duplicates: " + duplicate_tickets.size(), 10);
        update("ManualyChk: " + unreadlines.size(), 10);
        update("----------------", 10);
        update("Done... ", 100);

        txtMissing.append("Verify manually:\n");
        for (String s : unreadlines) {
            txtMissing.append(s + "\n");
        }
    }

    private void populateTblTjq(DefaultTableModel tjqModel, String tktno,
            String name, String bfare, String tax, String fees, String com) {

        int row = tjqModel.getRowCount();
        tjqModel.insertRow(row, new Object[]{tktno, name, bfare, tax, fees, com});
    }

    private void populateTblTicket(DefaultTableModel ticketModel, Ticket t, String status) {
        int row = ticketModel.getRowCount();

        if (t != null) {
            ticketModel.insertRow(row, new Object[]{t.getTicketNo(), PnrUtil.calculatePartialName(t.getFullPaxName()),
                t.getBaseFare(), t.getTax(), t.getFee(), t.getCommission(), status});
        } else {
            ticketModel.insertRow(row, new Object[]{"", "", "", "", "", "", status});
        }
    }

    private void update(String message, int progress) {
        txtStatus.append(message);
        txtStatus.append("\n");
        setProgress(progress);
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    public static boolean isValidFile(File f) {
        String firstLine = "";
        String lastLine = "";
        try {
            Scanner scanner = new Scanner(new FileReader(f));

            int linePosition = 0;
            try {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    if (linePosition == 0) {
                        firstLine = line;
                    } else {
                        lastLine = line;
                    }
                    linePosition++;
                }
            } finally {
                scanner.close();
            }

        } catch (FileNotFoundException ex) {
        }

        return (firstLine.startsWith("TJP-START") && lastLine.startsWith("TJP-END"))
                || firstLine.contains("QUERY REPORT");
    }
}
