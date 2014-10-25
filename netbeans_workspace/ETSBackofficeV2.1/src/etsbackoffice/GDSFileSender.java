/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Ref Source: http://www.harezmi.com.tr/how-to-attach-zipped-content-while-sending-mail/?lang=en
 * Ref Source: http://www.java2s.com/Code/Java/File-Input-Output/UseJavacodetozipafolder.htm
 */
package etsbackoffice;

import etsbackoffice.businesslogic.AuthenticationBo;
import etsbackoffice.businesslogic.EmailService;
import etsbackoffice.gui.DlgWait;
import java.awt.Cursor;
import java.io.*;
import java.util.zip.*;
import org.springframework.core.io.ByteArrayResource;

/**
 *
 * @author Yusuf
 */
public class GDSFileSender {

    static final int BUFFER = 2048;
    byte[] fileStream;    
    

    public GDSFileSender() {
    }

    public void zipAirAndEmail() throws Exception { 
          
        emailZipFolder("c:\\BKUP_AIR", "c:\\BKUP_AIR.zip");
        //dlgWait.disposeDlgWait();
    }

    public void emailZipFolder(String srcFolder, String destZipFile) throws Exception {
        
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        //fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(bout);

        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
        emailFolder(bout);
    }

    private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
            throws Exception {

        File folder = new File(srcFile);
        
        if (folder.isDirectory()) {            
            addFolderToZip(path, srcFile, zip);
        } else {            
            byte[] buf = new byte[1024];
            int len;            
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {                
                zip.write(buf, 0, len);
            }
        }
    }

    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
            throws Exception {
        File folder = new File(srcFolder);
        int i = 0;
        for (String fileName : folder.list()) {
                      
            if (path.equals("")) {           
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {                
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }

    public void emailFolder(ByteArrayOutputStream bout) {
        EmailService email = new EmailService();

        String recepientAddress = "yusuf.shetu@googlemail.com";
        String subject = "AIR from " + AuthenticationBo.getmAgent().getName();
        String body = "AIR from " + AuthenticationBo.getmAgent().getName();        
        ByteArrayResource attachment = new ByteArrayResource(bout.toByteArray());

        email.SendMailWithBAR(recepientAddress, subject, body, attachment);
    }        
}
