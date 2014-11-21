package com.ets.service.pnr;

import com.ets.dao.pnr.ItineraryDAO;
import com.ets.domain.pnr.Itinerary;
import com.ets.domain.pnr.Ticket;
import com.ets.model.report.SegmentReport;
import com.ets.util.DateUtil;
import com.ets.util.Enums;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author Yusuf
 */
@Service("itineraryService")
public class ItineraryService {

    @Resource(name = "itineraryDAO")
    private ItineraryDAO dao;

    public SegmentReport segmentReport(String ticketStatus,
            String airLineCode, String from, String to, String officeId) {

        Integer status = null;
        String[] officeIds = null;
        String[] airLineCodes = null;

        if (!"null".equals(ticketStatus) && ticketStatus != null) {
            status = Enums.TicketStatus.valueOf(ticketStatus).getId();
        }

        if ("null".equals(airLineCode) || airLineCode == null) {
            airLineCode = null;
        } else {
            airLineCodes = airLineCode.split(",");
        }

        if ("null".equals(officeId) || officeId == null) {
            officeId = null;
        } else {
            officeIds = officeId.split(",");
        }

        Date dateFrom = DateUtil.stringToDate(from, "ddMMMyyyy");
        Date dateTo = DateUtil.stringToDate(to, "ddMMMyyyy");
        List results = dao.findSegments(status, airLineCodes, dateFrom, dateTo, officeIds);

        SegmentReport report = new SegmentReport();
        
         for (int i = 0; i < results.size(); i++) {
            Object[] object = (Object[]) results.get(i);
            
            Ticket ticket = (Ticket) object[1];
            Itinerary segment = (Itinerary) object[0];                            
            report.addSegment(segment, segment.getPnr(), ticket);
        }
        report.generateSummery();
        return report;
    }
}
