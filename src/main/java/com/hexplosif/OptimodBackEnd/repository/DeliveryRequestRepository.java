package com.hexplosif.OptimodBackEnd.repository;

import com.hexplosif.OptimodBackEnd.model.DeliveryRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRequestRepository extends CrudRepository<DeliveryRequest, Long> {
}
