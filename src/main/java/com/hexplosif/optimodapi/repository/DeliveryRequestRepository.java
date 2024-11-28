package com.hexplosif.optimodapi.repository;

import com.hexplosif.optimodapi.model.DeliveryRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRequestRepository extends CrudRepository<DeliveryRequest, Long> {
}
