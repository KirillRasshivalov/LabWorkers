package algo.demo.controllers;

import algo.demo.database.ImportHistoryTable;
import algo.demo.repository.FileHistoryRepository;
import algo.demo.services.FileDumpService;
import algo.demo.services.MinioService;
import algo.demo.services.UserService;
import jakarta.annotation.security.RolesAllowed;
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
@RolesAllowed({"USER", "ADMIN"})
public class FileDumpController {

    private static final Logger logger = LoggerFactory.getLogger(FileDumpController.class);

    @Inject
    private UserService userService;

    @Inject
    private FileDumpService fileDumpService;

    @Inject
    private FileHistoryRepository fileHistoryRepository;

    @Inject
    private MinioService minioService;

    @POST
    @Path("/upload-script")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addFile(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken,
                            @FormDataParam("file") InputStream fileInputStream) {
        logger.info("Пришел запрос на добавление данных из файла. От юзера: " + userService.getUsername(jwtToken));
        try {
            String fileName = userService.getUsername(jwtToken) + "_upload_file";
            fileDumpService.parseFile(fileInputStream, userService.getUsername(jwtToken), fileName);
            logger.info("Файл успешно распаршен, данные добавлены в бд.");
            return Response.ok()
                    .entity("Данные успешно добавлены.")
                    .build();
        } catch (Exception e) {
            logger.error("Произошла ошибка в контроллере парсера лаб ворков: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @GET
    @Path("/download/{historyId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@HeaderParam(HttpHeaders.AUTHORIZATION) String jwtToken,
                                 @PathParam("historyId") Long historyId) {
        try {
            ImportHistoryTable history = fileHistoryRepository.findById(historyId);
            if (history == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Запись истории не найдена")
                        .build();
            }
            String username = userService.getUsername(jwtToken);
            if (!username.equals(history.getUser()) && !userService.isUserAdmin(username)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("Нет доступа к файлу")
                        .build();
            }
            InputStream fileStream = minioService.downloadFile(history.getFileObjectName());
            return Response.ok(fileStream)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + history.getFileName() + "\"")
                    .build();
        } catch (Exception e) {
            logger.error("Ошибка при скачивании файла: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ошибка при скачивании файла")
                    .build();
        }
    }
}
