package algo.demo.controllers;

import algo.demo.database.CoordinatesTable;
import algo.demo.database.LabWorkTable;
import algo.demo.security.JwtUtil;
import algo.demo.services.SpecialOperationsService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/operations")
@RolesAllowed({"ADMIN", "USER"})
public class SpecialOperationController {

    private static final Logger logger = LoggerFactory.getLogger(SpecialOperationController.class);

    @Inject
    private SpecialOperationsService specialOperationsService;

    @Inject
    private JwtUtil jwtUtil;

    @GET
    @Path("/middleValue")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateMiddleValueOfMinPoint(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken) {
        logger.info("Пришел запрос на получение среднего значения поля минимальной оценки. От пользователя " +
                jwtUtil.extractUsername(jwtToken));
        try {
            Double middleValue = specialOperationsService.middleValueOfMinPoint();
            logger.info("Успешно найдено медианное знаыение.");
            return Response.ok()
                    .entity(middleValue)
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время вычисления среднего значения.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/minCoordinates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateMinCoordinates(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken) {
        logger.info("Пришел запрос на получение коллекции с минимальными координатами. От пользователя " +
                jwtUtil.extractUsername(jwtToken));
        try {
            LabWorkTable labWorkWithMinCoordinates = specialOperationsService.getLabWorkByMinId();
            logger.info("Успешно найдена коллекция с минимальной координатой.");
            return Response.ok()
                    .entity(labWorkWithMinCoordinates)
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время вычисления коллекции с минимальной координатой.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/minCoordinatesXY")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateMinCoordinatesXY(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken) {
        logger.info("Пришел запрос на показ минимальных координат X и Y. От пользовтаеля: "
                + jwtUtil.extractUsername(jwtToken));
        try {
            CoordinatesTable coordinatesTableMin = specialOperationsService.getMinCoordinates();
            logger.info("Успешно получена минимальная координата.");
            return Response.ok()
                    .entity(coordinatesTableMin)
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время вычисления минимальной координаты.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/uniqueMass")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateUniqueMass(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken) {
        logger.info("Пришел запрос на получения уникальных минимальных баллов. От пользователя: " +
                jwtUtil.extractUsername(jwtToken));
        try {
            List<Integer> unicMinimalPoints = specialOperationsService.getUnicMass();
            logger.info("Успешно получен массив уникальных данных.");
            return Response.ok()
                    .entity(unicMinimalPoints)
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время вычисления массива.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/decrease/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response decreaseLabDifficulty(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken,
                                          @PathParam("id") Long id,
                                          @QueryParam("steps") Integer steps)
    {
        logger.info("Пришел запрос на понижение сложности на " + steps + ". От пользователя: " +
                jwtUtil.extractUsername(jwtToken));
        try {
            specialOperationsService.decreaseDifficultInLabWork(id, steps);
            logger.info("Уровень успешно понижен.");
            return Response.ok()
                    .entity("Все четко.")
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка во время уменьшения сложности " + e.getMessage());
            return Response.
                    status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
