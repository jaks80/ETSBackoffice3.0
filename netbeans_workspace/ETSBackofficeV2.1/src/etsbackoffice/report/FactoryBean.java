package etsbackoffice.report;

/**
 *
 * @author Yusuf
 */
import etsbackoffice.domain.AccountingDocument;
import java.util.*;

public class FactoryBean {

    public static List createBeanCollection() {
        List coll = new ArrayList();
        AccountingDocument acDoc = new AccountingDocument();
        acDoc.setAcDocRef(001);
        coll.add(acDoc);
        return coll;
    }
}
