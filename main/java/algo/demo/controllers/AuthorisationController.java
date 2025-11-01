package algo.demo.controllers;

import algo.demo.dto.AuthResponse;
import algo.demo.dto.UserLoginRequest;
import algo.demo.jwt.JwtUtil;
import algo.demo.services.AuthorisationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/auth")
public class AuthorisationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorisationController.class);

    @Inject
    private AuthorisationService authorisationService;

    @Inject
    private JwtUtil jwtUtil;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(UserLoginRequest userLoginRequest) {
        logger.info("Пришел запрос на регистрацию.");
        if (!authorisationService.isDataCorrect(userLoginRequest)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new AuthResponse(
                            null,
                            null,
                            "Неверные данные для регистрации.")
                    )
                    .build();
        }
        try {
            authorisationService.registerUser(userLoginRequest);
            String token = jwtUtil.generateToken(userLoginRequest.username());
            return Response.ok()
                    .entity(new AuthResponse(
                            token,
                            userLoginRequest.username(),
                            "Пользователь успешно зарегистрирован.")
                    )
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse(
                            null,
                            null,
                            e.getMessage())
                    )
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response enterUser(UserLoginRequest userLoginRequest) {
        logger.info("Пришел запрос на авторизацию.");
        if (!authorisationService.isDataCorrect(userLoginRequest)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Неверные данные для входа.")
                    .build();
        }
        try {
            if (authorisationService.isUserCorrect(userLoginRequest)) {
                String token = jwtUtil.generateToken(userLoginRequest.username());
                return Response.ok()
                        .entity(new AuthResponse(
                                token,
                                userLoginRequest.username(),
                                "Пользователь подтвержден.")
                        )
                        .build();
            }
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(new AuthResponse(
                            null,
                            null,
                            "Неверный пароль или имя пользователя.")
                    )
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AuthResponse(
                            null,
                            null,
                            e.getMessage())
                    )
                    .build();
        }
    }
}
