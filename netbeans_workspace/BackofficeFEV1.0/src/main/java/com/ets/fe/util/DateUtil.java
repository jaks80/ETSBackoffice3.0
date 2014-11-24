package com.ets.fe.util;

import com.ets.fe.AppSettings;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yusuf
 */
public class DateUtil {

    private static final SimpleDateFormat dfInput = new SimpleDateFormat("yyMMdd");
    private static final SimpleDateFormat dfOutput = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dfInput1 = new SimpleDateFormat("ddMMMyy");
    private static Calendar cal = Calendar.getInstance();

    /**
     * For AIR
     *
     * @param yyMMdd
     * @return
     */
    public static Date yyMMddToDate(String yyMMdd) {

        Date date = null;
        try {
            date = dfOutput.parse(dfOutput.format(dfInput.parse(yyMMdd)));
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return date;
    }

    /**
     * For AIR
     *
     * @param ddmm
     * @return
     */
    public static Date ddMMMToDate(String ddMMM) {
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String tempDate = ddMMM.concat(year);
        SimpleDateFormat dfIn = new SimpleDateFormat("ddMMMyyyy");
        SimpleDateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd");
        String finalDate = null;
        Date date = null;
        try {
            finalDate = dfOut.format(dfIn.parse(tempDate));
            date = dfOut.parse(finalDate);
        } catch (ParseException ex) {
            System.out.println("Exception parsing date..." + ex);
        }
        return date;
    }

    /**
     * For AIR TRFP
     *
     * @param dateString
     * @return
     */
    public static Date refundDate(String dateString) {
        Date date = null;
        try {
            String d = dfOutput.format(dfInput1.parse(dateString));
            date = dfOutput.parse(d);
            return date;
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return date;
    }

    public static Date getBeginingOfMonth() {
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static Date getEndOfMonth() {
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static String dateToString(Date date){
     return dateToString(date,AppSettings.get("dateformat"));
    }
    
    public static Date stringToDate(String dateString){
     return stringToDate(dateString,AppSettings.get("dateformat"));
    }
    
    
    public static String dateToString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date stringToDate(String dateString, String dateStringFormat) {
        DateFormat dateFormat = new SimpleDateFormat(dateStringFormat);
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }    
}
