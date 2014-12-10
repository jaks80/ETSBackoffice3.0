package com.ets.accountingdoc.service;

/**
 *
 * @author Yusuf
 */
public class AcDocUtil {

    public static Long generateAcDocRef(Long lastInvRef) {
        if (lastInvRef != null) {
            return ++lastInvRef;
        } else {
            return Long.valueOf("1001");
        }
    }

//    public static AccountingDocumentLine createNewLine(String title,
//            String remark,
//            BigDecimal discount, int qty,
//            T entity) {
//        AccountingDocumentLine newLine = AccountingDocumentLine();
//        newLine.setRemark(remark);
//        newLine.setQty(qty);
//        newLine.setDiscount(discount);
//
//        if (entity instanceof OtherService) {
//            newLine.setOtherService(entity);
//        } else if (entity instanceof AdditionalCharge) {
//            newLine.AdditionalCharge(entity);
//        }
//    }
}
