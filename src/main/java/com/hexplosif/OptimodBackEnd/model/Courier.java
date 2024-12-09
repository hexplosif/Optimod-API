package com.hexplosif.OptimodBackEnd.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "courier")
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;
}
