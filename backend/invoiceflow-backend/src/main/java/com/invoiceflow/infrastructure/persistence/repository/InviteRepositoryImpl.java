package com.invoiceflow.infrastructure.persistence.repository;

import com.invoiceflow.domain.model.Invite;
import com.invoiceflow.domain.repository.InviteRepository;
import com.invoiceflow.infrastructure.persistence.entity.InviteEntity;
import com.invoiceflow.infrastructure.persistence.mapper.InviteMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class InviteRepositoryImpl implements InviteRepository, PanacheRepositoryBase<InviteEntity, UUID> {

    @Override
    @Transactional
    public Invite create(UUID companyId, String email, String role, UUID createdByUserId) {
        InviteEntity entity = new InviteEntity();
        entity.setCompanyId(companyId);
        entity.setEmail(email.trim().toLowerCase());
        entity.setRole(role);
        entity.setStatus("PENDING");
        entity.setCreatedByUserId(createdByUserId);

        persist(entity);
        flush();
        getEntityManager().refresh(entity);
        return InviteMapper.toDomain(entity);
    }

    @Override
    public Optional<Invite> findPendingByCompanyAndEmail(UUID companyId, String email) {
        InviteEntity entity = find(
                "companyId = ?1 and email = ?2 and status = 'PENDING'",
                companyId, email.trim().toLowerCase()
        ).firstResult();

        return Optional.ofNullable(InviteMapper.toDomain(entity));
    }

    @Override
    public Optional<Invite> findPendingByEmail(String email) {
        InviteEntity entity = find(
                "email = ?1 and status = 'PENDING'",
                email.trim().toLowerCase()
        ).firstResult();

        return Optional.ofNullable(InviteMapper.toDomain(entity));
    }

    @Override
    @Transactional
    public void markAccepted(UUID inviteId) {
        InviteEntity entity = findById(inviteId);
        if (entity == null) {
            return;
        }

        entity.setStatus("ACCEPTED");
        entity.setAcceptedAt(OffsetDateTime.now());
        persist(entity);
    }
}