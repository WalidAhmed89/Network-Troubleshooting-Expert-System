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
@Table(name = "rule_conclusions")
public class RuleConclusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @Column(name = "conclusion_name", nullable = false, length = 100)
    private String conclusionName;

    @Column(name = "conclusion_value", nullable = false, length = 200)
    private String conclusionValue;
}
