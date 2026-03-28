package com.invoiceflow.domain.repository;

import com.invoiceflow.domain.model.Company;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    boolean existsAny();
    Company create(String name);
    Optional<Company> findCompanyById(UUID id);
}
