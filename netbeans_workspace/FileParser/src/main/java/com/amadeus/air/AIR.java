package com.amadeus.air;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Yusuf
 */
public class AIR {

    private String type;
    private String creationDate;
    private String version;
    private String page;
    
    private List<String> lines = new ArrayList<>();

    public AIR() {

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
}
