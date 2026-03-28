package com.invoiceflow.application.service;

import com.invoiceflow.domain.model.Company;
import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.CompanyRepository;
import com.invoiceflow.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class BootstrapService {

    @Inject
    UserRepository userRepository;

    @Inject
    CompanyRepository companyRepository;

    public Result bootstrap(String companyName, String firebaseUid, String email) {
        if (companyRepository.existsAny() || userRepository.existsAdmin()){
            throw new IllegalStateException("Bootstrap already completed");
        }
        Company company = companyRepository.create(companyName);
        User admin = userRepository.createUser(company.getId(), firebaseUid, email, "admin");
        return new Result(company.getId(), admin.getId());
    }

    public record Result(UUID companyId, UUID userId) {}

}
