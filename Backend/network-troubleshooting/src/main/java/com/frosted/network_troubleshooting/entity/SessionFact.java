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
@Table(name = "session_facts")
public class SessionFact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private DiagnosisSession sessions;

    @Column(name = "fact_name",nullable = false,length = 100)
    private String factName;

    @Column(name = "fact_value",nullable = false,length = 100)
    private String factValue;

}
