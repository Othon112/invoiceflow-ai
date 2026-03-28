package com.invoiceflow.application.service;

import com.invoiceflow.domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;

import java.util.Set;

@ApplicationScoped
public class AuthorizationService {
    public void requireAnyRole(User user, String... roles){
        if (user == null){
            throw new ForbiddenException("User not authenticated");
        }

        if (user.getRole() == null) {
            throw new ForbiddenException("User has no role assigned");
        }

        Set<String> allowed = Set.of(roles);

        if ( !allowed.contains(user.getRole())){
            throw new ForbiddenException("Insufficient permissions. Required one of: " +  allowed + ", but was: " + user.getRole());
        }
    }

    public void requireAdmin(User user){
        requireAnyRole(user, "admin");
    }

    public void requireAccountantOrAdmin(User user){
        requireAnyRole(user, "admin", "accountant");
    }

    public void requireReviewerOrAdmin(User user){
        requireAnyRole(user, "admin", "reviewer");
    }

    public void requireReadAccess(User user){
        requireAnyRole(user, "admin", "accountant",  "reviewer", "viewer");
    }
}
