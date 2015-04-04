package com.amadeus.reader;

import java.io.File;
import java.util.Observable;

public class EventSource extends Observable implements Runnable {

    File air;
    File errorAir;
    File[] airs;
    File[] errorAirs;
    int numberOfAirs = 0;

    public void run() {
        while (true) {
            String response = "notify";
            air = new File("C://AIR");
            errorAir = new File("C://ERROR_AIR");

            if (!air.exists()) {
                break;
            } //else {
                //if (!errorAir.exists()) {
                //    errorAir.mkdir();
                //}
            //}

            airs = air.listFiles();
            numberOfAirs = air.listFiles().length;

            if (numberOfAirs > 0) {
                setChanged();
                notifyObservers(response);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
