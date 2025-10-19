package com.julia.taskmanagementapp.repository;

import com.julia.taskmanagementapp.model.Role;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findByRoleIn(Set<String> roles);
}
