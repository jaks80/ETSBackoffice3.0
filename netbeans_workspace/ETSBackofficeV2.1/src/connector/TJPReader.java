/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connector;

import etsbackoffice.domain.Ticket;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */

public class TJPReader {
    
    private File file;
    private String fileType;
    
    public TJPReader(File file){
     this.file = file;
     setFileType(this.file);
    }
    
    private void setFileType(File file) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(this.file));
            String line = null;

            while ((line = bf.readLine()) != null) {
                if (line.startsWith("TJP")) {
                    fileType = "TJP";
                    break;
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     private void parseTJPFile(File file) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(this.file));
            String line = null;

            while ((line = bf.readLine()) != null) {
                String charSeq = line.substring(0, 6);
                if(isNumeric(charSeq)){
                
                }
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Ticket getTktFromTjpLine(String line) {
        String[] data = line.split(" ");
        Ticket t = new Ticket();
        try {
            t.setTicketNo(data[1].trim());
            t.setBaseFare(new BigDecimal(data[2]));
            t.setTax(new BigDecimal(data[3]));
            t.setBspCom(new BigDecimal(data[5]));
        } catch (Exception e) {
            return null;
        }
        return t;
    }
    
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(.\\d+)?");
    }
     
    public String getFileType() {
        return fileType;
    }    
}
