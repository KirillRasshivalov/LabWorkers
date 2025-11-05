package algo.demo.controllers;

import algo.demo.services.FileDumpService;
import algo.demo.services.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

@Path("/files")
public class FileDumpController {

    private static final Logger logger = LoggerFactory.getLogger(FileDumpController.class);

    @Inject
    private UserService userService;

    @Inject
    private FileDumpService fileDumpService;

    @POST
    @Path("/upload-script")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addFile(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken,
                            @FormDataParam("file") InputStream fileInputStream) {
        logger.info("Пришел запрос на добавление данных из файла. От юзера: " + userService.getUsername(jwtToken));
        try {
            fileDumpService.parseFile(fileInputStream, userService.getUsername(jwtToken));
            logger.info("Файл успешно распаршен, данные добавлены в бд.");
            return Response.ok()
                    .entity("Данные успешно добавлены.")
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка в контроллере парсера лаб ворков: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
