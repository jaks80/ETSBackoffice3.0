package etsbackoffice.businesslogic;

/**
 *
 * @author OMAR
 */
public class Enums {

    public enum VATType {

        NOVAT, VAT
    }

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

    public enum AccountingDocumentType {
        
        INVOICE(1), RFDCNOTE(2), CNOTE(3), DNOTE(4);
        private int id;

        AccountingDocumentType(int id) {
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
                    return RFDCNOTE.toString();
                case 3:
                    return CNOTE.toString();
                case 4:
                    return DNOTE.toString();
                default:
                    return null;
            }
        }
    }

    public enum TransType {

        CASH(1), CHEQUE(2), CREDITCARD(3), DEBITCARD(4), BANKDEPOSIT(5),
        ONLINETRANSFER(6), BALANCETRANSFER(7), DIRECT_DEBIT(8), BAC(9), DEBIT(10), CREDIT(11), OTHER(12);
        private int id;

        TransType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static String valueOf(int id) {
            switch (id) {
                case 1:
                    return CASH.toString();
                case 2:
                    return CHEQUE.toString();
                case 3:
                    return CREDITCARD.toString();
                case 4:
                    return DEBITCARD.toString();
                case 5:
                    return BANKDEPOSIT.toString();
                case 6:
                    return ONLINETRANSFER.toString();
                case 7:
                    return BALANCETRANSFER.toString();
                case 8:
                    return DIRECT_DEBIT.toString();
                case 9:
                    return BAC.toString();
                case 10:
                    return DEBIT.toString();
                case 11:
                    return CREDIT.toString();
                default:
                    return OTHER.toString();
            }
        }
    }
}
