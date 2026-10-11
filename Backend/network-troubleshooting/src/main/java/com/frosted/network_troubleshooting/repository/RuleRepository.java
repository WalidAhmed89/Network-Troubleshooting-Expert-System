package com.frosted.network_troubleshooting.repository;

import com.frosted.network_troubleshooting.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleRepository extends JpaRepository<Rule, Integer> {
    List<Rule> findByEnabledTrueOrderByPriorityAsc();
}
