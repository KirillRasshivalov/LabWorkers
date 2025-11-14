package algo.demo.controllers;

import algo.demo.database.ImportHistoryTable;
import algo.demo.services.HistoryService;
import algo.demo.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/history")
@RolesAllowed({"ADMIN", "USER"})
public class ShowHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(ShowHistoryController.class);

    @Inject
    private UserService userService;

    @Inject
    private HistoryService historyService;

    @GET
    @Path("/files_dump")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response filesDumpHistory(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken) {
        logger.info("Пришел запрос на показ всех файлов которые были импортированы в программу." +
                " От пользовтаеля: " + userService.getUsername(jwtToken));
        try {
            List<ImportHistoryTable> listOfDumps = historyService.getHistoryOfImports(jwtToken);
            return Response.ok()
                    .entity(listOfDumps)
                    .build();
        } catch (Exception e) {
            logger.error("Прооизошла ошибка во время показа импортов: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
