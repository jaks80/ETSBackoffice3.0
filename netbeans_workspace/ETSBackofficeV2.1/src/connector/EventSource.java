package connector;

import java.io.File;
import java.util.Observable;

public class EventSource extends Observable implements Runnable {

    File air;
    File errorAir;
    File[] airs;
    File[] errorAirs;
    int numberOfAirs = 0;
    int numberOfErrorAirs = 0;

    public void run() {
        while (true) {
            String response = "notify";
            air = new File("C://AIR");
            errorAir = new File("C://ERROR_AIR");
            
            if (!air.exists()) {
                break;
            } else {
                if (!errorAir.exists()) {
                    errorAir.mkdir();
                }
            }

            airs = air.listFiles();
            numberOfAirs = air.listFiles().length;

            if (numberOfAirs > 0) {
                setChanged();
                notifyObservers(response);
            } else { 
                errorAirs = errorAir.listFiles();
                numberOfErrorAirs = errorAir.listFiles().length;
                if (numberOfErrorAirs > 0) {                    
                    sendErrorAirBackToAir(errorAirs);
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

     //Note: There is a chance of loosing file if user terminates application
     //while transfering file from one folder to another.
    public void sendErrorAirBackToAir(File[] errorAirs) {

        try {
            for (int i = 0; i < errorAirs.length; i++) {
                boolean sent = errorAirs[i].renameTo(new File("C://AIR", errorAirs[i].getName()));
                
                System.out.println("File transfer status: "+ errorAirs[i].getName()+" "+ sent);
            }
        } catch (Exception e) {
            System.out.println("Exception error air transfer: "+ e);
        }
    }
}
