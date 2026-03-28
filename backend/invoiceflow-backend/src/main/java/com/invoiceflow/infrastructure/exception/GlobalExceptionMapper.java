package com.invoiceflow.infrastructure.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {

        if (exception instanceof WebApplicationException webEx) {
            return build(
                    webEx.getResponse().getStatus(),
                    webEx.getClass().getSimpleName(),
                    webEx.getMessage()
            );
        }

        if (exception instanceof IllegalArgumentException) {
            return build(400, "BadRequest", exception.getMessage());
        }

        if (exception instanceof IllegalStateException) {
            return build(409, "Conflict", exception.getMessage());
        }

        return build(500, "InternalServerError", "Unexpected server error");
    }

    private Response build(int status, String error, String message) {
        ErrorResponse body = new ErrorResponse(
                status,
                error,
                message,
                OffsetDateTime.now().toString()
        );

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }

    public record ErrorResponse(
            int status,
            String error,
            String message,
            String timestamp
    ) {}
}