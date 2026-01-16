package algo.demo.controllers;

import algo.demo.dto.StatisticInfo;
import algo.demo.exceptions.DefaultException;
import algo.demo.services.SpecialOperationsService;
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

@Path("/statistics")
@RolesAllowed({"USER", "ADMIN"})
public class WorkersStatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(WorkersStatisticsController.class);

    @Inject
    private SpecialOperationsService specialOperationsService;

    @Inject
    private UserService userService;

    @GET
    @Path("/main_info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken) {
        logger.info("Пришел запрос на показ статистики от пользователя: " +
                userService.getUsername(jwtToken));
        try {
            StatisticInfo statisticInfo = specialOperationsService.collectStatistics(userService.getUsername(jwtToken));
            return Response.ok()
                    .entity(statisticInfo)
                    .build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DefaultException(e.getMessage());
        }
    }
}
