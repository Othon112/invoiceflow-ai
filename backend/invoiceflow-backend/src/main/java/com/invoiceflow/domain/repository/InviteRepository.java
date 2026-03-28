package com.invoiceflow.domain.repository;

import com.invoiceflow.domain.model.Invite;

import java.util.Optional;
import java.util.UUID;

public interface InviteRepository {
    Invite create(UUID companyId, String email, String role, UUID createdByUserId);
    Optional<Invite> findPendingByCompanyAndEmail(UUID companyId, String email);
    Optional<Invite> findPendingByEmail(String email);
    void markAccepted(UUID inviteId);
}
