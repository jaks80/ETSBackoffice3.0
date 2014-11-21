package com.amadeus.air;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Yusuf
 */
public class AIR {

    private String type;
    private String creationDate;
    private String bookingAgtOid;        
    private String ticketingAgtOid;
    private String totalPages;
    private String version;
    private String page;
    private File file;
    
    private List<String> lines = new ArrayList<>();

    public AIR() {

    }

    
    public String getALine() {

        for (String s : lines) {
            if (s.startsWith("A-")) {
                return s;
            }
        }        
        return null;
    }
    
    public String getMUCLine() {

        for (String s : lines) {
            if (s.startsWith("MUC")) {
                return s;
            }
        }        
        return null;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public void addLine(String line){
     this.lines.add(line);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getBookingAgtOid() {
        return bookingAgtOid;
    }

    public void setBookingAgtOid(String bookingAgtOid) {
        this.bookingAgtOid = bookingAgtOid;
    }

    public String getTicketingAgtOid() {
        return ticketingAgtOid;
    }

    public void setTicketingAgtOid(String ticketingAgtOid) {
        this.ticketingAgtOid = ticketingAgtOid;
    }
}
