package com.ets.fe.productivity.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class ProductivityReportFactroyBean {

    private static List<ProductivityReport> reports = new ArrayList<>();

    public static Collection getBeanCollection() {
        return reports;
    }

    public static void setReports(List<ProductivityReport> aReports) {
        reports = aReports;
    }   
}
