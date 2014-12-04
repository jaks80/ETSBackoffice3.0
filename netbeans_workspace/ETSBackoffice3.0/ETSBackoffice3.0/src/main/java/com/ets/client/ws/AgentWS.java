package com.ets.client.ws;

import com.ets.client.collection.Agents;
import com.ets.client.domain.Agent;
import com.ets.client.service.AgentService;
import java.util.List;
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
@Path("/agent-management")
@Consumes("application/xml")
@Produces("application/xml")
public class AgentWS {

    @Autowired
    AgentService service;

    @GET
    @Path("/agents")
    public Agents find(@QueryParam("name") String name,
            @QueryParam("postCode") String postCode,
            @QueryParam("officeID") String officeID) {

        List<Agent> list = service.findAll(name, postCode, officeID);
        Agents agents = new Agents();
        agents.setList(list);
        return agents;
    }

    @POST
    @Path("/new")
    public Agent create(Agent agent) {
        return service.saveorUpdate(agent);
    }

    @PUT
    @Path("/update")
    public Agent update(Agent agent) {
        return service.saveorUpdate(agent);
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") long id) {
        return Response.status(200).build();
    }
}
