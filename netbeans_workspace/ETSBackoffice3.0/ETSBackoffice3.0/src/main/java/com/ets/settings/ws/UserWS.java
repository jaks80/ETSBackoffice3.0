package com.ets.settings.ws;

import com.ets.settings.domain.User;
import com.ets.settings.domain.Users;
import com.ets.settings.service.UserService;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Yusuf
 */
@Controller
@Path("/user-management")
@Consumes("application/xml")
@Produces("application/xml")
public class UserWS {

    @Autowired
    UserService service;

    @GET
    @Path("/login")
    @PermitAll
    public User login(@QueryParam("loginId") String loginId,
            @QueryParam("password") String password,
            @QueryParam("newPassword") String newPassword) {

        User user = service.login(loginId, password, newPassword);
        return user;
    }

    @POST
    @Path("/logout")
    @PermitAll
    public Response logout(User user) {
        return Response.status(200).build();
    }

    @GET
    @Path("/users")
    @RolesAllowed("SM")
    public Users find() {
        List<User> list = service.findAll();
        Users users = new Users();
        users.setList(list);
        return users;
    }

    @POST
    @Path("/new")
    @RolesAllowed("SU")
    public User create(User user) {
        return service.saveorUpdate(user);
    }

    @PUT
    @Path("/update")
    @RolesAllowed("AD")
    public User update(User user) {
        return service.saveorUpdate(user);
    }
}
