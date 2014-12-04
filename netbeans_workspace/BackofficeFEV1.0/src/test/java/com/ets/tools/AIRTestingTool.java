package com.ets.tools;

import com.amadeus.air.AIR;
import com.amadeus.air.AIRLineParser;
import com.amadeus.air.FileToAIRConverter;
import com.ets.fe.util.DateUtil;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.LinkedHashSet;
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

    @Test
    public void findCancelledSegmentTest(){
     for (File airFile : airFiles) {
            FileToAIRConverter converter = new FileToAIRConverter();
            AIR air = converter.convert(airFile);
            if(air.getType().equals("TRFP")){
             //System.out.println("Not a TTP file");
             continue;
            }
            findCancelledSegment(air);
        }
        System.out.println("Done...");
    }
    
    private void findCancelledSegment(AIR air) {
        for (String s : air.getLines()) {
            if (s.startsWith("U-") || s.startsWith("H-")) {
                String[] vals = AIRLineParser.parseULine(s);
                if(vals[0].contains("X")){
                    System.out.println(air.getAirSequenceNumber()+" "+air.getAirFile().getName()+" "+s);
                }
            }
        }
    }
    
    //@Test
    public void findMultiPageNonIssueFileTest(){
    for (File airFile : airFiles) {
            FileToAIRConverter converter = new FileToAIRConverter();
            AIR air = converter.convert(airFile);
            if(!air.getType().equals("TTP")){
             //System.out.println("Not a TTP file");
             continue;
            }
            findMultiPageNonIssueFile(air);
        }
        System.out.println("Done...");
    }
    
    private void findMultiPageNonIssueFile(AIR air){
    
        String pagingString="";
                
        for (String s : air.getLines()) { 
         if (s.startsWith("AMD")) {
                String[] vals = AIRLineParser.parseAMDLine(s);
                pagingString = vals[1];
                break;
            }
        }
        if(!"1/1".equals(pagingString)){
            System.out.println(air.getAirFile().getName()+" "+pagingString);
        }
        
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

            if (s.startsWith("TKXL")) {
                String[] data = AIRLineParser.parseTKLine(s);
                Date tkOKDate = DateUtil.ddMMMToDate(data[0].substring(2));
                tkOKdates.add(tkOKDate);
            }
        }

        if(!tkOKdates.isEmpty()){
        StringBuilder sb = new StringBuilder();
        
        sb.append(" TKOKD QTY: "+tkOKdates.size());
        if(tkOKdates.iterator().hasNext()){
         sb.append("TKOKD: "+tkOKdates.iterator().next());
        }
        sb.append("ACD: "+airCreationDate.toString());
        
        System.out.println(sb);
        }
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
