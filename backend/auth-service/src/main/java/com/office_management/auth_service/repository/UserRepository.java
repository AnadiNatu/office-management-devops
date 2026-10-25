package com.office_management.auth_service.repository;

import com.office_management.auth_service.entity.Users;
import com.office_management.auth_service.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users , Long> {
    Optional<Users> findByEmailIgnoreCase(String email);

    Optional<Users> findByPhoneNumber(String phoneNumber);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    List<Users> findByRole(UserRoles role);

    List<Users> findByEnabledTrue();

    List<Users> findByRoleAndEnabledTrue(UserRoles role);


}