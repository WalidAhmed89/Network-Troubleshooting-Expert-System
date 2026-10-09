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
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name",nullable = false,length = 100)
    private String name;

    @Column(columnDefinition  = "TEXT")
    private String description;

    @Column
    private Integer priority = 3;

    @Column
    private Boolean enabled = true;

}
