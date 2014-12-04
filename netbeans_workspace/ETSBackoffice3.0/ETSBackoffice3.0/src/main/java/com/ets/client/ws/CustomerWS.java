package com.ets.client.ws;

import com.ets.client.collection.Customers;
import com.ets.client.domain.Customer;
import com.ets.client.service.CustomerService;
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
@Path("/customer-management")
@Consumes("application/xml")
@Produces("application/xml")
public class CustomerWS {

    @Autowired
    CustomerService service;

    @GET
    @Path("/customers")
    public Customers find(@QueryParam("surName") String surName,
            @QueryParam("foreName") String foreName,
            @QueryParam("postCode") String postCode,
            @QueryParam("telNo") String telNo) {

        List<Customer> list = service.findAll(surName, foreName, postCode, telNo);
        Customers customers = new Customers();
        customers.setList(list);
        return customers;
    }

    @POST
    @Path("/new")
    public Customer create(Customer customer) {

        return service.saveorUpdate(customer);
    }

    @PUT
    @Path("/update")
    public Customer update(Customer customer) {
        return service.saveorUpdate(customer);
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") long id) {
        return Response.status(200).build();
    }
}
