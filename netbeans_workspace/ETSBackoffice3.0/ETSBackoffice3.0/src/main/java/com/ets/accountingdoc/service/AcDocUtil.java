package com.ets.accountingdoc.service;

import com.ets.util.DateUtil;
import java.util.Objects;

/**
 *
 * @author Yusuf
 */
public class AcDocUtil {

    public static Integer generateAcDocRef(Integer lastInvRef) {

        Integer lastInvPrefix = 0, currentYear = DateUtil.getYY();
        String finalAcDocRef = "";

        lastInvPrefix = lastInvPrefix = lastInvRef / 1000000000;

        if (lastInvRef == 0 || !Objects.equals(currentYear, lastInvPrefix)) {
            finalAcDocRef = currentYear.toString() + "000000001";//Starting invref from YY000001
        } else {
            finalAcDocRef = String.valueOf(++lastInvRef);
        }
        return Integer.valueOf(finalAcDocRef);
    }
}
