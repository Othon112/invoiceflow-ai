package com.invoiceflow.infrastructure.persistence.repository;

import com.invoiceflow.domain.model.Invoice;
import com.invoiceflow.domain.repository.InvoiceRepository;
import com.invoiceflow.infrastructure.persistence.entity.InvoiceEntity;
import com.invoiceflow.infrastructure.persistence.mapper.InvoiceMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class InvoiceRepositoryImpl implements InvoiceRepository, PanacheRepositoryBase<InvoiceEntity, UUID> {

    @Override
    @Transactional
    public Invoice create(Invoice invoice) {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setCompanyId(invoice.getCompanyId());
        entity.setDocumentId(invoice.getDocumentId());
        entity.setCreatedByUserId(invoice.getCreatedByUserId());
        entity.setStatus(invoice.getStatus());

        persist(entity);
        flush();
        getEntityManager().refresh(entity);

        return InvoiceMapper.toDomain(entity);
    }

    @Override
    public Optional<Invoice> findByDocumentId(UUID documentId) {
        InvoiceEntity entity = find("documentId", documentId).firstResult();
        return Optional.ofNullable(InvoiceMapper.toDomain(entity));
    }

    @Override
    public List<Invoice> listByCompany(UUID companyId, int limit, int offset) {
        if (limit <= 0) limit = 20;
        if (offset < 0) offset = 0;

        List<InvoiceEntity> entities = find(
                "companyId = ?1 order by createdAt desc",
                companyId
        ).range(offset, offset + limit - 1).list();

        List<Invoice> result = new ArrayList<>();
        for (InvoiceEntity e : entities) {
            result.add(InvoiceMapper.toDomain(e));
        }
        return result;
    }

    @Override
    public long countByCompany(UUID companyId) {
        return count("companyId", companyId);
    }

    @Override
    public Optional<Invoice> findByInvoiceId(UUID id) {
        InvoiceEntity entity = findById(id);
        return Optional.ofNullable(InvoiceMapper.toDomain(entity));
    }

    @Override
    public long countByCompanyAndVendor(UUID companyId, String vendorName) {
        return count(
                "companyId = ?1 and lower(vendorName) = lower(?2)",
                companyId,
                vendorName
        );
    }

    @Override
    public Double averageTotalAmountByCompanyAndVendor(UUID companyId, String vendorName) {
        return getEntityManager()
                .createQuery(
                        """
                        select avg(i.totalAmount)
                        from InvoiceEntity i
                        where i.companyId = :companyId
                          and lower(i.vendorName) = lower(:vendorName)
                          and i.totalAmount is not null
                        """,
                        Double.class
                )
                .setParameter("companyId", companyId)
                .setParameter("vendorName", vendorName)
                .getSingleResult();
    }

    @Override
    public boolean existsPossibleDuplicate(UUID companyId, String vendorName, Long totalAmount, UUID excludeInvoiceId) {
        Long result = getEntityManager()
                .createQuery(
                        """
                        select count(i)
                        from InvoiceEntity i
                        where i.companyId = :companyId
                          and lower(i.vendorName) = lower(:vendorName)
                          and i.totalAmount = :totalAmount
                          and i.id <> :excludeInvoiceId
                        """,
                        Long.class
                )
                .setParameter("companyId", companyId)
                .setParameter("vendorName", vendorName)
                .setParameter("totalAmount", totalAmount)
                .setParameter("excludeInvoiceId", excludeInvoiceId)
                .getSingleResult();

        return result != null && result > 0;
    }

    @Override
    @Transactional
    public Invoice update(Invoice invoice) {
        InvoiceEntity entity = findById(invoice.getId());
        if (entity == null) {
            throw new IllegalStateException("Invoice not found");
        }

        entity.setStatus(invoice.getStatus());
        entity.setVendorName(invoice.getVendorName());
        entity.setInvoiceNumber(invoice.getInvoiceNumber());
        entity.setInvoiceDate(invoice.getInvoiceDate());
        entity.setCurrency(invoice.getCurrency());
        entity.setSubtotalAmount(invoice.getSubtotalAmount());
        entity.setTaxAmount(invoice.getTaxAmount());
        entity.setTotalAmount(invoice.getTotalAmount());

        entity.setVatId(invoice.getVatId());
        entity.setRiskScore(invoice.getRiskScore());
        entity.setRiskLevel(invoice.getRiskLevel());
        entity.setRiskFlagsJson(invoice.getRiskFlagsJson());

        entity.setAiInsight(invoice.getAiInsight());

        flush();
        getEntityManager().refresh(entity);

        return InvoiceMapper.toDomain(entity);
    }

    @Override
    public Long sumTotalAmountForCurrentMonth(UUID companyId) {
        java.time.OffsetDateTime startOfMonth = java.time.OffsetDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        Long result = getEntityManager()
                .createQuery(
                        """
                        select coalesce(sum(i.totalAmount), 0)
                        from InvoiceEntity i
                        where i.companyId = :companyId
                          and i.createdAt >= :startOfMonth
                          and i.totalAmount is not null
                        """,
                        Long.class
                )
                .setParameter("companyId", companyId)
                .setParameter("startOfMonth", startOfMonth)
                .getSingleResult();

        return result == null ? 0L : result;
    }

    @Override
    public long countByCompanyAndStatus(UUID companyId, String status) {
        return count("companyId = ?1 and status = ?2", companyId, status);
    }

    @Override
    public long countHighRiskByCompany(UUID companyId) {
        return count("companyId = ?1 and riskLevel = ?2", companyId, "HIGH");
    }

    @Override
    public List<Object[]> findTopVendorsByCompany(UUID companyId, int limit) {
        if (limit <= 0) {
            limit = 5;
        }

        return getEntityManager()
                .createQuery(
                        """
                        select i.vendorName, coalesce(sum(i.totalAmount), 0)
                        from InvoiceEntity i
                        where i.companyId = :companyId
                          and i.vendorName is not null
                          and i.totalAmount is not null
                        group by i.vendorName
                        order by sum(i.totalAmount) desc
                        """,
                        Object[].class
                )
                .setParameter("companyId", companyId)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<Invoice> findRecentRiskyInvoicesByCompany(UUID companyId, int limit) {
        if (limit <= 0) {
            limit = 5;
        }

        List<InvoiceEntity> entities = find(
                "companyId = ?1 and riskLevel in (?2, ?3) order by updatedAt desc",
                companyId,
                "HIGH",
                "MEDIUM"
        ).page(0, limit).list();

        List<Invoice> result = new java.util.ArrayList<>();
        for (InvoiceEntity entity : entities) {
            result.add(InvoiceMapper.toDomain(entity));
        }
        return result;
    }
}