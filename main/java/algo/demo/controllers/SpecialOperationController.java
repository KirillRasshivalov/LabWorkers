package algo.demo.controllers;

import algo.demo.database.CoordinatesTable;
import algo.demo.database.LabWorkTable;
import algo.demo.services.SpecialOperationsService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/operations")
public class SpecialOperationController {

    private static final Logger logger = LoggerFactory.getLogger(SpecialOperationController.class);

    @Inject
    private SpecialOperationsService specialOperationsService;

    @GET
    @Path("/middleValue")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculateMiddleValueOfMinPoint() {
        logger.info("Пришел запрос на получение среднего значения поля минимальной оценки.");
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
    public Response calculateMinCoordinates() {
        logger.info("Пришел запрос на получение коллекции с минимальными координатами.");
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
    public Response calculateMinCoordinatesXY() {
        logger.info("Пришел запрос на показ минимальных координат X и Y");
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
    public Response calculateUniqueMass() {
        logger.info("Пришел запрос на получения уникальных минимальных баллов.");
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
    public Response decreaseLabDifficulty(@PathParam("id") Long id, @QueryParam("steps") Integer steps) {
        logger.info("Пришел запрос на понижение сложности на " + steps);
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
