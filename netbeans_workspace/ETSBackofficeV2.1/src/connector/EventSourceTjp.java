/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connector;

import java.io.File;
import java.util.Observable;
/**
 *
 * @author Yusuf
 */
public class EventSourceTjp extends Observable implements Runnable{
    File amadeus_doc;
    File[] amadeus_docs;
    int numberOfFiles = 0;

    public void run() {
        while (true) {
            String response = "notify";
            amadeus_doc = new File("C://AMADEUS_DOC");
            if (!amadeus_doc.exists()) {
                break;
            }
            amadeus_docs = amadeus_doc.listFiles();
            numberOfFiles = amadeus_doc.listFiles().length;
            if (numberOfFiles > 0) {
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
