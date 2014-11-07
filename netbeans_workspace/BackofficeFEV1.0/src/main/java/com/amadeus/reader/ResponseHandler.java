package com.amadeus.reader;

import com.amadeus.air.AIR;
import com.amadeus.air.FileToAIRConverter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class ResponseHandler implements Observer {

    List<AIR> airs = new ArrayList<>();
    
    @Override
    public void update(Observable o, Object arg) {

        try {
            File[] airFiles = checkAirDirectory();
            Thread.sleep(4000);//Wait to get all files ready
            for (int i = 0; i < airFiles.length; i++) {
                FileToAIRConverter air = new FileToAIRConverter();                
                airs.add(air.convert(airFiles[i]));
            }
            System.out.println("Done");
        } catch (InterruptedException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private File[] checkAirDirectory() {
        File[] files = null;
        try {
            files = new File("C://AIR").listFiles();
        } catch (Throwable e) {

        }
        return files;
    }
}
