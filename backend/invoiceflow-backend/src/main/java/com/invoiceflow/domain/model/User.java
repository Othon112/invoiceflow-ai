package com.invoiceflow.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private UUID id;
    private UUID companyId;
    private String firebaseUid;
    private String email;
    private String role;

}


