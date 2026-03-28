package com.invoiceflow.infrastructure.persistence.mapper;

import com.invoiceflow.domain.model.Company;
import com.invoiceflow.infrastructure.persistence.entity.CompanyEntity;

public class CompanyMapper {

    public static Company toDomain(CompanyEntity companyEntity) {
        if (companyEntity == null) {
            return null;
        }
        return new Company(companyEntity.getId(), companyEntity.getName());
    }

    public static CompanyEntity toEntity(Company company) {
        if (company == null) {
            return null;
        }
        return new CompanyEntity(company.getId(), company.getName());
    }
}
