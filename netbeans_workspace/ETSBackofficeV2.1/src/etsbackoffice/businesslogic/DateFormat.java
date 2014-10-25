package etsbackoffice.businesslogic;

import java.sql.Timestamp;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateFormat {
    
    private String dfMysql = null;
    private String dfGUI = null;
    private SimpleDateFormat dfMySql = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");
    private SimpleDateFormat dfGui = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat fullDfGui = new SimpleDateFormat("dd-MM-yyyy:HH-mm-ss");
    private SimpleDateFormat dfInPut = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dfAir = new SimpleDateFormat("yyMMdd");
    SimpleDateFormat dfOutput = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar cal = Calendar.getInstance();
    
    public DateFormat() {
    }
    
    public String currentDateMysqlFormat() {
        String tempDate = dfMySql.format(cal.getTime());
        return tempDate;
    }
    
    public String dateMysqlFormat(String date) {
        return dfMySql.format(date);
    }
    
    public String dateForGui(Date date) {
        String dateForGUI = "";
        if (date != null) {
            dateForGUI = dfGui.format(date);
        }
        return dateForGUI;
    }
    
    public Date getCurrentDate() {
        return new java.util.Date();
    }
    
    public String getCurrentTime() {
        cal = cal = new GregorianCalendar();
        int hour12 = cal.get(Calendar.HOUR);            // 0..11
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);     // 0..23
        int min = cal.get(Calendar.MINUTE);             // 0..59
        int sec = cal.get(Calendar.SECOND);             // 0..59
        int ms = cal.get(Calendar.MILLISECOND);         // 0..999
        int ampm = cal.get(Calendar.AM_PM);             // 0=AM, 1=PM
        String currenttime = hour24 + ":" + min + ":" + sec;
        return currenttime;
    }
    
    public String getCurrentDateTime() {
        //cal = cal = new Calendar();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH));
        String day = String.valueOf(cal.get(Calendar.DATE));
        String hour24 = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));     // 0..23
        String min = String.valueOf(cal.get(Calendar.MINUTE));             // 0..59
        String sec = String.valueOf(cal.get(Calendar.SECOND));             // 0..59
        String currentDateTime = year + month + day + "/" + hour24 + min + sec;
        return currentDateTime;
    }
    
    public String getCurrentYear() {        
        String year = String.valueOf(cal.get(Calendar.YEAR));        
        return year.substring(2);
    }

    public String getDateTimeDigitOnly(Date date) {
        
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonth());
        String day = String.valueOf(date.getDay());
        String hour24 = String.valueOf(date.getHours());
        String min = String.valueOf(date.getMinutes());
        String sec = String.valueOf(date.getSeconds());
        String currentDateTime = year + month + day + "/" + hour24 + min + sec;
        return currentDateTime;
    }
    
    public Timestamp getCurrentTimeStamp() {
        Date now = cal.getTime();
        Timestamp instance = new java.sql.Timestamp(now.getTime());
        return instance;
    }
    
    public Date getThirdPartyIssueDate(String ddmm) {
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String tempDate = ddmm.concat(year);
        SimpleDateFormat dfIn = new SimpleDateFormat("ddMMMyyyy");
        SimpleDateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd");
        String finalDate = null;
        Date date = null;
        try {
            finalDate = dfOut.format(dfIn.parse(tempDate));
            date = dfOut.parse(finalDate);
        } catch (ParseException ex) {
            Logger.getLogger(DateFormat.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return date;
    }
}
