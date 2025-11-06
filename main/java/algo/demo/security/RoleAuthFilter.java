package algo.demo.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RoleAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (isPublicPath(requestContext.getUriInfo().getPath())) {
            return;
        }

        Method resourceMethod = getResourceMethod(requestContext);
        if (resourceMethod != null) {
            jakarta.annotation.security.RolesAllowed rolesAllowed =
                    resourceMethod.getAnnotation(jakarta.annotation.security.RolesAllowed.class);

            if (rolesAllowed != null) {
                boolean hasAccess = false;
                for (String requiredRole : rolesAllowed.value()) {
                    if (requestContext.getSecurityContext().isUserInRole(requiredRole)) {
                        hasAccess = true;
                        break;
                    }
                }
                if (!hasAccess) {
                    abortWithForbidden(requestContext, "Insufficient permissions");
                }
            }
        }
    }

    private boolean isPublicPath(String path) {
        String[] PUBLIC_PATHS = {"/auth/login", "/auth/register"};
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }

    private Method getResourceMethod(ContainerRequestContext context) {
        try {
            return (Method) context.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        } catch (Exception e) {
            return null;
        }
    }

    private void abortWithForbidden(ContainerRequestContext context, String message) {
        context.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}
