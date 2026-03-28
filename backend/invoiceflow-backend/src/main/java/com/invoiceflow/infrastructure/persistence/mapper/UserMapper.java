package com.invoiceflow.infrastructure.persistence.mapper;

import com.invoiceflow.domain.model.User;
import com.invoiceflow.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        User user = new User();
        user.setId(userEntity.getId());
        user.setCompanyId(userEntity.getCompanyId());
        user.setFirebaseUid(userEntity.getFirebaseUid());
        user.setEmail(userEntity.getEmail());
        user.setRole(userEntity.getRole());
        return user;
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setCompanyId(user.getCompanyId());
        userEntity.setFirebaseUid(user.getFirebaseUid());
        userEntity.setEmail(user.getEmail());
        userEntity.setRole(user.getRole());
        return userEntity;
    }
}