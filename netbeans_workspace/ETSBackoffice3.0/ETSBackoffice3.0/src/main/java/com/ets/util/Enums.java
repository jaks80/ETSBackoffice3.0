package com.ets.util;

/**
 *
 * @author OMAR
 */
public class Enums {

    public enum GDS {

        AMADEUS(1), GALILEO(2), WORLDSPAN(3), SABRE(4);
        private int id;

        GDS(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 1:
                    return AMADEUS.toString();
                case 2:
                    return GALILEO.toString();
                case 3:
                    return WORLDSPAN.toString();
                case 4:
                    return SABRE.toString();
                default:
                    return null;
            }
        }
    }

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

}
