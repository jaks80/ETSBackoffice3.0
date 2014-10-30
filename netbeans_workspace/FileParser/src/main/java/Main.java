
import com.amadeus.reader.EventSource;
import com.amadeus.reader.ResponseHandler;

/**
 *
 * @author Yusuf
 */
public class Main {

    public static void main(String[] args) {
        ResponseHandler respHandler;
        EventSource evSrc;
        Thread thread;

        respHandler = new ResponseHandler();
        evSrc = new EventSource();
        evSrc.addObserver(respHandler);
        thread = new Thread(evSrc);
        thread.start();
    }
}
