package com.ets.fe.util;

/**
 *
 * @author OMAR
 */
public class Enums {

    public enum AIRType {

        BT(1), TTP(2), INV(3), TRFP(4);
        private int id;

        AIRType(int id) {
            this.id = id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 1:
                    return BT.toString();
                case 2:
                    return TTP.toString();
                case 3:
                    return INV.toString();
                case 4:
                    return TRFP.toString();
                default:
                    return null;
            }
        }
    }

    public enum TicketStatus {

        BOOK(1), ISSUE(2), REISSUE(3), REFUND(4), VOID(5);
        private int id;

        TicketStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 1:
                    return BOOK.toString();
                case 2:
                    return ISSUE.toString();
                case 3:
                    return REISSUE.toString();
                case 4:
                    return REFUND.toString();
                case 5:
                    return VOID.toString();
                default:
                    return null;
            }
        }
    }

    public enum AcDocType {

        INVOICE(1), PAYMENT(2), CREDITMEMO(3), DEBITMEMO(4), REFUND(5);
        private int id;

        AcDocType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 1:
                    return INVOICE.toString();
                case 2:
                    return PAYMENT.toString();
                case 3:
                    return CREDITMEMO.toString();
                case 4:
                    return DEBITMEMO.toString();
                case 5:
                    return REFUND.toString();
                default:
                    return null;
            }
        }
    }
}
