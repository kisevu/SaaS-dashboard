package com.oscaris.kitchen.user.repository;
/*
*
@author ameda
@project kitchen-reader
*
*/

import com.oscaris.kitchen.user.entity.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends MongoRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByToken(String token);
}
