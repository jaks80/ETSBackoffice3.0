package com.amadeus.air;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Yusuf
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TJQ implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    private String dateStart;
    @XmlElement
    private String dateEnd;
    @XmlElement
    private String officeId;
    @XmlElement
    private List<String> lines = new ArrayList<>();

    public static Map<String,String> getLinesValues(String line) {
        line = line.replaceAll("\\s+", " ").trim();
        String[] vals = line.split(" ");
        for (String s : vals) {
            s = s.trim();
        }

        /*Reversing line because beginning of the line has some unstable characters.
        To avoind unnecessary complexity we are just reversing line to get data from
        end of the line.*/
        List<String> list = Arrays.asList(vals);
        Collections.reverse(list);
        vals = (String[]) list.toArray();
        
        Map<String,String> map = new HashMap<>();
        map.put("TRNC", vals[0]);
        map.put("RLOC", vals[1]);
        map.put("PAX NAME", vals[3]);
        map.put("COMM", vals[5]);
        map.put("FEE", vals[6]);
        map.put("TAX", vals[7]);
        map.put("TOTAL DOC", vals[8]);
        map.put("DOC NUMBER", vals[9]);
        return map;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public void addLine(String line) {
        this.lines.add(line);
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }
}
