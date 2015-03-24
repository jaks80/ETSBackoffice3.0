package com.amadeus.air;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class FileToTJQConverter {

    public TJQ convert(File file) {

        boolean readableBlock = false;

        TJQ tjq = new TJQ();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = bf.readLine()) != null) {
                if (!readableBlock) {
                    if (line.contains("QUERY REPORT")) {
                        String[] vals = line.split("   ");
                        for (String s : vals) {
                            if (s.contains("QUERY REPORT")) {
                                String date_val = s.replaceFirst("QUERY REPORT", "").trim();
                                if (date_val.contains("-")) {
                                    String[] dates = date_val.split("-");
                                    tjq.setDateStart(dates[0]);
                                    tjq.setDateEnd(dates[1]);
                                    break;
                                } else {
                                    tjq.setDateStart(date_val);
                                    break;
                                }
                            }
                        }
                    }

                    if (line.startsWith("-----------------------------")) {
                        readableBlock = true;
                    }
                } else {
                    if(line.contains("SEQ NO") || 
                            line.contains("-------") || 
                            line.trim().isEmpty() || 
                            line.length()<20 || 
                            line.contains("CNJ")){
                     continue;
                    }
                    
                    tjq.addLine(line);
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileToTJQConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileToTJQConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return tjq;
    }

    private TJQ parseLine(String line) {

        if (line.contains("CNJ")) {
            return null;
        }
        TJQ tjq = new TJQ();
        String[] vals = line.split(" ");
        return tjq;
    }
}
