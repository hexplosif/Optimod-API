package com.hexplosif.OptimodBackEnd.repository;

import com.hexplosif.OptimodBackEnd.model.Courier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepository extends CrudRepository<Courier, Long> {
}