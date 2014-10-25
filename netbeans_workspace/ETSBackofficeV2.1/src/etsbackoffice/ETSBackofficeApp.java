/*
 * ETSBackofficeApp.java
 */
package etsbackoffice;

import connector.EventSource;
import connector.ResponseHandler;
import etsbackoffice.gui.DlgLogin;
import etsbackoffice.gui.ETSBackofficeMain;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
//import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceManager;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The main class of the application.
 */
public class ETSBackofficeApp extends SingleFrameApplication {

    public ApplicationContext ctx;    
    ResponseHandler respHandler;
    EventSource evSrc;
    Thread thread;
    ResourceMap resource;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        if(login()){                                
         show(new ETSBackofficeMain(this));
        }
    }

    @Override
    protected void initialize(String[] arigs) {
        org.jdesktop.application.ApplicationContext ctxt = getContext();
        ResourceManager mgr = ctxt.getResourceManager();
        resource = mgr.getResourceMap(ETSBackofficeApp.class);                
    }

    private boolean login() {
        boolean success = false;
        DlgLogin dlgLogin = new DlgLogin(this.getMainFrame());        
        if (dlgLogin.showLoginDialog()) {
            success = true;
        } else {
            success = false;
        }
        return success;
    }
   /* @Override
    protected void shutdown() {
        // The default shutdown saves session window state.
        super.shutdown();
        // Now perform any other shutdown tasks you need.
    }*/

      
    public void close() {
        // The default shutdown saves session window state.
        super.shutdown();
        // Now perform any other shutdown tasks you need.
        exit();
    }

        
    public void showMainFrame() {
        show(new ETSBackofficeMain(this));
    }

    public void startFileReading() {
        respHandler = new ResponseHandler();
        evSrc = new EventSource();
        evSrc.addObserver(respHandler);
        thread = new Thread(evSrc);
        thread.start();
    }

    public void configureSpringContext() {
        try {
            ctx = new ClassPathXmlApplicationContext("META-INF/bean-hibernate.xml");
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e, "Spring Exception", JOptionPane.WARNING_MESSAGE);
            System.out.println(e);
        }
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of ETSBackofficeApp
     */
    public static ETSBackofficeApp getApplication() {
        return Application.getInstance(ETSBackofficeApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            lockApp l = new lockApp();
        } catch (RuntimeException ex) {
            // exit main app            
            JOptionPane.showMessageDialog(null, "ETSBackoffice is already running!", "ETSBAckoffice", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        launch(ETSBackofficeApp.class, args);
    }         
}
