package com.invoiceflow.domain.repository;

import com.invoiceflow.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    // The method below handles both cases: if the user exists or not, if it doesnt it would create it.
    Optional<User> findByFirebaseUid(String firebaseUid);
    User createUser(UUID companyId, String firebaseUid, String email, String role);
    boolean existsAdmin();
}
