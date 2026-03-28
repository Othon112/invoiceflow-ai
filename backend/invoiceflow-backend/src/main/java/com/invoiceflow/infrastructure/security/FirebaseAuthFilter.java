package com.invoiceflow.infrastructure.security;

import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class FirebaseAuthFilter implements ContainerRequestFilter {

    @Inject
    FirebaseTokenVerifier tokenVerifier;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        if (isPublicPath(path)) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortUnauthorized(requestContext);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            abortUnauthorized(requestContext);
            return;
        }

        try {
            FirebaseToken decoded = tokenVerifier.verify(token);
            requestContext.setProperty("firebase.uid", decoded.getUid());
            requestContext.setProperty("firebase.email", decoded.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
            abortUnauthorized(requestContext);
        }
    }

    private boolean isPublicPath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.equals("/health") || path.startsWith("/health/")) {
            return true;
        }

        if (path.startsWith("/q/")) {
            return true;
        }

        return false;
    }

    private void abortUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build()
        );
    }
}
