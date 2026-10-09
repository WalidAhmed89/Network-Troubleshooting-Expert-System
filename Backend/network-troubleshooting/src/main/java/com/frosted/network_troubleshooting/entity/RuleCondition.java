package com.frosted.network_troubleshooting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rule_conditions")
public class RuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @Column(name = "fact_name", nullable = false, length = 100)
    private String factName;

    @Column(nullable = false, length = 20)
    private String operator;

    @Column(name = "expected_value", nullable = false, length = 100)
    private String expectedValue;
}
