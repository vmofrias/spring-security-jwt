package com.ldsk.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ldsk.userservice.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByName(String name);
}
