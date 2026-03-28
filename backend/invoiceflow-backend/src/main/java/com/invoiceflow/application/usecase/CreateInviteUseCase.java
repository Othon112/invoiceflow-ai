package com.invoiceflow.application.usecase;

import com.invoiceflow.domain.model.Invite;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.InviteRepository;
import com.invoiceflow.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import java.util.Set;

@ApplicationScoped
public class CreateInviteUseCase {

    private static final Set<String> ALLOWED_ROLES =
            Set.of("admin", "accountant", "reviewer", "viewer");

    @Inject
    UserRepository userRepository;

    @Inject
    InviteRepository inviteRepository;

    public Invite execute(String firebaseUid, String invitedEmail, String role) {
        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!"admin".equals(currentUser.getRole())) {
            throw new IllegalStateException("Only admins can invite users");
        }

        String normalizedRole = role.trim().toLowerCase();
        if (!ALLOWED_ROLES.contains(normalizedRole)) {
            throw new BadRequestException("Invalid role. Allowed: " + ALLOWED_ROLES);
        }

        return inviteRepository.create(
                currentUser.getCompanyId(),
                invitedEmail,
                normalizedRole,
                currentUser.getId()
        );
    }
}