package com.amadeus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class DateConverter {

    private static final SimpleDateFormat dfInput = new SimpleDateFormat("yyMMdd");
    private static final SimpleDateFormat dfOutput = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dfInput1 = new SimpleDateFormat("ddMMMyy");
    private static Calendar cal = Calendar.getInstance();

    public static Date yyMMddToDate(String yyMMdd) throws ParseException {
        return dfOutput.parse(dfOutput.format(dfInput.parse(yyMMdd)));
    }

    public static Date ddmmToDate(String ddmm) {
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
            System.out.println("Exception parsing date..."+ex);
        }
        return date;
    }
    
    public static Date refundDate(String dateString) throws ParseException {

        String d = dfOutput.format(dfInput1.parse(dateString));
        Date date = dfOutput.parse(d);
        return date;
    }
}
