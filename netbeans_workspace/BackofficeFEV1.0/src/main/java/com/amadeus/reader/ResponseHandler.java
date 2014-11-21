package com.amadeus.reader;

import com.amadeus.air.AIR;
import com.amadeus.air.AirUtil;
import com.amadeus.air.FileToAIRConverter;
import java.io.File;
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
            Thread.sleep(3000);//Wait to get all files ready
            for (File airFile : airFiles) {
                int noOfTry = 0;
                if (AirUtil.isValidFile(airFile)) {
                    FileToAIRConverter airConverter = new FileToAIRConverter();
                    AIR air = airConverter.convert(airFile);
                    Thread t = new Thread(new ReaderThread(air));                    
                    t.start();
                    t.join();
                } else {
                    System.out.println("File is not valid: noOfTry = " + noOfTry);
                    noOfTry++;
                    Thread.sleep(3000);
                    if (noOfTry == 2) {
                        AirUtil.sendAirToErrorDirectory(airFile);
                    }
                }
            }
            System.out.println("Done");
        } catch (InterruptedException ex) {
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
