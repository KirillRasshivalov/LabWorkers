package algo.demo.security;

import algo.demo.enums.Roles;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private TokenBlacklist tokenBlacklist;

    private static final String[] PUBLIC_PATHS = {
            "/auth/login",
            "/auth/register"
    };

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();


        if (isPublicPath(path)) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }
        String token = authHeader.substring("Bearer ".length());
        if (tokenBlacklist.isBlacklisted(token)) {
            abortWithUnauthorized(requestContext, "Token revoked");
            return;
        }
        if (!jwtUtil.isTokenValid(token)) {
            abortWithUnauthorized(requestContext, "Invalid or expired token");
            return;
        }
        String username;
        Roles role;
        try {
            username = jwtUtil.extractUsername(token);
            role = jwtUtil.extractRole(token);
        } catch (Exception e) {
            abortWithUnauthorized(requestContext, "Failed to extract user from token");
            return;
        }
        setSecurityContext(requestContext, username, role);
    }

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }

    private void abortWithUnauthorized(ContainerRequestContext context, String message) {
        context.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }

    private void setSecurityContext(ContainerRequestContext context, String username, Roles role) {
        final SecurityContext currentSecurityContext = context.getSecurityContext();

        context.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> username;
            }

            @Override
            public boolean isUserInRole(String requiredRole) {
                return role.name().equals(requiredRole);
            }

            @Override
            public boolean isSecure() {
                return currentSecurityContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        });
    }
}
