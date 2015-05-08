package com.ets.fe.accounts.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class AccountsReportFactoryBean {
    
    private static List<AccountsReport> accountsline = new ArrayList<>();

    public static Collection getBeanCollection() {
        return getAccountsline();
    }

    public static List<AccountsReport> getAccountsline() {
        return accountsline;
    }   
}
