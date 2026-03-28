package com.invoiceflow.infrastructure.persistence.mapper;

import com.invoiceflow.domain.model.Invite;
import com.invoiceflow.infrastructure.persistence.entity.InviteEntity;

public class InviteMapper {
    public static Invite toDomain(InviteEntity inviteEntity) {
        if (inviteEntity == null){
            return null;
        }
        Invite invite = new Invite();
        invite.setId(inviteEntity.getId());
        invite.setCompanyId(inviteEntity.getCompanyId());
        invite.setEmail(inviteEntity.getEmail());
        invite.setRole(inviteEntity.getRole());
        invite.setStatus(inviteEntity.getStatus());
        invite.setCreatedAt(inviteEntity.getCreatedAt());
        invite.setAcceptedAt(inviteEntity.getAcceptedAt());
        invite.setCreatedByUserId(inviteEntity.getCreatedByUserId());
        return invite;

    }
}
