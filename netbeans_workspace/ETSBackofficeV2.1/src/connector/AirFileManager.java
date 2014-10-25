package connector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 *
 * @author Yusuf
 */
public class AirFileManager implements Runnable{

    //static List<File> pendingAirFiles = new ArrayList<File>();
    static File air;
    private static String airNo;
    private static String airType;
    private static int airSectionNo;
    private static int airTotalNo;
    //Test test;// = new Test();   
    static final Logger logger = Logger.getLogger(ResponseHandler.class.getName());

    public AirFileManager(File air) {
        AirFileManager.air = air;
    }
   
    public static File[] getFilesFromAirDirectory() {
        File[] airFiles = new File("C://AIR").listFiles();
        return airFiles;
    }

    public static boolean isValidFile(File f) {
        String firstLine = "";
        String lastLine = "";
        try {
            Scanner scanner = new Scanner(new FileReader(f));

            int linePosition = 0;
            try {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    if (linePosition == 0) {
                        firstLine = line;
                    } else {
                        lastLine = line;
                    }
                    linePosition++;
                }
            } finally {
                scanner.close();
            }

        } catch (FileNotFoundException ex) {
            
            logger.fatal(ex);
        } catch (Exception e) {
            
            logger.fatal(e);
        }
        if (firstLine.startsWith("AIR") && lastLine.startsWith("END")) {
            return true;
        } else {
            return false;
        }
    }

    public static void backupAir(File airFile) {
        File dir = new File("C://BKUP_AIR");
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            boolean backedUp = airFile.renameTo(new File("C://BKUP_AIR", getAirNo(airFile) + ".txt"));
            if (backedUp) {
                System.out.println("BKUP Completed");
            } else {
                System.out.println("Could not BKUP");
                boolean deleted = airFile.delete();
                if (deleted) {
                    System.out.println("Deleted");
                } else {
                    System.out.println("Could not delete");
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    public static void sendAirToErrorDirectory(File airFile) {
        File dir = new File("C://ERROR_AIR");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            System.out.println("Sending air to error folder");
            boolean sent = airFile.renameTo(new File("C://ERROR_AIR", getAirNo(airFile) + ".txt"));
            if (sent) {
                System.out.println("Sent air to error folder");
            } else {
                System.out.println("Could not sent");
                boolean deleted = airFile.delete();
                if (deleted) {
                    System.out.println("Deleted");
                } else {
                    System.out.println("Could not delete");
                }
            }
        } catch (Exception e) {
            logger.fatal(e);
            System.out.println("Could not send...trying to delete");
            airFile.delete();
            System.out.println("Deleted");
        }
    }

    public static String getAirNo(File air) {
        Scanner s = null;
        try {
            s = new Scanner(air);
            fileReading:
            while (s.hasNextLine()) {
                String l = s.nextLine();
                if (l.startsWith("AIR")) {
                    String temp = "";
                    String[] data = l.split(";");
                    temp = data[0].substring(0, 3);
                    setAirNo(data[4]);
                    break fileReading;
                }
            }

        } catch (FileNotFoundException ex) {
            logger.fatal(ex);
        } finally {
            s.close();
        }
        return getAirNo();
    }

    private void setFileProperties() {
        Scanner s = null;
        try {
            s = new Scanner(air);
            fileReading:
            while (s.hasNextLine()) {
                String l = s.nextLine();
                if (l.startsWith("AIR")) {
                    String temp = "";
                    String[] data = l.split(";");
                    temp = data[0].substring(0, 3);
                    setAirNo(data[4]);
                } else if (l.startsWith("B-")) {
                    if (l.contains("BT")) {
                        setAirType("bt");
                    } else if (l.contains("ET")) {
                        setAirType("et");
                    } else if (l.contains("TTP")) {
                        setAirType("ttp");
                    } else if (l.contains("INV")) {
                        setAirType("inv");
                    } else if (l.contains("TRFP")) {
                        setAirType("trfp");
                    }
                } else if (l.startsWith("AMD")) {
                    String[] data = l.split(";");
                    setAirSectionNo(Integer.parseInt(data[1].substring(0, 1)));
                    setAirTotalNo(Integer.parseInt(data[1].substring(2, 3)));
                    if (data[2].substring(0, 4) == null ? "VOID" == null : data[2].substring(0, 4).equals("VOID")) {
                        setAirType("void");
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            logger.fatal(ex);
            logger.fatal(ex);
        } catch (Exception e) {
            logger.fatal(e);
            logger.fatal(e);
        } finally {
            s.close();
        }
    }

    public void run() {
        int noOfTry = 0;        
        fileValidity:
        while (noOfTry < 2) {
            try {
                if (isValidFile(AirFileManager.air)) {
                    System.out.println("File is valid");
                    setFileProperties();
                    if(!"bt".equals(getAirType())){
                    FileHandler f = new FileHandler(air);
                    f.start();                      
                    }
                    backupAir(air);
                    break fileValidity;
                } else {
                    System.out.println("File is not valid: noOfTry = " + noOfTry);
                    noOfTry++;
                    Thread.sleep(3000);
                    if (noOfTry == 2) {
                        sendAirToErrorDirectory(AirFileManager.air);
                    }
                }
            } catch (Throwable ex) {
                System.out.println("Error reading air: " +ex);
                sendAirToErrorDirectory(AirFileManager.air);                
                logger.fatal(ex);
            }
        }
    }

    public static String getAirNo() {
        return airNo;
    }

    public static void setAirNo(String aAirNo) {
        airNo = aAirNo;
    }

    public static String getAirType() {
        return airType;
    }

    public static void setAirType(String aAirType) {
        airType = aAirType;
    }

    public static int getAirSectionNo() {
        return airSectionNo;
    }

    public static void setAirSectionNo(int aAirSectionNo) {
        airSectionNo = aAirSectionNo;
    }

    public static int getAirTotalNo() {
        return airTotalNo;
    }

    public static void setAirTotalNo(int aAirTotalNo) {
        airTotalNo = aAirTotalNo;
    }
}
