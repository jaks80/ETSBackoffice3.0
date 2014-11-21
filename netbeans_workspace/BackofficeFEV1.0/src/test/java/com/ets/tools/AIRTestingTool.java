package com.ets.tools;

import com.amadeus.air.AIR;
import com.amadeus.air.AIRLineParser;
import com.amadeus.air.FileToAIRConverter;
import com.ets.util.DateUtil;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Yusuf
 */
public class AIRTestingTool {
    
    private File[] airFiles;

    public AIRTestingTool() {

    }

    @Before
    public void setUp() throws URISyntaxException, IOException {
        airFiles = checkAirDirectory();
        System.out.println("Files found: " + airFiles.length);        
    }

    //@Test
    public void runIssueDateTestingTool() {
        for (File airFile : airFiles) {
            FileToAIRConverter converter = new FileToAIRConverter();
            AIR air = converter.convert(airFile);
            if(!air.getType().equals("TTP")){
             //System.out.println("Not a TTP file");
             continue;
            }
            findIssuDateConflict(air);
        }
    }
    /**
     * This tool testing if TKOK Date and AIR creation date is same in all TTP
     * case.
     * Result: Ticket could be oked before and Issued after. So both date can not
     * be always same;
     */
    private void findIssuDateConflict(AIR air) {

        Date airCreationDate = null;
        Set<Date> tkOKdates = new LinkedHashSet<>();

        for (String s : air.getLines()) {            

            if (s.startsWith("D-")) {
                String[] vals = AIRLineParser.parseDLine(s);
                airCreationDate = DateUtil.yyMMddToDate(vals[2]);
            }

            if (s.startsWith("TKOK")) {
                String[] data = AIRLineParser.parseTKLine(s);
                Date tkOKDate = DateUtil.ddMMMToDate(data[0].substring(2));
                tkOKdates.add(tkOKDate);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(air.getFile().getName());
        sb.append(" TKOKD QTY: "+tkOKdates.size());
        if(tkOKdates.iterator().hasNext()){
         sb.append("TKOKD: "+tkOKdates.iterator().next());
        }
        sb.append("ACD: "+airCreationDate.toString());
        
        System.out.println(sb);
    }

    private File[] checkAirDirectory() {
        File[] files = null;
        try {
            files = new File("G:\\Amadeus\\BKUP_AIR").listFiles();
        } catch (Throwable e) {

        }
        return files;
    }
}
