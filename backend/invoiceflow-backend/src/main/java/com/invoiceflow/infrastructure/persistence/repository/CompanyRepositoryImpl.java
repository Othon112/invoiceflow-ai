package com.invoiceflow.infrastructure.persistence.repository;

import com.invoiceflow.domain.model.Company;
import com.invoiceflow.domain.repository.CompanyRepository;
import com.invoiceflow.infrastructure.persistence.entity.CompanyEntity;
import com.invoiceflow.infrastructure.persistence.mapper.CompanyMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CompanyRepositoryImpl implements CompanyRepository , PanacheRepositoryBase<CompanyEntity, UUID> {

    @Override
    public boolean existsAny(){
        return count() > 0;
    }

    @Override
    @Transactional
    public Company create(String name) {
        CompanyEntity ce = new CompanyEntity();
        ce.setName(name);
        persist(ce);
        return CompanyMapper.toDomain(ce);
    }

    @Override
    public Optional<Company> findCompanyById(UUID id) {
        CompanyEntity companyEntity = findById(id);
        if (companyEntity == null) {
            return Optional.empty();
        }
        return Optional.of(CompanyMapper.toDomain(companyEntity));
    }
}
