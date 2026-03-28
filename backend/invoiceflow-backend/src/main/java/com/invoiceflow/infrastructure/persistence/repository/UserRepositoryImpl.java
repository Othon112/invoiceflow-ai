package com.invoiceflow.infrastructure.persistence.repository;

import com.invoiceflow.domain.model.User;
import com.invoiceflow.domain.repository.UserRepository;
import com.invoiceflow.infrastructure.persistence.entity.UserEntity;
import com.invoiceflow.infrastructure.persistence.mapper.UserMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<UserEntity, UUID> {

    @Override
    public Optional<User> findByFirebaseUid(String firebaseUid) {

        System.out.println("🔎 findByFirebaseUid called with: " + firebaseUid);
        System.out.println("📦 users table count (via Hibernate): " + count());

        UserEntity first = findAll().firstResult();
        if (first != null) {
            System.out.println("👤 first user firebaseUid in DB (via Hibernate): " + first.getFirebaseUid());
        } else {
            System.out.println("👤 no users returned by Hibernate");
        }

        UserEntity userEntity = find("firebaseUid = ?1", firebaseUid).firstResult();
        if (userEntity == null) {
            return Optional.empty();
        }
        return Optional.of(UserMapper.toDomain(userEntity));
    }

    @Override
    @Transactional
    public User createUser(UUID companyId, String firebaseUid, String email, String role) {
        UserEntity userEntity = new UserEntity();
        userEntity.setCompanyId(companyId);
        userEntity.setFirebaseUid(firebaseUid);
        userEntity.setEmail(email);
        userEntity.setRole(role);

        persist(userEntity);

        return UserMapper.toDomain(userEntity);
    }

    @Override
    public boolean existsAdmin() {
        return count("role", "admin") > 0;
    }
}