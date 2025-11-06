package algo.demo.controllers;

import algo.demo.database.UsersTable;
import algo.demo.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/users")
@RolesAllowed("ADMIN")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Inject
    private UserService userService;

    @GET
    @Path("/get_all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken) {
        logger.info("Пришел запрос на получения всех пользователей системы от: "
                + userService.getUsername(jwtToken));
        try {
            List<UsersTable> users = userService.getAllUsers();
            return Response.ok()
                    .entity(users)
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
