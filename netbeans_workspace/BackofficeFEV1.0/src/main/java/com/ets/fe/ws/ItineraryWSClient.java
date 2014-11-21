package com.ets.fe.ws;


import com.ets.fe.model.report.sales.SegmentReport;
import com.ets.util.APIConfig;
import com.ets.util.DateUtil;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class ItineraryWSClient {

    public SegmentReport segmentReport(String ticketStatus, String careerCode,
            Date issueDateFrom, Date issueDateTo, String officeId) {

        String dateStart = DateUtil.dateToString(issueDateFrom, "ddMMMyyyy");
        String dateEnd = DateUtil.dateToString(issueDateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.itinerary.segment-history")
                + "?ticketStatus=" + ticketStatus + "&careerCode=" + careerCode
                + "&dateStart=" + dateStart + "&dateEnd=" + dateEnd + "&officeId=" + officeId;

        SegmentReport report = new SegmentReport();
        report = RestClientUtil.getEntity(SegmentReport.class, url, report);
        return report;
    }
}
