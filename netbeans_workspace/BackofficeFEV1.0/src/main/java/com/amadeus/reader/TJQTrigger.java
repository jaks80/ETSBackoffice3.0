package com.amadeus.reader;

import com.amadeus.air.FileToTJQConverter;
import com.amadeus.air.TJQ;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * This class is the starting point of reading TJQ file. 
 * @author Yusuf
 */
public class TJQTrigger implements Runnable {

    File tjq;
    File[] files;

    public void run() {

        tjq = new File("C://Amadeus_Documents");
        if (!tjq.exists()) {
            System.out.println("Pro printer not configured for TJQ analysis.");
        } else {
            files = tjq.listFiles();
            if (tjq.listFiles().length > 0) {
                for (File file : tjq.listFiles()) {
                    if (isValidFile(file)) {
                        FileToTJQConverter converter = new FileToTJQConverter();
                        TJQ tjq = converter.convert(file);
                    } else {
                        //delete or backup
                    }
                }
            }
        }
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
        } 

        return firstLine.startsWith("TJP-START") && lastLine.startsWith("TJP-END");
    }
}
