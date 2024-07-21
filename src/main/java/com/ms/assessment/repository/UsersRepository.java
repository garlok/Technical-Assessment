package com.ms.assessment.repository;

import com.ms.assessment.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
   Users findByUserName(String userName);
}
