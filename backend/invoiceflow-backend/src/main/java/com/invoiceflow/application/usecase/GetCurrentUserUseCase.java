package com.invoiceflow.application.usecase;

import com.invoiceflow.domain.model.Invite;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.InviteRepository;
import com.invoiceflow.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class GetCurrentUserUseCase {

    @Inject
    UserRepository userRepository;

    @Inject
    InviteRepository inviteRepository;

    @Transactional
    public User execute(String firebaseUid, String email) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseGet(() -> provisionUserFromInvite(firebaseUid, email));
    }

    private User provisionUserFromInvite(String firebaseUid, String email) {
        Invite invite = inviteRepository.findPendingByEmail(email)
                .orElseThrow(() -> new ForbiddenException("Not invited"));

        User user = userRepository.createUser(
                invite.getCompanyId(),
                firebaseUid,
                email,
                invite.getRole()
        );

        inviteRepository.markAccepted(invite.getId());

        return user;
    }
}