package algo.demo.exceptions;

import algo.demo.dto.AuthResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof LoginException || exception instanceof RegistrationException) {
            return Response.status(Response.Status.UNAUTHORIZED).
                    entity(new AuthResponse(null, null, exception.getMessage())).
                    build();
        }
        return Response.status(Response.Status.BAD_REQUEST).
                entity(exception.getMessage()).
                build();
    }
}
