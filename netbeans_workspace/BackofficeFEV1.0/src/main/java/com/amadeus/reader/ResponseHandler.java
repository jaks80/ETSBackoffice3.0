package com.amadeus.reader;

import com.amadeus.air.*;
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

    @Override
    public void update(Observable o, Object arg) {

        startReading();
    }

    public void startReading() {
        try {
            File[] airFiles = checkAirDirectory();
            Thread.sleep(3000);//Wait to get all files ready
            int i = 0;
            for (; i < airFiles.length; i++) {
                int noOfTry = 0;
                if (AirUtil.isValidFile(airFiles[i])) {
                    List<File> filesDone = new ArrayList<>();
                    FileToAIRConverter airConverter = new FileToAIRConverter();
                    AIR air = airConverter.convert(airFiles[i]);
                    
                    if(air == null){
                     AirUtil.sendAirToErrorDirectory(airFiles[i]);
                     continue;
                    }
                    
                    filesDone.add(airFiles[i]);
                    for (int j = air.getCurrentPage() + 1; j <= air.getTotalPages(); j++) {                        
                        try {
                            airConverter = new FileToAIRConverter();
                            air.addPage(airConverter.convert(airFiles[++i]));
                            filesDone.add(airFiles[i]);
                        } catch (ArrayIndexOutOfBoundsException e) {}                        
                    }

                    AirWSClient client = new AirWSClient();
                    Integer httpStatus = client.postAir(air);

                    if (httpStatus == 200) {
                        for (File f : filesDone) {
                            AirUtil.backupAir(f);
                        }
                    }else{
                     for (File f : filesDone) {
                          AirUtil.sendAirToErrorDirectory(f);
                        }
                    }
                } else {
                    System.out.println("File is not valid: noOfTry = " + noOfTry);
                    noOfTry++;
                    Thread.sleep(3000);
                    if (noOfTry == 2) {
                        AirUtil.sendAirToErrorDirectory(airFiles[i]);
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
        } catch (Throwable e) {}
        return files;
    }
}
