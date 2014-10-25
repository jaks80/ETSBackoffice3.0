/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connector;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Yusuf
 */
public class ResponseHandlerTjp implements Observer {

    private TJPReader tjpReader;

    public void update(Observable o, Object arg) {
        File[] amadeus_docs = checkAmadeus_DocDirectory();

        for (int i = 0; i < amadeus_docs.length; i++) {
            tjpReader = new TJPReader(amadeus_docs[i]);
        }
    }

    private File[] checkAmadeus_DocDirectory() {
        return new File("C://AMADEUS_DOC").listFiles();
    }
}
