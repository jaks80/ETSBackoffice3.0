package com.ets.accountingdoc.service;

import com.ets.util.DateUtil;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 *
 * @author Yusuf
 */
public class DocumentUtil {
    
    public Integer generateAcDocRef() {

        Integer lastInvRef = 0, lastInvPrefix = 0, currentYear = DateUtil.getYY();
        String finalAcDocRef = "";    

        lastInvRef = 0;//getAcDocDao().getMaxAcDocRef();
        lastInvPrefix = lastInvPrefix = lastInvRef / 1000000;

        if (lastInvRef == 0 || !Objects.equals(currentYear, lastInvPrefix)) {
            finalAcDocRef = currentYear.toString() + "000001";//Starting invref from YY000001    
        } else {
            finalAcDocRef = String.valueOf(++lastInvRef);
        }
        return Integer.valueOf(finalAcDocRef);
    }
}
