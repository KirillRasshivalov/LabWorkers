package algo.demo.controllers;

import algo.demo.dto.AuthResponse;
import algo.demo.dto.UserLoginRequest;
import algo.demo.exceptions.LoginException;
import algo.demo.exceptions.RegistrationException;
import algo.demo.security.JwtUtil;
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

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(UserLoginRequest userLoginRequest) {
        logger.info("Пришел запрос на регистрацию.");
        try {
            authorisationService.registerUser(userLoginRequest);
            String token = authorisationService.getToken(userLoginRequest);
            return Response.ok()
                    .entity(new AuthResponse(
                            token,
                            userLoginRequest.username(),
                            "Пользователь успешно зарегистрирован.")
                    )
                    .build();
        } catch (Exception e) {
            throw new RegistrationException(e.getMessage());
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response enterUser(UserLoginRequest userLoginRequest) {
        logger.info("Пришел запрос на авторизацию.");
        try {
            authorisationService.isUserCorrect(userLoginRequest);
            String token = authorisationService.getToken(userLoginRequest);
            return Response.ok()
                    .entity(new AuthResponse(
                            token,
                            userLoginRequest.username(),
                            "Пользователь подтвержден.")
                    )
                    .build();
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }
}
