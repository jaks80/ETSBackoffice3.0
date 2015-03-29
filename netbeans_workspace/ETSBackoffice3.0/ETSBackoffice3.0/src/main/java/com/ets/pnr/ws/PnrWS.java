package com.ets.pnr.ws;

import com.ets.pnr.model.collection.Pnrs;
import com.ets.pnr.domain.Pnr;
import com.ets.pnr.model.ATOLCertificate;
import com.ets.pnr.service.PnrService;
import com.ets.util.DateUtil;
import java.util.Date;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/pnr-management")
@Consumes("application/xml")
@Produces("application/xml")
public class PnrWS {

    @Autowired
    private PnrService service;

    @POST
    @Path("/new")
    @RolesAllowed("GS")
    public Pnr create(Pnr pnr) {
        service.save(pnr);
        return pnr;
    }

    @PUT
    @Path("/update")
    @RolesAllowed("GS")
    public Pnr update(Pnr pnr) {
        service.save(pnr);
        return pnr;
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("SM")
    public Response delete(@PathParam("id") long id) {

        if (service.delete(id)) {
            return Response.status(200).build();
        } else {
            return Response.status(500).build();
        }
    }

    @GET
    @Produces("application/xml")
    @Path("/history")
    @RolesAllowed("SM")
    public Pnrs getHistory(@QueryParam("bookingAgtOid") String bookingAgtOid,
            @QueryParam("ticketingAgtOid") String ticketingAgtOid,
            @QueryParam("dateStart") String dateStart,
            @QueryParam("dateEnd") String dateEnd) {
        Pnrs pnrs = service.pnrHistory(dateStart, dateEnd, ticketingAgtOid, bookingAgtOid);
        return pnrs;
    }

    @GET
    @Produces("application/xml")
    @Path("/byid/{id}")
    @RolesAllowed("GS")
    public Pnr getById(@PathParam("id") long id) {
        return service.getByIdWithChildren(id);
    }

    @GET
    @Produces("application/xml")
    @Path("/atolcertbyid")
    //@RolesAllowed("GS")
    @PermitAll
    public ATOLCertificate getAtolCertificate(@QueryParam("id") long id, @QueryParam("issuedate") String issuedate) {
        Date date = DateUtil.stringToDate(issuedate, "ddMMMyyyy");
        return service.getAtolCertificate(id, date);
    }

    @GET
    @Produces("application/xml")
    @Path("/withchildren/{id}")
    @RolesAllowed("GS")
    public Pnr getByIdWithChildren(@PathParam("id") long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GET
    @Produces("application/xml")
    @Path("/bytkt/{tktNo}/{surName}")
    @RolesAllowed("GS")
    public Pnrs getPnrByTktNo(@QueryParam("tktNo") String tktNo, @QueryParam("surName") String surName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @GET
    @Produces("application/xml")
    @Path("/bypaxname")
    @RolesAllowed("GS")
    public Pnrs getPnrByName(@QueryParam("surName") String surName, @QueryParam("foreName") String foreName) {
        List<Pnr> list = service.getPnrByName(surName, foreName);
        Pnrs pnrs = new Pnrs();
        pnrs.setList(list);
        return pnrs;
    }

    @GET
    @Produces("application/xml")
    @Path("/bygdsPnr")
    @RolesAllowed("GS")
    public Pnrs getPnrBygdsPnr(@QueryParam("gdsPnr") String gdsPnr) {
        List<Pnr> list = service.getByGDSPnr(gdsPnr);
        Pnrs pnrs = new Pnrs();
        pnrs.setList(list);
        return pnrs;
    }

    @GET
    @Produces("application/xml")
    @Path("/uninvoicedpnr")
    @RolesAllowed("GS")
    public Pnrs getUninvoicedPnr() {

        List<Pnr> list = service.searchUninvoicedPnr();
        Pnrs pnrs = new Pnrs();
        pnrs.setList(list);

        return pnrs;
    }

    @GET
    @Produces("application/xml")
    @Path("/pnrtoday")
    @RolesAllowed("GS")
    public Pnrs getPnrsToday(@QueryParam("date") String date) {

        List<Pnr> list = service.searchPnrsToday(date);
        Pnrs pnrs = new Pnrs();
        pnrs.setList(list);

        return pnrs;
    }
}
