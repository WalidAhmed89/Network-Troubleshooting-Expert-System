package com.frosted.network_troubleshooting.repository;

import com.frosted.network_troubleshooting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionFactRepository extends JpaRepository<User,Integer> {
}
