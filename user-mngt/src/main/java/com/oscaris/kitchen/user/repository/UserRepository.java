package com.oscaris.kitchen.user.repository;/*
*
@author ameda
@project kitchen-reader
*
*/

import com.oscaris.kitchen.user.entity.Role;
import com.oscaris.kitchen.user.entity.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Users,String> {
    Optional<Users> findByEmail(String email);
    List<Users> findByRole(Role role);
}
