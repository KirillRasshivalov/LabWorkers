package algo.demo.controllers;

import algo.demo.database.LabWorkTable;
import algo.demo.dto.LabWork;
import algo.demo.security.JwtUtil;
import algo.demo.services.LabWorkService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/labworks")
public class LabWorkController {

    private static final Logger logger = LoggerFactory.getLogger(LabWorkController.class);

    @Inject
    private LabWorkService labWorkService;

    @Inject
    private JwtUtil jwtUtil;


    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewLabWork(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken,
                                  LabWork labWork)
    {
        logger.info("Пришел запрос на добавление нового лаб ворка от пользователя: " + jwtUtil.extractUsername(jwtToken));
        try {
            LabWorkTable createdLabWork = labWorkService.addLabWork(labWork);
            logger.info("Лаб воркер успешно добавлен.");
            return Response.status(Response.Status.CREATED)
                    .entity(createdLabWork)
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время добавления лаб воркера" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLabWork(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken,
                                  @PathParam("id") Long id,
                                  LabWork labWork)
    {
        logger.info("Пришел запрос на обновление коллекции с id = " + id + '\n' +
                "От пользователя " + jwtUtil.extractUsername(jwtToken));
        try {
            LabWorkTable updatedLabWork = labWorkService.updateLabWork(id, labWork);
            logger.info("Лаб воркер успешно обновлен.");
            return Response.status(Response.Status.ACCEPTED)
                    .entity(updatedLabWork)
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время обновления лаб воркера." + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteLabWork(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken,
                                  @PathParam("id") Long id)
    {
        logger.info("Пришел запрос на удаление лаб воркера по id = " + id + '\n' +
                "От пользователя " + jwtUtil.extractUsername(jwtToken));
        try {
            labWorkService.deleteLabWork(id);
            logger.info("Лаб воркер успешно удален.");
            return Response.ok()
                    .entity("Успешно")
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время удаления лаб воркера. " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/showAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response shawAll() {
        logger.info("Пришел запрос на показ всех лаб воркеров.");
        try {
            List<LabWorkTable> allLabWorkers = labWorkService.getAllLabWorks();
            logger.info("Удалось успешно получить список всех лаб ворков.");
            return Response.ok()
                    .entity(allLabWorkers)
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка при показе лаб воркеров. " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLabWork(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken,
                               @PathParam("id") Long id) {
        logger.info("Пришел запрос на получение пользователя по id = " + id + '\n' +
                "От пользователя " + jwtUtil.extractUsername(jwtToken));
        try {
            LabWorkTable labWorkTable = labWorkService.getLabWorkById(id);
            logger.info("Удалось успешно получить лаь воркера.");
            return Response.ok()
                    .entity(labWorkTable)
                    .build();
        } catch (Exception e) {
            logger.info("Произошла ошибка при показе лаб воркера. " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
