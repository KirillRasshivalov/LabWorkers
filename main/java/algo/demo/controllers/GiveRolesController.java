package algo.demo.controllers;

import algo.demo.database.UsersTable;
import algo.demo.security.JwtUtil;
import algo.demo.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/role")
@RolesAllowed("ADMIN")
public class GiveRolesController {

    private static final Logger logger = LoggerFactory.getLogger(GiveRolesController.class);

    @Inject
    private UserService userService;

    @POST
    @Path("/assign")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response assignRole(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken, UsersTable usersTable) {
        logger.info("Пришел запрос на выдачу прав доступа для юзера: " + usersTable.getName() +
                " От админа: " + userService.getUsername(jwtToken) );
        try {
            userService.assignRoleToUser(usersTable.getName());
            logger.info("Роль успешно выдана.");
            return Response.ok()
                    .entity("Роль успешно выдана.")
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время выдачи роли.");
            throw new RuntimeException(e.getMessage());
        }
    }
}
