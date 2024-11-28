package com.hexplosif.optimodapi.service;

import com.hexplosif.optimodapi.model.DeliveryRequest;
import com.hexplosif.optimodapi.repository.DeliveryRequestRepository;
import java.util.Optional;
import lombok.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryRequestService {
    @Autowired
    private DeliveryRequestRepository nodeRepository;

    public Optional<DeliveryRequest> findDeliveryRequestById(final Long id) {
        return this.nodeRepository.findById(id);
    }

    public DeliveryRequest saveDeliveryRequest(final DeliveryRequest node) {
        return (DeliveryRequest)this.nodeRepository.save(node);
    }

    public void deleteDeliveryRequestById(final Long id) {
        this.nodeRepository.deleteById(id);
    }

    public Iterable<DeliveryRequest> findAllDeliveryRequests() {
        return this.nodeRepository.findAll();
    }

    public void deleteAllDeliveryRequests() {
        this.nodeRepository.deleteAll();
    }

    public long countDeliveryRequests() {
        return this.nodeRepository.count();
    }
}