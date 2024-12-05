package com.hexplosif.OptimodBackEnd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "delivery_request")
public class DeliveryRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pickup")
    private Long idPickup;

    @Column(name = "delivery")
    private Long idDelivery;

    @Column(name = "warehouse")
    private Long idWarehouse;

    @Column(name = "courrier")
    private Long idCourrier;
}