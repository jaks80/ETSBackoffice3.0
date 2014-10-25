package connector;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Logger;

/**
 *
 * @author Yusuf
 */
public class ResponseHandler implements Observer {
   
    static final Logger logger = Logger.getLogger(ResponseHandler.class.getName());

    @Override
    public void update(Observable o, Object arg) {
        try {
            File[] airFiles = checkAirDirectory();
            Thread.sleep(4000);//Wait to get all files ready
            for (int i = 0; i < airFiles.length; i++) {
                try {
                    Thread t = new Thread(new AirFileManager(airFiles[i]));
                    t.start();
                    t.join();

                } catch (Throwable e) {
                    AirFileManager.sendAirToErrorDirectory(airFiles[i]);
                    logger.fatal(e);
                    continue;
                }
            }
        } catch (Throwable e) {
             logger.fatal(e);
        }
    }

    private File[] checkAirDirectory() {
        File[] files = null;
        try {
            files = new File("C://AIR").listFiles();
        } catch (Throwable e) {
            logger.fatal(e);
        }
        return files;
    }
}
