package com.ets.fe.pnr.ws;

import com.ets.fe.report.model.SegmentReport;
import com.ets.fe.util.RestClientUtil;
import com.ets.fe.APIConfig;
import com.ets.fe.pnr.model.Itinerary;
import com.ets.fe.util.DateUtil;
import java.util.Date;

/**
 *
 * @author Yusuf
 */
public class ItineraryWSClient {

    public Itinerary update(Itinerary segment) {
        segment.recordUpdateBy();

        String url = APIConfig.get("ws.itinerary.update");
        segment = RestClientUtil.putEntity(Itinerary.class, url, segment);
        return segment;
    }

    public SegmentReport segmentReport(String ticketStatus, String airLineCode,
            Date issueDateFrom, Date issueDateTo, String officeId) {

        String dateStart = DateUtil.dateToString(issueDateFrom, "ddMMMyyyy");
        String dateEnd = DateUtil.dateToString(issueDateTo, "ddMMMyyyy");

        String url = APIConfig.get("ws.itinerary.segment-history")
                + "?ticketStatus=" + ticketStatus + "&airLineCode=" + airLineCode
                + "&dateStart=" + dateStart + "&dateEnd=" + dateEnd + "&officeId=" + officeId;

        SegmentReport report = new SegmentReport();
        report = RestClientUtil.getEntity(SegmentReport.class, url, report);
        return report;
    }
}
