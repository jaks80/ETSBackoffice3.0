package com.ets.util;

/**
 *
 * @author Yusuf
 */
public class Enums {

    public enum UserType {

        MANAGER(0), SUPERVISOR(1), GENERALSALES(3);
        private int id;

        UserType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 0:
                    return MANAGER.toString();
                case 1:
                    return SUPERVISOR.toString();
                case 2:
                    return GENERALSALES.toString();
                default:
                    return null;
            }
        }
    }

    public enum ClientType {

        AGENT(0), CUSTOMER(1);
        private int id;

        ClientType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 0:
                    return AGENT.toString();
                case 1:
                    return CUSTOMER.toString();
                default:
                    return null;
            }
        }
    }

    public enum AIRType {

        BT(0), TTP(1), INV(2), TRFP(3);
        private int id;

        AIRType(int id) {
            this.id = id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 0:
                    return BT.toString();
                case 1:
                    return TTP.toString();
                case 2:
                    return INV.toString();
                case 3:
                    return TRFP.toString();
                default:
                    return null;
            }
        }
    }

    public enum TicketStatus {

        BOOK(0), ISSUE(1), REISSUE(2), REFUND(3), VOID(4);
        private int id;

        TicketStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 0:
                    return BOOK.toString();
                case 1:
                    return ISSUE.toString();
                case 2:
                    return REISSUE.toString();
                case 3:
                    return REFUND.toString();
                case 4:
                    return VOID.toString();
                default:
                    return null;
            }
        }
    }

    public enum AcDocType {

        INVOICE(0), PAYMENT(1), CREDITMEMO(2), DEBITMEMO(3), REFUND(4);
        private int id;

        AcDocType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 0:
                    return INVOICE.toString();
                case 1:
                    return PAYMENT.toString();
                case 2:
                    return CREDITMEMO.toString();
                case 3:
                    return DEBITMEMO.toString();
                case 4:
                    return REFUND.toString();
                default:
                    return null;
            }
        }
    }

    public enum AcDocStatus {

        ACTIVE(0), ARCHIVE(1), VOID(2);
        private int id;

        AcDocStatus(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 0:
                    return ACTIVE.toString();
                case 1:
                    return ARCHIVE.toString();
                case 2:
                    return VOID.toString();
                default:
                    return null;
            }
        }
    }

    public enum CalculationType {

        FIXED(0), VARIABLE(1), PERCENT(2);
        private int id;

        CalculationType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 0:
                    return FIXED.toString();
                case 1:
                    return VARIABLE.toString();
                case 2:
                    return PERCENT.toString();
                default:
                    return null;
            }
        }
    }

    public enum PaymentType {

        CASH(0), CHEQUE(1), CREDIT_CARD(2), DEBIT_CARD(3), BANKT_RANSFER(4), OTHER(5);
        private int id;

        PaymentType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 0:
                    return CASH.toString();
                case 1:
                    return CHEQUE.toString();
                case 2:
                    return CREDIT_CARD.toString();
                case 3:
                    return DEBIT_CARD.toString();
                case 4:
                    return BANKT_RANSFER.toString();
                case 5:
                    return OTHER.toString();
                default:
                    return null;
            }
        }
    }
}
