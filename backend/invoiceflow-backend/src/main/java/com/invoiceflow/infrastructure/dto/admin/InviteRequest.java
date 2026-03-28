package com.invoiceflow.infrastructure.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteRequest {
    public String email;
    public String role;
}