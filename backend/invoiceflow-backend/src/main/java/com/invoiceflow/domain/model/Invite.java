package com.invoiceflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Invite {
    private UUID id;
    private UUID companyId;
    private String email;
    private String role;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime acceptedAt;
    private UUID createdByUserId;

}
