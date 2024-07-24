package com.oscaris.kitchen.user.repository;
/*
*
@author ameda
@project kitchen-reader
*
*/

import com.oscaris.kitchen.user.entity.PasswordResetToken;
import com.oscaris.kitchen.user.entity.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByUser(Users user);
    Optional<PasswordResetToken> findByEmail(String email);
}
