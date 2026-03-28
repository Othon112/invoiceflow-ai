package com.invoiceflow.infrastructure.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.container.ContainerRequestContext;


@RequestScoped
public class FirebaseRequestContext {

    @Context
    ContainerRequestContext requestContext;

    public String uid() {
        Object v = requestContext.getProperty("firebase.uid");
        return v == null ? null : v.toString();
    }

    public String email() {
        Object v = requestContext.getProperty("firebase.email");
        return v == null ? null : v.toString();
    }
}